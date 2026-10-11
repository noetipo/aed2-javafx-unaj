package pe.edu.unaj.aed2.semana07.tarea;

import java.util.List;
import java.util.Map;

/**
 * TAREA SEMANA 07 — problemas clásicos que se resuelven con BFS y DFS. Completa los TODO.
 * <p>
 * El grafo llega como lista de adyacencia de un grafo NO dirigido: si v aparece en ady.get(u), u aparece en
 * ady.get(v). Todos los vértices son claves del mapa (aunque no tengan vecinos).
 * <ul>
 *   <li>Puedes usar ArrayDeque, HashSet, HashMap y ArrayList.</li>
 *   <li>No cambies las firmas: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-07/tarea.md
 */
public final class TareaSemana07 {

    private TareaSemana07() {
    }

    /** Cantidad de componentes conexas: grupos de vértices conectados entre sí. */
    public static int contarComponentes(Map<String, List<String>> ady) {
        // TODO 1: recorre los vértices; cada vez que encuentres uno sin visitar, hay una componente
        //         nueva: lánzale un BFS o DFS que marque toda su componente.
        throw new UnsupportedOperationException("TODO: implementar contarComponentes");
    }

    /**
     * Camino con la MENOR cantidad de aristas de s a t, incluyendo ambos extremos ([s] si s = t).
     * Lista vacía si no hay camino.
     */
    public static List<String> caminoMinimo(Map<String, List<String>> ady, String s, String t) {
        // TODO 2: BFS desde s guardando padre[w] = v al descubrir w; luego reconstruye de t hacia s.
        throw new UnsupportedOperationException("TODO: implementar caminoMinimo");
    }

    /** true si el grafo (no dirigido) tiene al menos un ciclo. */
    public static boolean tieneCiclo(Map<String, List<String>> ady) {
        // TODO 3: DFS recordando desde qué vértice llegaste (el padre). Si encuentras un vecino ya visitado
        //         que NO es tu padre, hay un ciclo. Ojo: el grafo puede tener varias componentes.
        throw new UnsupportedOperationException("TODO: implementar tieneCiclo");
    }
}
