package br.ufpe.cin.mergers.util.converters;

import com.beust.jcommander.IStringConverter;

import br.ufpe.cin.mergers.textual.CSDiff;
import br.ufpe.cin.mergers.textual.ConsecutiveLines;
import br.ufpe.cin.mergers.textual.Diff3;
import br.ufpe.cin.mergers.util.TextualMergeStrategy;

public class TextualMergeStrategyConverter implements IStringConverter<TextualMergeStrategy> {
    @Override
    public TextualMergeStrategy convert(String value) {
        switch (value.toUpperCase()) {
            case "CSDIFF":
                return new CSDiff();
            case "CONSECUTIVE":
                return new ConsecutiveLines();
            default:
                return new Diff3();
        }
    }
}
