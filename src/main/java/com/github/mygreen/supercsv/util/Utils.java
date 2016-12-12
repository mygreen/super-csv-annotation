package com.github.mygreen.supercsv.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

import com.github.mygreen.supercsv.annotation.PatternFlag;
import com.github.mygreen.supercsv.builder.BuildCase;


/**
 * ユーティリティクラス。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class Utils {
    
    /**
     * <a href="http://www.joda.org/joda-time/" target="_blank">Joda-Time</a>のライブラリが利用可能かどうか。
     */
    public static final boolean ENABLED_LIB_JODA_TIME;
    static {
        boolean enabled;
        try {
            Class.forName("org.joda.time.LocalDateTime");
            enabled = true;
        } catch(ClassNotFoundException e) {
            enabled = false;
        }
        ENABLED_LIB_JODA_TIME = enabled;
    }
    
    /**
     * 文字列が空文字か判定する。
     * @param str
     * @return
     */
    public static boolean isEmpty(final String str) {
        if(str == null || str.isEmpty()) {
            return true;
        }
        
        if(str.length() == 1) {
            return str.charAt(0) == '\u0000';
        }
        
        return false;
    }
    
    /**
     * 文字列が空文字でないか判定する。
     * @param str
     * @return
     */
    public static boolean isNotEmpty(final String str) {
        return !isEmpty(str);
    }
    
    /**
     * コレクションが空か判定する。
     * @param collection
     * @return nullまたはサイズが0のときにtrueを返す。
     */
    public static boolean isEmpty(final Collection<?> collection) {
        if(collection == null || collection.isEmpty()) {
            return true;
        }
        
        return false;
    }
    
    public static boolean isNotEmpty(final Collection<?> collection) {
        return !isEmpty(collection);
    }
    
    /**
     * 配列がが空か判定する。 
     * @param arrays
     * @return nullまたは、配列のサイズが0のときにtrueを返す。
     */
    public static boolean isEmpty(final Object[] arrays) {
        if(arrays == null || arrays.length == 0) {
            return true;
        }
        
        return false;
    }
    
    /**
     * 配列が空でないか判定する
     * @param arrays
     * @return
     */
    public static boolean isNotEmpty(final Object[] arrays) {
        return !isEmpty(arrays);
    }
    
    /**
     * 文字列形式のロケールをオブジェクトに変換する。
     * <p>アンダーバーで区切った'ja_JP'を分解して、Localeに渡す。
     * @since 1.2
     * @param str
     * @return 引数が空の時はデフォルトロケールを返す。
     */
    public static Locale getLocale(final String str) {
        
        if(isEmpty(str)) {
            return Locale.getDefault();
        }
        
        if(!str.contains("_")) {
            return new Locale(str);
        }
        
        final String[] split = str.split("_");
        if(split.length == 2) {
            return new Locale(split[0], split[1]);
            
        } else {
            return new Locale(split[0], split[1], split[2]);
        }
        
    }
    
    /**
     * アノテーションの指定した属性値を取得する。
     * <p>アノテーションの修飾子はpublicである必要があります。</p>
     * @param anno アノテーションのインスタンス
     * @param attrName 属性名
     * @param attrType 属性のタイプ。
     * @return 属性を持たない場合、空を返す。
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getAnnotationAttribute(final Annotation anno, final String attrName, final Class<T> attrType) {
        
        try {
            final Method method = anno.annotationType().getMethod(attrName);
            method.setAccessible(true);
            if(!attrType.equals(method.getReturnType())) {
                return Optional.empty();
            }
            
            final Object value = method.invoke(anno);
            return Optional.of((T)value);
            
        } catch (Exception e) {
            return Optional.empty();
        }
        
    }
    
    /**
     * アノテーションの指定した属性値を持つかどうか判定する。
     * <p>アノテーションの修飾子はpublicである必要があります。</p>
     * @param anno アノテーションのインスタンス
     * @param attrName 属性名
     * @param attrType 属性のタイプ。
     * @return 属性を持つ場合trueを返す。
     */
    public static <T>  boolean hasAnnotationAttribute(final Annotation anno, final String attrName, final Class<T> attrType) {
        
        return getAnnotationAttribute(anno, attrName, attrType).isPresent();
        
    }
    
    /**
     * アノテーションの属性「cases」を持つ場合、指定した種類を持つか判定する。
     * <p>属性「buildCase」を持たない場合、または、空の配列の場合は、必ずtrueを返します。</p>
     * 
     * @param anno 判定対象のアノテーション。
     * @param buildCase 組み立てる種類。
     * @return trueの場合、指定した種類を含みます。
     * @throws NullPointerException anno or buildCase is null.
     */
    public static boolean containsBuildCase(final Annotation anno, final BuildCase buildCase) {
        
        Objects.requireNonNull(anno);
        Objects.requireNonNull(buildCase);
        
        final Optional<BuildCase[]> attrCases = getAnnotationAttribute(anno, "cases", BuildCase[].class);
        if(attrCases.isPresent()) {
            final BuildCase[] casesValue = attrCases.get();
            if(casesValue.length == 0) {
                // 値が空の配列の場合
                return true;
            }
            
            for(BuildCase value : casesValue) {
                if(value == buildCase) {
                    return true;
                }
            }
            
            return false;
        }
        
        // 属性を持たない場合
        return true;
    }
    
    /**
     * <a href="http://www.joda.org/joda-time/" target="_blank">Joda-Time</a>のライブラリが利用可能かどうか。
     * @return {@literal true}利用可能。
     */
    public static boolean isEnabledJodaTime() {
        return ENABLED_LIB_JODA_TIME;
    }
    
    /**
     * プリミティブ型の初期値を取得する。
     * @param type プリミティブ型のクラス型。
     * @return 非プリミティブ型や該当するクラスがない場合はnullを返す。
     * @throws NullPointerException type is null.
     */
    public static Object getPrimitiveDefaultValue(final Class<?> type) {
        
        Objects.requireNonNull(type, "type should not be null.");
        
        if(!type.isPrimitive()) {
            return null;
            
        } else if(boolean.class.isAssignableFrom(type)) {
            return false;
            
        } else if(char.class.isAssignableFrom(type)) {
            return '\u0000';
            
        } else if(byte.class.isAssignableFrom(type)) {
            return (byte)0;
            
        } else if(short.class.isAssignableFrom(type)) {
            return (short)0;
            
        } else if(int.class.isAssignableFrom(type)) {
            return 0;
            
        } else if(long.class.isAssignableFrom(type)) {
            return 0l;
            
        } else if(float.class.isAssignableFrom(type)) {
            return 0.0f;
            
        } else if(double.class.isAssignableFrom(type)) {
            return 0.0d;
        }
        
        return null;
        
    }
    
    /**
     * 文字列配列の結合
     * @param array1
     * @param array2
     * @return
     */
    public static String[] concat(final String[] array1, final String[] array2) {
        
        int size = array1.length + array2.length;
        List<String> list = new ArrayList<>(size);
        list.addAll(Arrays.asList(array1));
        list.addAll(Arrays.asList(array2));
        
        return list.toArray(new String[size]);
        
    }
    
    /**
     * コレクションを配列に変換する。
     * @param collection 変換対象のコレクション。
     * @return 変換した配列。
     * @throws NullPointerException collection is null.
     */
    public static int[] toArray(final Collection<Integer> collection) {
        Objects.requireNonNull(collection);
        
        final int size = collection.size();
        final int[] array = new int[size];
        
        int i=0;
        for(Integer value : collection) {
            array[i] = value;
            i++;
        }
        
        return array;
    }
    
    /**
     * 正規表現のフラグを組み立てる。
     * @param flags 正規表現の列挙型のフラグ
     * @return
     */
    public static int buildRegexFlags(final PatternFlag[] flags) {
        
        int intFlag = 0;
        for(PatternFlag flag : flags) {
            intFlag = intFlag | flag.getValue();
        }
        
        return intFlag;
        
    }
    
    /**
     * 先頭の文字を小文字にする。
     * @param str
     * @return 引数がnull、空文字の場合、そのまま返す。
     */
    public static String uncapitalize(final String str) {
        final int strLen;
        if(str == null || (strLen = str.length()) == 0) {
            return str;
        }
        
        return new StringBuilder(strLen)
            .append(String.valueOf(str.charAt(0)).toLowerCase())
            .append(str.substring(1))
            .toString();
    }
    
}
