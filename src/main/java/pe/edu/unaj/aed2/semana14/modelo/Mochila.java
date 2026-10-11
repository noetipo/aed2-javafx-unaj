package pe.edu.unaj.aed2.semana14.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana14.modelo.EstadoMochila.Tipo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * Mochila 0/1 con PROGRAMACIÓN DINÁMICA: cada producto se lleva entero o no se lleva, sin pasar la capacidad, y se
 * busca la mayor ganancia. dp[i][c] es la mejor ganancia usando solo los primeros i productos con capacidad c.
 */
public final class Mochila {

    /** Un producto (o proyecto): nombre, peso (o costo) y valor (o beneficio). */
    public record Producto(String nombre, int peso, int valor) {
    }

    /** Mejor valor y los índices (desde 0) de los productos elegidos. */
    public record Resultado(int mejor, List<Integer> elegidos) {
    }

    /** Los productos del visualizador: lo que un comerciante lleva a la feria (pesos en decenas de kg). */
    public static final List<Producto> FERIA = List.of(
            new Producto("Queso de Ayaviri", 2, 30),
            new Producto("Trucha", 6, 60),
            new Producto("Quinua", 5, 48),
            new Producto("Chuño", 5, 45),
            new Producto("Papa nativa", 3, 15));

    /** Capacidad del mototaxi de carga en el visualizador (decenas de kg). */
    public static final int CAPACIDAD_FERIA = 10;

    private Mochila() {
    }

    public static Resultado resolver(List<Producto> productos, int capacidad) {
        int n = productos.size();
        int[][] dp = tabla(productos, capacidad);
        List<Integer> elegidos = new ArrayList<>();
        int c = capacidad;
        for (int i = n; i >= 1; i--) {
            if (dp[i][c] != dp[i - 1][c]) {
                elegidos.add(0, i - 1);
                c -= productos.get(i - 1).peso();
            }
        }
        return new Resultado(dp[n][capacidad], elegidos);
    }

    private static int[][] tabla(List<Producto> productos, int capacidad) {
        int n = productos.size();
        int[][] dp = new int[n + 1][capacidad + 1];
        for (int i = 1; i <= n; i++) {
            int w = productos.get(i - 1).peso();
            int v = productos.get(i - 1).valor();
            for (int c = 0; c <= capacidad; c++) {
                dp[i][c] = dp[i - 1][c];
                if (w <= c && dp[i - 1][c - w] + v > dp[i][c]) {
                    dp[i][c] = dp[i - 1][c - w] + v;
                }
            }
        }
        return dp;
    }

    /**
     * Regla voraz para comparar: ordenar por valor/peso de mayor a menor y llevar cada producto si todavía cabe.
     * Rápida, pero no siempre óptima en la mochila 0/1.
     */
    public static Resultado voraz(List<Producto> productos, int capacidad) {
        List<Integer> orden = new ArrayList<>(IntStream.range(0, productos.size()).boxed().toList());
        orden.sort(Comparator.comparingDouble(k -> -(double) productos.get(k).valor() / productos.get(k).peso()));
        int libre = capacidad;
        int total = 0;
        List<Integer> elegidos = new ArrayList<>();
        for (int k : orden) {
            if (productos.get(k).peso() <= libre) {
                libre -= productos.get(k).peso();
                total += productos.get(k).valor();
                elegidos.add(k);
            }
        }
        elegidos.sort(null);
        return new Resultado(total, elegidos);
    }

    // ------------------------------------------------------------------
    // Pseudocódigo y código Java
    // ------------------------------------------------------------------

    public static final List<String> PSEUDOCODIGO = List.of(
            "mochila(peso, valor, C):",
            "    dp[0][c] ← 0 para todo c                     // sin productos, ganancia 0",
            "    para i ← 1 .. n:",
            "        para c ← 0 .. C:",
            "            dp[i][c] ← dp[i−1][c]                // opción 1: NO llevar el producto i",
            "            si peso[i] ≤ c y dp[i−1][c − peso[i]] + valor[i] > dp[i][c]:",
            "                dp[i][c] ← dp[i−1][c − peso[i]] + valor[i]   // opción 2: llevarlo",
            "    reconstruir: c ← C; para i ← n .. 1: si dp[i][c] ≠ dp[i−1][c]: llevar i; c ← c − peso[i]",
            "    retornar dp[n][C]");

