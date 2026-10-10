package pe.edu.unaj.aed2.comun.visual;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Dibuja cualquier árbol binario sin acoplarse a su clase de nodo:
 * se le pasan funciones para obtener hijo izquierdo, derecho, etiqueta y color.
 * <p>
 * Distribución: la coordenada x es la posición del nodo en el recorrido
 * inorden y la y es su profundidad, lo que evita cruces de aristas.
 *
 * <pre>{@code
 * DibujoArbol.dibujar(gc, raiz, Nodo::izq, Nodo::der,
 *         n -> String.valueOf(n.valor), n -> Paleta.NORMAL, ancho, alto);
 * }</pre>
 */
public final class DibujoArbol {

    public static final double RADIO = 18;

    private DibujoArbol() {
    }

    public static <N> void dibujar(GraphicsContext gc, N raiz,
                                   Function<N, N> izquierdo, Function<N, N> derecho,
                                   Function<N, String> etiqueta, Function<N, Color> color,
                                   double ancho, double alto) {
        if (raiz == null) {
            return;
        }
        Map<N, int[]> posiciones = new IdentityHashMap<>();
        int[] contador = {0};
        int profundidadMax = ubicar(raiz, 0, izquierdo, derecho, posiciones, contador);

        double margen = RADIO + 8;
        double dx = (ancho - 2 * margen) / Math.max(1, contador[0] - 1);
        double dy = Math.min(70, (alto - 2 * margen) / Math.max(1, profundidadMax));
        Function<N, double[]> xy = n -> {
            int[] p = posiciones.get(n);
            double x = contador[0] == 1 ? ancho / 2 : margen + p[0] * dx;
            return new double[]{x, margen + p[1] * dy};
        };

        gc.setStroke(Paleta.BORDE);
        gc.setLineWidth(1.5);
        for (N n : posiciones.keySet()) {
            double[] a = xy.apply(n);
            arista(gc, a, izquierdo.apply(n), xy);
            arista(gc, a, derecho.apply(n), xy);
        }
        for (N n : posiciones.keySet()) {
            double[] a = xy.apply(n);
            nodo(gc, a[0], a[1], etiqueta.apply(n), color.apply(n));
        }
    }

    /** Dibuja un nodo circular con su etiqueta centrada. */
    public static void nodo(GraphicsContext gc, double x, double y, String texto, Color relleno) {
        gc.setFill(relleno);
        gc.fillOval(x - RADIO, y - RADIO, 2 * RADIO, 2 * RADIO);
        gc.setStroke(Paleta.BORDE);
        gc.setLineWidth(1.5);
        gc.strokeOval(x - RADIO, y - RADIO, 2 * RADIO, 2 * RADIO);
        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font("System", FontWeight.BOLD, 12));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(texto, x, y);
    }

    private static <N> void arista(GraphicsContext gc, double[] desde, N hijo, Function<N, double[]> xy) {
        if (hijo != null) {
            double[] hasta = xy.apply(hijo);
            gc.strokeLine(desde[0], desde[1], hasta[0], hasta[1]);
        }
    }

    /** Recorrido inorden que asigna {columna, profundidad}; retorna la profundidad máxima. */
    private static <N> int ubicar(N n, int profundidad, Function<N, N> izquierdo, Function<N, N> derecho,
                                  Map<N, int[]> posiciones, int[] contador) {
        if (n == null) {
            return profundidad - 1;
        }
        int maxIzq = ubicar(izquierdo.apply(n), profundidad + 1, izquierdo, derecho, posiciones, contador);
        posiciones.put(n, new int[]{contador[0]++, profundidad});
        int maxDer = ubicar(derecho.apply(n), profundidad + 1, izquierdo, derecho, posiciones, contador);
        return Math.max(profundidad, Math.max(maxIzq, maxDer));
    }
}
