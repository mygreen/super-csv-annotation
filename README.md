super-csv-annotation
====================

'Super CSV' extention library for annotation
(1)this library automatic build CellProcessor from Annotation with JavaBean.
(2)simply showing localized messages.

------------------------
depends
------------------------
 (1)JDK1.6+ (SuperCSV2.x is JDK1.5)
 (2)SuperCSV 2.x

===============================
(1) @CsvEnity annotate Bean class.
 ・header = automatic header
=============================
example @CsvBean
=============================
@CsvBean(header=true)
public class SampleBean1{
...
}
=============================

(2) @CsvColumn annotate Field type. set for public / private / protcted field.
 ・position = rqeuired argment. column index start with zero(0). 
 ・label = header label. if empty, use filed name.
 ・optional = set CellProcessor NotNull(false) / Optional(true). default false.
 ・trim = if set true, set CellProcessor Trim()
 ・inputDefaultValue = on reading, set CellProcessor 'ConvertNullTo'. if field type String class, empty value as '@empty'.
 ・outputDefaultValue = on writing, set CellProcessor 'ConvertNullTo'.if field type String class, empty value as '@empty'.
 ・unique = constaint option. set CellProcessor 'Unique()'.
 ・equalsValue = constain option. set CellProcessor 'Equals()'.
 ・builderClass = set your customize CellProcessorBuilder class. this class must inherit 'AbstractCellProcessorBuilder'

=============================
Example @CsvColumn
=============================
// Java Bean
@CsvBean(header=true)
public class SampleBean1{
    
    @CsvColumn(position = 0, optional = true)
    private int integer1 input
    
    @CsvColumn(position = 1, optional = false, unique = true)
    private Integer integer2;
    
    @CsvColumn(position = 2, optional = true, trim = true, inputDefaultValue="aa")
    public String string3;
    
    @CsvColumn(position = 3, ,outputDefaultValue="2012-10-13 00:00:00")
    public Date date4;
    
    @CsvColumn(position = 4, inputDefaultValue="RED", outputDefualtValue="BLUE")
    public Color enum5;
    
    enum Color {
       RED, BLUE, GREEN, Yellow;
    }
}
----------------------------
(1)build field 'integer1' CellProcessor
 #input CellProcessor
  new Optional()
 
 #output CellProcessor
  new Optional()

(2)build field 'integer2' CellProcessor
 #input CellProcessor
  new NotNull(new Unique())
 
 #output CellProcessor
  new NotNull(new Unique())

(3)build field 'string3' CellProcessor
 #input CellProcessor
  new Optional(new new ConvertNullTo('aa', Trim()))
 
 #output CellProcessor
  new Optional(new Trim())
  
(4)build field 'date4' CellProcessor
 #input CellProcessor
  new ConvertNullTo(date obj('2012-10-13 00:00:00'), new NotNull(new ParseLocaleDate('yyyy-MM-dd HH:mm:ss')))
 
 #output CellProcessor
  new NotNull(ConvertNullTo(date obj('2012-10-13 00:00:00'), new FormatLocaleDate('yyyy-MM-dd HH:mm:ss')))

(5)build field 'enum5' CellProcessor
 #input CellProcessor
  new ConvertNullTo(enum obj('RED'), new NotNull(new ParseEnum()))
 
 #output CellProcessor
  new NotNull(ConvertNullTo(enum obj('BLUE')))

=============================

=============================
example @CsvStringConverter / @CsvNumberConverter / @CsvDateConverter / @CsvEnumConverter
=============================
(1) @CsvStringConverter is setting for String class.
 ・minLength : constrain the minimum character long. set CellProcessor 'MinLength' (custom processor)
 ・maxLength : constrain the maximum character long. set CellProcessor 'MaxLength' (custom processor)
             if minLength > 0 and maxLength >0, set CellProcessor 'StrMinMax'
 ・exactlength : constain the equals character long. set CellProcessor 'Strlen'
 ・regex : constrain the reqular expression pattern. set CellProcessor 'StrRegEx'
 ・forbid : constrain the not contain fobbien substring. set CellProcessor 'ForbidSubStr'
 ・contain : constrain the contain substirng. set CellProcessor 'RequireSubStr'
 ・notEmpty : constain the not empty. set CellProcesor 'StrNotNullOrEmpty'

(2) @CsvNumberConverter is setting for number classes. 
    number classes : byte/shortint/long/float/double/Byte/Integer/Long/Float/Double/BigDecimal/BigInteger 
    
