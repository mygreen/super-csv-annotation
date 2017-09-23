--------------------------------------------------------
変換処理用の既存のアノテーション
--------------------------------------------------------

既存のアノテーションとして、以下のものが用意されています。

.. list-table:: 変換規則を指定する既存のアノテーション
   :widths: 30 55 15
   :header-rows: 1
   
   * - アノテーション
     - 概要
     - 参照
     
   * - ``@CsvTrim``
     - 前後の空白をトリミングします。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvTrim.html>`_

   * - ``@CsvOneSideTrim`` *[v2.1+]*
     - 前後どちらか一方をトリミングします。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvOneSideTrim.html>`_

   * - ``@CsvDefaultValue``
     - 値がnullのときに他の値に変換します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvDefaultValue.html>`_

   * - ``@CsvNullConvert``
     - 指定した値と一致するときにnullに変換します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvNullConvert.html>`_
     
   * - ``@CsvLower``
     - 英字のアルファベットの大文字から小文字に変換します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvLower.html>`_

   * - ``@CsvUpper``
     - 英字のアルファベットの小文字を大文字に変換します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvUpper.html>`_

   * - ``@CsvRegexReplace``
     - 正規表現による置換を行います。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvRegexReplace.html>`_

   * - ``@CsvWordReplace``
     - 語彙による置換を行います。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvWordReplace.html>`_

   * - ``@CsvFullChar``
     - 半角文字を日本語の全角文字に変換します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvFullChar.html>`_

   * - ``@CsvHalfChar``
     - 日本語の全角文字を半角文字に変換します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvHalfChar.html>`_

   * - ``@CsvTruncate``
     - 一定の文字長を超える場合に切り出しを行います。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvTruncate.html>`_

   * - ``@CsvLeftPad``
     - 左側にパディングを行います。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvLeftPad.html>`_

   * - ``@CsvRightPad``
     - 右側にパディングを行います。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvRightPad.html>`_

   * - ``@CsvMultiPad`` *[v2.1+]*
     - 柔軟な設定でパディングを行います。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvMultiPad.html>`_

   * - ``@CsvFixedSize`` *[v2.1+]*
     - | 固定長のサイズに変換します。
       | 詳細は、「 :doc:`fixedsizecolumn` 」を参照してください。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/conversion/CsvFixedSize.html>`_


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
処理順序の指定
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

属性 ``order`` で処理順序を指定することができます。

* 値が大きいほど後から実行されます。
* 値が同じ場合は、アノテーションのFQCN（完全限定クラス名）の昇順で実行されます。

  * 属性orderを省略した場合は、デフォルト値 ``0`` が適用されます。

* 読み込み時、書き込み時とも同じ処理順序になります。
* 属性 ``order`` が付与されていないアノテーションは順番が付与されているものよりも後になります。

.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    
    import com.github.mygreen.supercsv.annotation.conversion.*;
    
    @CsvBean
    public class SampleCsv {
        
        // 空白の場合、トリミングして空文字となった場合に入力値なしと判断して、nullに変換します。
        @CsvColumn(number=1)
        @CsvTrim(order=1)
        @CsvNullConvert(value="", order=2)
        private String comment;
        
        // getter/setterは省略
    }


^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
処理ケースの指定
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

属性 ``cases`` で、アノテーションを適用するケースとして「読み込み時」「書き込み時」を限定することができます。

* 列挙型 ``BuildCase`` で指定します。

  * ``BuildCase.Read`` が読み込み時、 ``BuildCase.Write`` が書き込み時を表します。

* 属性の値が空（配列が空）の場合、または、属性 cases を指定しない場合は、全てのケースに該当します。
* 既存のアノテーションは、基本的に全て属性値が空が設定され、全てのケースに該当します。


.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.conversion.*;
    import com.github.mygreen.supercsv.builder.BuildCase;
    
    @CsvBean
    public class SampleCsv {
        
        // 空白の場合、トリミングして空文字となった場合に入力値なしと判断して、nullに変換します。
        @CsvColumn(number=1)
        @CsvTrim(order=1, cases={})  // 全てのケースに適用
        @CsvNullConvert(value="N/A", cases=BuildCase.Read)  // 読み込み時のみ適用
        @CsvDefault(value="N/A", cases=BuildCase.Write)     // 書き込み時のみ適用
        private String comment;
        
        // getter/setterは省略
    }



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
グループの指定
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

属性 ``groups`` で、グループ用クラスを指定することで、属性 *cases* より柔軟に適用するケースをを限定することができます。

* Bean Validation のgroupと同じような考え方ですが、適用される順序は関係ありません。

  * 本ライブラリでは、順序を指定したいときは、属性 *order* を指定します。
  
* 属性を指定しない（空の）場合は、デフォルトのグループ ``com.github.mygreen.supercsv.annotation.DefaultGroup`` が適用されたと同じ意味になります。
  
  * Bean Validationのデフォルトグループ ``javax.validation.groups.Default`` とは異なるため、特にBeanValidationのアノテーションと混在させる場合は注意してください。
  
* グループ用クラスは、実装が必要ないため、通常はインタフェースで作成します。

.. sourcecode:: java
    :linenos:
    
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.DefaultGroup;
    
    import com.github.mygreen.supercsv.annotation.conversion.*;
    
    @CsvBean
    public class SampleCsv {
        
        @CsvColumn(number=1)
        @CsvHalfChar(order=1)
        @DefaultValue(value="10", groups=AdminGroup.class, order=2)
        @DefaultValue(value="0", groups=NormalGroup.class, order=2)
        private Integer value;
        
        // getter/setterは省略
    }
    
    // グループ用クラスの作成
    public static interface AdminGroup {}
    public static interface NormalGroup {}
    


実行時は、``CsvAnnotationBeanReader/CsvAnnotationBeanWriter/BeanMappingFactory`` の引数で指定します。

.. sourcecode:: java
    :linenos:
    
    
    import com.github.mygreen.supercsv.builder.BeanMapping;
    import com.github.mygreen.supercsv.builder.BeanMappingFactory;
    import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;
    import com.github.mygreen.supercsv.io.CsvAnnotationBeanWriter;
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    import java.util.ArrayList;
    import java.util.List;
    
    import org.supercsv.prefs.CsvPreference;
    
    
    public class Sample {
        
        // 読み込み時のグループの指定
        public void sampleRead() {
            
            CsvAnnotationBeanReader<SampleCsv> csvReader = new CsvAnnotationBeanReader<>(
                    SampleCsv.class,
                    Files.newBufferedReader(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE,
                    DefaultGroup.class, AdminGroup.class); // デフォルトとAdminのグループクラスを指定する。
            
            //... 以下省略
        
        }
        
        // 書き込み時のグループの指定
        public void sampleWrite() {
            
            CsvAnnotationBeanWriter<SampleCsv> csvWriter = new CsvAnnotationBeanWriter<>(
                    SampleCsv.class,
                    Files.newBufferedWriter(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE,
                    DefaultGroup.class, NormalGroup.class); // デフォルトとNoraml用のグループクラスを指定する。
            
            //... 以下省略

        }
        
        // BeanMapping作成時の指定
        public void sampleBeanMapping() {
        
            // BeanMappingの作成
            BeanMappingFactory mappingFactory = new BeanMappingFactory();
            BeanMapping<SampleCsv> beanMapping = mappingFactory.create(SampleCsv.class,
                DefaultGroup.class, NormalGroup.class);  // デフォルトとNoraml用のグループクラスを指定する。
            
            CsvAnnotationBeanReader<SampleCsv> csvReader = new CsvAnnotationBeanReader<>(
                    beanMapping,
                    Files.newBufferedReader(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            //... 以下省略
        }
        
    }



