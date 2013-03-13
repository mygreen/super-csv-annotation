super-csv-annotation
====================

'Super CSV' extention library for annotation
+ this library automatic building for CellProcessor from Annotation with JavaBean.
+ and simply showing localized messages.

# Depends
------------------------------
+ JDK1.6+
    - (SuperCSV2.x is JDK1.5+, but this library )
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

###  @CsvColumn annotate Field type.
set for public / private / protected field.

#### annotation elements
- position : int = rqeuired argument. column index start with zero(0). 
- label : String = header label. if empty, use field name.
- optional : boolean = set CellProcessor NotNull(false) / Optional(true). default false.
- trim : boolean = if set true, set CellProcessor Trim()
- inputDefaultValue : String = if set this values, then reading to set for CellProcessor 'ConvertNullTo'. 
    - if field type String class, empty value as '@empty'.
- outputDefaultValue : String = if set this values, then writing to set for CellProcessor 'ConvertNullTo'. 
    - if field type String class, empty value as '@empty'.
- unique : boolean = constricting option. check the value for unique. if set the true, reading/wriing to set CellProcessor 'Unique()'.
- equalsValue : String = constricting option. check the value for equals. if set the value, reading/wriing to set CellProcessor 'Equals()'.
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
       RED, BLUE, GREEN, YELLOW;
    }
}
```

#### this api build CellProcessor from above examples.

0. build CellProcessor for field 'integer1' with int. 
    - ( @CsvColumn(position = 0, optional = true) )
```java
 // build CellProcessor for reading
 new Optional(new ParseInt())
 
 // build CellProcessor for writing
 new Optional()
```
1. build CellProcessor for field 'integer2' with java.lang.Integer.
    - ( @CsvColumn(position = 1, optional = false, unique = true) )
```java
 // build CellProcessor for reading
  new NotNull(new Unique(new ParseInt()))
 
 // build CellProcessor for writing
  new NotNull(new Unique())
```
2. build CellProcessor for field 'string3' with java.lang.String.
    - ( @CsvColumn(position = 2, optional = true, trim = true, inputDefaultValue="aa") )
```java
 // build CellProcessor for reading
  new ConvertNullTo('a', new Optional(new Trim()))
 
 // build CellProcessor for writing
  new Optional(new Trim())
```
3. build CellProcessor for field 'date4' with java.util.Date.
    - ( @CsvColumn(position = 3, ,outputDefaultValue="2012-10-13 00:00:00") )
```java
 // build CellProcessor for reading
 // use default pattern 'yyyy-MM-dd HH:mm:ss'
  new NotNull(new ParseLocaleDate("yyyy-MM-dd HH:mm:ss"))
 
 // build CellProcessor for writing
 // use default pattern 'yyyy-MM-dd HH:mm:ss'
  new NotNull(ConvertNullTo("2012-10-13 00:00:00", new FormatLocaleDate("yyyy-MM-dd HH:mm:ss")))
```
4. build CellProcessor for field 'bool5' with boolean.
    - ( @CsvColumn(position = 4, inputDefaultValue="false" )
```java
 // build CellProcessor for reading
  new ConvertNullTo( /*boolean obj('false')*/, new NotNull( new ParseBoolean()))
 
 // build CellProcessor for writing
  new NotNull( new FmtBool())
```
5. build CellProcessor for field 'enum6' with Enum class.
    - ( @CsvColumn(position = 5, inputDefaultValue="RED", outputDefualtValue="BLUE") )
```java
 // build CellProcessor for reading
  new ConvertNullTo(/*enum obj('RED')*/, new NotNull(new ParseEnum()))
 
 // build CellProcessor for writing
  new ConvertNullTo("BLUE", new NotNull())
