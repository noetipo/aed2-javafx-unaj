package pe.edu.unaj.aed2.semana02.caso;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana02.modelo.LlamadaRecursiva;
import pe.edu.unaj.aed2.semana02.modelo.PasoLlamada;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * ¿De cuántas formas se puede subir una escalinata de n peldaños dando
 * pasos de 1 o 2 peldaños?
 * <p>
 * Recurrencia: formas(n) = formas(n-1) + formas(n-2), con formas(0) = formas(1) = 1.
 * Es la sucesión de Fibonacci desplazada. La versión recursiva directa repite
 * subproblemas y es O(φⁿ); con memorización cada subproblema se calcula una vez: O(n).
 */
public final class Escalinata {

    private Escalinata() {
    }

    /** Recursión directa (exponencial). */
    public static long formasRecursivo(int n) {
        if (n <= 1) {
            return 1;
        }
        return formasRecursivo(n - 1) + formasRecursivo(n - 2);
    }

    /** Recursión con memorización (lineal). */
    public static long formasMemo(int n) {
        return formasMemo(n, new HashMap<>());
    }

    private static long formasMemo(int n, Map<Integer, Long> memo) {
        if (n <= 1) {
            return 1;
        }
        Long guardado = memo.get(n);
        if (guardado != null) {
            return guardado;
        }
        long r = formasMemo(n - 1, memo) + formasMemo(n - 2, memo);
        memo.put(n, r);
        return r;
    }

    /** Número de llamadas que hace {@link #formasRecursivo(int)}: 2·formas(n) − 1. */
    public static long llamadasRecursivo(int n) {
        return n <= 1 ? 1 : 1 + llamadasRecursivo(n - 1) + llamadasRecursivo(n - 2);
    }

    /** Número de llamadas que hace {@link #formasMemo(int)}: 2n − 1 para n ≥ 1. */
    public static long llamadasMemo(int n) {
        return arbolMemo(n).totalLlamadas();
    }

    /** Árbol completo de llamadas de la versión recursiva directa. */
    public static LlamadaRecursiva arbolRecursivo(int n) {
        return arbol(n, null, new int[]{0});
    }

    /** Árbol de llamadas con memorización: las llamadas reusadas no tienen hijos. */
    public static LlamadaRecursiva arbolMemo(int n) {
        return arbol(n, new HashMap<>(), new int[]{0});
    }

    /** Construye el árbol numerando las llamadas en orden de ejecución; {@code memo} null = sin memoria. */
    private static LlamadaRecursiva arbol(int n, Map<Integer, Long> memo, int[] contador) {
        int id = contador[0]++;
        if (n <= 1) {
            return new LlamadaRecursiva(id, n, 1, false, null, null);
        }
        if (memo != null && memo.containsKey(n)) {
            return new LlamadaRecursiva(id, n, memo.get(n), true, null, null);
        }
        LlamadaRecursiva izq = arbol(n - 1, memo, contador);
        LlamadaRecursiva der = arbol(n - 2, memo, contador);
        long r = izq.resultado() + der.resultado();
        if (memo != null) {
            memo.put(n, r);
        }
        return new LlamadaRecursiva(id, n, r, false, izq, der);
    }

    // ------------------------------------------------------------------
    // Ejecución paso a paso (pila de llamadas)
    // ------------------------------------------------------------------

    public static final List<String> PSEUDO_RECURSIVO = List.of(
            "formas(n):",
            "    si n ≤ 1: retornar 1",
            "    retornar formas(n−1) + formas(n−2)");

    public static final List<String> PSEUDO_MEMO = List.of(
            "formas(n):",
            "    si n ≤ 1: retornar 1",
            "    si n está en memo: retornar memo[n]",
            "    r ← formas(n−1) + formas(n−2)",
            "    memo[n] ← r;  retornar r");

    public static final CodigoAlgoritmo CODIGO_RECURSIVO = new CodigoAlgoritmo(PSEUDO_RECURSIVO, """
            public static long formas(int n) {                 //@1
                if (n <= 1) {                                  //@2
                    return 1;                                  //@2
                }
                return formas(n - 1) + formas(n - 2);          //@3
            }
            """);

    public static final CodigoAlgoritmo CODIGO_MEMO = new CodigoAlgoritmo(PSEUDO_MEMO, """
            private static final Map<Integer, Long> memo = new HashMap<>();

            public static long formas(int n) {                 //@1
                if (n <= 1) {                                  //@2
                    return 1;                                  //@2
                }
                if (memo.containsKey(n)) {                     //@3
                    return memo.get(n);                        //@3
                }
                long r = formas(n - 1) + formas(n - 2);        //@4
                memo.put(n, r);                                //@5
                return r;                                      //@5
            }
            """);

