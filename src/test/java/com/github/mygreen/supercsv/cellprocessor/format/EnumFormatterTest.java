package com.github.mygreen.supercsv.cellprocessor.format;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import org.junit.Before;
import org.junit.Test;

import com.github.mygreen.supercsv.cellprocessor.format.EnumFormatter;
import com.github.mygreen.supercsv.cellprocessor.format.TextParseException;

/**
 * {@link EnumFormatter}のテスタ
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class EnumFormatterTest {
    
    private EnumFormatter<TestEnum> formatter;
    private EnumFormatter<TestEnum> selectorFormatter;
    private EnumFormatter<TestEnum> ignoreCaseFormatter;
    private EnumFormatter<TestEnum> ignoreCaseSelectorFormatter;
    
    public enum TestEnum {
        Red("赤(RED)"), Blue("青(BLUE)"), Yellow("黄(Yellow)");
        
        final String aliasName;
        
        private TestEnum(String aliasName) {
            this.aliasName = aliasName;
        }
        
        public String aliasName() {
            return aliasName;
        }
    }
    
    @Before
    public void setUp() throws Exception {
        
        this.formatter = new EnumFormatter<TestEnum>(TestEnum.class);
        this.selectorFormatter = new EnumFormatter<>(TestEnum.class, "aliasName");
        this.ignoreCaseFormatter = new EnumFormatter<>(TestEnum.class, true);
        this.ignoreCaseSelectorFormatter = new EnumFormatter<>(TestEnum.class, true, "aliasName");
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testConstructorAtTypeNull() {
        new EnumFormatter<>(null);
        fail();
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorAtNotExistSelector() {
        new EnumFormatter<>(TestEnum.class, "aaa");
        fail();
    }
    
    @Test
    public void testGetType() {
        
        assertThat(formatter.getType(), equalTo(TestEnum.class));
        
    }
    
    @Test
    public void testIsIgnoreCase() {
        
        assertThat(formatter.isIgnoreCase(), is(false));
        assertThat(selectorFormatter.isIgnoreCase(), is(false));
        assertThat(ignoreCaseFormatter.isIgnoreCase(), is(true));
        assertThat(ignoreCaseSelectorFormatter.isIgnoreCase(), is(true));
        
    }
    
    @Test
    public void testGetSelectorMethod() {
        
        assertThat(formatter.getSelectorMethod().isPresent(), is(false));
        assertThat(selectorFormatter.getSelectorMethod().isPresent(), is(true));
        assertThat(ignoreCaseFormatter.getSelectorMethod().isPresent(), is(false));
        assertThat(ignoreCaseSelectorFormatter.getSelectorMethod().isPresent(), is(true));
        
    }
    
    @Test
    public void testParse() {
        
        assertThat(formatter.parse("Blue"), is(TestEnum.Blue));
        
    }
    
    @Test(expected = TextParseException.class)
    public void testParse_fail() {
        
        formatter.parse("bLuE");
        
    }
    
    @Test
    public void testParseWithSelector() {
        
        assertThat(selectorFormatter.parse("青(BLUE)"), is(TestEnum.Blue));
        
    }
    
    @Test(expected = TextParseException.class)
    public void testParseWithSelector_wrong() {
        
        selectorFormatter.parse("青(bLuE)");
        
    }
    
    @Test
    public void testParseWithIgnoreCase() {
        
        assertThat(ignoreCaseFormatter.parse("bLuE"), is(TestEnum.Blue));
        
    }
    
    @Test
    public void testParseWithIgnoreCaseSelector() {
        
        assertThat(ignoreCaseSelectorFormatter.parse("青(bLuE)"), is(TestEnum.Blue));
        
    }
    
    @Test
    public void testPrint() {
        
        assertThat(formatter.print(TestEnum.Blue), is("Blue"));
    }
    
    @Test
    public void testPrintWithSelector() {
        
        assertThat(selectorFormatter.print(TestEnum.Blue), is("青(BLUE)"));
    }
    
    @Test
    public void testPrintWithIgnoreCase() {
        
        assertThat(ignoreCaseFormatter.print(TestEnum.Blue), is("Blue"));
        
    }
    
    @Test
    public void testPrintWithIgnoreCaseSelector() {
        
        assertThat(ignoreCaseSelectorFormatter.print(TestEnum.Blue), is("青(BLUE)"));
        
    }
}
