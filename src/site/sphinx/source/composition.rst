======================================
アノテーションの合成
======================================

既存のアノテーションを組み合わせて、容易にアノテーションを作成することができます。

この機能は、同じアノテーションの組み合わせを他の多くのフィールドに設定したくないときに利用します。

--------------------------------------------------------
アノテーションの合成の基本
--------------------------------------------------------

合成可能なアノテーションは決まっており、 :doc:`書式の指定用 <format>` 、 :doc:`値の変換用 <conversion>` 、 :doc:`値の検証用 <validation>` の3種のアノテーションです。
カラム指定用の ``@CsvColumn`` などは合成できません。


* ``@Target`` として、``ElementType.FIELD`` と ``ElementType.ANNOTATION_TYPE`` の2つを指定します。

  * 通常はFieldのみで問題ないですが、 さらに合成するときがあるため、 *ANNOTATION_TYPE* も追加しておきます。

* ``@Repeatable`` として、複数のアノテーションを設定できるようにします。

  * 内部クラスのアノテーションとして、 *List* を定義します。

* 合成したのアノテーションと示すためのメタアノテーション ``@CsvComposition`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/CsvComposition.html>`_ ]を指定します。


.. sourcecode:: java
    :linenos:
    
    import java.lang.annotation.Annotation;
    import java.lang.annotation.Documented;
    import java.lang.annotation.ElementType;
    import java.lang.annotation.Repeatable;
    import java.lang.annotation.Retention;
    import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;
    
    import com.github.mygreen.supercsv.annotation.CsvComposition;
    import com.github.mygreen.supercsv.annotation.constraint.*;
    import com.github.mygreen.supercsv.annotation.conversion.*;
    import com.github.mygreen.supercsv.annotation.format.*;
    
    
    // 合成したアノテーション
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(CsvSalary.List.class)
    @CsvComposition                                      // 合成のアノテーションを表現するために指定します。
    @CsvNumberFormat(pattern="#,##0")                   // 書式の指定用のアノテーション
    @CsvDefaultValue(value="0", groups=ReadGroup.class)  // 値の変換用のアノテーション
    @CsvRequire                                          // 値の検証用のアノテーション（必須チェック）
    @CsvNumberRange(min="0", max="100,000,000" groups=NormalGroup.class)          // 値の検証用のアノテーション（範囲チェック）
    @CsvNumberRange(min="0", max="100,000,000,000", groups=ManagerGroup.class)    // 値の検証用のアノテーション（範囲チェック）
    public @interface CsvSalary {
        
        // 繰り返しのアノテーションの格納用アノテーションの定義
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            CsvSalary[] value();
        }
    }


使用する際には、他のアノテーションと同様にフィールドに付与します。

.. sourcecode:: java
    :linenos:

    import com.github.mygreen.supercsv.annotation.CsvBean;
    import com.github.mygreen.supercsv.annotation.CsvColumn;
    
    @CsvBean
    public class SampleCsv {
        
        @CsvColumn(number=1)
        @CsvSalary
        private Integer salary;
        
        // getter/setterは省略
    }


--------------------------------------------------------
属性の上書き
--------------------------------------------------------

合成したアノテーションに対して、一部の属性値を可変にしたい場合は、アノテーション ``@CsvOverridesAttribute`` [ `JavaDoc <../apidocs/com/github/mygreen/supercsv/annotation/CsvOverridesAttribute.html>`_ ]を使用します。

* 属性 ``annotation`` で上書き対象のアノテーションを指定し、属性 ``name`` で属性名を指定します。
* アノテーション ``@CsvOverridesAttribute`` を複数付与することで、1つの属性で複数の属性を上書きすることができます。

