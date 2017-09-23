======================================
リリースノート
======================================

--------------------------------------------------------
ver.2.1 - 2017-09-23
--------------------------------------------------------

* 修正内容
  
  * `#27 <https://github.com/mygreen/super-csv-annotation/pull/27>`_ - ``MessageInterpolatorAdapter.java`` 内の変数 ``defautlMessage`` のタイプミスとして、 ``defaultMessage`` に修正。
  * `#28 <https://github.com/mygreen/super-csv-annotation/pull/28>`_ - メッセージコードを生成するメソッド ``MessageCodeGenerator#addCode(...)`` の不良の修正。

* 変更内容。
  
  * `#26 <https://github.com/mygreen/super-csv-annotation/pull/26>`_ - 固定長のサイズのカラムに対応し、アノテーション ``@CsvFixedSize`` を追加しました。
  
    * 詳細は、「 :doc:`fixedsizecolumn` 」を参照してください。

  * `#29 <https://github.com/mygreen/super-csv-annotation/issues/29>`_ / `#31 <https://github.com/mygreen/super-csv-annotation/pull/31>`_ - BeanValdiation2.0に対応しました。さらに、以下の日時型に対応しました。
  
    * JSR-310(Date and Time)のクラスタイプとして、``OffsetDateTime/OffsetTime/Year/YearMonth/MonthDay`` に対応しました。
    * Joda-Timeのクラスタイプとして、``YearMonth/MonthDay`` に対応しました。
  
  * `#32 <https://github.com/mygreen/super-csv-annotation/pull/32>`_ - カラム番号を指定しないで、ラベルによるマッピングする機能を追加しました。
  
    * 詳細は、「 :doc:`labelledcolumn` 」 を参照してください。
  
  * メソッド ``CsvAnnotationBeanWriter#writeAll()``  呼び出しの最後に、 ``flush()`` メソッドを呼ぶよう修正。


--------------------------------------------------------
ver.2.0.3 - 2017-02-20
--------------------------------------------------------

* 修正内容。
  
  * `#24 <https://github.com/mygreen/super-csv-annotation/issues/24>`_ - 値の変換用のアノテーション ``@CsvFullChar/@CsvHalfChar`` を設定しても値が変換されない事象を修正。
  * `#25 <https://github.com/mygreen/super-csv-annotation/issues/25>`_ - 書き込み前の処理をコールバックするアノテーション ``@CsvPreWrite`` を利用し、フィールドの値を書き換えても、出力されるファイルに結果が反映されない事象を修正。
  

--------------------------------------------------------
ver.2.0.2 - 2017-01-30
--------------------------------------------------------

* 修正内容。
  
  * `#22 <https://github.com/mygreen/super-csv-annotation/issues/22>`_ - メソッド ``CsvAnnotationWriter#writeAll(...)`` を呼ぶと、StackOverflowError が発生する事象を修正。
  


--------------------------------------------------------
ver.2.0.1 - 2016-12-18
--------------------------------------------------------

* 修正内容。
  
  * ドキュメントの誤字の見直し。
  * `#20 <https://github.com/mygreen/super-csv-annotation/issues/20>`_ - 合成したアノテーションのとき、共通の属性casesが上書きされない事象を修正。

* 変更内容
  
  * `#21 <https://github.com/mygreen/super-csv-annotation/issues/21>`_ - 置換語彙を取得するプロバイダのメソッド ``RepalcedWordProvider#getReplacedWords(...)`` の戻り値を変更。語彙を格納するクラスを内部クラスに変更。
  


--------------------------------------------------------
ver.2.0 - 2016-12-11
--------------------------------------------------------

* 根本から作り直し、大幅に変更。
  
  * ver.1.x系とは互換性はありません。
  * 書式用/検証用/変換用の各種アノテーションを用意し、それぞれの処理で容易に拡張できるようにしています。
  

--------------------------------------------------------
ver.1.2 - 2016-09-03
--------------------------------------------------------

* 機能追加.
   
  * `#14 <https://github.com/mygreen/super-csv-annotation/issues/14>`_ - Java8のDate and Time APIに対応。Joda-Timeに対応。
    
    * 前提環境を、Java8に変更。
    
  * `#15 <https://github.com/mygreen/super-csv-annotation/issues/15>`_ - Super CSV 2.4.0に対応。
  * `#17 <https://github.com/mygreen/super-csv-annotation/issues/17>`_ - 非推奨のアノテーションの属性の削除。
    
    * アノテーション ``@CsvDateConverter`` の属性 language, countryの削除。
    * アノテーション ``@CsvNumberConverter`` の属性 language, countryの削除。
     
  * `#18 <https://github.com/mygreen/super-csv-annotation/issues/18>`_ - アノテーションの属性名の変更。
    
    * アノテーション ``@CsvBooleanConverter`` の属性 lenientをignoreCaseに変更。
    * アノテーション ``@CsvEnumConverter`` の属性 lenientをignoreCaseに変更。
   
   * `#19 <https://github.com/mygreen/super-csv-annotation/issues/19>`_ - テスタの作成。
   
* 不良修正
  
  * ありません。


--------------------------------------------------------
ver.1.1 - 2014-11-30
--------------------------------------------------------
 
* 機能追加.
   
  * `#9 <https://github.com/mygreen/super-csv-annotation/issues/9>`_- CellProcessorBuilderのインスタンス生成をカスタマイズ可能にする。
  * `#10 <https://github.com/mygreen/super-csv-annotation/issues/10>`_ - クラス名の変更 : MessageConverter -> CsvMessageConverter。
  * `#11 <https://github.com/mygreen/super-csv-annotation/issues/11>`_ - ResourceBundleMessageResolverのローカルのプロパティファイルの読み込み対応。
  * `#12 <https://github.com/mygreen/super-csv-annotation/issues/12>`_ - @CsvDateConverter/@CsvNumberConverterの属性localeの追加。
  * `#13 <https://github.com/mygreen/super-csv-annotation/issues/13>`_ - メソッドの名の変更 : *CellProcessorBuilderContainer#registBuilder* -> *CellProcessorBuilderContainer#registerBuilder* 。
  * `#3 <https://github.com/mygreen/super-csv-annotation/issues/3>`_ - Mavenセントラルリポジトリへの対応。
  
* 不良修正
  
  * ありません。


