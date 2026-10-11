package pe.edu.unaj.aed2.semana07.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana07.modelo.EstadoRecorrido.Estado;
import pe.edu.unaj.aed2.semana07.modelo.EstadoRecorrido.Tipo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Grafo no dirigido con lista de adyacencia: para cada vértice, la lista de sus vecinos en el orden
 * en que se agregaron las aristas. Ese orden decide a qué vecino se va primero en BFS y DFS.
 */
public class Grafo {

    private final Map<String, List<String>> ady = new LinkedHashMap<>();

    public void agregarVertice(String v) {
        ady.computeIfAbsent(v, k -> new ArrayList<>());
    }

    public void agregarArista(String u, String v) {
        if (u.equals(v)) {
            throw new IllegalArgumentException("No se admiten lazos: " + u);
        }
        agregarVertice(u);
        agregarVertice(v);
        if (!ady.get(u).contains(v)) {
            ady.get(u).add(v);
            ady.get(v).add(u);
        }
    }

    public Set<String> vertices() {
        return Collections.unmodifiableSet(ady.keySet());
    }

    public List<String> vecinos(String v) {
        return Collections.unmodifiableList(ady.getOrDefault(v, List.of()));
    }

    /** Aristas como pares {u, v}, cada una una sola vez. */
    public List<String[]> aristas() {
        List<String[]> lista = new ArrayList<>();
        Set<String> vistas = new HashSet<>();
        for (var e : ady.entrySet()) {
            for (String w : e.getValue()) {
                if (vistas.add(EstadoRecorrido.arista(e.getKey(), w))) {
                    lista.add(new String[]{e.getKey(), w});
                }
            }
        }
        return lista;
    }

    /** Copia sin las aristas indicadas (claves "U-V"), para simular tramos bloqueados. */
    public Grafo sin(Set<String> bloqueadas) {
        Grafo g = new Grafo();
        ady.keySet().forEach(g::agregarVertice);
        for (String[] a : aristas()) {
            if (!bloqueadas.contains(EstadoRecorrido.arista(a[0], a[1]))) {
                g.agregarArista(a[0], a[1]);
            }
        }
        return g;
    }

    // ------------------------------------------------------------------
    // Recorridos (siguen el código de la pestaña Java)
    // ------------------------------------------------------------------

    /** Cada vértice alcanzable desde s con su distancia en tramos, en el orden en que BFS los descubre. */
    public Map<String, Integer> bfs(String s) {
        Map<String, Integer> dist = new LinkedHashMap<>();
        Deque<String> cola = new ArrayDeque<>();
        dist.put(s, 0);
        cola.addLast(s);
        while (!cola.isEmpty()) {
            String v = cola.removeFirst();
            for (String w : vecinos(v)) {
                if (!dist.containsKey(w)) {
                    dist.put(w, dist.get(v) + 1);
                    cola.addLast(w);
                }
            }
        }
        return dist;
    }

    /** Vértices alcanzables desde s en el orden en que DFS (recursivo) los visita. */
    public List<String> dfs(String s) {
        List<String> orden = new ArrayList<>();
        dfs(s, new HashSet<>(), orden);
        return orden;
    }

    private void dfs(String v, Set<String> visitado, List<String> orden) {
        visitado.add(v);
        orden.add(v);
        for (String w : vecinos(v)) {
            if (!visitado.contains(w)) {
                dfs(w, visitado, orden);
            }
        }
    }

    /** Camino con menos tramos de s a t (vacío si no hay), reconstruido con los padres de BFS. */
    public List<String> caminoMinimo(String s, String t) {
        Map<String, String> padre = new LinkedHashMap<>();
        Deque<String> cola = new ArrayDeque<>();
        padre.put(s, null);
        cola.addLast(s);
        while (!cola.isEmpty()) {
            String v = cola.removeFirst();
            for (String w : vecinos(v)) {
                if (!padre.containsKey(w)) {
                    padre.put(w, v);
                    cola.addLast(w);
                }
            }
        }
        if (!padre.containsKey(t)) {
            return List.of();
        }
        List<String> camino = new ArrayList<>();
        for (String v = t; v != null; v = padre.get(v)) {
            camino.add(0, v);
        }
        return camino;
    }

