package pe.edu.unaj.aed2.semana09.tarea;

/**
 * TAREA SEMANA 09 — Árboles de expansión mínima con arreglos.
 * <p>
 * Los vértices son 0..n-1. Una lista de tramos es un {@code int[][]} donde cada fila es {u, v, km}.
 * Una matriz {@code w} usa w[i][j] = km de la arista i–j, o -1 si no hay arista (w[i][i] = 0).
 * <ul>
 *   <li>Puedes usar {@code semana09.modelo.UnionFind} o escribir tu propio union-find con un arreglo.</li>
 *   <li>No cambies las firmas: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-09/tarea.md
 */
public final class TareaSemana09 {

    private TareaSemana09() {
    }

    /** Km totales del árbol de expansión mínima con Prim O(V²) sobre la matriz; -1 si el grafo no es conexo. */
    public static int primMatriz(int[][] w) {
        // TODO 1: arreglos enArbol y costo (costo[j] = km del tramo más barato que une j con el árbol; empieza
        //         "infinito" salvo costo[0] = 0). V veces: elige el j fuera del árbol con menor costo (si es
        //         infinito, el grafo no es conexo), agrégalo, suma su costo y actualiza costo[] con sus aristas.
        //         Es Dijkstra con UNA diferencia: compara solo w[j][x], no dist[j] + w[j][x].
        throw new UnsupportedOperationException("TODO: implementar primMatriz");
    }

    /**
     * Km totales del árbol de expansión más barato que INCLUYE el tramo {@code tramos[obligatorio]} (por ejemplo,
     * un cable que ya existe y se quiere aprovechar); -1 si el grafo no es conexo.
     */
    public static int costoConTramoObligatorio(int n, int[][] tramos, int obligatorio) {
        // TODO 2: Kruskal, pero antes de recorrer los tramos ordenados une los extremos del obligatorio y cuenta su km.
        //         No ordenes el arreglo original: haz una copia (el índice obligatorio se refiere al original).
        throw new UnsupportedOperationException("TODO: implementar costoConTramoObligatorio");
    }

    /**
     * Agrupa los n pueblos en k zonas (2 &lt;= k &lt;= n) uniendo primero los más cercanos, como Kruskal, y se detiene
     * cuando quedan k grupos. Retorna el ESPACIAMIENTO: el km del tramo más corto entre dos zonas distintas
     * (cuanto mayor, mejor separadas están). -1 si nunca quedan exactamente k grupos o no hay tramo entre zonas.
     */
    public static int espaciamiento(int n, int[][] tramos, int k) {
        // TODO 3: cuenta los grupos (empiezan en n y baja 1 con cada unión). Recorre los tramos ordenados: si los
        //         extremos están en grupos distintos y ya hay k grupos, ese km es la respuesta; si no, únelos.
        throw new UnsupportedOperationException("TODO: implementar espaciamiento");
    }
}
