package pe.edu.unaj.aed2.semana14.vista;

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
import pe.edu.unaj.aed2.semana14.modelo.EstadoMochila;
import pe.edu.unaj.aed2.semana14.modelo.Mochila;
import pe.edu.unaj.aed2.semana14.modelo.Mochila.Producto;

import java.util.List;

/** Semana 14: programación dinámica con la mochila 0/1 (lo que un comerciante lleva a la feria). */
public class VisualizadorMochila extends BorderPane {

    private static final List<Producto> PRODUCTOS = Mochila.FERIA;
    private static final int C = Mochila.CAPACIDAD_FERIA;

    private final PanelAnimacion<EstadoMochila> panel =
            new PanelAnimacion<>(VisualizadorMochila::dibujar, EstadoMochila::descripcion);

    public VisualizadorMochila() {
        PanelTraza<EstadoMochila> traza = new PanelTraza<>(panel, Mochila.CODIGO);
        Mochila.Resultado voraz = Mochila.voraz(PRODUCTOS, C);
        HBox opciones = new HBox(8, new Label("Mototaxi de carga: " + C + " decenas de kg. Ganancias en soles"
                + " (ficticias). La regla voraz por ganancia/peso lograría " + voraz.mejor() + "."));
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8));
        Label titulo = new Label("Semana 14 · Programación dinámica: mochila 0/1");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.55);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        panel.setPasos(Mochila.pasos(PRODUCTOS, C));
    }

    private static void dibujar(GraphicsContext gc, EstadoMochila paso, double ancho, double alto) {
        int filas = PRODUCTOS.size() + 1;
        double izquierda = 150;
        double arriba = 64;
        double celda = Math.max(24, Math.min(48, Math.min((ancho - izquierda - 20) / (C + 1),
                (alto - arriba - 90) / filas)));
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("System", 12));
        gc.setFill(Color.web("#546e7a"));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Amarillo: casilla que se calcula · Azul: dp[i−1][c] (no llevar) · Naranja: dp[i−1][c−peso]"
                + " (llevar)", 10, 14);
        gc.fillText("Verde: camino de reconstrucción y productos elegidos", 10, 30);
        gc.setFont(Font.font("System", FontWeight.BOLD, 12));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Paleta.TEXTO);
        gc.fillText("capacidad c →", izquierda + (C + 1) * celda / 2, arriba - 30);
        for (int c = 0; c <= C; c++) {
            gc.fillText(String.valueOf(c), izquierda + (c + 0.5) * celda, arriba - 12);
        }
        for (int i = 0; i < filas; i++) {
            double y = arriba + i * celda;
            gc.setTextAlign(TextAlignment.RIGHT);
            boolean elegido = i >= 1 && paso.elegidos().contains(i - 1);
            gc.setFill(elegido ? Color.web("#2e7d32") : Paleta.TEXTO);
            gc.setFont(Font.font("System", elegido ? FontWeight.BOLD : FontWeight.NORMAL, 12));
            String rotulo = i == 0 ? "0 · (ninguno)" : i + " · " + PRODUCTOS.get(i - 1).nombre();
            gc.fillText(rotulo, izquierda - 8, y + celda / 2 - (i == 0 ? 0 : 6));
            if (i >= 1) {
                gc.setFont(Font.font("System", 10));
                gc.fillText("peso " + PRODUCTOS.get(i - 1).peso() + " · S/ " + PRODUCTOS.get(i - 1).valor(),
                        izquierda - 8, y + celda / 2 + 8);
            }
            for (int c = 0; c <= C; c++) {
                double x = izquierda + c * celda;
                Color fondo = Color.WHITE;
                if (paso.recorrido().contains(i + "," + c)) {
                    fondo = Color.web("#c8e6c9");
                }
                if (paso.i() >= 1 && paso.tipo() != EstadoMochila.Tipo.RECONSTRUYE) {
                    if (i == paso.i() - 1 && c == paso.c()) {
                        fondo = Color.web("#bbdefb");
                    } else if (i == paso.i() - 1 && c == paso.diagonal()) {
                        fondo = Paleta.COMPARANDO;
                    }
                }
                if (i == paso.i() && c == paso.c()) {
                    fondo = paso.tipo() == EstadoMochila.Tipo.RECONSTRUYE ? Paleta.FINALIZADO : Paleta.RESALTADO;
                }
                gc.setFill(fondo);
                gc.fillRect(x, y, celda, celda);
                gc.setStroke(Color.web("#b0bec5"));
                gc.setLineWidth(1);
                gc.strokeRect(x, y, celda, celda);
                int v = paso.dp().get(i).get(c);
                if (v >= 0) {
                    gc.setFill(Paleta.TEXTO);
                    gc.setTextAlign(TextAlignment.CENTER);
                    gc.setFont(Font.font("System", 13));
                    gc.fillText(String.valueOf(v), x + celda / 2, y + celda / 2);
                }
            }
        }
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font("System", FontWeight.BOLD, 13));
        int mejor = paso.dp().get(filas - 1).get(C);
        gc.fillText(mejor >= 0 ? "Mejor ganancia: S/ " + mejor + (paso.elegidos().isEmpty() ? ""
                : "  ·  llevar: " + String.join(", ", paso.elegidos().stream().sorted()
                .map(k -> PRODUCTOS.get(k).nombre()).toList())) : "Llenando la tabla fila por fila…",
                10, arriba + filas * celda + 26);
    }
}
