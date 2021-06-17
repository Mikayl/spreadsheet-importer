package ro.nom.vmt.spreadsheet_importer.importing;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.nom.vmt.spreadsheet_importer.interfaces.BaseImportMapper;
import ro.nom.vmt.spreadsheet_importer.interfaces.Problem;
import ro.nom.vmt.spreadsheet_importer.problems.RowProblem;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ImportData<T> {

    private static final Logger log = LoggerFactory.getLogger(ImportData.class);

    private final BaseImportMapper<T> mapper;


    private final List<RowContext<T>> rowContexts = new ArrayList<>();
    private final Set<Problem> problems = new LinkedHashSet<>();

    private int noOfSheets = 0;
    private long importIndex = 0L;


    ImportData(BaseImportMapper<T> mapper) {
        this.mapper = mapper;
    }

    protected void submit(RowContext<T> rowContext) {
        this.rowContexts.add(rowContext);
    }

    protected T process(Row row, Map<String, LinkedHashMap<String, Integer>> columnPositions, Importer.Options<T> options) {
        return this.newRowContext(this, options).map(row, columnPositions);
    }

    protected RowContext<T> newRowContext(ImportData<T> importData, Importer.Options<T> options) {
        log.debug("Generating new row context with import index {}", importIndex + 1);
        return new RowContext<>(importData, mapper, options, ++importIndex);
    }


    protected void addValidationProblem(Problem problem) {
        this.problems.add(problem);
    }

    protected void incrNoOfSheets() {
        noOfSheets++;
    }

    public int getNoOfSheets() {
        return noOfSheets;
    }

    public List<T> getValidRows() {
        return rowContexts
                .stream()
                .filter(RowContext::isValid)
                .map(RowContext::getMappedRow)
                .collect(Collectors.toList());
    }

    public List<T> getInvalidRows() {
        return rowContexts
                .stream()
                .filter(RowContext::isInvalid)
                .map(RowContext::getMappedRow)
                .collect(Collectors.toList());
    }

    public List<T> getInvalidRows(Class<? extends Problem> validationProblemClass) {
        return rowContexts
                .stream()
                .filter(rowContext -> rowContext.isInvalid(validationProblemClass))
                .map(RowContext::getMappedRow)
                .collect(Collectors.toList());
    }

    public List<T> getAllRows() {
        return rowContexts
                .stream()
                .map(RowContext::getMappedRow)
                .collect(Collectors.toList());
    }

    public long getRowNoTotal() {
        return rowContexts.size();
    }

    public long getRowNoValid() {
        return rowContexts
                .stream()
                .filter(RowContext::isValid)
                .count();
    }

    public long getRowNoInvalid() {
        return rowContexts
                .stream()
                .filter(RowContext::isInvalid)
                .count();
    }


    public boolean isValid() {
        return this.problems.isEmpty()
                && rowContexts
                .stream()
                .allMatch(RowContext::isValid);
    }

    public List<Problem> getValidationProblems() {
        return Stream.concat(
                this.problems.stream(),
                rowContexts.stream().map(RowContext::getProblems).flatMap(Collection::stream)
        ).collect(Collectors.toList());
    }

    public List<RowProblem> getValidationProblems(T data) {
        return rowContexts
                .stream()
                .filter(rowContext -> rowContext.getMappedRow().equals(data))
                .findFirst()
                .map(RowContext::getProblems)
                .orElseThrow(NoSuchElementException::new);
    }

    public List<Problem> getValidationProblems(Class<? extends Problem> clazz) {
        return this.getValidationProblems()
                .stream()
                .filter(validationProblem -> clazz.isAssignableFrom(validationProblem.getClass()) && validationProblem.getClass().isAssignableFrom(clazz))
                .collect(Collectors.toList());
    }

    public List<Problem> getValidationProblemsTree(Class<? extends Problem> clazz) {
        return this.getValidationProblems()
                .stream()
                .filter(validationProblem -> clazz.isAssignableFrom(validationProblem.getClass()))
                .collect(Collectors.toList());
    }


    @Override
    public String toString() {
        return String.format("ImportData{allRows=%s, validRows=%s, validationProblems=%s}", getRowNoTotal(), getRowNoValid(), getValidationProblems().size());
    }
}
