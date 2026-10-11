package pe.edu.unaj.aed2.semana14.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fotografía inmutable de la mochila 0/1 con programación dinámica en un paso.
 *
 * @param dp          la tabla: dp[i][c] = mejor ganancia con los primeros i productos y capacidad c (-1 = sin calcular)
 * @param i           fila que se calcula o revisa (-1 si ninguna)
 * @param c           columna (capacidad) que se calcula o revisa (-1 si ninguna)
 * @param diagonal    columna de la casilla dp[i−1][c − peso] usada al LLEVAR el producto (-1 si no aplica)
 * @param elegidos    productos (índices desde 0) elegidos al reconstruir
 * @param recorrido   casillas "i,c" visitadas al reconstruir
 * @param tipo        qué ocurre
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param evaluacion  qué se evalúa en la línea actual y con qué valores
 * @param explicacion la misma evaluación en lenguaje natural, incluyendo a dónde va la ejecución
 * @param descripcion texto breve para la leyenda
 */
public record EstadoMochila(List<List<Integer>> dp, int i, int c, int diagonal, Set<Integer> elegidos,
                            Set<String> recorrido, Tipo tipo, int linea, Map<String, String> variables,
                            String evaluacion, String explicacion, String descripcion) implements PasoTrazable {

    public enum Tipo { INICIO, NO_CABE, NO_CONVIENE, LLEVA, RECONSTRUYE, FIN }

    public EstadoMochila {
        dp = dp.stream().map(List::copyOf).toList();
        elegidos = Set.copyOf(elegidos);
        recorrido = Set.copyOf(recorrido);
        variables = PasoTrazable.copiar(variables);
    }
}