    // ------------------------------------------------------------------
    // Pseudocódigo y código Java
    // ------------------------------------------------------------------

    public static final List<String> PSEUDO_BFS = List.of(
            "bfs(s):",
            "    dist[s] ← 0;  cola.encolar(s)            // s queda descubierto",
            "    mientras la cola no esté vacía:",
            "        v ← cola.desencolar()",
            "        para cada vecino w de v:",
            "            si w no fue descubierto (no tiene dist):",
            "                dist[w] ← dist[v] + 1;  cola.encolar(w)",
            "    retornar dist");

    public static final List<String> PSEUDO_DFS = List.of(
            "dfs(v):",
            "    marcar v como visitado;  agregar v al orden",
            "    para cada vecino w de v:",
            "        si w no fue visitado:",
            "            dfs(w)                           // al terminar, se vuelve aquí");

    static final String JAVA = """
            import java.util.ArrayDeque;
            import java.util.ArrayList;
            import java.util.Deque;
            import java.util.HashSet;
            import java.util.LinkedHashMap;
            import java.util.List;
            import java.util.Map;
            import java.util.Set;

            public class Grafo {

                // Lista de adyacencia: cada vértice con la lista de sus vecinos.
                private final Map<String, List<String>> ady = new LinkedHashMap<>();

                public void agregarArista(String u, String v) {
                    ady.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
                    ady.computeIfAbsent(v, k -> new ArrayList<>()).add(u);
                }

                // Recorrido en anchura: cada vértice alcanzable con su distancia en tramos desde s.
                public Map<String, Integer> bfs(String s) {                       //@bfs:1
                    Map<String, Integer> dist = new LinkedHashMap<>();   // también marca los descubiertos
                    Deque<String> cola = new ArrayDeque<>();
                    dist.put(s, 0);                                               //@bfs:2
                    cola.addLast(s);                                              //@bfs:2
                    while (!cola.isEmpty()) {                                     //@bfs:3
                        String v = cola.removeFirst();                            //@bfs:4
                        for (String w : ady.getOrDefault(v, List.of())) {         //@bfs:5
                            if (!dist.containsKey(w)) {                           //@bfs:6
                                dist.put(w, dist.get(v) + 1);                     //@bfs:7
                                cola.addLast(w);                                  //@bfs:7
                            }
                        }
                    }
                    return dist;                                                  //@bfs:8
                }

                // Recorrido en profundidad: vértices en el orden en que se visitan.
                public List<String> dfs(String s) {
                    List<String> orden = new ArrayList<>();
                    dfs(s, new HashSet<>(), orden);
                    return orden;
                }

                // Agrega a "orden" todo lo alcanzable desde v; por eso no necesita devolver nada.
                private void dfs(String v, Set<String> visitado, List<String> orden) {   //@dfs:1
                    visitado.add(v);                                              //@dfs:2
                    orden.add(v);                                                 //@dfs:2
                    for (String w : ady.getOrDefault(v, List.of())) {             //@dfs:3
                        if (!visitado.contains(w)) {                              //@dfs:4
                            dfs(w, visitado, orden);                              //@dfs:5
                        }
                    }
                }

                public static void main(String[] args) {
                    Grafo red = new Grafo();
                    String[][] carreteras = {
                        {"Juliaca", "Puno"}, {"Juliaca", "Lampa"}, {"Juliaca", "Pucará"}, {"Juliaca", "Azángaro"},
                        {"Juliaca", "Huancané"}, {"Lampa", "Pucará"}, {"Pucará", "Ayaviri"}, {"Azángaro", "Putina"},
                        {"Putina", "Huancané"}, {"Huancané", "Moho"}, {"Puno", "Chucuito"}, {"Chucuito", "Ilave"},
                        {"Ilave", "Juli"}, {"Juli", "Yunguyo"}, {"Juli", "Desaguadero"}
                    };
                    for (String[] c : carreteras) {
                        red.agregarArista(c[0], c[1]);
                    }
                    System.out.println("BFS desde Juliaca: " + red.bfs("Juliaca"));
                    System.out.println("DFS desde Juliaca: " + red.dfs("Juliaca"));
                }
            }
            """;

