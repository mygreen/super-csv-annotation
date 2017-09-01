package com.github.mygreen.supercsv.builder;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.annotation.constraint.CsvEquals;
import com.github.mygreen.supercsv.annotation.constraint.CsvRequire;
import com.github.mygreen.supercsv.annotation.constraint.CsvUnique;
import com.github.mygreen.supercsv.annotation.constraint.CsvUniqueHashCode;
import com.github.mygreen.supercsv.annotation.conversion.CsvDefaultValue;
import com.github.mygreen.supercsv.annotation.conversion.CsvFullChar;
import com.github.mygreen.supercsv.annotation.conversion.CsvHalfChar;
import com.github.mygreen.supercsv.annotation.conversion.CsvLeftPad;
import com.github.mygreen.supercsv.annotation.conversion.CsvLower;
import com.github.mygreen.supercsv.annotation.conversion.CsvMultiPad;
import com.github.mygreen.supercsv.annotation.conversion.CsvNullConvert;
import com.github.mygreen.supercsv.annotation.conversion.CsvOneSideTrim;
import com.github.mygreen.supercsv.annotation.conversion.CsvRegexReplace;
import com.github.mygreen.supercsv.annotation.conversion.CsvRightPad;
import com.github.mygreen.supercsv.annotation.conversion.CsvTrim;
import com.github.mygreen.supercsv.annotation.conversion.CsvUpper;
import com.github.mygreen.supercsv.annotation.conversion.CsvWordReplace;
import com.github.mygreen.supercsv.annotation.format.CsvFormat;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorHandler;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.ConversionProcessorHandler;
import com.github.mygreen.supercsv.cellprocessor.ProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.EqualsFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.RequireFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.UniqueFactory;
import com.github.mygreen.supercsv.cellprocessor.constraint.UniqueHashCodeFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.DefaultValueFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.FullCharFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.HalfCharFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.LeftPadFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.LowerFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.MultiPadFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.NullConvertFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.OneSideTrimFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.RegexReplaceFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.RightPadFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.TrimFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.UpperFactory;
import com.github.mygreen.supercsv.cellprocessor.conversion.WordReplaceFactory;
import com.github.mygreen.supercsv.cellprocessor.format.ParseProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.PrintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * アノテーションによる{@link CellProcessor}を組み立てるベースとなるクラス。
 * <p>共通の{@link CellProcessor}などを追加する処理を定義します。
 *
 * @param <T> 処理対象のクラスタイプ。
 * @version 2.0
 * @author T.TSUCHIE
 *
 */
public abstract class AbstractProcessorBuilder<T> implements ProcessorBuilder<T> {

    /**
     * 読み込み時の変換用のCellProcessorを作成する。
     */
    protected List<ProcessorFactory> readingFactory = new ArrayList<>();

    /**
     * 書き込み時の変換用のCellProcessorを作成する。
     */
    protected List<ProcessorFactory> writingFactory = new ArrayList<>();

    /**
     * 変換のCellProcessorを作成する
     */
    protected ConversionProcessorHandler conversionHandler = new ConversionProcessorHandler();

    /**
     * 制約のCellProcessorを作成する
     */
    protected ConstraintProcessorHandler constraintHandler = new ConstraintProcessorHandler();

    /**
     * デフォルトコンストラクタ。
     * <p>{@link #init()}メソッドが呼ばれる。
     */
    public AbstractProcessorBuilder() {
        init();
    }

    /**
     * デフォルトの{@link ProcessorFactory}などの登録を行い、初期化を行う。
     *
     */
    protected void init() {

        // 読み込み用の登録
        registerForReading(conversionHandler);
        registerForReading(new ParseProcessorFactory<>());
        registerForReading(constraintHandler);

        // 書き込み用の登録
        registerForWriting(constraintHandler);
        registerForWriting(new PrintProcessorFactory<>());
        registerForWriting(conversionHandler);

        // 変換用の登録
        registerForConversion(CsvNullConvert.class, new NullConvertFactory());
        registerForConversion(CsvDefaultValue.class, new DefaultValueFactory());
        registerForConversion(CsvTrim.class, new TrimFactory());
        registerForConversion(CsvUpper.class, new UpperFactory());
        registerForConversion(CsvLower.class, new LowerFactory());
        registerForConversion(CsvRegexReplace.class, new RegexReplaceFactory());
        registerForConversion(CsvWordReplace.class, new WordReplaceFactory());
        registerForConversion(CsvFullChar.class, new FullCharFactory());
        registerForConversion(CsvHalfChar.class, new HalfCharFactory());
        registerForConversion(CsvLeftPad.class, new LeftPadFactory());
        registerForConversion(CsvRightPad.class, new RightPadFactory());
        registerForConversion(CsvMultiPad.class, new MultiPadFactory());
        registerForConversion(CsvOneSideTrim.class, new OneSideTrimFactory());

        // 制約用の登録
        registerForConstraint(CsvRequire.class, new RequireFactory());
        registerForConstraint(CsvUnique.class, new UniqueFactory<>());
        registerForConstraint(CsvUniqueHashCode.class, new UniqueHashCodeFactory<>());
        registerForConstraint(CsvEquals.class, new EqualsFactory<>());

    }

