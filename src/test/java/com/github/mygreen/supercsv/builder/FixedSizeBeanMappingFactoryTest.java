package com.github.mygreen.supercsv.builder;

import static org.assertj.core.api.Assertions.*;

import org.junit.Before;
import org.junit.Test;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.CsvPartial;
import com.github.mygreen.supercsv.annotation.conversion.CsvFixedSize;
import com.github.mygreen.supercsv.cellprocessor.conversion.ByteSizePaddingProcessor;
import com.github.mygreen.supercsv.cellprocessor.conversion.CharWidthPaddingProcessor;
import com.github.mygreen.supercsv.cellprocessor.conversion.SimplePaddingProcessor;
import com.github.mygreen.supercsv.exception.SuperCsvInvalidAnnotationException;


/**
 * {@link FixedSizeBeanMappingFactory}のテストクラス。
 *
 * @since 2.5
 * @author T.TSUCHIE
 *
 */
public class FixedSizeBeanMappingFactoryTest {
    
    private FixedSizeBeanMappingFactory factory;
    
    private final Class<?>[] groupEmpty = new Class[]{};
    
    @Before
    public void setUp() throws Exception {
        this.factory = new FixedSizeBeanMappingFactory();
    }
    
    @Test
    public void testCreate_normal() {
        
        BeanMapping<NormalFixedSizeBean> beanMapping = factory.create(NormalFixedSizeBean.class, groupEmpty);
        
        {
            FixedSizeColumnProperty col = beanMapping.getColumnMapping(1).get().getFixedSizeProperty();
            assertThat(col.getSize()).isEqualTo(5);
            assertThat(col.isRightAlign()).isFalse();
            assertThat(col.isChopped()).isFalse();
            assertThat(col.getPadChar()).isEqualTo('0');
            assertThat(col.getPaddingProcessor()).isInstanceOf(CharWidthPaddingProcessor.class);
        }
        
        {
            FixedSizeColumnProperty col = beanMapping.getColumnMapping(2).get().getFixedSizeProperty();
            assertThat(col.getSize()).isEqualTo(10);
            assertThat(col.isRightAlign()).isTrue();
            assertThat(col.isChopped()).isFalse();
            assertThat(col.getPadChar()).isEqualTo(' ');
            assertThat(col.getPaddingProcessor()).isInstanceOf(SimplePaddingProcessor.class);
        }
        
        {
            FixedSizeColumnProperty col = beanMapping.getColumnMapping(3).get().getFixedSizeProperty();
            assertThat(col.getSize()).isEqualTo(20);
            assertThat(col.isRightAlign()).isFalse();
            assertThat(col.isChopped()).isTrue();
            assertThat(col.getPadChar()).isEqualTo('-');
            assertThat(col.getPaddingProcessor()).isInstanceOf(ByteSizePaddingProcessor.Utf8.class);
        }
        
    }
    
    /**
     * {@link CsvFixedSize}の設定が存在しない。
     */
    @Test
    public void testCreate_lackFixedSizeAnno() {
        assertThatThrownBy(() -> factory.create(LackFixedSizeBean.class, groupEmpty))
            .isInstanceOf(SuperCsvInvalidAnnotationException.class)
            .hasMessage("'%s' において、固定長CSV用のアノテーション @CsvFixedSize を [col3(3)] に設定してください。",
                    LackFixedSizeBean.class.getName());
    }
    
    /**
     * {@link CsvFixedSize}の設定が存在しない。 - 文的なカラムの定義
     */
    @Test
    public void testCreate_lackFixedSizeAnno_partial() {
        assertThatThrownBy(() -> factory.create(LackFixedSizeParitalBean.class, groupEmpty))
            .isInstanceOf(SuperCsvInvalidAnnotationException.class)
            .hasMessage("'%s' において、固定長CSV用のアノテーション @CsvFixedSize を [col3(3)] に設定してください。",
                    LackFixedSizeParitalBean.class.getName());
    }
    
    /**
     * 正常なCSV固定長の定義。
     *
     */
    @CsvBean
    private static class NormalFixedSizeBean {
        
        @CsvFixedSize(size=5, padChar='0')
        @CsvColumn(number=1)
        private Integer no;
        
        @CsvFixedSize(size=10, rightAlign=true, padChar=' ', paddingProcessor=SimplePaddingProcessor.class)
        @CsvColumn(number=2, label="名前")
        private String name;
        
        @CsvFixedSize(size=20, chopped=true, padChar='-', paddingProcessor=ByteSizePaddingProcessor.Utf8.class)
        @CsvColumn(number=3, label="コメント")
        private String comment;
        
    }
    
    /**
     * 固定長の設定がない場合
     *
     */
    @CsvBean
    private static class LackFixedSizeBean {
        
        @CsvFixedSize(size=6)
        @CsvColumn(number=1)
        private String col1;
        
        @CsvFixedSize(size=7)
        @CsvColumn(number=2)
        private String col2;
        
        @CsvColumn(number=3)
        private String col3;
        
        @CsvFixedSize(size=9)
        @CsvColumn(number=4)
        private String col4;
        
    }
    
    /**
     * 固定長の設定がない場合 - 文的なカラムの定義
     *
     */
    @CsvPartial(columnSize = 5, headers={
            @CsvPartial.Header(number=3, label="col3"),
            @CsvPartial.Header(number=5, label="col5", fixedSize=@CsvFixedSize(size=9))
    })
    @CsvBean
    private static class LackFixedSizeParitalBean {
        
        @CsvFixedSize(size=6)
        @CsvColumn(number=1)
        private String col1;
        
        @CsvFixedSize(size=7)
        @CsvColumn(number=2)
        private String col2;
        
        @CsvFixedSize(size=9)
        @CsvColumn(number=4)
        private String col4;
        
    }
}
