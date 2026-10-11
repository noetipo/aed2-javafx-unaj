package pe.edu.unaj.aed2.semana09.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana07.modelo.EstadoRecorrido;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana08.modelo.GrafoPonderado;
import pe.edu.unaj.aed2.semana09.modelo.EstadoArbolExpansion.Tipo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.function.Function;

/**
 * Árbol de expansión mínima (MST) de un grafo ponderado no dirigido: los tramos que conectan a TODOS los
 * vértices con el menor peso total. Dos algoritmos voraces: Kruskal (tramos de menor a mayor, con union-find)
 * y Prim (crece desde un vértice con una cola de prioridad).
 */
public final class ArbolExpansion {

    /** Tramo u–v con su peso. En Prim, u es el pueblo que ya está en el árbol y v al que se llegaría. */
    public record Tramo(String u, String v, int km) {
        public String clave() {
            return EstadoRecorrido.arista(u, v);
        }
    }

    /** Posibles tendidos de fibra óptica entre los pueblos de la red (semana 07). */
    public static final String TENDIDOS = "/pe/edu/unaj/aed2/semana09/tendidos.csv";

    private ArbolExpansion() {
    }

    /** Los pueblos de la red vial con los posibles tendidos como aristas. */
    public static RedVial cargarTendidos() {
        return RedVial.cargar(TENDIDOS);
    }

    /** Cada arista una sola vez (u &lt; v), en el orden de la lista de adyacencia. */
    public static List<Tramo> tramos(GrafoPonderado g) {
        List<Tramo> lista = new ArrayList<>();
        for (String u : g.vertices()) {
            for (GrafoPonderado.Arista a : g.vecinos(u)) {
                if (u.compareTo(a.destino()) < 0) {
                    lista.add(new Tramo(u, a.destino(), a.km()));
                }
            }
        }
        return lista;
    }

    private static List<Tramo> tramosDesde(GrafoPonderado g, String u) {
        return g.vecinos(u).stream().map(a -> new Tramo(u, a.destino(), a.km())).toList();
    }

    public static int total(List<Tramo> arbol) {
        return arbol.stream().mapToInt(Tramo::km).sum();
    }

    // ------------------------------------------------------------------
    // Kruskal y Prim (siguen el código de la pestaña Java)
    // ------------------------------------------------------------------

    public static List<Tramo> kruskal(GrafoPonderado g) {
        List<Tramo> orden = new ArrayList<>(tramos(g));
        orden.sort(Comparator.comparingInt(Tramo::km));
        List<Tramo> arbol = new ArrayList<>();
        UnionFind<String> grupos = new UnionFind<>();
        for (Tramo t : orden) {
            if (grupos.unir(t.u(), t.v())) {
                arbol.add(t);
                if (arbol.size() == g.vertices().size() - 1) {
                    break;
                }
            }
        }
        return arbol;
    }

    public static List<Tramo> prim(GrafoPonderado g, String s) {
        Set<String> enArbol = new LinkedHashSet<>();
        List<Tramo> arbol = new ArrayList<>();
        PriorityQueue<Tramo> cola = new PriorityQueue<>(Comparator.comparingInt(Tramo::km));
        enArbol.add(s);
        cola.addAll(tramosDesde(g, s));
        while (!cola.isEmpty() && enArbol.size() < g.vertices().size()) {
            Tramo t = cola.poll();
            if (enArbol.contains(t.v())) {
                continue;
            }
            arbol.add(t);
            enArbol.add(t.v());
            for (Tramo sig : tramosDesde(g, t.v())) {
                if (!enArbol.contains(sig.v())) {
                    cola.add(sig);
                }
            }
        }
        return arbol;
    }

    // ------------------------------------------------------------------
    // Pseudocódigo y código Java
    // ------------------------------------------------------------------

    public static final List<String> PSEUDO_KRUSKAL = List.of(
            "kruskal():",
            "    ordenar los tramos de menor a mayor km;  arbol ← []",
            "    cada pueblo forma un grupo solo (union-find)",
            "    para cada tramo (u, v, km) en ese orden:",
            "        si encontrar(u) ≠ encontrar(v):          // ¿están en grupos distintos?",
            "            arbol.agregar((u, v, km));  unir(u, v)",
            "            si arbol tiene n − 1 tramos: terminar  // ya están todos conectados",
            "    retornar arbol");

