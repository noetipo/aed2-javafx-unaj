package pe.edu.unaj.aed2.semana10.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana07.modelo.EstadoRecorrido.Estado;
import pe.edu.unaj.aed2.semana10.modelo.EstadoTopologico.Tipo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Grafo DIRIGIDO como lista de adyacencia: el arco u → v significa "u va antes que v" (u es prerrequisito de v).
 * Incluye dos formas de obtener un orden topológico: Kahn (grados de entrada y una cola) y DFS (postorden invertido).
 */
public class GrafoDirigido {

    private final Map<String, List<String>> ady = new LinkedHashMap<>();

    public void agregarVertice(String v) {
        ady.computeIfAbsent(v, k -> new ArrayList<>());
    }

    public void agregarArco(String u, String v) {
        agregarVertice(u);
        agregarVertice(v);
        ady.get(u).add(v);
    }

    public Set<String> vertices() {
        return Collections.unmodifiableSet(ady.keySet());
    }

    public List<String> sucesores(String v) {
        return Collections.unmodifiableList(ady.getOrDefault(v, List.of()));
    }

    /** Los vértices con un arco hacia v (sus prerrequisitos directos). */
    public List<String> predecesores(String v) {
        List<String> p = new ArrayList<>();
        ady.forEach((u, s) -> {
            if (s.contains(v)) {
                p.add(u);
            }
        });
        return p;
    }

    /** Cada arco como {u, v}. */
    public List<String[]> arcos() {
        List<String[]> lista = new ArrayList<>();
        ady.forEach((u, s) -> s.forEach(v -> lista.add(new String[]{u, v})));
        return lista;
    }

    /** Una copia con un arco más (por ejemplo, un prerrequisito mal digitado). */
    public GrafoDirigido conArco(String u, String v) {
        GrafoDirigido g = new GrafoDirigido();
        ady.keySet().forEach(g::agregarVertice);
        arcos().forEach(a -> g.agregarArco(a[0], a[1]));
        g.agregarArco(u, v);
        return g;
    }

    public Map<String, Integer> gradosEntrada() {
        Map<String, Integer> grado = new LinkedHashMap<>();
        ady.keySet().forEach(v -> grado.put(v, 0));
        ady.values().forEach(s -> s.forEach(w -> grado.merge(w, 1, Integer::sum)));
        return grado;
    }

    // ------------------------------------------------------------------
    // Kahn y DFS (siguen el código de la pestaña Java)
    // ------------------------------------------------------------------

    /** Orden topológico con Kahn; lanza IllegalStateException si hay un ciclo. */
    public List<String> kahn() {
        Map<String, Integer> grado = gradosEntrada();
        ArrayDeque<String> cola = new ArrayDeque<>();
        for (String v : ady.keySet()) {
            if (grado.get(v) == 0) {
                cola.add(v);
            }
        }
        List<String> orden = new ArrayList<>();
        while (!cola.isEmpty()) {
            String v = cola.poll();
            orden.add(v);
            for (String w : ady.get(v)) {
                grado.put(w, grado.get(w) - 1);
                if (grado.get(w) == 0) {
                    cola.add(w);
                }
            }
        }
        if (orden.size() < ady.size()) {
            throw new IllegalStateException("Hay un ciclo: no existe orden topológico");
        }
        return orden;
    }

    /** Orden topológico con DFS (postorden invertido); lanza IllegalStateException si hay un ciclo. */
    public List<String> ordenDfs() {
        Map<String, Estado> color = new HashMap<>();
        LinkedList<String> orden = new LinkedList<>();
        for (String v : ady.keySet()) {
            if (color.getOrDefault(v, Estado.NO_VISITADO) == Estado.NO_VISITADO) {
                visitar(v, color, orden);
            }
        }
        return orden;
    }

