package pe.edu.unaj.aed2.semana07.vista;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pe.edu.unaj.aed2.comun.mapa.MapaSimplificado;
import pe.edu.unaj.aed2.comun.mapa.MapaSimplificado.Lugar;
import pe.edu.unaj.aed2.comun.visual.DibujoArbol;
import pe.edu.unaj.aed2.comun.visual.DibujoGrafo;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.semana07.modelo.EstadoRecorrido;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Dibuja la red vial sobre su posición geográfica aproximada, con el lago Titicaca como referencia.
 * Quien llama decide el color de cada pueblo y de cada carretera.
 */
public final class DibujoRed {

    /** Cómo pintar una carretera: color y grosor. */
    public record Trazo(Color color, double grosor) {
    }

    private static final double MARGEN = 40;
    private static final double RADIO = 14;

    private DibujoRed() {
    }

    public static void dibujar(GraphicsContext gc, RedVial red, Function<String, Color> relleno,
                               Function<String, Color> borde, BiFunction<String, String, Trazo> carretera,
                               Function<String, String> nota, double ancho, double alto) {
        List<Lugar> lugares = red.lugares();
        lago(gc, lugares, ancho, alto);

        for (String[] a : red.grafo().aristas()) {
            double[] p = posicion(red, a[0], ancho, alto);
            double[] q = posicion(red, a[1], ancho, alto);
            Trazo t = carretera.apply(a[0], a[1]);
            DibujoGrafo.arista(gc, p[0], p[1], q[0], q[1], null, false, t.color(), t.grosor());
        }
        for (String v : red.grafo().vertices()) {
            double[] p = posicion(red, v, ancho, alto);
            Color b = borde.apply(v);
            if (b != null) {
                gc.setFill(b);
                gc.fillOval(p[0] - RADIO - 5, p[1] - RADIO - 5, 2 * RADIO + 10, 2 * RADIO + 10);
            }
            Color fondo = relleno.apply(v);
            DibujoArbol.nodo(gc, p[0], p[1], "", fondo, RADIO);
            gc.setFill(fondo.getBrightness() < 0.5 ? Color.WHITE : Paleta.TEXTO);
            gc.setFont(Font.font("System", FontWeight.BOLD, 10));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText(v, p[0], p[1]);
            gc.setFill(Paleta.TEXTO);
            gc.setFont(Font.font("System", FontWeight.NORMAL, 12));
            gc.setTextBaseline(VPos.TOP);
            gc.fillText(red.pueblos().get(v).nombre(), p[0], p[1] + RADIO + 2);
            String texto = nota.apply(v);
            if (texto != null && !texto.isEmpty()) {
                gc.setFill(Color.web("#1565c0"));
                gc.setFont(Font.font("System", FontWeight.BOLD, 12));
                gc.setTextBaseline(VPos.BOTTOM);
                gc.fillText(texto, p[0], p[1] - RADIO - 2);
            }
        }
    }

    public static double[] posicion(RedVial red, String v, double ancho, double alto) {
        return MapaSimplificado.aPantalla(red.pueblos().get(v), red.lugares(), ancho, alto, MARGEN);
    }

    /** Elipse aproximada del lago Titicaca (lado peruano y boliviano), solo como referencia visual. */
    private static void lago(GraphicsContext gc, List<Lugar> lugares, double ancho, double alto) {
        double[] noroeste = MapaSimplificado.aPantalla(new Lugar("", "", -15.30, -69.95), lugares, ancho, alto, MARGEN);
        double[] sureste = MapaSimplificado.aPantalla(new Lugar("", "", -16.15, -68.75), lugares, ancho, alto, MARGEN);
        gc.setFill(Color.web("#e1f5fe"));
        gc.fillOval(noroeste[0], noroeste[1], sureste[0] - noroeste[0], sureste[1] - noroeste[1]);
        gc.setFill(Color.web("#4fc3f7"));
        gc.setFont(Font.font("System", FontPosture.ITALIC, 13));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("Lago Titicaca (referencial)", (noroeste[0] + sureste[0]) / 2, (noroeste[1] + sureste[1]) / 2);
    }

    /** Colores estándar del recorrido: blanco sin visitar, gris en frontera, oscuro terminado. */
    public static Color colorEstado(EstadoRecorrido.Estado e) {
        return switch (e) {
            case NO_VISITADO -> Color.WHITE;
            case EN_FRONTERA -> Color.web("#b0bec5");
            case TERMINADO -> Color.web("#37474f");
        };
    }
}
