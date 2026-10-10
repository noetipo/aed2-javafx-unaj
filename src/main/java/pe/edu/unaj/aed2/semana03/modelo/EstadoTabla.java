package pe.edu.unaj.aed2.semana03.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.List;
import java.util.Map;

/**
 * Fotografía inmutable de la tabla hash en un paso de insertar o buscar.
 *
 * @param cubetas     claves de cada cubeta, en orden de la lista enlazada
 * @param clave       clave de la operación en curso
 * @param cubeta      cubeta que se está consultando (-1 si ninguna)
 * @param posicion    posición dentro de la cubeta que se compara o se agregó (-1 si ninguna)
 * @param tipo        qué ocurre en este paso
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param evaluacion  qué se evalúa en la línea actual y con qué valores
 * @param explicacion la misma evaluación en lenguaje natural, incluyendo a dónde va la ejecución
 * @param descripcion texto breve para la leyenda
 */
public record EstadoTabla(List<List<String>> cubetas, String clave, int cubeta, int posicion, Tipo tipo,
                          int linea, Map<String, String> variables, String evaluacion,
                          String explicacion, String descripcion) implements PasoTrazable {

    public enum Tipo { INICIO, HASH, CUBETA_VACIA, COMPARACION, ENCONTRADA, YA_EXISTE, NO_ENCONTRADA,
        AGREGADA, CARGA, REHASH, FIN }

    public EstadoTabla {
        cubetas = cubetas.stream().map(List::copyOf).toList();
        variables = PasoTrazable.copiar(variables);
    }

    public int capacidad() {
        return cubetas.size();
    }

    public int tamanio() {
        return cubetas.stream().mapToInt(List::size).sum();
    }
}
