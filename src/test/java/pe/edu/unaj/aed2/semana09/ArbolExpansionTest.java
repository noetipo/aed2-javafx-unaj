package pe.edu.unaj.aed2.semana09;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana08.modelo.GrafoPonderado;
import pe.edu.unaj.aed2.semana09.caso.ProyectoFibra;
import pe.edu.unaj.aed2.semana09.modelo.ArbolExpansion;
import pe.edu.unaj.aed2.semana09.modelo.ArbolExpansion.Tramo;
import pe.edu.unaj.aed2.semana09.modelo.EstadoArbolExpansion;
import pe.edu.unaj.aed2.semana09.modelo.EstadoArbolExpansion.Tipo;
import pe.edu.unaj.aed2.semana09.modelo.UnionFind;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArbolExpansionTest {

    private static final RedVial RED = ArbolExpansion.cargarTendidos();
    private static final GrafoPonderado GRAFO = GrafoPonderado.desde(RED);

    @Test
    void unionFindUneGruposYDetectaCiclos() {
        UnionFind<String> uf = new UnionFind<>();
        assertFalse(uf.mismoGrupo("a", "b"));
        assertTrue(uf.unir("a", "b"));
        assertTrue(uf.unir("c", "d"));
        assertTrue(uf.unir("b", "d"));
        assertTrue(uf.mismoGrupo("a", "c"));
        assertFalse(uf.unir("a", "c"), "a y c ya estaban juntos: unirlos cerraría un ciclo");
        assertEquals("e", uf.encontrar("e"));
    }

    @Test
    void losTendidosSonLosPueblosDeLaRedConMasAristas() {
        assertEquals(RedVial.cargar().grafo().vertices(), GRAFO.vertices());
        assertEquals(19, ArbolExpansion.tramos(GRAFO).size());
    }

    @Test
    void kruskalYPrimDanElMismoTotalDesdeCualquierPueblo() {
        List<Tramo> k = ArbolExpansion.kruskal(GRAFO);
        assertEquals(GRAFO.vertices().size() - 1, k.size());
        assertTrue(conectaATodos(GRAFO, k));
        int total = ArbolExpansion.total(k);
        assertEquals(541, total);
        for (String s : GRAFO.vertices()) {
            List<Tramo> p = ArbolExpansion.prim(GRAFO, s);
            assertEquals(total, ArbolExpansion.total(p), "Prim desde " + s);
            assertTrue(conectaATodos(GRAFO, p));
        }
    }

    @Test
    void coincidenConLaFuerzaBrutaEnGrafosAleatorios() {
        Random r = new Random(9);
        for (int prueba = 0; prueba < 60; prueba++) {
            int n = 2 + r.nextInt(5);
            GrafoPonderado g = aleatorio(r, n, 1 + r.nextInt(8));
            List<Tramo> todos = ArbolExpansion.tramos(g);
            int esperado = fuerzaBruta(g, todos);
            if (esperado < 0) {
                assertTrue(ArbolExpansion.kruskal(g).size() < n - 1, "no conexo: Kruskal deja un bosque");
                continue;
            }
            assertEquals(esperado, ArbolExpansion.total(ArbolExpansion.kruskal(g)));
            for (String s : g.vertices()) {
                assertEquals(esperado, ArbolExpansion.total(ArbolExpansion.prim(g, s)));
            }
        }
    }

    @Test
    void pasosDeKruskalSiguenAlAlgoritmo() {
        List<EstadoArbolExpansion> pasos = ArbolExpansion.pasosKruskal(GRAFO, RED.nombres());
        EstadoArbolExpansion fin = pasos.get(pasos.size() - 1);
        assertEquals(ArbolExpansion.kruskal(GRAFO).stream().map(Tramo::clave).toList(), fin.arbol());
        assertEquals(541, fin.total());
        assertEquals(5, pasos.stream().filter(p -> p.tipo() == Tipo.DESCARTA).count());
        assertEquals(1, pasos.stream().filter(p -> p.linea() == 7).count(), "un solo break");
        // Al terminar, todos los pueblos están en el mismo grupo.
        assertEquals(1, fin.grupos().values().stream().distinct().count());
        assertEquals(GRAFO.vertices().size(), fin.grupos().size());
        // Los km examinados nunca bajan: el orden es de menor a mayor.
        int anterior = 0;
        for (EstadoArbolExpansion p : pasos) {
            if (p.u() != null) {
                int km = GRAFO.km(p.u(), p.v());
                assertTrue(km >= anterior);
                anterior = km;
            }
        }
    }

    @Test
    void pasosDePrimSiguenAlAlgoritmo() {
        for (String s : GRAFO.vertices()) {
            List<EstadoArbolExpansion> pasos = ArbolExpansion.pasosPrim(GRAFO, s, Map.of());
            EstadoArbolExpansion fin = pasos.get(pasos.size() - 1);
            assertEquals(ArbolExpansion.prim(GRAFO, s).stream().map(Tramo::clave).toList(), fin.arbol());
            assertEquals(GRAFO.vertices(), fin.conectados());
            for (EstadoArbolExpansion p : pasos) {
                if (p.tipo() == Tipo.ACEPTA) {
                    // Lo que queda en la cola nunca es más barato que lo que se acaba de aceptar.
                    int km = GRAFO.km(p.u(), p.v());
                    assertTrue(p.pendientes().stream().allMatch(x -> Integer.parseInt(x.substring(1, x.indexOf(',')))
                            >= km));
                }
            }
        }
        // Juliaca: primero Lampa (38 km), el tramo más barato que sale de Juliaca.
        EstadoArbolExpansion primero = ArbolExpansion.pasosPrim(GRAFO, "JUL", Map.of()).stream()
                .filter(p -> p.tipo() == Tipo.ACEPTA).findFirst().orElseThrow();
        assertEquals("LAM", primero.v());
    }

    @Test
    void elCasoComparaExpansionMinimaConCaminosMinimos() {
        ProyectoFibra proyecto = new ProyectoFibra(RED);
        ProyectoFibra.Diseno expansion = proyecto.expansionMinima();
        for (String c : GRAFO.vertices()) {
            ProyectoFibra.Diseno caminos = proyecto.caminosMinimos(c);
            assertEquals(GRAFO.vertices().size() - 1, caminos.tramos().size());
            assertTrue(caminos.km() >= expansion.km(), "ningún árbol usa menos cable que el de expansión mínima");
            Map<String, Integer> dijkstra = GRAFO.dijkstra(c).dist();
            for (ProyectoFibra.Fila f : proyecto.tabla(c)) {
                assertEquals(dijkstra.get(f.id()), f.porCaminos());
                assertTrue(f.diferencia() >= 0, "por el árbol de caminos mínimos la señal nunca llega más lejos");
            }
        }
        assertTrue(proyecto.caminosMinimos("JUL").km() > expansion.km());
    }

    // ------------------------------------------------------------------

    private static GrafoPonderado aleatorio(Random r, int n, int aristas) {
        GrafoPonderado g = new GrafoPonderado();
        for (int i = 0; i < n; i++) {
            g.agregarVertice("v" + i);
        }
        for (int k = 0; k < aristas; k++) {
            int a = r.nextInt(n);
            int b = r.nextInt(n);
            if (a != b) {
                g.agregarArista("v" + a, "v" + b, 1 + r.nextInt(9));
            }
        }
        return g;
    }

    private static boolean conectaATodos(GrafoPonderado g, List<Tramo> tramos) {
        UnionFind<String> uf = new UnionFind<>();
        tramos.forEach(t -> uf.unir(t.u(), t.v()));
        String r = uf.encontrar(g.vertices().iterator().next());
        return g.vertices().stream().allMatch(v -> uf.encontrar(v).equals(r));
    }

    /** Prueba todos los subconjuntos de n − 1 tramos; -1 si ninguno conecta a todos. */
    private static int fuerzaBruta(GrafoPonderado g, List<Tramo> todos) {
        int n = g.vertices().size();
        int mejor = -1;
        for (int mascara = 0; mascara < (1 << todos.size()); mascara++) {
            if (Integer.bitCount(mascara) != n - 1) {
                continue;
            }
            List<Tramo> elegidos = new java.util.ArrayList<>();
            for (int k = 0; k < todos.size(); k++) {
                if ((mascara & (1 << k)) != 0) {
                    elegidos.add(todos.get(k));
                }
            }
            if (conectaATodos(g, elegidos)) {
                int total = ArbolExpansion.total(elegidos);
                if (mejor < 0 || total < mejor) {
                    mejor = total;
                }
            }
        }
        return mejor;
    }
}
