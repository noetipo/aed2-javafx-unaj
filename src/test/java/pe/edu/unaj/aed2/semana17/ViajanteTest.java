package pe.edu.unaj.aed2.semana17;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana17.caso.RutaDron;
import pe.edu.unaj.aed2.semana17.modelo.EstadoViajante;
import pe.edu.unaj.aed2.semana17.modelo.Viajante;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViajanteTest {

    private static final RedVial RED = RedVial.cargar();

    @Test
    void elEjemplo() {
        Viajante v = new Viajante(RED, Viajante.EJEMPLO);
        assertEquals(392, v.fuerzaBruta().km());
        assertEquals(5040, v.fuerzaBruta().evaluadas(), "7! rutas");
        assertEquals(464, v.vecinoMasCercano().km());
        assertEquals(392, v.dosOpt().km(), "aquí 2-opt llega al óptimo");
    }

    @Test
    void dosOptNoSiempreLlegaAlOptimo() {
        Viajante v = new Viajante(RED, List.of("JUL", "YUN", "PUC", "JLI", "ILA", "CHU", "LAM", "AYA"));
        assertEquals(468, v.vecinoMasCercano().km());
        assertEquals(460, v.dosOpt().km());
        assertEquals(458, v.fuerzaBruta().km(), "óptimo local ≠ óptimo global");
    }

    @Test
    void relacionesEntreMetodosEnInstanciasAleatorias() {
        Random r = new Random(17);
        List<String> todos = new ArrayList<>(RED.pueblos().keySet());
        todos.remove("JUL");
        for (int prueba = 0; prueba < 40; prueba++) {
            java.util.Collections.shuffle(todos, r);
            List<String> s = new ArrayList<>(List.of("JUL"));
            s.addAll(todos.subList(0, 1 + r.nextInt(7)));
            Viajante v = new Viajante(RED, s);
            Viajante.Ruta exacta = v.fuerzaBruta();
            Viajante.Ruta vecino = v.vecinoMasCercano();
            Viajante.Ruta opt = v.dosOpt();
            assertTrue(exacta.km() <= opt.km() && opt.km() <= vecino.km(), s.toString());
            for (Viajante.Ruta ruta : List.of(exacta, vecino, opt)) {
                assertEquals("JUL", ruta.orden().get(0));
                assertEquals(new HashSet<>(s), new HashSet<>(ruta.orden()));
            }
            assertEquals(Viajante.factorial(s.size() - 1), exacta.evaluadas());
        }
    }

    @Test
    void losPasosTerminanEnLasMismasRutas() {
        Viajante v = new Viajante(RED, Viajante.EJEMPLO);
        List<EstadoViajante> vecino = v.pasosVecino(Map.of());
        EstadoViajante fin = vecino.get(vecino.size() - 1);
        assertEquals(v.vecinoMasCercano().orden(), fin.ruta());
        assertEquals(464, fin.km());
        List<EstadoViajante> opt = v.pasosDosOpt(Map.of());
        assertEquals(392, opt.get(opt.size() - 1).km());
        // Cada mejora de 2-opt acorta la ruta.
        int anterior = Integer.MAX_VALUE;
        for (EstadoViajante p : opt) {
            if (p.tipo() == EstadoViajante.Tipo.MEJORA) {
                assertTrue(p.km() < anterior);
                anterior = p.km();
            }
        }
    }

    @Test
    void elCasoNoCorreLaFuerzaBrutaConMuchasPostas() {
        RutaDron dron = new RutaDron(RED);
        List<RutaDron.Resultado> pocos = dron.comparar(List.of("JUL", "PUN", "LAM", "AZA"));
        assertEquals(3, pocos.size());
        assertTrue(pocos.get(0).ruta() != null);
        List<RutaDron.Resultado> muchos = dron.comparar(new ArrayList<>(RED.pueblos().keySet()));
        assertNull(muchos.get(0).ruta(), "14 pueblos: 13! rutas, demasiadas");
        assertTrue(muchos.get(2).ruta().km() <= muchos.get(1).ruta().km());
        assertEquals(6227020800.0, RutaDron.rutas(14), 0.5);
    }
}
