package pe.edu.unaj.aed2.semana15.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana15.modelo.Huffman.Nodo;

import java.util.List;
import java.util.Map;

/**
 * Fotografía inmutable de la construcción del árbol de Huffman en un paso.
 *
 * @param bosque      árboles que hay en la cola de prioridad, de menor a mayor
 * @param a           primer árbol extraído (null si ninguno)
 * @param b           segundo árbol extraído (null si ninguno)
 * @param nuevo       árbol recién creado al unir a y b (null si ninguno)
 * @param codigos     códigos ya asignados (al final)
 * @param tipo        qué ocurre
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param evaluacion  qué se evalúa en la línea actual y con qué valores
 * @param explicacion la misma evaluación en lenguaje natural, incluyendo a dónde va la ejecución
 * @param descripcion texto breve para la leyenda
 */
public record EstadoHuffman(List<Nodo> bosque, Nodo a, Nodo b, Nodo nuevo, Map<Character, String> codigos, Tipo tipo,
                            int linea, Map<String, String> variables, String evaluacion, String explicacion,
                            String descripcion) implements PasoTrazable {

    public enum Tipo { FRECUENCIAS, HOJAS, UNE, RAIZ, CODIGOS }

    public EstadoHuffman {
        bosque = List.copyOf(bosque);
        codigos = Map.copyOf(codigos);
        variables = PasoTrazable.copiar(variables);
    }
}
