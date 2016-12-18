^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
@CsvEnumFormat
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

列挙型の変換規則の設定を行うためのアノテーションです。

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
基本的な使い方
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

列挙型の場合、アノテーション ``@CsvEnumFormat`` を付与しなくてもマッピングできます。
その際は、カラムの値と列挙型の要素の値をマッピングさせます。
要素の値とは、 ``Enum#name()`` で取得できる値です。

* 属性 ``ignoreCase`` の値をtrueにすると、読み込み時に大文字/小文字の区別なく変換します。

.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.format.CsvEnumFormat;
    
    @CsvBean
    public class SampleCsv {
        
        @CsvColumn(number=1, label="権限")
        private RoleType role;
        
        // 読み込み時に大文字・小文字の区別を行いません。
        @CsvColumn(number=2, label="権限2")
        @CsvEnumFormat(ignoreCase=true)
        private RoleType role2;
        
        // setter/getterは省略
    }
    
    // 列挙型の定義
    public enum RoleType {
        Normal, Admin;
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
別名でマッピングする場合
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

別名でマッピングする場合、属性 ``selector`` で列挙型の要素の別名を取得するメソッド名を指定します。

次の例では、読み込み時に入力値が *一般権限* の場合、 *RoleType.Normal* にマッピングされます。

.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.format.CsvEnumFormat;
    
    @CsvBean
    public class SampleCsv {
        
        @CsvColumn(number=1, label="権限")
        @CsvEnumFormat(selector="localeName")
        private RoleType role;
        
        // setter/getterは省略
    }
    
    // 列挙型の定義
    public enum RoleType {
        Normal("一般権限"), Admin("管理者権限");
        
        // 別名の設定
        private String localeName;
        
        private RoleType(String localeName) {
            this.localeName = localeName;
        }
      
        // 別名の取得用メソッド
        public String localeName() {
            return this.localeName;
        }
        
    }


~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
読み込み時の値のパースに失敗した際のメッセージ
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

読み込み時に値のパースに失敗した時に独自のメッセージとして、属性 ``message`` で指定することができます。

メッセージ中は、`JEXL <http://commons.apache.org/proper/commons-jexl/>`_ による式言語を使うことができ、
予め登録されている変数を用いて、メッセージ内容を独自にカスタマイズすることができます。
詳細は、:doc:`値の検証時のメッセージ <validation_message>` を参照してください。

メッセージ中で利用可能な変数は、`JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/format/CsvEnumFormat.html>`_ を参照してください。


.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.format.CsvEnumFormat;
    
    @CsvBean
    public class SampleCsv {
        
        @CsvColumn(number=1, label="ステータス")
        @CsvEnumFormat(message="[{rowNumber}行, {columnNumber}列] : 項目「{label}」の値（{validatedValue}）は、何れかの値「${f:join(enums, ', ')}」である必要があります。"
        private RoleType role;
        
        // setter/getterは省略
    }
    
    // 列挙型の定義
    public enum RoleType {
        Normal, Admin;
    }




