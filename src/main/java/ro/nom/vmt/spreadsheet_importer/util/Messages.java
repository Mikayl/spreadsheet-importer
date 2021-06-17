package ro.nom.vmt.spreadsheet_importer.util;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class Messages {
    public static final String COLUMN_NOT_PRESENT;
    public static final String FILE_PROBLEM;
    public static final String SHEET_PROBLEM;
    public static final String SHEET_NOT_PRESENT;
    public static final String ROW_PROBLEM;
    public static final String VALUE_FORMAT_PROBLEM;
    public static final String VALUE_FORMAT_REGEX_PROBLEM;
    public static final String VALUE_FORMAT_ENUM_PROBLEM;
    public static final String VALUE_FORMULA_NOT_ALLOWED_PROBLEM;
    public static final String VALUE_NULL_PROBLEM;
    public static final String VALUE_PROBLEM;
    public static final String VALUE_RANGE_PROBLEM;
    private static final Logger log = LoggerFactory.getLogger(Messages.class);

    static {
        ClassLoader classLoader = ClassLoader.getSystemClassLoader();

        String columnNotPresent = null;
        String fileProblem = null;
        String sheetProblem = null;
        String sheetNotPresent = null;
        String rowProblem = null;
        String valueFormatProblem = null;
        String valueFormatRegexProblem = null;
        String valueFormatEnumProblem = null;
        String valueFormulaNotAllowedProblem = null;
        String valueNullProblem = null;
        String valueProblem = null;
        String valueRangeProblem = null;

        try (InputStream is = classLoader.getResourceAsStream("sheet_importer.messages")) {
            if (is != null) {
                Properties properties = new Properties();
                properties.load(is);
                columnNotPresent = Objects.requireNonNull(properties.get("columnNotPresent")).toString();
                fileProblem = Objects.requireNonNull(properties.get("fileProblem")).toString();
                sheetProblem = Objects.requireNonNull(properties.get("sheetProblem")).toString();
                sheetNotPresent = Objects.requireNonNull(properties.get("sheetNotPresent")).toString();
                rowProblem = Objects.requireNonNull(properties.get("rowProblem")).toString();
                valueFormatProblem = Objects.requireNonNull(properties.get("valueFormatProblem")).toString();
                valueFormatRegexProblem = Objects.requireNonNull(properties.get("valueFormatRegexProblem")).toString();
                valueFormatEnumProblem = Objects.requireNonNull(properties.get("valueFormatEnumProblem")).toString();
                valueFormulaNotAllowedProblem = Objects.requireNonNull(properties.get("valueFormulaNotAllowedProblem")).toString();
                valueNullProblem = Objects.requireNonNull(properties.get("valueNullProblem")).toString();
                valueProblem = Objects.requireNonNull(properties.get("valueProblem")).toString();
                valueRangeProblem = Objects.requireNonNull(properties.get("valueRangeProblem")).toString();

            }
        } catch (IOException e) {
            log.error("Problem loading message formats from file", e);
        } finally {
            COLUMN_NOT_PRESENT = columnNotPresent;
            FILE_PROBLEM = fileProblem;
            SHEET_PROBLEM = sheetProblem;
            SHEET_NOT_PRESENT = sheetNotPresent;
            ROW_PROBLEM = rowProblem;
            VALUE_FORMAT_PROBLEM = valueFormatProblem;
            VALUE_FORMAT_REGEX_PROBLEM = valueFormatRegexProblem;
            VALUE_FORMAT_ENUM_PROBLEM = valueFormatEnumProblem;
            VALUE_FORMULA_NOT_ALLOWED_PROBLEM = valueFormulaNotAllowedProblem;
            VALUE_NULL_PROBLEM = valueNullProblem;
            VALUE_PROBLEM = valueProblem;
            VALUE_RANGE_PROBLEM = valueRangeProblem;
        }


    }


    private Messages() {
        throw new IllegalAccessError();
    }


}
