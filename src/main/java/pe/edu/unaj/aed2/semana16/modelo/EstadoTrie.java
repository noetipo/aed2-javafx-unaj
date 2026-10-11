package pe.edu.unaj.aed2.semana16.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fotografía inmutable de un trie en un paso. Cada nodo se identifica por el prefijo que representa ("" es la raíz,
 * "pu" es el nodo al que se llega leyendo p y luego u).
 *
 * @param nodos       prefijos de todos los nodos que existen
 * @param finales     prefijos que son una palabra completa
 * @param actual      nodo donde está la ejecución (null si ninguno)
 * @param nuevo       nodo recién creado (null si ninguno)
 * @param palabra     palabra que se inserta o prefijo que se busca
 * @param resultados  palabras encontradas al autocompletar
 * @param tipo        qué ocurre
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param evaluacion  qué se evalúa en la línea actual y con qué valores
 * @param explicacion la misma evaluación en lenguaje natural, incluyendo a dónde va la ejecución
 * @param descripcion texto breve para la leyenda
 */
public record EstadoTrie(Set<String> nodos, Set<String> finales, String actual, String nuevo, String palabra,
                         List<String> resultados, Tipo tipo, int linea, Map<String, String> variables,
                         String evaluacion, String explicacion, String descripcion) implements PasoTrazable {

    public enum Tipo { INICIO, CREA, BAJA, MARCA, NO_EXISTE, RECOLECTA, FIN }

    public EstadoTrie {
        nodos = Set.copyOf(nodos);
        finales = Set.copyOf(finales);
        resultados = List.copyOf(resultados);
        variables = PasoTrazable.copiar(variables);
    }
}
