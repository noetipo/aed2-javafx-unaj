package pe.edu.unaj.aed2.semana12.caso;

import pe.edu.unaj.aed2.semana12.modelo.RedFlujo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Caso práctico semana 12: tres cuencas lecheras (Ayaviri, Azángaro y Putina) envían leche en camiones a la planta
 * de Juliaca. Con varias fuentes se agrega una FUENTE FICTICIA que las alimenta, con un arco por cuenca cuya
 * capacidad es su producción. Así sigue siendo un problema de flujo máximo de una fuente a un destino.
 */
public class CuencaLechera {

    /** Vértice ficticio que reparte la producción a las cuencas (no está en el mapa). */
    public static final String FUENTE = "FUENTE";
    public static final String PLANTA = "JUL";
    public static final List<String> CUENCAS = List.of("AYA", "AZA", "PUT");

    /** Resultado de un día: cuánto llega y por dónde. */
    public record Envio(int producido, int entregado, Map<String, Integer> porCuenca, RedFlujo.Resultado flujo) {
        public int perdido() {
            return producido - entregado;
        }
    }

    /** Qué pasa si se amplía un tramo. */
    public record Ampliacion(RedFlujo.Arco tramo, int entregado, int ganancia) {
    }

    private final RedFlujo rutas;

    public CuencaLechera(RedFlujo rutas) {
        this.rutas = rutas;
    }

    public RedFlujo rutas() {
        return rutas;
    }

    /** Las rutas más la fuente ficticia con un arco a cada cuenca. */
    public RedFlujo red(Map<String, Integer> produccion) {
        RedFlujo r = new RedFlujo();
        r.agregarVertice(FUENTE);
        rutas.vertices().forEach(r::agregarVertice);
        for (String c : CUENCAS) {
            r.agregarArco(FUENTE, c, produccion.getOrDefault(c, 0));
        }
        rutas.arcos().forEach(a -> r.agregarArco(a.u(), a.v(), a.capacidad()));
        return r;
    }

    public Envio enviar(Map<String, Integer> produccion) {
        return enviar(red(produccion), produccion);
    }

    private Envio enviar(RedFlujo red, Map<String, Integer> produccion) {
        RedFlujo.Resultado r = red.edmondsKarp(FUENTE, PLANTA);
        Map<String, Integer> porCuenca = new LinkedHashMap<>();
        for (String c : CUENCAS) {
            porCuenca.put(c, r.flujo().get(FUENTE + ">" + c));
        }
        int producido = CUENCAS.stream().mapToInt(c -> produccion.getOrDefault(c, 0)).sum();
        return new Envio(producido, r.total(), porCuenca, r);
    }

    /** Cada tramo de carretera ampliado en {@code extra} camiones, de mayor a menor ganancia. */
    public List<Ampliacion> ampliaciones(Map<String, Integer> produccion, int extra) {
        int base = enviar(produccion).entregado();
        RedFlujo red = red(produccion);
        List<Ampliacion> lista = new ArrayList<>();
        for (RedFlujo.Arco a : rutas.arcos()) {
            int nuevo = enviar(red.conCapacidad(a.u(), a.v(), a.capacidad() + extra), produccion).entregado();
            lista.add(new Ampliacion(a, nuevo, nuevo - base));
        }
        lista.sort(Comparator.comparingInt((Ampliacion x) -> -x.ganancia()));
        return lista;
    }
}