    static final String JAVA = """
            import java.util.ArrayList;
            import java.util.List;

            public class Mochila {

                record Resultado(int mejor, List<Integer> elegidos) { }

                // Mochila 0/1: la mayor ganancia sin pasar la capacidad, y qué productos llevar.
                public static Resultado resolver(int[] peso, int[] valor, int capacidad) {          //@1
                    int n = peso.length;
                    int[][] dp = new int[n + 1][capacidad + 1];                                      //@2
                    for (int i = 1; i <= n; i++) {                                                   //@3
                        for (int c = 0; c <= capacidad; c++) {                                       //@4
                            dp[i][c] = dp[i - 1][c];                                                 //@5
                            if (peso[i - 1] <= c                                                     //@6
                                    && dp[i - 1][c - peso[i - 1]] + valor[i - 1] > dp[i][c]) {       //@6
                                dp[i][c] = dp[i - 1][c - peso[i - 1]] + valor[i - 1];                //@7
                            }
                        }
                    }
                    List<Integer> elegidos = new ArrayList<>();
                    int c = capacidad;
                    for (int i = n; i >= 1; i--) {                                                   //@8
                        if (dp[i][c] != dp[i - 1][c]) {                                              //@8
                            elegidos.add(0, i - 1);                                                  //@8
                            c -= peso[i - 1];                                                        //@8
                        }
                    }
                    return new Resultado(dp[n][capacidad], elegidos);                                //@9
                }

                public static void main(String[] args) {
                    String[] producto = {%s};
                    int[] peso = {%s};       // decenas de kg
                    int[] valor = {%s};      // ganancia en soles
                    Resultado r = resolver(peso, valor, %d);
                    List<String> llevar = new ArrayList<>();
                    for (int k : r.elegidos()) {
                        llevar.add(producto[k]);
                    }
                    System.out.println("Ganancia máxima: " + r.mejor() + " soles");
                    System.out.println("Llevar: " + llevar);
                }
            }
            """;

    public static final CodigoAlgoritmo CODIGO = new CodigoAlgoritmo(PSEUDOCODIGO, String.format(JAVA,
            String.join(", ", FERIA.stream().map(p -> "\"" + p.nombre() + "\"").toList()),
            String.join(", ", FERIA.stream().map(p -> String.valueOf(p.peso())).toList()),
            String.join(", ", FERIA.stream().map(p -> String.valueOf(p.valor())).toList()),
            CAPACIDAD_FERIA));

    // ------------------------------------------------------------------
    // Paso a paso
    // ------------------------------------------------------------------

