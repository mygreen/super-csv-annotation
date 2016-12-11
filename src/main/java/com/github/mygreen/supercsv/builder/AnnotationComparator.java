package com.github.mygreen.supercsv.builder;

import java.lang.annotation.Annotation;
import java.util.Comparator;

import com.github.mygreen.supercsv.util.Utils;


/**
 * 入力値検証を行うアノテーションの順番に並び変える。
 * <p>並び順は、アノテーションの属性「order」の定義に従う。</p>
 * <p>属性「order」の値が同じ場合は、クラス名の昇順になります。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class AnnotationComparator implements Comparator<Annotation> {
    
    /**
     * 属性「order」が定義されていないときの値。
     */
    private static final int DEFAULT_ORDER = Integer.MAX_VALUE;
    
    @Override
    public int compare(final Annotation anno1, final Annotation anno2) {
        
        final String name1 = anno1.annotationType().getName();
        final String name2 = anno2.annotationType().getName();
        
        final int order1 = Utils.getAnnotationAttribute(anno1, "order", int.class).orElse(DEFAULT_ORDER);
        final int order2 = Utils.getAnnotationAttribute(anno2, "order", int.class).orElse(DEFAULT_ORDER);
        
        if(order1 == order2) {
            return name1.compareTo(name2);
        } else {
            return Integer.compare(order1, order2);
        }
    }
    
}
