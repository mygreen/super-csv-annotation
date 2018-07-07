package com.github.mygreen.supercsv.builder;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * {@link SpringBeanFactory}のテスト
 *
 * @since 2.2
 * @author T.TSUCHIE
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=SpringBeanFactoryTest.TestConfig.class)
public class SpringBeanFactoryTest {

    @Autowired
    private SpringBeanFactory beanFactory;

    @Before
    public void setUp() throws Exception {
        assertThat(beanFactory).isNotNull();

    }

    /**
     * Spring管理外の場合
     */
    @Test
    public void testCreate_nonSpringBean() {

        NotSpringBean bean = (NotSpringBean) beanFactory.create(NotSpringBean.class);
        assertThat(bean.hello()).isEqualTo("hello@NotSpringBean");

    }

    /**
     * Spring管理外の場合 - インジェクションがある
     */
    @Test
    public void testCreate_nonSpringWithInject() {

        NonSpringWithInject bean = (NonSpringWithInject) beanFactory.create(NonSpringWithInject.class);
        assertThat(bean.hello()).isEqualTo("hello@atSpringBean - NonSpringWithInject");

    }

    /**
     * Spring管理の場合
     */
    @Test
    public void testCreate_atSpringBean() {

        AtSpringBean bean = (AtSpringBean) beanFactory.create(AtSpringBean.class);
        assertThat(bean.hello()).isEqualTo("hello@atSpringBean");

    }

    /**
     * Spring管理の場合 - 複数存在する
     */
    @Test
    public void testCreate_atSpringMultipleBean() {

        AtSpringMultipleBean bean = (AtSpringMultipleBean) beanFactory.create(AtSpringMultipleBean.class);
        assertThat(bean.hello()).containsPattern("^hello@atSpringMultipleBean[1-2]$");

    }

    /**
     * Spring管理の場合 - {@literal @Component}
     */
    @Test
    public void testCreate_atSpring_ScanComponent() {

        AtSpringComponentBean bean = (AtSpringComponentBean) beanFactory.create(AtSpringComponentBean.class);
        assertThat(bean.hello()).isEqualTo("hello@atSpringBean - @Component");

    }

    /**
     * Spring管理の場合 - {@literal @Service}
     */
    @Test
    public void testCreate_atSpring_ScanService() {

        AtSpringServiceBean bean = (AtSpringServiceBean) beanFactory.create(AtSpringServiceBean.class);
        assertThat(bean.hello()).isEqualTo("hello@atSpringBean - @Service");

    }

    /**
     * Spring管理の場合 - {@literal @Repository}
     */
    @Test
    public void testCreate_atSpring_ScanRepository() {

        AtSpringRepositoryBean bean = (AtSpringRepositoryBean) beanFactory.create(AtSpringRepositoryBean.class);
        assertThat(bean.hello()).isEqualTo("hello@atSpringBean - @Repository");

    }

    /**
     * Spring管理の場合 - {@literal @Controller}
     */
    @Test
    public void testCreate_atSpring_ScanController() {

        AtSpringControllerBean bean = (AtSpringControllerBean) beanFactory.create(AtSpringControllerBean.class);
        assertThat(bean.hello()).isEqualTo("hello@atSpringBean - @Controller");

    }

    /**
     * SpringのJavaConfig
     *
     */
    @Configurable
    public static class TestConfig {

        @Description("Springのコンテナを経由するCSV用のBeanFactoryの定義")
        @Bean
        SpringBeanFactory springBeanFactory() {
            return new SpringBeanFactory();
        }

        @Description("Spring管理のクラス - 名前も準ずる")
        @Bean
        AtSpringBean atSpringBean() {
            AtSpringBean bean = new AtSpringBean();
            bean.message = "hello@atSpringBean";
            return bean;
        }

        @Description("Spring管理のクラス - Bean名が異なる。標準の名称が存在しない")
        @Bean
        AtSpringMultipleBean atSpringMultipleBean1() {
            AtSpringMultipleBean bean = new AtSpringMultipleBean();
            bean.message = "hello@atSpringMultipleBean1";
            return bean;
        }

        @Description("Spring管理のクラス - Bean名が異なる。標準の名称が存在しない")
        @Bean
        AtSpringMultipleBean atSpringMultipleBean2() {
            AtSpringMultipleBean bean = new AtSpringMultipleBean();
            bean.message = "hello@atSpringMultipleBean2";
            return bean;
        }


    }

    /**
     * Spring管理外のクラス
     *
     */
    private class NotSpringBean {

        String message;

        NotSpringBean() {
            this.message = "hello@NotSpringBean";
        }

        String hello() {
            return message;
        }

    }

    /**
     * Spring管理外のクラスだが、インジェクションがある。
     *
     */
    private class NonSpringWithInject {

        @Autowired
        AtSpringBean atSpringBean;

        String hello() {
            return atSpringBean.hello() + " - NonSpringWithInject";
        }

    }

    /**
     * Spring管理のクラス
     *
     */
    private static class AtSpringBean {

        String message;

        String hello() {
            return message;
        }

    }

    /**
     * Spring管理のクラス - 複数定義
     *
     */
    private static class AtSpringMultipleBean {

        String message;

        String hello() {
            return message;
        }

    }

    @Component("componentBean")
    private static class AtSpringComponentBean {

        @Autowired
        AtSpringBean atSpringBean;

        String hello() {
            return atSpringBean.hello() + " - @Component";
        }

    }

    @Service("serviceBean")
    private static class AtSpringServiceBean {

        @Autowired
        AtSpringBean atSpringBean;

        String hello() {
            return atSpringBean.hello() + " - @Service";
        }

    }

    @Repository("repositoryBean")
    private static class AtSpringRepositoryBean {

        @Autowired
        AtSpringBean atSpringBean;

        String hello() {
            return atSpringBean.hello() + " - @Repository";
        }

    }

    @Controller("controllerBean")
    private static class AtSpringControllerBean {

        @Autowired
        AtSpringBean atSpringBean;

        String hello() {
            return atSpringBean.hello() + " - @Controller";
        }

    }

}
