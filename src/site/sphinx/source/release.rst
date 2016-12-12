======================================
リリースノート
======================================

--------------------------------------------------------
ver.2.0 - 2016-12-11
--------------------------------------------------------

* 根本から作り直し、大幅に変更。
  
  * ver.1.x系とは互換性はありません。
  * 書式用/検証用/変換用の各種アノテーションを用意し、それぞれの処理で容易に拡張できるようにしています。
  

--------------------------------------------------------
ver.1.5.1 - 2016-10-30
--------------------------------------------------------

* `#85 <https://github.com/mygreen/xlsmapper/issues/85>`_ ``PatternValidator`` のエラーメッセージ中に使用する変数 `patternName` としてパターン名を指定できるように引数を追加しました。
  
* `#86 <https://github.com/mygreen/xlsmapper/issues/86>`_ 入力値検証時のエラーメッセージのキーのパターンを追加しました。

  * `\<エラーコード\>.\<オブジェクト名\>.\<パス\>.\<フィールド名\>` の形式を追加しました。


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


