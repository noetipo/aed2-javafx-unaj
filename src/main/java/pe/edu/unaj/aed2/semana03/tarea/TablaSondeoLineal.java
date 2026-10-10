package pe.edu.unaj.aed2.semana03.tarea;

/**
 * TAREA SEMANA 03 — tabla hash con DIRECCIONAMIENTO ABIERTO y sondeo lineal.
 * <p>
 * A diferencia del encadenamiento (visualizador), aquí cada casilla guarda a lo más una clave.
 * Si la casilla hash(clave) está ocupada, se prueba la siguiente: (i + 1) mod m, (i + 2) mod m, ...
 * <p>
 * Reglas:
 * <ul>
 *   <li>No uses HashMap, HashSet ni otras colecciones: solo el arreglo {@code tabla}.</li>
 *   <li>No cambies las firmas: las pruebas dependen de ellas.</li>
 *   <li>Verifica tu avance con: {@code ./mvnw test -Dgroups=tarea -DexcludedGroups=none}</li>
 * </ul>
 * Enunciado y rúbrica: docs/semana-03/tarea.md
 */
public class TablaSondeoLineal {

    private final String[] tabla;
    private int n;

    public TablaSondeoLineal(int capacidad) {
        tabla = new String[capacidad];
    }

    /** Ya implementado: el mismo hash de Horner del visualizador. */
    public static int hash(String clave, int m) {
        int h = 0;
        for (int k = 0; k < clave.length(); k++) {
            h = (h * 31 + clave.charAt(k)) % m;
        }
        return h;
    }

    /**
     * Inserta la clave con sondeo lineal.
     *
     * @return false si la clave ya estaba; true si se insertó
     * @throws IllegalStateException si la tabla está llena
     */
    public boolean insertar(String clave) {
        // TODO 1: empieza en i = hash(clave, m). Mientras tabla[i] no sea null:
        //         si es la clave, retorna false; si no, i = (i + 1) % m.
        //         Cuidado: si diste la vuelta completa, la tabla está llena.
        //         Al encontrar una casilla null, guarda la clave, n++ y retorna true.
        throw new UnsupportedOperationException("TODO: implementar insertar");
    }

    /** true si la clave está en la tabla. */
    public boolean buscar(String clave) {
        // TODO 2: recorre desde hash(clave, m) igual que insertar. Si llegas a una casilla null,
        //         la clave NO está (¿por qué basta con eso?). No des más de m pasos.
        throw new UnsupportedOperationException("TODO: implementar buscar");
    }

    /**
     * Cantidad de casillas que se revisan al buscar la clave (incluida la que la contiene o
     * la casilla null donde termina la búsqueda).
     */
    public int sondeos(String clave) {
        // TODO 3: igual que buscar, pero contando las casillas revisadas.
        throw new UnsupportedOperationException("TODO: implementar sondeos");
    }

    public double factorCarga() {
        return (double) n / tabla.length;
    }

    /** Copia del arreglo interno (para las pruebas). */
    public String[] casillas() {
        return tabla.clone();
    }
}
