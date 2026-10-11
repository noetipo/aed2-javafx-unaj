package pe.edu.unaj.aed2.semana13;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana13.tarea.TareaSemana13;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Pruebas de la tarea. No se ejecutan con "./mvnw test"; para correrlas:
 * ./mvnw test -Dgroups=tarea -DexcludedGroups=none
 */
@Tag("tarea")
class TareaSemana13Test {

    @Test
    void contarReinasConValoresConocidos() {
        int[] esperado = {1, 0, 0, 2, 10, 4, 40, 92};
        for (int n = 1; n <= 8; n++) {
            assertEquals(esperado[n - 1], TareaSemana13.contarReinas(n), "n = " + n);
        }
    }

    @Test
    void subconjuntoEnElEjemplo() {
        int[] sacos = {50, 30, 25, 20, 45};
        boolean[] e = TareaSemana13.subconjuntoConSuma(sacos, 95);
        assertNotNull(e);
        assertEquals(95, suma(sacos, e));
        assertNull(TareaSemana13.subconjuntoConSuma(sacos, 31));
        assertNotNull(TareaSemana13.subconjuntoConSuma(sacos, 0), "no llevar nada suma 0");
    }

    @Test
    void subconjuntoCoincideConFuerzaBruta() {
        Random r = new Random(13);
        for (int prueba = 0; prueba < 200; prueba++) {
            int n = r.nextInt(10);
            int[] pesos = new int[n];
            for (int i = 0; i < n; i++) {
                pesos[i] = 1 + r.nextInt(30);
            }
            int objetivo = r.nextInt(80);
            boolean posible = false;
            for (int m = 0; m < (1 << n) && !posible; m++) {
                int s = 0;
                for (int i = 0; i < n; i++) {
                    if ((m & (1 << i)) != 0) {
                        s += pesos[i];
                    }
                }
                posible = s == objetivo;
            }
            boolean[] e = TareaSemana13.subconjuntoConSuma(pesos, objetivo);
            String caso = Arrays.toString(pesos) + " → " + objetivo;
            if (posible) {
                assertNotNull(e, caso);
                assertEquals(n, e.length, caso);
                assertEquals(objetivo, suma(pesos, e), caso);
            } else {
                assertNull(e, caso);
            }
        }
    }

    @Test
    void contarCaminosEnMapasPequenios() {
        assertEquals(2, TareaSemana13.contarCaminos(mapa("S.", ".E")));
        assertEquals(12, TareaSemana13.contarCaminos(mapa("S..", "...", "..E")));
        assertEquals(1, TareaSemana13.contarCaminos(mapa("S#.", ".#.", "..E")), "solo rodeando por abajo");
        assertEquals(0, TareaSemana13.contarCaminos(mapa("S#E")));
        assertEquals(1, TareaSemana13.contarCaminos(mapa("SE")));
        assertEquals(7, TareaSemana13.contarCaminos(mapa("S...", ".#..", "...E")));
    }

    private static char[][] mapa(String... filas) {
        return Arrays.stream(filas).map(String::toCharArray).toArray(char[][]::new);
    }

    private static int suma(int[] pesos, boolean[] elegido) {
        int s = 0;
        for (int i = 0; i < pesos.length; i++) {
            if (elegido[i]) {
                s += pesos[i];
            }
        }
        return s;
    }
}
