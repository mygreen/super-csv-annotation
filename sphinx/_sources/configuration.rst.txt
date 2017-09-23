======================================
システム設定
======================================

--------------------------------------------------------
システム設定の変更方法
--------------------------------------------------------

CellProcessorを組み立てる際の動作を ``Configuration`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/builder/Configuration.html>`_ ]クラスでカスタマイズすることができます。

* Configuration は、``BeanMappingFactory`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/builder/BeanMappingFactory.html>`_ ]から設定／取得できます。

 * :doc:`Spring Frameworkとの連携 <spring_beanfactory>` を行う場合は、BeanMappingFactoryの変更を行います。
 
* BeanMappingFactoryの設定を変更した場合は、BeanMappingを ``CsvAnnotationBeanReader`` や ``CsvAnnoationBeanWriter`` に直接渡します。

.. sourcecode:: java
    :linenos:
    
    
    import com.github.mygreen.supercsv.builder.BeanMapping;
    import com.github.mygreen.supercsv.builder.BeanMappingFactory;
    import com.github.mygreen.supercsv.builder.Configuration;
    import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    
    import org.supercsv.prefs.CsvPreference;
    
    public class Sample {
        
        // 読み込み時の指定
        public void sampleConfiguration() {
        
            // BeanMappingFactoryから、Configurationを取得する
            BeanMappingFactory mappingFactory = new BeanMappingFactory();
            Configuration config = mappingFactory.getConfiguration();
            
            // 設定の変更
            config.setSkipValidationOnWrite(true);
            
            // BeanMappingオブジェクトの作成
            BeanMapping<SampleCsv> beanMapping = mappingFactory.create(SampleCsv.class,
                DefaultGroup.class, WriteGroup.class);  // デフォルトと書き込み用のグループクラスを指定する。
            
            CsvAnnotationBeanReader<SampleCsv> csvReader = new CsvAnnotationBeanReader<>(
                    beanMapping, // BeanMappingのオブジェクトを直接渡します
                    Files.newBufferedReader(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            //... 以下省略
        }
        
    }



--------------------------------------------------------
システム設定の項目一覧
--------------------------------------------------------

``Configuration`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/builder/Configuration.html>`_ ]で設定可能な項目一覧を下記に示します。


.. list-table:: システム設定の項目一覧
   :widths: 30 70
   :header-rows: 1
   
   * - プロパティ名
     - 説明
     
   * - *beanFactory*
     - | 独自のTextFormatter/ConstraintProcessorFactory/ConversionProcessorFactoryなどの
       | インスタンスを作成するためのクラスです。
       | ``SpringBeanFactory`` に切り替えることで、Spring FrameworkのDI機能を使用することができます。
       
   * - *annoationComparator*
     - | アノテーションを取得する際に、順番を一定に保つために並び変えるためのクラスです。
       | 標準のクラス ``AnnotationComparator`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/builder/AnnotationComparator.html>`_ ]は、アノテーションの属性「order」を元に並び変えます。
   
   * - *builderResolver*
     - | 各タイプの ``ProcessorBuilder`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/builder/ProcessorBuilder.html>`_ ]のインスタンスを管理するためのクラスです。
       | 基本的に、各種アノテーションで独自のクラスタイプに対応・カスタマイズすることができますが、
       | 既存の実装を切り替えることもできます。
     
   * - *skipValidationOnWrite*
     - | 書き込み時の値の検証を一律でスキップするかどうか設定することができます。
       | デフォルトは *false* で値の検証を行います。



