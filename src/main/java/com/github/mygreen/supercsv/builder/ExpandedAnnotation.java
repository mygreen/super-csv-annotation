package com.github.mygreen.supercsv.builder;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import com.github.mygreen.supercsv.annotation.CsvComposition;

/**
 * 展開したアノテーション情報を保持するクラス。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class ExpandedAnnotation{
    
    private final Annotation original;
    
    /**
     * 合成されたアノテーションかどうか。
     */
    private final boolean composed;
    
    /**
     * 繰り返しのアノテーションの場合のインデックス番号。
     */
    private int index;
    
    /**
     * 合成されたアノテーションのクラスに付与されたアノテーション
     */
    private final List<ExpandedAnnotation> childs = new ArrayList<>();
    
    /**
     * 
     * @param original 元となるアノテーション。
     * @param composed 合成されたアノテーションかどうか。
     */
    public ExpandedAnnotation(final Annotation original, final boolean composed) {
        this.original = original;
        this.composed = composed;
    }
    
    /**
     * 元のアノテーションを取得する。
     * @return 元のアノテーションのインスタンスを返します。
     */
    public Annotation getOriginal() {
        return original;
    }
    
    /**
     * 指定したアノテーションのクラスタイプかどうか。
     * @param clazz 比較対象のアノテーションのクラスタイプ。
     * @return trueの場合、比較対象のクラスタイプを一致します。
     */
    public boolean isAnnotationType(final Class<?> clazz) {
        return original.annotationType().equals(clazz);
    }
    
    /**
     * {@link CsvComposition}が付与された合成されたアノテーションかどうか。
     * @return trueの場合、合成されたアノテーションを指します。
     */
    public boolean isComposed() {
        return composed;
    }
    
    /**
     * 合成されたアノテーションのクラスに付与されたアノテーションを取得する。
     * @return 合成されたアノテーションに付与されているアノテーション情報を返します。
     */
    public List<ExpandedAnnotation> getChilds() {
        return childs;
    }
    
    public void addChilds(final List<ExpandedAnnotation> childs) {
        this.childs.addAll(childs);
    }
    
    /**
     * 繰り返しのアノテーションの場合のインデックス番号を取得する。
     * @return 0から始まる。繰り返しでない場合は常に0を返す。
     */
    public int getIndex() {
        return index;
    }
    
    /**
     * 繰り返しのアノテーションの場合のインデックス番号を指定する。
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }
    
}
