^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
@CsvDateTimeFormat
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

日時型に対する書式を指定する際に利用するアノテーションです。

アノテーションを付与しないときや属性 ``pattern`` を指定しないときは、クラスタイプごとに決まった標準の書式が適用されます。
対応するJavaのクラスタイプと標準の書式は以下の通りです。

.. list-table:: 対応する日時のクラスタイプと標準の書式
   :widths: 50 50
   :header-rows: 1
   
   * - クラスタイプ
     - 標準の書式
     
   * - ``java.util.Date``
     - *yyyy-MM-dd HH:mm:ss*
     
   * - ``java.util.Calendar``
     - *yyyy-MM-dd HH:mm:ss*
     
   * - ``java.sql.Date``
     - *yyyy-MM-dd*
     
   * - ``java.sql.Time``
     - *HH:mm:ss*
     
   * - ``java.sql.Timestamp``
     - *yyyy-MM-dd HH:mm:ss.SSS*
     
   * - ``java.time.LocalDateTime``
     - *uuuu-MM-dd HH:mm:ss*
     
   * - ``java.time.LocalDate``
     - *uuuu-MM-dd*
     
   * - ``java.time.ZonedDateTime``
     - *uuuu-MM-dd HH:mm:ssxxx'['VV']'*
     
   * - ``org.joda.time.LocalDateTime``
     - *yyyy-MM-dd HH:mm:ss*
     
   * - ``org.joda.time.LocalDate``
     - *yyyy-MM-dd*
     
   * - ``org.joda.time.LocalTime``
     - *HH:mm:ss*
     
   * - ``org.joda.time.DateTime``
     - *yyyy-MM-dd HH:mm:ssZZ*


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
読み込み/書き込み時の書式を指定したい場合
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

* 属性 ``pattern`` で書式を指定します。
  
  * 省略した場合、クラスタイプごとの標準の書式が適用されます。
  
  * java.util.Date/java.util.Calendar/java.sql.XXXX系のクラスの場合、 `java.text.SimpleDateFormat <https://docs.oracle.com/javase/jp/8/docs/api/java/text/SimpleDateFormat.html>`_ で解釈可能な書式を設定します。
  
  * java.time.XXX系のクラスの場合、 `java.time.format.DateTimeFormatter <https://docs.oracle.com/javase/jp/8/docs/api/java/time/format/DateTimeFormatter.html>`_ で解釈可能な書式を設定します。
  
  * org.joda.time.XXX系のクラスの場合は、 `org.joda.time.format.DateTimeFormat <http://www.joda.org/joda-time/apidocs/org/joda/time/format/DateTimeFormat.html>`_ で解釈可能な書式を設定します。

* 属性 ``locale`` でロケールを指定します。
  
  * 言語コードのみを指定する場合、 ``ja`` の2桁で指定します。
  * 言語コードと国コードを指定する場合、 ``ja_JP`` のようにアンダーバーで区切り指定します。
  * 和暦を扱う時など、バリアントを指定する場合も同様に、 ``ja_JP_JP`` のようにさらにアンダーバーで区切り指定します。
    
* 属性 ``timezone`` でタイムゾーンを指定します。
  
  * Asia/Tokyo, GMT, GMT+09:00などの値を指定します。  
  * ただし、オフセットを持たないクラスタイプ「LocalDateTime, LocalDate, LocalTime」の時は、指定しても意味がありません。

* 書式に合わない値をパースした場合、例外 ``SuperCsvValidationException`` が発生します。

.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.format.CsvDateTimeFormat;
    
    import java.util.Date;
    import java.time.LocalDateTime;
    
    @CsvBean
    public class SampleCsv {
        
        // 和暦を扱う場合
        @CsvColumn(number=1)
        @CsvDateTimeFormat(pattern="GGGGyy年MM月dd日", locale="ja_JP_JP")
        private Date japaneseDate;
        
        @CsvColumn(number=2, label="更新日時")
        @CsvDateTimeFormat(pattern="uuuu/MM/dd HH:mm:ss")
        private LocalDateTime updateTime;
        
        // setter/getterは省略
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
読み込み時に曖昧に解析したい場合
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

曖昧なケースでも読み込めるようにしたいときは、属性 ``lenient`` の値をtrueにします。

例えば、 *2016-02-31* と存在しない日を解析する場合、 *2016-03-02* と補正が行われます。


.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.format.CsvDateTimeFormat;
    
    import java.time.LocalDate;
    
    @CsvBean
    public class SampleCsv {
        
        @CsvColumn(number=1, label="誕生日")
        @CsvDateTimeFormat(lenient=true)
        private LocalDate birthday;
        
        // setter/getterは省略
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
読み込み時の値のパースに失敗した際のメッセージ
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

読み込み時に値のパースに失敗した時に独自のメッセージとして、属性 ``message`` で指定することができます。

メッセージ中は、`JEXL <http://commons.apache.org/proper/commons-jexl/>`_ による式言語を使うことができ、
予め登録されている変数を用いて、メッセージ内容を独自にカスタマイズすることができます。
詳細は、:doc:`値の検証時のメッセージ <validation_message>` を参照してください。

メッセージ中で利用可能な変数は、`JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/format/CsvDateTimeFormat.html>`_ を参照してください。


.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.format.CsvDateTimeFormat;
    
    import java.time.LocalDate;
    
    @CsvBean
    public class SampleCsv {
        
        @CsvColumn(number=1, label="誕生日")
        @CsvDateTimeFormat(pattern="uuuu/M/d", message="[{rowNumber}行, {columnNumber}列] : 項目「{label}」の値（{validatedValue}）は、日付の書式「{pattern}」として不正です。"
        private LocalDate birthday;
        
        // setter/getterは省略
    }





