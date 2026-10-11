package pe.edu.unaj.aed2.semana06.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana06.modelo.EstadoMonticulo.Tipo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static pe.edu.unaj.aed2.semana06.modelo.EstadoMonticulo.padre;

/**
 * Montículo de mínimos (min-heap) guardado en un arreglo: el hijo izquierdo de k está en 2k + 1,
 * el derecho en 2k + 2 y su padre en (k − 1) / 2. Cada padre es menor o igual que sus hijos, así
 * que el mínimo siempre está en la posición 0. Es la base de una cola de prioridad.
 *
 * @param <K> tipo de los elementos; el "menor" según compareTo es el de mayor prioridad
 */
public class MonticuloMin<K extends Comparable<K>> {

    private final List<K> a = new ArrayList<>();

    // ------------------------------------------------------------------
    // Operaciones (siguen el código de la pestaña Java)
    // ------------------------------------------------------------------

    public void insertar(K x) {
        a.add(x);
        int i = a.size() - 1;
        while (i > 0 && a.get(padre(i)).compareTo(a.get(i)) > 0) {
            intercambiar(i, padre(i));
            i = padre(i);
        }
    }

    public K extraerMin() {
        if (a.isEmpty()) {
            throw new NoSuchElementException("Montículo vacío");
        }
        K min = a.get(0);
        K ultimo = a.remove(a.size() - 1);
        if (!a.isEmpty()) {
            a.set(0, ultimo);
            hundir(0);
        }
        return min;
    }

    private void hundir(int i) {
        int n = a.size();
        while (2 * i + 1 < n) {
            int m = 2 * i + 1;
            if (m + 1 < n && a.get(m + 1).compareTo(a.get(m)) < 0) {
                m++;
            }
            if (a.get(i).compareTo(a.get(m)) <= 0) {
                break;
            }
            intercambiar(i, m);
            i = m;
        }
    }

    public K minimo() {
        if (a.isEmpty()) {
            throw new NoSuchElementException("Montículo vacío");
        }
        return a.get(0);
    }

    public int tamanio() {
        return a.size();
    }

    public boolean estaVacio() {
        return a.isEmpty();
    }

    /** Copia del arreglo interno, en orden de posiciones. */
    public List<K> arreglo() {
        return List.copyOf(a);
    }

    private void intercambiar(int i, int j) {
        K t = a.get(i);
        a.set(i, a.get(j));
        a.set(j, t);
    }

    // ------------------------------------------------------------------
    // Pseudocódigo y código Java
    // ------------------------------------------------------------------

    public static final List<String> PSEUDO_INSERTAR = List.of(
            "insertar(x):",
            "    a[n] ← x;  n ← n + 1             // al final: primera hoja libre",
            "    i ← n − 1",
            "    mientras i > 0 y a[padre(i)] > a[i]:     // padre(i) = (i − 1) / 2",
            "        intercambiar a[i] y a[padre(i)];  i ← padre(i)");

    public static final List<String> PSEUDO_EXTRAER = List.of(
            "extraerMin():",
            "    min ← a[0]                       // el mínimo siempre está en la raíz",
            "    a[0] ← a[n − 1];  n ← n − 1      // el último pasa a la raíz",
            "    i ← 0",
            "    mientras i tenga hijos (2i + 1 < n):",
            "        m ← el hijo menor de i",
            "        si a[i] ≤ a[m]: salir del mientras",
            "        intercambiar a[i] y a[m];  i ← m",
            "    retornar min");

