package ro.nom.vmt.spreadsheet_importer.interfaces;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import java.util.function.UnaryOperator;

@FunctionalInterface
public interface ColumnPreProcessor {

    UnaryOperator<String> processFunction();
}
