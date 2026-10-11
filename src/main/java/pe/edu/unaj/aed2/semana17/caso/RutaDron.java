package pe.edu.unaj.aed2.semana17.caso;

import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana17.modelo.Viajante;

import java.util.ArrayList;
import java.util.List;

/**
 * Caso práctico semana 17: un dron sale de Juliaca con vacunas, visita las postas elegidas y vuelve. Compara la fuerza
 * bruta (exacta), el vecino más cercano y 2-opt en km, rutas evaluadas y tiempo; y estima cuánto tardaría la fuerza
 * bruta con más postas.
 */
public class RutaDron {

    /** Resultado de un método: la ruta (null si no se pudo calcular) y el tiempo que tomó. */
    public record Resultado(String metodo, Viajante.Ruta ruta, double milisegundos) {
    }

    private final RedVial red;

    public RutaDron(RedVial red) {
        this.red = red;
    }

    public RedVial red() {
        return red;
    }

    /** @param pueblos postas a visitar; el primero es la base (Juliaca) */
    public List<Resultado> comparar(List<String> pueblos) {
        Viajante v = new Viajante(red, pueblos);
        List<Resultado> r = new ArrayList<>();
        if (pueblos.size() <= Viajante.MAXIMO_FUERZA_BRUTA) {
            long t0 = System.nanoTime();
            Viajante.Ruta exacta = v.fuerzaBruta();
            r.add(new Resultado("Fuerza bruta (exacta)", exacta, (System.nanoTime() - t0) / 1e6));
        } else {
            r.add(new Resultado("Fuerza bruta (exacta)", null, 0));
        }
        long t1 = System.nanoTime();
        Viajante.Ruta vecino = v.vecinoMasCercano();
        r.add(new Resultado("Vecino más cercano", vecino, (System.nanoTime() - t1) / 1e6));
        long t2 = System.nanoTime();
        Viajante.Ruta mejorada = v.dosOpt();
        r.add(new Resultado("Vecino más cercano + 2-opt", mejorada, (System.nanoTime() - t2) / 1e6));
        return r;
    }

    /** (n − 1)!: rutas que revisa la fuerza bruta con n pueblos (contando la base). */
    public static double rutas(int n) {
        double f = 1;
        for (int i = 2; i < n; i++) {
            f *= i;
        }
        return f;
    }
}
