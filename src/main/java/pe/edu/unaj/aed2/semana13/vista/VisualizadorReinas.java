package pe.edu.unaj.aed2.semana13.vista;

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
import pe.edu.unaj.aed2.semana13.modelo.EstadoReinas;
import pe.edu.unaj.aed2.semana13.modelo.NReinas;

/** Semana 13: backtracking con N reinas. */
public class VisualizadorReinas extends BorderPane {

    private static final Color CLARA = Color.web("#f5f0e6");
    private static final Color OSCURA = Color.web("#c8b99a");

    private final PanelAnimacion<EstadoReinas> panel =
            new PanelAnimacion<>(VisualizadorReinas::dibujar, EstadoReinas::descripcion);
    private final ComboBox<Integer> tamanio = new ComboBox<>();

    public VisualizadorReinas() {
        PanelTraza<EstadoReinas> traza = new PanelTraza<>(panel, NReinas.CODIGO);
        tamanio.getItems().setAll(4, 5, 6, 7, 8);
        tamanio.getSelectionModel().select(Integer.valueOf(6));
        tamanio.setOnAction(e -> panel.setPasos(NReinas.pasos(tamanio.getValue())));
        HBox opciones = new HBox(8, new Label("Tamaño del tablero (n)"), tamanio,
                new Label("Con n = 8 hay más de mil pasos: usa Reproducir con más velocidad."));
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8));
        Label titulo = new Label("Semana 13 · Backtracking: N reinas");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.5);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        panel.setPasos(NReinas.pasos(tamanio.getValue()));
    }

    private static void dibujar(GraphicsContext gc, EstadoReinas paso, double ancho, double alto) {
        int n = paso.tablero().size();
        double lado = Math.min(ancho - 60, alto - 90);
        double celda = lado / n;
        double x0 = (ancho - lado) / 2;
        double y0 = 40;
        for (int f = 0; f < n; f++) {
            for (int c = 0; c < n; c++) {
                Color fondo = (f + c) % 2 == 0 ? CLARA : OSCURA;
                if (f == paso.fila() && c == paso.col()) {
                    fondo = switch (paso.tipo()) {
                        case ATACADA -> Paleta.INTERCAMBIO;
                        case COLOCA -> Paleta.FINALIZADO;
                        case QUITA -> Color.web("#ffcc80");
                        default -> fondo;
                    };
                } else if (paso.tipo() == EstadoReinas.Tipo.RETROCEDE && f == paso.fila()) {
                    fondo = fondo.interpolate(Paleta.INTERCAMBIO, 0.35);
                }
                gc.setFill(fondo);
                gc.fillRect(x0 + c * celda, y0 + f * celda, celda, celda);
            }
            gc.setFill(Paleta.TEXTO);
            gc.setFont(Font.font("System", 12));
            gc.setTextAlign(TextAlignment.RIGHT);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText("fila " + f, x0 - 6, y0 + (f + 0.5) * celda);
        }
        gc.setStroke(Paleta.BORDE);
        gc.setLineWidth(1.5);
        gc.strokeRect(x0, y0, lado, lado);

        if (paso.atacante() >= 0) {
            int fa = paso.atacante();
            int ca = paso.tablero().get(fa);
            gc.setStroke(Paleta.INTERCAMBIO);
            gc.setLineWidth(3);
            gc.strokeLine(x0 + (ca + 0.5) * celda, y0 + (fa + 0.5) * celda,
                    x0 + (paso.col() + 0.5) * celda, y0 + (paso.fila() + 0.5) * celda);
        }
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        for (int f = 0; f < n; f++) {
            int c = paso.tablero().get(f);
            if (c >= 0) {
                gc.setFill(f == paso.atacante() ? Paleta.INTERCAMBIO : Paleta.TEXTO);
                gc.setFont(Font.font("System", FontWeight.BOLD, celda * 0.6));
                gc.fillText("♛", x0 + (c + 0.5) * celda, y0 + (f + 0.5) * celda);
            }
        }

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(Font.font("System", 12));
        gc.setFill(Color.web("#546e7a"));
        gc.fillText("Verde: se coloca · Rojo: casilla atacada (la línea une a la reina que ataca) · Naranja: se quita"
                + " (backtracking)", 10, 14);
        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font("System", FontWeight.BOLD, 13));
        gc.fillText("Casillas probadas: " + paso.intentos() + "  ·  reinas puestas: "
                + paso.tablero().stream().filter(c -> c >= 0).count() + " de " + n, 10, alto - 14);
    }
}
