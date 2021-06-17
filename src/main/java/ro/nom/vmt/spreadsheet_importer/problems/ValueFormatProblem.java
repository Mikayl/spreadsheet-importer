package ro.nom.vmt.spreadsheet_importer.problems;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import ro.nom.vmt.spreadsheet_importer.util.Messages;

public class ValueFormatProblem extends ValueProblem {


    public ValueFormatProblem(String sheetName, Integer rowNo, Integer columnIndex, String column, String value) {
        super(sheetName, rowNo, columnIndex, column, value);

    }


    @Override
    public String toString() {
        return String.format(Messages.VALUE_FORMAT_PROBLEM, getSheetName(), getRowNo() + 1, value, column == null ? "" : column, columnIndex);
    }
}
