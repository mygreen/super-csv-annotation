package org.supercsv.ext.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.Equals;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.builder.CellProcessorBuilder;
import org.supercsv.ext.builder.DefaultCellProcessorBuilder;
import org.supercsv.ext.cellprocessor.Trim;


/**
 * CSVのカラムを定義するためのアノテーション。
 * <p>初期値や制約などを定義するためにも利用します。
 * 
 * @version 1.1
 * @since 1.0
 * @author T.TSUCHIE
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvColumn {
    
    /**
     * カラムのインデックスを指定します。
     * <p>他のカラムの値との重複や抜けは許可しません。</p>
     * @return インデックスは0(ゼロ)から始まります。
     */
    int position();
    
    /**
     * 見出しとなるラベルを指定します。
     * <p>省略した場合、フィールド名が利用されます。
     */
    String label() default "";
    
    /**
     * カラムの値がnullを許可するか指定します。
     * <p>trueの場合、{@link CellProcessor}の{@link Optional}が設定されます。</p>
     * <p>falseの場合、{@link CellProcessor}の{@link NotNull}が設定されます。</p>
     * 
     * @return trueの場合、カラムの値としてnullまたは空を許可します。
     */
    boolean optional() default false;
    
    /**
     * 値をトリムするか指定します。
     * <p>trueの場合、{@link CellProcessor}の{@link Trim}が設定されます。</p>
     * @return trueの場合、値をトリミングします。
     */
    boolean trim() default false;
    
    /**
     * 読み込み時のデフォルト値を指定します。
     * <p>{@link CellProcessor}の{@link ConvertNullTo}が設定されます。</p>
     * <p>文字列型の場合、空文字として読み込みたい場合は、特殊なマジックナンバー {@literal @empty} を指定します。
     * @return カラムの値が空の時に代替となる値を指定します。
     *    ただし、ブール型、数値や日時型の場合は、アノテーションで指定した書式に沿った値を指定する必要があります。
     */
    String inputDefaultValue() default "";
    
    /**
     * 書き込み時のデフォルト値を指定します。
     * <p>{@link CellProcessor}の{@link ConvertNullTo}が設定されます。</p>
     * <p>文字列型の場合、空文字として読み込みたい場合は、特殊なマジックナンバー {@literal @empty} を指定します。
     * @return カラムの値が空の時に代替となる値を指定します。
     * 
     */
    String outputDefaultValue() default "";
    
    
    /**
     * カラムの値が他の行と比較してユニークであるかチェックするか指定します。
     * <p>trueの場合、{@link CellProcessor}の{@link Unique}が設定されます。</p>
     * @return trueの場合、値がユニークかチェックします。
     */
    boolean unique() default false;
    
    /**
     * カラムの値が指定した値と等しいかどうかチェックするか指定します。
     * <p>ブール型、数値や日時型の場合は、アノテーションで指定した書式に沿った値を指定する必要があります。</p>
     * <p>trueの場合、{@link CellProcessor}の{@link Equals}が指定されます。</p>
     * @return trueの場合、等しいかチェックします。
     */
    String equalsValue() default "";
    
    /**
     * 独自の{@link CellProcessorBuilder}を指定して{@link CellProcessor} を組み立てたい場合に指定します。
     * @return {@link CellProcessorBuilder}を実装したクラスを指定します。
     */
    @SuppressWarnings("rawtypes")
    Class<? extends CellProcessorBuilder> builderClass() default DefaultCellProcessorBuilder.class;
}
