package ro.nom.vmt.spreadsheet_importer.importing;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ro.nom.vmt.spreadsheet_importer.interfaces.BaseImportMapper;
import ro.nom.vmt.spreadsheet_importer.problems.ColumnNotPresentProblem;
import ro.nom.vmt.spreadsheet_importer.problems.FileProblem;
import ro.nom.vmt.spreadsheet_importer.problems.RowProblem;
import ro.nom.vmt.spreadsheet_importer.problems.SheetNotPresent;
import ro.nom.vmt.spreadsheet_importer.util.Pair;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Importer<T> {

    private static final Logger log = LoggerFactory.getLogger(Importer.class);

    private final BaseImportMapper<T> mapper;

    private final Options<T> options;


    private Importer(Class<? extends BaseImportMapper<T>> mapperClass, Options<T> options) {
        try {
            this.mapper = mapperClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            log.error("Could not instantiate mapper class because: {}", e.getMessage(), e);
            throw new InstantiationError("Could not instantiate mapper class");
        }
        this.options = options;
    }

    public static <T> ImporterBuilder<T> builder(Class<? extends BaseImportMapper<T>> mapperClass) {
        return new ImporterBuilder<>(mapperClass);
    }

    public static <T> Importer<T> build(Class<? extends BaseImportMapper<T>> mapperClass) {
        return new ImporterBuilder<>(mapperClass).build();
    }

    public static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        if (row.getLastCellNum() <= 0) {
            return true;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && cell.toString() != null && !cell.toString().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public ImportData<T> process(InputStream inputStream) {
        ImportData<T> importData = new ImportData<>(mapper);

        log.debug("Opening the input stream");
        try (Workbook workbook = WorkbookFactory.create(inputStream)) {
            log.debug("Workbook was created");

            int sheetsNo = workbook.getNumberOfSheets();
            log.debug("Found {} sheets in the workbook", sheetsNo);

            Set<Integer> sheetsToImport = getSheetsToImport(importData, workbook, sheetsNo);
            if (log.isDebugEnabled()) {
                log.debug("Importing the following sheets: {}", sheetsToImport.stream().map(String::valueOf).collect(Collectors.joining(",")));
            }

            for (Integer i : sheetsToImport) {
                Sheet sheet = workbook.getSheetAt(i);
                log.debug("Importing the rows from the sheet at: {} ({})", i, sheet.getSheetName());
                importData.incrNoOfSheets();

                Iterator<Row> rowIterator = sheet.rowIterator();

                Map<String, LinkedHashMap<String, Integer>> columnPositions = getColumnPositions(rowIterator);
                if (log.isDebugEnabled()) {
                    log.debug("The column positions for the sheet {} ({}) are: {}", i, sheet.getSheetName(), columnPositionsToString(columnPositions));
                }

                boolean allRequiredColumnsPresent = validateRequiredColumnsExist(importData, sheet, columnPositions);

                if (allRequiredColumnsPresent) {
                    log.debug("All required columns are present for {} ({})", i, sheet.getSheetName());
                    //Removes not present columns
                    columnPositions.entrySet().removeIf(e -> e.getValue().size() == 0);

                    rowIterator.forEachRemaining(row -> {
                        if (!isRowEmpty(row)) {
                            importData.process(row, columnPositions, options);
                        }
                    });
                }
            }
        } catch (Exception e) {
            log.warn("Error while processing the input stream for import!", e);
            importData.addValidationProblem(new FileProblem());
        }

        log.info("Processed an import with the following data summary:{}", importData);
        return importData;
    }

    private Set<Integer> getSheetsToImport(ImportData<T> importData, Workbook workbook, int sheetsNo) {
        if (!mapper.getSheetNames().isEmpty()) {
            //we use the names
            Set<Integer> sheetsToImport = new HashSet<>();
            for (int i = 0; i < sheetsNo; i++) {
                String sheetName = workbook.getSheetAt(i).getSheetName();
                if (sheetName != null && !sheetName.isEmpty()) {
                    mapper.getSheetNames()
                            .stream()
                            .filter(sheetName::matches)
                            .findFirst()
                            .ifPresent(name -> sheetsToImport.add(workbook.getSheetIndex(sheetName)));
                }
            }
            if (sheetsToImport.isEmpty()) {
                importData.addValidationProblem(new SheetNotPresent(String.join(",", mapper.getSheetNames())));
            }
            return sheetsToImport;
        } else {
            Set<Integer> sheetsToImport;
            //we use the indexes
            sheetsToImport = mapper.getSheetPositions();
            sheetsToImport
                    .stream()
                    .filter(integer -> integer < 0 || integer > sheetsNo)
                    .forEach(index -> importData.addValidationProblem(new SheetNotPresent("#" + index)));

            return sheetsToImport;

        }
    }

    protected Map<String, LinkedHashMap<String, Integer>> getColumnPositions(Iterator<Row> rowIterator) {
        if (mapper.isNamed()) {
            return getNamedColumnPositions(rowIterator.next(), mapper.getColumnNames());
        } else {
            if (mapper.hasHeader()) {
                Row row = rowIterator.next();
                return mapper.getColumnPositions()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                            LinkedHashMap<String, Integer> tmpMap = new LinkedHashMap<>();
                            Cell cell = row.getCell(e.getValue());
                            if (cell != null && !cell.getCellType().equals(CellType.BLANK)) {
                                tmpMap.put(cell.getStringCellValue(), e.getValue());
                            }
                            return tmpMap;
                        }));
            } else {
                return mapper.getColumnPositions()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> {
                            LinkedHashMap<String, Integer> tmpMap = new LinkedHashMap<>();
                            tmpMap.put(e.getValue().toString(), e.getValue());
                            return tmpMap;
                        }));

            }
        }
    }

    protected Map<String, LinkedHashMap<String, Integer>> getNamedColumnPositions(Row headerRow, Map<String, String> columnNames) {
        Map<String, LinkedHashMap<String, Integer>> columnPosition = new HashMap<>();
        Map<Integer, String> headerPositions = new HashMap<>();
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && !cell.getCellType().equals(CellType.BLANK)) {
                headerPositions.put(i, cell.getStringCellValue());
            }

        }
        for (Map.Entry<String, String> columnNameEntry : columnNames.entrySet()) {
            LinkedHashMap<String, Integer> matchingColumns =
                    headerPositions
                            .entrySet()
                            .stream()
                            .filter(integerStringEntry -> integerStringEntry.getValue().matches(columnNameEntry.getValue()))
                            .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e2, LinkedHashMap::new));

            columnPosition.put(columnNameEntry.getKey(), matchingColumns);

        }

        Set<String> matchedColumns =
                columnPosition
                        .entrySet()
                        .stream()
                        .flatMap(entry -> entry.getValue().entrySet().stream())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toSet());

        LinkedHashMap<String, Integer> unmatchedColumns =
                headerPositions
                        .entrySet()
                        .stream()
                        .filter(integerStringEntry -> !matchedColumns.contains(integerStringEntry.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey, (e1, e2) -> e2, LinkedHashMap::new));

        columnPosition.put(null, unmatchedColumns); //TODO maybe not use null?

        return columnPosition;

    }

    private boolean validateRequiredColumnsExist(ImportData<T> importData, Sheet sheet, Map<String, LinkedHashMap<String, Integer>> columnPositions) {
        Map<String, Pair<Integer, Integer>> columnRequiredAppearences = mapper.getColumnRequiredAppearances();

        long x = columnPositions
                .entrySet()
                .stream()
                .flatMap(entry -> {
                    int presenceCounter = entry.getValue().size();
                    if (entry.getKey() != null && (columnRequiredAppearences.get(entry.getKey()).p1 > presenceCounter || columnRequiredAppearences.get(entry.getKey()).p2 < presenceCounter)) {
                        importData.addValidationProblem(new ColumnNotPresentProblem(sheet.getSheetName(), entry.getKey()));
                        return Stream.of(entry);
                    }
                    return Stream.empty();
                })
                .count();


        return x == 0;
    }

    private String columnPositionsToString(Map<String, LinkedHashMap<String, Integer>> columnPositions) {
        return columnPositions
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + " = ["
                        + entry.getValue()
                        .entrySet()
                        .stream()
                        .map(entryInner -> entryInner.getKey() + " = " + entryInner.getValue().toString())
                        .collect(Collectors.joining(","))
                        + "]; "
                )
                .collect(Collectors.joining(","));

    }

    public static class Options<T> {
        private final List<BiFunction<T, Pair<String, Integer>, List<RowProblem>>> validators = new ArrayList<>();
        private final List<Consumer<T>> consumersForValid = new ArrayList<>();
        private final List<Consumer<T>> consumersForInvalid = new ArrayList<>();
        private final List<BiConsumer<T, List<RowProblem>>> consumersForInvalidWithProblems = new ArrayList<>();
        private final List<Consumer<T>> consumers = new ArrayList<>();
        private final List<BiConsumer<T, List<RowProblem>>> consumersWithProblems = new ArrayList<>();

        public List<BiFunction<T, Pair<String, Integer>, List<RowProblem>>> getValidators() {
            return validators;
        }

        public List<Consumer<T>> getConsumersForValid() {
            return consumersForValid;
        }

        public List<Consumer<T>> getConsumersForInvalid() {
            return consumersForInvalid;
        }

        public List<Consumer<T>> getConsumers() {
            return consumers;
        }

        public List<BiConsumer<T, List<RowProblem>>> getConsumersForInvalidWithProblems() {
            return consumersForInvalidWithProblems;
        }

        public List<BiConsumer<T, List<RowProblem>>> getConsumersWithProblems() {
            return consumersWithProblems;
        }
    }

    public static class ImporterBuilder<T> {
        private final Class<? extends BaseImportMapper<T>> mapperClass;
        private final Options<T> options;

        public ImporterBuilder(Class<? extends BaseImportMapper<T>> mapperClass) {
            this.mapperClass = mapperClass;
            options = new Options<>();
        }

        public Importer<T> build() {
            return new Importer<>(mapperClass, options);
        }

        public ImporterBuilder<T> withValidator(BiFunction<T, Pair<String, Integer>, List<RowProblem>> validator) {
            this.options.validators.add(validator);
            return this;
        }

        public ImporterBuilder<T> withConsumer(Consumer<T> consumer) {
            this.options.consumers.add(consumer);
            return this;
        }

        public ImporterBuilder<T> withConsumer(BiConsumer<T, List<RowProblem>> consumer) {
            this.options.consumersWithProblems.add(consumer);
            return this;
        }

        public ImporterBuilder<T> withConsumerForValid(Consumer<T> consumerForValid) {
            this.options.consumersForValid.add(consumerForValid);
            return this;
        }

        public ImporterBuilder<T> withConsumerForInvalid(Consumer<T> consumerForInvalid) {
            this.options.consumersForInvalid.add(consumerForInvalid);
            return this;
        }

        public ImporterBuilder<T> withConsumerForInvalid(BiConsumer<T, List<RowProblem>> consumerForInvalid) {
            this.options.consumersForInvalidWithProblems.add(consumerForInvalid);
            return this;
        }


    }


}
