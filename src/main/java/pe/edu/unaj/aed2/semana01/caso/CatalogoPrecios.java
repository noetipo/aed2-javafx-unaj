package pe.edu.unaj.aed2.semana01.caso;

import pe.edu.unaj.aed2.semana01.modelo.Ordenamientos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Caso práctico semana 01: catálogo de precios de mercados de Juliaca y Puno.
 * Lógica pura (sin JavaFX): carga desde CSV y ordena con merge sort.
 */
public class CatalogoPrecios {

    public record Producto(String nombre, String mercado, String unidad, double precio) {
    }

    public static final String RECURSO = "/pe/edu/unaj/aed2/semana01/precios_mercado.csv";

    private final List<Producto> productos;

    public CatalogoPrecios(List<Producto> productos) {
        this.productos = new ArrayList<>(productos);
    }

    /** Carga el CSV incluido en los recursos del proyecto. */
    public static CatalogoPrecios desdeRecurso() {
        try (InputStream in = CatalogoPrecios.class.getResourceAsStream(RECURSO)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró " + RECURSO);
            }
            return desdeCsv(in);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Formato: producto,mercado,unidad,precio (primera línea = cabecera). */
    public static CatalogoPrecios desdeCsv(InputStream in) throws IOException {
        List<Producto> lista = new ArrayList<>();
        BufferedReader lector = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        lector.readLine();
        String linea;
        while ((linea = lector.readLine()) != null) {
            if (linea.isBlank()) {
                continue;
            }
            String[] c = linea.split(",");
            lista.add(new Producto(c[0].trim(), c[1].trim(), c[2].trim(), Double.parseDouble(c[3].trim())));
        }
        return new CatalogoPrecios(lista);
    }

    public List<Producto> productos() {
        return List.copyOf(productos);
    }

    public List<Producto> ordenadosPorPrecio(boolean ascendente) {
        Comparator<Producto> criterio = Comparator.comparingDouble(Producto::precio);
        List<Producto> copia = new ArrayList<>(productos);
        Ordenamientos.mergeSort(copia, ascendente ? criterio : criterio.reversed());
        return copia;
    }

    public List<Producto> ordenadosPorNombre() {
        List<Producto> copia = new ArrayList<>(productos);
        Ordenamientos.mergeSort(copia, Comparator.comparing(Producto::nombre, String.CASE_INSENSITIVE_ORDER));
        return copia;
    }
}
