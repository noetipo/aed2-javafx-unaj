package pe.edu.unaj.aed2.semana01.tarea;

/**
 * TAREA SEMANA 01 — completa los métodos marcados con TODO.
 * <p>
 * Reglas:
 * <ul>
 *   <li>No uses {@code Arrays.sort} ni {@code Collections.sort}.</li>
 *   <li>No cambies las firmas de los métodos: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-01/tarea.md
 */
public final class TareaSemana01 {

    private TareaSemana01() {
    }

    /**
     * Ordena {@code a} de menor a mayor con el algoritmo de SELECCIÓN.
     *
     * @return número de comparaciones entre elementos realizadas
     */
    public static long seleccion(int[] a) {
        // TODO 1: en cada pasada i, busca el índice del menor en a[i..n-1]
        //         e intercámbialo con a[i]. Cuenta cada comparación.
        throw new UnsupportedOperationException("TODO: implementar seleccion");
    }

    /**
     * Búsqueda binaria iterativa sobre un arreglo ORDENADO.
     *
     * @return índice de {@code x} en {@code a}, o -1 si no está
     */
    public static int busquedaBinaria(int[] a, int x) {
        // TODO 2: mantén los límites [inicio, fin] y compara con el elemento central.
        throw new UnsupportedOperationException("TODO: implementar busquedaBinaria");
    }

    /**
     * Cuenta cuántas comparaciones hace la búsqueda LINEAL en el peor caso
     * para un arreglo de tamaño n, y cuántas hace la binaria (⌊log2 n⌋ + 1).
     *
     * @return arreglo {lineal, binaria}
     */
    public static long[] comparacionesPeorCaso(int n) {
        // TODO 3: retorna {n, floor(log2(n)) + 1}. Para n = 0 retorna {0, 0}.
        throw new UnsupportedOperationException("TODO: implementar comparacionesPeorCaso");
    }
}
