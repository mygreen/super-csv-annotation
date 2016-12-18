package com.github.mygreen.supercsv.cellprocessor.format;

import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.*;

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
        assertThat(formatter.parse("true")).isEqualTo(true);
        assertThat(formatter.parse("false")).isEqualTo(false);
        
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
        assertThat(customValueFormatter.parse("○")).isEqualTo(true);
        assertThat(customValueFormatter.parse("×")).isEqualTo(false);
    }
    
    @Test(expected=TextParseException.class)
    public void testParseWithCustomValue_fail() {
        customValueFormatter.parse("OK");
    }
    
    @Test
    public void testParseWithCustomValueIgnoreCase() {
        assertThat(customValueIgnoreCaseFormatter.parse("OK")).isEqualTo(true);
        assertThat(customValueIgnoreCaseFormatter.parse("CANCEL")).isEqualTo(false);
        
        // failToFalse
        assertThat(customValueIgnoreCaseFormatter.parse("aaaa")).isEqualTo(false);
    }
    
    @Test
    public void testPrint() {
        assertThat(formatter.print(true)).isEqualTo("true");
        assertThat(formatter.print(false)).isEqualTo("false");
    }
    
    @Test
    public void testPrintWithCustomValue() {
        assertThat(customValueFormatter.print(true)).isEqualTo("○");
        assertThat(customValueFormatter.print(false)).isEqualTo("×");
    }
    
    @Test
    public void testSetAndGetValidationMessage() {
        
        // default (no set)
        assertThat(formatter.getValidationMessage().isPresent()).isEqualTo(false);
        
        // set
        formatter.setValidationMessage(VALIDATION_MESSAGE);
        assertThat(formatter.getValidationMessage().get()).isEqualTo(VALIDATION_MESSAGE);
        
        
    }
    
    @Test
    public void testGetMessageVariables() {
        
        Map<String, Object> vars = customValueFormatter.getMessageVariables();
        
        assertThat(vars).containsKey("trueValues")
            .containsKey("falseValues")
            .containsEntry("ignoreCase", false)
            .containsEntry("failToFalse", false);
        
        
    }
    
}
