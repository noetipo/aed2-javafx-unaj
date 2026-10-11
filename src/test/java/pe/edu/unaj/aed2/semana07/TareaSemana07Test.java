package pe.edu.unaj.aed2.semana07;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana07.tarea.TareaSemana07;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la tarea. No se ejecutan con "./mvnw test"; para correrlas:
 * ./mvnw test -Dgroups=tarea -DexcludedGroups=none
 */
@Tag("tarea")
class TareaSemana07Test {

    /** Construye una lista de adyacencia no dirigida a partir de "A-B" y vértices sueltos "C". */
    private static Map<String, List<String>> grafo(String... partes) {
        Map<String, List<String>> ady = new LinkedHashMap<>();
        for (String p : partes) {
            String[] uv = p.split("-");
            ady.computeIfAbsent(uv[0], k -> new ArrayList<>());
            if (uv.length == 2) {
                ady.computeIfAbsent(uv[1], k -> new ArrayList<>());
                ady.get(uv[0]).add(uv[1]);
                ady.get(uv[1]).add(uv[0]);
            }
        }
        return ady;
    }

    @Test
    void contarComponentes() {
        assertEquals(0, TareaSemana07.contarComponentes(grafo()));
        assertEquals(1, TareaSemana07.contarComponentes(grafo("A")));
        assertEquals(1, TareaSemana07.contarComponentes(grafo("A-B", "B-C", "C-D")));
        assertEquals(3, TareaSemana07.contarComponentes(grafo("A-B", "C-D", "E")));
        assertEquals(2, TareaSemana07.contarComponentes(grafo("A-B", "B-C", "C-A", "X-Y")));
    }

    @Test
    void caminoMinimo() {
        // A - B - C - D  y un atajo A - E - D: el camino mínimo de A a D tiene 2 aristas.
        Map<String, List<String>> g = grafo("A-B", "B-C", "C-D", "A-E", "E-D", "F");
        assertEquals(List.of("A", "E", "D"), TareaSemana07.caminoMinimo(g, "A", "D"));
        assertEquals(List.of("A"), TareaSemana07.caminoMinimo(g, "A", "A"));
        assertEquals(List.of("C", "B"), TareaSemana07.caminoMinimo(g, "C", "B"));
        assertTrue(TareaSemana07.caminoMinimo(g, "A", "F").isEmpty());
        List<String> largo = TareaSemana07.caminoMinimo(grafo("1-2", "2-3", "3-4", "4-5"), "1", "5");
        assertEquals(List.of("1", "2", "3", "4", "5"), largo);
    }

    @Test
    void tieneCiclo() {
        assertFalse(TareaSemana07.tieneCiclo(grafo()));
        assertFalse(TareaSemana07.tieneCiclo(grafo("A-B", "B-C", "B-D")));          // árbol
        assertTrue(TareaSemana07.tieneCiclo(grafo("A-B", "B-C", "C-A")));           // triángulo
        assertFalse(TareaSemana07.tieneCiclo(grafo("A-B", "C-D", "E")));            // bosque
        assertTrue(TareaSemana07.tieneCiclo(grafo("A-B", "X-Y", "Y-Z", "Z-W", "W-X"))); // ciclo en otra componente
    }
}
