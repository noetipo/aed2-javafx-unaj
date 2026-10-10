package pe.edu.unaj.aed2.semana04.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana04.modelo.EstadoArbol.NodoVista;
import pe.edu.unaj.aed2.semana04.modelo.EstadoArbol.Tipo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Árbol binario de búsqueda (ABB): para cada nodo, las claves de su subárbol izquierdo son
 * menores y las del derecho, mayores. No admite claves repetidas.
 * <p>
 * Las operaciones normales ({@link #insertar}, {@link #buscar}, {@link #eliminar}, {@link #inorden})
 * siguen exactamente el código Java de la pestaña Java; las versiones {@code pasos*} hacen lo mismo
 * registrando cada paso para animarlo.
 *
 * @param <K> tipo de las claves (Integer en el visualizador, String en el caso)
 */
public class ArbolBusqueda<K extends Comparable<K>> {

    private static final class Nodo<K> {
        final int id;
        K clave;
        Nodo<K> izq;
        Nodo<K> der;

        Nodo(int id, K clave) {
            this.id = id;
            this.clave = clave;
        }
    }

    private Nodo<K> raiz;
    private int n;
    private int siguienteId;

    // ------------------------------------------------------------------
    // Operaciones
    // ------------------------------------------------------------------

    /** true si la clave era nueva. */
    public boolean insertar(K x) {
        int antes = n;
        raiz = insertar(raiz, x);
        return n > antes;
    }

    private Nodo<K> insertar(Nodo<K> r, K x) {
        if (r == null) {
            n++;
            return new Nodo<>(siguienteId++, x);
        }
        int c = x.compareTo(r.clave);
        if (c < 0) {
            r.izq = insertar(r.izq, x);
        } else if (c > 0) {
            r.der = insertar(r.der, x);
        }
        return r;
    }

    public boolean buscar(K x) {
        return encontrar(x) != null;
    }

    /** Comparaciones de clave que hace buscar(x) (una por nodo visitado). */
    public int comparaciones(K x) {
        int k = 0;
        Nodo<K> actual = raiz;
        while (actual != null) {
            k++;
            int c = x.compareTo(actual.clave);
            if (c == 0) {
                return k;
            }
            actual = c < 0 ? actual.izq : actual.der;
        }
        return k;
    }

    private Nodo<K> encontrar(K x) {
        Nodo<K> actual = raiz;
        while (actual != null) {
            int c = x.compareTo(actual.clave);
            if (c == 0) {
                return actual;
            }
            actual = c < 0 ? actual.izq : actual.der;
        }
        return null;
    }

    /** true si la clave existía y se eliminó. */
    public boolean eliminar(K x) {
        int antes = n;
        raiz = eliminar(raiz, x);
        return n < antes;
    }

    private Nodo<K> eliminar(Nodo<K> r, K x) {
        if (r == null) {
            return null;
        }
        int c = x.compareTo(r.clave);
        if (c < 0) {
            r.izq = eliminar(r.izq, x);
        } else if (c > 0) {
            r.der = eliminar(r.der, x);
        } else {
            if (r.izq == null) {
                n--;
                return r.der;
            }
            if (r.der == null) {
                n--;
                return r.izq;
            }
            Nodo<K> sucesor = r.der;
            while (sucesor.izq != null) {
                sucesor = sucesor.izq;
            }
            r.clave = sucesor.clave;
            r.der = eliminar(r.der, sucesor.clave);
        }
        return r;
    }

    public List<K> inorden() {
        List<K> salida = new ArrayList<>();
        inorden(raiz, salida);
        return salida;
    }

    private void inorden(Nodo<K> r, List<K> salida) {
        if (r != null) {
            inorden(r.izq, salida);
            salida.add(r.clave);
            inorden(r.der, salida);
        }
    }

    public int tamanio() {
        return n;
    }

    /** La clave guardada que es igual (compareTo == 0) a x, o null. Útil si K lleva datos extra. */
    public K obtener(K x) {
        Nodo<K> nodo = encontrar(x);
        return nodo == null ? null : nodo.clave;
    }

    /** Un único paso con el estado actual del árbol (para mostrarlo sin operar). */
    public List<EstadoArbol<K>> estadoActual(String mensaje) {
        Grabador g = new Grabador("insertar", null);
        g.paso(null, null, Tipo.INICIO, 0, "", mensaje, mensaje);
        return g.pasos;
    }

    /** Altura en niveles: árbol vacío = 0, un solo nodo = 1. */
    public int altura() {
        return altura(raiz);
    }

    private int altura(Nodo<K> r) {
        return r == null ? 0 : 1 + Math.max(altura(r.izq), altura(r.der));
    }

    public NodoVista<K> vista() {
        return vista(raiz);
    }

    private NodoVista<K> vista(Nodo<K> r) {
        return r == null ? null : new NodoVista<>(r.id, r.clave, vista(r.izq), vista(r.der));
    }

    // ------------------------------------------------------------------
    // Pseudocódigo y código Java
    // ------------------------------------------------------------------

    public static final List<String> PSEUDO_INSERTAR = List.of(
            "insertar(r, x):",
            "    si r = nulo: retornar nuevo nodo con x",
            "    si x < r.clave: r.izq ← insertar(r.izq, x)",
            "    si no, si x > r.clave: r.der ← insertar(r.der, x)",
            "    retornar r          // si x = r.clave, el árbol no cambia");

    public static final List<String> PSEUDO_BUSCAR = List.of(
            "buscar(x):",
            "    actual ← raíz",
            "    mientras actual ≠ nulo:",
            "        si x = actual.clave: retornar verdadero",
            "        si x < actual.clave: actual ← actual.izq",
            "        si no: actual ← actual.der",
            "    retornar falso");

    public static final List<String> PSEUDO_ELIMINAR = List.of(
            "eliminar(r, x):",
            "    si r = nulo: retornar nulo           // x no está",
            "    si x < r.clave: r.izq ← eliminar(r.izq, x)",
            "    si no, si x > r.clave: r.der ← eliminar(r.der, x)",
            "    si no:                               // r es el nodo a eliminar",
            "        si r.izq = nulo: retornar r.der",
            "        si r.der = nulo: retornar r.izq",
            "        sucesor ← menor nodo del subárbol derecho",
            "        r.clave ← sucesor.clave",
            "        r.der ← eliminar(r.der, sucesor.clave)",
            "    retornar r");

    public static final List<String> PSEUDO_INORDEN = List.of(
            "inorden(r):",
            "    si r = nulo: retornar",
            "    inorden(r.izq)",
            "    emitir r.clave",
            "    inorden(r.der)");

    /** Clase Java completa (lista para copiar y ejecutar) con marcadores por método. */
    private static final String JAVA = """
            import java.util.ArrayList;
            import java.util.List;

            public class ArbolBusqueda {

                static class Nodo {
                    int clave;
                    Nodo izq, der;

                    Nodo(int clave) {
                        this.clave = clave;
                    }
                }

                private Nodo raiz;

                public boolean buscar(int x) {                          //@buscar:1
                    Nodo actual = raiz;                                 //@buscar:2
                    while (actual != null) {                            //@buscar:3
                        if (x == actual.clave) {                        //@buscar:4
                            return true;                                //@buscar:4
                        } else if (x < actual.clave) {                  //@buscar:5
                            actual = actual.izq;                        //@buscar:5
                        } else {
                            actual = actual.der;                        //@buscar:6
                        }
                    }
                    return false;                                       //@buscar:7
                }

                public void insertar(int x) {
                    raiz = insertar(raiz, x);
                }

                // Devuelve la raíz del subárbol r, ya con x insertado.
                private Nodo insertar(Nodo r, int x) {                  //@insertar:1
                    if (r == null) {                                    //@insertar:2
                        return new Nodo(x);                             //@insertar:2
                    }
                    if (x < r.clave) {                                  //@insertar:3
                        r.izq = insertar(r.izq, x);                     //@insertar:3
                    } else if (x > r.clave) {                           //@insertar:4
                        r.der = insertar(r.der, x);                     //@insertar:4
                    }
                    return r;                                           //@insertar:5
                }

                public void eliminar(int x) {
                    raiz = eliminar(raiz, x);
                }

                // Devuelve la raíz del subárbol r, ya sin x.
                private Nodo eliminar(Nodo r, int x) {                  //@eliminar:1
                    if (r == null) {                                    //@eliminar:2
                        return null;                                    //@eliminar:2
                    }
                    if (x < r.clave) {                                  //@eliminar:3
                        r.izq = eliminar(r.izq, x);                     //@eliminar:3
                    } else if (x > r.clave) {                           //@eliminar:4
                        r.der = eliminar(r.der, x);                     //@eliminar:4
                    } else {                                            //@eliminar:5
                        if (r.izq == null) {                            //@eliminar:6
                            return r.der;                               //@eliminar:6
                        }
                        if (r.der == null) {                            //@eliminar:7
                            return r.izq;                               //@eliminar:7
                        }
                        Nodo sucesor = r.der;                           //@eliminar:8
                        while (sucesor.izq != null) {                   //@eliminar:8
                            sucesor = sucesor.izq;                      //@eliminar:8
                        }
                        r.clave = sucesor.clave;                        //@eliminar:9
                        r.der = eliminar(r.der, sucesor.clave);         //@eliminar:10
                    }
                    return r;                                           //@eliminar:11
                }

                public List<Integer> inorden() {
                    List<Integer> salida = new ArrayList<>();
                    inorden(raiz, salida);
                    return salida;
                }

                // Va agregando las claves a "salida"; por eso no necesita devolver nada.
                private void inorden(Nodo r, List<Integer> salida) {    //@inorden:1
                    if (r == null) {                                    //@inorden:2
                        return;                                         //@inorden:2
                    }
                    inorden(r.izq, salida);                             //@inorden:3
                    salida.add(r.clave);                                //@inorden:4
                    inorden(r.der, salida);                             //@inorden:5
                }

                public static void main(String[] args) {
                    ArbolBusqueda arbol = new ArbolBusqueda();
                    int[] claves = {50, 30, 70, 20, 40, 60, 80, 35, 45, 65};
                    for (int c : claves) {
                        arbol.insertar(c);
                    }
                    System.out.println("Inorden: " + arbol.inorden());
                    System.out.println("buscar(45) = " + arbol.buscar(45));
                    System.out.println("buscar(55) = " + arbol.buscar(55));
                    arbol.eliminar(30);
                    System.out.println("Sin 30: " + arbol.inorden());
                }
            }
            """;

    public static final CodigoAlgoritmo CODIGO_INSERTAR = CodigoAlgoritmo.seccion(PSEUDO_INSERTAR, JAVA, "insertar");
    public static final CodigoAlgoritmo CODIGO_BUSCAR = CodigoAlgoritmo.seccion(PSEUDO_BUSCAR, JAVA, "buscar");
    public static final CodigoAlgoritmo CODIGO_ELIMINAR = CodigoAlgoritmo.seccion(PSEUDO_ELIMINAR, JAVA, "eliminar");
    public static final CodigoAlgoritmo CODIGO_INORDEN = CodigoAlgoritmo.seccion(PSEUDO_INORDEN, JAVA, "inorden");

    // ------------------------------------------------------------------
    // Operaciones paso a paso
    // ------------------------------------------------------------------

    /** Registro de pasos compartido por las cuatro operaciones. */
    private final class Grabador {
        final String operacion;
        final K x;
        final List<EstadoArbol<K>> pasos = new ArrayList<>();
        final Deque<Nodo<K>> pila = new ArrayDeque<>();
        final List<K> salida = new ArrayList<>();
        /** Nombre con que empezó cada llamada (no cambia aunque el nodo reciba la clave del sucesor). */
        final Map<Nodo<K>, String> etiquetas = new IdentityHashMap<>();

        Grabador(String operacion, K x) {
            this.operacion = operacion;
            this.x = x;
        }

        void paso(Nodo<K> actual, Nodo<K> destacado, Tipo tipo, int linea, String evaluacion,
                  String explicacion, String descripcion) {
            Set<Integer> ids = new LinkedHashSet<>();
            pila.forEach(p -> ids.add(p.id));
            boolean iterativo = operacion.equals("buscar");
            List<String> enOrden = new ArrayList<>();
            pila.descendingIterator().forEachRemaining(p -> enOrden.add(iterativo ? String.valueOf(p.clave) : de(p)));
            String llamadas = enOrden.isEmpty() ? "—" : String.join(iterativo ? " → " : " › ", enOrden);
            Map<String, String> variables;
            if (x == null) {
                variables = PasoTrazable.variables("r", actual == null ? "null" : actual.clave,
                        "salida", salida.toString(), "pila", llamadas);
            } else if (iterativo) {
                variables = PasoTrazable.variables("x", x, "actual", actual == null ? "null" : actual.clave,
                        "actual.clave", actual == null ? "—" : actual.clave, "n", n, "camino", llamadas);
            } else {
                variables = PasoTrazable.variables("x", x, "r", actual == null ? "null" : actual.clave,
                        "r.clave", actual == null ? "—" : actual.clave, "n", n, "pila", llamadas);
            }
            pasos.add(new EstadoArbol<>(vista(), actual == null ? -1 : actual.id, ids,
                    destacado == null ? -1 : destacado.id, tipo, salida, linea, variables, evaluacion,
                    explicacion, descripcion));
        }

        /** Nombre de la llamada que se hizo sobre el nodo r. */
        String de(Nodo<K> r) {
            return etiquetas.getOrDefault(r, llamada(operacion, r));
        }
    }

    private static String nodo(Nodo<?> r) {
        return r == null ? "null" : String.valueOf(r.clave);
    }

    /** "insertar(r=30)" o "insertar(null)". */
    private static String llamada(String op, Nodo<?> r) {
        return op + "(" + (r == null ? "null" : "r=" + r.clave) + ")";
    }

    // ---------------- insertar ----------------

    public List<EstadoArbol<K>> pasosInsertar(K x) {
        Grabador g = new Grabador("insertar", x);
        g.paso(null, null, Tipo.INICIO, 0, "raiz = insertar(raiz, " + x + ")",
                "Se llama insertar(" + x + "). El método público llama a insertar(raiz, " + x + ") y guarda en raiz"
                        + " el nodo que esa llamada devuelva."
                        + (raiz == null ? " El árbol está vacío, así que raiz es null." : ""),
                "insertar(" + x + ")");
        raiz = insertarPasos(g, raiz, x, null, false);
        g.paso(null, encontrar(x), Tipo.FIN, 0, "raiz = " + nodo(raiz),
                "La llamada inicial regresó y el método público guarda su resultado en raiz (sigue siendo "
                        + nodo(raiz) + "). El árbol tiene ahora " + n + " nodo" + (n == 1 ? "" : "s") + ".",
                "Fin de insertar(" + x + ").");
        return g.pasos;
    }

    private Nodo<K> insertarPasos(Grabador g, Nodo<K> r, K x, Nodo<K> padre, boolean izquierda) {
        String esta = llamada("insertar", r);
        if (r == null) {
            n++;
            Nodo<K> nuevo = new Nodo<>(siguienteId++, x);
            g.paso(null, null, Tipo.NULO, 2, "r == null  →  verdadero  →  return new Nodo(" + x + ")",
                    "Comienza " + esta + ". El if consulta r == null: es VERDADERO, llegamos a un lugar vacío del"
                            + " árbol. Ahí va " + x + ": se crea un nodo nuevo y se retorna. " + (padre == null
                            ? "Como no hay padre, este nodo será la raíz."
                            : "El control regresa a " + g.de(padre) + ", que lo colgará como hijo "
                                    + (izquierda ? "izquierdo" : "derecho") + " de " + padre.clave + "."),
                    "Lugar vacío: se crea " + x + ".");
            return nuevo;
        }
        g.pila.push(r);
        g.etiquetas.put(r, esta);
        int c = x.compareTo(r.clave);
        String origen = padre == null ? "Es la llamada inicial, con r = raíz = " + r.clave + "."
                : "La hizo " + g.de(padre) + " para su subárbol " + (izquierda ? "izquierdo" : "derecho") + ".";
        if (c < 0) {
            g.paso(r, null, Tipo.COMPARACION, 3,
                    "r == null → falso;  x < r.clave  →  " + x + " < " + r.clave + "  →  verdadero",
                    "Comienza " + esta + ". " + origen + " r no es null. El if consulta x < r.clave: " + x + " < "
                            + r.clave + " es VERDADERO, así que " + x + " debe ir en el subárbol IZQUIERDO de "
                            + r.clave + ". Se llama " + llamada("insertar", r.izq) + " y " + esta
                            + " queda esperando lo que devuelva.",
                    x + " < " + r.clave + ": a la izquierda.");
            Nodo<K> antes = r.izq;
            r.izq = insertarPasos(g, r.izq, x, r, true);
            g.paso(r, antes == null ? r.izq : null, Tipo.ASIGNACION, 3, "r.izq = " + nodo(r.izq),
                    asignacion(esta, r, "izquierdo", antes, r.izq, "insertar"),
                    antes == null ? x + " queda como hijo izquierdo de " + r.clave + "." : "r.izq sigue igual.");
        } else if (c > 0) {
            g.paso(r, null, Tipo.COMPARACION, 4,
                    "x < r.clave → " + x + " < " + r.clave + " → falso;  x > r.clave  →  verdadero",
                    "Comienza " + esta + ". " + origen + " r no es null. El if consulta x < r.clave: " + x + " < "
                            + r.clave + " es FALSO. Luego x > r.clave: es VERDADERO, así que " + x + " debe ir en el"
                            + " subárbol DERECHO de " + r.clave + ". Se llama " + llamada("insertar", r.der)
                            + " y " + esta + " queda esperando lo que devuelva.",
                    x + " > " + r.clave + ": a la derecha.");
            Nodo<K> antes = r.der;
            r.der = insertarPasos(g, r.der, x, r, false);
            g.paso(r, antes == null ? r.der : null, Tipo.ASIGNACION, 4, "r.der = " + nodo(r.der),
                    asignacion(esta, r, "derecho", antes, r.der, "insertar"),
                    antes == null ? x + " queda como hijo derecho de " + r.clave + "." : "r.der sigue igual.");
        } else {
            g.paso(r, r, Tipo.ENCONTRADO, 4, "x < r.clave → falso;  x > r.clave → falso  →  x == r.clave",
                    "Comienza " + esta + ". " + origen + " Ni x < r.clave ni x > r.clave: " + x + " == " + r.clave
                            + ". La clave ya está en el árbol y un ABB no guarda repetidos, así que no se baja más"
                            + " y el árbol no cambia.",
                    x + " ya existe.");
        }
        g.paso(r, null, Tipo.RETORNO, 5, "return r  →  " + r.clave,
                esta + " termina con return r: devuelve el mismo nodo " + r.clave + " (con su subárbol ya"
                        + " actualizado) " + regresoA(padre == null ? null : g.de(padre), izquierda),
                "Retorna " + r.clave + ".");
        g.pila.pop();
        return r;
    }

    // ---------------- buscar ----------------

    public List<EstadoArbol<K>> pasosBuscar(K x) {
        Grabador g = new Grabador("buscar", x);
        g.paso(raiz, null, Tipo.INICIO, 2, "actual = raiz = " + nodo(raiz),
                "Se llama buscar(" + x + "). Es iterativa: no hay llamadas recursivas, solo la variable actual que"
                        + " baja por el árbol. Empieza en la raíz, actual = " + nodo(raiz) + ".",
                "buscar(" + x + ")");
        Nodo<K> actual = raiz;
        Set<Nodo<K>> camino = new LinkedHashSet<>();
        while (actual != null) {
            camino.add(actual);
            g.pila.clear();
            camino.forEach(g.pila::push);
            int c = x.compareTo(actual.clave);
            String mientras = "El while consulta actual != null: actual = " + actual.clave + ", es VERDADERO. ";
            if (c == 0) {
                g.paso(actual, actual, Tipo.ENCONTRADO, 4, "x == actual.clave  →  " + x + " == " + actual.clave
                                + "  →  verdadero  →  return true",
                        mientras + "El if consulta x == actual.clave: " + x + " == " + actual.clave + " es VERDADERO."
                                + " ¡Encontrado! Se ejecuta return true después de " + camino.size() + " comparación"
                                + (camino.size() == 1 ? "" : "es") + ", una por nivel recorrido.",
                        "¡" + x + " encontrado!");
                return g.pasos;
            }
            if (c < 0) {
                g.paso(actual, null, Tipo.COMPARACION, 5, "x == actual.clave → falso;  x < actual.clave  →  " + x
                                + " < " + actual.clave + "  →  verdadero  →  actual = " + nodo(actual.izq),
                        mientras + "El if consulta x == actual.clave: " + x + " == " + actual.clave + " es falso."
                                + " Luego x < actual.clave: " + x + " < " + actual.clave + " es VERDADERO, así que si"
                                + " " + x + " existe, está a la IZQUIERDA. actual pasa a su hijo izquierdo, "
                                + nodo(actual.izq) + ", y el while vuelve a consultar. Todo el subárbol derecho de "
                                + actual.clave + " queda descartado sin mirarlo.",
                        x + " < " + actual.clave + ": a la izquierda.");
                actual = actual.izq;
            } else {
                g.paso(actual, null, Tipo.COMPARACION, 6, "x == actual.clave → falso;  x < actual.clave → falso"
                                + "  →  actual = " + nodo(actual.der),
                        mientras + "El if consulta x == actual.clave: " + x + " == " + actual.clave + " es falso."
                                + " Luego x < actual.clave: " + x + " < " + actual.clave + " también es falso, así"
                                + " que " + x + " es mayor: si existe, está a la DERECHA. actual pasa a su hijo"
                                + " derecho, " + nodo(actual.der) + ", y el while vuelve a consultar. Todo el subárbol"
                                + " izquierdo de " + actual.clave + " queda descartado.",
                        x + " > " + actual.clave + ": a la derecha.");
                actual = actual.der;
            }
        }
        g.paso(null, null, Tipo.NO_ENCONTRADO, 7, "actual != null  →  null != null  →  falso  →  return false",
                "El while consulta actual != null: actual es null, porque se intentó bajar a un hijo que no existe."
                        + " La condición es FALSA y se sale del while. Se ejecuta return false: " + x + " no está en"
                        + " el árbol. Si estuviera, tendría que estar justo en ese lugar vacío.",
                x + " no está.");
        return g.pasos;
    }

    // ---------------- eliminar ----------------

    public List<EstadoArbol<K>> pasosEliminar(K x) {
        Grabador g = new Grabador("eliminar", x);
        g.paso(null, null, Tipo.INICIO, 0, "raiz = eliminar(raiz, " + x + ")",
                "Se llama eliminar(" + x + "). El método público llama a eliminar(raiz, " + x + ") y guarda en raiz"
                        + " el nodo que devuelva (la raíz puede cambiar si se elimina justo la raíz).",
                "eliminar(" + x + ")");
        raiz = eliminarPasos(g, raiz, x, null, false);
        g.paso(null, null, Tipo.FIN, 0, "raiz = " + nodo(raiz),
                "La llamada inicial regresó y el método público guarda su resultado en raiz = " + nodo(raiz) + ". "
                        + "El árbol tiene ahora " + n + " nodo" + (n == 1 ? "" : "s")
                        + " y sigue siendo un ABB: su recorrido inorden sale ordenado.",
                "Fin de eliminar(" + x + ").");
        return g.pasos;
    }

    private Nodo<K> eliminarPasos(Grabador g, Nodo<K> r, K x, Nodo<K> padre, boolean izquierda) {
        String esta = llamada("eliminar", r);
        if (r == null) {
            g.paso(null, null, Tipo.NO_ENCONTRADO, 2, "r == null  →  verdadero  →  return null",
                    "Comienza " + esta + ". El if consulta r == null: es VERDADERO. Se bajó hasta un lugar vacío sin"
                            + " encontrar " + x + ", así que " + x + " no está en el árbol. Se retorna null "
                            + (padre == null ? "y el árbol (vacío) no cambia."
                            : "a " + g.de(padre) + ", que deja su hijo " + (izquierda ? "izquierdo"
                                    : "derecho") + " en null, como ya estaba: nada cambia."),
                    x + " no está.");
            return null;
        }
        g.pila.push(r);
        g.etiquetas.put(r, esta);
        int c = x.compareTo(r.clave);
        String origen = padre == null ? "Es la llamada inicial, con r = raíz = " + r.clave + "."
                : "La hizo " + g.de(padre) + " para su subárbol " + (izquierda ? "izquierdo" : "derecho") + ".";
        Nodo<K> resultado = r;
        if (c < 0 || c > 0) {
            boolean izq = c < 0;
            Nodo<K> hijo = izq ? r.izq : r.der;
            g.paso(r, null, Tipo.COMPARACION, izq ? 3 : 4,
                    izq ? "x < r.clave  →  " + x + " < " + r.clave + "  →  verdadero"
                            : "x < r.clave → falso;  x > r.clave  →  " + x + " > " + r.clave + "  →  verdadero",
                    "Comienza " + esta + ". " + origen + " r no es null. " + (izq
                            ? "El if consulta x < r.clave: " + x + " < " + r.clave + " es VERDADERO"
                            : "x < r.clave (" + x + " < " + r.clave + ") es falso y x > r.clave es VERDADERO")
                            + ": si " + x + " existe, está en el subárbol " + (izq ? "IZQUIERDO" : "DERECHO") + ". Se llama "
                            + llamada("eliminar", hijo) + " y " + esta + " espera el subárbol que devuelva, ya sin "
                            + x + ".",
                    x + (izq ? " < " : " > ") + r.clave + (izq ? ": a la izquierda." : ": a la derecha."));
            Nodo<K> nuevo = eliminarPasos(g, hijo, x, r, izq);
            if (izq) {
                r.izq = nuevo;
            } else {
                r.der = nuevo;
            }
            g.paso(r, nuevo != hijo ? nuevo : null, Tipo.ASIGNACION, izq ? 3 : 4,
                    (izq ? "r.izq = " : "r.der = ") + nodo(nuevo),
                    asignacion(esta, r, izq ? "izquierdo" : "derecho", hijo, nuevo, "eliminar"),
                    nuevo != hijo ? "Se reengancha el hijo " + (izq ? "izquierdo" : "derecho") + " de " + r.clave + "."
                            : "Sin cambios en " + r.clave + ".");
        } else {
            String tipoNodo = r.izq == null && r.der == null ? "una HOJA (no tiene hijos)"
                    : r.izq == null || r.der == null ? "un nodo con UN solo hijo" : "un nodo con DOS hijos";
            g.paso(r, r, Tipo.A_ELIMINAR, 5, "x == r.clave  →  " + x + " == " + r.clave,
                    "Comienza " + esta + ". " + origen + " Ni x < r.clave ni x > r.clave: " + x + " == " + r.clave
                            + ", así que r es el nodo a eliminar. Es " + tipoNodo + ". Según el caso, se decide qué"
                            + " nodo devolver para que tome su lugar.",
                    "Eliminar " + r.clave + ": " + tipoNodo + ".");
            if (r.izq == null) {
                n--;
                g.paso(r, r, Tipo.RETORNO, 6, "r.izq == null  →  verdadero  →  return r.der = " + nodo(r.der),
                        "El if consulta r.izq == null: es VERDADERO. " + (r.der == null
                                ? "Como r es una hoja, se retorna r.der, que es null: el lugar de " + r.clave
                                        + " queda vacío."
                                : "Se retorna r.der = " + r.der.clave + ": el hijo derecho sube a ocupar el lugar de "
                                        + r.clave + ", con todo su subárbol.")
                                + " Nadie vuelve a apuntar a " + r.clave + ", así que desaparece del árbol. Ese valor ("
                                + nodo(r.der) + ") se devuelve " + regresoA(padre == null ? null : g.de(padre), izquierda),
                        r.clave + " se reemplaza por " + nodo(r.der) + ".");
                g.pila.pop();
                return r.der;
            }
            if (r.der == null) {
                n--;
                g.paso(r, r, Tipo.RETORNO, 7, "r.izq == null → falso;  r.der == null  →  verdadero  →  return r.izq = "
                                + nodo(r.izq),
                        "r.izq no es null, pero el if consulta r.der == null: es VERDADERO. Se retorna r.izq = "
                                + r.izq.clave + ": el hijo izquierdo sube a ocupar el lugar de " + r.clave
                                + " con todo su subárbol, y " + r.clave + " desaparece. Ese nodo (" + r.izq.clave + ") se devuelve "
                                + regresoA(padre == null ? null : g.de(padre), izquierda),
                        r.clave + " se reemplaza por " + r.izq.clave + ".");
                g.pila.pop();
                return r.izq;
            }
            Nodo<K> sucesor = r.der;
            List<String> recorrido = new ArrayList<>();
            recorrido.add(String.valueOf(sucesor.clave));
            while (sucesor.izq != null) {
                sucesor = sucesor.izq;
                recorrido.add(String.valueOf(sucesor.clave));
            }
            g.paso(r, sucesor, Tipo.SUCESOR, 8, "sucesor = " + String.join(" → ", recorrido),
                    "r tiene dos hijos, así que no se puede quitar sin más: quedarían dos subárboles sueltos. La"
                            + " idea es poner en su lugar la clave que le sigue en orden: el SUCESOR, que es el menor"
                            + " del subárbol derecho. Se parte de r.der = " + r.der.clave + " y el while baja a la"
                            + " izquierda mientras se pueda: " + String.join(" → ", recorrido) + ". El sucesor es "
                            + sucesor.clave + " (no tiene hijo izquierdo).",
                    "Sucesor de " + r.clave + ": " + sucesor.clave + ".");
            K anterior = r.clave;
            r.clave = sucesor.clave;
            g.paso(r, r, Tipo.REEMPLAZO, 9, "r.clave = sucesor.clave = " + sucesor.clave,
                    "Se copia la clave del sucesor en r: el nodo que tenía " + anterior + " ahora dice "
                            + sucesor.clave + ". El orden se mantiene: todo lo de la izquierda es menor que "
                            + sucesor.clave + " y todo lo de la derecha, mayor. Pero ahora " + sucesor.clave + " aparece"
                            + " dos veces: falta quitar el original del subárbol derecho.",
                    anterior + " se reemplaza por " + sucesor.clave + ".");
            K clave = sucesor.clave;
            Nodo<K> hijo = r.der;
            g.paso(r, sucesor, Tipo.COMPARACION, 10, "r.der = eliminar(r.der = " + hijo.clave + ", " + clave + ")",
                    "Se llama " + llamada("eliminar", hijo) + " (ahora con x = " + clave + ")" + " para borrar el " + clave + " original del subárbol"
                            + " derecho. Ese nodo no tiene hijo izquierdo, así que su eliminación será un caso simple"
                            + " (hoja o un solo hijo).",
                    "Quitar el " + clave + " repetido.");
            Nodo<K> nuevo = eliminarPasosClave(g, hijo, clave, r);
            r.der = nuevo;
            g.paso(r, null, Tipo.ASIGNACION, 10, "r.der = " + nodo(nuevo),
                    "La llamada regresó y " + esta + " (cuyo nodo ahora guarda " + r.clave + ") pone en r.der lo"
                            + " que devolvió, " + nodo(nuevo) + ". El " + clave + " repetido ya no está.",
                    "Subárbol derecho de " + r.clave + " actualizado.");
        }
        boolean cambio = !r.clave.equals(x) && c == 0;
        g.paso(r, null, Tipo.RETORNO, 11, "return r  →  " + r.clave,
                esta + " termina con return r: devuelve el nodo " + (cambio ? "que ahora guarda " : "")
                        + r.clave + ", con su subárbol ya actualizado, " + regresoA(padre == null ? null : g.de(padre), izquierda),
                "Retorna " + r.clave + ".");
        g.pila.pop();
        return resultado;
    }

    /** eliminar(r.der, clave del sucesor): reutiliza los mismos pasos, con otra x en las variables. */
    private Nodo<K> eliminarPasosClave(Grabador g, Nodo<K> hijo, K clave, Nodo<K> padre) {
        Grabador sub = new Grabador("eliminar", clave);
        sub.pila.addAll(g.pila);
        sub.etiquetas.putAll(g.etiquetas);
        Nodo<K> nuevo = eliminarPasos(sub, hijo, clave, padre, false);
        g.pasos.addAll(sub.pasos);
        return nuevo;
    }

    // ---------------- inorden ----------------

    public List<EstadoArbol<K>> pasosInorden() {
        Grabador g = new Grabador("inorden", null);
        g.paso(null, null, Tipo.INICIO, 0, "inorden(raiz)",
                "Se llama inorden(raiz). Para cada nodo, el recorrido inorden visita primero todo su subárbol"
                        + " izquierdo, luego el nodo y al final su subárbol derecho. En un ABB eso entrega las claves"
                        + " ordenadas de menor a mayor.",
                "inorden");
        inordenPasos(g, raiz, null, false);
        g.paso(null, null, Tipo.FIN, 0, "salida = " + g.salida,
                "La llamada inicial terminó. La salida es " + g.salida + ": las claves quedaron ordenadas sin haber"
                        + " ordenado nada, solo por la forma del ABB.",
                "Inorden: " + g.salida);
        return g.pasos;
    }

    private void inordenPasos(Grabador g, Nodo<K> r, Nodo<K> padre, boolean izquierda) {
        if (r == null) {
            g.paso(null, null, Tipo.NULO, 2, "r == null  →  verdadero  →  return",
                    "Se llama inorden(null) y el if consulta r == null: es VERDADERO, no hay nada que recorrer, así"
                            + " que retorna de inmediato. " + (padre == null ? "El árbol estaba vacío."
                            : "El control regresa a inorden(r=" + padre.clave + "), que " + (izquierda
                                    ? "ya terminó su subárbol izquierdo: ahora emitirá " + padre.clave + "."
                                    : "ya terminó su subárbol derecho, así que también termina.")),
                    "Lugar vacío: return.");
            return;
        }
        g.pila.push(r);
        g.paso(r, null, Tipo.VISITA, 3, "r == null → falso;  inorden(r.izq = " + nodo(r.izq) + ")",
                "Comienza inorden(r=" + r.clave + "). r no es null. Antes de emitir " + r.clave + " hay que recorrer"
                        + " todo su subárbol izquierdo: se llama inorden(" + nodo(r.izq) + ") y inorden(r=" + r.clave
                        + ") queda esperando en la pila.",
                "Visita " + r.clave + ": primero su izquierda.");
        inordenPasos(g, r.izq, r, true);
        g.salida.add(r.clave);
        g.paso(r, r, Tipo.EMITE, 4, "salida.add(" + r.clave + ")  →  salida = " + g.salida,
                "El subárbol izquierdo de " + r.clave + " ya se recorrió, así que se emite " + r.clave + ": la salida"
                        + " queda " + g.salida + ". Ahora se llama inorden(" + nodo(r.der) + ") para su subárbol"
                        + " derecho.",
                "Emite " + r.clave + ".");
        inordenPasos(g, r.der, r, false);
        g.pila.pop();
    }

    // ---------------- textos comunes ----------------

    /** Explica la asignación r.izq/r.der = (lo que devolvió la llamada recursiva). */
    private static String asignacion(String esta, Nodo<?> r, String lado, Nodo<?> antes, Nodo<?> despues, String op) {
        String base = "La llamada sobre el subárbol " + lado + " regresó y " + esta + " guarda lo que devolvió en r."
                + (lado.equals("izquierdo") ? "izq" : "der") + " = " + nodo(despues) + ". ";
        if (antes == despues) {
            return base + "Es el mismo nodo que ya estaba, así que en " + r.clave + " no cambia nada: la"
                    + " asignación solo importa en el nivel donde hubo un cambio.";
        }
        if (op.equals("insertar")) {
            return base + "Antes ahí había null: ¡así es como el nodo nuevo queda colgado del árbol!";
        }
        return base + "Antes ahí estaba " + nodo(antes) + "; ahora " + (despues == null ? "queda vacío"
                : "cuelga " + despues.clave) + ": así es como el nodo eliminado sale del árbol.";
    }

    /** "y regresa a insertar(r=30), que lo guardará en su r.izq." ({@code padre} null = método público). */
    private static String regresoA(String padre, boolean izquierda) {
        if (padre == null) {
            return "y regresa al método público, que lo guarda en raiz.";
        }
        return "y regresa a " + padre + ", que lo guardará en su r." + (izquierda ? "izq." : "der.");
    }
}