    public static List<EstadoMochila> pasos(List<Producto> productos, int capacidad) {
        int n = productos.size();
        int[][] dp = new int[n + 1][capacidad + 1];
        for (int i = 1; i <= n; i++) {
            Arrays.fill(dp[i], -1);
        }
        List<EstadoMochila> pasos = new ArrayList<>();
        Set<Integer> elegidos = new LinkedHashSet<>();
        Set<String> recorrido = new LinkedHashSet<>();
        pasos.add(paso(dp, -1, -1, -1, elegidos, recorrido, Tipo.INICIO, 2, productos,
                "dp[0][c] = 0 para c = 0.." + capacidad,
                "Se llama mochila con " + n + " productos y capacidad " + capacidad + ". La idea de la PROGRAMACIÓN"
                        + " DINÁMICA: en vez de probar las 2^" + n + " = " + (1 << n) + " combinaciones, se llena una"
                        + " tabla donde dp[i][c] es la mejor ganancia usando solo los primeros i productos con"
                        + " capacidad c. Cada casilla se calcula con dos casillas de la fila de arriba, ya resueltas."
                        + " La fila 0 (ningún producto) vale 0.",
                "mochila: fila 0 en ceros."));
        for (int i = 1; i <= n; i++) {
            Producto p = productos.get(i - 1);
            for (int c = 0; c <= capacidad; c++) {
                int sinLlevar = dp[i - 1][c];
                String fila = (c == 0 ? "Empieza la fila " + i + " (" + p.nombre() + ", peso " + p.peso() + ", valor "
                        + p.valor() + "). " : "") + "Casilla dp[" + i + "][" + c + "]: con los primeros " + i
                        + " productos y capacidad " + c + ". Opción 1, NO llevar " + p.nombre() + ": se copia la"
                        + " casilla de arriba, dp[" + (i - 1) + "][" + c + "] = " + sinLlevar + ".";
                if (p.peso() > c) {
                    dp[i][c] = sinLlevar;
                    pasos.add(paso(dp, i, c, -1, elegidos, recorrido, Tipo.NO_CABE, 5, productos,
                            "peso[i-1] <= c  →  " + p.peso() + " <= " + c + "  →  falso  →  dp[i][c] = " + sinLlevar,
                            fila + " El if consulta si " + p.nombre() + " cabe: pesa " + p.peso() + " y la capacidad es "
                                    + c + ", NO cabe, así que queda " + sinLlevar + "." + siguiente(i, c, n, capacidad),
                            "dp[" + i + "][" + c + "] = " + sinLlevar + " (no cabe)."));
                    continue;
                }
                int llevando = dp[i - 1][c - p.peso()] + p.valor();
                String opcion2 = " Opción 2, LLEVARLO: ocupa " + p.peso() + ", así que queda capacidad " + (c - p.peso())
                        + " para los productos anteriores: dp[" + (i - 1) + "][" + (c - p.peso()) + "] + " + p.valor()
                        + " = " + dp[i - 1][c - p.peso()] + " + " + p.valor() + " = " + llevando + ".";
                if (llevando > sinLlevar) {
                    dp[i][c] = llevando;
                    pasos.add(paso(dp, i, c, c - p.peso(), elegidos, recorrido, Tipo.LLEVA, 7, productos,
                            "dp[i-1][c-peso[i-1]] + valor[i-1] > dp[i][c]  →  " + dp[i - 1][c - p.peso()] + " + "
                                    + p.valor() + " = " + llevando + " > " + sinLlevar + "  →  verdadero",
                            fila + opcion2 + " El if consulta si llevarlo es mejor: " + llevando + " > " + sinLlevar
                                    + " es verdadero, así que dp[" + i + "][" + c + "] = " + llevando + "."
                                    + siguiente(i, c, n, capacidad),
                            "dp[" + i + "][" + c + "] = " + llevando + " (llevando " + p.nombre() + ")."));
                } else {
                    dp[i][c] = sinLlevar;
                    pasos.add(paso(dp, i, c, c - p.peso(), elegidos, recorrido, Tipo.NO_CONVIENE, 6, productos,
                            "dp[i-1][c-peso[i-1]] + valor[i-1] > dp[i][c]  →  " + dp[i - 1][c - p.peso()] + " + "
                                    + p.valor() + " = " + llevando + " > " + sinLlevar + "  →  falso",
                            fila + opcion2 + " El if consulta si llevarlo es mejor: " + llevando + " > " + sinLlevar
                                    + " es falso, así que se queda " + sinLlevar + " (no llevarlo)."
                                    + siguiente(i, c, n, capacidad),
                            "dp[" + i + "][" + c + "] = " + sinLlevar + " (no conviene)."));
                }
            }
        }
        int c = capacidad;
        for (int i = n; i >= 1; i--) {
            Producto p = productos.get(i - 1);
            recorrido.add(i + "," + c);
            boolean lleva = dp[i][c] != dp[i - 1][c];
            int antes = c;
            if (lleva) {
                elegidos.add(i - 1);
                c -= p.peso();
            }
            pasos.add(paso(dp, i, antes, lleva ? c : -1, elegidos, recorrido, Tipo.RECONSTRUYE, 8, productos,
                    "dp[i][c] != dp[i-1][c]  →  " + dp[i][antes] + " != " + dp[i - 1][antes] + "  →  " + (lleva
                            ? "verdadero  →  llevar " + p.nombre() + ";  c = " + antes + " - " + p.peso() + " = " + c
                            : "falso"),
                    (i == n ? "La tabla está llena: la mejor ganancia es dp[" + n + "][" + capacidad + "] = "
                            + dp[n][capacidad] + ". Para saber QUÉ llevar se reconstruye desde esa casilla hacia"
                            + " arriba. " : "")
                            + "¿dp[" + i + "][" + antes + "] = " + dp[i][antes] + " es distinto de la casilla de arriba ("
                            + dp[i - 1][antes] + ")? " + (lleva ? "SÍ: el valor cambió al considerar " + p.nombre()
                            + ", así que se LLEVA. Quedan " + c + " de capacidad para los anteriores: se sube a la fila "
                            + (i - 1) + ", columna " + c + "."
                            : "NO: la mejor ganancia ya se lograba sin " + p.nombre() + ", así que no se lleva y se sube"
                            + " a la fila " + (i - 1) + " en la misma columna."),
                    lleva ? "Llevar " + p.nombre() + "." : "No llevar " + p.nombre() + "."));
        }
        int pesoTotal = elegidos.stream().mapToInt(k -> productos.get(k).peso()).sum();
        List<String> nombres = elegidos.stream().sorted().map(k -> productos.get(k).nombre()).toList();
        pasos.add(paso(dp, -1, -1, -1, elegidos, recorrido, Tipo.FIN, 9, productos,
                "return dp[n][C] = " + dp[n][capacidad],
                "Se retorna " + dp[n][capacidad] + ": llevar " + String.join(", ", nombres) + " (peso " + pesoTotal
                        + " de " + capacidad + "). Se calcularon " + n * (capacidad + 1) + " casillas, cada una en O(1):"
                        + " O(n · C), en lugar de revisar las " + (1 << n) + " combinaciones.",
                "Ganancia máxima: " + dp[n][capacidad] + "."));
        return pasos;
    }

