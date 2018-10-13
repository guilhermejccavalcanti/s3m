package br.ufpe.cin.mergers.util.converters;


import br.ufpe.cin.mergers.util.RenamingStrategy;
import com.beust.jcommander.IStringConverter;

import static br.ufpe.cin.mergers.util.RenamingStrategy.KEEP_BOTH_METHODS;
import static br.ufpe.cin.mergers.util.RenamingStrategy.MERGE_METHODS;
import static br.ufpe.cin.mergers.util.RenamingStrategy.SAFE;

public class RenamingStrategyConverter implements IStringConverter<RenamingStrategy> {
    @Override
    public RenamingStrategy convert(String value) {
        switch (value.toUpperCase()) {
            case "BOTH":
                return KEEP_BOTH_METHODS;
            case "MERGE":
                return MERGE_METHODS;
            default:
                return SAFE;
        }
    }
}