    private void visitar(String v, Map<String, Estado> color, LinkedList<String> orden) {
        color.put(v, Estado.EN_FRONTERA);
        for (String w : ady.get(v)) {
            Estado c = color.getOrDefault(w, Estado.NO_VISITADO);
            if (c == Estado.EN_FRONTERA) {
                throw new IllegalStateException("Hay un ciclo: " + v + " -> " + w);
            }
            if (c == Estado.NO_VISITADO) {
                visitar(w, color, orden);
            }
        }
        color.put(v, Estado.TERMINADO);
        orden.addFirst(v);
    }

    public boolean tieneCiclo() {
        return !encontrarCiclo().isEmpty();
    }

    /** Los vértices de algún ciclo, en orden (el último apunta al primero); vacío si no hay ciclos. */
    public List<String> encontrarCiclo() {
        Map<String, Estado> color = new HashMap<>();
        List<String> pila = new ArrayList<>();
        for (String v : ady.keySet()) {
            if (!color.containsKey(v)) {
                List<String> ciclo = buscarCiclo(v, color, pila);
                if (!ciclo.isEmpty()) {
                    return ciclo;
                }
            }
        }
        return List.of();
    }

    private List<String> buscarCiclo(String v, Map<String, Estado> color, List<String> pila) {
        color.put(v, Estado.EN_FRONTERA);
        pila.add(v);
        for (String w : ady.get(v)) {
            Estado c = color.get(w);
            if (c == Estado.EN_FRONTERA) {
                return List.copyOf(pila.subList(pila.indexOf(w), pila.size()));
            }
            if (c == null) {
                List<String> ciclo = buscarCiclo(w, color, pila);
                if (!ciclo.isEmpty()) {
                    return ciclo;
                }
            }
        }
        color.put(v, Estado.TERMINADO);
        pila.remove(pila.size() - 1);
        return List.of();
    }

    /**
     * Nivel de cada vértice: la cadena de prerrequisitos más larga que llega a él (0 si no tiene). Es el primer
     * ciclo académico (desde 0) en que se podría llevar el curso. Requiere un grafo sin ciclos.
     */
    public Map<String, Integer> niveles() {
        Map<String, Integer> nivel = new LinkedHashMap<>();
        ady.keySet().forEach(v -> nivel.put(v, 0));
        for (String v : kahn()) {
            for (String w : ady.get(v)) {
                nivel.put(w, Math.max(nivel.get(w), nivel.get(v) + 1));
            }
        }
        return nivel;
    }

    /** Cuántos vértices tiene la cadena más larga que EMPIEZA en cada vértice (1 si nadie depende de él). */
    public Map<String, Integer> alturas() {
        Map<String, Integer> altura = new HashMap<>();
        List<String> orden = kahn();
        for (int k = orden.size() - 1; k >= 0; k--) {
            String v = orden.get(k);
            int mejor = 0;
            for (String w : ady.get(v)) {
                mejor = Math.max(mejor, altura.get(w));
            }
            altura.put(v, mejor + 1);
        }
        return altura;
    }

    /** La cadena de prerrequisitos más larga (el camino crítico). */
    public List<String> caminoCritico() {
        Map<String, Integer> altura = alturas();
        String v = null;
        for (String x : ady.keySet()) {
            if (v == null || altura.get(x) > altura.get(v)) {
                v = x;
            }
        }
        List<String> camino = new ArrayList<>();
        while (v != null) {
            camino.add(v);
            String siguiente = null;
            for (String w : ady.get(v)) {
                if (altura.get(w) == altura.get(v) - 1 && siguiente == null) {
                    siguiente = w;
                }
            }
            v = siguiente;
        }
        return camino;
    }

    // ------------------------------------------------------------------
    // Pseudocódigo y código Java
    // ------------------------------------------------------------------

    public static final List<String> PSEUDO_KAHN = List.of(
            "kahn():",
            "    para cada v: grado[v] ← cuántos arcos llegan a v      // prerrequisitos",
            "    cola ← los v con grado[v] = 0                          // se pueden llevar ya",
            "    mientras la cola no esté vacía:",
            "        v ← cola.sacar();  orden.agregarAlFinal(v)",
            "        para cada arco v → w:",
            "            grado[w] ← grado[w] − 1                        // un prerrequisito menos",
            "            si grado[w] = 0: cola.agregar(w)               // ya no espera a nadie",
            "    si orden tiene menos de n vértices: error, hay un ciclo",
            "    retornar orden");