    private static String siguiente(int i, int c, int n, int capacidad) {
        if (c < capacidad) {
            return " El for de c sigue con la columna " + (c + 1) + ".";
        }
        return i < n ? " Fila completa: el for de i pasa al producto " + (i + 1) + "."
                : " Era la última casilla: la tabla está llena.";
    }

    private static EstadoMochila paso(int[][] dp, int i, int c, int diagonal, Set<Integer> elegidos,
                                      Set<String> recorrido, Tipo tipo, int linea, List<Producto> productos,
                                      String evaluacion, String explicacion, String descripcion) {
        List<List<Integer>> tabla = Arrays.stream(dp).map(f -> Arrays.stream(f).boxed().toList()).toList();
        boolean hay = i >= 1 && c >= 0;
        Producto p = hay ? productos.get(i - 1) : null;
        return new EstadoMochila(tabla, i, c, diagonal, elegidos, recorrido, tipo, linea,
                PasoTrazable.variables(
                        "i", hay ? i : "—",
                        "c", hay ? c : "—",
                        "peso[i-1]", hay ? p.peso() : "—",
                        "valor[i-1]", hay ? p.valor() : "—",
                        "dp[i-1][c]", hay ? dp[i - 1][c] : "—",
                        "dp[i][c]", hay && dp[i][c] >= 0 ? dp[i][c] : "—"),
                evaluacion, explicacion, descripcion);
    }

    /** Para la vista y las pruebas: la tabla completa. */
    public static int[][] tablaCompleta(List<Producto> productos, int capacidad) {
        return tabla(productos, capacidad);
    }

    /** Valor total de una selección de índices. */
    public static int valor(List<Producto> productos, List<Integer> elegidos) {
        return elegidos.stream().mapToInt(k -> productos.get(k).valor()).sum();
    }

    /** Peso total de una selección de índices. */
    public static int peso(List<Producto> productos, List<Integer> elegidos) {
        return elegidos.stream().mapToInt(k -> productos.get(k).peso()).sum();
    }
}
