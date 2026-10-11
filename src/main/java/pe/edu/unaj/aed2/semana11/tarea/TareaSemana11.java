package pe.edu.unaj.aed2.semana11.tarea;

/**
 * TAREA SEMANA 11 — Todos los pares y programación dinámica sobre matrices.
 * <p>
 * Los vértices son 0..n-1. En una matriz de pesos {@code w}, w[i][j] es el peso del arco i → j, o -1 si no hay
 * arco (w[i][i] = 0). Para un grafo no dirigido, w es simétrica.
 * <ul>
 *   <li>No modifiques las matrices que recibes.</li>
 *   <li>No cambies las firmas: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-11/tarea.md
 */
public final class TareaSemana11 {

    /** Valor para "inalcanzable". */
    public static final int INFINITO = Integer.MAX_VALUE;

    private TareaSemana11() {
    }

    /** Distancias mínimas entre todos los pares con Floyd–Warshall; INFINITO si no hay camino. */
    public static int[][] floydWarshall(int[][] w) {
        // TODO 1: copia w cambiando -1 por INFINITO y aplica los tres for (k afuera). Pregunta por INFINITO antes de
        //         sumar: INFINITO + algo desborda el int.
        throw new UnsupportedOperationException("TODO: implementar floydWarshall");
    }

    /**
     * Clausura transitiva (algoritmo de Warshall): alcanza[i][j] es true si hay un camino de UNO o más arcos de i a j.
     * Por ejemplo, si arco son los prerrequisitos de la semana 10, dice si un curso es prerrequisito directo o
     * indirecto de otro. alcanza[i][i] solo es true si i está en un ciclo.
     */
    public static boolean[][] clausuraTransitiva(boolean[][] arco) {
        // TODO 2: la misma idea que Floyd–Warshall, con booleanos: alcanza[i][j] = alcanza[i][j] ||
        //         (alcanza[i][k] && alcanza[k][j]).
        throw new UnsupportedOperationException("TODO: implementar clausuraTransitiva");
    }

    /**
     * El CENTRO del grafo: el vértice cuyo vértice más lejano está más cerca (menor excentricidad). Si hay empate,
     * el de menor índice. -1 si ningún vértice alcanza a todos.
     */
    public static int centro(int[][] w) {
        // TODO 3: con la matriz de floydWarshall, la excentricidad de i es el máximo de su fila.
        throw new UnsupportedOperationException("TODO: implementar centro");
    }
}
