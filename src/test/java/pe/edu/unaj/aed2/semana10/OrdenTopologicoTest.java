package pe.edu.unaj.aed2.semana10;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana10.caso.PlanEstudios;
import pe.edu.unaj.aed2.semana10.caso.PlanEstudios.Prioridad;
import pe.edu.unaj.aed2.semana10.modelo.EstadoTopologico;
import pe.edu.unaj.aed2.semana10.modelo.EstadoTopologico.Tipo;
import pe.edu.unaj.aed2.semana10.modelo.GrafoDirigido;
import pe.edu.unaj.aed2.semana10.modelo.MallaCurricular;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrdenTopologicoTest {

    private static final MallaCurricular MALLA = MallaCurricular.cargar();
    private static final GrafoDirigido G = MALLA.grafo();

    @Test
    void laMallaNoTieneCiclosYAmbosOrdenesSonValidos() {
        assertEquals(16, G.vertices().size());
        assertEquals(18, G.arcos().size());
        assertFalse(G.tieneCiclo());
        assertEquals(List.of("MAT", "PRO", "COM", "CAL", "DIS", "POO", "MET", "EST", "AED", "BDA", "AE2", "SOP",
                "ISW", "INA", "RED", "TES"), G.kahn());
        assertTrue(esOrdenValido(G, G.kahn()));
        assertTrue(esOrdenValido(G, G.ordenDfs()));
        assertEquals(6, G.caminoCritico().size());
        assertEquals(5, G.niveles().get("TES"));
    }

    @Test
    void elPrerrequisitoMalDigitadoCreaUnCiclo() {
        GrafoDirigido conError = MALLA.grafoConError();
        assertEquals(List.of("AED", "AE2"), conError.encontrarCiclo());
        assertThrows(IllegalStateException.class, conError::kahn);
        assertThrows(IllegalStateException.class, conError::ordenDfs);
    }

    @Test
    void coincidenEnGrafosAleatorios() {
        Random r = new Random(10);
        for (int prueba = 0; prueba < 200; prueba++) {
            int n = 1 + r.nextInt(8);
            GrafoDirigido g = new GrafoDirigido();
            for (int i = 0; i < n; i++) {
                g.agregarVertice("v" + i);
            }
            int m = r.nextInt(2 * n);
            for (int k = 0; k < m; k++) {
                g.agregarArco("v" + r.nextInt(n), "v" + r.nextInt(n));
            }
            boolean ciclo = g.tieneCiclo();
            assertEquals(ciclo, lanza(g::kahn), "Kahn y DFS deben coincidir en si hay ciclo");
            assertEquals(ciclo, lanza(g::ordenDfs));
            if (ciclo) {
                List<String> c = g.encontrarCiclo();
                for (int i = 0; i < c.size(); i++) {
                    assertTrue(g.sucesores(c.get(i)).contains(c.get((i + 1) % c.size())), "ciclo real: " + c);
                }
            } else {
                assertTrue(esOrdenValido(g, g.kahn()));
                assertTrue(esOrdenValido(g, g.ordenDfs()));
            }
        }
    }

    @Test
    void pasosDeKahnYDfsLleganAlMismoResultado() {
        List<EstadoTopologico> kahn = G.pasosKahn(Map.of());
        assertEquals(G.kahn(), kahn.get(kahn.size() - 1).orden());
        List<EstadoTopologico> dfs = G.pasosDfs(Map.of());
        assertEquals(G.ordenDfs(), dfs.get(dfs.size() - 1).orden());
        assertEquals(16, dfs.stream().filter(p -> p.tipo() == Tipo.TERMINA).count());
        // En DFS, la pila de llamadas solo tiene vértices GRISES.
        for (EstadoTopologico p : dfs) {
            for (String v : p.frontera()) {
                assertEquals(pe.edu.unaj.aed2.semana07.modelo.EstadoRecorrido.Estado.EN_FRONTERA, p.estados().get(v));
            }
        }
        // Con el error, los dos terminan en un paso de ciclo que marca el arco AED II → AED I.
        for (List<EstadoTopologico> pasos : List.of(MALLA.grafoConError().pasosKahn(Map.of()),
                MALLA.grafoConError().pasosDfs(Map.of()))) {
            EstadoTopologico fin = pasos.get(pasos.size() - 1);
            assertEquals(Tipo.CICLO, fin.tipo());
            assertEquals("AE2>AED", fin.arcoCiclo());
        }
    }

    @Test
    void elPlanRespetaPrerrequisitosYElTope() {
        PlanEstudios plan = new PlanEstudios(MALLA);
        assertEquals(6, plan.minimoCiclos());
        for (int k = 1; k <= 6; k++) {
            for (Prioridad p : Prioridad.values()) {
                List<List<String>> ciclos = plan.plan(k, p);
                Set<String> aprobados = new HashSet<>();
                for (List<String> ciclo : ciclos) {
                    assertTrue(ciclo.size() <= k);
                    for (String v : ciclo) {
                        assertTrue(aprobados.containsAll(G.predecesores(v)), v + " sin sus prerrequisitos");
                    }
                    aprobados.addAll(ciclo);
                }
                assertEquals(16, aprobados.size());
                assertTrue(ciclos.size() >= Math.max(6, (16 + k - 1) / k));
            }
        }
        // Con 3 por ciclo, elegir por orden alfabético deja POO para después y retrasa la cadena crítica.
        assertEquals(7, plan.plan(3, Prioridad.ALFABETICO).size());
        assertEquals(6, plan.plan(3, Prioridad.CADENA_MAS_LARGA).size());
        assertEquals(0, plan.holgura("POO"));
        assertEquals(3, plan.holgura("COM"));
    }

    private static boolean lanza(Runnable r) {
        try {
            r.run();
            return false;
        } catch (IllegalStateException e) {
            return true;
        }
    }

    private static boolean esOrdenValido(GrafoDirigido g, List<String> orden) {
        if (orden.size() != g.vertices().size() || !new HashSet<>(orden).equals(g.vertices())) {
            return false;
        }
        for (String[] a : g.arcos()) {
            if (orden.indexOf(a[0]) >= orden.indexOf(a[1])) {
                return false;
            }
        }
        return true;
    }
}
