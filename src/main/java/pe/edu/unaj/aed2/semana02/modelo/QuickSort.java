package pe.edu.unaj.aed2.semana02.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    /** Pseudocódigo que acompaña a {@link #pasos}; las líneas se numeran desde 1. */
    public static final List<String> PSEUDOCODIGO = List.of(
            "quicksort(a, inicio, fin):",
            "    si inicio ≥ fin: retornar",
            "    pivote ← a[fin];  i ← inicio",
            "    para j ← inicio hasta fin−1:",
            "        si a[j] < pivote:",
            "            intercambiar a[i] y a[j];  i ← i + 1",
            "    intercambiar a[i] y a[fin]",
            "    quicksort(a, inicio, i−1)",
            "    quicksort(a, i+1, fin)");

    /** Quicksort en Java; los marcadores //@n enlazan cada línea con el pseudocódigo. */
    public static final CodigoAlgoritmo CODIGO = new CodigoAlgoritmo(PSEUDOCODIGO, """
            public static void quicksort(int[] a, int inicio, int fin) {   //@1
                if (inicio >= fin) {                                       //@2
                    return;                                                //@2
                }
                int pivote = a[fin];                                       //@3
                int i = inicio;                                            //@3
                for (int j = inicio; j < fin; j++) {                       //@4
                    if (a[j] < pivote) {                                   //@5
                        intercambiar(a, i, j);                             //@6
                        i++;                                               //@6
                    }
                }
                intercambiar(a, i, fin);                                   //@7
                quicksort(a, inicio, i - 1);                               //@8
                quicksort(a, i + 1, fin);                                  //@9
            }

            private static void intercambiar(int[] a, int x, int y) {
                int temp = a[x];
                a[x] = a[y];
                a[y] = temp;
            }
            """);

    public static List<EstadoParticion> pasos(int[] original) {
        int[] a = original.clone();
        boolean[] fijos = new boolean[a.length];
        List<EstadoParticion> pasos = new ArrayList<>();
        pasos.add(new EstadoParticion(a, -1, -1, -1, -1, -1, fijos, 0, EstadoParticion.Tipo.INICIO, 0,
                Map.of(),
                "Arreglo inicial. Quicksort particiona alrededor de un pivote y se llama a sí mismo."));
        pasos(a, 0, a.length - 1, 0, fijos, pasos);
        Arrays.fill(fijos, true);
        pasos.add(new EstadoParticion(a, -1, -1, -1, -1, -1, fijos, 0, EstadoParticion.Tipo.FIN, 0,
                Map.of(), "Arreglo ordenado."));
        return pasos;
    }

    private static void pasos(int[] a, int inicio, int fin, int nivel,
                              boolean[] fijos, List<EstadoParticion> pasos) {
        if (inicio > fin) {
            return;
        }
        if (inicio == fin) {
            fijos[inicio] = true;
            pasos.add(new EstadoParticion(a, inicio, fin, -1, -1, -1, fijos, nivel, EstadoParticion.Tipo.LLAMADA, 2,
                    PasoTrazable.variables("inicio", inicio, "fin", fin, "nivel", nivel),
                    "quicksort(" + inicio + ", " + fin + "): un solo elemento, caso base."));
            return;
        }
        int pivote = a[fin];
        int i = inicio;
        pasos.add(new EstadoParticion(a, inicio, fin, fin, i, -1, fijos, nivel, EstadoParticion.Tipo.LLAMADA, 3,
                variables(inicio, fin, nivel, pivote, i, null, a),
                "quicksort(" + inicio + ", " + fin + ") nivel " + nivel + ": pivote = " + pivote + "."));
        for (int j = inicio; j < fin; j++) {
            pasos.add(new EstadoParticion(a, inicio, fin, fin, i, j, fijos, nivel, EstadoParticion.Tipo.COMPARACION, 5,
                    variables(inicio, fin, nivel, pivote, i, j, a),
                    "¿" + a[j] + " < " + pivote + "?"));
            if (a[j] < pivote) {
                intercambiar(a, i, j);
                pasos.add(new EstadoParticion(a, inicio, fin, fin, i, j, fijos, nivel,
                        EstadoParticion.Tipo.INTERCAMBIO, 6,
                        variables(inicio, fin, nivel, pivote, i + 1, j, a),
                        "Sí: " + a[i] + " pasa a la zona de menores (posición " + i + ")."));
                i++;
            }
        }
        intercambiar(a, i, fin);
        fijos[i] = true;
        pasos.add(new EstadoParticion(a, inicio, fin, i, -1, -1, fijos, nivel, EstadoParticion.Tipo.PIVOTE_UBICADO, 7,
                variables(inicio, fin, nivel, pivote, i, null, a),
                "El pivote " + pivote + " queda en su posición final " + i + "."));
        pasos(a, inicio, i - 1, nivel + 1, fijos, pasos);
        pasos(a, i + 1, fin, nivel + 1, fijos, pasos);
    }

    private static Map<String, String> variables(int inicio, int fin, int nivel, int pivote,
                                                 int i, Integer j, int[] a) {
        return PasoTrazable.variables("inicio", inicio, "fin", fin, "nivel", nivel, "pivote", pivote,
                "i", i, "j", j == null ? "—" : j, "a[j]", j == null ? "—" : a[j]);
    }

    private static void intercambiar(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }
}
