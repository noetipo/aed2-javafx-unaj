package pe.edu.unaj.aed2.semana17.vista;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pe.edu.unaj.aed2.comun.visual.DibujoGrafo;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana07.vista.DibujoRed;

import java.util.List;
import java.util.Set;
import java.util.function.Function;

/** Dibuja la ruta del dron (líneas rectas con flecha) sobre el mapa de los pueblos, con las carreteras de fondo. */
public final class DibujoRuta {

    private DibujoRuta() {
    }

    /**
     * @param ruta     pueblos en orden; si {@code cerrada}, se dibuja también el regreso al primero
     * @param quitados tramos "U-V" que se acaban de quitar (2-opt), se dibujan punteados en rojo
     * @param nuevos   tramos "U-V" que se acaban de agregar, se dibujan en verde
     */
    public static void dibujar(GraphicsContext gc, RedVial red, List<String> ruta, boolean cerrada,
                               Set<String> quitados, Set<String> nuevos, Function<String, Color> relleno,
                               Function<String, Color> borde, Function<String, Integer> distancia, double ancho,
                               double alto) {
        DibujoRed.dibujar(gc, red, relleno, borde, (u, v) -> new DibujoRed.Trazo(Color.web("#eceff1"), 1.2),
                v -> "", ancho, alto);
        for (String c : quitados) {
            String[] uv = c.split("-");
            double[] p = DibujoRed.posicion(red, uv[0], ancho, alto);
            double[] q = DibujoRed.posicion(red, uv[1], ancho, alto);
            gc.setLineDashes(8, 6);
            DibujoGrafo.arista(gc, p[0], p[1], q[0], q[1], null, false, Paleta.INTERCAMBIO, 3);
            gc.setLineDashes((double[]) null);
        }
        int tramos = cerrada ? ruta.size() : ruta.size() - 1;
        for (int k = 0; k < tramos; k++) {
            String u = ruta.get(k);
            String v = ruta.get((k + 1) % ruta.size());
            double[] p = DibujoRed.posicion(red, u, ancho, alto);
            double[] q = DibujoRed.posicion(red, v, ancho, alto);
            boolean nuevo = nuevos.contains(u + "-" + v) || nuevos.contains(v + "-" + u);
            DibujoGrafo.arista(gc, p[0], p[1], q[0], q[1], String.valueOf(distancia.apply(u + "-" + v)), true,
                    nuevo ? Color.web("#2e7d32") : Color.web("#1e88e5"), nuevo ? 4.5 : 3);
        }
        for (int k = 0; k < ruta.size(); k++) {
            double[] p = DibujoRed.posicion(red, ruta.get(k), ancho, alto);
            gc.setFill(Paleta.TEXTO);
            gc.setFont(Font.font("System", FontWeight.BOLD, 11));
            gc.setTextAlign(TextAlignment.LEFT);
            gc.setTextBaseline(VPos.BOTTOM);
            gc.fillText(k == 0 ? "base" : String.valueOf(k), p[0] + 13, p[1] - 10);
        }
    }
}
