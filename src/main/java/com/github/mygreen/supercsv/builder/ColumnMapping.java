package com.github.mygreen.supercsv.builder;

import java.util.Optional;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * 解析したカラムのマッピング情報です。
 *
 * @version 2.5
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

    /**
     * 固定長カラムの情報
     */
    private Optional<FixedSizeColumnProperty> fixedSizeProperty = Optional.empty();

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
     * 番号が決まっている（1以上）かどうか。
     * @since 2.1
     * @return trueの場合、番号は1以上であり決まっています。
     */
    public boolean isDeterminedNumber() {
        return number >= 1;
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

    /**
     * 固定長のカラム情報を取得する。
     * @since 2.5
     * @return 固定長の情報。設定情報がない場合はnullを返す。
     */
    public FixedSizeColumnProperty getFixedSizeProperty() {
        return fixedSizeProperty.orElse(null);
    }

    /**
     * 固定長のカラム情報を設定する。
     * @since 2.5
     * @param fixedSizeProperty 固定長の情報。
     */
    public void setFixedSizeProperty(FixedSizeColumnProperty fixedSizeProperty) {
        this.fixedSizeProperty = Optional.of(fixedSizeProperty);
    }

    /**
     * 読み込み時のCellProcessorを取得します。
     * @return 読み込み時のCellProcessor
     */
    public CellProcessor getCellProcessorForReading() {
        return cellProcessorForReading;
    }

    /**
     * 読み込み時のCellProcessorを設定します。
     * @param cellProcessorForReading 読み込み時のCellProcessor
     */
    public void setCellProcessorForReading(CellProcessor cellProcessorForReading) {
        this.cellProcessorForReading = cellProcessorForReading;
    }

    /**
     * 書き込み時のCellProcessorを取得します。
     * @return 書き込み時のCellProcessor
     */
    public CellProcessor getCellProcessorForWriting() {
        return cellProcessorForWriting;
    }

    /**
     * 書き込み時のCellProcessorを設定します。
     * @param cellProcessorForWriting 書き込み時のCellProcessor
     */
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
