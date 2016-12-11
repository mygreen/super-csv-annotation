package com.github.mygreen.supercsv.localization;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;


/**
 * {@link Properties}を元にメッセージを解決するためのクラス。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class PropertiesMessageResolver implements MessageResolver {
    
    private Properties properties;
    
    /**
     * デフォルトのコンストラクタ。
     * <p>プロパティの中身は空です。</p>
     */
    public PropertiesMessageResolver() {
        this.properties = new Properties();
    }
    
    /**
     * プロパティを指定してインスタンスを作成する。
     * @param properties
     * @throws NullPointerException properties is null.
     */
    public PropertiesMessageResolver(final Properties properties) {
        Objects.requireNonNull(properties, "properties should not be null.");
        
        this.properties = properties;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getMessage(final String code) {
        return Optional.ofNullable(properties.getProperty(code));
    }
    
    /**
     * 
     * @return 設定されているプロパティを取得する。
     */
    public Properties getProperties() {
        return properties;
    }
    
    /**
     * プロパティを設定する
     * @param properties 設定されているプロパティ
     */
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    
}