・pattern : Number format pattern. set CellProcessor 'FormatLocaleNumber' (custom processor).
・lenient : paring string to Number object non-exactly. optional argument for CellProcessor 'FormatLocaleNumber'.
・currency : Code(ISO 4217 Code). optional argument for CellProcessor 'FormatLocaleNumber'.
・language : Locale with language. optional argument for CellProcessor 'FormatLocaleNumber'.
・country : Locale with country. optional argument for CellProcessor 'FormatLocaleNumber'.
・min : constarin the mininum value. set CellProcessor 'Min' (custom processor)
・max : constarin the maximum value. set CellProcessor 'Max' (custom processor)
       if min != "" and max != "", set CellProcessor 'NumberRange' (custom processor)

(3)@CsvDateConverter is setting for date class.
   date classes : java.util.Date / java.sql.Date / java.sql.Time / java.sql.Timestamp 
   
・pattern : Date format pattern. set CellProcessor 'FormatLocaleDate / ParseLocaleDate' (custom processor).
・lenient : parse string to Date object non-exactly. optional argument for CellProcessor 'FormatLocaleDate / ParseLocaleDate'.
・timezone : optional argument for CellProcessor 'FormatLocaleDate / ParseLocaleDate'.
・language : Locale with language. optional argument for CellProcessor 'FormatLocaleDate / ParseLocaleDate'.
・country : Locale with country. optional argument for CellProcessor 'FormatLocaleDate / ParseLocaleDate'.
・min : constarin the mininum value. set CellProcessor 'FutrueDate' (custom processor)
・max : constarin the maximum value. set CellProcessor 'PastDate' (custom processor)
       if min != "" and max != "", set CellProcessor 'DateRange' (custom processor)

(4)@CsvEnumConverter is setting for Enum class.
・lenient : parse with ignored case. optional argument for CellProsessor ''

--------------------------------------
@CsvBean(header=true)
public class SampleBean1{

    @CsvColumn(position = 0, label="数字")
    private int integer1;
    
    @CsvColumn(position = 1, optional=true)
    @CsvNumberConverter(pattern="###,###,###")
    private Integer integer2;
    
    @CsvColumn(position = 2)
    private String string1;
    
    @CsvColumn(position = 3, optional=true, inputDefaultValue="@empty")
    @CsvStringConverter(maxLength=6, contain={"1"})
    private String string2;
    
    @CsvColumn(position = 4)
    private Date date1;
    
    @CsvColumn(position = 5, optional=true)
    @CsvDateConverter(pattern="yyyy/MM/dd", min="2000/10/30")
    private Timestamp date2;
    
    @CsvColumn(position = 6, label="enum class", optional=true, inputDefaultValue="BLUE")
    @CsvEnumConveret(lenient = true)
    private Color enum1;
    
}

===================================================
Sample Writer
===================================================

(1) use CsvBeanWriter
---------------------------------------------------
// create cell processor and field name mapping
CsvAnnotationBeanParser helper = new CsvAnnotationBeanParser();
CsvBeanMapping<SampleBean1> mappingBean = helper.parse(SampleBean1.class, false);

String[] nameMapping = mappingBean.getNameMapping();
CellProcessor[] cellProcessors = mappingBean.getOutputCellProcessor();

StringWriter strWriter = new StringWriter();
ICsvBeanWriter csvWriter = null;
csvWriter = new CsvBeanWriter(strWriter, CsvPreference.STANDARD_PREFERENCE);

// write bean data.
List<SampleBean1> list = ...;
csvWriter.writeHeader(mappingBean.getHeader());
for(final SampleBean1 item : list) {
    csvWriter.write(item, nameMapping, cellProcessors);
    csvWriter.flush();
}
---------------------------------------------------
(2) use CsvAnnotationBeanWriter (custom class)
---------------------------------------------------
StringWriter strWriter = new StringWriter();
CsvAnnotationBeanWriter<SampleBean1> csvWriter = 
    new CsvAnnotationBeanWriter<SampleBean1>(SampleBean1.class, 
        strWriter, CsvPreference.STANDARD_PREFERENCE);

// write bean data.
List<SampleBean1> list = ...;
csvWriter.writeHeader();  // use custom method.
for(final SampleBean1 item : list) {
    csvWriter.write(item);  // use cutom method.
    csvWriter.flush();
}

===================================================
Sample Reader
===================================================

(1) use CsvBeanReader
---------------------------------------------------
// create cell processor and field name mapping
CsvAnnotationBeanParser helper = new CsvAnnotationBeanParser();
CsvBeanMapping<SampleBean1> mappingBean = helper.parse(SampleBean1.class, false);

