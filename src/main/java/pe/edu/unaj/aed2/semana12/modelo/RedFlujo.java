package pe.edu.unaj.aed2.semana12.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana12.modelo.EstadoFlujo.Tipo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Red de flujo: grafo dirigido donde cada arco tiene una CAPACIDAD (aquí, camiones por día). Calcula el flujo
 * máximo de s a t con Edmonds–Karp: caminos de aumento hallados con BFS sobre la red residual.
 * <p>
 * Se guarda como matrices (igual que la pestaña Java): flujo[v][u] = −flujo[u][v], y el residual de u → v es
 * cap[u][v] − flujo[u][v]. Así, si hay flujo en u → v, el residual de v → u es positivo: se puede "devolver".
 */
public class RedFlujo {

    /** Arco u → v con su capacidad. */
    public record Arco(String u, String v, int capacidad) {
        public String clave() {
            return EstadoFlujo.arco(u, v);
        }
    }

    /** Resultado final: flujo total, flujo de cada arco y el corte mínimo. */
    public record Resultado(int total, Map<String, Integer> flujo, Set<String> ladoS, List<Arco> corte) {
    }

    public static final String RUTAS_LECHE = "/pe/edu/unaj/aed2/semana12/rutas_leche.csv";

    private final List<String> vertices = new ArrayList<>();
    private final List<Arco> arcos = new ArrayList<>();

    public void agregarVertice(String v) {
        if (!vertices.contains(v)) {
            vertices.add(v);
        }
    }

    public void agregarArco(String u, String v, int capacidad) {
        if (capacidad < 0 || u.equals(v)) {
            throw new IllegalArgumentException("Arco inválido: " + u + " -> " + v);
        }
        if (arcos.stream().anyMatch(a -> (a.u().equals(u) && a.v().equals(v)) || (a.u().equals(v) && a.v().equals(u)))) {
            throw new IllegalArgumentException("Ya hay un arco entre " + u + " y " + v);
        }
        agregarVertice(u);
        agregarVertice(v);
        arcos.add(new Arco(u, v, capacidad));
    }

    public List<String> vertices() {
        return List.copyOf(vertices);
    }

    public List<Arco> arcos() {
        return List.copyOf(arcos);
    }

    public int capacidad(String u, String v) {
        return arcos.stream().filter(a -> a.u().equals(u) && a.v().equals(v)).mapToInt(Arco::capacidad).findFirst()
                .orElse(0);
    }

    /** Una copia con otra capacidad para el arco u → v (para probar ampliaciones). */
    public RedFlujo conCapacidad(String u, String v, int capacidad) {
        RedFlujo r = new RedFlujo();
        vertices.forEach(r::agregarVertice);
        for (Arco a : arcos) {
            r.agregarArco(a.u(), a.v(), a.u().equals(u) && a.v().equals(v) ? capacidad : a.capacidad());
        }
        return r;
    }

    /** Las rutas de los camiones de leche hacia la planta de Juliaca (capacidades ficticias). */
    public static RedFlujo cargarLeche() {
        RedFlujo r = new RedFlujo();
        try (InputStream in = RedFlujo.class.getResourceAsStream(RUTAS_LECHE)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró " + RUTAS_LECHE);
            }
            BufferedReader lector = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            lector.readLine();
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.isBlank()) {
                    String[] c = linea.split(";");
                    r.agregarArco(c[0], c[1], Integer.parseInt(c[2]));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return r;
    }

    private int[][] matriz() {
        int n = vertices.size();
        int[][] cap = new int[n][n];
        for (Arco a : arcos) {
            cap[vertices.indexOf(a.u())][vertices.indexOf(a.v())] = a.capacidad();
        }
        return cap;
    }

    // ------------------------------------------------------------------
    // Edmonds–Karp (sigue el código de la pestaña Java)
    // ------------------------------------------------------------------

