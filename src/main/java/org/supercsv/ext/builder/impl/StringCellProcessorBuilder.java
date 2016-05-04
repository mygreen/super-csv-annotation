package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.supercsv.cellprocessor.constraint.ForbidSubStr;
import org.supercsv.cellprocessor.constraint.RequireSubStr;
import org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty;
import org.supercsv.cellprocessor.constraint.StrRegEx;
import org.supercsv.cellprocessor.constraint.Strlen;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.annotation.CsvStringConverter;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.builder.CellProcessorBuilder;
import org.supercsv.ext.cellprocessor.constraint.Length;
import org.supercsv.ext.cellprocessor.constraint.MaxLength;
import org.supercsv.ext.cellprocessor.constraint.MinLength;
import org.supercsv.ext.util.Utils;


/**
 * String型の{@link CellProcessorBuilder}クラス。
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class StringCellProcessorBuilder extends AbstractCellProcessorBuilder<String> {
    
    /**
     * 初期値を空文字として読み込むためのマジックナンバー。
     */
    public static final String CONVERT_NULL_STRING_EMPTY = "@empty";
    
    /**
     * アノテーション{@link CsvStringConverter} を取得する。
     * @param annos アノテーションの一覧
     * @return アノテーションがない場合は空を返す。
     */
    protected Optional<CsvStringConverter> getStringConverterAnnotation(final Annotation[] annos) {
        
        return getAnnotation(annos, CsvStringConverter.class);
        
    }
    
    /**
     * 最小文字長を取得する。
     * @param converterAnno アノテーションの一覧
     * @return アノテーションがない場合は、空を返す。
     */
    protected Optional<Integer> getMinLength(final Optional<CsvStringConverter> converterAnno) {
        
        return converterAnno.map(a -> a.minLength())
                .filter(n -> n >= 0);
        
    }
    
    /**
     * 最大文字長を取得する。
     * @param converterAnno アノテーションの一覧
     * @return アノテーションがない場合は、空を返す。
     */
    protected Optional<Integer> getMaxLength(final Optional<CsvStringConverter> converterAnno) {
        
        return converterAnno.map(a -> a.maxLength())
                .filter(n -> n >= 0);
    }
    
    /**
     * 文字長を取得する。
     * @param converterAnno アノテーションの一覧
     * @return アノテーションがない場合は、空の配列を返す。
     */
    protected int[] getExactLength(final Optional<CsvStringConverter> converterAnno) {
        
        return converterAnno.map(a -> a.exactLength())
                .orElse(new int[0]);
    }
    
    /**
     * 正規表現を取得する。
     * @param converterAnno アノテーションの一覧
     * @return アノテーションがない場合は、空を返す。
     */
    protected Optional<String> getRegex(final Optional<CsvStringConverter> converterAnno) {
        
        return converterAnno.map(a -> a.regex())
                .filter(s -> s.length() > 0);
    }
    
    /**
     * 禁止語彙を取得する。
     * @param converterAnno アノテーションの一覧
     * @return アノテーションがない場合は、空の配列を返す。
     */
    protected String[] getForbid(final Optional<CsvStringConverter> converterAnno) {
        
        return converterAnno.map(a -> a.forbid())
                .orElse(new String[0]);
        
    }
    
    /**
     * 必須語彙を取得する。
     * @param converterAnno アノテーションの一覧
     * @return アノテーションがない場合は、空の配列を返す。
     */
    protected String[] getContain(final Optional<CsvStringConverter> converterAnno) {
        
        return converterAnno.map(a -> a.contain())
                .orElse(new String[0]);
    }
    
    /**
     * 値がnullか空文字を許可するかどうかの設定を取得します。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return trueの場合、nullか空文字を許可します。
     */
    protected boolean getNotEmpty(final Optional<CsvStringConverter> converterAnno) {
        
        return converterAnno.map(a -> a.notEmpty())
                .orElse(false);
    }
    
    @Override
    protected CellProcessor buildOutputCellProcessorWithConvertNullTo(final Class<String> type, final Annotation[] annos, 
            final CellProcessor processor, final boolean ignoreValidationProcessor, final CsvColumn csvColumnAnno) {
        
        if(!csvColumnAnno.outputDefaultValue().isEmpty()) {
            final Optional<String> defaultValue = parseValue(type, annos, csvColumnAnno.outputDefaultValue());
            return prependConvertNullToProcessor(type, annos, processor, defaultValue.get());
        }
        
        return processor;
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<String> type, final  Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final Optional<CsvStringConverter> converterAnno = getStringConverterAnnotation(annos);
        final Optional<Integer> minLength = getMinLength(converterAnno);
        final Optional<Integer> maxLength = getMaxLength(converterAnno);
        final int[] exactLength = getExactLength(converterAnno);
        final Optional<String> regex = getRegex(converterAnno);
        final String[] forbid = getForbid(converterAnno);
        final String[] contain = getContain(converterAnno);
        final boolean notEmpty = getNotEmpty(converterAnno);
        
        CellProcessor cp = processor;
        
        if(!ignoreValidationProcessor) {
            cp = prependForbidProcessor(cp, forbid);
            cp = prependContainProcessor(cp, contain);
            cp = prependRegExProcessor(cp, regex);
            cp = prependLengthProcessor(cp, minLength, maxLength, exactLength);
            cp = prependNotEmptyProcessor(cp, notEmpty);
        }
        
        return cp;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<String> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final Optional<CsvStringConverter> converterAnno = getStringConverterAnnotation(annos);
        final Optional<Integer> minLength = getMinLength(converterAnno);
        final Optional<Integer> maxLength = getMaxLength(converterAnno);
        final int[] exactLength = getExactLength(converterAnno);
        final Optional<String> regex = getRegex(converterAnno);
        final String[] forbid = getForbid(converterAnno);
        final String[] contain = getContain(converterAnno);
        final boolean notEmpty = getNotEmpty(converterAnno);
        
        CellProcessor cp = processor;
        cp = prependForbidProcessor(cp, forbid);
        cp = prependContainProcessor(cp, contain);
        cp = prependRegExProcessor(cp, regex);
        cp = prependLengthProcessor(cp, minLength, maxLength, exactLength);
        cp = prependNotEmptyProcessor(cp, notEmpty);
        
        return cp;
    }
    
    /**
     * 文字長をチェックする{@link CellProcessor}をChainの前に追加する。
     * @param processor 組み立て途中の{@link CellProcessor}
     * @param minLength 最小文字長
     * @param maxLength 最大文字長
     * @param exactLength 文字長
     * @return 組み立てた{@link CellProcessor}
     */
    protected CellProcessor prependLengthProcessor(final CellProcessor processor, 
            final Optional<Integer> minLength, final Optional<Integer> maxLength, final int[] exactLength) {
        
        if(minLength.isPresent() && maxLength.isPresent()) {
            return (processor == null ? 
                    new Length(minLength.get(), maxLength.get()) : new Length(minLength.get(), maxLength.get(), processor));
            
        } else if(minLength.isPresent()) {
            return (processor == null ? 
                    new MinLength(minLength.get()) : new MinLength(minLength.get(), processor));
            
        } else if(maxLength.isPresent()) {
            return (processor == null ? 
                    new MaxLength(maxLength.get()) : new MaxLength(maxLength.get(), processor));
            
        } else if(exactLength.length > 0) {
            return (processor == null ? 
                    new Strlen(exactLength) : new Strlen(exactLength, processor));
            
        }
        
        return processor;
    }
    
    /**
     * 指定した正規表現のパターンに一致するかチェックする{@link CellProcessor}をChainの前に追加する。
     * @param cellProcessor 組み立て途中の{@link CellProcessor}
     * @param regex 正規表現
     * @return 組み立てた{@link CellProcessor}
     */
    protected CellProcessor prependRegExProcessor(final CellProcessor cellProcessor, final Optional<String> regex) {
        
        if(regex.isPresent()) {
            return (cellProcessor == null ?
                    new StrRegEx(regex.get()) : new StrRegEx(regex.get(), (StringCellProcessor) cellProcessor));
            }
        
        return cellProcessor;
    }
    
    /**
     * 指定した禁止語彙を含まないかチェックする{@link CellProcessor}をChainの前に追加する。
     * @param cellProcessor 組み立て途中の{@link CellProcessor}
     * @param forbid 禁止語彙
     * @return 組み立てた{@link CellProcessor}
     */
    protected CellProcessor prependForbidProcessor(final CellProcessor cellProcessor, final String[] forbid) {
        
        if(forbid.length == 0) {
            return cellProcessor;
        }
        
        return (cellProcessor == null ?
                new ForbidSubStr(forbid) : new ForbidSubStr(forbid, (StringCellProcessor) cellProcessor));
        
    }
    
    /**
     * 指定した必須語彙を含むかチェックする{@link CellProcessor}をChainの前に追加する。
     * @param cellProcessor 組み立て途中の{@link CellProcessor}
     * @param contains 必須語彙
     * @return 組み立てた{@link CellProcessor}
     */
    protected CellProcessor prependContainProcessor(final CellProcessor cellProcessor, final String[] contains) {
        
        if(contains.length == 0) {
            return cellProcessor;
        }
        
        return (cellProcessor == null ?
                new RequireSubStr(contains) : new RequireSubStr(contains, (StringCellProcessor) cellProcessor));
        
    }
    
    /**
     * 文字がnullまたは空文字を許可しないかチェックする{@link CellProcessor}をChainの前に追加する。
     * @param processor 組み立て途中の{@link CellProcessor}
     * @param notEmpty nullまたは空文字を許可しないかどうか。
     * @return 組み立てた{@link CellProcessor}
     */
    protected CellProcessor prependNotEmptyProcessor(final CellProcessor processor, final boolean notEmpty) {
        
        if(!notEmpty) {
            return processor;
        }
        
        return (processor == null ?
                new StrNotNullOrEmpty() : new StrNotNullOrEmpty((StringCellProcessor) processor));
        
    }
    
    @Override
    public Optional<String> parseValue(final Class<String> type, final Annotation[] annos, final String strValue) {
        
        if(Utils.isEmpty(strValue)) {
            return Optional.empty();
        }
        
        if(strValue.equals(CONVERT_NULL_STRING_EMPTY)) {
            return Optional.of("");
        }
        return Optional.of(strValue);
    }
}
