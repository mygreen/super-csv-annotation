package com.github.mygreen.supercsv.builder;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.CsvComposition;
import com.github.mygreen.supercsv.annotation.CsvOverridesAttribute;
import com.github.mygreen.supercsv.annotation.DefaultGroup;
import com.github.mygreen.supercsv.annotation.constraint.CsvNumberMax;
import com.github.mygreen.supercsv.annotation.constraint.CsvLengthMax;
import com.github.mygreen.supercsv.annotation.constraint.CsvDateTimeMax;
import com.github.mygreen.supercsv.annotation.constraint.CsvNumberRange;
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.annotation.constraint.CsvUnique;
import com.github.mygreen.supercsv.annotation.conversion.CsvLower;
import com.github.mygreen.supercsv.annotation.conversion.CsvTrim;
import com.github.mygreen.supercsv.annotation.format.CsvDateTimeFormat;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;

/**
 * {@link AnnotationExpander}のテスタ。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class AnnotationExpanderTest {
    
    private AnnotationExpander expander;
    
    @Before
    public void setUp() throws Exception {
        this.expander = new AnnotationExpander(new AnnotationComparator());
    }
    
    /**
     * コンストラクタのテスト - 引数がnullの場合
     */
    @Test(expected=NullPointerException.class)
    public void testConstructor_argNull() {
        
        new AnnotationExpander(null);
        fail();
        
    }
    
    /**
     * {@link AnnotationExpander#expand(Annotation[])} のテスタ
     * <p>引数がnullの場合</p>
     */
    @Test(expected=NullPointerException.class)
    public void testExpandAnnotationArray_argNull() {
        expander.expand((Annotation[])null);
        fail();
    }
    
    /**
     * {@link AnnotationExpander#expand(Annotation)} のテスタ
     * <p>引数がnullの場合</p>
     */
    @Test(expected=NullPointerException.class)
    public void testExpandAnnotation_argNull() {
        expander.expand((Annotation)null);
        fail();
    }
    
    /**
     * 通常のアノテーションの展開
     */
    @Test
    public void testExpand_normal() {
        
        Field field = getSampleField("normal");
        
        Annotation targetAnno = field.getAnnotation(CsvColumn.class);
        
        List<ExpandedAnnotation> actual = expander.expand(targetAnno);
        
        assertThat(actual).hasSize(1);
        
        {
            ExpandedAnnotation expandedAnno = actual.get(0);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(CsvColumn.class);
            assertThat(expandedAnno.getIndex()).isEqualTo(0);
            assertThat(expandedAnno.isComposed()).isEqualTo(false);
            assertThat(expandedAnno.getChilds()).hasSize(0);
        }
        
        
    }
    
    /**
     * 複数のアノテーションの展開 - 並び順指定の場合
     */
    @Test
    public void testExpand_multi() {
        
        Field field = getSampleField("multi");
        
        Annotation[] targetAnno = field.getAnnotations();
        
        List<ExpandedAnnotation> actual = expander.expand(targetAnno);
        
        assertThat(actual).hasSize(6);
        
        {
            ExpandedAnnotation expandedAnno = actual.get(0);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(CsvLower.class);
        }
        
        {
            ExpandedAnnotation expandedAnno = actual.get(1);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(CsvUnique.class);
        }
        
        {
            ExpandedAnnotation expandedAnno = actual.get(2);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(CsvDateTimeMax.class);
        }
        
        {
            ExpandedAnnotation expandedAnno = actual.get(3);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(CsvTrim.class);
        }
        
        {
            ExpandedAnnotation expandedAnno = actual.get(4);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(CsvColumn.class);
        }
        
        {
            ExpandedAnnotation expandedAnno = actual.get(5);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(CsvDateTimeFormat.class);
        }
        
        
    }
    
    /**
     * 繰り返しのアノテーションが1つの場合の展開
     */
    @Test
    public void testExpand_repeatSingle() {
        
        Field field = getSampleField("repeatSingle");
        
        Annotation targetAnno = field.getAnnotation(CsvLengthMax.class);
        
        List<ExpandedAnnotation> actual = expander.expand(targetAnno);
        
        assertThat(actual).hasSize(1);
        
        {
            ExpandedAnnotation expandedAnno = actual.get(0);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(CsvLengthMax.class);
            assertThat(expandedAnno.getIndex()).isEqualTo(0);
            assertThat(expandedAnno.isComposed()).isEqualTo(false);
            assertThat(expandedAnno.getChilds()).isEmpty();
        }
        
    }
    
    /**
     * 繰り返しのアノテーションが複数場合の展開
     */
    @Test
    public void testExpand_repeatMulti() {
        
        Field field = getSampleField("repeatMulti");
        
        Annotation targetAnno = field.getAnnotation(CsvLengthMax.List.class);
        
        List<ExpandedAnnotation> actual = expander.expand(targetAnno);
        
        assertThat(actual).hasSize(3);
        
        {
            ExpandedAnnotation expandedAnno = actual.get(0);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(CsvLengthMax.class);
            assertThat(expandedAnno.getIndex()).isEqualTo(0);
            assertThat(expandedAnno.isComposed()).isEqualTo(false);
            assertThat(expandedAnno.getChilds()).hasSize(0);
            
            CsvLengthMax originalAnno = (CsvLengthMax) expandedAnno.getOriginal();
            assertThat(originalAnno.value()).isEqualTo(10);
        }
        
        {
            ExpandedAnnotation expandedAnno = actual.get(1);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(CsvLengthMax.class);
            assertThat(expandedAnno.getIndex()).isEqualTo(1);
            assertThat(expandedAnno.isComposed()).isEqualTo(false);
            assertThat(expandedAnno.getChilds()).hasSize(0);
            
            CsvLengthMax originalAnno = (CsvLengthMax) expandedAnno.getOriginal();
            assertThat(originalAnno.value()).isEqualTo(20);
        }
        
        {
            ExpandedAnnotation expandedAnno = actual.get(2);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(CsvLengthMax.class);
            assertThat(expandedAnno.getIndex()).isEqualTo(2);
            assertThat(expandedAnno.isComposed()).isEqualTo(false);
            assertThat(expandedAnno.getChilds()).hasSize(0);
            
            CsvLengthMax originalAnno = (CsvLengthMax) expandedAnno.getOriginal();
            assertThat(originalAnno.value()).isEqualTo(30);
        }
        
    }
    
    /**
     * 合成のアノテーション - 属性のオーバライドなしのシンプルな物。
     */
    @Test
    public void testExpand_composeSimple() {
        
        Field field = getSampleField("composeSimple");
        
        Annotation targetAnno = field.getAnnotation(ComposeSimple.class);
        
        List<ExpandedAnnotation> actual = expander.expand(targetAnno);
        
        assertThat(actual).hasSize(1);
        
        {
            ExpandedAnnotation expandedAnno = actual.get(0);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(ComposeSimple.class);
            
            assertThat(expandedAnno.getChilds()).hasSize(8);
            
//            expandedAnno.getChilds().forEach(anno -> System.out.println(anno.getOriginal().annotationType().getName()));
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(0);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvRequire.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(1);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvNumberMax.class);
                
                assertThat(childAnno.getIndex()).isEqualTo(1);
                
                CsvNumberMax anno = (CsvNumberMax) childAnno.getOriginal();
                assertThat(anno.order()).isEqualTo(1);
                assertThat(anno.value()).isEqualTo("30");
                
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(2);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvNumberMax.class);
                
                assertThat(childAnno.getIndex()).isEqualTo(0);
                
                CsvNumberMax anno = (CsvNumberMax) childAnno.getOriginal();
                assertThat(anno.order()).isEqualTo(2);
                assertThat(anno.value()).isEqualTo("20");
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(3);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvComposition.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(4);
                assertThat(childAnno.getOriginal()).isInstanceOf(Documented.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(5);
                assertThat(childAnno.getOriginal()).isInstanceOf(Repeatable.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(6);
                assertThat(childAnno.getOriginal()).isInstanceOf(Retention.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(7);
                assertThat(childAnno.getOriginal()).isInstanceOf(Target.class);
            }
            
            
        }
        
    }
    
    /**
     * 合成のアノテーション - 属性のオーバライドなしのシンプルな物。
     * ただし、繰り返しがあり
     */
    @Test
    public void testExpand_composeSimpleRepeat() {
        
        Field field = getSampleField("composeSimpleRepeat");
        
        Annotation targetAnno = field.getAnnotation(ComposeSimple.List.class);
        
        List<ExpandedAnnotation> actual = expander.expand(targetAnno);
        
        assertThat(actual).hasSize(2);
        
        {
            ExpandedAnnotation expandedAnno = actual.get(0);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(ComposeSimple.class);
            assertThat(expandedAnno.getIndex()).isEqualTo(0);
            
            assertThat(expandedAnno.getChilds()).hasSize(8);
            
            ComposeSimple compositeAnno = (ComposeSimple) expandedAnno.getOriginal();
            assertThat(compositeAnno.value()).isEqualTo("repeat-1");
            
        }
        
        {
            ExpandedAnnotation expandedAnno = actual.get(1);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(ComposeSimple.class);
            assertThat(expandedAnno.getIndex()).isEqualTo(1);
            
            assertThat(expandedAnno.getChilds()).hasSize(8);
            
            ComposeSimple compositeAnno = (ComposeSimple) expandedAnno.getOriginal();
            assertThat(compositeAnno.value()).isEqualTo("repeat-2");
        }
        
    }
    
    /**
     * 合成のアノテーション - 属性のオーバーライドあり
     * <p>独自の属性を上書き</p>
     */
    @Test
    public void testExpand_composeOverrideCustom() {
        
        Field field = getSampleField("composeOverrideCustom");
        
        Annotation targetAnno = field.getAnnotation(ComposeOverrideCustom.class);
        
        List<ExpandedAnnotation> actual = expander.expand(targetAnno);
        
        assertThat(actual).hasSize(1);
        
        {
            ExpandedAnnotation expandedAnno = actual.get(0);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(ComposeOverrideCustom.class);
            assertThat(expandedAnno.getIndex()).isEqualTo(0);
            
            assertThat(expandedAnno.getChilds()).hasSize(8);
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(0);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvRequire.class);
                
                CsvRequire anno = (CsvRequire) childAnno.getOriginal();
                assertThat(anno.considerBlank()).isEqualTo(true);
                assertThat(anno.considerEmpty()).isEqualTo(true);
            }
            
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(1);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvNumberRange.class);
                
                assertThat(childAnno.getIndex()).isEqualTo(1);
                
                CsvNumberRange anno = (CsvNumberRange) childAnno.getOriginal();
                assertThat(anno.order()).isEqualTo(1);
                assertThat(anno.min()).isEqualTo("2");
                assertThat(anno.max()).isEqualTo("2");
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(2);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvNumberRange.class);
                
                assertThat(childAnno.getIndex()).isEqualTo(0);
                
                CsvNumberRange anno = (CsvNumberRange) childAnno.getOriginal();
                assertThat(anno.order()).isEqualTo(2);
                assertThat(anno.min()).isEqualTo("0");
                assertThat(anno.max()).isEqualTo("5");
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(3);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvComposition.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(4);
                assertThat(childAnno.getOriginal()).isInstanceOf(Documented.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(5);
                assertThat(childAnno.getOriginal()).isInstanceOf(Repeatable.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(6);
                assertThat(childAnno.getOriginal()).isInstanceOf(Retention.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(7);
                assertThat(childAnno.getOriginal()).isInstanceOf(Target.class);
            }
            
        }
        
    }
    
    /**
     * 合成のアノテーション - 属性のオーバーライドあり
     * <p>共通の属性の上書き</p>
     */
    @Test
    public void testExpand_composeOverrideDefault1() {
        
        Field field = getSampleField("composeOverrideDefault1");
        
        Annotation targetAnno = field.getAnnotation(ComposeOverrideDefault1.class);
        
        List<ExpandedAnnotation> actual = expander.expand(targetAnno);
        
        assertThat(actual).hasSize(1);
        
        {
            ExpandedAnnotation expandedAnno = actual.get(0);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(ComposeOverrideDefault1.class);
            assertThat(expandedAnno.getIndex()).isEqualTo(0);
            
            assertThat(expandedAnno.getChilds()).hasSize(8);
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(0);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvRequire.class);
                
                CsvRequire anno = (CsvRequire) childAnno.getOriginal();
                assertThat(anno.message()).isEqualTo("値は不正です");
                assertThat(anno.groups()).containsExactly(DefaultGroup.class, Group2.class);
                assertThat(anno.cases()).containsExactly(BuildCase.Read, BuildCase.Write);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(1);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvNumberRange.class);
                
                assertThat(childAnno.getIndex()).isEqualTo(1);
                
                CsvNumberRange anno = (CsvNumberRange) childAnno.getOriginal();
                assertThat(anno.order()).isEqualTo(1);
                assertThat(anno.min()).isEqualTo("0");
                assertThat(anno.max()).isEqualTo("30");
                
                assertThat(anno.message()).isEqualTo("値は不正です");
                assertThat(anno.groups()).containsExactly(DefaultGroup.class, Group2.class);
                assertThat(anno.cases()).containsExactly(BuildCase.Read, BuildCase.Write);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(2);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvNumberRange.class);
                
                assertThat(childAnno.getIndex()).isEqualTo(0);
                
                CsvNumberRange anno = (CsvNumberRange) childAnno.getOriginal();
                assertThat(anno.order()).isEqualTo(2);
                assertThat(anno.min()).isEqualTo("0");
                assertThat(anno.max()).isEqualTo("20");
                
                assertThat(anno.message()).isEqualTo("値は不正です");
                assertThat(anno.groups()).containsExactly(DefaultGroup.class, Group2.class);
                assertThat(anno.cases()).containsExactly(BuildCase.Read, BuildCase.Write);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(3);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvComposition.class);
            }
            
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(4);
                assertThat(childAnno.getOriginal()).isInstanceOf(Documented.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(5);
                assertThat(childAnno.getOriginal()).isInstanceOf(Repeatable.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(6);
                assertThat(childAnno.getOriginal()).isInstanceOf(Retention.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(7);
                assertThat(childAnno.getOriginal()).isInstanceOf(Target.class);
            }
            
        }
        
    }
    
    /**
     * 合成のアノテーション - 属性のオーバーライドあり
     * <p>共通の属性の上書き</p>
     * <p>1部の属性を上書き</p>
     */
    @Test
    public void testExpand_composeOverrideDefault2() {
        
        Field field = getSampleField("composeOverrideDefault2");
        
        Annotation targetAnno = field.getAnnotation(ComposeOverrideDefault2.class);
        
        List<ExpandedAnnotation> actual = expander.expand(targetAnno);
        
        assertThat(actual).hasSize(1);
        
        {
            ExpandedAnnotation expandedAnno = actual.get(0);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(ComposeOverrideDefault2.class);
            assertThat(expandedAnno.getIndex()).isEqualTo(0);
            
            assertThat(expandedAnno.getChilds()).hasSize(8);
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(0);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvRequire.class);
                
                CsvRequire anno = (CsvRequire) childAnno.getOriginal();
                assertThat(anno.message()).isEqualTo("値は不正です");
                assertThat(anno.groups()).containsExactly(DefaultGroup.class, Group2.class);
                assertThat(anno.cases()).containsExactly();
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(1);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvNumberRange.class);
                
                assertThat(childAnno.getIndex()).isEqualTo(1);
                
                CsvNumberRange anno = (CsvNumberRange) childAnno.getOriginal();
                assertThat(anno.order()).isEqualTo(1);
                assertThat(anno.min()).isEqualTo("0");
                assertThat(anno.max()).isEqualTo("30");
                
                assertThat(anno.message()).isEqualTo("値は不正です");
                assertThat(anno.groups()).containsExactly(DefaultGroup.class, Group2.class);
                assertThat(anno.cases()).containsExactly(BuildCase.Read, BuildCase.Write);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(2);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvNumberRange.class);
                
                assertThat(childAnno.getIndex()).isEqualTo(0);
                
                CsvNumberRange anno = (CsvNumberRange) childAnno.getOriginal();
                assertThat(anno.order()).isEqualTo(2);
                assertThat(anno.min()).isEqualTo("0");
                assertThat(anno.max()).isEqualTo("20");
                
                assertThat(anno.message()).isEqualTo("範囲内に設定してください");
                assertThat(anno.groups()).containsExactly(Group2.class);
                assertThat(anno.cases()).containsExactly(BuildCase.Write);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(3);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvComposition.class);
            }
            
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(4);
                assertThat(childAnno.getOriginal()).isInstanceOf(Documented.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(5);
                assertThat(childAnno.getOriginal()).isInstanceOf(Repeatable.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(6);
                assertThat(childAnno.getOriginal()).isInstanceOf(Retention.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(7);
                assertThat(childAnno.getOriginal()).isInstanceOf(Target.class);
            }
            
        }
        
    }
    
    /**
     * 合成のアノテーション - 属性のオーバーライドあり
     * <p>共通の属性の上書き</p>
     * <p>1部の属性を上書き</p>
     */
    @Test
    public void testExpand_composeOverrideDefault3() {
        
        Field field = getSampleField("composeOverrideDefault3");
        
        Annotation targetAnno = field.getAnnotation(ComposeOverrideDefault3.class);
        
        List<ExpandedAnnotation> actual = expander.expand(targetAnno);
        
        assertThat(actual).hasSize(1);
        
        {
            ExpandedAnnotation expandedAnno = actual.get(0);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(ComposeOverrideDefault3.class);
            assertThat(expandedAnno.getIndex()).isEqualTo(0);
            
            assertThat(expandedAnno.getChilds()).hasSize(8);
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(0);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvRequire.class);
                
                CsvRequire anno = (CsvRequire) childAnno.getOriginal();
                assertThat(anno.message()).isEqualTo("必須です");
                assertThat(anno.groups()).containsExactly(Group2.class);
                assertThat(anno.cases()).containsExactly(BuildCase.Write);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(1);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvNumberRange.class);
                
                assertThat(childAnno.getIndex()).isEqualTo(1);
                
                CsvNumberRange anno = (CsvNumberRange) childAnno.getOriginal();
                assertThat(anno.order()).isEqualTo(1);
                assertThat(anno.min()).isEqualTo("0");
                assertThat(anno.max()).isEqualTo("30");
                
                assertThat(anno.message()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvNumberRange.message}");
                assertThat(anno.groups()).hasSize(0);
                assertThat(anno.cases()).hasSize(0);
                
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(2);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvNumberRange.class);
                
                assertThat(childAnno.getIndex()).isEqualTo(0);
                
                CsvNumberRange anno = (CsvNumberRange) childAnno.getOriginal();
                assertThat(anno.order()).isEqualTo(2);
                assertThat(anno.min()).isEqualTo("0");
                assertThat(anno.max()).isEqualTo("20");
                
                assertThat(anno.message()).isEqualTo("値は不正です");
                assertThat(anno.groups()).containsExactly(DefaultGroup.class, Group2.class);
                assertThat(anno.cases()).containsExactly(BuildCase.Read, BuildCase.Write);

            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(3);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvComposition.class);
            }
            
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(4);
                assertThat(childAnno.getOriginal()).isInstanceOf(Documented.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(5);
                assertThat(childAnno.getOriginal()).isInstanceOf(Repeatable.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(6);
                assertThat(childAnno.getOriginal()).isInstanceOf(Retention.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(7);
                assertThat(childAnno.getOriginal()).isInstanceOf(Target.class);
            }
            
        }
        
    }
    
    /**
     * 合成のアノテーション - 属性のオーバーライドあり
     * <p>属性名の省略</p>
     * <p>1部の属性を上書き</p>
     */
    @Test
    public void testExpand_composeOverrideDefault4() {
        
        Field field = getSampleField("composeOverrideDefault4");
        
        Annotation targetAnno = field.getAnnotation(ComposeOverrideDefault4.class);
        
        List<ExpandedAnnotation> actual = expander.expand(targetAnno);
        
        assertThat(actual).hasSize(1);
        
        {
            ExpandedAnnotation expandedAnno = actual.get(0);
            assertThat(expandedAnno.getOriginal()).isInstanceOf(ComposeOverrideDefault4.class);
            assertThat(expandedAnno.getIndex()).isEqualTo(0);
            
            assertThat(expandedAnno.getChilds()).hasSize(7);
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(0);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvNumberRange.class);
                
                assertThat(childAnno.getIndex()).isEqualTo(1);
                
                CsvNumberRange anno = (CsvNumberRange) childAnno.getOriginal();
                assertThat(anno.order()).isEqualTo(1);
                assertThat(anno.min()).isEqualTo("0");
                assertThat(anno.max()).isEqualTo("30");
                
                assertThat(anno.message()).isEqualTo("{com.github.mygreen.supercsv.annotation.constraint.CsvNumberRange.message}");
                assertThat(anno.groups()).hasSize(0);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(1);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvNumberRange.class);
                
                assertThat(childAnno.getIndex()).isEqualTo(0);
                
                CsvNumberRange anno = (CsvNumberRange) childAnno.getOriginal();
                assertThat(anno.order()).isEqualTo(2);
                assertThat(anno.min()).isEqualTo("0");
                assertThat(anno.max()).isEqualTo("5");
                
                assertThat(anno.groups()).containsExactly(Group1.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(2);
                assertThat(childAnno.getOriginal()).isInstanceOf(CsvComposition.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(3);
                assertThat(childAnno.getOriginal()).isInstanceOf(Documented.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(4);
                assertThat(childAnno.getOriginal()).isInstanceOf(Repeatable.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(5);
                assertThat(childAnno.getOriginal()).isInstanceOf(Retention.class);
            }
            
            {
                ExpandedAnnotation childAnno = expandedAnno.getChilds().get(6);
                assertThat(childAnno.getOriginal()).isInstanceOf(Target.class);
            }
            
        }
        
    }
    
    /**
     * 合成のアノテーション - 上書き対象の属性が見つからない場合
     */
    @Test
    public void testExpand_composeOverrideNotFoundAttr1() {
        
        Field field = getSampleField("composeOverrideAttrNotFound1");
        Annotation targetAnno = field.getAnnotation(ComposeOverrideAttrNotFound1.class);
        
        assertThatThrownBy(() -> expander.expand(targetAnno))
            .isInstanceOf(SuperCsvInvalidAnnotationException.class)
            .hasMessage("アノテーション @ComposeOverrideAttrNotFound1 において、アノテーション @CsvOverridesAnnotation で上書きするアノテーション @CsvNumberRange の属性(java.lang.String test) が見つかりません。");
        
    }
    
    /**
     * 合成のアノテーション - 上書き対象の属性が見つからない場合
     * - インデックス指定
     */
    @Test
    public void testExpand_composeOverrideNotFoundAttr2() {
        
        Field field = getSampleField("composeOverrideAttrNotFound2");
        Annotation targetAnno = field.getAnnotation(ComposeOverrideAttrNotFound2.class);
        
        //TODO: 現状は正常終了してしまう。
        
//        assertThatThrownBy(() -> expander.expand(targetAnno))
//            .isInstanceOf(SuperCsvInvalidAnnotationException.class)
//            .hasMessage("アノテーション @ComposeOverrideAttrNotFound2 において、アノテーション @CsvOverridesAnnotation で上書きするアノテーション 1番目の @CsvNumberRange の属性(java.lang.String test) が見つかりません。");
//        
    }
    
    /**
     * テスト用のクラスのフィールドを取得する
     * @param fieldName 取得し対象のフィールドの名称
     * @return
     */
    private static Field getSampleField(final String fieldName) {
        
        try {
            final Field field = SampleCsv.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            
            return field;
        } catch(Exception e) {
            throw new RuntimeException(String.format("fail get field : '%s'.", fieldName), e);
        }
        
    }
    
    private static class SampleCsv {
        
        @CsvColumn(number=1, label="test")
        private int normal;
        
        @CsvDateTimeFormat(pattern="yyyy/MM/dd")
        @CsvColumn(number=2, label="test")
        @CsvDateTimeMax(value="2050/10/21", order=5)
        @CsvUnique(order=3)
        @CsvLower(order=1)
        @CsvTrim(order=10)
        private Date multi;
        
        @CsvLengthMax(value=10)
        private String repeatSingle;
        
        @CsvLengthMax(value=10)
        @CsvLengthMax(value=20)
        @CsvLengthMax(value=30)
        private String repeatMulti;
        
        @ComposeSimple(value="simple")
        private Integer composeSimple;
        
        @ComposeSimple(value="repeat-1")
        @ComposeSimple(value="repeat-2")
        private Integer composeSimpleRepeat;
        
        @ComposeOverrideCustom(value="override-custom", max1="5")
        private Integer composeOverrideCustom;
        
        @ComposeOverrideDefault1(value="override-default-1", message="値は不正です", groups={DefaultGroup.class, Group2.class}, cases={BuildCase.Read, BuildCase.Write})
        private Integer composeOverrideDefault1;
        
        @ComposeOverrideDefault2(value="override-default-2", message="値は不正です", groups={DefaultGroup.class, Group2.class},
                rangeMessage="範囲内に設定してください", rangeGroups={Group2.class}, rangeCases={BuildCase.Read, BuildCase.Write})
        private Integer composeOverrideDefault2;
        
        @ComposeOverrideDefault3(value="override-default-3", message="値は不正です", groups={DefaultGroup.class, Group2.class}, cases={BuildCase.Read, BuildCase.Write})
        private Integer composeOverrideDefault3;
        
        @ComposeOverrideDefault4(value="override-default-4", max="5")
        private Integer composeOverrideDefault4;
        
        // エラー確認用
        @ComposeOverrideAttrNotFound1(value="v", test="t")
        private Integer composeOverrideAttrNotFound1;
        
        @ComposeOverrideAttrNotFound2(value="v", test="t")
        private Integer composeOverrideAttrNotFound2;
        
    }
    
    // テスト用のグループ1
    private interface Group1 { }
    
    // テスト用のグループ2
    private interface Group2 { }
    
    /**
     * 合成のアノテーション
     * <p>属性の上書きはなし</p>
     *
     */
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(ComposeSimple.List.class)
    @CsvComposition
    @CsvRequire
    @CsvNumberMax(value="20", order=2)
    @CsvNumberMax(value="30", order=1)
    public static @interface ComposeSimple {
        
        String value();
        
        // 繰り返しのアノテーションの格納用アノテーションの定義
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            ComposeSimple[] value();
        }
    }
    
    /**
     * 合成のアノテーション
     * <p>属性の上書きあり</p>
     * <p>固有の属性をオーバーライドする</p>
     *
     */
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(ComposeOverrideCustom.List.class)
    @CsvComposition
    @CsvRequire(considerBlank=false, considerEmpty=false, message="必須です")
    @CsvNumberRange(min="0", max="20", order=2)
    @CsvNumberRange(min="0", max="30", order=1)
    public static @interface ComposeOverrideCustom {
        
        String value();
        
        @CsvOverridesAttribute(annotation=CsvRequire.class, name="considerBlank")
        @CsvOverridesAttribute(annotation=CsvRequire.class, name="considerEmpty")
        boolean considerSpace() default true;
        
        @CsvOverridesAttribute(annotation=CsvNumberRange.class, name="max", index=0)
        String max1();
        
        @CsvOverridesAttribute(annotation=CsvNumberRange.class, name="max", index=1)
        @CsvOverridesAttribute(annotation=CsvNumberRange.class, name="min", index=1)
        String max2() default "2";
        
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            ComposeOverrideCustom[] value();
        }
    }
    
    /**
     * 合成のアノテーション
     * <p>属性の上書きあり</p>
     * <p>共通の属性 message groups casesのをオーバーライドする</p>
     *
     */
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(ComposeOverrideDefault1.List.class)
    @CsvComposition
    @CsvRequire(message="必須です", groups={Group2.class})
    @CsvNumberRange(min="0", max="20", order=2, groups={Group1.class}, cases=BuildCase.Read)
    @CsvNumberRange(min="0", max="30", order=1, cases=BuildCase.Write)
    public static @interface ComposeOverrideDefault1 {
        
        String value();
        
        String message() default "";
        
        Class<?>[] groups() default {};
        
        BuildCase[] cases() default {};
        
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            ComposeOverrideDefault1[] value();
        }
        
    }
    
    /**
     * 合成のアノテーション
     * <p>属性の上書きあり</p>
     * <p>共通の属性のmessage, groups, casesをオーバーライドする</p>
     * <p>1部のみを書き換える。</p>
     *
     */
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(ComposeOverrideDefault2.List.class)
    @CsvComposition
    @CsvRequire(message="必須です", groups={Group2.class})
    @CsvNumberRange(min="0", max="20", order=2, groups={Group1.class}, cases=BuildCase.Write)
    @CsvNumberRange(min="0", max="30", order=1, cases=BuildCase.Read)
    public static @interface ComposeOverrideDefault2 {
        
        String value();
        
        String message() default "";
        
        @CsvOverridesAttribute(annotation=CsvNumberRange.class, name="message", index=0)
        String rangeMessage() default "";
        
        Class<?>[] groups() default {};
        
        @CsvOverridesAttribute(annotation=CsvNumberRange.class, name="groups", index=0)
        Class<?>[] rangeGroups() default {};
        
        @CsvOverridesAttribute(annotation=CsvNumberRange.class, name="cases", index=1)
        BuildCase[] rangeCases() default {};
        
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            ComposeOverrideDefault2[] value();
        }
        
    }
    
    /**
     * 合成のアノテーション
     * <p>属性の上書きあり</p>
     * <p>cases、messageやgroupsの共通の属性をオーバーライドする</p>
     * <p>1部のみを書き換える。</p>
     *
     */
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(ComposeOverrideDefault3.List.class)
    @CsvComposition
    @CsvRequire(message="必須です", groups={Group2.class}, cases={BuildCase.Write})
    @CsvNumberRange(min="0", max="20", order=2, groups={Group1.class}, cases={BuildCase.Read})
    @CsvNumberRange(min="0", max="30", order=1)
    public static @interface ComposeOverrideDefault3 {
        
        String value();
        
        @CsvOverridesAttribute(annotation=CsvNumberRange.class, name="message", index=0)
        String message() default "";
        
        @CsvOverridesAttribute(annotation=CsvNumberRange.class, name="cases", index=0)
        BuildCase[] cases() default {};
        
        @CsvOverridesAttribute(annotation=CsvNumberRange.class, name="groups", index=0)
        Class<?>[] groups() default {};
        
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            ComposeOverrideDefault3[] value();
        }
        
    }
    
    /**
     * 合成のアノテーション
     * <p>属性の上書きあり</p>
     * <p>属性名の省略</p>
     *
     */
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @CsvComposition
    @Repeatable(ComposeOverrideDefault4.List.class)
    @CsvNumberRange(min="0", max="20", order=2, groups={Group1.class})
    @CsvNumberRange(min="0", max="30", order=1)
    public static @interface ComposeOverrideDefault4 {
        
        String value();
        
        @CsvOverridesAttribute(annotation=CsvNumberRange.class, index=0)
        String max();
        
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            ComposeOverrideDefault4[] value();
        }
        
    }
    
    /**
     * 合成のアノテーション
     * 一致する上書き対象の属性が見つからない場合
     *
     */
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(ComposeOverrideAttrNotFound1.List.class)
    @CsvComposition
    @CsvRequire(message="必須です")
    @CsvNumberRange(min="0", max="30", order=1)
    public static @interface ComposeOverrideAttrNotFound1 {
        
        String value();
        
        // 存在しない属性
        @CsvOverridesAttribute(annotation=CsvNumberRange.class, name="test")
        String test();
        
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            ComposeOverrideAttrNotFound1[] value();
        }
        
    }
    
    /**
     * 合成のアノテーション
     * 一致する上書き対象の属性が見つからない場合
     * - インデックスが一致しない
     *
     */
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(ComposeOverrideAttrNotFound2.List.class)
    @CsvComposition
    @CsvRequire(message="必須です")
    @CsvNumberRange(min="0", max="30", order=1)
    public static @interface ComposeOverrideAttrNotFound2 {
        
        String value();
        
        // 存在しない属性 - インデックスが不正
        @CsvOverridesAttribute(annotation=CsvNumberRange.class, name="test", index=1)
        String test();
        
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            ComposeOverrideAttrNotFound2[] value();
        }
        
    }
}