    public Resultado edmondsKarp(String s, String t) {
        int[][] cap = matriz();
        int n = cap.length;
        int is = vertices.indexOf(s);
        int it = vertices.indexOf(t);
        int[][] flujo = new int[n][n];
        int total = 0;
        while (true) {
            int[] padre = bfs(cap, flujo, is, it);
            if (padre[it] == -1) {
                return resultado(cap, flujo, padre, total);
            }
            int b = Integer.MAX_VALUE;
            for (int v = it; v != is; v = padre[v]) {
                b = Math.min(b, cap[padre[v]][v] - flujo[padre[v]][v]);
            }
            for (int v = it; v != is; v = padre[v]) {
                flujo[padre[v]][v] += b;
                flujo[v][padre[v]] -= b;
            }
            total += b;
        }
    }

    private static int[] bfs(int[][] cap, int[][] flujo, int s, int t) {
        int n = cap.length;
        int[] padre = new int[n];
        Arrays.fill(padre, -1);
        padre[s] = s;
        ArrayDeque<Integer> cola = new ArrayDeque<>();
        cola.add(s);
        while (!cola.isEmpty() && padre[t] == -1) {
            int u = cola.poll();
            for (int v = 0; v < n; v++) {
                if (padre[v] == -1 && cap[u][v] - flujo[u][v] > 0) {
                    padre[v] = u;
                    cola.add(v);
                }
            }
        }
        return padre;
    }

    private Resultado resultado(int[][] cap, int[][] flujo, int[] padre, int total) {
        Set<String> ladoS = new LinkedHashSet<>();
        for (int v = 0; v < padre.length; v++) {
            if (padre[v] != -1) {
                ladoS.add(vertices.get(v));
            }
        }
        List<Arco> corte = arcos.stream().filter(a -> ladoS.contains(a.u()) && !ladoS.contains(a.v())).toList();
        return new Resultado(total, flujos(flujo), ladoS, corte);
    }

    private Map<String, Integer> flujos(int[][] flujo) {
        Map<String, Integer> m = new LinkedHashMap<>();
        for (Arco a : arcos) {
            m.put(a.clave(), Math.max(0, flujo[vertices.indexOf(a.u())][vertices.indexOf(a.v())]));
        }
        return m;
    }

    // ------------------------------------------------------------------
    // Pseudocódigo y código Java
    // ------------------------------------------------------------------

    public static final List<String> PSEUDOCODIGO = List.of(
            "edmondsKarp(s, t):",
            "    flujo[u][v] ← 0 para todo par;  total ← 0",
            "    repetir:",
            "        padre ← BFS de s a t por arcos con residual = cap − flujo > 0",
            "        si t no se alcanzó: retornar total          // no quedan caminos de aumento",
            "        b ← menor residual del camino s → t          // el cuello de botella",
            "        para cada arco (u, v) del camino:",
            "            flujo[u][v] ← flujo[u][v] + b;  flujo[v][u] ← flujo[v][u] − b",
            "        total ← total + b");

