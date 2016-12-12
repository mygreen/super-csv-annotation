======================================
Spring Frameworkとの連携
======================================

DI(Depenency Injection) 機能のフレームワーク `Spring Framework <https://projects.spring.io/spring-framework/>`_ と連携することができます。

Spring Framework のコンテナで管理可能、DI可能な部分は、次の箇所になります。

これらの機能・箇所は、 ``BeanFactory`` によるインスタンスを新しく作成する箇所であり、その実装を ``SpringBeanFactory`` に切り替え得ることで、DIを実現します。


.. list-table:: Spring Frameworkとの連携可能な箇所
   :widths: 40 60
   :header-rows: 1
   
   * - 機能・箇所
     - 説明
     
   * - :doc:`独自の書式を指定する機能 <format_custom>`
     - ``TextFormatter`` の実装クラスがSpringBeanとして管理可能です。

   * - :doc:`独自の変換処理の実装機能 <conversion_custom>`
     - ``ConversionProcessorFactory`` の実装クラスがSpringBeanとして管理可能です。

   * - :doc:`独自のカラム値の検証の実装機能 <validation_custom>`
     - ``ConstraintProcessorFactory`` の実装クラスがSpringBeanとして管理可能です。

   * - :doc:`独自のValidatorの実装機能 <validation_validator>`
     - ``CsvValidator`` の実装クラスがSpringBeanとして管理可能です。
     
   * - :doc:`独自のリスナーの実装機能 <lifecycle_listener>`
     - リスナクラスがSpringBeanとして管理可能です。
     
   * - :doc:`独自のProcessorBuilder実装機能 <processorbuilder>`
     - ``ProcessorBuilder`` の実装クラスがSpringBeanとして管理可能です。
     
   * - :doc:`BeanValidationの連携機能 <validation_beanvalidation>`
     - *BeanValidation* の検証用の実装クラスがSpringBeanとして管理可能です。
     
   * - :doc:`エラーメッセージのカスタマイズ機能 <validation_message>`
     - ``MessageResolver`` の内容を ``MessagSource`` から参照可能です。
     


--------------------------------------------------------
ライブラリの追加
--------------------------------------------------------

Spring Frameworkを利用する際には、ライブリを追加します。
Mavenを利用している場合は、pom.xmlに以下を追加します。

Spring Frameworkのバージョンは、3.0以上を指定してください。

.. sourcecode:: xml
    :linenos:
    
    <dependency>
        <groupId>org.springframework</groupId>
        <artifactId>spring-context</artifactId>
        <version>4.3.2.RELEASE</version>
    </dependency>



.. 以降は、埋め込んで作成する
.. include::  ./spring_beanfactory.rst
.. include::  ./spring_message.rst


