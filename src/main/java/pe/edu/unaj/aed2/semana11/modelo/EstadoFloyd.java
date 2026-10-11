package pe.edu.unaj.aed2.semana11.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fotografía inmutable de Floyd–Warshall en un paso.
 *
 * @param vertices    ids de los vértices, en el orden de las filas y columnas
 * @param dist        matriz de distancias; {@link FloydWarshall#INF} = sin camino conocido
 * @param sig         siguiente parada en el camino de i a j (índice), o -1 si no hay camino
 * @param k           vértice intermedio que se está probando (-1 si ninguno)
 * @param i           fila examinada (-1 si ninguna)
 * @param j           columna examinada (-1 si ninguna)
 * @param mejoradas   casillas "i,j" que mejoraron con este k
 * @param tipo        qué ocurre
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param evaluacion  qué se evalúa en la línea actual y con qué valores
 * @param explicacion la misma evaluación en lenguaje natural, incluyendo a dónde va la ejecución
 * @param descripcion texto breve para la leyenda
 */
public record EstadoFloyd(List<String> vertices, List<List<Integer>> dist, List<List<Integer>> sig, int k, int i,
                          int j, Set<String> mejoradas, Tipo tipo, int linea, Map<String, String> variables,
                          String evaluacion, String explicacion, String descripcion) implements PasoTrazable {

    public enum Tipo { INICIO, NUEVO_K, MEJORA, NO_MEJORA, SIN_CAMINO, FIN }

    public EstadoFloyd {
        vertices = List.copyOf(vertices);
        dist = dist.stream().map(List::copyOf).toList();
        sig = sig.stream().map(List::copyOf).toList();
        mejoradas = Set.copyOf(mejoradas);
        variables = PasoTrazable.copiar(variables);
    }

    public static String casilla(int i, int j) {
        return i + "," + j;
    }
}