    static final String JAVA = """
            import java.util.ArrayDeque;
            import java.util.Arrays;

            public class FlujoMaximo {

                record Resultado(int total, int[][] flujo) { }

                // cap[u][v]: capacidad del arco u -> v (0 si no hay). Flujo máximo de s a t.
                public static Resultado edmondsKarp(int[][] cap, int s, int t) {                 //@1
                    int n = cap.length;
                    int[][] flujo = new int[n][n];                                               //@2
                    int total = 0;                                                               //@2
                    while (true) {                                                               //@3
                        int[] padre = bfs(cap, flujo, s, t);                                     //@4
                        if (padre[t] == -1) {                                                    //@5
                            return new Resultado(total, flujo);                                  //@5
                        }
                        int b = Integer.MAX_VALUE;
                        for (int v = t; v != s; v = padre[v]) {                                  //@6
                            b = Math.min(b, cap[padre[v]][v] - flujo[padre[v]][v]);              //@6
                        }
                        for (int v = t; v != s; v = padre[v]) {                                  //@7
                            flujo[padre[v]][v] += b;                                             //@8
                            flujo[v][padre[v]] -= b;                                             //@8
                        }
                        total += b;                                                              //@9
                    }
                }

                // BFS en la red residual: padre[v] = desde dónde se llegó a v (-1 si no se llegó).
                private static int[] bfs(int[][] cap, int[][] flujo, int s, int t) {
                    int n = cap.length;
                    int[] padre = new int[n];
                    Arrays.fill(padre, -1);
                    padre[s] = s;
                    ArrayDeque<Integer> cola = new ArrayDeque<>();
                    cola.add(s);
                    while (!cola.isEmpty() && padre[t] == -1) {
                        int u = cola.poll();
                        for (int v = 0; v < n; v++) {
                            if (padre[v] == -1 && cap[u][v] - flujo[u][v] > 0) {
                                padre[v] = u;
                                cola.add(v);
                            }
                        }
                    }
                    return padre;
                }

                public static void main(String[] args) {
                    String[] lugar = {"Ayaviri", "Azángaro", "Pucará", "Juliaca", "Huancané", "Lampa", "Putina"};
                    int n = lugar.length;
                    int[][] cap = new int[n][n];
                    cap[0][1] = %s;   // Ayaviri -> Azángaro
                    cap[1][2] = %s;   // Azángaro -> Pucará
                    cap[2][3] = %s;   // Pucará -> Juliaca
                    cap[1][4] = %s;   // Azángaro -> Huancané
                    cap[4][3] = %s;   // Huancané -> Juliaca
                    cap[0][5] = %s;   // Ayaviri -> Lampa
                    cap[5][2] = %s;   // Lampa -> Pucará
                    cap[1][6] = %s;   // Azángaro -> Putina
                    cap[6][4] = %s;   // Putina -> Huancané
                    Resultado r = edmondsKarp(cap, 0, 3);
                    System.out.println("Flujo máximo Ayaviri -> Juliaca: " + r.total() + " camiones por día");
                    for (int u = 0; u < n; u++) {
                        for (int v = 0; v < n; v++) {
                            if (cap[u][v] > 0) {
                                System.out.println(lugar[u] + " -> " + lugar[v] + ": "
                                        + Math.max(0, r.flujo()[u][v]) + "/" + cap[u][v]);
                            }
                        }
                    }
                }
            }
            """;

    /** Código Java con las capacidades del main tomadas de la red. */
    public static CodigoAlgoritmo codigo(RedFlujo red) {
        String java = String.format(JAVA, red.capacidad("AYA", "AZA"), red.capacidad("AZA", "PUC"),
                red.capacidad("PUC", "JUL"), red.capacidad("AZA", "HUA"), red.capacidad("HUA", "JUL"),
                red.capacidad("AYA", "LAM"), red.capacidad("LAM", "PUC"), red.capacidad("AZA", "PUT"),
                red.capacidad("PUT", "HUA"));
        return new CodigoAlgoritmo(PSEUDOCODIGO, java);
    }

    // ------------------------------------------------------------------
    // Paso a paso
    // ------------------------------------------------------------------

