package pe.edu.unaj.aed2.semana17.caso;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana17.caso.RutaDron.Resultado;
import pe.edu.unaj.aed2.semana17.modelo.Viajante;
import pe.edu.unaj.aed2.semana17.vista.DibujoRuta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Caso práctico semana 17: ruta del dron de vacunas. Se eligen las postas; la tabla compara los tres métodos y estima
 * cuánto tardaría la fuerza bruta con más postas; el mapa muestra la ruta del método elegido.
 */
public class VistaCasoDron extends BorderPane {

    private static final String BASE = "JUL";
    private static final List<String> INICIALES = List.of("PUN", "LAM", "AYA", "AZA", "HUA", "MOH", "ILA", "PUC",
            "CHU");

    private final RutaDron dron = new RutaDron(RedVial.cargar());
    private final Map<String, String> nombres = dron.red().nombres();
    private final Map<String, CheckBox> postas = new LinkedHashMap<>();
    private final ComboBox<String> mostrar = new ComboBox<>();
    private final Label resultado = new Label();
    private final Canvas lienzo = new Canvas();
    private final TableView<Resultado> tabla = new TableView<>();
    private final TableView<Integer> crecimiento = new TableView<>();
    private List<Resultado> resultados = List.of();
    private List<String> elegidos = List.of();
    private double nsPorRuta = 100;

    public VistaCasoDron() {
        Label titulo = new Label("Semana 17 · Caso: la ruta del dron de vacunas");
        titulo.getStyleClass().add("titulo-vista");
        Label nota = new Label("El dron sale de la base en Juliaca, pasa por cada posta elegida y vuelve. Vuela en línea"
                + " recta. ¿Qué orden recorre menos km? Con más de " + (Viajante.MAXIMO_FUERZA_BRUTA - 1) + " postas, la"
                + " fuerza bruta ya no se ejecuta.");
        nota.setWrapText(true);
        FlowPane casillas = new FlowPane(8, 4);
        nombres.keySet().stream().filter(id -> !id.equals(BASE)).forEach(id -> {
            CheckBox c = new CheckBox(nombres.get(id));
            c.setSelected(INICIALES.contains(id));
            c.setOnAction(e -> actualizar());
            postas.put(id, c);
            casillas.getChildren().add(c);
        });
        mostrar.getItems().setAll("Fuerza bruta (exacta)", "Vecino más cercano", "Vecino más cercano + 2-opt");
        mostrar.getSelectionModel().select(1);
        mostrar.setOnAction(e -> dibujar());
        HBox barra = new HBox(8, new Label("Mostrar en el mapa"), mostrar);
        barra.setAlignment(Pos.CENTER_LEFT);
        resultado.getStyleClass().add("leyenda-paso");
        resultado.setWrapText(true);
        VBox cabecera = new VBox(6, titulo, nota, casillas, barra, resultado);
        cabecera.setPadding(new Insets(0, 8, 8, 8));
        setTop(cabecera);

        Pane contenedor = new Pane(lienzo);
        contenedor.setMinSize(0, 0);
        contenedor.setPrefSize(600, 450);
        lienzo.widthProperty().bind(contenedor.widthProperty());
        lienzo.heightProperty().bind(contenedor.heightProperty());
        lienzo.widthProperty().addListener(o -> dibujar());
        lienzo.heightProperty().addListener(o -> dibujar());

        tabla.getColumns().setAll(List.of(
                columna("Método", Resultado::metodo, 190),
                columna("km", r -> r.ruta() == null ? "—" : String.valueOf(r.ruta().km()), 60),
                columna("Rutas o cambios revisados", r -> r.ruta() == null ? "demasiadas"
                        : String.format(Locale.ROOT, "%,d", r.ruta().evaluadas()), 160),
                columna("Tiempo", r -> r.ruta() == null ? "—" : String.format(Locale.ROOT, "%.2f ms", r.milisegundos()),
                        90)));
        tabla.setPrefHeight(130);
        crecimiento.getColumns().setAll(List.of(
                columnaEntera("Postas", n -> String.valueOf(n - 1), 70),
                columnaEntera("Rutas: (n − 1)!", n -> String.format(Locale.ROOT, "%,.0f", RutaDron.rutas(n)), 190),
                columnaEntera("Tiempo estimado (fuerza bruta)", n -> tiempo(RutaDron.rutas(n) * nsPorRuta / 1e9), 200)));
        crecimiento.getItems().setAll(List.of(6, 9, 11, 13, 14, 16, 21, 31));
        Label tituloTabla = new Label("¿Cuánto tardaría la fuerza bruta? (con la velocidad medida en esta computadora)");
        tituloTabla.getStyleClass().add("subtitulo-traza");
        tituloTabla.setWrapText(true);
        VBox derecha = new VBox(6, tabla, tituloTabla, crecimiento);
        derecha.setPadding(new Insets(8));
        VBox.setVgrow(crecimiento, Priority.ALWAYS);

        SplitPane division = new SplitPane(contenedor, derecha);
        division.setDividerPositions(0.52);
        setCenter(division);
        actualizar();
    }

