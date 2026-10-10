package pe.edu.unaj.aed2.semana02;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pe.edu.unaj.aed2.semana02.modelo.EstadoParticion;
import pe.edu.unaj.aed2.semana02.modelo.QuickSort;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuickSortTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 3, 10, 1000})
    void ordenaArreglosAleatoriosConRepetidos(int n) {
        int[] a = new Random(n).ints(n, 0, 20).toArray();
        int[] esperado = a.clone();
        Arrays.sort(esperado);
        QuickSort.ordenar(a);
        assertArrayEquals(esperado, a);
    }

    @Test
    void particionDejaMenoresALaIzquierdaYMayoresALaDerecha() {
        int[] a = {7, 2, 9, 1, 5, 8, 4};
        int p = QuickSort.particionar(a, 0, a.length - 1);
        assertEquals(4, a[p]);
        for (int k = 0; k < p; k++) {
            assertTrue(a[k] < 4);
        }
        for (int k = p + 1; k < a.length; k++) {
            assertTrue(a[k] >= 4);
        }
    }

    @Test
    void peorCasoEnArregloOrdenadoEsCuadratico() {
        int n = 100;
        int[] ordenado = new int[n];
        Arrays.setAll(ordenado, i -> i);
        assertEquals((long) n * (n - 1) / 2, QuickSort.ordenar(ordenado.clone()));
        assertEquals(n - 1, QuickSort.profundidadMaxima(ordenado));
    }

    @Test
    void enPromedioLaProfundidadEsLogaritmica() {
        int[] a = new Random(3).ints(4096, 0, 1_000_000).toArray();
        assertTrue(QuickSort.profundidadMaxima(a) < 4 * 12, "profundidad muy alta para datos aleatorios");
    }

    @Test
    void pasosTerminanOrdenadosConTodosFijos() {
        int[] original = {5, 3, 8, 1, 9, 2};
        List<EstadoParticion> pasos = QuickSort.pasos(original);
        EstadoParticion ultimo = pasos.get(pasos.size() - 1);
        assertEquals(EstadoParticion.Tipo.FIN, ultimo.tipo());
        assertArrayEquals(new int[]{1, 2, 3, 5, 8, 9}, ultimo.valores());
        assertArrayEquals(new int[]{5, 3, 8, 1, 9, 2}, original);
        long ubicados = pasos.stream().filter(p -> p.tipo() == EstadoParticion.Tipo.PIVOTE_UBICADO).count();
        assertTrue(ubicados >= 1 && ubicados <= original.length);
    }
}
