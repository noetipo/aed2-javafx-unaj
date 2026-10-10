package pe.edu.unaj.aed2.comun.traza;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
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
 */
public final class CodigoAlgoritmo {

    private static final Pattern MARCADOR = Pattern.compile("\\s*//@([\\d,]+)\\s*$");

    private final List<String> pseudocodigo;
    private final List<String> java;
    private final List<Set<Integer>> enlaces;

    public CodigoAlgoritmo(List<String> pseudocodigo, String javaConMarcadores) {
        this.pseudocodigo = List.copyOf(pseudocodigo);
        List<String> lineas = new ArrayList<>();
        List<Set<Integer>> marcas = new ArrayList<>();
        for (String linea : javaConMarcadores.stripTrailing().split("\n", -1)) {
            Matcher m = MARCADOR.matcher(linea);
            Set<Integer> conjunto = new LinkedHashSet<>();
            if (m.find()) {
                for (String n : m.group(1).split(",")) {
                    if (!n.isBlank()) {
                        conjunto.add(Integer.parseInt(n.trim()));
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
}