    public static final CodigoAlgoritmo CODIGO_BFS = CodigoAlgoritmo.seccion(PSEUDO_BFS, JAVA, "bfs");
    public static final CodigoAlgoritmo CODIGO_DFS = CodigoAlgoritmo.seccion(PSEUDO_DFS, JAVA, "dfs");

    // ------------------------------------------------------------------
    // Paso a paso
    // ------------------------------------------------------------------

    private final class Grabador {
        final Map<String, Estado> estados = new LinkedHashMap<>();
        final Set<String> arbol = new LinkedHashSet<>();
        final List<String> orden = new ArrayList<>();
        final Map<String, Integer> dist = new LinkedHashMap<>();
        final List<EstadoRecorrido> pasos = new ArrayList<>();
        final Map<String, String> nombres;

        Grabador(Map<String, String> nombres) {
            this.nombres = nombres;
            ady.keySet().forEach(v -> estados.put(v, Estado.NO_VISITADO));
        }

        String n(String v) {
            return nombres.getOrDefault(v, v);
        }

        void paso(String actual, String vecino, List<String> frontera, Tipo tipo, int linea,
                  Map<String, String> variables, String evaluacion, String explicacion, String descripcion) {
            pasos.add(new EstadoRecorrido(estados, actual, vecino, arbol, frontera, orden, dist, tipo, linea,
                    variables, evaluacion, explicacion, descripcion));
        }

        String lista(List<String> vs) {
            return vs.isEmpty() ? "[]" : "[" + String.join(", ", vs.stream().map(this::n).toList()) + "]";
        }
    }