    /** Clase Java completa (lista para copiar y ejecutar) con marcadores para insertar, extraer y heapsort. */
    static final String JAVA = """
            import java.util.Arrays;

            public class Monticulo {

                private int[] a = new int[8];
                private int n = 0;

                private static int padre(int i) {
                    return (i - 1) / 2;
                }

                private static void intercambiar(int[] a, int i, int j) {
                    int t = a[i];
                    a[i] = a[j];
                    a[j] = t;
                }

                public void insertar(int x) {                                 //@insertar:1
                    if (n == a.length) {
                        a = Arrays.copyOf(a, 2 * n);   // si está lleno, crece
                    }
                    a[n] = x;                                                 //@insertar:2
                    n++;                                                      //@insertar:2
                    int i = n - 1;                                            //@insertar:3
                    while (i > 0 && a[padre(i)] > a[i]) {                     //@insertar:4
                        intercambiar(a, i, padre(i));                         //@insertar:5
                        i = padre(i);                                         //@insertar:5
                    }
                }

                public int extraerMin() {                                     //@extraer:1
                    if (n == 0) {
                        throw new IllegalStateException("Montículo vacío");
                    }
                    int min = a[0];                                           //@extraer:2
                    a[0] = a[n - 1];                                          //@extraer:3
                    n--;                                                      //@extraer:3
                    int i = 0;                                                //@extraer:4
                    while (2 * i + 1 < n) {                                   //@extraer:5
                        int m = 2 * i + 1;                                    //@extraer:6
                        if (m + 1 < n && a[m + 1] < a[m]) {                   //@extraer:6
                            m++;                                              //@extraer:6
                        }
                        if (a[i] <= a[m]) {                                   //@extraer:7
                            break;                                            //@extraer:7
                        }
                        intercambiar(a, i, m);                                //@extraer:8
                        i = m;                                                //@extraer:8
                    }
                    return min;                                               //@extraer:9
                }

                public boolean estaVacio() {
                    return n == 0;
                }

                // Ordena de menor a mayor con un montículo de MÁXIMOS dentro del mismo arreglo.
                public static int[] heapsort(int[] arreglo) {                 //@heapsort:1
                    int[] a = arreglo.clone();
                    int n = a.length;
                    for (int i = n / 2 - 1; i >= 0; i--) {                    //@heapsort:2
                        hundir(a, i, n);                                      //@heapsort:2
                    }
                    for (int fin = n - 1; fin > 0; fin--) {                   //@heapsort:3
                        intercambiar(a, 0, fin);                              //@heapsort:4
                        hundir(a, 0, fin);                                    //@heapsort:5
                    }
                    return a;
                }

                // Baja a[i] mientras sea menor que su hijo mayor (montículo de tamaño n).
                private static void hundir(int[] a, int i, int n) {           //@heapsort:6
                    while (2 * i + 1 < n) {                                   //@heapsort:7
                        int m = 2 * i + 1;                                    //@heapsort:8
                        if (m + 1 < n && a[m + 1] > a[m]) {                   //@heapsort:8
                            m++;                                              //@heapsort:8
                        }
                        if (a[i] >= a[m]) {                                   //@heapsort:9
                            break;                                            //@heapsort:9
                        }
                        intercambiar(a, i, m);                                //@heapsort:10
                        i = m;                                                //@heapsort:10
                    }
                }

                public static void main(String[] args) {
                    Monticulo cola = new Monticulo();
                    for (int x : new int[]{40, 20, 50, 10, 30, 15, 5}) {
                        cola.insertar(x);
                    }
                    StringBuilder extraidos = new StringBuilder();
                    while (!cola.estaVacio()) {
                        extraidos.append(cola.extraerMin()).append(' ');
                    }
                    System.out.println("Extraídos: " + extraidos.toString().trim());

                    int[] datos = {35, 12, 48, 7, 26, 19, 41, 3, 30, 15};
                    System.out.println("Original: " + Arrays.toString(datos));
                    System.out.println("Ordenado: " + Arrays.toString(heapsort(datos)));
                }
            }
            """;

    public static final CodigoAlgoritmo CODIGO_INSERTAR = CodigoAlgoritmo.seccion(PSEUDO_INSERTAR, JAVA, "insertar");
    public static final CodigoAlgoritmo CODIGO_EXTRAER = CodigoAlgoritmo.seccion(PSEUDO_EXTRAER, JAVA, "extraer");

    // ------------------------------------------------------------------
    // Paso a paso
    // ------------------------------------------------------------------

    private List<String> texto() {
        return a.stream().map(String::valueOf).toList();
    }

    private EstadoMonticulo paso(int i, int j, Tipo tipo, int linea, Map<String, String> variables,
                                 String evaluacion, String explicacion, String descripcion) {
        return new EstadoMonticulo(texto(), a.size(), i, j, tipo, linea, variables, evaluacion, explicacion,
                descripcion);
    }

    /** Un único paso con el estado actual (para mostrar el montículo sin operar). */
    public List<EstadoMonticulo> estadoActual(String mensaje) {
        return List.of(paso(-1, -1, Tipo.INICIO, 0, Map.of(), "", mensaje, mensaje));
    }

