package pe.edu.unaj.aed2.semana05.caso;

import pe.edu.unaj.aed2.semana04.caso.DiccionarioQuechua;
import pe.edu.unaj.aed2.semana04.caso.DiccionarioQuechua.Entrada;
import pe.edu.unaj.aed2.semana04.modelo.ArbolBusqueda;
import pe.edu.unaj.aed2.semana05.modelo.ArbolAVL;

import java.util.List;
import java.util.Map;

/**
 * Caso práctico semana 05: el diccionario quechua–español de la semana 04, insertado en orden
 * alfabético en un ABB común (que degenera) y en un AVL (que se mantiene equilibrado rotando).
 */
public class DiccionarioAVL {

    private final List<Entrada> entradas;
    private final ArbolBusqueda<Entrada> abb = new ArbolBusqueda<>();
    private final ArbolAVL<Entrada> avl = new ArbolAVL<>();

    public DiccionarioAVL(List<Entrada> alfabetico) {
        this.entradas = List.copyOf(alfabetico);
        for (Entrada e : entradas) {
            abb.insertar(e);
            avl.insertar(e);
        }
    }

    public static DiccionarioAVL desdeRecurso() {
        return new DiccionarioAVL(DiccionarioQuechua.desdeRecurso().entradas());
    }

    public int tamanio() {
        return entradas.size();
    }

    public ArbolBusqueda<Entrada> abb() {
        return abb;
    }

    public ArbolAVL<Entrada> avl() {
        return avl;
    }

    public Map<String, Integer> rotaciones() {
        return avl.rotaciones();
    }

    public String traducir(String palabra) {
        Entrada e = avl.obtener(new Entrada(palabra, ""));
        return e == null ? null : e.significado();
    }

    public int comparacionesABB(String palabra) {
        return abb.comparaciones(new Entrada(palabra, ""));
    }

    public int comparacionesAVL(String palabra) {
        return avl.comparaciones(new Entrada(palabra, ""));
    }

    /** Promedio de comparaciones para encontrar cada palabra: {ABB, AVL}. */
    public double[] promedios() {
        double a = 0;
        double v = 0;
        for (Entrada e : entradas) {
            a += abb.comparaciones(e);
            v += avl.comparaciones(e);
        }
        return new double[]{a / entradas.size(), v / entradas.size()};
    }

    /** Peor caso de búsqueda en un AVL: altura ≤ 1,44 · log₂(n + 2). */
    public double cotaAVL() {
        return 1.44 * Math.log(entradas.size() + 2) / Math.log(2);
    }
}
