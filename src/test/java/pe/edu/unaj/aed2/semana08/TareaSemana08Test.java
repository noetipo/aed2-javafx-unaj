package pe.edu.unaj.aed2.semana08;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana08.tarea.TareaSemana08;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la tarea. No se ejecutan con "./mvnw test"; para correrlas:
 * ./mvnw test -Dgroups=tarea -DexcludedGroups=none
 */
@Tag("tarea")
class TareaSemana08Test {

    private static final int I = TareaSemana08.INFINITO;

    //   0 --4-- 1 --1-- 2        el camino directo 0-2 (10) es peor que 0-1-2 (5)
    //   |               |
    //   10 ------------ +        3 --2-- 4   (otra componente)
    private static int[][] ejemplo() {
        int[][] w = new int[5][5];
        for (int[] fila : w) {
            java.util.Arrays.fill(fila, -1);
        }
        for (int k = 0; k < 5; k++) {
            w[k][k] = 0;
        }
        arista(w, 0, 1, 4);
        arista(w, 1, 2, 1);
        arista(w, 0, 2, 10);
        arista(w, 3, 4, 2);
        return w;
    }

    private static void arista(int[][] w, int a, int b, int p) {
        w[a][b] = p;
        w[b][a] = p;
    }

    @Test
    void dijkstraMatriz() {
        assertArrayEquals(new int[]{0, 4, 5, I, I}, TareaSemana08.dijkstraMatriz(ejemplo(), 0));
        assertArrayEquals(new int[]{I, I, I, 2, 0}, TareaSemana08.dijkstraMatriz(ejemplo(), 4));
    }

    @Test
    void coincideConFloydWarshallEnGrafosAleatorios() {
        Random r = new Random(8);
        for (int prueba = 0; prueba < 40; prueba++) {
            int n = 2 + r.nextInt(9);
            int[][] w = new int[n][n];
            long[][] d = new long[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    w[i][j] = i == j ? 0 : -1;
                    d[i][j] = i == j ? 0 : Long.MAX_VALUE / 4;
                }
            }
            for (int k = 0; k < n * 2; k++) {
                int a = r.nextInt(n);
                int b = r.nextInt(n);
                if (a != b) {
                    int p = r.nextInt(20);
                    arista(w, a, b, p);
                }
            }
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (w[i][j] >= 0) {
                        d[i][j] = w[i][j];
                    }
                }
            }
            for (int k = 0; k < n; k++) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        d[i][j] = Math.min(d[i][j], d[i][k] + d[k][j]);
                    }
                }
            }
            for (int s = 0; s < n; s++) {
                int[] dist = TareaSemana08.dijkstraMatriz(w, s);
                for (int t = 0; t < n; t++) {
                    long esperado = d[s][t] >= Long.MAX_VALUE / 4 ? I : d[s][t];
                    assertEquals(esperado, dist[t], "de " + s + " a " + t);
                }
            }
        }
    }

    @Test
    void caminoMatriz() {
        assertEquals(List.of(0, 1, 2), TareaSemana08.caminoMatriz(ejemplo(), 0, 2));
        assertEquals(List.of(2, 1, 0), TareaSemana08.caminoMatriz(ejemplo(), 2, 0));
        assertEquals(List.of(3), TareaSemana08.caminoMatriz(ejemplo(), 3, 3));
        assertTrue(TareaSemana08.caminoMatriz(ejemplo(), 0, 4).isEmpty());
    }

    @Test
    void distanciaConParada() {
        assertEquals(5, TareaSemana08.distanciaConParada(ejemplo(), 0, 1, 2));
        assertEquals(6, TareaSemana08.distanciaConParada(ejemplo(), 0, 2, 1));    // 0→2 (5, por 1) + 2→1 (1)
        assertEquals(8, TareaSemana08.distanciaConParada(ejemplo(), 1, 0, 1));    // ir a 0 y volver: 4 + 4
        assertEquals(-1, TareaSemana08.distanciaConParada(ejemplo(), 0, 3, 4));
    }
}
