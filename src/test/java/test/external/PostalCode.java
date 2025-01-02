package test.external;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Optional;

import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.util.CsvContext;

import com.github.mygreen.supercsv.annotation.conversion.CsvConversion;
import com.github.mygreen.supercsv.builder.BuildCase;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.AbstractTextFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextParseException;
import com.github.mygreen.supercsv.util.Utils;

/**
 * 外部パッケージの独自の型。
 * <p>郵便番号を表現する。
 *
 *
 * @author T.TSUCHIE
 *
 */
public class PostalCode implements Serializable {

    private static final String SEPARATOR = "-";
    
    /** 上3桁 */
    private String code1;
    
    /** 下4桁 */
    private String code2;
    
    public PostalCode(final String value) {
        if(!parse(value)) {
            throw new IllegalArgumentException("not support format : " + value);
        }
    }

    public boolean parse(final String value) {
        
        if(Utils.isEmpty(value)) {
            return false;
        }
        
        String[] splits = value.split(SEPARATOR);
        if(splits.length != 2) {
            return false;
        }
        
        if(splits[0].length() != 3) {
            return false;
        }
        
        this.code1 = splits[0];
        
        if(splits[1].length() != 4) {
            return false;
        }
        
        this.code2 = splits[1];
        
        return true;
    }
    
    @Override
    public String toString() {
        return code1 + SEPARATOR + code2;
    }
    
    public String getCode1() {
        return code1;
    }
    
    public String getCode2() {
        return code2;
    }
    
    /**
     * 変換処理用のCellProcessor
     */
    public static class PostalCodeCellProcessor extends CellProcessorAdaptor implements CellProcessor {

        public PostalCodeCellProcessor() {
            super();
        }
        
        public PostalCodeCellProcessor(final CellProcessor next) {
            super(next);
        }
        
        @Override
        public <T> T execute(Object value, CsvContext context) {
            if(value == null) {
                return next.execute(value, context);
            }
            
            final PostalCode result = new PostalCode(value.toString());
            return next.execute(result, context);
        }
        
    }
    
    /**
     * 変換処理用のFactory
     *
     */
    public static class PostalCodeFactory implements ConversionProcessorFactory<CsvPostalCode> {

        @Override
        public Optional<CellProcessor> create(CsvPostalCode anno, Optional<CellProcessor> next,
                FieldAccessor field, TextFormatter<?> formatter, Configuration config) {
            
            // CellProcessorのインスタンスを作成します
            final PostalCodeCellProcessor processor = next.map(n ->  new PostalCodeCellProcessor(n))
                    .orElseGet(() -> new PostalCodeCellProcessor());

            return Optional.of(processor);
        }
        
    }
    
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(CsvPostalCode.List.class)
    @CsvConversion(PostalCodeFactory.class)
    public @interface CsvPostalCode {
        
        /** 共通の属性 - ケース */
        BuildCase[] cases() default {};

        /** 共通の属性 - グループ **/
        Class<?>[] groups() default {};

        /** 共通の属性 - 並び順 **/
        int order() default 0;

        // 繰り返しのアノテーションの格納用アノテーションの定義
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {

            CsvPostalCode[] value();
        }
        
    }
    
    public static class PostalCodeFormatter extends AbstractTextFormatter<PostalCode> {

        @Override
        public PostalCode parse(String text) {
            try {
                return new PostalCode(text);
            } catch(Exception e) {
                throw new TextParseException(text, PostalCode.class, e);
            }
        }

        @Override
        public String print(PostalCode object) {
            return object.toString();
        }
        
    }
    
}
