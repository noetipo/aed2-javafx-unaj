package pe.edu.unaj.aed2.semana02.vista;

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
import pe.edu.unaj.aed2.semana02.modelo.EstadoParticion;
import pe.edu.unaj.aed2.semana02.modelo.QuickSort;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/** Semana 02: quicksort paso a paso, mostrando el rango de cada llamada recursiva. */
public class VisualizadorQuickSort extends BorderPane {

    private final PanelAnimacion<EstadoParticion> panel =
            new PanelAnimacion<>(VisualizadorQuickSort::dibujar, EstadoParticion::descripcion);
    private final PanelTraza<EstadoParticion> traza = new PanelTraza<>(panel, QuickSort.CODIGO);
    private final ComboBox<String> entrada = new ComboBox<>();
    private final Spinner<Integer> tamanio = new Spinner<>(4, 24, 10);
    private final Label estadisticas = new Label();
    private long semilla = 11;

    public VisualizadorQuickSort() {
        entrada.getItems().addAll("Aleatorio", "Ya ordenado (peor caso)", "Invertido");
        entrada.getSelectionModel().selectFirst();
        tamanio.setPrefWidth(80);

        Button generar = new Button("Nuevo arreglo");
        generar.setOnAction(e -> {
            semilla++;
            cargar();
        });
        entrada.setOnAction(e -> cargar());
        tamanio.valueProperty().addListener((o, a, b) -> cargar());

        HBox opciones = new HBox(8, new Label("Entrada"), entrada,
                new Label("n ="), tamanio, generar, estadisticas);
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8));

        Label titulo = new Label("Semana 02 · Quicksort: divide y vencerás");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.5);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        cargar();
    }

    private void cargar() {
        int n = tamanio.getValue();
        int[] datos = new Random(semilla).ints(n, 1, 100).toArray();
        switch (entrada.getValue()) {
            case "Ya ordenado (peor caso)" -> Arrays.sort(datos);
            case "Invertido" -> {
                Arrays.sort(datos);
                for (int i = 0; i < n / 2; i++) {
                    int t = datos[i];
                    datos[i] = datos[n - 1 - i];
                    datos[n - 1 - i] = t;
                }
            }
            default -> {
            }
        }
        List<EstadoParticion> pasos = QuickSort.pasos(datos);
        long comparaciones = pasos.stream().filter(p -> p.tipo() == EstadoParticion.Tipo.COMPARACION).count();
        estadisticas.setText("Comparaciones: " + comparaciones
                + " · Profundidad de recursión: " + QuickSort.profundidadMaxima(datos)
                + " (log₂ n ≈ " + Math.round(Math.log(n) / Math.log(2)) + ")");
        panel.setPasos(pasos);
    }

    private static void dibujar(GraphicsContext gc, EstadoParticion paso, double ancho, double alto) {
        int[] v = paso.valores();
        int max = Arrays.stream(v).max().orElse(1);
        double margen = 20;
        double anchoBarra = (ancho - 2 * margen) / v.length;
        double altoUtil = alto - 2 * margen - 40;

        // Rango de la llamada recursiva actual.
        if (paso.inicio() >= 0) {
            double x0 = margen + paso.inicio() * anchoBarra;
            double x1 = margen + (paso.fin() + 1) * anchoBarra;
            gc.setFill(Color.web("#e3f2fd"));
            gc.fillRect(x0, margen, x1 - x0, alto - 2 * margen);
            gc.setFill(Paleta.TEXTO);
            gc.setFont(Font.font(12));
            gc.setTextAlign(TextAlignment.LEFT);
            gc.setTextBaseline(VPos.TOP);
            gc.fillText("quicksort(" + paso.inicio() + ", " + paso.fin() + ") · nivel " + paso.profundidad(),
                    x0 + 4, margen + 2);
        }

        gc.setFont(Font.font(11));
        gc.setTextAlign(TextAlignment.CENTER);
        for (int k = 0; k < v.length; k++) {
            double h = altoUtil * v[k] / max;
            double x = margen + k * anchoBarra;
            double y = alto - margen - h;
            gc.setFill(color(paso, k));
            gc.fillRect(x + 2, y, anchoBarra - 4, h);
            gc.setStroke(Paleta.BORDE);
            gc.strokeRect(x + 2, y, anchoBarra - 4, h);
            gc.setFill(Paleta.TEXTO);
            gc.setTextBaseline(VPos.BOTTOM);
            gc.fillText(String.valueOf(v[k]), x + anchoBarra / 2, y - 2);
            if (k == paso.i() && paso.tipo() != EstadoParticion.Tipo.LLAMADA) {
                gc.setTextBaseline(VPos.TOP);
                gc.fillText("i", x + anchoBarra / 2, alto - margen + 2);
            }
        }
    }

    private static Color color(EstadoParticion paso, int k) {
        if (paso.estaFijo(k) && !(paso.tipo() == EstadoParticion.Tipo.PIVOTE_UBICADO && k == paso.pivote())) {
            return Paleta.FINALIZADO;
        }
        if (k == paso.pivote()) {
            return Paleta.VISITADO;
        }
        if (k == paso.j()) {
            return paso.tipo() == EstadoParticion.Tipo.INTERCAMBIO ? Paleta.INTERCAMBIO : Paleta.COMPARANDO;
        }
        if (paso.tipo() == EstadoParticion.Tipo.INTERCAMBIO && k == paso.i()) {
            return Paleta.INTERCAMBIO;
        }
        if (paso.enRango(k) && paso.i() >= 0 && k < paso.i()) {
            return Paleta.RESALTADO; // zona de menores que el pivote
        }
        return Paleta.NORMAL;
    }
}
