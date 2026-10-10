package pe.edu.unaj.aed2.semana03.caso;

import pe.edu.unaj.aed2.semana03.modelo.TablaHash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Caso práctico semana 03: padrón (ficticio) de mototaxis de Juliaca indexado por código.
 * Lógica pura (sin JavaFX): compara la búsqueda en la tabla hash con la búsqueda lineal.
 */
public class PadronMototaxis {

    public record Mototaxi(String codigo, String propietario, String asociacion, String paradero) {
    }

    public static final String RECURSO = "/pe/edu/unaj/aed2/semana03/padron_mototaxis.csv";
    private static final int CAPACIDAD_INICIAL = 11;

    private final List<Mototaxi> lista;
    private final TablaHash<Mototaxi> tabla = new TablaHash<>(CAPACIDAD_INICIAL);

    public PadronMototaxis(List<Mototaxi> mototaxis) {
        this.lista = List.copyOf(mototaxis);
        for (Mototaxi m : lista) {
            tabla.insertar(m.codigo(), m);
        }
    }

    public static PadronMototaxis desdeRecurso() {
        try (InputStream in = PadronMototaxis.class.getResourceAsStream(RECURSO)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró " + RECURSO);
            }
            BufferedReader lector = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            lector.readLine();
            List<Mototaxi> lista = new ArrayList<>();
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.isBlank()) {
                    String[] c = linea.split(",");
                    lista.add(new Mototaxi(c[0].trim(), c[1].trim(), c[2].trim(), c[3].trim()));
                }
            }
            return new PadronMototaxis(lista);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<Mototaxi> mototaxis() {
        return lista;
    }

    /** Búsqueda en la tabla hash; null si no existe. */
    public Mototaxi buscar(String codigo) {
        return tabla.buscar(codigo);
    }

    public int comparacionesHash(String codigo) {
        return tabla.comparaciones(codigo);
    }

    /** Comparaciones de una búsqueda lineal en el orden del padrón (n si no está). */
    public int comparacionesLineal(String codigo) {
        for (int k = 0; k < lista.size(); k++) {
            if (lista.get(k).codigo().equals(codigo)) {
                return k + 1;
            }
        }
        return lista.size();
    }

    public int capacidad() {
        return tabla.capacidad();
    }

    public double factorCarga() {
        return tabla.factorCarga();
    }

    public int[] longitudesCadenas() {
        return tabla.longitudes();
    }

    /** Promedio de comparaciones de una búsqueda exitosa (todas las claves): {hash, lineal}. */
    public double[] promedios() {
        double hash = 0;
        double lineal = 0;
        for (Mototaxi m : lista) {
            hash += comparacionesHash(m.codigo());
            lineal += comparacionesLineal(m.codigo());
        }
        return new double[]{hash / lista.size(), lineal / lista.size()};
    }

    /** Padrón con solo los primeros {@code n} registros (para ver cómo crece el costo). */
    public PadronMototaxis primeros(int n) {
        return new PadronMototaxis(lista.subList(0, Math.min(n, lista.size())));
    }
}