    public static final List<String> PSEUDO_DFS = List.of(
            "ordenDfs():",
            "    para cada v BLANCO: visitar(v)",
            "    retornar orden",
            "visitar(v):",
            "    color[v] ← GRIS                       // está en la pila de llamadas",
            "    para cada arco v → w:",
            "        si color[w] = GRIS: error, hay un ciclo",
            "        si color[w] = BLANCO: visitar(w)",
            "    color[v] ← NEGRO;  orden.agregarAlInicio(v)   // todo lo que depende de v ya está");

    static final String JAVA = """
            import java.util.ArrayDeque;
            import java.util.ArrayList;
            import java.util.HashMap;
            import java.util.LinkedHashMap;
            import java.util.LinkedList;
            import java.util.List;
            import java.util.Map;

            public class OrdenTopologico {

                enum Color { BLANCO, GRIS, NEGRO }

                private final Map<String, List<String>> ady = new LinkedHashMap<>();
                private final Map<String, Color> color = new HashMap<>();

                // Arco u -> v: u es prerrequisito de v (u va antes).
                public void agregarArco(String u, String v) {
                    ady.computeIfAbsent(u, k -> new ArrayList<>()).add(v);
                    ady.computeIfAbsent(v, k -> new ArrayList<>());
                }

                // Kahn: saca primero los que no tienen prerrequisitos pendientes.
                public List<String> kahn() {                                                    //@kahn:1
                    Map<String, Integer> grado = new HashMap<>();
                    for (String v : ady.keySet()) {                                             //@kahn:2
                        grado.putIfAbsent(v, 0);                                                //@kahn:2
                        for (String w : ady.get(v)) {                                           //@kahn:2
                            grado.merge(w, 1, Integer::sum);                                    //@kahn:2
                        }
                    }
                    ArrayDeque<String> cola = new ArrayDeque<>();
                    for (String v : ady.keySet()) {                                             //@kahn:3
                        if (grado.get(v) == 0) {                                                //@kahn:3
                            cola.add(v);                                                        //@kahn:3
                        }
                    }
                    List<String> orden = new ArrayList<>();
                    while (!cola.isEmpty()) {                                                   //@kahn:4
                        String v = cola.poll();                                                 //@kahn:5
                        orden.add(v);                                                           //@kahn:5
                        for (String w : ady.get(v)) {                                           //@kahn:6
                            grado.put(w, grado.get(w) - 1);                                     //@kahn:7,8
                            if (grado.get(w) == 0) {                                            //@kahn:8
                                cola.add(w);                                                    //@kahn:8
                            }
                        }
                    }
                    if (orden.size() < ady.size()) {                                            //@kahn:9
                        throw new IllegalStateException("Hay un ciclo: no existe orden topológico");   //@kahn:9
                    }
                    return orden;                                                               //@kahn:10
                }

                // DFS: un curso entra al orden (al inicio) cuando ya terminaron todos los que dependen de él.
                public List<String> ordenDfs() {                                                //@dfs:1
                    color.clear();
                    LinkedList<String> orden = new LinkedList<>();
                    for (String v : ady.keySet()) {                                             //@dfs:2
                        if (color.getOrDefault(v, Color.BLANCO) == Color.BLANCO) {              //@dfs:2
                            visitar(v, orden);                                                  //@dfs:2
                        }
                    }
                    return orden;                                                               //@dfs:3
                }

                private void visitar(String v, LinkedList<String> orden) {                      //@dfs:4
                    color.put(v, Color.GRIS);                                                   //@dfs:5
                    for (String w : ady.get(v)) {                                               //@dfs:6
                        Color c = color.getOrDefault(w, Color.BLANCO);                          //@dfs:7,8
                        if (c == Color.GRIS) {                                                  //@dfs:7
                            throw new IllegalStateException("Hay un ciclo: " + v + " -> " + w); //@dfs:7
                        }
                        if (c == Color.BLANCO) {                                                //@dfs:8
                            visitar(w, orden);                                                  //@dfs:8
                        }
                    }
                    color.put(v, Color.NEGRO);                                                  //@dfs:9
                    orden.addFirst(v);                                                          //@dfs:9
                }

                public static void main(String[] args) {
                    OrdenTopologico malla = new OrdenTopologico();
                    malla.agregarArco("Matemática", "Cálculo");
                    malla.agregarArco("Matemática", "Discreta");
                    malla.agregarArco("Programación", "POO");
                    malla.agregarArco("POO", "AED I");
                    malla.agregarArco("Discreta", "AED I");
                    malla.agregarArco("AED I", "AED II");
                    malla.agregarArco("Cálculo", "Estadística");
                    malla.agregarArco("AED II", "IA");
                    malla.agregarArco("Estadística", "IA");
                    System.out.println("Kahn: " + malla.kahn());
                    System.out.println("DFS:  " + malla.ordenDfs());
                    malla.agregarArco("AED II", "AED I");   // un prerrequisito mal digitado
                    try {
                        malla.kahn();
                    } catch (IllegalStateException e) {
                        System.out.println("Con AED II -> AED I: " + e.getMessage());
                    }
                }
            }
            """;

