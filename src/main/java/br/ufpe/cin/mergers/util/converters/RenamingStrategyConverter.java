package br.ufpe.cin.mergers.util.converters;


import br.ufpe.cin.mergers.util.RenamingStrategy;
import com.beust.jcommander.IStringConverter;

import static br.ufpe.cin.mergers.util.RenamingStrategy.*;

public class RenamingStrategyConverter implements IStringConverter<RenamingStrategy> {
    @Override
    public RenamingStrategy convert(String value) {
        switch (value) {
            case "both":
                return KEEP_BOTH_METHODS;
            case "merge":
                return MERGE_METHODS;
            default:
                return SAFE;
        }
    }
}