    @Override
    public Optional<CellProcessor> buildForReading(final Class<T> type, final FieldAccessor field,
            final Configuration config, final Class<?>[] groups) {

        // 登録時とは逆順に処理する
        final List<ProcessorFactory> factories = new ArrayList<>(readingFactory);
        Collections.reverse(factories);

        final TextFormatter<T> formatter = getFormatter(field, config);

        Optional<CellProcessor> processor = Optional.empty();
        for(ProcessorFactory factory : factories) {
            processor = factory.create(processor, field, formatter, config, BuildCase.Read, groups);
        }

        return processor;

    }

    @Override
    public Optional<CellProcessor> buildForWriting(final Class<T> type, final FieldAccessor field,
            final Configuration config, final Class<?>[] groups) {

        // 登録時とは逆順に処理する
        final List<ProcessorFactory> factories = new ArrayList<>(writingFactory);
        Collections.reverse(factories);

        final TextFormatter<T> formatter = getFormatter(field, config);

        Optional<CellProcessor> processor = Optional.empty();
        for(ProcessorFactory factory : factories) {

            //制約のProcessorの実行有無の判定
            if(config.isSkipValidationOnWrite()
                    && factory instanceof ConstraintProcessorHandler) {
                continue;
            }

            processor = factory.create(processor, field, formatter, config, BuildCase.Write, groups);
        }

        return processor;
    }

    /**
     * 読み込み用のCellProcessorを作成するクラスを登録する。
     * <p>実行時は、登録された順に処理される。
     * @param factory {@link ProcessorFactory}の実装クラス。
     */
    public void registerForReading(final ProcessorFactory factory) {
        this.readingFactory.add(factory);
    }

    /**
     * 書き込み用のCellProcessorを作成するクラスを登録する。
     * <p>実行時は、登録された順に処理される。
     * @param factory {@link ProcessorFactory}の実装クラス。
     */
    public void registerForWriting(final ProcessorFactory factory) {
        this.writingFactory.add(factory);
    }

    /**
     * 変換のCellProcessorを作成するクラスを登録する。読み込み時と書き込み時は共通です。
     *
     * @param <A> アノテーションのクラス
     * @param anno 関連づけるアノテーション
     * @param factory アノテーションを処理する{@link ConversionProcessorFactory}の実装。
     */
    public <A extends Annotation> void registerForConversion(final Class<A> anno, final ConversionProcessorFactory<A> factory) {
        this.conversionHandler.register(anno, factory);
    }

    /**
     * 制約のCellProcessorを作成するクラスを登録する。読み込み時と書き込み時は共通です。
     *
     * @param <A> アノテーションのクラス
     * @param anno 関連づけるアノテーション
     * @param factory アノテーションを処理する{@link ConstraintProcessorFactory}の実装。
     */
    public <A extends Annotation> void registerForConstraint(final Class<A> anno, final ConstraintProcessorFactory<A> factory) {
        this.constraintHandler.register(anno, factory);
    }

    /**
     * 文字列とオブジェクトを相互変換するフォーマッタを取得します。
     * <p>アノテーション{@link CsvFormat}が指定されている場合は、そちらを優先します。</p>
     * @param field フィールド情報
     * @param config システム設定
     * @return フォーマッタを取得します。
     */
    @SuppressWarnings("unchecked")
    public TextFormatter<T> getFormatter(final FieldAccessor field, final Configuration config) {

        if(field.hasAnnotation(CsvFormat.class)) {
            CsvFormat formatAnno = field.getAnnotation(CsvFormat.class).get();

            final TextFormatter<T> formatter = (TextFormatter<T>) config.getBeanFactory().create(formatAnno.formatter());
            if(!formatAnno.message().isEmpty()) {
                formatter.setValidationMessage(formatAnno.message());
            }
            return formatter;

        } else {
            return getDefaultFormatter(field, config);

        }
    }

    /**
     * 文字列とオブジェクトを相互変換する標準のフォーマッタを取得します。
     * <p>書式が設定されている場合は、書式に沿って処理を行います。</p>
     * @param field フィールド情報
     * @param config システム設定
     * @return 標準のフォーマッタを取得します。
     */
    protected abstract TextFormatter<T> getDefaultFormatter(FieldAccessor field, Configuration config);

    /**
     * 登録している変換用のアノテーションとそのファクトリクラスの情報を取得します。
     * @return アノテーションと対応する{@link ConversionProcessorFactory}のマップ。
     */
    public Set<Map.Entry<Class<? extends Annotation>, ConversionProcessorFactory<?>>>  getEntrySetForConversion() {
        return conversionHandler.getEntrySet();
    }

    /**
     * 登録している検証用のアノテーションとそのファクトリクラスの情報を取得します。
     * @return アノテーションと対応する{@link ConstraintProcessorFactory}のマップ。
     */
    public Set<Map.Entry<Class<? extends Annotation>, ConstraintProcessorFactory<?>>>  getEntrySetForConsraint() {
        return constraintHandler.getEntrySet();
    }

}
