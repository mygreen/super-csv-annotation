package com.github.mygreen.supercsv.builder.spring;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.supercsv.cellprocessor.ift.CellProcessor;

import com.github.mygreen.supercsv.builder.Configuration;
import com.github.mygreen.supercsv.builder.FieldAccessor;
import com.github.mygreen.supercsv.cellprocessor.ConstraintProcessorFactory;
import com.github.mygreen.supercsv.cellprocessor.format.TextFormatter;

/**
 * {@link UserNameExist}を作成するCellProcessorの作成。
 * <p>Springで管理する。</p>
 *
 * @since 2.0
 * @author T.TSUCHIE
 *
 */
@Component
public class UserNameExistFactory implements ConstraintProcessorFactory<CsvUserNameExist> {
    
    @Autowired
    private UserService userService;
    
    @Override
    public Optional<CellProcessor> create(final CsvUserNameExist anno, final Optional<CellProcessor> next,
            final FieldAccessor field, final TextFormatter<?> formatter, final Configuration config) {
        
        final UserNameExist processor = next.map(n -> new UserNameExist(userService, n))
                .orElseGet(() -> new UserNameExist(userService));
        processor.setValidationMessage(anno.message());
        
        return Optional.of(processor);
    }
    
}
