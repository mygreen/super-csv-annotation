super-csv-annotation
====================

'Super CSV' extention library for annotation
+ this library automatic build CellProcessor from Annotation with JavaBean.
+ simply showing localized messages.

# Depends
------------------------------
+ JDK1.6+ (SuperCSV2.x is JDK1.5)
+ SuperCSV 2.x

# Usage
------------------------------

## Require Annotation

### @CsvEnity annotate Bean class.
#### annotation elements
- header : boolean = whether using header. 

#### Example @CsvBean

```java
@CsvBean(header=true)
public class SampleBean1{
...
}
```

###  @CsvColumn annotate Field type. set for public / private / protcted field.
#### annotation elements
- position : int = rqeuired argment. column index start with zero(0). 
- label : String = header label. if empty, use filed name.
- optional : boolean = set CellProcessor NotNull(false) / Optional(true). default false.
- trim : boolean = if set true, set CellProcessor Trim()
- inputDefaultValue : String = if set this values, then reading to set for CellProcessor 'ConvertNullTo'. 
    - if field type String class, empty value as '@empty'.
- outputDefaultValue : String = if set this values, then writing to set for CellProcessor 'ConvertNullTo'. 
    - if field type String class, empty value as '@empty'.
- unique : boolean = constaint option. check the value for unique. if set the true, reading/wriing to set CellProcessor 'Unique()'.
- equalsValue : String = constain option. check the value for equals. if set the value, reading/wriing to set CellProcessor 'Equals()'.
- builderClass : Class = you can set fo your customize CellProcessorBuilder class. this class must inherit 'AbstractCellProcessorBuilder'.

### Example @CsvColumn
```java
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
    
    @CsvColumn(position = 4, inputDefaultValue="false", outputDefualtValue="true")
    public boolean boole5;
    
    @CsvColumn(position = 5, inputDefaultValue="RED", outputDefualtValue="BLUE")
    public Color enum6;
    
    enum Color {
       RED, BLUE, GREEN, Yellow;
    }
}
```

#### this api build cell processos from avobe examples.

0. build field 'integer1' CellProcessor ( @CsvColumn(position = 0, optional = true) )
```java
 // buld for input CellProcessor
 new Optional(new ParseInt())
 
 // buld for output CellProcessor
 new Optional()
```

1. build field 'integer2' CellProcessor ( @CsvColumn(position = 1, optional = false, unique = true) )
```java
 // buld for input CellProcessor
  new NotNull(new Unique(new ParseInt()))
 
 // buld for output CellProcessor
  new NotNull(new Unique())
```

2. build field 'string3' CellProcessor ( @CsvColumn(position = 2, optional = true, trim = true, inputDefaultValue="aa") )
```java
 // buld for input CellProcessor
  new ConvertNullTo('a', new Optional(new Trim()))
 
 // buld for output CellProcessor
  new Optional(new Trim())
```

3. build field 'date4' CellProcessor ( @CsvColumn(position = 3, ,outputDefaultValue="2012-10-13 00:00:00") )
```java
 // buld for input CellProcessor
 // use default pattern 'yyyy-MM-dd HH:mm:ss'
  new ConvertNullTo(date obj('2012-10-13 00:00:00'), new NotNull(new ParseLocaleDate('yyyy-MM-dd HH:mm:ss')))
 
 // buld for output CellProcessor
 // use default pattern 'yyyy-MM-dd HH:mm:ss'
  new NotNull(ConvertNullTo( date obj('2012-10-13 00:00:00'), new FormatLocaleDate('yyyy-MM-dd HH:mm:ss')))
```

4. build field 'bool5' CellProcessor ( @CsvColumn(position = 4, inputDefaultValue="false" )
```java
 // buld for input CellProcessor
  new ConvertNullTo( boolean obj('false'), new NotNull( new ParseBoolean()))
 
 // buld for output CellProcessor
  new NotNull( new FmtBool())
```

5. build field 'enum6' CellProcessor ( @CsvColumn(position = 5, inputDefaultValue="RED", outputDefualtValue="BLUE") )
```java
 // buld for input CellProcessor
  new ConvertNullTo(enum obj('RED'), new NotNull(new ParseEnum()))
 
 // buld for output CellProcessor
  new ConvertNullTo(enum obj('BLUE', new NotNull())
```


## Optional Annotation

### @CsvStringConverter is setting for String class.
this annotation for String classes.

#### annotation elements
- minLength : int = constrain the minimum character long. set CellProcessor 'MinLength' (custom processor).
- maxLength int = constrain the maximum character long. set CellProcessor 'MaxLength' (custom processor).
    - if minLength > 0 and maxLength >0, set CellProcessor 'StrMinMax'.
- exactlength : int = constain the equals character long. set CellProcessor 'Strlen'.
- regex : String = constrain the reqular expression pattern. set CellProcessor 'StrRegEx'.
- forbid : String[] = constrain the not contain fobbien substring. set CellProcessor 'ForbidSubStr'.
- contain : String[] = constrain the contain substirng. set CellProcessor 'RequireSubStr'.
- notEmpty : boolean = constain the not empty. set CellProcesor 'StrNotNullOrEmpty'

### @CsvNumberConverter is setting for number classes. 
this annotation for number classes : byte/shortint/long/float/double/Byte/Integer/Long/Float/Double/BigDecimal/BigInteger 

#### annotation elements
- pattern : String = Number format pattern. set CellProcessor 'FormatLocaleNumber' (custom processor).
    - if empty, parse for Number object parse method. ex) Integer.parseInt(...), Double.parseDouble(...).