String[] nameMapping = mappingBean.getNameMapping();
CellProcessor[] cellProcessors = mappingBean.getInputCellProcessor();

File inputFile = new File("src/test/data/test_error.csv");
ICsvBeanReader csvReader = new CsvBeanReader(
   new InputStreamReader(new FileInputStream(inputFile), "Windows-31j"),
       CsvPreference.STANDARD_PREFERENCE);

// write bean data.
List<SampleBean1> list = new ArrayList<SampleBean1>();
String[] headers = csvReader.getHeader(true);
while((bean1 = csvReader.read(SampleBean1.class, nameMapping, cellProcessors)) != null) {
    System.out.println(bean1);
    list.add(bean1);
}
---------------------------------------------------
(2) use CsvAnnotationBeanWriter (custom class)
---------------------------------------------------
File inputFile = new File("src/test/data/test_error.csv");
CsvAnnotationBeanReader csvReader = 
    new CsvAnnotationBeanReader(SampleBean1.class, strWriter, CsvPreference.STANDARD_PREFERENCE);

// write bean data.
List<SampleBean1> list = new ArrayList<SampleBean1>();
String[] headers = csvReader.getHeader();  // use custom method.
while((bean1 = csvReader.read()) != null) {
    System.out.println(bean1);
    list.add(bean1);
}


===================================================
Sample Localize Message
===================================================
 use ValidatableCsvBeanReader (custom class)
// create cell processor and field name mapping
CsvAnnotationBeanParser helper = new CsvAnnotationBeanParser();
CsvBeanMapping<SampleBean1> mappingBean = helper.parse(SampleBean1.class, false);

String[] nameMapping = mappingBean.getNameMapping();
CellProcessor[] cellProcessors = mappingBean.getInputCellProcessor();

File inputFile = new File("src/test/data/test_error.csv");
ICsvBeanReader csvReader = new ValidatableCsvBeanReader(
   new InputStreamReader(new FileInputStream(inputFile), "Windows-31j"),
       CsvPreference.STANDARD_PREFERENCE);

// create instance of exeception converter
CsvExceptionConveter exceptionConveter = new CsvExceptionConveter();
MessageConverter messageConverter = new MessageConverter();


// write bean data.
List<SampleBean1> list = new ArrayList<SampleBean1>();
try {
    String[] headers = csvReader.getHeader(true);
    while((bean1 = csvReader.read(SampleBean1.class, nameMapping, cellProcessors)) != null) {
        System.out.println(bean1);
        list.add(bean1);
    }
} catch(SuperCsvException e) {
    // convert exception to String message
    List<CsvMessage> csvErrors= exceptionConveter.convertCsvError(e);
    List<String> messages = messageConverter.convertMessage(csvErrors);
    for(String str : messages) {
        System.err.println(str);
    }
}
---------------------------
Custoize messages.
---------------------------
・set message resolver.

CsvExceptionConveter exceptionConveter = new CsvExceptionConveter();
MessageConverter messageConverter = new MessageConverter();
messageConverter.setMessageResolver(new ResourceBundleMessageResolver(... your resource bundle))

---------------------------
message example (org/supercsv/ext/SuperCsvMessages.properties)
---------
# common variable
# ${lineNumber} = the line number of the file being read/written
# ${rowNumber} = the CSV row number (CSV rows can span multiple lines)
# ${columnNumber} = the CSV column number
# ${value} = the invalidate value (source).
csvError=(row, column)=(${lineNumber}, ${columnNumber}) : hasError
csvError.noMatchColumnSize=(row)=(${lineNumber}) : no match column size. expected size=${expectedSize}, but actual size='${value}'

# original CellProcessor
org.supercsv.cellprocessor.ConvertNullTo.violated=(row, column)=(${lineNumber}, ${columnNumber}) : fail to convert from null object
org.supercsv.cellprocessor.FmtBool.violated=(row, column)=(${lineNumber}, ${columnNumber}) : fail to format boolean from '${value}'
org.supercsv.cellprocessor.FmtDate.violated=(row, column)=(${lineNumber}, ${columnNumber}) : fail to date from '${value}'
org.supercsv.cellprocessor.FmtNumber.violated=(row, column)=(${lineNumber}, ${columnNumber}) : fail to number from '${value}'
org.supercsv.cellprocessor.HashMapper.violated=(row, column)=(${lineNumber}, ${columnNumber}) : fail to convert '${value}' with hash map
org.supercsv.cellprocessor.Optional.violated=(row, column)=(${lineNumber}, ${columnNumber}) : fail to convert optional value
org.supercsv.cellprocessor.ParseBigDecimal.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot parse to BigDecimal object
org.supercsv.cellprocessor.ParseBool.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot parse to Boolean object
org.supercsv.cellprocessor.ParseChar.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot parse to Character objet
org.supercsv.cellprocessor.ParseDate.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot parse to Date object
org.supercsv.cellprocessor.ParseDouble.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot parse to Double object
org.supercsv.cellprocessor.ParseInt.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot parse to Integer object
org.supercsv.cellprocessor.ParseLong.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot parse to Long object
org.supercsv.cellprocessor.StrReplace.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot replace string
org.supercsv.cellprocessor.Token.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot convert special token
org.supercsv.cellprocessor.Trim.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot triming character
org.supercsv.cellprocessor.Truncate.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot truncate

