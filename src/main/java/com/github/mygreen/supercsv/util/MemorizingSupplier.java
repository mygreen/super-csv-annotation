package com.github.mygreen.supercsv.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * 結果をキャッシュする {@link} Supplier} の実装クラス。
 * <p>Google Guavaの {@code com.google.common.base.Suppliers.MemoizingSupplier} の実装を参考にしています。</p>
 * 
 * @param <T> キャッシュするオブジェクトの型
 * @since 2.5
 * @author T.TSUCHIE
 *
 */
public class MemorizingSupplier<T> implements Supplier<T> {
    
    private final Supplier<T> deleagte;

    private final AtomicBoolean initialized = new AtomicBoolean(false);

    private transient Object lock = new Object();
    
    private T value;
    
    public static <T> Supplier<T> of(final Supplier<T> deleagte) {
        return new MemorizingSupplier<>(deleagte);
    }
    
    /**
     * デフォルトコンストラクタ
     * @param deleagte キャッシュするオブジェクトを生成する {@link Supplier}
     */
    private MemorizingSupplier(final Supplier<T> deleagte) {
        ArgUtils.notNull(deleagte, "deleagte");
        this.deleagte = deleagte;
    }
    
    @Override
    public T get() {
        
        if (!initialized.get()) {
            synchronized(lock) {
                this.value = deleagte.get();
                initialized.set(true);
            }
        }
        
        return value;
    }
    
    /**
     * このSupplierが初期化されているかどうかを返します。
     * 
     * @return 初期化されている場合はtrueを返します。
     */
    public boolean isInitialized() {
        return initialized.get();
    }

}