    public List<EstadoMonticulo> pasosInsertar(K x) {
        List<EstadoMonticulo> pasos = new ArrayList<>();
        pasos.add(paso(-1, -1, Tipo.INICIO, 0, PasoTrazable.variables("x", x, "n", a.size()),
                "insertar(" + x + ")",
                "Se llama insertar(" + x + "). Un montículo debe seguir siendo un árbol COMPLETO (sin huecos), así"
                        + " que el nuevo elemento solo puede ir en la primera posición libre del arreglo, a[" + a.size()
                        + "]. Después hay que hacerlo FLOTAR hasta que su padre no sea mayor que él.",
                "insertar(" + x + ")"));
        a.add(x);
        int i = a.size() - 1;
        pasos.add(paso(i, -1, Tipo.COLOCA, 2, variablesInsertar(x, i),
                "a[" + i + "] = " + x + ";  n = " + a.size() + ";  i = " + i,
                "Se guarda " + x + " en a[" + i + "] (la última hoja del árbol) y n pasa a " + a.size() + ". Luego"
                        + " i = n − 1 = " + i + " marca dónde está el elemento que va a flotar."
                        + (i == 0 ? " Como es el único elemento, ya es la raíz." : ""),
                x + " entra en a[" + i + "]."));
        while (true) {
            if (i == 0) {
                pasos.add(paso(i, -1, Tipo.QUIETO, 4, variablesInsertar(x, i),
                        "i > 0  →  0 > 0  →  falso",
                        "El while consulta i > 0: i = 0, es FALSO, así que " + x + " llegó a la raíz: es el nuevo"
                                + " mínimo del montículo. Se sale del while e insertar termina.",
                        x + " quedó en la raíz."));
                break;
            }
            int p = padre(i);
            K vp = a.get(p);
            K vi = a.get(i);
            boolean sube = vp.compareTo(vi) > 0;
            pasos.add(paso(i, p, Tipo.COMPARACION, 4, variablesInsertar(x, i),
                    "i > 0 && a[padre(i)] > a[i]  →  " + i + " > 0 && a[" + p + "] > a[" + i + "]  →  " + vp + " > "
                            + vi + "  →  " + PasoTrazable.vf(sube),
                    "El while consulta dos cosas. Primero i > 0: " + i + " > 0 es verdadero, " + vi + " todavía tiene"
                            + " padre. Luego se calcula padre(i) = (" + i + " − 1) / 2 = " + p + " y se pregunta si el"
                            + " padre a[" + p + "] = " + vp + " es mayor que a[" + i + "] = " + vi + ". "
                            + (sube ? "Sí: el padre es mayor, se rompe la regla del montículo, así que la ejecución"
                            + " entra al while para subir " + vi + "."
                            : "No: " + vp + " ≤ " + vi + ", la regla se cumple. Se sale del while: " + vi
                            + " quedó en su lugar e insertar termina."),
                    sube ? vp + " > " + vi + ": sube." : vp + " ≤ " + vi + ": se queda."));
            if (!sube) {
                break;
            }
            intercambiar(i, p);
            pasos.add(paso(p, i, Tipo.INTERCAMBIO, 5, variablesInsertar(x, p),
                    "intercambiar(a, " + i + ", " + p + ");  i = " + p,
                    "Se intercambian a[" + i + "] y a[" + p + "]: " + vi + " sube a la posición " + p + " y " + vp
                            + " baja a la " + i + ". Luego i = padre(i) = " + p + " y la ejecución regresa a la"
                            + " condición del while para comparar " + vi + " con su nuevo padre.",
                    vi + " sube a a[" + p + "]."));
            i = p;
        }
        pasos.add(paso(-1, -1, Tipo.FIN, 0, PasoTrazable.variables("x", x, "n", a.size()), "",
                "insertar(" + x + ") terminó. El arreglo vuelve a ser un montículo: todo padre es ≤ que sus hijos, y"
                        + " el mínimo, " + a.get(0) + ", está en a[0]. Costo: a lo más una comparación por nivel, O(log n).",
                "Fin de insertar(" + x + ")."));
        return pasos;
    }

    private Map<String, String> variablesInsertar(K x, int i) {
        return PasoTrazable.variables("x", x, "n", a.size(), "i", i,
                "padre(i)", i > 0 ? padre(i) : "—",
                "a[i]", a.get(i),
                "a[padre(i)]", i > 0 ? a.get(padre(i)) : "—");
    }

