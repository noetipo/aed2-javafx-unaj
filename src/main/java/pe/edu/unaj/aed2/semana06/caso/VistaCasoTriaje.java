package pe.edu.unaj.aed2.semana06.caso;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.semana06.caso.TriajeEmergencias.Atencion;
import pe.edu.unaj.aed2.semana06.caso.TriajeEmergencias.Paciente;
import pe.edu.unaj.aed2.semana06.vista.DibujoMonticulo;

import java.util.List;
import java.util.Locale;
import java.util.function.Function;

/**
 * Caso práctico semana 06: tópico de emergencias con triaje. Muestra la cola de prioridad (montículo)
 * en cada momento en que el médico llama a un paciente y compara las esperas con la atención por
 * orden de llegada.
 */
public class VistaCasoTriaje extends BorderPane {

    /** Color de cada nivel de triaje: P1 rojo, P2 naranja, P3 amarillo, P4 celeste, P5 gris. */
    private static final Color[] PRIORIDAD = {null, Color.web("#ef5350"), Color.web("#ffa726"),
            Color.web("#fff176"), Color.web("#81d4fa"), Color.web("#e0e0e0")};

    private final TriajeEmergencias triaje = TriajeEmergencias.desdeRecurso();
    private final Canvas lienzo = new Canvas();
    private final Slider momento = new Slider();
    private final Label detalle = new Label();
    private final TableView<Atencion> tabla = new TableView<>();

    public VistaCasoTriaje() {
        Label titulo = new Label("Semana 06 · Caso: tópico de emergencias con triaje");
        titulo.getStyleClass().add("titulo-vista");
        Label nota = new Label("Datos ficticios. Un médico atiende a un paciente cada " + TriajeEmergencias.DURACION
                + " minutos. Prioridad (escala didáctica de 5 niveles): P1 inmediata · P2 muy urgente · P3 urgente"
                + " · P4 normal · P5 no urgente.");
        nota.setWrapText(true);

        int total = triaje.porPrioridad().size();
        momento.setMin(0);
        momento.setMax(total - 1);
        momento.setMajorTickUnit(1);
        momento.setMinorTickCount(0);
        momento.setSnapToTicks(true);
        momento.setBlockIncrement(1);
        momento.valueProperty().addListener((o, a, b) -> actualizar());
        detalle.getStyleClass().add("leyenda-paso");
        detalle.setWrapText(true);

        VBox cabecera = new VBox(6, titulo, nota, new Label("Llamado número (mueve el control):"), momento, detalle);
        cabecera.setPadding(new Insets(0, 8, 8, 8));
        setTop(cabecera);

        Pane contenedor = new Pane(lienzo);
        contenedor.setMinSize(0, 0);
        contenedor.setPrefSize(600, 300);
        lienzo.widthProperty().bind(contenedor.widthProperty());
        lienzo.heightProperty().bind(contenedor.heightProperty());
        lienzo.widthProperty().addListener(o -> actualizar());
        lienzo.heightProperty().addListener(o -> actualizar());
        Label tituloCola = new Label("Cola de prioridad (montículo de mínimos) justo antes del llamado ·"
                + " la raíz, posición 0, es quien pasa");
        tituloCola.getStyleClass().add("subtitulo-traza");
        VBox izquierda = new VBox(6, tituloCola, contenedor, tablaAtenciones());
        izquierda.setPadding(new Insets(8));
        VBox.setVgrow(contenedor, Priority.ALWAYS);

        SplitPane division = new SplitPane(izquierda, graficoEsperas());
        division.setDividerPositions(0.6);
        setCenter(division);
        actualizar();
    }