    /** Pasos de ejecución de {@code arbol}, que debe venir de {@link #arbolRecursivo} o {@link #arbolMemo}. */
    public static List<PasoLlamada> pasos(LlamadaRecursiva arbol, boolean conMemo) {
        Simulacion sim = new Simulacion(arbol.totalLlamadas(), conMemo);
        sim.agregar(-1, PasoLlamada.Tipo.INICIO, 0, "—", "formas(" + arbol.n() + ")",
                "Se llamará formas(" + arbol.n() + ") para contar de cuántas formas se suben " + arbol.n()
                        + " peldaños. Cada llamada que no sea caso base se pausa para pedir dos resultados más"
                        + " pequeños, así que observa cómo crece y se vacía la pila de ejecución"
                        + (conMemo ? ", y cómo memo evita repetir cálculos." : "."),
                "Se llamará formas(" + arbol.n() + ")" + (conMemo ? " usando memorización." : "."));
        sim.visitar(arbol, null, false);
        sim.agregar(-1, PasoLlamada.Tipo.FIN, 0, String.valueOf(arbol.resultado()),
                "formas(" + arbol.n() + ") retorna " + arbol.resultado(),
                "La pila quedó vacía: todas las llamadas regresaron. formas(" + arbol.n() + ") = "
                        + arbol.resultado() + ", es decir, hay " + arbol.resultado() + " formas de subir "
                        + arbol.n() + " peldaños. Se hicieron " + sim.llamadas + " llamadas en total"
                        + (conMemo ? " (sin memo serían " + llamadasRecursivo(arbol.n()) + ")." : "."),
                "formas(" + arbol.n() + ") = " + arbol.resultado() + " con " + sim.llamadas + " llamadas.");
        return sim.pasos;
    }

    private static final class Simulacion {
        final PasoLlamada.Estado[] estados;
        final boolean conMemo;
        final Deque<String> pila = new ArrayDeque<>();
        final Map<Integer, Long> memo = new TreeMap<>();
        final List<PasoLlamada> pasos = new ArrayList<>();
        int llamadas;

        Simulacion(int total, boolean conMemo) {
            this.estados = new PasoLlamada.Estado[total];
            Arrays.fill(estados, PasoLlamada.Estado.PENDIENTE);
            this.conMemo = conMemo;
        }

