package com.github.mygreen.supercsv.io;

import java.io.Reader;
import java.io.Writer;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.supercsv.comment.CommentMatcher;
import org.supercsv.encoder.CsvEncoder;
import org.supercsv.io.ITokenizer;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.NormalQuoteMode;
import org.supercsv.quote.QuoteMode;

import com.github.mygreen.supercsv.builder.BeanMapping;
import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FixedSizeBeanMappingFactory;
import com.github.mygreen.supercsv.util.ArgUtils;
import com.github.mygreen.supercsv.util.MemorizingSupplier;

/**
 * 固定長のCSV設定。
 * 
 * @since 2.5
 * @param <T> マッピング対象のBeanのクラスタイプ
 * @author T.TSUCHIE
 *
 */
public class FixedSizeCsvPreference<T> {

    /**
     * CSVの設定情報。
     * ※QuoteModeやエンコーダなどの設定は、{@link FixedSizeCsvEncoder}に引き継がれます。
     */
    private CsvPreference csvPreference;

    private BeanMappingCache<T> beanMappingCache;

    private TokenizerFactory<T> tokenizerFactory;

    private Configuration configuration;
    
    private FixedSizeCsvPreference(final Builder<T> builder) {
        Objects.requireNonNull(builder, "builder should not be null.");
        
        this.csvPreference = buildCsvPreference(builder);
        this.tokenizerFactory = builder.tokenizerFactory;
        this.configuration = builder.configuration;
        
        FixedSizeBeanMappingFactory factory = new FixedSizeBeanMappingFactory();
        factory.setConfiguration(configuration);
        this.beanMappingCache = BeanMappingCache.create(factory.create(builder.beanType, builder.groups));
    }
    
    /**
     * 固定長用のCSV設定情報をもとに、通常のCSV設定情報を作成します。
     * 
     * @param builder 固定長用のCSV設定情報
     * @return 通常のCSV設定情報
     */
    private CsvPreference buildCsvPreference(final Builder<T> builder) {
        
        // クオート文字列、区切り文字列は固定長の場合は不要だが、CsvPreferenceでは必須のため仮値として指定する。
        CsvPreference.Builder preferenceBuilder = new CsvPreference.Builder('"', ',', builder.endOfLineSymbols)
                .useQuoteMode(builder.quoteMode)
                .useEncoder(builder.encoderFactory.create(MemorizingSupplier.of(() -> beanMappingCache.getOriginal())))
                .ignoreEmptyLines(builder.ignoreEmptyLines)
                .surroundingSpacesNeedQuotes(false);

        builder.commentMatcher.ifPresent(matcher -> {
            preferenceBuilder.skipComments(matcher);
        });
        
        return preferenceBuilder.build();
    }
    
    /**
     * {@link FixedSizeCsvPreference}の ビルダークラスを取得します。
     * @param <T> Beanのクラスタイプ
     * @param beanType Beanのクラスタイプ
     * @param groups グループ情報。適用するアノテーションを切り替える際に指定します。
     * @return {@link FixedSizeCsvPreference}の ビルダークラスのインスタンス。
     */
    public static <T> Builder<T> builder(final Class<T> beanType, final Class<?>... groups) {
        return new Builder<>(beanType, groups);
    }
    
    /**
     * CSVの設定情報を取得します。
     * @return CSVの設定情報
     */
    public CsvPreference getCsvPreference() {
        return csvPreference;
    }

    /**
     * Beanのマッピング情報を取得します。
     * @return Beanのマッピング情報
     */
    public BeanMappingCache<T> getBeanMappingCache() {
        return beanMappingCache;
    }
    
    /**
     * 設定情報を取得します。
     * @return 設定情報
     */
    public Configuration getConfiguration() {
        return configuration;
    }
    
    /**
     * 固定長CSVを読み込み時のTokenizerを作成します。
     * 
     * @param reader Reader
     * @return Tokenizerのインスタンス。
     */
    public ITokenizer createTokenizer(final Reader reader) {
        return tokenizerFactory.create(reader, csvPreference, beanMappingCache.getOriginal());
    }

