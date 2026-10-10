package pe.edu.unaj.aed2.comun;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana01.modelo.Ordenamientos;
import pe.edu.unaj.aed2.semana02.caso.Escalinata;
import pe.edu.unaj.aed2.semana02.modelo.QuickSort;

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
                Escalinata.pasos(Escalinata.arbolRecursivo(5), false));
        for (List<? extends PasoTrazable> pasos : todos) {
            for (PasoTrazable p : pasos) {
                if (p.linea() > 0) {
                    assertFalse(p.evaluacion().isBlank(), "Paso sin evaluación en la línea " + p.linea());
                    assertFalse(p.explicacion().isBlank(), "Paso sin explicación en la línea " + p.linea());
                }
            }
        }
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
                Escalinata.CODIGO_RECURSIVO, Escalinata.CODIGO_MEMO);
    }
}
