======================================
基本的な使い方
======================================

----------------------------
ダウンロード
----------------------------

Mavenを使用する場合は *pom.xml* に以下の記述を追加してください。

.. sourcecode:: xml
    :linenos:
    :caption: pom.xmlの依存関係
    
    <dependency>
        <groupId>com.github.mygreen</groupId>
        <artifactId>super-csv-annotation</artifactId>
        <version>2.1</version>
    </dependency>


本ライブラリは、ロギングライブラリ `SLF4j <https://www.slf4j.org/>`_ を使用しているため、好きな実装を追加してください。

.. sourcecode:: xml
    :linenos:
    :caption: ロギングライブラリの実装の追加（Log4jの場合）
    
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-log4j12</artifactId>
        <version>1.7.1</version>
    </dependency>
    <dependency>
        <groupId>log4j</groupId>
        <artifactId>log4j</artifactId>
        <version>1.2.14</version>
    </dependency>


--------------------------------------
CSV用のBeanクラスの定義
--------------------------------------

CSVの1レコード分をマッピングするためのPOJOクラスを作成します。

* CSV用のクラスは、 ``public`` である必要があります。

  * 引数なしの ``public`` なコンストラクタが必要です。
  * コンストラクタを定義しない場合は、デフォルトコンストラクタでもかまいません。
  
* CSV用のクラスであることを示すために、アノテーション ``@CsvBean`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/CsvBean.html>`_ ] をクラスに付与します。

  * 属性 ``header`` をtrueとすると、 *CsvAnnotationBeanReader#readAll(...)* と *CsvAnnotationBeanWriter#writeAll(...)* メソッドの呼び出し時に、ヘッダー行がある前提として処理します。

* カラムをフィールドにマッピングするために、アノテーション ``@CsvColumn`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/CsvColumn.html>`_ ]をフィールドに付与します。

  * 属性 ``number`` で、マッピングするカラムの番号を指定します。カラムの番号は1から始まります。
  * 属性 ``label`` で、ヘッダー行のラベル名を指定することができます。省略した場合はフィールド名が適用されます。

* フィールドに対応するgetter/setterのアクセッサメソッドを作成します。

.. sourcecode:: java
    :linenos:
    :caption: Beanクラスのサンプル
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    
    
    // レコード用のPOJOクラスの定義
    @CsvBean(header=true)
    public class UserCsv {
        
        @CsvColumn(number=1)
        private int no;
        
        @CsvColumn(number=2, label="名前")
        private String name;
        
        // 引数なしのコンストラクタ
        public UserCsv() {
        
        }
        
        // 以下、フィールドに対するsetter/getterメソッド
        public int getNo() {
            return no;
        }
        
        public void setNo(int no) {
            this.no = no;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.naem = name;
        }
    }
    

.. note::
    
    アクセッサメソッドを動的に生成する `Lombok <http://projectlombok.org/>`_ を使用すると、コード量を減らすことができます。


--------------------------------------
読み込み方法
--------------------------------------

* CSVファイルを読み込む場合は、クラス ``CsvAnnotationBeanReader`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/io/CsvAnnotationBeanReader.html>`_ ]を使用します。
* 一度に全レコードを読み込む場合は、メソッド ``readAll(...)`` を使用します。
* 1件ずつ読み込む場合は、メソッド ``read(...)`` を使用します。

