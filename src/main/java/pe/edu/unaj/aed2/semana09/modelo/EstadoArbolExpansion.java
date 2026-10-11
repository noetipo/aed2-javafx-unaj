package pe.edu.unaj.aed2.semana09.modelo;

import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fotografía inmutable de Kruskal o Prim en un paso. Las aristas se identifican como "U-V"
 * ({@code EstadoRecorrido.arista}).
 *
 * @param arbol       tramos aceptados, en el orden en que entraron
 * @param descartadas tramos rechazados porque cerraban un ciclo
 * @param candidatas  tramos que esperan en la cola (solo Prim)
 * @param u           un extremo del tramo que se examina (null si ninguno)
 * @param v           el otro extremo (en Prim, el pueblo al que se llegaría)
 * @param grupos      número de grupo de cada pueblo, estable entre pasos; 0 = solo (solo Kruskal)
 * @param conectados  pueblos ya unidos al árbol (solo Prim)
 * @param pendientes  Kruskal: tramos que faltan revisar, en orden; Prim: la cola de menor a mayor. Como "(km, U-V)"
 * @param total       km acumulados del árbol
 * @param tipo        qué ocurre
 * @param linea       línea del pseudocódigo que se ejecuta (0 si ninguna)
 * @param variables   valores de las variables en este paso
 * @param evaluacion  qué se evalúa en la línea actual y con qué valores
 * @param explicacion la misma evaluación en lenguaje natural, incluyendo a dónde va la ejecución
 * @param descripcion texto breve para la leyenda
 */
public record EstadoArbolExpansion(List<String> arbol, Set<String> descartadas, Set<String> candidatas, String u,
                                   String v, Map<String, Integer> grupos, Set<String> conectados,
                                   List<String> pendientes, int total, Tipo tipo, int linea,
                                   Map<String, String> variables, String evaluacion, String explicacion,
                                   String descripcion) implements PasoTrazable {

    public enum Tipo { INICIO, ACEPTA, DESCARTA, INSERTA, OMITE, FIN }

    public EstadoArbolExpansion {
        arbol = List.copyOf(arbol);
        descartadas = Set.copyOf(descartadas);
        candidatas = Set.copyOf(candidatas);
        grupos = Map.copyOf(grupos);
        conectados = Set.copyOf(conectados);
        pendientes = List.copyOf(pendientes);
        variables = PasoTrazable.copiar(variables);
    }
}
