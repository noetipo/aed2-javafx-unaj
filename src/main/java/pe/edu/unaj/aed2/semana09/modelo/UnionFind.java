package pe.edu.unaj.aed2.semana09.modelo;

import java.util.HashMap;
import java.util.Map;

/**
 * Conjuntos disjuntos (union-find): cada elemento apunta a un "jefe" y el jefe de un grupo se apunta
 * a sí mismo. {@code encontrar} sube hasta el jefe y, de paso, hace que todos apunten directo a él
 * (compresión de caminos). Un elemento que nunca se usó forma un grupo solo.
 * <p>
 * Es la misma versión de la pestaña Java (sin unión por rango, para que sea corta); la teoría explica
 * cómo el rango la vuelve casi O(1).
 */
public class UnionFind<T> {

    private final Map<T, T> jefe = new HashMap<>();

    /** El representante del grupo de x. */
    public T encontrar(T x) {
        jefe.putIfAbsent(x, x);
        if (!jefe.get(x).equals(x)) {
            jefe.put(x, encontrar(jefe.get(x)));
        }
        return jefe.get(x);
    }

    /** Junta los grupos de a y b; false si ya estaban juntos (unirlos cerraría un ciclo). */
    public boolean unir(T a, T b) {
        T ra = encontrar(a);
        T rb = encontrar(b);
        if (ra.equals(rb)) {
            return false;
        }
        jefe.put(ra, rb);
        return true;
    }

    public boolean mismoGrupo(T a, T b) {
        return encontrar(a).equals(encontrar(b));
    }

    /** El jefe directo de x (sin comprimir), para mostrar la estructura tal como está. */
    public T jefeDe(T x) {
        return jefe.getOrDefault(x, x);
    }
}
