package pe.edu.unaj.aed2.semana10;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana10.modelo.GrafoDirigido;
import pe.edu.unaj.aed2.semana10.tarea.TareaSemana10;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la tarea. No se ejecutan con "./mvnw test"; para correrlas:
 * ./mvnw test -Dgroups=tarea -DexcludedGroups=none
 */
@Tag("tarea")
class TareaSemana10Test {

    //   0 → 1 → 3        rombo: 3 se alcanza por dos caminos, pero NO hay ciclo
    //   ↓       ↑
    //   2 ──────┘        4 → 5 (otra parte)
    private static final int[][] ROMBO = {{0, 1}, {0, 2}, {1, 3}, {2, 3}, {4, 5}};

    @Test
    void ordenTopologicoEnElEjemplo() {
        assertTrue(esValido(6, ROMBO, TareaSemana10.ordenTopologico(6, ROMBO)));
        assertTrue(esValido(3, new int[0][], TareaSemana10.ordenTopologico(3, new int[0][])));
        assertEquals(0, TareaSemana10.ordenTopologico(3, new int[][]{{0, 1}, {1, 2}, {2, 0}}).length);
    }

    @Test
    void tieneCicloEnElEjemplo() {
        assertFalse(TareaSemana10.tieneCiclo(6, ROMBO), "llegar dos veces a 3 no es un ciclo");
        assertTrue(TareaSemana10.tieneCiclo(3, new int[][]{{0, 1}, {1, 2}, {2, 0}}));
        assertTrue(TareaSemana10.tieneCiclo(2, new int[][]{{1, 1}}), "un arco a sí mismo es un ciclo");
        assertFalse(TareaSemana10.tieneCiclo(4, new int[0][]));
    }

    @Test
    void minimoCiclosEnElEjemplo() {
        assertEquals(3, TareaSemana10.minimoCiclos(6, ROMBO), "0 → 1 → 3");
        assertEquals(1, TareaSemana10.minimoCiclos(4, new int[0][]));
        assertEquals(0, TareaSemana10.minimoCiclos(0, new int[0][]));
        assertEquals(-1, TareaSemana10.minimoCiclos(3, new int[][]{{0, 1}, {1, 2}, {2, 0}}));
    }

    @Test
    void coincideConElModeloEnGrafosAleatorios() {
        Random r = new Random(10);
        for (int prueba = 0; prueba < 300; prueba++) {
            int n = 1 + r.nextInt(9);
            int[][] arcos = new int[r.nextInt(2 * n)][];
            GrafoDirigido g = new GrafoDirigido();
            for (int i = 0; i < n; i++) {
                g.agregarVertice(String.valueOf(i));
            }
            for (int k = 0; k < arcos.length; k++) {
                arcos[k] = new int[]{r.nextInt(n), r.nextInt(n)};
                g.agregarArco(String.valueOf(arcos[k][0]), String.valueOf(arcos[k][1]));
            }
            String caso = n + " " + Arrays.deepToString(arcos);
            boolean ciclo = g.tieneCiclo();
            assertEquals(ciclo, TareaSemana10.tieneCiclo(n, arcos), caso);
            int[] orden = TareaSemana10.ordenTopologico(n, arcos);
            if (ciclo) {
                assertEquals(0, orden.length, caso);
                assertEquals(-1, TareaSemana10.minimoCiclos(n, arcos), caso);
            } else {
                assertTrue(esValido(n, arcos, orden), caso);
                assertEquals(g.caminoCritico().size(), TareaSemana10.minimoCiclos(n, arcos), caso);
            }
        }
    }

    private static boolean esValido(int n, int[][] arcos, int[] orden) {
        if (orden.length != n) {
            return false;
        }
        int[] posicion = new int[n];
        boolean[] visto = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (orden[i] < 0 || orden[i] >= n || visto[orden[i]]) {
                return false;
            }
            visto[orden[i]] = true;
            posicion[orden[i]] = i;
        }
        for (int[] a : arcos) {
            if (posicion[a[0]] >= posicion[a[1]]) {
                return false;
            }
        }
        return true;
    }
}
