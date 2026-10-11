package pe.edu.unaj.aed2.semana06.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana06.modelo.EstadoMonticulo.Tipo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Heapsort: ordena de menor a mayor dentro del mismo arreglo. Primero lo convierte en un montículo
 * de MÁXIMOS (de abajo hacia arriba, en O(n)); luego, n − 1 veces, lleva la raíz (el mayor) al final
 * de la zona sin ordenar y hunde la nueva raíz. Total: O(n log n), sin memoria extra.
 */
public final class Heapsort {

    private Heapsort() {
    }

    public static int[] ordenar(int[] arreglo) {
        int[] a = arreglo.clone();
        int n = a.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            hundir(a, i, n);
        }
        for (int fin = n - 1; fin > 0; fin--) {
            intercambiar(a, 0, fin);
            hundir(a, 0, fin);
        }
        return a;
    }

    private static void hundir(int[] a, int i, int n) {
        while (2 * i + 1 < n) {
            int m = 2 * i + 1;
            if (m + 1 < n && a[m + 1] > a[m]) {
                m++;
            }
            if (a[i] >= a[m]) {
                break;
            }
            intercambiar(a, i, m);
            i = m;
        }
    }

    private static void intercambiar(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    public static final List<String> PSEUDOCODIGO = List.of(
            "heapsort(a):",
            "    para i ← n/2 − 1 bajando hasta 0: hundir(a, i, n)     // construir montículo de máximos",
            "    para fin ← n − 1 bajando hasta 1:",
            "        intercambiar a[0] y a[fin]                       // el mayor va a su lugar final",
            "        hundir(a, 0, fin)                                // reparar el montículo restante",
            "hundir(a, i, n):",
            "    mientras 2i + 1 < n:",
            "        m ← el hijo MAYOR de i",
            "        si a[i] ≥ a[m]: salir del mientras",
            "        intercambiar a[i] y a[m];  i ← m");

    public static final CodigoAlgoritmo CODIGO = CodigoAlgoritmo.seccion(PSEUDOCODIGO, MonticuloMin.JAVA, "heapsort");

    // ------------------------------------------------------------------
    // Paso a paso
    // ------------------------------------------------------------------

    private static final class Grabador {
        final int[] a;
        final List<EstadoMonticulo> pasos = new ArrayList<>();
        int tamanio;
        String fase = "construir";

        Grabador(int[] a) {
            this.a = a;
            this.tamanio = a.length;
        }

        void paso(int i, int j, Tipo tipo, int linea, Map<String, String> variables, String evaluacion,
                  String explicacion, String descripcion) {
            pasos.add(new EstadoMonticulo(Arrays.stream(a).mapToObj(String::valueOf).toList(), tamanio, i, j, tipo,
                    linea, variables, evaluacion, explicacion, descripcion));
        }

        Map<String, String> variables(int i, int m) {
            return PasoTrazable.variables("fase", fase, "n", tamanio, "i", i < 0 ? "—" : i, "m", m < 0 ? "—" : m,
                    "a[i]", i < 0 ? "—" : a[i], "a[m]", m < 0 ? "—" : a[m]);
        }
    }

    public static List<EstadoMonticulo> pasos(int[] original) {
        Grabador g = new Grabador(original.clone());
        int[] a = g.a;
        int n = a.length;
        g.paso(-1, -1, Tipo.INICIO, 0, g.variables(-1, -1), "heapsort(" + Arrays.toString(a) + ")",
                "Se llama heapsort sobre " + n + " elementos. Vista como árbol completo, la posición k tiene a sus hijos"
                        + " en 2k + 1 y 2k + 2. Fase 1: convertir el arreglo en un montículo de MÁXIMOS. Fase 2: sacar"
                        + " el mayor una y otra vez y dejarlo al final.",
                "heapsort");
        for (int i = n / 2 - 1; i >= 0; i--) {
            g.paso(i, -1, Tipo.CONSTRUYE, 2, g.variables(i, -1), "hundir(a, " + i + ", " + n + ")",
                    (i == n / 2 - 1 ? "Las posiciones desde n/2 = " + (n / 2) + " en adelante son hojas, que ya son"
                            + " montículos de un elemento. Por eso el for empieza en n/2 − 1 = " + i + ", el último nodo"
                            + " con hijos, y va hacia la raíz. " : "El for baja i a " + i + ". ")
                            + "Se llama hundir(a, " + i + ", " + n + ") para que el subárbol que cuelga de a[" + i + "] = "
                            + a[i] + " sea un montículo de máximos (sus subárboles hijos ya lo son).",
                    "Construir: hundir desde a[" + i + "].");
            hundir(g, i, n, "for de construcción", i > 0 ? "baja i a " + (i - 1) : "termina: la fase 1 está lista");
        }
        g.fase = "ordenar";
        for (int fin = n - 1; fin > 0; fin--) {
            int mayor = a[0];
            int otro = a[fin];
            intercambiar(a, 0, fin);
            g.tamanio = fin;
            g.paso(0, fin, Tipo.UBICA_MAYOR, 4, g.variables(0, -1),
                    "intercambiar(a, 0, " + fin + ")  →  a[" + fin + "] = " + mayor,
                    (fin == n - 1 ? "El arreglo ya es un montículo de máximos: el mayor está en a[0]. Empieza la fase 2"
                            + " con fin = " + fin + ". " : "El for baja fin a " + fin + ". ")
                            + "Se intercambian a[0] y a[" + fin + "]: el mayor que queda, " + mayor + ", pasa a la"
                            + " posición " + fin + ", que es su lugar DEFINITIVO (verde), y " + otro + " sube a la raíz."
                            + (fin == 1 ? " Queda un montículo de un solo elemento, que ya es válido (es el menor"
                            + " de todos); igual se llama hundir(a, 0, 1), que no tendrá nada que hacer."
                            : " El montículo se achica a " + fin + " elementos, pero " + otro + " probablemente rompe"
                            + " la regla arriba: se llama hundir(a, 0, " + fin + ")."),
                    mayor + " queda fijo en a[" + fin + "].");
            hundir(g, 0, fin, "for de ordenamiento", fin - 1 > 0 ? "baja fin a " + (fin - 1) : "termina");
        }
        g.tamanio = 0;
        g.paso(-1, -1, Tipo.FIN, 0, g.variables(-1, -1), "return " + Arrays.toString(a),
                "El for terminó: todas las posiciones quedaron en su lugar y heapsort devuelve "
                        + Arrays.toString(a) + ". Construir costó O(n) y cada una de las n − 1 extracciones O(log n):"
                        + " en total O(n log n), siempre, y sin usar otro arreglo.",
                "Ordenado.");
        return g.pasos;
    }

    /** hundir(a, i, n) registrando pasos; {@code quien} y {@code luego} explican a dónde vuelve el control. */
    private static void hundir(Grabador g, int i, int n, String quien, String luego) {
        int[] a = g.a;
        while (true) {
            if (2 * i + 1 >= n) {
                g.paso(i, -1, Tipo.QUIETO, 7, g.variables(i, -1),
                        "2i + 1 < n  →  " + (2 * i + 1) + " < " + n + "  →  falso",
                        "El while de hundir consulta si i = " + i + " tiene hijos dentro del montículo: 2·" + i
                                + " + 1 = " + (2 * i + 1) + " no es < n = " + n + ". " + a[i] + " llegó al fondo. hundir"
                                + " termina y regresa al " + quien + ", que " + luego + ".",
                        a[i] + " llegó al fondo.");
                return;
            }
            int izq = 2 * i + 1;
            boolean hayDer = izq + 1 < n;
            int m = hayDer && a[izq + 1] > a[izq] ? izq + 1 : izq;
            g.paso(i, m, Tipo.HIJO, 8, g.variables(i, m),
                    "m = " + (hayDer ? "mayor(a[" + izq + "] = " + a[izq] + ", a[" + (izq + 1) + "] = " + a[izq + 1] + ")"
                            : "a[" + izq + "] (único hijo)") + "  →  m = " + m,
                    "El while de hundir consulta 2i + 1 < n: " + izq + " < " + n + " es verdadero. "
                            + (hayDer ? a[i] + " tiene dos hijos, a[" + izq + "] = " + a[izq] + " y a[" + (izq + 1)
                            + "] = " + a[izq + 1] + "; se elige el MAYOR, " + a[m] + ", porque en un montículo de"
                            + " máximos el que sube tiene que ser mayor que su hermano."
                            : "Solo tiene hijo izquierdo, a[" + izq + "] = " + a[izq] + "."),
                    "Hijo mayor de " + a[i] + ": " + a[m] + ".");
            boolean quieto = a[i] >= a[m];
            g.paso(i, m, quieto ? Tipo.QUIETO : Tipo.COMPARACION, 9, g.variables(i, m),
                    "a[i] >= a[m]  →  " + a[i] + " >= " + a[m] + "  →  " + PasoTrazable.vf(quieto),
                    "El if consulta si a[i] = " + a[i] + " es ≥ que su hijo mayor a[m] = " + a[m] + ". "
                            + (quieto ? "Sí: ya está en su lugar. break sale del while, hundir termina y regresa al "
                            + quien + ", que " + luego + "."
                            : "No: es menor que su hijo, hay que bajarlo."),
                    quieto ? a[i] + " ≥ " + a[m] + ": se queda." : a[i] + " < " + a[m] + ": baja.");
            if (quieto) {
                return;
            }
            int vi = a[i];
            int vm = a[m];
            intercambiar(a, i, m);
            g.paso(m, i, Tipo.INTERCAMBIO, 10, g.variables(m, -1),
                    "intercambiar(a, " + i + ", " + m + ");  i = " + m,
                    "Se intercambian: " + vm + " sube a la posición " + i + " y " + vi + " baja a la " + m + ". Luego"
                            + " i = m = " + m + " y el while vuelve a consultar.",
                    vi + " baja a a[" + m + "].");
            i = m;
        }
    }
}
