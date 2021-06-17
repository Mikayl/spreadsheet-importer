package ro.nom.vmt.spreadsheet_importer.problems;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import java.util.Collections;
import java.util.List;

public class InstantiationProblem extends RuntimeException {

    public final List<RowProblem> problems;

    public InstantiationProblem(List<RowProblem> problems) {
        this.problems = problems;
    }

    public InstantiationProblem(RowProblem problem) {
        this.problems = Collections.singletonList(problem);
    }

    public List<RowProblem> getValidationProblems() {
        return problems;
    }
}
