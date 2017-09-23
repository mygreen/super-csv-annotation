======================================
機能概要
======================================

本ライブラリは、JavaのCSVファイルのライブラリ「 `Super CSV <http://super-csv.github.io/super-csv/>`_ 」に、
アノテーション機能を追加したものです。

アノテーションを利用することで、煩わしいCellProcessorの組み立てを簡略化ですることがきます。


----------------------------------------
前提条件
----------------------------------------

本ライブラリの前提条件を以下に示します。

Super CSV本体の前提のJavaは、ver.1.5以上ですが、本ライブラリは、Java 1.8以上が前提となります。

全て独自のCellProcessorを利用しているため、
Super CSVの拡張ライブラリ
「 `Super CSV Java 8 Extension <http://super-csv.github.io/super-csv/super-csv-java8/index.html>`_ 」
「 `Super CSV Joda-Time Extension <http://super-csv.github.io/super-csv/super-csv-joda/index.html>`_ 」
は必要ありません。

.. list-table:: 前提条件
   :widths: 50 50
   :header-rows: 1
   
   * - 項目
     - 値
     
   * - Java
     - ver.1.8
     
   * - `Super CSV <http://super-csv.github.io/super-csv/index.html>`_
     - ver.2.4+

   * - `Joda-Time <http://www.joda.org/joda-time/>`_ (option)
     - ver.2.9+
     
   * - `Spring Framework <https://projects.spring.io/spring-framework/>`_ (option)
     - ver.3.0+

   * - | Bean Validation  (option)
       | ( `Hibernate Validator <http://hibernate.org/validator/>`_ )
     - | ver.1.0/1.1/2.0
       | (Hibernate Validator 4.x/5.x/6.x)



----------------------------------------
対応しているクラスタイプ
----------------------------------------

本ライブラリは、標準では以下のクラスタイプに対応しています。
独自のクラスタイプに対応することも可能で、その場合は、「 :doc:`format_custom` 」を参照してください。

.. list-table:: 対応しているクラスタイプ(Java標準)
   :widths: 33 33 33
   :header-rows: 1
   
   * - 基本型
     - 日時型
     - その他
     
   * - boolean/java.lang.Boolean
     - java.util.Date
     - java.math.BigDecimal
     
   * - byte/java.lang.Byte
     - java.util.Calendar
     - java.math.BigInteger
     
   * - short/java.lang.Short
     - java.sql.Date
     - 
     
   * - int/java.lang.Integer
     - java.sql.Time
     - 
     
   * - long/java.lang.Long
     - java.sql.Timestamp
     - 
     
   * - float/java.lang.Float
     - java.time.LocalDateTime
     - 
     
   * - double/java.lang.Double
     - java.time.LocalDate
     - 
     
   * - char/java.lang.Character
     - java.time.LocalTime
     - 
     
   * - String
     - java.time.ZonedDateTime
     -
     
   * - 列挙型
     - java.time.OffsetDateTime
     -
     
   * - 
     - java.time.OffsetTime
     -
   
   * - 
     - java.time.Year
     -
   
   * - 
     - java.time.YearMonth
     -
   * - 
     - java.time.MonthDay
     -

.. list-table:: 対応しているクラスタイプ(サードパーティ)
   :widths: 33 33 33
   :header-rows: 1
   
   * - Joda-Time
     - 
     - 
     
   * - org.joda.time.LocalDateTime
     - 
     - 
     
     
   * - org.joda.time.LocalDate
     - 
     -
     
   * - org.joda.time.LocalTime
     - 
     -
     
   * - org.joda.time.DateTime
     - 
     -
   * - org.joda.time.YearMonth
     - 
     -
   * - org.joda.time.MonthDay
     - 
     -
     

----------------------------------------
処理の流れ
----------------------------------------

本ライブラリは、Super CSVを拡張していますが、アノテーションを元に自動で組み立てた ``CellProcessor`` を使用しているため、CSVの処理部分は変わりません。

また、独自のアノテーションと対応するCellProcessorを呼び出すことが可能で、独自の拡張が容易にできます。

.. list-table:: 読み込み時の処理の流れ
   :widths: 10 20 70
   :header-rows: 1
   
   * - 順序
     - 処理
     - 説明
   
   * - 1
     - CellProcessorの組み立て
     - | Beanに定義したアノテーションを元に、CellProcessorを組み立てます。
       | ``BeanMappingFactory`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/builder/BeanMappingFactory.html>`_ ] で処理を行います。
       
   * - 2
     - CSVの読み込み
     - | Super CSVの本来の機能を利用しています。
       | `CsvPreference <http://super-csv.github.io/super-csv/preferences.html>`_ をカスタマイズすることで、
       | タブ区切りなどに対応できます。
   
   * - 3
     - 変換処理
     - | オブジェクトに変換前の文字列に対して、変換を行うCellProcessorを実行します。
       | トリミングなどの様々なアノテーションが準備されていますが、独自に追加もできます。
       | 詳細は、「 :doc:`conversion` 」を参照してください。

   * - 4
     - パース処理
     - | 文字列から各オブジェクトにパースを行うCellProcessorを実行します。
       | 日時、数値などの書式を指定可能なアノテーションが準備されていますが、独自に追加もできます。
       | 詳細は、「 :doc:`format` 」を参照してください。
       | ``PrintProcessor`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/cellprocessor/format/PrintProcessor.html>`_ ] で処理を行います。

   * - 5
     - 検証処理
     - | オブジェクトに変換した値に対して、値の検証を行うCellProcessorを実行します。
       | 様々なアノテーションが準備されていますが、独自に追加もできます。
       | また、外部ライブラリであるBean Validationも利用可能です。
       | 詳細は、「 :doc:`validation` 」を参照してください。


.. list-table:: 書き込み時の処理の流れ
   :widths: 10 20 70
   :header-rows: 1
   
   * - 順序
     - 処理
     - 説明
   
   * - 1
     - CellProcessorの組み立て
     - | Beanに定義したアノテーションを元に、CellProcessorを組み立てます。
       | ``BeanMappingFactory`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/builder/BeanMappingFactory.html>`_ ] で処理を行います。
       
   * - 2
     - 検証処理
     - | オブジェクトに変換した値に対して、値の検証を行うCellProcessorを実行します。
       | 様々なアノテーションが準備されていますが、独自に追加もできます。
       | また、外部ライブラリであるBean Validationも利用可能です。
       | 詳細は、「 :doc:`validation` 」を参照してください。
    
   * - 3
     - フォーマット処理
     - | オブジェクから文字列にフォーマットを行うCellProcessorを実行します。
       | 日時、数値などの書式を指定可能なアノテーションが準備されていますが、独自に追加もできます。
       | 詳細は、「 :doc:`format` 」を参照してください。
       | ``ParseProcessor`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/cellprocessor/format/ParseProcessor.html>`_ ] で処理を行います。
    
   * - 4
     - 変換処理
     - | フォーマット後の文字列に対して、変換を行うCellProcessorを実行します。
       | トリミングなどの様々なアノテーションが準備されていますが、独自に追加もできます。
       | 詳細は、「 :doc:`conversion` 」を参照してください。
    
   * - 5
     - CSVの書き込み
     - | Super CSVの本来の機能を利用しています。
       | `CsvPreference <http://super-csv.github.io/super-csv/preferences.html>`_ をカスタマイズすることで、
       | タブ区切りなどに対応できます。
   



