package pe.edu.unaj.aed2.semana08.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
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
import pe.edu.unaj.aed2.semana07.vista.DibujoRed;
import pe.edu.unaj.aed2.semana08.modelo.EstadoDijkstra;
import pe.edu.unaj.aed2.semana08.modelo.GrafoPonderado;

import java.util.Locale;
import java.util.Map;

/** Semana 08: Dijkstra paso a paso sobre la red vial de la región Puno, con km estimados. */
public class VisualizadorDijkstra extends BorderPane {

    private static final RedVial RED = RedVial.cargar();
    private static final GrafoPonderado GRAFO = GrafoPonderado.desde(RED);
    private static final Map<String, String> NOMBRES = RED.nombres();

    private final PanelAnimacion<EstadoDijkstra> panel =
            new PanelAnimacion<>(VisualizadorDijkstra::dibujar, EstadoDijkstra::descripcion);
    private final PanelTraza<EstadoDijkstra> traza = new PanelTraza<>(panel, GrafoPonderado.codigo(GRAFO));
    private final ComboBox<String> origen = new ComboBox<>();

    public VisualizadorDijkstra() {
        GRAFO.vertices().forEach(v -> origen.getItems().add(NOMBRES.get(v) + " (" + v + ")"));
        origen.getSelectionModel().selectFirst();
        origen.setOnAction(e -> cargar());
        HBox opciones = new HBox(8, new Label("Origen"), origen, new Label(String.format(Locale.ROOT,
                "km estimados = línea recta × %.2f (red simplificada y didáctica).", GrafoPonderado.SINUOSIDAD)));
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8));

        Label titulo = new Label("Semana 08 · Caminos más cortos: Dijkstra");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.55);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        cargar();
    }

    private void cargar() {
        String texto = origen.getValue();
        String s = texto.substring(texto.lastIndexOf('(') + 1, texto.length() - 1);
        panel.setPasos(GRAFO.pasosDijkstra(s, NOMBRES));
    }

    private static void dibujar(GraphicsContext gc, EstadoDijkstra paso, double ancho, double alto) {
        double abajo = 30;
        double arriba = 30;
        gc.save();
        gc.translate(0, arriba);
        DibujoRed.dibujar(gc, RED,
                v -> paso.cerrados().contains(v) ? Color.web("#37474f")
                        : paso.distancias().containsKey(v) ? Color.web("#b0bec5") : Color.WHITE,
                v -> v.equals(paso.actual()) ? Paleta.COMPARANDO : v.equals(paso.vecino()) ? Paleta.RESALTADO : null,
                (u, v) -> {
                    boolean examinada = (u.equals(paso.actual()) && v.equals(paso.vecino()))
                            || (v.equals(paso.actual()) && u.equals(paso.vecino()));
                    if (examinada) {
                        return new DibujoRed.Trazo(paso.tipo() == EstadoDijkstra.Tipo.MEJORA ? Paleta.COMPARANDO
                                : Color.web("#e57373"), 4);
                    }
                    if (u.equals(paso.padres().get(v)) || v.equals(paso.padres().get(u))) {
                        return new DibujoRed.Trazo(Color.web("#2e7d32"), 4);
                    }
                    return new DibujoRed.Trazo(Color.web("#b0bec5"), 1.5);
                },
                (u, v) -> String.valueOf(GRAFO.km(u, v)),
                v -> paso.distancias().containsKey(v) ? paso.distancias().get(v) + " km" : "∞",
                ancho, alto - arriba - abajo);
        gc.restore();
        gc.setFill(Paleta.TEXTO);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("System", FontWeight.BOLD, 13));
        gc.fillText("Cola de prioridad (de menor a mayor): " + (paso.cola().isEmpty() ? "vacía"
                : String.join(" ", paso.cola())), 10, alto - 14);
        gc.setFont(Font.font("System", FontWeight.NORMAL, 12));
        gc.setFill(Color.web("#546e7a"));
        gc.fillText("Blanco: ∞ · Gris: distancia provisional · Oscuro: cerrado (distancia definitiva)", 10, 12);
        gc.fillText("Verde: árbol de caminos mínimos · Naranja: la arista mejora · Rojo claro: no mejora", 10, 28);
    }
}
