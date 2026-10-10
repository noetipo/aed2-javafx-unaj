package pe.edu.unaj.aed2.semana04.tarea;

import java.util.List;

/**
 * TAREA SEMANA 04 — recursión sobre árboles binarios. Completa los métodos marcados con TODO.
 * <p>
 * Reglas:
 * <ul>
 *   <li>Las soluciones deben ser RECURSIVAS: caso base {@code r == null} y llamadas sobre r.izq y r.der.</li>
 *   <li>No cambies las firmas ni la clase Nodo: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-04/tarea.md
 */
public final class TareaSemana04 {

    /** Nodo de árbol binario (no necesariamente de búsqueda). */
    public static class Nodo {
        public int clave;
        public Nodo izq;
        public Nodo der;

        public Nodo(int clave, Nodo izq, Nodo der) {
            this.clave = clave;
            this.izq = izq;
            this.der = der;
        }

        public Nodo(int clave) {
            this(clave, null, null);
        }
    }

    private TareaSemana04() {
    }

    /** Altura en niveles: árbol vacío = 0, un solo nodo = 1. */
    public static int altura(Nodo r) {
        // TODO 1: si r es null, 0; si no, 1 + el mayor de las alturas de sus dos subárboles.
        throw new UnsupportedOperationException("TODO: implementar altura");
    }

    /** Cantidad de hojas (nodos sin hijos). */
    public static int contarHojas(Nodo r) {
        // TODO 2: tres casos: árbol vacío, r es hoja, o suma de las hojas de cada lado.
        throw new UnsupportedOperationException("TODO: implementar contarHojas");
    }

    /** Recorrido POSTORDEN (izquierdo, derecho, raíz): agrega las claves a {@code salida}. */
    public static void postorden(Nodo r, List<Integer> salida) {
        // TODO 3: como inorden del visualizador, pero la raíz va al final.
        throw new UnsupportedOperationException("TODO: implementar postorden");
    }

    /**
     * true si el árbol cumple la propiedad de ABB: TODA clave del subárbol izquierdo es menor que la
     * del nodo y TODA clave del derecho es mayor (no basta con mirar los hijos directos).
     */
    public static boolean esABB(Nodo r) {
        // TODO 4: usa un método auxiliar esABB(r, minimo, maximo) que reciba el rango permitido:
        //         a la izquierda el máximo pasa a ser r.clave; a la derecha, el mínimo.
        throw new UnsupportedOperationException("TODO: implementar esABB");
    }
}
