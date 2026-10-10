package pe.edu.unaj.aed2.semana02;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana02.tarea.TareaSemana02;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas de la tarea. No se ejecutan con "./mvnw test"; para correrlas:
 * ./mvnw test -Dgroups=tarea -DexcludedGroups=none
 */
@Tag("tarea")
class TareaSemana02Test {

    @Test
    void potencia() {
        assertEquals(1, TareaSemana02.potencia(7, 0));
        assertEquals(7, TareaSemana02.potencia(7, 1));
        assertEquals(1024, TareaSemana02.potencia(2, 10));
        assertEquals(-27, TareaSemana02.potencia(-3, 3));
        assertEquals(4_052_555_153_018_976_267L, TareaSemana02.potencia(3, 39));
        assertEquals(0, TareaSemana02.potencia(0, 5));
    }

    @Test
    void maximo() {
        int[] a = {4, -2, 17, 8, 17, 3, -9};
        assertEquals(17, TareaSemana02.maximo(a, 0, a.length - 1));
        assertEquals(8, TareaSemana02.maximo(a, 3, 3));
        assertEquals(4, TareaSemana02.maximo(a, 0, 1));
        assertEquals(-9, TareaSemana02.maximo(new int[]{-9}, 0, 0));
        assertThrows(IllegalArgumentException.class, () -> TareaSemana02.maximo(a, 3, 2));
    }

    @Test
    void kEsimoMenor() {
        int[] base = new Random(5).ints(301, -1000, 1000).toArray();
        int[] ordenado = base.clone();
        Arrays.sort(ordenado);
        for (int k : new int[]{0, 1, 150, 299, 300}) {
            assertEquals(ordenado[k], TareaSemana02.kEsimoMenor(base.clone(), k), "k = " + k);
        }
        assertEquals(5, TareaSemana02.kEsimoMenor(new int[]{5, 5, 5, 5}, 2));
    }
}
