package ro.nom.vmt.spreadsheet_importer.interfaces;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import ro.nom.vmt.spreadsheet_importer.problems.RowProblem;

import java.util.List;

@FunctionalInterface
public interface Validatable {

    List<RowProblem> validate(String sheetName, Integer rowNo);
}
