package pe.edu.unaj.aed2.semana08.caso;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.semana07.modelo.EstadoRecorrido;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana07.vista.DibujoRed;
import pe.edu.unaj.aed2.semana08.caso.PlanificadorRutas.Destino;
import pe.edu.unaj.aed2.semana08.caso.PlanificadorRutas.Ruta;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Caso práctico semana 08: reparto desde un pueblo de la red. En el mapa, la ruta más corta en km
 * (Dijkstra, verde) y la de menos tramos (BFS, morada); en la tabla, todos los destinos.
 */
public class VistaCasoRutas extends BorderPane {

    private final PlanificadorRutas plan = new PlanificadorRutas(RedVial.cargar());
    private final Map<String, String> nombres = plan.red().nombres();
    private final ComboBox<String> origen = new ComboBox<>();
    private final ComboBox<String> destino = new ComboBox<>();
    private final Label resultado = new Label();
    private final Canvas lienzo = new Canvas();
    private final TableView<Destino> tabla = new TableView<>();

    public VistaCasoRutas() {
        Label titulo = new Label("Semana 08 · Caso: rutas de reparto desde Juliaca");
        titulo.getStyleClass().add("titulo-vista");
        Label nota = new Label("¿Conviene la ruta con menos tramos o la de menos kilómetros? Verde: Dijkstra (menos km)."
                + " Morado: BFS (menos tramos). km estimados sobre una red simplificada y didáctica.");
        nota.setWrapText(true);
        plan.grafo().vertices().forEach(v -> {
            origen.getItems().add(nombres.get(v) + " (" + v + ")");
            destino.getItems().add(nombres.get(v) + " (" + v + ")");
        });
        origen.getSelectionModel().select("Juliaca (JUL)");
        destino.getSelectionModel().select("Putina (PUT)");
        origen.setOnAction(e -> actualizar(true));
        destino.setOnAction(e -> actualizar(false));
        HBox barra = new HBox(8, new Label("Origen"), origen, new Label("Destino"), destino);
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
        lienzo.widthProperty().addListener(o -> actualizar(false));
        lienzo.heightProperty().addListener(o -> actualizar(false));

        tabla.getColumns().setAll(
                columna("Destino", d -> nombres.get(d.id()), 100),
                columna("km (Dijkstra)", d -> String.valueOf(d.masCorta().km()), 95),
                columna("Ruta más corta", d -> texto(d.masCorta()), 300),
                columna("Tramos", d -> String.valueOf(d.masCorta().tramos()), 60),
                columna("km por la ruta de menos tramos", d -> d.kmDeMas() == 0 ? "igual"
                        : d.menosTramos().km() + " (+" + d.kmDeMas() + ")", 190));
        tabla.getSelectionModel().selectedItemProperty().addListener((o, a, d) -> {
            if (d != null) {
                destino.getSelectionModel().select(nombres.get(d.id()) + " (" + d.id() + ")");
            }
        });
        Label tituloTabla = new Label("Todos los destinos, de más cercano a más lejano");
        tituloTabla.getStyleClass().add("subtitulo-traza");
        VBox derecha = new VBox(6, tituloTabla, tabla);
        derecha.setPadding(new Insets(8));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        SplitPane division = new SplitPane(contenedor, derecha);
        division.setDividerPositions(0.5);
        setCenter(division);
        actualizar(true);
    }

    private static TableColumn<Destino, String> columna(String titulo, Function<Destino, String> valor, double ancho) {
        TableColumn<Destino, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(valor.apply(f.getValue())));
        c.setSortable(false);
        c.setPrefWidth(ancho);
        return c;
    }

    private String texto(Ruta r) {
        return String.join(" → ", r.pueblos().stream().map(nombres::get).toList());
    }

    private static String id(ComboBox<String> combo) {
        String t = combo.getValue();
        return t.substring(t.lastIndexOf('(') + 1, t.length() - 1);
    }

    private static Set<String> tramos(List<String> camino) {
        Set<String> s = new HashSet<>();
        for (int k = 1; k < camino.size(); k++) {
            s.add(EstadoRecorrido.arista(camino.get(k - 1), camino.get(k)));
        }
        return s;
    }

    private void actualizar(boolean recargarTabla) {
        String s = id(origen);
        String t = id(destino);
        if (recargarTabla) {
            tabla.getItems().setAll(plan.tabla(s));
        }
        Ruta corta = plan.masCorta(s, t);
        Ruta pocos = plan.menosTramos(s, t);
        if (s.equals(t)) {
            resultado.setText("Elige un destino distinto del origen.");
        } else if (corta.pueblos().equals(pocos.pueblos())) {
            resultado.setText("La ruta más corta a " + nombres.get(t) + " es también la de menos tramos: " + texto(corta)
                    + " · " + corta.km() + " km en " + corta.tramos() + " tramos.");
        } else {
            resultado.setText("Dijkstra: " + texto(corta) + " · " + corta.km() + " km en " + corta.tramos()
                    + " tramos.   BFS: " + texto(pocos) + " · " + pocos.km() + " km en " + pocos.tramos()
                    + " tramos.   Contar tramos cuesta " + (pocos.km() - corta.km()) + " km de más.");
        }
        Set<String> verde = tramos(corta.pueblos());
        Set<String> morado = tramos(pocos.pueblos());
        var gc = lienzo.getGraphicsContext2D();
        gc.setFill(Paleta.FONDO);
        gc.fillRect(0, 0, lienzo.getWidth(), lienzo.getHeight());
        Map<String, Integer> dist = plan.grafo().dijkstra(s).dist();
        DibujoRed.dibujar(gc, plan.red(),
                v -> v.equals(s) ? Paleta.FINALIZADO : v.equals(t) ? Paleta.RESALTADO : Color.web("#e3f2fd"),
                v -> null,
                (u, v) -> {
                    String c = EstadoRecorrido.arista(u, v);
                    if (verde.contains(c)) {
                        return new DibujoRed.Trazo(Color.web("#2e7d32"), 6);
                    }
                    if (morado.contains(c)) {
                        return new DibujoRed.Trazo(Color.web("#8e24aa"), 4);
                    }
                    return new DibujoRed.Trazo(Color.web("#b0bec5"), 1.5);
                },
                (u, v) -> String.valueOf(plan.grafo().km(u, v)),
                v -> dist.containsKey(v) ? dist.get(v) + " km" : "",
                lienzo.getWidth(), lienzo.getHeight());
    }
}
