package pe.edu.unaj.aed2.semana01.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Algoritmos de ordenamiento usados para estudiar complejidad:
 * burbuja e inserción (O(n²)) frente a merge sort (O(n log n)).
 * <p>
 * Cada método de ordenamiento retorna el número de comparaciones realizadas,
 * para contrastar el conteo de operaciones con el tiempo medido.
 * Los métodos {@code pasos*} generan la secuencia que anima la vista.
 */
public final class Ordenamientos {

    private Ordenamientos() {
    }

    // ------------------------------------------------------------------
    // Ordenamientos (modifican el arreglo, retornan comparaciones)
    // ------------------------------------------------------------------

    public static long burbuja(int[] a) {
        long comparaciones = 0;
        for (int fin = a.length - 1; fin > 0; fin--) {
            boolean huboIntercambio = false;
            for (int j = 0; j < fin; j++) {
                comparaciones++;
                if (a[j] > a[j + 1]) {
                    intercambiar(a, j, j + 1);
                    huboIntercambio = true;
                }
            }
            if (!huboIntercambio) {
                break;
            }
        }
        return comparaciones;
    }

    public static long insercion(int[] a) {
        long comparaciones = 0;
        for (int i = 1; i < a.length; i++) {
            int actual = a[i];
            int j = i - 1;
            while (j >= 0) {
                comparaciones++;
                if (a[j] <= actual) {
                    break;
                }
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = actual;
        }
        return comparaciones;
    }

    public static long mergeSort(int[] a) {
        long[] comparaciones = {0};
        int[] auxiliar = new int[a.length];
        mergeSort(a, auxiliar, 0, a.length - 1, comparaciones);
        return comparaciones[0];
    }

    private static void mergeSort(int[] a, int[] aux, int inicio, int fin, long[] comparaciones) {
        if (inicio >= fin) {
            return;
        }
        int medio = (inicio + fin) >>> 1;
        mergeSort(a, aux, inicio, medio, comparaciones);
        mergeSort(a, aux, medio + 1, fin, comparaciones);
        System.arraycopy(a, inicio, aux, inicio, fin - inicio + 1);
        int i = inicio;
        int j = medio + 1;
        for (int k = inicio; k <= fin; k++) {
            if (i > medio) {
                a[k] = aux[j++];
            } else if (j > fin) {
                a[k] = aux[i++];
            } else {
                comparaciones[0]++;
                a[k] = aux[j] < aux[i] ? aux[j++] : aux[i++];
            }
        }
    }

    /** Merge sort genérico y estable, para ordenar objetos por cualquier criterio. */
    public static <T> void mergeSort(List<T> lista, Comparator<? super T> criterio) {
        if (lista.size() < 2) {
            return;
        }
        int medio = lista.size() / 2;
        List<T> izquierda = new ArrayList<>(lista.subList(0, medio));
        List<T> derecha = new ArrayList<>(lista.subList(medio, lista.size()));
        mergeSort(izquierda, criterio);
        mergeSort(derecha, criterio);
        int i = 0;
        int j = 0;
        for (int k = 0; k < lista.size(); k++) {
            if (j >= derecha.size()
                    || (i < izquierda.size() && criterio.compare(izquierda.get(i), derecha.get(j)) <= 0)) {
                lista.set(k, izquierda.get(i++));
            } else {
                lista.set(k, derecha.get(j++));
            }
        }
    }

    // ------------------------------------------------------------------
    // Generación de pasos para la animación
    // ------------------------------------------------------------------

    /** Pseudocódigo que acompaña a {@link #pasosBurbuja}; las líneas se numeran desde 1. */
    public static final List<String> PSEUDO_BURBUJA = List.of(
            "para fin ← n−1 hasta 1:",
            "    huboIntercambio ← falso",
            "    para j ← 0 hasta fin−1:",
            "        si a[j] > a[j+1]:",
            "            intercambiar a[j] y a[j+1]",
            "            huboIntercambio ← verdadero",
            "    si no huboIntercambio: terminar");

    /** Pseudocódigo que acompaña a {@link #pasosInsercion}. */
    public static final List<String> PSEUDO_INSERCION = List.of(
            "para i ← 1 hasta n−1:",
            "    j ← i",
            "    mientras j > 0 y a[j−1] > a[j]:",
            "        intercambiar a[j−1] y a[j]",
            "        j ← j − 1");

    /** Burbuja en Java; los marcadores //@n enlazan cada línea con el pseudocódigo. */
    public static final CodigoAlgoritmo CODIGO_BURBUJA = new CodigoAlgoritmo(PSEUDO_BURBUJA, """
            public static void burbuja(int[] a) {
                int n = a.length;
                for (int fin = n - 1; fin > 0; fin--) {        //@1
                    boolean huboIntercambio = false;           //@2
                    for (int j = 0; j < fin; j++) {            //@3
                        if (a[j] > a[j + 1]) {                 //@4
                            int temp = a[j];                   //@5
                            a[j] = a[j + 1];                   //@5
                            a[j + 1] = temp;                   //@5
                            huboIntercambio = true;            //@6
                        }
                    }
                    if (!huboIntercambio) {                    //@7
                        break;                                 //@7
                    }
                }
            }
            """);

    /** Inserción (con intercambios, como en la animación) en Java. */
    public static final CodigoAlgoritmo CODIGO_INSERCION = new CodigoAlgoritmo(PSEUDO_INSERCION, """
            public static void insercion(int[] a) {
                for (int i = 1; i < a.length; i++) {           //@1
                    int j = i;                                 //@2
                    while (j > 0 && a[j - 1] > a[j]) {         //@3
                        int temp = a[j - 1];                   //@4
                        a[j - 1] = a[j];                       //@4
                        a[j] = temp;                           //@4
                        j--;                                   //@5
                    }
                }
            }
            """);

    public static List<EstadoArreglo> pasosBurbuja(int[] original) {
        int[] a = original.clone();
        List<EstadoArreglo> pasos = new ArrayList<>();
        int comparaciones = 0;
        int intercambios = 0;
        pasos.add(new EstadoArreglo(a, -1, -1, EstadoArreglo.Tipo.INICIO, 0, true, 0, Map.of(),
                "n = a.length = " + a.length,
                "Arreglo inicial. Burbuja lleva el mayor al final en cada pasada."));
        int ordenados = 0;
        for (int fin = a.length - 1; fin > 0; fin--) {
            boolean huboIntercambio = false;
            for (int j = 0; j < fin; j++) {
                comparaciones++;
                boolean mayor = a[j] > a[j + 1];
                pasos.add(new EstadoArreglo(a, j, j + 1, EstadoArreglo.Tipo.COMPARACION, ordenados, true, 4,
                        variablesBurbuja(fin, j, a, huboIntercambio, comparaciones, intercambios),
                        "a[j] > a[j + 1]  →  a[" + j + "] > a[" + (j + 1) + "]  →  " + a[j] + " > " + a[j + 1]
                                + "  →  " + PasoTrazable.vf(mayor),
                        "¿" + a[j] + " > " + a[j + 1] + "?"));
                if (mayor) {
                    int temp = a[j];
                    intercambiar(a, j, j + 1);
                    huboIntercambio = true;
                    intercambios++;
                    pasos.add(new EstadoArreglo(a, j, j + 1, EstadoArreglo.Tipo.INTERCAMBIO, ordenados, true, 5,
                            variablesBurbuja(fin, j, a, huboIntercambio, comparaciones, intercambios),
                            "temp = " + temp + ";  a[" + j + "] = " + a[j] + ";  a[" + (j + 1) + "] = temp = " + temp,
                            "Sí: se intercambian " + a[j + 1] + " y " + a[j] + "."));
                }
            }
            ordenados++;
            if (!huboIntercambio) {
                pasos.add(new EstadoArreglo(a, -1, -1, EstadoArreglo.Tipo.FIN, a.length, true, 7,
                        PasoTrazable.variables("fin", fin, "huboIntercambio", false,
                                "comparaciones", comparaciones, "intercambios", intercambios),
                        "!huboIntercambio  →  !falso  →  verdadero  →  break",
                        "Una pasada sin intercambios: el arreglo ya está ordenado."));
                return pasos;
            }
        }
        pasos.add(new EstadoArreglo(a, -1, -1, EstadoArreglo.Tipo.FIN, a.length, true, 0,
                PasoTrazable.variables("comparaciones", comparaciones, "intercambios", intercambios),
                "fin > 0  →  0 > 0  →  falso: termina el for",
                "Arreglo ordenado."));
        return pasos;
    }

    private static Map<String, String> variablesBurbuja(int fin, int j, int[] a, boolean hubo,
                                                        int comparaciones, int intercambios) {
        return PasoTrazable.variables("fin", fin, "j", j, "a[j]", a[j], "a[j+1]", a[j + 1],
                "huboIntercambio", hubo, "comparaciones", comparaciones, "intercambios", intercambios);
    }

    public static List<EstadoArreglo> pasosInsercion(int[] original) {
        int[] a = original.clone();
        List<EstadoArreglo> pasos = new ArrayList<>();
        int comparaciones = 0;
        int intercambios = 0;
        pasos.add(new EstadoArreglo(a, -1, -1, EstadoArreglo.Tipo.INICIO, 1, false, 0, Map.of(),
                "a.length = " + a.length,
                "Arreglo inicial. Inserción hace crecer un prefijo ordenado."));
        for (int i = 1; i < a.length; i++) {
            int j = i;
            while (j > 0) {
                comparaciones++;
                boolean mayor = a[j - 1] > a[j];
                pasos.add(new EstadoArreglo(a, j - 1, j, EstadoArreglo.Tipo.COMPARACION, i, false, 3,
                        variablesInsercion(i, j, a, comparaciones, intercambios),
                        "j > 0 && a[j - 1] > a[j]  →  " + j + " > 0 && " + a[j - 1] + " > " + a[j]
                                + "  →  " + PasoTrazable.vf(mayor),
                        "Insertando " + a[j] + ": ¿" + a[j - 1] + " > " + a[j] + "?"));
                if (!mayor) {
                    break;
                }
                int temp = a[j - 1];
                intercambiar(a, j - 1, j);
                intercambios++;
                pasos.add(new EstadoArreglo(a, j - 1, j, EstadoArreglo.Tipo.INTERCAMBIO, i, false, 4,
                        variablesInsercion(i, j, a, comparaciones, intercambios),
                        "temp = " + temp + ";  a[" + (j - 1) + "] = " + a[j - 1] + ";  a[" + j + "] = temp = " + temp,
                        "Sí: " + a[j - 1] + " retrocede una posición."));
                j--;
            }
        }
        pasos.add(new EstadoArreglo(a, -1, -1, EstadoArreglo.Tipo.FIN, a.length, false, 0,
                PasoTrazable.variables("comparaciones", comparaciones, "intercambios", intercambios),
                "i < a.length  →  " + a.length + " < " + a.length + "  →  falso: termina el for",
                "Arreglo ordenado."));
        return pasos;
    }

    private static Map<String, String> variablesInsercion(int i, int j, int[] a,
                                                          int comparaciones, int intercambios) {
        return PasoTrazable.variables("i", i, "j", j, "a[j-1]", a[j - 1], "a[j]", a[j],
                "comparaciones", comparaciones, "intercambios", intercambios);
    }

    // ------------------------------------------------------------------
    // Utilidades
    // ------------------------------------------------------------------

    public static int[] aleatorio(int n, long semilla) {
        Random r = new Random(semilla);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) {
            a[i] = r.nextInt(Math.max(10, n * 10)) + 1;
        }
        return a;
    }

    public static boolean estaOrdenado(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i - 1] > a[i]) {
                return false;
            }
        }
        return true;
    }

    private static void intercambiar(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }
}