    private static TableColumn<Resultado, String> columna(String titulo, Function<Resultado, String> valor,
                                                        double ancho) {
        TableColumn<Resultado, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(valor.apply(f.getValue())));
        c.setSortable(false);
        c.setPrefWidth(ancho);
        return c;
    }

    private static TableColumn<Integer, String> columnaEntera(String titulo, Function<Integer, String> valor,
                                                           double ancho) {
        TableColumn<Integer, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(valor.apply(f.getValue())));
        c.setSortable(false);
        c.setPrefWidth(ancho);
        return c;
    }

    private static String tiempo(double segundos) {
        if (segundos < 1) {
            return String.format(Locale.ROOT, "%.0f ms", segundos * 1000);
        }
        if (segundos < 3600) {
            return String.format(Locale.ROOT, "%.0f s", segundos);
        }
        if (segundos < 86400 * 365) {
            return String.format(Locale.ROOT, "%.1f días", segundos / 86400);
        }
        return String.format(Locale.ROOT, "%,.0f años", segundos / (86400 * 365.0));
    }

    private void actualizar() {
        List<String> lista = new ArrayList<>(List.of(BASE));
        postas.forEach((id, c) -> {
            if (c.isSelected()) {
                lista.add(id);
            }
        });
        elegidos = lista;
        if (lista.size() < 2) {
            resultados = List.of();
            tabla.getItems().clear();
            resultado.setText("Elige al menos una posta.");
            dibujar();
            return;
        }
        resultados = dron.comparar(lista);
        tabla.getItems().setAll(resultados);
        Resultado exacta = resultados.get(0);
        if (exacta.ruta() != null && exacta.ruta().evaluadas() > 1000) {
            nsPorRuta = exacta.milisegundos() * 1e6 / exacta.ruta().evaluadas();
        }
        crecimiento.refresh();
        Resultado vecino = resultados.get(1);
        Resultado opt = resultados.get(2);
        StringBuilder t = new StringBuilder((lista.size() - 1) + " postas. ");
        if (exacta.ruta() != null) {
            int o = exacta.ruta().km();
            t.append("Óptimo: ").append(o).append(" km. Vecino más cercano: ").append(vecino.ruta().km()).append(" km (")
                    .append(porcentaje(vecino.ruta().km(), o)).append("). Con 2-opt: ").append(opt.ruta().km())
                    .append(" km (").append(porcentaje(opt.ruta().km(), o)).append(").");
        } else {
            t.append("La fuerza bruta revisaría ").append(String.format(Locale.ROOT, "%,.0f", RutaDron.rutas(lista.size())))
                    .append(" rutas: no se ejecuta. Vecino más cercano: ").append(vecino.ruta().km())
                    .append(" km; con 2-opt: ").append(opt.ruta().km()).append(" km. Sin el óptimo, no se sabe cuánto"
                            + " falta.");
        }
        resultado.setText(t.toString());
        dibujar();
    }

    private static String porcentaje(int km, int optimo) {
        return km == optimo ? "es el óptimo" : String.format(Locale.ROOT, "+%.1f %%", 100.0 * (km - optimo) / optimo);
    }

    private void dibujar() {
        var gc = lienzo.getGraphicsContext2D();
        gc.setFill(Paleta.FONDO);
        gc.fillRect(0, 0, lienzo.getWidth(), lienzo.getHeight());
        if (resultados.isEmpty()) {
            return;
        }
        Resultado r = resultados.get(mostrar.getSelectionModel().getSelectedIndex());
        if (r.ruta() == null) {
            r = resultados.get(2);
        }
        List<String> ruta = r.ruta().orden();
        Viajante v = new Viajante(dron.red(), elegidos);
        DibujoRuta.dibujar(gc, dron.red(), ruta, true, Set.of(), Set.of(),
                id -> id.equals(BASE) ? Paleta.FINALIZADO : elegidos.contains(id) ? Color.web("#bbdefb") : Color.WHITE,
                id -> null, t -> v.distancia(t.split("-")[0], t.split("-")[1]), lienzo.getWidth(), lienzo.getHeight());
    }
}
