package pe.edu.unaj.aed2.semana11;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana08.modelo.GrafoPonderado;
import pe.edu.unaj.aed2.semana11.caso.UbicacionAlmacen;
import pe.edu.unaj.aed2.semana11.caso.UbicacionAlmacen.Criterio;
import pe.edu.unaj.aed2.semana11.caso.UbicacionAlmacen.Propuesta;
import pe.edu.unaj.aed2.semana11.modelo.EstadoFloyd;
import pe.edu.unaj.aed2.semana11.modelo.FloydWarshall;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FloydWarshallTest {

    private static final GrafoPonderado RED = GrafoPonderado.desde(RedVial.cargar());

    @Test
    void coincideConDijkstraDesdeCadaPueblo() {
        verificarContraDijkstra(RED);
        FloydWarshall.Resultado r = FloydWarshall.calcular(RED);
        assertEquals(108, r.distancia("JUL", "PUT"));
        assertEquals(List.of("JUL", "HUA", "PUT"), r.camino("JUL", "PUT"));
        assertEquals(List.of("PUN"), r.camino("PUN", "PUN"));
    }

    @Test
    void coincideConDijkstraEnGrafosAleatorios() {
        Random azar = new Random(11);
        for (int prueba = 0; prueba < 50; prueba++) {
            int n = 1 + azar.nextInt(9);
            GrafoPonderado g = new GrafoPonderado();
            for (int i = 0; i < n; i++) {
                g.agregarVertice("v" + i);
            }
            int m = azar.nextInt(2 * n);
            for (int k = 0; k < m; k++) {
                int a = azar.nextInt(n);
                int b = azar.nextInt(n);
                if (a != b) {
                    g.agregarArista("v" + a, "v" + b, azar.nextInt(30));
                }
            }
            verificarContraDijkstra(g);
        }
    }

    @Test
    void losPasosTerminanEnLaMismaMatriz() {
        GrafoPonderado sub = FloydWarshall.subred(RED, FloydWarshall.SUBRED);
        List<EstadoFloyd> pasos = FloydWarshall.pasos(sub, Map.of());
        FloydWarshall.Resultado r = FloydWarshall.calcular(sub);
        EstadoFloyd fin = pasos.get(pasos.size() - 1);
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                assertEquals(r.dist()[i][j], fin.dist().get(i).get(j));
                assertEquals(r.sig()[i][j], fin.sig().get(i).get(j));
            }
        }
        assertEquals(7, pasos.stream().filter(p -> p.tipo() == EstadoFloyd.Tipo.NUEVO_K).count());
        // Por cada k se examinan los 6 × 5 pares que no están en la diagonal, ni en la fila ni en la columna k.
        assertEquals(7 * 30, pasos.stream().filter(p -> p.i() >= 0).count());
        // Juliaca → Putina: primero 127 por Azángaro (k = AZA), luego 108 por Huancané (k = HUA).
        List<Integer> mejoras = pasos.stream().filter(p -> p.tipo() == EstadoFloyd.Tipo.MEJORA
                        && p.i() == 0 && p.j() == FloydWarshall.SUBRED.indexOf("PUT"))
                .map(p -> p.dist().get(p.i()).get(p.j())).toList();
        assertEquals(List.of(127, 108), mejoras);
        // Una casilla nunca empeora.
        for (int p = 1; p < pasos.size(); p++) {
            for (int i = 0; i < 7; i++) {
                for (int j = 0; j < 7; j++) {
                    assertTrue(pasos.get(p).dist().get(i).get(j) <= pasos.get(p - 1).dist().get(i).get(j));
                }
            }
        }
    }

    @Test
    void elCasoEvaluaTodasLasUbicaciones() {
        UbicacionAlmacen u = new UbicacionAlmacen(RedVial.cargar());
        assertEquals(14, u.ranking(1, Criterio.MAS_LEJANO).size());
        assertEquals(91, u.ranking(2, Criterio.TOTAL).size());
        Propuesta centro = u.ranking(1, Criterio.MAS_LEJANO).get(0);
        Propuesta mediana = u.ranking(1, Criterio.TOTAL).get(0);
        assertEquals(List.of("PUN"), centro.almacenes());
        assertEquals(166, centro.peor());
        assertEquals(List.of("JUL"), mediana.almacenes());
        assertEquals(1354, mediana.total());
        Propuesta dos = u.ranking(2, Criterio.MAS_LEJANO).get(0);
        assertEquals(List.of("JUL", "JLI"), dos.almacenes());
        assertEquals(109, dos.peor());
        // Cada pueblo queda asignado a su almacén más cercano.
        FloydWarshall.Resultado t = u.tabla();
        u.asignacion(dos.almacenes()).forEach((v, a) -> {
            for (String otro : dos.almacenes()) {
                assertTrue(t.distancia(a, v) <= t.distancia(otro, v));
            }
        });
    }

    private static void verificarContraDijkstra(GrafoPonderado g) {
        FloydWarshall.Resultado r = FloydWarshall.calcular(g);
        for (String s : g.vertices()) {
            Map<String, Integer> d = g.dijkstra(s).dist();
            for (String t : g.vertices()) {
                int esperado = d.getOrDefault(t, FloydWarshall.INF);
                assertEquals(esperado, r.distancia(s, t), s + " → " + t);
                List<String> c = r.camino(s, t);
                if (esperado == FloydWarshall.INF) {
                    assertTrue(c.isEmpty());
                } else {
                    int suma = 0;
                    for (int k = 1; k < c.size(); k++) {
                        suma += g.km(c.get(k - 1), c.get(k));
                    }
                    assertEquals(esperado, suma, "el camino suma la distancia");
                }
            }
        }
    }
}
