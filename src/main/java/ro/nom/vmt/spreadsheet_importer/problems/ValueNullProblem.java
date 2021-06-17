package ro.nom.vmt.spreadsheet_importer.problems;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import ro.nom.vmt.spreadsheet_importer.util.Messages;

public class ValueNullProblem extends ValueProblem {


    public ValueNullProblem(String sheetName, Integer rowNo, Integer columnIndex, String column) {
        super(sheetName, rowNo, columnIndex, column, null);
    }

    @Override
    public String toString() {
        return String.format(Messages.VALUE_NULL_PROBLEM, getSheetName(), getRowNo() + 1, column == null ? "" : column, columnIndex);
    }
}
