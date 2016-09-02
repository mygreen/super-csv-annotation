package org.supercsv.ext.io;


/**
 * テスト用の列挙型
 * 
 * @since 1.2
 * @author T.TSUCHIE
 *
 */
public enum SampleEnum {
    
    RED("赤"), BLUE("青"), YELLOW("黄");
    
    final String aliasName;
    
    private SampleEnum(String aliasName) {
        this.aliasName = aliasName;
    }
    
    public String aliasName() {
        return aliasName;
    }
    
    
}
