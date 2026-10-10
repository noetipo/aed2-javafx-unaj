package pe.edu.unaj.aed2.semana04.caso;

import pe.edu.unaj.aed2.semana04.modelo.ArbolBusqueda;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Caso práctico semana 04: diccionario quechua–español guardado en un ABB.
 * <p>
 * El mismo vocabulario se inserta de dos formas: en orden alfabético (como viene el archivo)
 * y en orden mezclado. El primero degenera en una "lista" de altura n; el segundo queda mucho
 * más bajo. Así se ve que el costo de un ABB depende de su ALTURA, no solo de n.
 */
public class DiccionarioQuechua {

    /** Palabra con su significado; se ordena y compara solo por la palabra. */
    public record Entrada(String palabra, String significado) implements Comparable<Entrada> {
        @Override
        public int compareTo(Entrada otra) {
            return palabra.compareTo(otra.palabra);
        }

        @Override
        public String toString() {
            return palabra;
        }
    }

    public static final String RECURSO = "/pe/edu/unaj/aed2/semana04/diccionario_quechua.csv";
    private static final long SEMILLA = 7;

    private final List<Entrada> alfabetico;
    private final List<Entrada> mezclado;
    private final ArbolBusqueda<Entrada> arbolAlfabetico = new ArbolBusqueda<>();
    private final ArbolBusqueda<Entrada> arbolMezclado = new ArbolBusqueda<>();

    public DiccionarioQuechua(List<Entrada> entradas) {
        alfabetico = new ArrayList<>(entradas);
        Collections.sort(alfabetico);
        mezclado = new ArrayList<>(alfabetico);
        Collections.shuffle(mezclado, new Random(SEMILLA));
        alfabetico.forEach(arbolAlfabetico::insertar);
        mezclado.forEach(arbolMezclado::insertar);
    }

    public static DiccionarioQuechua desdeRecurso() {
        try (InputStream in = DiccionarioQuechua.class.getResourceAsStream(RECURSO)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró " + RECURSO);
            }
            BufferedReader lector = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            lector.readLine();
            List<Entrada> lista = new ArrayList<>();
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.isBlank()) {
                    String[] c = linea.split(";");
                    lista.add(new Entrada(c[0].trim(), c[1].trim()));
                }
            }
            return new DiccionarioQuechua(lista);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public int tamanio() {
        return alfabetico.size();
    }

    public List<Entrada> entradas() {
        return List.copyOf(alfabetico);
    }

    /** Orden en que se insertó el árbol mezclado. */
    public List<Entrada> ordenMezclado() {
        return List.copyOf(mezclado);
    }

    public ArbolBusqueda<Entrada> arbolAlfabetico() {
        return arbolAlfabetico;
    }

    public ArbolBusqueda<Entrada> arbolMezclado() {
        return arbolMezclado;
    }

    /** Significado de la palabra, o null si no está. */
    public String traducir(String palabra) {
        Entrada e = arbolMezclado.obtener(new Entrada(palabra, ""));
        return e == null ? null : e.significado();
    }

    public int comparacionesAlfabetico(String palabra) {
        return arbolAlfabetico.comparaciones(new Entrada(palabra, ""));
    }

    public int comparacionesMezclado(String palabra) {
        return arbolMezclado.comparaciones(new Entrada(palabra, ""));
    }

    /** Promedio de comparaciones para encontrar cada palabra: {alfabético, mezclado}. */
    public double[] promedios() {
        double a = 0;
        double m = 0;
        for (Entrada e : alfabetico) {
            a += arbolAlfabetico.comparaciones(e);
            m += arbolMezclado.comparaciones(e);
        }
        return new double[]{a / alfabetico.size(), m / alfabetico.size()};
    }

    /** Altura mínima posible con n nodos: ⌈log₂(n + 1)⌉. */
    public int alturaMinima() {
        return 32 - Integer.numberOfLeadingZeros(alfabetico.size());
    }
}
