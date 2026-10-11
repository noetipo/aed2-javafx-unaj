package pe.edu.unaj.aed2.semana13;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana13.caso.HorarioExamenes;
import pe.edu.unaj.aed2.semana13.modelo.EstadoReinas;
import pe.edu.unaj.aed2.semana13.modelo.NReinas;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BacktrackingTest {

    @Test
    void lasSolucionesSonValidas() {
        assertArrayEquals(new int[]{0, 4, 7, 5, 2, 6, 1, 3}, NReinas.resolver(8));
        for (int n = 1; n <= 9; n++) {
            int[] t = NReinas.resolver(n);
            if (n == 2 || n == 3) {
                assertNull(t, "n = " + n + " no tiene solución");
                continue;
            }
            for (int f = 0; f < n; f++) {
                assertEquals(-1, NReinas.atacante(t, f, t[f]), "n = " + n + ", fila " + f);
            }
        }
    }

    @Test
    void elPasoAPasoLlegaALaMismaSolucion() {
        for (int n = 4; n <= 8; n++) {
            List<EstadoReinas> pasos = NReinas.pasos(n);
            EstadoReinas fin = pasos.get(pasos.size() - 1);
            assertEquals(EstadoReinas.Tipo.SOLUCION, fin.tipo());
            int[] t = fin.tablero().stream().mapToInt(Integer::intValue).toArray();
            assertArrayEquals(NReinas.resolver(n), t);
            // Cada reina que se coloca se quita después o queda en la solución.
            long colocadas = pasos.stream().filter(p -> p.tipo() == EstadoReinas.Tipo.COLOCA).count();
            long quitadas = pasos.stream().filter(p -> p.tipo() == EstadoReinas.Tipo.QUITA).count();
            assertEquals(n, colocadas - quitadas);
            // En ningún momento hay dos reinas que se ataquen.
            for (EstadoReinas p : pasos) {
                int[] tab = p.tablero().stream().mapToInt(Integer::intValue).toArray();
                for (int f = 0; f < n; f++) {
                    if (tab[f] >= 0) {
                        assertEquals(-1, NReinas.atacante(tab, f, tab[f]));
                    }
                }
            }
        }
        List<EstadoReinas> sinSolucion = NReinas.pasos(3);
        assertEquals(EstadoReinas.Tipo.RETROCEDE, sinSolucion.get(sinSolucion.size() - 1).tipo());
        assertEquals(0, sinSolucion.get(sinSolucion.size() - 1).fila());
    }

    @Test
    void elHorarioDeExamenes() {
        HorarioExamenes h = HorarioExamenes.cargar();
        assertEquals(10, h.ids().size());
        int[] voraz = h.voraz();
        assertTrue(valido(h, voraz));
        assertEquals(5, java.util.Arrays.stream(voraz).max().orElse(-1) + 1, "la regla voraz usa 5 días");
        assertEquals(3, h.minimoDias());
        assertFalse(h.backtracking(2).encontrado());
        HorarioExamenes.Resultado r = h.backtracking(3);
        assertTrue(r.encontrado());
        assertTrue(valido(h, r.dias()));
        assertEquals(3, r.diasUsados());
    }

    private static boolean valido(HorarioExamenes h, int[] dias) {
        List<String> ids = h.ids();
        for (int a = 0; a < ids.size(); a++) {
            for (int b = a + 1; b < ids.size(); b++) {
                if (h.conflicto(ids.get(a), ids.get(b)) && dias[a] == dias[b]) {
                    return false;
                }
            }
        }
        return true;
    }
}
