package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.util.Optional;

import org.supercsv.cellprocessor.FmtBool;
import org.supercsv.cellprocessor.ift.BoolCellProcessor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvBooleanConverter;
import org.supercsv.ext.annotation.CsvColumn;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.cellprocessor.ParseBoolean;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;
import org.supercsv.ext.util.Utils;


/**
 * boolean/Boolean型を{@link CellProcessor}を組み立てるためのクラス。
 * 
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class BooleanCellProcessorBuilder extends AbstractCellProcessorBuilder<Boolean> {
    
    @Override
    protected CellProcessor buildInputCellProcessorWithConvertNullTo(final Class<Boolean> type, final Annotation[] annos,
            final CellProcessor cellProcessor, final CsvColumn csvColumnAnno) {
        
        // プリミティブ型の場合、オプションかつ初期値が与えられていない場合、falseに変換する。
        if(type.isPrimitive() && csvColumnAnno.optional() && csvColumnAnno.inputDefaultValue().isEmpty()) {
            return prependConvertNullToProcessor(type, annos, cellProcessor, false);
            
        } else if(!csvColumnAnno.inputDefaultValue().isEmpty()) {
            Optional<Boolean> value = parseValue(type, annos, csvColumnAnno.inputDefaultValue());
            return prependConvertNullToProcessor(type, annos, cellProcessor, value.get());
        }
        
        return cellProcessor;
    }
    
    /**
     * アノテーション{@link CsvBooleanConverter} を取得する。
     * @param annos アノテーションの一覧
     * @return アノテーションがない場合は空を返す。
     */
    protected Optional<CsvBooleanConverter> getBooleanConverterAnnotation(final Annotation[] annos) {
        
        return getAnnotation(annos, CsvBooleanConverter.class);
        
    }
    
    /**
     * trueの値を出力するときの文字列を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合は、デフォルト値を返す。
     */
    protected String getOutputTrueValue(final Optional<CsvBooleanConverter> converterAnno) {
        
        return converterAnno.map(a -> a.outputTrueValue())
                .orElse("true");
    }
    
    /**
     * falseの値を出力するときの文字列を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合は、デフォルト値を返す。
     */
    protected String getOutputFalseValue(final Optional<CsvBooleanConverter> converterAnno) {
        
        return converterAnno.map(a -> a.outputFalseValue())
                .orElse("false");
        
    }
    
    /**
     * trueの値として読み込む文字列の候補を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合は、デフォルト値を返す。
     */
    protected String[] getInputTrueValue(final Optional<CsvBooleanConverter> converterAnno) {
        
        return converterAnno.map(a -> a.inputTrueValue())
                .orElse(new String[]{"true", "1", "yes", "on", "y", "t"});
        
    }
    
    /**
     * falseの値として読み込む文字列の候補を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return アノテーションがない場合は、デフォルト値を返す。
     */
    protected String[] getInputFalseValue(final Optional<CsvBooleanConverter> converterAnno) {
        
        return converterAnno.map(a -> a.inputFalseValue())
                .orElse(new String[]{"false", "0", "no", "off", "f", "n"});
        
    }
    
    /**
     * 大文字・小文字を無視して読み込むかの設定を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return trueの場合、大文字・小文字を無視して読み込む。
     */
    protected boolean getIgnoreCase(final Optional<CsvBooleanConverter> converterAnno) {
        
        return converterAnno.map(a -> a.ignoreCase())
                .orElse(false);
    }
    
    /**
     * 読み込み時に候補値に一致しない場合に、falseとして読み込むかの設定を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return trueの場合、大文字・小文字を無視して読み込む。
     */
    protected boolean getFailToFalse(final Optional<CsvBooleanConverter> converterAnno) {
        
        return converterAnno.map(a -> a.failToFalse())
                .orElse(false);
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<Boolean> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final Optional<CsvBooleanConverter> converterAnno = getBooleanConverterAnnotation(annos);
        final String trueValue = getOutputTrueValue(converterAnno);
        final String falseValue = getOutputFalseValue(converterAnno);
        
        CellProcessor cp = processor;
        cp = (cp == null 
                ? new FmtBool(trueValue, falseValue) : new FmtBool(trueValue, falseValue, (StringCellProcessor) cp));
        return cp;
        
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<Boolean> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final Optional<CsvBooleanConverter> converterAnno = getBooleanConverterAnnotation(annos);
        final String[] trueValue = getInputTrueValue(converterAnno);
        final String[] falseValue = getInputFalseValue(converterAnno);
        final boolean ignoreCase = getIgnoreCase(converterAnno);
        final boolean failToFalse = getFailToFalse(converterAnno);
        
        CellProcessor cp = processor;
        cp = (cp == null
                ? new ParseBoolean(trueValue, falseValue, ignoreCase).setFailToFalse(failToFalse) :
                    new ParseBoolean(trueValue, falseValue, ignoreCase, (BoolCellProcessor) cp).setFailToFalse(failToFalse));
        
        return cp;
    }
    
    @Override
    public Optional<Boolean> parseValue(final Class<Boolean> type, final Annotation[] annos, final String strValue) {
        
        final Optional<CsvBooleanConverter> converterAnno = getBooleanConverterAnnotation(annos);
        final String[] trueValue = getInputTrueValue(converterAnno);
        final String[] falseValue = getInputFalseValue(converterAnno);
        final boolean ignoreCase = getIgnoreCase(converterAnno);
        final boolean failToFalse = getFailToFalse(converterAnno);
        
        for(String trueStr : trueValue) {
            if(ignoreCase && trueStr.equalsIgnoreCase(strValue)) {
                return Optional.of(Boolean.TRUE);
            } else if(!ignoreCase && trueStr.equals(strValue)) {
                return Optional.of(Boolean.TRUE);
            }
        }
        
        for(String falseStr : falseValue) {
            if(ignoreCase && falseStr.equalsIgnoreCase(strValue)) {
                return Optional.of(Boolean.FALSE);
            } else if(!ignoreCase && falseStr.equals(strValue)) {
                return Optional.of(Boolean.FALSE);
            }
        }
        
        if(Utils.isEmpty(strValue) && type.isAssignableFrom(Boolean.class)) {
            return Optional.empty();
        }
        
        if(failToFalse) {
            return Optional.of(Boolean.FALSE);
        }
        
        throw new SuperCsvInvalidAnnotationException(String.format("defaultValue '%s' cannot parse.", strValue));
    }
    
    
}
