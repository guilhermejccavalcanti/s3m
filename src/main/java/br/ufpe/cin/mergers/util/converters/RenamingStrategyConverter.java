package br.ufpe.cin.mergers.util.converters;

import com.beust.jcommander.IStringConverter;

import br.ufpe.cin.mergers.util.RenamingStrategy;

import static br.ufpe.cin.mergers.util.RenamingStrategy.*;

public class RenamingStrategyConverter implements IStringConverter<RenamingStrategy> {
    @Override
    public RenamingStrategy convert(String value) {
        switch (value.toUpperCase()) {
            case "NO-EXTRA-FP":
                return NO_EXTRA_FP;
            case "BOTH":
                return KEEP_SIMILAR_METHODS;
            case "MERGE":
                return MERGE_SIMILAR;
            default:
                return SAFELY_MERGE_SIMILAR;
        }
    }
}