    /**
     * 固定長CSVの読み込みを行う {@link FixedSizeCsvAnnotationBeanReader}を作成します。
     * @param reader Reader
     * @return {@link FixedSizeCsvAnnotationBeanReader}のインスタンス。
     */
    public FixedSizeCsvAnnotationBeanReader<T> csvReader(final Reader reader) {
        return new FixedSizeCsvAnnotationBeanReader<>(reader, this);
    }
    
    /**
     * 固定長CSVの書き込みを行う {@link FixedSizeCsvAnnotationBeanWriter}を作成します。
     * @param writer Writer
     * @return {@link FixedSizeCsvAnnotationBeanWriter}のインスタンス。
     */
    public FixedSizeCsvAnnotationBeanWriter<T> csvWriter(final Writer writer) {
        return new FixedSizeCsvAnnotationBeanWriter<>(writer, this);
    }
    
    /**
     * Tokenizerのインスタンスを作成する。
     * 
     * @param <T> マッピング対象のBeanのクラスタイプ
     */
    public interface TokenizerFactory<T> {
        
        /**
         * Tokenizerのインスタンスを作成する。
         * 
         * @param reader 読み込み元のリーダー
         * @param preference CSVの設定情報
         * @param beanMapping Beanのマッピング情報
         * @return Tokenizerのインスタンス
         */
        ITokenizer create(Reader reader, CsvPreference preference, BeanMapping<T> beanMapping);
    }
    
    /**
     * CsvEncoderのインスタンスを作成する。
     *
     *
    * @param <T> マッピング対象のBeanのクラスタイプ
     */
    public interface CsvEncoderFactory<T> {
        
        /**
         * CsvEncoderのインスタンスを作成する。
         * <p>CsvEncoderのインスタンス作成時は、BeanMappingのインスタンスは未作成のため遅延評価する必要があるため、Supplierを使用する。
         * @param beanMappingSupplier Beanのマッピング情報を取得する処理。
         * @return CsvEncoderのインスタンス
         */
        CsvEncoder create(Supplier<BeanMapping<T>> beanMappingSupplier);
    }

    
    /**
     * {@link FixedSizeCsvPreference} のビルダー。
     * 
     * @param <T> マッピング対象のBeanのクラスタイプ
     *
     */
    public static class Builder<T> {
        
        private final Class<T> beanType;
        
        private final Class<?>[] groups;
        
        /** EOL（改行コード） */
        private String endOfLineSymbols;
        
        /** 空行を無視するかどうか */
        private boolean ignoreEmptyLines;
        
        private QuoteMode quoteMode;
        
        /** 読み込み時に固定長カラムサイズ定義に従い分解して処理する */
        private TokenizerFactory<T> tokenizerFactory;
        
        /** 書き込み時に固定長カラムサイズ定義に従い処理する */
        private CsvEncoderFactory<T> encoderFactory;

        /**
         * コメント行かの判定処理。
         */
        private Optional<CommentMatcher> commentMatcher;
        
        private Configuration configuration;
        
        /**
         * コンストラクタ。
         * @param beanType Beanのクラスタイプ
         * @param groups グループ情報。適用するアノテーションを切り替える際に指定します。
         * @throws NullPointerException beanType is null.
         */
        public Builder(final Class<T> beanType, final Class<?>... groups) {
            Objects.requireNonNull(beanType, "beanType should not be null.");
            
            this.beanType = beanType;
            this.groups = groups;
        }
        
        /**
         * EOL（改行コード）を設定します。
         * @param endOfLineSymbols EOL（改行コード）。デフォルトは、{@literal \r\n}。
         * @return Builder自身のインスタンス。
         * @throws NullPointerException endOfLineSymbols is null.
         * @throws IllegalArgumentException endOfLineSymbols is empty.
         */
        public Builder<T> endOfLineSymbols(final String endOfLineSymbols) {
            ArgUtils.notEmpty(endOfLineSymbols, "endOfLineSymbols");
            
            this.endOfLineSymbols = endOfLineSymbols;
            return this;
        }
        