    public static final CodigoAlgoritmo CODIGO_KAHN = CodigoAlgoritmo.seccion(PSEUDO_KAHN, JAVA, "kahn");
    public static final CodigoAlgoritmo CODIGO_DFS = CodigoAlgoritmo.seccion(PSEUDO_DFS, JAVA, "dfs");

    // ------------------------------------------------------------------
    // Paso a paso: Kahn
    // ------------------------------------------------------------------

    public List<EstadoTopologico> pasosKahn(Map<String, String> nombres) {
        Function<String, String> n = v -> nombres.getOrDefault(v, v);
        Map<String, Integer> grado = gradosEntrada();
        Map<String, Estado> estados = estadosIniciales();
        ArrayDeque<String> cola = new ArrayDeque<>();
        List<String> orden = new ArrayList<>();
        Set<String> quitados = new LinkedHashSet<>();
        List<EstadoTopologico> pasos = new ArrayList<>();

        String masPedido = ady.keySet().stream().max((a, b) -> grado.get(a) - grado.get(b)).orElseThrow();
        pasos.add(new EstadoTopologico(estados, grado, List.copyOf(cola), orden, null, null, quitados, null,
                Tipo.INICIO, 2, varsKahn(n, null, null, grado, cola, orden),
                "grado = " + grado,
                "Se llama kahn(). Primero se cuenta, para cada curso, cuántos arcos LLEGAN a él: su grado de entrada,"
                        + " es decir, cuántos prerrequisitos le faltan (\"g = …\" sobre cada curso). Por ejemplo, "
                        + n.apply(masPedido) + " tiene grado " + grado.get(masPedido) + ": necesita "
                        + lista(n, predecesores(masPedido)) + ".",
                "kahn(): grados de entrada."));
        for (String v : ady.keySet()) {
            if (grado.get(v) == 0) {
                cola.add(v);
                estados.put(v, Estado.EN_FRONTERA);
            }
        }
        pasos.add(new EstadoTopologico(estados, grado, List.copyOf(cola), orden, null, null, quitados, null,
                Tipo.INICIO, 3, varsKahn(n, null, null, grado, cola, orden),
                "cola = " + cola,
                "Los cursos con grado 0 no tienen prerrequisitos: se pueden llevar desde ya. Entran a la cola: "
                        + lista(n, List.copyOf(cola)) + ". Los demás esperan a que su grado llegue a 0.",
                "Sin prerrequisitos: " + lista(n, List.copyOf(cola)) + "."));

        while (!cola.isEmpty()) {
            int enCola = cola.size();
            String v = cola.poll();
            orden.add(v);
            estados.put(v, Estado.TERMINADO);
            List<String> salen = ady.get(v);
            pasos.add(new EstadoTopologico(estados, grado, List.copyOf(cola), orden, v, null, quitados, null,
                    Tipo.SACA, 5, varsKahn(n, v, null, grado, cola, orden),
                    "v = cola.poll() = " + v + ";  orden.add(" + v + ")  →  orden = " + orden,
                    "El while consulta si la cola está vacía: no (" + enCola + (enCola == 1 ? " curso" : " cursos")
                            + "). Se saca el primero, " + n.apply(v) + ": todos sus prerrequisitos ya están en el"
                            + " orden, así que se agrega al FINAL (posición " + orden.size() + "). "
                            + (salen.isEmpty() ? n.apply(v) + " no es prerrequisito de ningún curso: el for no hace"
                            + " nada y el while vuelve a consultar la cola."
                            : salen.size() == 1 ? "Ahora el for recorre su único arco saliente: el curso que lo pide ("
                            + lista(n, salen) + ")." : "Ahora el for recorre sus " + salen.size() + " arcos"
                            + " salientes: los cursos que lo piden (" + lista(n, salen) + ")."),
                    n.apply(v) + " entra al orden (posición " + orden.size() + ")."));
            for (int k = 0; k < salen.size(); k++) {
                String w = salen.get(k);
                int antes = grado.get(w);
                grado.put(w, antes - 1);
                quitados.add(EstadoTopologico.arco(v, w));
                String fin = k == salen.size() - 1
                        ? " Era el último arco de " + n.apply(v) + ": el for termina y el while vuelve a consultar"
                        + " la cola." : " El for sigue con el siguiente arco.";
                String toma = "El for toma el arco " + n.apply(v) + " → " + n.apply(w) + ". Como " + n.apply(v)
                        + " ya está en el orden, a " + n.apply(w) + " le falta un prerrequisito menos: su grado baja de "
                        + antes + " a " + (antes - 1) + ". El if consulta si llegó a 0: ";
                if (antes - 1 == 0) {
                    cola.add(w);
                    estados.put(w, Estado.EN_FRONTERA);
                    pasos.add(new EstadoTopologico(estados, grado, List.copyOf(cola), orden, v, w, quitados, null,
                            Tipo.ENCOLA, 8, varsKahn(n, v, w, grado, cola, orden),
                            "grado[w] = " + antes + " - 1 = 0  →  grado.get(w) == 0  →  verdadero  →  cola.add(" + w
                                    + ")",
                            toma + "SÍ, ya están todos sus prerrequisitos en el orden, así que " + n.apply(w)
                                    + " entra al final de la cola." + fin,
                            n.apply(w) + " ya no espera a nadie: a la cola."));
                } else {
                    List<String> faltan = predecesores(w).stream().filter(p -> !orden.contains(p)).toList();
                    pasos.add(new EstadoTopologico(estados, grado, List.copyOf(cola), orden, v, w, quitados, null,
                            Tipo.DECREMENTA, 7, varsKahn(n, v, w, grado, cola, orden),
                            "grado[w] = " + antes + " - 1 = " + (antes - 1) + "  →  grado.get(w) == 0  →  falso",
                            toma + "no, " + n.apply(w) + " todavía espera a " + lista(n, faltan)
                                    + ", así que no entra a la cola." + fin,
                            n.apply(w) + ": grado " + antes + " → " + (antes - 1) + "."));
                }
            }
        }

        if (orden.size() < ady.size()) {
            List<String> fuera = ady.keySet().stream().filter(v -> !orden.contains(v)).toList();
            List<String> ciclo = encontrarCiclo();
            String arcoCiclo = EstadoTopologico.arco(ciclo.get(ciclo.size() - 1), ciclo.get(0));
            pasos.add(new EstadoTopologico(estados, grado, List.of(), orden, null, null, quitados, arcoCiclo,
                    Tipo.CICLO, 9, varsKahn(n, null, null, grado, cola, orden),
                    "orden.size() < ady.size()  →  " + orden.size() + " < " + ady.size()
                            + "  →  verdadero  →  throw",
                    "La cola quedó vacía, pero el if encuentra que solo " + orden.size() + " de " + ady.size()
                            + " cursos entraron al orden. Quedaron fuera " + lista(n, fuera) + ": sus grados nunca"
                            + " llegaron a 0 porque hay un CICLO (" + cadena(n, ciclo) + "). Cada curso del ciclo espera"
                            + " a otro del mismo ciclo, así que ninguno puede ser el primero. Se lanza una excepción: no"
                            + " existe orden topológico.",
                    "¡Ciclo! No existe orden topológico."));
            return pasos;
        }
        pasos.add(new EstadoTopologico(estados, grado, List.of(), orden, null, null, quitados, null,
                Tipo.FIN, 9, varsKahn(n, null, null, grado, cola, orden),
                "orden.size() < ady.size()  →  " + orden.size() + " < " + ady.size() + "  →  falso",
                "La cola quedó vacía. El if consulta si algún curso quedó fuera del orden: " + orden.size() + " < "
                        + ady.size() + " es falso, entraron todos, así que no hay ciclos y la ejecución pasa al"
                        + " return.",
                "Entraron los " + ady.size() + " cursos."));
        pasos.add(new EstadoTopologico(estados, grado, List.of(), orden, null, null, quitados, null,
                Tipo.FIN, 10, varsKahn(n, null, null, grado, cola, orden),
                "return orden  →  " + orden,
                "kahn() retorna el ORDEN TOPOLÓGICO: cada curso aparece después de todos sus prerrequisitos. Llevarlos"
                        + " en este orden siempre es válido. No es el único orden posible: con otra cola (o con DFS)"
                        + " sale otro, también correcto.",
                "Fin de Kahn."));
        return pasos;
    }

