--------------------------------------------------------
ライフサイクル・コールバック用のアノテーション
--------------------------------------------------------

ライフサイクルイベントに対応するアノテーションをメソッドに付与することで、
そのメソッドをコールバック呼び出しすることができます。

.. list-table:: ライフサイクルイベント用のアノテーション
   :widths: 30 55 15
   :header-rows: 1
   
   * - アノテーション
     - 概要
     - 参照
     
   * - ``@CsvPreRead``
     - レコードの読み込み前に実行されます。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/CsvPreRead.html>`_

   * - ``@CsvPostRead``
     - レコードの読み込み後に実行されます。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/CsvPostRead.html>`_

   * - ``@CsvPreWrite``
     - レコードの書き込み前に実行されます。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/CsvPreWrite.html>`_

   * - ``@CsvPostWrite``
     - レコードの書き込み後に実行されます。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/CsvPostWrite.html>`_

.. note::
   
   不明な引数を定義している場合は、nullが渡されます。
   
   

メソッドには以下の引数を取ることができます。

* 引数は、とらなくても可能です。
* 引数の順番は任意で指定可能です。


.. list-table:: コールバック用のメソッドに指定可能な引数一覧
   :widths: 50 50
   :header-rows: 1
   
   * - 引数のタイプ
     - 説明
   
   * - ``org.supercsv.util.CsvContext`` [ `JavaDoc <http://super-csv.github.io/super-csv/apidocs/org/supercsv/util/CsvContext.html>`_ ]
     - CSVの1レコード分の情報。
   
   * - ``com.github.mygreen.supercsv.validation.CsvBindingErrors`` [`JavaDoc <../apidocs/com/github/mygreen/supercsv/validation/CsvBindingErrors.html>`_ ]
     - CSVの1レコード分のエラー情報。
   
   * - ``com.github.mygreen.supercsv.validation.ValidationContext`` [`JavaDoc <../apidocs/com/github/mygreen/supercsv/validation/ValidationContext.html>`_ ]
     - 値の検証用の情報。
   
   * - ``Class[]``
     - グループのクラス情報。
   
   * - *処理対象のBeanクラス*
     - 処理対象のBeanオブジェクト。


以下のような時にライフサイクル・コールバック関数を利用します。

* 書き込み前のフィールドの初期化。
* 読み込み後の入力値の検証。

.. sourcecode:: java
    :linenos:
    
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.CsvPostRead;
    import com.github.mygreen.supercsv.annotation.CsvPostWrite;
    import com.github.mygreen.supercsv.annotation.CsvPreRead;
    import com.github.mygreen.supercsv.annotation.CsvPreWrite;
    
    @CsvBean(header=true)
    public class SampleCsv {
        
        @CsvColumn(number=1, label="ID")
        private Integer id;
        
        @CsvColumn(number=2, label="値")
        private String value;
        
        @CsvPreRead
        public void handlePreRead() {
            
            // レコードの読み込み前に呼び出されます。
        }
        
        @CsvPostRead
        public void handlePostRead(final CsvBindingErrors bindingErrors, final Class<?>[] groups) {
            
            // レコードの読み込み後に呼び出されます。
            
        }
        
        @CsvPreWrite
        public void handlePreWrite(final CsvBindingErrors bindingErrors, final Class<?>[] groups) {
            
            // レコードの書き込み前に呼び出されます。
            
        }
        
        @CsvPostWrite
        public void handlePostWrite(final CsvBindingErrors bindingErrors, final Class<?>[] groups) {
            
            // レコードの書き込み後に呼び出されます。
            
        }
        
        // setter/getterは省略
    }



