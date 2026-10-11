package pe.edu.unaj.aed2.semana10.caso;

import pe.edu.unaj.aed2.semana10.modelo.GrafoDirigido;
import pe.edu.unaj.aed2.semana10.modelo.MallaCurricular;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Caso práctico semana 10: un estudiante arma su plan de estudios ciclo por ciclo. En cada ciclo puede llevar
 * a lo más {@code maxPorCiclo} cursos, y solo los que ya tienen aprobados TODOS sus prerrequisitos (los que en
 * Kahn tendrían grado 0). Si hay más disponibles que vacantes, una regla de prioridad decide cuáles llevar.
 */
public class PlanEstudios {

    /** Cómo elegir entre los cursos disponibles cuando no entran todos. */
    public enum Prioridad {
        ALFABETICO("Por orden alfabético"),
        CADENA_MAS_LARGA("Primero la cadena más larga");

        private final String texto;

        Prioridad(String texto) {
            this.texto = texto;
        }

        @Override
        public String toString() {
            return texto;
        }
    }

    private final MallaCurricular malla;
    private final GrafoDirigido grafo;
    private final List<String> ordenMalla;
    private final Map<String, Integer> alturas;
    private final Map<String, Integer> niveles;

    public PlanEstudios(MallaCurricular malla) {
        this.malla = malla;
        this.grafo = malla.grafo();
        this.ordenMalla = List.copyOf(grafo.vertices());
        this.alturas = grafo.alturas();
        this.niveles = grafo.niveles();
    }

    public MallaCurricular malla() {
        return malla;
    }

    /** Cursos que dependen de v en cadena, contándolo a él: cuántos ciclos faltan como mínimo desde v. */
    public int cadena(String v) {
        return alturas.get(v);
    }

    /** Con vacantes ilimitadas: la longitud del camino crítico. Ningún plan termina antes. */
    public int minimoCiclos() {
        return grafo.caminoCritico().size();
    }

    /**
     * Holgura de un curso: cuántos ciclos se puede atrasar (con vacantes ilimitadas) sin alargar la carrera.
     * Los cursos con holgura 0 están en algún camino crítico: atrasarlos atrasa el egreso.
     */
    public int holgura(String v) {
        return minimoCiclos() - niveles.get(v) - alturas.get(v);
    }

    /** Arco crítico: une dos cursos sin holgura que van en ciclos consecutivos de una cadena crítica. */
    public boolean esArcoCritico(String u, String v) {
        return holgura(u) == 0 && holgura(v) == 0 && niveles.get(v) == niveles.get(u) + 1;
    }

    /** Lista de ciclos; cada ciclo es la lista de cursos que se llevan en él. */
    public List<List<String>> plan(int maxPorCiclo, Prioridad prioridad) {
        if (maxPorCiclo < 1) {
            throw new IllegalArgumentException("Debe poder llevarse al menos un curso por ciclo");
        }
        Collator espaniol = Collator.getInstance(Locale.forLanguageTag("es-PE"));
        Comparator<String> alfabetico = Comparator.comparing((String v) -> malla.cursos().get(v).corto(), espaniol);
        Comparator<String> criterio = prioridad == Prioridad.ALFABETICO ? alfabetico
                : Comparator.comparingInt((String v) -> -alturas.get(v)).thenComparing(alfabetico);
        Set<String> aprobados = new HashSet<>();
        List<List<String>> ciclos = new ArrayList<>();
        while (aprobados.size() < ordenMalla.size()) {
            List<String> disponibles = new ArrayList<>();
            for (String v : ordenMalla) {
                if (!aprobados.contains(v) && aprobados.containsAll(grafo.predecesores(v))) {
                    disponibles.add(v);
                }
            }
            disponibles.sort(criterio);
            List<String> ciclo = List.copyOf(disponibles.subList(0, Math.min(maxPorCiclo, disponibles.size())));
            aprobados.addAll(ciclo);
            ciclos.add(ciclo);
        }
        return ciclos;
    }
}
