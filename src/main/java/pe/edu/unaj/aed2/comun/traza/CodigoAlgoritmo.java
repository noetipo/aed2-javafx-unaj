package pe.edu.unaj.aed2.comun.traza;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pseudocódigo y código Java de un algoritmo, enlazados línea a línea.
 * <p>
 * En el código Java, cada línea que corresponde a una línea del pseudocódigo
 * termina con un marcador {@code //@n} (o {@code //@n,m}). El marcador no se
 * muestra: solo sirve para resaltar las líneas Java del paso actual.
 *
 * <pre>{@code
 * if (a[j] > a[j + 1]) {     //@4
 *     intercambiar(a, j);    //@5
 * }
 * }</pre>
 * Cuando una misma clase tiene varios métodos animables (p. ej. insertar y buscar), cada
 * marcador lleva la etiqueta del método: {@code //@insertar:2 buscar:2}. Con
 * {@link #seccion(List, String, String)} se elige qué etiqueta enlazar; las demás se ignoran.
 */
public final class CodigoAlgoritmo {

    private static final Pattern MARCADOR = Pattern.compile("\\s*//@([\\w:,\\s]+)$");

    private final List<String> pseudocodigo;
    private final List<String> java;
    private final List<Set<Integer>> enlaces;

    public CodigoAlgoritmo(List<String> pseudocodigo, String javaConMarcadores) {
        this(pseudocodigo, javaConMarcadores, null);
    }

    /** Enlaza solo los marcadores {@code //@etiqueta:n} de la clase completa {@code javaConMarcadores}. */
    public static CodigoAlgoritmo seccion(List<String> pseudocodigo, String javaConMarcadores, String etiqueta) {
        return new CodigoAlgoritmo(pseudocodigo, javaConMarcadores, etiqueta);
    }

    private CodigoAlgoritmo(List<String> pseudocodigo, String javaConMarcadores, String etiqueta) {
        this.pseudocodigo = List.copyOf(pseudocodigo);
        List<String> lineas = new ArrayList<>();
        List<Set<Integer>> marcas = new ArrayList<>();
        for (String linea : javaConMarcadores.stripTrailing().split("\n", -1)) {
            Matcher m = MARCADOR.matcher(linea);
            Set<Integer> conjunto = new LinkedHashSet<>();
            if (m.find()) {
                for (String token : m.group(1).trim().split("\\s+")) {
                    String numeros = token;
                    int dosPuntos = token.indexOf(':');
                    if (dosPuntos >= 0) {
                        if (!token.substring(0, dosPuntos).equals(etiqueta)) {
                            continue;
                        }
                        numeros = token.substring(dosPuntos + 1);
                    } else if (etiqueta != null) {
                        continue;
                    }
                    for (String n : numeros.split(",")) {
                        if (!n.isBlank()) {
                            conjunto.add(Integer.parseInt(n.trim()));
                        }
                    }
                }
                linea = linea.substring(0, m.start());
            }
            lineas.add(linea);
            marcas.add(Collections.unmodifiableSet(conjunto));
        }
        this.java = List.copyOf(lineas);
        this.enlaces = List.copyOf(marcas);
    }

    public List<String> pseudocodigo() {
        return pseudocodigo;
    }

    /** Código Java sin marcadores. */
    public List<String> java() {
        return java;
    }

    /**
     * La línea Java donde realmente ocurre la línea {@code lineaPseudocodigo}: la primera enlazada solo
     * a ella. Así, en {@code int p = particionar(...)  //@3,4,5,6,7} (la llamada) gana la línea
     * de adentro de particionar. Retorna 0 si no hay ninguna.
     */
    public int lineaPrincipal(int lineaPseudocodigo) {
        int primera = 0;
        for (int k = 0; k < enlaces.size(); k++) {
            if (enlaces.get(k).contains(lineaPseudocodigo)) {
                if (enlaces.get(k).size() == 1) {
                    return k + 1;
                }
                if (primera == 0) {
                    primera = k + 1;
                }
            }
        }
        return primera;
    }

    /** true si la línea Java {@code numero} (desde 1) está enlazada con el pseudocódigo. */
    public boolean estaEnlazada(int numero) {
        return numero >= 1 && numero <= enlaces.size() && !enlaces.get(numero - 1).isEmpty();
    }

    /** Líneas Java (desde 1) que corresponden a la línea {@code lineaPseudocodigo}. */
    public Set<Integer> lineasJava(int lineaPseudocodigo) {
        Set<Integer> resultado = new LinkedHashSet<>();
        for (int k = 0; k < enlaces.size(); k++) {
            if (enlaces.get(k).contains(lineaPseudocodigo)) {
                resultado.add(k + 1);
            }
        }
        return resultado;
    }

    /**
     * Valores de las variables que aparecen en una línea de código, p. ej.
     * {@code "j = 4 · a[j] = 95 · a[j+1] = 101"}. Compara sin espacios para que
     * la variable "a[j+1]" coincida con el código "a[j + 1]".
     */
    public static String valoresEnLinea(String linea, Map<String, String> valores) {
        int comentario = linea.indexOf("//");
        if (comentario >= 0) {
            linea = linea.substring(0, comentario); // los comentarios no se ejecutan
        }
        String compacta = linea.replace(" ", "");
        List<String> partes = new ArrayList<>();
        for (Map.Entry<String, String> e : valores.entrySet()) {
            String nombre = e.getKey();
            if (e.getValue().equals("—")) {
                continue;
            }
            boolean aparece = nombre.matches("\\w+")
                    ? Pattern.compile("\\b" + Pattern.quote(nombre) + "\\b").matcher(linea).find()
                    : compacta.contains(nombre.replace(" ", ""));
            if (aparece) {
                partes.add(nombre + " = " + e.getValue());
            }
        }
        return String.join(" · ", partes);
    }
}