.. sourcecode:: java
    :linenos:
    
    import java.lang.annotation.Annotation;
    import java.lang.annotation.Documented;
    import java.lang.annotation.ElementType;
    import java.lang.annotation.Repeatable;
    import java.lang.annotation.Retention;
    import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;
    
    import com.github.mygreen.supercsv.annotation.CsvComposition;
    import com.github.mygreen.supercsv.annotation.CsvOverridesAttribute;
    import com.github.mygreen.supercsv.annotation.constraint.*;
    import com.github.mygreen.supercsv.annotation.conversion.*;
    import com.github.mygreen.supercsv.annotation.format.*;
    
    
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(CsvSalary.List.class)
    @CsvComposition
    @CsvNumberFormat(pattern="#,##0")
    @CsvDefaultValue(value="0", groups=ReadGroup.class)  // 上書き対象のアノテーション
    @CsvRequire                                          // 上書き対象のアノテーション
    @CsvNumberRange(min="0", max="100,000,000", groups=NormalGroup.class)
    @CsvNumberRange(min="0", max="100,000,000,000", groups=ManagerGroup.class)
    public @interface CsvSalary {
        
        // @CsvDefaultValueの属性valueの上書き
        @CsvOverridesAttribute(annotation=CsvDefaultValue.class, name="value")
        String defaultValueRead();
        
        // @CsvRequireの属性considerBlankとconsiderEmptyの上書き
        @CsvOverridesAttribute(annotation=CsvRequire.class, name="considerBlank")
        @CsvOverridesAttribute(annotation=CsvRequire.class, name="considerEmpty")
        boolean considerSpace() default true;
        
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            CsvSalary[] value();
        }
    }


上書き対象のアノテーション自体が複数付与されている場合、区別するために ``@CsvOverridesAttribute(index=<インデックス>)`` で指定します。

* 属性 ``index`` は0から始まります。
* インデックスを指定しない場合は、該当するアノテーションの属性が全て上書きされます。


.. sourcecode:: java
    :linenos:
    
    import java.lang.annotation.Annotation;
    import java.lang.annotation.Documented;
    import java.lang.annotation.ElementType;
    import java.lang.annotation.Repeatable;
    import java.lang.annotation.Retention;
    import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;
    
    import com.github.mygreen.supercsv.annotation.CsvComposition;
    import com.github.mygreen.supercsv.annotation.CsvOverridesAttribute;
    import com.github.mygreen.supercsv.annotation.constraint.*;
    import com.github.mygreen.supercsv.annotation.conversion.*;
    import com.github.mygreen.supercsv.annotation.format.*;
    
    
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(CsvSalary.List.class)
    @CsvComposition
    @CsvNumberFormat(pattern="#,##0")
    @CsvDefaultValue(value="0", groups=ReadGroup.class)
    @CsvRequire
    @CsvNumberRange(min="0", max="100,000,000", groups=NormalGroup.class)       // 1番目（index=0）のアノテーション
    @CsvNumberRange(min="0", max="100,000,000,000", groups=ManagerGroup.class) // 2番目（index=1）のアノテーション
    public @interface CsvSalary {
        
        // 2番目（インデックスが1）の@CsvNumberRangeの属性maxの上書き
        @CsvOverridesAttribute(annotation=CsvNumberRange.class, name="max", index=1)
        String managerSalaryMax() default "100,000,000,000,000";
        
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            CsvSalary[] value();
        }
    }



--------------------------------------------------------
共通の属性の上書き
--------------------------------------------------------

共通の属性である ``cases``, ``groups`` , ``message`` は、アノテーション *@CsvOverridesAttribute* が無くても上書きすることができます。

