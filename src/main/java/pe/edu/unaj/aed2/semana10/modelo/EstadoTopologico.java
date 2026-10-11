package pe.edu.unaj.aed2.semana10.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana07.modelo.EstadoRecorrido.Estado;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fotografía inmutable del orden topológico (Kahn o DFS) en un paso. Los arcos se identifican como "U>V".
 *
 * @param estados     NO_VISITADO (blanco), EN_FRONTERA (gris: en la cola de Kahn o en la pila de llamadas de DFS)
 *                    y TERMINADO (ya está en el orden)
 * @param grados      Kahn: prerrequisitos pendientes de cada vértice (grado de entrada); vacío en DFS
 * @param frontera    Kahn: la cola, del frente al final; DFS: la pila de llamadas, de abajo arriba
 * @param orden       el orden topológico construido hasta ahora
 * @param actual      vértice que se procesa (null si ninguno)
 * @param vecino      vértice al que apunta el arco examinado (null si ninguno)
 * @param arcosHechos Kahn: arcos ya "quitados"; DFS: arcos por los que se llamó a visitar
 * @param arcoCiclo   arco que demuestra un ciclo (null si no hay)
 * @param tipo        qué ocurre
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param evaluacion  qué se evalúa en la línea actual y con qué valores
 * @param explicacion la misma evaluación en lenguaje natural, incluyendo a dónde va la ejecución
 * @param descripcion texto breve para la leyenda
 */
public record EstadoTopologico(Map<String, Estado> estados, Map<String, Integer> grados, List<String> frontera,
                               List<String> orden, String actual, String vecino, Set<String> arcosHechos,
                               String arcoCiclo, Tipo tipo, int linea, Map<String, String> variables,
                               String evaluacion, String explicacion, String descripcion) implements PasoTrazable {

    public enum Tipo { INICIO, SACA, DECREMENTA, ENCOLA, LLAMA, VISITA, YA_TERMINADO, TERMINA, CICLO, FIN }

    public EstadoTopologico {
        estados = Map.copyOf(estados);
        grados = Map.copyOf(grados);
        frontera = List.copyOf(frontera);
        orden = List.copyOf(orden);
        arcosHechos = Set.copyOf(arcosHechos);
        variables = PasoTrazable.copiar(variables);
    }

    /** Clave de un arco dirigido u → v. */
    public static String arco(String u, String v) {
        return u + ">" + v;
    }
}
