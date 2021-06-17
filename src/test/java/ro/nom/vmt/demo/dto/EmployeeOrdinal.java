package ro.nom.vmt.demo.dto;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import ro.nom.vmt.demo.*;
import ro.nom.vmt.spreadsheet_importer.annotation.Import;
import ro.nom.vmt.spreadsheet_importer.annotation.Importable;
import ro.nom.vmt.spreadsheet_importer.annotation.Inject;
import ro.nom.vmt.spreadsheet_importer.annotation.Ordinal;
import ro.nom.vmt.spreadsheet_importer.interfaces.Problem;
import ro.nom.vmt.spreadsheet_importer.interfaces.Validatable;
import ro.nom.vmt.spreadsheet_importer.problems.RowProblem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ro.nom.vmt.data.FileData.EMAIL_REGEX;
import static ro.nom.vmt.spreadsheet_importer.annotation.Inject.InjectType.*;


@Importable(sheetNames = {"London.*", "Non-existent"})
public class EmployeeOrdinal implements Validatable {

    @Ordinal(14)
    @Import
    LocalDateTime lastLogin;
    @Ordinal(12)
    @Import
    LocalDate hiredOn;
    @Ordinal(13)
    @Import
    LocalTime startsWorkAt;
    @Ordinal(15)
    @Import(matches = "yy-MM-dd_HH:mm")
    LocalDateTime passwordExpires;
    @Inject(UNMATCHED_COLUMNS)
    Map<String, String> unmatchedColumns;
    Set<Class<? extends Problem>> expectedValidationProblems;
    @Inject(SHEET_INDEX)
    private Integer sheetIndex;
    @Inject(SHEET_NAME)
    private String sheetName;
    @Inject(IMPORT_INDEX)
    private Long importIndex;
    @Inject(ROW_NUMBER)
    private Integer rowNumber;
    @Ordinal(0)
    @Import(required = true)
    private String firstName;
    @Ordinal(1)
    @Import(required = true, matches = "[a-zA-Z]+", preProcess = ToUpperCaseProcessor.class)
    private String lastName;
    @Ordinal(2)
    @Import(preProcess = EnumProcessor.class)
    private GenderEnum gender;
    @Ordinal(3)
    @Import
    private Byte age;
    @Ordinal(4)
    @Import(required = true, matches = EMAIL_REGEX)
    private Email email;
    @Ordinal(5)
    @Import
    private String phone;
    @Ordinal(6)
    @Import(formulaAllowed = false)
    private Integer salary;
    @Ordinal(7)
    @Import
    private Boolean isMarried;

    @Override
    public List<RowProblem> validate(String sheetName, Integer rowNo) {
        List<RowProblem> problemList = new ArrayList<>();
        if (salary != null && salary < 0) {
            problemList.add(new BusinessProblem(sheetName, rowNo));
        }
        return problemList;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Integer getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(Integer rowNumber) {
        this.rowNumber = rowNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public GenderEnum getGender() {
        return gender;
    }

    public void setGender(GenderEnum gender) {
        this.gender = gender;
    }

    public Byte getAge() {
        return age;
    }

    public void setAge(Byte age) {
        this.age = age;
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public Boolean getIsMarried() {
        return isMarried;
    }

    public void setIsMarried(Boolean married) {
        isMarried = married;
    }


    public Long getImportIndex() {
        return importIndex;
    }

    public void setImportIndex(Long importIndex) {
        this.importIndex = importIndex;
    }

    public Map<String, String> getUnmatchedColumns() {
        return unmatchedColumns;
    }

    public void setUnmatchedColumns(Map<String, String> unmatchedColumns) {
        this.unmatchedColumns = unmatchedColumns;
    }

    public Set<Class<? extends Problem>> getExpectedValidationProblems() {
        return expectedValidationProblems;
    }

    public void setExpectedValidationProblems(Set<Class<? extends Problem>> expectedValidationProblems) {
        this.expectedValidationProblems = expectedValidationProblems;
    }


    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public LocalDate getHiredOn() {
        return hiredOn;
    }

    public void setHiredOn(LocalDate hiredOn) {
        this.hiredOn = hiredOn;
    }

    public LocalTime getStartsWorkAt() {
        return startsWorkAt;
    }

    public void setStartsWorkAt(LocalTime startsWorkAt) {
        this.startsWorkAt = startsWorkAt;
    }

    public LocalDateTime getPasswordExpires() {
        return passwordExpires;
    }

    public void setPasswordExpires(LocalDateTime passwordExpires) {
        this.passwordExpires = passwordExpires;
    }

    public Integer getSheetIndex() {
        return sheetIndex;
    }

    public void setSheetIndex(Integer sheetIndex) {
        this.sheetIndex = sheetIndex;
    }


}
