[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.mygreen/super-csv-annotation/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.mygreen/super-csv-annotation/)
 [![Javadocs](http://javadoc.io/badge/com.github.mygreen/super-csv-annotation.svg?color=blue)](http://javadoc.io/doc/com.github.mygreen/super-csv-annotation) [![Build Status](https://github.com/mygreen/super-csv-annotation/actions/workflows/verify.yml/badge.svg)](https://travis-ci.org/mygreen/super-csv-annotation)

Super CSV Annotation
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

1. Add dependency for Super Csv Annotation
    ```xml
    <dependency>
        <groupId>com.github.mygreen</groupId>
        <artifactId>super-csv-annotation</artifactId>
        <version>2.3</version>
    </dependency>
    ```
2. Add dependency for Logging library. Example Logback.
    ```xml
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.11</version>
        <scope>test</scope>
    </dependency>
    ```

# Build

1. Setup Java SE 8 (1.8.0_121+)
2. Setup Maven
3. Setup Sphinx (building for manual)
    1. install Python
    2. install sphinx and theme for read the docs, janome
    ```console
    # pip install sphinx
    # pip install sphinx_rtd_theme --upgrade
    # pip install janome
    ```
4. Build with Maven
    1. make jar files.
    ```console
    # mvn clean package
    ```
    2. generate site.
    ```console
    # mvn site -Dgpg.skip=true
    ```

# Document
- Project infomation
  - http://mygreen.github.io/super-csv-annotation/index.html
- Manual
  - http://mygreen.github.io/super-csv-annotation/sphinx/index.html
- Javadoc
  - http://mygreen.github.io/super-csv-annotation/apidocs/index.html
  - http://javadoc.io/doc/com.github.mygreen/super-csv-annotation/
