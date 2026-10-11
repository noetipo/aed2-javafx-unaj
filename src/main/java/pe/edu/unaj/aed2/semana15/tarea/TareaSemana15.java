package pe.edu.unaj.aed2.semana15.tarea;

import java.util.Map;

/**
 * TAREA SEMANA 15 — Algoritmos voraces.
 * <p>
 * En las dos primeras, la regla voraz correcta da el óptimo; en el informe debes explicar POR QUÉ.
 * <ul>
 *   <li>No modifiques los arreglos que recibes.</li>
 *   <li>No cambies las firmas: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-15/tarea.md
 */
public final class TareaSemana15 {

    private TareaSemana15() {
    }

    /**
     * Reservas del auditorio: la actividad k va de inicio[k] a fin[k] (fin[k] &gt; inicio[k]). Dos actividades se pueden
     * aceptar juntas si no se superponen (una puede empezar justo cuando termina otra). ¿Cuántas, como máximo?
     */
    public static int maxActividades(int[] inicio, int[] fin) {
        // TODO 1: la regla voraz que funciona es "la que TERMINA primero". Ordena por fin (sin modificar los arreglos)
        //         y acepta cada actividad que empiece cuando ya terminó la última aceptada.
        throw new UnsupportedOperationException("TODO: implementar maxActividades");
    }

    /**
     * Mochila FRACCIONARIA: se puede llevar una parte de un producto (por ejemplo, quinua a granel). Retorna el mayor
     * valor posible sin pasar la capacidad.
     */
    public static double mochilaFraccionaria(int[] peso, int[] valor, int capacidad) {
        // TODO 2: aquí sí sirve la regla voraz de la semana 14: valor/peso de mayor a menor; del último que no cabe
        //         entero, se lleva la fracción que entra.
        throw new UnsupportedOperationException("TODO: implementar mochilaFraccionaria");
    }

    /**
     * Decodifica bits con un código LIBRE DE PREFIJOS (como el de Huffman). Lanza IllegalArgumentException si los bits
     * no forman una secuencia válida de códigos (por ejemplo, si sobra un pedazo al final).
     */
    public static String decodificar(String bits, Map<Character, String> codigos) {
        // TODO 3: lee los bits de izquierda a derecha acumulándolos; en cuanto lo acumulado sea el código de algún
        //         carácter, emítelo y vuelve a empezar. Pista: invierte el mapa (código → carácter).
        throw new UnsupportedOperationException("TODO: implementar decodificar");
    }
}
