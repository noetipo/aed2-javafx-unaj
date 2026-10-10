package pe.edu.unaj.aed2.semana01.caso;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pe.edu.unaj.aed2.comun.benchmark.Cronometro;
import pe.edu.unaj.aed2.comun.benchmark.GraficoComplejidad;
import pe.edu.unaj.aed2.semana01.caso.CatalogoPrecios.Producto;
import pe.edu.unaj.aed2.semana01.modelo.Ordenamientos;

import java.util.Map;
import java.util.function.ToLongFunction;

/**
 * Caso práctico semana 01: ordenar precios de mercados locales y medir
 * empíricamente cómo crece el tiempo de cada algoritmo con n.
 */
public class VistaCasoMercado extends BorderPane {

    private static final int[] TAMANIOS = {500, 1000, 2000, 3000, 4000, 5000, 6000};
    private static final Map<String, ToLongFunction<int[]>> ALGORITMOS = Map.of(
            "Burbuja", Ordenamientos::burbuja,
            "Inserción", Ordenamientos::insercion,
            "Merge sort", Ordenamientos::mergeSort);

    private final CatalogoPrecios catalogo = CatalogoPrecios.desdeRecurso();
    private final TableView<Producto> tabla = new TableView<>();
    private final GraficoComplejidad grafico = new GraficoComplejidad("Tiempo de ordenamiento vs. n");

    public VistaCasoMercado() {
        Label titulo = new Label("Semana 01 · Caso: precios de mercados de Juliaca y Puno");
        titulo.getStyleClass().add("titulo-vista");
        setTop(titulo);

        SplitPane division = new SplitPane(crearPanelTabla(), crearPanelBenchmark());
        division.setDividerPositions(0.42);
        setCenter(division);
    }

    private VBox crearPanelTabla() {
        TableColumn<Producto, String> nombre = new TableColumn<>("Producto");
        nombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().nombre()));
        TableColumn<Producto, String> mercado = new TableColumn<>("Mercado");
        mercado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().mercado()));
        TableColumn<Producto, String> unidad = new TableColumn<>("Unidad");
        unidad.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().unidad()));
        TableColumn<Producto, Double> precio = new TableColumn<>("Precio (S/)");
        precio.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().precio()));
        precio.setSortable(false);
        nombre.setSortable(false);
        mercado.setSortable(false);
        unidad.setSortable(false);
        tabla.getColumns().setAll(nombre, mercado, unidad, precio);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.getItems().setAll(catalogo.productos());

        ComboBox<String> criterio = new ComboBox<>();
        criterio.getItems().addAll("Orden original", "Precio ascendente", "Precio descendente", "Nombre");
        criterio.getSelectionModel().selectFirst();
        criterio.setOnAction(e -> tabla.getItems().setAll(switch (criterio.getValue()) {
            case "Precio ascendente" -> catalogo.ordenadosPorPrecio(true);
            case "Precio descendente" -> catalogo.ordenadosPorPrecio(false);
            case "Nombre" -> catalogo.ordenadosPorNombre();
            default -> catalogo.productos();
        }));

        HBox barra = new HBox(8, new Label("Ordenar con merge sort por:"), criterio);
        barra.setAlignment(Pos.CENTER_LEFT);
        VBox panel = new VBox(8, barra, tabla);
        panel.setPadding(new Insets(8));
        VBox.setVgrow(tabla, Priority.ALWAYS);
        return panel;
    }

    private VBox crearPanelBenchmark() {
        Button medir = new Button("Medir tiempos");
        ProgressIndicator progreso = new ProgressIndicator();
        progreso.setVisible(false);
        progreso.setPrefSize(22, 22);
        Label nota = new Label("Arreglos aleatorios de enteros · mediana de 5 corridas tras 3 de calentamiento");

        medir.setOnAction(e -> {
            grafico.limpiar();
            Task<Void> tarea = new Task<>() {
                @Override
                protected Void call() {
                    Cronometro cronometro = new Cronometro();
                    for (int n : TAMANIOS) {
                        for (String nombre : new String[]{"Burbuja", "Inserción", "Merge sort"}) {
                            ToLongFunction<int[]> algoritmo = ALGORITMOS.get(nombre);
                            double ms = cronometro.medirMilisegundos(
                                    () -> Ordenamientos.aleatorio(n, n), algoritmo::applyAsLong);
                            grafico.agregarPunto(nombre, n, ms);
                        }
                    }
                    return null;
                }
            };
            medir.disableProperty().bind(tarea.runningProperty());
            progreso.visibleProperty().bind(tarea.runningProperty());
            Thread hilo = new Thread(tarea, "benchmark-semana01");
            hilo.setDaemon(true);
            hilo.start();
        });

        HBox barra = new HBox(8, medir, progreso, nota);
        barra.setAlignment(Pos.CENTER_LEFT);
        VBox panel = new VBox(8, barra, grafico);
        panel.setPadding(new Insets(8));
        VBox.setVgrow(grafico, Priority.ALWAYS);
        return panel;
    }
}
