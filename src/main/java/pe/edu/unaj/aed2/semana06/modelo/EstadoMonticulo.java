package pe.edu.unaj.aed2.semana06.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.List;
import java.util.Map;

/**
 * Fotografía inmutable del arreglo de un montículo en un paso.
 *
 * @param valores     contenido del arreglo (como texto, para dibujar)
 * @param tamanio     cuántas posiciones forman el montículo; las demás ya están ordenadas (heapsort) o libres
 * @param i           posición del elemento que sube o baja (-1 si ninguna)
 * @param j           posición con la que se compara o intercambia (-1 si ninguna)
 * @param tipo        qué ocurre
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param evaluacion  qué se evalúa en la línea actual y con qué valores
 * @param explicacion la misma evaluación en lenguaje natural, incluyendo a dónde va la ejecución
 * @param descripcion texto breve para la leyenda
 */
public record EstadoMonticulo(List<String> valores, int tamanio, int i, int j, Tipo tipo, int linea,
                              Map<String, String> variables, String evaluacion, String explicacion,
                              String descripcion) implements PasoTrazable {

    public enum Tipo { INICIO, COLOCA, COMPARACION, INTERCAMBIO, TOMA_RAIZ, MUEVE_ULTIMO, HIJO, QUIETO,
        CONSTRUYE, UBICA_MAYOR, FIN }

    public EstadoMonticulo {
        valores = List.copyOf(valores);
        variables = PasoTrazable.copiar(variables);
    }

    /** Índice del padre de k en el arreglo: (k − 1) / 2. */
    public static int padre(int k) {
        return (k - 1) / 2;
    }
}
