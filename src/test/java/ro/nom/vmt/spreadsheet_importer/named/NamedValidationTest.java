package ro.nom.vmt.spreadsheet_importer.named;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */


import org.junit.jupiter.api.Test;
import ro.nom.vmt.data.FileData;
import ro.nom.vmt.demo.BusinessProblem;
import ro.nom.vmt.demo.dto.EmployeeNamed;
import ro.nom.vmt.demo.dto.EmployeeNamedImportMapper;
import ro.nom.vmt.spreadsheet_importer.importing.ImportData;
import ro.nom.vmt.spreadsheet_importer.importing.Importer;
import ro.nom.vmt.spreadsheet_importer.interfaces.Problem;
import ro.nom.vmt.spreadsheet_importer.problems.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NamedValidationTest {

    private final ImportData<EmployeeNamed> importData;
    private final List<EmployeeNamed> employees = FileData.getEmployeesNamed();

    public NamedValidationTest() throws IOException {
        InputStream is = new FileInputStream(this.getClass().getClassLoader().getResource("Employees.xlsx").getFile());
        importData = Importer.build(EmployeeNamedImportMapper.class).process(is);
    }

    @Test
    public void importProblemsAllTest() {

        List<EmployeeNamed> employeesImported = importData.getAllRows();

        assertEquals(employees.size(), employeesImported.size());

        for (int i = 0; i < employees.size(); i++) {
            for (Problem problem : importData.getValidationProblems(employeesImported.get(i))) {
                //if this fails it means there is a problem being found that is not declared in FileData
                assertTrue(employees.get(i).getExpectedValidationProblems().contains(problem.getClass()));
            }
        }
    }

    @Test
    public void importColumnValueRequiredButNullTest() {

        List<EmployeeNamed> employeeNamedList = importData.getInvalidRows(ValueNullProblem.class);
        List<Problem> problemList = importData.getValidationProblems(ValueNullProblem.class);

        assertEquals(2, employeeNamedList.size());
        assertEquals(2, problemList.size());
    }

    @Test
    public void importColumnFormatTest() {

        List<EmployeeNamed> employeeNamedList = importData.getInvalidRows(ValueFormatProblem.class);
        List<Problem> problemList = importData.getValidationProblems(ValueFormatProblem.class);

        assertEquals(2, employeeNamedList.size());
        assertEquals(2, problemList.size());
    }

    @Test
    public void importColumnFormatEnumTest() {

        List<EmployeeNamed> employeeNamedList = importData.getInvalidRows(ValueFormatEnumProblem.class);
        List<Problem> problemList = importData.getValidationProblems(ValueFormatEnumProblem.class);

        assertEquals(1, employeeNamedList.size());
        assertEquals(1, problemList.size());
    }

    @Test
    public void importColumnFormatRegexTest() {

        List<EmployeeNamed> employeeNamedList = importData.getInvalidRows(ValueFormatRegexProblem.class);
        List<Problem> problemList = importData.getValidationProblems(ValueFormatRegexProblem.class);

        assertEquals(2, employeeNamedList.size());
        assertEquals(3, problemList.size());
    }

    @Test
    public void importColumnFormulaRegexTest() {

        List<EmployeeNamed> employeeNamedList = importData.getInvalidRows(ValueFormulaNotAllowedProblem.class);
        List<Problem> problemList = importData.getValidationProblems(ValueFormulaNotAllowedProblem.class);

        assertEquals(1, employeeNamedList.size());
        assertEquals(1, problemList.size());
    }

    @Test
    public void importBusinessProblemTest() {

        List<EmployeeNamed> employeeNamedList = importData.getInvalidRows(BusinessProblem.class);
        List<Problem> problemList = importData.getValidationProblems(BusinessProblem.class);

        assertEquals(1, employeeNamedList.size());
        assertEquals(1, problemList.size());
    }


}