        /**
         * 空行を無視するかどうかを設定します。
         * @param ignoreEmptyLines 空行を無視する場合は{@literal true}を指定します。
         * @return Builder自身のインスタンス。
         */
        public Builder<T> ignoreEmptyLines(final boolean ignoreEmptyLines) {
            this.ignoreEmptyLines = ignoreEmptyLines;
            return this;
        }
        
        /**
         * クォートモードを設定します。
         * @param quoteMode クォートモード。デフォルトは、{@link NormalQuoteMode}。
         * @return Builder自身のインスタンス。
         * @throws NullPointerException quoteMode is null.
         */
        public Builder<T> quoteMode(final QuoteMode quoteMode) {
            Objects.requireNonNull(quoteMode, "quoteMode should not be null.");
            
            this.quoteMode = quoteMode;
            return this;
        }
        
        /**
         * Tokenizerを作成する処理を設定します。
         * @param tokenizerFactory Tokenizerを作成する処理。デフォルトは、{@link FixedSizeTokenizer}のインスタンスです。
         * @return Builder自身のインスタンス。
         * @throws NullPointerException tokenizerFactory is null.
         */
        public Builder<T> tokenizerFactory(final TokenizerFactory<T> tokenizerFactory) {
            Objects.requireNonNull(tokenizerFactory, "tokenizerFactory should not be null.");
            
            this.tokenizerFactory = tokenizerFactory;
            return this;
        }
        
        /**
         * CsvEncoderを作成する処理を設定します。
         * @param encoderFactory CsvEncoderを作成する処理を設定します。デフォルトは、{@link FixedSizeCsvEncoder}のインスタンスです。
         * @return Builder自身のインスタンス。
         * @throws NullPointerException csvEncoderFactory is null.
         */
        public Builder<T> encoderFactory(final CsvEncoderFactory<T> encoderFactory) {
            Objects.requireNonNull(encoderFactory, "encoderFactory should not be null.");
            
            this.encoderFactory = encoderFactory;
            return this;
        }
        
        /**
         * コメント行かの判定処理を設定します。
         * @param commentMatcher コメント行かの判定処理。デフォルトは {@literal null}で何もしない。
         * @return Builder自身のインスタンス。
         */
        public Builder<T> skipComment(final CommentMatcher commentMatcher) {
            this.commentMatcher = Optional.of(commentMatcher);
            return this;
            
        }
        
        /**
         * 設定情報を設定します。
         * @param configuration 設定情報
         * @return Builder自身のインスタンス。
         * @throws NullPointerException configuration is null.
         */
        public Builder<T> configuration(final Configuration configuration) {
            Objects.requireNonNull(configuration, "configuration should not be null.");
            
            this.configuration = configuration;
            return this;
        }
        
        /**
         * {@link FixedSizeCsvPreference}のインスタンスを組み立てます。
         * 
         * @return {@link FixedSizeCsvPreference}のインスタンス。
         */
        public FixedSizeCsvPreference<T> build() {
            
            if (endOfLineSymbols == null) {
                endOfLineSymbols = "\r\n";
            }
            
            if (quoteMode == null) {
                quoteMode = new NormalQuoteMode();
            }
            
            if (commentMatcher == null) {
                commentMatcher = Optional.empty();
            }
            
            if (tokenizerFactory == null) {
                tokenizerFactory = new TokenizerFactory<T>() {
                    
                    @Override
                    public ITokenizer create(Reader reader, CsvPreference preference, BeanMapping<T> beanMapping) {
                        return new FixedSizeTokenizer(reader, preference, beanMapping);
                    }
                };
                
            }
            
            if (encoderFactory == null) {
                encoderFactory = new CsvEncoderFactory<T>() {
                    
                   @Override
                    public CsvEncoder create(Supplier<BeanMapping<T>> beanMappingSupplier) {
                        return new FixedSizeCsvEncoder<T>(beanMappingSupplier);
                    }
                };
                
            }
            
            if (configuration == null) {
                configuration = new Configuration();
            }
            
            return new FixedSizeCsvPreference<>(this);
        }
        
    }

}
