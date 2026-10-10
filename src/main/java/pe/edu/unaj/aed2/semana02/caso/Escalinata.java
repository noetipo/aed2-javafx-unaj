package pe.edu.unaj.aed2.semana02.caso;

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

    /** Pasos de ejecución de {@code arbol}, que debe venir de {@link #arbolRecursivo} o {@link #arbolMemo}. */
    public static List<PasoLlamada> pasos(LlamadaRecursiva arbol, boolean conMemo) {
        Simulacion sim = new Simulacion(arbol.totalLlamadas(), conMemo);
        sim.agregar(-1, PasoLlamada.Tipo.INICIO, 0, "—",
                "Se llamará formas(" + arbol.n() + ")" + (conMemo ? " usando memorización." : "."));
        sim.visitar(arbol);
        sim.agregar(-1, PasoLlamada.Tipo.FIN, 0, String.valueOf(arbol.resultado()),
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

        void visitar(LlamadaRecursiva l) {
            llamadas++;
            pila.addLast("f" + l.n());
            estados[l.id()] = PasoLlamada.Estado.EN_PILA;
            agregar(l.id(), PasoLlamada.Tipo.LLAMADA, 1, "—", "Se llama formas(" + l.n() + ").");
            if (l.n() <= 1) {
                estados[l.id()] = PasoLlamada.Estado.RETORNADA;
                agregar(l.id(), PasoLlamada.Tipo.CASO_BASE, 2, "1",
                        "Caso base: formas(" + l.n() + ") retorna 1.");
            } else if (l.reusada()) {
                estados[l.id()] = PasoLlamada.Estado.REUSADA;
                agregar(l.id(), PasoLlamada.Tipo.REUSO, 3, String.valueOf(l.resultado()),
                        "formas(" + l.n() + ") ya está en memo: retorna " + l.resultado() + " sin recalcular.");
            } else {
                visitar(l.izquierda());
                visitar(l.derecha());
                if (conMemo) {
                    memo.put(l.n(), l.resultado());
                }
                estados[l.id()] = PasoLlamada.Estado.RETORNADA;
                agregar(l.id(), PasoLlamada.Tipo.RETORNO, conMemo ? 5 : 3, String.valueOf(l.resultado()),
                        "formas(" + l.n() + ") = " + l.izquierda().resultado() + " + "
                                + l.derecha().resultado() + " = " + l.resultado()
                                + (conMemo ? "; se guarda en memo." : "."));
            }
            pila.removeLast();
        }

        void agregar(int nodo, PasoLlamada.Tipo tipo, int linea, String retorno, String descripcion) {
            String n = nodo < 0 ? "—" : pila.isEmpty() ? "—" : pila.peekLast().substring(1);
            Map<String, String> variables = conMemo
                    ? PasoTrazable.variables("n", n, "retorna", retorno, "profundidad", pila.size(),
                    "llamadas", llamadas, "pila", String.join(" › ", pila), "memo", memo.toString())
                    : PasoTrazable.variables("n", n, "retorna", retorno, "profundidad", pila.size(),
                    "llamadas", llamadas, "pila", String.join(" › ", pila));
            pasos.add(new PasoLlamada(nodo, tipo, estados, new ArrayList<>(pila), linea, variables, descripcion));
        }
    }
}
