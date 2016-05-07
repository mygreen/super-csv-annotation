package org.supercsv.ext.builder;

import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.ift.CellProcessor;


/**
 * 解析したCSVのBeanのマッピング情報。
 * 
 * @author T.TSUCHIE
 *
 */
public class CsvBeanMapping<T> {
    
    private final Class<T> type;
    
    private boolean header;
    
    private List<CsvColumnMapping> columns;
    
    /**
     * CSVのマッピング情報を作成するコンストラクタ。
     * @param type クラスタイプ。
     */
    public CsvBeanMapping(final Class<T> type) {
        this.type = type;
    }
    
    /**
     * カラムのヘッダーの一覧を取得する。
     * @return ヘッダーの一覧。
     */
    public String[] getHeader() {
        
        List<String> list = new ArrayList<>();
        for(CsvColumnMapping column : columns) {
            list.add(column.getLabel());
        }
        return list.toArray(new String[0]);
    }
    
    /**
     * カラム名の一覧を取得する。
     * @return カラム名の一覧。
     */
    public String[] getNameMapping() {
        
        List<String> list = new ArrayList<String>();
        for(CsvColumnMapping column : columns) {
            list.add(column.getColumnName());
        }
        return list.toArray(new String[0]);
    }
    
    /**
     * 読み込み用の{@link CellProcessor} を取得する。
     * @return カラムの位置順に整列されている{@link CellProcessor}。
     */
    public CellProcessor[] getInputCellProcessor() {
        
        List<CellProcessor> list = new ArrayList<CellProcessor>();
        for(CsvColumnMapping column : columns) {
            list.add(column.getInputCellProcessor());
        }
        return list.toArray(new CellProcessor[0]);
    }
    
    /**
     * 書き込み用の{@link CellProcessor} を取得する。
     * @return カラムの位置順に整列されている{@link CellProcessor}。
     */
    public CellProcessor[] getOutputCellProcessor() {
        
        List<CellProcessor> list = new ArrayList<CellProcessor>();
        for(CsvColumnMapping column : columns) {
            list.add(column.getOutputCellProcessor());
        }
        return list.toArray(new CellProcessor[0]);
    }
    
    /**
     * カラム情報を取得する。
     * @return カラム情報
     */
    public List<CsvColumnMapping> getColumns() {
        return columns;
    }
    
    /**
     * カラム情報を設定する。
     * @param columns カラム情報。カラムの位置順に整列されている必要があります。
     */
    public void setColumns(final List<CsvColumnMapping> columns) {
        this.columns = columns;
    }
    
    /**
     * クラス情報を取得する。
     * @return クラス情報
     */
    public Class<T> getType() {
        return type;
    }
    
    public boolean isHeader() {
        return header;
    }
    
    public void setHeader(final boolean header) {
        this.header = header;
    }
    
}
