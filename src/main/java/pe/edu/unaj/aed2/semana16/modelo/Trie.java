package pe.edu.unaj.aed2.semana16.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana16.modelo.EstadoTrie.Tipo;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Trie (árbol de prefijos): cada nodo es una letra y cada camino desde la raíz deletrea un prefijo. Las palabras que
 * comparten comienzo comparten nodos. Buscar o autocompletar un prefijo de largo m cuesta O(m), sin importar cuántas
 * palabras haya.
 *
 * @param <V> dato guardado con cada palabra (por ejemplo, su significado); puede ser null
 */
public class Trie<V> {

    private static final class Nodo<V> {
        final Map<Character, Nodo<V>> hijos = new TreeMap<>();
        boolean finDePalabra;
        V valor;
    }

    /** Palabras del visualizador: comparten "p", "pu" y "pa". Están en el diccionario de la semana 04. */
    public static final List<String> EJEMPLO = List.of("puma", "puka", "puriy", "puñuy", "pacha", "para");

    private final Nodo<V> raiz = new Nodo<>();
    private int palabras;
    private int nodos = 1;

    public void insertar(String palabra, V valor) {
        Nodo<V> n = raiz;
        for (char c : palabra.toCharArray()) {
            if (!n.hijos.containsKey(c)) {
                n.hijos.put(c, new Nodo<>());
                nodos++;
            }
            n = n.hijos.get(c);
        }
        if (!n.finDePalabra) {
            palabras++;
        }
        n.finDePalabra = true;
        n.valor = valor;
    }

    public void insertar(String palabra) {
        insertar(palabra, null);
    }

    private Nodo<V> bajar(String prefijo) {
        Nodo<V> n = raiz;
        for (char c : prefijo.toCharArray()) {
            n = n.hijos.get(c);
            if (n == null) {
                return null;
            }
        }
        return n;
    }

    public boolean contiene(String palabra) {
        Nodo<V> n = bajar(palabra);
        return n != null && n.finDePalabra;
    }

    public V valor(String palabra) {
        Nodo<V> n = bajar(palabra);
        return n == null || !n.finDePalabra ? null : n.valor;
    }

    /** Todas las palabras que empiezan con el prefijo, en orden alfabético (según el código de cada carácter). */
    public List<String> autocompletar(String prefijo) {
        List<String> lista = new ArrayList<>();
        Nodo<V> n = bajar(prefijo);
        if (n != null) {
            recolectar(n, new StringBuilder(prefijo), lista);
        }
        return lista;
    }

    private static <V> void recolectar(Nodo<V> n, StringBuilder actual, List<String> lista) {
        if (n.finDePalabra) {
            lista.add(actual.toString());
        }
        for (Map.Entry<Character, Nodo<V>> e : n.hijos.entrySet()) {
            actual.append(e.getKey());
            recolectar(e.getValue(), actual, lista);
            actual.deleteCharAt(actual.length() - 1);
        }
    }

    public int palabras() {
        return palabras;
    }

    /** Nodos del trie, contando la raíz. */
    public int nodos() {
        return nodos;
    }

    /** Prefijos de todos los nodos y de los que son fin de palabra (para dibujar). */
    public Set<String> prefijos(boolean soloFinales) {
        Set<String> s = new LinkedHashSet<>();
        juntar(raiz, new StringBuilder(), s, soloFinales);
        return s;
    }

    private static <V> void juntar(Nodo<V> n, StringBuilder actual, Set<String> s, boolean soloFinales) {
        if (!soloFinales || n.finDePalabra) {
            s.add(actual.toString());
        }
        for (Map.Entry<Character, Nodo<V>> e : n.hijos.entrySet()) {
            actual.append(e.getKey());
            juntar(e.getValue(), actual, s, soloFinales);
            actual.deleteCharAt(actual.length() - 1);
        }
    }

    // ------------------------------------------------------------------
    // Pseudocódigo y código Java
    // ------------------------------------------------------------------

