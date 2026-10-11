package pe.edu.unaj.aed2.semana10.vista;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pe.edu.unaj.aed2.comun.visual.DibujoArbol;
import pe.edu.unaj.aed2.comun.visual.DibujoGrafo;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.semana07.vista.DibujoRed.Trazo;
import pe.edu.unaj.aed2.semana10.modelo.GrafoDirigido;
import pe.edu.unaj.aed2.semana10.modelo.MallaCurricular;

import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Dibuja la malla curricular de izquierda a derecha: la columna de cada curso es su nivel (la cadena de
 * prerrequisitos más larga que llega a él) y la fila viene del CSV. Así todos los arcos apuntan a la derecha.
 */
public final class DibujoMalla {

    private static final double MARGEN_X = 50;
    private static final double MARGEN_Y = 34;
    private static final double RADIO = DibujoGrafo.RADIO;

    private DibujoMalla() {
    }

    /**
     * @param grafo el grafo a dibujar (puede tener arcos de más, como el prerrequisito mal digitado); las posiciones
     *              salen siempre de la malla original
     */
    public static void dibujar(GraphicsContext gc, MallaCurricular malla, GrafoDirigido grafo,
                               Function<String, Color> relleno, Function<String, Color> borde,
                               BiFunction<String, String, Trazo> arco, Function<String, String> nota,
                               double ancho, double alto) {
        Map<String, Integer> niveles = malla.grafo().niveles();
        int columnas = niveles.values().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
        int filas = malla.cursos().values().stream().mapToInt(MallaCurricular.Curso::fila).max().orElse(0) + 1;
        Function<String, double[]> posicion = v -> new double[]{
                MARGEN_X + niveles.get(v) * (ancho - 2 * MARGEN_X) / Math.max(1, columnas - 1),
                MARGEN_Y + malla.cursos().get(v).fila() * (alto - 2 * MARGEN_Y - 14) / Math.max(1, filas - 1)};

        for (String[] a : grafo.arcos()) {
            double[] p = posicion.apply(a[0]);
            double[] q = posicion.apply(a[1]);
            // Si también existe el arco contrario, se separan para que se vean los dos.
            double dx = 0;
            double dy = 0;
            if (grafo.sucesores(a[1]).contains(a[0])) {
                double largo = Math.hypot(q[0] - p[0], q[1] - p[1]);
                dx = -(q[1] - p[1]) / largo * 7;
                dy = (q[0] - p[0]) / largo * 7;
            }
            Trazo t = arco.apply(a[0], a[1]);
            DibujoGrafo.arista(gc, p[0] + dx, p[1] + dy, q[0] + dx, q[1] + dy, null, true, t.color(), t.grosor());
        }
        for (String v : grafo.vertices()) {
            double[] p = posicion.apply(v);
            Color b = borde.apply(v);
            if (b != null) {
                gc.setFill(b);
                gc.fillOval(p[0] - RADIO - 5, p[1] - RADIO - 5, 2 * RADIO + 10, 2 * RADIO + 10);
            }
            Color fondo = relleno.apply(v);
            DibujoArbol.nodo(gc, p[0], p[1], "", fondo, RADIO);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.setFill(fondo.getBrightness() < 0.5 ? Color.WHITE : Paleta.TEXTO);
            gc.setFont(Font.font("System", FontWeight.BOLD, 10));
            gc.fillText(v, p[0], p[1]);
            gc.setFill(Paleta.TEXTO);
            gc.setFont(Font.font("System", FontWeight.NORMAL, 11));
            gc.setTextBaseline(VPos.TOP);
            gc.fillText(malla.cursos().get(v).corto(), p[0], p[1] + RADIO + 2);
            String texto = nota.apply(v);
            if (texto != null && !texto.isEmpty()) {
                gc.setFill(Color.web("#1565c0"));
                gc.setFont(Font.font("System", FontWeight.BOLD, 12));
                gc.setTextBaseline(VPos.BOTTOM);
                gc.fillText(texto, p[0], p[1] - RADIO - 2);
            }
        }
    }
}
