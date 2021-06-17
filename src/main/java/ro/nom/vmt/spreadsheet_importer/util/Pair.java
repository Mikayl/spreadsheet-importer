package ro.nom.vmt.spreadsheet_importer.util;

/*
 *@Author Mihai Vasile (2021)
 *
 * This file is part of the Spreadsheet Importer project
 * This file as well as the project have an MIT license
 */

import java.util.Objects;

public class Pair<P1, P2> {
    public final P1 p1;
    public final P2 p2;

    public Pair(P1 p1, P2 p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public String toString() {
        return p1 + "&" + p2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(p1, pair.p1) &&
                Objects.equals(p2, pair.p2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(p1, p2);
    }
}
