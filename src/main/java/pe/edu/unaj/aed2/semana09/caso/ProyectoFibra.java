package pe.edu.unaj.aed2.semana09.caso;

import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana08.modelo.GrafoPonderado;
import pe.edu.unaj.aed2.semana09.modelo.ArbolExpansion;
import pe.edu.unaj.aed2.semana09.modelo.ArbolExpansion.Tramo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Caso práctico semana 09: un proyecto quiere unir los pueblos de la red con fibra óptica tendiendo cable solo
 * por algunos de los posibles tramos. Compara dos diseños que usan n − 1 tramos:
 * <ul>
 *   <li>el árbol de EXPANSIÓN mínima (Kruskal): el menor total de cable;</li>
 *   <li>el árbol de CAMINOS mínimos desde un centro (Dijkstra, semana 08): cada pueblo lo más cerca posible del
 *   centro por el cable, aunque se tienda más.</li>
 * </ul>
 */
public class ProyectoFibra {

    /** Costo por km de cable tendido. Valor FICTICIO, solo para dar escala a los km. */
    public static final int SOLES_POR_KM = 30_000;

    /** Un diseño de red: los tramos donde se tiende cable. */
    public record Diseno(String nombre, List<Tramo> tramos) {
        public int km() {
            return ArbolExpansion.total(tramos);
        }

        public long soles() {
            return (long) km() * SOLES_POR_KM;
        }

        public Set<String> claves() {
            return tramos.stream().map(Tramo::clave).collect(Collectors.toSet());
        }
    }

    /** Km de cable que recorre la señal del centro a un pueblo en cada diseño. */
    public record Fila(String id, int porExpansion, int porCaminos) {
        public int diferencia() {
            return porExpansion - porCaminos;
        }
    }

    private final RedVial red;
    private final GrafoPonderado grafo;

    public ProyectoFibra(RedVial tendidos) {
        this.red = tendidos;
        this.grafo = GrafoPonderado.desde(tendidos);
    }

    public RedVial red() {
        return red;
    }

    public GrafoPonderado grafo() {
        return grafo;
    }

    public Diseno expansionMinima() {
        return new Diseno("Árbol de expansión mínima (Kruskal)", ArbolExpansion.kruskal(grafo));
    }

    /** Los tramos padre → hijo que deja Dijkstra desde el centro. */
    public Diseno caminosMinimos(String centro) {
        GrafoPonderado.Resultado r = grafo.dijkstra(centro);
        List<Tramo> tramos = new ArrayList<>();
        for (String v : grafo.vertices()) {
            String p = r.padre().get(v);
            if (p != null) {
                tramos.add(new Tramo(p, v, grafo.km(p, v)));
            }
        }
        return new Diseno("Árbol de caminos mínimos desde " + red.nombres().get(centro) + " (Dijkstra)", tramos);
    }

    /** Km de cable desde el centro a cada pueblo, recorriendo solo los tramos del diseño. */
    public Map<String, Integer> distanciasPorCable(Diseno d, String centro) {
        GrafoPonderado arbol = new GrafoPonderado();
        grafo.vertices().forEach(arbol::agregarVertice);
        d.tramos().forEach(t -> arbol.agregarArista(t.u(), t.v(), t.km()));
        return arbol.dijkstra(centro).dist();
    }

    /** Una fila por pueblo (sin el centro), de más cercano a más lejano por el árbol de caminos mínimos. */
    public List<Fila> tabla(String centro) {
        Map<String, Integer> expansion = distanciasPorCable(expansionMinima(), centro);
        Map<String, Integer> caminos = distanciasPorCable(caminosMinimos(centro), centro);
        List<Fila> filas = new ArrayList<>();
        for (String v : grafo.vertices()) {
            if (!v.equals(centro) && caminos.containsKey(v)) {
                filas.add(new Fila(v, expansion.get(v), caminos.get(v)));
            }
        }
        filas.sort(Comparator.comparingInt(Fila::porCaminos));
        return filas;
    }
}
