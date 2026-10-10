package pe.edu.unaj.aed2.semana04.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fotografía inmutable del árbol en un paso de una operación.
 *
 * @param raiz        copia del árbol (null si está vacío)
 * @param actual      id del nodo que se está visitando (-1 si ninguno)
 * @param pila        ids de los nodos cuyas llamadas esperan en la pila (o el camino recorrido)
 * @param destacado   id del nodo protagonista: recién creado, encontrado, a eliminar o sucesor (-1 si ninguno)
 * @param tipo        qué ocurre
 * @param salida      claves emitidas por el recorrido (vacía en otras operaciones)
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param evaluacion  qué se evalúa en la línea actual y con qué valores
 * @param explicacion la misma evaluación en lenguaje natural, incluyendo a dónde va la ejecución
 * @param descripcion texto breve para la leyenda
 * @param <K>         tipo de las claves
 */
public record EstadoArbol<K>(NodoVista<K> raiz, int actual, Set<Integer> pila, int destacado, Tipo tipo,
                             List<K> salida, int linea, Map<String, String> variables, String evaluacion,
                             String explicacion, String descripcion) implements PasoTrazable {

    public enum Tipo { INICIO, VISITA, COMPARACION, NULO, ASIGNACION, RETORNO, ENCONTRADO, NO_ENCONTRADO,
        A_ELIMINAR, SUCESOR, REEMPLAZO, EMITE, FIN }

    /** Nodo inmutable para dibujar; {@code id} distingue nodos aunque compartan clave por un instante. */
    public record NodoVista<K>(int id, K clave, NodoVista<K> izquierdo, NodoVista<K> derecho) {
    }

    public EstadoArbol {
        pila = Set.copyOf(pila);
        salida = List.copyOf(salida);
        variables = PasoTrazable.copiar(variables);
    }
}
