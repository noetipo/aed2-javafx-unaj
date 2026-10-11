package pe.edu.unaj.aed2.semana09;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana08.modelo.GrafoPonderado;
import pe.edu.unaj.aed2.semana09.modelo.ArbolExpansion;
import pe.edu.unaj.aed2.semana09.modelo.ArbolExpansion.Tramo;
import pe.edu.unaj.aed2.semana09.tarea.TareaSemana09;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pruebas de la tarea. No se ejecutan con "./mvnw test"; para correrlas:
 * ./mvnw test -Dgroups=tarea -DexcludedGroups=none
 */
@Tag("tarea")
class TareaSemana09Test {

    //   0 --1-- 1
    //   |     / |        árbol mínimo: 0-1 (1), 1-2 (2), 2-3 (3)  →  6
    //   4   2   5        el tramo 0-2 (4) cerraría un ciclo
    //   |  /    |
    //   2 --3-- 3
    private static final int[][] TRAMOS = {{0, 1, 1}, {1, 2, 2}, {0, 2, 4}, {2, 3, 3}, {1, 3, 5}};

    @Test
    void primMatrizEnElEjemplo() {
        assertEquals(6, TareaSemana09.primMatriz(matriz(4, TRAMOS)));
        assertEquals(0, TareaSemana09.primMatriz(new int[][]{{0}}));
        assertEquals(-1, TareaSemana09.primMatriz(matriz(5, TRAMOS)), "el vértice 4 está aislado");
    }

    @Test
    void tramoObligatorioEnElEjemplo() {
        assertEquals(6, TareaSemana09.costoConTramoObligatorio(4, TRAMOS, 0), "0-1 ya estaba en el árbol");
        assertEquals(8, TareaSemana09.costoConTramoObligatorio(4, TRAMOS, 2), "0-2 (4) reemplaza a 1-2 (2)");
        assertEquals(8, TareaSemana09.costoConTramoObligatorio(4, TRAMOS, 4), "1-3 (5) reemplaza a 2-3 (3)");
        assertEquals(-1, TareaSemana09.costoConTramoObligatorio(5, TRAMOS, 0));
    }

    @Test
    void espaciamientoEnElEjemplo() {
        assertEquals(3, TareaSemana09.espaciamiento(4, TRAMOS, 2), "zonas {0,1,2} y {3}");
        assertEquals(2, TareaSemana09.espaciamiento(4, TRAMOS, 3), "zonas {0,1}, {2} y {3}");
        assertEquals(1, TareaSemana09.espaciamiento(4, TRAMOS, 4), "cada vértice solo");
        assertEquals(-1, TareaSemana09.espaciamiento(6, TRAMOS, 2), "4 y 5 aislados: quedan al menos 3 grupos");
    }

    @Test
    void coincideConElModeloEnGrafosAleatorios() {
        Random r = new Random(9);
        for (int prueba = 0; prueba < 80; prueba++) {
            int n = 2 + r.nextInt(8);
            int[][] tramos = aleatorios(r, n, n - 1 + r.nextInt(2 * n));
            GrafoPonderado g = grafo(n, tramos);
            List<Tramo> arbol = ArbolExpansion.kruskal(g);
            boolean conexo = arbol.size() == n - 1;
            int total = ArbolExpansion.total(arbol);
            assertEquals(conexo ? total : -1, TareaSemana09.primMatriz(matriz(n, tramos)),
                    "primMatriz en " + Arrays.deepToString(tramos));

            // Con el tramo obligatorio a 0 km, Kruskal lo toma primero; luego se le suma su km real.
            int obligatorio = r.nextInt(tramos.length);
            int[][] copia = Arrays.stream(tramos).map(int[]::clone).toArray(int[][]::new);
            copia[obligatorio][2] = 0;
            int esperado = conexo ? ArbolExpansion.total(ArbolExpansion.kruskal(grafo(n, copia)))
                    + tramos[obligatorio][2] : -1;
            assertEquals(esperado, TareaSemana09.costoConTramoObligatorio(n, tramos, obligatorio),
                    "obligatorio " + obligatorio + " en " + Arrays.deepToString(tramos));

            // En un grafo conexo, el espaciamiento de k zonas es el (k−1)-ésimo tramo más largo del árbol mínimo.
            if (conexo && n >= 2) {
                List<Integer> largos = new ArrayList<>(arbol.stream().map(Tramo::km).toList());
                largos.sort(Comparator.reverseOrder());
                for (int k = 2; k <= n; k++) {
                    assertEquals((int) largos.get(k - 2), TareaSemana09.espaciamiento(n, tramos, k),
                            "k = " + k + " en " + Arrays.deepToString(tramos));
                }
            }
        }
    }

    @Test
    void noModificaLosTramos() {
        int[][] tramos = Arrays.stream(TRAMOS).map(int[]::clone).toArray(int[][]::new);
        TareaSemana09.costoConTramoObligatorio(4, tramos, 2);
        TareaSemana09.espaciamiento(4, tramos, 2);
        assertEquals(Arrays.deepToString(TRAMOS), Arrays.deepToString(tramos));
    }

    // ------------------------------------------------------------------

    private static int[][] aleatorios(Random r, int n, int m) {
        List<int[]> lista = new ArrayList<>();
        boolean[][] usado = new boolean[n][n];
        for (int k = 0; k < m; k++) {
            int a = r.nextInt(n);
            int b = r.nextInt(n);
            if (a != b && !usado[a][b]) {
                usado[a][b] = true;
                usado[b][a] = true;
                lista.add(new int[]{a, b, 1 + r.nextInt(20)});
            }
        }
        if (lista.isEmpty()) {
            lista.add(new int[]{0, 1, 7});
        }
        return lista.toArray(new int[0][]);
    }

    private static int[][] matriz(int n, int[][] tramos) {
        int[][] w = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(w[i], -1);
            w[i][i] = 0;
        }
        for (int[] t : tramos) {
            if (t[0] < n && t[1] < n) {
                w[t[0]][t[1]] = t[2];
                w[t[1]][t[0]] = t[2];
            }
        }
        return w;
    }

    private static GrafoPonderado grafo(int n, int[][] tramos) {
        GrafoPonderado g = new GrafoPonderado();
        for (int i = 0; i < n; i++) {
            g.agregarVertice(String.valueOf(i));
        }
        for (int[] t : tramos) {
            g.agregarArista(String.valueOf(t[0]), String.valueOf(t[1]), t[2]);
        }
        return g;
    }
}
