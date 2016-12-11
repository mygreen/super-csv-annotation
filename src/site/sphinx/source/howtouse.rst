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
        <artifactId>supre-csv-annotation</artifactId>
        <version>2.0</version>
    </dependency>


--------------------------------------
CSV用のJavaBeanクラスの定義
--------------------------------------

CSVの1レコード分をマッピングするためのPOJOクラスを作成します。

* CSV用のクラスは、 ``public`` である必要があります。

  * 引数なしの ``public`` なコンストラクタが必要です。
  * コンストラクタを定義しない場合は、デフォルトコンストラクタでもかまいません。
  
* CSV用のクラスであることを示すために、アノテーション ``@CsvBean`` をクラスに付与します。

  * 属性 ``header`` をtrueとすると、 *CsvAnnotationBeanReader#readAll(...)* と *CsvAnnotationBeanWriter#writeAll(...)* メソッドの呼び出し時に、ヘッダー行がある前提として処理します。

* カラムをフィールドにマッピングするために、アノテーション ``@CsvColumn`` をフィールドに付与します。

  * 属性 ``number`` で、マッピングするカラムの番号を指定します。カラムの番号は1から始まります。
  * 属性 ``label`` で、ヘッダー行のラベル名を指定することができます。省略した場合はフィールド名が適用されます。

* フィールドに対応するgetter/setterのアクセッサメソッドを作成します。

.. sourcecode:: java
    :linenos:
    
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

* CSVファイルを読み込む場合は、クラス ``CsvAnnotationBeanReader`` を使用します。
* 一度に全レコードを読み込む場合は、メソッド ``readAll(...)`` を使用します。
* 1件ずつ読み込む場合は、メソッド ``read(...)`` を使用します。

.. sourcecode:: java
    :linenos:
    
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

* CSVファイルを読み込む場合は、クラス ``CsvAnnotationBeanWriter`` を使用します。
* 一度に全レコードを書き込む場合は、メソッド ``writeAll(...)`` を使用します。
* 1件ずつ書き込む場合は、メソッド ``write(...)`` を使用します。

.. sourcecode:: java
    :linenos:
    
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
        
        // レコードを1件ずつ読み込む場合
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

