package pe.edu.unaj.aed2.semana07.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fotografía inmutable de un recorrido (BFS o DFS) en un paso.
 *
 * @param estados     estado de cada vértice
 * @param actual      vértice que se está procesando (null si ninguno)
 * @param vecino      vecino que se está examinando (null si ninguno)
 * @param arbol       aristas del árbol del recorrido, como "U-V" (por quién se descubrió cada vértice)
 * @param frontera    contenido de la cola (BFS, del frente al final) o de la pila de llamadas (DFS, de abajo arriba)
 * @param orden       vértices en el orden en que se visitaron
 * @param distancias  distancia en tramos desde el origen (solo BFS)
 * @param tipo        qué ocurre
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param evaluacion  qué se evalúa en la línea actual y con qué valores
 * @param explicacion la misma evaluación en lenguaje natural, incluyendo a dónde va la ejecución
 * @param descripcion texto breve para la leyenda
 */
public record EstadoRecorrido(Map<String, Estado> estados, String actual, String vecino, Set<String> arbol,
                              List<String> frontera, List<String> orden, Map<String, Integer> distancias, Tipo tipo,
                              int linea, Map<String, String> variables, String evaluacion, String explicacion,
                              String descripcion) implements PasoTrazable {

    /** Blanco: sin descubrir · Gris: descubierto, en la cola o en la pila · Negro: terminado. */
    public enum Estado { NO_VISITADO, EN_FRONTERA, TERMINADO }

    public enum Tipo { INICIO, SACA, VECINO_NUEVO, VECINO_VISTO, LLAMADA, REGRESA, FIN }

    public EstadoRecorrido {
        estados = Map.copyOf(estados);
        arbol = Set.copyOf(arbol);
        frontera = List.copyOf(frontera);
        orden = List.copyOf(orden);
        distancias = Map.copyOf(distancias);
        variables = PasoTrazable.copiar(variables);
    }

    /** Clave de una arista no dirigida, independiente del sentido. */
    public static String arista(String u, String v) {
        return u.compareTo(v) < 0 ? u + "-" + v : v + "-" + u;
    }
}
