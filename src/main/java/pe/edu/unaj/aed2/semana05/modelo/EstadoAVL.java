package pe.edu.unaj.aed2.semana05.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.Map;
import java.util.Set;

/**
 * Fotografía inmutable del árbol AVL en un paso de la inserción.
 *
 * @param raiz        copia del árbol, con la altura guardada en cada nodo (null si está vacío)
 * @param actual      id del nodo cuya llamada está activa (-1 si ninguno)
 * @param pila        ids de los nodos cuyas llamadas esperan en la pila
 * @param destacados  ids de los nodos protagonistas: el nuevo, el desequilibrado o los que rotan
 * @param tipo        qué ocurre
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param evaluacion  qué se evalúa en la línea actual y con qué valores
 * @param explicacion la misma evaluación en lenguaje natural, incluyendo a dónde va la ejecución
 * @param descripcion texto breve para la leyenda
 * @param <K>         tipo de las claves
 */
public record EstadoAVL<K>(NodoAVL<K> raiz, int actual, Set<Integer> pila, Set<Integer> destacados, Tipo tipo,
                           int linea, Map<String, String> variables, String evaluacion, String explicacion,
                           String descripcion) implements PasoTrazable {

    public enum Tipo { INICIO, COMPARACION, NUEVO, REPETIDO, ACTUALIZA, EQUILIBRADO, DESEQUILIBRIO, ROTACION, FIN }

    /** Nodo inmutable para dibujar, con su altura guardada. */
    public record NodoAVL<K>(int id, K clave, int altura, NodoAVL<K> izquierdo, NodoAVL<K> derecho) {

        /** Factor de equilibrio: altura(izquierdo) − altura(derecho). */
        public int factor() {
            return alturaDe(izquierdo) - alturaDe(derecho);
        }

        private static int alturaDe(NodoAVL<?> n) {
            return n == null ? 0 : n.altura();
        }
    }

    public EstadoAVL {
        pila = Set.copyOf(pila);
        destacados = Set.copyOf(destacados);
        variables = PasoTrazable.copiar(variables);
    }
}
