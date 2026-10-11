package pe.edu.unaj.aed2.semana11.caso;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.semana07.modelo.EstadoRecorrido;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana07.vista.DibujoRed;
import pe.edu.unaj.aed2.semana11.caso.UbicacionAlmacen.Criterio;
import pe.edu.unaj.aed2.semana11.caso.UbicacionAlmacen.Propuesta;
import pe.edu.unaj.aed2.semana11.modelo.FloydWarshall;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Caso práctico semana 11: ubicación del almacén de ayuda para heladas. El mapa muestra la propuesta elegida con
 * las rutas a cada pueblo; las pestañas muestran el ranking de todas las ubicaciones y la tabla de distancias.
 */
public class VistaCasoAlmacen extends BorderPane {

    private static final int FILAS_RANKING = 15;

    private final UbicacionAlmacen ubicacion = new UbicacionAlmacen(RedVial.cargar());
    private final Map<String, String> nombres = ubicacion.red().nombres();
    private final ComboBox<Integer> cantidad = new ComboBox<>();
    private final ComboBox<Criterio> criterio = new ComboBox<>();
    private final Label resultado = new Label();
    private final Canvas lienzo = new Canvas();
    private final TableView<Propuesta> ranking = new TableView<>();
    private Propuesta elegida;

    public VistaCasoAlmacen() {
        Label titulo = new Label("Semana 11 · Caso: almacén de ayuda para las heladas");
        titulo.getStyleClass().add("titulo-vista");
        Label nota = new Label("Con la tabla de distancias entre TODOS los pares (Floyd–Warshall) se evalúa cada"
                + " ubicación posible. Cada pueblo es atendido por su almacén más cercano. km estimados, red didáctica.");
        nota.setWrapText(true);
        cantidad.getItems().setAll(1, 2);
        cantidad.getSelectionModel().select(0);
        criterio.getItems().setAll(Criterio.values());
        criterio.getSelectionModel().select(Criterio.MAS_LEJANO);
        cantidad.setOnAction(e -> actualizar());
        criterio.setOnAction(e -> actualizar());
        HBox barra = new HBox(8, new Label("Almacenes"), cantidad, new Label("Criterio"), criterio);
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

        ranking.getColumns().setAll(List.of(
                columna("Almacén(es)", p -> String.join(" + ", p.almacenes().stream().map(nombres::get).toList()), 170),
                columna("Pueblo más lejano", p -> p.peor() + " km", 120),
                columna("Recorrido total", p -> p.total() + " km", 110)));
        ranking.getSelectionModel().selectedItemProperty().addListener((o, a, p) -> {
            if (p != null) {
                elegida = p;
                dibujar();
            }
        });
        TabPane pestanias = new TabPane(new Tab("Ranking de ubicaciones", ranking),
                new Tab("Tabla de distancias", tablaDistancias()));
        pestanias.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        SplitPane division = new SplitPane(contenedor, pestanias);
        division.setDividerPositions(0.52);
        setCenter(division);
        actualizar();
    }

    private static <T> TableColumn<T, String> columna(String titulo, Function<T, String> valor, double ancho) {
        TableColumn<T, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(valor.apply(f.getValue())));
        c.setSortable(false);
        c.setPrefWidth(ancho);
        return c;
    }

    /** La clásica tabla de distancias de un atlas: fila = origen, columna = destino. */
    private TableView<String> tablaDistancias() {
        FloydWarshall.Resultado t = ubicacion.tabla();
        TableView<String> tabla = new TableView<>();
        tabla.getColumns().add(columna("", nombres::get, 95));
        for (String destino : t.vertices()) {
            TableColumn<String, String> c = columna(destino, origen -> {
                int d = t.distancia(origen, destino);
                return d == FloydWarshall.INF ? "∞" : String.valueOf(d);
            }, 46);
            tabla.getColumns().add(c);
        }
        tabla.getItems().setAll(t.vertices());
        return tabla;
    }

    private void actualizar() {
        List<Propuesta> todas = ubicacion.ranking(cantidad.getValue(), criterio.getValue());
        ranking.getItems().setAll(todas.subList(0, Math.min(FILAS_RANKING, todas.size())));
        elegida = todas.get(0);
        Propuesta otraMejor = ubicacion.ranking(cantidad.getValue(),
                criterio.getValue() == Criterio.MAS_LEJANO ? Criterio.TOTAL : Criterio.MAS_LEJANO).get(0);
        String texto = "Mejor ubicación: " + nombresDe(elegida) + " · el pueblo más lejano queda a " + elegida.peor()
                + " km · recorrido total " + elegida.total() + " km (de " + todas.size() + " ubicaciones evaluadas).";
        if (!otraMejor.almacenes().equals(elegida.almacenes())) {
            texto += " Con el otro criterio ganaría " + nombresDe(otraMejor) + " (" + otraMejor.peor() + " km al más"
                    + " lejano, " + otraMejor.total() + " km en total): cada criterio protege a alguien distinto.";
        } else {
            texto += " Con el otro criterio gana la misma ubicación.";
        }
        resultado.setText(texto);
        ranking.getSelectionModel().select(0);
        dibujar();
    }

    private String nombresDe(Propuesta p) {
        return String.join(" + ", p.almacenes().stream().map(nombres::get).toList());
    }

    private void dibujar() {
        if (elegida == null) {
            return;
        }
        FloydWarshall.Resultado t = ubicacion.tabla();
        Map<String, String> atiende = ubicacion.asignacion(elegida.almacenes());
        Set<String> rutas = new HashSet<>();
        String masLejano = null;
        for (String v : t.vertices()) {
            List<String> c = t.camino(atiende.get(v), v);
            for (int k = 1; k < c.size(); k++) {
                rutas.add(EstadoRecorrido.arista(c.get(k - 1), c.get(k)));
            }
            if (masLejano == null || t.distancia(atiende.get(v), v) > t.distancia(atiende.get(masLejano), masLejano)) {
                masLejano = v;
            }
        }
        String lejano = masLejano;
        var gc = lienzo.getGraphicsContext2D();
        gc.setFill(Paleta.FONDO);
        gc.fillRect(0, 0, lienzo.getWidth(), lienzo.getHeight());
        DibujoRed.dibujar(gc, ubicacion.red(),
                v -> elegida.almacenes().contains(v) ? Paleta.FINALIZADO
                        : v.equals(lejano) ? Paleta.INTERCAMBIO : Color.web("#e3f2fd"),
                v -> elegida.almacenes().contains(v) ? Color.web("#2e7d32") : null,
                (u, v) -> rutas.contains(EstadoRecorrido.arista(u, v)) ? new DibujoRed.Trazo(Color.web("#2e7d32"), 4)
                        : new DibujoRed.Trazo(Color.web("#cfd8dc"), 1.5),
                (u, v) -> String.valueOf(ubicacion.grafo().km(u, v)),
                v -> elegida.almacenes().contains(v) ? "almacén" : t.distancia(atiende.get(v), v) + " km",
                lienzo.getWidth(), lienzo.getHeight());
    }
}
