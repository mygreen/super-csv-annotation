package com.github.mygreen.supercsv.builder.spring;

import java.net.URL;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

/**
 * Springで管理するテスト用のサービス
 * <p>Springで管理する。</p>
 * 
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Service
public class UserService {
    
    /**
     * ユーザ名が存在するかどうか。
     * @param userName チェック対象のユーザ名。
     * @return trueの場合、ユーザ名が存在する。
     */
    public boolean existByUserName(final String userName) {
        
        if(userName == null) {
            return false;
            
        } else if(userName.equals("admin") || userName.equals("test")) {
            return true;
        }
        
        return false;
        
    }
    
    /**
     * URLが許可されたプロトコルかどうか
     * @param url チェック対象のURL
     * @return
     */
    public boolean isValidProtocol(final URL url) {
        String protocol = url.getProtocol();
        
        return protocol.equals("http") || protocol.equals("https");
    }
    
    /**
     * メールアドレスのパターンのチェック
     * @param mail チェック対象のメールアドレス
     * @return
     */
    public boolean isMailPattern(final String mail) {
        
        Pattern pattern = Pattern.compile(".+@.+");
        
        return pattern.matcher(mail).matches();
        
    }
    
}
