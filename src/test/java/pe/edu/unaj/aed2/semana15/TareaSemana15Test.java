package pe.edu.unaj.aed2.semana15;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana14.modelo.Mochila;
import pe.edu.unaj.aed2.semana15.modelo.Huffman;
import pe.edu.unaj.aed2.semana15.tarea.TareaSemana15;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la tarea. No se ejecutan con "./mvnw test"; para correrlas:
 * ./mvnw test -Dgroups=tarea -DexcludedGroups=none
 */
@Tag("tarea")
class TareaSemana15Test {

    @Test
    void actividadesEnElEjemplo() {
        // 8-12, 9-10, 10-11, 11-13, 12-14: lo mejor es 9-10, 10-11, 11-13 (3), no la que empieza primero.
        assertEquals(3, TareaSemana15.maxActividades(new int[]{8, 9, 10, 11, 12}, new int[]{12, 10, 11, 13, 14}));
        assertEquals(0, TareaSemana15.maxActividades(new int[0], new int[0]));
        // Una larga que tapa a tres cortas: "la que empieza primero" elegiría solo 0-10.
        assertEquals(3, TareaSemana15.maxActividades(new int[]{0, 1, 4, 7}, new int[]{10, 3, 6, 9}));
    }

    @Test
    void actividadesCoincidenConFuerzaBruta() {
        Random r = new Random(15);
        for (int prueba = 0; prueba < 200; prueba++) {
            int n = r.nextInt(10);
            int[] inicio = new int[n];
            int[] fin = new int[n];
            for (int k = 0; k < n; k++) {
                inicio[k] = r.nextInt(20);
                fin[k] = inicio[k] + 1 + r.nextInt(6);
            }
            int[] copiaInicio = inicio.clone();
            int[] copiaFin = fin.clone();
            int mejor = 0;
            for (int m = 0; m < (1 << n); m++) {
                if (compatibles(inicio, fin, m)) {
                    mejor = Math.max(mejor, Integer.bitCount(m));
                }
            }
            assertEquals(mejor, TareaSemana15.maxActividades(inicio, fin), Arrays.toString(inicio) + " " + Arrays.toString(fin));
            assertArrayEquals(copiaInicio, inicio);
            assertArrayEquals(copiaFin, fin);
        }
    }

    @Test
    void mochilaFraccionariaEnElEjemplo() {
        // Feria (semana 14): queso 2/30, trucha 6/60, quinua 5/48, chuño 5/45, papa 3/15; capacidad 10.
        // Queso entero (30) + trucha entera (60) + 2/5 de quinua (19.2) = 109.2
        assertEquals(109.2, TareaSemana15.mochilaFraccionaria(new int[]{2, 6, 5, 5, 3}, new int[]{30, 60, 48, 45, 15}, 10),
                1e-9);
        assertEquals(0.0, TareaSemana15.mochilaFraccionaria(new int[]{4}, new int[]{10}, 0), 1e-9);
        assertEquals(10.0, TareaSemana15.mochilaFraccionaria(new int[]{4}, new int[]{10}, 10), 1e-9, "todo cabe");
        assertEquals(5.0, TareaSemana15.mochilaFraccionaria(new int[]{4}, new int[]{10}, 2), 1e-9, "la mitad");
    }

    @Test
    void mochilaFraccionariaEntreCotas() {
        Random r = new Random(15);
        for (int prueba = 0; prueba < 200; prueba++) {
            int n = 1 + r.nextInt(7);
            int[] peso = new int[n];
            int[] valor = new int[n];
            List<Mochila.Producto> productos = new ArrayList<>();
            int maxPeso = 0;
            for (int k = 0; k < n; k++) {
                peso[k] = 1 + r.nextInt(9);
                valor[k] = r.nextInt(40);
                maxPeso = Math.max(maxPeso, peso[k]);
                productos.add(new Mochila.Producto("p" + k, peso[k], valor[k]));
            }
            int c = r.nextInt(25);
            double f = TareaSemana15.mochilaFraccionaria(peso, valor, c);
            // Nunca peor que la 0/1, y nunca mejor que la 0/1 con espacio para un producto entero más.
            assertTrue(f + 1e-9 >= Mochila.resolver(productos, c).mejor(), "≥ mochila 0/1");
            assertTrue(f - 1e-9 <= Mochila.resolver(productos, c + maxPeso).mejor(), "≤ 0/1 con un producto más");
        }
    }

    @Test
    void decodificarTextosConHuffman() {
        for (String t : List.of("pachamama paqarin", "a", "aaab", "Allillanchu? Imaynalla kachkanki?")) {
            Map<Character, String> codigos = Huffman.codigos(t);
            assertEquals(t, TareaSemana15.decodificar(Huffman.codificar(t, codigos), codigos));
        }
        Map<Character, String> c = Map.of('a', "0", 'b', "10", 'c', "11");
        assertEquals("abcab", TareaSemana15.decodificar("01011010", c));
        assertEquals("", TareaSemana15.decodificar("", c));
        assertThrows(IllegalArgumentException.class, () -> TareaSemana15.decodificar("0101", c), "sobra un 1");
    }

    private static boolean compatibles(int[] inicio, int[] fin, int mascara) {
        for (int a = 0; a < inicio.length; a++) {
            for (int b = a + 1; b < inicio.length; b++) {
                if ((mascara & (1 << a)) != 0 && (mascara & (1 << b)) != 0
                        && inicio[a] < fin[b] && inicio[b] < fin[a]) {
                    return false;
                }
            }
        }
        return true;
    }
}
