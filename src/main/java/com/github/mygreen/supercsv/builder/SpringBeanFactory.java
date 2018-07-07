package com.github.mygreen.supercsv.builder;

import java.util.Objects;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * Springのコンテナからインタンスを作成するためのクラスです。
 * <p>利用するには、このクラスをSpringのコンテナに登録しておく必要があります。</p>
 * <p>Springのコンテナに登録されていないクラスは、通常のクラスとしてインスタンスを作成します。
 *  <br>ただし、コンテナ管理外のクラスに対しても、アノテーション{@link Autowired}によるインジェクションが可能です。
 * </p>
 *
 * @version 2.2
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class SpringBeanFactory implements BeanFactory<Class<?>, Object>, ApplicationContextAware, InitializingBean {

    private AutowireCapableBeanFactory beanFactory;

    private ApplicationContext applicationContext;

    private BeanFactory<Class<?>, Object> defaultBeanFactory = new DefaultBeanFactory();

    @Override
    public Object create(final Class<?> clazz) {
        Objects.requireNonNull(clazz, "clazz should not be null.");

        final String beanName = getBeanName(clazz);
        if(beanFactory.containsBean(beanName)) {
            // Spring管理のクラスの場合
            return beanFactory.getBean(beanName, clazz);

        } else {
            // 通常のBeanクラスの場合
            Object obj = defaultBeanFactory.create(clazz);

            // Springコンテナ管理外でもインジェクションする。
            beanFactory.autowireBean(obj);

            return obj;
        }
    }

    private String getBeanName(final Class<?> clazz) {

        final Component componentAnno = clazz.getAnnotation(Component.class);
        if(componentAnno != null && !componentAnno.value().isEmpty()) {
            return componentAnno.value();
        }

        final Service serviceAnno = clazz.getAnnotation(Service.class);
        if(serviceAnno != null && !serviceAnno.value().isEmpty()) {
            return serviceAnno.value();
        }

        final Repository repositoryAnno = clazz.getAnnotation(Repository.class);
        if(repositoryAnno != null && !repositoryAnno.value().isEmpty()) {
            return repositoryAnno.value();
        }

        final Controller controllerAnno = clazz.getAnnotation(Controller.class);
        if(controllerAnno != null && !controllerAnno.value().isEmpty()) {
            return controllerAnno.value();
        }

        // ステレオタイプのアノテーションでBean名の指定がない場合は、クラス名の先頭を小文字にした名称とする。
        String simpleName = uncapitalize(clazz.getSimpleName());

        String[] names = applicationContext.getBeanNamesForType(clazz);
        if(names.length == 0) {
            return simpleName;
        }

        // 複数存在する場合、候補の中から一番シンプルな形式の名前で一致するものを選択する
        for(String name : names) {
            if(name.equals(simpleName)) {
                return name;
            }
        }

        // 見つからない場合は、候補の先頭のものにする。
        return names[0];
    }

    /**
     * 先頭の文字を小文字にする。
     * @param str 変換対象の文字列
     * @return 引数がnull、空文字の場合、そのまま返す。
     */
    private static String uncapitalize(final String str) {
        final int strLen;
        if(str == null || (strLen = str.length()) == 0) {
            return str;
        }

        return new StringBuilder(strLen)
            .append(String.valueOf(str.charAt(0)).toLowerCase())
            .append(str.substring(1))
            .toString();
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if(applicationContext != null && beanFactory == null) {
            this.beanFactory = applicationContext.getAutowireCapableBeanFactory();
        }

    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
