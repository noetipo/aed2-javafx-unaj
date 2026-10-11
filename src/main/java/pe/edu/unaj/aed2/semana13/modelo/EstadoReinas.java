package pe.edu.unaj.aed2.semana13.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.List;
import java.util.Map;

/**
 * Fotografía inmutable de N reinas con backtracking en un paso.
 *
 * @param tablero     columna de la reina de cada fila (-1 si la fila está vacía)
 * @param fila        fila que se está resolviendo (-1 si ninguna)
 * @param col         columna que se prueba (-1 si ninguna)
 * @param atacante    fila de la reina que ataca la casilla probada (-1 si ninguna)
 * @param intentos    casillas probadas hasta ahora (para medir el trabajo)
 * @param tipo        qué ocurre
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param evaluacion  qué se evalúa en la línea actual y con qué valores
 * @param explicacion la misma evaluación en lenguaje natural, incluyendo a dónde va la ejecución
 * @param descripcion texto breve para la leyenda
 */
public record EstadoReinas(List<Integer> tablero, int fila, int col, int atacante, int intentos, Tipo tipo, int linea,
                           Map<String, String> variables, String evaluacion, String explicacion,
                           String descripcion) implements PasoTrazable {

    public enum Tipo { INICIO, ATACADA, COLOCA, QUITA, RETROCEDE, SOLUCION }

    public EstadoReinas {
        tablero = List.copyOf(tablero);
        variables = PasoTrazable.copiar(variables);
    }
}
