package pe.edu.unaj.aed2.semana17.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana17.modelo.EstadoViajante.Tipo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Problema del VIAJANTE (TSP): salir del origen, visitar cada pueblo una vez y volver, con el menor recorrido. Es
 * NP-difícil: no se conoce un algoritmo rápido que siempre dé el óptimo. Se comparan la fuerza bruta (exacta, pero
 * (n−1)! rutas), la heurística del vecino más cercano y la mejora local 2-opt.
 * <p>
 * Contexto: un DRON lleva vacunas a las postas de varios pueblos y vuelve a su base; vuela en línea recta, así que la
 * distancia es la de la fórmula del haversine (semana 07), redondeada a km.
 */
public class Viajante {

    /** Una ruta cerrada (el regreso al origen está implícito) con su largo y cuántas rutas se evaluaron. */
    public record Ruta(List<String> orden, int km, long evaluadas) {
    }

    /** Pueblos del visualizador: el origen es el primero. */
    public static final List<String> EJEMPLO = List.of("JUL", "PUN", "LAM", "AYA", "AZA", "HUA", "MOH", "ILA");

    /** Más pueblos que esto y la fuerza bruta tarda demasiado (11! ≈ 40 millones de rutas). */
    public static final int MAXIMO_FUERZA_BRUTA = 11;

    private final List<String> pueblos;
    private final int[][] d;