    public static final List<String> PSEUDO_PRIM = List.of(
            "prim(s):",
            "    enArbol ← {s};  arbol ← [];  cola.insertar(cada tramo de s)",
            "    mientras la cola no esté vacía y falten pueblos:",
            "        (u, v, km) ← cola.extraerMin()           // el tramo más barato que sale del árbol",
            "        si v ∈ enArbol: continuar                // v ya está conectado: cerraría un ciclo",
            "        arbol.agregar((u, v, km));  enArbol.agregar(v)",
            "        para cada tramo (v, w, p):",
            "            si w ∉ enArbol: cola.insertar((v, w, p))",
            "    retornar arbol");

    static final String JAVA = """
            import java.util.ArrayList;
            import java.util.Comparator;
            import java.util.HashMap;
            import java.util.HashSet;
            import java.util.LinkedHashMap;
            import java.util.List;
            import java.util.Map;
            import java.util.PriorityQueue;
            import java.util.Set;

            public class ArbolExpansion {

                record Tramo(String u, String v, int km) {
                    @Override
                    public String toString() {
                        return u + "-" + v + " " + km;
                    }
                }

                private final Map<String, List<Tramo>> ady = new LinkedHashMap<>();
                private final List<Tramo> tramos = new ArrayList<>();
                private final Map<String, String> jefe = new HashMap<>();   // union-find

                public void agregarTramo(String u, String v, int km) {
                    tramos.add(new Tramo(u, v, km));
                    ady.computeIfAbsent(u, k -> new ArrayList<>()).add(new Tramo(u, v, km));
                    ady.computeIfAbsent(v, k -> new ArrayList<>()).add(new Tramo(v, u, km));
                }

                // Union-find: el representante del grupo de x (de paso, comprime el camino).
                private String encontrar(String x) {
                    jefe.putIfAbsent(x, x);                  // un pueblo nuevo es jefe de sí mismo
                    if (!jefe.get(x).equals(x)) {
                        jefe.put(x, encontrar(jefe.get(x)));
                    }
                    return jefe.get(x);
                }

                // Kruskal: revisa los tramos de menor a mayor y acepta los que unen dos grupos distintos.
                public List<Tramo> kruskal() {                                              //@kruskal:1
                    List<Tramo> orden = new ArrayList<>(tramos);
                    orden.sort(Comparator.comparingInt(Tramo::km));                         //@kruskal:2
                    List<Tramo> arbol = new ArrayList<>();                                  //@kruskal:2
                    jefe.clear();                                                           //@kruskal:3
                    for (Tramo t : orden) {                                                 //@kruskal:4
                        if (!encontrar(t.u()).equals(encontrar(t.v()))) {                   //@kruskal:5
                            arbol.add(t);                                                   //@kruskal:6
                            jefe.put(encontrar(t.u()), encontrar(t.v()));                   //@kruskal:6
                            if (arbol.size() == ady.size() - 1) {                           //@kruskal:7
                                break;                                                      //@kruskal:7
                            }
                        }
                    }
                    return arbol;                                                           //@kruskal:8
                }

                // Prim: crece desde s agregando siempre el tramo más barato que sale del árbol.
                public List<Tramo> prim(String s) {                                         //@prim:1
                    Set<String> enArbol = new HashSet<>();
                    List<Tramo> arbol = new ArrayList<>();
                    PriorityQueue<Tramo> cola = new PriorityQueue<>(Comparator.comparingInt(Tramo::km));
                    enArbol.add(s);                                                         //@prim:2
                    cola.addAll(ady.get(s));                                                //@prim:2
                    while (!cola.isEmpty() && enArbol.size() < ady.size()) {                //@prim:3
                        Tramo t = cola.poll();                                              //@prim:4
                        if (enArbol.contains(t.v())) {                                      //@prim:5
                            continue;                                                       //@prim:5
                        }
                        arbol.add(t);                                                       //@prim:6
                        enArbol.add(t.v());                                                 //@prim:6
                        for (Tramo sig : ady.get(t.v())) {                                  //@prim:7
                            if (!enArbol.contains(sig.v())) {                               //@prim:8
                                cola.add(sig);                                              //@prim:8
                            }
                        }
                    }
                    return arbol;                                                           //@prim:9
                }

                public static int total(List<Tramo> arbol) {
                    int suma = 0;
                    for (Tramo t : arbol) {
                        suma += t.km();
                    }
                    return suma;
                }

                public static void main(String[] args) {
                    ArbolExpansion red = new ArbolExpansion();
                    red.agregarTramo("Juliaca", "Puno", %s);
                    red.agregarTramo("Juliaca", "Azángaro", %s);
                    red.agregarTramo("Juliaca", "Huancané", %s);
                    red.agregarTramo("Azángaro", "Putina", %s);
                    red.agregarTramo("Putina", "Huancané", %s);
                    red.agregarTramo("Azángaro", "Huancané", %s);
                    List<Tramo> k = red.kruskal();
                    System.out.println("Kruskal: " + k + " = " + total(k) + " km");
                    List<Tramo> p = red.prim("Juliaca");
                    System.out.println("Prim desde Juliaca: " + p + " = " + total(p) + " km");
                }
            }
            """;

