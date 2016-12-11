package com.github.mygreen.supercsv.localization;

import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.MessageSourceAccessor;


/**
 * Springの{@link MessageSource}をブリッジする{@link MessageResolver}。
 * 
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class SpringMessageResolver implements MessageResolver {
    
    protected MessageSourceAccessor messageSourceAccessor;
    
    public SpringMessageResolver() {
        
    }
    
    public SpringMessageResolver(final MessageSource messageSource) {
        setMessageSource(messageSource);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<String> getMessage(final String code) {
        try {
            return Optional.of(messageSourceAccessor.getMessage(code));
            
        } catch(NoSuchMessageException e) {
            return Optional.empty();
        }
    }
    
    /**
     * メッセージソースを設定する
     * @param messageSource Springのメッセージソース
     */
    public void setMessageSource(final MessageSource messageSource) {
        this.messageSourceAccessor = new MessageSourceAccessor(messageSource);
    }
    
    /**
     * メッセージソースを取得する。
     * @return 現在設定されているメッセージソースをラップした{@link MessageSourceAccessor}を返す。
     */
    protected MessageSourceAccessor getMessageSourceAccessor() {
        return messageSourceAccessor;
    }
}
