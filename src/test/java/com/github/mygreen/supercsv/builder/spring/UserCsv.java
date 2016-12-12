package com.github.mygreen.supercsv.builder.spring;

import java.net.URL;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.annotation.format.CsvFormat;
import com.github.mygreen.supercsv.validation.beanvalidation.CsvBeanValidator;

@CsvBean(header=true, validators=CsvBeanValidator.class, listeners=UserListener.class)
public class UserCsv {
    
    @CsvColumn(number=1)
    @CsvRequire
    private String id;
    
    // Spring管理のCellProcessorFactoryを指定する場合
    @CsvColumn(number=2, label="名前")
    @CsvUserNameExist
    private String name;
    
    // Spring管理のBuilderを指定する場合
    @CsvColumn(number=3, label="ホームページ", builder=UrlProcessorBuilder.class)
    private URL homepage;
    
    // SrpingBean管理のFormatterを指定する場合。メッセージを独自に指定して、prototypeスコープの確認を行う
    @CsvColumn(number=4, label="ホームページ（予備）")
    @CsvFormat(formatter=UrlFormatter.class, message="{csvContext} : 項目「{label}」の値（{validatedValue}）は、サブのホームページの値として不正です。")
    private URL homepageSub;
    
    // BeanValidationでかつSpringのインジェクションを利用する場合
    @CsvColumn(number=5, label="E-mail")
    @UserMailPattern
    private String email;
    
    public UserCsv id(final String id) {
        this.id = id;
        return this;
    }
    
    public UserCsv name(final String name) {
        this.name = name;
        return this;
    }
    
    public UserCsv homepage(final URL homepage) {
        this.homepage = homepage;
        return this;
    }
    public UserCsv homepageSub(final URL homepageSub) {
        this.homepageSub = homepageSub;
        return this;
    }
    public UserCsv email(final String email) {
        this.email = email;
        return this;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public URL getHomepage() {
        return homepage;
    }
    
    public void setHomepage(URL homepage) {
        this.homepage = homepage;
    }
    
    public URL getHomepageSub() {
        return homepageSub;
    }
    
    public void setHomepageSub(URL homepageSub) {
        this.homepageSub = homepageSub;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
}
