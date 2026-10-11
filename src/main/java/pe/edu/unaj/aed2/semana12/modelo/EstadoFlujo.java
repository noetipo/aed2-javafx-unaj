package pe.edu.unaj.aed2.semana12.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fotografía inmutable de Edmonds–Karp en un paso. Los arcos se identifican como "U>V".
 *
 * @param flujo       flujo actual en cada arco de la red original
 * @param camino      camino de aumento encontrado (vértices de s a t); vacío si ninguno
 * @param haciaAtras  arcos del camino recorridos AL REVÉS (devuelven flujo), como "U>V" del arco original
 * @param cuello      arco del camino con menor residual (el cuello de botella), o null
 * @param ladoS       al final: vértices alcanzables desde s en la red residual (un lado del corte mínimo)
 * @param total       flujo total que sale de s
 * @param iteracion   número de camino de aumento (desde 1; 0 al inicio)
 * @param tipo        qué ocurre
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param evaluacion  qué se evalúa en la línea actual y con qué valores
 * @param explicacion la misma evaluación en lenguaje natural, incluyendo a dónde va la ejecución
 * @param descripcion texto breve para la leyenda
 */
public record EstadoFlujo(Map<String, Integer> flujo, List<String> camino, Set<String> haciaAtras, String cuello,
                          Set<String> ladoS, int total, int iteracion, Tipo tipo, int linea,
                          Map<String, String> variables, String evaluacion, String explicacion,
                          String descripcion) implements PasoTrazable {

    public enum Tipo { INICIO, CAMINO, CUELLO, AUMENTA, FIN }

    public EstadoFlujo {
        flujo = Map.copyOf(flujo);
        camino = List.copyOf(camino);
        haciaAtras = Set.copyOf(haciaAtras);
        ladoS = Set.copyOf(ladoS);
        variables = PasoTrazable.copiar(variables);
    }

    public static String arco(String u, String v) {
        return u + ">" + v;
    }
}
