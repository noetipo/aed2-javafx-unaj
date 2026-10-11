package pe.edu.unaj.aed2.semana12.tarea;

/**
 * TAREA SEMANA 12 — Flujo máximo con matrices.
 * <p>
 * Los vértices son 0..n-1 y cap[u][v] es la capacidad del arco u → v (0 si no hay). Puede haber arcos en ambos
 * sentidos entre dos vértices.
 * <ul>
 *   <li>No modifiques las matrices que recibes.</li>
 *   <li>No cambies las firmas: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-12/tarea.md
 */
public final class TareaSemana12 {

    private TareaSemana12() {
    }

    /** Valor del flujo máximo de s a t (0 si s == t no se pide: s y t siempre son distintos). */
    public static int flujoMaximo(int[][] cap, int s, int t) {
        // TODO 1: Edmonds–Karp. Guarda el flujo NETO en una matriz f con f[v][u] = -f[u][v]; el residual de u → v es
        //         cap[u][v] - f[u][v]. Así funciona aunque haya arcos en ambos sentidos.
        throw new UnsupportedOperationException("TODO: implementar flujoMaximo");
    }

    /**
     * Un corte mínimo: ladoS[v] es true si v queda del lado de s (alcanzable desde s en la red residual al terminar).
     * La suma de cap[u][v] con u en el lado de s y v fuera debe ser igual al flujo máximo.
     */
    public static boolean[] corteMinimo(int[][] cap, int s, int t) {
        // TODO 2: después de calcular el flujo máximo, un BFS más desde s por arcos con residual > 0.
        throw new UnsupportedOperationException("TODO: implementar corteMinimo");
    }

    /**
     * Emparejamiento bipartito máximo: puede[i][j] es true si el conductor i puede cubrir la ruta j. Cada conductor
     * cubre a lo más una ruta y cada ruta tiene a lo más un conductor. Retorna cuántas rutas se pueden cubrir.
     */
    public static int emparejamientoMaximo(boolean[][] puede) {
        // TODO 3: arma una red: fuente → cada conductor (cap 1), conductor i → ruta j si puede (cap 1),
        //         cada ruta → destino (cap 1). El flujo máximo es la respuesta.
        throw new UnsupportedOperationException("TODO: implementar emparejamientoMaximo");
    }
}
