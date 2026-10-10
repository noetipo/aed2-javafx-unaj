package pe.edu.unaj.aed2.semana02.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.List;
import java.util.Map;

/**
 * Un paso en la ejecución de una función recursiva sobre su árbol de llamadas.
 *
 * @param nodo        id de la llamada protagonista del paso (-1 si ninguna)
 * @param tipo        qué ocurre
 * @param estados     estado de cada llamada del árbol, indexado por id
 * @param pila        pila de ejecución, desde la llamada más externa a la actual
 * @param linea       línea del pseudocódigo (0 si ninguna)
 * @param variables   valores visibles en este paso
 * @param descripcion texto para la leyenda
 */
public record PasoLlamada(int nodo, Tipo tipo, Estado[] estados, List<String> pila,
                          int linea, Map<String, String> variables, String descripcion)
        implements PasoTrazable {

    public enum Tipo { INICIO, LLAMADA, CASO_BASE, REUSO, RETORNO, FIN }

    /** Estado de una llamada en un instante dado. */
    public enum Estado { PENDIENTE, EN_PILA, RETORNADA, REUSADA }

    public PasoLlamada {
        estados = estados.clone();
        pila = List.copyOf(pila);
        variables = PasoTrazable.copiar(variables);
    }

    @Override
    public Estado[] estados() {
        return estados.clone();
    }

    public Estado estado(int id) {
        return estados[id];
    }
}
