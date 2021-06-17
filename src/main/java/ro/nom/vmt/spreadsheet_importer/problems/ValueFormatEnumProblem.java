package ro.nom.vmt.spreadsheet_importer.problems;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import ro.nom.vmt.spreadsheet_importer.util.Messages;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ValueFormatEnumProblem extends ValueFormatProblem {

    private final Class<? extends Enum<?>> enumClass;

    public ValueFormatEnumProblem(String sheetName, Integer rowNo, Integer columnIndex, String column, String value, Class<? extends Enum<?>> enumClass) {
        super(sheetName, rowNo, columnIndex, column, value);
        this.enumClass = enumClass;

    }


    @Override
    public String toString() {
        String allowedValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining(";"));
        return String.format(Messages.VALUE_FORMAT_ENUM_PROBLEM, getSheetName(), getRowNo() + 1, value, column == null ? "" : column, columnIndex, allowedValues);
    }
}
