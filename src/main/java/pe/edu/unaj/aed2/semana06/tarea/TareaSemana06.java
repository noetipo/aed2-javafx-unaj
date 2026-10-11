package pe.edu.unaj.aed2.semana06.tarea;

/**
 * TAREA SEMANA 06 — montículos sobre arreglos. Completa los métodos marcados con TODO.
 * <p>
 * Recuerda: en la posición k, los hijos están en 2k + 1 y 2k + 2, y el padre en (k − 1) / 2.
 * <ul>
 *   <li>No cambies las firmas: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-06/tarea.md
 */
public final class TareaSemana06 {

    private TareaSemana06() {
    }

    /** true si el arreglo completo cumple la propiedad de montículo de MÍNIMOS (cada padre ≤ sus hijos). */
    public static boolean esMonticuloMin(int[] a) {
        // TODO 1: basta revisar cada posición k ≥ 1 contra su padre.
        throw new UnsupportedOperationException("TODO: implementar esMonticuloMin");
    }

    /**
     * Convierte el arreglo, EN EL MISMO arreglo, en un montículo de mínimos con el método de abajo hacia
     * arriba (O(n)): hundir cada posición desde n/2 − 1 hasta 0. Como heapsort, pero con mínimos.
     */
    public static void construirMonticulo(int[] a) {
        // TODO 2: escribe un método auxiliar hundir(a, i, n) para mínimos y úsalo.
        throw new UnsupportedOperationException("TODO: implementar construirMonticulo");
    }

    /**
     * Los k números más grandes de {@code a}, de mayor a menor. Usa java.util.PriorityQueue (que en Java es
     * un montículo de MÍNIMOS) manteniendo dentro a lo más k elementos: si llega uno mayor que el mínimo de
     * la cola, el mínimo sale. Así cuesta O(n log k) y no hace falta ordenar todo el arreglo.
     *
     * @throws IllegalArgumentException si k es negativo o mayor que a.length
     */
    public static int[] kMayores(int[] a, int k) {
        // TODO 3
        throw new UnsupportedOperationException("TODO: implementar kMayores");
    }
}