    private Map<String, Estado> estadosIniciales() {
        Map<String, Estado> e = new HashMap<>();
        ady.keySet().forEach(v -> e.put(v, Estado.NO_VISITADO));
        return e;
    }

    private static Map<String, String> varsKahn(Function<String, String> n, String v, String w,
                                                Map<String, Integer> grado, ArrayDeque<String> cola,
                                                List<String> orden) {
        return PasoTrazable.variables(
                "v", v == null ? "—" : n.apply(v),
                "w", w == null ? "—" : n.apply(w),
                "grado.get(w)", w == null ? "—" : grado.get(w),
                "cola", cola.toString(),
                "orden", orden.toString(),
                "orden.size()", orden.size());
    }

    private static String lista(Function<String, String> n, List<String> vs) {
        List<String> nombres = vs.stream().map(n).toList();
        if (nombres.size() <= 1) {
            return String.join("", nombres);
        }
        return String.join(", ", nombres.subList(0, nombres.size() - 1)) + " y " + nombres.get(nombres.size() - 1);
    }

    private static String cadena(Function<String, String> n, List<String> ciclo) {
        List<String> c = new ArrayList<>(ciclo.stream().map(n).toList());
        c.add(n.apply(ciclo.get(0)));
        return String.join(" → ", c);
    }

