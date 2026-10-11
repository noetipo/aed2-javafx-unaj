package pe.edu.unaj.aed2.semana12;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana12.tarea.TareaSemana12;

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
class TareaSemana12Test {

    // La red clásica donde hay que devolver flujo: 0 = fuente, 5 = destino.
    //   0 → 1 (2), 0 → 2 (2), 1 → 3 (2), 2 → 3 (2), 1 → 4 (2), 3 → 5 (2), 4 → 5 (2)
    private static int[][] ejemplo() {
        int[][] c = new int[6][6];
        c[0][1] = 2;
        c[0][2] = 2;
        c[1][3] = 2;
        c[2][3] = 2;
        c[1][4] = 2;
        c[3][5] = 2;
        c[4][5] = 2;
        return c;
    }

    @Test
    void flujoMaximoEnElEjemplo() {
        assertEquals(4, TareaSemana12.flujoMaximo(ejemplo(), 0, 5));
        assertEquals(0, TareaSemana12.flujoMaximo(ejemplo(), 5, 0), "no hay arcos de regreso");
        int[][] dobles = new int[2][2];
        dobles[0][1] = 3;
        dobles[1][0] = 5;
        assertEquals(3, TareaSemana12.flujoMaximo(dobles, 0, 1), "arcos en ambos sentidos");
    }

    @Test
    void corteMinimoEnElEjemplo() {
        boolean[] lado = TareaSemana12.corteMinimo(ejemplo(), 0, 5);
        assertTrue(lado[0]);
        assertFalse(lado[5]);
        assertEquals(4, capacidadDelCorte(ejemplo(), lado));
    }

    @Test
    void emparejamientoEnElEjemplo() {
        // 3 conductores, 3 rutas: el conductor 0 solo puede la ruta 0, el 1 puede 0 y 1, el 2 puede 1 y 2.
        boolean[][] puede = {{true, false, false}, {true, true, false}, {false, true, true}};
        assertEquals(3, TareaSemana12.emparejamientoMaximo(puede));
        boolean[][] pocos = {{true, false}, {true, false}, {true, false}};
        assertEquals(1, TareaSemana12.emparejamientoMaximo(pocos), "las tres solo pueden la misma ruta");
        assertEquals(0, TareaSemana12.emparejamientoMaximo(new boolean[2][3]));
    }

    @Test
    void coincideConFuerzaBrutaEnRedesAleatorias() {
        Random r = new Random(12);
        for (int prueba = 0; prueba < 150; prueba++) {
            int n = 2 + r.nextInt(6);
            int[][] cap = new int[n][n];
            for (int k = r.nextInt(3 * n); k > 0; k--) {
                int a = r.nextInt(n);
                int b = r.nextInt(n);
                if (a != b) {
                    cap[a][b] = r.nextInt(10);
                }
            }
            int[][] copia = Arrays.stream(cap).map(int[]::clone).toArray(int[][]::new);
            int esperado = corteMinimoFuerzaBruta(cap, 0, n - 1);
            String caso = Arrays.deepToString(cap);
            assertEquals(esperado, TareaSemana12.flujoMaximo(cap, 0, n - 1), caso);
            boolean[] lado = TareaSemana12.corteMinimo(cap, 0, n - 1);
            assertTrue(lado[0] && !lado[n - 1], caso);
            assertEquals(esperado, capacidadDelCorte(cap, lado), caso);
            assertArrayEquals(copia, cap, "no debe modificar cap");
        }
        for (int prueba = 0; prueba < 100; prueba++) {
            int a = 1 + r.nextInt(5);
            int b = 1 + r.nextInt(5);
            boolean[][] puede = new boolean[a][b];
            for (boolean[] fila : puede) {
                for (int j = 0; j < b; j++) {
                    fila[j] = r.nextInt(3) == 0;
                }
            }
            assertEquals(emparejamientoFuerzaBruta(puede, 0, new boolean[b]), TareaSemana12.emparejamientoMaximo(puede),
                    Arrays.deepToString(puede));
        }
    }

    private static int capacidadDelCorte(int[][] cap, boolean[] lado) {
        int suma = 0;
        for (int u = 0; u < cap.length; u++) {
            for (int v = 0; v < cap.length; v++) {
                if (lado[u] && !lado[v]) {
                    suma += cap[u][v];
                }
            }
        }
        return suma;
    }

    private static int corteMinimoFuerzaBruta(int[][] cap, int s, int t) {
        int n = cap.length;
        int mejor = Integer.MAX_VALUE;
        for (int m = 0; m < (1 << n); m++) {
            if ((m & (1 << s)) == 0 || (m & (1 << t)) != 0) {
                continue;
            }
            boolean[] lado = new boolean[n];
            for (int v = 0; v < n; v++) {
                lado[v] = (m & (1 << v)) != 0;
            }
            mejor = Math.min(mejor, capacidadDelCorte(cap, lado));
        }
        return mejor;
    }

    private static int emparejamientoFuerzaBruta(boolean[][] puede, int i, boolean[] usada) {
        if (i == puede.length) {
            return 0;
        }
        int mejor = emparejamientoFuerzaBruta(puede, i + 1, usada);
        for (int j = 0; j < usada.length; j++) {
            if (puede[i][j] && !usada[j]) {
                usada[j] = true;
                mejor = Math.max(mejor, 1 + emparejamientoFuerzaBruta(puede, i + 1, usada));
                usada[j] = false;
            }
        }
        return mejor;
    }
}
