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

import com.github.mygreen.supercsv.annotation.constraint.CsvConstraint;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.util.Utils;

/**
 * フィールドに設定されている制約のアノテーションをハンドリングして、{@link CellProcessor}を作成する。
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ConstraintProcessorHandler implements ProcessorFactory {
    
    private static Logger logger = LoggerFactory.getLogger(ConstraintProcessorHandler.class);
    
    private final Map<Class<? extends Annotation>, ConstraintProcessorFactory<?>> factoryMap = new HashMap<>();
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Optional<CellProcessor> create(final Optional<CellProcessor> processor, final FieldAccessor field,
            final TextFormatter<?> formatter, final Configuration config, final BuildCase buildCase, final Class<?>[] groups) {
        
        // 制約のアノテーションを取得する
        final List<Annotation> annos = field.getAnnotationsByGroup(groups).stream()
                .filter(anno -> anno.annotationType().getAnnotation(CsvConstraint.class) != null)
                .filter(anno -> Utils.containsBuildCase(anno, buildCase))
                .collect(Collectors.toList());
        
        Collections.reverse(annos);
        
        Optional<CellProcessor> cp = processor;
        
        for(Annotation anno : annos) {
            
            final CsvConstraint constraintAnno = anno.annotationType().getAnnotation(CsvConstraint.class);
            
            if(factoryMap.containsKey(anno.annotationType())) {
                // 登録済みのものから取得する。
                final ConstraintProcessorFactory factory = factoryMap.get(anno.annotationType());
                cp = factory.create(anno, cp, field, formatter, config);
                
            } else if(constraintAnno.value().length > 0) {
                /*
                 * アノテーション「@CsvConstraint」が指定されている場合、クラスのインスタンスを作成する。
                 * ・定義上、複数指定可能になっているたが、先頭のクラスのみインスタンス化する。
                 */
                for(Class<? extends ConstraintProcessorFactory> factoryClass : constraintAnno.value()) {
                    final ConstraintProcessorFactory factory = 
                            (ConstraintProcessorFactory) config.getBeanFactory().create(factoryClass);
                    cp = factory.create(anno, cp, field, formatter, config);
                }
                
            } else {
                // factoryが見つからない場合
                logger.warn("Not found {} with the annotation {}.", ConstraintProcessorFactory.class.getSimpleName(), anno.getClass());
            }
            
        }
        
        return cp;
    }
    
    /**
     * アノテーションに対する{@link ConstraintProcessorFactory}を登録する。
     *
     * @param <A> アノテーションのタイプ
     * @param anno 関連づけるアノテーション
     * @param factory 制約の{@link CellProcessor}を作成する{@link ConstraintProcessorFactory}の実装。
     */
    public <A extends Annotation> void register(final Class<A> anno, final ConstraintProcessorFactory<A> factory) {
        factoryMap.put(anno, factory);
    }
    
    /**
     * 登録されている{@link ConstraintProcessorFactory}情報を取得する。
     * @return アノテーションと対応する{@link ConstraintProcessorFactory}のマップ。
     */
    public Set<Map.Entry<Class<? extends Annotation>, ConstraintProcessorFactory<?>>> getEntrySet() {
        return factoryMap.entrySet();
    }
    
}
