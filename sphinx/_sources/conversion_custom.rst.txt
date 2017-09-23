--------------------------------------------------------
独自の変換処理の作成方法
--------------------------------------------------------

独自の変換処理を実装するには、3つのステップを踏む必要があります。

1. CellProcessorの実装クラスの作成
2. アノテーションクラスの作成
3. CellProcessorを作成するためのファクトリクラスの作成


以下、それぞれに対して解説していきます。

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
CellProcessorの実装クラスの作成
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

サンプルとして、任意の文字列を追加するCellProcessorを作成します。

*  抽象クラス ``CellProcessorAdaptor`` [ `JavaDoc <http://super-csv.github.io/super-csv/apidocs/org/supercsv/cellprocessor/CellProcessorAdaptor.html>`_ ] を継承します。

  * CellProcessorは、「Chain of Responsibility」パターンであるため、その構造を表現するためのクラスとなります。

* インタフェースとして ``StringCellProcessor`` [ `JavaDoc <http://super-csv.github.io/super-csv/apidocs/org/supercsv/cellprocessor/ift/StringCellProcessor.html>`_ ] を実装します。

  * この実装は特に必要ないですが、扱うカラムの値の種類を表現するめのものです。
    CellProcessorを直接組み立てる時に、これらのインタフェースでchainとして追加する次のCellProcessorを限定するために使用します。
  * 変換処理は、必ず文字列に対して行うため実装しておきます。

* コンストラクタとして、chainの次の処理となるCellProcessorを引数に持つものと、持たないものを必ず2つ実装します。

* メソッド ``execute(...)`` 内で処理の実装を行います。
  
  * nullの場合、次の処理に委譲するようにします。
    Super CSVの既存のCellProcessorではメソッドvalidateInputNotNull(...)を呼びnullチェックを行いますが、
    本ライブラリではnullに対する処理は他のCellProcessorで行うため、次の処理に渡します。
  
  * 変換した値を次の処理に渡します。

.. sourcecode:: java
    :linenos:
    
    
    import org.supercsv.cellprocessor.CellProcessorAdaptor;
    import org.supercsv.cellprocessor.ift.CellProcessor;
    import org.supercsv.cellprocessor.ift.StringCellProcessor;
    import org.supercsv.util.CsvContext;
    
    // 独自の変換用のCellProcessor
    public class CustomConversion extends CellProcessorAdaptor implements StringCellProcessor {
        
        private String text;
        
        public CustomConversion(final String text) {
            super();
            checkPreconditions(text);
            this.text = text;
        }
        
        public CustomConversion(final String text, final CellProcessor next) {
            super(next);
            checkPreconditions(text);
            this.text = text;
        }
        
        // コンストラクタで渡した独自の引数のチェック処理
        private static void checkPreconditions(final String text) {
            if(text == null) {
                throw new NullPointerException("text should not be null.");
            }
        }
        
        @Override
        public <T> T execute(final Object value, final CsvContext context) {
            if(value == null) {
                // nullの場合、次の処理に委譲します。
                return next.execute(value, context);
            }
            
            // 最後尾に文字列を足す
            final String result = value.toString() + text;
            
            // 変換した値を次の処理に委譲します。
            return next.execute(result, context);
        }
        
    }



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
変換処理用のアノテーションクラスの作成
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

* ``@Target`` として、``ElementType.FIELD`` と ``ElementType.ANNOTATION_TYPE`` の2つを指定します。

  * 通常はFieldのみで問題ないですが、 :doc:`アノテーションを合成 <composition>` するときがあるため、 *ANNOTATION_TYPE* も追加しておきます。

* ``@Repeatable`` として、複数のアノテーションを設定できるようにします。

  * 内部クラスのアノテーションとして、 *List* を定義します。

* 変換用のアノテーションと示すためのメタアノテーション ``@CsvConversion`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvConversion.html>`_ ]を指定します。
* 共通の属性として、 ``cases`` と ``groups`` 、 ``order`` を定義します。
* 固有の属性 として、``text`` を定義します。これはCellProcessorに渡す値となります。


.. sourcecode:: java
    :linenos:
    
    import java.lang.annotation.Documented;
    import java.lang.annotation.ElementType;
    import java.lang.annotation.Repeatable;
    import java.lang.annotation.Retention;
    import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;
    
    import com.github.mygreen.supercsv.annotation.conversion.CsvConversion;
    import com.github.mygreen.supercsv.builder.BuildCase;
    
    
    // 独自の変換用のアノテーション
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(CsvCustomConversion.List.class)
    @CsvConversion(CustomConversionFactory.class)  // ファクトリクラスを指定
    public @interface CsvCustomConversion {
        
        // 固有の属性 - 追加する値を指定します。
        String text();
        
        // 共通の属性 - ケース
        BuildCase[] cases() default {};
        
        // 共通の属性 - グループ
        Class<?>[] groups() default {};
        
        // 共通の属性 - 並び順
        int order() default 0;
        
        // 繰り返しのアノテーションの格納用アノテーションの定義
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            CsvCustomConversion[] value();
        }
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
変換処理用のファクトリクラスの作成
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

アノテーションをハンドリングして、CellProcessorを作成するためのファクトリクラスを作成します。

* インタフェース ``ConversionProcessorFactory`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/cellprocessor/ConversionProcessorFactory.html>`_ ] を実装します。
* 独自のCellProcessorのCustomConversionのインスタンスを作成します。
* Chainの次の処理となるCellProcessorの変数「next」は、空であることがあるため、コンストラクタで分けます。

.. sourcecode:: java
    :linenos:
    
    
    import com.github.mygreen.supercsv.builder.BuildType;
    import com.github.mygreen.supercsv.builder.Configuration;
    import com.github.mygreen.supercsv.builder.FieldAccessor;
    import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;
    import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
    
    public class CustomConversionFactory implements ConversionProcessorFactory<CsvCustomConversion> {
        
        @Override
        public Optional<CellProcessor> create(CsvCustomConversion anno, Optional<CellProcessor> next,
                FieldAccessor field, TextFormatter<?> formatter, Configuration config) {
            
            // CellProcessorのインスタンスを作成します
            final CustomConversion processor = next.map(n ->  new CustomConversion(anno.text(), n))
                    .orElseGet(() -> new CustomConversion(anno.text()));
            
            return Optional.of(processor);
            
        }
        
    }

