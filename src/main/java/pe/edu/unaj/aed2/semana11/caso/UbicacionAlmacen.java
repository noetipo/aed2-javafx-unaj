package pe.edu.unaj.aed2.semana11.caso;

import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana08.modelo.GrafoPonderado;
import pe.edu.unaj.aed2.semana11.modelo.FloydWarshall;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Caso práctico semana 11: ¿en qué pueblo conviene poner el almacén de ayuda para las heladas (abrigo, alimentos,
 * medicinas)? Con la tabla de distancias de Floyd–Warshall se evalúan TODAS las ubicaciones posibles, con uno o dos
 * almacenes, según dos criterios distintos.
 */
public class UbicacionAlmacen {

    /** Qué se quiere minimizar. */
    public enum Criterio {
        MAS_LEJANO("Que el pueblo más lejano quede lo más cerca posible"),
        TOTAL("Que el recorrido total sea el menor");

        private final String texto;

        Criterio(String texto) {
            this.texto = texto;
        }

        @Override
        public String toString() {
            return texto;
        }
    }

    /**
     * Una ubicación posible.
     *
     * @param almacenes los pueblos con almacén
     * @param peor      km desde su almacén más cercano al pueblo peor atendido
     * @param total     suma, para todos los pueblos, de los km a su almacén más cercano
     */
    public record Propuesta(List<String> almacenes, int peor, int total) {
    }

    private final RedVial red;
    private final GrafoPonderado grafo;
    private final FloydWarshall.Resultado tabla;

    public UbicacionAlmacen(RedVial red) {
        this.red = red;
        this.grafo = GrafoPonderado.desde(red);
        this.tabla = FloydWarshall.calcular(grafo);
    }

    public RedVial red() {
        return red;
    }

    public GrafoPonderado grafo() {
        return grafo;
    }

    public FloydWarshall.Resultado tabla() {
        return tabla;
    }

    public Propuesta evaluar(List<String> almacenes) {
        int peor = 0;
        int total = 0;
        for (String v : tabla.vertices()) {
            int d = tabla.distancia(almacenes.get(cercano(almacenes, v)), v);
            peor = Math.max(peor, d);
            total += d;
        }
        return new Propuesta(List.copyOf(almacenes), peor, total);
    }

    /** Índice (en {@code almacenes}) del almacén más cercano a v. */
    private int cercano(List<String> almacenes, String v) {
        int mejor = 0;
        for (int a = 1; a < almacenes.size(); a++) {
            if (tabla.distancia(almacenes.get(a), v) < tabla.distancia(almacenes.get(mejor), v)) {
                mejor = a;
            }
        }
        return mejor;
    }

    /** Pueblo → el almacén que lo atiende (el más cercano). */
    public Map<String, String> asignacion(List<String> almacenes) {
        Map<String, String> m = new LinkedHashMap<>();
        for (String v : tabla.vertices()) {
            m.put(v, almacenes.get(cercano(almacenes, v)));
        }
        return m;
    }

    /** Todas las ubicaciones con {@code cantidad} almacenes (1 o 2), de mejor a peor según el criterio. */
    public List<Propuesta> ranking(int cantidad, Criterio criterio) {
        if (cantidad < 1 || cantidad > 2) {
            throw new IllegalArgumentException("Se evalúan 1 o 2 almacenes");
        }
        List<String> vs = tabla.vertices();
        List<Propuesta> todas = new ArrayList<>();
        for (int a = 0; a < vs.size(); a++) {
            if (cantidad == 1) {
                todas.add(evaluar(List.of(vs.get(a))));
            } else {
                for (int b = a + 1; b < vs.size(); b++) {
                    todas.add(evaluar(List.of(vs.get(a), vs.get(b))));
                }
            }
        }
        Comparator<Propuesta> porPeor = Comparator.comparingInt(Propuesta::peor);
        Comparator<Propuesta> porTotal = Comparator.comparingInt(Propuesta::total);
        todas.sort(criterio == Criterio.MAS_LEJANO ? porPeor.thenComparing(porTotal) : porTotal.thenComparing(porPeor));
        return todas;
    }
}
