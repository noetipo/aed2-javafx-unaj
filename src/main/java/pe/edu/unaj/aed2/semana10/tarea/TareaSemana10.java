package pe.edu.unaj.aed2.semana10.tarea;

/**
 * TAREA SEMANA 10 — Grafos dirigidos con arreglos.
 * <p>
 * Los vértices son 0..n-1. {@code arcos} es un {@code int[][]} donde cada fila {u, v} es el arco u → v
 * ("u es prerrequisito de v"). Puede haber vértices sin arcos.
 * <ul>
 *   <li>Arma tu propia lista de adyacencia (por ejemplo, {@code List<List<Integer>>}).</li>
 *   <li>No cambies las firmas: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-10/tarea.md
 */
public final class TareaSemana10 {

    private TareaSemana10() {
    }

    /** Un orden topológico de los n vértices (cualquiera válido); arreglo vacío si hay un ciclo. */
    public static int[] ordenTopologico(int n, int[][] arcos) {
        // TODO 1: Kahn con un arreglo grado[]. Cola con los de grado 0; al sacar v, baja el grado de sus sucesores
        //         y encola los que llegan a 0. Si al final salieron menos de n, hay un ciclo.
        throw new UnsupportedOperationException("TODO: implementar ordenTopologico");
    }

    /** true si el grafo dirigido tiene algún ciclo (incluido un arco v → v). */
    public static boolean tieneCiclo(int n, int[][] arcos) {
        // TODO 2: DFS con tres colores (0 blanco, 1 gris, 2 negro). Un arco hacia un vértice GRIS es un arco de
        //         regreso: hay ciclo. Ojo: llegar a un vértice NEGRO NO es un ciclo (piensa en un rombo a→b, a→c,
        //         b→d, c→d). No uses tu ordenTopologico: el objetivo es practicar DFS.
        throw new UnsupportedOperationException("TODO: implementar tieneCiclo");
    }

    /**
     * Mínimo de ciclos académicos para llevar los n cursos si en cada ciclo se pueden llevar todos los disponibles:
     * cuántos vértices tiene el camino más largo. 0 si n = 0; -1 si hay un ciclo.
     */
    public static int minimoCiclos(int n, int[][] arcos) {
        // TODO 3: recorre un orden topológico y calcula nivel[w] = max(nivel[w], nivel[v] + 1) por cada arco v → w,
        //         empezando con nivel 1 para todos. La respuesta es el nivel máximo.
        throw new UnsupportedOperationException("TODO: implementar minimoCiclos");
    }
}