    /** Código Java con los km del main tomados de la red (así coinciden con la aplicación). */
    public static String java(GrafoPonderado g) {
        return String.format(JAVA, g.km("JUL", "PUN"), g.km("JUL", "AZA"), g.km("JUL", "HUA"),
                g.km("AZA", "PUT"), g.km("PUT", "HUA"), g.km("AZA", "HUA"));
    }

    public static CodigoAlgoritmo codigoKruskal(GrafoPonderado g) {
        return CodigoAlgoritmo.seccion(PSEUDO_KRUSKAL, java(g), "kruskal");
    }

    public static CodigoAlgoritmo codigoPrim(GrafoPonderado g) {
        return CodigoAlgoritmo.seccion(PSEUDO_PRIM, java(g), "prim");
    }

    // ------------------------------------------------------------------
    // Paso a paso: Kruskal
    // ------------------------------------------------------------------

    public static List<EstadoArbolExpansion> pasosKruskal(GrafoPonderado g, Map<String, String> nombres) {
        Function<String, String> n = v -> nombres.getOrDefault(v, v);
        List<Tramo> orden = new ArrayList<>(tramos(g));
        orden.sort(Comparator.comparingInt(Tramo::km));
        int pueblos = g.vertices().size();
        UnionFind<String> uf = new UnionFind<>();
        List<String> arbol = new ArrayList<>();
        Set<String> descartadas = new LinkedHashSet<>();
        Map<String, Integer> grupos = new HashMap<>();
        List<EstadoArbolExpansion> pasos = new ArrayList<>();
        int total = 0;

        pasos.add(new EstadoArbolExpansion(arbol, descartadas, Set.of(), null, null, grupos, Set.of(),
                pendientes(orden, 0), 0, Tipo.INICIO, 2,
                varsKruskal(n, null, null, null, arbol, 0, pueblos),
                "orden = " + pendientes(orden, 0).size() + " tramos de " + orden.get(0).km() + " a "
                        + orden.get(orden.size() - 1).km() + " km;  arbol = []",
                "Se llama kruskal(). Primero se ordenan los " + orden.size() + " tramos de MENOR a MAYOR km (abajo"
                        + " del mapa se ven en ese orden). Kruskal es VORAZ: siempre prueba el tramo más barato que"
                        + " queda y lo acepta si sirve, sin volver atrás. El árbol empieza vacío.",
                "kruskal(): tramos ordenados por km."));
        pasos.add(new EstadoArbolExpansion(arbol, descartadas, Set.of(), null, null, grupos, Set.of(),
                pendientes(orden, 0), 0, Tipo.INICIO, 3,
                varsKruskal(n, null, null, null, arbol, 0, pueblos),
                "jefe = {}  →  " + pueblos + " grupos de un solo pueblo",
                "jefe.clear() deja a cada uno de los " + pueblos + " pueblos como jefe de sí mismo: hay " + pueblos
                        + " grupos de un solo pueblo (en el mapa, todos blancos). Union-find guarda para cada pueblo"
                        + " su \"jefe\"; dos pueblos están conectados si al subir por los jefes llegan al mismo"
                        + " representante. Así se sabe en casi O(1) si un tramo cerraría un ciclo.",
                pueblos + " grupos de un pueblo."));

        for (int k = 0; k < orden.size(); k++) {
            Tramo t = orden.get(k);
            String ru = uf.encontrar(t.u());
            String rv = uf.encontrar(t.v());
            String tramo = n.apply(t.u()) + " – " + n.apply(t.v()) + " (" + t.km() + " km)";
            String consulta = "El for toma el siguiente tramo más barato: " + tramo + ". El if le pregunta a"
                    + " union-find si sus extremos ya están conectados: encontrar(" + n.apply(t.u()) + ") = "
                    + n.apply(ru) + " y encontrar(" + n.apply(t.v()) + ") = " + n.apply(rv) + ".";
            if (!ru.equals(rv)) {
                int antesU = tamanio(grupos, t.u());
                int antesV = tamanio(grupos, t.v());
                uf.unir(t.u(), t.v());
                unirGrupos(grupos, t.u(), t.v());
                arbol.add(t.clave());
                total += t.km();
                boolean completo = arbol.size() == pueblos - 1;
                pasos.add(new EstadoArbolExpansion(arbol, descartadas, Set.of(), t.u(), t.v(), grupos, Set.of(),
                        pendientes(orden, k + 1), total, Tipo.ACEPTA, 6,
                        varsKruskal(n, t, ru, rv, arbol, total, pueblos),
                        "encontrar(t.u()) != encontrar(t.v())  →  " + n.apply(ru) + " != " + n.apply(rv)
                                + "  →  verdadero  →  arbol.add(t);  jefe[" + n.apply(ru) + "] = " + n.apply(rv),
                        consulta + " Son grupos DISTINTOS (" + descripcionGrupo(n, t.u(), antesU) + " y "
                                + descripcionGrupo(n, t.v(), antesV) + "), así que el tramo no cierra ningún ciclo:"
                                + " ENTRA al árbol, que ya lleva " + arbol.size() + " de " + (pueblos - 1)
                                + " tramos y " + total + " km. unir hace que el jefe de " + n.apply(ru) + " sea "
                                + n.apply(rv) + ": los dos grupos quedan como uno solo ("
                                + (pueblos - arbol.size() == 1 ? "ya hay un único grupo)." : (pueblos - arbol.size())
                                + " grupos en total).")
                                + " El if de la línea 7 consulta si ya hay n − 1 = " + (pueblos - 1)
                                + " tramos: " + arbol.size() + " == " + (pueblos - 1) + " es "
                                + (completo ? "verdadero (lo explica el paso siguiente)."
                                : "falso, así que el for sigue con el siguiente tramo."),
                        "Acepta " + n.apply(t.u()) + " – " + n.apply(t.v()) + " (" + t.km() + " km)."));
                if (completo) {
                    int sinRevisar = orden.size() - k - 1;
                    pasos.add(new EstadoArbolExpansion(arbol, descartadas, Set.of(), null, null, grupos, Set.of(),
                            pendientes(orden, k + 1), total, Tipo.FIN, 7,
                            varsKruskal(n, t, null, null, arbol, total, pueblos),
                            "arbol.size() == ady.size() - 1  →  " + arbol.size() + " == " + (pueblos - 1)
                                    + "  →  verdadero  →  break",
                            "El if consulta si el árbol ya tiene n − 1 tramos: " + arbol.size() + " == "
                                    + (pueblos - 1) + " es verdadero. Un árbol con " + pueblos + " pueblos tiene"
                                    + " exactamente " + (pueblos - 1) + " tramos, así que ya están TODOS en un solo"
                                    + " grupo. break sale del for sin revisar " + (sinRevisar == 1 ? "el tramo que"
                                    + " falta" : "los " + sinRevisar + " tramos que faltan") + ": cualquiera de ellos"
                                    + " cerraría un ciclo.",
                            "Árbol completo: " + arbol.size() + " tramos."));
                    break;
                }
            } else {
                descartadas.add(t.clave());
                pasos.add(new EstadoArbolExpansion(arbol, descartadas, Set.of(), t.u(), t.v(), grupos, Set.of(),
                        pendientes(orden, k + 1), total, Tipo.DESCARTA, 5,
                        varsKruskal(n, t, ru, rv, arbol, total, pueblos),
                        "encontrar(t.u()) != encontrar(t.v())  →  " + n.apply(ru) + " != " + n.apply(rv)
                                + "  →  falso",
                        consulta + " Es el MISMO representante: " + n.apply(t.u()) + " y " + n.apply(t.v())
                                + " ya están conectados por tramos más baratos del árbol. Agregar este cerraría un"
                                + " CICLO y solo gastaría " + t.km() + " km de más, así que se descarta: el if no"
                                + " entra y el for pasa al siguiente tramo.",
                        "Descarta " + n.apply(t.u()) + " – " + n.apply(t.v()) + ": cerraría un ciclo."));
            }
        }
        boolean conexo = arbol.size() == pueblos - 1;
        pasos.add(new EstadoArbolExpansion(arbol, descartadas, Set.of(), null, null, grupos, Set.of(),
                List.of(), total, Tipo.FIN, 8, varsKruskal(n, null, null, null, arbol, total, pueblos),
                "return arbol  →  " + tramos(arbol.size()) + ", " + total + " km",
                conexo ? "kruskal() retorna el ÁRBOL DE EXPANSIÓN MÍNIMA: " + arbol.size() + " tramos que conectan los "
                        + pueblos + " pueblos con " + total + " km en total. Ningún otro conjunto de tramos que los"
                        + " conecte a todos usa menos km."
                        : "Se revisaron todos los tramos y quedaron " + (pueblos - arbol.size()) + " grupos: el grafo"
                        + " no es conexo, así que kruskal() retorna un BOSQUE de expansión mínima (" + total + " km).",
                "Fin de Kruskal: " + total + " km."));
        return pasos;
    }

