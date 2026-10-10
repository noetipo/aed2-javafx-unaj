package pe.edu.unaj.aed2.semana01.modelo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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

    public static List<EstadoArreglo> pasosBurbuja(int[] original) {
        int[] a = original.clone();
        List<EstadoArreglo> pasos = new ArrayList<>();
        pasos.add(new EstadoArreglo(a, -1, -1, EstadoArreglo.Tipo.INICIO, 0, true,
                "Arreglo inicial. Burbuja lleva el mayor al final en cada pasada."));
        int ordenados = 0;
        for (int fin = a.length - 1; fin > 0; fin--) {
            boolean huboIntercambio = false;
            for (int j = 0; j < fin; j++) {
                pasos.add(new EstadoArreglo(a, j, j + 1, EstadoArreglo.Tipo.COMPARACION, ordenados, true,
                        "¿" + a[j] + " > " + a[j + 1] + "?"));
                if (a[j] > a[j + 1]) {
                    intercambiar(a, j, j + 1);
                    huboIntercambio = true;
                    pasos.add(new EstadoArreglo(a, j, j + 1, EstadoArreglo.Tipo.INTERCAMBIO, ordenados, true,
                            "Sí: se intercambian " + a[j + 1] + " y " + a[j] + "."));
                }
            }
            ordenados++;
            if (!huboIntercambio) {
                pasos.add(new EstadoArreglo(a, -1, -1, EstadoArreglo.Tipo.FIN, a.length, true,
                        "Una pasada sin intercambios: el arreglo ya está ordenado."));
                return pasos;
            }
        }
        pasos.add(new EstadoArreglo(a, -1, -1, EstadoArreglo.Tipo.FIN, a.length, true, "Arreglo ordenado."));
        return pasos;
    }

    public static List<EstadoArreglo> pasosInsercion(int[] original) {
        int[] a = original.clone();
        List<EstadoArreglo> pasos = new ArrayList<>();
        pasos.add(new EstadoArreglo(a, -1, -1, EstadoArreglo.Tipo.INICIO, 1, false,
                "Arreglo inicial. Inserción hace crecer un prefijo ordenado."));
        for (int i = 1; i < a.length; i++) {
            int j = i;
            while (j > 0) {
                pasos.add(new EstadoArreglo(a, j - 1, j, EstadoArreglo.Tipo.COMPARACION, i, false,
                        "Insertando " + a[j] + ": ¿" + a[j - 1] + " > " + a[j] + "?"));
                if (a[j - 1] <= a[j]) {
                    break;
                }
                intercambiar(a, j - 1, j);
                pasos.add(new EstadoArreglo(a, j - 1, j, EstadoArreglo.Tipo.INTERCAMBIO, i, false,
                        "Sí: " + a[j - 1] + " retrocede una posición."));
                j--;
            }
        }
        pasos.add(new EstadoArreglo(a, -1, -1, EstadoArreglo.Tipo.FIN, a.length, false, "Arreglo ordenado."));
        return pasos;
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
