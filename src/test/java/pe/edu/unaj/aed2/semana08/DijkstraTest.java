package pe.edu.unaj.aed2.semana08;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana08.caso.PlanificadorRutas;
import pe.edu.unaj.aed2.semana08.modelo.EstadoDijkstra;
import pe.edu.unaj.aed2.semana08.modelo.GrafoPonderado;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DijkstraTest {

    private static final RedVial RED = RedVial.cargar();
    private static final GrafoPonderado G = GrafoPonderado.desde(RED);

    private static long[][] floyd(GrafoPonderado g, List<String> vs) {
        int n = vs.size();
        long inf = Long.MAX_VALUE / 4;
        long[][] d = new long[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d[i][j] = i == j ? 0 : inf;
            }
            for (GrafoPonderado.Arista a : g.vecinos(vs.get(i))) {
                d[i][vs.indexOf(a.destino())] = Math.min(d[i][vs.indexOf(a.destino())], a.km());
            }
        }
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    d[i][j] = Math.min(d[i][j], d[i][k] + d[k][j]);
                }
            }
        }
        return d;
    }

    private static void verificarContraFloyd(GrafoPonderado g) {
        List<String> vs = new ArrayList<>(g.vertices());
        long[][] d = floyd(g, vs);
        for (String s : vs) {
            GrafoPonderado.Resultado r = g.dijkstra(s);
            for (String t : vs) {
                long esperado = d[vs.indexOf(s)][vs.indexOf(t)];
                if (esperado >= Long.MAX_VALUE / 4) {
                    assertTrue(!r.dist().containsKey(t) && r.camino(t).isEmpty());
                } else {
                    assertEquals(esperado, (long) r.dist().get(t), s + " → " + t);
                    List<String> c = r.camino(t);
                    int suma = 0;
                    for (int k = 1; k < c.size(); k++) {
                        suma += g.km(c.get(k - 1), c.get(k));
                    }
                    assertEquals(esperado, suma, "el camino reconstruido suma la distancia");
                }
            }
        }
    }

    @Test
    void enLaRedVialCoincideConFloydWarshall() {
        verificarContraFloyd(G);
        assertEquals(49, G.km("JUL", "PUN"));
        assertEquals(List.of("JUL", "HUA", "PUT"), G.dijkstra("JUL").camino("PUT"));
    }

    @Test
    void enGrafosAleatoriosCoincideConFloydWarshall() {
        Random r = new Random(21);
        for (int prueba = 0; prueba < 30; prueba++) {
            GrafoPonderado g = new GrafoPonderado();
            int n = 3 + r.nextInt(12);
            for (int k = 0; k < n; k++) {
                g.agregarVertice("V" + k);
            }
            for (int k = 0; k < n * 2; k++) {
                int a = r.nextInt(n);
                int b = r.nextInt(n);
                if (a != b) {
                    g.agregarArista("V" + a, "V" + b, r.nextInt(30));
                }
            }
            verificarContraFloyd(g);
        }
        assertThrows(IllegalArgumentException.class, () -> new GrafoPonderado().agregarArista("A", "B", -1));
    }

    @Test
    void losPasosTerminanConLasMismasDistanciasYCierranEnOrdenCreciente() {
        for (String s : G.vertices()) {
            List<EstadoDijkstra> pasos = G.pasosDijkstra(s, Map.of());
            EstadoDijkstra fin = pasos.get(pasos.size() - 1);
            assertEquals(G.dijkstra(s).dist(), fin.distancias());
            assertEquals(G.dijkstra(s).padre(), fin.padres());
            int anterior = -1;
            for (EstadoDijkstra p : pasos) {
                if (p.tipo() == EstadoDijkstra.Tipo.EXTRAE) {
                    int d = p.distancias().get(p.actual());
                    assertTrue(d >= anterior, "Dijkstra cierra los vértices de menor a mayor distancia");
                    anterior = d;
                }
            }
        }
    }

    @Test
    void lasEntradasViejasSeDescartan() {
        // A–B directo cuesta 10, pero A–C–B cuesta 3: (10, B) entra primero y luego queda vieja.
        GrafoPonderado g = new GrafoPonderado();
        g.agregarArista("A", "B", 10);
        g.agregarArista("A", "C", 1);
        g.agregarArista("C", "B", 2);
        List<EstadoDijkstra> pasos = g.pasosDijkstra("A", Map.of());
        assertTrue(pasos.stream().anyMatch(p -> p.tipo() == EstadoDijkstra.Tipo.DESCARTA));
        assertEquals(3, g.dijkstra("A").dist().get("B"));
    }

    @Test
    void planificadorComparaDijkstraConBfs() {
        PlanificadorRutas plan = new PlanificadorRutas(RED);
        PlanificadorRutas.Ruta corta = plan.masCorta("JUL", "PUT");
        PlanificadorRutas.Ruta pocos = plan.menosTramos("JUL", "PUT");
        assertEquals(2, corta.tramos());
        assertEquals(2, pocos.tramos());
        assertEquals(108, corta.km());
        assertEquals(127, pocos.km());
        List<PlanificadorRutas.Destino> tabla = plan.tabla("JUL");
        assertEquals(13, tabla.size());
        for (PlanificadorRutas.Destino d : tabla) {
            assertTrue(d.kmDeMas() >= 0, "BFS nunca puede ganarle a Dijkstra en km");
        }
        for (int k = 1; k < tabla.size(); k++) {
            assertTrue(tabla.get(k - 1).masCorta().km() <= tabla.get(k).masCorta().km());
        }
    }
}
