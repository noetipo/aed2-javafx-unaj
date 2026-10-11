package pe.edu.unaj.aed2.semana08.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana08.modelo.EstadoDijkstra.Tipo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Grafo no dirigido con pesos enteros (km) en las aristas, guardado como lista de adyacencia.
 * Incluye Dijkstra con una cola de prioridad (el montículo de la semana 06).
 */
public class GrafoPonderado {

    /** Arista hacia {@code destino} con su peso. */
    public record Arista(String destino, int km) {
    }

    /** Entrada de la cola de prioridad: un candidato a distancia {@code dist} para {@code v}. */
    private record Entrada(int dist, String v) {
    }

    /** Factor con que se estima el largo de la carretera a partir de la distancia en línea recta. */
    public static final double SINUOSIDAD = 1.25;

    private final Map<String, List<Arista>> ady = new LinkedHashMap<>();

    public void agregarVertice(String v) {
        ady.computeIfAbsent(v, k -> new ArrayList<>());
    }

    public void agregarArista(String u, String v, int km) {
        if (km < 0) {
            throw new IllegalArgumentException("Dijkstra no admite pesos negativos: " + u + "-" + v);
        }
        agregarVertice(u);
        agregarVertice(v);
        ady.get(u).add(new Arista(v, km));
        ady.get(v).add(new Arista(u, km));
    }

    /** La red vial de la semana 07 con km estimados: línea recta × {@link #SINUOSIDAD}, redondeado. */
    public static GrafoPonderado desde(RedVial red) {
        GrafoPonderado g = new GrafoPonderado();
        red.grafo().vertices().forEach(g::agregarVertice);
        for (String[] a : red.grafo().aristas()) {
            g.agregarArista(a[0], a[1], (int) Math.round(red.distanciaKm(a[0], a[1]) * SINUOSIDAD));
        }
        return g;
    }

    public Set<String> vertices() {
        return Collections.unmodifiableSet(ady.keySet());
    }

    public List<Arista> vecinos(String v) {
        return Collections.unmodifiableList(ady.getOrDefault(v, List.of()));
    }

    /** Km de la carretera u–v; si hay varias en paralelo, la más corta. */
    public int km(String u, String v) {
        return vecinos(u).stream().filter(a -> a.destino().equals(v)).mapToInt(Arista::km).min()
                .orElseThrow(() -> new IllegalArgumentException("No hay carretera " + u + "-" + v));
    }

    // ------------------------------------------------------------------
    // Dijkstra (sigue el código de la pestaña Java)
    // ------------------------------------------------------------------

    /** Resultado de Dijkstra: distancia mínima y padre de cada vértice alcanzable. */
    public record Resultado(Map<String, Integer> dist, Map<String, String> padre) {

        /** Camino de s (la raíz) a t; vacío si t no es alcanzable. */
        public List<String> camino(String t) {
            if (!dist.containsKey(t)) {
                return List.of();
            }
            List<String> c = new ArrayList<>();
            for (String v = t; v != null; v = padre.get(v)) {
                c.add(0, v);
            }
            return c;
        }
    }

    public Resultado dijkstra(String s) {
        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> padre = new HashMap<>();
        PriorityQueue<Entrada> cola = new PriorityQueue<>(Comparator.comparingInt(Entrada::dist));
        dist.put(s, 0);
        cola.add(new Entrada(0, s));
        while (!cola.isEmpty()) {
            Entrada e = cola.poll();
            if (e.dist() > dist.get(e.v())) {
                continue;
            }
            for (Arista a : vecinos(e.v())) {
                int nueva = e.dist() + a.km();
                if (nueva < dist.getOrDefault(a.destino(), Integer.MAX_VALUE)) {
                    dist.put(a.destino(), nueva);
                    padre.put(a.destino(), e.v());
                    cola.add(new Entrada(nueva, a.destino()));
                }
            }
        }
        return new Resultado(dist, padre);
    }

    // ------------------------------------------------------------------
    // Pseudocódigo y código Java
    // ------------------------------------------------------------------

