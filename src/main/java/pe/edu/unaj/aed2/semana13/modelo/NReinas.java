package pe.edu.unaj.aed2.semana13.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana13.modelo.EstadoReinas.Tipo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * N reinas con BACKTRACKING: se coloca una reina por fila; si una fila no tiene ninguna columna segura, se vuelve a
 * la fila anterior y se mueve su reina. Sirve para ver cómo la búsqueda prueba, falla y deshace.
 */
public final class NReinas {

    private NReinas() {
    }

    /** Primera solución (columna de cada fila) en el orden de prueba de izquierda a derecha, o null si no hay. */
    public static int[] resolver(int n) {
        int[] tablero = new int[n];
        Arrays.fill(tablero, -1);
        return colocar(tablero, 0) ? tablero : null;
    }

    private static boolean colocar(int[] tablero, int fila) {
        int n = tablero.length;
        if (fila == n) {
            return true;
        }
        for (int col = 0; col < n; col++) {
            if (atacante(tablero, fila, col) < 0) {
                tablero[fila] = col;
                if (colocar(tablero, fila + 1)) {
                    return true;
                }
                tablero[fila] = -1;
            }
        }
        return false;
    }

    /** Fila de la primera reina de arriba que ataca (fila, col): misma columna o misma diagonal; -1 si ninguna. */
    public static int atacante(int[] tablero, int fila, int col) {
        for (int f = 0; f < fila; f++) {
            if (tablero[f] == col || Math.abs(tablero[f] - col) == fila - f) {
                return f;
            }
        }
        return -1;
    }

    // ------------------------------------------------------------------
    // Pseudocódigo y código Java
    // ------------------------------------------------------------------

    public static final List<String> PSEUDOCODIGO = List.of(
            "colocar(fila):",
            "    si fila = n: retornar verdadero                 // todas las filas tienen reina",
            "    para col ← 0 .. n − 1:",
            "        si ninguna reina de arriba ataca (fila, col):   // misma columna o diagonal",
            "            tablero[fila] ← col                     // probar aquí",
            "            si colocar(fila + 1): retornar verdadero",
            "            tablero[fila] ← −1                      // deshacer: BACKTRACKING",
            "    retornar falso                                  // ninguna columna sirvió");

    static final String JAVA = """
            import java.util.Arrays;

            public class NReinas {

                // Retorna la columna de la reina de cada fila, o null si no hay solución.
                public static int[] resolver(int n) {
                    int[] tablero = new int[n];
                    Arrays.fill(tablero, -1);
                    return colocar(tablero, 0) ? tablero : null;
                }

                private static boolean colocar(int[] tablero, int fila) {                  //@1
                    int n = tablero.length;
                    if (fila == n) {                                                         //@2
                        return true;                                                         //@2
                    }
                    for (int col = 0; col < n; col++) {                                      //@3
                        if (esSegura(tablero, fila, col)) {                                  //@4
                            tablero[fila] = col;                                             //@5
                            if (colocar(tablero, fila + 1)) {                                //@6
                                return true;                                                 //@6
                            }
                            tablero[fila] = -1;                                              //@7
                        }
                    }
                    return false;                                                            //@8
                }

                // ¿Ninguna reina de las filas de arriba ataca (fila, col)? Ni misma columna ni misma diagonal.
                private static boolean esSegura(int[] tablero, int fila, int col) {
                    for (int f = 0; f < fila; f++) {
                        if (tablero[f] == col || Math.abs(tablero[f] - col) == fila - f) {
                            return false;
                        }
                    }
                    return true;
                }

                public static void main(String[] args) {
                    int[] t = resolver(8);
                    for (int fila = 0; fila < t.length; fila++) {
                        StringBuilder linea = new StringBuilder();
                        for (int col = 0; col < t.length; col++) {
                            linea.append(t[fila] == col ? "Q " : ". ");
                        }
                        System.out.println(linea);
                    }
                    System.out.println("Columnas por fila: " + Arrays.toString(t));
                }
            }
            """;

    public static final CodigoAlgoritmo CODIGO = new CodigoAlgoritmo(PSEUDOCODIGO, JAVA);

    // ------------------------------------------------------------------
    // Paso a paso
    // ------------------------------------------------------------------

    public static List<EstadoReinas> pasos(int n) {
        List<EstadoReinas> pasos = new ArrayList<>();
        int[] tablero = new int[n];
        Arrays.fill(tablero, -1);
        int[] intentos = {0};
        pasos.add(paso(tablero, 0, -1, -1, intentos[0], Tipo.INICIO, 1, n,
                "colocar(0)",
                "Se llama resolver(" + n + "), que llama colocar(0). Hay que poner " + n + " reinas en un tablero de "
                        + n + " × " + n + " sin que ninguna ataque a otra (misma fila, columna o diagonal). Como cada fila"
                        + " debe tener exactamente una reina, se va FILA POR FILA: en cada una se prueban las columnas"
                        + " de izquierda a derecha.",
                "colocar(0)"));
        pasos(tablero, 0, intentos, pasos);
        return pasos;
    }

