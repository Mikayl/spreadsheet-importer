package ro.nom.vmt.spreadsheet_importer.ordinal;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */


import org.junit.jupiter.api.Test;
import ro.nom.vmt.data.FileData;
import ro.nom.vmt.demo.dto.EmployeeOrdinal;
import ro.nom.vmt.demo.dto.EmployeeOrdinalImportMapper;
import ro.nom.vmt.spreadsheet_importer.importing.ImportData;
import ro.nom.vmt.spreadsheet_importer.importing.Importer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class OrdinalTest {

    private final ImportData<EmployeeOrdinal> importData;
    private final List<EmployeeOrdinal> employeesValid = FileData.getEmployeesOrdinalValid();
    private final List<EmployeeOrdinal> employees = FileData.getEmployeesOrdinal();

    public OrdinalTest() throws IOException {
        InputStream is = new FileInputStream(this.getClass().getClassLoader().getResource("Employees.xlsx").getFile());
        importData = Importer.build(EmployeeOrdinalImportMapper.class).process(is);

        assertEquals(employees.size(), importData.getRowNoTotal());
        assertEquals(employees.size(), importData.getAllRows().size());

        assertEquals(employeesValid.size(), importData.getRowNoValid());
        assertEquals(employeesValid.size(), importData.getValidRows().size());
    }


    @Test
    public void importStringTest() {

        for (int i = 0; i < employeesValid.size(); i++) {
            assertEquals(employeesValid.get(i).getFirstName(), importData.getValidRows().get(i).getFirstName());
            assertEquals(employeesValid.get(i).getPhone(), importData.getValidRows().get(i).getPhone());
        }
    }

    @Test
    public void importBooleanTest() {

        for (int i = 0; i < employees.size(); i++) {
            assertEquals(employees.get(i).getIsMarried(), importData.getAllRows().get(i).getIsMarried());
        }
    }

    @Test
    public void importByteTest() {

        for (int i = 0; i < employees.size(); i++) {
            assertEquals(employees.get(i).getAge(), importData.getAllRows().get(i).getAge());
        }
    }

    @Test
    public void importIntegerTest() {

        for (int i = 0; i < employees.size(); i++) {
            assertEquals(employees.get(i).getSalary(), importData.getAllRows().get(i).getSalary());
        }
    }

    @Test
    public void importEnumTest() {

        for (int i = 0; i < employees.size(); i++) {
            assertEquals(employees.get(i).getGender(), importData.getAllRows().get(i).getGender());
        }
    }

    @Test
    public void importCustomObjectTest() {

        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getEmail() == null) {
                assertNull(importData.getAllRows().get(i).getEmail());
            } else {
                assertEquals(employees.get(i).getEmail().getAddress(), importData.getAllRows().get(i).getEmail().getAddress());
                assertEquals(employees.get(i).getEmail().getProvider(), importData.getAllRows().get(i).getEmail().getProvider());
                assertEquals(employees.get(i).getEmail().getTld(), importData.getAllRows().get(i).getEmail().getTld());
            }
        }
    }

    @Test
    public void importGeneratedTest() {

        for (int i = 0; i < employees.size(); i++) {
            assertEquals(employees.get(i).getSheetIndex(), importData.getAllRows().get(i).getSheetIndex());
            assertEquals(employees.get(i).getSheetName(), importData.getAllRows().get(i).getSheetName());
            assertEquals(employees.get(i).getImportIndex(), importData.getAllRows().get(i).getImportIndex());
            assertEquals(employees.get(i).getRowNumber(), importData.getAllRows().get(i).getRowNumber());
        }
    }

    @Test
    public void importLocalDateTimeTest() {

        for (int i = 0; i < employees.size(); i++) {
            assertEquals(employees.get(i).getLastLogin(), importData.getAllRows().get(i).getLastLogin());
        }
    }

    @Test
    public void importLocalDateTimeFormattedTest() {

        for (int i = 0; i < employees.size(); i++) {
            assertEquals(employees.get(i).getPasswordExpires(), importData.getAllRows().get(i).getPasswordExpires());
        }
    }

    @Test
    public void importLocalDateTest() {

        for (int i = 0; i < employees.size(); i++) {
            assertEquals(employees.get(i).getHiredOn(), importData.getAllRows().get(i).getHiredOn());
        }
    }

    @Test
    public void importLocalTimeTest() {

        for (int i = 0; i < employees.size(); i++) {
            assertEquals(employees.get(i).getStartsWorkAt(), importData.getAllRows().get(i).getStartsWorkAt());
        }
    }

    @Test
    public void importPreProcessTest() {

        for (int i = 0; i < employees.size(); i++) {
            assertEquals(employees.get(i).getLastName(), importData.getAllRows().get(i).getLastName());
        }
    }


    @Test
    public void checkNoOfSheetsTest() {

        assertEquals(2, importData.getNoOfSheets());
    }


}
