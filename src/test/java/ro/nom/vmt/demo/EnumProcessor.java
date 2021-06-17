package ro.nom.vmt.demo;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import ro.nom.vmt.spreadsheet_importer.interfaces.ColumnPreProcessor;

import java.util.function.UnaryOperator;

public class EnumProcessor implements ColumnPreProcessor {
    @Override
    public UnaryOperator<String> processFunction() {
        return s -> s.trim().toUpperCase();
    }
}
