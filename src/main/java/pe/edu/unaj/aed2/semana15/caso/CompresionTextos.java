package pe.edu.unaj.aed2.semana15.caso;

import pe.edu.unaj.aed2.semana15.modelo.Huffman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Caso práctico semana 15: ¿cuánto se comprimen mensajes en quechua, aimara o castellano con Huffman? Compara con ASCII
 * (8 bits por carácter) y con un código de largo fijo. También muestra por qué un texto "parejo" casi no se comprime.
 */
public class CompresionTextos {

    /** Una fila de la tabla: un carácter, cuántas veces aparece, su código y los bits que aporta. */
    public record Fila(char caracter, int veces, String codigo) {
        public int bits() {
            return veces * codigo.length();
        }
    }

    /** Resumen de un texto. */
    public record Resumen(int caracteres, int ascii, int fijo, int huffman, String bits, String decodificado) {
        public double ahorro() {
            return ascii == 0 ? 0 : 100.0 * (ascii - huffman) / ascii;
        }
    }

    private static final String FRASES = "/pe/edu/unaj/aed2/semana15/frases.csv";

    private CompresionTextos() {
    }

    /** idioma → frase de ejemplo. */
    public static Map<String, String> frases() {
        Map<String, String> m = new LinkedHashMap<>();
        try (InputStream in = CompresionTextos.class.getResourceAsStream(FRASES)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró " + FRASES);
            }
            BufferedReader lector = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            lector.readLine();
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.isBlank()) {
                    int corte = linea.indexOf(';');
                    m.put(linea.substring(0, corte), linea.substring(corte + 1));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return m;
    }

    /** Filas de la tabla, de más a menos frecuente. */
    public static List<Fila> tabla(String texto) {
        Map<Character, String> codigos = Huffman.codigos(texto);
        List<Fila> filas = new ArrayList<>();
        Huffman.frecuencias(texto).forEach((c, f) -> filas.add(new Fila(c, f, codigos.get(c))));
        filas.sort(Comparator.comparingInt((Fila f) -> -f.veces()).thenComparing(Fila::caracter));
        return filas;
    }

    public static Resumen resumir(String texto) {
        Map<Character, String> codigos = Huffman.codigos(texto);
        String bits = Huffman.codificar(texto, codigos);
        return new Resumen(texto.length(), 8 * texto.length(), Huffman.bitsFijos(texto), bits.length(), bits,
                Huffman.decodificar(bits, Huffman.arbol(texto)));
    }
}