    public List<EstadoFlujo> pasos(String s, String t, Map<String, String> nombres) {
        Function<String, String> n = v -> nombres.getOrDefault(v, v);
        int[][] cap = matriz();
        int tam = cap.length;
        int is = vertices.indexOf(s);
        int it = vertices.indexOf(t);
        int[][] flujo = new int[tam][tam];
        int total = 0;
        int iteracion = 0;
        List<EstadoFlujo> pasos = new ArrayList<>();
        int salida = arcos.stream().filter(a -> a.u().equals(s)).mapToInt(Arco::capacidad).sum();
        int llegada = arcos.stream().filter(a -> a.v().equals(t)).mapToInt(Arco::capacidad).sum();
        pasos.add(new EstadoFlujo(flujos(flujo), List.of(), Set.of(), null, Set.of(), 0, 0, Tipo.INICIO, 2,
                vars(n, null, null, 0, 0),
                "flujo = 0 en todos los arcos;  total = 0",
                "Se llama edmondsKarp(" + n.apply(s) + ", " + n.apply(t) + "). Cada arco tiene una CAPACIDAD (camiones"
                        + " por día) y empieza sin flujo: \"0/12\" sobre cada arco. Por ningún arco puede pasar más que su"
                        + " capacidad, y en cada pueblo intermedio lo que entra debe salir (la leche no se queda en el"
                        + " camino). De " + n.apply(s) + " salen arcos por " + salida + " camiones y a " + n.apply(t)
                        + " llegan arcos por " + llegada + ": el flujo máximo no puede pasar de " + Math.min(salida,
                        llegada) + ", pero puede ser menor si hay un cuello de botella en medio.",
                "edmondsKarp(" + n.apply(s) + ", " + n.apply(t) + ")"));

        while (true) {
            int[] padre = bfs(cap, flujo, is, it);
            if (padre[it] == -1) {
                Resultado r = resultado(cap, flujo, padre, total);
                int suma = r.corte().stream().mapToInt(Arco::capacidad).sum();
                pasos.add(new EstadoFlujo(r.flujo(), List.of(), Set.of(), null, r.ladoS(), total, iteracion, Tipo.FIN, 5,
                        vars(n, null, null, 0, total),
                        "padre[t] == -1  →  verdadero  →  return total = " + total,
                        "BFS ya no alcanza " + n.apply(t) + ": no queda ningún camino de aumento. El if de la línea 5"
                                + " retorna total = " + total + " camiones por día: el FLUJO MÁXIMO. Por la red residual,"
                                + " desde " + n.apply(s) + " solo se alcanza este grupo: " + lista(n, List.copyOf(r.ladoS()))
                                + ". Los arcos que salen de ese grupo (" + lista(n, r.corte().stream()
                                .map(a -> a.u() + ">" + a.v()).toList()) + ") están LLENOS y suman " + suma + ": son el"
                                + " CORTE MÍNIMO, el verdadero cuello de botella. Teorema de flujo máximo y corte mínimo:"
                                + " " + total + " = " + suma + ".",
                        "Flujo máximo: " + total + " camiones por día."));
                return pasos;
            }
            iteracion++;
            List<String> camino = new ArrayList<>();
            Set<String> atras = new LinkedHashSet<>();
            for (int v = it; v != is; v = padre[v]) {
                camino.add(0, vertices.get(v));
                if (cap[padre[v]][v] == 0) {
                    atras.add(EstadoFlujo.arco(vertices.get(v), vertices.get(padre[v])));
                }
            }
            camino.add(0, s);
            pasos.add(new EstadoFlujo(flujos(flujo), camino, atras, null, Set.of(), total, iteracion, Tipo.CAMINO, 4,
                    vars(n, camino, null, 0, total),
                    "padre = BFS(s, t)  →  camino " + String.join(" > ", camino),
                    "Vuelta " + iteracion + " del repetir. BFS busca, por los arcos que todavía tienen espacio (residual"
                            + " > 0), un camino de " + n.apply(s) + " a " + n.apply(t) + " con la menor cantidad de"
                            + " tramos: " + String.join(" → ", camino.stream().map(n).toList()) + "."
                            + (atras.isEmpty() ? " El if de la línea 5 consulta si se alcanzó " + n.apply(t)
                            + ": sí, así que se puede enviar más."
                            : " ¡Ojo! El camino recorre AL REVÉS el arco " + lista(n, List.copyOf(atras))
                            + ": usa su residual hacia atrás, que existe porque ese arco ya lleva flujo. Recorrerlo así"
                            + " significa DEVOLVER camiones de ese arco y mandarlos por otro lado."),
                    "Camino " + iteracion + ": " + String.join(" → ", camino.stream().map(n).toList())));

            int b = Integer.MAX_VALUE;
            String cuello = null;
            List<String> residuales = new ArrayList<>();
            for (int v = it; v != is; v = padre[v]) {
                int r = cap[padre[v]][v] - flujo[padre[v]][v];
                residuales.add(0, String.valueOf(r));
                if (r < b) {
                    b = r;
                    cuello = EstadoFlujo.arco(vertices.get(padre[v]), vertices.get(v));
                }
            }
            String[] c = cuello.split(">");
            boolean cuelloAtras = cap[vertices.indexOf(c[0])][vertices.indexOf(c[1])] == 0;
            pasos.add(new EstadoFlujo(flujos(flujo), camino, atras, cuello, Set.of(), total, iteracion, Tipo.CUELLO, 6,
                    vars(n, camino, cuello, b, total),
                    "b = min(" + String.join(", ", residuales) + ") = " + b,
                    "El for de la línea 6 recorre el camino desde " + n.apply(t) + " hacia atrás con los padres y"
                            + " calcula el residual de cada tramo: " + String.join(", ", residuales) + ". El menor es " + b
                            + ", en " + n.apply(c[0]) + " → " + n.apply(c[1]) + (cuelloAtras ? " (solo se pueden devolver"
                            + " lo que ese arco llevaba: " + camiones(b) + ")" : "") + ": es el CUELLO DE BOTELLA. Por este"
                            + " camino no pueden ir más de " + camiones(b) + ".",
                    "Cuello de botella: " + b + "."));

            for (int v = it; v != is; v = padre[v]) {
                flujo[padre[v]][v] += b;
                flujo[v][padre[v]] -= b;
            }
            total += b;
            List<String> llenos = new ArrayList<>();
            for (int k = 1; k < camino.size(); k++) {
                int u = vertices.indexOf(camino.get(k - 1));
                int v = vertices.indexOf(camino.get(k));
                if (cap[u][v] > 0 && cap[u][v] == flujo[u][v]) {
                    llenos.add(n.apply(camino.get(k - 1)) + " → " + n.apply(camino.get(k)));
                }
            }
            pasos.add(new EstadoFlujo(flujos(flujo), camino, atras, cuello, Set.of(), total, iteracion, Tipo.AUMENTA, 8,
                    vars(n, camino, cuello, b, total),
                    "flujo[u][v] += " + b + ";  flujo[v][u] -= " + b + "  (en cada tramo);  total = " + total,
                    "El for de la línea 7 envía " + camiones(b) + " por todo el camino: suma " + b + " al flujo de cada"
                            + " tramo y resta " + b + " en el sentido contrario"
                            + (atras.isEmpty() ? "" : " (en " + lista(n, List.copyOf(atras)) + ", recorrido al revés, eso"
                            + " DESCUENTA " + b + " del flujo que llevaba)") + ". "
                            + (llenos.isEmpty() ? "" : "Quedan LLENOS: " + String.join(", ", llenos) + ". ")
                            + "La línea 9 suma al total: " + total + " camiones por día. El repetir vuelve a la línea 4"
                            + " para buscar otro camino.",
                    "+" + b + " → total " + total + "."));
        }
    }

    private static String camiones(int b) {
        return b == 1 ? "1 camión" : b + " camiones";
    }

    private static String lista(Function<String, String> n, List<String> claves) {
        List<String> nombres = claves.stream().map(k -> k.contains(">")
                ? n.apply(k.split(">")[0]) + " → " + n.apply(k.split(">")[1]) : n.apply(k)).toList();
        if (nombres.isEmpty()) {
            return "ninguno";
        }
        if (nombres.size() == 1) {
            return nombres.get(0);
        }
        return String.join(", ", nombres.subList(0, nombres.size() - 1)) + " y " + nombres.get(nombres.size() - 1);
    }

    private static Map<String, String> vars(Function<String, String> n, List<String> camino, String cuello, int b,
                                            int total) {
        return PasoTrazable.variables(
                "padre", camino == null ? "—" : String.join(" > ", camino),
                "b", cuello == null ? "—" : b,
                "total", total);
    }
}
