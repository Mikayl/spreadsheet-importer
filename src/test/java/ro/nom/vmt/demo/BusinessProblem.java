package ro.nom.vmt.demo;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import ro.nom.vmt.spreadsheet_importer.problems.RowProblem;

public class BusinessProblem extends RowProblem {


    public BusinessProblem(String sheetName, Integer rowNo) {
        super(sheetName, rowNo);
    }
}
