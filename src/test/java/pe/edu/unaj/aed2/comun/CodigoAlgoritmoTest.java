package pe.edu.unaj.aed2.comun;

import org.junit.jupiter.api.Test;
import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;
import pe.edu.unaj.aed2.semana01.modelo.Ordenamientos;
import pe.edu.unaj.aed2.semana02.caso.Escalinata;
import pe.edu.unaj.aed2.semana02.modelo.QuickSort;

import java.util.List;
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
