package pe.edu.unaj.aed2.semana02.modelo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Quicksort con partición de Lomuto (pivote = último elemento del rango).
 * <p>
 * Divide y vencerás: la partición deja el pivote en su posición final con los
 * menores a la izquierda y los mayores a la derecha; luego se resuelven
 * recursivamente ambas mitades. O(n log n) en promedio, O(n²) en el peor caso
 * (arreglo ya ordenado con este pivote).
 */
public final class QuickSort {

    private QuickSort() {
    }

    /** Ordena {@code a} y retorna el número de comparaciones. */
    public static long ordenar(int[] a) {
        long[] comparaciones = {0};
        ordenar(a, 0, a.length - 1, comparaciones);
        return comparaciones[0];
    }

    private static void ordenar(int[] a, int inicio, int fin, long[] comparaciones) {
        if (inicio >= fin) {
            return; // caso base: 0 o 1 elemento
        }
        comparaciones[0] += fin - inicio;
        int p = particionar(a, inicio, fin);
        ordenar(a, inicio, p - 1, comparaciones);
        ordenar(a, p + 1, fin, comparaciones);
    }

    /**
     * Partición de Lomuto sobre {@code a[inicio..fin]}.
     *
     * @return índice final del pivote
     */
    public static int particionar(int[] a, int inicio, int fin) {
        int pivote = a[fin];
        int i = inicio;
        for (int j = inicio; j < fin; j++) {
            if (a[j] < pivote) {
                intercambiar(a, i, j);
                i++;
            }
        }
        intercambiar(a, i, fin);
        return i;
    }

    /** Profundidad máxima de recursión alcanzada al ordenar una copia de {@code a}. */
    public static int profundidadMaxima(int[] a) {
        return profundidad(a.clone(), 0, a.length - 1);
    }

    private static int profundidad(int[] a, int inicio, int fin) {
        if (inicio >= fin) {
            return 0;
        }
        int p = particionar(a, inicio, fin);
        return 1 + Math.max(profundidad(a, inicio, p - 1), profundidad(a, p + 1, fin));
    }

    // ------------------------------------------------------------------
    // Pasos para la animación
    // ------------------------------------------------------------------

    public static List<EstadoParticion> pasos(int[] original) {
        int[] a = original.clone();
        boolean[] fijos = new boolean[a.length];
        List<EstadoParticion> pasos = new ArrayList<>();
        pasos.add(new EstadoParticion(a, -1, -1, -1, -1, -1, fijos, 0, EstadoParticion.Tipo.INICIO,
                "Arreglo inicial. Quicksort particiona alrededor de un pivote y se llama a sí mismo."));
        pasos(a, 0, a.length - 1, 0, fijos, pasos);
        Arrays.fill(fijos, true);
        pasos.add(new EstadoParticion(a, -1, -1, -1, -1, -1, fijos, 0, EstadoParticion.Tipo.FIN,
                "Arreglo ordenado."));
        return pasos;
    }

    private static void pasos(int[] a, int inicio, int fin, int nivel,
                              boolean[] fijos, List<EstadoParticion> pasos) {
        if (inicio > fin) {
            return;
        }
        if (inicio == fin) {
            fijos[inicio] = true;
            pasos.add(new EstadoParticion(a, inicio, fin, -1, -1, -1, fijos, nivel, EstadoParticion.Tipo.LLAMADA,
                    "quicksort(" + inicio + ", " + fin + "): un solo elemento, caso base."));
            return;
        }
        pasos.add(new EstadoParticion(a, inicio, fin, fin, inicio, -1, fijos, nivel, EstadoParticion.Tipo.LLAMADA,
                "quicksort(" + inicio + ", " + fin + ") nivel " + nivel + ": pivote = " + a[fin] + "."));
        int pivote = a[fin];
        int i = inicio;
        for (int j = inicio; j < fin; j++) {
            pasos.add(new EstadoParticion(a, inicio, fin, fin, i, j, fijos, nivel, EstadoParticion.Tipo.COMPARACION,
                    "¿" + a[j] + " < " + pivote + "?"));
            if (a[j] < pivote) {
                intercambiar(a, i, j);
                pasos.add(new EstadoParticion(a, inicio, fin, fin, i, j, fijos, nivel,
                        EstadoParticion.Tipo.INTERCAMBIO,
                        "Sí: " + a[i] + " pasa a la zona de menores (posición " + i + ")."));
                i++;
            }
        }
        intercambiar(a, i, fin);
        fijos[i] = true;
        pasos.add(new EstadoParticion(a, inicio, fin, i, -1, -1, fijos, nivel, EstadoParticion.Tipo.PIVOTE_UBICADO,
                "El pivote " + pivote + " queda en su posición final " + i + "."));
        pasos(a, inicio, i - 1, nivel + 1, fijos, pasos);
        pasos(a, i + 1, fin, nivel + 1, fijos, pasos);
    }

    private static void intercambiar(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }
}
