package pe.edu.unaj.aed2.semana15.modelo;

import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana15.modelo.EstadoHuffman.Tipo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.TreeMap;

/**
 * Códigos de Huffman: un algoritmo VORAZ que comprime texto dando códigos binarios cortos a los caracteres frecuentes
 * y largos a los raros. Une siempre los dos árboles menos frecuentes (con la cola de prioridad de la semana 06).
 */
public final class Huffman {

    /** Nodo del árbol. Hoja: caracter no es null. orden desempata frecuencias iguales (el más antiguo primero). */
    public record Nodo(int frec, Character caracter, Nodo izq, Nodo der, int orden) {
        public boolean esHoja() {
            return caracter != null;
        }

        /** Texto para dibujar: "a:4" en las hojas, la frecuencia en los nodos internos. */
        public String etiqueta() {
            return esHoja() ? visible(caracter) + ":" + frec : String.valueOf(frec);
        }

        /** Los caracteres de las hojas de este árbol, de izquierda a derecha. */
        public String caracteres() {
            return esHoja() ? visible(caracter) : izq.caracteres() + der.caracteres();
        }
    }

    public static final Comparator<Nodo> PRIORIDAD = Comparator.comparingInt(Nodo::frec).thenComparingInt(Nodo::orden);

    /** Texto del visualizador. */
    public static final String EJEMPLO = "pachamama paqarin";

    private Huffman() {
    }

    /** Un espacio se muestra como "␣" para que se vea. */
    public static String visible(char c) {
        return c == ' ' ? "␣" : String.valueOf(c);
    }

    public static Map<Character, Integer> frecuencias(String texto) {
        Map<Character, Integer> frec = new TreeMap<>();
        for (char ch : texto.toCharArray()) {
            frec.merge(ch, 1, Integer::sum);
        }
        return frec;
    }

    /** Árbol de Huffman del texto (null si el texto está vacío). */
    public static Nodo arbol(String texto) {
        PriorityQueue<Nodo> cola = new PriorityQueue<>(PRIORIDAD);
        int orden = 0;
        for (Map.Entry<Character, Integer> e : frecuencias(texto).entrySet()) {
            cola.add(new Nodo(e.getValue(), e.getKey(), null, null, orden++));
        }
        while (cola.size() > 1) {
            Nodo a = cola.poll();
            Nodo b = cola.poll();
            cola.add(new Nodo(a.frec() + b.frec(), null, a, b, orden++));
        }
        return cola.poll();
    }

    public static Map<Character, String> codigos(String texto) {
        Map<Character, String> codigos = new TreeMap<>();
        Nodo raiz = arbol(texto);
        if (raiz != null) {
            asignar(raiz, "", codigos);
        }
        return codigos;
    }

    private static void asignar(Nodo n, String camino, Map<Character, String> codigos) {
        if (n.esHoja()) {
            codigos.put(n.caracter(), camino.isEmpty() ? "0" : camino);
            return;
        }
        asignar(n.izq(), camino + "0", codigos);
        asignar(n.der(), camino + "1", codigos);
    }

    public static String codificar(String texto, Map<Character, String> codigos) {
        StringBuilder bits = new StringBuilder();
        for (char ch : texto.toCharArray()) {
            bits.append(codigos.get(ch));
        }
        return bits.toString();
    }

    /** Recorre el árbol bit por bit: 0 a la izquierda, 1 a la derecha; al llegar a una hoja, sale un carácter. */
    public static String decodificar(String bits, Nodo raiz) {
        StringBuilder texto = new StringBuilder();
        if (raiz == null) {
            return "";
        }
        if (raiz.esHoja()) {
            return String.valueOf(raiz.caracter()).repeat(bits.length());
        }
        Nodo n = raiz;
        for (char b : bits.toCharArray()) {
            n = b == '0' ? n.izq() : n.der();
            if (n.esHoja()) {
                texto.append(n.caracter());
                n = raiz;
            }
        }
        return texto.toString();
    }

    /** Bits con Huffman. */
    public static int bitsHuffman(String texto) {
        return codificar(texto, codigos(texto)).length();
    }

    /** Bits con un código de largo fijo: ⌈log2(caracteres distintos)⌉ por carácter (mínimo 1). */
    public static int bitsFijos(String texto) {
        int distintos = frecuencias(texto).size();
        int largo = 1;
        while ((1 << largo) < distintos) {
            largo++;
        }
        return texto.length() * largo;
    }

    // ------------------------------------------------------------------
    // Pseudocódigo y código Java
    // ------------------------------------------------------------------

