package pe.edu.unaj.aed2.semana08.caso;

import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana08.modelo.GrafoPonderado;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Caso práctico semana 08: una distribuidora en Juliaca planifica el reparto a los pueblos de la red.
 * Compara la ruta con MENOS TRAMOS (BFS, semana 07) con la de MENOS KILÓMETROS (Dijkstra).
 */
public class PlanificadorRutas {

    /** Una ruta con sus totales. */
    public record Ruta(List<String> pueblos, int km) {
        public int tramos() {
            return Math.max(0, pueblos.size() - 1);
        }
    }

    /** Fila de la tabla de distancias desde el origen. */
    public record Destino(String id, Ruta masCorta, Ruta menosTramos) {
        public int kmDeMas() {
            return menosTramos.km() - masCorta.km();
        }
    }

    private final RedVial red;
    private final GrafoPonderado grafo;

    public PlanificadorRutas(RedVial red) {
        this.red = red;
        this.grafo = GrafoPonderado.desde(red);
    }

    public RedVial red() {
        return red;
    }

    public GrafoPonderado grafo() {
        return grafo;
    }

    public int km(List<String> pueblos) {
        int total = 0;
        for (int k = 1; k < pueblos.size(); k++) {
            total += grafo.km(pueblos.get(k - 1), pueblos.get(k));
        }
        return total;
    }

    public Ruta masCorta(String s, String t) {
        List<String> camino = grafo.dijkstra(s).camino(t);
        return new Ruta(camino, km(camino));
    }

    public Ruta menosTramos(String s, String t) {
        List<String> camino = red.grafo().caminoMinimo(s, t);
        return new Ruta(camino, km(camino));
    }

    /** Todos los destinos alcanzables desde s, ordenados por km. */
    public List<Destino> tabla(String s) {
        GrafoPonderado.Resultado r = grafo.dijkstra(s);
        List<Destino> filas = new ArrayList<>();
        for (Map.Entry<String, Integer> e : r.dist().entrySet()) {
            if (!e.getKey().equals(s)) {
                filas.add(new Destino(e.getKey(), new Ruta(r.camino(e.getKey()), e.getValue()),
                        menosTramos(s, e.getKey())));
            }
        }
        filas.sort((a, b) -> Integer.compare(a.masCorta().km(), b.masCorta().km()));
        return filas;
    }
}
