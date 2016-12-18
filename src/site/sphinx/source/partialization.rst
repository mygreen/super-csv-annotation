======================================
部分的なカラムの読み書き
======================================

Beanに定義しているカラムのみ処理する方法を説明します。

--------------------------------------------------------
部分的なカラムの読み込み
--------------------------------------------------------

CSVファイルで一部のカラムを読み込みたい場合は、フィールドの定義を単純に行わなければマッピングできます。

* 最後のカラム番号が実際のCSVファイルのカラム番号よりも小さい場合は、``@CsvPartial(columnSize=<カラムサイズ>)`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/CsvPartial.html>`_ ] で、実際のカラムサイズを指定します。
* ``@CsvBean(validateHeader=false)`` に設定し、ヘッダー行の検証をスキップするようにしてください。
  
  * 値を *true* にして、ヘッダー行の検証を行うと、定義していない見出しがあり、値が一致しないためエラーとなります。

.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.CsvPartial;
    
    
    @CsvBean(header=true, validateHeader=false) // ヘッダー行がある場合は、検証をスキップします。
    @CsvPartial(columnSize=5)   // 実際のCSVファイルのカラムサイズを指定します。
    public class SampleCsv {
    
        @CsvColumn(number=1)
        private int id;
        
        @CsvColumn(number=2, label="氏名")
        private String name;
        
        // カラム番号3は読み込まない場合は、定義を行いません。
        
        @CsvColumn(number=4, label="メールアドレス")
        private String email;
        
        // カラム番号5を読み込まない場合は、定義を行いません。
    }


``@CsvBean(validateHeader=true)`` にして、ヘッダー行の検証を行いたい場合は、定義していないカラムの見出しの定義が必要となるため、
``@CsvBean(headers={})`` でカラムの見出しの定義を行います。

.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.CsvPartial;
    
    
    @CsvBean(header=true, validateHeader=true) // ヘッダー行の検証を行う
    @CsvPartial(columnSize=5, headers={          // 定義されていないカラムの見出しを定義します。
        @CsvPartial.Header(number=3, label="電話番号"),
        @CsvPartial.Header(number=5, label="生年月日")
    })
    public class SampleCsv {
    
        @CsvColumn(number=1)
        private int id;
        
        @CsvColumn(number=2, label="氏名")
        private String name;
        
        // カラム番号3を読み込まない場合は、定義を行いません。
        
        @CsvColumn(number=4, label="メールアドレス")
        private String email;
        
        // カラム番号5を読み込まない場合は、定義を行いません。
    
    }


--------------------------------------------------------
部分的なカラムの書き込み
--------------------------------------------------------

部分的なカラムの書き込みも、読み込み時と同様に行います。
定義していないカラムの値は、空として出力されます。

* 最後のカラム番号が実際のCSVファイルのカラム番号よりも小さい場合は、``@CsvPartial(columnSize=<カラムサイズ>)`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/CsvPartial.html>`_ ]で、実際のカラムサイズを指定します。
* ヘッダー行を書き込む場合、定義されていないカラムの見出しは、 ``column<カラム番号>`` の形式となります。

  * 定義していないカラムの見出しの値を指定したい場合は、``@CsvBean(headers={})`` で設定します。


.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.CsvPartial;
    
    
    @CsvBean(header=true)
    @CsvPartial(columnSize=5, headers={   // 定義されていないカラムの見出しを定義します。
        @CsvPartial.Header(number=3, label="電話番号"),   
        @CsvPartial.Header(number=5, label="生年月日")
    })
    public class SampleCsv {
        
        @CsvColumn(number=1)
        private int id;
        
        @CsvColumn(number=2, label="氏名")
        private String name;
        
        // カラム番号3を書き込みまない場合は、定義を行いません。
        
        @CsvColumn(number=4, label="メールアドレス")
        private String email;
        
        // カラム番号5を書き込みまない場合は、定義を行いません。
    
    }



