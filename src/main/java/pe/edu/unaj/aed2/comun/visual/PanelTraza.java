package pe.edu.unaj.aed2.comun.visual;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

/**
 * Pestañas que acompañan a un {@link PanelAnimacion} para seguir la ejecución:
 * <ul>
 *   <li><b>Pseudocódigo</b>: resalta la línea que se ejecuta y muestra las variables actuales.</li>
 *   <li><b>Traza</b>: prueba de escritorio, una fila por paso; al hacer clic en una fila
 *       la animación salta a ese paso.</li>
 * </ul>
 */
public class PanelTraza<T extends PasoTrazable> extends TabPane {

    private final PanelAnimacion<T> panel;
    private final VBox lineas = new VBox(2);
    private final GridPane variables = new GridPane();
    private final TableView<Integer> tabla = new TableView<>();
    private boolean sincronizando;

    public PanelTraza(PanelAnimacion<T> panel, List<String> pseudocodigo) {
        this.panel = panel;
        getStyleClass().add("panel-traza");
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        setMinWidth(300);

        Label tituloVariables = new Label("Variables");
        tituloVariables.getStyleClass().add("subtitulo-traza");
        variables.setHgap(12);
        variables.setVgap(4);
        variables.getStyleClass().add("variables-traza");
        lineas.getStyleClass().add("pseudocodigo");

        VBox contenidoCodigo = new VBox(10, lineas, tituloVariables, variables);
        contenidoCodigo.setPadding(new Insets(10));
        ScrollPane desplazable = new ScrollPane(contenidoCodigo);
        desplazable.setFitToWidth(true);

        tabla.setPlaceholder(new Label("Sin pasos"));
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tabla.getSelectionModel().selectedIndexProperty().addListener((o, a, fila) -> {
            if (!sincronizando && fila.intValue() >= 0) {
                panel.irA(fila.intValue());
            }
        });
        VBox contenidoTraza = new VBox(tabla);
        VBox.setVgrow(tabla, Priority.ALWAYS);

        getTabs().addAll(new Tab("Pseudocódigo", desplazable), new Tab("Traza", contenidoTraza));

        setPseudocodigo(pseudocodigo);
        panel.pasosProperty().addListener((o, a, b) -> reconstruirTabla());
        panel.indiceProperty().addListener((o, a, b) -> actualizar());
        reconstruirTabla();
    }

    /** Cambia el pseudocódigo mostrado (p. ej. al elegir otro algoritmo). */
    public void setPseudocodigo(List<String> pseudocodigo) {
        lineas.getChildren().clear();
        for (int k = 0; k < pseudocodigo.size(); k++) {
            Label linea = new Label(String.format("%2d  %s", k + 1, pseudocodigo.get(k)));
            linea.getStyleClass().add("linea-codigo");
            linea.setMaxWidth(Double.MAX_VALUE);
            lineas.getChildren().add(linea);
        }
        actualizar();
    }

    private void reconstruirTabla() {
        List<T> pasos = panel.pasos();
        Set<String> nombres = new LinkedHashSet<>();
        pasos.forEach(p -> nombres.addAll(p.variables().keySet()));

        List<TableColumn<Integer, String>> columnas = new ArrayList<>();
        columnas.add(columna("#", k -> String.valueOf(k + 1), 40));
        columnas.add(columna("Línea", k -> {
            int l = pasos.get(k).linea();
            return l == 0 ? "" : String.valueOf(l);
        }, 50));
        for (String nombre : nombres) {
            columnas.add(columna(nombre, k -> pasos.get(k).variables().getOrDefault(nombre, ""), 70));
        }
        columnas.add(columna("Acción", k -> panel.describir(pasos.get(k)), 320));

        sincronizando = true;
        tabla.getColumns().setAll(columnas);
        tabla.getItems().setAll(IntStream.range(0, pasos.size()).boxed().toList());
        sincronizando = false;
        actualizar();
    }

    private TableColumn<Integer, String> columna(String titulo,
                                                 IntFunction<String> valor, double ancho) {
        TableColumn<Integer, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(valor.apply(f.getValue())));
        c.setSortable(false);
        c.setPrefWidth(ancho);
        return c;
    }

    private void actualizar() {
        T paso = panel.pasoActual();
        int lineaActual = paso == null ? 0 : paso.linea();
        for (int k = 0; k < lineas.getChildren().size(); k++) {
            var nodo = lineas.getChildren().get(k);
            nodo.getStyleClass().remove("linea-actual");
            if (k + 1 == lineaActual) {
                nodo.getStyleClass().add("linea-actual");
            }
        }

        variables.getChildren().clear();
        if (paso != null) {
            int fila = 0;
            for (Map.Entry<String, String> e : paso.variables().entrySet()) {
                Label nombre = new Label(e.getKey());
                nombre.getStyleClass().add("nombre-variable");
                Label valor = new Label(e.getValue());
                valor.getStyleClass().add("valor-variable");
                variables.addRow(fila++, nombre, valor);
            }
        }

        int indice = panel.indiceProperty().get();
        if (indice < tabla.getItems().size()) {
            sincronizando = true;
            tabla.getSelectionModel().clearAndSelect(indice);
            tabla.scrollTo(Math.max(0, indice - 3));
            sincronizando = false;
        }
    }
}
