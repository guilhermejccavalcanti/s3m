package br.ufpe.cin.mergers.util.converters;

import com.beust.jcommander.IStringConverter;

import br.ufpe.cin.mergers.*;

public class MergeStrategyConverter implements IStringConverter<MergeStrategy> {
    @Override
    public MergeStrategy convert(String value) {
        switch (value.toUpperCase()) {
            default:
                return new TextualMerge();
        }
    }
}