    private TableView<Atencion> tablaAtenciones() {
        tabla.getColumns().setAll(
                columna("#", a -> String.valueOf(triaje.porPrioridad().indexOf(a) + 1), 40),
                columna("Atendido (min)", a -> String.valueOf(a.minuto()), 100),
                columna("Paciente", a -> a.paciente().nombre(), 90),
                columna("Prioridad", a -> "P" + a.paciente().prioridad(), 70),
                columna("Motivo", a -> a.paciente().motivo(), 200),
                columna("Llegó (min)", a -> String.valueOf(a.paciente().minuto()), 85),
                columna("Esperó (min)", a -> String.valueOf(a.espera()), 90));
        tabla.getItems().setAll(triaje.porPrioridad());
        tabla.setPrefHeight(220);
        tabla.getSelectionModel().selectedIndexProperty().addListener((o, a, b) -> {
            if (b.intValue() >= 0) {
                momento.setValue(b.intValue());
            }
        });
        return tabla;
    }

    private static TableColumn<Atencion, String> columna(String titulo, Function<Atencion, String> valor, double ancho) {
        TableColumn<Atencion, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(valor.apply(f.getValue())));
        c.setSortable(false);
        c.setPrefWidth(ancho);
        return c;
    }

    private VBox graficoEsperas() {
        CategoryAxis x = new CategoryAxis();
        x.setLabel("Prioridad");
        NumberAxis y = new NumberAxis();
        y.setLabel("Espera promedio (min)");
        BarChart<String, Number> grafico = new BarChart<>(x, y);
        grafico.setTitle("¿Cuánto espera cada prioridad?");
        grafico.setAnimated(false);
        agregarSerie(grafico, "Por orden de llegada (cola)", TriajeEmergencias.esperaPromedio(triaje.porLlegada()));
        agregarSerie(grafico, "Por prioridad (montículo)", TriajeEmergencias.esperaPromedio(triaje.porPrioridad()));
        Label resumen = new Label(resumen());
        resumen.setWrapText(true);
        VBox caja = new VBox(6, grafico, resumen);
        caja.setPadding(new Insets(8));
        VBox.setVgrow(grafico, Priority.ALWAYS);
        return caja;
    }

    private static void agregarSerie(BarChart<String, Number> grafico, String nombre, double[] promedios) {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        serie.setName(nombre);
        for (int p = 1; p <= 5; p++) {
            serie.getData().add(new XYChart.Data<>("P" + p, Double.isNaN(promedios[p]) ? 0 : promedios[p]));
        }
        grafico.getData().add(serie);
    }

    private String resumen() {
        double[] fifo = TriajeEmergencias.esperaPromedio(triaje.porLlegada());
        double[] heap = TriajeEmergencias.esperaPromedio(triaje.porPrioridad());
        return String.format(Locale.ROOT, "Un paciente P1 espera en promedio %.1f min por orden de llegada y %.1f min"
                + " con la cola de prioridad. A cambio, un P5 pasa de %.1f a %.1f min: el montículo no hace que se"
                + " atienda más rápido en total, decide A QUIÉN se atiende primero.", fifo[1], heap[1], fifo[5], heap[5]);
    }

    private void actualizar() {
        int k = (int) Math.round(momento.getValue());
        Atencion a = triaje.porPrioridad().get(k);
        List<Paciente> cola = a.colaAntes();
        Paciente p = a.paciente();
        detalle.setText("Minuto " + a.minuto() + ": hay " + cola.size() + " paciente" + (cola.size() == 1 ? "" : "s")
                + " en espera. extraerMin() devuelve la raíz: " + p.nombre() + " (P" + p.prioridad() + ", "
                + p.motivo() + "), que llegó en el minuto " + p.minuto() + " y esperó " + a.espera() + " min.");
        if (tabla.getSelectionModel().getSelectedIndex() != k) {
            tabla.getSelectionModel().select(k);
            tabla.scrollTo(Math.max(0, k - 2));
        }
        var gc = lienzo.getGraphicsContext2D();
        gc.setFill(Paleta.FONDO);
        gc.fillRect(0, 0, lienzo.getWidth(), lienzo.getHeight());
        DibujoMonticulo.dibujar(gc, cola.stream().map(Paciente::toString).toList(), cola.size(),
                i -> PRIORIDAD[cola.get(i).prioridad()],
                lienzo.getWidth(), lienzo.getHeight());
    }
}
