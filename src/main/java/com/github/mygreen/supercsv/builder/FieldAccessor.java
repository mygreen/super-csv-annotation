package com.github.mygreen.supercsv.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.supercsv.exception.SuperCsvReflectionException;

import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.util.Utils;

/**
 * フィールドに統一的にアクセスするためのクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class FieldAccessor {
    
    /**
     * フィールドの実体
     */
    private final Field field;
    
    /**
     * フィールドの名称
     */
    private final String name;
    
    /**
     * フィールドのタイプ
     */
    private final Class<?> type;
    
    /**
     * アノテーションの一覧
     */
    private final List<ExpandedAnnotation> expandedAnnos = new ArrayList<>();
    
    /**
     * フィールド情報を指定するコンストラクタ。
     * @param field フィールド情報
     * @param comparator アノテーションの順序を比較するためのコンパレータ。
     * @throws NullPointerException {@literal field or comparator == null.}
     */
    public FieldAccessor(final Field field, final Comparator<Annotation> comparator) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(comparator);
        
        field.setAccessible(true);
        
        this.field = field;
        this.type = field.getType();
        this.name = field.getName();
        
        final AnnotationExpander expander = new AnnotationExpander(comparator);
        this.expandedAnnos.addAll(expander.expand(field.getAnnotations()));
    }
    
    /**
     * アノテーションのタイプを指定してアノテーションを取得します。
     * <p>繰り返しのアノテーションの場合、初めに見つかったものを返します。</p>
     * 
     * @param <A> 取得対象のアノテーションのタイプ
     * @param annoClass 取得対象のアノテーションのタイプ。
     * @return 指定したアノテーションが見つからない場合は、空を返します。
     * @throws NullPointerException {@literal annoClass is null.}
     */
    public <A extends Annotation> Optional<A> getAnnotation(final Class<A> annoClass) {
        Objects.requireNonNull(annoClass, "annoClass should not be null.");
        
        return getAnnotationsByType(expandedAnnos, annoClass).stream()
                .findFirst();
        
    }
    
    /**
     * アノテーションのタイプを指定してアノテーション一覧を取得します。
     * <p>繰り返しのアノテーションの場合、初めに見つかったものを返します。</p>
     * 
     * @param <A> 取得対象のアノテーションのタイプ
     * @param annoClass 取得対象のアノテーションのタイプ。
     * @return 指定したアノテーションが見つからない場合は、空のリスト返します。
     * @throws NullPointerException {@literal annoClass is null.}
     */
    public <A extends Annotation> List<A> getAnnotations(final Class<A> annoClass) {
        Objects.requireNonNull(annoClass, "annoClass should not be null.");
        
        return getAnnotationsByType(expandedAnnos, annoClass);
    }
    
    @SuppressWarnings({"unchecked"})
    private static <A extends Annotation> List<A> getAnnotationsByType(
            final List<ExpandedAnnotation> expanedAnnos, final Class<A> annoClass) {
        
        final List<A> list = new ArrayList<>();
        
        for(ExpandedAnnotation anno : expanedAnnos) {
            
            if(anno.isAnnotationType(annoClass)) {
                list.add((A)anno.getOriginal());
            
            } else if(anno.isComposed()) {
                
                list.addAll(getAnnotationsByType(anno.getChilds(), annoClass));
                
            }
            
            
        }
        
        return list;
        
    }
    
    /**
     * 指定したアノテーションと持つかどうか。
     * <p>繰り返し可能なアノテーションの場合、初めに見つかったものを返します。</p>
     * 
     * @param <A> 取得対象のアノテーションのタイプ
     * @param annoClass 取得対象のアノテーションのタイプ。
     * @return {@literal true}の場合、アノテーションを持ちます。
     * @throws NullPointerException {@literal annoClass is null.}
     */
    public <A extends Annotation> boolean hasAnnotation(final Class<A> annoClass) {
        return getAnnotation(annoClass).isPresent();
    }
    
    /**
     * アノテーションのタイプとグループを指定してアノテーションを取得します。
     * 
     * @param <A> 取得対象のアノテーションのタイプ
     * @param annoClass 取得対象のアノテーションのタイプ。
     * @param groups グループ（クラスタイプ）による絞り込み。属性groupsが存在する場合に、絞り込みます。
     * @return 指定したアノテーションが見つからない場合は、サイズ0のリストを返します。
     * @throws NullPointerException {@literal annoClass is null.}
     */
    public <A extends Annotation> List<A> getAnnotationsByGroup(final Class<A> annoClass, final Class<?>... groups) {
        Objects.requireNonNull(annoClass, "annoClass should not be null.");
        
        return getAnnotations(annoClass).stream()
                .filter(anno -> hasGroups(anno, groups))
                .collect(Collectors.toList());
        
    }
    
    /**
     * グループを指定して指定したアノテーションを持つかどうか判定します。
     * 
     * @param <A> 取得対象のアノテーションのタイプ
     * @param annoClass 判定対象のアノテーションのグループ
     * @param groups グループ（クラスタイプ）による絞り込み。属性groupsが存在する場合に、絞り込みます。
     * @return 指定したアノテーションが見つからない場合は、サイズ0のリストを返します。
     */
    public <A extends Annotation> boolean hasAnnotationByGroup(final Class<A> annoClass, final Class<?>... groups) {
        
        return getAnnotationsByGroup(annoClass, groups).size() > 0;
        
    }
    
    
    /**
     * 付与されているアノテーションの一覧を取得する。
     * 
     * @param groups グループ（クラスタイプ）による絞り込み。属性groupsが存在する場合に、絞り込みます。
     * @return 指定したアノテーションが見つからない場合は、サイズ0のリストを返します。
     */
    public List<Annotation> getAnnotationsByGroup(final Class<?>... groups) {
        
        return getAnnotations(expandedAnnos).stream()
                .filter(anno -> hasGroups(anno, groups))
                .collect(Collectors.toList());
        
    }
    
    @SuppressWarnings({"unchecked"})
    private static <A extends Annotation> List<A> getAnnotations(final List<ExpandedAnnotation> expanedAnnos) {
        
        final List<A> list = new ArrayList<>();
        
        for(ExpandedAnnotation anno : expanedAnnos) {
            if(anno.isComposed()) {
                list.addAll(getAnnotations(anno.getChilds()));
                
            } else {
                list.add((A)anno.getOriginal());
                
            }
        }
        
        return list;
        
    }
    
    /**
     * アノテーションの属性{@literal groups} が指定したグループと一致するか比較します。
     * <p>groups属性を持たない場合は、必ずfalseを返します。</p>
     * @param anno 検証対象のアノテーション。
     * @param groups 比較対象のグループ情報。
     * @return {@literal true}の場合、指定したグループを持ちます。
     */
    @SuppressWarnings("rawtypes")
    private boolean hasGroups(final Annotation anno, final Class<?>... groups) {
        
        final Optional<Class[]> targetGroups = Utils.getAnnotationAttribute(anno, "groups", Class[].class);
        
        if(!targetGroups.isPresent()) {
            // groups属性を持たない場合
            return false;
            
        }
        
        if(groups.length == 0) {
            if(targetGroups.get().length == 0) {
                // グループの指定がない場合は、デフォルトグループとして処理。
                return true;
                
            } else {
                for(Class<?> targetGroup : targetGroups.get()) {
                    if(targetGroup.equals(DefaultGroup.class)) {
                        // デフォルトを直接指定している場合に、グループと一致。
                        return true;
                    }
                }
            }
            
        } else {
            // グループの指定がある場合
            for(Class<?> group : groups) {
                
                if(group.equals(DefaultGroup.class) && targetGroups.get().length == 0) {
                    // フィールド側にグループの指定がない場合は、デフォルトグループとして処理する。
                    return true;
                }
                
                for(Class<?> targetGroup : targetGroups.get()) {
                    // 一致するグループを持つか判定する。
                    if(targetGroup.equals(group)) {
                        return true;
                    }
                }
                
            }
            
        }
        
        return false;
        
    }
    
    /**
     * フィールドの名称を取得する。
     * @return フィールド名
     */
    public String getName() {
        return name;
    }
    
    /**
     * クラス名付きのフィールド名称を取得する。
     * @return {@literal <クラス名#フィールド名>}の形式
     */
    public String getNameWithClass() {
        return getDeclaredClass().getName() + "#" + getName();
    }
    
    /**
     * フィールドのタイプを取得する。
     * @return フィールドのクラスタイプ。
     */
    public Class<?> getType() {
        return type;
    }
    
    /**
     * フィールドのタイプのクラス名称を取得する。
     * @return パッケージ名付きのFQDNの形式。
     */
    public String getTypeName() {
        return getType().getName();
    }
    
    /**
     * フィールドが定義されているクラス情報を取得する。
     * 
     * @see Field#getDeclaringClass()
     * @return フィールドが定義されているクラス上方。
     */
    public Class<?> getDeclaredClass() {
        return field.getDeclaringClass();
    }
    
    /**
     * フィールドのタイプが指定してたタイプかどうか。
     * <p>{@link Class#isAssignableFrom(Class)}により比較を行う。
     * @param clazz 比較対象のクラスタイプ。
     * @return タイプが一致する場合、{@literal true}を返す。
     */
    public boolean isTypeOf(final Class<?> clazz) {
        return clazz.isAssignableFrom(getType());
    }
    
    /**
     * フィールドの値を取得する。
     * @param record レコードオブジェクト。
     * @return フィールドの値。
     * @throws IllegalArgumentException レコードのインスタンスがフィールドが定義されているクラスと異なる場合。
     * @throws SuperCsvReflectionException フィールドの値の取得に失敗した場合。
     */
    public Object getValue(final Object record) {
        Objects.requireNonNull(record);
        
        if(!getDeclaredClass().equals(record.getClass())) {
            throw new IllegalArgumentException(String.format("not match record class type. expected=%s. actual=%s, ",
                    type.getName(), record.getClass().getName()));
        }
        
        try {
            return field.get(record);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new SuperCsvReflectionException("fail get field value.", e);
        }
        
    }
}
