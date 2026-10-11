package pe.edu.unaj.aed2.semana17.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.List;
import java.util.Map;

/**
 * Fotografía inmutable de una heurística del viajante en un paso.
 *
 * @param ruta        pueblos de la ruta en orden (empieza en el origen; el regreso al origen está implícito)
 * @param cerrada     true si la ruta ya vuelve al origen
 * @param actual      pueblo donde está el viajante (vecino más cercano) o null
 * @param candidato   pueblo examinado o elegido (null si ninguno)
 * @param tramoA      2-opt: primer tramo examinado como {desde, hasta} (null si ninguno)
 * @param tramoB      2-opt: segundo tramo examinado (null si ninguno)
 * @param km          largo de la ruta (con el regreso si está cerrada)
 * @param tipo        qué ocurre
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param evaluacion  qué se evalúa en la línea actual y con qué valores
 * @param explicacion la misma evaluación en lenguaje natural, incluyendo a dónde va la ejecución
 * @param descripcion texto breve para la leyenda
 */
public record EstadoViajante(List<String> ruta, boolean cerrada, String actual, String candidato, List<String> tramoA,
                             List<String> tramoB, int km, Tipo tipo, int linea, Map<String, String> variables,
                             String evaluacion, String explicacion, String descripcion) implements PasoTrazable {

    public enum Tipo { INICIO, ELIGE, CIERRA, REVISA, MEJORA, PASADA, FIN }

    public EstadoViajante {
        ruta = List.copyOf(ruta);
        tramoA = tramoA == null ? null : List.copyOf(tramoA);
        tramoB = tramoB == null ? null : List.copyOf(tramoB);
        variables = PasoTrazable.copiar(variables);
    }
}
