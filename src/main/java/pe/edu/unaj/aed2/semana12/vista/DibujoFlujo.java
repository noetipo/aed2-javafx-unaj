package pe.edu.unaj.aed2.semana12.vista;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pe.edu.unaj.aed2.comun.mapa.MapaSimplificado;
import pe.edu.unaj.aed2.comun.visual.DibujoArbol;
import pe.edu.unaj.aed2.comun.visual.DibujoGrafo;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana07.vista.DibujoRed.Trazo;
import pe.edu.unaj.aed2.semana12.modelo.RedFlujo;

import java.util.Map;
import java.util.function.Function;

/**
 * Dibuja una red de flujo sobre la posición aproximada de los pueblos: cada arco es una flecha con "flujo/capacidad".
 * Quien llama decide el color y el grosor de cada arco y el relleno de cada pueblo.
 */
public final class DibujoFlujo {

    private static final double MARGEN = 50;
    private static final RedVial PUEBLOS = RedVial.cargar();

    private DibujoFlujo() {
    }

    public static double[] posicion(RedFlujo red, String v, double ancho, double alto) {
        RedVial zona = PUEBLOS.zona(red.vertices().stream().filter(PUEBLOS.pueblos()::containsKey).toList());
        return MapaSimplificado.aPantalla(PUEBLOS.pueblos().get(v), zona.lugares(), ancho, alto, MARGEN);
    }

    public static void dibujar(GraphicsContext gc, RedFlujo red, Map<String, Integer> flujo,
                               Function<RedFlujo.Arco, Trazo> arco, Function<String, Color> relleno,
                               Function<String, Color> borde, double ancho, double alto) {
        for (RedFlujo.Arco a : red.arcos()) {
            if (!PUEBLOS.pueblos().containsKey(a.u()) || !PUEBLOS.pueblos().containsKey(a.v())) {
                continue; // p. ej., la fuente ficticia del caso: no tiene lugar en el mapa
            }
            double[] p = posicion(red, a.u(), ancho, alto);
            double[] q = posicion(red, a.v(), ancho, alto);
            Trazo t = arco.apply(a);
            DibujoGrafo.arista(gc, p[0], p[1], q[0], q[1], null, true, t.color(), t.grosor());
            double mx = (p[0] + q[0]) / 2;
            double my = (p[1] + q[1]) / 2;
            String texto = flujo.getOrDefault(a.clave(), 0) + "/" + a.capacidad();
            gc.setFont(Font.font("System", FontWeight.BOLD, 12));
            gc.setFill(Paleta.FONDO);
            gc.fillRoundRect(mx - 20, my - 9, 40, 18, 8, 8);
            gc.setStroke(t.color());
            gc.setLineWidth(1);
            gc.strokeRoundRect(mx - 20, my - 9, 40, 18, 8, 8);
            gc.setFill(Paleta.TEXTO);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText(texto, mx, my);
        }
        for (String v : red.vertices()) {
            if (!PUEBLOS.pueblos().containsKey(v)) {
                continue;
            }
            double[] p = posicion(red, v, ancho, alto);
            Color b = borde.apply(v);
            if (b != null) {
                gc.setFill(b);
                gc.fillOval(p[0] - DibujoGrafo.RADIO - 5, p[1] - DibujoGrafo.RADIO - 5, 2 * DibujoGrafo.RADIO + 10,
                        2 * DibujoGrafo.RADIO + 10);
            }
            Color fondo = relleno.apply(v);
            DibujoArbol.nodo(gc, p[0], p[1], "", fondo, DibujoGrafo.RADIO);
            gc.setFill(fondo.getBrightness() < 0.5 ? Color.WHITE : Paleta.TEXTO);
            gc.setFont(Font.font("System", FontWeight.BOLD, 10));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText(v, p[0], p[1]);
            gc.setFill(Paleta.TEXTO);
            gc.setFont(Font.font("System", FontWeight.NORMAL, 12));
            gc.setTextBaseline(VPos.TOP);
            gc.fillText(PUEBLOS.pueblos().get(v).nombre(), p[0], p[1] + DibujoGrafo.RADIO + 2);
        }
    }
}
