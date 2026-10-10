package pe.edu.unaj.aed2.semana02.tarea;

import pe.edu.unaj.aed2.semana02.modelo.QuickSort;

/**
 * TAREA SEMANA 02 — completa los métodos marcados con TODO.
 * <p>
 * Reglas:
 * <ul>
 *   <li>Las soluciones deben ser RECURSIVAS (sin bucles for/while, salvo donde se indique).</li>
 *   <li>No uses {@code Math.pow}, {@code Arrays.sort} ni {@code Collections.sort}.</li>
 *   <li>No cambies las firmas de los métodos: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-02/tarea.md
 */
public final class TareaSemana02 {

    private TareaSemana02() {
    }

    /**
     * Calcula base^exponente con divide y vencerás en O(log exponente):
     * x^e = (x^(e/2))² si e es par, y x·x^(e−1) si es impar.
     *
     * @param exponente mayor o igual que 0
     */
    public static long potencia(long base, int exponente) {
        // TODO 1: caso base e == 0. Calcula x^(e/2) UNA sola vez y reutilízalo.
        throw new UnsupportedOperationException("TODO: implementar potencia");
    }

    /**
     * Máximo de {@code a[inicio..fin]} dividiendo el rango en dos mitades.
     *
     * @throws IllegalArgumentException si el rango está vacío
     */
    public static int maximo(int[] a, int inicio, int fin) {
        // TODO 2: caso base de un elemento; si no, máximo de cada mitad y compara.
        throw new UnsupportedOperationException("TODO: implementar maximo");
    }

    /**
     * k-ésimo menor elemento (k = 0 es el mínimo) con QUICKSELECT:
     * particiona con {@link QuickSort#particionar(int[], int, int)} y continúa
     * SOLO en la mitad que contiene la posición k. Promedio O(n).
     * Puede modificar el arreglo. Aquí sí puedes usar un bucle en lugar de recursión.
     */
    public static int kEsimoMenor(int[] a, int k) {
        // TODO 3: si la posición del pivote es k, termina; si es mayor, busca a la
        //         izquierda; si es menor, busca a la derecha.
        throw new UnsupportedOperationException("TODO: implementar kEsimoMenor");
    }
}
