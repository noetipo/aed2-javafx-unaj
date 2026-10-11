package pe.edu.unaj.aed2.semana13.tarea;

/**
 * TAREA SEMANA 13 — Backtracking.
 * <p>
 * Las tres funciones siguen el mismo esquema: elegir, probar recursivamente y DESHACER la elección si no sirvió.
 * <ul>
 *   <li>No cambies las firmas: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-13/tarea.md
 */
public final class TareaSemana13 {

    private TareaSemana13() {
    }

    /** Cuántas formas distintas hay de poner n reinas sin que se ataquen (n = 8 da 92). */
    public static int contarReinas(int n) {
        // TODO 1: como colocar(fila) del visualizador, pero en vez de detenerte en la primera solución, súmalas todas.
        throw new UnsupportedOperationException("TODO: implementar contarReinas");
    }

    /**
     * Elegir algunos sacos para cargar EXACTAMENTE {@code objetivo} kg. Retorna qué sacos se eligen (elegido[i]) o
     * null si es imposible. pesos[i] &gt; 0.
     */
    public static boolean[] subconjuntoConSuma(int[] pesos, int objetivo) {
        // TODO 2: para cada saco hay dos ramas: llevarlo o no. Poda: si la suma ya pasó el objetivo, vuelve atrás.
        throw new UnsupportedOperationException("TODO: implementar subconjuntoConSuma");
    }

    /**
     * Cuántos caminos SIMPLES (sin repetir casillas) hay de 'S' a 'E' en el mapa, moviéndose arriba, abajo,
     * izquierda o derecha. '#' es pared y '.' es libre. Hay exactamente una 'S' y una 'E'.
     */
    public static int contarCaminos(char[][] mapa) {
        // TODO 3: desde S, marca la casilla como visitada, prueba los 4 vecinos y DESMÁRCALA al volver; al llegar a
        //         E cuenta 1.
        throw new UnsupportedOperationException("TODO: implementar contarCaminos");
    }
}