```


## Optional Annotation

### @CsvStringConverter is setting for String class.
this annotation for String classes.

#### annotation elements
- minLength : int = constricting the minimum character long. set CellProcessor 'MinLength' (custom processor).
- maxLength int = constricting the maximum character long. set CellProcessor 'MaxLength' (custom processor).
    - if minLength > 0 and maxLength >0, set CellProcessor 'StrMinMax'.
- exactlength : int = constain the equals character long. set CellProcessor 'Strlen'.
- regex : String = constricting the reqular expression pattern. set CellProcessor 'StrRegEx'.
- forbid : String[] = constricting the not contain fobbien substring. set CellProcessor 'ForbidSubStr'.
- contain : String[] = constricting the contain substirng. set CellProcessor 'RequireSubStr'.
- notEmpty : boolean = constricting the not empty. set CellProcesor 'StrNotNullOrEmpty'

### @CsvNumberConverter is setting for number classes. 
this annotation for number classes:
- byte/shortint/long/float/double/Byte/Integer/Long/Float/Double/BigDecimal/BigInteger 

#### annotation elements
- pattern : String = Number format pattern. set CellProcessor 'FormatLocaleNumber' (custom processor).
    - if empty, parse for Number object parse method. ex) Integer.parseInt(...), Double.parseDouble(...).
- lenient : boolean = Paring from string to Number object non-exactly.
    - optional argument for CellProcessor 'FormatLocaleNumber'.
- currency : String = Code(ISO 4217 Code). optional argument for CellProcessor 'FormatLocaleNumber'.
- language : String = Locale with language. optional argument for CellProcessor 'FormatLocaleNumber'.
- country : String = Locale with country. optional argument for CellProcessor 'FormatLocaleNumber'.
- min : String = constricting the mininum value. set CellProcessor 'Min' (custom processor)
    - if build for input processor, parse this value by element 'pattern'.
- max : String = String = constricting the maximum value. set CellProcessor 'Max' (custom processor)
    - if min != "" and max != "", set CellProcessor 'NumberRange' (custom processor)
    - if build input processor, parse this value by element 'pattern'.

### @CsvDateConverter is setting for date class.
this annotation for date classes : java.util.Date / java.sql.Date / java.sql.Time / java.sql.Timestamp 

#### annotation elements
- pattern : String = Date format pattern. set CellProcessor 'FormatLocaleDate / ParseLocaleDate' (custom processor).
- lenient : boolean = parse string to Date object non-exactly. optional argument for CellProcessor 'FormatLocaleDate / ParseLocaleDate'.
- timezone : String = optional argument for CellProcessor 'FormatLocaleDate / ParseLocaleDate'.
- language : String = Locale with language. optional argument for CellProcessor 'FormatLocaleDate / ParseLocaleDate'.
- country : String = Locale with country. optional argument for CellProcessor 'FormatLocaleDate / ParseLocaleDate'.
- min : String = constricting the mininum value. set CellProcessor 'FutrueDate' (custom processor)
    - if build for input processor, parse this value by element 'pattern'.
- max : String = constricting the maximum value. set CellProcessor 'PastDate' (custom processor)
    - if min != "" and max != "", set CellProcessor 'DateRange' (custom processor)
    - if build for input processor, parse this value by element 'pattern'.

### @CsvBooleanConverter is seting for boolean class.
this annotation for Boolean classes : boolean / Boolean.

#### annotation elements
- inputTrueValues : String[] = parse string as true value. set CellProcessor 'ParseBoolean' (custom processor)
- inputFalseValues : String[] = parse string as false value. set CellProcessor 'ParseBoolean' (custom processor)
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
    @CsvStringConverter(minLength=5, regex="[a-zA-Z]{5,8}")
    private String string2;
    
    @CsvColumn(position = 3, optional=true, inputDefaultValue="@empty")
    @CsvStringConverter(maxLength=6, contain={"abc", "bbb"})
    private String string3;
    
    @CsvColumn(position = 4)
    @CsvDateConverter(pattern="yyyy/MM/dd HH:mm", min="2000/10/30 00:00", max="2000/12/31 23:59")
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

1. build CellProcessor field 'float1' with float type.
    - ( @CsvColumn(position = 0, label="数字") + @CsvNumberConverter(min="101.0", max="200.5") )
``` java
 // build CellProcessor for reading
 new NotNull(new ParseFloat(new NumerRange<Float>(/*float obj('101.0')*/, /**float obj('200.5')*/))
 
 // build CellProcessor for writing
 new NotNull(new NumerRange<Float>(/**float obj("101.0")*/, /**float obj('200.5')*/)
```
2. build CellProcessor for field 'bigDecimal1' with BigDecimal.
    - (  @CsvColumn(position = 1, optional=true) + @CsvNumberConverter(pattern="###,###,###", max="100,00,000") )
```java
 // build CellProcessor for reading
 new Optional(new ParseBigDecimal("###,###,###",  new Max<BigDecimal>(/*big decimal obj('100,00,000')*/))
 
 // build CellProcessor for writing
 new Optional(new Max<BigDecimal>(/*big decimal obj('100,00,000')*/,  NumberLocaleFormat("###,###,###")))
```
3. build CellProcessor for field 'string2' with String.
    - ( @CsvColumn(position = 2) + @CsvStringConverter(minLength=5, maxLength=8, regex="[a-zA-Z]*") )
```java
 // build CellProcessor for reading
 new NotNull(new StrRegEx("[a-zA-Z]*",  new Length(5, 8))
 
 // build CellProcessor for writing
 new NotNull(new StrRegEx("[a-zA-Z]*",  new Length(5, 8))
```
4. build CellProcessor for field 'string3' with String.
    - ( @CsvColumn(position = 3, optional=true, inputDefaultValue="@empty") + @CsvStringConverter(maxLength=6, contain={"abc", "bbb"}) )
```java
 // build CellProcessor for reading
 new ConvertNullTo("", new Optional(new MaxLength(6, new RequiredSubStr(new String{"abc", "bbb"})))
 
 // build CellProcessor for writing
 new ConvertNullTo("", new Optional(new MaxLength(6, new RequiredSubStr(new String{"abc", "bbb"})))
```
5. build CellProcessor for field 'date4' with java.util.Date.
    - ( @CsvColumn(position = 4) + @CsvDateConverter(pattern="yyyy/MM/dd HH:mm", min="2000/10/30 00:00", max=2000/12/31 23:59") )
```java
 // build CellProcessor for reading
 new NotNull(new ParseLocaleDate("yyyy/MM/dd HH:mm", new DateRange(/*data obj('2000/10/30 00:00')*/, /*date obj('2000/12/31 23:59')*/)))
 
 // build CellProcessor for writing
 new NotNull(new DateRange(/*data obj('2000/10/30 00:00')*/, /*date obj('2000/12/31 23:59')*/), new FormatLocaleDate("yyyy/MM/dd HH:mm"))
```
5. build CellProcessor for field 'date5' with java.sql.Timestamp
    - ( @CsvColumn(position = 5, optional=true) + @CsvDateConverter(pattern="yyyy/MM/dd", min="2000/10/30")
 )
```java
 // build CellProcessor for reading
 new Optional(new ParseLocaleDate("yyyy/MM/dd", new Future(/*data obj('2000/10/30')*/)))
 
 // build CellProcessor for writing
 new Optional(new Future(/*data obj('2000/10/30')*/), new FormatLocaleDate("yyyy/MM/dd"))
```
6. build CellProcessor for field 'bool6' with java.lang.Boolean
    - ( @CsvColumn(position = 6, optional=true) + @CsvBooleanConverter(inputTrueValue = {"○"}, inputFalseValue = {"×"}, outputTrueValue = "○", outputFalseValue="×")
    )
```java
 // build CellProcessor for reading
 new Optional( new ParseBoolean(new String[]{"○"}, new String[]{"×"}))
 
 // build CellProcessor for writing
 new Optional( new FmtBool("○", "×"))
```
7. build CellProcessor for field 'enum7' with Enum type.
    - ( @CsvColumn(position = 7, label="enum class", optional=true, inputDefaultValue="BLUE") + @CsvEnumConveret(lenient = true)
    )
```java
 // build CellProcessor for reading
 new ConvertNullTo(/*parse enum('BLUE')*/, new Optional(new ParseEnum(false))
 
 // build CellProcessor for writing
 new Optional()
```


## Sample for writing

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

## Sample for readingr
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

## Sample for showing  the localized message
### use ValidatableCsvBeanReader (custom class)

```java
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
 


