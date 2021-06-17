package ro.nom.vmt.spreadsheet_importer.problems;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import ro.nom.vmt.spreadsheet_importer.interfaces.Problem;
import ro.nom.vmt.spreadsheet_importer.util.Messages;

public class SheetProblem implements Problem {

    private final String sheetName;

    public SheetProblem(String sheetName) {
        this.sheetName = sheetName;
    }

    public String getSheetName() {
        return sheetName;
    }


    @Override
    public String toString() {
        return String.format(Messages.SHEET_PROBLEM, sheetName);

    }

}
