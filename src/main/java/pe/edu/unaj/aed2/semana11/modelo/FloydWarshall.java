package pe.edu.unaj.aed2.semana11.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana08.modelo.GrafoPonderado;
import pe.edu.unaj.aed2.semana11.modelo.EstadoFloyd.Tipo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Caminos más cortos entre TODOS los pares de vértices con Floyd–Warshall: programación dinámica sobre una matriz.
 * En la vuelta k se permite usar el vértice k como parada intermedia; al terminar todas, dist[i][j] es el mínimo.
 */
public final class FloydWarshall {

    /** "No hay camino conocido". Nunca se suma: se pregunta antes (sumar a MAX_VALUE desborda el int). */
    public static final int INF = Integer.MAX_VALUE;

    /** Los 7 pueblos del norte del lago que usa el visualizador (con 14, serían 14³ = 2744 comparaciones). */
    public static final List<String> SUBRED = List.of("JUL", "PUN", "LAM", "PUC", "AZA", "PUT", "HUA");

    private FloydWarshall() {
    }

    /** Matrices finales: distancia mínima y siguiente parada para cada par. */
    public record Resultado(List<String> vertices, int[][] dist, int[][] sig) {

        public int indice(String v) {
            int k = vertices.indexOf(v);
            if (k < 0) {
                throw new IllegalArgumentException("Vértice desconocido: " + v);
            }
            return k;
        }

        /** Km mínimos de u a v; INF si no hay camino. */
        public int distancia(String u, String v) {
            return dist[indice(u)][indice(v)];
        }

        /** El camino de u a v siguiendo sig; vacío si no hay camino. */
        public List<String> camino(String u, String v) {
            int i = indice(u);
            int j = indice(v);
            List<String> c = new ArrayList<>();
            if (i != j && sig[i][j] == -1) {
                return c;
            }
            c.add(vertices.get(i));
            while (i != j) {
                i = sig[i][j];
                c.add(vertices.get(i));
            }
            return c;
        }

        /** Km al vértice más lejano desde v (su excentricidad); INF si alguno es inalcanzable. */
        public int excentricidad(String v) {
            return Arrays.stream(dist[indice(v)]).max().orElse(0);
        }

        /** Suma de los km desde v a todos los demás; INF si alguno es inalcanzable. */
        public long suma(String v) {
            long s = 0;
            for (int d : dist[indice(v)]) {
                if (d == INF) {
                    return INF;
                }
                s += d;
            }
            return s;
        }
    }

