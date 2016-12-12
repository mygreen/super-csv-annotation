package com.github.mygreen.supercsv.builder;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;


import org.junit.Before;
import org.junit.Test;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.CsvPartial;
import com.github.mygreen.supercsv.builder.BeanMappingFactory;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;

/**
 * {@link BeanMappingFactory}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class BeanMappingFactoryTest {
    
    private BeanMappingFactory factory;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    
    @Before
    public void setUp() throws Exception {
        factory = new BeanMappingFactory();
    }
    
    /**
     * コンストラクタのテスト - 引数がnullの場合
     */
    @Test(expected=NullPointerException.class)
    public void testConstructor_argNull() {
        
        factory.create(null, groupEmpty);
        
        fail();
        
    }
    
    /**
     * フィールドの修飾子のテスト
     */
    @Test
    public void testCreate_modifires() {
        
        BeanMapping<AllModifiresBean> beanMapping = factory.create(AllModifiresBean.class, groupEmpty);
        
        assertThat(beanMapping.getColumns()).hasSize(4);
        
    }
    
    /**
     * {@link CsvBean}アノテーションが存在しない
     */
    @Test
    public void testCreate_beanAnno_no() {
        
        assertThatThrownBy(() -> factory.create(NoBeanAnnoBean.class, groupEmpty))
                .isInstanceOf(SuperCsvInvalidAnnotationException.class)
                .hasMessage("'%s' において、アノテーション @CsvBean が見つかりません。",
                        NoBeanAnnoBean.class.getName());
    }
    
    /**
     * {@link CsvColumn}アノテーションが存在しない
     */
    @Test
    public void testCreate_columnAnno_no() {
        
        assertThatThrownBy(() -> factory.create(NoColumnAnnoBean.class, groupEmpty))
            .isInstanceOf(SuperCsvInvalidAnnotationException.class)
            .hasMessage("'%s' において、アノテーション @CsvColumn が見つかりません。",
                    NoColumnAnnoBean.class.getName());
        
    }
    
    /**
     * {@link CsvColumn}アノテーションのnumber属性値が0以下の場合
     */
    @Test
    public void testCreate_columnAnno_numberWrong_lessZero() {
        
        assertThatThrownBy(() -> factory.create(ColumnNumberLessZeroBean.class, groupEmpty))
            .isInstanceOf(SuperCsvInvalidAnnotationException.class)
            .hasMessage("'%s' において、アノテーション @CsvColumn の属性 'number' の値（0）は、1以上の値を設定してください。",
                    ColumnNumberLessZeroBean.class.getName());
        
    }
    
    /**
     * {@link CsvColumn}アノテーションのnumber属性値が重複の場合
     */
    @Test
    public void testCreate_columnAnno_numberWrong_duplicate() {
        
        assertThatThrownBy(() -> factory.create(ColumnNumberDuplicateBean.class, groupEmpty))
            .isInstanceOf(SuperCsvInvalidAnnotationException.class)
            .hasMessage("'%s' において、アノテーション @CsvColumn の属性 'number' の値（[2]）が重複しています。",
                    ColumnNumberDuplicateBean.class.getName());
            
    }
    
    /**
     * 部分的なカラムの場合 - {@link CsvPartial}はない。
     */
    @Test
    public void testCreate_partial_noAnno() {
        
        BeanMapping<PartialNoAnnoBean> beanMapping = factory.create(PartialNoAnnoBean.class, groupEmpty);
        
        assertThat(beanMapping.getColumns()).hasSize(4);
        
        {
            // 部分的なカラム
            ColumnMapping column = beanMapping.getColumnMapping(1).get();
            assertThat(column.isPartialized()).isTrue();
            assertThat(column.getField()).isNull();
            assertThat(column.getName()).isNull();
            assertThat(column.getLabel()).isEqualTo("column1");
            assertThat(column.getCellProcessorForReading()).isNull();
            assertThat(column.getCellProcessorForWriting()).isNull();
            
        }
        
        {
            // 通常のカラム
            ColumnMapping column = beanMapping.getColumnMapping(2).get();
            assertThat(column.isPartialized()).isFalse();
            assertThat(column.getField()).isNotNull();
            assertThat(column.getName()).isEqualTo("col2");
            assertThat(column.getLabel()).isEqualTo("col2");
            assertThat(column.getCellProcessorForReading()).isNotNull();
            assertThat(column.getCellProcessorForWriting()).isNotNull();
            
        }
        
        {
            // 部分的なカラム
            ColumnMapping column = beanMapping.getColumnMapping(3).get();
            assertThat(column.isPartialized()).isTrue();
            assertThat(column.getField()).isNull();
            assertThat(column.getName()).isNull();
            assertThat(column.getLabel()).isEqualTo("column3");
            assertThat(column.getCellProcessorForReading()).isNull();
            assertThat(column.getCellProcessorForWriting()).isNull();
            
        }
        
        {
            // 通常のカラム
            ColumnMapping column = beanMapping.getColumnMapping(4).get();
            assertThat(column.isPartialized()).isFalse();
            assertThat(column.getField()).isNotNull();
            assertThat(column.getName()).isEqualTo("col4");
            assertThat(column.getLabel()).isEqualTo("カラム4");
            assertThat(column.getCellProcessorForReading()).isNotNull();
            assertThat(column.getCellProcessorForWriting()).isNotNull();
            
        }
        
    }
    
    /**
     * 部分的なカラムの場合 - {@link CsvPartial}あり
     */
    @Test
    public void testCreate_partial_existAnno() {
        
        BeanMapping<PartialExistAnnoBean> beanMapping = factory.create(PartialExistAnnoBean.class, groupEmpty);
        
        assertThat(beanMapping.getColumns()).hasSize(5);
        
        {
            // 部分的なカラム
            ColumnMapping column = beanMapping.getColumnMapping(1).get();
            assertThat(column.isPartialized()).isTrue();
            assertThat(column.getField()).isNull();
            assertThat(column.getName()).isNull();
            assertThat(column.getLabel()).isEqualTo("カラム1");
            assertThat(column.getCellProcessorForReading()).isNull();
            assertThat(column.getCellProcessorForWriting()).isNull();
            
        }
        
        {
            // 通常のカラム
            ColumnMapping column = beanMapping.getColumnMapping(2).get();
            assertThat(column.isPartialized()).isFalse();
            assertThat(column.getField()).isNotNull();
            assertThat(column.getName()).isEqualTo("col2");
            assertThat(column.getLabel()).isEqualTo("col2");
            assertThat(column.getCellProcessorForReading()).isNotNull();
            assertThat(column.getCellProcessorForWriting()).isNotNull();
            
        }
        
        {
            // 部分的なカラム
            ColumnMapping column = beanMapping.getColumnMapping(3).get();
            assertThat(column.isPartialized()).isTrue();
            assertThat(column.getField()).isNull();
            assertThat(column.getName()).isNull();
            assertThat(column.getLabel()).isEqualTo("カラム3");
            assertThat(column.getCellProcessorForReading()).isNull();
            assertThat(column.getCellProcessorForWriting()).isNull();
            
        }
        
        {
            // 通常のカラム
            ColumnMapping column = beanMapping.getColumnMapping(4).get();
            assertThat(column.isPartialized()).isFalse();
            assertThat(column.getField()).isNotNull();
            assertThat(column.getName()).isEqualTo("col4");
            assertThat(column.getLabel()).isEqualTo("カラム4");
            assertThat(column.getCellProcessorForReading()).isNotNull();
            assertThat(column.getCellProcessorForWriting()).isNotNull();
        }
        
        {
            // 部分的なカラム
            ColumnMapping column = beanMapping.getColumnMapping(5).get();
            assertThat(column.isPartialized()).isTrue();
            assertThat(column.getField()).isNull();
            assertThat(column.getName()).isNull();
            assertThat(column.getLabel()).isEqualTo("カラム5");
            assertThat(column.getCellProcessorForReading()).isNull();
            assertThat(column.getCellProcessorForWriting()).isNull();
            
        }
        
    }
    
    /**
     * 部分的なカラムの場合 - {@link CsvPartial}のcolumnSizeの値が、定義されているカラムよりも小さい場合
     */
    @Test
    public void testCreate_partial_overSize() {
        
        assertThatThrownBy(() -> factory.create(PartialOverSizeBean.class, groupEmpty))
            .isInstanceOf(SuperCsvInvalidAnnotationException.class)
            .hasMessage("'%s' において、アノテーション @CsvPartial の属性 'columnSize' の値 (2) は、定義している最大の @CsvColumn の属性 'number' の値（4）以上の設定をしてください。",
                    PartialOverSizeBean.class.getName());
        
    }
    
    /**
     * 修飾子の確認用のBean
     *
     */
    @CsvBean
    class AllModifiresBean {
        
        @CsvColumn(number=1)
        public String fpublic;
        
        @CsvColumn(number=2)
        protected String fprotected;
        
        @CsvColumn(number=3)
        String fdefault;
        
        @CsvColumn(number=4)
        private String fprivate;
        
        public String getFpublic() {
            return fpublic;
        }
        
        public void setFpublic(String fpublic) {
            this.fpublic = fpublic;
        }
        
        public String getFprotected() {
            return fprotected;
        }
        
        public void setFprotected(String fprotected) {
            this.fprotected = fprotected;
        }
        
        public String getFdefault() {
            return fdefault;
        }
        
        public void setFdefault(String fdefault) {
            this.fdefault = fdefault;
        }
        
        public String getFprivate() {
            return fprivate;
        }
        
        public void setFprivate(String fprivate) {
            this.fprivate = fprivate;
        }
    }
    
    /**
     * Bean用のアノテーションがない
     *
     */
    private static class NoBeanAnnoBean {
        
        @CsvColumn(number=1)
        private String col1;
        
    }
    
    /**
     * カラム用のアノテーションがない
     *
     */
    @CsvBean
    private static class NoColumnAnnoBean {
        
        private String col1;
        
    }
    
    /**
     * カラムの番号が0以下の場合
     *
     */
    @CsvBean
    private static class ColumnNumberLessZeroBean {
        
        @CsvColumn(number=0)
        private String col1;
        
        @CsvColumn(number=1)
        private String col2;
    }
    
    /**
     * カラムの番号が重複の場合
     *
     */
    @CsvBean
    private static class ColumnNumberDuplicateBean {
        
        @CsvColumn(number=1)
        private String col1;
        
        @CsvColumn(number=2)
        private String col2;
        
        @CsvColumn(number=2)
        private String col3;
        
        @CsvColumn(number=4)
        private String col4;
    }
    
    /**
     * 部分的なカラムの場合 - {@link CsvPartial}はない。
     *
     */
    @CsvBean
    private static class PartialNoAnnoBean {
        
        @CsvColumn(number=2)
        private String col2;
        
        @CsvColumn(number=4, label="カラム4")
        private String col4;
        
    }
    
    /**
     * 部分的なカラムの場合 - {@link CsvPartial}あり。
     *
     */
    @CsvBean
    @CsvPartial(columnSize=5, headers={
            @CsvPartial.Header(number=1, label="カラム1"),
            @CsvPartial.Header(number=3, label="カラム3"),
            @CsvPartial.Header(number=5, label="カラム5"),
    })
    private static class PartialExistAnnoBean {
        
        @CsvColumn(number=2)
        private String col2;
        
        @CsvColumn(number=4, label="カラム4")
        private String col4;
        
    }
    
    /**
     * 部分的なカラムの場合 - 想定のサイズが定義されているカラムよりも小さい場合
     *
     */
    @CsvBean
    @CsvPartial(columnSize=2)
    private static class PartialOverSizeBean {
        
        @CsvColumn(number=2)
        private String col2;
        
        @CsvColumn(number=4, label="カラム4")
        private String col4;
        
    }
}
