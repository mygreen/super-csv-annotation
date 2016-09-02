package org.supercsv.ext.builder.spring;


/**
 * テスト用のサービス。
 * @author T.TSUCHIE
 *
 */
public class UserService {
    
    /**
     * ユーザ名が存在するかどうか。
     * @param userName
     * @return
     */
    public boolean existByUserName(final String userName) {
        
        if(userName == null) {
            return false;
            
        } else if(userName.equals("admin") || userName.equals("test")) {
            return true;
        }
        
        return false;
        
    }
    
}