    private static String tramos(int cantidad) {
        return cantidad == 1 ? "1 tramo" : cantidad + " tramos";
    }

    private static List<String> pendientes(List<Tramo> orden, int desde) {
        return orden.subList(desde, orden.size()).stream().map(t -> "(" + t.km() + ", " + t.clave() + ")").toList();
    }

    private static int tamanio(Map<String, Integer> grupos, String v) {
        Integer g = grupos.get(v);
        return g == null ? 1 : (int) grupos.values().stream().filter(g::equals).count();
    }

    private static String descripcionGrupo(Function<String, String> n, String v, int tamanio) {
        return tamanio == 1 ? n.apply(v) + " estaba solo" : "el de " + n.apply(v) + " tiene " + tamanio + " pueblos";
    }

    /** Números de grupo estables: al unir dos grupos queda el número menor; un grupo nuevo toma el menor libre. */
    private static void unirGrupos(Map<String, Integer> grupos, String u, String v) {
        Integer gu = grupos.get(u);
        Integer gv = grupos.get(v);
        if (gu == null && gv == null) {
            int libre = 1;
            while (grupos.containsValue(libre)) {
                libre++;
            }
            grupos.put(u, libre);
            grupos.put(v, libre);
        } else if (gu == null) {
            grupos.put(u, gv);
        } else if (gv == null) {
            grupos.put(v, gu);
        } else {
            int queda = Math.min(gu, gv);
            int sale = Math.max(gu, gv);
            grupos.replaceAll((x, num) -> num == sale ? queda : num);
        }
    }

