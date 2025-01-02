package test.external;

import java.time.LocalDate;

import com.github.mygreen.supercsv.annotation.CsvBean;
import com.github.mygreen.supercsv.annotation.CsvColumn;
import com.github.mygreen.supercsv.annotation.constraint.CsvLengthMax;
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.annotation.format.CsvDateTimeFormat;
import com.github.mygreen.supercsv.annotation.format.CsvEnumFormat;
import com.github.mygreen.supercsv.annotation.format.CsvFormat;

import test.external.PostalCode.PostalCodeFormatter;

/**
 * サンプルのCSV。
 * <p>外部パッケージの確認用。
 *
 * @author T.TSUCHIE
 *
 */
@CsvBean(header = true)
public class ExternalCsv {
    
    public static enum Role {
        Admin, Developer, Repoter
    }
    
    @CsvRequire
    @CsvColumn(number = 1, label = "ID")
    private Long id;
    
    @CsvRequire
    @CsvLengthMax(20)
    @CsvColumn(number = 2, label = "名前")
    private String name;
    
    @CsvColumn(number=3, label="入社日付")
    @CsvDateTimeFormat(pattern="uuuu/MM/dd")   // 日時の書式を指定する
    private LocalDate joinedDate;
    
    @CsvColumn(number=4, label="ロール")
    @CsvEnumFormat(ignoreCase = true)
    private Role role;
    
    @CsvColumn(number=5, label="郵便番号")
    @CsvFormat(formatter=PostalCodeFormatter.class, message="[{rowNumber}行, {columnNumber}列] : 項目「{label}」の値（${empty(printer) ? validatedValue : printer.print(validatedValue)}）は、郵便番号の形式として不正です。")
    private PostalCode postalCode;
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LocalDate getJoinedDate() {
        return joinedDate;
    }
    
    public void setJoinedDate(LocalDate joinedDate) {
        this.joinedDate = joinedDate;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }

    public PostalCode getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(PostalCode postalCode) {
        this.postalCode = postalCode;
    }
    
}