    public static final List<String> PSEUDOCODIGO = List.of(
            "huffman(texto):",
            "    contar la frecuencia de cada carácter",
            "    cola ← una hoja (frecuencia, carácter) por cada carácter     // montículo de mínimos",
            "    mientras la cola tenga más de un árbol:",
            "        a ← cola.extraerMin();  b ← cola.extraerMin()            // los dos MENOS frecuentes",
            "        cola.insertar(nodo(a.frec + b.frec, izq = a, der = b))   // los une",
            "    raíz ← cola.extraerMin()",
            "    código de cada hoja = camino desde la raíz (izquierda 0, derecha 1)",
            "    retornar códigos");

    static final String JAVA = """
            import java.util.Comparator;
            import java.util.Map;
            import java.util.PriorityQueue;
            import java.util.TreeMap;

            public class Huffman {

                // Hoja: caracter != null. Nodo interno: tiene izq y der. orden desempata frecuencias iguales.
                record Nodo(int frec, Character caracter, Nodo izq, Nodo der, int orden) { }

                public static Map<Character, String> codigos(String texto) {                  //@1
                    Map<Character, Integer> frec = new TreeMap<>();
                    for (char ch : texto.toCharArray()) {                                          //@2
                        frec.merge(ch, 1, Integer::sum);                                           //@2
                    }
                    PriorityQueue<Nodo> cola = new PriorityQueue<>(
                            Comparator.comparingInt(Nodo::frec).thenComparingInt(Nodo::orden));
                    int orden = 0;
                    for (Map.Entry<Character, Integer> e : frec.entrySet()) {                      //@3
                        cola.add(new Nodo(e.getValue(), e.getKey(), null, null, orden++));         //@3
                    }
                    while (cola.size() > 1) {                                                      //@4
                        Nodo a = cola.poll();                                                      //@5
                        Nodo b = cola.poll();                                                      //@5
                        cola.add(new Nodo(a.frec() + b.frec(), null, a, b, orden++));              //@6
                    }
                    Nodo raiz = cola.poll();                                                       //@7
                    Map<Character, String> codigos = new TreeMap<>();
                    if (raiz != null) {
                        asignar(raiz, "", codigos);                                                //@8
                    }
                    return codigos;                                                                //@9
                }

                // Recorre el árbol: a la izquierda agrega "0", a la derecha "1"; en una hoja, ese es su código.
                private static void asignar(Nodo n, String camino, Map<Character, String> codigos) {
                    if (n.caracter() != null) {
                        codigos.put(n.caracter(), camino.isEmpty() ? "0" : camino);
                        return;
                    }
                    asignar(n.izq(), camino + "0", codigos);
                    asignar(n.der(), camino + "1", codigos);
                }

                public static void main(String[] args) {
                    String texto = "%s";
                    Map<Character, String> codigos = codigos(texto);
                    StringBuilder bits = new StringBuilder();
                    for (char ch : texto.toCharArray()) {
                        bits.append(codigos.get(ch));
                    }
                    System.out.println("Códigos: " + codigos);
                    System.out.println("Huffman: " + bits.length() + " bits; ASCII: " + 8 * texto.length() + " bits");
                    System.out.println(bits);
                }
            }
            """;

    public static final CodigoAlgoritmo CODIGO = new CodigoAlgoritmo(PSEUDOCODIGO, String.format(JAVA, EJEMPLO));

    // ------------------------------------------------------------------
    // Paso a paso
    // ------------------------------------------------------------------

