package pe.edu.unaj.aed2.semana05;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana05.caso.DiccionarioAVL;
import pe.edu.unaj.aed2.semana05.modelo.ArbolAVL;
import pe.edu.unaj.aed2.semana05.modelo.EstadoAVL;
import pe.edu.unaj.aed2.semana05.modelo.EstadoAVL.NodoAVL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArbolAVLTest {

    /** Altura real si el subárbol es ABB y AVL con alturas guardadas correctas; -1 si no. */
    private static int verificar(NodoAVL<Integer> r, long min, long max) {
        if (r == null) {
            return 0;
        }
        if (r.clave() <= min || r.clave() >= max) {
            return -1;
        }
        int a = verificar(r.izquierdo(), min, r.clave());
        int b = verificar(r.derecho(), r.clave(), max);
        if (a < 0 || b < 0 || Math.abs(a - b) > 1 || 1 + Math.max(a, b) != r.altura()) {
            return -1;
        }
        return 1 + Math.max(a, b);
    }

    @Test
    void elEjemploProduceLosCuatroCasosDeRotacion() {
        ArbolAVL<Integer> a = new ArbolAVL<>();
        for (int x : new int[]{30, 20, 10, 12, 18, 15, 25}) {
            a.insertar(x);
        }
        assertEquals(Map.of("LL", 1, "RR", 1, "LR", 1, "RL", 1), a.rotaciones());
        assertEquals(3, a.altura());
        assertEquals(18, a.vista().clave());
        assertTrue(verificar(a.vista(), Long.MIN_VALUE, Long.MAX_VALUE) > 0);
    }

    @Test
    void clavesOrdenadasNoDegeneranElArbol() {
        ArbolAVL<Integer> a = new ArbolAVL<>();
        for (int x = 1; x <= 15; x++) {
            a.insertar(x);
        }
        assertEquals(4, a.altura());
        assertEquals(8, a.vista().clave());
        for (int x = 16; x <= 1023; x++) {
            a.insertar(x);
        }
        assertEquals(10, a.altura()); // 1023 claves ordenadas: árbol perfecto de 10 niveles
    }

    @Test
    void miles_de_inserciones_aleatorias_mantienen_la_propiedad_AVL() {
        Random r = new Random(5);
        ArbolAVL<Integer> a = new ArbolAVL<>();
        TreeSet<Integer> referencia = new TreeSet<>();
        for (int k = 0; k < 5000; k++) {
            int x = r.nextInt(3000);
            assertEquals(referencia.add(x), a.insertar(x));
        }
        assertEquals(new ArrayList<>(referencia), a.inorden());
        int h = verificar(a.vista(), Long.MIN_VALUE, Long.MAX_VALUE);
        assertTrue(h > 0);
        assertTrue(h <= 1.44 * Math.log(referencia.size() + 2) / Math.log(2));
    }

    @Test
    void laInsercionPasoAPasoDejaElMismoArbolYCadaFotoEsValida() {
        ArbolAVL<Integer> normal = new ArbolAVL<>();
        ArbolAVL<Integer> animado = new ArbolAVL<>();
        Random r = new Random(11);
        for (int k = 0; k < 200; k++) {
            int x = r.nextInt(400);
            normal.insertar(x);
            List<EstadoAVL<Integer>> pasos = animado.pasosInsertar(x);
            assertEquals(EstadoAVL.Tipo.FIN, pasos.get(pasos.size() - 1).tipo());
            assertEquals(normal.inorden(), animado.inorden());
            // Cada foto, incluso a mitad de una rotación, debe dibujar un ABB con todas las claves.
            for (EstadoAVL<Integer> p : pasos) {
                List<Integer> enLaFoto = new ArrayList<>();
                inorden(p.raiz(), enLaFoto);
                for (int i = 1; i < enLaFoto.size(); i++) {
                    assertTrue(enLaFoto.get(i - 1) < enLaFoto.get(i), "foto desordenada: " + enLaFoto);
                }
            }
            List<Integer> final_ = new ArrayList<>();
            inorden(pasos.get(pasos.size() - 1).raiz(), final_);
            assertEquals(normal.inorden(), final_);
        }
        assertEquals(normal.rotaciones(), animado.rotaciones());
        assertEquals(normal.altura(), animado.altura());
    }

    private static void inorden(NodoAVL<Integer> r, List<Integer> salida) {
        if (r != null) {
            inorden(r.izquierdo(), salida);
            salida.add(r.clave());
            inorden(r.derecho(), salida);
        }
    }

    @Test
    void lasFotosDeRotacionMuestranElSubarbolYaRotado() {
        ArbolAVL<Integer> a = new ArbolAVL<>();
        a.pasosInsertar(30);
        a.pasosInsertar(20);
        List<EstadoAVL<Integer>> pasos = a.pasosInsertar(10);
        EstadoAVL<Integer> rotacion = pasos.stream()
                .filter(p -> p.tipo() == EstadoAVL.Tipo.ROTACION).findFirst().orElseThrow();
        assertEquals(20, rotacion.raiz().clave());
        assertEquals(3, contar(rotacion.raiz()));
        assertFalse(a.insertar(20));
    }

    private static int contar(NodoAVL<Integer> r) {
        return r == null ? 0 : 1 + contar(r.izquierdo()) + contar(r.derecho());
    }

    @Test
    void diccionarioAlfabeticoQuedaEquilibrado() {
        DiccionarioAVL d = DiccionarioAVL.desdeRecurso();
        assertEquals(38, d.tamanio());
        assertEquals(38, d.abb().altura());
        assertEquals(6, d.avl().altura());
        assertEquals("blanco", d.traducir("yuraq"));
        assertTrue(d.comparacionesAVL("yuraq") <= 6);
        assertEquals(37, d.comparacionesABB("yuraq"));
        double[] p = d.promedios();
        assertTrue(p[1] < p[0] / 3);
        int total = d.rotaciones().values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(d.rotaciones().get("RR"), total); // en orden creciente solo aparece el caso RR
    }
}
