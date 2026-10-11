package pe.edu.unaj.aed2.semana16.tarea;

/**
 * TAREA SEMANA 16 — Tries.
 * <p>
 * Escribe tu propio trie dentro de esta clase (por ejemplo, una clase interna Nodo con un arreglo o un mapa de hijos).
 * Las palabras usan solo letras minúsculas de la 'a' a la 'z'.
 * <ul>
 *   <li>No uses la clase Trie del paquete modelo: el objetivo es construirlo tú.</li>
 *   <li>No cambies las firmas: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-16/tarea.md
 */
public final class TareaSemana16 {

    private TareaSemana16() {
    }

    /** El prefijo más largo que comparten TODAS las palabras ("" si no hay palabras o no comparten nada). */
    public static String prefijoComunMasLargo(String[] palabras) {
        // TODO 1: inserta todas en un trie y baja desde la raíz mientras el nodo tenga UN solo hijo y no sea fin de
        //         palabra.
        throw new UnsupportedOperationException("TODO: implementar prefijoComunMasLargo");
    }

    /**
     * Para cada prefijo, cuántas palabras de la lista empiezan con él (las repetidas cuentan cada vez). Debe ser
     * rápido: 100 000 palabras y 100 000 prefijos en menos de un par de segundos.
     */
    public static int[] contarConPrefijo(String[] palabras, String[] prefijos) {
        // TODO 2: guarda en cada nodo un contador de cuántas palabras pasan por él; responder es bajar por el prefijo.
        throw new UnsupportedOperationException("TODO: implementar contarConPrefijo");
    }

    /**
     * La palabra más larga que se puede "construir" de a una letra: todos sus prefijos (de largo 1 en adelante)
     * también están en la lista. Si empatan, la menor alfabéticamente; "" si ninguna.
     * Ejemplo: {"p", "pu", "pum", "puma", "puka"} → "puma" ("puka" no, porque falta "puk").
     */
    public static String palabraConstruible(String[] palabras) {
        // TODO 3: recorre el trie solo por nodos que son fin de palabra y quédate con el camino más profundo.
        throw new UnsupportedOperationException("TODO: implementar palabraConstruible");
    }
}
