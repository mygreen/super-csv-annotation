package com.github.mygreen.supercsv.builder;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.Before;
import org.junit.Test;


/**
 * {@link DefaultBeanFactory}のテスタ
 *
 * @since 2.2
 * @author T.TSUCHIE
 *
 */
public class DefaultBeanFactoryTest {

    private BeanFactory<Class<?>, Object> beanFactory;

    @Before
    public void setUp() throws Exception {
        this.beanFactory = new DefaultBeanFactory();
    }

    @Test
    public void testCreate_staticClass() {

        StaticClass bean = (StaticClass) beanFactory.create(StaticClass.class);
        assertThat(bean.doExecute()).isEqualTo("static-class");

    }

    @Test
    public void testCreate_nonStaticClass() {

        NonStaticClass bean = (NonStaticClass) beanFactory.create(NonStaticClass.class);
        assertThat(bean.doExecute()).isEqualTo("non-static-class");

    }

    /**
     * staticクラス
     *
     */
    private static class StaticClass {

        String message = "static-class";

        String doExecute() {
            return message;
        }

    }

    /**
     * 非staticクラス
     *
     */
    private static class NonStaticClass {

        String message = "non-static-class";

        String doExecute() {
            return message;
        }

    }
}
