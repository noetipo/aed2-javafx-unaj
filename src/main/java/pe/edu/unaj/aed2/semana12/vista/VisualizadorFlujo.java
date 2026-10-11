package pe.edu.unaj.aed2.semana12.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pe.edu.unaj.aed2.comun.visual.ControlesPaso;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.comun.visual.PanelAnimacion;
import pe.edu.unaj.aed2.comun.visual.PanelTraza;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana07.vista.DibujoRed.Trazo;
import pe.edu.unaj.aed2.semana12.modelo.EstadoFlujo;
import pe.edu.unaj.aed2.semana12.modelo.RedFlujo;

import java.util.Map;

/** Semana 12: flujo máximo con Edmonds–Karp, de Ayaviri a la planta de Juliaca. */
public class VisualizadorFlujo extends BorderPane {

    private static final RedFlujo RED = RedFlujo.cargarLeche();
    private static final Map<String, String> NOMBRES = RedVial.cargar().nombres();

    private final PanelAnimacion<EstadoFlujo> panel =
            new PanelAnimacion<>(VisualizadorFlujo::dibujar, EstadoFlujo::descripcion);

    public VisualizadorFlujo() {
        PanelTraza<EstadoFlujo> traza = new PanelTraza<>(panel, RedFlujo.codigo(RED));
        HBox opciones = new HBox(8, new Label("De Ayaviri (cuenca lechera) a la planta de Juliaca. Capacidades en"
                + " camiones por día: valores ficticios, red didáctica."));
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8));
        Label titulo = new Label("Semana 12 · Flujo máximo: Edmonds–Karp");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.55);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        panel.setPasos(RED.pasos("AYA", "JUL", NOMBRES));
    }

    private static void dibujar(GraphicsContext gc, EstadoFlujo paso, double ancho, double alto) {
        double arriba = 34;
        double abajo = 30;
        boolean fin = paso.tipo() == EstadoFlujo.Tipo.FIN;
        gc.save();
        gc.translate(0, arriba);
        DibujoFlujo.dibujar(gc, RED, paso.flujo(),
                a -> {
                    int f = paso.flujo().getOrDefault(a.clave(), 0);
                    if (fin && paso.ladoS().contains(a.u()) && !paso.ladoS().contains(a.v())) {
                        return new Trazo(Paleta.INTERCAMBIO, 6);
                    }
                    if (a.clave().equals(paso.cuello()) || reves(a).equals(paso.cuello())) {
                        return new Trazo(Paleta.INTERCAMBIO, 6);
                    }
                    if (paso.haciaAtras().contains(a.clave())) {
                        return new Trazo(Paleta.VISITADO, 6);
                    }
                    if (enCamino(paso, a)) {
                        return new Trazo(Paleta.COMPARANDO, 6);
                    }
                    return f == 0 ? new Trazo(Color.web("#b0bec5"), 1.5)
                            : new Trazo(Color.web("#1e88e5"), 1.5 + 4.0 * f / a.capacidad());
                },
                v -> v.equals("AYA") ? Paleta.FINALIZADO : v.equals("JUL") ? Paleta.RESALTADO
                        : fin && paso.ladoS().contains(v) ? Color.web("#c8e6c9") : Color.web("#e3f2fd"),
                v -> fin && paso.ladoS().contains(v) ? Color.web("#2e7d32") : null,
                ancho, alto - arriba - abajo);
        gc.restore();

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("System", FontWeight.NORMAL, 12));
        gc.setFill(Color.web("#546e7a"));
        gc.fillText("Azul: lleva flujo (más grueso = más lleno) · Naranja: camino de aumento · Morado: arco recorrido"
                + " al revés (devuelve)", 10, 12);
        gc.fillText("Rojo: cuello de botella (al final: el corte mínimo) · Etiqueta: flujo/capacidad", 10, 28);
        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font("System", FontWeight.BOLD, 14));
        gc.fillText("Flujo total: " + paso.total() + " camiones por día"
                + (paso.iteracion() > 0 ? "   ·   camino de aumento n.º " + paso.iteracion() : ""), 10, alto - 14);
    }

    private static String reves(RedFlujo.Arco a) {
        return EstadoFlujo.arco(a.v(), a.u());
    }

    private static boolean enCamino(EstadoFlujo paso, RedFlujo.Arco a) {
        for (int k = 1; k < paso.camino().size(); k++) {
            if (paso.camino().get(k - 1).equals(a.u()) && paso.camino().get(k).equals(a.v())) {
                return true;
            }
        }
        return false;
    }
}
