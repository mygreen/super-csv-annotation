package com.github.mygreen.supercsv.builder;


/**
 * CellProcessorを組み立てる時のケースを表現する列挙型です。
 * <p>読み込み/書き込み用と区別する時に利用します。</p>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public enum BuildCase {
    
    /** 読み込み時 */
    Read,
    /** 書き込み時 */
    Write;
    
}
