package pe.edu.unaj.aed2.semana07.caso;

import pe.edu.unaj.aed2.semana07.modelo.EstadoRecorrido;
import pe.edu.unaj.aed2.semana07.modelo.Grafo;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Caso práctico semana 07: bloqueos de carreteras. Con BFS se calcula qué pueblos siguen conectados
 * con un origen cuando se bloquean algunos tramos, y cuáles son los tramos CRÍTICOS: aquellos que,
 * si se bloquean solos, dejan pueblos aislados (en teoría de grafos, los "puentes").
 */
public class BloqueoVial {

    private final RedVial red;

    public BloqueoVial(RedVial red) {
        this.red = red;
    }

    public RedVial red() {
        return red;
    }

    /** Pueblos a los que se puede llegar desde {@code origen} con los tramos bloqueados. */
    public Set<String> alcanzables(String origen, Set<String> bloqueados) {
        return red.grafo().sin(bloqueados).bfs(origen).keySet();
    }

    /** Pueblos aislados de {@code origen}. */
    public List<String> aislados(String origen, Set<String> bloqueados) {
        Set<String> llega = alcanzables(origen, bloqueados);
        return red.grafo().vertices().stream().filter(v -> !llega.contains(v)).toList();
    }

    /** Ruta con menos tramos (vacía si no hay). */
    public List<String> ruta(String origen, String destino, Set<String> bloqueados) {
        return red.grafo().sin(bloqueados).caminoMinimo(origen, destino);
    }

    /**
     * Tramos críticos: se prueba bloquear cada carretera sola y, si el grafo queda con menos pueblos
     * alcanzables desde cualquier punto, esa carretera es crítica. O(E · (V + E)): suficiente aquí.
     */
    public List<String> tramosCriticos() {
        Grafo g = red.grafo();
        String alguno = g.vertices().iterator().next();
        int total = g.bfs(alguno).size();
        List<String> criticos = new ArrayList<>();
        for (String[] a : g.aristas()) {
            String clave = EstadoRecorrido.arista(a[0], a[1]);
            if (g.sin(Set.of(clave)).bfs(alguno).size() < total) {
                criticos.add(clave);
            }
        }
        return criticos;
    }

    /** Todas las carreteras como pares {desde, hasta}, en el orden y sentido del archivo. */
    public List<String[]> carreteras() {
        return red.grafo().aristas();
    }
}