    public static final List<String> PSEUDOCODIGO = List.of(
            "dijkstra(s):",
            "    dist[todos] ← ∞;  dist[s] ← 0;  cola.insertar((0, s))",
            "    mientras la cola no esté vacía:",
            "        (d, v) ← cola.extraerMin()               // el candidato más cercano",
            "        si d > dist[v]: continuar                // entrada vieja: v ya se cerró con menos",
            "        para cada vecino w de v, con peso p:",
            "            si dist[v] + p < dist[w]:            // ¿pasar por v mejora el camino a w?",
            "                dist[w] ← dist[v] + p;  padre[w] ← v;  cola.insertar((dist[w], w))",
            "    retornar dist, padre");

    static final String JAVA = """
            import java.util.ArrayList;
            import java.util.Comparator;
            import java.util.HashMap;
            import java.util.LinkedHashMap;
            import java.util.List;
            import java.util.Map;
            import java.util.PriorityQueue;

            public class Dijkstra {

                record Arista(String destino, int km) { }

                record Entrada(int dist, String v) { }

                private final Map<String, List<Arista>> ady = new LinkedHashMap<>();
                private final Map<String, String> padre = new HashMap<>();

                public void agregarCarretera(String u, String v, int km) {
                    ady.computeIfAbsent(u, k -> new ArrayList<>()).add(new Arista(v, km));
                    ady.computeIfAbsent(v, k -> new ArrayList<>()).add(new Arista(u, km));
                }

                // Distancia mínima desde s a cada pueblo alcanzable (y guarda los padres para el camino).
                public Map<String, Integer> dijkstra(String s) {                    //@dijkstra:1
                    Map<String, Integer> dist = new HashMap<>();     // si no está, vale "infinito"
                    PriorityQueue<Entrada> cola = new PriorityQueue<>(Comparator.comparingInt(Entrada::dist));
                    padre.clear();
                    dist.put(s, 0);                                                 //@dijkstra:2
                    cola.add(new Entrada(0, s));                                    //@dijkstra:2
                    while (!cola.isEmpty()) {                                       //@dijkstra:3
                        Entrada e = cola.poll();                                    //@dijkstra:4
                        if (e.dist() > dist.get(e.v())) {                           //@dijkstra:5
                            continue;                                               //@dijkstra:5
                        }
                        for (Arista a : ady.getOrDefault(e.v(), List.of())) {       //@dijkstra:6
                            int nueva = e.dist() + a.km();                          //@dijkstra:7
                            if (nueva < dist.getOrDefault(a.destino(), Integer.MAX_VALUE)) {   //@dijkstra:7
                                dist.put(a.destino(), nueva);                       //@dijkstra:8
                                padre.put(a.destino(), e.v());                      //@dijkstra:8
                                cola.add(new Entrada(nueva, a.destino()));          //@dijkstra:8
                            }
                        }
                    }
                    return dist;                                                    //@dijkstra:9
                }

                // Camino de s a t usando los padres del último dijkstra(s).
                public List<String> camino(String t) {
                    List<String> c = new ArrayList<>();
                    for (String v = t; v != null; v = padre.get(v)) {
                        c.add(0, v);
                    }
                    return c;
                }

                public static void main(String[] args) {
                    Dijkstra red = new Dijkstra();
                    red.agregarCarretera("Juliaca", "Puno", %s);
                    red.agregarCarretera("Juliaca", "Azángaro", %s);
                    red.agregarCarretera("Juliaca", "Huancané", %s);
                    red.agregarCarretera("Azángaro", "Putina", %s);
                    red.agregarCarretera("Putina", "Huancané", %s);
                    Map<String, Integer> dist = red.dijkstra("Juliaca");
                    System.out.println("Distancias desde Juliaca: " + new java.util.TreeMap<>(dist));
                    System.out.println("Camino a Putina: " + red.camino("Putina") + " = " + dist.get("Putina") + " km");
                }
            }
            """;

    /** Código Java con los km del ejemplo del main tomados de la red (así coinciden con la aplicación). */
    public static CodigoAlgoritmo codigo(GrafoPonderado g) {
        String java = String.format(JAVA, g.km("JUL", "PUN"), g.km("JUL", "AZA"), g.km("JUL", "HUA"),
                g.km("AZA", "PUT"), g.km("PUT", "HUA"));
        return CodigoAlgoritmo.seccion(PSEUDOCODIGO, java, "dijkstra");
    }

    // ------------------------------------------------------------------
    // Paso a paso
    // ------------------------------------------------------------------