# original CellProcessor( constraint )
org.supercsv.cellprocessor.constraint.DMinMax.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' in the range min through max
org.supercsv.cellprocessor.constraint.Equals.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' is not equal
org.supercsv.cellprocessor.constraint.ForbidSubStr.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' must not contain the forbidden substring
org.supercsv.cellprocessor.constraint.IsIncludedIn.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' must include in the value
org.supercsv.cellprocessor.constraint.LMinMax.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' is not in the range min through max
org.supercsv.cellprocessor.constraint.NotNull.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot be null
org.supercsv.cellprocessor.constraint.RequireHashCode.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' must be equal hash code
org.supercsv.cellprocessor.constraint.RequireSubStr.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' must contain the substring
org.supercsv.cellprocessor.constraint.Strlen.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' is not between min and max characters long
org.supercsv.cellprocessor.constraint.StrMinMax.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' is not between min and max chracters long.
org.supercsv.cellprocessor.constraint.StrNotNullOrEmpty.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot be empty
org.supercsv.cellprocessor.constraint.StrRegEx.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' does not match the pattern
org.supercsv.cellprocessor.constraint.Unique.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' is not be unique
org.supercsv.cellprocessor.constraint.UniqueHashCode.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' has not uniqe hash code

# customize's contibution CellProcessor
org.supercsv.ext.cellprocessor.FormatLocaleDate.violated=(row, column)=(${lineNumber}, ${columnNumber}) : fail to convert date with locale from '${value}'
org.supercsv.ext.cellprocessor.FormatLocaleNumber.violated=(row, column)=(${lineNumber}, ${columnNumber}) : fail to convert number from '${value}'
org.supercsv.ext.cellprocessor.ParseBigInteger.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot parse to BigInteger object
org.supercsv.ext.cellprocessor.ParseByte.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' is cannot parse to Byte object
org.supercsv.ext.cellprocessor.ParseEnum.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' is cannot parse to Enum object
org.supercsv.ext.cellprocessor.ParseFloat.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' is cannot parse to Float object
org.supercsv.ext.cellprocessor.ParseLocaleDate.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot parse to Date object with pattern '${pattern}'
org.supercsv.ext.cellprocessor.ParseLocaleNumber.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot parse to Number object with pattern '${pattern}'
org.supercsv.ext.cellprocessor.ParseLocaleSqlDate.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot parse to SqlDate object with pattern '${pattern}'
org.supercsv.ext.cellprocessor.ParseLocaleTime.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot parse to Time object with pattern '${pattern}'
org.supercsv.ext.cellprocessor.ParseLocaleTimestamp.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot parse to Timestamp object with pattern '${pattern}'
org.supercsv.ext.cellprocessor.ParseShort.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot parse to Short object

# customize's contibution CellProcessor ( constraint )
org.supercsv.ext.cellprocessor.constraint.DateRange.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' is not in the range ${min} through ${max}
org.supercsv.ext.cellprocessor.constraint.FutureDate.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' must be in the future ${min} 
org.supercsv.ext.cellprocessor.constraint.Length.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' is not between ${min} and ${max} characters long
org.supercsv.ext.cellprocessor.constraint.Max.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot be greater than ${max}
org.supercsv.ext.cellprocessor.constraint.MaxLength.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot be longer than ${max} characters
org.supercsv.ext.cellprocessor.constraint.Min.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannnt be smaller than ${min}
org.supercsv.ext.cellprocessor.constraint.MinLength.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' cannot be shorter than ${min} characters
org.supercsv.ext.cellprocessor.constraint.NumberRange.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' is not in the range ${min} throud ${max}
org.supercsv.ext.cellprocessor.constraint.PastDate.violated=(row, column)=(${lineNumber}, ${columnNumber}) : '${value}' must be in the past ${max}
===========================
 


