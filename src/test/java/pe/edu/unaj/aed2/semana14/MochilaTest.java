package pe.edu.unaj.aed2.semana14;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana14.caso.PresupuestoParticipativo;
import pe.edu.unaj.aed2.semana14.modelo.EstadoMochila;
import pe.edu.unaj.aed2.semana14.modelo.Mochila;
import pe.edu.unaj.aed2.semana14.modelo.Mochila.Producto;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MochilaTest {

    @Test
    void laFeria() {
        Mochila.Resultado r = Mochila.resolver(Mochila.FERIA, Mochila.CAPACIDAD_FERIA);
        assertEquals(93, r.mejor());
        assertEquals(List.of(2, 3), r.elegidos(), "quinua y chuño");
        assertEquals(90, Mochila.voraz(Mochila.FERIA, Mochila.CAPACIDAD_FERIA).mejor(), "la regla voraz falla");
    }

    @Test
    void coincideConFuerzaBruta() {
        Random azar = new Random(14);
        for (int prueba = 0; prueba < 200; prueba++) {
            int n = azar.nextInt(9);
            List<Producto> productos = new ArrayList<>();
            for (int k = 0; k < n; k++) {
                productos.add(new Producto("p" + k, 1 + azar.nextInt(8), azar.nextInt(50)));
            }
            int capacidad = azar.nextInt(20);
            int mejor = 0;
            for (int m = 0; m < (1 << n); m++) {
                int peso = 0;
                int valor = 0;
                for (int k = 0; k < n; k++) {
                    if ((m & (1 << k)) != 0) {
                        peso += productos.get(k).peso();
                        valor += productos.get(k).valor();
                    }
                }
                if (peso <= capacidad) {
                    mejor = Math.max(mejor, valor);
                }
            }
            Mochila.Resultado r = Mochila.resolver(productos, capacidad);
            assertEquals(mejor, r.mejor());
            assertEquals(mejor, Mochila.valor(productos, r.elegidos()), "la reconstrucción suma lo mismo");
            assertTrue(Mochila.peso(productos, r.elegidos()) <= capacidad);
            assertTrue(Mochila.voraz(productos, capacidad).mejor() <= mejor);
        }
    }

    @Test
    void elPasoAPasoLlenaLaMismaTabla() {
        List<EstadoMochila> pasos = Mochila.pasos(Mochila.FERIA, Mochila.CAPACIDAD_FERIA);
        int[][] tabla = Mochila.tablaCompleta(Mochila.FERIA, Mochila.CAPACIDAD_FERIA);
        EstadoMochila fin = pasos.get(pasos.size() - 1);
        for (int i = 0; i < tabla.length; i++) {
            for (int c = 0; c < tabla[i].length; c++) {
                assertEquals(tabla[i][c], fin.dp().get(i).get(c));
            }
        }
        assertEquals(java.util.Set.of(2, 3), fin.elegidos());
        assertEquals(5 * 11, pasos.stream().filter(p -> p.linea() >= 5 && p.linea() <= 7).count(), "una por casilla");
    }

    @Test
    void elPresupuestoParticipativo() {
        PresupuestoParticipativo p = PresupuestoParticipativo.cargar();
        assertEquals(7400, p.optimo(250).beneficiarios());
        assertEquals(7350, p.vorazPorSol(250).beneficiarios());
        assertEquals(6450, p.vorazPorBeneficiarios(250).beneficiarios());
        for (int m = 50; m <= 600; m += 10) {
            int optimo = p.optimo(m).beneficiarios();
            assertTrue(p.optimo(m).costo() <= m);
            assertTrue(p.vorazPorSol(m).beneficiarios() <= optimo);
            assertTrue(p.vorazPorBeneficiarios(m).beneficiarios() <= optimo);
        }
    }
}
