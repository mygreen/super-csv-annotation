package org.supercsv.ext.builder;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Optional;

import org.supercsv.cellprocessor.ConvertNullTo;
import org.supercsv.cellprocessor.constraint.Equals;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.constraint.Unique;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.cellprocessor.Trim;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;


/**
 * {@link CellProcessorBuilder} のベース、テンプレートクラス。
 * <p>{@link CellProcessorBuilder}を実装する際には、基本的にこのクラスを実装します。
 * 
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractCellProcessorBuilder<T> implements CellProcessorBuilder<T> {
    
    /**
     * 指定したアノテーションのクラスを取得する。
     * @since 1.2
     * @param annos アノテーションの一覧。
     * @param clazz 取得したいアノテーションのクラス。
     * @return 絞り込んだアノテーションのインスタンス。
     */
    protected static <A extends Annotation> Optional<A> getAnnotation(final Annotation[] annos, final Class<A> clazz) {
        
        return Arrays.stream(annos)
                .filter(a -> clazz.isAssignableFrom(a.getClass()))
                .map(a -> clazz.cast(a))
                .findFirst();
    }
    
    /**
     * カラム定義用のアノテーション{@link CsvColumn}を絞り込みます。
     * @param annos 付与されているアノテーション一覧。
     * @return {@link CsvColumn}の値。
     * @throws SuperCsvInvalidAnnotationException {@link CsvColumn}が見つからない場合。
     */
    protected CsvColumn getColumnAnnotation(final Annotation[] annos) {
        
        return getAnnotation(annos, CsvColumn.class)
                .orElseThrow(() -> new SuperCsvInvalidAnnotationException(
                        String.format("not found anotation '%s'", CsvColumn.class.getName())));
        
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<T> type, final Annotation[] annos,
            final boolean ignoreValidationProcessor) {
        
        final CsvColumn csvColumnAnno = getColumnAnnotation(annos);
        
        CellProcessor cellProcessor = null;
        
        if(csvColumnAnno.trim()) {
            cellProcessor = prependTrimProcessor(type, annos, cellProcessor);
        }
        
        cellProcessor = buildOutputCellProcessor(type, annos, cellProcessor, ignoreValidationProcessor);
        
        if(csvColumnAnno.unique() && !ignoreValidationProcessor) {
            cellProcessor = prependUniqueProcessor(type, annos, cellProcessor);
        }
        
        if(!csvColumnAnno.equalsValue().isEmpty() && !ignoreValidationProcessor) {
            cellProcessor = prependEqualsProcessor(type, annos, cellProcessor,
                    parseValue(type, annos, csvColumnAnno.equalsValue()).get());
        }
        
        if(csvColumnAnno.optional()) {
            cellProcessor = prependOptionalProcessor(type, annos, cellProcessor);
        } else {
            cellProcessor = prependNotNullProcessor(type, annos, cellProcessor);
        }
        
        cellProcessor = buildOutputCellProcessorWithConvertNullTo(type, annos, cellProcessor, ignoreValidationProcessor, csvColumnAnno);
        
        return cellProcessor;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<T> type, final Annotation[] annos) {
        
        final CsvColumn csvColumnAnno = getColumnAnnotation(annos);
        
        CellProcessor cellProcessor = null;
        
        cellProcessor = buildInputCellProcessor(type, annos, cellProcessor);
        
        if(csvColumnAnno.unique()) {
            cellProcessor = prependUniqueProcessor(type, annos, cellProcessor);
        }
        
        if(!csvColumnAnno.equalsValue().isEmpty()) {
            cellProcessor = prependEqualsProcessor(type, annos, cellProcessor,
                    csvColumnAnno.equalsValue());
        }
        if(csvColumnAnno.trim()) {
            cellProcessor = prependTrimProcessor(type, annos, cellProcessor);
        }
        
        if(csvColumnAnno.optional()) {
            cellProcessor = prependOptionalProcessor(type, annos, cellProcessor);
        } else {
            cellProcessor = prependNotNullProcessor(type, annos, cellProcessor);
        }
        
        cellProcessor = buildInputCellProcessorWithConvertNullTo(type, annos, cellProcessor, csvColumnAnno);
        
        return cellProcessor;
    }
    
    /**
     * 書き込み時の{@link CellProcessor}である{@link ConvertNullTo}を組み立てる。
     * <p>{@link ConvertNullTo}は、値がnullの時に指定した値に入れ替えるための{@link CellProcessor}。</p>
     * 
     * @since 1.2
     * @param type フィールドのクラスタイプ。
     * @param annos フィールドに付与された全てのアノテーション。
     * @param cellProcessor 組み立て途中の{@link CellProcessor}
     * @param ignoreValidationProcessor 値がtrueの場合、最大値の検証などの制約チェックをするCellProcessorは無視し、組み込まない。
     * @param csvColumnAnno カラム定義用のアノテーション{@link CsvColumn}
     * @return 組み立てた{@link CellProcessor}
     */
    protected CellProcessor buildOutputCellProcessorWithConvertNullTo(final Class<T> type, final Annotation[] annos, final CellProcessor cellProcessor,
            final boolean ignoreValidationProcessor, final CsvColumn csvColumnAnno) {
        
        if(!csvColumnAnno.outputDefaultValue().isEmpty()) {
            final Object defaultValue = csvColumnAnno.outputDefaultValue();
            return prependConvertNullToProcessor(type, annos, cellProcessor, defaultValue);
        }
        
        return cellProcessor;
    }
    
    /**
     * 読み込み時の{@link CellProcessor}である{@link ConvertNullTo}を組み立てる。
     * 
     * @since 1.2
     * @param type フィールドのクラスタイプ。
     * @param annos フィールドに付与された全てのアノテーション。
     * @param cellProcessor 組み立て途中の{@link CellProcessor}
     * @param csvColumnAnno カラム定義用のアノテーション{@link CsvColumn}
     * @return 組み立てた{@link CellProcessor}
     */
    protected CellProcessor buildInputCellProcessorWithConvertNullTo(final Class<T> type, final Annotation[] annos, final CellProcessor cellProcessor,
            final CsvColumn csvColumnAnno) {
        
        if(!csvColumnAnno.inputDefaultValue().isEmpty()) {
            Optional<T> value = parseValue(type, annos, csvColumnAnno.inputDefaultValue());
            return prependConvertNullToProcessor(type, annos, cellProcessor, value.get());
        }
        
        return cellProcessor;
    }
    
    /**
     * 組み立て途中の{@link CellProcessor}に{@link ConvertNullTo}を、Chainの前に追加する。
     * <p>{@link ConvertNullTo}は、値がnullの時に指定した値に入れ替えるための{@link CellProcessor}。</p>
     * 
     * @param type フィールドのクラスタイプ。
     * @param annos フィールドに付与された全てのアノテーション。
     * @param cellProcessor 組み立て途中の{@link CellProcessor}
     * @param value nullの場合に入れ替える値。
     * @return 組み立てた{@link CellProcessor}
     */
    protected CellProcessor prependConvertNullToProcessor(final Class<T> type, final Annotation[] annos, final CellProcessor cellProcessor,
            final Object value) {
        
        return (cellProcessor == null ? 
                new ConvertNullTo(value) : new ConvertNullTo(value, cellProcessor));
    }
    
    /**
     * 組み立て途中の{@link CellProcessor}に{@link Equals}を、Chainの前に追加する。
     * <p>{@link Equals}は、カラムの値が指定した値と等しいかチェックするための{@link CellProcessor}。</p>
     * 
     * @param type フィールドのクラスタイプ。
     * @param annos フィールドに付与された全てのアノテーション。
     * @param cellProcessor 組み立て途中の{@link CellProcessor}
     * @param value 比較対象の値。
     * @return 組み立てた{@link CellProcessor}
     */
    protected CellProcessor prependEqualsProcessor(final Class<T> type, final Annotation[] annos, final CellProcessor cellProcessor,
            final Object value) {
        
        return (cellProcessor == null ? 
                new Equals(value) : new Equals(value, cellProcessor));
    }
    
    /**
     * 組み立て途中の{@link CellProcessor}に{@link Unique}を、Chainの前に追加する。
     * <p>{@link Unique}は、カラムの値が他の行のカラムの値と異なるかチェックするための{@link CellProcessor}。</p>
     * 
     * @param type フィールドのクラスタイプ。
     * @param annos フィールドに付与された全てのアノテーション。
     * @param cellProcessor 組み立て途中の{@link CellProcessor}
     * @return 組み立てた{@link CellProcessor}
     */
    protected CellProcessor prependUniqueProcessor(final Class<T> type, final Annotation[] annos, final CellProcessor cellProcessor) {
        return (cellProcessor == null ? new Unique() : new Unique(cellProcessor));
    }
    
    /**
     * 組み立て途中の{@link CellProcessor}に{@link org.supercsv.cellprocessor.Optional}を、Chainの前に追加する。
     * <p>{@link org.supercsv.cellprocessor.Optional}は、カラムの値が空、nullの場合を許可するための{@link CellProcessor}。</p>
     * 
     * @param type フィールドのクラスタイプ。
     * @param annos フィールドに付与された全てのアノテーション。
     * @param cellProcessor 組み立て途中の{@link CellProcessor}
     * @return 組み立てた{@link CellProcessor}
     */
    protected CellProcessor prependOptionalProcessor(final Class<T> type, final Annotation[] annos, final CellProcessor cellProcessor) {
        return (cellProcessor == null ? new org.supercsv.cellprocessor.Optional() : new org.supercsv.cellprocessor.Optional(cellProcessor));
    }
    
    /**
     * 組み立て途中の{@link CellProcessor}に{@link NotNull}を、Chainの前に追加する。
     * <p>{@link NotNull}は、カラムの値がnullの場合を許可しないための{@link CellProcessor}。</p>
     * 
     * @param type フィールドのクラスタイプ。
     * @param annos フィールドに付与された全てのアノテーション。
     * @param cellProcessor 組み立て途中の{@link CellProcessor}
     * @return 組み立てた{@link CellProcessor}
     */
    protected CellProcessor prependNotNullProcessor(final Class<T> type, final Annotation[] annos, final CellProcessor cellProcessor) {
        return (cellProcessor == null ? new NotNull() : new NotNull(cellProcessor));
    }
    
    /**
     * 組み立て途中の{@link CellProcessor}に{@link Trim}を、Chainの前に追加する。
     * <p>{@link Trim}は、カラムの値をトリムするための{@link CellProcessor}。</p>
     * 
     * @param type フィールドのクラスタイプ。
     * @param annos フィールドに付与された全てのアノテーション。
     * @param cellProcessor 組み立て途中の{@link CellProcessor}
     * @return 組み立てた{@link CellProcessor}
     */
    protected CellProcessor prependTrimProcessor(final Class<T> type, final Annotation[] annos, final CellProcessor cellProcessor) {
        return (cellProcessor == null ? new Trim() : new Trim(cellProcessor));
    }
    
    /**
     * 書き込み用の各クラスタイプごとなどの固有の{@link CellProcessor}を組み立てる。
     * <p>固有のCellProcessorは、共通のCellProcessorの後に追加される。</p>
     * 
     * @param type フィールドのクラスタイプ。
     * @param annos フィールドに付与された全てのアノテーション。
     * @param cellProcessor 組み立て途中の{@link CellProcessor}
     * @param ignoreValidationProcessor 最大値の検証などの制約チェックをするCellProcessorは無視し、組み込まない。
     * @return 組み立てた{@link CellProcessor}
     */
    public abstract CellProcessor buildOutputCellProcessor(Class<T> type, Annotation[] annos, CellProcessor cellProcessor,
            boolean ignoreValidationProcessor);
    
    /**
     * 読み込み用の各クラスタイプごとなどの固有の{@link CellProcessor}を組み立てる。
     * <p>固有のCellProcessorは、共通のCellProcessorの後に追加される。</p>
     * 
     * @param type フィールドのクラスタイプ。
     * @param annos フィールドに付与された全てのアノテーション。
     * @param cellProcessor 組み立て途中の{@link CellProcessor}
     * @return 組み立てた{@link CellProcessor}
     */
    public abstract CellProcessor buildInputCellProcessor(Class<T> type, Annotation[] annos, CellProcessor cellProcessor);
    
    /**
     * 指定したクラスタイプに文字列を変換する。
     * <p>アノテーションによって変換規則が定義されている場合は、それらに沿った値に変換する。</p>
     * <p>初期値や最大値などクラスタイプ固有の値で指定された文字列を変換するために使用する。</p>
     * 
     * @param type フィールドのクラスタイプ。
     * @param annos フィールドに付与された全てのアノテーション。
     * @param strValue 変換対象の文字列。
     * @return 変換された値。
     */
    public abstract Optional<T> parseValue(Class<T> type, Annotation[] annos, String strValue);
    
}
