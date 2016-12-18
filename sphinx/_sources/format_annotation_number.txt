^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
@CsvNumberFormat
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

数値型に対する書式を指定する際に利用するアノテーションです。

対応するJavaのクラスタイプは以下の通りです。

* ``byte/short/int/long/float/double`` のプリミティブ型とそのラッパークラス。
* ``java.math.BigDecimal`` / ``java.math.BigInteger`` の数値クラス。

.. note::
   
   プリミティブ型に対して読み込む際に、CSVのカラムの値が空の場合、それぞれのプリミティブ型の初期値が設定されます。
   
   整数型の場合は ``0`` が、小数型の場合は ``0.0`` が設定されます。
   初期値を変更したい場合は、アノテーション ``@CsvDefaultValue`` [`JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvDefaultValue.html>`_]を使用してください。



~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
読み込み/書き込み時の書式を指定したい場合
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

* 属性 ``pattern`` で書式を指定します。
    
  * Javaのクラス `java.text.DecimalFormat <https://docs.oracle.com/javase/jp/8/docs/api/java/text/DecimalFormat.html>`_ で解釈可能な書式を設定します。
  
* 属性 ``locale`` でロケールを指定します。
  
  * 言語コードのみを指定する場合、'ja'の2桁で指定します。
  * 言語コードと国コードを指定する場合、'ja _JP'のようにアンダーバーで区切り指定します。
    
* 属性 ``currency`` で通貨コード（ `ISO-4217コード <https://ja.wikipedia.org/wiki/ISO_4217>`_ ）を指定します。
    
  * Javaのクラス ``java.util.Currency`` で解釈可能なコードを指定します。

* 書式に合わない値をパースした場合、例外 ``SuperCsvValidationException`` が発生します。

.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.format.CsvNumberFormat;
    
    @CsvBean
    public class SampleCsv {
        
        @CsvColumn(number=1)
        @CsvNumberFormat(pattern="#,##0")
        private int number;
        
        @CsvColumn(number=2, label="給与")
        @CsvNumberFormat(pattern="\u00A4\u00A4 #,##0.0000", locale="ja_JP", currency="USD")
        private Double salary;
        
        // setter/getterは省略
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
読み込み時に曖昧に解析したい場合
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

曖昧なケースでも読み込めるようにしたいときは、属性 ``lenient`` の値をtrueにします。

例えば、 *12.51* と小数を整数型にマッピングする場合、*13* と丸めの補正が行われます。
また、 *123,456.0ab* のように、途中から数値以外の文字が出現した場合、それまでの文字 *123,456.0* を抽出して処理が行われます。


.. note::
    
    数値型へのパースは、本ライブラリでは ``java.text.DecimalFormat`` を利用し、
    結果を一旦 ``java.math.BigDecimal`` で読み込み、そこからさらに、各クラスタイプに変換しています。
    
    *DecimalFormat#parse(...)* は、``123,456.0abc`` のように、途中から数値以外の文字が出現した場合、それまでの文字を読み込み、正常処理することができます。
    
    *BigDecimal* で、`1234.567` という小数を読み込み、その値を ``BigDecimal#intValue()`` で整数のint型として取得した場合、小数部分は無視されます。
    int型で扱える桁数を超えたときも自動的に切り捨てられます。



.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.format.CsvNumberFormat;
    
    @CsvBean
    public class SampleCsv {
        
        @CsvColumn(number=1)
        @CsvNumberFormat(pattern="#,##0", lenient=true)
        private int number;
        
        @CsvColumn(number=2, label="給与")
        @CsvNumberFormat(lenient=true)
        private Double salary;
        
        // setter/getterは省略
    }



~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
丸めの精度と方法を指定したい場合
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

* 属性 ``precision`` で丸めの精度を指定することができます。

  * この属性は、属性patternを書式を指定しない場合に有効になります。
  * 主に小数の場合に有効桁数を揃える際に利用します。
    例えば、precision=4で、文字列 ``123.45`` を double型にマッピングする場合、結果は ``123.4`` として読み込まれます。
  
* 属性 ``rounding`` で、丸め方の方法を指定します。
  
  * 値は、列挙型 ``java.math.RoundingMode`` を設定します。
  * デフォルトでは、``RoundingMode.HALF_EVEN`` です。
    詳細は、 `RoundingModeのJavaDoc <https://docs.oracle.com/javase/jp/8/docs/api/java/math/RoundingMode.html>`_ を参照してください。
  * 属性patternを指定した場合は、書式自身が精度を表現しており、属性roundingで丸めの方法を指定することができます。


.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.format.CsvNumberFormat;
    
    import java.math.RoundingMode;
    
    @CsvBean
    public class SampleCsv {
        
        // 丸めの精度と方法を指定する場合
        @CsvColumn(number=1, label="比率")
        @CsvNumberFormat(precision=4, rounding=RoundingMode.HALF_UP)
        private double rate;
        
        // 書式と丸め方法を指定する場合
        @CsvColumn(number=2, label="給与")
        @XlsNumberFormat(pattern="#,##0", rounding=RoundingMode.CEILING, lenient=true)
        private int salary;
        
        // setter/getterは省略
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
読み込み時の値のパースに失敗した際のメッセージ
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

読み込み時に値のパースに失敗した時に独自のメッセージとして、属性 ``message`` で指定することができます。

メッセージ中は、`JEXL <http://commons.apache.org/proper/commons-jexl/>`_ による式言語を使うことができ、
予め登録されている変数を用いて、メッセージ内容を独自にカスタマイズすることができます。
詳細は、:doc:`値の検証時のメッセージ <validation_message>` を参照してください。

メッセージ中で利用可能な変数は、`JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/format/CsvNumberFormat.html>`_ を参照してください。


.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.format.CsvNumberFormat;
    
    @CsvBean
    public class SampleCsv {
        
        @CsvColumn(number=1, label="給料")
        @CsvNumberFormat(pattern="", message="[{rowNumber}行, {columnNumber}列] : 項目「{label}」の値（{validatedValue}）は、整数の書式「{pattern}」として不正です。"
        private int salary;
        
        // setter/getterは省略
    }