- lenient : boolean = Paring from string to Number object non-exactly. optional argument for CellProcessor 'FormatLocaleNumber'.
- currency : String = Code(ISO 4217 Code). optional argument for CellProcessor 'FormatLocaleNumber'.
- language : String = Locale with language. optional argument for CellProcessor 'FormatLocaleNumber'.
- country : String = Locale with country. optional argument for CellProcessor 'FormatLocaleNumber'.
- min : String = constarin the mininum value. set CellProcessor 'Min' (custom processor)
    - if buld for input processor, pase this value by element 'pattern'.
- max : String = String = constarin the maximum value. set CellProcessor 'Max' (custom processor)
    - if min != "" and max != "", set CellProcessor 'NumberRange' (custom processor)
    - if build input processor, pase this value by element 'pattern'.

### @CsvDateConverter is setting for date class.
this annotation for date classes : java.util.Date / java.sql.Date / java.sql.Time / java.sql.Timestamp 

#### annotation elements
- pattern : String = Date format pattern. set CellProcessor 'FormatLocaleDate / ParseLocaleDate' (custom processor).
- lenient : boolean = parse string to Date object non-exactly. optional argument for CellProcessor 'FormatLocaleDate / ParseLocaleDate'.
- timezone : String = optional argument for CellProcessor 'FormatLocaleDate / ParseLocaleDate'.
- language : String = Locale with language. optional argument for CellProcessor 'FormatLocaleDate / ParseLocaleDate'.
- country : String = Locale with country. optional argument for CellProcessor 'FormatLocaleDate / ParseLocaleDate'.
- min : String = constarin the mininum value. set CellProcessor 'FutrueDate' (custom processor)
    - if buld for input processor, pase this value by element 'pattern'.
- max : String = constarin the maximum value. set CellProcessor 'PastDate' (custom processor)
    - if min != "" and max != "", set CellProcessor 'DateRange' (custom processor)
    - if buld for input processor, pase this value by element 'pattern'.

### @CsvBooleanConverter is seting for boolean class.
this annotation for Boolean classes : boolean / Boolean.

#### annotation elements
- inputTrueValues : String[] = pase string as true value. set CellProcessor 'ParseBoolean' (custom processor)
- inputTrueValues : String[] = pase string as false value. set CellProcessor 'ParseBoolean' (custom processor)
- outputTrueValue : String = output boolean(true) to string value. set CellProsessor 'FtmBool'.
- outputFalseValue : String = output boolean(false) to string value. set CellProsessor 'FtmBool'.
- lenient : boolean = if this value is 'true', parse whith ignore lower / upper case.
- failtToFalse : boolean : if fail parsing the value, return to false.

### @CsvEnumConverter is setting for Enum class.
this annotation for Enum classes.

#### annotation elements
- lenient boolean = if this value is 'true', parse with ignored case. optional argument for CellProsessor 'ParseEnum'

## Example @CsvStringConverter / @CsvNumberConverter / @CsvDateConverter / @CsvBooleanConverter / @CsvEnumConverter

```java
@CsvBean(header=true)
public class SampleBean1{

    @CsvColumn(position = 0, label="数字")
    @CsvNumberConverter(min="101.0", max="200.5")
    private float float0;
    
    @CsvColumn(position = 1, optional=true)
    @CsvNumberConverter(pattern="###,###,###", max="100,00,000")
    private BigDecimal bigDecimal1;
    
    @CsvColumn(position = 2)
    private String string2;
    
    @CsvColumn(position = 3, optional=true, inputDefaultValue="@empty")
    @CsvStringConverter(maxLength=6, contain={"abc", "bbb"})
    private String string3;
    
    @CsvColumn(position = 4)
    @CsvDateConverter(pattern="yyyy/MM/dd HH:mm", min="2000/10/30 00:00", max=2000/12/31 23:59")
    private Date date4;
    
    @CsvColumn(position = 5, optional=true)
    @CsvDateConverter(pattern="yyyy/MM/dd", min="2000/10/30")
    private Timestamp date5;
    
    @CsvColumn(position = 6, optional=true)
    @CsvBooleanConverter(inputTrueValue = {"○"}, inputFalseValue = {"×"}, outputTrueValue = "○", outputFalseValue="×")
    private Boolean bool6;
    
    @CsvColumn(position = 7, label="enum class", optional=true, inputDefaultValue="BLUE")
    @CsvEnumConveret(lenient = true)
    private Color enum7;
    
}
```
### this api build cell processos from above examples.

