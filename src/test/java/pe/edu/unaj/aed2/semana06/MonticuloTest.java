package pe.edu.unaj.aed2.semana06;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana06.caso.TriajeEmergencias;
import pe.edu.unaj.aed2.semana06.caso.TriajeEmergencias.Atencion;
import pe.edu.unaj.aed2.semana06.modelo.EstadoMonticulo;
import pe.edu.unaj.aed2.semana06.modelo.Heapsort;
import pe.edu.unaj.aed2.semana06.modelo.MonticuloMin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MonticuloTest {

    private static boolean esMonticuloMin(List<Integer> a) {
        for (int k = 1; k < a.size(); k++) {
            if (a.get((k - 1) / 2) > a.get(k)) {
                return false;
            }
        }
        return true;
    }

    @Test
    void insertarYExtraerComoPriorityQueue() {
        Random r = new Random(6);
        MonticuloMin<Integer> m = new MonticuloMin<>();
        PriorityQueue<Integer> referencia = new PriorityQueue<>();
        for (int k = 0; k < 3000; k++) {
            if (referencia.isEmpty() || r.nextInt(3) > 0) {
                int x = r.nextInt(500);
                m.insertar(x);
                referencia.add(x);
            } else {
                assertEquals(referencia.poll(), m.extraerMin());
            }
            assertTrue(esMonticuloMin(m.arreglo()));
        }
        assertEquals(referencia.size(), m.tamanio());
        assertThrows(RuntimeException.class, () -> new MonticuloMin<Integer>().extraerMin());
    }

    @Test
    void ejemploDelVisualizador() {
        MonticuloMin<Integer> m = new MonticuloMin<>();
        for (int x : new int[]{40, 20, 50, 10, 30, 15, 5}) {
            m.insertar(x);
        }
        assertEquals(List.of(5, 20, 10, 40, 30, 50, 15), m.arreglo());
        List<Integer> salida = new ArrayList<>();
        while (!m.estaVacio()) {
            salida.add(m.extraerMin());
        }
        assertEquals(List.of(5, 10, 15, 20, 30, 40, 50), salida);
    }

    @Test
    void losPasosDejanElMismoMonticuloQueLasOperacionesNormales() {
        Random r = new Random(8);
        MonticuloMin<Integer> normal = new MonticuloMin<>();
        MonticuloMin<Integer> animado = new MonticuloMin<>();
        for (int k = 0; k < 400; k++) {
            if (normal.estaVacio() || r.nextBoolean()) {
                int x = r.nextInt(100);
                normal.insertar(x);
                List<EstadoMonticulo> pasos = animado.pasosInsertar(x);
                assertEquals(EstadoMonticulo.Tipo.FIN, pasos.get(pasos.size() - 1).tipo());
            } else {
                normal.extraerMin();
                animado.pasosExtraer();
            }
            assertEquals(normal.arreglo(), animado.arreglo());
        }
    }

    @Test
    void heapsortOrdenaYSusPasosTerminanOrdenados() {
        Random r = new Random(2);
        for (int prueba = 0; prueba < 100; prueba++) {
            int[] a = r.ints(r.nextInt(30), -50, 50).toArray();
            int[] esperado = a.clone();
            Arrays.sort(esperado);
            assertArrayEquals(esperado, Heapsort.ordenar(a));
            List<EstadoMonticulo> pasos = Heapsort.pasos(a);
            List<Integer> final_ = pasos.get(pasos.size() - 1).valores().stream().map(Integer::valueOf).toList();
            assertEquals(Arrays.stream(esperado).boxed().toList(), final_);
        }
        // En cada paso de la fase 2, la zona ya ordenada contiene los mayores en su lugar final.
        int[] datos = {35, 12, 48, 7, 26, 19, 41, 3, 30, 15};
        int[] ordenado = Heapsort.ordenar(datos);
        for (EstadoMonticulo p : Heapsort.pasos(datos)) {
            for (int k = p.tamanio(); k < datos.length; k++) {
                if (p.tipo() != EstadoMonticulo.Tipo.INICIO) {
                    assertEquals(String.valueOf(ordenado[k]), p.valores().get(k));
                }
            }
        }
    }

    @Test
    void triajeAtiendePrimeroALosMasUrgentes() {
        TriajeEmergencias t = TriajeEmergencias.desdeRecurso();
        assertEquals(24, t.llegadas().size());
        assertEquals(24, t.porPrioridad().size());
        for (Atencion a : t.porPrioridad()) {
            assertTrue(a.minuto() >= a.paciente().minuto(), "no se atiende antes de llegar");
            for (var esperando : a.colaAntes()) {
                assertTrue(a.paciente().compareTo(esperando) <= 0, "se llamó a alguien menos urgente");
            }
        }
        for (int k = 1; k < t.porLlegada().size(); k++) {
            assertTrue(t.porLlegada().get(k - 1).paciente().minuto() <= t.porLlegada().get(k).paciente().minuto());
        }
        double[] fifo = TriajeEmergencias.esperaPromedio(t.porLlegada());
        double[] heap = TriajeEmergencias.esperaPromedio(t.porPrioridad());
        assertTrue(heap[1] < fifo[1]);
        assertTrue(heap[5] > fifo[5]);
        double totalFifo = t.porLlegada().stream().mapToInt(Atencion::espera).sum();
        double totalHeap = t.porPrioridad().stream().mapToInt(Atencion::espera).sum();
        assertTrue(totalHeap <= totalFifo * 1.5, "el total no debería dispararse");
    }
}
