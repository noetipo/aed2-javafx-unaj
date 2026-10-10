package pe.edu.unaj.aed2.semana02.modelo;

/**
 * Nodo del árbol de llamadas de una función recursiva binaria
 * (cada llamada genera como máximo dos llamadas hijas).
 *
 * @param id        número de la llamada en orden de ejecución (preorden, desde 0)
 * @param n         argumento de la llamada
 * @param resultado valor que retornó
 * @param reusada   true si el resultado se tomó de la memoria (no se recalculó)
 * @param izquierda primera llamada hija, o null
 * @param derecha   segunda llamada hija, o null
 */
public record LlamadaRecursiva(int id, int n, long resultado, boolean reusada,
                               LlamadaRecursiva izquierda, LlamadaRecursiva derecha) {

    /** Cantidad de nodos (llamadas) del árbol. */
    public int totalLlamadas() {
        return 1 + (izquierda == null ? 0 : izquierda.totalLlamadas())
                + (derecha == null ? 0 : derecha.totalLlamadas());
    }

    /** Llamadas que se resolvieron desde la memoria. */
    public int totalReusadas() {
        return (reusada ? 1 : 0) + (izquierda == null ? 0 : izquierda.totalReusadas())
                + (derecha == null ? 0 : derecha.totalReusadas());
    }
}
