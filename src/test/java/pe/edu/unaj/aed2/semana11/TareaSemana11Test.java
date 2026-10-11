package pe.edu.unaj.aed2.semana11;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana11.tarea.TareaSemana11;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la tarea. No se ejecutan con "./mvnw test"; para correrlas:
 * ./mvnw test -Dgroups=tarea -DexcludedGroups=none
 */
@Tag("tarea")
class TareaSemana11Test {

    private static final int I = TareaSemana11.INFINITO;

    //   0 --4-- 1 --1-- 2 --3-- 3        (no dirigido)    el centro es 1: su más lejano (3) está a 4
    private static int[][] linea() {
        int[][] w = vacia(4);
        arista(w, 0, 1, 4);
        arista(w, 1, 2, 1);
        arista(w, 2, 3, 3);
        return w;
    }

    @Test
    void floydEnElEjemplo() {
        int[][] d = TareaSemana11.floydWarshall(linea());
        assertArrayEquals(new int[]{0, 4, 5, 8}, d[0]);
        assertArrayEquals(new int[]{8, 4, 3, 0}, d[3]);
        int[][] dirigido = vacia(3);
        dirigido[0][1] = 2;
        dirigido[1][2] = 2;
        int[][] r = TareaSemana11.floydWarshall(dirigido);
        assertArrayEquals(new int[]{0, 2, 4}, r[0]);
        assertArrayEquals(new int[]{I, I, 0}, r[2], "en un grafo dirigido no se puede volver");
    }

    @Test
    void clausuraEnElEjemplo() {
        // Matemática (0) → Discreta (1) → AED I (2) → AED II (3); Comunicación (4) sola.
        boolean[][] arco = new boolean[5][5];
        arco[0][1] = true;
        arco[1][2] = true;
        arco[2][3] = true;
        boolean[][] a = TareaSemana11.clausuraTransitiva(arco);
        assertTrue(a[0][3], "Matemática es prerrequisito indirecto de AED II");
        assertFalse(a[3][0]);
        assertFalse(a[0][0], "sin ciclos, nadie se alcanza a sí mismo");
        assertFalse(a[4][0]);
        arco[3][1] = true;
        assertTrue(TareaSemana11.clausuraTransitiva(arco)[2][2], "1 → 2 → 3 → 1 es un ciclo");
    }

    @Test
    void centroEnElEjemplo() {
        assertEquals(1, TareaSemana11.centro(linea()));
        assertEquals(0, TareaSemana11.centro(vacia(1)));
        assertEquals(-1, TareaSemana11.centro(vacia(2)), "dos vértices sin arcos: nadie alcanza a todos");
    }

    @Test
    void coincideConUnaReferenciaEnGrafosAleatorios() {
        Random r = new Random(11);
        for (int prueba = 0; prueba < 100; prueba++) {
            int n = 1 + r.nextInt(8);
            boolean dirigido = r.nextBoolean();
            int[][] w = vacia(n);
            for (int k = r.nextInt(3 * n); k > 0; k--) {
                int a = r.nextInt(n);
                int b = r.nextInt(n);
                if (a != b) {
                    w[a][b] = r.nextInt(20);
                    if (!dirigido) {
                        w[b][a] = w[a][b];
                    }
                }
            }
            int[][] copia = Arrays.stream(w).map(int[]::clone).toArray(int[][]::new);
            int[][] esperado = referencia(w);
            assertArrayEquals(esperado, TareaSemana11.floydWarshall(w), Arrays.deepToString(w));
            assertArrayEquals(copia, w, "no debe modificar w");

            boolean[][] arco = new boolean[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    arco[i][j] = i != j && w[i][j] >= 0;
                }
            }
            assertArrayEquals(alcance(arco), TareaSemana11.clausuraTransitiva(arco), Arrays.deepToString(w));

            int centro = -1;
            int mejor = I;
            for (int i = 0; i < n; i++) {
                int exc = Arrays.stream(esperado[i]).max().orElse(0);
                if (exc < mejor) {
                    mejor = exc;
                    centro = i;
                }
            }
            assertEquals(centro, TareaSemana11.centro(w), Arrays.deepToString(w));
        }
    }

    // ------------------------------------------------------------------

    private static int[][] vacia(int n) {
        int[][] w = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(w[i], -1);
            w[i][i] = 0;
        }
        return w;
    }

    private static void arista(int[][] w, int a, int b, int p) {
        w[a][b] = p;
        w[b][a] = p;
    }

    /** Bellman–Ford desde cada vértice (otra forma de obtener lo mismo, más lenta). */
    private static int[][] referencia(int[][] w) {
        int n = w.length;
        int[][] d = new int[n][n];
        for (int s = 0; s < n; s++) {
            Arrays.fill(d[s], I);
            d[s][s] = 0;
            for (int vuelta = 0; vuelta < n; vuelta++) {
                for (int u = 0; u < n; u++) {
                    for (int v = 0; v < n; v++) {
                        if (u != v && w[u][v] >= 0 && d[s][u] != I && d[s][u] + w[u][v] < d[s][v]) {
                            d[s][v] = d[s][u] + w[u][v];
                        }
                    }
                }
            }
        }
        return d;
    }

    /** BFS desde cada vértice: alcanza[i][j] si hay camino de uno o más arcos. */
    private static boolean[][] alcance(boolean[][] arco) {
        int n = arco.length;
        boolean[][] a = new boolean[n][n];
        for (int s = 0; s < n; s++) {
            ArrayDeque<Integer> cola = new ArrayDeque<>();
            for (int v = 0; v < n; v++) {
                if (arco[s][v] && !a[s][v]) {
                    a[s][v] = true;
                    cola.add(v);
                }
            }
            while (!cola.isEmpty()) {
                int u = cola.poll();
                for (int v = 0; v < n; v++) {
                    if (arco[u][v] && !a[s][v]) {
                        a[s][v] = true;
                        cola.add(v);
                    }
                }
            }
        }
        return a;
    }
}
