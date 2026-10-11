package pe.edu.unaj.aed2.semana09.caso;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.semana07.modelo.EstadoRecorrido;
import pe.edu.unaj.aed2.semana07.vista.DibujoRed;
import pe.edu.unaj.aed2.semana09.caso.ProyectoFibra.Diseno;
import pe.edu.unaj.aed2.semana09.caso.ProyectoFibra.Fila;
import pe.edu.unaj.aed2.semana09.modelo.ArbolExpansion;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Caso práctico semana 09: red de fibra óptica entre los pueblos. En el mapa se ve el diseño elegido
 * (expansión mínima en verde o caminos mínimos en morado) y, sobre cada pueblo, los km de cable que recorre
 * la señal desde el centro. La tabla compara ambos diseños pueblo por pueblo.
 */
public class VistaCasoFibra extends BorderPane {

    private static final Color VERDE = Color.web("#2e7d32");
    private static final Color MORADO = Color.web("#8e24aa");

    private final ProyectoFibra proyecto = new ProyectoFibra(ArbolExpansion.cargarTendidos());
    private final Map<String, String> nombres = proyecto.red().nombres();
    private final ComboBox<String> centro = new ComboBox<>();
    private final ToggleButton verExpansion = new ToggleButton("Expansión mínima (Kruskal)");
    private final ToggleButton verCaminos = new ToggleButton("Caminos mínimos (Dijkstra)");
    private final Label resultado = new Label();
    private final Canvas lienzo = new Canvas();
    private final TableView<Fila> tabla = new TableView<>();
    private final NumberFormat soles = NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-PE"));

    public VistaCasoFibra() {
        Label titulo = new Label("Semana 09 · Caso: red de fibra óptica entre los pueblos");
        titulo.getStyleClass().add("titulo-vista");
        Label nota = new Label("Hay que conectar los " + proyecto.grafo().vertices().size() + " pueblos tendiendo"
                + " cable solo por algunos de los posibles tramos. ¿Menos cable en total o señal más cerca del centro?"
                + " km estimados y costo FICTICIO de S/ " + soles.format(ProyectoFibra.SOLES_POR_KM) + " por km.");
        nota.setWrapText(true);
        proyecto.grafo().vertices().forEach(v -> centro.getItems().add(nombres.get(v) + " (" + v + ")"));
        centro.getSelectionModel().select("Juliaca (JUL)");
        centro.setOnAction(e -> actualizar());
        ToggleGroup grupo = new ToggleGroup();
        verExpansion.setToggleGroup(grupo);
        verCaminos.setToggleGroup(grupo);
        verExpansion.setSelected(true);
        grupo.selectedToggleProperty().addListener((o, antes, ahora) -> {
            if (ahora == null) {
                antes.setSelected(true); // siempre hay un diseño elegido
            }
            actualizar();
        });
        HBox barra = new HBox(8, new Label("Centro (nodo principal)"), centro, new Label("Mostrar"), verExpansion,
                verCaminos);
        barra.setAlignment(Pos.CENTER_LEFT);
        resultado.getStyleClass().add("leyenda-paso");
        resultado.setWrapText(true);
        VBox cabecera = new VBox(6, titulo, nota, barra, resultado);
        cabecera.setPadding(new Insets(0, 8, 8, 8));
        setTop(cabecera);

        Pane contenedor = new Pane(lienzo);
        contenedor.setMinSize(0, 0);
        contenedor.setPrefSize(650, 500);
        lienzo.widthProperty().bind(contenedor.widthProperty());
        lienzo.heightProperty().bind(contenedor.heightProperty());
        lienzo.widthProperty().addListener(o -> dibujar());
        lienzo.heightProperty().addListener(o -> dibujar());

        tabla.getColumns().setAll(
                columna("Pueblo", f -> nombres.get(f.id()), 110),
                columna("Cable desde el centro: expansión mínima", f -> f.porExpansion() + " km", 230),
                columna("Caminos mínimos", f -> f.porCaminos() + " km", 120),
                columna("Diferencia", f -> f.diferencia() == 0 ? "igual" : "+" + f.diferencia() + " km", 90));
        Label tituloTabla = new Label("¿Cuántos km de cable recorre la señal desde el centro hasta cada pueblo?");
        tituloTabla.getStyleClass().add("subtitulo-traza");
        tituloTabla.setWrapText(true);
        VBox derecha = new VBox(6, tituloTabla, tabla);
        derecha.setPadding(new Insets(8));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        SplitPane division = new SplitPane(contenedor, derecha);
        division.setDividerPositions(0.55);
        setCenter(division);
        actualizar();
    }

    private static TableColumn<Fila, String> columna(String titulo, Function<Fila, String> valor, double ancho) {
        TableColumn<Fila, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(valor.apply(f.getValue())));
        c.setSortable(false);
        c.setPrefWidth(ancho);
        return c;
    }

    private String centroId() {
        String t = centro.getValue();
        return t.substring(t.lastIndexOf('(') + 1, t.length() - 1);
    }

    private void actualizar() {
        String c = centroId();
        Diseno expansion = proyecto.expansionMinima();
        Diseno caminos = proyecto.caminosMinimos(c);
        tabla.getItems().setAll(proyecto.tabla(c));
        int ahorro = caminos.km() - expansion.km();
        long peor = proyecto.tabla(c).stream().mapToInt(Fila::diferencia).max().orElse(0);
        resultado.setText("Expansión mínima: " + expansion.km() + " km de cable (S/ " + soles.format(expansion.soles())
                + ").   Caminos mínimos desde " + nombres.get(c) + ": " + caminos.km() + " km (S/ "
                + soles.format(caminos.soles()) + ").   "
                + (ahorro == 0 ? "Esta vez ambos diseños coinciden en km."
                : "Kruskal ahorra " + ahorro + " km de cable, pero la señal llega a algún pueblo hasta " + peor
                + " km más lejos del centro."));
        dibujar();
    }

    private void dibujar() {
        String c = centroId();
        boolean expansion = verExpansion.isSelected();
        Diseno diseno = expansion ? proyecto.expansionMinima() : proyecto.caminosMinimos(c);
        Set<String> elegidos = diseno.claves();
        Map<String, Integer> dist = proyecto.distanciasPorCable(diseno, c);
        Color color = expansion ? VERDE : MORADO;
        var gc = lienzo.getGraphicsContext2D();
        gc.setFill(Paleta.FONDO);
        gc.fillRect(0, 0, lienzo.getWidth(), lienzo.getHeight());
        DibujoRed.dibujar(gc, proyecto.red(),
                v -> v.equals(c) ? Paleta.RESALTADO : Color.web("#e3f2fd"),
                v -> null,
                (u, v) -> elegidos.contains(EstadoRecorrido.arista(u, v)) ? new DibujoRed.Trazo(color, 5)
                        : new DibujoRed.Trazo(Color.web("#cfd8dc"), 1.5),
                (u, v) -> String.valueOf(proyecto.grafo().km(u, v)),
                v -> v.equals(c) ? "centro" : dist.containsKey(v) ? dist.get(v) + " km" : "",
                lienzo.getWidth(), lienzo.getHeight());
    }
}
