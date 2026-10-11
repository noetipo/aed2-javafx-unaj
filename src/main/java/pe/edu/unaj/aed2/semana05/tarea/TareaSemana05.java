package pe.edu.unaj.aed2.semana05.tarea;

/**
 * TAREA SEMANA 05 — piezas de un árbol AVL. Completa los métodos marcados con TODO.
 * <p>
 * Reglas:
 * <ul>
 *   <li>Las rotaciones deben DEVOLVER la nueva raíz del subárbol y dejar actualizadas las alturas.</li>
 *   <li>No cambies las firmas ni la clase Nodo: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-05/tarea.md
 */
public final class TareaSemana05 {

    /** Nodo de AVL: guarda su altura (una hoja tiene altura 1). */
    public static class Nodo {
        public int clave;
        public int altura = 1;
        public Nodo izq;
        public Nodo der;

        public Nodo(int clave) {
            this.clave = clave;
        }

        public Nodo(int clave, Nodo izq, Nodo der) {
            this.clave = clave;
            this.izq = izq;
            this.der = der;
            this.altura = 1 + Math.max(alturaGuardada(izq), alturaGuardada(der));
        }
    }

    private TareaSemana05() {
    }

    /** Ya implementado: altura guardada en el nodo (0 si es null). */
    public static int alturaGuardada(Nodo n) {
        return n == null ? 0 : n.altura;
    }

    /** altura(izq) − altura(der), usando las alturas guardadas. Un árbol vacío tiene factor 0. */
    public static int factorEquilibrio(Nodo n) {
        // TODO 1
        throw new UnsupportedOperationException("TODO: implementar factorEquilibrio");
    }

    /**
     * Rotación simple a la derecha sobre y: su hijo izquierdo x sube. Actualiza las alturas de y y
     * de x (en ese orden: y queda abajo) y devuelve x.
     */
    public static Nodo rotarDerecha(Nodo y) {
        // TODO 2: x = y.izq; y.izq = x.der; x.der = y; actualizar alturas; retornar x.
        throw new UnsupportedOperationException("TODO: implementar rotarDerecha");
    }

    /** Rotación simple a la izquierda sobre x: su hijo derecho sube. Simétrica a rotarDerecha. */
    public static Nodo rotarIzquierda(Nodo x) {
        // TODO 3
        throw new UnsupportedOperationException("TODO: implementar rotarIzquierda");
    }

    /**
     * true si el árbol es un AVL válido: es un ABB (como en la semana 04), cada nodo cumple
     * |altura real(izq) − altura real(der)| ≤ 1, y la altura guardada en cada nodo es la real.
     */
    public static boolean esAVL(Nodo r) {
        // TODO 4: conviene un auxiliar que calcule la altura real y devuelva -1 si encuentra un problema.
        throw new UnsupportedOperationException("TODO: implementar esAVL");
    }
}
