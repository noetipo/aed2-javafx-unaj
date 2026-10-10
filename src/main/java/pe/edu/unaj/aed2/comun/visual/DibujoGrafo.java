package pe.edu.unaj.aed2.comun.visual;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Primitivas para dibujar grafos sobre un Canvas: vértices, aristas
 * (dirigidas o no) con peso opcional. Las coordenadas las decide quien llama,
 * por ejemplo a partir de {@link pe.edu.unaj.aed2.comun.mapa.MapaSimplificado}.
 */
public final class DibujoGrafo {

    public static final double RADIO = 16;

    private DibujoGrafo() {
    }

    public static void vertice(GraphicsContext gc, double x, double y, String etiqueta, Color relleno) {
        DibujoArbol.nodo(gc, x, y, etiqueta, relleno);
    }

    /**
     * Dibuja una arista entre los centros de dos vértices.
     *
     * @param peso     texto a mostrar en el punto medio, o {@code null}
     * @param dirigida si es true dibuja una flecha hacia (x2, y2)
     */
    public static void arista(GraphicsContext gc, double x1, double y1, double x2, double y2,
                              String peso, boolean dirigida, Color color, double grosor) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double largo = Math.hypot(dx, dy);
        if (largo < 1e-6) {
            return;
        }
        double ux = dx / largo;
        double uy = dy / largo;
        // Recortar para que la línea empiece y termine en el borde del círculo.
        double ax = x1 + ux * RADIO;
        double ay = y1 + uy * RADIO;
        double bx = x2 - ux * RADIO;
        double by = y2 - uy * RADIO;

        gc.setStroke(color);
        gc.setLineWidth(grosor);
        gc.strokeLine(ax, ay, bx, by);

        if (dirigida) {
            double tam = 10;
            double px = -uy;
            double py = ux;
            gc.setFill(color);
            gc.fillPolygon(
                    new double[]{bx, bx - ux * tam + px * tam / 2, bx - ux * tam - px * tam / 2},
                    new double[]{by, by - uy * tam + py * tam / 2, by - uy * tam - py * tam / 2},
                    3);
        }
        if (peso != null) {
            double mx = (x1 + x2) / 2 - uy * 10;
            double my = (y1 + y2) / 2 + ux * 10;
            gc.setFont(Font.font(11));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.setFill(Paleta.FONDO);
            gc.fillRect(mx - 14, my - 8, 28, 16);
            gc.setFill(Paleta.TEXTO);
            gc.fillText(peso, mx, my);
        }
    }

    public static void arista(GraphicsContext gc, double x1, double y1, double x2, double y2,
                              String peso, boolean dirigida) {
        arista(gc, x1, y1, x2, y2, peso, dirigida, Paleta.BORDE, 1.5);
    }
}