    public List<EstadoMonticulo> pasosExtraer() {
        List<EstadoMonticulo> pasos = new ArrayList<>();
        if (a.isEmpty()) {
            pasos.add(paso(-1, -1, Tipo.FIN, 0, Map.of(), "n == 0  →  excepción",
                    "El montículo está vacío: no hay mínimo que extraer (el código Java lanza una excepción).",
                    "Montículo vacío."));
            return pasos;
        }
        K min = a.get(0);
        pasos.add(paso(0, -1, Tipo.TOMA_RAIZ, 2, PasoTrazable.variables("min", min, "n", a.size()),
                "min = a[0] = " + min,
                "Se llama extraerMin(). Por la regla del montículo, el mínimo siempre está en la raíz: min = a[0] = "
                        + min + ". Sacarlo deja un hueco arriba, y el árbol tiene que seguir siendo completo.",
                "min = " + min + "."));
        K ultimo = a.remove(a.size() - 1);
        if (a.isEmpty()) {
            pasos.add(paso(-1, -1, Tipo.FIN, 9, PasoTrazable.variables("min", min, "n", 0),
                    "n = 0;  return " + min,
                    "Era el único elemento: el montículo queda vacío y se retorna " + min + ".",
                    "Se extrajo " + min + "."));
            return pasos;
        }
        a.set(0, ultimo);
        pasos.add(paso(0, -1, Tipo.MUEVE_ULTIMO, 3, variablesExtraer(min, 0, -1),
                "a[0] = a[" + a.size() + "] = " + ultimo + ";  n = " + a.size(),
                "Para tapar el hueco sin dejar huecos abajo, el ÚLTIMO elemento (" + ultimo + ") pasa a la raíz y n"
                        + " baja a " + a.size() + ". Seguramente " + ultimo + " es demasiado grande para estar arriba:"
                        + " ahora hay que HUNDIRLO. Se empieza con i = 0.",
                ultimo + " pasa a la raíz."));
        int i = 0;
        int n = a.size();
        while (true) {
            if (2 * i + 1 >= n) {
                pasos.add(paso(i, -1, Tipo.QUIETO, 5, variablesExtraer(min, i, -1),
                        "2i + 1 < n  →  " + (2 * i + 1) + " < " + n + "  →  falso",
                        "El while consulta si i = " + i + " tiene hijos: su hijo izquierdo estaría en 2·" + i + " + 1 = "
                                + (2 * i + 1) + ", que no es < n = " + n + ". No tiene hijos: " + a.get(i) + " llegó al"
                                + " fondo. Se sale del while y se ejecuta return min.",
                        a.get(i) + " llegó a una hoja."));
                break;
            }
            int izq = 2 * i + 1;
            int m = izq;
            boolean hayDer = izq + 1 < n;
            if (hayDer && a.get(izq + 1).compareTo(a.get(izq)) < 0) {
                m = izq + 1;
            }
            pasos.add(paso(i, m, Tipo.HIJO, 6, variablesExtraer(min, i, m),
                    "m = " + (hayDer ? "menor(a[" + izq + "] = " + a.get(izq) + ", a[" + (izq + 1) + "] = "
                            + a.get(izq + 1) + ")" : "a[" + izq + "] (único hijo)") + "  →  m = " + m,
                    "El while consulta 2i + 1 < n: " + izq + " < " + n + " es verdadero, " + a.get(i) + " tiene hijos. "
                            + (hayDer ? "Tiene dos: a[" + izq + "] = " + a.get(izq) + " y a[" + (izq + 1) + "] = "
                            + a.get(izq + 1) + ". Se elige el MENOR, " + a.get(m) + " (posición " + m + "), porque si"
                            + " sube debe ser menor que su hermano."
                            : "Solo tiene hijo izquierdo, a[" + izq + "] = " + a.get(izq) + ", así que m = " + izq + "."),
                    "Hijo menor de " + a.get(i) + ": " + a.get(m) + "."));
            K vi = a.get(i);
            K vm = a.get(m);
            boolean quieto = vi.compareTo(vm) <= 0;
            pasos.add(paso(i, m, quieto ? Tipo.QUIETO : Tipo.COMPARACION, 7, variablesExtraer(min, i, m),
                    "a[i] <= a[m]  →  " + vi + " <= " + vm + "  →  " + PasoTrazable.vf(quieto),
                    "El if consulta si a[i] = " + vi + " es ≤ que su hijo menor a[m] = " + vm + ". "
                            + (quieto ? "Sí: la regla se cumple, " + vi + " ya está en su lugar. break sale del while y"
                            + " se ejecuta return min."
                            : "No: " + vi + " es mayor que su hijo, hay que bajarlo."),
                    quieto ? vi + " ≤ " + vm + ": se queda." : vi + " > " + vm + ": baja."));
            if (quieto) {
                break;
            }
            intercambiar(i, m);
            pasos.add(paso(m, i, Tipo.INTERCAMBIO, 8, variablesExtraer(min, m, -1),
                    "intercambiar(a, " + i + ", " + m + ");  i = " + m,
                    "Se intercambian: " + vm + " sube a la posición " + i + " y " + vi + " baja a la " + m + ". Luego"
                            + " i = m = " + m + " y la ejecución regresa a la condición del while.",
                    vi + " baja a a[" + m + "]."));
            i = m;
        }
        pasos.add(paso(-1, -1, Tipo.FIN, 9, PasoTrazable.variables("min", min, "n", a.size()), "return " + min,
                "Se ejecuta return min: extraerMin devuelve " + min + ". El arreglo vuelve a ser un montículo y el"
                        + " nuevo mínimo, " + a.get(0) + ", ya está en a[0]. Costo: O(log n), una bajada por nivel.",
                "Se extrajo " + min + "."));
        return pasos;
    }

    private Map<String, String> variablesExtraer(K min, int i, int m) {
        return PasoTrazable.variables("min", min, "n", a.size(), "i", i, "m", m < 0 ? "—" : m,
                "a[i]", a.get(i), "a[m]", m < 0 ? "—" : a.get(m));
    }
}
