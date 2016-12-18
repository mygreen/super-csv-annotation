package com.github.mygreen.supercsv.builder;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.CsvComposition;
import com.github.mygreen.supercsv.annotation.CsvOverridesAttribute;
import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.annotation.constraint.CsvLengthMax;
import com.github.mygreen.supercsv.annotation.constraint.CsvNumberRange;
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.io.SampleNormalBean;

/**
 * {@link FieldAccessor}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class FieldAccessorTest {
    
    private Comparator<Annotation> comparator;
    
    @Before
    public void setUp() throws Exception {
        this.comparator = new AnnotationComparator();
    }
    
    @Test
    public void testConstructor_normal() throws Exception {
        
        Field field = SampleBean.class.getDeclaredField("col1");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        assertThat(property.getName()).isEqualTo("col1");
        assertThat(property.getType()).isEqualTo(String.class);
        assertThat(property.getDeclaredClass()).isEqualTo(SampleBean.class);
        
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructor_arg_null() {
        
        new FieldAccessor(null, comparator);
        
    }
    
    /**
     * {@link FieldAccessor#getName()}
     */
    @Test
    public void testGetName() throws Exception{
        
        Field field = SampleBean.class.getDeclaredField("col1");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        assertThat(property.getName()).isEqualTo("col1");
    }
    
    /**
     * {@link FieldAccessor#getNameWithClass()}
     */
    @Test
    public void testGetNameWithClass() throws Exception{
        
        Field field = SampleBean.class.getDeclaredField("col1");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        assertThat(property.getNameWithClass()).isEqualTo(SampleBean.class.getName() + "#col1");
    }
    
    /**
     * {@link FieldAccessor#getTypeName()}
     */
    @Test
    public void testGetTypeName() throws Exception{
        
        Field field = SampleBean.class.getDeclaredField("col1");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        assertThat(property.getTypeName()).isEqualTo("java.lang.String");
    }
    
    /**
     * {@link FieldAccessor#isTypeOf(Class)}
     */
    @Test
    public void testIsTypeOf() throws Exception{
        
        Field field = SampleBean.class.getDeclaredField("col1");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        assertThat(property.isTypeOf(String.class)).isTrue();
        assertThat(property.isTypeOf(int.class)).isFalse();
    }
    
    /**
     * {@link FieldAccessor#getValue(Object)}
     */
    @Test
    public void testGetValue() throws Exception{
        
        Field field = SampleBean.class.getDeclaredField("col1");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        SampleBean targetObj = new SampleBean();
        assertThat(property.getValue(targetObj)).isNull();
        
        targetObj.col1 = "test";
        assertThat(property.getValue(targetObj)).isEqualTo("test");

    }
    
    /**
     * {@link FieldAccessor#getValue(Object)}
     * <p>Beanのタイプが不一致</p>
     */
    @Test(expected=IllegalArgumentException.class)
    public void testGetValue_wrongType() throws Exception {
        
        Field field = SampleBean.class.getDeclaredField("col1");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        property.getValue(new SampleNormalBean());
        fail();
    }
    
    /**
     * アノテーションの取得 - 1件取得
     */
    @Test
    public void testGetAnnotation() throws Exception {
        
        Field field = SampleBean.class.getDeclaredField("col1");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        {
            // 存在するアノテーション
            Optional<CsvColumn> anno = property.getAnnotation(CsvColumn.class);
            assertThat(anno.isPresent()).isTrue();
            assertThat(anno.get().number()).isEqualTo(1);
        }
        
        {
            // 存在しないアノテーション
            Optional<CsvRequire> anno = property.getAnnotation(CsvRequire.class);
            assertThat(anno.isPresent()).isFalse();
        }
        
    }
    
    /**
     * アノテーションの取得 - 複数件の取得
     */
    @Test
    public void testGetAnnotations() throws Exception {
        
        Field field = SampleBean.class.getDeclaredField("col4");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        {
            List<CsvColumn> annos = property.getAnnotations(CsvColumn.class);
            assertThat(annos).hasSize(1);
        }
        
        {
            // 繰り返しのアノテーション
            List<CsvNumberRange> annos = property.getAnnotations(CsvNumberRange.class);
            assertThat(annos).hasSize(2);
        }
    }
    
    /**
     * アノテーションの取得 - 合成のアノテーション
     */
    @Test
    public void testGetAnnotations_composition() throws Exception {
        
        Field field = SampleBean.class.getDeclaredField("col5");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        {
            List<CsvColumn> annos = property.getAnnotations(CsvColumn.class);
            assertThat(annos).hasSize(1);
            
        }
        
        {
            // 合成のアノテーション自身
            List<CsvUsername> annos = property.getAnnotations(CsvUsername.class);
            assertThat(annos).hasSize(1);
            
        }
        
        {
            // 合成のアノテーションの中のアノテーション
            List<CsvLengthMax> annos = property.getAnnotations(CsvLengthMax.class);
            assertThat(annos).hasSize(2);
            
        }
        
    }
    
    /**
     * アノテーションの取得 - グループ属性なしの場合
     */
    @Test
    public void testGetAnnotationsByGroup_noGroup() throws Exception {
        
        Field field = SampleBean.class.getDeclaredField("col1");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        {
            // 存在するアノテーション - groups属性を持たない
            List<CsvColumn> annos = property.getAnnotationsByGroup(CsvColumn.class);
            assertThat(annos).hasSize(0);
        }
        
        {
            // 存在しないアノテーション - groups属性を持つ
            List<CsvRequire> annos = property.getAnnotationsByGroup(CsvRequire.class);
            assertThat(annos).hasSize(0);
        }
        
        {
            // 存在するアノテーション（デフォルトグループ） - groups属性を持たない
            List<CsvColumn> annos = property.getAnnotationsByGroup(CsvColumn.class, DefaultGroup.class);
            assertThat(annos).hasSize(0);
        }
        
        {
            // 存在しないアノテーション（デフォルトグループ） - groups属性を持つ
            List<CsvRequire> annos = property.getAnnotationsByGroup(CsvRequire.class, DefaultGroup.class);
            assertThat(annos).hasSize(0);
        }
    }
    
    /**
     * アノテーションの取得 - グループ属性ありの場合
     */
    @Test
    public void testGetAnnotationsByGroup_group() throws Exception {
        
        Field field = SampleBean.class.getDeclaredField("col2");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        {
            // グループ属性が定義されていないアノテーション
            List<CsvColumn> annos = property.getAnnotationsByGroup(CsvColumn.class, Group1.class);
            assertThat(annos).hasSize(0);
        }
        
        {
            // 存在するアノテーション
            List<CsvNumberRange> annos = property.getAnnotationsByGroup(CsvNumberRange.class, Group1.class);
            assertThat(annos).hasSize(1);
            assertThat(annos.get(0).min()).isEqualTo("50");
        }
        
        {
            // 存在しないアノテーション
            List<CsvRequire> annos = property.getAnnotationsByGroup(CsvRequire.class, Group1.class);
            assertThat(annos).hasSize(0);
        }
        
        {
            // 存在しないグループ
            List<CsvNumberRange> annos = property.getAnnotationsByGroup(CsvNumberRange.class, Group2.class);
            assertThat(annos).hasSize(0);
        }
        
    }
    
    /**
     * アノテーションの取得 - グループの指定あり(デフォルトグループ)
     */
    @Test
    public void testGetAnnotationsByGroup_defaultGroup() throws Exception {
        
        Field field = SampleBean.class.getDeclaredField("col3");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        {
            // グループの指定なし（定義側もグループの指定なし）
            List<CsvColumn> annos = property.getAnnotationsByGroup(CsvColumn.class);
            assertThat(annos).hasSize(0);
        }
        
        {
            // グループの指定なし（定義側がデフォルトグループ）
            List<CsvNumberRange> annos = property.getAnnotationsByGroup(CsvNumberRange.class);
            assertThat(annos).hasSize(1);
            assertThat(annos.get(0).min()).isEqualTo("50");
        }
        
        {
            // 存在するアノテーション（定義側がデフォルトグループ）
            List<CsvNumberRange> annos = property.getAnnotationsByGroup(CsvNumberRange.class, DefaultGroup.class);
            assertThat(annos).hasSize(1);
            assertThat(annos.get(0).min()).isEqualTo("50");
        }
        
    }
    
    /**
     * アノテーションの取得 - 複数の同じアノテーション
     */
    @Test
    public void testGetAnnotationsByGroup_repeatGroup() throws Exception {
        
        Field field = SampleBean.class.getDeclaredField("col4");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        {
            // デフォルトのグループ
            List<CsvNumberRange> annos = property.getAnnotationsByGroup(CsvNumberRange.class, DefaultGroup.class);
            assertThat(annos).hasSize(1);
            
            {
                CsvNumberRange anno = annos.get(0);
                assertThat(anno.min()).isEqualTo("1");
            }
        }
        
        {
            // グループの指定
            List<CsvNumberRange> annos = property.getAnnotationsByGroup(CsvNumberRange.class, Group1.class);
            
            assertThat(annos).hasSize(1);
            
            {
                CsvNumberRange anno = annos.get(0);
                assertThat(anno.min()).isEqualTo("50");
            }
            
        }
        
        {
            // 複数のグループの指定
            List<CsvNumberRange> annos = property.getAnnotationsByGroup(CsvNumberRange.class, DefaultGroup.class, Group1.class);
            assertThat(annos).hasSize(2);
            
            {
                CsvNumberRange anno = annos.get(0);
                assertThat(anno.min()).isEqualTo("1");
            }
            
            {
                CsvNumberRange anno = annos.get(1);
                assertThat(anno.min()).isEqualTo("50");
            }
            
        }
        
    }
    
    /**
     * アノテーションの一覧の取得。
     * <p>繰り返しのアノテーションの場合</p>
     */
    @Test
    public void testGetAnnoationsByGroup_repetable() throws Exception {
        
        Field field = SampleBean.class.getDeclaredField("col4");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        {
            // デフォルトのグループ
            List<Annotation> annos = property.getAnnotationsByGroup();
            
            assertThat(annos).hasSize(1);
            
            {
                assertThat(annos.get(0)).isInstanceOf(CsvNumberRange.class);
                
                CsvNumberRange anno = (CsvNumberRange) annos.get(0);
                assertThat(anno.min()).isEqualTo("1");
                assertThat(anno.max()).isEqualTo("100");
            }
        
        }
        
        {
            // 複数のグループの指定
            List<Annotation> annos = property.getAnnotationsByGroup(DefaultGroup.class, Group1.class);
            assertThat(annos).hasSize(2);
            
            {
                assertThat(annos.get(0)).isInstanceOf(CsvNumberRange.class);
                
                CsvNumberRange anno = (CsvNumberRange) annos.get(0);
                assertThat(anno.min()).isEqualTo("1");
                assertThat(anno.max()).isEqualTo("100");
            }
            
            {
                assertThat(annos.get(1)).isInstanceOf(CsvNumberRange.class);
                
                CsvNumberRange anno = (CsvNumberRange) annos.get(1);
                assertThat(anno.min()).isEqualTo("50");
                assertThat(anno.max()).isEqualTo("200");
            }
        }
        
    }
    
    /**
     * アノテーションの一覧の取得。
     * <p>合成したアノテーションの場合</p>
     */
    @Test
    public void tesGetAnnotationsByGroup_composition() throws Exception {
        
        Field field = SampleBean.class.getDeclaredField("col5");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        {
            // デフォルトのグループ
            List<Annotation> annos = property.getAnnotationsByGroup();
            assertThat(annos).hasSize(2);
            
            {
                assertThat(annos.get(0)).isInstanceOf(CsvRequire.class);
                
                CsvRequire anno = (CsvRequire) annos.get(0);
                assertThat(anno.message()).isEqualTo("ユーザ名は不正です。");
                
            }
            
            {
                assertThat(annos.get(1)).isInstanceOf(CsvLengthMax.class);
                
                CsvLengthMax anno = (CsvLengthMax) annos.get(1);
                assertThat(anno.message()).isEqualTo("ユーザ名は不正です。");
                
                assertThat(anno.value()).isEqualTo(64);
                
            }
            
            
            
        }
        
        {
            // 複数のグループの指定
            List<Annotation> annos = property.getAnnotationsByGroup(DefaultGroup.class, Group1.class);
            assertThat(annos).hasSize(3);
            
            {
                assertThat(annos.get(0)).isInstanceOf(CsvRequire.class);
                
                CsvRequire anno = (CsvRequire) annos.get(0);
                assertThat(anno.message()).isEqualTo("ユーザ名は不正です。");
                
            }
            
            {
                assertThat(annos.get(1)).isInstanceOf(CsvLengthMax.class);
                
                CsvLengthMax anno = (CsvLengthMax) annos.get(1);
                assertThat(anno.message()).isEqualTo("ユーザ名は不正です。");
                
                assertThat(anno.order()).isEqualTo(1);
                assertThat(anno.value()).isEqualTo(20);
                
            }
            
            {
                assertThat(annos.get(2)).isInstanceOf(CsvLengthMax.class);
                
                CsvLengthMax anno = (CsvLengthMax) annos.get(2);
                assertThat(anno.message()).isEqualTo("ユーザ名は不正です。");
                
                assertThat(anno.order()).isEqualTo(2);
                assertThat(anno.value()).isEqualTo(64);
                
            }
            
        }
    }
    
    @Test
    public void testHasAnnotation() throws Exception {
        
        Field field = SampleBean.class.getDeclaredField("col1");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        assertThat(property.hasAnnotation(CsvColumn.class)).isTrue();
        assertThat(property.hasAnnotation(CsvRequire.class)).isFalse();
        
    }
    
    /**
     * アノテーションの判定 - グループ属性なしの場合
     */
    @Test
    public void testHasAnnotationByGroup_noGroup() throws Exception {
        
        Field field = SampleBean.class.getDeclaredField("col1");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        {
            // 存在するアノテーション - groups属性を持たない
            assertThat(property.hasAnnotationByGroup(CsvColumn.class)).isFalse();
        }
        
        {
            // 存在しないアノテーション - groups属性を持つ
            assertThat(property.hasAnnotationByGroup(CsvRequire.class)).isFalse();
        }
        
        {
            // 存在するアノテーション（デフォルトグループ） - groups属性を持たない
            assertThat(property.hasAnnotationByGroup(CsvColumn.class, DefaultGroup.class)).isFalse();
        }
        
        {
            // 存在しないアノテーション（デフォルトグループ） - groups属性を持つ
            assertThat(property.hasAnnotationByGroup(CsvRequire.class, DefaultGroup.class)).isFalse();
        }
        
    }
    
    /**
     * アノテーションの判定 - グループ属性を持つ場合
     */
    @Test
    public void testHasAnnotationByGroup_group() throws Exception {
        
        Field field = SampleBean.class.getDeclaredField("col2");
        FieldAccessor property = new FieldAccessor(field, comparator);
        
        {
            // グループ属性が定義されていないアノテーション
            assertThat(property.hasAnnotationByGroup(CsvColumn.class, Group1.class)).isFalse();
        }
        
        {
            // 存在するアノテーション
            assertThat(property.hasAnnotationByGroup(CsvNumberRange.class, Group1.class)).isTrue();
        }
        
        {
            // 存在しないアノテーション
            assertThat(property.hasAnnotationByGroup(CsvRequire.class, Group1.class)).isFalse();
        }
        
        {
            // 存在しないグループ
            assertThat(property.hasAnnotationByGroup(CsvNumberRange.class, Group2.class)).isFalse();
        }
        
    }
    
    @CsvBean
    private static class SampleBean {
        
        /**
         * グループ指定なし
         */
        @CsvColumn(number=1, label="列1")
        @CsvLengthMax(value=100)
        private String col1;
        
        /**
         * グループ指定あり
         */
        @CsvColumn(number=2)
        @CsvNumberRange(min="50", max="200", groups={Group1.class})
        private int col2;
        
        /**
         * グループ指定あり - デフォルトグループ
         */
        @CsvColumn(number=3)
        @CsvNumberRange(min="50", max="200", groups={DefaultGroup.class})
        private int col3;
        
        /**
         * 同じアノテーションがある
         */
        @CsvColumn(number=4)
        @CsvNumberRange(min="1", max="100")
        @CsvNumberRange(min="50", max="200", groups={Group1.class})
        private LocalDate col4;
        
        /**
         * 合成したアノテーション
         */
        @CsvColumn(number=5)
        @CsvUsername(message="ユーザ名は不正です。", maxLength=20)
        private String col5;
        
        
    }
    
    // テスト用のグループ
    private interface Group1 { }
    private interface Group2 { }
    
    // 合成したアノテーション
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @CsvComposition
    @CsvRequire
    @CsvLengthMax(value=64, order=2, groups={DefaultGroup.class})
    @CsvLengthMax(value=10, order=1, groups={Group1.class})
    public @interface CsvUsername {
        
        String message() default "";
        
        @CsvOverridesAttribute(annotation=CsvLengthMax.class, name="value", index=1)
        int maxLength() default 0;
        
    }
    
}
