package pe.edu.unaj.aed2.semana01.modelo;

import java.util.Arrays;

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
 * @param descripcion texto explicativo para la leyenda
 */
public record EstadoArreglo(int[] valores, int a, int b, Tipo tipo,
                            int ordenados, boolean alFinal, String descripcion) {

    public enum Tipo { INICIO, COMPARACION, INTERCAMBIO, FIN }

    public EstadoArreglo {
        valores = valores.clone();
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
