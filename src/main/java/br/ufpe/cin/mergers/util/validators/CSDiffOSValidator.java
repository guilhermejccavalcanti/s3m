package br.ufpe.cin.mergers.util.validators;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import org.apache.commons.lang.SystemUtils;

public class CSDiffOSValidator implements IParameterValidator {
    public void validate(String name, String value) throws ParameterException {
        if (runsCSDiff(value) && !SystemUtils.IS_OS_LINUX) {
            throw new ParameterException(name + " " + value + " option can only be used in Linux distributions");
        }
    }

    private boolean runsCSDiff(String textualMergeStrategy) {
        textualMergeStrategy = textualMergeStrategy.toUpperCase();
        switch (textualMergeStrategy) {
            case "CSDIFF":
            case "CONSECUTIVE":
            case "AUTOTUNING":
                return true;
            default:
                return false;
        }
    }
}
