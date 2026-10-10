package pe.edu.unaj.aed2.semana03.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Tabla hash con encadenamiento separado: cada cubeta es una lista enlazada con las
 * entradas cuyas claves caen en el mismo índice. Si el factor de carga n/m supera
 * {@link #CARGA_MAXIMA}, se hace rehash a 2m + 1 cubetas.
 * <p>
 * {@link #insertar} y {@link #buscar} hacen la operación; {@link #pasosInsertar} y
 * {@link #pasosBuscar} la hacen igual pero registrando cada paso para animarla.
 *
 * @param <V> valor asociado a cada clave
 */
public class TablaHash<V> {

    public static final double CARGA_MAXIMA = 0.75;

    /** Par clave-valor guardado en una cubeta. */
    public record Entrada<V>(String clave, V valor) {
    }

    private List<LinkedList<Entrada<V>>> tabla;
    private int n;

    public TablaHash(int capacidad) {
        if (capacidad < 1) {
            throw new IllegalArgumentException("La capacidad debe ser al menos 1");
        }
        tabla = vacia(capacidad);
    }

    /** Método de Horner: h = (h · 31 + código del carácter) mod m, carácter por carácter. */
    public static int hash(String clave, int m) {
        int h = 0;
        for (int k = 0; k < clave.length(); k++) {
            h = (h * 31 + clave.charAt(k)) % m;
        }
        return h;
    }

    /** Inserta o actualiza. Retorna false si la clave ya existía (en ese caso actualiza su valor). */
    public boolean insertar(String clave, V valor) {
        LinkedList<Entrada<V>> cubeta = tabla.get(hash(clave, tabla.size()));
        for (int k = 0; k < cubeta.size(); k++) {
            if (cubeta.get(k).clave().equals(clave)) {
                cubeta.set(k, new Entrada<>(clave, valor));
                return false;
            }
        }
        cubeta.add(new Entrada<>(clave, valor));
        n++;
        if (factorCarga() > CARGA_MAXIMA) {
            rehash();
        }
        return true;
    }

    /** Valor asociado a la clave, o null si no está. */
    public V buscar(String clave) {
        for (Entrada<V> e : tabla.get(hash(clave, tabla.size()))) {
            if (e.clave().equals(clave)) {
                return e.valor();
            }
        }
        return null;
    }

    public boolean contiene(String clave) {
        for (Entrada<V> e : tabla.get(hash(clave, tabla.size()))) {
            if (e.clave().equals(clave)) {
                return true;
            }
        }
        return false;
    }

    /** Cuántas veces se compara la clave buscada con claves de su cubeta hasta encontrarla o agotarla. */
    public int comparaciones(String clave) {
        int k = 0;
        for (Entrada<V> e : tabla.get(hash(clave, tabla.size()))) {
            k++;
            if (e.clave().equals(clave)) {
                return k;
            }
        }
        return k;
    }

    public int tamanio() {
        return n;
    }

    public int capacidad() {
        return tabla.size();
    }

    public double factorCarga() {
        return (double) n / tabla.size();
    }

    /** Longitud de cada cadena, para ver qué tan pareja es la distribución. */
    public int[] longitudes() {
        return tabla.stream().mapToInt(List::size).toArray();
    }

    /** Claves de cada cubeta (copia). */
    public List<List<String>> claves() {
        return tabla.stream().map(c -> c.stream().map(Entrada::clave).toList()).toList();
    }

    private void rehash() {
        List<LinkedList<Entrada<V>>> vieja = tabla;
        tabla = vacia(vieja.size() * 2 + 1);
        for (LinkedList<Entrada<V>> cubeta : vieja) {
            for (Entrada<V> e : cubeta) {
                tabla.get(hash(e.clave(), tabla.size())).add(e);
            }
        }
    }

    private static <V> List<LinkedList<Entrada<V>>> vacia(int m) {
        List<LinkedList<Entrada<V>>> t = new ArrayList<>(m);
        for (int i = 0; i < m; i++) {
            t.add(new LinkedList<>());
        }
        return t;
    }

    // ------------------------------------------------------------------
    // Pseudocódigo y código Java
    // ------------------------------------------------------------------

    public static final List<String> PSEUDO_INSERTAR = List.of(
            "insertar(clave):",
            "    i ← hash(clave)",
            "    para cada e en tabla[i]:",
            "        si e = clave: retornar falso",
            "    agregar clave al final de tabla[i];  n ← n + 1",
            "    si n / m > 0.75: rehash()",
            "    retornar verdadero");

    public static final List<String> PSEUDO_BUSCAR = List.of(
            "buscar(clave):",
            "    i ← hash(clave)",
            "    para cada e en tabla[i]:",
            "        si e = clave: retornar verdadero",
            "    retornar falso");

    /** Clase Java completa (lista para copiar y ejecutar) con marcadores por método. */
    private static final String JAVA = """
            import java.util.ArrayList;
            import java.util.LinkedList;
            import java.util.List;

            public class TablaHash {

                private List<LinkedList<String>> tabla = new ArrayList<>();
                private int n = 0;

                public TablaHash(int capacidad) {
                    for (int i = 0; i < capacidad; i++) {
                        tabla.add(new LinkedList<>());
                    }
                }

                // Método de Horner: h = (h * 31 + código) mod m.
                public static int hash(String clave, int m) {
                    int h = 0;
                    for (int k = 0; k < clave.length(); k++) {
                        h = (h * 31 + clave.charAt(k)) % m;
                    }
                    return h;
                }

                public boolean insertar(String clave) {               //@insertar:1
                    int i = hash(clave, tabla.size());                //@insertar:2
                    for (String e : tabla.get(i)) {                   //@insertar:3
                        if (e.equals(clave)) {                        //@insertar:4
                            return false;                             //@insertar:4
                        }
                    }
                    tabla.get(i).add(clave);                          //@insertar:5
                    n++;                                              //@insertar:5
                    if ((double) n / tabla.size() > 0.75) {           //@insertar:6
                        rehash();                                     //@insertar:6
                    }
                    return true;                                      //@insertar:7
                }

                public boolean buscar(String clave) {                 //@buscar:1
                    int i = hash(clave, tabla.size());                //@buscar:2
                    for (String e : tabla.get(i)) {                   //@buscar:3
                        if (e.equals(clave)) {                        //@buscar:4
                            return true;                              //@buscar:4
                        }
                    }
                    return false;                                     //@buscar:5
                }

                // Crea 2m + 1 cubetas y vuelve a insertar todo:
                // con otro m, cada clave puede caer en otra cubeta.
                private void rehash() {
                    List<LinkedList<String>> vieja = tabla;
                    tabla = new ArrayList<>();
                    for (int i = 0; i < vieja.size() * 2 + 1; i++) {
                        tabla.add(new LinkedList<>());
                    }
                    for (LinkedList<String> cubeta : vieja) {
                        for (String clave : cubeta) {
                            tabla.get(hash(clave, tabla.size())).add(clave);
                        }
                    }
                }

                public static void main(String[] args) {
                    TablaHash padron = new TablaHash(7);
                    String[] codigos = {"JUL-102", "JUL-215", "PUN-031", "JUL-347", "CAR-008", "JUL-520"};
                    for (String c : codigos) {
                        padron.insertar(c);
                    }
                    System.out.println("buscar(\\"JUL-347\\") = " + padron.buscar("JUL-347"));
                    System.out.println("buscar(\\"PUN-999\\") = " + padron.buscar("PUN-999"));
                }
            }
            """;

    public static final CodigoAlgoritmo CODIGO_INSERTAR = CodigoAlgoritmo.seccion(PSEUDO_INSERTAR, JAVA, "insertar");
    public static final CodigoAlgoritmo CODIGO_BUSCAR = CodigoAlgoritmo.seccion(PSEUDO_BUSCAR, JAVA, "buscar");

    // ------------------------------------------------------------------
    // Operaciones paso a paso
    // ------------------------------------------------------------------

    /** Un único paso con el estado actual de la tabla (para mostrarla sin operar). */
    public List<EstadoTabla> estadoActual() {
        return List.of(paso("", -1, -1, EstadoTabla.Tipo.INICIO, 0, null, "",
                "Tabla con m = " + tabla.size() + " cubetas vacías. Escribe una clave y pulsa Insertar.",
                "Tabla nueva."));
    }

    /** Inserta (igual que {@link #insertar}) registrando cada paso. */
    public List<EstadoTabla> pasosInsertar(String clave, V valor) {
        List<EstadoTabla> pasos = new ArrayList<>();
        int m = tabla.size();
        pasos.add(paso(clave, -1, -1, EstadoTabla.Tipo.INICIO, 0, null, "insertar(\"" + clave + "\")",
                "Se llama insertar(\"" + clave + "\"). La tabla tiene m = " + m + " cubetas y guarda "
                        + claves(n) + ". Primero hay que calcular en qué cubeta va la clave.",
                "insertar(\"" + clave + "\")"));
        int i = hash(clave, m);
        pasos.add(paso(clave, i, -1, EstadoTabla.Tipo.HASH, 2, null,
                "i = hash(\"" + clave + "\", " + m + ") = " + i,
                explicarHash(clave, m) + " La clave debe ir en la cubeta " + i + "; antes de agregarla,"
                        + " hay que revisar esa cubeta por si ya existe.",
                "hash(\"" + clave + "\") = " + i));
        LinkedList<Entrada<V>> cubeta = tabla.get(i);
        if (cubeta.isEmpty()) {
            pasos.add(paso(clave, i, -1, EstadoTabla.Tipo.CUBETA_VACIA, 3, null,
                    "tabla[" + i + "] está vacía  →  el for no da ninguna vuelta",
                    "El for recorre la lista de la cubeta " + i + " para ver si la clave ya existe, pero la"
                            + " cubeta está vacía: el for termina sin dar ninguna vuelta y la ejecución pasa a"
                            + " agregar la clave.",
                    "Cubeta " + i + " vacía."));
        }
        for (int k = 0; k < cubeta.size(); k++) {
            String e = cubeta.get(k).clave();
            boolean igual = e.equals(clave);
            pasos.add(paso(clave, i, k, igual ? EstadoTabla.Tipo.YA_EXISTE : EstadoTabla.Tipo.COMPARACION, 4, e,
                    "e.equals(clave)  →  \"" + e + "\".equals(\"" + clave + "\")  →  " + PasoTrazable.vf(igual),
                    "El for toma el elemento " + (k + 1) + " de la cubeta " + i + ", e = \"" + e + "\", y el if"
                            + " consulta si es igual a la clave \"" + clave + "\". "
                            + (igual
                            ? "Es VERDADERO: la clave ya está en la tabla. Se ejecuta return false: no se agrega"
                                    + " un duplicado (en el modelo con valores, se actualiza el valor) y la"
                                    + " ejecución regresa a quien llamó a insertar."
                            : "Es FALSO: hubo una COLISIÓN (otra clave en la misma cubeta), pero no es la misma"
                                    + " clave. " + (k + 1 < cubeta.size()
                                    ? "El for avanza al siguiente elemento de la lista."
                                    : "Era el último elemento: el for termina y la ejecución pasa a agregar.")),
                    igual ? "\"" + clave + "\" ya existe." : "Colisión con \"" + e + "\"."));
            if (igual) {
                cubeta.set(k, new Entrada<>(clave, valor));
                pasos.add(paso(clave, i, k, EstadoTabla.Tipo.FIN, 0, e, "return false",
                        "insertar terminó y devolvió false: la tabla no cambió y sigue con " + claves(n) + ".",
                        "No se insertó."));
                return pasos;
            }
        }
        cubeta.add(new Entrada<>(clave, valor));
        n++;
        pasos.add(paso(clave, i, cubeta.size() - 1, EstadoTabla.Tipo.AGREGADA, 5, null,
                "tabla[" + i + "].add(\"" + clave + "\");  n++  →  n = " + n,
                "Se agrega \"" + clave + "\" al final de la lista de la cubeta " + i
                        + (cubeta.size() > 1 ? ", que ahora tiene " + cubeta.size() + " elementos encadenados." : ".")
                        + " Luego n++ deja n = " + n + ".",
                "\"" + clave + "\" agregada en la cubeta " + i + "."));
        double carga = factorCarga();
        boolean excede = carga > CARGA_MAXIMA;
        pasos.add(paso(clave, i, cubeta.size() - 1, EstadoTabla.Tipo.CARGA, 6, null,
                "n / m > 0.75  →  " + n + " / " + m + " = " + f(carga) + " > 0.75  →  " + PasoTrazable.vf(excede),
                "El if consulta el factor de carga α = n / m = " + n + " / " + m + " = " + f(carga) + ", que es el"
                        + " largo promedio de las cadenas. "
                        + (excede
                        ? "Supera 0.75: las cadenas se están alargando y las búsquedas se harían lentas, así que"
                                + " se llama a rehash()."
                        : "No supera 0.75, así que no hace falta agrandar la tabla y la ejecución sigue al return."),
                "α = " + f(carga) + (excede ? ": hace falta rehash." : ".")));
        if (excede) {
            List<List<String>> antes = claves();
            rehash();
            int nuevoM = tabla.size();
            StringBuilder movidas = new StringBuilder();
            for (int c = 0; c < antes.size(); c++) {
                for (String k : antes.get(c)) {
                    int nueva = hash(k, nuevoM);
                    if (movidas.length() > 0) {
                        movidas.append("; ");
                    }
                    movidas.append(k).append(": ").append(c).append(" → ").append(nueva);
                }
            }
            int nuevaI = hash(clave, nuevoM);
            pasos.add(paso(clave, nuevaI, claves().get(nuevaI).indexOf(clave), EstadoTabla.Tipo.REHASH, 6, null,
                    "rehash()  →  m pasa de " + m + " a " + nuevoM,
                    "rehash() crea una tabla nueva de 2·" + m + " + 1 = " + nuevoM + " cubetas y vuelve a insertar"
                            + " las " + n + " claves. Como el hash usa mod m y m cambió, cada clave puede caer en"
                            + " otra cubeta: " + movidas + ". El factor de carga baja a " + n + " / " + nuevoM
                            + " = " + f(factorCarga()) + ". Cuando rehash() termina, la ejecución regresa a"
                            + " insertar, a la línea que sigue al if (el return).",
                    "Rehash: m = " + nuevoM + "."));
        }
        pasos.add(paso(clave, hash(clave, tabla.size()), claves().get(hash(clave, tabla.size())).indexOf(clave),
                EstadoTabla.Tipo.FIN, 7, null, "return true",
                "Se ejecuta return true: insertar devuelve true a quien la llamó, indicando que la clave es nueva."
                        + " La tabla queda con " + claves(n) + " en m = " + tabla.size() + " cubetas.",
                "\"" + clave + "\" insertada."));
        return pasos;
    }

    /** Busca (igual que {@link #buscar}) registrando cada paso. */
    public List<EstadoTabla> pasosBuscar(String clave) {
        List<EstadoTabla> pasos = new ArrayList<>();
        int m = tabla.size();
        pasos.add(paso(clave, -1, -1, EstadoTabla.Tipo.INICIO, 0, null, "buscar(\"" + clave + "\")",
                "Se llama buscar(\"" + clave + "\"). Gracias al hash no hace falta revisar " + claves(n)
                        + " una por una: basta mirar la cubeta donde la clave tendría que estar.",
                "buscar(\"" + clave + "\")"));
        int i = hash(clave, m);
        pasos.add(paso(clave, i, -1, EstadoTabla.Tipo.HASH, 2, null,
                "i = hash(\"" + clave + "\", " + m + ") = " + i,
                explicarHash(clave, m) + " Si la clave existe, solo puede estar en la cubeta " + i + ".",
                "hash(\"" + clave + "\") = " + i));
        LinkedList<Entrada<V>> cubeta = tabla.get(i);
        for (int k = 0; k < cubeta.size(); k++) {
            String e = cubeta.get(k).clave();
            boolean igual = e.equals(clave);
            pasos.add(paso(clave, i, k, igual ? EstadoTabla.Tipo.ENCONTRADA : EstadoTabla.Tipo.COMPARACION, 4, e,
                    "e.equals(clave)  →  \"" + e + "\".equals(\"" + clave + "\")  →  " + PasoTrazable.vf(igual),
                    "El for toma el elemento " + (k + 1) + " de la cubeta " + i + ", e = \"" + e + "\", y el if"
                            + " consulta si es igual a \"" + clave + "\". "
                            + (igual
                            ? "Es VERDADERO: ¡encontrada! Se ejecuta return true y buscar devuelve true a quien la"
                                    + " llamó, después de solo " + (k + 1) + " comparación" + (k == 0 ? "" : "es")
                                    + "."
                            : "Es FALSO. " + (k + 1 < cubeta.size()
                                    ? "El for avanza al siguiente elemento de la cadena."
                                    : "Era el último elemento: el for termina sin encontrarla.")),
                    igual ? "¡Encontrada!" : "\"" + e + "\" no es."));
            if (igual) {
                pasos.add(paso(clave, i, k, EstadoTabla.Tipo.FIN, 0, e, "return true",
                        "buscar terminó: la clave está en la cubeta " + i + ", como elemento " + (k + 1)
                                + " de su cadena.",
                        "Encontrada."));
                return pasos;
            }
        }
        pasos.add(paso(clave, i, -1, EstadoTabla.Tipo.NO_ENCONTRADA, 5, null,
                (cubeta.isEmpty() ? "tabla[" + i + "] está vacía;  " : "") + "return false",
                (cubeta.isEmpty()
                        ? "La cubeta " + i + " está vacía: el for no da ninguna vuelta. "
                        : "Se revisó toda la cadena de la cubeta " + i + " (" + cubeta.size() + " elemento"
                                + (cubeta.size() == 1 ? "" : "s") + ") sin encontrarla. ")
                        + "Se ejecuta return false: la clave no está en la tabla. No hace falta mirar otras"
                        + " cubetas, porque hash(\"" + clave + "\") siempre da " + i + ".",
                "No encontrada."));
        return pasos;
    }

    private EstadoTabla paso(String clave, int cubeta, int posicion, EstadoTabla.Tipo tipo, int linea, String e,
                             String evaluacion, String explicacion, String descripcion) {
        Map<String, String> variables = PasoTrazable.variables(
                "clave", "\"" + clave + "\"",
                "i", cubeta < 0 ? "—" : cubeta,
                "e", e == null ? "—" : "\"" + e + "\"",
                "n", n,
                "m", tabla.size(),
                "n/m", f(factorCarga()));
        return new EstadoTabla(claves(), clave, cubeta, posicion, tipo, linea, variables, evaluacion,
                explicacion, descripcion);
    }

    /** Horner carácter por carácter, en palabras. */
    static String explicarHash(String clave, int m) {
        StringBuilder sb = new StringBuilder("Se calcula hash(\"" + clave + "\", " + m + ") con el método de Horner,"
                + " carácter por carácter: h = (h · 31 + código del carácter) mod " + m + ". ");
        int h = 0;
        for (int k = 0; k < clave.length(); k++) {
            char c = clave.charAt(k);
            int nuevo = (h * 31 + c) % m;
            sb.append("'").append(c).append("'(").append((int) c).append("): (").append(h).append("·31 + ")
                    .append((int) c).append(") mod ").append(m).append(" = ").append(nuevo)
                    .append(k + 1 < clave.length() ? "; " : ". ");
            h = nuevo;
        }
        sb.append("Resultado: i = ").append(h).append(".");
        return sb.toString();
    }

    private static String claves(int cantidad) {
        return cantidad == 1 ? "1 clave" : cantidad + " claves";
    }

    private static String f(double x) {
        return String.format(Locale.ROOT, "%.2f", x);
    }
}
