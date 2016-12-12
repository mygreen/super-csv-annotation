package com.github.mygreen.supercsv.cellprocessor;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.conversion.CsvConversion;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.util.Utils;

/**
 * フィールドに設定されている変換用のアノテーションをハンドリングして、{@link CellProcessor}を作成するクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ConversionProcessorHandler implements ProcessorFactory {
    
    private static Logger logger = LoggerFactory.getLogger(ConversionProcessorHandler.class);
    
    private final Map<Class<? extends Annotation>, ConversionProcessorFactory<?>> factoryMap = new HashMap<>();
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Optional<CellProcessor> create(final Optional<CellProcessor> processor, final FieldAccessor field,
            final TextFormatter<?> formatter, final Configuration config, final BuildCase buildCase, final Class<?>[] groups) {
        
        // 変換用のアノテーションを取得する
        final List<Annotation> annos = field.getAnnotationsByGroup(groups).stream()
                .filter(anno -> anno.annotationType().getAnnotation(CsvConversion.class) != null)
                .filter(anno -> Utils.containsBuildCase(anno, buildCase))
                .collect(Collectors.toList());
        
        Collections.reverse(annos);
        
        Optional<CellProcessor> cp = processor;
        
        for(Annotation anno : annos) {
            
            final CsvConversion conversionAnno = anno.annotationType().getAnnotation(CsvConversion.class);
            
            if(factoryMap.containsKey(anno.annotationType())) {
                // 登録済みのものから取得する。
                final ConversionProcessorFactory factory = factoryMap.get(anno.annotationType());
                cp = factory.create(anno, cp, field, formatter, config);
                
            } else if(conversionAnno.value().length > 0) {
                /*
                 * アノテーション「@CsvConversion」が指定されている場合、クラスのインスタンスを作成する。
                 * ・定義上、複数指定可能になっているたが、先頭のクラスのみインスタンス化する。
                 */
                for(Class<? extends ConversionProcessorFactory> factoryClass : conversionAnno.value()) {
                    final ConversionProcessorFactory factory = 
                            (ConversionProcessorFactory) config.getBeanFactory().create(factoryClass);
                    cp = factory.create(anno, cp, field, formatter, config);
                }
                
            } else {
                // factoryが見つからない場合
                logger.warn("Not found {} with the annotation {}.", ConversionProcessorFactory.class.getSimpleName(), anno.getClass());
            }
            
        }
        
        return cp;
    }
    
    /**
     * アノテーションに対する{@link ConversionProcessorFactory}を登録する。
     * 
     * @param <A> アノテーションのタイプ
     * @param anno 関連づけるアノテーション
     * @param factory 制約の{@link CellProcessor}を作成する{@link ConversionProcessorFactory}の実装。
     */
    public <A extends Annotation> void register(final Class<A> anno, final ConversionProcessorFactory<A> factory) {
        factoryMap.put(anno, factory);
    }
    
    /**
     * 登録されている{@link ConversionProcessorFactory}情報を取得する。
     * @return アノテーションと対応する{@link ConversionProcessorFactory}のマップ。
     */
    public Set<Map.Entry<Class<? extends Annotation>, ConversionProcessorFactory<?>>> getEntrySet() {
        return factoryMap.entrySet();
    }
    
}
