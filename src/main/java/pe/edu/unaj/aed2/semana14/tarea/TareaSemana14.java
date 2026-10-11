package pe.edu.unaj.aed2.semana14.tarea;

/**
 * TAREA SEMANA 14 — Programación dinámica.
 * <p>
 * En las tres, define primero QUÉ significa cada casilla de tu tabla, luego cómo se calcula a partir de casillas ya
 * resueltas (la recurrencia) y por último el orden en que se llena.
 * <ul>
 *   <li>Nada de recursión sin memoria: debe ser O(tamaño de la tabla).</li>
 *   <li>No cambies las firmas: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-14/tarea.md
 */
public final class TareaSemana14 {

    private TareaSemana14() {
    }

    /**
     * La menor cantidad de monedas que suman exactamente {@code monto}, con monedas ilimitadas de cada valor; -1 si no
     * se puede. Con {1, 3, 4} y monto 6 la respuesta es 2 (3 + 3), aunque la regla voraz daría 3 (4 + 1 + 1).
     */
    public static int cambioMonedas(int[] monedas, int monto) {
        // TODO 1: min[x] = menor cantidad para sumar x. min[0] = 0; min[x] = 1 + mínimo de min[x − m] para cada moneda
        //         m ≤ x que tenga solución.
        throw new UnsupportedOperationException("TODO: implementar cambioMonedas");
    }

    /** Largo de la subsecuencia común más larga (no necesariamente contigua) de a y b. */
    public static int subsecuenciaComun(String a, String b) {
        // TODO 2: L[i][j] = respuesta para los primeros i caracteres de a y j de b. Si a[i−1] == b[j−1],
        //         L[i][j] = L[i−1][j−1] + 1; si no, el mayor entre L[i−1][j] y L[i][j−1].
        throw new UnsupportedOperationException("TODO: implementar subsecuenciaComun");
    }

    /**
     * Distancia de edición (Levenshtein): la menor cantidad de inserciones, borrados o cambios de UN carácter para
     * convertir a en b. Sirve para sugerir palabras parecidas en un diccionario ("killa" → "qilla" = 1).
     */
    public static int distanciaEdicion(String a, String b) {
        // TODO 3: D[i][j] para los primeros i y j caracteres. D[i][0] = i, D[0][j] = j. Si los caracteres son iguales,
        //         D[i−1][j−1]; si no, 1 + el mínimo de borrar, insertar o cambiar.
        throw new UnsupportedOperationException("TODO: implementar distanciaEdicion");
    }
}
