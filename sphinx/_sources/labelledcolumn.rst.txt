======================================
ラベルによるカラムのマッピング
======================================

カラム番号を指定しないで、ラベルのみよるカラムの定義を用いた読み書きの方法を説明します。 *[v2.1+]*

ラベルはヘッダー行の値と一致したものとマッピングします。

--------------------------------------------------------
Beanの定義
--------------------------------------------------------

* ラベルのみによるマッピングは、アノテーション ``@CsvColumn`` の属性 ``number`` を省略します。
* フィールド名とCSVのヘッダー行の該当する見出しが異なる場合は、 ``label`` で指定します。
* 従来の属性 ``number`` でカラム番号で指定することもできます。

  * 最終的にマッピングする場合、別々なフィールドに対して同じカラムをマッピングするこはできません。
  * 見出しが同じ値の場合は、 ``number`` を指定して区別するようにしてください。

.. sourcecode:: java
    :linenos:
    :caption: Beanの定義
    
    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    
    @CsvBean(header=true, validateHeader=true)
    public class SampleBean {
        
        // ラベルがフィールド名
        @CsvColumn
        private int no;
        
        // 従来のカラム番号を指定
        @CsvColumn(number=2)
        private String name;
        
        // ラベルだけ指定
        @CsvColumn(label="生年月日")
        @CsvDateTimeFormat(pattern="uuuu/MM/dd")
        private LocalDate birthday;
        
        // カラム番号とラベルの両方を指定
        @CsvColumn(number=4, label="備考")
        private String comment;
        
        // getter/setterの定義は省略
    
    }


--------------------------------------------------------
読み込み方法
--------------------------------------------------------

ラベルによるマッピングを行い読み込むには、 ``LazyCsvAnnotationBeanReader`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/io/LazyCsvAnnotationBeanReader.html>`_ ] を使用します。

* 全件読み込む場合の使用方法は、基本的に既存の ``CsvAnnotationBeanReader`` と変わりません。
* １件ずつ読み込む場合は、メソッド ``LazyCsvAnnotationBeanReader#init()`` を呼んでマッピング情報を初期化します。

  * 処理内容としては、ヘッダー行を読み込み、それを元にBeanのフィールドとマッピングを行い、カラムの番号を決定します。
  * 全件読み込むメソッド ``readAll()`` 内では、メソッド ``init()`` が呼ばれているため、初期化が省略することができます。
    特に、 ``readAll()`` を呼び出す前に、 ``init()`` を実行してもかまいません。

