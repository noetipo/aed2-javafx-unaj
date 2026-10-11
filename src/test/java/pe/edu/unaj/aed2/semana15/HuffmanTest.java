package pe.edu.unaj.aed2.semana15;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana15.caso.CompresionTextos;
import pe.edu.unaj.aed2.semana15.modelo.EstadoHuffman;
import pe.edu.unaj.aed2.semana15.modelo.Huffman;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HuffmanTest {

    @Test
    void elEjemplo() {
        assertEquals(51, Huffman.bitsHuffman(Huffman.EJEMPLO));
        assertEquals(68, Huffman.bitsFijos(Huffman.EJEMPLO));
        assertEquals("11", Huffman.codigos(Huffman.EJEMPLO).get('a'), "la más frecuente tiene el código más corto");
    }

    @Test
    void codificarYDecodificarRecuperaElTexto() {
        Random r = new Random(15);
        for (int prueba = 0; prueba < 300; prueba++) {
            StringBuilder s = new StringBuilder();
            int n = 1 + r.nextInt(40);
            for (int k = 0; k < n; k++) {
                s.append("aaaabbcdeqk ñ".charAt(r.nextInt(13)));
            }
            String t = s.toString();
            Map<Character, String> codigos = Huffman.codigos(t);
            assertEquals(t, Huffman.decodificar(Huffman.codificar(t, codigos), Huffman.arbol(t)));
            assertTrue(libreDePrefijos(codigos), "ningún código es prefijo de otro");
            assertTrue(Huffman.bitsHuffman(t) <= Huffman.bitsFijos(t), "nunca peor que un código fijo");
            // Óptimo: coincide con el costo calculado sumando las uniones (cada unión agrega su frecuencia).
            assertEquals(costoPorUniones(t), Huffman.bitsHuffman(t));
        }
    }

    @Test
    void losPasosUnenSiempreLosDosMenores() {
        List<EstadoHuffman> pasos = Huffman.pasos(Huffman.EJEMPLO);
        int distintos = Huffman.frecuencias(Huffman.EJEMPLO).size();
        assertEquals(distintos - 1, pasos.stream().filter(p -> p.tipo() == EstadoHuffman.Tipo.UNE).count());
        for (EstadoHuffman p : pasos) {
            if (p.tipo() == EstadoHuffman.Tipo.UNE) {
                for (Huffman.Nodo otro : p.bosque()) {
                    if (otro != p.nuevo()) {
                        assertTrue(otro.frec() >= p.b().frec(), "los que quedan no son menores que los unidos");
                    }
                }
            }
        }
        assertEquals(Huffman.codigos(Huffman.EJEMPLO), pasos.get(pasos.size() - 1).codigos());
    }

    @Test
    void elCasoComprimeLasFrases() {
        CompresionTextos.frases().forEach((idioma, t) -> {
            CompresionTextos.Resumen r = CompresionTextos.resumir(t);
            assertEquals(t, r.decodificado(), idioma);
            assertTrue(r.huffman() <= r.fijo() && r.fijo() <= r.ascii(), idioma);
        });
        CompresionTextos.Resumen parejo = CompresionTextos.resumir("abcdefghijklmnop");
        assertEquals(parejo.fijo(), parejo.huffman(), "16 caracteres distintos, una vez cada uno: no hay ganancia");
        assertFalse(CompresionTextos.tabla("aab").isEmpty());
    }

    private static boolean libreDePrefijos(Map<Character, String> codigos) {
        for (String a : codigos.values()) {
            for (String b : codigos.values()) {
                if (a != b && b.startsWith(a)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static int costoPorUniones(String t) {
        java.util.PriorityQueue<Integer> cola = new java.util.PriorityQueue<>(Huffman.frecuencias(t).values());
        if (cola.size() == 1) {
            return t.length();
        }
        int costo = 0;
        while (cola.size() > 1) {
            int s = cola.poll() + cola.poll();
            costo += s;
            cola.add(s);
        }
        return costo;
    }
}
