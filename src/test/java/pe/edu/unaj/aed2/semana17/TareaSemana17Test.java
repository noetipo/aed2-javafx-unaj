package pe.edu.unaj.aed2.semana17;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana17.modelo.Viajante;
import pe.edu.unaj.aed2.semana17.tarea.TareaSemana17;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la tarea. No se ejecutan con "./mvnw test"; para correrlas:
 * ./mvnw test -Dgroups=tarea -DexcludedGroups=none
 */
@Tag("tarea")
class TareaSemana17Test {

    // Un cuadrado de lado 10 con un vértice en el centro: la mejor ruta recorre el borde y entra al centro.
    private static final int[][] CUADRADO = {
            {0, 10, 14, 10, 7},
            {10, 0, 10, 14, 7},
            {14, 10, 0, 10, 7},
            {10, 14, 10, 0, 7},
            {7, 7, 7, 7, 0}};

    @Test
    void ejemplos() {
        assertEquals(44, TareaSemana17.ramificacionYPoda(CUADRADO));
        assertEquals(44, TareaSemana17.heldKarp(CUADRADO));
        assertEquals(0, TareaSemana17.heldKarp(new int[][]{{0}}));
        assertEquals(10, TareaSemana17.heldKarp(new int[][]{{0, 5}, {5, 0}}), "ir y volver");
        int[] ruta = TareaSemana17.insercionMasBarata(CUADRADO);
        assertTrue(esRuta(ruta, 5));
    }

    @Test
    void coincidenConLaFuerzaBrutaDelModelo() {
        Random r = new Random(17);
        for (int prueba = 0; prueba < 60; prueba++) {
            int n = 2 + r.nextInt(7);
            int[][] d = puntos(r, n);
            int optimo = optimo(d);
            assertEquals(optimo, TareaSemana17.ramificacionYPoda(d), Arrays.deepToString(d));
            assertEquals(optimo, TareaSemana17.heldKarp(d), Arrays.deepToString(d));
            int[] ruta = TareaSemana17.insercionMasBarata(d);
            assertTrue(esRuta(ruta, n), Arrays.toString(ruta));
            assertTrue(largo(d, ruta) <= 2 * optimo, "inserción más barata: a lo más el doble del óptimo");
        }
    }

    @Test
    void sonRapidos() {
        Random r = new Random(1717);
        int[][] d12 = puntos(r, 12);
        int poda = assertTimeoutPreemptively(Duration.ofSeconds(8), () -> TareaSemana17.ramificacionYPoda(d12));
        int dp12 = assertTimeoutPreemptively(Duration.ofSeconds(3), () -> TareaSemana17.heldKarp(d12));
        assertEquals(dp12, poda, "los dos métodos exactos deben coincidir");
        int[][] d16 = puntos(r, 16);
        int dp16 = assertTimeoutPreemptively(Duration.ofSeconds(3), () -> TareaSemana17.heldKarp(d16));
        int[] heuristica = TareaSemana17.insercionMasBarata(d16);
        assertTrue(esRuta(heuristica, 16));
        assertTrue(largo(d16, heuristica) >= dp16, "ninguna ruta es mejor que el óptimo");
    }

    // ------------------------------------------------------------------

    /**
     * Puntos al azar en un cuadrado de 100 × 100; distancia euclidiana redondeada hacia arriba (sigue cumpliendo la
     * desigualdad triangular).
     */
    private static int[][] puntos(Random r, int n) {
        double[][] p = new double[n][2];
        for (double[] q : p) {
            q[0] = r.nextDouble() * 100;
            q[1] = r.nextDouble() * 100;
        }
        int[][] d = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d[i][j] = (int) Math.ceil(Math.hypot(p[i][0] - p[j][0], p[i][1] - p[j][1]));
            }
        }
        return d;
    }

    private static int optimo(int[][] d) {
        List<String> nombres = new ArrayList<>();
        for (int i = 0; i < d.length; i++) {
            nombres.add("v" + i);
        }
        return new Viajante(nombres, d).fuerzaBruta().km();
    }

    private static boolean esRuta(int[] ruta, int n) {
        if (ruta.length != n || ruta[0] != 0) {
            return false;
        }
        boolean[] visto = new boolean[n];
        for (int v : ruta) {
            if (v < 0 || v >= n || visto[v]) {
                return false;
            }
            visto[v] = true;
        }
        return true;
    }

    private static int largo(int[][] d, int[] ruta) {
        int total = 0;
        for (int k = 0; k < ruta.length; k++) {
            total += d[ruta[k]][ruta[(k + 1) % ruta.length]];
        }
        return total;
    }
}