    public static final List<String> PSEUDO_INSERTAR = List.of(
            "insertar(palabra):",
            "    n ← raíz",
            "    para cada letra c de la palabra:",
            "        si n no tiene un hijo c: crearlo         // empieza una rama nueva",
            "        n ← hijo c de n                          // bajar un nivel",
            "    marcar n como fin de palabra");

    public static final List<String> PSEUDO_AUTOCOMPLETAR = List.of(
            "autocompletar(prefijo):",
            "    n ← raíz",
            "    para cada letra c del prefijo:",
            "        n ← hijo c de n;  si no existe: retornar []    // ninguna palabra empieza así",
            "    recolectar las palabras de todo el subárbol de n (en orden)",
            "    retornar palabras");

    static final String JAVA = """
            import java.util.ArrayList;
            import java.util.List;
            import java.util.Map;
            import java.util.TreeMap;

            public class Trie {

                static class Nodo {
                    Map<Character, Nodo> hijos = new TreeMap<>();   // en orden alfabético
                    boolean finDePalabra;
                }

                private final Nodo raiz = new Nodo();

                public void insertar(String palabra) {                                    //@insertar:1
                    Nodo n = raiz;                                                         //@insertar:2
                    for (char c : palabra.toCharArray()) {                                 //@insertar:3
                        if (!n.hijos.containsKey(c)) {                                     //@insertar:4
                            n.hijos.put(c, new Nodo());                                    //@insertar:4
                        }
                        n = n.hijos.get(c);                                                //@insertar:5
                    }
                    n.finDePalabra = true;                                                 //@insertar:6
                }

                // Todas las palabras que empiezan con prefijo.
                public List<String> autocompletar(String prefijo) {                       //@buscar:1
                    Nodo n = raiz;                                                         //@buscar:2
                    for (char c : prefijo.toCharArray()) {                                 //@buscar:3
                        n = n.hijos.get(c);                                                //@buscar:4
                        if (n == null) {                                                   //@buscar:4
                            return new ArrayList<>();                                      //@buscar:4
                        }
                    }
                    List<String> palabras = new ArrayList<>();
                    recolectar(n, new StringBuilder(prefijo), palabras);                   //@buscar:5
                    return palabras;                                                       //@buscar:6
                }

                // Recorre el subárbol en preorden: primero el nodo, luego sus hijos en orden alfabético.
                private void recolectar(Nodo n, StringBuilder actual, List<String> palabras) {
                    if (n.finDePalabra) {
                        palabras.add(actual.toString());
                    }
                    for (Map.Entry<Character, Nodo> e : n.hijos.entrySet()) {
                        actual.append(e.getKey());
                        recolectar(e.getValue(), actual, palabras);
                        actual.deleteCharAt(actual.length() - 1);
                    }
                }

                public static void main(String[] args) {
                    Trie t = new Trie();
                    for (String p : new String[]{%s}) {
                        t.insertar(p);
                    }
                    System.out.println("pu -> " + t.autocompletar("pu"));
                    System.out.println("pa -> " + t.autocompletar("pa"));
                    System.out.println("pi -> " + t.autocompletar("pi"));
                }
            }
            """;

    private static final String JAVA_EJEMPLO = String.format(JAVA,
            String.join(", ", EJEMPLO.stream().map(p -> "\"" + p + "\"").toList()));

    public static final CodigoAlgoritmo CODIGO_INSERTAR =
            CodigoAlgoritmo.seccion(PSEUDO_INSERTAR, JAVA_EJEMPLO, "insertar");
    public static final CodigoAlgoritmo CODIGO_AUTOCOMPLETAR =
            CodigoAlgoritmo.seccion(PSEUDO_AUTOCOMPLETAR, JAVA_EJEMPLO, "buscar");

    // ------------------------------------------------------------------
    // Paso a paso
    // ------------------------------------------------------------------

