package com.github.mygreen.supercsv.builder;

import java.lang.annotation.Annotation;
import java.util.Comparator;

import org.supercsv.cellprocessor.ift.CellProcessor;

/**
 * {@link CellProcessor}を組み立てる際の設定を保持するクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class Configuration {
    
    /** 
     * Beanのインスタンスの作成クラス
     */
    private BeanFactory<Class<?>, Object> beanFactory = new DefaultBeanFactory();
    
    /**
     * アノテーションを一定の順序に並び変えるクラス。
     */
    private Comparator<Annotation> annoationComparator = new AnnotationComparator();
    
    /**
     * フィールドのタイプに対して{@link ProcessorBuilder}を解決するクラス。
     */
    private ProcessorBuilderResolver builderResolver = new ProcessorBuilderResolver();
    
    /**
     * 書き込み時に入力値検証処理をスキップするかどうか。
     */
    private boolean skipValidationOnWrite = false;
    
    public Configuration() {
        
    }
    
    /**
     * Beanを生成するためのFactoryクラスを取得します。
     * <p>アノテーションの属性でクラスが指定された場合に、インスタンスを取得・作成するときに利用します。</p>
     * @return 実装クラスを取得します。
     */
    public BeanFactory<Class<?>, Object> getBeanFactory() {
        return beanFactory;
    }
    
    /**
     * Beanを生成するためのFactoryクラスを設定します。
     * <p>アノテーションの属性でクラスが指定された場合に、インスタンスを取得・作成するときに利用します。</p>
     * @param beanFactory 実装クラスを指定します。
     */
    public void setBeanFactory(BeanFactory<Class<?>, Object> beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    /**
     * アノテーションの一定の順序に並び変えるクラスを取得します。
     * <p>入力値検証や変換処理の順序を一定にするために使用します。</p>
     * <p>デフォルトでは、{@link AnnotationComparator}が設定されています。</p>
     * @return {@link Comparator}の実装を取得する。
     */
    public Comparator<Annotation> getAnnoationComparator() {
        return annoationComparator;
    }
    
    /**
     * アノテーションの一定の順序に並び変えるクラスを設定します。
     * <p>入力値検証や変換処理の順序を一定にするために使用します。</p>
     * <p>デフォルトでは、{@link AnnotationComparator}が設定されています。</p>
     * 
     * @param annoationComparator {@link Comparator}の実装
     */
    public void setAnnoationComparator(Comparator<Annotation> annoationComparator) {
        this.annoationComparator = annoationComparator;
    }
    
    /**
     * フィールドのタイプに対して{@link ProcessorBuilder}を解決するクラスを取得します。
     * <p>対応していないタイプに対応するときなど、このクラスに{@link ProcessorBuilder}の実装を登録します。</p>
     * @return 既存の{@link ProcessorBuilderResolver}を取得して、設定を変更する際に利用します。
     */
    public ProcessorBuilderResolver getBuilderResolver() {
        return builderResolver;
    }
    
    /**
     * フィールドのタイプに対して{@link ProcessorBuilder}を解決するクラスを設定します。
     * <p>対応していないタイプに対応するときなど、このクラスに{@link ProcessorBuilder}の実装を登録します。</p>
     * @param builderResolver 新しく{@link ProcessorBuilderResolver}を設定する際に利用します。
     */
    public void setBuilderResolver(ProcessorBuilderResolver builderResolver) {
        this.builderResolver = builderResolver;
    }
    
    /**
     * 書き込み時に入力値検証をスキップするかどうかを取得します。
     * <p>初期値は、{@literal false}で、入力値検証を行いまます。</p>
     * @return {@literal true}の場合、制約のCellProcessorやレコードのValidatorがスキップされます。
     */
    public boolean isSkipValidationOnWrite() {
        return skipValidationOnWrite;
    }
    
    /**
     * 書き込み時に入力値検証をスキップするかどうかを設定します。
     * @param skipValidationOnWrite {@literal true}の場合、制約のCellProcessorやレコードのValidatorがスキップされます。
     */
    public void setSkipValidationOnWrite(boolean skipValidationOnWrite) {
        this.skipValidationOnWrite = skipValidationOnWrite;
    }
}
