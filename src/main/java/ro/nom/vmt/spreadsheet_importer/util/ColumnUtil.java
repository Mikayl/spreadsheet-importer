package ro.nom.vmt.spreadsheet_importer.util;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.nom.vmt.spreadsheet_importer.importing.RowContext;
import ro.nom.vmt.spreadsheet_importer.problems.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;


public class ColumnUtil {

    private static final Logger log = LoggerFactory.getLogger(ColumnUtil.class);

    private static final DataFormatter dataFormatter = new DataFormatter();


    private static final List<String> trueAliases = Arrays.asList("TRUE", "1", "T", "Y", "YES");
    private static final List<String> falseAliases = Arrays.asList("FALSE", "0", "F", "N", "NO");

    private ColumnUtil() {
        throw new IllegalAccessError();
    }

    public static <T> Byte getByte(RowContext<T> rowContext, Row row, int columnIndex, ColumnOptions options) {
        Double d = getDouble(rowContext, row, columnIndex, options);
        if (d == null) {
            return null;
        } else {
            if (d > Byte.MAX_VALUE || d < Byte.MIN_VALUE) {
                rowContext.addValidationProblem(new ValueRangeProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName(), d.toString()));
                return null;
            }

            return d.byteValue();
        }
    }


    public static <T> Short getShort(RowContext<T> rowContext, Row row, int columnIndex, ColumnOptions options) {
        Double d = getDouble(rowContext, row, columnIndex, options);
        if (d == null) {
            return null;
        } else {
            if (d > Short.MAX_VALUE || d < Short.MIN_VALUE) {
                rowContext.addValidationProblem(new ValueRangeProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName(), d.toString()));
                return null;
            }
            return d.shortValue();
        }
    }


    public static <T> Integer getInteger(RowContext<T> rowContext, Row row, int columnIndex, ColumnOptions options) {
        Double d = getDouble(rowContext, row, columnIndex, options);
        if (d == null) {
            return null;
        } else {
            if (d > Integer.MAX_VALUE || d < Integer.MIN_VALUE) {
                rowContext.addValidationProblem(new ValueRangeProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName(), d.toString()));
                return null;
            }
            return d.intValue();
        }
    }


    public static <T> Long getLong(RowContext<T> rowContext, Row row, int columnIndex, ColumnOptions options) {
        Double d = getDouble(rowContext, row, columnIndex, options);
        if (d == null) {
            return null;
        } else {
            if (d > Long.MAX_VALUE || d < Long.MIN_VALUE) {
                rowContext.addValidationProblem(new ValueRangeProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName(), d.toString()));
                return null;
            }
            return d.longValue();
        }
    }


    public static <T> Float getFloat(RowContext<T> rowContext, Row row, int columnIndex, ColumnOptions options) {
        Double d = getDouble(rowContext, row, columnIndex, options);
        if (d == null) {
            return null;
        } else {
            if (d > Float.MAX_VALUE || d < -Float.MAX_VALUE) {
                rowContext.addValidationProblem(new ValueRangeProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName(), d.toString()));
                return null;
            }
            return d.floatValue();
        }
    }


    public static <T> Double getDouble(RowContext<T> rowContext, Row row, int columnIndex, ColumnOptions options) {
        try {
            Cell cell = row.getCell(columnIndex);
            if (cell == null || cell.getCellType().equals(CellType.BLANK)) {
                if (options.isRequired()) {
                    rowContext.addValidationProblem(new ValueNullProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName()));
                    return null;
                }
                return null;
            }
            if (cell.getCellType().equals(CellType.NUMERIC)) {
                return cell.getNumericCellValue();
            }
            if (cell.getCellType().equals(CellType.FORMULA)) {
                CellValue cellValue = getCellValueFromFormula(cell, rowContext, row, columnIndex, options);
                if (cellValue == null) {
                    return null;
                }
                if (cellValue.getCellType().equals(CellType.NUMERIC)) {
                    return cellValue.getNumberValue();
                }
                return getaParsedDoubleFromCell(rowContext, row, columnIndex, options, cellValue.formatAsString());

            }
            String string = getString(rowContext, row, columnIndex, options);
            if (string != null) {
                return getaParsedDoubleFromCell(rowContext, row, columnIndex, options, string);
            } else {
                return null;
            }


        } catch (Exception e) {
            log.warn("Problem processing row: {}@#{}, column: {}@#{}", row.getSheet().getSheetName(), row.getRowNum(), options.getColumnName(), columnIndex, e);
            rowContext.addValidationProblem(new ValueProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName(), null));
            return null;
        }
    }

    private static <T> Double getaParsedDoubleFromCell(RowContext<T> rowContext, Row row, int columnIndex, ColumnOptions options, String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException nfe) {
            rowContext.addValidationProblem(new ValueFormatProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName(), s));
            return null;
        }
    }


    public static <T> String getString(RowContext<T> rowContext, Row row, int columnIndex, ColumnOptions options) {
        String string;
        try {
            Cell cell = row.getCell(columnIndex);
            if (cell == null) {
                if (options.isRequired()) {
                    rowContext.addValidationProblem(new ValueNullProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName()));
                }
                return null;

            }
            if (cell.getCellType().equals(CellType.FORMULA)) {
                CellValue cellValue = getCellValueFromFormula(cell, rowContext, row, columnIndex, options);
                if (cellValue == null) {
                    return null;
                }
                return cellValue.formatAsString();
            }
            string = getStringFromCell(cell, rowContext, row, columnIndex, options);
            if (string == null) {
                return null;
            }
            String regex = options.getMatches();
            if (regex != null && !regex.isEmpty() && !string.matches(regex)) {
                rowContext.addValidationProblem(new ValueFormatRegexProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName(), string, regex));
                return null;

            }

            return string;


        } catch (Exception e) {
            log.warn("Problem processing row: {}@#{}, column: {}@#{}", row.getSheet().getSheetName(), row.getRowNum(), options.getColumnName(), columnIndex, e);
            rowContext.addValidationProblem(new ValueProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName(), null));
            return null;
        }


    }


    public static <T> Boolean getBoolean(RowContext<T> rowContext, Row row, int columnIndex, ColumnOptions options) {
        String string = getString(rowContext, row, columnIndex, options);

        if (string != null) {
            if (trueAliases.contains(string.toUpperCase())) {
                return Boolean.TRUE;
            }
            if (falseAliases.contains(string.toUpperCase())) {
                return Boolean.FALSE;
            }
            rowContext.addValidationProblem(new ValueFormatProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName(), string));
        }

        return null;

    }

    public static <T> LocalDateTime getLocalDateTime(RowContext<T> rowContext, Row row, int columnIndex, ColumnOptions options) {
        try {
            Cell cell = row.getCell(columnIndex);
            if (cell == null) {
                if (options.isRequired()) {
                    rowContext.addValidationProblem(new ValueNullProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName()));
                }
                return null;
            }
            if (cell.getCellType().equals(CellType.NUMERIC)) {
                return cell.getLocalDateTimeCellValue();
            }
            if (cell.getCellType().equals(CellType.FORMULA)) {
                return getLocalDateTimeIfFormula(cell, rowContext, row, columnIndex, options);
            }


            String matches = options.getMatches();
            String string = getStringFromCell(cell, rowContext, row, columnIndex, options);
            if (string == null) {
                return null;
            }
            if (matches != null && !matches.isEmpty()) {
                return LocalDateTime.parse(string, DateTimeFormatter.ofPattern(matches));
            } else {
                return LocalDateTime.parse(string);
            }


        } catch (Exception ex) {
            log.warn("Problem processing row: {}@#{}, column: {}@#{}", row.getSheet().getSheetName(), row.getRowNum(), options.getColumnName(), columnIndex, ex);
            rowContext.addValidationProblem(new ValueProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName(), null));
            return null;
        }
    }

    public static <T> LocalDate getLocalDate(RowContext<T> rowContext, Row row, int columnIndex, ColumnOptions options) {
        try {
            Cell cell = row.getCell(columnIndex);

            if (cell == null) {
                if (options.isRequired()) {
                    rowContext.addValidationProblem(new ValueNullProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName()));
                }
                return null;
            }
            if (cell.getCellType().equals(CellType.NUMERIC)) {
                return cell.getLocalDateTimeCellValue().toLocalDate();
            }

            if (cell.getCellType().equals(CellType.FORMULA)) {
                LocalDateTime localDateTime = getLocalDateTimeIfFormula(cell, rowContext, row, columnIndex, options);
                if (localDateTime == null) {
                    return null;
                } else {
                    return localDateTime.toLocalDate();
                }

            }

            String matches = options.getMatches();
            String string = getStringFromCell(cell, rowContext, row, columnIndex, options);
            if (string == null) {
                return null;
            }
            if (matches != null && !matches.isEmpty()) {
                return LocalDate.parse(string, DateTimeFormatter.ofPattern(matches));
            } else {
                return LocalDate.parse(string);
            }


        } catch (Exception ex) {
            log.warn("Problem processing row: {}@#{}, column: {}@#{}", row.getSheet().getSheetName(), row.getRowNum(), options.getColumnName(), columnIndex, ex);
            rowContext.addValidationProblem(new ValueProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName(), null));
            return null;
        }
    }

    public static <T> LocalTime getLocalTime(RowContext<T> rowContext, Row row, int columnIndex, ColumnOptions options) {
        try {
            Cell cell = row.getCell(columnIndex);

            if (cell == null) {
                if (options.isRequired()) {
                    rowContext.addValidationProblem(new ValueNullProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName()));
                }
                return null;
            }
            if (cell.getCellType().equals(CellType.NUMERIC)) {
                return cell.getLocalDateTimeCellValue().toLocalTime();
            }

            if (cell.getCellType().equals(CellType.FORMULA)) {
                LocalDateTime localDateTime = getLocalDateTimeIfFormula(cell, rowContext, row, columnIndex, options);
                if (localDateTime == null) {
                    return null;
                } else {
                    return localDateTime.toLocalTime();
                }
            }

            String matches = options.getMatches();
            String string = getStringFromCell(cell, rowContext, row, columnIndex, options);
            if (string == null) {
                return null;
            }
            if (matches != null && !matches.isEmpty()) {
                return LocalTime.parse(string, DateTimeFormatter.ofPattern(matches));
            } else {
                return LocalTime.parse(string);
            }


        } catch (Exception ex) {
            log.warn("Problem processing row: {}@#{}, column: {}@#{}", row.getSheet().getSheetName(), row.getRowNum(), options.getColumnName(), columnIndex, ex);
            rowContext.addValidationProblem(new ValueProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName(), null));
            return null;
        }
    }

    private static <T> LocalDateTime getLocalDateTimeIfFormula(Cell cell, RowContext<T> rowContext, Row row, int columnIndex, ColumnOptions options) {
        CellValue cellValue = getCellValueFromFormula(cell, rowContext, row, columnIndex, options);
        if (cellValue == null) {
            return null;
        }
        try {
            return cell.getLocalDateTimeCellValue();
        } catch (Exception e) {
            log.warn("Problem processing row: {}@#{}, column: {}@#{}", row.getSheet().getSheetName(), row.getRowNum(), options.getColumnName(), columnIndex, e);
            rowContext.addValidationProblem(new ValueProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName(), cellValue.formatAsString()));
            return null;
        }
    }

    private static <T> String getStringFromCell(Cell cell, RowContext<T> rowContext, Row row, int columnIndex, ColumnOptions options) {
        String string = dataFormatter.formatCellValue(cell);
        if (string == null || string.isEmpty()) {
            if (options.isRequired()) {
                rowContext.addValidationProblem(new ValueNullProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName()));
            }
            return null;
        }
        string = processString(string, options.getPreProcess());
        if (options.isTrim()) {
            string = string.trim();
        }
        return string;
    }


    private static String processString(String str, List<UnaryOperator<String>> preProcess) {
        if (preProcess == null || preProcess.isEmpty()) {
            return str;
        }
        String processedStr = str;
        for (UnaryOperator<String> processor : preProcess) {
            processedStr = processor.apply(processedStr);
        }
        return processedStr;
    }

    private static <T> CellValue getCellValueFromFormula(Cell cell, RowContext<T> rowContext, Row row, int columnIndex, ColumnOptions options) {
        if (!options.isFormulaAllowed()) {
            rowContext.addValidationProblem(new ValueFormulaNotAllowedProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName(), cell.getCellFormula()));
            return null;
        }
        FormulaEvaluator evaluator = row.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
        CellValue cellValue = evaluator.evaluate(cell);
        if (cellValue == null || cellValue.formatAsString().isEmpty()) {
            if (options.isRequired()) {
                rowContext.addValidationProblem(new ValueNullProblem(row.getSheet().getSheetName(), row.getRowNum(), columnIndex, options.getColumnName()));
            }
            return null;
        }
        return cellValue;
    }


}