    public List<EstadoDijkstra> pasosDijkstra(String s, Map<String, String> nombres) {
        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> padre = new HashMap<>();
        Set<String> cerrados = new LinkedHashSet<>();
        PriorityQueue<Entrada> cola = new PriorityQueue<>(Comparator.comparingInt(Entrada::dist));
        List<EstadoDijkstra> pasos = new ArrayList<>();
        java.util.function.Function<String, String> n = v -> nombres.getOrDefault(v, v);

        dist.put(s, 0);
        cola.add(new Entrada(0, s));
        pasos.add(paso(dist, padre, cerrados, s, null, cola, Tipo.INICIO, 2, vars(n, null, null, 0, null, dist, cola),
                "dist[" + n.apply(s) + "] = 0;  cola = " + texto(cola, n),
                "Se llama dijkstra(" + n.apply(s) + "). Al inicio toda distancia vale ∞ (todavía no se conoce ningún"
                        + " camino), salvo la del origen, que es 0. La idea: cerrar los pueblos de MÁS CERCANO a MÁS"
                        + " LEJANO. Para saber cuál es el más cercano sin revisar todos, se usa una cola de prioridad"
                        + " (el montículo de la semana 06) con pares (km, pueblo). Entra (0, " + n.apply(s) + ").",
                "dijkstra(" + n.apply(s) + ")"));
        while (!cola.isEmpty()) {
            Entrada e = cola.poll();
            String v = e.v();
            if (e.dist() > dist.get(v)) {
                pasos.add(paso(dist, padre, cerrados, v, null, cola, Tipo.DESCARTA, 5,
                        vars(n, v, null, e.dist(), null, dist, cola),
                        "d > dist[v]  →  " + e.dist() + " > " + dist.get(v) + "  →  verdadero  →  continue",
                        "extraerMin() devolvió (" + e.dist() + ", " + n.apply(v) + "), pero esa es una entrada VIEJA:"
                                + " después de insertarla se encontró un camino mejor a " + n.apply(v) + " (" + dist.get(v)
                                + " km), que ya se procesó. El if consulta d > dist[v]: " + e.dist() + " > "
                                + dist.get(v) + " es verdadero, así que continue salta al siguiente ciclo del while sin"
                                + " hacer nada. (La cola de Java no permite bajar la prioridad de un elemento, por eso"
                                + " se insertan duplicados y se descartan al salir.)",
                        "Descarta (" + e.dist() + ", " + n.apply(v) + ")."));
                continue;
            }
            cerrados.add(v);
            List<Arista> vecinos = vecinos(v);
            pasos.add(paso(dist, padre, cerrados, v, null, cola, Tipo.EXTRAE, 4, vars(n, v, null, e.dist(), null, dist, cola),
                    "(d, v) = cola.extraerMin() = (" + e.dist() + ", " + n.apply(v) + ");  d > dist[v] → falso",
                    "El while consulta si la cola está vacía: no lo está. extraerMin() saca el candidato MÁS CERCANO: "
                            + n.apply(v) + " a " + e.dist() + " km. El if de la línea 5 consulta si es una entrada vieja: "
                            + e.dist() + " > " + dist.get(v) + " es falso, así que es válida. Ahora " + n.apply(v)
                            + " queda CERRADO: su distancia ya es definitiva"
                            + (cola.isEmpty() ? ", porque no queda ningún otro candidato."
                            : ": cualquier desvío tendría que pasar por un pueblo de la cola, que está a " + e.dist()
                            + " km o más, y como los km nunca son negativos, no podría salir más corto.")
                            + " El for revisará sus " + vecinos.size() + " carretera"
                            + (vecinos.size() == 1 ? "" : "s") + ".",
                    n.apply(v) + " cerrado a " + e.dist() + " km."));
            for (Arista a : vecinos) {
                String w = a.destino();
                int nueva = e.dist() + a.km();
                Integer antes = dist.get(w);
                boolean mejora = nueva < (antes == null ? Integer.MAX_VALUE : antes);
                String actualTexto = antes == null ? "∞" : String.valueOf(antes);
                // La fuente monoespaciada de "En código" y de las variables no dibuja ∞: ahí se escribe "infinito".
                String actualCodigo = antes == null ? "infinito" : String.valueOf(antes);
                if (mejora) {
                    dist.put(w, nueva);
                    padre.put(w, v);
                    cola.add(new Entrada(nueva, w));
                    pasos.add(paso(dist, padre, cerrados, v, w, cola, Tipo.MEJORA, 8,
                            vars(n, v, w, e.dist(), a.km(), dist, cola),
                            "dist[v] + p < dist[w]  →  " + e.dist() + " + " + a.km() + " = " + nueva + " < " + actualCodigo
                                    + "  →  verdadero",
                            "El for toma la carretera " + n.apply(v) + " – " + n.apply(w) + " (" + a.km() + " km). El if"
                                    + " consulta: ¿llegar a " + n.apply(w) + " pasando por " + n.apply(v) + " (" + e.dist()
                                    + " + " + a.km() + " = " + nueva + " km) es mejor que lo que se sabía (" + actualTexto
                                    + ")? SÍ: se RELAJA la arista. dist[" + n.apply(w) + "] = " + nueva + ", padre["
                                    + n.apply(w) + "] = " + n.apply(v) + " y se inserta (" + nueva + ", " + n.apply(w)
                                    + ") en la cola." + (antes == null ? ""
                                    : " La entrada vieja (" + antes + ", " + n.apply(w) + ") queda en la cola; se"
                                    + " descartará cuando salga."),
                            n.apply(w) + ": " + actualTexto + " → " + nueva + " km."));
                } else {
                    pasos.add(paso(dist, padre, cerrados, v, w, cola, Tipo.NO_MEJORA, 7,
                            vars(n, v, w, e.dist(), a.km(), dist, cola),
                            "dist[v] + p < dist[w]  →  " + e.dist() + " + " + a.km() + " = " + nueva + " < " + actualCodigo
                                    + "  →  falso",
                            "El for toma la carretera " + n.apply(v) + " – " + n.apply(w) + " (" + a.km() + " km). El if"
                                    + " consulta: ¿pasar por " + n.apply(v) + " (" + nueva + " km) mejora lo que ya se"
                                    + " sabía de " + n.apply(w) + " (" + actualTexto + " km)? NO, así que no se cambia"
                                    + " nada y el for sigue" + (cerrados.contains(w) ? " (" + n.apply(w) + " ya estaba"
                                    + " cerrado)." : "."),
                            n.apply(w) + ": sin mejora."));
                }
            }
        }
        pasos.add(paso(dist, padre, cerrados, null, null, cola, Tipo.FIN, 9, vars(n, null, null, 0, null, dist, cola),
                "return dist, padre",
                "La cola quedó vacía: todos los pueblos alcanzables están cerrados y se retornan dist y padre. Las"
                        + " carreteras resaltadas forman el ÁRBOL DE CAMINOS MÍNIMOS: siguiendo los padres desde cualquier"
                        + " pueblo hacia atrás se obtiene su ruta más corta desde " + n.apply(s) + ".",
                "Fin de Dijkstra."));
        return pasos;
    }

