package ro.nom.vmt.spreadsheet_importer.ordinal;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */


import org.junit.jupiter.api.Test;
import ro.nom.vmt.data.FileData;
import ro.nom.vmt.demo.BusinessProblem;
import ro.nom.vmt.demo.dto.EmployeeOrdinal;
import ro.nom.vmt.demo.dto.EmployeeOrdinalImportMapper;
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

public class OrdinalColumnOptionsTest {

    private final List<EmployeeOrdinal> employeesValid = FileData.getEmployeesOrdinalValid();
    private final List<EmployeeOrdinal> employeesInvalid = FileData.getEmployeesOrdinalInvalid();
    private final List<EmployeeOrdinal> employees = FileData.getEmployeesOrdinal();

    InputStream is = new FileInputStream(this.getClass().getClassLoader().getResource("Employees.xlsx").getFile());

    public OrdinalColumnOptionsTest() throws FileNotFoundException {
    }

    @Test
    public void importValidationTest() {
        //we test with a validator that will invalidate the first normally valid row
        BiFunction<EmployeeOrdinal, Pair<String, Integer>, List<RowProblem>> validator = (employeeOrdinal, pair) -> {
            if (employeeOrdinal.getFirstName().equals(employeesValid.get(0).getFirstName())) {
                return Collections.singletonList(new BusinessProblem(pair.p1, pair.p2));
            }
            return Collections.emptyList();
        };

        ImportData<EmployeeOrdinal> importData = Importer.builder(EmployeeOrdinalImportMapper.class)
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

        Consumer<EmployeeOrdinal> consumer1 = EmployeeOrdinal -> sumSalary.addAndGet(EmployeeOrdinal.getSalary() == null ? 0 : EmployeeOrdinal.getSalary());
        Consumer<EmployeeOrdinal> consumer2 = EmployeeOrdinal -> sumAge.addAndGet(EmployeeOrdinal.getAge() == null ? 0 : EmployeeOrdinal.getAge());

        employees.forEach(EmployeeOrdinal -> expectedSumSalary.addAndGet(EmployeeOrdinal.getSalary() == null ? 0 : EmployeeOrdinal.getSalary()));
        employees.forEach(EmployeeOrdinal -> expectedSumAge.addAndGet(EmployeeOrdinal.getAge() == null ? 0 : EmployeeOrdinal.getAge()));

        Importer.builder(EmployeeOrdinalImportMapper.class)
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

        Consumer<EmployeeOrdinal> consumer1 = EmployeeOrdinal -> sumSalary.addAndGet(EmployeeOrdinal.getSalary() == null ? 0 : EmployeeOrdinal.getSalary());
        Consumer<EmployeeOrdinal> consumer2 = EmployeeOrdinal -> sumAge.addAndGet(EmployeeOrdinal.getAge() == null ? 0 : EmployeeOrdinal.getAge());

        employeesValid.forEach(EmployeeOrdinal -> expectedSumSalary.addAndGet(EmployeeOrdinal.getSalary() == null ? 0 : EmployeeOrdinal.getSalary()));
        employeesValid.forEach(EmployeeOrdinal -> expectedSumAge.addAndGet(EmployeeOrdinal.getAge() == null ? 0 : EmployeeOrdinal.getAge()));

        Importer.builder(EmployeeOrdinalImportMapper.class)
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

        Consumer<EmployeeOrdinal> consumer1 = EmployeeOrdinal -> sumSalary.addAndGet(EmployeeOrdinal.getSalary() == null ? 0 : EmployeeOrdinal.getSalary());
        Consumer<EmployeeOrdinal> consumer2 = EmployeeOrdinal -> sumAge.addAndGet(EmployeeOrdinal.getAge() == null ? 0 : EmployeeOrdinal.getAge());

        employeesInvalid.forEach(EmployeeOrdinal -> expectedSumSalary.addAndGet(EmployeeOrdinal.getSalary() == null ? 0 : EmployeeOrdinal.getSalary()));
        employeesInvalid.forEach(EmployeeOrdinal -> expectedSumAge.addAndGet(EmployeeOrdinal.getAge() == null ? 0 : EmployeeOrdinal.getAge()));

        Importer.builder(EmployeeOrdinalImportMapper.class)
                .withConsumerForInvalid(consumer1)
                .withConsumerForInvalid(consumer2)
                .build()
                .process(is);

        assertEquals(expectedSumSalary.get(), sumSalary.get());
        assertEquals(expectedSumSalary.get(), sumSalary.get());
    }


}
