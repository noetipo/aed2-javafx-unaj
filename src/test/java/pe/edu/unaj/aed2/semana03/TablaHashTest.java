package pe.edu.unaj.aed2.semana03;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana03.caso.PadronMototaxis;
import pe.edu.unaj.aed2.semana03.modelo.EstadoTabla;
import pe.edu.unaj.aed2.semana03.modelo.TablaHash;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TablaHashTest {

    private static final String[] EJEMPLO = {"JUL-102", "JUL-215", "PUN-031", "JUL-347", "CAR-008", "JUL-520"};

    @Test
    void hashDeHornerCoincideConElCalculoManual() {
        // "AB": h = (0·31 + 65) mod 7 = 2;  h = (2·31 + 66) mod 7 = 128 mod 7 = 2
        assertEquals(2, TablaHash.hash("AB", 7));
        // Sin mod intermedio: 65·31 + 66 = 2081; 2081 mod 7 = 2 (Horner da lo mismo)
        assertEquals(2081 % 7, TablaHash.hash("AB", 7));
        // Horner con mod en cada paso = el polinomio completo Σ c·31^k mod m, calculado sin desborde.
        for (String c : EJEMPLO) {
            BigInteger polinomio = BigInteger.ZERO;
            for (char ch : c.toCharArray()) {
                polinomio = polinomio.multiply(BigInteger.valueOf(31)).add(BigInteger.valueOf(ch));
            }
            for (int m : new int[]{7, 11, 15}) {
                assertEquals(polinomio.mod(BigInteger.valueOf(m)).intValue(), TablaHash.hash(c, m), c);
            }
        }
    }

    @Test
    void insertarBuscarYDuplicados() {
        TablaHash<Integer> t = new TablaHash<>(7);
        for (int k = 0; k < EJEMPLO.length; k++) {
            assertTrue(t.insertar(EJEMPLO[k], k));
        }
        assertFalse(t.insertar("JUL-347", 99));
        assertEquals(99, t.buscar("JUL-347"));
        assertEquals(EJEMPLO.length, t.tamanio());
        assertNull(t.buscar("PUN-999"));
        assertFalse(t.contiene("PUN-999"));
    }

    @Test
    void rehashCuandoElFactorDeCargaSuperaElLimite() {
        TablaHash<String> t = new TablaHash<>(7);
        for (String c : EJEMPLO) {
            t.insertar(c, c);
            assertTrue(t.factorCarga() <= TablaHash.CARGA_MAXIMA);
        }
        assertEquals(15, t.capacidad()); // 6 / 7 = 0.86 > 0.75  →  2·7 + 1 = 15
        for (String c : EJEMPLO) {
            assertTrue(t.contiene(c), c);
            assertTrue(t.claves().get(TablaHash.hash(c, 15)).contains(c));
        }
    }

    @Test
    void lasOperacionesPasoAPasoDejanLaMismaTablaQueLasNormales() {
        TablaHash<String> normal = new TablaHash<>(7);
        TablaHash<String> animada = new TablaHash<>(7);
        for (String c : EJEMPLO) {
            normal.insertar(c, c);
            List<EstadoTabla> pasos = animada.pasosInsertar(c, c);
            assertEquals(EstadoTabla.Tipo.FIN, pasos.get(pasos.size() - 1).tipo());
            assertEquals(normal.claves(), pasos.get(pasos.size() - 1).cubetas());
        }
        assertEquals(normal.claves(), animada.claves());
        assertTrue(animada.pasosInsertar("JUL-215", "x").stream()
                .anyMatch(p -> p.tipo() == EstadoTabla.Tipo.YA_EXISTE));
        assertTrue(animada.pasosInsertar("PUN-777", "x").size() > 3);
    }

    @Test
    void elPasoDeRehashMuestraLaTablaAgrandada() {
        TablaHash<String> t = new TablaHash<>(7);
        boolean huboRehash = false;
        for (String c : EJEMPLO) {
            for (EstadoTabla p : t.pasosInsertar(c, c)) {
                if (p.tipo() == EstadoTabla.Tipo.REHASH) {
                    huboRehash = true;
                    assertEquals(15, p.capacidad());
                }
            }
        }
        assertTrue(huboRehash);
    }

    @Test
    void buscarPasoAPaso() {
        TablaHash<String> t = new TablaHash<>(7);
        for (String c : EJEMPLO) {
            t.insertar(c, c);
        }
        List<EstadoTabla> encontrada = t.pasosBuscar("JUL-347");
        assertTrue(encontrada.stream().anyMatch(p -> p.tipo() == EstadoTabla.Tipo.ENCONTRADA));
        List<EstadoTabla> ausente = t.pasosBuscar("PUN-999");
        assertEquals(EstadoTabla.Tipo.NO_ENCONTRADA, ausente.get(ausente.size() - 1).tipo());
        long comparaciones = encontrada.stream()
                .filter(p -> p.tipo() == EstadoTabla.Tipo.COMPARACION || p.tipo() == EstadoTabla.Tipo.ENCONTRADA)
                .count();
        assertEquals(t.comparaciones("JUL-347"), comparaciones);
    }

    @Test
    void padronUsaLaTablaHashYGanaALaBusquedaLineal() {
        PadronMototaxis p = PadronMototaxis.desdeRecurso();
        assertEquals(120, p.mototaxis().size());
        Set<String> codigos = new HashSet<>();
        for (PadronMototaxis.Mototaxi m : p.mototaxis()) {
            assertTrue(codigos.add(m.codigo()), "código repetido " + m.codigo());
            assertEquals(m, p.buscar(m.codigo()));
        }
        assertNull(p.buscar("XXX-000"));
        double[] promedios = p.promedios();
        assertTrue(promedios[0] < 2, "hash: " + promedios[0]);
        assertTrue(promedios[1] > 50, "lineal: " + promedios[1]);
        assertTrue(p.factorCarga() <= TablaHash.CARGA_MAXIMA);
    }
}
