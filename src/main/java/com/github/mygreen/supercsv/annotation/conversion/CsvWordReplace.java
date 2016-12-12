package com.github.mygreen.supercsv.annotation.conversion;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.cellprocessor.conversion.ReplacedWordProvider;

/**
 * 語彙に一致した文字列を置換します。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(CsvWordReplace.List.class)
@CsvConversion(value={})
public @interface CsvWordReplace {
    
    /**
     * 置換対象の語彙の一覧
     * @return 複数指定する場合、{@link #replacements()}と同じ個数を指定します。
     */
    String[] words() default {};
    
    /**
     * 置換後の値の一覧
     * @return 複数指定する場合、{@link #words()}と同じ個数を指定します。
     */
    String[] replacements() default {};
    
    /**
     * 語彙を取得するプロバイダクラスを指定します。
     * @return 指定する場合は実装クラスを指定ます。
     */
    Class<? extends ReplacedWordProvider>[] provider() default {};
    
    /**
     * 適用するケースを指定します。
     * @return 何も指定しない場合は全てのケースに適用されます。
     */
    BuildCase[] cases() default {};
    
    /**
     * グループのクラスを指定します。
     * <p>処理ごとに適用するアノテーションを切り替えたい場合に指定します。
     * @return 指定しない場合は、{@link DefaultGroup}が適用され全ての処理に適用されます。
     */
    Class<?>[] groups() default {};
    
    /**
     * アノテーションの処理順序の定義。
     * @return 値が大きいほど後に実行されます。
     *         値が同じ場合は、アノテーションのクラス名の昇順になります。
     */
    int order() default 0;
    
    /**
     * アノテーションを複数個指定する際の要素です。
     */
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        
        CsvWordReplace[] value();
    }
    
}