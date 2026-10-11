package pe.edu.unaj.aed2.semana07;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana07.caso.BloqueoVial;
import pe.edu.unaj.aed2.semana07.modelo.EstadoRecorrido;
import pe.edu.unaj.aed2.semana07.modelo.Grafo;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GrafoTest {

    private static final RedVial RED = RedVial.cargar();

    /** Distancias mínimas en aristas por fuerza bruta (Floyd–Warshall con pesos 1), para comparar con BFS. */
    private static int[][] floyd(Grafo g, List<String> vs) {
        int n = vs.size();
        int inf = 1_000_000;
        int[][] d = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d[i][j] = i == j ? 0 : inf;
            }
            for (String w : g.vecinos(vs.get(i))) {
                d[i][vs.indexOf(w)] = 1;
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

    @Test
    void redVialCargada() {
        assertEquals(14, RED.grafo().vertices().size());
        assertEquals(15, RED.grafo().aristas().size());
        assertEquals(List.of("PUN", "LAM", "PUC", "AZA", "HUA"), RED.grafo().vecinos("JUL"));
        assertTrue(RED.distanciaKm("JUL", "PUN") > 30 && RED.distanciaKm("JUL", "PUN") < 50);
    }

    @Test
    void bfsDaLasDistanciasMinimasEnTramos() {
        Grafo g = RED.grafo();
        List<String> vs = new ArrayList<>(g.vertices());
        int[][] d = floyd(g, vs);
        for (String s : vs) {
            Map<String, Integer> dist = g.bfs(s);
            assertEquals(vs.size(), dist.size());
            for (String t : vs) {
                assertEquals(d[vs.indexOf(s)][vs.indexOf(t)], dist.get(t), s + " → " + t);
                assertEquals(dist.get(t) + 1, g.caminoMinimo(s, t).size());
            }
        }
        assertEquals(List.of("JUL", "PUN", "LAM", "PUC", "AZA", "HUA", "CHU", "AYA", "PUT", "MOH", "ILA", "JLI",
                "YUN", "DES"), new ArrayList<>(g.bfs("JUL").keySet()));
        assertEquals(List.of("JUL", "PUN", "CHU", "ILA", "JLI", "YUN", "DES", "LAM", "PUC", "AYA", "AZA", "PUT",
                "HUA", "MOH"), g.dfs("JUL"));
    }

    @Test
    void losPasosTerminanConElMismoRecorridoYUnArbolDeVMenos1Aristas() {
        Grafo g = RED.grafo();
        for (String s : g.vertices()) {
            List<EstadoRecorrido> bfs = g.pasosBfs(s, Map.of());
            EstadoRecorrido fin = bfs.get(bfs.size() - 1);
            assertEquals(new ArrayList<>(g.bfs(s).keySet()), fin.orden());
            assertEquals(g.bfs(s), fin.distancias());
            assertEquals(fin.orden().size() - 1, fin.arbol().size());
            for (EstadoRecorrido p : bfs) {
                for (String enCola : p.frontera()) {
                    assertEquals(EstadoRecorrido.Estado.EN_FRONTERA, p.estados().get(enCola));
                }
            }
            List<EstadoRecorrido> dfs = g.pasosDfs(s, Map.of());
            EstadoRecorrido finDfs = dfs.get(dfs.size() - 1);
            assertEquals(g.dfs(s), finDfs.orden());
            assertEquals(finDfs.orden().size() - 1, finDfs.arbol().size());
            assertTrue(finDfs.estados().values().stream().allMatch(e -> e == EstadoRecorrido.Estado.TERMINADO));
        }
    }

    @Test
    void grafoAleatorioDesconectado() {
        Random r = new Random(12);
        Grafo g = new Grafo();
        for (int k = 0; k < 30; k++) {
            g.agregarVertice("V" + k);
        }
        for (int k = 0; k < 25; k++) {
            int a = r.nextInt(30);
            int b = r.nextInt(30);
            if (a != b) {
                g.agregarArista("V" + a, "V" + b);
            }
        }
        List<String> vs = new ArrayList<>(g.vertices());
        int[][] d = floyd(g, vs);
        for (String s : vs) {
            Map<String, Integer> dist = g.bfs(s);
            for (String t : vs) {
                int esperado = d[vs.indexOf(s)][vs.indexOf(t)];
                if (esperado >= 1_000_000) {
                    assertTrue(!dist.containsKey(t) && g.caminoMinimo(s, t).isEmpty());
                } else {
                    assertEquals(esperado, dist.get(t));
                }
            }
            assertEquals(dist.keySet(), Set.copyOf(g.dfs(s)), "BFS y DFS alcanzan los mismos vértices");
        }
    }

    @Test
    void bloqueoVial() {
        BloqueoVial b = new BloqueoVial(RED);
        assertTrue(b.aislados("JUL", Set.of()).isEmpty());
        assertEquals(List.of("PUN", "CHU", "ILA", "JLI", "YUN", "DES"), b.aislados("JUL", Set.of("JUL-PUN")));
        // Con el tramo Juliaca–Azángaro bloqueado se sigue llegando por Huancané y Putina: es un ciclo.
        assertTrue(b.aislados("JUL", Set.of("AZA-JUL")).isEmpty());
        assertEquals(List.of("JUL", "HUA", "PUT", "AZA"), b.ruta("JUL", "AZA", Set.of("AZA-JUL")));
        assertEquals(List.of("AYA-PUC", "CHU-PUN", "HUA-MOH", "ILA-JLI", "CHU-ILA", "JLI-YUN", "DES-JLI",
                "JUL-PUN").stream().sorted().toList(), b.tramosCriticos().stream().sorted().toList());
    }
}
