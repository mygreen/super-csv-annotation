======================================
固定長のカラムの読み書き
======================================

固定長のカラムは、書き込み時は任意の長さになるよう空白などを詰め、読み込み時は書き込み時に詰めた空白などの文字を除去するとで実現しています。*[v2.1+]*

固定長のカラムを定義したい場合は、フィールドにアノテーション ``@CsvFixedSize`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvFixedSize.html>`_ ] を付与します。

このアノテーションは、 以下の変換用のアノテーションを合成したアノテーションになります。

* 書き込み時は、パディングするアノテーション ``@CsvMultiPad`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvMultiPad.html>`_ ]。
* 読み込み時は、トリムするアノテーション ``@CsvOneSideTrim`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvOneSideTrim.html>`_ ]。


--------------------------------------------------------
基本的な設定
--------------------------------------------------------

``@CsvFixedSize`` の属性を指定することで、細やかな設定ができます。

* 属性 ``size`` にて、固定長のサイズを指定します。
* 属性 ``rightAlign`` で右寄せするかどうか指定します。

  * ``true`` のとき右寄せとなります。書き込み時に左側にパディング文字を詰め、読み見込み時に左側の文字をトリミングします。
  * ``false`` のとき左寄席で、デフォルト値となります。書き込み時に右側にパディング文字を詰め、読み見込み時に右側の文字をトリミングします。

* 属性 ``padChar`` で、パディングする文字を指定することができます。
  
  * デフォルトは半角空白です。
  * 値には、半角以外の全角文字の指定可能です。

* 属性 ``chopped`` で、書き込み時に指定したサイズを超えている場合、切り出すか指定します。

  * デフォルトは ``true`` で切り出しません。

.. sourcecode:: java
    :linenos:
    :caption: Beanの定義例
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.CsvFixedSize;
    
    
    @CsvBean
    public class SampleCsv {
    
        // 右詰めする
        @CsvColumn(number=1)
        @CsvFixedSize(size=10, rightAlign=true)
        private int id;
        
        // パディング文字を全角空白にする。
        // 全角を入力する前提としたカラムと想定し、さらに @CsvFullChar で半角を全角に変換します。
        @CsvColumn(number=2, label="氏名")
        @CsvFixedSize(size=20, padChar='　')
        @CsvFullChar
        private String name;
        
        // パディング文字をアンダースコア（_）にする。
        @CsvColumn(number=3, label="生年月日")
        @CsvFixedSize(length=10, padChar='_')
        @CsvDateTimeFormat(pattern="uuuu-MM-dd")
        private LocalDate birthday;
        
        // 文字サイズを超えている場合は、切り出す。
        @CsvColumn(number=4, label="備考")
        @CsvFixedSize(size=20, chopped=true)
        private String comment;
        
        // getter, setterは省略
    }


出力されるCSVは下記のようになります。

ヘッダー行が固定長になっていないことがわかります。

.. sourcecode:: none
    :linenos:
    :caption: 出力されるCSV(1)
    
    id,氏名,生年月日,備考
             1,山田　太郎　　　　　,1990-01-12,コメント            
             2,山田　花子　　　　　,__________,                    


--------------------------------------------------------
ヘッダー行の固定長の指定
--------------------------------------------------------

ヘッダー行もカラムの定義と同様に固定長にしたい場合は、``@CsvBean(headerMapper=<ヘッダーのマッピング処理クラス>)`` でヘッダーの処理方法を変更します。

``FixedSizeHeaderMapper`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/builder/FixedSizeHeaderMapper.html>`_ ] は、フィールドに付与されたアノテーション ``@CsvFixedSize`` を元に、見出しに対してパディングを行います。

.. sourcecode:: java
    :linenos:
    :caption: ヘッダーのマッピング定義を変更する
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.CsvFixedSize;
    import com.github.mygreen.supercsv.builder.FixedSizeHeaderMapper;
    
    @CsvBean(header=true, headerMapper=FixedSizeHeaderMapper.class)
    public class SampleCsv {
    
        // 右詰めする
        @CsvColumn(number=1)
        @CsvFixedSize(size=10, rightAlign=true)
        private int id;
        
        // 以下、省略
    }

.. sourcecode:: none
    :linenos:
    :caption: 出力されるCSV - ヘッダーも固定長にする
    
            id,氏名　　　　　　　　,生年月日__,備考                
             1,山田　太郎　　　　　,1990-01-12,コメント            
             2,山田　花子　　　　　,__________,                    


.. note::
    
    ヘッダーの見出しが全角、実際の値が半角で出力するような場合、意図した変換がされない場合があります。
    その際は、独自に ``com.github.mygreen.supercsv.builder.HeaderMapper`` を実装したクラスを指定することで対応できます。


--------------------------------------------------------
パディング処理方式の切り替え
--------------------------------------------------------

固定長としてパディングする場合、サイズカウント方法の考え方は、複数あります。
例えば、半角は１文字、全角は2文字ぶんとして換算する。
または、文字のバイト数で換算することもあります。

* パディング処理の実装を切り替えることができ、``@CsvFixedSize`` の属性 ``paddingProcessor`` でパディング処理の実装クラスを指定します。
* 本ライブラリでは、以下のパディング処理の実装が提供されています。

  * ``SimplePaddingProcessor`` - 文字の種別にかかわらず１文字としてカウントしてパディングします。
  * ``CharWidthPaddingProcessor`` - 文字の幅（半角は1文字、全角は2文字）によってカウントしてパディングします。デフォルトの実装です。
  * ``ByteSizePaddingProcessor`` - バイト数によってカウントしてパディングします。

    * バイト数で換算する場合、文字コードに依存するため、文字コードに対応したサブクラスを指定する必要があります。

* 独自のパディング処理を指定したい場合は、 ``com.github.mygreen.supercsv.cellprocessor.conversion.PaddingProcessor`` を実装したクラスを指定してください。


.. sourcecode:: java
    :linenos:
    :caption: ヘッダーのマッピング定義を変更する
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.CsvFixedSize;
    import com.github.mygreen.supercsv.builder.FixedSizeHeaderMapper;
    import com.github.mygreen.supercsv.cellprocessor.conversion.ByteSizePaddingProcessor;
    import com.github.mygreen.supercsv.cellprocessor.conversion.CharWidthPaddingProcessor;
    import com.github.mygreen.supercsv.cellprocessor.conversion.SimplePaddingProcessor;
    
    @CsvBean(header=true, headerMapper=FixedSizeHeaderMapper.class)
    public class SampleCsv {
    
        // 文字の種別にかかわらず１文字としてカウントしてパディングします。
        @CsvColumn(number=1)
        @CsvFixedSize(size=10, paddingProcessor=SimplePaddingProcessor.class)
        private int id;
        
        // 文字の幅（半角は1文字、全角は2文字）によってカウントしてパディングします。
        @CsvColumn(number=2)
        @CsvFixedSize(size=20, paddingProcessor=CharWidthPaddingProcessor.class)
        private String name;
        
        // バイト数によってカウントしてパディングします。
        @CsvColumn(number=3)
        @CsvFixedSize(size=20, paddingProcessor=ByteSizePaddingProcessor.Windows31j.class)
        private String comment;
        
        // 以下、省略
    }