    private static Map<String, String> varsKruskal(Function<String, String> n, Tramo t, String ru, String rv,
                                                   List<String> arbol, int total, int pueblos) {
        return PasoTrazable.variables(
                "t", t == null ? "—" : texto(n, t),
                "encontrar(t.u())", ru == null ? "—" : n.apply(ru),
                "encontrar(t.v())", rv == null ? "—" : n.apply(rv),
                "arbol.size()", arbol.size(),
                "ady.size()", pueblos,
                "arbol", tramos(arbol.size()) + ", " + total + " km");
    }

    // ------------------------------------------------------------------
    // Paso a paso: Prim
    // ------------------------------------------------------------------

    public static List<EstadoArbolExpansion> pasosPrim(GrafoPonderado g, String s, Map<String, String> nombres) {
        Function<String, String> n = v -> nombres.getOrDefault(v, v);
        int pueblos = g.vertices().size();
        Set<String> enArbol = new LinkedHashSet<>();
        List<String> arbol = new ArrayList<>();
        Set<String> descartadas = new LinkedHashSet<>();
        PriorityQueue<Tramo> cola = new PriorityQueue<>(Comparator.comparingInt(Tramo::km));
        List<EstadoArbolExpansion> pasos = new ArrayList<>();
        int total = 0;

        enArbol.add(s);
        List<Tramo> iniciales = tramosDesde(g, s);
        cola.addAll(iniciales);
        pasos.add(prim(arbol, descartadas, cola, null, enArbol, 0, Tipo.INICIO, 2,
                varsPrim(n, null, null, enArbol, cola, arbol, 0, pueblos),
                "enArbol = {" + n.apply(s) + "};  cola = " + textoCola(n, cola),
                "Se llama prim(" + n.apply(s) + "). El árbol empieza con un solo pueblo, " + n.apply(s) + ", y crece"
                        + " de a un pueblo por vuelta. " + (iniciales.size() == 1 ? "El único tramo que sale de "
                        + n.apply(s) + " entra" : "Los " + iniciales.size() + " tramos que salen de " + n.apply(s)
                        + " entran") + " a la cola de prioridad (el montículo de la semana 06): son los candidatos para"
                        + " conectar al siguiente pueblo. La cola siempre entrega el más barato.",
                "prim(" + n.apply(s) + ")"));

        while (!cola.isEmpty() && enArbol.size() < pueblos) {
            Tramo t = cola.poll();
            String sale = "El while consulta: la cola no está vacía y faltan pueblos (" + enArbol.size() + " de "
                    + pueblos + " en el árbol), así que sigue. extraerMin() saca el tramo MÁS BARATO de la cola: "
                    + n.apply(t.u()) + " → " + n.apply(t.v()) + " (" + t.km() + " km).";
            if (enArbol.contains(t.v())) {
                descartadas.add(t.clave());
                pasos.add(prim(arbol, descartadas, cola, t, enArbol, total, Tipo.DESCARTA, 5,
                        varsPrim(n, t, null, enArbol, cola, arbol, total, pueblos),
                        "enArbol.contains(t.v())  →  " + n.apply(t.v()) + " en el árbol  →  verdadero  →  continue",
                        sale + " El if consulta si " + n.apply(t.v()) + " ya está en el árbol: SÍ, entró antes por un"
                                + " tramo igual o más barato. Este tramo uniría dos pueblos ya conectados y cerraría un"
                                + " CICLO, así que continue lo descarta y el while vuelve a consultar la cola.",
                        "Descarta " + n.apply(t.u()) + " – " + n.apply(t.v()) + ": cerraría un ciclo."));
                continue;
            }
            arbol.add(t.clave());
            enArbol.add(t.v());
            total += t.km();
            List<Tramo> siguientes = tramosDesde(g, t.v());
            pasos.add(prim(arbol, descartadas, cola, t, enArbol, total, Tipo.ACEPTA, 6,
                    varsPrim(n, t, null, enArbol, cola, arbol, total, pueblos),
                    "enArbol.contains(t.v())  →  falso  →  arbol.add(t);  enArbol.add(" + n.apply(t.v()) + ")",
                    sale + " El if consulta si " + n.apply(t.v()) + " ya está en el árbol: no, así que el tramo"
                            + " ENTRA: el árbol lleva " + tramos(arbol.size()) + " y " + total + " km, y "
                            + n.apply(t.v()) + " queda conectado. ¿Por qué es seguro elegirlo? Es el tramo más barato"
                            + " que cruza de los pueblos conectados a los que faltan; todo árbol debe cruzar esa"
                            + " frontera con algún tramo, y ninguno cuesta menos (propiedad del corte). Ahora el for"
                            + " revisará " + (siguientes.size() == 1 ? "el único tramo"
                            : "los " + siguientes.size() + " tramos") + " de " + n.apply(t.v()) + ".",
                    "Conecta " + n.apply(t.v()) + " por " + n.apply(t.u()) + " (" + t.km() + " km)."));
            for (int k = 0; k < siguientes.size(); k++) {
                Tramo sig = siguientes.get(k);
                String ultimo = k == siguientes.size() - 1
                        ? " Era el último tramo de " + n.apply(t.v()) + ": el for termina y el while vuelve a"
                        + " consultar la cola." : " El for sigue con el siguiente tramo.";
                String toma = "El for toma el tramo " + n.apply(sig.u()) + " – " + n.apply(sig.v()) + " (" + sig.km()
                        + " km). El if consulta si " + n.apply(sig.v()) + " todavía NO está en el árbol: ";
                if (!enArbol.contains(sig.v())) {
                    cola.add(sig);
                    pasos.add(prim(arbol, descartadas, cola, sig, enArbol, total, Tipo.INSERTA, 8,
                            varsPrim(n, t, sig, enArbol, cola, arbol, total, pueblos),
                            "!enArbol.contains(sig.v())  →  verdadero  →  cola.add(" + texto(n, sig) + ")",
                            toma + "es verdadero, así que el tramo es un CANDIDATO para conectarlo y se inserta en la"
                                    + " cola. Todavía no se decide nada: quizá aparezca otro tramo más barato hacia "
                                    + n.apply(sig.v()) + "." + ultimo,
                            "Candidato " + n.apply(sig.u()) + " – " + n.apply(sig.v()) + " (" + sig.km() + " km)."));
                } else {
                    boolean regreso = sig.v().equals(t.u());
                    pasos.add(prim(arbol, descartadas, cola, sig, enArbol, total, Tipo.OMITE, 8,
                            varsPrim(n, t, sig, enArbol, cola, arbol, total, pueblos),
                            "!enArbol.contains(sig.v())  →  falso",
                            toma + "es falso, " + n.apply(sig.v()) + " ya está conectado"
                                    + (regreso ? " (es el pueblo desde donde se acaba de llegar)" : "")
                                    + ". Insertarlo solo serviría para cerrar un ciclo, así que no entra a la cola."
                                    + ultimo,
                            n.apply(sig.v()) + " ya está en el árbol."));
                }
            }
        }
        boolean conexo = enArbol.size() == pueblos;
        String motivo = conexo
                ? "ya están los " + pueblos + " pueblos en el árbol (los tramos que quedan en la cola solo cerrarían"
                + " ciclos)"
                : "la cola quedó vacía pero solo se conectaron " + enArbol.size() + " de " + pueblos + " pueblos:"
                + " el grafo no es conexo";
        pasos.add(prim(arbol, descartadas, cola, null, enArbol, total, Tipo.FIN, 9,
                varsPrim(n, null, null, enArbol, cola, arbol, total, pueblos),
                "return arbol  →  " + tramos(arbol.size()) + ", " + total + " km",
                "El while termina porque " + motivo + ". prim(" + n.apply(s) + ") retorna " + tramos(arbol.size())
                        + " con " + total + " km. Es el mismo total que da Kruskal: el peso del árbol de"
                        + " expansión mínima no depende del algoritmo ni del pueblo de inicio.",
                "Fin de Prim: " + total + " km."));
        return pasos;
    }

