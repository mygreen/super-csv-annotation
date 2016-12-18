--------------------------------------------------------
独自の検証処理の作成方法
--------------------------------------------------------

独自の検証処理を実装するには、3つのステップを踏む必要があります。

1. CellProcessorの実装クラスの作成
2. アノテーションクラスの作成
3. CellProcessorを作成するためのファクトリクラスの作成


以下、それぞれに対して解説していきます。

^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
CellProcessorの実装クラスの作成
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

サンプルとして、最後が任意の文字で終わるか検証するCellProcessorを作成します。

* 抽象クラス ``ValidationCellProcessor`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/cellprocessor/ValidationCellProcessor.html>`_ ] を継承して作成します。

  * *ValidationCellProcessor* は、値の検証に特化したCellProcessorの実装です。
  * CellProcessorは、「Chain of Responsibility」パターンであるため、その構造を表現するためのクラスとなります。

* 今回は、文字列型の値を検証するため、インタフェースとして ``StringCellProcessor`` [ `JavaDoc <http://super-csv.github.io/super-csv/apidocs/org/supercsv/cellprocessor/ift/StringCellProcessor.html>`_ ] を実装します。

  * この実装は特に必要ないですが、扱うカラムの値の種類を表現するめのものです。
    CellProcessorを直接組み立てる時に、これらのインタフェースでchainとして追加する次のCellProcessorを限定するために使用します。
  * 扱う値が数値型のときは ``LongCellProcessor`` [ `JavaDoc <http://super-csv.github.io/super-csv/apidocs/org/supercsv/cellprocessor/ift/LongCellProcessor.html>`_ ]などと、扱う値によって変更してください。

* コンストラクタとして、chainの次の処理となるCellProcessorを引数に持つものと、持たないものを必ず2つ実装します。

* メソッド ``execute(...)`` 内で処理の実装を行います。
  
  * nullの場合、次の処理に委譲するようにします。
    Super CSVの既存のCellProcessorではメソッドvalidateInputNotNull(...)を呼びnullチェックを行いますが、
    本ライブラリではnullに対する処理は他のCellProcessorで行うため、次の処理に渡します。
  
  * 検証対象のクラスタイプが不正な場合は、例外 ``SuperCsvCellProcessorException`` をスローします。
    アノテーションを間違った型に付与した場合に発生する場合がありますが、ファクトリクラスの段階で弾いてもかまいません。
  
  * 正常な値であれば、次の処理に渡します。
  * 問題がある場合、例外 ``SuperCsvValidationException`` をスローします。
    その際に、メソッド *createValidationException(...)* を呼び出して、ビルダクラスを利用して例外クラスを組み立てます。


.. sourcecode:: java
    :linenos:
    
    import org.supercsv.cellprocessor.ift.CellProcessor;
    import org.supercsv.cellprocessor.ift.StringCellProcessor;
    import org.supercsv.util.CsvContext;
    
    import com.github.mygreen.supercsv.cellprocessor.ValidationCellProcessor;
    
    // 独自の値の検証用のCellProcessor
    public class CustomConstraint extends ValidationCellProcessor
            implements StringCellProcessor {
        
        private String text;
        
        public CustomConstraint(final String text) {
            super();
            checkPreconditions(text);
            this.text = text;
        }
        
        public CustomConstraint(final String text, final CellProcessor next) {
            super(next);
            checkPreconditions(text);
            this.text = text;
        }
        
        // コンストラクタで渡した独自の引数のチェック処理
        private static void checkPreconditions(final String text) {
            if(text == null) {
                throw new NullPointerException("text should not be null.");
            } else if(text.isEmpty()) {
                throw new NullPointerException("text should not be empty.");
            }
        }
        
        @Override
        public <T> T execute(final Object value, final CsvContext context) {
            if(value == null) {
                // nullの場合、次の処理に委譲します。
                return next.execute(value, context);
            }
            
            final String result;
            if(value instanceof String) {
                result = (String)value;
                
            } else {
                // 検証対象のクラスタイプが不正な場合
                throw new SuperCsvCellProcessorException(String.class, value, context, this);
            }
            
            // 最後が指定した値で終了するかどうか
            if(result.endsWith(text)) {
                // 正常な値の場合、次の処理に委譲します。
                return next.execute(value, context);
            }
            
            // エラーがある場合、例外クラスを組み立てます。
            throw createValidationException(context)
                .messageFormat("Not ends with %s.", text)
                .messageVariables("suffix", text)
                .build();
            
        }
        
    }



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
値の検証用のアノテーションクラスの作成
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


