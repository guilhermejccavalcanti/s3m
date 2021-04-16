package br.ufpe.cin.mergers.util.converters;

import com.beust.jcommander.IStringConverter;

import br.ufpe.cin.mergers.*;

public class TextualMergeStrategyConverter implements IStringConverter<TextualMergeStrategy> {
    @Override
    public TextualMergeStrategy convert(String value) {
        switch (value.toUpperCase()) {
            default:
                return new Diff3();
        }
    }
}
