package pe.edu.unaj.aed2.semana01;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import pe.edu.unaj.aed2.semana01.caso.CatalogoPrecios;
import pe.edu.unaj.aed2.semana01.caso.CatalogoPrecios.Producto;
import pe.edu.unaj.aed2.semana01.modelo.EstadoArreglo;
import pe.edu.unaj.aed2.semana01.modelo.Ordenamientos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdenamientosTest {

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 2, 7, 100, 1000})
    void losTresAlgoritmosOrdenan(int n) {
        int[] esperado = Ordenamientos.aleatorio(n, 42);
        int[] burbuja = esperado.clone();
        int[] insercion = esperado.clone();
        int[] merge = esperado.clone();
        Arrays.sort(esperado);

        Ordenamientos.burbuja(burbuja);
        Ordenamientos.insercion(insercion);
        Ordenamientos.mergeSort(merge);

        assertArrayEquals(esperado, burbuja);
        assertArrayEquals(esperado, insercion);
        assertArrayEquals(esperado, merge);
    }

    @Test
    void burbujaEnArregloOrdenadoHaceNMenosUnaComparacion() {
        int[] a = {1, 2, 3, 4, 5, 6};
        assertEquals(5, Ordenamientos.burbuja(a));
    }

    @Test
    void insercionEnPeorCasoHaceNCuadradoMedios() {
        int n = 50;
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = n - i;
        }
        assertEquals((long) n * (n - 1) / 2, Ordenamientos.insercion(a));
    }

    @Test
    void mergeSortHaceMenosComparacionesQueBurbujaEnPromedio() {
        int[] a = Ordenamientos.aleatorio(2000, 1);
        assertTrue(Ordenamientos.mergeSort(a.clone()) < Ordenamientos.burbuja(a.clone()) / 10);
    }

    @Test
    void mergeSortGenericoEsEstable() {
        List<String[]> pares = new ArrayList<>(List.of(
                new String[]{"b", "1"}, new String[]{"a", "2"}, new String[]{"b", "3"}, new String[]{"a", "4"}));
        Ordenamientos.mergeSort(pares, Comparator.comparing(p -> p[0]));
        assertEquals(List.of("2", "4", "1", "3"), pares.stream().map(p -> p[1]).toList());
    }

    @Test
    void pasosTerminanOrdenadosYNoModificanLaEntrada() {
        int[] original = {5, 1, 4, 2, 3};
        for (List<EstadoArreglo> pasos : List.of(
                Ordenamientos.pasosBurbuja(original), Ordenamientos.pasosInsercion(original))) {
            assertEquals(EstadoArreglo.Tipo.INICIO, pasos.get(0).tipo());
            EstadoArreglo ultimo = pasos.get(pasos.size() - 1);
            assertEquals(EstadoArreglo.Tipo.FIN, ultimo.tipo());
            assertArrayEquals(new int[]{1, 2, 3, 4, 5}, ultimo.valores());
        }
        assertArrayEquals(new int[]{5, 1, 4, 2, 3}, original);
    }

    @Test
    void estadoArregloEsInmutable() {
        int[] datos = {3, 2, 1};
        EstadoArreglo e = new EstadoArreglo(datos, 0, 1, EstadoArreglo.Tipo.COMPARACION, 0, true, 0,
                Map.of(), "", "");
        datos[0] = 99;
        e.valores()[1] = 99;
        assertArrayEquals(new int[]{3, 2, 1}, e.valores());
    }

    @Test
    void catalogoSeCargaYOrdenaPorPrecio() {
        CatalogoPrecios catalogo = CatalogoPrecios.desdeRecurso();
        assertFalse(catalogo.productos().isEmpty());
        List<Producto> asc = catalogo.ordenadosPorPrecio(true);
        for (int i = 1; i < asc.size(); i++) {
            assertTrue(asc.get(i - 1).precio() <= asc.get(i).precio());
        }
        List<Producto> desc = catalogo.ordenadosPorPrecio(false);
        assertEquals(asc.get(0).precio(), desc.get(desc.size() - 1).precio());
    }

    @Test
    void cadaPasoApuntaAUnaLineaValidaDelPseudocodigo() {
        int[] datos = Ordenamientos.aleatorio(12, 3);
        for (EstadoArreglo p : Ordenamientos.pasosBurbuja(datos)) {
            assertTrue(p.linea() >= 0 && p.linea() <= Ordenamientos.PSEUDO_BURBUJA.size());
        }
        for (EstadoArreglo p : Ordenamientos.pasosInsercion(datos)) {
            assertTrue(p.linea() >= 0 && p.linea() <= Ordenamientos.PSEUDO_INSERCION.size());
        }
    }

    @Test
    void trazaLlevaLaCuentaDeComparaciones() {
        int[] datos = Ordenamientos.aleatorio(15, 8);
        List<EstadoArreglo> pasos = Ordenamientos.pasosInsercion(datos);
        String final_ = pasos.get(pasos.size() - 1).variables().get("comparaciones");
        assertEquals(String.valueOf(Ordenamientos.insercion(datos.clone())), final_);
    }
}
