package pe.edu.unaj.aed2.semana12;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana12.caso.CuencaLechera;
import pe.edu.unaj.aed2.semana12.modelo.EstadoFlujo;
import pe.edu.unaj.aed2.semana12.modelo.RedFlujo;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FlujoMaximoTest {

    private static final RedFlujo LECHE = RedFlujo.cargarLeche();

    @Test
    void flujoMaximoDeLaRedDeLeche() {
        RedFlujo.Resultado r = LECHE.edmondsKarp("AYA", "JUL");
        assertEquals(16, r.total());
        assertEquals(List.of("AYA>AZA", "LAM>PUC"), r.corte().stream().map(RedFlujo.Arco::clave).toList());
        verificarFlujoValido(LECHE, r, "AYA", "JUL");
    }

    @Test
    void elPasoAPasoUsaArcosAlRevesYTerminaIgual() {
        List<EstadoFlujo> pasos = LECHE.pasos("AYA", "JUL", Map.of());
        assertEquals(2, pasos.stream().filter(p -> p.tipo() == EstadoFlujo.Tipo.CAMINO && !p.haciaAtras().isEmpty())
                .count(), "dos caminos devuelven flujo por Azángaro → Pucará");
        EstadoFlujo fin = pasos.get(pasos.size() - 1);
        assertEquals(16, fin.total());
        assertEquals(LECHE.edmondsKarp("AYA", "JUL").flujo(), fin.flujo());
        // El total nunca baja y BFS halla caminos cada vez igual o más largos (propiedad de Edmonds–Karp).
        int largo = 0;
        for (EstadoFlujo p : pasos) {
            if (p.tipo() == EstadoFlujo.Tipo.CAMINO) {
                assertTrue(p.camino().size() >= largo);
                largo = p.camino().size();
            }
        }
    }

    @Test
    void flujoIgualAlCorteMinimoEnRedesAleatorias() {
        Random azar = new Random(12);
        for (int prueba = 0; prueba < 150; prueba++) {
            int n = 2 + azar.nextInt(6);
            RedFlujo red = new RedFlujo();
            for (int i = 0; i < n; i++) {
                red.agregarVertice("v" + i);
            }
            for (int k = azar.nextInt(3 * n); k > 0; k--) {
                int a = azar.nextInt(n);
                int b = azar.nextInt(n);
                try {
                    red.agregarArco("v" + a, "v" + b, azar.nextInt(10));
                } catch (IllegalArgumentException repetido) {
                    // arco inválido o ya existente: se ignora
                }
            }
            RedFlujo.Resultado r = red.edmondsKarp("v0", "v" + (n - 1));
            verificarFlujoValido(red, r, "v0", "v" + (n - 1));
            assertEquals(fuerzaBrutaCorte(red, n), r.total(), "flujo máximo = corte mínimo (por fuerza bruta)");
        }
    }

    @Test
    void elCasoSoloMejoraAmpliandoUnTramoDelCorte() {
        CuencaLechera c = new CuencaLechera(LECHE);
        Map<String, Integer> p = Map.of("AYA", 14, "AZA", 8, "PUT", 5);
        CuencaLechera.Envio e = c.enviar(p);
        assertEquals(27, e.producido());
        assertEquals(17, e.entregado());
        List<CuencaLechera.Ampliacion> a = c.ampliaciones(p, 3);
        assertEquals("PUC>JUL", a.get(0).tramo().clave());
        assertEquals(3, a.get(0).ganancia());
        assertTrue(a.stream().skip(1).allMatch(x -> x.ganancia() == 0));
        // Sin producción no llega nada; con muy poca, llega todo.
        assertEquals(0, c.enviar(Map.of()).entregado());
        assertEquals(5, c.enviar(Map.of("AYA", 5)).entregado());
    }

    private static void verificarFlujoValido(RedFlujo red, RedFlujo.Resultado r, String s, String t) {
        for (RedFlujo.Arco a : red.arcos()) {
            int f = r.flujo().get(a.clave());
            assertTrue(f >= 0 && f <= a.capacidad(), "capacidad en " + a);
        }
        for (String v : red.vertices()) {
            if (v.equals(s) || v.equals(t)) {
                continue;
            }
            int entra = red.arcos().stream().filter(a -> a.v().equals(v)).mapToInt(a -> r.flujo().get(a.clave())).sum();
            int sale = red.arcos().stream().filter(a -> a.u().equals(v)).mapToInt(a -> r.flujo().get(a.clave())).sum();
            assertEquals(entra, sale, "conservación en " + v);
        }
        int sale = red.arcos().stream().filter(a -> a.u().equals(s)).mapToInt(a -> r.flujo().get(a.clave())).sum()
                - red.arcos().stream().filter(a -> a.v().equals(s)).mapToInt(a -> r.flujo().get(a.clave())).sum();
        assertEquals(r.total(), sale);
        assertTrue(r.ladoS().contains(s));
        assertFalse(r.ladoS().contains(t));
        assertEquals(r.total(), r.corte().stream().mapToInt(RedFlujo.Arco::capacidad).sum());
    }

    /** El menor corte entre todos los subconjuntos que contienen a v0 y no a v(n−1). */
    private static int fuerzaBrutaCorte(RedFlujo red, int n) {
        int mejor = Integer.MAX_VALUE;
        for (int mascara = 0; mascara < (1 << n); mascara++) {
            if ((mascara & 1) == 0 || (mascara & (1 << (n - 1))) != 0) {
                continue;
            }
            int m = mascara;
            int corte = red.arcos().stream().filter(a -> dentro(m, a.u()) && !dentro(m, a.v()))
                    .mapToInt(RedFlujo.Arco::capacidad).sum();
            mejor = Math.min(mejor, corte);
        }
        return mejor;
    }

    private static boolean dentro(int mascara, String v) {
        return (mascara & (1 << Integer.parseInt(v.substring(1)))) != 0;
    }
}