    /** BFS paso a paso; {@code nombres} traduce ids a nombres legibles (puede estar vacío). */
    public List<EstadoRecorrido> pasosBfs(String s, Map<String, String> nombres) {
        Grabador g = new Grabador(nombres);
        Deque<String> cola = new ArrayDeque<>();
        g.dist.put(s, 0);
        g.estados.put(s, Estado.EN_FRONTERA);
        cola.addLast(s);
        g.paso(s, null, List.copyOf(cola), Tipo.INICIO, 2, varsBfs(g, null, null, cola),
                "dist[" + g.n(s) + "] = 0;  cola = " + g.lista(List.copyOf(cola)),
                "Se llama bfs(" + g.n(s) + "). BFS recorre por CAPAS: primero el origen, luego todos sus vecinos"
                        + " (a 1 tramo), luego los vecinos de esos (a 2 tramos), etc. Para lograrlo usa una COLA: lo"
                        + " primero que entra es lo primero que sale. " + g.n(s) + " recibe distancia 0, queda"
                        + " descubierto (gris) y entra a la cola.",
                "bfs(" + g.n(s) + ")");
        while (!cola.isEmpty()) {
            String v = cola.removeFirst();
            g.estados.put(v, Estado.TERMINADO);
            g.orden.add(v);
            List<String> vecinos = vecinos(v);
            g.paso(v, null, List.copyOf(cola), Tipo.SACA, 4, varsBfs(g, v, null, cola),
                    "cola no vacía → verdadero;  v = cola.desencolar() = " + g.n(v),
                    "El while consulta si la cola está vacía: no lo está, así que sigue. Se saca el que está al"
                            + " FRENTE de la cola: v = " + g.n(v) + " (a " + g.dist.get(v) + " tramo"
                            + (g.dist.get(v) == 1 ? "" : "s") + " del origen). Ahora el for revisará sus "
                            + vecinos.size() + " vecino" + (vecinos.size() == 1 ? "" : "s") + ": " + g.lista(vecinos)
                            + ".",
                    "Sale " + g.n(v) + " de la cola.");
            for (String w : vecinos) {
                boolean nuevo = !g.dist.containsKey(w);
                if (nuevo) {
                    g.dist.put(w, g.dist.get(v) + 1);
                    g.estados.put(w, Estado.EN_FRONTERA);
                    g.arbol.add(EstadoRecorrido.arista(v, w));
                    cola.addLast(w);
                    g.paso(v, w, List.copyOf(cola), Tipo.VECINO_NUEVO, 7, varsBfs(g, v, w, cola),
                            "!dist.containsKey(" + g.n(w) + ") → verdadero;  dist[" + g.n(w) + "] = " + g.dist.get(v)
                                    + " + 1 = " + g.dist.get(w) + ";  encolar",
                            "El for toma el vecino w = " + g.n(w) + ". El if consulta si ya fue descubierto: NO tiene"
                                    + " distancia, así que es nuevo. Se le asigna dist = dist[" + g.n(v) + "] + 1 = "
                                    + g.dist.get(w) + ", queda gris y entra al FINAL de la cola, que ahora es "
                                    + g.lista(List.copyOf(cola)) + ". La carretera " + g.n(v) + "–" + g.n(w)
                                    + " pasa a ser parte del árbol de BFS (por ahí se descubrió " + g.n(w) + ").",
                            g.n(w) + " descubierto a " + g.dist.get(w) + " tramo" + (g.dist.get(w) == 1 ? "" : "s") + ".");
                } else {
                    g.paso(v, w, List.copyOf(cola), Tipo.VECINO_VISTO, 6, varsBfs(g, v, w, cola),
                            "!dist.containsKey(" + g.n(w) + ") → falso",
                            "El for toma el vecino w = " + g.n(w) + ". El if consulta si ya fue descubierto: SÍ (tiene"
                                    + " dist = " + g.dist.get(w) + "), así que se ignora; no se vuelve a encolar. El for"
                                    + " sigue con el próximo vecino" + (vecinos.indexOf(w) == vecinos.size() - 1
                                    ? " (era el último: el for termina y el while vuelve a consultar la cola)."
                                    : "."),
                            g.n(w) + " ya fue descubierto.");
                }
            }
        }
        g.paso(null, null, List.of(), Tipo.FIN, 8, varsBfs(g, null, null, cola), "return dist",
                "La cola quedó vacía: el while termina y se retorna dist. Se alcanzaron " + g.dist.size() + " de "
                        + ady.size() + " vértices. Las aristas resaltadas forman el ÁRBOL DE BFS: el camino que baja"
                        + " por él desde " + g.n(s) + " es uno con la menor cantidad de tramos posible."
                        + (g.dist.size() < ady.size() ? " Los que quedaron blancos no tienen ningún camino desde "
                        + g.n(s) + "." : ""),
                "Fin de BFS.");
        return g.pasos;
    }

    private Map<String, String> varsBfs(Grabador g, String v, String w, Deque<String> cola) {
        return PasoTrazable.variables("v", v == null ? "—" : g.n(v), "w", w == null ? "—" : g.n(w),
                "dist[v]", v == null ? "—" : g.dist.get(v), "dist[w]", w == null || !g.dist.containsKey(w) ? "—" : g.dist.get(w),
                "cola", g.lista(List.copyOf(cola)), "orden", g.lista(g.orden));
    }

    /** DFS recursivo paso a paso. */
    public List<EstadoRecorrido> pasosDfs(String s, Map<String, String> nombres) {
        Grabador g = new Grabador(nombres);
        Deque<String> pila = new ArrayDeque<>();
        dfsPasos(g, s, null, pila);
        g.paso(null, null, List.of(), Tipo.FIN, 0, varsDfs(g, null, null, pila), "return orden",
                "La llamada inicial dfs(" + g.n(s) + ") terminó: la pila de llamadas quedó vacía. Orden de visita: "
                        + g.lista(g.orden) + ". Las aristas resaltadas forman el ÁRBOL DE DFS: muestran por dónde se"
                        + " bajó en profundidad antes de retroceder.",
                "Fin de DFS.");
        return g.pasos;
    }

