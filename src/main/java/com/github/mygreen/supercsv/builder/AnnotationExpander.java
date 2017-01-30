package com.github.mygreen.supercsv.builder;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.supercsv.exception.SuperCsvReflectionException;

import com.github.mygreen.supercsv.annotation.CsvComposition;
import com.github.mygreen.supercsv.annotation.CsvOverridesAttribute;
import com.github.mygreen.supercsv.annotation.constraint.CsvConstraint;
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.annotation.conversion.CsvConversion;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;
import com.github.mygreen.supercsv.localization.MessageBuilder;
import com.github.mygreen.supercsv.util.Utils;

/**
 * 繰り返しのアノテーション、合成のアノテーションを考慮して、アノテーションを展開します。
 * <p>並び順は、コンストラクタで指定されたものに並び変えられる。</p>
 * 
 * <p>繰り返しのアノテーション{@link Repeatable}が付与されたアノテーションの場合、
 *    取得する際には複数のアノテーションがまとめたアノテーションとして別に取得されるため分解する。</p>
 * 
 * <p>合成のアノテーション{@link CsvComposition}が付与されたアノテーションの場合、
 *    付与されているアノテーションに分解する。
 *    その際に、定義されている属性を元に、付与されているアノテーションの属性を上書きする。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class AnnotationExpander {
    
    private final Comparator<ExpandedAnnotation> comparator;
    
    /**
     * アノテーションの並び順を指定するコンストラクタ。
     * 
     * @param annotationComparator アノテーションの並び順を指定するためのComparator。
     * @throws NullPointerException {@literal annotationComparator == null.}
     */
    public AnnotationExpander(final Comparator<Annotation> annotationComparator) {
        Objects.requireNonNull(annotationComparator);
        
        this.comparator = new Comparator<ExpandedAnnotation>() {
            
            @Override
            public int compare(final ExpandedAnnotation o1, final ExpandedAnnotation o2) {
                return annotationComparator.compare(o1.getOriginal(), o2.getOriginal());
            }
            
        };
    }
    
    /**
     * 複数のアノテーションを展開する。
     * @param targetAnnos  展開対象のアノテーション
     * @return 展開されたアノテーション
     * @throws NullPointerException {@literal targetAnnos == null.}
     */
    public List<ExpandedAnnotation> expand(final Annotation[] targetAnnos) {
        Objects.requireNonNull(targetAnnos);
        
        final List<ExpandedAnnotation> expanedList = new ArrayList<>();
        for(Annotation targetAnno : targetAnnos) {
            expanedList.addAll(expand(targetAnno));
        }
        
        Collections.sort(expanedList, comparator);
        
        return expanedList;
        
    }
    
    /**
     * アノテーションを展開する。
     * @param targetAnno 展開対象のアノテーション
     * @return 展開されたアノテーション
     * @throws NullPointerException {@literal targetAnno == null.}
     */
    public List<ExpandedAnnotation> expand(final Annotation targetAnno) {
        Objects.requireNonNull(targetAnno);
        
        final List<ExpandedAnnotation> expandedList = new ArrayList<>();
        
        if(isRepeated(targetAnno)) {
            // 繰り返しのアノテーションの場合、要素を抽出する。
            try {
                final Method method = targetAnno.getClass().getMethod("value");
                final Annotation[] annos = (Annotation[]) method.invoke(targetAnno);
                
                int index = 0;
                for(Annotation anno : annos) {
                    final List<ExpandedAnnotation> repeatedAnnos = expand(anno);
                    for(ExpandedAnnotation repeatedAnno : repeatedAnnos) {
                        repeatedAnno.setIndex(index);
                    }
                    
                    expandedList.addAll(repeatedAnnos);
                    index++;
                }
                
            } catch (Exception e) {
                throw new RuntimeException("fail get repeated value attribute.", e);
            }
            
        } else if(isComposed(targetAnno)) {
            final ExpandedAnnotation composedAnno = new ExpandedAnnotation(targetAnno, true);
            
            // 合成のアノテーションの場合、メタアノテーションを子供としてさらに抽出する。
            final List<Annotation> childAnnos = Arrays.asList(targetAnno.annotationType().getAnnotations());
            for(Annotation anno : childAnnos) {
                
                final List<ExpandedAnnotation> nestedAnnos = expand(anno).stream()
                        .map(nestedAnno -> overrideAttribute(targetAnno, nestedAnno))
                        .collect(Collectors.toList());
                
                composedAnno.addChilds(nestedAnnos);
                
            }
            
            Collections.sort(composedAnno.getChilds(), comparator);
            expandedList.add(composedAnno);
            
        } else {
            // 通常のアノテーションの場合
            expandedList.add(new ExpandedAnnotation(targetAnno, false));
            
        }
        
        Collections.sort(expandedList, comparator);
        return expandedList;
        
    }
    
    /**
     * 繰り返されたアノテーションかどうか判定する。
     * <p>属性「value」に、繰り返しのアノテーション{@link Repeatable}が付与されている
     *    アノテーションの配列を保持しているかどうかで判定する。</p>
     * @param targetAnno
     * @return
     */
    private boolean isRepeated(final Annotation targetAnno) {
        
        try {
            final Method method = targetAnno.getClass().getMethod("value");
            
            // 値のクラスタイプがアノテーションの配列かどうかのチェック
            final Class<?> returnType = method.getReturnType();
            if(!(returnType.isArray() && Annotation.class.isAssignableFrom(returnType.getComponentType()))) {
                return false;
            }
            
            final Annotation[] annos = (Annotation[]) method.invoke(targetAnno);
            if(annos.length == 0) {
                return false;
            }
            
            // @Repetableアノテーションが付与されているかどうか
            if(annos[0].annotationType().getAnnotation(Repeatable.class) != null) {
                return true;
            }
            
        } catch (Exception e) {
            
        }
        
        return false;
        
    }
    
    /**
     * 合成されたアノテーションかどうか判定する。
     * <p>メタアノテーション{@link CsvComposition}が付与されているかどうかで判定する。</p>
     * @param targetAnno
     * @return
     */
    private boolean isComposed(final Annotation targetAnno) {
        
        return targetAnno.annotationType().getAnnotation(CsvComposition.class) != null;
        
    }
    
    /**
     * 合成したアノテーションの属性を、構成されるアノテーションに反映する。
     * @param compositionAnno 
     * @param nestedAnno
     * @return
     */
    private ExpandedAnnotation overrideAttribute(final Annotation compositionAnno, final ExpandedAnnotation nestedAnno) {
        
        final Annotation originalAnno = nestedAnno.getOriginal();
        if(!isOverridableAnnotation(originalAnno)) {
            return nestedAnno;
        }
        
        // 上書きするアノテーションの属性の組み立て
        final Map<String, Object> overrideAttrs = buildOverrideAttribute(compositionAnno, nestedAnno);
        if(overrideAttrs.isEmpty()) {
            return nestedAnno;
        }
        
        // 既存のアノテーションの属性の作成
        final Class<?> annotationClass = originalAnno.annotationType();
        final Map<String, Object> defaultValues = new HashMap<>();
        for(Method method : annotationClass.getMethods()) {
            try {
                method.setAccessible(true);
                if(method.getParameterCount() == 0) {
                    final Object value = method.invoke(originalAnno);
                    defaultValues.put(method.getName(), value);
                    
                } else {
                    final Object value = method.getDefaultValue();
                    if(value != null) {
                        defaultValues.put(method.getName(), value);
                    }
                    
                }
                
            } catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(String.format("fail get annotation attribute %s#%s.", annotationClass.getName(), method.getName()), e);
            }
        }
        
        // アノテーションのインスタンスの組み立てなおし
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final Object annoObj = Proxy.newProxyInstance(classLoader, new Class[]{annotationClass},
                new InvocationHandler() {
                    
                    @Override
                    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                        final String name = method.getName();
                        if (name.equals("annotationType")) {
                            return annotationClass;
                            
                        } else if(overrideAttrs.containsKey(name)){
                            return overrideAttrs.get(name);
                            
                        } else {
                            return defaultValues.get(name);
                        }
                    }
        });
        
        // 値をコピーする
        final ExpandedAnnotation propagatedAnno = new ExpandedAnnotation((Annotation)annoObj, nestedAnno.isComposed());
        propagatedAnno.setIndex(nestedAnno.getIndex());
        propagatedAnno.addChilds(nestedAnno.getChilds());
        
        return propagatedAnno;
    }
    
    /**
     * アノテーションの属性を上書き可能かか判定する。
     * <p>ただし、実際には合成のアノテーションに属性がなければ上書きはされないので、あくまで上書き可能かの判定しか行わない。</p>
     * <p>条件は以下の通り。</p>
     * <ul>
     *  <li>メタアノテーション{@link CsvConstraint}、{@link CsvConversion}が付与されているアノテーションである。</li>
     *  <li>アノテーション{@link CsvRequire}である。</li>
     *  <li>フォーマット用のアノテーションである。パッケージ名から判定する。</li>
     * </ul>
     * @param targetAnno 判定対象のアノテーション
     * @return trueの場合、上書き対象。
     */
    private boolean isOverridableAnnotation(final Annotation targetAnno) {
        
        final Class<?> annoType = targetAnno.annotationType();
        if(annoType.getAnnotation(CsvConstraint.class) != null) {
            return true;
        }
        
        if(annoType.getAnnotation(CsvConversion.class) != null) {
            return true;
        }
        
        if(annoType.getTypeName().startsWith("com.github.mygreen.supercsv.annotation.format")) {
            return true;
        }
        
        return false;
        
    }
    
    /**
     * 上書きする属性の組み立て
     * @param compositionAnno 合成のアノテーション
     * @param targetAnno 上書き対象のアノテーション
     * @return
     */
    @SuppressWarnings("rawtypes")
    private Map<String, Object> buildOverrideAttribute(final Annotation compositionAnno, final ExpandedAnnotation targetAnno) {
        
        final Annotation originalAnno = targetAnno.getOriginal();
        
        final Map<String, Object> overrideAttrs = new HashMap<>();
        
        // @CsvOvrerideAttributeが付与されたメソッド名
        final Set<String> overrideMethodNames = new HashSet<>();
        
        // @CsvOverridesAttributeが付与された属性の組み立て
        for(Method compositionMethod : compositionAnno.annotationType().getMethods()) {
            
            final List<CsvOverridesAttribute> annoList = new ArrayList<>();
            final CsvOverridesAttribute overrideAttrAnno = compositionMethod.getAnnotation(CsvOverridesAttribute.class);
            if(overrideAttrAnno != null) {
                annoList.add(overrideAttrAnno);
            }
            
            // 繰り返しのアノテーションの場合
            final CsvOverridesAttribute.List overrideAttrAnnoList = compositionMethod.getAnnotation(CsvOverridesAttribute.List.class);
            if(overrideAttrAnnoList != null) {
                annoList.addAll(Arrays.asList(overrideAttrAnnoList.value()));
            }
            
            if(annoList.isEmpty()) {
                // @CsvOverridesAttributeが付与されていない場合
                continue;
            }
            
            overrideMethodNames.add(compositionMethod.getName());
            
            for(CsvOverridesAttribute anno : annoList) {
                // アノテーションのクラスの判定
                if(!anno.annotation().equals(originalAnno.annotationType())) {
                    continue;
                }
                
                // インデックスの判定
                if(anno.index() >= 0 && anno.index() != targetAnno.getIndex()) {
                    continue;
                }
                
                final String attrName = anno.name().isEmpty() ? compositionMethod.getName() : anno.name();
                
                if(!Utils.hasAnnotationAttribute(originalAnno, attrName, compositionMethod.getReturnType())) {
                    // 上書き対象の属性が見つからない場合
                    throw new SuperCsvInvalidAnnotationException(originalAnno, MessageBuilder.create("anno.CsvOverridesAnnotation.notFoundAttr")
                            .varWithAnno("compositionAnno", compositionAnno.annotationType())
                            .varWithAnno("overrideAnno", originalAnno.annotationType())
                            .varWithClass("attrType", compositionMethod.getReturnType())
                            .var("attrName", attrName)
                            .format());
                    
                }
                
                // 属性値の取得
                try {
                    Object attrValue = compositionMethod.invoke(compositionAnno);
                    overrideAttrs.put(attrName, attrValue);
                    
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new SuperCsvReflectionException(MessageBuilder.create("anno.CsvOverridesAnnotation.failGetAttr")
                            .varWithAnno("compositionAnno", compositionAnno.annotationType())
                            .var("attrName", attrName)
                            .format(),
                            e);
                }
            
            }
            
        }
        
        // message 属性の取得。
        // 既に取得していたり、@CsvOverridesAttributeが付与されている場合はスキップする。
        if(!overrideAttrs.containsKey("message") && !overrideMethodNames.contains("message")
                && Utils.hasAnnotationAttribute(originalAnno, "message", String.class)) {
            
            final Optional<String> messageAttr = Utils.getAnnotationAttribute(compositionAnno, "message", String.class);
            if(messageAttr.isPresent() && Utils.isNotEmpty(messageAttr.get())) {
                overrideAttrs.put("message", messageAttr.get());
            }
        }
        
        // groups 属性の取得。
        // 既に取得していたり、@CsvOverridesAttributeが付与されている場合はスキップする。
        if(!overrideAttrs.containsKey("groups") && !overrideMethodNames.contains("groups")
                && Utils.hasAnnotationAttribute(originalAnno, "groups", Class[].class)) {
            
            final Optional<Class[]> groupsAttr = Utils.getAnnotationAttribute(compositionAnno, "groups", Class[].class);
            if(groupsAttr.isPresent() && Utils.isNotEmpty(groupsAttr.get())) {
                overrideAttrs.put("groups", groupsAttr.get());
            }
        }
        
        // cases 属性の取得。
        // 既に取得していたり、@CsvOverridesAttributeが付与されている場合はスキップする。
        if(!overrideAttrs.containsKey("cases") && !overrideMethodNames.contains("cases")
                && Utils.hasAnnotationAttribute(originalAnno, "cases", BuildCase[].class)) {
            
            final Optional<BuildCase[]> casesAttr = Utils.getAnnotationAttribute(compositionAnno, "cases", BuildCase[].class);
            if(casesAttr.isPresent() && Utils.isNotEmpty(casesAttr.get())) {
                overrideAttrs.put("cases", casesAttr.get());
            }
        }
        
        return overrideAttrs;
    }
    
}
