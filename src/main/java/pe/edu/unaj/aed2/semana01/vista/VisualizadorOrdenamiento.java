package pe.edu.unaj.aed2.semana01.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import pe.edu.unaj.aed2.comun.visual.ControlesPaso;
import pe.edu.unaj.aed2.comun.visual.PanelAnimacion;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.comun.visual.PanelTraza;
import pe.edu.unaj.aed2.semana01.modelo.EstadoArreglo;
import pe.edu.unaj.aed2.semana01.modelo.Ordenamientos;

import java.util.List;

/** Semana 01: animación paso a paso de burbuja e inserción. */
public class VisualizadorOrdenamiento extends BorderPane {

    private final PanelAnimacion<EstadoArreglo> panel =
            new PanelAnimacion<>(VisualizadorOrdenamiento::dibujar, EstadoArreglo::descripcion);
    private final PanelTraza<EstadoArreglo> traza = new PanelTraza<>(panel, Ordenamientos.PSEUDO_BURBUJA);
    private final ComboBox<String> algoritmo = new ComboBox<>();
    private final Spinner<Integer> tamanio = new Spinner<>(4, 30, 12);
    private final Label estadisticas = new Label();
    private long semilla = 7;

    public VisualizadorOrdenamiento() {
        algoritmo.getItems().addAll("Burbuja", "Inserción");
        algoritmo.getSelectionModel().selectFirst();
        tamanio.setPrefWidth(80);

        Button generar = new Button("Nuevo arreglo");
        generar.setOnAction(e -> {
            semilla++;
            cargar();
        });
        algoritmo.setOnAction(e -> cargar());
        tamanio.valueProperty().addListener((o, a, b) -> cargar());

        HBox opciones = new HBox(8, new Label("Algoritmo"), algoritmo,
                new Label("n ="), tamanio, generar, estadisticas);
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8));

        Label titulo = new Label("Semana 01 · Ordenamientos elementales O(n²)");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.6);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        cargar();
    }

    private void cargar() {
        int[] datos = Ordenamientos.aleatorio(tamanio.getValue(), semilla);
        boolean burbuja = algoritmo.getValue().equals("Burbuja");
        List<EstadoArreglo> pasos = burbuja ? Ordenamientos.pasosBurbuja(datos) : Ordenamientos.pasosInsercion(datos);
        traza.setPseudocodigo(burbuja ? Ordenamientos.PSEUDO_BURBUJA : Ordenamientos.PSEUDO_INSERCION);
        long comparaciones = pasos.stream().filter(p -> p.tipo() == EstadoArreglo.Tipo.COMPARACION).count();
        long intercambios = pasos.stream().filter(p -> p.tipo() == EstadoArreglo.Tipo.INTERCAMBIO).count();
        estadisticas.setText("Comparaciones: " + comparaciones + " · Intercambios: " + intercambios);
        panel.setPasos(pasos);
    }

    private static void dibujar(GraphicsContext gc, EstadoArreglo paso, double ancho, double alto) {
        int[] v = paso.valores();
        int max = 1;
        for (int x : v) {
            max = Math.max(max, x);
        }
        double margen = 20;
        double anchoBarra = (ancho - 2 * margen) / v.length;
        double altoUtil = alto - 2 * margen - 16;
        gc.setFont(Font.font(11));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.BOTTOM);
        for (int i = 0; i < v.length; i++) {
            double h = altoUtil * v[i] / max;
            double x = margen + i * anchoBarra;
            double y = alto - margen - h;
            gc.setFill(color(paso, i));
            gc.fillRect(x + 2, y, anchoBarra - 4, h);
            gc.setStroke(Paleta.BORDE);
            gc.strokeRect(x + 2, y, anchoBarra - 4, h);
            gc.setFill(Paleta.TEXTO);
            gc.fillText(String.valueOf(v[i]), x + anchoBarra / 2, y - 2);
        }
    }

    private static Color color(EstadoArreglo paso, int i) {
        if (i == paso.a() || i == paso.b()) {
            return paso.tipo() == EstadoArreglo.Tipo.INTERCAMBIO ? Paleta.INTERCAMBIO : Paleta.COMPARANDO;
        }
        return paso.estaOrdenado(i) ? Paleta.FINALIZADO : Paleta.NORMAL;
    }
}
