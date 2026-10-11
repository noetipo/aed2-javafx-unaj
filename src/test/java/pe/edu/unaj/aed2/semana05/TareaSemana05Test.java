package pe.edu.unaj.aed2.semana05;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.semana05.tarea.TareaSemana05;
import pe.edu.unaj.aed2.semana05.tarea.TareaSemana05.Nodo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de la tarea. No se ejecutan con "./mvnw test"; para correrlas:
 * ./mvnw test -Dgroups=tarea -DexcludedGroups=none
 */
@Tag("tarea")
class TareaSemana05Test {

    @Test
    void factorEquilibrio() {
        assertEquals(0, TareaSemana05.factorEquilibrio(null));
        assertEquals(0, TareaSemana05.factorEquilibrio(new Nodo(5)));
        Nodo izquierdo = new Nodo(30, new Nodo(20, new Nodo(10), null), null);
        assertEquals(2, TareaSemana05.factorEquilibrio(izquierdo));
        assertEquals(-1, TareaSemana05.factorEquilibrio(new Nodo(5, null, new Nodo(8))));
    }

    @Test
    void rotarDerechaArreglaElCasoLL() {
        //       30            20
        //      /             /  \
        //    20     →      10    30
        //   /  \                /
        //  10   25            25
        Nodo n10 = new Nodo(10);
        Nodo n25 = new Nodo(25);
        Nodo n20 = new Nodo(20, n10, n25);
        Nodo n30 = new Nodo(30, n20, null);
        Nodo raiz = TareaSemana05.rotarDerecha(n30);
        assertSame(n20, raiz);
        assertSame(n10, raiz.izq);
        assertSame(n30, raiz.der);
        assertSame(n25, n30.izq);
        assertNull(n30.der);
        assertEquals(2, n30.altura);
        assertEquals(3, n20.altura);
        assertTrue(TareaSemana05.esAVL(raiz));
    }

    @Test
    void rotarIzquierdaArreglaElCasoRR() {
        Nodo n35 = new Nodo(35);
        Nodo n40 = new Nodo(40, n35, new Nodo(50));
        Nodo n30 = new Nodo(30, null, n40);
        Nodo raiz = TareaSemana05.rotarIzquierda(n30);
        assertSame(n40, raiz);
        assertSame(n30, raiz.izq);
        assertSame(n35, n30.der);
        assertEquals(2, n30.altura);
        assertEquals(3, n40.altura);
        assertTrue(TareaSemana05.esAVL(raiz));
    }

    @Test
    void dobleRotacionParaElCasoLR() {
        //     30          30          20
        //    /           /           /  \
        //  10     →    20     →    10    30
        //    \        /
        //     20    10
        Nodo n20 = new Nodo(20);
        Nodo n10 = new Nodo(10, null, n20);
        Nodo n30 = new Nodo(30, n10, null);
        n30.izq = TareaSemana05.rotarIzquierda(n30.izq);
        Nodo raiz = TareaSemana05.rotarDerecha(n30);
        assertSame(n20, raiz);
        assertSame(n10, raiz.izq);
        assertSame(n30, raiz.der);
        assertEquals(2, raiz.altura);
        assertTrue(TareaSemana05.esAVL(raiz));
    }

    @Test
    void esAVL() {
        assertTrue(TareaSemana05.esAVL(null));
        assertFalse(TareaSemana05.esAVL(new Nodo(30, new Nodo(20, new Nodo(10), null), null)));  // fe = +2
        assertFalse(TareaSemana05.esAVL(new Nodo(30, new Nodo(40), null)));                       // no es ABB
        Nodo mentira = new Nodo(20, new Nodo(10), new Nodo(30));
        mentira.altura = 5;                                                                    // altura falsa
        assertFalse(TareaSemana05.esAVL(mentira));
        // Cada nodo tiene |fe| ≤ 1 con alturas guardadas falsas, pero las reales están desequilibradas.
        Nodo hoja = new Nodo(1);
        Nodo medio = new Nodo(2, hoja, null);
        Nodo arriba = new Nodo(3, medio, null);
        arriba.altura = 2;
        medio.altura = 1;
        assertFalse(TareaSemana05.esAVL(arriba));
    }
}
