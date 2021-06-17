package ro.nom.vmt.spreadsheet_importer.problems;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */


import ro.nom.vmt.spreadsheet_importer.util.Messages;

public class RowProblem extends SheetProblem {

    private final Integer rowNo;

    public RowProblem(String sheetName, Integer rowNo) {
        super(sheetName);
        this.rowNo = rowNo;
    }

    public Integer getRowNo() {
        return rowNo;
    }


    @Override
    public String toString() {
        return String.format(Messages.ROW_PROBLEM, getSheetName(), rowNo == null ? "------" : Integer.toString(rowNo + 1));

    }

}
