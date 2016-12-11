package com.github.mygreen.supercsv.cellprocessor.format;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

/**
 * {@link BooleanFormatter}のテスタ。
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
public class BoolenFormatterTest {
    
    private BooleanFormatter formatter;
    private BooleanFormatter customValueFormatter;
    private BooleanFormatter customValueIgnoreCaseFormatter;
    
    private String[] readCustomTrues = {"○", "レ", "Ok"};
    private String[] readCustomFalses = {"×", "ー", "Cancel", ""};
    
    private String writeCustomTrues = "○";
    private String writeCustomFalses = "×";
    
    private static final String VALIDATION_MESSAGE = "値は「{joindedTrueValues}, {joindedFalseValues}」の何れかである必要があります。";
    
    @Before
    public void setUp() throws Exception {
        this.formatter = new BooleanFormatter();
        
        this.customValueFormatter = new BooleanFormatter(readCustomTrues, readCustomFalses,
                writeCustomTrues, writeCustomFalses, false, false);
        
        this.customValueIgnoreCaseFormatter = new BooleanFormatter(readCustomTrues, readCustomFalses,
                writeCustomTrues, writeCustomFalses, true, true);
    }
    
    @Test
    public void testParse() {
        assertThat(formatter.parse("true"), is(true));
        assertThat(formatter.parse("false"), is(false));
        
    }
    
    @Test(expected=TextParseException.class)
    public void testParse_inputNull() {
        formatter.parse(null);
        fail();
    }
    
    @Test(expected=TextParseException.class)
    public void testParse_inputEmpty() {
        formatter.parse("");
        fail();
    }
    
    @Test(expected=TextParseException.class)
    public void testParse_fail() {
        formatter.parse("True");
        fail();
    }
    
    @Test
    public void testParseWithCustomValue() {
        assertThat(customValueFormatter.parse("○"), is(true));
        assertThat(customValueFormatter.parse("×"), is(false));
    }
    
    @Test(expected=TextParseException.class)
    public void testParseWithCustomValue_fail() {
        customValueFormatter.parse("OK");
    }
    
    @Test
    public void testParseWithCustomValueIgnoreCase() {
        assertThat(customValueIgnoreCaseFormatter.parse("OK"), is(true));
        assertThat(customValueIgnoreCaseFormatter.parse("CANCEL"), is(false));
        
        // failToFalse
        assertThat(customValueIgnoreCaseFormatter.parse("aaaa"), is(false));
    }
    
    @Test
    public void testPrint() {
        assertThat(formatter.print(true), is("true"));
        assertThat(formatter.print(false), is("false"));
    }
    
    @Test
    public void testPrintWithCustomValue() {
        assertThat(customValueFormatter.print(true), is("○"));
        assertThat(customValueFormatter.print(false), is("×"));
    }
    
    @Test
    public void testSetAndGetValidationMessage() {
        
        // default (no set)
        assertThat(formatter.getValidationMessage().isPresent(), is(false));
        
        // set
        formatter.setValidationMessage(VALIDATION_MESSAGE);
        assertThat(formatter.getValidationMessage().get(), is(VALIDATION_MESSAGE));
        
        
    }
    
    @Test
    public void testGetMessageVariables() {
        
        Map<String, Object> vars = customValueFormatter.getMessageVariables();
        
        assertThat(vars, hasKey("trueValues"));
        assertThat(vars, hasKey("falseValues"));
        
        assertThat(vars, hasEntry("ignoreCase", false));
        assertThat(vars, hasEntry("failToFalse", false));
        
    }
    
}
