package ro.nom.vmt.demo.dto;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import ro.nom.vmt.demo.Email;
import ro.nom.vmt.demo.EnumProcessor;
import ro.nom.vmt.demo.GenderEnum;
import ro.nom.vmt.demo.ToUpperCaseProcessor;
import ro.nom.vmt.spreadsheet_importer.annotation.Import;
import ro.nom.vmt.spreadsheet_importer.annotation.Importable;
import ro.nom.vmt.spreadsheet_importer.annotation.Inject;
import ro.nom.vmt.spreadsheet_importer.annotation.Named;

import java.time.LocalDateTime;
import java.util.List;

import static ro.nom.vmt.data.FileData.EMAIL_REGEX;
import static ro.nom.vmt.spreadsheet_importer.annotation.Inject.InjectType.SHEET_NAME;

@Importable(isNamed = true, sheetNames = {"London.*"})
public class Employee {

    @Named(value = "Bonus.*", minimumMatches = 2, maximumMatches = Integer.MAX_VALUE)
    @Import
    List<String> bonuses;
    @Named("Password expires")
    @Import(matches = "yy-MM-dd_HH:mm")
    LocalDateTime passwordExpires;
    @Inject(SHEET_NAME)
    private String sheetName;
    @Named("First Name")
    @Import(trim = false, formulaAllowed = false)
    private String firstName;
    @Named("Last Name")
    @Import(matches = "[a-zA-Z]+", preProcess = ToUpperCaseProcessor.class)
    private String lastName;
    @Named(value = "Gender")
    @Import(preProcess = EnumProcessor.class)
    private GenderEnum gender;
    @Named("Email")
    @Import(required = true, matches = EMAIL_REGEX)
    private Email email;

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
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

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public List<String> getBonuses() {
        return bonuses;
    }

    public void setBonuses(List<String> bonuses) {
        this.bonuses = bonuses;
    }

    public LocalDateTime getPasswordExpires() {
        return passwordExpires;
    }

    public void setPasswordExpires(LocalDateTime passwordExpires) {
        this.passwordExpires = passwordExpires;
    }
}