    // ------------------------------------------------------------------
    // Paso a paso: DFS
    // ------------------------------------------------------------------

    public List<EstadoTopologico> pasosDfs(Map<String, String> nombres) {
        RecorridoDfs r = new RecorridoDfs(v -> nombres.getOrDefault(v, v));
        try {
            List<String> saltados = new ArrayList<>();
            for (String v : ady.keySet()) {
                if (r.estados.get(v) == Estado.NO_VISITADO) {
                    String previos = saltados.isEmpty() ? ""
                            : " (antes pasó por " + lista(r.n, saltados) + ", que ya eran NEGROS)";
                    saltados.clear();
                    r.agregar(v, null, Tipo.LLAMA, 2, null,
                            "color.get(v) == BLANCO  →  verdadero  →  visitar(" + v + ")",
                            (r.pasos.isEmpty() ? "Se llama ordenDfs(). " : "")
                                    + "El for de ordenDfs toma " + r.n.apply(v) + previos + ": es BLANCO (nadie lo"
                                    + " visitó), así que llama visitar(" + r.n.apply(v) + ").",
                            "ordenDfs llama visitar(" + r.n.apply(v) + ").");
                    r.visitar(v, null, false);
                } else {
                    saltados.add(v);
                }
            }
            r.agregar(null, null, Tipo.FIN, 3, null,
                    "return orden  →  " + r.orden,
                    "El for de ordenDfs terminó" + (saltados.isEmpty() ? ""
                            : " (los cursos que le faltaban ya eran NEGROS)") + ": todos los cursos están NEGROS y en el orden. Cada curso se"
                            + " insertó al INICIO cuando ya habían terminado los que dependen de él, así que queda"
                            + " delante de ellos: es un orden topológico (distinto del de Kahn, pero igual de válido).",
                    "Fin de DFS.");
        } catch (CicloEncontrado e) {
            // El paso del ciclo ya se registró: la ejecución termina con la excepción.
        }
        return r.pasos;
    }