.. sourcecode:: java
    :linenos:
    
    import java.lang.annotation.Annotation;
    import java.lang.annotation.Documented;
    import java.lang.annotation.ElementType;
    import java.lang.annotation.Repeatable;
    import java.lang.annotation.Retention;
    import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;
    
    import com.github.mygreen.supercsv.annotation.CsvComposition;
    import com.github.mygreen.supercsv.annotation.CsvOverridesAttribute;
    import com.github.mygreen.supercsv.annotation.constraint.*;
    import com.github.mygreen.supercsv.annotation.conversion.*;
    import com.github.mygreen.supercsv.annotation.format.*;
    import com.github.mygreen.supercsv.builder.BuildCase;
    
    
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(CsvSalary.List.class)
    @CsvComposition
    @CsvNumberFormat(pattern="#,##0")                                           // 共通の属性messageを持つ
    @CsvDefaultValue(value="0", groups=ReadGroup.class)                          // 共通の属性groupsを持つ
    @CsvRequire                                                                  // 共通の属性message, groupsを持つ
    @CsvNumberRange(min="0", max="100,000,000", groups=NormalGroup.class)         // 共通の属性message, groupsを持つ
    @CsvNumberRange(min="0", max="100,000,000,000", groups=ManagerGroup.class)   // 共通の属性message, groupsを持つ
    public @interface CsvSalary {
        
        // 共通の属性 - エラーメッセージ
        String message() default "";
        
        // 共通の属性 - ケース
        BuildCase[] cases() default {};
        
        // 共通の属性 - グループ
        Class<?>[] groups() default {};
        
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            CsvSalary[] value();
        }
    }



もちろん、共通の属性 *cases* 、 *message* 、 *groups* も、アノテーション *@CsvOverridesAttribute* を使用して、特定のアノテーションの属性を上書きすることができます。

下記の例の場合、*@CsvOverridesAttribute* で上書きされていないアノテーションの属性 *cases* 、 *message* 、 *groups* は、共通の属性 *cases* 、 *message* 、 *groups* で上書きされます。

.. sourcecode:: java
    :linenos:
    
    import java.lang.annotation.Annotation;
    import java.lang.annotation.Documented;
    import java.lang.annotation.ElementType;
    import java.lang.annotation.Repeatable;
    import java.lang.annotation.Retention;
    import java.lang.annotation.RetentionPolicy;
    import java.lang.annotation.Target;
    
    import com.github.mygreen.supercsv.annotation.CsvComposition;
    import com.github.mygreen.supercsv.annotation.CsvOverridesAttribute;
    import com.github.mygreen.supercsv.annotation.constraint.*;
    import com.github.mygreen.supercsv.annotation.conversion.*;
    import com.github.mygreen.supercsv.annotation.format.*;
    
    
    @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Repeatable(CsvSalary.List.class)
    @CsvComposition
    @CsvNumberFormat(pattern="#,##0")                                            // 共通の属性messageを持つ
    @CsvDefaultValue(value="0", groups=ReadGroup.class)                          // 共通の属性cases, groupsを持つ
    @CsvRequire                                                                  // 共通の属性cases, message, groupsを持つ
    @CsvNumberRange(min="0", max="100,000,000", groups=NormalGroup.class)        // 共通の属性cases, message, groupsを持つ
    @CsvNumberRange(min="0", max="100,000,000,000", groups=ManagerGroup.class)   // 共通の属性cases, message, groupsを持つ
    public @interface CsvSalary {
        
        // 共通の属性 - エラーメッセージ
        String message() default "";
        
        // 2番目（index=1）の@CsvNumberRangeの属性messageの上書き
        @CsvOverridesAttribute(annotation=CsvNumberRange.class, name="message", index=1)
        String rangeMessage() default "管理者の場合の給料は、{min}～{max}の範囲内で設定してください。";
        
        // 共通の属性 - ケース
        BuildCase[] cases() default {};
        
        // 1番目（index=0）の@CsvNumberRangeの属性casesの上書き
        @CsvOverridesAttribute(annotation=CsvNumberRange.class, name="cases", index=0)
        BuildCases[] normalRangeCases() default {};
        
        // 共通の属性 - グループ
        Class<?>[] groups() default {};
        
        // 1番目（index=0）の@CsvNumberRangeの属性groupsの上書き
        @CsvOverridesAttribute(annotation=CsvNumberRange.class, name="groups", index=0)
        Class<?>[] normalRangeGroups() default {};
        
        @Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
        @Retention(RetentionPolicy.RUNTIME)
        @Documented
        @interface List {
            
            CsvSalary[] value();
        }
    }



