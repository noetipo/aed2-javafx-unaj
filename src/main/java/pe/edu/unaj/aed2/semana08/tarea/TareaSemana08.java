package pe.edu.unaj.aed2.semana08.tarea;

import java.util.List;

/**
 * TAREA SEMANA 08 — Dijkstra "clásico" sobre una MATRIZ de adyacencia, sin cola de prioridad.
 * <p>
 * {@code w[i][j]} es el peso de la arista i–j, o -1 si no hay arista (w[i][i] = 0). En cada vuelta se elige,
 * revisando todo el arreglo, el vértice NO cerrado con menor distancia: O(V) por vuelta, O(V²) en total.
 * Conviene en grafos densos; en dispersos gana la versión con montículo del visualizador.
 * <ul>
 *   <li>No uses PriorityQueue: el objetivo es la versión con arreglos.</li>
 *   <li>No cambies las firmas: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-08/tarea.md
 */
public final class TareaSemana08 {

    /** Valor para "inalcanzable". */
    public static final int INFINITO = Integer.MAX_VALUE;

    private TareaSemana08() {
    }

    /** Distancia mínima de s a cada vértice; INFINITO si no se puede llegar. */
    public static int[] dijkstraMatriz(int[][] w, int s) {
        // TODO 1: arreglos dist (INFINITO salvo dist[s] = 0) y cerrado. V veces: elige el no cerrado de
        //         menor dist (si es INFINITO, termina), ciérralo y relaja sus aristas (w[v][j] >= 0).
        //         Cuidado: INFINITO + peso desborda el int.
        throw new UnsupportedOperationException("TODO: implementar dijkstraMatriz");
    }

    /** Vértices del camino más corto de s a t, incluidos ambos; lista vacía si no hay camino. */
    public static List<Integer> caminoMatriz(int[][] w, int s, int t) {
        // TODO 2: repite la idea de dijkstraMatriz guardando padre[j] = v cada vez que relajas, y reconstruye.
        throw new UnsupportedOperationException("TODO: implementar caminoMatriz");
    }

    /** Km mínimos de s a t pasando OBLIGATORIAMENTE por {@code parada}; -1 si no es posible. */
    public static int distanciaConParada(int[][] w, int s, int parada, int t) {
        // TODO 3: dos Dijkstra: s → parada y parada → t (en un grafo no dirigido, dist(parada, t) sale de
        //         dijkstraMatriz(w, parada)).
        throw new UnsupportedOperationException("TODO: implementar distanciaConParada");
    }
}
