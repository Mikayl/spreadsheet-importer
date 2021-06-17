package ro.nom.vmt.spreadsheet_importer.problems;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import ro.nom.vmt.spreadsheet_importer.util.Messages;

public class ColumnNotPresentProblem extends SheetProblem {

    private final String column;

    public ColumnNotPresentProblem(String sheetName, String column) {
        super(sheetName);
        this.column = column;
    }

    @Override
    public String toString() {
        return String.format(Messages.COLUMN_NOT_PRESENT, getSheetName(), column);
    }
}
