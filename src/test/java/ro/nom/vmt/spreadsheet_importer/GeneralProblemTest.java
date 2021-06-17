package ro.nom.vmt.spreadsheet_importer;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import org.junit.jupiter.api.Test;
import ro.nom.vmt.demo.dto.EmployeeNamed;
import ro.nom.vmt.demo.dto.EmployeeNamedImportMapper;
import ro.nom.vmt.spreadsheet_importer.importing.ImportData;
import ro.nom.vmt.spreadsheet_importer.importing.Importer;
import ro.nom.vmt.spreadsheet_importer.problems.ColumnNotPresentProblem;
import ro.nom.vmt.spreadsheet_importer.problems.FileProblem;
import ro.nom.vmt.spreadsheet_importer.problems.SheetNotPresent;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class GeneralProblemTest {


    @Test
    public void importNoSheetsNamedTest() throws IOException {

        InputStream is = new FileInputStream(this.getClass().getClassLoader().getResource("Employees_no_matching_spreadsheet.xlsx").getFile());
        ImportData<EmployeeNamed> importData = Importer.build(EmployeeNamedImportMapper.class).process(is);

        assertFalse(importData.isValid());
        assertEquals(0, importData.getRowNoTotal());
        assertEquals(1, importData.getValidationProblems().size());
        assertTrue(importData.getValidationProblems().get(0) instanceof SheetNotPresent);
    }

    @Test
    public void importNoSheetsOrdinalTest() throws IOException {

        InputStream is = new FileInputStream(this.getClass().getClassLoader().getResource("Employees_no_matching_spreadsheet.xlsx").getFile());
        ImportData<EmployeeNamed> importData = Importer.build(EmployeeNamedImportMapper.class).process(is);

        assertFalse(importData.isValid());
        assertEquals(0, importData.getRowNoTotal());
        assertEquals(1, importData.getValidationProblems().size());
        assertTrue(importData.getValidationProblems().get(0) instanceof SheetNotPresent);
    }

    @Test
    public void importRequiredColumnMissingTest() throws IOException {

        InputStream is = new FileInputStream(this.getClass().getClassLoader().getResource("Employees_required_column_missing.xlsx").getFile());
        ImportData<EmployeeNamed> importData = Importer.build(EmployeeNamedImportMapper.class).process(is);

        assertFalse(importData.isValid());
        assertEquals(0, importData.getRowNoTotal());
        assertEquals(8, importData.getValidationProblems().size());
        assertTrue(importData.getValidationProblems().get(0) instanceof ColumnNotPresentProblem);
    }

    @Test
    public void importNotExcelTest() throws IOException {

        InputStream is = new FileInputStream(this.getClass().getClassLoader().getResource("sample.pdf").getFile());
        ImportData<EmployeeNamed> importData = Importer.build(EmployeeNamedImportMapper.class).process(is);

        assertFalse(importData.isValid());
        assertEquals(1, importData.getValidationProblems().size());
        assertTrue(importData.getValidationProblems().get(0) instanceof FileProblem);
    }
}