* ``@Target`` として、``ElementType.FIELD`` と ``ElementType.ANNOTATION_TYPE`` の2つを指定します。

  * 通常はFieldのみで問題ないですが、 :doc:`アノテーションを合成 <composition>` するときがあるため、 *ANNOTATION_TYPE* も追加しておきます。

* ``@Repeatable`` として、複数のアノテーションを設定できるようにします。

  * 内部アノテーションとして、 *List* を定義します。

* 値の検証用のアノテーションであることを示すためのメタアノテーション ``@CsvConstraint`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvConstraint.html>`_ ]を指定します。
  
  * 属性 ``value`` に、``ConstraintProcessorFactory`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/cellprocessor/ConstraintProcessorFactory.html>`_ ]を実装したCellProcessorのファクトリクラスの実装を指定します。
  
* 共通の属性として、 ``cases`` と ``groups`` 、 ``order`` を定義します。
  
  * 省略した場合は、それぞれのデフォルト値が適用されます。
  
* 必要であれば、固有の属性を定義します。今回は、``text`` を定義します。これはCellProcessorに渡す値となります。

.. sourcecode:: java
    :linenos:
    
    import java.lang.annotation.Documented;
    import java.lang.annotation.ElementType;
    import java.lang.annotation.Repeatable;
    import java.lang.annotation.Retention;
    import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;
    
    import com.github.mygreen.supercsv.annotation.constraint.CsvConstraint;
    import com.github.mygreen.supercsv.builder.BuildCase;
    
    
    // 独自の値の検証用のアノテーション
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(CsvCustomConstraint.List.class)
    @CsvConstraint(CustomConstratinFactory.class)  // ファクトリクラスを指定
    public @interface CsvCustomConstraint {
        
        // 固有の属性 - チェックすることとなる最後の文字を指定します。
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
            
            CsvCustomConstraint[] value();
        }
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
値の検証用のファクトリクラスの作成
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

アノテーションをハンドリングして、CellProcessorを作成するためのファクトリクラスを作成します。

* インタフェース ``ConstraintProcessorFactory`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/cellprocessor/ConstraintProcessorFactory.html>`_ ]を実装します。
* アノテーションが検証対象のクラスタイプ以外に付与される場合があるため、その際は無視するようにします。
* 独自のCellProcessorのCustomConstraintのインスタンスを作成します。
* Chainの次の処理となるCellProcessorの変数「next」は、空であることがあるため、コンストラクタで分けます。

.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.builder.BuildType;
    import com.github.mygreen.supercsv.builder.Configuration;
    import com.github.mygreen.supercsv.builder.FieldAccessor;
    import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
    import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;
    
    public class CustomConstraintFactory implements ConstraintProcessorFactory<CsvCustomConstraint> {
        
        @Override
        public Optional<CellProcessor> create(CsvCustomConstraint anno, Optional<CellProcessor> next,
                FieldAccessor field, TextFormatter<?> formatter, Configuration config) {
            
            if(!String.class.isAssignableFrom(field.getType())) {
                // 検証対象のクラスタイプと一致しない場合は、弾きます。
                return next;
            }
            
            // CellProcessorのインスタンスを作成します
            final CustomConstraint processor = next.map(n ->  new CustomConstraint(anno.value(), n))
                    .orElseGet(() -> new CustomConstraint(anno.value()));
            
            return Optional.of(processor);
            
        }
        
    }






