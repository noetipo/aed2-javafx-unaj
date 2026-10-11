package pe.edu.unaj.aed2.semana12.caso;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
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
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana07.vista.DibujoRed.Trazo;
import pe.edu.unaj.aed2.semana12.caso.CuencaLechera.Ampliacion;
import pe.edu.unaj.aed2.semana12.caso.CuencaLechera.Envio;
import pe.edu.unaj.aed2.semana12.modelo.RedFlujo;
import pe.edu.unaj.aed2.semana12.vista.DibujoFlujo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Caso práctico semana 12: producción diaria de tres cuencas lecheras y capacidad de las rutas a la planta de Juliaca.
 * El mapa muestra el envío máximo y el corte mínimo; la tabla, cuánto se gana ampliando cada tramo.
 */
public class VistaCasoLeche extends BorderPane {

    private final CuencaLechera cuenca = new CuencaLechera(RedFlujo.cargarLeche());
    private final Map<String, String> nombres = RedVial.cargar().nombres();
    private final Map<String, Spinner<Integer>> produccion = new LinkedHashMap<>();
    private final Spinner<Integer> extra = new Spinner<>(1, 10, 3);
    private final Label resultado = new Label();
    private final Canvas lienzo = new Canvas();
    private final TableView<Ampliacion> tabla = new TableView<>();
    private Envio envio;

    public VistaCasoLeche() {
        Label titulo = new Label("Semana 12 · Caso: la leche de las cuencas a la planta de Juliaca");
        titulo.getStyleClass().add("titulo-vista");
        Label nota = new Label("Cada cuenca produce cierta cantidad de camiones de leche por día y cada ruta tiene una"
                + " capacidad. ¿Cuánto llega a la planta? ¿Qué tramo conviene ampliar? Valores ficticios.");
        nota.setWrapText(true);
        HBox barra = new HBox(8);
        barra.setAlignment(Pos.CENTER_LEFT);
        Map<String, Integer> inicial = Map.of("AYA", 14, "AZA", 8, "PUT", 5);
        for (String c : CuencaLechera.CUENCAS) {
            Spinner<Integer> s = new Spinner<>(0, 30, inicial.get(c));
            s.setPrefWidth(70);
            s.valueProperty().addListener((o, a, b) -> actualizar());
            produccion.put(c, s);
            barra.getChildren().addAll(new Label(nombres.get(c)), s);
        }
        extra.setPrefWidth(70);
        extra.valueProperty().addListener((o, a, b) -> actualizar());
        barra.getChildren().addAll(new Label("   Ampliar un tramo en"), extra, new Label("camiones"));
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

        tabla.getColumns().setAll(List.of(
                columna("Tramo ampliado", a -> nombres.get(a.tramo().u()) + " → " + nombres.get(a.tramo().v()), 190),
                columna("Capacidad", a -> a.tramo().capacidad() + " → " + (a.tramo().capacidad() + extra.getValue()), 90),
                columna("Llegan a la planta", a -> String.valueOf(a.entregado()), 120),
                columna("Ganancia", a -> a.ganancia() == 0 ? "nada" : "+" + a.ganancia(), 80)));
        Label tituloTabla = new Label("¿Qué tramo conviene ampliar?");
        tituloTabla.getStyleClass().add("subtitulo-traza");
        VBox derecha = new VBox(6, tituloTabla, tabla);
        derecha.setPadding(new Insets(8));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        SplitPane division = new SplitPane(contenedor, derecha);
        division.setDividerPositions(0.55);
        setCenter(division);
        actualizar();
    }

    private static TableColumn<Ampliacion, String> columna(String titulo, Function<Ampliacion, String> valor,
                                                         double ancho) {
        TableColumn<Ampliacion, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(valor.apply(f.getValue())));
        c.setSortable(false);
        c.setPrefWidth(ancho);
        return c;
    }

    private Map<String, Integer> produccion() {
        Map<String, Integer> m = new LinkedHashMap<>();
        produccion.forEach((c, s) -> m.put(c, s.getValue()));
        return m;
    }

    private void actualizar() {
        Map<String, Integer> p = produccion();
        envio = cuenca.enviar(p);
        List<Ampliacion> ampliaciones = cuenca.ampliaciones(p, extra.getValue());
        tabla.getItems().setAll(ampliaciones);
        List<RedFlujo.Arco> corte = envio.flujo().corte();
        List<String> tramos = corte.stream().filter(a -> !a.u().equals(CuencaLechera.FUENTE))
                .map(a -> nombres.get(a.u()) + " → " + nombres.get(a.v())).toList();
        List<String> limitadas = corte.stream().filter(a -> a.u().equals(CuencaLechera.FUENTE))
                .map(a -> nombres.get(a.v())).toList();
        StringBuilder texto = new StringBuilder("Se producen " + envio.producido() + " camiones y llegan "
                + envio.entregado() + " a la planta");
        texto.append(envio.perdido() == 0 ? ": todo llega." : " (" + envio.perdido() + " se quedan sin transporte).");
        texto.append(" Por cuenca: ");
        envio.porCuenca().forEach((c, f) -> texto.append(nombres.get(c)).append(" ").append(f).append("/")
                .append(p.get(c)).append("  "));
        texto.append(" Corte mínimo: ").append(tramos.isEmpty() ? "" : String.join(", ", tramos) + " (llenos)")
                .append(limitadas.isEmpty() ? "" : (tramos.isEmpty() ? "" : " + ") + "la producción de "
                        + String.join(" y ", limitadas)).append(".");
        Ampliacion mejor = ampliaciones.get(0);
        texto.append(mejor.ganancia() == 0 ? " Ampliar un solo tramo no ayuda: el límite está en varios a la vez."
                : " Lo mejor: ampliar " + nombres.get(mejor.tramo().u()) + " → " + nombres.get(mejor.tramo().v())
                + " (+" + mejor.ganancia() + "). Ampliar un tramo fuera del corte no sirve de nada.");
        resultado.setText(texto.toString());
        dibujar();
    }

    private void dibujar() {
        if (envio == null) {
            return;
        }
        var gc = lienzo.getGraphicsContext2D();
        gc.setFill(Paleta.FONDO);
        gc.fillRect(0, 0, lienzo.getWidth(), lienzo.getHeight());
        var ladoS = envio.flujo().ladoS();
        gc.save();
        gc.translate(0, 22);
        DibujoFlujo.dibujar(gc, cuenca.rutas(), envio.flujo().flujo(),
                a -> {
                    if (ladoS.contains(a.u()) && !ladoS.contains(a.v())) {
                        return new Trazo(Paleta.INTERCAMBIO, 5);
                    }
                    int f = envio.flujo().flujo().getOrDefault(a.clave(), 0);
                    return f == 0 ? new Trazo(Color.web("#b0bec5"), 1.5)
                            : new Trazo(Color.web("#1e88e5"), 1.5 + 4.0 * f / a.capacidad());
                },
                v -> v.equals(CuencaLechera.PLANTA) ? Paleta.RESALTADO
                        : CuencaLechera.CUENCAS.contains(v) ? Paleta.FINALIZADO : Color.web("#e3f2fd"),
                v -> null,
                lienzo.getWidth(), lienzo.getHeight() - 22);
        gc.restore();
        gc.setFill(Color.web("#546e7a"));
        gc.setFont(javafx.scene.text.Font.font(12));
        gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT);
        gc.fillText("Verde: cuencas · Amarillo: planta · Azul: lleva leche · Rojo: corte mínimo (tramos llenos que"
                + " limitan el envío)", 10, 16);
    }
}
