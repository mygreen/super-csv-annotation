--------------------------------------------------------
値の検証用の既存のアノテーション
--------------------------------------------------------

既存のアノテーションとして、以下のものが用意されています。

.. list-table:: 値の検証方法を指定する既存のアノテーション（全てのクラスタイプ）
   :widths: 30 55 15
   :header-rows: 1
   
   * - アノテーション
     - 概要
     - 参照
     
   * - ``@CsvRequire``
     - 必須チェックを行います。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvRequire.html>`_

   * - ``@CsvEquals``
     - 指定した値と等しいか検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvEquals.html>`_

   * - ``@CsvUnique``
     - 他のレコードの値と異なるか検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvUnique.html>`_

   * - ``@CsvUniqueHashCode``
     - 他のレコードの値と異なるかハッシュコードにより検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvUniqueHashCode.html>`_


.. list-table:: 値の検証方法を指定する既存のアノテーション（String型）
   :widths: 30 55 15
   :header-rows: 1
   
   * - アノテーション
     - 概要
     - 参照
     
   * - ``@CsvPattern``
     - 正規表現と一致するか検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvPattern.html>`_

   * - ``@CsvLengthMin``
     - 指定した文字長以上か検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvLengthMin.html>`_

   * - ``@CsvLengthMax``
     - 指定した文字長以内か検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvLengthMax.html>`_

   * - ``@CsvLengthBetween``
     - 指定した文字長の範囲内か検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvLengthBetween.html>`_

   * - ``@CsvLengthExact``
     - 指定した文字長か検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvLengthExact.html>`_

   * - ``@CsvLengthExact``
     - 指定した文字長か検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvLengthExact.html>`_

   * - ``@CsvWordForbid``
     - 指定した語彙を含んでいないか検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvWordForbid.html>`_

   * - ``@CsvWordRequire``
     - 指定した語彙を含んでいるか検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvWordRequire.html>`_


.. list-table:: 値の検証方法を指定する既存のアノテーション（数値型）
   :widths: 30 55 15
   :header-rows: 1
   
   * - アノテーション
     - 概要
     - 参照
     
   * - ``@CsvNumberMin``
     - 指定した下限値以上か検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvNumberMin.html>`_

   * - ``@CsvNumberMax``
     - 指定した上限値以下か検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvNumberMax.html>`_

   * - ``@CsvNumberRange``
     - 指定した値の範囲内か検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvNumberRange.html>`_

.. list-table:: 値の検証方法を指定する既存のアノテーション（日時型）
   :widths: 30 55 15
   :header-rows: 1
   
   * - アノテーション
     - 概要
     - 参照
     
   * - ``@CsvDateTimeMin``
     - 指定した値以降（下限値以上）か検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvDateTimeMin.html>`_

   * - ``@CsvDateTimeMax``
     - 指定した値以前（上限値以下）か検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvDateTimeMax.html>`_

   * - ``@CsvDateTimeRange``
     - 指定した値の期間内か検証します。
     - `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/constraint/CsvDateTimeRange.html>`_



^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
処理順序の指定
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

属性 ``order`` で処理順序を指定することができます。

* 値が大きいほど後から実行されます。
* 値が同じ場合は、アノテーションのFQCN（完全限定クラス名）の昇順で実行されます。

  * 属性orderを省略した場合は、デフォルト値 ``0`` が適用されます。
  * ただし、必須チェック用の ``@CsvRequire`` は、初めに実行されるよう、属性orderのデフォルト値にはIntegerの最小値（-2147483648）が設定されています。

* 読み込み時、書き込み時とも同じ処理順序になります。
* 属性 ``order`` が付与されていないアノテーションは順番が付与されているものよりも後になります。


.. sourcecode:: java
    :linenos:
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    
    import com.github.mygreen.supercsv.annotation.constraint.*;
    
    @CsvBean
    public class SampleCsv {
        
        @CsvColumn(number=1)
        @CsvRequire
        @CsvUnique(order=2)
        @CsvNumberMin(value="0", order=3)
        private Integer value;
        
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
    import com.github.mygreen.supercsv.annotation.constraint.*;
    import com.github.mygreen.supercsv.builder.BuildCase;
    
    @CsvBean
    public class SampleCsv {
        
        // 空白の場合、トリミングして空文字となった場合に入力値なしと判断して、nullに変換します。
        @CsvColumn(number=1)
        @CsvLengthMax(value=10, cases={})             // 全てのケースに適用
        @CsvLengthMin(value=0, cases=BuildCase.Read)  // 読み込み時のみ適用
        @CsvUnique(cases=BuildCase.Write)             // 書き込み時のみ適用
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
    import com.github.mygreen.supercsv.annotation.constraint.*;
    
    @CsvBean
    public class SampleCsv {
        
        @CsvColumn(number=1)
        @CsvRequire
        @CsvNumberMin(value="0", groups=AdminGroup.class, order=2)
        @CsvNumberMax(value="100", groups=NormalGroup.class, order=2)
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
                    DefaultGroup.class, AdminGroup.class); // デフォルトとAdmin用のグループクラスを指定する。
            
            //... 以下省略
        
        }
        
        // 書き込み時のグループの指定
        public void sampleWrite() {
            
            CsvAnnotationBeanWriter<SampleCsv> csvWriter = new CsvAnnotationBeanWriter<>(
                    SampleCsv.class,
                    Files.newBufferedWriter(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE,
                    DefaultGroup.class, NoramlGroup.class); // デフォルトとNoraml用のグループクラスを指定する。
            
            //... 以下省略

        }
        
        // BeanMapping作成時の指定
        public void sampleBeanMapping() {
        
            // BeanMappingの作成
            BeanMappingFactory mappingFactory = new BeanMappingFactory();
            BeanMapping<SampleCsv> beanMapping = mappingFactory.create(SampleCsv.class,
                DefaultGroup.class, NoramlGroup.class);  // デフォルトとNormal用のグループクラスを指定する。
            
            CsvAnnotationBeanReader<SampleCsv> csvReader = new CsvAnnotationBeanReader<>(
                    beanMapping,
                    Files.newBufferedReader(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            //... 以下省略
        }
        
    }


