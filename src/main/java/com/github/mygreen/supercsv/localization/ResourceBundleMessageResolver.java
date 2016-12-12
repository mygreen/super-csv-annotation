package com.github.mygreen.supercsv.localization;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import com.github.mygreen.supercsv.util.ArgUtils;

/**
 * {@link ResourceBundle}を元にメッセージを解決するクラス。
 * <p>クラスパスのルートにリソース名が{@literal SuperCsvMessages}のプロパティファイルを配置していると自動的に読み込みます。</p>
 * <p>デフォルトでは、{@link ResourceBundleMessageResolver#DEFAULT_MESSAGE}に配置されているリソースファイルを読み込みます。</p>
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class ResourceBundleMessageResolver implements MessageResolver {
    
    /**
     * デフォルトのメッセージソースのぱす
     */
    public static final String DEFAULT_MESSAGE = "com.github.mygreen.supercsv.localization.SuperCsvMessages";
    
    private final Map<ResourceBundle, List<String>> messageBundleKeys = new HashMap<ResourceBundle, List<String>>(8);
    
    private final LinkedList<ResourceBundle> messageBundles = new LinkedList<ResourceBundle>();
    
    /**
     * メッセージリソースのパスを指定して、インスタンスを作成します。
     * @param baseName メッセージリソースのパス。
     * @param appendUserResource クラスパスのルートにあるユーザ定義のメッセージソースも読み込むかどうか指定します。
     *      引数baseNameの値が {@literal sample.SampleMessages}のとき、クラスパスのルート上にある「SampleMessages」を読み込みます。
     * @throws NullPointerException baseName is null.
     * @throws IllegalArgumentException baseName is empty.
     */
    public ResourceBundleMessageResolver(final String baseName, final boolean appendUserResource) {
        ArgUtils.notEmpty(baseName, "baseName");
        
        addResourceBundle(ResourceBundle.getBundle(baseName));
        
        // ユーザ定義のリソースを読み込む
        if(appendUserResource) {
            // リソース名の切り出し
            final int index = baseName.lastIndexOf(".");
            if(index > 0) {
                final String userName = baseName.substring(index+1);
                try {
                    addResourceBundle(ResourceBundle.getBundle(userName));
                } catch(Throwable e) { }
            }
        }
        
    }
    
    /**
     * デフォルトのコンストラクタ。
     * <p>デフォルトのメッセージソース{@link #DEFAULT_MESSAGE}が自動的に読み込まれます。</p>
     * 
     */
    public ResourceBundleMessageResolver() {
        this(DEFAULT_MESSAGE, true);
    }
    
    /**
     * 独自のメッセージソースを指定してインスタンスを作成する。
     * <p>デフォルトのメッセージソース{@link #DEFAULT_MESSAGE}が自動的に読み込まれます。</p>
     * @param resourceBundle 独自のメッセージメース
     * @throws NullPointerException resourceBundle is null.
     */
    public ResourceBundleMessageResolver(final ResourceBundle resourceBundle) {
        this();
        Objects.requireNonNull(resourceBundle, "resourceBundle should no be null");
        addResourceBundle(resourceBundle);
    }
    
    /**
     * {@inheritDoc}
     */
    public Optional<String> getMessage(final String code) {
        for(final ResourceBundle bundle : messageBundles) {
            final List<String> keys = messageBundleKeys.get(bundle);
            if(keys.contains(code)) {
                try {
                    return Optional.of(bundle.getString(code));
                } catch(MissingResourceException e) {
                    return Optional.empty();
                }
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * メッセージソースを追加します。
     * @param resourceBundle 追加するメッセージソース。
     * @return 既に追加済みの場合はfalseを返します。
     * @throws NullPointerException resourceBundle is null.
     */
    public final boolean addResourceBundle(final ResourceBundle resourceBundle) {
        Objects.requireNonNull(resourceBundle, "resourceBundle should not be null.");
        
        if(messageBundles.contains(resourceBundle)) {
            return false;
        }
        
        messageBundles.addFirst(resourceBundle);
        final List<String> keys = new ArrayList<String>();
        
        for(final Enumeration<String> keysEnum = resourceBundle.getKeys(); keysEnum.hasMoreElements();) {
            keys.add(keysEnum.nextElement());
        }
        
        messageBundleKeys.put(resourceBundle, keys);
        
        return true;
    }
    
   /**
     * メッセージソースを削除する。
     * @param resourceBundle 削除対象のメッセージソース
     * @return 登録されているメッセージソースがある場合はtrueを返します。
     * @throws NullPointerException resourceBundle is null.
     */
    public boolean removeResourceBundle(final ResourceBundle resourceBundle) {
        Objects.requireNonNull(resourceBundle, "resourceBundle should not be null.");
        
        if(!messageBundles.contains(resourceBundle)) {
            return false;
        }
        
        messageBundles.remove(resourceBundle);
        return true;
    }
}
