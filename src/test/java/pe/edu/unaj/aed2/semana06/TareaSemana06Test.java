package pe.edu.unaj.aed2.semana06;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana06.tarea.TareaSemana06;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la tarea. No se ejecutan con "./mvnw test"; para correrlas:
 * ./mvnw test -Dgroups=tarea -DexcludedGroups=none
 */
@Tag("tarea")
class TareaSemana06Test {

    @Test
    void esMonticuloMin() {
        assertTrue(TareaSemana06.esMonticuloMin(new int[0]));
        assertTrue(TareaSemana06.esMonticuloMin(new int[]{7}));
        assertTrue(TareaSemana06.esMonticuloMin(new int[]{5, 20, 10, 40, 30, 50, 15}));
        assertTrue(TareaSemana06.esMonticuloMin(new int[]{3, 3, 3}));
        assertFalse(TareaSemana06.esMonticuloMin(new int[]{5, 20, 10, 40, 30, 50, 8}));  // 8 < su padre 10
        assertFalse(TareaSemana06.esMonticuloMin(new int[]{5, 20, 10, 40, 19}));          // 19 < su padre 20
    }

    @Test
    void construirMonticulo() {
        int[] a = {9, 4, 7, 1, 8, 2, 6, 3, 5};
        int[] copia = a.clone();
        TareaSemana06.construirMonticulo(a);
        assertTrue(TareaSemana06.esMonticuloMin(a), Arrays.toString(a));
        Arrays.sort(copia);
        int[] ordenado = a.clone();
        Arrays.sort(ordenado);
        assertArrayEquals(copia, ordenado, "debe tener los mismos elementos");
        assertArrayEquals(new int[]{1, 3, 2, 4, 8, 7, 6, 9, 5}, a, "construcción de abajo hacia arriba");
        Random r = new Random(3);
        for (int prueba = 0; prueba < 50; prueba++) {
            int[] b = r.ints(r.nextInt(40), -100, 100).toArray();
            TareaSemana06.construirMonticulo(b);
            assertTrue(TareaSemana06.esMonticuloMin(b), Arrays.toString(b));
        }
    }

    @Test
    void kMayores() {
        int[] a = {35, 12, 48, 7, 26, 19, 41, 3, 30, 15};
        assertArrayEquals(new int[]{48, 41, 35}, TareaSemana06.kMayores(a, 3));
        assertArrayEquals(new int[0], TareaSemana06.kMayores(a, 0));
        assertArrayEquals(new int[]{9, 9, 8}, TareaSemana06.kMayores(new int[]{1, 9, 8, 9, 2}, 3));
        int[] todos = TareaSemana06.kMayores(a, a.length);
        assertArrayEquals(new int[]{48, 41, 35, 30, 26, 19, 15, 12, 7, 3}, todos);
        assertArrayEquals(new int[]{35, 12, 48, 7, 26, 19, 41, 3, 30, 15}, a, "no debe modificar la entrada");
        assertThrows(IllegalArgumentException.class, () -> TareaSemana06.kMayores(a, 11));
        assertThrows(IllegalArgumentException.class, () -> TareaSemana06.kMayores(a, -1));
    }
}