.. sourcecode:: java
    :linenos:
    :caption: 読み込むサンプル
    
    import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    import java.util.ArrayList;
    import java.util.List;
    
    import org.supercsv.prefs.CsvPreference;
    
    public class Sample {
    
        // 全レコードを一度に読み込む場合
        public void sampleReadAll() {
            
            CsvAnnotationBeanReader<UserCsv> csvReader = new CsvAnnotationBeanReader<>(
                    UserCsv.class,
                    Files.newBufferedReader(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            List<UserCsv> list = csvReader.readAll();
            
            csvReader.close();
        }
        
        // レコードを1件ずつ読み込む場合
        public void sampleRead() {
            
            CsvAnnotationBeanReader<UserCsv> csvReader = new CsvAnnotationBeanReader<>(
                    UserCsv.class,
                    Files.newBufferedReader(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            List<UserCsv> list = new ArrayList<>();
            
            // ヘッダー行の読み込み
            String headers[] = csvReader.getHeader(true);
            
            UserCsv record = null;
            while((record = csvReader.read()) != null) {
                list.add(record);
            }
            
            csvReader.close();
        }
    }
    

--------------------------------------
書き込み方法
--------------------------------------

* CSVファイルを書き込む場合は、クラス ``CsvAnnotationBeanWriter`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/io/CsvAnnotationBeanWriter.html>`_ ]を使用します。
* 一度に全レコードを書き込む場合は、メソッド ``writeAll(...)`` を使用します。
* 1件ずつ書き込む場合は、メソッド ``write(...)`` を使用します。

.. sourcecode:: java
    :linenos:
    :caption: 書き込むサンプル
    
    import com.github.mygreen.supercsv.io.CsvAnnotationBeanWriter;
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    import java.util.ArrayList;
    import java.util.List;
    
    import org.supercsv.prefs.CsvPreference;
    
    public class Sample {
    
        // 全レコードを一度に書き込む場合
        public void sampleWriteAll() {
            
            CsvAnnotationBeanWriter<UserCsv> csvWriter = new CsvAnnotationBeanWriter<>(
                    UserCsv.class,
                    Files.newBufferedWriter(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            // 書き込み用のデータの作成
            List<UserCsv> list = new ArrayList<>();
            UserCsv record1 = new UserCsv();
            record1.setNo(1);
            record1.setName("山田太郎");
            liad.add(record1);
            
            UserCsv record2 = new UserCsv();
            record2.setNo(2);
            record2.setName("鈴木次郎");
            liad.add(record2);
            
            // ヘッダー行と全レコードデータの書き込み
            csvWriter.writeAll(list);
            
            csvWriter.close();
        }
        
        // レコードを1件ずつ書き込む場合
        public void sampleWrite() {
           
            CsvAnnotationBeanWriter<UserCsv> csvWriter = new CsvAnnotationBeanWriter<>(
                    UserCsv.class,
                    Files.newBufferedWriter(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            // ヘッダー行の書き込み
            csvWriter.writeHeader();
            
            // レコードのデータの書き込み
            UserCsv record1 = new UserCsv();
            record1.setNo(1);
            record1.setName("山田太郎");
            csvWriter.write(record1);
            
            UserCsv record2 = new UserCsv();
            record2.setNo(2);
            record2.setName("鈴木次郎");
            csvWriter.write(record2);
            
            csvWrier.flush();
            csvWrier.close();
            
        }
    }
    

--------------------------------------
値の加工方法
--------------------------------------

本ライブラリには、様々なアノテーションが用意されており、:doc:`書式の指定 <format>` 、:doc:`トリムなどの値の変換 <conversion>` 、 :doc:`値の検証 <validation>` を行うことができます。
もちろん、独自のアノテーションを作成することもできます。

また、値を変換するアノテーションと検証を行うアノテーションにおいては、適用順や読み込み／書き込み時に適用するケースを指定する属性がそれぞれ ``order`` 、 ``cases`` にて可能です。


.. sourcecode:: java
    :linenos:
    :caption: 値を加工するアノテーションのサンプル
    
    import java.time.LocalDate;
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    import com.github.mygreen.supercsv.annotation.constraint.CsvNumberMin;
    import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
    import com.github.mygreen.supercsv.annotation.constraint.CsvUnique;
    import com.github.mygreen.supercsv.annotation.conversion.CsvDefaultValue;
    import com.github.mygreen.supercsv.annotation.conversion.CsvNullConvert;
    import com.github.mygreen.supercsv.annotation.format.CsvDateTimeFormat;
    import com.github.mygreen.supercsv.annotation.format.CsvNumberFormat;
    import com.github.mygreen.supercsv.builder.BuildCase;
    
    @CsvBean
    public class SampleCsv {
        
        @CsvColumn(number=1, label="ID")
        @CsvRequire                        // 必須チェックを行う
        @CsvUnique(order=1)                // 全レコード内で値がユニークか検証する(順番指定)
        @CsvNumberMin(value="0", order=2)  // 下限値以上か検証する(順番指定)
        private Integer id;
        
        @CsvColumn(number=2, label="名前")
        private String name;
        
        @CsvColumn(number=3, label="誕生日")
        @CsvDateTimeFormat(pattern="yyyy年MM月dd日")   // 日時の書式を指定する
        private LocalDate birthday;
        
        @CsvColumn(number=4, label="給料")
        @CsvNumberFormat(pattern="#,##0")                    // 数値の書式を指定する
        @CsvDefaultValue(value="N/A", cases=BuildCase.Write)  // 書き込み時に値がnull(空)の場合、「N/A」として出力します。
        @CsvNullConvert(value="N/A", cases=BuildCase.Read)    // 読み込み時に値が「N/A」のとき、nullとして読み込みます。
        private Integer salary;
        
        // getter/setterは省略
        
    }
    

--------------------------------------
タブ区切りCSVファイルへの対応
--------------------------------------

本ライブラリは、CSVの処理はSuper CSVの機能をそのまま使用しているため、
*CsvAnnotationBeanReader/CsvAnnotationBeanWriter* に渡す `CsvPreference <http://super-csv.github.io/super-csv/preferences.html>`_ をカスタマイズすることで、タブ区切りなどに対応できます。

.. sourcecode:: java
    :linenos:
    :caption: CSVの書式を変更するサンプル
    
    import com.github.mygreen.supercsv.io.CsvAnnotationBeanReader;
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    import java.util.ArrayList;
    import java.util.List;
    
    import org.supercsv.prefs.CsvPreference;
    import org.supercsv.quote.AlwaysQuoteMode;
    
    public class Sample {
    
        // 書き込む場合
        public void sampleWrite() {
            
            // CsvPreferencesのカスタマイズ
            // タブ区切り、改行コード「LF」、必ずダブルクウォートで囲む設定
            final CsvPreference preference = new CsvPreference.Builder('\"', '\t', "\n")
                .useQuoteMode(new AlwaysQuoteMode())
                .build();
            
            CsvAnnotationBeanWriter<UserCsv> csvReader = new CsvAnnotationBeanWriter<>(
                    UserCsv.class,
                    Files.newBufferedReader(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    preference);
            
            // 省略
        }
        
    }
    


