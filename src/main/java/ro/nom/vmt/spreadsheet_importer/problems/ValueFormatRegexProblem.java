package ro.nom.vmt.spreadsheet_importer.problems;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import ro.nom.vmt.spreadsheet_importer.util.Messages;

public class ValueFormatRegexProblem extends ValueFormatProblem {

    private final String regex;

    public ValueFormatRegexProblem(String sheetName, Integer rowNo, Integer columnIndex, String column, String value, String regex) {
        super(sheetName, rowNo, columnIndex, column, value);
        this.regex = regex;
    }


    @Override
    public String toString() {
        return String.format(Messages.VALUE_FORMAT_REGEX_PROBLEM, getSheetName(), getRowNo() + 1, value, column == null ? "" : column, columnIndex, regex);
    }
}
