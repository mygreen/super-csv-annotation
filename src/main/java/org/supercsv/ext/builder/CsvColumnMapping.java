package org.supercsv.ext.builder;

import org.supercsv.cellprocessor.ift.CellProcessor;


/**
 * 解析したCSVのカラムのマッピング情報。
 *
 * @author T.TSUCHIE
 *
 */
public class CsvColumnMapping {
    
    private Class<?> columnType;
    
    private String columnName;
    
    private int position;
    
    private String label;
    
    private CellProcessor outputCellProcessor;
    
    private CellProcessor inputCellProcessor;
    
    /**
     * カラムのクラスタイプ。
     * @return クラスタイプ。実質フィールドのクラスタイプになります。
     */
    public Class<?> getColumnType() {
        return columnType;
    }
    
    /**
     * カラムのクラスタイプを設定します。
     * @param columnType クラスタイプ。実質フィールドのクラスタイプになります。
     */
    public void setColumnType(final Class<?> columnType) {
        this.columnType = columnType;
    }
    
    /**
     * カラムの名称を取得します。
     * @return カラムの名称。
     */
    public String getColumnName() {
        return columnName;
    }
    
    /**
     * カラムの名称を設定します。
     * @param columnName カラムの名称。
     */
    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }
    
    /**
     * カラムの位置情報（インデックス）を取得します。
     * @return 0から始まります。
     */
    public int getPosition() {
        return position;
    }
    
    /**
     * カラムの位置情報（インデックス）を取得します。
     * @param position 0から始まります。
     */
    public void setPosition(int position) {
        this.position = position;
    }
    
    /**
     * カラムの見出しを取得します。
     * @return カラムの見出し。
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * カラムの見出しを設定します。
     * @param label カラムの見出し。
     */
    public void setLabel(String label) {
        this.label = label;
    }
    
    /**
     * 書き込み用の{@link CellProcessor}を取得します。
     * @return 組み立てた{@link CellProcessor}。
     */
    public CellProcessor getOutputCellProcessor() {
        return outputCellProcessor;
    }
    
    /**
     * 書き込み用の{@link CellProcessor}を設定します。
     * @param outputCellProcessor 組み立てた{@link CellProcessor}。
     */
    public void setOutputCellProcessor(final CellProcessor outputCellProcessor) {
        this.outputCellProcessor = outputCellProcessor;
    }
    
    /**
     * 読み込み用の{@link CellProcessor}を取得します。
     * @return 組み立てた{@link CellProcessor}。
     */
    public CellProcessor getInputCellProcessor() {
        return inputCellProcessor;
    }
    
    /**
     * 読み込み用の{@link CellProcessor}を設定します。
     * @param inputCellProcessor 組み立てた{@link CellProcessor}。
     */
    public void setInputCellProcessor(final CellProcessor inputCellProcessor) {
        this.inputCellProcessor = inputCellProcessor;
    }
    
}
