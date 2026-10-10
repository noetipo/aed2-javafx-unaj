package pe.edu.unaj.aed2.comun.traza;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Paso de un algoritmo que puede seguirse sobre su pseudocódigo
 * (prueba de escritorio). Java puro: lo implementan los records del modelo.
 */
public interface PasoTrazable {

    /** Línea del pseudocódigo (desde 1) que se ejecuta en este paso; 0 si ninguna. */
    int linea();

    /** Variables visibles en este paso, en el orden en que deben mostrarse. */
    Map<String, String> variables();

    /**
     * Construye un mapa ordenado e inmutable a partir de pares clave, valor:
     * {@code PasoTrazable.variables("i", 2, "j", 5)}.
     */
    static Map<String, String> variables(Object... claveValor) {
        if (claveValor.length % 2 != 0) {
            throw new IllegalArgumentException("Se esperan pares clave, valor");
        }
        Map<String, String> mapa = new LinkedHashMap<>();
        for (int k = 0; k < claveValor.length; k += 2) {
            mapa.put(String.valueOf(claveValor[k]), String.valueOf(claveValor[k + 1]));
        }
        return Collections.unmodifiableMap(mapa);
    }

    /** Copia defensiva que conserva el orden. */
    static Map<String, String> copiar(Map<String, String> original) {
        return Collections.unmodifiableMap(new LinkedHashMap<>(original));
    }
}
