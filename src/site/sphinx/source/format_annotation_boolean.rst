^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
@CsvBooleanFormat
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

クラスタイプが「boolean/Boolean」のマッピング規則を定義する際に使用します。

通常の「true、false」以外に、「○、×」などでのマッピングが可能となります。

.. note::
   
   プリミティブ型に対して読み込む際に、CSVのカラムの値が空の場合、 ``false`` が設定されます。
   初期値を変更したい場合は、アノテーション ``@CsvDefaultValue`` [`JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvDefaultValue.html>`_]を使用してください。


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
読み込み時の値の指定
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

属性 ``readForTrue`` 、 ``readForFalse`` で、読み込み時のtrueまたはfalseと判定する候補の値を指定します。
   
* 属性readForTrueとreadForFalseの値に重複がある場合、readForTrueの定義が優先されまます。
  
* 属性readForTrueを指定しない場合、デフォルトで「"true", "1", "yes", "on", "y", "t"」が設定されます。
  
* 属性readForFalseを指定しない場合、デフォルトで「"false", "0", "no", "off", "f", "n"」が設定されます。
    
* 属性 ``ignoreCase`` の値をtrueにすると、読み込み時に大文字・小文字の区別なく候補の値と比較します。


.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.format.CsvBooleanFormat;
    
    @CsvBean
    public class SampleCsv {
        
        // boolean型の読み込み時のtrueとfalseの値の変換規則を指定します。
        @CsvColumn(number=1, label="ステータス")
        @CsvBooleanFormat(
                readForTrue={"○", "有効", "レ"},
                readForFalse={"×", "無効", "-", ""})
        private boolean availaled;
        
        // 読み込み時の大文字・小文字の区別を行わない
        @CsvColumn(number=2, label="チェック")
        @CsvBooleanFormat(
              readForTrue={"OK"},
              readForFalse={"NO"},
              ignoreCase=true)
        private Boolean checked;
        
        // getter/setterは省略
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
書き込み時の値の指定
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

属性 ``writeAsTrue`` と ``writeAsFalse`` で、書き込み時のtrueまたはfalse値に該当する文字を指定します。
    
* 属性writeAsTrueを指定しない場合は、デフォルトで"true"が設定され、セルのタイプもブール型になります。
  
* 属性writeAsFalseを指定しない場合は、デフォルトで"false"が設定され、セルのタイプもブール型になります。
    
* 読み込みと書き込みの両方を行う場合、属性readForTrueとreadForFalseの値に属性writeAsTrueとwriteAsFalseの値を含める必要があります。
    

.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.format.CsvBooleanFormat;
    
    @CsvBean
    public class SampleCsv {
        
        // boolean型の書き込み時のtrueとfalseの値の変換規則を指定します。
        @CsvColumn(number=1, label="ステータス")
        @CsvBooleanFormat(
                readForTrue={"○", "有効", "レ"}, // 読み書きの両方を行う場合、書き込む値を含める必要がある。
                readForFalse={"×", "無効", "-", ""},
                writeAsTrue="○",
                writeAsFalse="×")
        )
        private boolean availaled;
        
        // getter/setterは省略
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
読み込み時の値のパースに失敗した際の処理
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 
読み込み時にtrueまたはfalseに変換できない場合、例外 ``SuperCsvValidationException`` が発生します。

* 属性 ``failToFalse`` をtrueに設定することで、変換できない場合に強制的に値をfalseとして読み込み、例外を発生しなくできます。

.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    
    import com.github.mygreen.supercsv.annotation.format.CsvBooleanFormat;
    
    public class SampleCsv {
        
        // 読み込み時のtrue,falseに定義していない値を読み込んだときにfalseとして読み込む。
        @CsvColumn(number=1, label="ステータス")
        @CsvBooleanFormat(
                readForTrue={"○", "有効", "レ"},
                readForFalse={"×", "無効", "-", ""},
                failToFalse=true)
        private boolean availaled;
        
        // setter/getterは省略
    }



~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
読み込み時の値のパースに失敗した際のメッセージ
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

読み込み時に値のパースに失敗した時に独自のメッセージとして、属性 ``message`` で指定することができます。

メッセージ中は、`JEXL <http://commons.apache.org/proper/commons-jexl/>`_ による式言語を使うことができ、
予め登録されている変数を用いて、メッセージ内容を独自にカスタマイズすることができます。
詳細は、:doc:`値の検証時のメッセージ <validation_message>` を参照してください。

メッセージ中で利用可能な変数は、`JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/format/CsvBooleanFormat.html>`_ を参照してください。


.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.format.CsvBooleanFormat;
    
    @CsvBean
    public class SampleCsv {
        
        @CsvColumn(number=1, label="ステータス")
        @CsvBooleanFormat(message="[{rowNumber}行, {columnNumber}列] : 項目「{label}」の値（{validatedValue}）は、trueの値「${f:join(trueValues, ', ')}」、またはfalseの値「${f:join(falseValues, ', ')}」の何れかの値で設定してください。"
        private boolean availaled;
        
        // setter/getterは省略
        
    }





