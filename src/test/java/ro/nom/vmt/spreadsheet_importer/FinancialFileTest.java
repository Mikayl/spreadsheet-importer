package ro.nom.vmt.spreadsheet_importer;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */


import org.junit.jupiter.api.Test;
import ro.nom.vmt.demo.BusinessProblem;
import ro.nom.vmt.demo.dto.Financial;
import ro.nom.vmt.demo.dto.FinancialImportMapper;
import ro.nom.vmt.spreadsheet_importer.importing.ImportData;
import ro.nom.vmt.spreadsheet_importer.importing.Importer;
import ro.nom.vmt.spreadsheet_importer.problems.RowProblem;
import ro.nom.vmt.spreadsheet_importer.problems.ValueFormatRegexProblem;
import ro.nom.vmt.spreadsheet_importer.problems.ValueNullProblem;
import ro.nom.vmt.spreadsheet_importer.problems.ValueRangeProblem;
import ro.nom.vmt.spreadsheet_importer.util.Pair;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

public class FinancialFileTest {

    private final ImportData<Financial> importData;

    public FinancialFileTest() throws IOException {
        BiFunction<Financial, Pair<String, Integer>, List<RowProblem>> validator = (financial, pair) -> {
            List<RowProblem> rowProblems = new ArrayList<>();
            if (financial.getGrossSales().equals(34056.99F)) {
                rowProblems.add(new BusinessProblem(pair.p1, pair.p2));
            }
            if (financial.getMonthNumber() != null && (financial.getMonthNumber() > 12 || financial.getMonthNumber() < 0)) {
                rowProblems.add(new ValueRangeProblem(pair.p1, pair.p2, null, "month number", financial.getMonthNumber().toString()));
            }
            return rowProblems;
        };

        InputStream is = new FileInputStream(this.getClass().getClassLoader().getResource("Financial_sample.xls").getFile());
        importData = Importer.builder(FinancialImportMapper.class)
                .withValidator(validator)
                .build()
                .process(is);
    }

    @Test
    public void importedCountsTest() {

        assertEquals(700, importData.getRowNoTotal());
        assertEquals(importData.getRowNoTotal(), importData.getAllRows().size());

        assertEquals(694, importData.getRowNoValid());
        assertEquals(importData.getRowNoValid(), importData.getValidRows().size());

        assertEquals(6, importData.getRowNoInvalid());
        assertEquals(importData.getRowNoInvalid(), importData.getInvalidRows().size());
    }

    @Test
    public void checkRowNo1Test() {

        assertEquals(importData.getAllRows().get(0), importData.getValidRows().get(0));//first row should be valid

        Financial financial = importData.getAllRows().get(0);

        assertEquals(Financial.Segment.GOVERNMENT, financial.getSegment());
        assertEquals("Canada", financial.getCountry());
        assertEquals("Carretera", financial.getProduct());
        assertEquals(Financial.DiscountBand.NONE, financial.getDiscountBand());
        assertEquals(1618, financial.getUnitsSold());
        assertEquals(3, financial.getManufacturingPrice());
        assertEquals(20, financial.getSalePrice());
        assertEquals(32_370, financial.getGrossSales());
        assertEquals(0, financial.getDiscounts());
        assertEquals(32_370, financial.getSales());
        assertEquals(16_185, financial.getCogs());
        assertEquals(16_185, financial.getProfit());
        assertEquals(LocalDate.of(2014, 1, 1), financial.getLocalDate());
        assertEquals(1, financial.getMonthNumber().intValue());
        assertEquals(Month.JANUARY, financial.getMonthName());
        assertEquals(2014, financial.getYear().intValue());

    }

    @Test
    public void checkNullColumnTest() {
        Financial financialBad = importData.getAllRows().get(9); //row 10 has a null column

        assertNull(financialBad.getSegment());
        assertEquals(1, importData.getValidationProblems(financialBad).size());
        assertTrue(importData.getValidationProblems(financialBad).get(0) instanceof ValueNullProblem);
    }

    @Test
    public void checkRegexColumnTest() {
        Financial financialBad = importData.getAllRows().get(19);


        assertNull(financialBad.getProduct());
        assertEquals(1, importData.getValidationProblems(financialBad).size());
        assertTrue(importData.getValidationProblems(financialBad).get(0) instanceof ValueFormatRegexProblem);
    }

    @Test
    public void checkRegexNumberColumnTest() {
        Financial financialBad = importData.getAllRows().get(29);

        assertNull(financialBad.getManufacturingPrice());
        assertEquals(1, importData.getValidationProblems(financialBad).size());
        assertTrue(importData.getValidationProblems(financialBad).get(0) instanceof ValueFormatRegexProblem);
    }

    @Test
    public void checkBusinessProblemColumnTest() {
        Financial financialBad = importData.getAllRows().get(39);

        assertEquals(34056.99F, financialBad.getGrossSales());
        assertEquals(1, importData.getValidationProblems(financialBad).size());
        assertTrue(importData.getValidationProblems(financialBad).get(0) instanceof BusinessProblem);
    }

    @Test
    public void checkRangeProblem1ColumnTest() {
        Financial financialBad = importData.getAllRows().get(49);

        assertEquals(13, financialBad.getMonthNumber().intValue());
        assertEquals(1, importData.getValidationProblems(financialBad).size());
        assertTrue(importData.getValidationProblems(financialBad).get(0) instanceof ValueRangeProblem);
    }

    @Test
    public void checkRangeProblem21ColumnTest() {
        Financial financialBad = importData.getAllRows().get(59);

        assertNull(financialBad.getMonthNumber());
        assertEquals(1, importData.getValidationProblems(financialBad).size());
        assertTrue(importData.getValidationProblems(financialBad).get(0) instanceof ValueRangeProblem);
    }

    @Test
    public void checkNoOfSheetsTest() {

        assertEquals(1, importData.getNoOfSheets());
    }

}
