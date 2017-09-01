package com.github.mygreen.supercsv.builder;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * 解析したカラムのマッピング情報です。
 *
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public class ColumnMapping implements Comparable<ColumnMapping> {
    
    private FieldAccessor field;
    
    private String label;
    
    private int number;
    
    /**
     * 部分的なカラムかどうか。
     */
    private boolean partialized;
    
    private CellProcessor cellProcessorForReading;
    
    private CellProcessor cellProcessorForWriting;
    
    private TextFormatter<?> formatter;
    
    /**
     * {@link #number}の昇順。
     * <p>{@link #number}が同じ場合は、フィールド名の昇順。</p>
     */
    @Override
    public int compareTo(final ColumnMapping o) {
        
        if(this.number == o.number) {
            return this.field.getName().compareTo(o.field.getName());
            
        } else {
            return Integer.compare(number, o.number);
        }
        
    }
    
    /**
     * カラムの名称を取得する。
     * @return Beanに定義されているフィールドの名称を取得します。
     *         部分的なカラムの場合はnullを返します。
     */
    public String getName() {
        return field != null ? field.getName() : null;
    }
    
    /**
     * フィールド情報を取得します。
     * @return 部分的なカラムの場合はnullを返します。
     */
    public FieldAccessor getField() {
        return field;
    }
    
    public void setField(FieldAccessor field) {
        this.field = field;
    }
    
    /**
     * ラベル情報を取得します。
     * @return ラベル情報。
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * ラベル情報を設定します。
     * @param label ラベル情報。
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * カラムの番号を取得します。
     * @return 1から始まります。
     */
    public int getNumber() {
        return number;
    }
    
    /**
     * カラムの番号を設定します。
     * @param number 1から始まります。
     */
    public void setNumber(int number) {
        this.number = number;
    }
    
    /**
     * 部分的なカラムかどうか判定する。
     * @return trueの場合、部分的なカラムです。
     */
    public boolean isPartialized() {
        return partialized;
    }
    
    /**
     * 部分的なカラムかどうか設定する。
     * @param partialized trueの場合、部分的なカラムです。
     */
    public void setPartialized(boolean partialized) {
        this.partialized = partialized;
    }
    
    public CellProcessor getCellProcessorForReading() {
        return cellProcessorForReading;
    }
    
    public void setCellProcessorForReading(CellProcessor cellProcessorForReading) {
        this.cellProcessorForReading = cellProcessorForReading;
    }
    
    public CellProcessor getCellProcessorForWriting() {
        return cellProcessorForWriting;
    }
    
    public void setCellProcessorForWriting(CellProcessor cellProcessorForWriting) {
        this.cellProcessorForWriting = cellProcessorForWriting;
    }
    
    /**
     * フィールドのオブジェクトに対するフォーマッタ。
     * @return 部分的なカラムの場合、nullを返す。
     */
    public TextFormatter<?> getFormatter() {
        return formatter;
    }
    
    public void setFormatter(TextFormatter<?> formatter) {
        this.formatter = formatter;
    }
}
