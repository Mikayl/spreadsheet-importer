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
import ro.nom.vmt.spreadsheet_importer.problems.RowProblem;
import ro.nom.vmt.spreadsheet_importer.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NamedColumnOptionsTest {

    private final List<EmployeeNamed> employeesValid = FileData.getEmployeesNamedValid();
    private final List<EmployeeNamed> employeesInvalid = FileData.getEmployeesNamedInvalid();
    private final List<EmployeeNamed> employees = FileData.getEmployeesNamed();

    InputStream is = new FileInputStream(this.getClass().getClassLoader().getResource("Employees.xlsx").getFile());

    public NamedColumnOptionsTest() throws FileNotFoundException {
    }

    @Test
    public void importValidationTest() {
        //we test with a validator that will invalidate the first normally valid row
        BiFunction<EmployeeNamed, Pair<String, Integer>, List<RowProblem>> validator = (employeeNamed, pair) -> {
            if (employeeNamed.getFirstName().equals(employeesValid.get(0).getFirstName())) {
                return Collections.singletonList(new BusinessProblem(pair.p1, pair.p2));
            }
            return Collections.emptyList();
        };

        ImportData<EmployeeNamed> importData = Importer.builder(EmployeeNamedImportMapper.class)
                .withValidator(validator)
                .build()
                .process(is);

        assertEquals(employeesValid.size() - 1, importData.getRowNoValid());
        assertEquals(employeesValid.get(0).getSalary(), importData.getInvalidRows().get(0).getSalary());

    }


    @Test
    public void importConsumerTest() {
        AtomicInteger sumSalary = new AtomicInteger(0);
        AtomicInteger sumAge = new AtomicInteger(0);

        AtomicInteger expectedSumSalary = new AtomicInteger(0);
        AtomicInteger expectedSumAge = new AtomicInteger(0);

        Consumer<EmployeeNamed> consumer1 = employeeNamed -> sumSalary.addAndGet(employeeNamed.getSalary() == null ? 0 : employeeNamed.getSalary());
        Consumer<EmployeeNamed> consumer2 = employeeNamed -> sumAge.addAndGet(employeeNamed.getAge() == null ? 0 : employeeNamed.getAge());

        employees.forEach(employeeNamed -> expectedSumSalary.addAndGet(employeeNamed.getSalary() == null ? 0 : employeeNamed.getSalary()));
        employees.forEach(employeeNamed -> expectedSumAge.addAndGet(employeeNamed.getAge() == null ? 0 : employeeNamed.getAge()));

        Importer.builder(EmployeeNamedImportMapper.class)
                .withConsumer(consumer1)
                .withConsumer(consumer2)
                .build()
                .process(is);

        assertEquals(expectedSumSalary.get(), sumSalary.get());
        assertEquals(expectedSumSalary.get(), sumSalary.get());
    }

    @Test
    public void importConsumerValidTest() {
        AtomicInteger sumSalary = new AtomicInteger(0);
        AtomicInteger sumAge = new AtomicInteger(0);

        AtomicInteger expectedSumSalary = new AtomicInteger(0);
        AtomicInteger expectedSumAge = new AtomicInteger(0);

        Consumer<EmployeeNamed> consumer1 = employeeNamed -> sumSalary.addAndGet(employeeNamed.getSalary() == null ? 0 : employeeNamed.getSalary());
        Consumer<EmployeeNamed> consumer2 = employeeNamed -> sumAge.addAndGet(employeeNamed.getAge() == null ? 0 : employeeNamed.getAge());

        employeesValid.forEach(employeeNamed -> expectedSumSalary.addAndGet(employeeNamed.getSalary() == null ? 0 : employeeNamed.getSalary()));
        employeesValid.forEach(employeeNamed -> expectedSumAge.addAndGet(employeeNamed.getAge() == null ? 0 : employeeNamed.getAge()));

        Importer.builder(EmployeeNamedImportMapper.class)
                .withConsumerForValid(consumer1)
                .withConsumerForValid(consumer2)
                .build()
                .process(is);

        assertEquals(expectedSumSalary.get(), sumSalary.get());
        assertEquals(expectedSumSalary.get(), sumSalary.get());
    }

    @Test
    public void importConsumerInvalidTest() {
        AtomicInteger sumSalary = new AtomicInteger(0);
        AtomicInteger sumAge = new AtomicInteger(0);

        AtomicInteger expectedSumSalary = new AtomicInteger(0);
        AtomicInteger expectedSumAge = new AtomicInteger(0);

        Consumer<EmployeeNamed> consumer1 = employeeNamed -> sumSalary.addAndGet(employeeNamed.getSalary() == null ? 0 : employeeNamed.getSalary());
        Consumer<EmployeeNamed> consumer2 = employeeNamed -> sumAge.addAndGet(employeeNamed.getAge() == null ? 0 : employeeNamed.getAge());

        employeesInvalid.forEach(employeeNamed -> expectedSumSalary.addAndGet(employeeNamed.getSalary() == null ? 0 : employeeNamed.getSalary()));
        employeesInvalid.forEach(employeeNamed -> expectedSumAge.addAndGet(employeeNamed.getAge() == null ? 0 : employeeNamed.getAge()));

        Importer.builder(EmployeeNamedImportMapper.class)
                .withConsumerForInvalid(consumer1)
                .withConsumerForInvalid(consumer2)
                .build()
                .process(is);

        assertEquals(expectedSumSalary.get(), sumSalary.get());
        assertEquals(expectedSumSalary.get(), sumSalary.get());
    }


}
