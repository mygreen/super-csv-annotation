--------------------------------------------------------
Javaシステムプロパティによる設定
--------------------------------------------------------

Javaシステムプロパティによる設定可能な項目一覧を下記に示します。

javaシステムプロパティは、JVMの起動パラメータ ``-Dxxx=yyy`` または、 ``System.setProperty("xxx", "yyy")`` で指定します。

.. list-table:: Javaシステムプロパティによる設定項目一覧
    :widths: 30 70
    :header-rows: 1

  * - プロパティ名
    - 説明
     
  * - | *supercsv.annotation.jexlRestricted*
      | *[2.4+]*
    - | ``ExpressionLanguageJEXLImpl`` にて、JEXLを Restrict パーミッションでEL式を評価するか指定します。
      | デフォルトは `true` で、`JexlPermissions.RESTRICTED <https://commons.apache.org/proper/commons-jexl/apidocs/org/apache/commons/jexl3/introspection/JexlPermissions.html#RESTRICTED>`_ でEL式が評価されます。
      | 値を `false` に指定すると、`JexlPermissions.UNRESTRICTED <https://commons.apache.org/proper/commons-jexl/apidocs/org/apache/commons/jexl3/introspection/JexlPermissions.html#UNRESTRICTED>`_ が設定され、EL式が制限なく評価されますが、
      | ただし、ELインジェクションの脆弱性に繋がる可能性があるので注意してください。

  * - | *supercsv.annotation.jexlPermissions*
      | *[2.4+]*
    - | ``ExpressionLanguageJEXLImpl`` にて、JEXLを評価する際のパーミッションを指定します。
      | EL式中で実行／参照可能なパッケージを指定し、複数指定するときはカンマ(`,`)区切りで指定します。
      | 例 . `sample1.*,sample2.core.*`。
      | このプロパティは、 ``supercsv.annotation.jexlRestricted`` の値が `true` のときにおいて、Restrictパーミッションで実行されるときのみ有効になります。
