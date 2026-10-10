package pe.edu.unaj.aed2.semana04;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana04.caso.DiccionarioQuechua;
import pe.edu.unaj.aed2.semana04.modelo.ArbolBusqueda;
import pe.edu.unaj.aed2.semana04.modelo.EstadoArbol;
import pe.edu.unaj.aed2.semana04.modelo.EstadoArbol.NodoVista;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ArbolBusquedaTest {

    private static final int[] EJEMPLO = {50, 30, 70, 20, 40, 60, 80, 35, 45, 65};

    private static ArbolBusqueda<Integer> ejemplo() {
        ArbolBusqueda<Integer> a = new ArbolBusqueda<>();
        for (int c : EJEMPLO) {
            a.insertar(c);
        }
        return a;
    }

    /** Verifica la propiedad de ABB recorriendo la vista con rangos. */
    private static boolean esABB(NodoVista<Integer> r, long min, long max) {
        if (r == null) {
            return true;
        }
        return r.clave() > min && r.clave() < max
                && esABB(r.izquierdo(), min, r.clave()) && esABB(r.derecho(), r.clave(), max);
    }

    @Test
    void insertarBuscarEInorden() {
        ArbolBusqueda<Integer> a = ejemplo();
        assertEquals(List.of(20, 30, 35, 40, 45, 50, 60, 65, 70, 80), a.inorden());
        assertEquals(10, a.tamanio());
        assertEquals(4, a.altura());
        assertTrue(a.buscar(45));
        assertFalse(a.buscar(55));
        assertFalse(a.insertar(45));
        assertEquals(10, a.tamanio());
        assertEquals(3, a.comparaciones(40));
    }

    @Test
    void eliminarLosTresCasos() {
        ArbolBusqueda<Integer> a = ejemplo();
        assertTrue(a.eliminar(20));   // hoja
        assertTrue(a.eliminar(60));   // un hijo
        assertTrue(a.eliminar(30));   // dos hijos: sucesor 35
        assertTrue(a.eliminar(50));   // la raíz, dos hijos
        assertFalse(a.eliminar(99));
        assertEquals(List.of(35, 40, 45, 65, 70, 80), a.inorden());
        assertEquals(65, a.vista().clave());
        assertTrue(esABB(a.vista(), Long.MIN_VALUE, Long.MAX_VALUE));
    }

    @Test
    void operacionesAleatoriasSiempreDejanUnABB() {
        Random r = new Random(4);
        ArbolBusqueda<Integer> a = new ArbolBusqueda<>();
        TreeSet<Integer> referencia = new TreeSet<>();
        for (int k = 0; k < 2000; k++) {
            int x = r.nextInt(200);
            if (r.nextBoolean()) {
                assertEquals(referencia.add(x), a.insertar(x));
            } else {
                assertEquals(referencia.remove(x), a.eliminar(x));
            }
        }
        assertEquals(new ArrayList<>(referencia), a.inorden());
        assertTrue(esABB(a.vista(), Long.MIN_VALUE, Long.MAX_VALUE));
    }

    @Test
    void lasVersionesPasoAPasoHacenLoMismoQueLasNormales() {
        ArbolBusqueda<Integer> normal = new ArbolBusqueda<>();
        ArbolBusqueda<Integer> animado = new ArbolBusqueda<>();
        Random r = new Random(9);
        for (int k = 0; k < 300; k++) {
            int x = r.nextInt(60);
            if (k % 3 == 0) {
                normal.eliminar(x);
                List<EstadoArbol<Integer>> pasos = animado.pasosEliminar(x);
                assertEquals(EstadoArbol.Tipo.FIN, pasos.get(pasos.size() - 1).tipo());
            } else {
                normal.insertar(x);
                animado.pasosInsertar(x);
            }
            assertEquals(normal.inorden(), animado.inorden());
            assertEquals(normal.altura(), animado.altura());
        }
        assertEquals(normal.inorden(), animado.pasosInorden().get(animado.pasosInorden().size() - 1).salida());
    }

    @Test
    void buscarPasoAPasoCuentaUnaComparacionPorNivel() {
        ArbolBusqueda<Integer> a = ejemplo();
        List<EstadoArbol<Integer>> pasos = a.pasosBuscar(45);
        long visitas = pasos.stream().filter(p -> p.tipo() == EstadoArbol.Tipo.COMPARACION
                || p.tipo() == EstadoArbol.Tipo.ENCONTRADO).count();
        assertEquals(a.comparaciones(45), visitas);
        assertEquals(EstadoArbol.Tipo.ENCONTRADO, pasos.get(pasos.size() - 1).tipo());
        List<EstadoArbol<Integer>> ausente = a.pasosBuscar(55);
        assertEquals(EstadoArbol.Tipo.NO_ENCONTRADO, ausente.get(ausente.size() - 1).tipo());
    }

    @Test
    void eliminarConSucesorMuestraElNodoConLaClaveCopiada() {
        ArbolBusqueda<Integer> a = ejemplo();
        List<EstadoArbol<Integer>> pasos = a.pasosEliminar(30);
        assertTrue(pasos.stream().anyMatch(p -> p.tipo() == EstadoArbol.Tipo.SUCESOR));
        EstadoArbol<Integer> reemplazo = pasos.stream()
                .filter(p -> p.tipo() == EstadoArbol.Tipo.REEMPLAZO).findFirst().orElseThrow();
        // En ese instante 35 aparece dos veces: en el nodo que era 30 y en el sucesor original.
        assertEquals(2, contar(reemplazo.raiz(), 35));
        assertEquals(1, contar(pasos.get(pasos.size() - 1).raiz(), 35));
    }

    private static int contar(NodoVista<Integer> r, int clave) {
        return r == null ? 0 : (r.clave() == clave ? 1 : 0) + contar(r.izquierdo(), clave) + contar(r.derecho(), clave);
    }

    @Test
    void diccionarioDegeneradoFrenteAMezclado() {
        DiccionarioQuechua d = DiccionarioQuechua.desdeRecurso();
        assertEquals(38, d.tamanio());
        assertEquals("sol", d.traducir("inti"));
        assertEquals("camino", d.traducir("ñan"));
        assertNull(d.traducir("casa"));
        assertEquals(d.tamanio(), d.arbolAlfabetico().altura());       // degenerado: una "lista"
        assertTrue(d.arbolMezclado().altura() < d.tamanio() / 3);
        assertTrue(d.arbolMezclado().altura() >= d.alturaMinima());
        assertEquals(6, d.alturaMinima());
        double[] p = d.promedios();
        assertEquals((d.tamanio() + 1) / 2.0, p[0], 1e-9);              // 1 + 2 + ... + n, entre n
        assertTrue(p[1] < p[0] / 2);
    }
}
