--------------------------------------------------------
リスナークラスによるライフサイクルの管理
--------------------------------------------------------

リスナークラスはPOJOで作成し、ライスサイクル用のアノテーションでメソッドを実装します。

処理対象のレコードオブジェクトが必要なため、基本的に引数にはレコードクラスを指定しておきます。

.. sourcecode:: java
    :linenos:
    
    
    import com.github.mygreen.supercsv.annotation.CsvPostRead;
    import com.github.mygreen.supercsv.annotation.CsvPostWrite;
    import com.github.mygreen.supercsv.annotation.CsvPreRead;
    import com.github.mygreen.supercsv.annotation.CsvPreWrite;
    
    // リスナークラスの定義
    public class SampleListener {
        
        @CsvPreRead
        public void handlePreRead(final SampleCsv record) {
            
            // レコードの読み込み前に呼び出されます。
        }
        
        @CsvPostRead
        public void handlePostRead(final SampleCsv record, final CsvBindingErrors bindingErrors, final Class<?>[] groups) {
            
            // レコードの読み込み後に呼び出されます。
            
        }
        
        @CsvPreWrite
        public void handlePreWrite(final SampleCsv record, final CsvBindingErrors bindingErrors, final Class<?>[] groups) {
            
            // レコードの書き込み前に呼び出されます。
            
        }
        
        @CsvPostWrite
        public void handlePostWrite(final SampleCsv record, final CsvBindingErrors bindingErrors, final Class<?>[] groups) {
            
            // レコードの書き込み後に呼び出されます。
            
        }
        
    }


使用する際には、``@CsvBean(listeners=<リスナークラス>)`` にクラスを指定します。


.. sourcecode:: java
    :linenos:
    
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    
    // リスナクラスを指定します
    @CsvBean(header=true, listeners=SampleListener.class)
    public class SampleCsv {
        
        @CsvColumn(number=1, label="ID")
        private Integer id;
        
        @CsvColumn(number=2, label="値")
        private String value;
        
    }