    /** Inserta las palabras una por una, letra por letra. */
    public static List<EstadoTrie> pasosInsertar(List<String> palabras) {
        Trie<Void> t = new Trie<>();
        List<EstadoTrie> pasos = new ArrayList<>();
        for (String palabra : palabras) {
            pasos.add(paso(t, "", null, palabra, List.of(), Tipo.INICIO, 2, palabra, null,
                    "insertar(\"" + palabra + "\");  n = raíz",
                    "Se llama insertar(\"" + palabra + "\"). n empieza en la RAÍZ, que representa el prefijo vacío. Se"
                            + " bajará una letra por nivel; donde ya exista el camino se reutiliza, donde no, se crea.",
                    "insertar(\"" + palabra + "\")"));
            Nodo<Void> n = t.raiz;
            StringBuilder prefijo = new StringBuilder();
            for (int k = 0; k < palabra.length(); k++) {
                char c = palabra.charAt(k);
                prefijo.append(c);
                String p = prefijo.toString();
                String antes = p.substring(0, p.length() - 1);
                String nodoAntes = antes.isEmpty() ? "la raíz" : "el nodo \"" + antes + "\"";
                if (!n.hijos.containsKey(c)) {
                    n.hijos.put(c, new Nodo<>());
                    t.nodos++;
                    n = n.hijos.get(c);
                    pasos.add(paso(t, p, p, palabra, List.of(), Tipo.CREA, 4, palabra, c,
                            "n.hijos.containsKey('" + c + "')  →  falso  →  crear hijo '" + c + "'",
                            "El for toma la letra '" + c + "' (posición " + (k + 1) + " de " + palabra.length() + "). El"
                                    + " if consulta si " + nodoAntes + " ya tiene un hijo '" + c + "': NO, ninguna palabra"
                                    + " anterior empieza con \"" + p + "\". Se crea el nodo y la línea 5 baja a él."
                                    + (k == palabra.length() - 1 ? " Era la última letra: el for termina."
                                    : " El for sigue con la letra '" + palabra.charAt(k + 1) + "'."),
                            "Crea \"" + p + "\"."));
                } else {
                    n = n.hijos.get(c);
                    pasos.add(paso(t, p, null, palabra, List.of(), Tipo.BAJA, 5, palabra, c,
                            "n.hijos.containsKey('" + c + "')  →  verdadero  →  n = hijo '" + c + "'",
                            "El for toma la letra '" + c + "'. El if consulta si " + nodoAntes + " ya tiene un hijo '"
                                    + c + "': SÍ, otra palabra ya empieza con \"" + p + "\". No se crea nada: se REUTILIZA"
                                    + " ese nodo y n baja a él. Así el trie ahorra memoria con los prefijos comunes."
                                    + (k == palabra.length() - 1 ? " Era la última letra: el for termina."
                                    : " El for sigue con la letra '" + palabra.charAt(k + 1) + "'."),
                            "Reutiliza \"" + p + "\"."));
                }
            }
            n.finDePalabra = true;
            t.palabras++;
            pasos.add(paso(t, palabra, null, palabra, List.of(), Tipo.MARCA, 6, palabra, null,
                    "n.finDePalabra = true  (\"" + palabra + "\")",
                    "Se leyeron todas las letras: el nodo \"" + palabra + "\" se MARCA como fin de palabra (borde"
                            + " grueso). Sin esa marca no se sabría si \"" + palabra + "\" es una palabra o solo el comienzo"
                            + " de otra. insertar termina. El trie tiene " + t.palabras + " palabras en " + t.nodos
                            + " nodos.",
                    "\"" + palabra + "\" insertada."));
        }
        return pasos;
    }

