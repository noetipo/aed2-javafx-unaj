package pe.edu.unaj.aed2.semana02;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana02.caso.Escalinata;
import pe.edu.unaj.aed2.semana02.modelo.LlamadaRecursiva;
import pe.edu.unaj.aed2.semana02.modelo.PasoLlamada;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EscalinataTest {

    @Test
    void primerosValoresSonFibonacci() {
        long[] esperado = {1, 1, 2, 3, 5, 8, 13, 21, 34, 55};
        for (int n = 0; n < esperado.length; n++) {
            assertEquals(esperado[n], Escalinata.formasRecursivo(n), "n = " + n);
            assertEquals(esperado[n], Escalinata.formasMemo(n), "n = " + n);
        }
    }

    @Test
    void memoCalculaValoresGrandes() {
        assertEquals(2_504_730_781_961L, Escalinata.formasMemo(60));
    }

    @Test
    void conteoDeLlamadas() {
        for (int n = 1; n <= 20; n++) {
            assertEquals(2 * Escalinata.formasRecursivo(n) - 1, Escalinata.llamadasRecursivo(n));
            assertEquals(2L * n - 1, Escalinata.llamadasMemo(n));
        }
    }

    @Test
    void arbolesCoincidenConLosConteos() {
        LlamadaRecursiva directo = Escalinata.arbolRecursivo(7);
        assertEquals(21, directo.resultado());
        assertEquals(Escalinata.llamadasRecursivo(7), directo.totalLlamadas());
        assertEquals(0, directo.totalReusadas());

        LlamadaRecursiva memo = Escalinata.arbolMemo(7);
        assertEquals(21, memo.resultado());
        assertEquals(13, memo.totalLlamadas());
        assertEquals(4, memo.totalReusadas());
    }

    @Test
    void pasosRecorrenTodasLasLlamadasYVacianLaPila() {
        for (boolean conMemo : new boolean[]{false, true}) {
            LlamadaRecursiva arbol = conMemo ? Escalinata.arbolMemo(6) : Escalinata.arbolRecursivo(6);
            List<PasoLlamada> pasos = Escalinata.pasos(arbol, conMemo);
            long llamadas = pasos.stream().filter(p -> p.tipo() == PasoLlamada.Tipo.LLAMADA).count();
            assertEquals(arbol.totalLlamadas(), llamadas);
            PasoLlamada ultimo = pasos.get(pasos.size() - 1);
            assertTrue(ultimo.pila().isEmpty());
            assertEquals("13", ultimo.variables().get("retorna"));
            for (PasoLlamada.Estado e : ultimo.estados()) {
                assertTrue(e == PasoLlamada.Estado.RETORNADA || e == PasoLlamada.Estado.REUSADA);
            }
            int lineas = (conMemo ? Escalinata.PSEUDO_MEMO : Escalinata.PSEUDO_RECURSIVO).size();
            pasos.forEach(p -> assertTrue(p.linea() >= 0 && p.linea() <= lineas));
        }
    }
}