    /** Se lanza para cortar la recursión del paso a paso cuando aparece un arco de regreso. */
    private static final class CicloEncontrado extends RuntimeException {
        CicloEncontrado() {
            super(null, null, false, false);
        }
    }

    /** Estado mutable del paso a paso de DFS; cada llamada sabe quién la invocó. */
    private final class RecorridoDfs {
        final Function<String, String> n;
        final Map<String, Estado> estados = estadosIniciales();
        final List<String> pila = new ArrayList<>();
        final LinkedList<String> orden = new LinkedList<>();
        final Set<String> arbol = new LinkedHashSet<>();
        final List<EstadoTopologico> pasos = new ArrayList<>();

        RecorridoDfs(Function<String, String> n) {
            this.n = n;
        }

        void agregar(String v, String w, Tipo tipo, int linea, String arcoCiclo, String evaluacion,
                     String explicacion, String descripcion) {
            pasos.add(new EstadoTopologico(estados, Map.of(), pila, orden, v, w, arbol, arcoCiclo, tipo, linea,
                    PasoTrazable.variables(
                            "v", v == null ? "—" : n.apply(v),
                            "w", w == null ? "—" : n.apply(w),
                            "c", w == null ? "—" : color(estados.get(w)),
                            "orden", orden.toString()),
                    evaluacion, explicacion, descripcion));
        }

