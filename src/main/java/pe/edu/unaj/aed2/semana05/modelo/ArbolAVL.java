package pe.edu.unaj.aed2.semana05.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana05.modelo.EstadoAVL.NodoAVL;
import pe.edu.unaj.aed2.semana05.modelo.EstadoAVL.Tipo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Árbol AVL: un ABB que, después de cada inserción, revisa de abajo hacia arriba el factor de
 * equilibrio fe = altura(izq) − altura(der) y, si |fe| llega a 2, lo corrige con rotaciones.
 * Así la altura se mantiene en O(log n) aunque las claves lleguen ordenadas.
 * <p>
 * {@link #insertar} sigue exactamente el código de la pestaña Java; {@link #pasosInsertar} hace lo
 * mismo registrando cada paso.
 *
 * @param <K> tipo de las claves
 */
public class ArbolAVL<K extends Comparable<K>> {

    private static final class Nodo<K> {
        final int id;
        final K clave;
        int altura = 1;
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
    private final Map<String, Integer> rotaciones = new LinkedHashMap<>(Map.of("LL", 0, "RR", 0, "LR", 0, "RL", 0));

    // ------------------------------------------------------------------
    // Operaciones
    // ------------------------------------------------------------------

    private static int altura(Nodo<?> r) {
        return r == null ? 0 : r.altura;
    }

    private static void actualizar(Nodo<?> r) {
        r.altura = 1 + Math.max(altura(r.izq), altura(r.der));
    }

    private static <K> Nodo<K> rotarDerecha(Nodo<K> y) {
        Nodo<K> x = y.izq;
        y.izq = x.der;
        x.der = y;
        actualizar(y);
        actualizar(x);
        return x;
    }

    private static <K> Nodo<K> rotarIzquierda(Nodo<K> x) {
        Nodo<K> y = x.der;
        x.der = y.izq;
        y.izq = x;
        actualizar(x);
        actualizar(y);
        return y;
    }

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
        } else {
            return r;
        }
        actualizar(r);
        int fe = altura(r.izq) - altura(r.der);
        if (fe > 1 && x.compareTo(r.izq.clave) < 0) {
            contar("LL");
            return rotarDerecha(r);
        }
        if (fe < -1 && x.compareTo(r.der.clave) > 0) {
            contar("RR");
            return rotarIzquierda(r);
        }
        if (fe > 1 && x.compareTo(r.izq.clave) > 0) {
            contar("LR");
            r.izq = rotarIzquierda(r.izq);
            return rotarDerecha(r);
        }
        if (fe < -1 && x.compareTo(r.der.clave) < 0) {
            contar("RL");
            r.der = rotarDerecha(r.der);
            return rotarIzquierda(r);
        }
        return r;
    }

    private void contar(String caso) {
        rotaciones.merge(caso, 1, Integer::sum);
    }

    public boolean buscar(K x) {
        return obtener(x) != null;
    }

    /** La clave guardada igual (compareTo == 0) a x, o null. */
    public K obtener(K x) {
        Nodo<K> actual = raiz;
        while (actual != null) {
            int c = x.compareTo(actual.clave);
            if (c == 0) {
                return actual.clave;
            }
            actual = c < 0 ? actual.izq : actual.der;
        }
        return null;
    }

    /** Comparaciones que hace una búsqueda (una por nodo visitado). */
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

    /** Altura en niveles (vacío = 0). */
    public int altura() {
        return altura(raiz);
    }

    /** Rotaciones hechas hasta ahora por caso: LL, RR, LR, RL. */
    public Map<String, Integer> rotaciones() {
        return Map.copyOf(rotaciones);
    }

    public NodoAVL<K> vista() {
        return vista(raiz, null, null, new boolean[]{false});
    }

    /**
     * Copia para dibujar. Si encuentra {@code viejo} por primera vez, dibuja {@code nuevo} en su lugar:
     * así se ve el resultado de una rotación aunque el padre todavía no haya reenganchado el subárbol.
     * Solo se reemplaza una vez, porque después de rotar {@code viejo} queda dentro de {@code nuevo}.
     */
    private static <K> NodoAVL<K> vista(Nodo<K> r, Nodo<K> viejo, Nodo<K> nuevo, boolean[] usado) {
        if (r != null && r == viejo && !usado[0]) {
            usado[0] = true;
            r = nuevo;
        }
        return r == null ? null : new NodoAVL<>(r.id, r.clave, r.altura,
                vista(r.izq, viejo, nuevo, usado), vista(r.der, viejo, nuevo, usado));
    }

    // ------------------------------------------------------------------
    // Pseudocódigo y código Java
    // ------------------------------------------------------------------

    public static final List<String> PSEUDO_INSERTAR = List.of(
            "insertar(r, x):",
            "    si r = nulo: retornar nuevo nodo con x (altura 1)",
            "    si x < r.clave: r.izq ← insertar(r.izq, x)",
            "    si no, si x > r.clave: r.der ← insertar(r.der, x)",
            "    si no: retornar r                  // repetido",
            "    r.altura ← 1 + max(altura(r.izq), altura(r.der))",
            "    fe ← altura(r.izq) − altura(r.der)",
            "    si fe > 1 y x < r.izq.clave: retornar rotarDerecha(r)              // LL",
            "    si fe < −1 y x > r.der.clave: retornar rotarIzquierda(r)           // RR",
            "    si fe > 1 y x > r.izq.clave: r.izq ← rotarIzquierda(r.izq); retornar rotarDerecha(r)   // LR",
            "    si fe < −1 y x < r.der.clave: r.der ← rotarDerecha(r.der); retornar rotarIzquierda(r)  // RL",
            "    retornar r");

    /** Clase Java completa, lista para copiar y ejecutar. */
    private static final String JAVA = """
            import java.util.ArrayList;
            import java.util.List;

            public class ArbolAVL {

                static class Nodo {
                    int clave;
                    int altura = 1;
                    Nodo izq, der;

                    Nodo(int clave) {
                        this.clave = clave;
                    }
                }

                private Nodo raiz;

                private static int altura(Nodo n) {
                    return n == null ? 0 : n.altura;
                }

                private static void actualizar(Nodo n) {
                    n.altura = 1 + Math.max(altura(n.izq), altura(n.der));
                }

                // El hijo izquierdo x sube; y baja a la derecha de x.
                private static Nodo rotarDerecha(Nodo y) {
                    Nodo x = y.izq;
                    y.izq = x.der;
                    x.der = y;
                    actualizar(y);
                    actualizar(x);
                    return x;
                }

                // El hijo derecho y sube; x baja a la izquierda de y.
                private static Nodo rotarIzquierda(Nodo x) {
                    Nodo y = x.der;
                    x.der = y.izq;
                    y.izq = x;
                    actualizar(x);
                    actualizar(y);
                    return y;
                }

                public void insertar(int x) {
                    raiz = insertar(raiz, x);
                }

                // Devuelve la raíz del subárbol r, ya con x y equilibrado.
                private Nodo insertar(Nodo r, int x) {                 //@insertar:1
                    if (r == null) {                                   //@insertar:2
                        return new Nodo(x);                            //@insertar:2
                    }
                    if (x < r.clave) {                                 //@insertar:3
                        r.izq = insertar(r.izq, x);                    //@insertar:3
                    } else if (x > r.clave) {                          //@insertar:4
                        r.der = insertar(r.der, x);                    //@insertar:4
                    } else {
                        return r;                                      //@insertar:5
                    }
                    actualizar(r);                                     //@insertar:6
                    int fe = altura(r.izq) - altura(r.der);            //@insertar:7
                    if (fe > 1 && x < r.izq.clave) {                   //@insertar:8
                        return rotarDerecha(r);                        //@insertar:8
                    }
                    if (fe < -1 && x > r.der.clave) {                  //@insertar:9
                        return rotarIzquierda(r);                      //@insertar:9
                    }
                    if (fe > 1 && x > r.izq.clave) {                   //@insertar:10
                        r.izq = rotarIzquierda(r.izq);                 //@insertar:10
                        return rotarDerecha(r);                        //@insertar:10
                    }
                    if (fe < -1 && x < r.der.clave) {                  //@insertar:11
                        r.der = rotarDerecha(r.der);                   //@insertar:11
                        return rotarIzquierda(r);                      //@insertar:11
                    }
                    return r;                                          //@insertar:12
                }

                public int altura() {
                    return altura(raiz);
                }

                public List<Integer> inorden() {
                    List<Integer> salida = new ArrayList<>();
                    inorden(raiz, salida);
                    return salida;
                }

                private void inorden(Nodo r, List<Integer> salida) {
                    if (r != null) {
                        inorden(r.izq, salida);
                        salida.add(r.clave);
                        inorden(r.der, salida);
                    }
                }

                public static void main(String[] args) {
                    ArbolAVL arbol = new ArbolAVL();
                    for (int x = 1; x <= 15; x++) {
                        arbol.insertar(x);   // claves ordenadas: un ABB común quedaría de altura 15
                    }
                    System.out.println("Inorden: " + arbol.inorden());
                    System.out.println("Altura: " + arbol.altura());
                    System.out.println("Raíz: " + arbol.raiz.clave);
                }
            }
            """;

    public static final CodigoAlgoritmo CODIGO_INSERTAR = CodigoAlgoritmo.seccion(PSEUDO_INSERTAR, JAVA, "insertar");

    // ------------------------------------------------------------------
    // Inserción paso a paso
    // ------------------------------------------------------------------

    private final class Grabador {
        final K x;
        final List<EstadoAVL<K>> pasos = new ArrayList<>();
        final Deque<Nodo<K>> pila = new ArrayDeque<>();
        final Map<Nodo<K>, String> etiquetas = new IdentityHashMap<>();
        /** Subárbol que acaba de rotar y que su padre todavía no reengancha. */
        Nodo<K> pendienteViejo;
        Nodo<K> pendienteNuevo;

        Grabador(K x) {
            this.x = x;
        }

        String de(Nodo<K> r) {
            return etiquetas.getOrDefault(r, "insertar(r=" + r.clave + ")");
        }

        void paso(Nodo<K> actual, Set<Nodo<K>> destacados, Tipo tipo, int linea, String evaluacion,
                  String explicacion, String descripcion) {
            Set<Integer> ids = new LinkedHashSet<>();
            pila.forEach(p -> ids.add(p.id));
            Set<Integer> marcados = new LinkedHashSet<>();
            destacados.forEach(d -> marcados.add(d.id));
            List<String> llamadas = new ArrayList<>();
            pila.descendingIterator().forEachRemaining(p -> llamadas.add(de(p)));
            Map<String, String> variables = PasoTrazable.variables(
                    "x", x,
                    "r", actual == null ? "null" : actual.clave,
                    "altura(r.izq)", actual == null ? "—" : altura(actual.izq),
                    "altura(r.der)", actual == null ? "—" : altura(actual.der),
                    "fe", actual == null ? "—" : altura(actual.izq) - altura(actual.der),
                    "pila", llamadas.isEmpty() ? "—" : String.join(" › ", llamadas));
            pasos.add(new EstadoAVL<>(vista(raiz, pendienteViejo, pendienteNuevo, new boolean[]{false}),
                    actual == null ? -1 : actual.id,
                    ids, marcados, tipo, linea, variables, evaluacion, explicacion, descripcion));
        }
    }

    /** Un único paso con el estado actual del árbol (para mostrarlo sin operar). */
    public List<EstadoAVL<K>> estadoActual(String mensaje) {
        Grabador g = new Grabador(null);
        g.paso(null, Set.of(), Tipo.INICIO, 0, "", mensaje, mensaje);
        return g.pasos;
    }

    public List<EstadoAVL<K>> pasosInsertar(K x) {
        Grabador g = new Grabador(x);
        g.paso(null, Set.of(), Tipo.INICIO, 0, "raiz = insertar(raiz, " + x + ")",
                "Se llama insertar(" + x + "). Primero se baja como en un ABB común hasta un lugar vacío. Lo nuevo"
                        + " es la VUELTA: cada llamada, al recibir su subárbol, recalcula su altura y su factor de"
                        + " equilibrio fe = altura(izq) − altura(der), y si |fe| llega a 2 rota para corregirlo.",
                "insertar(" + x + ")");
        raiz = insertarPasos(g, raiz, x, null, false);
        g.pendienteViejo = null;
        g.pendienteNuevo = null;
        Nodo<K> nuevo = buscarNodo(x);
        g.paso(null, nuevo == null ? Set.of() : Set.of(nuevo), Tipo.FIN, 0, "raiz = " + clave(raiz),
                "La llamada inicial regresó y el método público guarda su resultado en raiz = " + clave(raiz) + ". El"
                        + " árbol tiene " + n + " nodo" + (n == 1 ? "" : "s") + " y altura " + altura(raiz)
                        + "; todos sus nodos cumplen |fe| ≤ 1.",
                "Fin de insertar(" + x + ").");
        return g.pasos;
    }

    private Nodo<K> buscarNodo(K x) {
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

    private Nodo<K> insertarPasos(Grabador g, Nodo<K> r, K x, Nodo<K> padre, boolean izquierda) {
        if (r == null) {
            n++;
            Nodo<K> nuevo = new Nodo<>(siguienteId++, x);
            g.paso(null, Set.of(), Tipo.NUEVO, 2, "r == null  →  verdadero  →  return new Nodo(" + x + ")",
                    "Se llama insertar(null): el if consulta r == null y es VERDADERO, se llegó a un lugar vacío. Se"
                            + " crea el nodo " + x + " con altura 1 (es una hoja) y se retorna. " + (padre == null
                            ? "Como no hay padre, será la raíz."
                            : "El control regresa a " + g.de(padre) + ", que lo colgará como hijo "
                                    + (izquierda ? "izquierdo." : "derecho.")),
                    "Se crea " + x + ".");
            return nuevo;
        }
        String esta = "insertar(r=" + r.clave + ")";
        g.pila.push(r);
        g.etiquetas.put(r, esta);
        String origen = padre == null ? "Es la llamada inicial, con r = raíz = " + r.clave + "."
                : "La hizo " + g.de(padre) + " para su subárbol " + (izquierda ? "izquierdo." : "derecho.");
        int c = x.compareTo(r.clave);
        if (c == 0) {
            g.paso(r, Set.of(r), Tipo.REPETIDO, 5, "x == r.clave  →  " + x + " == " + r.clave + "  →  return r",
                    "Comienza " + esta + ". " + origen + " " + x + " ya está en el árbol: un AVL no guarda repetidos,"
                            + " así que se retorna r sin cambios " + regresoA(g, padre, izquierda),
                    x + " ya existe.");
            g.pila.pop();
            return r;
        }
        boolean izq = c < 0;
        Nodo<K> hijo = izq ? r.izq : r.der;
        g.paso(r, Set.of(), Tipo.COMPARACION, izq ? 3 : 4,
                izq ? "x < r.clave  →  " + x + " < " + r.clave + "  →  verdadero"
                        : "x < r.clave → falso;  x > r.clave  →  " + x + " > " + r.clave + "  →  verdadero",
                "Comienza " + esta + ". " + origen + " Como " + x + (izq ? " < " : " > ") + r.clave + ", " + x
                        + " va en el subárbol " + (izq ? "IZQUIERDO" : "DERECHO") + ". Se llama insertar("
                        + clave(hijo) + ") y " + esta + " queda esperando el subárbol que devuelva.",
                x + (izq ? " < " : " > ") + r.clave + (izq ? ": a la izquierda." : ": a la derecha."));
        Nodo<K> devuelto = insertarPasos(g, hijo, x, r, izq);
        if (izq) {
            r.izq = devuelto;
        } else {
            r.der = devuelto;
        }
        g.pendienteViejo = null;
        g.pendienteNuevo = null;

        int alturaAntes = r.altura;
        actualizar(r);
        String lado = izq ? "izq" : "der";
        String recibido = hijo == null ? "el nodo nuevo " + devuelto.clave
                : devuelto == hijo ? "el mismo nodo " + devuelto.clave + " que ya tenía"
                : devuelto.clave + ", la nueva raíz de ese subárbol después de una rotación";
        g.paso(r, hijo == null || devuelto != hijo ? Set.of(devuelto) : Set.of(), Tipo.ACTUALIZA, 6,
                "r." + lado + " = " + devuelto.clave + ";  r.altura = 1 + max(" + altura(r.izq) + ", " + altura(r.der)
                        + ") = " + r.altura,
                "La llamada regresó y " + esta + " guarda en r." + lado + " " + recibido + ". Luego actualizar(r)"
                        + " recalcula su altura: 1 + max(altura(r.izq) = " + altura(r.izq) + ", altura(r.der) = "
                        + altura(r.der) + ") = " + r.altura + (alturaAntes == r.altura
                        ? " (no cambió)." : " (antes era " + alturaAntes + ")."),
                "Altura de " + r.clave + " = " + r.altura + ".");

        int hi = altura(r.izq);
        int hd = altura(r.der);
        int fe = hi - hd;
        String calculo = "fe = altura(r.izq) − altura(r.der) = " + hi + " − " + hd + " = " + signo(fe);
        Nodo<K> resultado;
        if (Math.abs(fe) <= 1) {
            g.paso(r, Set.of(), Tipo.EQUILIBRADO, 12, calculo + "  →  |fe| ≤ 1  →  return r",
                    "Se calcula el factor de equilibrio de " + r.clave + ": " + calculo + ". Como |fe| ≤ 1, " + r.clave
                            + " está EQUILIBRADO: los cuatro if de rotación son falsos y se ejecuta return r. "
                            + esta + " devuelve " + r.clave + " " + regresoA(g, padre, izquierda),
                    r.clave + " equilibrado (fe = " + signo(fe) + ").");
            resultado = r;
        } else {
            resultado = rotar(g, r, x, fe, calculo, esta, padre, izquierda);
        }
        g.pila.pop();
        return resultado;
    }

    /** Detecta el caso (LL, RR, LR, RL), registra el desequilibrio y aplica las rotaciones. */
    private Nodo<K> rotar(Grabador g, Nodo<K> r, K x, int fe, String calculo, String esta,
                          Nodo<K> padre, boolean izquierda) {
        boolean pesaIzquierda = fe > 1;
        Nodo<K> hijo = pesaIzquierda ? r.izq : r.der;
        boolean menorQueHijo = x.compareTo(hijo.clave) < 0;
        String caso = pesaIzquierda ? (menorQueHijo ? "LL" : "LR") : (menorQueHijo ? "RL" : "RR");
        int linea = switch (caso) {
            case "LL" -> 8;
            case "RR" -> 9;
            case "LR" -> 10;
            default -> 11;
        };
        String nombre = switch (caso) {
            case "LL" -> "IZQUIERDA-IZQUIERDA (LL)";
            case "RR" -> "DERECHA-DERECHA (RR)";
            case "LR" -> "IZQUIERDA-DERECHA (LR)";
            default -> "DERECHA-IZQUIERDA (RL)";
        };
        String remedio = switch (caso) {
            case "LL" -> "Se arregla con UNA rotación a la derecha sobre " + r.clave + ".";
            case "RR" -> "Se arregla con UNA rotación a la izquierda sobre " + r.clave + ".";
            case "LR" -> "Una sola rotación no basta (el nodo del medio quedaría mal): primero se rota a la izquierda"
                    + " el hijo " + hijo.clave + ", lo que convierte el caso en LL, y luego a la derecha sobre "
                    + r.clave + ".";
            default -> "Una sola rotación no basta: primero se rota a la derecha el hijo " + hijo.clave + ", lo que"
                    + " convierte el caso en RR, y luego a la izquierda sobre " + r.clave + ".";
        };
        String condicion = (pesaIzquierda ? "fe > 1" : "fe < −1") + " && x " + (menorQueHijo ? "<" : ">")
                + " r." + (pesaIzquierda ? "izq" : "der") + ".clave  →  " + signo(fe) + (pesaIzquierda ? " > 1" : " < −1")
                + " && " + x + (menorQueHijo ? " < " : " > ") + hijo.clave + "  →  verdadero";
        g.paso(r, Set.of(r), Tipo.DESEQUILIBRIO, linea, condicion,
                "Se calcula el factor de equilibrio de " + r.clave + ": " + calculo + ". ¡|fe| = 2! El subárbol "
                        + (pesaIzquierda ? "izquierdo" : "derecho") + " es dos niveles más alto que el otro: " + r.clave
                        + " está DESEQUILIBRADO. Para saber qué rotación usar se mira por dónde entró x: " + x
                        + (pesaIzquierda ? " está a la izquierda de " : " está a la derecha de ") + r.clave + " y "
                        + (menorQueHijo ? "a la izquierda" : "a la derecha") + " de su hijo " + hijo.clave + ". Es el caso "
                        + nombre + ". " + remedio,
                r.clave + " desequilibrado: caso " + caso + ".");
        contar(caso);

        Nodo<K> nueva;
        switch (caso) {
            case "LL" -> nueva = rotacionDerecha(g, r, linea, "", esta, padre, izquierda, null);
            case "RR" -> nueva = rotacionIzquierda(g, r, linea, "", esta, padre, izquierda, null);
            case "LR" -> {
                r.izq = rotacionIzquierda(g, r.izq, linea, "Primera rotación: ", esta, padre, izquierda, "r.izq");
                g.pendienteViejo = null;
                g.pendienteNuevo = null;
                nueva = rotacionDerecha(g, r, linea, "Segunda rotación: ", esta, padre, izquierda, null);
            }
            default -> {
                r.der = rotacionDerecha(g, r.der, linea, "Primera rotación: ", esta, padre, izquierda, "r.der");
                g.pendienteViejo = null;
                g.pendienteNuevo = null;
                nueva = rotacionIzquierda(g, r, linea, "Segunda rotación: ", esta, padre, izquierda, null);
            }
        }
        return nueva;
    }

    /** @param guardaEn dónde se guarda el resultado si es la primera de dos rotaciones; null si es la última */
    private Nodo<K> rotacionDerecha(Grabador g, Nodo<K> y, int linea, String prefijo, String esta,
                                    Nodo<K> padre, boolean izquierda, String guardaEn) {
        Nodo<K> x = y.izq;
        Nodo<K> t2 = x.der;
        Nodo<K> nueva = rotarDerecha(y);
        registrarRotacion(g, y, nueva, linea, prefijo + "rotarDerecha(" + y.clave + ")",
                "su hijo izquierdo " + x.clave + " sube a ocupar su lugar y " + y.clave + " baja como hijo DERECHO de "
                        + x.clave + ". " + (t2 == null ? "El hijo derecho de " + x.clave + " estaba vacío, así que "
                        + y.clave + " queda sin hijo izquierdo."
                        : "El antiguo hijo derecho de " + x.clave + " (" + t2.clave + ") pasa a ser hijo izquierdo de "
                        + y.clave + ": sigue estando entre ambos en orden."),
                esta, padre, izquierda, guardaEn);
        return nueva;
    }

    private Nodo<K> rotacionIzquierda(Grabador g, Nodo<K> x, int linea, String prefijo, String esta,
                                      Nodo<K> padre, boolean izquierda, String guardaEn) {
        Nodo<K> y = x.der;
        Nodo<K> t2 = y.izq;
        Nodo<K> nueva = rotarIzquierda(x);
        registrarRotacion(g, x, nueva, linea, prefijo + "rotarIzquierda(" + x.clave + ")",
                "su hijo derecho " + y.clave + " sube a ocupar su lugar y " + x.clave + " baja como hijo IZQUIERDO de "
                        + y.clave + ". " + (t2 == null ? "El hijo izquierdo de " + y.clave + " estaba vacío, así que "
                        + x.clave + " queda sin hijo derecho."
                        : "El antiguo hijo izquierdo de " + y.clave + " (" + t2.clave + ") pasa a ser hijo derecho de "
                        + x.clave + ": sigue estando entre ambos en orden."),
                esta, padre, izquierda, guardaEn);
        return nueva;
    }

    private void registrarRotacion(Grabador g, Nodo<K> viejo, Nodo<K> nueva, int linea, String llamada,
                                   String detalle, String esta, Nodo<K> padre, boolean izquierda, String guardaEn) {
        Nodo<K> bajo = nueva.izq == viejo ? nueva.izq : nueva.der;
        boolean ultima = guardaEn == null;
        g.pendienteViejo = viejo;
        g.pendienteNuevo = nueva;
        g.paso(viejo, Set.of(nueva, bajo), Tipo.ROTACION, linea, llamada + "  →  devuelve " + nueva.clave,
                llamada + ": " + detalle + " Se recalculan las alturas: " + bajo.clave + " → " + bajo.altura + ", "
                        + nueva.clave + " → " + nueva.altura + ". El orden del ABB se conserva (el inorden no cambia). "
                        + (ultima ? "El subárbol vuelve a estar equilibrado. "
                        : "Todavía no está equilibrado, pero ahora los tres nodos quedaron en línea recta: es un caso"
                                + " simple que la segunda rotación termina de arreglar. ") + (ultima
                        ? esta + " retorna " + nueva.clave + ", la nueva raíz de este subárbol, "
                                + regresoA(g, padre, izquierda)
                        : "El resultado se guarda en " + guardaEn + " y sigue la segunda rotación."),
                llamada + ".");
    }

    private String regresoA(Grabador g, Nodo<K> padre, boolean izquierda) {
        if (padre == null) {
            return "y regresa al método público, que lo guarda en raiz.";
        }
        return "y regresa a " + g.de(padre) + ", que lo guardará en su r." + (izquierda ? "izq." : "der.");
    }

    private static String clave(Nodo<?> r) {
        return r == null ? "null" : String.valueOf(r.clave);
    }

    private static String signo(int fe) {
        return fe > 0 ? "+" + fe : String.valueOf(fe);
    }
}
