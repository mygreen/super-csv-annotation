package com.github.mygreen.supercsv.localization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ResourceBundle}を任意の文字コードで読み込むためのコントローラ。
 * プロパティファイル形式のみサポートします。
 *
 * @since 2.2
 * @author T.TSUCHIE
 *
 */
public class EncodingControl extends ResourceBundle.Control {
    
    private static final Logger logger = LoggerFactory.getLogger(EncodingControl.class);
    
    private static final String SUPPORT_FORMAT = "java.properties";
    
    private final Charset encoding;
    
    /**
     * 文字コードUTF-8で設定する
     */
    public EncodingControl() {
        this("UTF-8");
    }
    
    public EncodingControl(final String encoding) {
        this(Charset.forName(encoding));
    }
    
    public EncodingControl(final Charset encoding) {
        this.encoding = encoding;
    }
    
    @Override
    public ResourceBundle newBundle(final String baseName, final Locale locale, final String format,
            final ClassLoader loader, final boolean reload)
            throws IllegalAccessException, InstantiationException, IOException {

        if(format.equals(SUPPORT_FORMAT)) {
            final String bundleName = toBundleName(baseName, locale);
            final String resourceName = toResourceName(bundleName, "properties");
            
            try (InputStream stream = getResourceStream(loader, resourceName);
                    InputStreamReader isr = new InputStreamReader(stream, encoding);
                    BufferedReader reader = new BufferedReader(isr)) {
                return new PropertyResourceBundle(reader);
            } catch (PrivilegedActionException e) {
                throw(IOException) e.getException();
            }

        } else {
            // 「java.class」はサポートしない。
            // プロパティファイル(java.properties)のみサポートする。
            logger.trace("Not support format. baseName={}, format={}, reload={}.", baseName, format, reload);
            return null;
        }

    }
    
    private InputStream getResourceStream(final ClassLoader loader, final String resourceName) throws PrivilegedActionException {
       
        return AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
            
            @Override
            public InputStream run() throws IOException {
                // realod=trueのときもキャッシュを使用せずに新しく読み込む。
                return loader.getResourceAsStream(resourceName);
            }
        });
    }
    
    
    /**
     * 設定されている文字コードを取得します。
     * @return リソースファイルの文字コード。
     */
    public Charset getEncoding() {
        return encoding;
    }

}
