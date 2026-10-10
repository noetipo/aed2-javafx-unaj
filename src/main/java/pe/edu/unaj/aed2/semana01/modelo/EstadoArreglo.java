package pe.edu.unaj.aed2.semana01.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.Arrays;
import java.util.Map;

/**
 * Fotografía inmutable del arreglo en un instante del ordenamiento.
 *
 * @param valores     copia del arreglo en ese paso
 * @param a           primer índice involucrado (-1 si ninguno)
 * @param b           segundo índice involucrado (-1 si ninguno)
 * @param tipo        qué está ocurriendo en este paso
 * @param ordenados   cantidad de posiciones que ya están en su lugar definitivo
 * @param alFinal     si es true, las posiciones ordenadas son las últimas (burbuja);
 *                    si es false, son las primeras (inserción, selección)
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param evaluacion  qué se evalúa en la línea actual y con qué valores
 * @param explicacion la misma evaluación en lenguaje natural, incluyendo a dónde va la ejecución
 * @param descripcion texto explicativo para la leyenda
 */
public record EstadoArreglo(int[] valores, int a, int b, Tipo tipo, int ordenados, boolean alFinal,
                            int linea, Map<String, String> variables, String evaluacion,
                            String explicacion, String descripcion)
        implements PasoTrazable {

    public enum Tipo { INICIO, COMPARACION, INTERCAMBIO, FIN }

    public EstadoArreglo {
        valores = valores.clone();
        variables = PasoTrazable.copiar(variables);
    }

    @Override
    public int[] valores() {
        return valores.clone();
    }

    /** true si la posición {@code i} ya está en su lugar definitivo. */
    public boolean estaOrdenado(int i) {
        if (tipo == Tipo.FIN) {
            return true;
        }
        return alFinal ? i >= valores.length - ordenados : i < ordenados;
    }

    @Override
    public String toString() {
        return tipo + " " + Arrays.toString(valores) + " (" + a + ", " + b + ")";
    }
}