        /** @param padreSigue true si a quien la llamó todavía le quedan arcos por revisar */
        void visitar(String v, String quien, boolean padreSigue) {
            estados.put(v, Estado.EN_FRONTERA);
            pila.add(v);
            List<String> salen = ady.get(v);
            agregar(v, null, Tipo.VISITA, 5, null,
                    "color[" + v + "] = GRIS;  pila = " + pila,
                    "visitar(" + n.apply(v) + ") empieza (la llamó " + (quien == null ? "ordenDfs"
                            : "visitar(" + n.apply(quien) + ")") + "). " + n.apply(v) + " se pinta GRIS: está en la"
                            + " pila de llamadas, \"en proceso\". Si más adelante un arco llega a un curso GRIS, hay un"
                            + " ciclo. " + (salen.isEmpty() ? "Nadie depende de " + n.apply(v) + ": el for no tiene"
                            + " arcos que revisar." : salen.size() == 1 ? "El for revisará su único arco, hacia "
                            + lista(n, salen) + "." : "El for revisará sus " + salen.size() + " arcos: "
                            + lista(n, salen) + "."),
                    "visitar(" + n.apply(v) + "): GRIS.");
            for (int k = 0; k < salen.size(); k++) {
                String w = salen.get(k);
                String fin = k == salen.size() - 1 ? " Era el último arco de " + n.apply(v) + "."
                        : " El for sigue con el siguiente arco.";
                String toma = "El for de visitar(" + n.apply(v) + ") toma el arco " + n.apply(v) + " → "
                        + n.apply(w) + ". ";
                Estado c = estados.get(w);
                if (c == Estado.EN_FRONTERA) {
                    List<String> ciclo = pila.subList(pila.indexOf(w), pila.size());
                    agregar(v, w, Tipo.CICLO, 7, EstadoTopologico.arco(v, w),
                            "c == GRIS  →  verdadero  →  throw",
                            toma + "El if consulta si " + n.apply(w) + " es GRIS: SÍ. " + n.apply(w) + " todavía está"
                                    + " en la pila de llamadas (" + lista(n, pila) + "): desde " + n.apply(w)
                                    + " se llegó a " + n.apply(v) + " y ahora " + n.apply(v) + " apunta de vuelta a "
                                    + n.apply(w) + ". Es un ARCO DE REGRESO y forma el ciclo " + cadena(n, ciclo)
                                    + ". Se lanza una excepción: no existe orden topológico.",
                            "¡Ciclo! " + n.apply(v) + " → " + n.apply(w) + " vuelve a un curso GRIS.");
                    throw new CicloEncontrado();
                }
                if (c == Estado.NO_VISITADO) {
                    arbol.add(EstadoTopologico.arco(v, w));
                    agregar(v, w, Tipo.LLAMA, 8, null,
                            "c = BLANCO  →  c == GRIS: falso;  c == BLANCO: verdadero  →  visitar(" + w + ")",
                            toma + "El if de la línea 7 consulta si " + n.apply(w) + " es GRIS (habría un ciclo): no."
                                    + " El de la línea 8 consulta si es BLANCO: sí, nadie lo ha visitado. visitar("
                                    + n.apply(v) + ") se pausa y llama visitar(" + n.apply(w) + "); cuando termine, la"
                                    + " ejecución regresará aquí, al for de " + n.apply(v) + ".",
                            "visitar(" + n.apply(v) + ") llama visitar(" + n.apply(w) + ").");
                    visitar(w, v, k < salen.size() - 1);
                } else {
                    agregar(v, w, Tipo.YA_TERMINADO, 8, null,
                            "c = NEGRO  →  c == GRIS: falso;  c == BLANCO: falso",
                            toma + n.apply(w) + " es NEGRO: ya terminó y ya está en el orden. No es GRIS (no hay"
                                    + " ciclo) ni BLANCO, así que no se llama de nuevo. Como " + n.apply(v) + " se"
                                    + " insertará al inicio DESPUÉS, quedará delante de " + n.apply(w)
                                    + ", que es lo correcto." + fin,
                            n.apply(w) + " ya está NEGRO.");
                }
            }
            estados.put(v, Estado.TERMINADO);
            pila.remove(pila.size() - 1);
            orden.addFirst(v);
            agregar(v, null, Tipo.TERMINA, 9, null,
                    "color[" + v + "] = NEGRO;  orden.addFirst(" + v + ")  →  orden = " + orden,
                    (salen.isEmpty() ? n.apply(v) + " no tiene arcos salientes: nadie depende de él. "
                            : "Se revisaron todos los arcos de " + n.apply(v) + ": todo lo que depende de él ya está"
                            + " en el orden. ") + n.apply(v) + " se pinta NEGRO y se inserta al INICIO del orden"
                            + (salen.isEmpty() ? "." : ", delante de esos cursos.") + " visitar(" + n.apply(v)
                            + ") termina y regresa a " + (quien == null ? "ordenDfs, que sigue con su for."
                            : "visitar(" + n.apply(quien) + "), " + (padreSigue
                            ? "que sigue con su siguiente arco." : "a la que ya no le quedan arcos: también termina.")),
                    n.apply(v) + " termina: NEGRO, al inicio del orden.");
        }

        private String color(Estado e) {
            return switch (e) {
                case NO_VISITADO -> "BLANCO";
                case EN_FRONTERA -> "GRIS";
                case TERMINADO -> "NEGRO";
            };
        }
    }
}
