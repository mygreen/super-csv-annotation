package org.supercsv.ext.builder;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;


/**
 * create instance for CellProcessorBuilder via SpringFramework container.
 * 
 * @since 1.1
 * @author T.TSUCHIE
 *
 */
public class SpringCellProcessorBuilderFacatory
        implements CellProcessorBuilderFactory, ApplicationContextAware, InitializingBean {
    
    private AutowireCapableBeanFactory beanFactory;
    
    private ApplicationContext applicationContext;
    
    @Override
    public <T extends CellProcessorBuilder<?>> T create(final Class<T> builderClass) throws Exception {
        Assert.notNull(builderClass, "builderClass must not be null");
        return this.beanFactory.createBean(builderClass);
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        
        if(applicationContext != null && beanFactory == null) {
            this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
        }
        
    }
    
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
    
    public AutowireCapableBeanFactory getBeanFactory() {
        return beanFactory;
    }
    
    public void setBeanFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
    
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
    
}
