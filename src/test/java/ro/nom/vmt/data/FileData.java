package ro.nom.vmt.data;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import ro.nom.vmt.demo.BusinessProblem;
import ro.nom.vmt.demo.Email;
import ro.nom.vmt.demo.GenderEnum;
import ro.nom.vmt.demo.dto.EmployeeNamed;
import ro.nom.vmt.demo.dto.EmployeeOrdinal;
import ro.nom.vmt.spreadsheet_importer.problems.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class FileData {

    public static final String EMAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    private static final List<EmployeeOrdinal> employeeOrdinalList;
    private static final List<EmployeeOrdinal> employeeOrdinalListValid;
    private static final List<EmployeeOrdinal> employeeOrdinalListInvalid;

    private static final List<EmployeeNamed> employeeNamedList;
    private static final List<EmployeeNamed> employeeNamedListValid;
    private static final List<EmployeeNamed> employeeNamedListInvalid;

    static {
        employeeOrdinalList = new ArrayList<>();
        employeeOrdinalListValid = new ArrayList<>();
        employeeOrdinalListInvalid = new ArrayList<>();

        EmployeeOrdinal employee = new EmployeeOrdinal();
        employee.setSheetIndex(0);
        employee.setSheetName("London E");
        employee.setImportIndex(1L);
        employee.setRowNumber(4);
        employee.setFirstName("Ellia");
        employee.setLastName("RICHARDSON");
        employee.setGender(GenderEnum.FEMALE);
        employee.setAge((byte) 28);
        employee.setEmail(new Email("e.richardson", "randatmail", "com"));
        employee.setPhone("013-2182-19");
        employee.setSalary(8389);
        employee.setIsMarried(Boolean.FALSE);
        employee.setHiredOn(LocalDate.of(2015, 1, 22));
        employee.setStartsWorkAt(LocalTime.of(8, 0));
        employee.setLastLogin(LocalDateTime.of(2021, 6, 11, 13, 40));
        employee.setPasswordExpires(LocalDateTime.of(2021, 9, 25, 17, 22));
        employee.setExpectedValidationProblems(new HashSet<>(Collections.emptyList()));
        employeeOrdinalList.add(employee);
        employeeOrdinalListValid.add(employee);

        employee = new EmployeeOrdinal();
        employee.setSheetIndex(0);
        employee.setSheetName("London E");
        employee.setImportIndex(2L);
        employee.setRowNumber(5);
        employee.setFirstName("Martin");
        employee.setLastName("MORGAN");
        employee.setGender(GenderEnum.MALE);
        employee.setAge((byte) 21);
        employee.setEmail(new Email("m.morgan", "randatmail", "com"));
        employee.setPhone("118-4479-55");
        employee.setSalary(3821);
        employee.setIsMarried(Boolean.FALSE);
        employee.setHiredOn(LocalDate.of(2015, 3, 17));
        employee.setStartsWorkAt(LocalTime.of(8, 30));
        employee.setLastLogin(LocalDateTime.of(2021, 6, 10, 21, 40));
        employee.setPasswordExpires(LocalDateTime.of(2021, 8, 22, 15, 22));
        employee.setExpectedValidationProblems(new HashSet<>(Collections.emptyList()));
        employeeOrdinalList.add(employee);
        employeeOrdinalListValid.add(employee);

        employee = new EmployeeOrdinal();
        employee.setSheetIndex(0);
        employee.setSheetName("London E");
        employee.setImportIndex(3L);
        employee.setRowNumber(6);
        employee.setFirstName("Eric");
//        employee.setLastName("HARRIS3"); //invalid
        employee.setGender(GenderEnum.MALE);
        employee.setAge(null); //invalid
        employee.setPhone("210-3023-63");
//        employee.setSalary(2944);   //invalid
        employee.setIsMarried(Boolean.TRUE);
        employee.setHiredOn(LocalDate.of(2015, 3, 14));
        employee.setStartsWorkAt(LocalTime.of(8, 0));
        employee.setLastLogin(LocalDateTime.of(2021, 6, 11, 12, 20));
        employee.setPasswordExpires(LocalDateTime.of(2021, 7, 2, 13, 29));
        employee.setExpectedValidationProblems(new HashSet<>(Arrays.asList(ValueNullProblem.class, ValueFormatProblem.class, ValueFormatRegexProblem.class, ValueRangeProblem.class)));
        employeeOrdinalList.add(employee);
        employeeOrdinalListInvalid.add(employee);

        employee = new EmployeeOrdinal();
        employee.setSheetIndex(0);
        employee.setSheetName("London E");
        employee.setImportIndex(4L);
        employee.setRowNumber(7);
        employee.setFirstName("Richard");
        employee.setLastName("TAYLOR");
        employee.setGender(GenderEnum.MALE);
        employee.setAge((byte) 24);
        employee.setEmail(new Email("r.taylor", "randatmail", "com"));
        employee.setPhone("661-8950-21");
        employee.setSalary(5324);
        employee.setIsMarried(Boolean.TRUE);
        employee.setHiredOn(LocalDate.of(2015, 4, 22));
        employee.setStartsWorkAt(LocalTime.of(9, 0));
        employee.setLastLogin(LocalDateTime.of(2021, 6, 11, 14, 7));
        employee.setPasswordExpires(LocalDateTime.of(2021, 8, 19, 8, 3));
        employee.setExpectedValidationProblems(new HashSet<>(Collections.emptyList()));
        employeeOrdinalList.add(employee);
        employeeOrdinalListValid.add(employee);

        employee = new EmployeeOrdinal();
        employee.setSheetIndex(0);
        employee.setSheetName("London E");
        employee.setImportIndex(5L);
        employee.setRowNumber(8);
        employee.setFirstName("Oliver");
        employee.setLastName("HUNT");
        employee.setGender(GenderEnum.MALE);
        employee.setAge((byte) 25);
        employee.setPhone("709-4582-07");
//        employee.setSalary(7125); //invalid
        employee.setIsMarried(Boolean.FALSE);
        employee.setHiredOn(LocalDate.of(2020, 11, 19));
        employee.setStartsWorkAt(LocalTime.of(9, 30));
        employee.setLastLogin(LocalDateTime.of(2021, 6, 10, 17, 58));
        employee.setPasswordExpires(LocalDateTime.of(2021, 6, 12, 11, 17));
        employee.setExpectedValidationProblems(new HashSet<>(Arrays.asList(ValueNullProblem.class, ValueFormulaNotAllowedProblem.class)));
        employeeOrdinalList.add(employee);
        employeeOrdinalListInvalid.add(employee);

        employee = new EmployeeOrdinal();
        employee.setSheetIndex(1);
        employee.setSheetName("London W");
        employee.setImportIndex(6L);
        employee.setRowNumber(1);
        employee.setFirstName("Jane");
        employee.setLastName("DEAN");
        employee.setGender(GenderEnum.FEMALE);
        employee.setAge((byte) 33);
        employee.setEmail(new Email("j.dean", "randatmail", "com"));
        employee.setPhone("013-2182-77");
        employee.setSalary(6389);
        employee.setIsMarried(Boolean.FALSE);
        employee.setHiredOn(LocalDate.of(2015, 2, 22));
        employee.setStartsWorkAt(LocalTime.of(8, 0));
        employee.setLastLogin(LocalDateTime.of(2021, 6, 11, 13, 39));
        employee.setPasswordExpires(LocalDateTime.of(2021, 9, 25, 17, 22));
        employee.setExpectedValidationProblems(new HashSet<>(Collections.emptyList()));
        employeeOrdinalList.add(employee);
        employeeOrdinalListValid.add(employee);

        employee = new EmployeeOrdinal();
        employee.setSheetIndex(1);
        employee.setSheetName("London W");
        employee.setImportIndex(7L);
        employee.setRowNumber(2);
        employee.setFirstName("Justin");
//        employee.setLastName("GERARD’S"); //invalid
//        employee.setGender("N/A"); //invalid
        employee.setAge((byte) 19);
//        employee.setEmail(new Email("j.gerard", "randatmail", "com")); //invalid
        employee.setPhone("118-4479-22");
        employee.setSalary(-5521);
//        employee.setIsMarried(Boolean.TRUE); //invalid
        employee.setHiredOn(LocalDate.of(2015, 4, 15));
        employee.setStartsWorkAt(LocalTime.of(9, 30));
        employee.setLastLogin(LocalDateTime.of(2021, 6, 10, 19, 48));
        employee.setPasswordExpires(LocalDateTime.of(2021, 8, 22, 15, 22));
        employee.setExpectedValidationProblems(new HashSet<>(Arrays.asList(ValueFormatProblem.class, ValueFormatEnumProblem.class, ValueFormatRegexProblem.class, BusinessProblem.class)));
        employeeOrdinalList.add(employee);
        employeeOrdinalListInvalid.add(employee);
//        employeeOrdinalListValid.add(employee);

    }

    static {
        employeeNamedList = new ArrayList<>();
        employeeNamedListValid = new ArrayList<>();
        employeeNamedListInvalid = new ArrayList<>();

        Map<String, String> bonusMap1;
        Map<Integer, String> bonusMap2;
        Map<String, String> unmatchedMap;

        EmployeeNamed employee = new EmployeeNamed();
        employee.setSheetIndex(0);
        employee.setSheetName("London E");
        employee.setImportIndex(1L);
        employee.setRowNumber(4);
        employee.setFirstName("Ellia");
        employee.setLastName("RICHARDSON");
        employee.setGender(GenderEnum.FEMALE);
        employee.setAge((byte) 28);
        employee.setEmail(new Email("e.richardson", "randatmail", "com"));
        employee.setPhone("013-2182-19");
        employee.setSalary(8389);
        employee.setIsMarried(Boolean.FALSE);
        employee.setBonuses(Arrays.asList("10.00%", "20.00%"));
        bonusMap1 = new HashMap<>();
        bonusMap1.put("Bonus 1", "10.00%");
        bonusMap1.put("Bonus 2", "20.00%");
        employee.setBonusesMap1(bonusMap1);
        bonusMap2 = new HashMap<>();
        bonusMap2.put(8, "10.00%");
        bonusMap2.put(9, "20.00%");
        employee.setBonusesMap2(bonusMap2);
        employee.setHiredOn(LocalDate.of(2015, 1, 22));
        employee.setStartsWorkAt(LocalTime.of(8, 0));
        employee.setLastLogin(LocalDateTime.of(2021, 6, 11, 13, 40));
        unmatchedMap = new HashMap<>();
        unmatchedMap.put("Random", "Hope");
        unmatchedMap.put("Column", "you");
        employee.setUnmatchedColumns(unmatchedMap);
        employee.setPasswordExpires(LocalDateTime.of(2021, 9, 25, 17, 22));
        employee.setExpectedValidationProblems(new HashSet<>(Collections.emptyList()));
        employeeNamedList.add(employee);
        employeeNamedListValid.add(employee);

        employee = new EmployeeNamed();
        employee.setSheetIndex(0);
        employee.setSheetName("London E");
        employee.setImportIndex(2L);
        employee.setRowNumber(5);
        employee.setFirstName("Martin");
        employee.setLastName("MORGAN");
        employee.setGender(GenderEnum.MALE);
        employee.setAge((byte) 21);
        employee.setEmail(new Email("m.morgan", "randatmail", "com"));
        employee.setPhone("118-4479-55");
        employee.setSalary(3821);
        employee.setIsMarried(Boolean.FALSE);
        employee.setBonuses(Arrays.asList(null, null));
        bonusMap1 = new HashMap<>();
        bonusMap1.put("Bonus 1", null);
        bonusMap1.put("Bonus 2", null);
        employee.setBonusesMap1(bonusMap1);
        bonusMap2 = new HashMap<>();
        bonusMap2.put(8, null);
        bonusMap2.put(9, null);
        employee.setBonusesMap2(bonusMap2);
        employee.setHiredOn(LocalDate.of(2015, 3, 17));
        employee.setStartsWorkAt(LocalTime.of(8, 30));
        employee.setLastLogin(LocalDateTime.of(2021, 6, 10, 21, 40));
        unmatchedMap = new HashMap<>();
        unmatchedMap.put("Random", "are");
        unmatchedMap.put("Column", "doing");
        employee.setUnmatchedColumns(unmatchedMap);
        employee.setPasswordExpires(LocalDateTime.of(2021, 8, 22, 15, 22));
        employee.setExpectedValidationProblems(new HashSet<>(Collections.emptyList()));
        employeeNamedList.add(employee);
        employeeNamedListValid.add(employee);

        employee = new EmployeeNamed();
        employee.setSheetIndex(0);
        employee.setSheetName("London E");
        employee.setImportIndex(3L);
        employee.setRowNumber(6);
        employee.setFirstName("Eric");
//        employee.setLastName("HARRIS3"); //invalid
        employee.setGender(GenderEnum.MALE);
        employee.setAge(null); //invalid;
        employee.setPhone("210-3023-63");
//        employee.setSalary(2944); invalid
        employee.setIsMarried(Boolean.TRUE);
        employee.setBonuses(Arrays.asList("25.00%", null));
        bonusMap1 = new HashMap<>();
        bonusMap1.put("Bonus 1", "25.00%");
        bonusMap1.put("Bonus 2", null);
        employee.setBonusesMap1(bonusMap1);
        bonusMap2 = new HashMap<>();
        bonusMap2.put(8, "25.00%");
        bonusMap2.put(9, null);
        employee.setBonusesMap2(bonusMap2);
        employee.setHiredOn(LocalDate.of(2015, 3, 14));
        employee.setStartsWorkAt(LocalTime.of(8, 0));
        employee.setLastLogin(LocalDateTime.of(2021, 6, 11, 12, 20));
        unmatchedMap = new HashMap<>();
        unmatchedMap.put("Random", "very");
        unmatchedMap.put("Column", null);
        employee.setUnmatchedColumns(unmatchedMap);
        employee.setPasswordExpires(LocalDateTime.of(2021, 7, 2, 13, 29));
        employee.setExpectedValidationProblems(new HashSet<>(Arrays.asList(ValueNullProblem.class, ValueFormatProblem.class, ValueFormatRegexProblem.class, ValueRangeProblem.class)));
        employeeNamedList.add(employee);
        employeeNamedListInvalid.add(employee);

        employee = new EmployeeNamed();
        employee.setSheetIndex(0);
        employee.setSheetName("London E");
        employee.setImportIndex(4L);
        employee.setRowNumber(7);
        employee.setFirstName("Richard");
        employee.setLastName("TAYLOR");
        employee.setGender(GenderEnum.MALE);
        employee.setAge((byte) 24);
        employee.setEmail(new Email("r.taylor", "randatmail", "com"));
        employee.setPhone("661-8950-21");
        employee.setSalary(5324);
        employee.setIsMarried(Boolean.TRUE);
        employee.setBonuses(Arrays.asList(null, "5.00%"));
        bonusMap1 = new HashMap<>();
        bonusMap1.put("Bonus 1", null);
        bonusMap1.put("Bonus 2", "5.00%");
        employee.setBonusesMap1(bonusMap1);
        bonusMap2 = new HashMap<>();
        bonusMap2.put(8, null);
        bonusMap2.put(9, "5.00%");
        employee.setBonusesMap2(bonusMap2);
        employee.setHiredOn(LocalDate.of(2015, 4, 22));
        employee.setStartsWorkAt(LocalTime.of(9, 0));
        employee.setLastLogin(LocalDateTime.of(2021, 6, 11, 14, 7));
        unmatchedMap = new HashMap<>();
        unmatchedMap.put("Random", "good");
        unmatchedMap.put("Column", "there");
        employee.setUnmatchedColumns(unmatchedMap);
        employee.setPasswordExpires(LocalDateTime.of(2021, 8, 19, 8, 3));
        employee.setExpectedValidationProblems(new HashSet<>(Collections.emptyList()));
        employeeNamedList.add(employee);
        employeeNamedListValid.add(employee);

        employee = new EmployeeNamed();
        employee.setSheetIndex(0);
        employee.setSheetName("London E");
        employee.setImportIndex(5L);
        employee.setRowNumber(8);
        employee.setFirstName("Oliver");
        employee.setLastName("HUNT");
        employee.setGender(GenderEnum.MALE);
        employee.setAge((byte) 25);
        employee.setPhone("709-4582-07");
//        employee.setSalary(7125); //invalid
        employee.setIsMarried(Boolean.FALSE);
        employee.setBonuses(Arrays.asList("100.00%", null));
        bonusMap1 = new HashMap<>();
        bonusMap1.put("Bonus 1", "100.00%");
        bonusMap1.put("Bonus 2", null);
        employee.setBonusesMap1(bonusMap1);
        bonusMap2 = new HashMap<>();
        bonusMap2.put(8, "100.00%");
        bonusMap2.put(9, null);
        employee.setBonusesMap2(bonusMap2);
        employee.setHiredOn(LocalDate.of(2020, 11, 19));
        employee.setStartsWorkAt(LocalTime.of(9, 30));
        employee.setLastLogin(LocalDateTime.of(2021, 6, 10, 17, 58));
        unmatchedMap = new HashMap<>();
        unmatchedMap.put("Random", "!");
        unmatchedMap.put("Column", null);
        employee.setUnmatchedColumns(unmatchedMap);
        employee.setPasswordExpires(LocalDateTime.of(2021, 6, 12, 11, 17));
        employee.setExpectedValidationProblems(new HashSet<>(Arrays.asList(ValueNullProblem.class, ValueFormulaNotAllowedProblem.class)));
        employeeNamedList.add(employee);
        employeeNamedListInvalid.add(employee);

        employee = new EmployeeNamed();
        employee.setSheetIndex(1);
        employee.setSheetName("London W");
        employee.setImportIndex(6L);
        employee.setRowNumber(1);
        employee.setFirstName("Jane");
        employee.setLastName("DEAN");
        employee.setGender(GenderEnum.FEMALE);
        employee.setAge((byte) 33);
        employee.setEmail(new Email("j.dean", "randatmail", "com"));
        employee.setPhone("013-2182-77");
        employee.setSalary(6389);
        employee.setIsMarried(Boolean.FALSE);
        employee.setBonuses(Arrays.asList("15.00%", "20.00%"));
        bonusMap1 = new HashMap<>();
        bonusMap1.put("Bonus 1", "15.00%");
        bonusMap1.put("Bonus 2", "20.00%");
        employee.setBonusesMap1(bonusMap1);
        bonusMap2 = new HashMap<>();
        bonusMap2.put(8, "15.00%");
        bonusMap2.put(9, "20.00%");
        employee.setBonusesMap2(bonusMap2);
        employee.setHiredOn(LocalDate.of(2015, 2, 22));
        employee.setStartsWorkAt(LocalTime.of(8, 0));
        employee.setLastLogin(LocalDateTime.of(2021, 6, 11, 13, 39));
        unmatchedMap = new HashMap<>();
        employee.setUnmatchedColumns(unmatchedMap);
        employee.setPasswordExpires(LocalDateTime.of(2021, 9, 25, 17, 22));
        employee.setExpectedValidationProblems(new HashSet<>(Collections.emptyList()));
        employeeNamedList.add(employee);
        employeeNamedListValid.add(employee);

        employee = new EmployeeNamed();
        employee.setSheetIndex(1);
        employee.setSheetName("London W");
        employee.setImportIndex(7L);
        employee.setRowNumber(2);
        employee.setFirstName("Justin");
//        employee.setLastName("GERARD’S"); //invalid
//        employee.setGender(GenderEnum.MALE); invalid
        employee.setAge((byte) 19);
//        employee.setEmail(new Email("j.gerard", "randatmail", "com")); invalid
        employee.setPhone("118-4479-22");
        employee.setSalary(-5521);
//        employee.setIsMarried(Boolean.TRUE); invalid
        employee.setBonuses(Arrays.asList("50.00%", "60.00%"));
        bonusMap1 = new HashMap<>();
        bonusMap1.put("Bonus 1", "50.00%");
        bonusMap1.put("Bonus 2", "60.00%");
        employee.setBonusesMap1(bonusMap1);
        bonusMap2 = new HashMap<>();
        bonusMap2.put(8, "50.00%");
        bonusMap2.put(9, "60.00%");
        employee.setBonusesMap2(bonusMap2);
        employee.setHiredOn(LocalDate.of(2015, 4, 15));
        employee.setStartsWorkAt(LocalTime.of(9, 30));
        employee.setLastLogin(LocalDateTime.of(2021, 6, 10, 19, 48));
        unmatchedMap = new HashMap<>();
        employee.setUnmatchedColumns(unmatchedMap);
        employee.setPasswordExpires(LocalDateTime.of(2021, 8, 22, 15, 22));
        employee.setExpectedValidationProblems(new HashSet<>(Arrays.asList(ValueFormatProblem.class, ValueFormatEnumProblem.class, ValueFormatRegexProblem.class, BusinessProblem.class)));
        employeeNamedList.add(employee);
        employeeNamedListInvalid.add(employee);

    }

    private FileData() throws IllegalAccessException {
        throw new IllegalAccessException();
    }

    public static List<EmployeeOrdinal> getEmployeesOrdinal() {
        return employeeOrdinalList;
    }

    public static List<EmployeeOrdinal> getEmployeesOrdinalValid() {
        return employeeOrdinalListValid;
    }

    public static List<EmployeeOrdinal> getEmployeesOrdinalInvalid() {
        return employeeOrdinalListInvalid;
    }

    public static List<EmployeeNamed> getEmployeesNamed() {
        return employeeNamedList;
    }

    public static List<EmployeeNamed> getEmployeesNamedValid() {
        return employeeNamedListValid;
    }

    public static List<EmployeeNamed> getEmployeesNamedInvalid() {
        return employeeNamedListInvalid;
    }
}
