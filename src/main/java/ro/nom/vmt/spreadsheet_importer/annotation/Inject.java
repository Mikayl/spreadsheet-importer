package ro.nom.vmt.spreadsheet_importer.annotation;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Inject {

    InjectType value();

    enum InjectType {
        IMPORT_INDEX,
        ROW_NUMBER,
        SHEET_NAME,
        SHEET_INDEX,
        UNMATCHED_COLUMNS,
    }

}
