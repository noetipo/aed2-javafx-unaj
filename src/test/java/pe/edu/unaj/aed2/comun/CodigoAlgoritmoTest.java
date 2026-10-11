package pe.edu.unaj.aed2.comun;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana01.modelo.Ordenamientos;
import pe.edu.unaj.aed2.semana02.caso.Escalinata;
import pe.edu.unaj.aed2.semana02.modelo.QuickSort;
import pe.edu.unaj.aed2.semana03.modelo.TablaHash;
import pe.edu.unaj.aed2.semana04.modelo.ArbolBusqueda;
import pe.edu.unaj.aed2.semana05.modelo.ArbolAVL;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CodigoAlgoritmoTest {

    @Test
    void quitaMarcadoresYEnlazaLineas() {
        CodigoAlgoritmo c = new CodigoAlgoritmo(List.of("a", "b"), """
                int x = 0;      //@1
                x++;            //@2,1
                return x;
                """);
        assertEquals(List.of("int x = 0;", "x++;", "return x;"), c.java());
        assertEquals(Set.of(1, 2), c.lineasJava(1));
        assertEquals(Set.of(2), c.lineasJava(2));
        assertTrue(c.lineasJava(3).isEmpty());
        assertEquals(1, c.lineaPrincipal(1));
        assertEquals(2, c.lineaPrincipal(2));
        assertEquals(0, c.lineaPrincipal(3));
    }

    @Test
    void laEvaluacionVaEnLaLineaDeAdentroYNoEnLaLlamada() {
        CodigoAlgoritmo c = new CodigoAlgoritmo(List.of("a", "b"), """
                int p = particionar(a);   //@1,2
                int pivote = a[fin];      //@2
                """);
        assertEquals(2, c.lineaPrincipal(2));
        assertEquals(1, c.lineaPrincipal(1));
    }

    @Test
    void marcadoresConEtiquetaEnlazanSoloSuSeccion() {
        String java = """
                int i = hash(clave);   //@insertar:2 buscar:2
                agregar(clave);        //@insertar:5
                return true;           //@buscar:4
                """;
        CodigoAlgoritmo insertar = CodigoAlgoritmo.seccion(List.of("a"), java, "insertar");
        CodigoAlgoritmo buscar = CodigoAlgoritmo.seccion(List.of("a"), java, "buscar");
        assertEquals(List.of("int i = hash(clave);", "agregar(clave);", "return true;"), insertar.java());
        assertEquals(Set.of(1), insertar.lineasJava(2));
        assertEquals(Set.of(2), insertar.lineasJava(5));
        assertTrue(insertar.lineasJava(4).isEmpty());
        assertEquals(Set.of(1), buscar.lineasJava(2));
        assertEquals(Set.of(3), buscar.lineasJava(4));
        assertTrue(buscar.lineasJava(5).isEmpty());
    }

    @Test
    void ningunaLineaVisibleConservaMarcadores() {
        for (CodigoAlgoritmo c : todos()) {
            c.java().forEach(l -> assertFalse(l.contains("//@"), l));
        }
    }

    @Test
    void cadaLineaUsadaEnLosPasosTieneSuEquivalenteJava() {
        int[] datos = {9, 4, 7, 1, 8, 2, 6, 3};
        verificar(Ordenamientos.CODIGO_BURBUJA, Ordenamientos.pasosBurbuja(datos));
        verificar(Ordenamientos.CODIGO_INSERCION, Ordenamientos.pasosInsercion(datos));
        verificar(QuickSort.CODIGO, QuickSort.pasos(datos));
        verificar(Escalinata.CODIGO_RECURSIVO, Escalinata.pasos(Escalinata.arbolRecursivo(5), false));
        verificar(Escalinata.CODIGO_MEMO, Escalinata.pasos(Escalinata.arbolMemo(5), true));
        TablaHash<String> tabla = new TablaHash<>(7);
        for (String c : new String[]{"JUL-102", "JUL-215", "PUN-031", "JUL-347", "CAR-008", "JUL-520"}) {
            verificar(TablaHash.CODIGO_INSERTAR, tabla.pasosInsertar(c, c));
        }
        verificar(TablaHash.CODIGO_INSERTAR, tabla.pasosInsertar("JUL-215", "x"));
        verificar(TablaHash.CODIGO_BUSCAR, tabla.pasosBuscar("JUL-347"));
        verificar(TablaHash.CODIGO_BUSCAR, tabla.pasosBuscar("PUN-999"));
        for (int x : new int[]{55, 30, 60, 20, 50, 99}) {
            ArbolBusqueda<Integer> arbol = arbolEjemplo();
            verificar(ArbolBusqueda.CODIGO_INSERTAR, arbol.pasosInsertar(x));
            verificar(ArbolBusqueda.CODIGO_BUSCAR, arbol.pasosBuscar(x));
            verificar(ArbolBusqueda.CODIGO_ELIMINAR, arbolEjemplo().pasosEliminar(x));
        }
        verificar(ArbolBusqueda.CODIGO_INORDEN, arbolEjemplo().pasosInorden());
        ArbolAVL<Integer> avl = new ArbolAVL<>();
        for (int x : new int[]{30, 20, 10, 12, 18, 15, 25, 25}) {
            verificar(ArbolAVL.CODIGO_INSERTAR, avl.pasosInsertar(x));
        }
    }

    @Test
    void valoresEnLineaMuestraSoloLasVariablesQueUsaLaLinea() {
        Map<String, String> valores = PasoTrazable.variables(
                "fin", 11, "j", 4, "a[j]", 95, "a[j+1]", 101, "i", "—", "huboIntercambio", true);
        assertEquals("j = 4 · a[j] = 95 · a[j+1] = 101",
                CodigoAlgoritmo.valoresEnLinea("        if (a[j] > a[j + 1]) {", valores));
        assertEquals("fin = 11 · j = 4",
                CodigoAlgoritmo.valoresEnLinea("for (int j = 0; j < fin; j++) {", valores));
        assertEquals("huboIntercambio = true",
                CodigoAlgoritmo.valoresEnLinea("    huboIntercambio = true;", valores));
        assertEquals("", CodigoAlgoritmo.valoresEnLinea("    // recorre j hasta fin", valores));
        assertEquals("j = 4", CodigoAlgoritmo.valoresEnLinea("j++; // avanza hasta fin", valores));
        // "i" no debe aparecer por "int", "if" ni "huboIntercambio"; además vale "—".
        assertEquals("", CodigoAlgoritmo.valoresEnLinea("int temp = 0; if (x) { }", valores));
    }

    @Test
    void cadaPasoConLineaTieneEvaluacion() {
        int[] datos = {9, 4, 7, 1, 8, 2, 6, 3};
        List<List<? extends PasoTrazable>> todos = List.of(Ordenamientos.pasosBurbuja(datos),
                Ordenamientos.pasosInsercion(datos), QuickSort.pasos(datos),
                Escalinata.pasos(Escalinata.arbolMemo(5), true),
                Escalinata.pasos(Escalinata.arbolRecursivo(5), false),
                new TablaHash<String>(7).pasosInsertar("JUL-102", "x"),
                ejemploTabla().pasosBuscar("JUL-347"), ejemploTabla().pasosBuscar("PUN-999"),
                arbolEjemplo().pasosInsertar(55), arbolEjemplo().pasosBuscar(45), arbolEjemplo().pasosBuscar(55),
                arbolEjemplo().pasosEliminar(30), arbolEjemplo().pasosEliminar(60), arbolEjemplo().pasosEliminar(20),
                arbolEjemplo().pasosEliminar(99), arbolEjemplo().pasosInorden(), avlEjemplo().pasosInsertar(5),
                avlEjemplo().pasosInsertar(20));
        for (List<? extends PasoTrazable> pasos : todos) {
            for (PasoTrazable p : pasos) {
                if (p.linea() > 0) {
                    assertFalse(p.evaluacion().isBlank(), "Paso sin evaluación en la línea " + p.linea());
                    assertFalse(p.explicacion().isBlank(), "Paso sin explicación en la línea " + p.linea());
                }
            }
        }
    }

    private static ArbolAVL<Integer> avlEjemplo() {
        ArbolAVL<Integer> a = new ArbolAVL<>();
        for (int x : new int[]{30, 20, 10, 12, 18, 15}) {
            a.insertar(x);
        }
        return a;
    }

    private static ArbolBusqueda<Integer> arbolEjemplo() {
        ArbolBusqueda<Integer> a = new ArbolBusqueda<>();
        for (int c : new int[]{50, 30, 70, 20, 40, 60, 80, 35, 45, 65}) {
            a.insertar(c);
        }
        return a;
    }

    private static TablaHash<String> ejemploTabla() {
        TablaHash<String> t = new TablaHash<>(7);
        for (String c : new String[]{"JUL-102", "JUL-215", "PUN-031", "JUL-347", "CAR-008", "JUL-520"}) {
            t.insertar(c, c);
        }
        return t;
    }

    private static void verificar(CodigoAlgoritmo codigo, List<? extends PasoTrazable> pasos) {
        for (PasoTrazable p : pasos) {
            if (p.linea() > 0) {
                assertTrue(p.linea() <= codigo.pseudocodigo().size());
                assertFalse(codigo.lineasJava(p.linea()).isEmpty(),
                        "La línea " + p.linea() + " del pseudocódigo no tiene marcador //@ en el Java");
            }
        }
    }

    private static List<CodigoAlgoritmo> todos() {
        return List.of(Ordenamientos.CODIGO_BURBUJA, Ordenamientos.CODIGO_INSERCION, QuickSort.CODIGO,
                Escalinata.CODIGO_RECURSIVO, Escalinata.CODIGO_MEMO, TablaHash.CODIGO_INSERTAR,
                TablaHash.CODIGO_BUSCAR, ArbolBusqueda.CODIGO_INSERTAR, ArbolBusqueda.CODIGO_BUSCAR,
                ArbolBusqueda.CODIGO_ELIMINAR, ArbolBusqueda.CODIGO_INORDEN, ArbolAVL.CODIGO_INSERTAR);
    }
}
