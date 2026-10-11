package pe.edu.unaj.aed2.semana13.caso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Caso práctico semana 13: programar los exámenes finales. Dos cursos que comparten estudiantes NO pueden tomarse el
 * mismo día. Es COLOREAR un grafo (cursos = vértices, conflictos = aristas, días = colores) con la menor cantidad de
 * colores. Se compara la regla voraz "el primer día libre, en el orden de la lista" con backtracking.
 * Datos ficticios.
 */
public class HorarioExamenes {

    /** Asignación de días (desde 0) por curso; null si no se encontró. */
    public record Resultado(int[] dias, int intentos) {
        public boolean encontrado() {
            return dias != null;
        }

        public int diasUsados() {
            return dias == null ? 0 : Arrays.stream(dias).max().orElse(-1) + 1;
        }
    }

    private static final String CURSOS = "/pe/edu/unaj/aed2/semana13/examenes.csv";
    private static final String CONFLICTOS = "/pe/edu/unaj/aed2/semana13/conflictos.csv";

    private final Map<String, String> cursos;
    private final boolean[][] conflicto;
    private final List<String> ids;

    public HorarioExamenes(Map<String, String> cursos, List<String[]> conflictos) {
        this.cursos = new LinkedHashMap<>(cursos);
        this.ids = List.copyOf(cursos.keySet());
        this.conflicto = new boolean[ids.size()][ids.size()];
        for (String[] c : conflictos) {
            int a = indice(c[0]);
            int b = indice(c[1]);
            conflicto[a][b] = true;
            conflicto[b][a] = true;
        }
    }

    public static HorarioExamenes cargar() {
        Map<String, String> cursos = new LinkedHashMap<>();
        leer(CURSOS).forEach(c -> cursos.put(c[0], c[1]));
        return new HorarioExamenes(cursos, leer(CONFLICTOS));
    }

    private static List<String[]> leer(String recurso) {
        try (InputStream in = HorarioExamenes.class.getResourceAsStream(recurso)) {
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

    private int indice(String id) {
        int k = ids.indexOf(id);
        if (k < 0) {
            throw new IllegalArgumentException("Curso desconocido: " + id);
        }
        return k;
    }

    public List<String> ids() {
        return ids;
    }

    public Map<String, String> nombres() {
        return cursos;
    }

    public boolean conflicto(String a, String b) {
        return conflicto[indice(a)][indice(b)];
    }

    /** Regla voraz: en el orden de la lista, cada curso al primer día sin conflictos. Rápida, pero no óptima. */
    public int[] voraz() {
        int n = ids.size();
        int[] dia = new int[n];
        Arrays.fill(dia, -1);
        for (int v = 0; v < n; v++) {
            int d = 0;
            while (!libre(dia, v, d)) {
                d++;
            }
            dia[v] = d;
        }
        return dia;
    }

    private boolean libre(int[] dia, int v, int d) {
        for (int u = 0; u < dia.length; u++) {
            if (conflicto[v][u] && dia[u] == d) {
                return false;
            }
        }
        return true;
    }

    /** Backtracking: busca una asignación con a lo más k días. */
    public Resultado backtracking(int k) {
        int[] dia = new int[ids.size()];
        Arrays.fill(dia, -1);
        int[] intentos = {0};
        boolean ok = asignar(dia, 0, k, intentos);
        return new Resultado(ok ? dia : null, intentos[0]);
    }

    private boolean asignar(int[] dia, int v, int k, int[] intentos) {
        if (v == dia.length) {
            return true;
        }
        for (int d = 0; d < k; d++) {
            intentos[0]++;
            if (libre(dia, v, d)) {
                dia[v] = d;
                if (asignar(dia, v + 1, k, intentos)) {
                    return true;
                }
                dia[v] = -1;
            }
        }
        return false;
    }

    /** La menor cantidad de días posible (el número cromático del grafo de conflictos). */
    public int minimoDias() {
        for (int k = 1; ; k++) {
            if (backtracking(k).encontrado()) {
                return k;
            }
        }
    }
}
