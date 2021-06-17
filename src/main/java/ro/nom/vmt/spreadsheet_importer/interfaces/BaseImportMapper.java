package ro.nom.vmt.spreadsheet_importer.interfaces;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import org.apache.poi.ss.usermodel.Row;
import ro.nom.vmt.spreadsheet_importer.importing.RowContext;
import ro.nom.vmt.spreadsheet_importer.util.Pair;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public interface BaseImportMapper<T> {

    T map(RowContext<T> ctx, Row row, Map<String, LinkedHashMap<String, Integer>> columnPositions);

    boolean hasHeader();

    boolean isNamed();

    Map<String, String> getColumnNames();

    Map<String, Integer> getColumnPositions();

    Map<String, Pair<Integer, Integer>> getColumnRequiredAppearances();

    Set<String> getSheetNames();

    Set<Integer> getSheetPositions();


}
