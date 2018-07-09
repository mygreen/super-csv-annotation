package com.github.mygreen.supercsv.localization;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * {@link ResourceBundle}を任意の文字コードで読み込むためのコントローラ。
 *
 * @since 2.2
 * @author T.TSUCHIE
 *
 */
public class EncodingControl extends ResourceBundle.Control {
    
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
    public ResourceBundle newBundle(final String baseName, final Locale locale, String format, final ClassLoader loader, final boolean reload) 
            throws IllegalAccessException, InstantiationException, IOException {
        
        String bundleName = toBundleName(baseName, locale);
        ResourceBundle bundle = null;
        if (format.equals("java.class"))
        {
          try
          {
            @SuppressWarnings(
            { "unchecked" })
            Class<? extends ResourceBundle> bundleClass = (Class<? extends ResourceBundle>) loader.loadClass(bundleName);

            // If the class isn't a ResourceBundle subclass, throw a
            // ClassCastException.
            if (ResourceBundle.class.isAssignableFrom(bundleClass))
            {
              bundle = bundleClass.newInstance();
            }
            else
            {
              throw new ClassCastException(bundleClass.getName() + " cannot be cast to ResourceBundle");
            }
          }
          catch (ClassNotFoundException ignored)
          {
          }
        }
        else if (format.equals("java.properties"))
        {
          final String resourceName = toResourceName(bundleName, "properties");
          final ClassLoader classLoader = loader;
          final boolean reloadFlag = reload;
          InputStreamReader isr = null;
          InputStream stream;
          try
          {
            stream = AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>()
            {
              @Override
              public InputStream run() throws IOException
              {
                InputStream is = null;
                if (reloadFlag)
                {
                  URL url = classLoader.getResource(resourceName);
                  if (url != null)
                  {
                    URLConnection connection = url.openConnection();
                    if (connection != null)
                    {
                      // Disable caches to get fresh data for
                      // reloading.
                      connection.setUseCaches(false);
                      is = connection.getInputStream();
                    }
                  }
                }
                else
                {
                  is = classLoader.getResourceAsStream(resourceName);
                }
                return is;
              }
            });
            if (stream != null)
            {
              isr = new InputStreamReader(stream, encoding);
            }
          }
          catch (PrivilegedActionException e)
          {
            throw (IOException) e.getException();
          }
          if (isr != null)
          {
            try
            {
              bundle = new PropertyResourceBundle(isr);
            }
            finally
            {
              isr.close();
            }
          }
        }
        else
        {
          throw new IllegalArgumentException("unknown format: " + format);
        }
        return bundle;
    }
    
    
    /**
     * 設定されている文字コードを取得します。
     * @return リソースファイルの文字コード。
     */
    public Charset getEncoding() {
        return encoding;
    }

}
