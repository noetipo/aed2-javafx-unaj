package pe.edu.unaj.aed2.semana16;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana16.caso.AutocompletadoQuechua;
import pe.edu.unaj.aed2.semana16.modelo.EstadoTrie;
import pe.edu.unaj.aed2.semana16.modelo.Trie;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrieTest {

    @Test
    void autocompletarElEjemplo() {
        Trie<Void> t = new Trie<>();
        Trie.EJEMPLO.forEach(t::insertar);
        assertEquals(List.of("puka", "puma", "puriy", "puñuy"), t.autocompletar("pu"));
        assertEquals(List.of("pacha", "para"), t.autocompletar("pa"));
        assertEquals(List.of(), t.autocompletar("pi"));
        assertTrue(t.contiene("puma"));
        assertFalse(t.contiene("pum"), "\"pum\" es solo un prefijo");
        assertEquals(6, t.palabras());
    }

    @Test
    void coincideConUnConjuntoOrdenado() {
        Random r = new Random(16);
        for (int prueba = 0; prueba < 100; prueba++) {
            Trie<Void> t = new Trie<>();
            TreeSet<String> conjunto = new TreeSet<>();
            int raiz = 1;
            for (int k = 0; k < 30; k++) {
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < 1 + r.nextInt(5); i++) {
                    s.append("aeiku".charAt(r.nextInt(5)));
                }
                t.insertar(s.toString());
                conjunto.add(s.toString());
            }
            for (String p : List.of("", "a", "ak", "ku", "zz", "ee")) {
                List<String> esperado = new ArrayList<>();
                for (String w : conjunto) {
                    if (w.startsWith(p)) {
                        esperado.add(w);
                    }
                }
                assertEquals(esperado, t.autocompletar(p), p);
            }
            assertEquals(conjunto.size(), t.palabras());
            // Cada nodo (menos la raíz) es un prefijo distinto de alguna palabra.
            TreeSet<String> prefijos = new TreeSet<>();
            conjunto.forEach(w -> {
                for (int k = 1; k <= w.length(); k++) {
                    prefijos.add(w.substring(0, k));
                }
            });
            assertEquals(prefijos.size() + raiz, t.nodos());
        }
    }

    @Test
    void losPasosConstruyenElMismoTrie() {
        List<EstadoTrie> pasos = Trie.pasosInsertar(Trie.EJEMPLO);
        Trie<Void> t = new Trie<>();
        Trie.EJEMPLO.forEach(t::insertar);
        EstadoTrie fin = pasos.get(pasos.size() - 1);
        assertEquals(t.prefijos(false), fin.nodos());
        assertEquals(t.prefijos(true), fin.finales());
        assertEquals(t.nodos() - 1, pasos.stream().filter(p -> p.tipo() == EstadoTrie.Tipo.CREA).count());
        List<EstadoTrie> busqueda = Trie.pasosAutocompletar(Trie.EJEMPLO, "pu");
        assertEquals(List.of("puka", "puma", "puriy", "puñuy"), busqueda.get(busqueda.size() - 1).resultados());
    }

    @Test
    void elDiccionarioQuechua() {
        AutocompletadoQuechua a = AutocompletadoQuechua.cargar();
        assertEquals(38, a.trie().palabras());
        assertEquals(List.of("puka", "puma", "puriy", "puñuy"), a.sugerencias("pu").stream()
                .map(AutocompletadoQuechua.Sugerencia::palabra).toList());
        assertEquals("perro", a.sugerencias("allqu").get(0).significado());
        assertTrue(a.trie().nodos() - 1 < a.letras(), "los prefijos comunes se guardan una vez");
        assertTrue(a.pasosTrie("pu") < a.revisionesLineales());
    }
}
