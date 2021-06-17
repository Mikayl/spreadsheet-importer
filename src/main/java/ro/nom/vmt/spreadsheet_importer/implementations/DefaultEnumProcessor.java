package ro.nom.vmt.spreadsheet_importer.implementations;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import ro.nom.vmt.spreadsheet_importer.interfaces.ColumnPreProcessor;

import java.text.Normalizer;
import java.util.function.UnaryOperator;

public class DefaultEnumProcessor implements ColumnPreProcessor {
    @Override
    public UnaryOperator<String> processFunction() {
        return s -> Normalizer.normalize(s, Normalizer.Form.NFKD)
                .trim()
                .replaceAll("[^\\p{ASCII}]", "")
                .replaceAll(" ", "_")
                .toUpperCase();
    }
}
