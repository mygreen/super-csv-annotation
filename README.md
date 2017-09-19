Supre CSV Annotation
====================

This library 'Super CSV' extension library with annotation function.
+ this library automatic building for CellProcessor from Annotation with JavaBean.
+ and simply showing localized messages.

# Depends
------------------------------
+ Java1.8
    - (SuperCSV2.x is Java1.6+, but this library require Java1.8)
+ SuperCSV 2.4+

# Setup

```xml
  <dependency>
    <groupId>com.github.mygreen</groupId>
    <artifactId>super-csv-annotation</artifactId>
    <version>2.1</version>
  </dependency>
```

# Build

1. Setup Java SE 8 (1.8.0_121+)
2. Setup Maven
3. Setup Sphinx (building for manual)
    a. install Python (recomend Python 2.7)
    b. install sphinx and theme for read the docs, janome
    ```console
    # pip install sphinx
    # pip install sphinx_rtd_theme --upgrade
    # pip install janome
    ```
4. Build with Maven
    ```console
    # mven site -Dgpg.skip=true
    ```

# Document
+ Project infomation
 + http://mygreen.github.io/super-csv-annotation/index.html
+ Manual
 + http://mygreen.github.io/super-csv-annotation/sphinx/index.html
+ Javadoc
 + http://mygreen.github.io/super-csv-annotation/apidocs/index.html
