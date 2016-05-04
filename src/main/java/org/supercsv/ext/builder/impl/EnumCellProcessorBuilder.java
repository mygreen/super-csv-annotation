package org.supercsv.ext.builder.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.EnumSet;
import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.cellprocessor.ift.StringCellProcessor;
import org.supercsv.ext.annotation.CsvEnumConverter;
import org.supercsv.ext.builder.AbstractCellProcessorBuilder;
import org.supercsv.ext.cellprocessor.FormatEnum;
import org.supercsv.ext.cellprocessor.ParseEnum;
import org.supercsv.ext.exception.SuperCsvInvalidAnnotationException;
import org.supercsv.ext.util.Utils;


/**
 * 列挙型の{@link CellProcessor}を組み立てるためのクラス。
 *
 * @version 1.2
 * @author T.TSUCHIE
 *
 */
public class EnumCellProcessorBuilder<T extends Enum<T>> extends AbstractCellProcessorBuilder<T> {
    
    /**
     * アノテーション{@link CsvEnumConverter} を取得する。
     * @param annos アノテーションの一覧
     * @return アノテーションがない場合は空を返す。
     */
    protected Optional<CsvEnumConverter> getEnumConverterAnnotation(final Annotation[] annos) {
        
        return getAnnotation(annos, CsvEnumConverter.class);
        
    }
    
    /**
     * 大文字・小文字を無視して読み込むかの設定を取得する。
     * @param converterAnno 変換規則を定義したアノテーション。
     * @return trueの場合、大文字・小文字を無視して読み込む。
     */
    protected boolean getIgnoreCase(final Optional<CsvEnumConverter> converterAnno) {
        
        return converterAnno.map(a -> a.ignoreCase())
                .orElse(false);
    }
    
    protected Optional<String> getValueMethodName(final Optional<CsvEnumConverter> converterAnno) {
        
        return converterAnno.map(a -> a.valueMethodName())
                .filter(s -> s.length() > 0);
        
    }
    
    @Override
    public CellProcessor buildOutputCellProcessor(final Class<T> type, final Annotation[] annos,
            final CellProcessor processor, final boolean ignoreValidationProcessor) {
        
        final Optional<CsvEnumConverter> converterAnno = getEnumConverterAnnotation(annos);
        final Optional<String> valueMethodName = getValueMethodName(converterAnno);
        
        CellProcessor cp = processor;
        if(valueMethodName.isPresent()) {
            cp = (cp == null ? 
                    new FormatEnum(type, valueMethodName.get()) :
                        new FormatEnum(type, valueMethodName.get(), (StringCellProcessor) cp));
        }
        
        return cp;
    }
    
    @Override
    public CellProcessor buildInputCellProcessor(final Class<T> type, final Annotation[] annos,
            final CellProcessor processor) {
        
        final Optional<CsvEnumConverter> converterAnno = getEnumConverterAnnotation(annos);
        final boolean ignoreCase = getIgnoreCase(converterAnno);
        final Optional<String> valueMethodName = getValueMethodName(converterAnno);
        
        CellProcessor cp = processor;
        if(valueMethodName.isPresent()) {
            cp = (cp == null ? 
                    new ParseEnum(type, ignoreCase, valueMethodName.get()) :
                        new ParseEnum(type, ignoreCase, valueMethodName.get(), cp));
        } else {
            cp = (cp == null ? 
                    new ParseEnum(type, ignoreCase) : new ParseEnum(type, ignoreCase, cp));
        }
        
        return cp;
    }
    
    
    @Override
    public Optional<T> parseValue(final Class<T> type, final Annotation[] annos, final String strValue) {
        
        if(Utils.isEmpty(strValue)) {
            return Optional.empty();
        }
        
        final Optional<CsvEnumConverter> converterAnno = getEnumConverterAnnotation(annos);
        final boolean ignoreCase = getIgnoreCase(converterAnno);
        final Optional<String> valueMethodName = getValueMethodName(converterAnno);
        
        final EnumSet<T> set = EnumSet.allOf(type);
        if(valueMethodName.isPresent()) {
            try {
                final Method valueMethod = type.getMethod(valueMethodName.get());
                valueMethod.setAccessible(true);
                
                for(T e: set) {
                    final String value = valueMethod.invoke(e).toString();
                    if(strValue.equals(value)) {
                        return Optional.of(e);
                    }
                    
                    if(ignoreCase && strValue.equalsIgnoreCase(value)) {
                        return Optional.of(e);
                    }
                }
                
            } catch(ReflectiveOperationException e) {
                throw new SuperCsvInvalidAnnotationException(
                        String.format("enum class '%s' has not method '%s'", type.getCanonicalName(), valueMethodName));
            }
            
        } else {
            for(T e : set) {
                if(strValue.equals(e.name())) {
                    return Optional.of(e);
                }
                
                if(ignoreCase && strValue.equalsIgnoreCase(e.name())) {
                    return Optional.of(e);
                }
                
            }
        }
        
        throw new SuperCsvInvalidAnnotationException(String.format("parse fail enum value %s", strValue));
    }
    
}