.. sourcecode:: java
    :linenos:
    :caption: ラベルによるマッピング - 読み込み
    
    import com.github.mygreen.supercsv.io.LazyCsvAnnotationBeanReader;
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    import java.util.ArrayList;
    import java.util.List;
    
    public class Sample {
        
        // 全レコードを一度に読み込む場合
        public void sampleReadAll() {
            
            LazyCsvAnnotationBeanReader<SampleBean> csvReader = new LazyCsvAnnotationBeanReader<>(
                    SampleBean.class,
                    Files.newBufferedReader(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            List<SampleBean> list = csvReader.readAll();
            
            csvReader.close();
        }
        
        // レコードを1件ずつ読み込む場合
        public void sampleRead() {
        
            LazyCsvAnnotationBeanReader<SampleBean> csvReader = new LazyCsvAnnotationBeanReader<>(
                    SampleBean.class,
                    Files.newBufferedReader(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            // ヘッダー行を読み込み初期化します
            csvReader.init();
            
            List<SampleBean> list = new ArrayList<>();
            
            SampleBean record = null;
            while((record = csvReader.read()) != null) {
                list.add(record);
            }
            
            csvReader.close();
        }
    }



ヘッダー行が存在しないファイルの場合は、メソッド ``init("見出し1","見出し2",...)`` でヘッダー情報を直接指定し、初期化します。
その場合、メソッド ``readAll()`` を呼ぶ前にも、直接ヘッダー情報を指定して初期化する必要があります。

.. sourcecode:: java
    :linenos:
    :caption: ヘッダー行が存在しないときの読み込み方法
    
    import com.github.mygreen.supercsv.io.LazyCsvAnnotationBeanReader;
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    import java.util.ArrayList;
    import java.util.List;
    
    public class Sample {
        
        // 全レコードを一度に読み込む場合
        public void sampleReadAll() {
            
            LazyCsvAnnotationBeanReader<SampleBean> csvReader = new LazyCsvAnnotationBeanReader<>(
                    SampleBean.class,
                    Files.newBufferedReader(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            // ヘッダー情報を指定して初期化します。
            csvReader.init("no", "name", "生年月日", "備考");
            
            List<SampleBean> list = csvReader.readAll();
            
            csvReader.close();
        }
        
        // レコードを1件ずつ読み込む場合
        public void sampleRead() {
        
            LazyCsvAnnotationBeanReader<SampleBean> csvReader = new LazyCsvAnnotationBeanReader<>(
                    SampleBean.class,
                    Files.newBufferedReader(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            // ヘッダー情報を指定して初期化します。
            csvReader.init("no", "name", "生年月日", "備考");
            
            List<SampleBean> list = new ArrayList<>();
            
            SampleBean record = null;
            while((record = csvReader.read()) != null) {
                list.add(record);
            }
            
            csvReader.close();
        }
    }


--------------------------------------------------------
書き出し方法
--------------------------------------------------------

ラベルによるマッピングを行い書き出すには、 ``LazyCsvAnnotationBeanWriter`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/io/LazyCsvAnnotationBeanWriter.html>`_ ] を使用します。

* 全件読み出す場合の使用方法は、基本的に既存の ``CsvAnnotationBeanWriter`` と変わりません。
* １件ずつ書き出す場合は、メソッド ``CsvAnnotationBeanWriter#init()`` を呼んでマッピング情報を初期化します。

  * 処理内容としては、カラムの番号を決定します。カラムの番号は、フィールド名の昇順になります。
  * Beanの定義中にカラム番号を指定しているフィールドと指定していないフィールドが混在する場合、カラム番号が空いているものが利用されます。
  * 全件書き出すメソッド ``writeAll()`` 内では、メソッド ``init()`` が呼ばれているため、初期化が省略することができます。
    特に、 ``writeAll()`` を呼び出す前に、 ``init()`` を実行してもかまいません。

.. sourcecode:: java
    :linenos:
    :caption: ラベルによるマッピング - 書き出し
    
    import com.github.mygreen.supercsv.io.LazyCsvAnnotationBeanWriter;
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    import java.util.ArrayList;
    import java.util.List;
    
    import org.supercsv.prefs.CsvPreference;
    
    public class Sample {
        
        // 全レコードを一度に書き込む場合
        public void sampleWriteAll() {
            
            LazyCsvAnnotationBeanWriter<UserCsv> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                    SampleCsv.class,
                    Files.newBufferedWriter(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            // 書き出し用のデータの作成
            List<SampleCsv> list = new ArrayList<>();
            
            SampleCsv record1 = new SampleCsv();
            record1.setNo(1);
            record1.setName("山田太郎");
            record1.setBirthday(LocalDate.of(2000, 10, 1));
            record1.setComment("あいうえお");
            liad.add(record1);
            
            SampleCsv record2 = new SampleCsv();
            record2.setNo(2);
            record2.setName("鈴木次郎");
            record2.setBirthday(LocalDate.of(2012, 1, 2));
            record2.setComment(null);
            liad.add(record2);
            
            // ヘッダー行と全レコードデータの書き出し
            csvWriter.writeAll(list);
            
            csvWriter.close();
        }
        
        // レコードを1件ずつ書き出す場合
        public void sampleWrite() {
            
            LazyCsvAnnotationBeanWriter<SampleCsv> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                    UserCsv.class,
                    Files.newBufferedWriter(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            // 初期化を行います
            csvWriter.init();
            
            // ヘッダー行の書き出し
            csvWriter.writeHeader();
            
            // レコードのデータの書き出し
            SampleCsv record1 = new UserCsv();
            record1.setNo(1);
            record1.setName("山田太郎");
            record1.setBirthday(LocalDate.of(2000, 10, 1));
            record1.setComment("あいうえお");
            csvWriter.write(record1);
            
            SampleCsv record2 = new UserCsv();
            record2.setNo(2);
            record2.setName("鈴木次郎");
            record2.setBirthday(LocalDate.of(2012, 1, 2));
            record2.setComment(null);
            csvWriter.write(record2);
            
            csvWrier.flush();
            csvWrier.close();
            
        }
    }


カラム順を独自に指定したい場合、メソッド ``init("見出し1","見出し2",...)`` でヘッダー情報を直接指定し、初期化します。
その場合、メソッド ``writeAll()`` を呼ぶ前にも、直接ヘッダー情報を指定して初期化する必要があります。

.. sourcecode:: java
    :linenos:
    :caption: カラムの順番を指定し書き出す
    
    import com.github.mygreen.supercsv.io.LazyCsvAnnotationBeanWriter;
    
    import java.nio.charset.Charset;
    import java.nio.file.Files;
    import java.io.File;
    import java.util.ArrayList;
    import java.util.List;
    
    import org.supercsv.prefs.CsvPreference;
    
    public class Sample {
        
        // 全レコードを一度に書き込む場合
        public void sampleWriteAll() {
            
            LazyCsvAnnotationBeanWriter<UserCsv> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                    SampleCsv.class,
                    Files.newBufferedWriter(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            // カラムの順番指定して初期化します。
            csvWriter.init("no", "name", "生年月日", "備考");
            
            // 書き出し用のデータの作成
            List<SampleCsv> list = new ArrayList<>();
            //・・・省略
            
            // ヘッダー行と全レコードデータの書き出し
            csvWriter.writeAll(list);
            
            csvWriter.close();
        }
        
        // レコードを1件ずつ書き出す場合
        public void sampleWrite() {
            
            LazyCsvAnnotationBeanWriter<SampleCsv> csvWriter = new LazyCsvAnnotationBeanWriter<>(
                    UserCsv.class,
                    Files.newBufferedWriter(new File("sample.csv").toPath(), Charset.forName("Windows-31j")),
                    CsvPreference.STANDARD_PREFERENCE);
            
            // カラムの順番指定して初期化します。
            csvWriter.init("no", "name", "生年月日", "備考");
            
            // ヘッダー行の書き出し
            csvWriter.writeHeader();
            
            // レコードのデータの書き出し
            //・・・省略
            
            csvWrier.flush();
            csvWrier.close();
            
        }
    }



