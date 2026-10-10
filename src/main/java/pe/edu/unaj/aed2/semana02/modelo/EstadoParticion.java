package pe.edu.unaj.aed2.semana02.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.Arrays;
import java.util.Map;

/**
 * Fotografía inmutable de quicksort en un instante.
 *
 * @param valores     copia del arreglo
 * @param inicio      inicio del subarreglo que se está particionando (-1 si ninguno)
 * @param fin         fin del subarreglo (inclusive)
 * @param pivote      índice donde está el pivote (-1 si ninguno)
 * @param i           frontera de la zona "menores que el pivote" (-1 si no aplica)
 * @param j           índice que se está examinando (-1 si no aplica)
 * @param fijos       fijos[k] es true si la posición k ya está en su lugar definitivo
 * @param profundidad nivel de recursión de la llamada actual
 * @param tipo        qué está ocurriendo
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param descripcion texto para la leyenda
 */
public record EstadoParticion(int[] valores, int inicio, int fin, int pivote, int i, int j,
                              boolean[] fijos, int profundidad, Tipo tipo, int linea,
                              Map<String, String> variables, String descripcion) implements PasoTrazable {

    public enum Tipo { INICIO, LLAMADA, COMPARACION, INTERCAMBIO, PIVOTE_UBICADO, FIN }

    public EstadoParticion {
        valores = valores.clone();
        fijos = fijos.clone();
        variables = PasoTrazable.copiar(variables);
    }

    @Override
    public int[] valores() {
        return valores.clone();
    }

    @Override
    public boolean[] fijos() {
        return fijos.clone();
    }

    public boolean estaFijo(int k) {
        return fijos[k];
    }

    public boolean enRango(int k) {
        return inicio >= 0 && k >= inicio && k <= fin;
    }

    @Override
    public String toString() {
        return tipo + " " + Arrays.toString(valores) + " [" + inicio + ".." + fin + "] p=" + pivote;
    }
}
