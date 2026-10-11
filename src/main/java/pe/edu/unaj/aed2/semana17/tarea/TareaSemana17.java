package pe.edu.unaj.aed2.semana17.tarea;

/**
 * TAREA SEMANA 17 — El viajante: exacto más rápido y una heurística.
 * <p>
 * d[i][j] es la distancia de i a j (simétrica, d[i][i] = 0). Una ruta empieza en 0, visita cada vértice una vez y
 * vuelve a 0.
 * <ul>
 *   <li>No cambies las firmas: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-17/tarea.md
 */
public final class TareaSemana17 {

    private TareaSemana17() {
    }

    /**
     * Largo de la ruta óptima con RAMIFICACIÓN Y PODA: backtracking que abandona una ruta parcial en cuanto su largo ya
     * iguala o supera la mejor ruta completa encontrada. Debe resolver 12 vértices en pocos segundos.
     */
    public static int ramificacionYPoda(int[][] d) {
        // TODO 1: empieza con una buena cota (por ejemplo, la ruta del vecino más cercano) para podar desde el inicio.
        //         Probar primero los vecinos más cercanos de cada vértice también ayuda.
        throw new UnsupportedOperationException("TODO: implementar ramificacionYPoda");
    }

    /**
     * Largo de la ruta óptima con PROGRAMACIÓN DINÁMICA (Held–Karp): mejor[S][j] = el camino más corto que sale de 0,
     * visita exactamente el conjunto S y termina en j. O(2^n · n²): con 16 vértices debe tardar menos de 3 segundos.
     */
    public static int heldKarp(int[][] d) {
        // TODO 2: representa S con los bits de un int. mejor[{j}][j] = d[0][j]; para S más grandes,
        //         mejor[S][j] = mín sobre k en S−{j} de mejor[S−{j}][k] + d[k][j]. Al final suma la vuelta a 0.
        throw new UnsupportedOperationException("TODO: implementar heldKarp");
    }

    /**
     * Heurística de INSERCIÓN MÁS BARATA: empezar con la ruta 0 → (el más cercano a 0) → 0 y, mientras falten vértices,
     * insertar el que aumente menos el largo, en el lugar donde lo aumente menos. Retorna el orden (empieza en 0).
     */
    public static int[] insercionMasBarata(int[][] d) {
        // TODO 3: insertar k entre a y b cuesta d[a][k] + d[k][b] − d[a][b]. En distancias "con desigualdad
        //         triangular" esta heurística nunca da más del doble del óptimo.
        throw new UnsupportedOperationException("TODO: implementar insercionMasBarata");
    }
}