    private static EstadoDijkstra paso(Map<String, Integer> dist, Map<String, String> padre, Set<String> cerrados,
                                       String actual, String vecino, PriorityQueue<Entrada> cola, Tipo tipo, int linea,
                                       Map<String, String> variables, String evaluacion, String explicacion,
                                       String descripcion) {
        List<String> enCola = cola.stream().sorted(Comparator.comparingInt(Entrada::dist))
                .map(e -> "(" + e.dist() + ", " + e.v() + ")").toList();
        return new EstadoDijkstra(dist, padre, cerrados, actual, vecino, enCola, tipo, linea, variables, evaluacion,
                explicacion, descripcion);
    }

    private static String texto(PriorityQueue<Entrada> cola, java.util.function.Function<String, String> n) {
        return "[" + String.join(", ", cola.stream().sorted(Comparator.comparingInt(Entrada::dist))
                .map(e -> "(" + e.dist() + ", " + n.apply(e.v()) + ")").toList()) + "]";
    }

    private static Map<String, String> vars(java.util.function.Function<String, String> n, String v, String w, int d,
                                            Integer p, Map<String, Integer> dist, PriorityQueue<Entrada> cola) {
        return PasoTrazable.variables(
                "v", v == null ? "—" : n.apply(v),
                "d", v == null ? "—" : d,
                "w", w == null ? "—" : n.apply(w),
                "p", p == null ? "—" : p,
                "dist[v]", v == null || !dist.containsKey(v) ? "—" : dist.get(v),
                "dist[w]", w == null ? "—" : dist.containsKey(w) ? dist.get(w) : "infinito",
                "cola", texto(cola, n));
    }
}