    private static boolean pasos(int[] tablero, int fila, int[] intentos, List<EstadoReinas> pasos) {
        int n = tablero.length;
        if (fila == n) {
            pasos.add(paso(tablero, -1, -1, -1, intentos[0], Tipo.SOLUCION, 2, n,
                    "fila == n  →  " + n + " == " + n + "  →  verdadero  →  return true",
                    "colocar(" + n + ") consulta si ya se llenaron todas las filas: " + n + " == " + n + " es verdadero."
                            + " ¡Hay SOLUCIÓN! Retorna verdadero a colocar(" + (n - 1) + "), que también retorna"
                            + " verdadero en la línea 6, y así hacia arriba hasta resolver(). Se probaron " + intentos[0]
                            + " casillas.",
                    "¡Solución! " + Arrays.toString(tablero)));
            return true;
        }
        for (int col = 0; col < n; col++) {
            intentos[0]++;
            int f = atacante(tablero, fila, col);
            if (f >= 0) {
                boolean columna = tablero[f] == col;
                pasos.add(paso(tablero, fila, col, f, intentos[0], Tipo.ATACADA, 4, n,
                        columna ? "tablero[" + f + "] == col  →  " + tablero[f] + " == " + col + "  →  atacada"
                                : "|tablero[" + f + "] - col| == fila - f  →  |" + tablero[f] + " - " + col + "| == "
                                + fila + " - " + f + "  →  atacada",
                        "El for de colocar(" + fila + ") prueba la columna " + col + ". El if consulta si alguna reina"
                                + " de arriba la ataca: la de la fila " + f + " (columna " + tablero[f] + ") está en la"
                                + (columna ? " MISMA COLUMNA." : " misma DIAGONAL: está a " + (fila - f) + " filas y "
                                + Math.abs(tablero[f] - col) + " columnas de distancia.") + " No se puede poner aquí; "
                                + (col < n - 1 ? "el for prueba la columna " + (col + 1) + "."
                                : "era la última columna, así que el for termina."),
                        "(" + fila + ", " + col + ") atacada por la fila " + f + "."));
                continue;
            }
            tablero[fila] = col;
            pasos.add(paso(tablero, fila, col, -1, intentos[0], Tipo.COLOCA, 5, n,
                    "esSegura(" + fila + ", " + col + ")  →  verdadero  →  tablero[" + fila + "] = " + col,
                    "El for de colocar(" + fila + ") prueba la columna " + col + ". El if consulta si alguna reina de"
                            + " arriba la ataca: " + (fila == 0 ? "no hay reinas arriba." : "ninguna (ni misma columna"
                            + " ni diagonal).") + " Se coloca la reina en (" + fila + ", " + col + ") y la línea 6 llama"
                            + " colocar(" + (fila + 1) + ") para seguir con la fila de abajo. Si esa llamada fracasa, la"
                            + " ejecución volverá aquí.",
                    "Reina en (" + fila + ", " + col + ")."));
            if (pasos(tablero, fila + 1, intentos, pasos)) {
                return true;
            }
            tablero[fila] = -1;
            pasos.add(paso(tablero, fila, col, -1, intentos[0], Tipo.QUITA, 7, n,
                    "colocar(" + (fila + 1) + ") == falso  →  tablero[" + fila + "] = -1",
                    "colocar(" + (fila + 1) + ") retornó falso: con la reina de la fila " + fila + " en la columna " + col
                            + " no hay forma de completar el tablero. BACKTRACKING: se QUITA esa reina (línea 7) y el for"
                            + " de colocar(" + fila + ") " + (col < n - 1 ? "prueba la columna " + (col + 1) + "."
                            : "termina, porque era la última columna."),
                    "Quita la reina de (" + fila + ", " + col + ")."));
        }
        pasos.add(paso(tablero, fila, -1, -1, intentos[0], Tipo.RETROCEDE, 8, n,
                "for terminado  →  return false",
                "colocar(" + fila + ") probó las " + n + " columnas y ninguna sirvió. Retorna FALSO a "
                        + (fila == 0 ? "resolver(): no hay solución para n = " + n + "."
                        : "colocar(" + (fila - 1) + "), que regresa a su línea 6, quitará su reina (columna "
                        + tablero[fila - 1] + ") y probará la siguiente columna."),
                "Fila " + fila + " sin salida: volver a la fila " + (fila - 1) + "."));
        return false;
    }

    private static EstadoReinas paso(int[] tablero, int fila, int col, int atacante, int intentos, Tipo tipo,
                                     int linea, int n, String evaluacion, String explicacion, String descripcion) {
        List<Integer> t = Arrays.stream(tablero).boxed().toList();
        return new EstadoReinas(t, fila, col, atacante, intentos, tipo, linea,
                PasoTrazable.variables(
                        "fila", fila < 0 ? "—" : fila,
                        "col", col < 0 ? "—" : col,
                        "n", n,
                        "tablero", Arrays.toString(tablero)),
                evaluacion, explicacion, descripcion);
    }
}
