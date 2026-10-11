package pe.edu.unaj.aed2.semana08.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fotografía inmutable de Dijkstra en un paso.
 *
 * @param distancias  mejor distancia conocida a cada vértice (ausente = ∞)
 * @param padres      por dónde se llega con esa distancia
 * @param cerrados    vértices cuya distancia ya es definitiva
 * @param actual      vértice que se está cerrando (null si ninguno)
 * @param vecino      vecino que se está examinando (null si ninguno)
 * @param cola        contenido de la cola de prioridad como "(km, v)", de menor a mayor
 * @param tipo        qué ocurre
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param evaluacion  qué se evalúa en la línea actual y con qué valores
 * @param explicacion la misma evaluación en lenguaje natural, incluyendo a dónde va la ejecución
 * @param descripcion texto breve para la leyenda
 */
public record EstadoDijkstra(Map<String, Integer> distancias, Map<String, String> padres, Set<String> cerrados,
                             String actual, String vecino, List<String> cola, Tipo tipo, int linea,
                             Map<String, String> variables, String evaluacion, String explicacion,
                             String descripcion) implements PasoTrazable {

    public enum Tipo { INICIO, EXTRAE, DESCARTA, MEJORA, NO_MEJORA, FIN }

    public EstadoDijkstra {
        distancias = Map.copyOf(distancias);
        padres = Map.copyOf(padres);
        cerrados = Set.copyOf(cerrados);
        cola = List.copyOf(cola);
        variables = PasoTrazable.copiar(variables);
    }
}
