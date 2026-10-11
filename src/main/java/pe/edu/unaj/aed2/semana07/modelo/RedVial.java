package pe.edu.unaj.aed2.semana07.modelo;

import pe.edu.unaj.aed2.comun.mapa.MapaSimplificado;
import pe.edu.unaj.aed2.comun.mapa.MapaSimplificado.Lugar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Red vial SIMPLIFICADA de la región Puno, con fines didácticos: pueblos con coordenadas aproximadas
 * y algunas carreteras que los unen. No pretende incluir todas las vías.
 */
public final class RedVial {

    private static final String PUEBLOS = "/pe/edu/unaj/aed2/semana07/pueblos.csv";
    private static final String CARRETERAS = "/pe/edu/unaj/aed2/semana07/carreteras.csv";

    private final Map<String, Lugar> pueblos;
    private final Grafo grafo;

    private RedVial(Map<String, Lugar> pueblos, Grafo grafo) {
        this.pueblos = pueblos;
        this.grafo = grafo;
    }

    public static RedVial cargar() {
        Map<String, Lugar> pueblos = new LinkedHashMap<>();
        for (String[] c : leer(PUEBLOS)) {
            pueblos.put(c[0], new Lugar(c[0], c[1], Double.parseDouble(c[2]), Double.parseDouble(c[3])));
        }
        Grafo grafo = new Grafo();
        pueblos.keySet().forEach(grafo::agregarVertice);
        for (String[] c : leer(CARRETERAS)) {
            if (!pueblos.containsKey(c[0]) || !pueblos.containsKey(c[1])) {
                throw new IllegalStateException("Carretera con un pueblo desconocido: " + c[0] + "-" + c[1]);
            }
            grafo.agregarArista(c[0], c[1]);
        }
        return new RedVial(pueblos, grafo);
    }

    private static List<String[]> leer(String recurso) {
        try (InputStream in = RedVial.class.getResourceAsStream(recurso)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró " + recurso);
            }
            BufferedReader lector = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            lector.readLine();
            List<String[]> filas = new ArrayList<>();
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.isBlank()) {
                    filas.add(linea.split(";"));
                }
            }
            return filas;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Grafo grafo() {
        return grafo;
    }

    public Map<String, Lugar> pueblos() {
        return pueblos;
    }

    public List<Lugar> lugares() {
        return List.copyOf(pueblos.values());
    }

    /** id → nombre, para mostrar nombres en lugar de códigos. */
    public Map<String, String> nombres() {
        Map<String, String> m = new LinkedHashMap<>();
        pueblos.forEach((id, l) -> m.put(id, l.nombre()));
        return m;
    }

    /** Distancia en línea recta (km) entre dos pueblos: referencial, la carretera es más larga. */
    public double distanciaKm(String u, String v) {
        return MapaSimplificado.distanciaKm(pueblos.get(u), pueblos.get(v));
    }
}
