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

    /**
     * Clase Java completa, lista para copiar y ejecutar; los marcadores //@n enlazan cada línea
     * con el pseudocódigo. ordenar devuelve una copia ordenada y particionar devuelve el índice del pivote.
     */
    public static final CodigoAlgoritmo CODIGO = new CodigoAlgoritmo(PSEUDOCODIGO, """
            import java.util.Arrays;

            public class QuickSort {

                // Devuelve una copia ordenada; el arreglo original no se modifica.
                public static int[] ordenar(int[] arreglo) {
                    int[] a = arreglo.clone();
                    quicksort(a, 0, a.length - 1);
                    return a;
                }

                // Ordena a[inicio..fin] dentro del mismo arreglo:
                // cada llamada trabaja sobre una parte de "a",
                // por eso no necesita devolver nada.
                private static void quicksort(int[] a, int inicio, int fin) {     //@1
                    if (inicio >= fin) {                                         //@2
                        return;                                                  //@2
                    }
                    int p = particionar(a, inicio, fin);                         //@3,4,5,6,7
                    quicksort(a, inicio, p - 1);                                 //@8
                    quicksort(a, p + 1, fin);                                    //@9
                }

                // Deja el pivote en su lugar definitivo
                // y devuelve esa posición.
                private static int particionar(int[] a, int inicio, int fin) {
                    int pivote = a[fin];                                         //@3
                    int i = inicio;                                              //@3
                    for (int j = inicio; j < fin; j++) {                         //@4
                        if (a[j] < pivote) {                                     //@5
                            intercambiar(a, i, j);                               //@6
                            i++;                                                 //@6
                        }
                    }
                    intercambiar(a, i, fin);                                     //@7
                    return i;                                                    //@7
                }

                private static void intercambiar(int[] a, int x, int y) {
                    int temp = a[x];
                    a[x] = a[y];
                    a[y] = temp;
                }

                public static void main(String[] args) {
                    int[] datos = {10, 39, 22, 49, 79, 85, 44, 3, 61, 90};
                    int[] ordenado = ordenar(datos);
                    System.out.println("Original: " + Arrays.toString(datos));
                    System.out.println("Ordenado: " + Arrays.toString(ordenado));
                }
            }
            """);

    /** Quién hizo la llamada recursiva actual: su rango, dónde quedó su pivote y de qué lado viene. */
    private record Llamador(int inicio, int fin, int pivote, boolean izquierda) {
    }

    public static List<EstadoParticion> pasos(int[] original) {
        int[] a = original.clone();
        boolean[] fijos = new boolean[a.length];
        List<EstadoParticion> pasos = new ArrayList<>();
        pasos.add(new EstadoParticion(a, -1, -1, -1, -1, -1, fijos, 0, EstadoParticion.Tipo.INICIO, 0,
                Map.of(), "quicksort(a, 0, " + (a.length - 1) + ")",
                "Se llama quicksort(a, 0, " + (a.length - 1) + ") sobre todo el arreglo. Cada llamada elige un"
                        + " pivote, lo deja en su lugar definitivo y luego se llama a sí misma dos veces: una para"
                        + " los menores y otra para los mayores.",
                "Arreglo inicial. Quicksort particiona alrededor de un pivote y se llama a sí mismo."));
        pasos(a, 0, a.length - 1, 0, fijos, pasos, null);
        Arrays.fill(fijos, true);
        pasos.add(new EstadoParticion(a, -1, -1, -1, -1, -1, fijos, 0, EstadoParticion.Tipo.FIN, 0,
                Map.of(), "", "Todas las llamadas recursivas regresaron: cada elemento fue pivote o quedó solo en"
                        + " su rango, así que el arreglo completo está ordenado.",
                "Arreglo ordenado."));
        return pasos;
    }

    private static void pasos(int[] a, int inicio, int fin, int nivel,
                              boolean[] fijos, List<EstadoParticion> pasos, Llamador llamador) {
        if (inicio > fin) {
            return;
        }
        if (inicio == fin) {
            fijos[inicio] = true;
            pasos.add(new EstadoParticion(a, inicio, fin, -1, -1, -1, fijos, nivel, EstadoParticion.Tipo.LLAMADA, 2,
                    PasoTrazable.variables("inicio", inicio, "fin", fin, "nivel", nivel),
                    "inicio >= fin  →  " + inicio + " >= " + fin + "  →  verdadero  →  return",
                    "Comienza quicksort(a, " + inicio + ", " + fin + ") en el nivel " + nivel + ". " + origen(llamador)
                            + " El if consulta si inicio >= fin: " + inicio + " >= " + fin + " es VERDADERO, porque"
                            + " el rango tiene un solo elemento (a[" + inicio + "] = " + a[inicio] + "), y un"
                            + " elemento solo ya está ordenado. Es el caso base: se ejecuta return sin hacer nada"
                            + " más. " + regreso(llamador),
                    "quicksort(" + inicio + ", " + fin + "): un solo elemento, caso base."));
            return;
        }
        int pivote = a[fin];
        int i = inicio;
        pasos.add(new EstadoParticion(a, inicio, fin, fin, i, -1, fijos, nivel, EstadoParticion.Tipo.LLAMADA, 3,
                variables(inicio, fin, nivel, pivote, i, null, a),
                "inicio >= fin  →  " + inicio + " >= " + fin + "  →  falso;  pivote = a[" + fin + "] = " + pivote
                        + ";  i = " + inicio,
                "Comienza quicksort(a, " + inicio + ", " + fin + ") en el nivel " + nivel + ". " + origen(llamador)
                        + " El if consulta si inicio >= fin: " + inicio + " >= " + fin + " es FALSO, porque hay "
                        + (fin - inicio + 1) + " elementos por ordenar, así que no se retorna y la ejecución sigue."
                        + " Se llama particionar(a, " + inicio + ", " + fin + "), y quicksort queda esperando el"
                        + " índice que esa función devolverá. Dentro de particionar se elige como pivote el último"
                        + " elemento del rango, a[" + fin + "] = " + pivote
                        + ", y se pone i = " + inicio + ": i marca la posición donde irá el próximo elemento menor"
                        + " que el pivote. Luego empieza el for con j = " + inicio + ".",
                "quicksort(" + inicio + ", " + fin + ") nivel " + nivel + ": pivote = " + pivote + "."));
        for (int j = inicio; j < fin; j++) {
            int valor = a[j];
            boolean menor = valor < pivote;
            pasos.add(new EstadoParticion(a, inicio, fin, fin, i, j, fijos, nivel, EstadoParticion.Tipo.COMPARACION, 5,
                    variables(inicio, fin, nivel, pivote, i, j, a),
                    "a[j] < pivote  →  a[" + j + "] < " + pivote + "  →  " + valor + " < " + pivote
                            + "  →  " + PasoTrazable.vf(menor),
                    "El for está en j = " + j + ". El if consulta: ¿a[j] = a[" + j + "] = " + valor
                            + " es menor que el pivote " + pivote + "? "
                            + (menor
                            ? "Sí, " + valor + " < " + pivote + ": la condición es VERDADERA. " + valor
                                    + " debe ir a la zona de los menores, así que la ejecución entra al if."
                            : "No, " + valor + " no es menor que " + pivote + ": la condición es FALSA. " + valor
                                    + " se queda en la zona de los mayores; la ejecución salta el if y regresa al"
                                    + " for. " + siguienteEnQuicksort(j, fin)),
                    "¿" + valor + " < " + pivote + "?"));
            if (menor) {
                intercambiar(a, i, j);
                pasos.add(new EstadoParticion(a, inicio, fin, fin, i, j, fijos, nivel,
                        EstadoParticion.Tipo.INTERCAMBIO, 6,
                        variables(inicio, fin, nivel, pivote, i + 1, j, a),
                        "intercambiar(a, " + i + ", " + j + ");  i++  →  i pasa de " + i + " a " + (i + 1),
                        "Dentro del if se llama intercambiar(a, i, j) con i = " + i + " y j = " + j + ": "
                                + (i == j
                                ? "como i y j son iguales, " + valor + " ya está en la zona de los menores y se"
                                        + " queda donde está."
                                : valor + " pasa a la posición " + i + " (zona de menores) y el " + a[j]
                                        + " que estaba ahí pasa a la posición " + j + ".")
                                + " Luego i++ deja i = " + (i + 1) + ": la zona de menores ahora es a[" + inicio
                                + ".." + i + "]. Termina el if y la ejecución regresa al for. "
                                + siguienteEnQuicksort(j, fin),
                        "Sí: " + valor + " pasa a la zona de menores (posición " + i + ")."));
                i++;
            }
        }
        intercambiar(a, i, fin);
        fijos[i] = true;
        pasos.add(new EstadoParticion(a, inicio, fin, i, -1, -1, fijos, nivel, EstadoParticion.Tipo.PIVOTE_UBICADO, 7,
                variables(inicio, fin, nivel, pivote, i, null, a),
                "j < fin  →  " + fin + " < " + fin + "  →  falso: termina el for;  intercambiar(a, " + i + ", " + fin
                        + ")  →  a[" + i + "] = " + pivote + ";  return i  →  p = " + i,
                "El for terminó y todos los menores que " + pivote + " quedaron en a[" + inicio + ".." + (i - 1)
                        + "]" + (i == inicio ? " (no hubo ninguno)" : "") + ". Se llama intercambiar(a, i, fin) con"
                        + " i = " + i + " y fin = " + fin + ": el pivote " + pivote + " pasa a la posición " + i
                        + ", que es su lugar DEFINITIVO, porque a su izquierda solo hay menores y a su derecha"
                        + " solo mayores o iguales. particionar ejecuta return i: devuelve " + i + " a quicksort,"
                        + " que lo guarda en p = " + i + ". Ahora quicksort hace la primera llamada recursiva, "
                        + "quicksort(a, " + inicio + ", " + (i - 1) + "), con los menores"
                        + (i - 1 < inicio ? " (rango vacío: regresa de inmediato)" : "")
                        + ", y cuando esa llamada regrese hará la segunda, quicksort(a, " + (i + 1) + ", " + fin
                        + "), con los mayores" + (i + 1 > fin ? " (rango vacío: regresa de inmediato)" : "") + ".",
                "El pivote " + pivote + " queda en su posición final " + i + "."));
        pasos(a, inicio, i - 1, nivel + 1, fijos, pasos, new Llamador(inicio, fin, i, true));
        pasos(a, i + 1, fin, nivel + 1, fijos, pasos, new Llamador(inicio, fin, i, false));
    }

    /** Explica qué llamada originó la actual. */
    private static String origen(Llamador l) {
        if (l == null) {
            return "Es la llamada inicial, sobre todo el arreglo.";
        }
        return "La hizo quicksort(a, " + l.inicio() + ", " + l.fin() + ") para ordenar su parte "
                + (l.izquierda() ? "izquierda (los menores que su pivote)." : "derecha (los mayores que su pivote).");
    }

    /** Explica a dónde regresa la ejecución cuando una llamada de caso base hace return. */
    private static String regreso(Llamador l) {
        if (l == null) {
            return "Era la llamada inicial, así que el ordenamiento terminó.";
        }
        String quien = "quicksort(a, " + l.inicio() + ", " + l.fin() + ")";
        if (l.izquierda()) {
            return "El return devuelve el control a " + quien + ", que estaba detenida en su primera llamada"
                    + " recursiva (la parte izquierda). Ahora continúa con la segunda, quicksort(a, "
                    + (l.pivote() + 1) + ", " + l.fin() + "), para los mayores que su pivote"
                    + (l.pivote() + 1 > l.fin()
                    ? "; ese rango está vacío, así que regresa de inmediato y " + quien + " también termina."
                    : ".");
        }
        return "El return devuelve el control a " + quien + ", que estaba en su segunda llamada recursiva (la"
                + " parte derecha). Como ya no le queda nada por hacer, " + quien
                + " también termina y regresa a quien la llamó.";
    }

    /** Qué hace el for de la partición después de procesar la posición j. */
    private static String siguienteEnQuicksort(int j, int fin) {
        return j + 1 < fin
                ? "El for hace j++ (j = " + (j + 1) + "), consulta j < fin (" + (j + 1) + " < " + fin
                        + ", verdadero) y examina el siguiente elemento."
                : "El for hace j++ (j = " + fin + ") y consulta j < fin (" + fin + " < " + fin
                        + "): es FALSO, así que el for termina y falta ubicar el pivote.";
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
