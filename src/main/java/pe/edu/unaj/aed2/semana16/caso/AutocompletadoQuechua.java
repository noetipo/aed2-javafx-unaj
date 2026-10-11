package pe.edu.unaj.aed2.semana16.caso;

import pe.edu.unaj.aed2.semana04.caso.DiccionarioQuechua;
import pe.edu.unaj.aed2.semana16.modelo.Trie;

import java.util.List;

/**
 * Caso práctico semana 16: autocompletar el diccionario quechua de la semana 04 mientras se escribe. Compara cuántos
 * pasos da el trie con cuántas palabras revisa una búsqueda lineal en la lista.
 */
public class AutocompletadoQuechua {

    /** Una sugerencia: palabra y significado. */
    public record Sugerencia(String palabra, String significado) {
    }

    private final Trie<String> trie = new Trie<>();
    private final List<DiccionarioQuechua.Entrada> entradas;
    private final int letras;

    public AutocompletadoQuechua(DiccionarioQuechua diccionario) {
        this.entradas = diccionario.entradas();
        entradas.forEach(e -> trie.insertar(e.palabra(), e.significado()));
        this.letras = entradas.stream().mapToInt(e -> e.palabra().length()).sum();
    }

    public static AutocompletadoQuechua cargar() {
        return new AutocompletadoQuechua(DiccionarioQuechua.desdeRecurso());
    }

    public Trie<String> trie() {
        return trie;
    }

    public List<Sugerencia> sugerencias(String prefijo) {
        return trie.autocompletar(prefijo).stream().map(p -> new Sugerencia(p, trie.valor(p))).toList();
    }

    /** Letras de todas las palabras: lo que ocuparía guardarlas por separado. */
    public int letras() {
        return letras;
    }

    /** Palabras que una búsqueda lineal tendría que revisar para encontrar todas las que empiezan con el prefijo. */
    public int revisionesLineales() {
        return entradas.size();
    }

    /** Nodos que visita el trie: bajar por el prefijo y luego recorrer el subárbol de las sugerencias. */
    public int pasosTrie(String prefijo) {
        int pasos = 0;
        StringBuilder p = new StringBuilder();
        for (char c : prefijo.toCharArray()) {
            p.append(c);
            if (!trie.prefijos(false).contains(p.toString())) {
                return pasos + 1;
            }
            pasos++;
        }
        String base = prefijo;
        return pasos + (int) trie.prefijos(false).stream().filter(x -> x.startsWith(base) && !x.equals(base)).count();
    }
}