    /** @param pueblos los pueblos a visitar; el primero es el origen (la base del dron) */
    public Viajante(RedVial red, List<String> pueblos) {
        this.pueblos = List.copyOf(pueblos);
        int n = pueblos.size();
        d = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d[i][j] = (int) Math.round(red.distanciaKm(pueblos.get(i), pueblos.get(j)));
            }
        }
    }

    /** A partir de una matriz de distancias ya calculada (nombres[i] es el vértice i; el 0 es el origen). */
    public Viajante(List<String> nombres, int[][] distancias) {
        this.pueblos = List.copyOf(nombres);
        this.d = new int[distancias.length][];
        for (int i = 0; i < distancias.length; i++) {
            d[i] = distancias[i].clone();
        }
    }

    public List<String> pueblos() {
        return pueblos;
    }

    public int distancia(String a, String b) {
        return d[pueblos.indexOf(a)][pueblos.indexOf(b)];
    }

    public int[][] matriz() {
        int[][] copia = new int[d.length][];
        for (int i = 0; i < d.length; i++) {
            copia[i] = d[i].clone();
        }
        return copia;
    }

    private int largo(List<Integer> ruta) {
        int total = 0;
        for (int k = 0; k < ruta.size(); k++) {
            total += d[ruta.get(k)][ruta.get((k + 1) % ruta.size())];
        }
        return total;
    }

    private Ruta ruta(List<Integer> indices, long evaluadas) {
        return new Ruta(indices.stream().map(pueblos::get).toList(), largo(indices), evaluadas);
    }

    // ------------------------------------------------------------------
    // Fuerza bruta, vecino más cercano y 2-opt
    // ------------------------------------------------------------------

    /** Prueba todas las rutas que empiezan en el origen: (n−1)! de ellas. */
    public Ruta fuerzaBruta() {
        if (pueblos.size() > MAXIMO_FUERZA_BRUTA) {
            throw new IllegalStateException("Demasiados pueblos para la fuerza bruta: " + pueblos.size());
        }
        List<Integer> actual = new ArrayList<>(List.of(0));
        boolean[] usado = new boolean[pueblos.size()];
        usado[0] = true;
        long[] evaluadas = {0};
        List<Integer> mejor = new ArrayList<>();
        int[] mejorKm = {Integer.MAX_VALUE};
        permutar(actual, usado, evaluadas, mejor, mejorKm);
        return ruta(mejor, evaluadas[0]);
    }

    private void permutar(List<Integer> actual, boolean[] usado, long[] evaluadas, List<Integer> mejor, int[] mejorKm) {
        if (actual.size() == pueblos.size()) {
            evaluadas[0]++;
            int km = largo(actual);
            if (km < mejorKm[0]) {
                mejorKm[0] = km;
                mejor.clear();
                mejor.addAll(actual);
            }
            return;
        }
        for (int v = 1; v < pueblos.size(); v++) {
            if (!usado[v]) {
                usado[v] = true;
                actual.add(v);
                permutar(actual, usado, evaluadas, mejor, mejorKm);
                actual.remove(actual.size() - 1);
                usado[v] = false;
            }
        }
    }

    public Ruta vecinoMasCercano() {
        return ruta(vecino(), pueblos.size() - 1);
    }

    private List<Integer> vecino() {
        int n = pueblos.size();
        boolean[] visitado = new boolean[n];
        List<Integer> ruta = new ArrayList<>(List.of(0));
        visitado[0] = true;
        int actual = 0;
        while (ruta.size() < n) {
            int siguiente = -1;
            for (int v = 0; v < n; v++) {
                if (!visitado[v] && (siguiente < 0 || d[actual][v] < d[actual][siguiente])) {
                    siguiente = v;
                }
            }
            visitado[siguiente] = true;
            ruta.add(siguiente);
            actual = siguiente;
        }
        return ruta;
    }

    /** 2-opt a partir del vecino más cercano. */
    public Ruta dosOpt() {
        List<Integer> ruta = vecino();
        long revisados = mejorar(ruta, null, null);
        return ruta(ruta, revisados);
    }

    /** Aplica 2-opt hasta que ningún cambio mejore; retorna cuántos pares se revisaron. */
    private long mejorar(List<Integer> ruta, List<EstadoViajante> pasos, Function<Integer, String> n) {
        int tam = ruta.size();
        long revisados = 0;
        boolean mejoro = true;
        int pasada = 0;
        while (mejoro) {
            mejoro = false;
            pasada++;
            for (int i = 0; i < tam - 1; i++) {
                for (int j = i + 2; j < tam; j++) {
                    if (i == 0 && j == tam - 1) {
                        continue; // son tramos vecinos (comparten el origen)
                    }
                    revisados++;
                    int a = ruta.get(i);
                    int b = ruta.get(i + 1);
                    int c = ruta.get(j);
                    int e = ruta.get((j + 1) % tam);
                    int antes = d[a][b] + d[c][e];
                    int despues = d[a][c] + d[b][e];
                    if (despues < antes) {
                        Collections.reverse(ruta.subList(i + 1, j + 1));
                        mejoro = true;
                        if (pasos != null) {
                            pasos.add(paso(ruta, true, null, null, List.of(a, b), List.of(c, e), Tipo.MEJORA, 5, n,
                                    "d(a,c) + d(b,e) < d(a,b) + d(c,e)  →  " + d[a][c] + " + " + d[b][e] + " = "
                                            + despues + " < " + d[a][b] + " + " + d[c][e] + " = " + antes
                                            + "  →  verdadero  →  invertir",
                                    "Se revisan los tramos " + n.apply(a) + " → " + n.apply(b) + " y " + n.apply(c)
                                            + " → " + n.apply(e) + ". El if consulta si cambiarlos por " + n.apply(a)
                                            + " → " + n.apply(c) + " y " + n.apply(b) + " → " + n.apply(e) + " acorta: "
                                            + despues + " < " + antes + " es verdadero. Se INVIERTE el pedazo de la ruta"
                                            + " entre " + n.apply(b) + " y " + n.apply(c) + " (se recorre al revés): la"
                                            + " ruta baja " + (antes - despues) + " km, a " + largo(ruta) + " km. Si dos"
                                            + " tramos se cruzaban en el mapa, ahora ya no.",
                                    "2-opt: −" + (antes - despues) + " km → " + largo(ruta) + " km."));
                        }
                    }
                }
            }
            if (pasos != null) {
                pasos.add(paso(ruta, true, null, null, null, null, Tipo.PASADA, 2, n,
                        "pasada " + pasada + ": hubo mejora  →  " + (mejoro ? "verdadero  →  otra pasada"
                                : "falso  →  terminar"),
                        "Terminó la pasada " + pasada + " por todos los pares de tramos. "
                                + (mejoro ? "Hubo al menos una mejora, así que el repetir hace otra pasada: un cambio"
                                + " puede habilitar otros." : "Ningún par mejoró: la ruta es un ÓPTIMO LOCAL para 2-opt"
                                + " y el repetir termina. Ojo: óptimo local no significa óptimo global."),
                        "Fin de la pasada " + pasada + "."));
            }
        }
        return revisados;
    }

    // ------------------------------------------------------------------
    // Pseudocódigo y código Java
    // ------------------------------------------------------------------

    public static final List<String> PSEUDO_VECINO = List.of(
            "vecinoMasCercano(origen):",
            "    ruta ← [origen];  actual ← origen",
            "    mientras falten pueblos:",
            "        siguiente ← el pueblo NO visitado más cercano a actual     // decisión voraz",
            "        ruta.agregar(siguiente);  actual ← siguiente",
            "    volver al origen;  retornar ruta");

    public static final List<String> PSEUDO_DOS_OPT = List.of(
            "dosOpt(ruta):",
            "    repetir mientras haya alguna mejora:",
            "        para cada par de tramos (a → b) y (c → e) que no se tocan:",
            "            si d(a, c) + d(b, e) < d(a, b) + d(c, e):           // ¿cambiarlos acorta?",
            "                invertir el pedazo de la ruta entre b y c          // descruzar",
            "    retornar ruta");

    static final String JAVA = """
            import java.util.Arrays;

            public class Viajante {

                // d[i][j]: km entre i y j. La ruta empieza en 0 y vuelve a 0 al final.
                public static int[] vecinoMasCercano(int[][] d) {                                   //@vecino:1
                    int n = d.length;
                    int[] ruta = new int[n];
                    boolean[] visitado = new boolean[n];
                    visitado[0] = true;                                                              //@vecino:2
                    int actual = 0;                                                                  //@vecino:2
                    for (int k = 1; k < n; k++) {                                                    //@vecino:3
                        int siguiente = -1;
                        for (int v = 0; v < n; v++) {                                                //@vecino:4
                            if (!visitado[v] && (siguiente < 0 || d[actual][v] < d[actual][siguiente])) {   //@vecino:4
                                siguiente = v;                                                       //@vecino:4
                            }
                        }
                        ruta[k] = siguiente;                                                         //@vecino:5
                        visitado[siguiente] = true;                                                  //@vecino:5
                        actual = siguiente;                                                          //@vecino:5
                    }
                    return ruta;                                                                     //@vecino:6
                }

                // Mejora local: mientras cambiar dos tramos por otros dos acorte la ruta, hacerlo.
                public static void dosOpt(int[][] d, int[] ruta) {                                  //@opt:1
                    int n = ruta.length;
                    boolean mejoro = true;
                    while (mejoro) {                                                                 //@opt:2
                        mejoro = false;                                                              //@opt:2
                        for (int i = 0; i < n - 1; i++) {                                            //@opt:3
                            for (int j = i + 2; j < n; j++) {                                        //@opt:3
                                if (i == 0 && j == n - 1) {
                                    continue;                     // son vecinos: comparten el origen
                                }
                                int a = ruta[i], b = ruta[i + 1], c = ruta[j], e = ruta[(j + 1) %% n];
                                if (d[a][c] + d[b][e] < d[a][b] + d[c][e]) {                         //@opt:4
                                    invertir(ruta, i + 1, j);                                        //@opt:5
                                    mejoro = true;                                                   //@opt:5
                                }
                            }
                        }
                    }
                }                                                                                    //@opt:6

                private static void invertir(int[] ruta, int desde, int hasta) {
                    while (desde < hasta) {
                        int t = ruta[desde];
                        ruta[desde++] = ruta[hasta];
                        ruta[hasta--] = t;
                    }
                }

                public static int largo(int[][] d, int[] ruta) {
                    int total = 0;
                    for (int k = 0; k < ruta.length; k++) {
                        total += d[ruta[k]][ruta[(k + 1) %% ruta.length]];
                    }
                    return total;
                }

                public static void main(String[] args) {
                    String[] pueblo = {%s};
                    int[][] d = {
            %s
                    };
                    int[] ruta = vecinoMasCercano(d);
                    System.out.println("Vecino más cercano: " + nombres(pueblo, ruta) + " = " + largo(d, ruta) + " km");
                    dosOpt(d, ruta);
                    System.out.println("Con 2-opt: " + nombres(pueblo, ruta) + " = " + largo(d, ruta) + " km");
                }

                static String nombres(String[] pueblo, int[] ruta) {
                    String[] r = new String[ruta.length];
                    for (int k = 0; k < ruta.length; k++) {
                        r[k] = pueblo[ruta[k]];
                    }
                    return Arrays.toString(r);
                }
            }
            """;

    /** Código Java con la matriz de distancias de los pueblos del ejemplo. */
    public CodigoAlgoritmo codigo(String seccion, Map<String, String> nombres) {
        List<String> filas = new ArrayList<>();
        for (int[] fila : d) {
            List<String> v = new ArrayList<>();
            for (int x : fila) {
                v.add(String.format("%3d", x));
            }
            filas.add("                    {" + String.join(", ", v) + "},");
        }
        String java = String.format(JAVA,
                String.join(", ", pueblos.stream().map(p -> "\"" + nombres.getOrDefault(p, p) + "\"").toList()),
                String.join("\n", filas));
        return CodigoAlgoritmo.seccion(seccion.equals("vecino") ? PSEUDO_VECINO : PSEUDO_DOS_OPT, java, seccion);
    }

    // ------------------------------------------------------------------
    // Paso a paso
    // ------------------------------------------------------------------

    public List<EstadoViajante> pasosVecino(Map<String, String> nombres) {
        Function<Integer, String> n = x -> nombres.getOrDefault(pueblos.get(x), pueblos.get(x));
        int tam = pueblos.size();
        List<EstadoViajante> pasos = new ArrayList<>();
        boolean[] visitado = new boolean[tam];
        List<Integer> ruta = new ArrayList<>(List.of(0));
        visitado[0] = true;
        int actual = 0;
        pasos.add(paso(ruta, false, 0, null, null, null, Tipo.INICIO, 2, n,
                "ruta = [" + n.apply(0) + "];  actual = " + n.apply(0),
                "Se llama vecinoMasCercano(" + n.apply(0) + "). Hay que visitar " + (tam - 1) + " pueblos y volver. La"
                        + " fuerza bruta probaría (" + (tam - 1) + ")! = " + factorial(tam - 1) + " rutas. Esta"
                        + " HEURÍSTICA hace algo mucho más simple: desde donde está, ir siempre al pueblo no visitado más"
                        + " cercano. El dron vuela en línea recta: las distancias son km en línea recta.",
                "vecinoMasCercano(" + n.apply(0) + ")"));
        while (ruta.size() < tam) {
            int siguiente = -1;
            List<String> opciones = new ArrayList<>();
            for (int v = 0; v < tam; v++) {
                if (!visitado[v]) {
                    opciones.add(n.apply(v) + " " + d[actual][v]);
                    if (siguiente < 0 || d[actual][v] < d[actual][siguiente]) {
                        siguiente = v;
                    }
                }
            }
            visitado[siguiente] = true;
            ruta.add(siguiente);
            int previo = actual;
            actual = siguiente;
            pasos.add(paso(ruta, false, previo, siguiente, null, null, Tipo.ELIGE, 4, n,
                    "siguiente = más cercano a " + n.apply(previo) + "  →  " + n.apply(siguiente) + " ("
                            + d[previo][siguiente] + " km)",
                    "El while consulta si faltan pueblos: faltan " + (tam - ruta.size() + 1) + ". Desde " + n.apply(previo)
                            + ", los no visitados están a: " + String.join(", ", opciones) + " km. El más cercano es "
                            + n.apply(siguiente) + " (" + d[previo][siguiente] + " km): se agrega a la ruta y el viajante"
                            + " pasa a estar ahí. Es una decisión VORAZ: no mira qué pasará después.",
                    "Ir a " + n.apply(siguiente) + " (" + d[previo][siguiente] + " km)."));
        }
        pasos.add(paso(ruta, true, actual, 0, null, null, Tipo.CIERRA, 6, n,
                "volver: " + n.apply(actual) + " → " + n.apply(0) + " = " + d[actual][0] + " km;  total = " + largo(ruta),
                "Ya no faltan pueblos: el while termina y el viajante VUELVE a " + n.apply(0) + " (" + d[actual][0]
                        + " km). Ruta total: " + largo(ruta) + " km. Se calculó con unas " + tam * tam + " comparaciones."
                        + (d[actual][0] > largo(ruta) / tam * 2 ? " El regreso es largo: las decisiones voraces dejaron"
                        + " para el final un pueblo lejano. Es el punto débil típico de esta heurística." : ""),
                "Ruta del vecino más cercano: " + largo(ruta) + " km."));
        return pasos;
    }

    public List<EstadoViajante> pasosDosOpt(Map<String, String> nombres) {
        Function<Integer, String> n = x -> nombres.getOrDefault(pueblos.get(x), pueblos.get(x));
        List<Integer> ruta = vecino();
        List<EstadoViajante> pasos = new ArrayList<>();
        int inicial = largo(ruta);
        pasos.add(paso(ruta, true, null, null, null, null, Tipo.INICIO, 1, n,
                "dosOpt(ruta del vecino más cercano: " + inicial + " km)",
                "Se llama dosOpt con la ruta del vecino más cercano (" + inicial + " km). 2-opt es una BÚSQUEDA LOCAL:"
                        + " toma dos tramos que no se tocan, a → b y c → e, y prueba reemplazarlos por a → c y b → e"
                        + " (invirtiendo el pedazo del medio). Si acorta, lo hace. Repite hasta que ningún cambio"
                        + " mejore.",
                "dosOpt: parte de " + inicial + " km."));
        mejorar(ruta, pasos, n);
        pasos.add(paso(ruta, true, null, null, null, null, Tipo.FIN, 6, n,
                "return ruta  →  " + largo(ruta) + " km",
                "dosOpt retorna una ruta de " + largo(ruta) + " km (antes " + inicial + "). Ningún cambio de dos tramos"
                        + " la acorta. Para saber si es la MEJOR posible habría que compararla con la fuerza bruta: se"
                        + " puede con pocos pueblos (caso de esta semana), pero no con muchos.",
                "2-opt: " + largo(ruta) + " km."));
        return pasos;
    }

    private EstadoViajante paso(List<Integer> ruta, boolean cerrada, Integer actual, Integer candidato,
                                List<Integer> tramoA, List<Integer> tramoB, Tipo tipo, int linea,
                                Function<Integer, String> n, String evaluacion, String explicacion,
                                String descripcion) {
        List<String> r = ruta.stream().map(pueblos::get).toList();
        int km = 0;
        for (int k = 0; k + 1 < ruta.size(); k++) {
            km += d[ruta.get(k)][ruta.get(k + 1)];
        }
        if (cerrada && ruta.size() > 1) {
            km += d[ruta.get(ruta.size() - 1)][ruta.get(0)];
        }
        return new EstadoViajante(r, cerrada, actual == null ? null : pueblos.get(actual),
                candidato == null ? null : pueblos.get(candidato),
                tramoA == null ? null : tramoA.stream().map(pueblos::get).toList(),
                tramoB == null ? null : tramoB.stream().map(pueblos::get).toList(), km, tipo, linea,
                PasoTrazable.variables(
                        "actual", actual == null ? "—" : n.apply(actual),
                        "siguiente", candidato == null ? "—" : n.apply(candidato),
                        "ruta", String.join(" > ", ruta.stream().map(n).toList()),
                        "km", km),
                evaluacion, explicacion, descripcion);
    }

    public static long factorial(int k) {
        long f = 1;
        for (int i = 2; i <= k; i++) {
            f *= i;
        }
        return f;
    }
}
