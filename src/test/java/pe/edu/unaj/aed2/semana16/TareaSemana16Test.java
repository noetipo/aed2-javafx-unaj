package pe.edu.unaj.aed2.semana16;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana16.tarea.TareaSemana16;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

/**
 * Pruebas de la tarea. No se ejecutan con "./mvnw test"; para correrlas:
 * ./mvnw test -Dgroups=tarea -DexcludedGroups=none
 */
@Tag("tarea")
class TareaSemana16Test {

    @Test
    void prefijoComunEnEjemplos() {
        assertEquals("pu", TareaSemana16.prefijoComunMasLargo(new String[]{"puma", "puka", "puriy"}));
        assertEquals("", TareaSemana16.prefijoComunMasLargo(new String[]{"puma", "inti"}));
        assertEquals("wasi", TareaSemana16.prefijoComunMasLargo(new String[]{"wasi", "wasikuna"}), "una es prefijo");
        assertEquals("killa", TareaSemana16.prefijoComunMasLargo(new String[]{"killa"}));
        assertEquals("", TareaSemana16.prefijoComunMasLargo(new String[0]));
    }

    @Test
    void contarConPrefijoEnEjemplos() {
        String[] palabras = {"puma", "puka", "puriy", "pacha", "para", "puma"};
        assertArrayEquals(new int[]{6, 4, 2, 0, 2, 6}, TareaSemana16.contarConPrefijo(palabras,
                new String[]{"p", "pu", "puma", "x", "pa", ""}));
    }

    @Test
    void contarConPrefijoEsRapido() {
        Random r = new Random(16);
        String[] palabras = new String[100_000];
        String[] prefijos = new String[100_000];
        for (int k = 0; k < palabras.length; k++) {
            palabras[k] = aleatoria(r, 5 + r.nextInt(10));
            prefijos[k] = aleatoria(r, 1 + r.nextInt(3));
        }
        int[] resultado = assertTimeoutPreemptively(Duration.ofSeconds(3),
                () -> TareaSemana16.contarConPrefijo(palabras, prefijos));
        for (int k = 0; k < 50; k++) {
            String p = prefijos[k];
            assertEquals(Arrays.stream(palabras).filter(w -> w.startsWith(p)).count(), resultado[k], p);
        }
    }

    @Test
    void palabraConstruibleEnEjemplos() {
        assertEquals("puma", TareaSemana16.palabraConstruible(new String[]{"p", "pu", "pum", "puma", "puka"}));
        assertEquals("", TareaSemana16.palabraConstruible(new String[]{"puma", "inti"}), "falta \"p\" y \"i\"");
        assertEquals("ab", TareaSemana16.palabraConstruible(new String[]{"a", "b", "ab", "ba"}), "empate: la menor");
    }

    @Test
    void coincidenConFuerzaBruta() {
        Random r = new Random(16);
        for (int prueba = 0; prueba < 200; prueba++) {
            int n = r.nextInt(12);
            String[] palabras = new String[n];
            for (int k = 0; k < n; k++) {
                palabras[k] = aleatoriaChica(r, 1 + r.nextInt(4));
            }
            String caso = Arrays.toString(palabras);
            assertEquals(prefijoComun(palabras), TareaSemana16.prefijoComunMasLargo(palabras), caso);
            assertEquals(construible(palabras), TareaSemana16.palabraConstruible(palabras), caso);
        }
    }

    // ------------------------------------------------------------------

    private static String aleatoria(Random r, int largo) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < largo; i++) {
            s.append((char) ('a' + r.nextInt(26)));
        }
        return s.toString();
    }

    private static String aleatoriaChica(Random r, int largo) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < largo; i++) {
            s.append((char) ('a' + r.nextInt(3)));
        }
        return s.toString();
    }

    private static String prefijoComun(String[] palabras) {
        if (palabras.length == 0) {
            return "";
        }
        String p = palabras[0];
        for (String w : palabras) {
            while (!w.startsWith(p)) {
                p = p.substring(0, p.length() - 1);
            }
        }
        return p;
    }

    private static String construible(String[] palabras) {
        Set<String> conjunto = new HashSet<>(Arrays.asList(palabras));
        String mejor = "";
        for (String w : conjunto) {
            boolean ok = true;
            for (int k = 1; k <= w.length() && ok; k++) {
                ok = conjunto.contains(w.substring(0, k));
            }
            if (ok && (w.length() > mejor.length() || (w.length() == mejor.length() && w.compareTo(mejor) < 0))) {
                mejor = w;
            }
        }
        return mejor;
    }
}
