package pe.edu.unaj.aed2.semana02.caso;

import pe.edu.unaj.aed2.semana02.modelo.LlamadaRecursiva;

import java.util.HashMap;
import java.util.Map;

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
        if (n <= 1) {
            return new LlamadaRecursiva(n, 1, false, null, null);
        }
        LlamadaRecursiva izq = arbolRecursivo(n - 1);
        LlamadaRecursiva der = arbolRecursivo(n - 2);
        return new LlamadaRecursiva(n, izq.resultado() + der.resultado(), false, izq, der);
    }

    /** Árbol de llamadas con memorización: las llamadas reusadas no tienen hijos. */
    public static LlamadaRecursiva arbolMemo(int n) {
        return arbolMemo(n, new HashMap<>());
    }

    private static LlamadaRecursiva arbolMemo(int n, Map<Integer, Long> memo) {
        if (n <= 1) {
            return new LlamadaRecursiva(n, 1, false, null, null);
        }
        Long guardado = memo.get(n);
        if (guardado != null) {
            return new LlamadaRecursiva(n, guardado, true, null, null);
        }
        LlamadaRecursiva izq = arbolMemo(n - 1, memo);
        LlamadaRecursiva der = arbolMemo(n - 2, memo);
        long r = izq.resultado() + der.resultado();
        memo.put(n, r);
        return new LlamadaRecursiva(n, r, false, izq, der);
    }
}
