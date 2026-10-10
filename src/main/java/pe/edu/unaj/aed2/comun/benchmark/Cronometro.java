package pe.edu.unaj.aed2.comun.benchmark;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Medición de tiempos con {@link System#nanoTime()}.
 * <p>
 * Ejecuta primero varias rondas de calentamiento (para que el JIT compile el
 * código) y luego reporta la mediana de las repeticiones, que es más estable
 * que el promedio frente a pausas del recolector de basura.
 */
public final class Cronometro {

    private final int calentamiento;
    private final int repeticiones;

    public Cronometro(int calentamiento, int repeticiones) {
        if (repeticiones < 1) {
            throw new IllegalArgumentException("Se necesita al menos una repetición");
        }
        this.calentamiento = calentamiento;
        this.repeticiones = repeticiones;
    }

    public Cronometro() {
        this(3, 5);
    }

    /** Mediana en nanosegundos de ejecutar {@code tarea}. */
    public long medirNanos(Runnable tarea) {
        return medirNanos(() -> tarea, Runnable::run);
    }

    /**
     * Variante para algoritmos que modifican su entrada (p. ej. ordenamientos):
     * {@code preparar} genera una entrada fresca que NO se cronometra.
     */
    public <E> long medirNanos(Supplier<E> preparar, Consumer<E> tarea) {
        for (int i = 0; i < calentamiento; i++) {
            tarea.accept(preparar.get());
        }
        long[] tiempos = new long[repeticiones];
        for (int i = 0; i < repeticiones; i++) {
            E entrada = preparar.get();
            long inicio = System.nanoTime();
            tarea.accept(entrada);
            tiempos[i] = System.nanoTime() - inicio;
        }
        Arrays.sort(tiempos);
        return tiempos[repeticiones / 2];
    }

    public double medirMilisegundos(Runnable tarea) {
        return medirNanos(tarea) / 1_000_000.0;
    }

    public <E> double medirMilisegundos(Supplier<E> preparar, Consumer<E> tarea) {
        return medirNanos(preparar, tarea) / 1_000_000.0;
    }
}
