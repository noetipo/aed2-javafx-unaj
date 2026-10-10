package pe.edu.unaj.aed2.semana01;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana01.modelo.Ordenamientos;
import pe.edu.unaj.aed2.semana01.tarea.TareaSemana01;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pruebas de la tarea. No se ejecutan con "./mvnw test"; para correrlas:
 * ./mvnw test -Dgroups=tarea -DexcludedGroups=none
 */
@Tag("tarea")
class TareaSemana01Test {

    @Test
    void seleccionOrdena() {
        for (int n : new int[]{0, 1, 2, 10, 500}) {
            int[] a = Ordenamientos.aleatorio(n, n + 3);
            int[] esperado = a.clone();
            Arrays.sort(esperado);
            TareaSemana01.seleccion(a);
            assertArrayEquals(esperado, a, "n = " + n);
        }
    }

    @Test
    void seleccionSiempreHaceNCuadradoMediosComparaciones() {
        int n = 40;
        long esperado = (long) n * (n - 1) / 2;
        assertEquals(esperado, TareaSemana01.seleccion(Ordenamientos.aleatorio(n, 9)));
        int[] ordenado = new int[n];
        Arrays.setAll(ordenado, i -> i);
        assertEquals(esperado, TareaSemana01.seleccion(ordenado));
    }

    @Test
    void busquedaBinaria() {
        int[] a = {2, 5, 8, 12, 16, 23, 38, 56, 72, 91};
        for (int i = 0; i < a.length; i++) {
            assertEquals(i, TareaSemana01.busquedaBinaria(a, a[i]));
        }
        assertEquals(-1, TareaSemana01.busquedaBinaria(a, 1));
        assertEquals(-1, TareaSemana01.busquedaBinaria(a, 13));
        assertEquals(-1, TareaSemana01.busquedaBinaria(a, 100));
        assertEquals(-1, TareaSemana01.busquedaBinaria(new int[0], 5));
    }

    @Test
    void comparacionesPeorCaso() {
        assertArrayEquals(new long[]{0, 0}, TareaSemana01.comparacionesPeorCaso(0));
        assertArrayEquals(new long[]{1, 1}, TareaSemana01.comparacionesPeorCaso(1));
        assertArrayEquals(new long[]{1000, 10}, TareaSemana01.comparacionesPeorCaso(1000));
        assertArrayEquals(new long[]{1024, 11}, TareaSemana01.comparacionesPeorCaso(1024));
    }
}
