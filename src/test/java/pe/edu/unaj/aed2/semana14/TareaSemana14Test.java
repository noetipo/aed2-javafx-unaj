package pe.edu.unaj.aed2.semana14;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana14.tarea.TareaSemana14;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

/**
 * Pruebas de la tarea. No se ejecutan con "./mvnw test"; para correrlas:
 * ./mvnw test -Dgroups=tarea -DexcludedGroups=none
 */
@Tag("tarea")
class TareaSemana14Test {

    @Test
    void cambioDeMonedasEnEjemplos() {
        assertEquals(2, TareaSemana14.cambioMonedas(new int[]{1, 3, 4}, 6), "3 + 3 (la regla voraz daría 3 monedas)");
        assertEquals(0, TareaSemana14.cambioMonedas(new int[]{1, 2}, 0));
        assertEquals(-1, TareaSemana14.cambioMonedas(new int[]{5, 10}, 3));
        assertEquals(3, TareaSemana14.cambioMonedas(new int[]{1, 2, 5}, 11), "5 + 5 + 1");
        assertEquals(20, TareaSemana14.cambioMonedas(new int[]{1, 2, 5}, 100));
    }

    @Test
    void cambioDeMonedasEsRapido() {
        assertTimeoutPreemptively(java.time.Duration.ofSeconds(2),
                () -> assertEquals(1000, TareaSemana14.cambioMonedas(new int[]{3, 7, 10}, 10000)));
    }

    @Test
    void cambioCoincideConFuerzaBruta() {
        Random r = new Random(14);
        for (int prueba = 0; prueba < 200; prueba++) {
            int[] monedas = r.ints(1 + r.nextInt(3), 1, 9).toArray();
            int monto = r.nextInt(25);
            assertEquals(bruta(monedas, monto, 0), TareaSemana14.cambioMonedas(monedas, monto),
                    Arrays.toString(monedas) + " → " + monto);
        }
    }

    @Test
    void subsecuenciaComunEnEjemplos() {
        assertEquals(4, TareaSemana14.subsecuenciaComun("ABCBDAB", "BDCABA"), "BCBA");
        assertEquals(0, TareaSemana14.subsecuenciaComun("", "puno"));
        assertEquals(4, TareaSemana14.subsecuenciaComun("wasi", "wasikuna"));
        assertEquals(3, TareaSemana14.subsecuenciaComun("juliaca", "lampa"), "l, a, a");
    }

    @Test
    void subsecuenciaCoincideConFuerzaBruta() {
        Random r = new Random(14);
        for (int prueba = 0; prueba < 200; prueba++) {
            String a = aleatoria(r, r.nextInt(9));
            String b = aleatoria(r, r.nextInt(9));
            int esperado = 0;
            for (int m = 0; m < (1 << a.length()); m++) {
                StringBuilder s = new StringBuilder();
                for (int i = 0; i < a.length(); i++) {
                    if ((m & (1 << i)) != 0) {
                        s.append(a.charAt(i));
                    }
                }
                if (esSubsecuencia(s.toString(), b)) {
                    esperado = Math.max(esperado, s.length());
                }
            }
            assertEquals(esperado, TareaSemana14.subsecuenciaComun(a, b), a + " / " + b);
        }
    }

    @Test
    void distanciaDeEdicionEnEjemplos() {
        assertEquals(3, TareaSemana14.distanciaEdicion("kitten", "sitting"));
        assertEquals(0, TareaSemana14.distanciaEdicion("puno", "puno"));
        assertEquals(3, TareaSemana14.distanciaEdicion("", "abc"));
        assertEquals(4, TareaSemana14.distanciaEdicion("inti", "intikuna"));
        assertEquals(1, TareaSemana14.distanciaEdicion("killa", "qilla"));
        assertEquals(1, TareaSemana14.distanciaEdicion("chaska", "chasca"));
    }

    @Test
    void distanciaCumpleSusPropiedades() {
        Random r = new Random(14);
        for (int prueba = 0; prueba < 200; prueba++) {
            String a = aleatoria(r, r.nextInt(8));
            String b = aleatoria(r, r.nextInt(8));
            String c = aleatoria(r, r.nextInt(8));
            int ab = TareaSemana14.distanciaEdicion(a, b);
            assertEquals(ab, TareaSemana14.distanciaEdicion(b, a), "simétrica");
            assertEquals(true, ab >= Math.abs(a.length() - b.length()));
            assertEquals(true, ab <= Math.max(a.length(), b.length()));
            assertEquals(true, TareaSemana14.distanciaEdicion(a, c) <= ab + TareaSemana14.distanciaEdicion(b, c),
                    "desigualdad triangular");
            // Borrar todo lo que no está en la subsecuencia común y luego insertar: nunca más que eso.
            int lcs = TareaSemana14.subsecuenciaComun(a, b);
            assertEquals(true, ab <= a.length() + b.length() - 2 * lcs);
        }
    }

    // ------------------------------------------------------------------

    private static int bruta(int[] monedas, int resto, int desde) {
        if (resto == 0) {
            return 0;
        }
        int mejor = -1;
        for (int k = desde; k < monedas.length; k++) {
            if (monedas[k] <= resto) {
                int r = bruta(monedas, resto - monedas[k], k);
                if (r >= 0 && (mejor < 0 || r + 1 < mejor)) {
                    mejor = r + 1;
                }
            }
        }
        return mejor;
    }

    private static String aleatoria(Random r, int largo) {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < largo; i++) {
            s.append("aiuqkl".charAt(r.nextInt(6)));
        }
        return s.toString();
    }

    private static boolean esSubsecuencia(String s, String t) {
        int j = 0;
        for (int i = 0; i < t.length() && j < s.length(); i++) {
            if (t.charAt(i) == s.charAt(j)) {
                j++;
            }
        }
        return j == s.length();
    }
}
