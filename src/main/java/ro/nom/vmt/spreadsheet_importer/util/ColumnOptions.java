package ro.nom.vmt.spreadsheet_importer.util;/*
 *@Author Mihai V (11/06/2021)
 */

import java.util.List;
import java.util.function.UnaryOperator;

public class ColumnOptions {
    private final boolean isRequired;
    private final boolean trim;
    private final boolean formulaAllowed;
    private final List<UnaryOperator<String>> preProcess;
    private final String matches;
    private final String columnName;

    public ColumnOptions(boolean isRequired, boolean trim, boolean formulaAllowed, List<UnaryOperator<String>> preProcess, String matches, String columnName) {
        this.isRequired = isRequired;
        this.trim = trim;
        this.formulaAllowed = formulaAllowed;
        this.preProcess = preProcess;
        this.matches = matches;
        this.columnName = columnName;
    }

    public static OptionsBuilder builder() {
        return new OptionsBuilder();
    }

    public boolean isRequired() {
        return isRequired;
    }

    public boolean isTrim() {
        return trim;
    }

    public boolean isFormulaAllowed() {
        return formulaAllowed;
    }

    public String getMatches() {
        return matches;
    }

    public List<UnaryOperator<String>> getPreProcess() {
        return preProcess;
    }

    public String getColumnName() {
        return columnName;
    }

    public static class OptionsBuilder {
        List<UnaryOperator<String>> preProcess;
        private boolean isRequired = false;
        private boolean trim = true;
        private boolean formulaAllowed = true;
        private String matches = "";
        private String columnName;

        private OptionsBuilder() {
        }


        public OptionsBuilder isRequired(boolean required) {
            this.isRequired = required;
            return this;
        }

        public OptionsBuilder trim(boolean trim) {
            this.trim = trim;
            return this;
        }

        public OptionsBuilder formulaAllowed(boolean formulaAllowed) {
            this.formulaAllowed = formulaAllowed;
            return this;
        }

        public OptionsBuilder preProcess(List<UnaryOperator<String>> preProcessors) {
            this.preProcess = preProcessors;
            return this;
        }

        public OptionsBuilder matches(String matches) {
            this.matches = matches;
            return this;
        }


        public OptionsBuilder columnName(String columnName) {
            this.columnName = columnName;
            return this;
        }

        public ColumnOptions build() {
            return new ColumnOptions(isRequired, trim, formulaAllowed, preProcess, matches, columnName);
        }
    }
}