1. build field 'float1' CellProcessor ( @CsvColumn(position = 0, label="数字") + @CsvNumberConverter(min="101.0", max="200.5") )
``` java
 // buld for input CellProcessor
 new NotNull(new ParseFloat(new NumerRange<Float>(Float.parseFloat('101.0'), Float.parseFloat('200.5')))
 
 // buld for output CellProcessor
 new NotNull(new NumerRange<Float>(Float.parseFloat('101.0'), Float.parseFloat('200.5'))
```
2. build field 'bigDecimal2' Cell Processor (  @CsvColumn(position = 1, optional=true) + @CsvNumberConverter(pattern="###,###,###", max="100,00,000") )
```java
 // buld for input CellProcessor
 new Optional(new ParseBigDecimal("###,###,###",  new Max<Float>(bigdecimal obj('101.0', "###,###,###")))
 
 // buld for output CellProcessor
 new Optional(new Max<BigDecimal>(bigdecimal obj('101.0', "###,###,###"),  NumberLocaleFormat("###,###,###")))
```

3. build field 'bigDecimal3' Cell Processor ( @CsvColumn(position = 3, optional=true, inputDefaultValue="@empty") + @CsvStringConverter(maxLength=6, contain={"abc", "bbb"}) )
```java
 // buld for input CellProcessor
 new ConvertNullTo("", new Optional(new MaxLength(6, new RequiredSubStr(new String{"abc", "bbb"})))
 
 // buld for output CellProcessor
 new ConvertNullTo("", new Optional(new MaxLength(6, new RequiredSubStr(new String{"abc", "bbb"})))
```

4. build field 'date4' Cell Processor ( @CsvColumn(position = 4) + @CsvDateConverter(pattern="yyyy/MM/dd HH:mm", min="2000/10/30 00:00", max=2000/12/31 23:59") )
```java
 // buld for input CellProcessor
 new NotNull(ParseLocaleDate("yyyy/MM/dd HH:mm", new DateRange(data obj('2000/10/30 00:00'), date obj('2000/12/31 23:59'))))
 
 // buld for output CellProcessor
 new NotNull(new DateRange(data obj('2000/10/30 00:00'), date obj('2000/12/31 23:59')), new FormatLocaleDate("yyyy/MM/dd HH:mm"))
```

5. build field 'date5' Cell Processor ( @CsvColumn(position = 5, optional=true) + @CsvDateConverter(pattern="yyyy/MM/dd", min="2000/10/30")
 )
```java
 // buld for input CellProcessor
 new Optional(ParseLocaleDate("yyyy/MM/dd", new Future(data obj('2000/10/30'))))
 
 // buld for output CellProcessor
 new Optional(new Future(data obj('2000/10/30')), new FormatLocaleDate("yyyy/MM/dd"))
```

6. build field 'bool6' Cell Processor ( @CsvColumn(position = 6, optional=true) + @CsvBooleanConverter(inputTrueValue = {"○"}, inputFalseValue = {"×"}, outputTrueValue = "○", outputFalseValue="×")
    )
```java
 // buld for input CellProcessor
 new Optional( ParseBoolean(new String[]{"○"}, new String[]{"×"}))
 
 // buld for output CellProcessor
 new Optional( new FmtBool("○", "×"))
```

7. build field 'enum7' Cell Processor ( @CsvColumn(position = 7, label="enum class", optional=true, inputDefaultValue="BLUE") + @CsvEnumConveret(lenient = true)
    )
```java
 // buld for input CellProcessor
 new ConvertNullTo(, new Optional(pase enum('BLUE'), ParseEnum(false))
 
 // buld for output CellProcessor
 new Optional()
```


## Sample Writer

###  use CsvBeanWriter
```java
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
```

### use CsvAnnotationBeanWriter (custom class)
```java
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
```

## Sample Reader
### use CsvBeanReader
```java
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
```

### use CsvAnnotationBeanReader (custom class)
```java
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
```

## Sample Localize Message

```java
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
```

### Custoize messages.
### set message resolver.

```java
CsvExceptionConveter exceptionConveter = new CsvExceptionConveter();
MessageConverter messageConverter = new MessageConverter();
messageConverter.setMessageResolver(new ResourceBundleMessageResolver(... your resource bundle))
```

#### message example (org/supercsv/ext/SuperCsvMessages.properties)
```
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
```
 


