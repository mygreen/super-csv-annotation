package org.supercsv.ext.spring;


public class UserService {
    
    public boolean existByUserId(final String userId) {
        
        if(userId == null) {
            return false;
            
        } else if(userId.contains("admin")) {
            return true;
        }
        
        return false;
        
    }
    
}
