package pe.edu.unaj.aed2.semana03;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana03.tarea.TablaSondeoLineal;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la tarea. No se ejecutan con "./mvnw test"; para correrlas:
 * ./mvnw test -Dgroups=tarea -DexcludedGroups=none
 */
@Tag("tarea")
class TablaSondeoLinealTest {

    // Con m = 7: "A" (65) cae en 2, "H" (72) en 2, "O" (79) en 2, "B" (66) en 3.
    @Test
    void lasColisionesSeResuelvenEnLaSiguienteCasillaLibre() {
        TablaSondeoLineal t = new TablaSondeoLineal(7);
        assertTrue(t.insertar("A"));
        assertTrue(t.insertar("H"));
        assertTrue(t.insertar("O"));
        assertTrue(t.insertar("B"));
        assertArrayEquals(new String[]{null, null, "A", "H", "O", "B", null}, t.casillas());
        assertEquals(4.0 / 7, t.factorCarga(), 1e-9);
    }

    @Test
    void elSondeoDaLaVueltaAlFinalDelArreglo() {
        TablaSondeoLineal t = new TablaSondeoLineal(7);
        // "F" (70) cae en 0; "M" (77) en 0 → 1; "U" (85) cae en 1 → 2.
        t.insertar("F");
        t.insertar("M");
        t.insertar("U");
        assertArrayEquals(new String[]{"F", "M", "U", null, null, null, null}, t.casillas());
        TablaSondeoLineal u = new TablaSondeoLineal(5);
        // m = 5: "D" (68) cae en 3, "I" (73) en 3 → 4, "N" (78) en 3 → 4 → 0.
        u.insertar("D");
        u.insertar("I");
        u.insertar("N");
        assertArrayEquals(new String[]{"N", null, null, "D", "I"}, u.casillas());
    }

    @Test
    void noInsertaDuplicados() {
        TablaSondeoLineal t = new TablaSondeoLineal(7);
        assertTrue(t.insertar("JUL-102"));
        assertFalse(t.insertar("JUL-102"));
        assertEquals(1.0 / 7, t.factorCarga(), 1e-9);
    }

    @Test
    void buscarYSondeos() {
        TablaSondeoLineal t = new TablaSondeoLineal(7);
        t.insertar("A");
        t.insertar("H");
        t.insertar("O");
        assertTrue(t.buscar("O"));
        assertEquals(3, t.sondeos("O"));
        assertEquals(1, t.sondeos("A"));
        assertFalse(t.buscar("V")); // "V" (86) cae en 2: revisa 2, 3, 4 y termina en la 5 (null)
        assertEquals(4, t.sondeos("V"));
        assertFalse(t.buscar("Z")); // "Z" (90) cae en 6, que está vacía
        assertEquals(1, t.sondeos("Z"));
    }

    @Test
    void tablaLlena() {
        TablaSondeoLineal t = new TablaSondeoLineal(3);
        t.insertar("A");
        t.insertar("B");
        t.insertar("C");
        assertFalse(t.buscar("D"));
        assertThrows(IllegalStateException.class, () -> t.insertar("D"));
    }
}