    private static EstadoArbolExpansion prim(List<String> arbol, Set<String> descartadas, PriorityQueue<Tramo> cola,
                                             Tramo t, Set<String> enArbol, int total, Tipo tipo, int linea,
                                             Map<String, String> variables, String evaluacion, String explicacion,
                                             String descripcion) {
        List<Tramo> ordenada = cola.stream().sorted(Comparator.comparingInt(Tramo::km)).toList();
        Set<String> candidatas = new LinkedHashSet<>();
        ordenada.forEach(x -> candidatas.add(x.clave()));
        return new EstadoArbolExpansion(arbol, descartadas, candidatas, t == null ? null : t.u(),
                t == null ? null : t.v(), Map.of(), enArbol,
                ordenada.stream().map(x -> "(" + x.km() + ", " + x.u() + "-" + x.v() + ")").toList(), total, tipo,
                linea, variables, evaluacion, explicacion, descripcion);
    }

    private static String texto(Function<String, String> n, Tramo t) {
        return n.apply(t.u()) + "-" + n.apply(t.v()) + " " + t.km();
    }

    private static String textoCola(Function<String, String> n, PriorityQueue<Tramo> cola) {
        return "[" + String.join(", ", cola.stream().sorted(Comparator.comparingInt(Tramo::km))
                .map(t -> "(" + t.km() + ", " + n.apply(t.u()) + "-" + n.apply(t.v()) + ")").toList()) + "]";
    }

    private static Map<String, String> varsPrim(Function<String, String> n, Tramo t, Tramo sig, Set<String> enArbol,
                                                PriorityQueue<Tramo> cola, List<String> arbol, int total,
                                                int pueblos) {
        return PasoTrazable.variables(
                "t", t == null ? "—" : texto(n, t),
                "sig", sig == null ? "—" : texto(n, sig),
                "enArbol", "{" + String.join(", ", enArbol.stream().map(n).toList()) + "}",
                "enArbol.size()", enArbol.size(),
                "ady.size()", pueblos,
                "cola", textoCola(n, cola),
                "arbol", tramos(arbol.size()) + ", " + total + " km");
    }
}
