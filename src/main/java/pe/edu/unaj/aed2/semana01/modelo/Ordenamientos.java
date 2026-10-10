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
        int n = a.length;
        List<EstadoArreglo> pasos = new ArrayList<>();
        int comparaciones = 0;
        int intercambios = 0;
        pasos.add(new EstadoArreglo(a, -1, -1, EstadoArreglo.Tipo.INICIO, 0, true, 0, Map.of(),
                "n = a.length = " + n,
                "Empieza burbuja con " + n + " elementos. El for externo arranca con fin = n − 1 = " + (n - 1)
                        + ". En cada pasada, el for interno recorre las posiciones 0 a fin comparando cada elemento"
                        + " con su vecino de la derecha, de modo que el mayor de esa zona va \"flotando\" hasta"
                        + " la posición fin.",
                "Arreglo inicial. Burbuja lleva el mayor al final en cada pasada."));
        int ordenados = 0;
        for (int fin = n - 1; fin > 0; fin--) {
            boolean huboIntercambio = false;
            for (int j = 0; j < fin; j++) {
                comparaciones++;
                int x = a[j];
                int y = a[j + 1];
                boolean mayor = x > y;
                String explicacion = (j == 0 ? "Comienza la pasada con fin = " + fin + ". " : "")
                        + "El if consulta: ¿a[j] es mayor que a[j + 1]? Con j = " + j + " se comparan a[" + j
                        + "] = " + x + " y su vecino a[" + (j + 1) + "] = " + y + ". "
                        + (mayor
                        ? "Como " + x + " > " + y + ", la condición es VERDADERA: están en desorden, así que la"
                                + " ejecución entra al if para intercambiarlos."
                        : "Como " + x + " no es mayor que " + y + ", la condición es FALSA: ya están en orden."
                                + " La ejecución salta el cuerpo del if y regresa al for interno. "
                                + siguienteEnBurbuja(j, fin, a, huboIntercambio));
                pasos.add(new EstadoArreglo(a, j, j + 1, EstadoArreglo.Tipo.COMPARACION, ordenados, true, 4,
                        variablesBurbuja(fin, j, a, huboIntercambio, comparaciones, intercambios),
                        "a[j] > a[j + 1]  →  a[" + j + "] > a[" + (j + 1) + "]  →  " + x + " > " + y
                                + "  →  " + PasoTrazable.vf(mayor),
                        explicacion,
                        "¿" + x + " > " + y + "?"));
                if (mayor) {
                    intercambiar(a, j, j + 1);
                    huboIntercambio = true;
                    intercambios++;
                    pasos.add(new EstadoArreglo(a, j, j + 1, EstadoArreglo.Tipo.INTERCAMBIO, ordenados, true, 5,
                            variablesBurbuja(fin, j, a, huboIntercambio, comparaciones, intercambios),
                            "temp = " + x + ";  a[" + j + "] = " + y + ";  a[" + (j + 1) + "] = temp = " + x,
                            "Dentro del if se hace el intercambio en tres pasos: se guarda a[" + j + "] = " + x
                                    + " en temp para no perderlo, se copia a[" + (j + 1) + "] = " + y + " en a[" + j
                                    + "] y luego temp en a[" + (j + 1) + "]. Ahora a[" + j + "] = " + y + " y a["
                                    + (j + 1) + "] = " + x + ". Se anota huboIntercambio = true. Termina el if y la"
                                    + " ejecución regresa al for interno. "
                                    + siguienteEnBurbuja(j, fin, a, true),
                            "Sí: se intercambian " + x + " y " + y + "."));
                }
            }
            ordenados++;
            if (!huboIntercambio) {
                pasos.add(new EstadoArreglo(a, -1, -1, EstadoArreglo.Tipo.FIN, n, true, 7,
                        PasoTrazable.variables("fin", fin, "huboIntercambio", false,
                                "comparaciones", comparaciones, "intercambios", intercambios),
                        "!huboIntercambio  →  !falso  →  verdadero  →  break",
                        "Terminada la pasada se consulta !huboIntercambio. Como en toda la pasada no se intercambió"
                                + " nada, huboIntercambio sigue en false y !false es VERDADERO: el arreglo ya está"
                                + " ordenado. Se ejecuta break, que sale del for externo, y el método termina"
                                + (fin - 1 > 0
                                ? " sin hacer " + (fin - 1 == 1 ? "la pasada que faltaba" : "las " + (fin - 1)
                                        + " pasadas que faltaban") + ". Esta es la parada temprana: por eso el"
                                        + " mejor caso de burbuja (arreglo ya ordenado) es O(n)."
                                : "; además era la última pasada, así que el ahorro aquí es nulo."),
                        "Una pasada sin intercambios: el arreglo ya está ordenado."));
                return pasos;
            }
        }
        pasos.add(new EstadoArreglo(a, -1, -1, EstadoArreglo.Tipo.FIN, n, true, 0,
                PasoTrazable.variables("comparaciones", comparaciones, "intercambios", intercambios),
                "fin > 0  →  0 > 0  →  falso: termina el for",
                "El for externo redujo fin hasta 0 y su condición fin > 0 ya es FALSA, así que termina y el"
                        + " método regresa a quien lo llamó. Cada pasada dejó un elemento fijo al final: el arreglo"
                        + " está ordenado con " + comparaciones + " comparaciones y " + intercambios + " intercambios.",
                "Arreglo ordenado."));
        return pasos;
    }

    /** Qué hace el for interno de burbuja después de terminar con el par (j, j + 1). */
    private static String siguienteEnBurbuja(int j, int fin, int[] a, boolean huboIntercambio) {
        if (j + 1 < fin) {
            return "El for hace j++ (j = " + (j + 1) + "), consulta j < fin (" + (j + 1) + " < " + fin
                    + ", verdadero) y vuelve al if para comparar el siguiente par.";
        }
        String texto = "El for hace j++ (j = " + fin + ") y consulta j < fin (" + fin + " < " + fin
                + "): es FALSO, así que el for interno termina. La pasada acabó y " + a[fin]
                + " quedó fijo en la posición " + fin + ". ";
        if (!huboIntercambio) {
            return texto + "Ahora se revisará si hubo intercambios en esta pasada.";
        }
        texto += "Se consulta !huboIntercambio: como sí hubo intercambios, es FALSO y no se hace break. ";
        return fin - 1 > 0
                ? texto + "El for externo hace fin-- (fin = " + (fin - 1) + ") y empieza otra pasada desde j = 0."
                : texto + "El for externo hace fin-- (fin = 0); fin > 0 es falso y el método termina.";
    }

    private static Map<String, String> variablesBurbuja(int fin, int j, int[] a, boolean hubo,
                                                        int comparaciones, int intercambios) {
        return PasoTrazable.variables("fin", fin, "j", j, "a[j]", a[j], "a[j+1]", a[j + 1],
                "huboIntercambio", hubo, "comparaciones", comparaciones, "intercambios", intercambios);
    }

    public static List<EstadoArreglo> pasosInsercion(int[] original) {
        int[] a = original.clone();
        int n = a.length;
        List<EstadoArreglo> pasos = new ArrayList<>();
        int comparaciones = 0;
        int intercambios = 0;
        pasos.add(new EstadoArreglo(a, -1, -1, EstadoArreglo.Tipo.INICIO, 1, false, 0, Map.of(),
                "a.length = " + n,
                "Empieza inserción con " + n + " elementos. Al inicio solo a[0] forma la parte ordenada. El for"
                        + " toma uno por uno los elementos desde i = 1 y el while los va moviendo hacia la"
                        + " izquierda hasta dejarlos en su lugar dentro de esa parte ordenada.",
                "Arreglo inicial. Inserción hace crecer un prefijo ordenado."));
        for (int i = 1; i < n; i++) {
            int j = i;
            while (j > 0) {
                comparaciones++;
                int x = a[j - 1];
                int y = a[j];
                boolean mayor = x > y;
                String explicacion = (j == i
                        ? "El for está en i = " + i + ": toca insertar a[" + i + "] = " + y
                                + " en la parte ordenada a[0.." + (i - 1) + "]. Se copia j = i = " + i + ". "
                        : "")
                        + "El while consulta dos condiciones unidas con &&. Primero j > 0: " + j
                        + " > 0 es verdadero. Luego, ¿el vecino de la izquierda a[j − 1] = a[" + (j - 1) + "] = "
                        + x + " es mayor que a[j] = a[" + j + "] = " + y + "? "
                        + (mayor
                        ? "Sí, " + x + " > " + y + ": la condición completa es VERDADERA y la ejecución entra al"
                                + " cuerpo del while para mover " + y + " un lugar a la izquierda."
                        : "No, " + x + " no es mayor que " + y + ": la condición completa es FALSA, " + y
                                + " ya está en su lugar. Se sale del while y la ejecución regresa al for, que hace"
                                + " i++ (i = " + (i + 1) + ")"
                                + (i + 1 < n ? " para insertar el siguiente elemento, a[" + (i + 1) + "] = "
                                        + a[i + 1] + "."
                                        : "; como i < a.length (" + n + " < " + n + ") es falso, el for termina."));
                pasos.add(new EstadoArreglo(a, j - 1, j, EstadoArreglo.Tipo.COMPARACION, i, false, 3,
                        variablesInsercion(i, j, a, comparaciones, intercambios),
                        "j > 0 && a[j - 1] > a[j]  →  " + j + " > 0 && " + x + " > " + y
                                + "  →  " + PasoTrazable.vf(mayor),
                        explicacion,
                        "Insertando " + y + ": ¿" + x + " > " + y + "?"));
                if (!mayor) {
                    break;
                }
                intercambiar(a, j - 1, j);
                intercambios++;
                pasos.add(new EstadoArreglo(a, j - 1, j, EstadoArreglo.Tipo.INTERCAMBIO, i, false, 4,
                        variablesInsercion(i, j, a, comparaciones, intercambios),
                        "temp = " + x + ";  a[" + (j - 1) + "] = " + y + ";  a[" + j + "] = temp = " + x,
                        "Dentro del while se intercambian a[" + (j - 1) + "] y a[" + j + "] usando temp: " + y
                                + " pasa a la posición " + (j - 1) + " y " + x + " a la posición " + j + ". Luego"
                                + " j-- deja j = " + (j - 1) + " y la ejecución regresa a la condición del while. "
                                + (j - 1 > 0
                                ? "Allí se volverá a comparar " + y + " con su nuevo vecino de la izquierda, a["
                                        + (j - 2) + "] = " + a[j - 2] + "."
                                : "Como ahora j = 0, la primera parte (j > 0) será falsa y, gracias al &&, ni"
                                        + " siquiera se consulta a[j − 1]: el while termina con " + y
                                        + " en la posición 0, porque es el menor hasta ahora. Luego el for hace i++"
                                        + (i + 1 < n ? " (i = " + (i + 1) + ") para el siguiente elemento."
                                                : " y termina, porque ya no quedan elementos.")),
                        "Sí: " + y + " retrocede una posición."));
                j--;
            }
        }
        pasos.add(new EstadoArreglo(a, -1, -1, EstadoArreglo.Tipo.FIN, n, false, 0,
                PasoTrazable.variables("comparaciones", comparaciones, "intercambios", intercambios),
                "i < a.length  →  " + n + " < " + n + "  →  falso: termina el for",
                "El for llegó a i = " + n + " y su condición i < a.length (" + n + " < " + n + ") es FALSA:"
                        + " todos los elementos fueron insertados. El método termina con " + comparaciones
                        + " comparaciones y " + intercambios + " intercambios.",
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