        /**
         * @param padre     llamada que hizo esta, o null si es la inicial
         * @param izquierda true si es el primer sumando del padre, formas(n − 1)
         */
        void visitar(LlamadaRecursiva l, LlamadaRecursiva padre, boolean izquierda) {
            llamadas++;
            pila.addLast("f" + l.n());
            estados[l.id()] = PasoLlamada.Estado.EN_PILA;
            String origen = padre == null
                    ? "Empieza formas(" + l.n() + "), la llamada inicial."
                    : "formas(" + padre.n() + ") necesita formas(" + (padre.n() - 1) + ") + formas("
                            + (padre.n() - 2) + "). Para obtener el " + (izquierda ? "primer" : "segundo")
                            + " sumando se pausa en esa línea y llama a formas(" + l.n() + ").";
            String plan;
            if (l.n() <= 1) {
                plan = " Como n = " + l.n() + ", esta llamada será un caso base.";
            } else if (l.reusada()) {
                plan = " Su resultado ya está en memo, así que no hará más llamadas.";
            } else {
                plan = " Como n = " + l.n() + " no es caso base" + (conMemo ? " ni está en memo" : "")
                        + ", tendrá que llamar a formas(" + (l.n() - 1) + ") y luego a formas(" + (l.n() - 2) + ").";
            }
            agregar(l.id(), PasoLlamada.Tipo.LLAMADA, 1, "—", "n = " + l.n(),
                    origen + " Se apila un nuevo marco con n = " + l.n() + "; la pila queda: "
                            + String.join(" › ", pila) + "." + plan,
                    "Se llama formas(" + l.n() + ").");
            if (l.n() <= 1) {
                estados[l.id()] = PasoLlamada.Estado.RETORNADA;
                agregar(l.id(), PasoLlamada.Tipo.CASO_BASE, 2, "1",
                        "n <= 1  →  " + l.n() + " <= 1  →  verdadero  →  return 1",
                        "El if consulta n <= 1: con n = " + l.n() + " es VERDADERO. Es el caso base: con "
                                + l.n() + " peldaño" + (l.n() == 1 ? "" : "s") + " hay exactamente una forma, así"
                                + " que formas(" + l.n() + ") devuelve 1 sin hacer más llamadas. "
                                + regreso(padre, izquierda),
                        "Caso base: formas(" + l.n() + ") retorna 1.");
            } else if (l.reusada()) {
                estados[l.id()] = PasoLlamada.Estado.REUSADA;
                agregar(l.id(), PasoLlamada.Tipo.REUSO, 3, String.valueOf(l.resultado()),
                        "n <= 1  →  " + l.n() + " <= 1  →  falso;  memo.containsKey(" + l.n()
                                + ")  →  verdadero  →  return memo.get(" + l.n() + ") = " + l.resultado(),
                        "Primero se consulta n <= 1: " + l.n() + " <= 1 es FALSO, no es caso base. Luego se"
                                + " consulta memo.containsKey(" + l.n() + "): es VERDADERO, porque formas(" + l.n()
                                + ") ya se calculó antes en otra rama. Se devuelve memo.get(" + l.n() + ") = "
                                + l.resultado() + " sin repetir ninguna llamada: aquí está el ahorro de la"
                                + " memorización. " + regreso(padre, izquierda),
                        "formas(" + l.n() + ") ya está en memo: retorna " + l.resultado() + " sin recalcular.");
            } else {
                visitar(l.izquierda(), l, true);
                visitar(l.derecha(), l, false);
                if (conMemo) {
                    memo.put(l.n(), l.resultado());
                }
                estados[l.id()] = PasoLlamada.Estado.RETORNADA;
                long a = l.izquierda().resultado();
                long b = l.derecha().resultado();
                String suma = "formas(" + (l.n() - 1) + ") + formas(" + (l.n() - 2) + ")  →  "
                        + a + " + " + b + " = " + l.resultado();
                agregar(l.id(), PasoLlamada.Tipo.RETORNO, conMemo ? 5 : 3, String.valueOf(l.resultado()),
                        conMemo ? "r = " + suma + ";  memo.put(" + l.n() + ", " + l.resultado() + ");  return "
                                + l.resultado() : "return " + suma,
                        "formas(" + l.n() + ") ya recibió sus dos resultados: formas(" + (l.n() - 1) + ") = " + a
                                + " y formas(" + (l.n() - 2) + ") = " + b + ". Los suma: " + a + " + " + b + " = "
                                + l.resultado() + ". "
                                + (conMemo ? "Guarda memo[" + l.n() + "] = " + l.resultado() + " para no volver a"
                                        + " calcularlo y " : "")
                                + "devuelve " + l.resultado() + "; su marco se quita de la pila. "
                                + regreso(padre, izquierda),
                        "formas(" + l.n() + ") = " + a + " + " + b + " = " + l.resultado()
                                + (conMemo ? "; se guarda en memo." : "."));
            }
            pila.removeLast();
        }

        /** A dónde vuelve la ejecución cuando una llamada termina. */
        private static String regreso(LlamadaRecursiva padre, boolean izquierda) {
            if (padre == null) {
                return "Era la llamada inicial, así que este es el resultado final.";
            }
            return izquierda
                    ? "El control regresa a formas(" + padre.n() + "), que estaba esperando su primer sumando,"
                            + " formas(" + (padre.n() - 1) + "). Con ese valor guardado, ahora llama a formas("
                            + (padre.n() - 2) + ")."
                    : "El control regresa a formas(" + padre.n() + "), que estaba esperando su segundo sumando,"
                            + " formas(" + (padre.n() - 2) + "). Ya tiene los dos valores y puede sumarlos.";
        }

        void agregar(int nodo, PasoLlamada.Tipo tipo, int linea, String retorno, String evaluacion,
                     String explicacion, String descripcion) {
            String n = nodo < 0 ? "—" : pila.isEmpty() ? "—" : pila.peekLast().substring(1);
            Map<String, String> variables = conMemo
                    ? PasoTrazable.variables("n", n, "retorna", retorno, "profundidad", pila.size(),
                    "llamadas", llamadas, "pila", String.join(" › ", pila), "memo", memo.toString())
                    : PasoTrazable.variables("n", n, "retorna", retorno, "profundidad", pila.size(),
                    "llamadas", llamadas, "pila", String.join(" › ", pila));
            pasos.add(new PasoLlamada(nodo, tipo, estados, new ArrayList<>(pila), linea, variables, evaluacion,
                    explicacion, descripcion));
        }
    }
}
