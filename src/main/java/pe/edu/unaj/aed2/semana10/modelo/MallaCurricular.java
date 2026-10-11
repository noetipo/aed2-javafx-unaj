package pe.edu.unaj.aed2.semana10.modelo;

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
 * Malla curricular FICTICIA y simplificada de una carrera de ingeniería, con fines didácticos: cursos y
 * prerrequisitos (arco requisito → curso). No corresponde al plan de estudios oficial de ninguna universidad.
 */
public final class MallaCurricular {

    /** Un curso. {@code fila} solo sirve para dibujarlo sin que los arcos se crucen. */
    public record Curso(String id, String nombre, String corto, int fila) {
    }

    private static final String CURSOS = "/pe/edu/unaj/aed2/semana10/cursos.csv";
    private static final String PRERREQUISITOS = "/pe/edu/unaj/aed2/semana10/prerrequisitos.csv";

    /** Un prerrequisito mal digitado (al revés) que crea un ciclo: AED II → AED I. */
    public static final String[] ARCO_ERRONEO = {"AE2", "AED"};

    private final Map<String, Curso> cursos;
    private final GrafoDirigido grafo;

    private MallaCurricular(Map<String, Curso> cursos, GrafoDirigido grafo) {
        this.cursos = cursos;
        this.grafo = grafo;
    }

    public static MallaCurricular cargar() {
        Map<String, Curso> cursos = new LinkedHashMap<>();
        GrafoDirigido grafo = new GrafoDirigido();
        for (String[] c : leer(CURSOS)) {
            cursos.put(c[0], new Curso(c[0], c[1], c[2], Integer.parseInt(c[3])));
            grafo.agregarVertice(c[0]);
        }
        for (String[] c : leer(PRERREQUISITOS)) {
            if (!cursos.containsKey(c[0]) || !cursos.containsKey(c[1])) {
                throw new IllegalStateException("Prerrequisito con un curso desconocido: " + c[0] + "-" + c[1]);
            }
            grafo.agregarArco(c[0], c[1]);
        }
        return new MallaCurricular(cursos, grafo);
    }

    private static List<String[]> leer(String recurso) {
        try (InputStream in = MallaCurricular.class.getResourceAsStream(recurso)) {
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

    public GrafoDirigido grafo() {
        return grafo;
    }

    /** La misma malla con el prerrequisito mal digitado. */
    public GrafoDirigido grafoConError() {
        return grafo.conArco(ARCO_ERRONEO[0], ARCO_ERRONEO[1]);
    }

    public Map<String, Curso> cursos() {
        return cursos;
    }

    /** id → nombre corto, para mostrar en el dibujo y en las explicaciones. */
    public Map<String, String> nombres() {
        Map<String, String> m = new LinkedHashMap<>();
        cursos.forEach((id, c) -> m.put(id, c.corto()));
        return m;
    }
}
