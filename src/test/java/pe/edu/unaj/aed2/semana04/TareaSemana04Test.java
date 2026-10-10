package pe.edu.unaj.aed2.semana04;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana04.tarea.TareaSemana04;
import pe.edu.unaj.aed2.semana04.tarea.TareaSemana04.Nodo;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la tarea. No se ejecutan con "./mvnw test"; para correrlas:
 * ./mvnw test -Dgroups=tarea -DexcludedGroups=none
 */
@Tag("tarea")
class TareaSemana04Test {

    //          50
    //        /    \
    //      30      70
    //     /  \       \
    //   20    40      80
    //        /
    //      35
    private static Nodo ejemplo() {
        return new Nodo(50,
                new Nodo(30, new Nodo(20), new Nodo(40, new Nodo(35), null)),
                new Nodo(70, null, new Nodo(80)));
    }

    @Test
    void altura() {
        assertEquals(0, TareaSemana04.altura(null));
        assertEquals(1, TareaSemana04.altura(new Nodo(7)));
        assertEquals(4, TareaSemana04.altura(ejemplo()));
        Nodo lista = new Nodo(1, null, new Nodo(2, null, new Nodo(3, null, new Nodo(4))));
        assertEquals(4, TareaSemana04.altura(lista));
    }

    @Test
    void contarHojas() {
        assertEquals(0, TareaSemana04.contarHojas(null));
        assertEquals(1, TareaSemana04.contarHojas(new Nodo(7)));
        assertEquals(3, TareaSemana04.contarHojas(ejemplo())); // 20, 35 y 80
    }

    @Test
    void postorden() {
        List<Integer> salida = new ArrayList<>();
        TareaSemana04.postorden(ejemplo(), salida);
        assertEquals(List.of(20, 35, 40, 30, 80, 70, 50), salida);
        List<Integer> vacia = new ArrayList<>();
        TareaSemana04.postorden(null, vacia);
        assertTrue(vacia.isEmpty());
    }

    @Test
    void esABB() {
        assertTrue(TareaSemana04.esABB(null));
        assertTrue(TareaSemana04.esABB(ejemplo()));
        // Cada nodo es correcto respecto de sus hijos, pero 55 está en el subárbol IZQUIERDO de 50.
        Nodo trampa = new Nodo(50, new Nodo(30, new Nodo(20), new Nodo(55)), new Nodo(70));
        assertFalse(TareaSemana04.esABB(trampa));
        Nodo repetido = new Nodo(50, new Nodo(50), null);
        assertFalse(TareaSemana04.esABB(repetido));
        assertFalse(TareaSemana04.esABB(new Nodo(10, new Nodo(15), null)));
        assertTrue(TareaSemana04.esABB(new Nodo(Integer.MIN_VALUE, null, new Nodo(Integer.MAX_VALUE))));
    }
}
