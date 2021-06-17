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
import ro.nom.vmt.spreadsheet_importer.util.Pair;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RowContext<T> {

    private static final Logger log = LoggerFactory.getLogger(RowContext.class);

    private final ImportData<T> importData;
    private final BaseImportMapper<T> importMapper;
    private final long importIndex;
    private final Importer.Options<T> options;


    private T mappedRow;

    private boolean isSubmitted = false;
    private boolean isValid = true;

    private List<RowProblem> problems = new ArrayList<>();

    RowContext(ImportData<T> importData, BaseImportMapper<T> importMapper, Importer.Options<T> options, long importIndex) {
        this.importData = importData;
        this.importMapper = importMapper;
        this.options = options;
        this.importIndex = importIndex;
    }

    public T map(Row row, Map<String, LinkedHashMap<String, Integer>> columnPositions) {
        log.debug("Mapping row {}@#{} associated with import index: {}", row.getSheet().getSheetName(), row.getRowNum(), importIndex);
        if (this.isSubmitted) {
            log.error("A row can no longer be submitted for {}@#{} associated with import index: {}", row.getSheet().getSheetName(), row.getRowNum(), importIndex);
            throw new UnsupportedOperationException("The row context was already submitted");
        }
        this.mappedRow = importMapper.map(this, row, columnPositions);

        log.debug("Row {}@#{} associated with import index: {} was mapped and will now be validated by the external validators if needed", row.getSheet().getSheetName(), row.getRowNum(), importIndex);
        this.options.getValidators().forEach(validator -> {
            try {
                this.addValidationProblems(validator.apply(this.mappedRow, new Pair<>(row.getSheet().getSheetName(), row.getRowNum())));
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                this.addValidationProblem(new RowProblem(row.getSheet().getSheetName(), row.getRowNum()));
            }
        });

        importData.submit(this);
        log.debug("Row {}@#{} associated with import index: {} was submitted", row.getSheet().getSheetName(), row.getRowNum(), importIndex);

        this.isSubmitted = true;

        this.options.getConsumers().forEach(consumer -> consumer.accept(this.mappedRow));
        this.options.getConsumersWithProblems().forEach(consumer -> consumer.accept(this.mappedRow, problems));
        if (this.isValid) {
            this.options.getConsumersForValid().forEach(consumer -> consumer.accept(this.mappedRow));
        } else {
            this.options.getConsumersForInvalid().forEach(consumer -> consumer.accept(this.mappedRow));
            this.options.getConsumersForInvalidWithProblems().forEach(consumer -> consumer.accept(this.mappedRow, this.problems));
        }

        return this.mappedRow;
    }

    public void addValidationProblem(RowProblem problem) {
        if (problem == null) {
            return;
        }
        if (this.isSubmitted) {
            log.error("A row can no longer be submitted for {}@#{} associated with import index: {}", problem.getSheetName(), problem.getRowNo(), importIndex);
            throw new UnsupportedOperationException("The row context was already submitted");
        }
        log.debug("Found a problem for row: {}@#{} associated with import index: {}", problem.getSheetName(), problem.getRowNo(), importIndex);
        this.isValid = false;
        this.problems.add(problem);
    }

    public void addValidationProblems(List<RowProblem> problems) {
        if (problems == null || problems.isEmpty()) {
            return;
        }
        if (this.isSubmitted) {
            log.error("A row can no longer be submitted for {}@#{} associated with import index: {}", problems.get(0).getSheetName(), problems.get(0).getRowNo(), importIndex);
            throw new UnsupportedOperationException("The row context was already submitted");
        }
        log.debug("Found multiple problems for row: {}@#{} associated with import index: {}", problems.get(0).getSheetName(), problems.get(0).getRowNo(), importIndex);
        this.isValid = false;
        this.problems.addAll(problems);
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean isInvalid() {
        return !isValid;
    }

    public boolean isInvalid(Class<? extends Problem> validationProblemClass) {
        return problems
                .stream()
                .anyMatch(validationProblem -> validationProblemClass.isAssignableFrom(validationProblem.getClass()));
    }

    public T getMappedRow() {
        return mappedRow;
    }

    public long getImportIndex() {
        return importIndex;
    }

    public List<RowProblem> getProblems() {
        return this.problems;
    }

}
