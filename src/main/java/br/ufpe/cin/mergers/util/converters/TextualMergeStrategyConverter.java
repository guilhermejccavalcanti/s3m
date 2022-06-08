package br.ufpe.cin.mergers.util.converters;

import com.beust.jcommander.IStringConverter;

import br.ufpe.cin.mergers.textual.*;
import br.ufpe.cin.mergers.util.TextualMergeStrategy;

public class TextualMergeStrategyConverter implements IStringConverter<TextualMergeStrategy> {
    @Override
    public TextualMergeStrategy convert(String value) {
        switch (value.toUpperCase()) {
            case "CSDIFF":
                return new CSDiff();
            case "CONSECUTIVE":
                return new ConsecutiveLines();
            case "AUTOTUNING":
                return new CSDiffAndDiff3();
            default:
                return new Diff3();
        }
    }
}