    private void dfsPasos(Grabador g, String v, String padre, Deque<String> pila) {
        pila.addLast(v);
        g.estados.put(v, Estado.EN_FRONTERA);
        g.orden.add(v);
        if (padre != null) {
            g.arbol.add(EstadoRecorrido.arista(padre, v));
        }
        List<String> vecinos = vecinos(v);
        g.paso(v, null, List.copyOf(pila), Tipo.LLAMADA, 2, varsDfs(g, v, null, pila),
                "visitado.add(" + g.n(v) + ");  orden = " + g.lista(g.orden),
                (padre == null ? "Se llama dfs(" + g.n(v) + "). DFS se va lo más LEJOS posible por un camino y solo"
                        + " retrocede cuando no queda a dónde ir; la pila de llamadas recuerda por dónde volver. "
                        : "Comienza dfs(" + g.n(v) + "), llamada desde dfs(" + g.n(padre) + "), que queda esperando"
                        + " en la pila. ")
                        + g.n(v) + " se marca como visitado y se agrega al orden. Ahora el for revisará sus vecinos: "
                        + g.lista(vecinos) + ".",
                "Visita " + g.n(v) + ".");
        for (String w : vecinos) {
            if (g.estados.get(w) == Estado.NO_VISITADO) {
                g.paso(v, w, List.copyOf(pila), Tipo.VECINO_NUEVO, 5, varsDfs(g, v, w, pila),
                        "!visitado.contains(" + g.n(w) + ") → verdadero  →  dfs(" + g.n(w) + ")",
                        "El for de dfs(" + g.n(v) + ") toma el vecino w = " + g.n(w) + ". El if consulta si ya fue"
                                + " visitado: NO, así que se llama dfs(" + g.n(w) + ") de inmediato, sin mirar los demás"
                                + " vecinos de " + g.n(v) + ": eso es ir en profundidad. dfs(" + g.n(v) + ") queda"
                                + " pausada en esta línea.",
                        "Baja a " + g.n(w) + ".");
                dfsPasos(g, w, v, pila);
            } else {
                g.paso(v, w, List.copyOf(pila), Tipo.VECINO_VISTO, 4, varsDfs(g, v, w, pila),
                        "!visitado.contains(" + g.n(w) + ") → falso",
                        "El for de dfs(" + g.n(v) + ") toma el vecino w = " + g.n(w) + ". El if consulta si ya fue"
                                + " visitado: SÍ, así que se ignora y el for sigue"
                                + (vecinos.indexOf(w) == vecinos.size() - 1 ? " (era el último vecino)." : "."),
                        g.n(w) + " ya visitado.");
            }
        }
        g.estados.put(v, Estado.TERMINADO);
        pila.removeLast();
        g.paso(v, null, List.copyOf(pila), Tipo.REGRESA, 3, varsDfs(g, v, null, pila),
                "fin del for  →  dfs(" + g.n(v) + ") termina",
                "El for de dfs(" + g.n(v) + ") revisó todos sus vecinos: " + g.n(v) + " queda TERMINADO (negro) y su"
                        + " llamada sale de la pila. " + (padre == null ? "Era la llamada inicial."
                        : "El control regresa a dfs(" + g.n(padre) + "), justo después de la línea donde se llamó a"
                        + " dfs(" + g.n(v) + "), y su for continúa con el siguiente vecino de " + g.n(padre) + "."),
                g.n(v) + " terminado.");
    }

    private Map<String, String> varsDfs(Grabador g, String v, String w, Deque<String> pila) {
        return PasoTrazable.variables("v", v == null ? "—" : g.n(v), "w", w == null ? "—" : g.n(w),
                "pila", pila.isEmpty() ? "—" : String.join(" › ", pila.stream().map(x -> "dfs(" + g.n(x) + ")").toList()),
                "orden", g.lista(g.orden));
    }
}