    /** Construye el trie con las palabras (sin pasos) y anima la búsqueda del prefijo. */
    public static List<EstadoTrie> pasosAutocompletar(List<String> palabras, String prefijo) {
        Trie<Void> t = new Trie<>();
        palabras.forEach(t::insertar);
        List<EstadoTrie> pasos = new ArrayList<>();
        pasos.add(paso(t, "", null, prefijo, List.of(), Tipo.INICIO, 2, prefijo, null,
                "autocompletar(\"" + prefijo + "\");  n = raíz",
                "Se llama autocompletar(\"" + prefijo + "\"). El trie ya tiene " + t.palabras + " palabras. n empieza en la"
                        + " raíz y bajará una letra del prefijo por nivel: son " + prefijo.length() + " pasos, sin"
                        + " importar si el diccionario tiene 10 o 10 000 palabras.",
                "autocompletar(\"" + prefijo + "\")"));
        Nodo<Void> n = t.raiz;
        StringBuilder p = new StringBuilder();
        for (int k = 0; k < prefijo.length(); k++) {
            char c = prefijo.charAt(k);
            String antes = p.toString();
            p.append(c);
            Nodo<Void> hijo = n.hijos.get(c);
            if (hijo == null) {
                pasos.add(paso(t, antes, null, prefijo, List.of(), Tipo.NO_EXISTE, 4, prefijo, c,
                        "n.hijos.get('" + c + "') == null  →  return []",
                        "El for toma la letra '" + c + "'. " + (antes.isEmpty() ? "La raíz" : "El nodo \"" + antes + "\"")
                                + " NO tiene un hijo '" + c + "': ninguna palabra empieza con \"" + p + "\". Se retorna"
                                + " la lista vacía sin revisar nada más.",
                        "Nadie empieza con \"" + p + "\"."));
                return pasos;
            }
            n = hijo;
            pasos.add(paso(t, p.toString(), null, prefijo, List.of(), Tipo.BAJA, 4, prefijo, c,
                    "n = n.hijos.get('" + c + "')  →  nodo \"" + p + "\"",
                    "El for toma la letra '" + c + "'. " + (antes.isEmpty() ? "La raíz" : "El nodo \"" + antes + "\"")
                            + " tiene un hijo '" + c + "', así que el if no entra y n baja al nodo \"" + p + "\"."
                            + (k == prefijo.length() - 1 ? " Era la última letra del prefijo." : ""),
                    "Baja a \"" + p + "\"."));
        }
        List<String> encontradas = new ArrayList<>();
        recolectar(n, new StringBuilder(prefijo), encontradas);
        pasos.add(paso(t, prefijo, null, prefijo, encontradas, Tipo.RECOLECTA, 5, prefijo, null,
                "recolectar(\"" + prefijo + "\")  →  " + encontradas,
                "Todas las palabras que empiezan con \"" + prefijo + "\" están en el SUBÁRBOL del nodo \"" + prefijo
                        + "\" (resaltado). recolectar lo recorre en preorden y, como los hijos se guardan en un TreeMap,"
                        + " salen en orden alfabético: " + String.join(", ", encontradas) + ".",
                encontradas.size() + " sugerencia" + (encontradas.size() == 1 ? "" : "s") + "."));
        pasos.add(paso(t, prefijo, null, prefijo, encontradas, Tipo.FIN, 6, prefijo, null,
                "return " + encontradas,
                "autocompletar retorna " + encontradas.size() + " palabra" + (encontradas.size() == 1 ? "" : "s")
                        + ". Bajar costó " + prefijo.length() + " pasos y recolectar es proporcional a lo que se"
                        + " devuelve: una lista ordenada de " + t.palabras + " palabras necesitaría compararlas todas"
                        + " (o una búsqueda binaria más un recorrido).",
                "Fin de autocompletar."));
        return pasos;
    }

    private static EstadoTrie paso(Trie<Void> t, String actual, String nuevo, String palabra, List<String> resultados,
                                   Tipo tipo, int linea, String texto, Character c, String evaluacion,
                                   String explicacion, String descripcion) {
        boolean buscando = tipo == Tipo.NO_EXISTE || tipo == Tipo.RECOLECTA || tipo == Tipo.FIN
                || (tipo != Tipo.CREA && tipo != Tipo.MARCA && evaluacion.startsWith("autocompletar"))
                || evaluacion.startsWith("n = n.hijos.get");
        return new EstadoTrie(t.prefijos(false), t.prefijos(true), actual, nuevo, palabra, resultados, tipo, linea,
                PasoTrazable.variables(
                        "c", c == null ? "—" : "'" + c + "'",
                        "n", actual == null ? "—" : actual.isEmpty() ? "raíz" : "\"" + actual + "\"",
                        buscando ? "prefijo" : "palabra", "\"" + texto + "\"",
                        "palabras", buscando && !resultados.isEmpty() ? resultados.toString() : "—"),
                evaluacion, explicacion, descripcion);
    }
}