    /** Matriz de pesos: km de la carretera directa (la más corta si hay varias), 0 en la diagonal, INF si no hay. */
    public static int[][] matriz(GrafoPonderado g, List<String> vertices) {
        int n = vertices.size();
        int[][] w = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(w[i], INF);
            w[i][i] = 0;
            for (GrafoPonderado.Arista a : g.vecinos(vertices.get(i))) {
                int j = vertices.indexOf(a.destino());
                if (j >= 0 && j != i) {
                    w[i][j] = Math.min(w[i][j], a.km());
                }
            }
        }
        return w;
    }

    /** Solo los vértices pedidos y las aristas entre ellos. */
    public static GrafoPonderado subred(GrafoPonderado g, List<String> ids) {
        GrafoPonderado s = new GrafoPonderado();
        ids.forEach(s::agregarVertice);
        for (String u : ids) {
            for (GrafoPonderado.Arista a : g.vecinos(u)) {
                if (ids.contains(a.destino()) && u.compareTo(a.destino()) < 0) {
                    s.agregarArista(u, a.destino(), a.km());
                }
            }
        }
        return s;
    }

    // ------------------------------------------------------------------
    // Floyd–Warshall (sigue el código de la pestaña Java)
    // ------------------------------------------------------------------

    public static Resultado calcular(GrafoPonderado g) {
        List<String> vertices = List.copyOf(g.vertices());
        int[][] w = matriz(g, vertices);
        int n = w.length;
        int[][] dist = new int[n][];
        int[][] sig = new int[n][n];
        for (int i = 0; i < n; i++) {
            dist[i] = w[i].clone();
            for (int j = 0; j < n; j++) {
                sig[i][j] = (w[i][j] != INF && i != j) ? j : -1;
            }
        }
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] != INF && dist[k][j] != INF && dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                        sig[i][j] = sig[i][k];
                    }
                }
            }
        }
        return new Resultado(vertices, dist, sig);
    }

    // ------------------------------------------------------------------
    // Pseudocódigo y código Java
    // ------------------------------------------------------------------

    public static final List<String> PSEUDOCODIGO = List.of(
            "floyd(w):",
            "    dist ← w (∞ sin carretera, 0 en la diagonal);  sig[i][j] ← j si hay carretera",
            "    para k ← 0 .. n − 1:                      // ahora se permite parar en k",
            "        para i ← 0 .. n − 1:",
            "            para j ← 0 .. n − 1:",
            "                si dist[i][k] + dist[k][j] < dist[i][j]:     // ¿i → k → j es mejor?",
            "                    dist[i][j] ← dist[i][k] + dist[k][j];  sig[i][j] ← sig[i][k]",
            "    retornar dist, sig");

    static final String JAVA = """
            import java.util.ArrayList;
            import java.util.Arrays;
            import java.util.List;

            public class FloydWarshall {

                static final int INF = Integer.MAX_VALUE;   // "no hay camino"

                // dist: km mínimos entre cada par; sig: la siguiente parada en ese camino (-1 si no hay).
                record Resultado(int[][] dist, int[][] sig) { }

                // w[i][j]: km de la carretera i-j, INF si no hay; w[i][i] = 0.
                public static Resultado floyd(int[][] w) {                                    //@1
                    int n = w.length;
                    int[][] dist = new int[n][];
                    int[][] sig = new int[n][n];
                    for (int i = 0; i < n; i++) {                                             //@2
                        dist[i] = w[i].clone();                                               //@2
                        for (int j = 0; j < n; j++) {                                         //@2
                            sig[i][j] = (w[i][j] != INF && i != j) ? j : -1;                  //@2
                        }
                    }
                    for (int k = 0; k < n; k++) {                                             //@3
                        for (int i = 0; i < n; i++) {                                         //@4
                            for (int j = 0; j < n; j++) {                                     //@5
                                if (dist[i][k] != INF && dist[k][j] != INF                    //@6
                                        && dist[i][k] + dist[k][j] < dist[i][j]) {            //@6
                                    dist[i][j] = dist[i][k] + dist[k][j];                     //@7
                                    sig[i][j] = sig[i][k];                                    //@7
                                }
                            }
                        }
                    }
                    return new Resultado(dist, sig);                                          //@8
                }

                // Camino de i a j: desde i, saltar a sig[i][j] hasta llegar a j.
                public static List<Integer> camino(int[][] sig, int i, int j) {
                    List<Integer> c = new ArrayList<>();
                    if (i != j && sig[i][j] == -1) {
                        return c;
                    }
                    c.add(i);
                    while (i != j) {
                        i = sig[i][j];
                        c.add(i);
                    }
                    return c;
                }

                static void carretera(int[][] w, int a, int b, int km) {
                    w[a][b] = km;
                    w[b][a] = km;
                }

                public static void main(String[] args) {
                    String[] pueblos = {"Juliaca", "Puno", "Azángaro", "Huancané", "Putina"};
                    int n = pueblos.length;
                    int[][] w = new int[n][n];
                    for (int i = 0; i < n; i++) {
                        Arrays.fill(w[i], INF);
                        w[i][i] = 0;
                    }
                    carretera(w, 0, 1, %s);   // Juliaca - Puno
                    carretera(w, 0, 2, %s);   // Juliaca - Azángaro
                    carretera(w, 0, 3, %s);   // Juliaca - Huancané
                    carretera(w, 2, 4, %s);   // Azángaro - Putina
                    carretera(w, 4, 3, %s);   // Putina - Huancané
                    Resultado r = floyd(w);
                    for (int i = 0; i < n; i++) {
                        System.out.println(pueblos[i] + ": " + Arrays.toString(r.dist()[i]));
                    }
                    List<String> ruta = new ArrayList<>();
                    for (int v : camino(r.sig(), 1, 4)) {
                        ruta.add(pueblos[v]);
                    }
                    System.out.println("Puno -> Putina: " + ruta + " = " + r.dist()[1][4] + " km");
                }
            }
            """;

    /** Código Java con los km del main tomados de la red (así coinciden con la aplicación). */
    public static CodigoAlgoritmo codigo(GrafoPonderado g) {
        String java = String.format(JAVA, g.km("JUL", "PUN"), g.km("JUL", "AZA"), g.km("JUL", "HUA"),
                g.km("AZA", "PUT"), g.km("PUT", "HUA"));
        return new CodigoAlgoritmo(PSEUDOCODIGO, java);
    }

    // ------------------------------------------------------------------
    // Paso a paso
    // ------------------------------------------------------------------

    /**
     * Pasos de Floyd–Warshall. Se omiten las casillas que no pueden mejorar (la diagonal, la fila k y la columna k):
     * el paso de cada k lo explica.
     */
    public static List<EstadoFloyd> pasos(GrafoPonderado g, Map<String, String> nombres) {
        List<String> vs = List.copyOf(g.vertices());
        Function<Integer, String> n = x -> nombres.getOrDefault(vs.get(x), vs.get(x));
        int[][] w = matriz(g, vs);
        int tam = w.length;
        int[][] dist = new int[tam][];
        int[][] sig = new int[tam][tam];
        for (int i = 0; i < tam; i++) {
            dist[i] = w[i].clone();
            for (int j = 0; j < tam; j++) {
                sig[i][j] = (w[i][j] != INF && i != j) ? j : -1;
            }
        }
        List<EstadoFloyd> pasos = new ArrayList<>();
        Set<String> mejoradas = new LinkedHashSet<>();
        int[] sinCarretera = primerParSin(dist);
        pasos.add(paso(vs, dist, sig, -1, -1, -1, mejoradas, Tipo.INICIO, 2, vars(n, -1, -1, -1, dist, sig),
                "dist = w;  sig[i][j] = j donde hay carretera",
                "Se llama floyd(w). dist empieza como la tabla de carreteras DIRECTAS: el km de cada carretera, 0 en la"
                        + " diagonal y ∞ donde no hay carretera directa"
                        + (sinCarretera == null ? "" : " (por ejemplo, " + n.apply(sinCarretera[0]) + " – "
                        + n.apply(sinCarretera[1]) + ")") + ". sig[i][j] = j: la siguiente parada es el destino mismo."
                        + " La idea es de PROGRAMACIÓN DINÁMICA: en cada vuelta de k se permite parar en un pueblo"
                        + " más. Al terminar la vuelta k, dist[i][j] es el camino más corto que solo para en los"
                        + " pueblos 0..k.",
                "floyd(w): matriz de carreteras directas."));

        for (int k = 0; k < tam; k++) {
            mejoradas = new LinkedHashSet<>();
            String antes = k == 0 ? "Hasta ahora no se permitía ninguna parada intermedia"
                    : "Hasta ahora solo se permitía parar en " + lista(n, k);
            pasos.add(paso(vs, dist, sig, k, -1, -1, mejoradas, Tipo.NUEVO_K, 3, vars(n, k, -1, -1, dist, sig),
                    "k = " + k + " (" + vs.get(k) + ")",
                    "Empieza la vuelta k = " + k + ": ¿conviene pasar por " + n.apply(k) + "? " + antes + ". Para"
                            + " cada par (i, j) se compara lo conocido con ir de i a " + n.apply(k) + " y de "
                            + n.apply(k) + " a j, usando la fila y la columna de " + n.apply(k) + " (resaltadas). Esas"
                            + " dos no cambian en esta vuelta (ir a " + n.apply(k) + " pasando por " + n.apply(k)
                            + " no ayuda), ni la diagonal: el paso a paso las salta.",
                    "k = " + k + ": ¿pasar por " + n.apply(k) + "?"));
            for (int i = 0; i < tam; i++) {
                for (int j = 0; j < tam; j++) {
                    if (i == j || i == k || j == k) {
                        continue;
                    }
                    String par = "de " + n.apply(i) + " a " + n.apply(j);
                    if (dist[i][k] == INF || dist[k][j] == INF) {
                        boolean primero = dist[i][k] == INF;
                        pasos.add(paso(vs, dist, sig, k, i, j, mejoradas, Tipo.SIN_CAMINO, 6,
                                vars(n, k, i, j, dist, sig),
                                (primero ? "dist[i][k] = infinito" : "dist[k][j] = infinito")
                                        + "  →  no se puede pasar por k  →  falso",
                                "El if consulta si ir " + par + " pasando por " + n.apply(k) + " es mejor. Pero "
                                        + (primero ? "todavía no se conoce camino de " + n.apply(i) + " a " + n.apply(k)
                                        : "todavía no se conoce camino de " + n.apply(k) + " a " + n.apply(j))
                                        + " (con las paradas permitidas hasta ahora): dist vale ∞, así que pasar por "
                                        + n.apply(k) + " no sirve y el if no entra. En Java la condición pregunta"
                                        + " != INF ANTES de sumar, porque sumar a Integer.MAX_VALUE desborda el int.",
                                n.apply(i) + " → " + n.apply(j) + ": sin camino por " + n.apply(k) + "."));
                        continue;
                    }
                    int nueva = dist[i][k] + dist[k][j];
                    int actual = dist[i][j];
                    String actualTexto = actual == INF ? "infinito" : String.valueOf(actual);
                    String actualHumano = actual == INF ? "∞, no se conocía camino" : actual + " km";
                    String suma = n.apply(i) + " → " + n.apply(k) + " " + dist[i][k] + " + " + n.apply(k) + " → "
                            + n.apply(j) + " " + dist[k][j] + " = " + nueva + " km";
                    if (nueva < actual) {
                        dist[i][j] = nueva;
                        sig[i][j] = sig[i][k];
                        mejoradas.add(EstadoFloyd.casilla(i, j));
                        pasos.add(paso(vs, dist, sig, k, i, j, mejoradas, Tipo.MEJORA, 7,
                                vars(n, k, i, j, dist, sig),
                                "dist[i][k] + dist[k][j] < dist[i][j]  →  " + dist[i][k] + " + " + dist[k][j] + " = "
                                        + nueva + " < " + actualTexto + "  →  verdadero",
                                "El if consulta: ¿ir " + par + " pasando por " + n.apply(k) + " (" + suma + ") es"
                                        + " mejor que lo conocido (" + actualHumano + ")? SÍ: dist[" + n.apply(i) + "]["
                                        + n.apply(j) + "] = " + nueva + " y sig[" + n.apply(i) + "][" + n.apply(j)
                                        + "] = sig[" + n.apply(i) + "][" + n.apply(k) + "] = " + n.apply(sig[i][j])
                                        + ": para ir a " + n.apply(j) + " se sale por el mismo lado que para ir a "
                                        + n.apply(k) + ". El for de j sigue.",
                                n.apply(i) + " → " + n.apply(j) + ": " + (actual == INF ? "∞" : actual) + " → " + nueva
                                        + " km por " + n.apply(k) + "."));
                    } else {
                        pasos.add(paso(vs, dist, sig, k, i, j, mejoradas, Tipo.NO_MEJORA, 6,
                                vars(n, k, i, j, dist, sig),
                                "dist[i][k] + dist[k][j] < dist[i][j]  →  " + dist[i][k] + " + " + dist[k][j] + " = "
                                        + nueva + " < " + actualTexto + "  →  falso",
                                "El if consulta: ¿ir " + par + " pasando por " + n.apply(k) + " (" + suma + ") es"
                                        + " mejor que lo conocido (" + actual + " km)? NO, así que la casilla se queda"
                                        + " igual y el for de j sigue.",
                                n.apply(i) + " → " + n.apply(j) + ": sin mejora."));
                    }
                }
            }
        }
        pasos.add(paso(vs, dist, sig, -1, -1, -1, Set.of(), Tipo.FIN, 8, vars(n, -1, -1, -1, dist, sig),
                "return dist, sig",
                "Terminaron las " + tam + " vueltas de k: ya se permitió parar en cualquier pueblo, así que dist tiene"
                        + " el camino más corto entre TODOS los pares (" + tam + " × " + tam + " casillas con "
                        + tam * tam * tam + " comparaciones: O(n³)). Para reconstruir una ruta se sigue sig: desde i se"
                        + " salta a sig[i][j] hasta llegar a j.",
                "Fin de Floyd–Warshall."));
        return pasos;
    }

    private static int[] primerParSin(int[][] dist) {
        for (int i = 0; i < dist.length; i++) {
            for (int j = i + 1; j < dist.length; j++) {
                if (dist[i][j] == INF) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    private static String lista(Function<Integer, String> n, int hasta) {
        List<String> nombres = new ArrayList<>();
        for (int x = 0; x < hasta; x++) {
            nombres.add(n.apply(x));
        }
        if (nombres.size() == 1) {
            return nombres.get(0);
        }
        return String.join(", ", nombres.subList(0, nombres.size() - 1)) + " y " + nombres.get(nombres.size() - 1);
    }

    private static EstadoFloyd paso(List<String> vs, int[][] dist, int[][] sig, int k, int i, int j,
                                    Set<String> mejoradas, Tipo tipo, int linea, Map<String, String> variables,
                                    String evaluacion, String explicacion, String descripcion) {
        return new EstadoFloyd(vs, aLista(dist), aLista(sig), k, i, j, mejoradas, tipo, linea, variables, evaluacion,
                explicacion, descripcion);
    }

    private static List<List<Integer>> aLista(int[][] m) {
        List<List<Integer>> filas = new ArrayList<>();
        for (int[] fila : m) {
            filas.add(Arrays.stream(fila).boxed().toList());
        }
        return filas;
    }

    private static String km(int d) {
        return d == INF ? "infinito" : String.valueOf(d);
    }

    private static Map<String, String> vars(Function<Integer, String> n, int k, int i, int j, int[][] dist,
                                            int[][] sig) {
        return PasoTrazable.variables(
                "k", k < 0 ? "—" : k + " (" + n.apply(k) + ")",
                "i", i < 0 ? "—" : i + " (" + n.apply(i) + ")",
                "j", j < 0 ? "—" : j + " (" + n.apply(j) + ")",
                "dist[i][k]", i < 0 ? "—" : km(dist[i][k]),
                "dist[k][j]", j < 0 ? "—" : km(dist[k][j]),
                "dist[i][j]", i < 0 ? "—" : km(dist[i][j]),
                "sig[i][j]", i < 0 ? "—" : sig[i][j] < 0 ? "-1" : sig[i][j] + " (" + n.apply(sig[i][j]) + ")");
    }
}