    public static List<EstadoHuffman> pasos(String texto) {
        List<EstadoHuffman> pasos = new ArrayList<>();
        Map<Character, Integer> frec = frecuencias(texto);
        List<String> lista = new ArrayList<>();
        frec.forEach((c, f) -> lista.add(visible(c) + "=" + f));
        pasos.add(new EstadoHuffman(List.of(), null, null, null, Map.of(), Tipo.FRECUENCIAS, 2,
                vars(null, null, null, List.of()),
                "frec = {" + String.join(", ", lista) + "}",
                "Se llama huffman(\"" + texto + "\"). El texto tiene " + texto.length() + " caracteres y " + frec.size()
                        + " distintos. Primero se cuenta cuántas veces aparece cada uno. Con ASCII, cada carácter usa 8"
                        + " bits (" + 8 * texto.length() + " en total). La idea de Huffman: los que aparecen MÁS deben"
                        + " tener códigos más CORTOS.",
                "Frecuencias de " + frec.size() + " caracteres."));
        PriorityQueue<Nodo> cola = new PriorityQueue<>(PRIORIDAD);
        int orden = 0;
        for (Map.Entry<Character, Integer> e : frec.entrySet()) {
            cola.add(new Nodo(e.getValue(), e.getKey(), null, null, orden++));
        }
        pasos.add(new EstadoHuffman(ordenada(cola), null, null, null, Map.of(), Tipo.HOJAS, 3,
                vars(null, null, null, ordenada(cola)),
                "cola = " + textoCola(ordenada(cola)),
                "Cada carácter se vuelve una HOJA (un árbol de un solo nodo) con su frecuencia, y todas entran a una cola"
                        + " de prioridad: el montículo de mínimos de la semana 06. Abajo se ven ordenadas de menor a"
                        + " mayor frecuencia (si empatan, primero la que entró antes).",
                frec.size() + " hojas en la cola."));
        while (cola.size() > 1) {
            int antes = cola.size();
            Nodo a = cola.poll();
            Nodo b = cola.poll();
            Nodo nuevo = new Nodo(a.frec() + b.frec(), null, a, b, orden++);
            cola.add(nuevo);
            pasos.add(new EstadoHuffman(ordenada(cola), a, b, nuevo, Map.of(), Tipo.UNE, 6,
                    vars(a, b, nuevo, ordenada(cola)),
                    "a = " + a.etiqueta() + ";  b = " + b.etiqueta() + "  →  nodo(" + a.frec() + " + " + b.frec() + " = "
                            + nuevo.frec() + ")",
                    "El while consulta si la cola tiene más de un árbol: tiene " + antes + ", así que sigue. Se extraen"
                            + " los DOS MENOS FRECUENTES: " + describir(a) + " y " + describir(b) + ". Se unen bajo un"
                            + " nodo nuevo de frecuencia " + a.frec() + " + " + b.frec() + " = " + nuevo.frec() + " ("
                            + a.caracteres() + " a la izquierda, " + b.caracteres() + " a la derecha) y vuelve a la"
                            + " cola. Es la decisión VORAZ: lo raro queda abajo, con códigos largos; lo frecuente se une"
                            + " tarde y queda cerca de la raíz. Quedan " + cola.size() + " árboles.",
                    "Une " + a.caracteres() + " (" + a.frec() + ") y " + b.caracteres() + " (" + b.frec() + ") → "
                            + nuevo.frec() + "."));
        }
        Nodo raiz = cola.poll();
        pasos.add(new EstadoHuffman(raiz == null ? List.of() : List.of(raiz), null, null, raiz, Map.of(), Tipo.RAIZ, 7,
                vars(null, null, raiz, List.of()),
                "cola.size() > 1  →  " + (raiz == null ? 0 : 1) + " > 1  →  falso  →  raiz = cola.poll()",
                "El while consulta si queda más de un árbol: queda uno solo, así que termina. Ese árbol es el árbol de"
                        + " Huffman: su raíz vale " + (raiz == null ? 0 : raiz.frec()) + ", el largo del texto.",
                "Árbol completo."));
        Map<Character, String> codigos = codigos(texto);
        List<String> listaCodigos = new ArrayList<>();
        codigos.forEach((c, cod) -> listaCodigos.add(visible(c) + "=" + cod));
        int bits = bitsHuffman(texto);
        pasos.add(new EstadoHuffman(raiz == null ? List.of() : List.of(raiz), null, null, raiz, codigos, Tipo.CODIGOS, 8,
                vars(null, null, raiz, List.of()),
                "códigos = {" + String.join(", ", listaCodigos) + "}",
                "Se recorre el árbol desde la raíz: cada paso a la izquierda agrega un 0 y a la derecha un 1; al llegar a"
                        + " una hoja, ese camino es su código. Ningún código es el comienzo de otro (código LIBRE DE"
                        + " PREFIJOS), porque los caracteres solo están en las hojas: así se puede decodificar sin"
                        + " separadores. El texto ocupa " + bits + " bits, frente a " + 8 * texto.length()
                        + " con ASCII y " + bitsFijos(texto) + " con un código fijo de "
                        + bitsFijos(texto) / Math.max(1, texto.length()) + " bits: ahorra "
                        + Math.round(100.0 * (8 * texto.length() - bits) / Math.max(1, 8 * texto.length()))
                        + " % respecto a ASCII.",
                "Códigos listos: " + bits + " bits."));
        return pasos;
    }

    private static List<Nodo> ordenada(PriorityQueue<Nodo> cola) {
        return cola.stream().sorted(PRIORIDAD).toList();
    }

    private static String describir(Nodo n) {
        return n.esHoja() ? "la hoja " + visible(n.caracter()) + " (" + n.frec() + ")"
                : "el árbol de " + n.caracteres() + " (" + n.frec() + ")";
    }

    private static String textoCola(List<Nodo> cola) {
        return "[" + String.join(", ", cola.stream().map(Nodo::etiqueta).toList()) + "]";
    }

    private static Map<String, String> vars(Nodo a, Nodo b, Nodo nuevo, List<Nodo> cola) {
        return PasoTrazable.variables(
                "a", a == null ? "—" : a.etiqueta() + (a.esHoja() ? "" : " (" + a.caracteres() + ")"),
                "b", b == null ? "—" : b.etiqueta() + (b.esHoja() ? "" : " (" + b.caracteres() + ")"),
                "cola", cola.isEmpty() ? "—" : textoCola(cola),
                "raiz", nuevo == null ? "—" : nuevo.etiqueta());
    }
}
