package pe.edu.unaj.aed2.semana07.caso;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.semana07.modelo.EstadoRecorrido;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana07.vista.DibujoRed;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Caso práctico semana 07: ¿qué pueblos quedan aislados de Juliaca si se bloquean carreteras?
 * Cada cambio vuelve a correr BFS sobre la red sin los tramos bloqueados.
 */
public class VistaCasoBloqueo extends BorderPane {

    private static final String ORIGEN = "JUL";

    private final BloqueoVial bloqueo = new BloqueoVial(RedVial.cargar());
    private final Map<String, String> nombres = bloqueo.red().nombres();
    private final Map<String, CheckBox> casillas = new LinkedHashMap<>();
    private final ComboBox<String> destino = new ComboBox<>();
    private final Label resultado = new Label();
    private final Canvas lienzo = new Canvas();
    private final List<String> criticos = bloqueo.tramosCriticos();

    public VistaCasoBloqueo() {
        Label titulo = new Label("Semana 07 · Caso: bloqueo de carreteras en la región Puno");
        titulo.getStyleClass().add("titulo-vista");
        Label nota = new Label("Marca los tramos bloqueados. Con BFS desde Juliaca se ve a qué pueblos todavía se puede"
                + " llegar y la ruta con menos tramos al destino. Red simplificada y didáctica.");
        nota.setWrapText(true);

        bloqueo.red().grafo().vertices().forEach(v -> {
            if (!v.equals(ORIGEN)) {
                destino.getItems().add(nombres.get(v) + " (" + v + ")");
            }
        });
        destino.getSelectionModel().select(destino.getItems().size() - 1);
        destino.setOnAction(e -> actualizar());
        Button limpiar = new Button("Quitar bloqueos");
        limpiar.setOnAction(e -> {
            casillas.values().forEach(c -> c.setSelected(false));
            actualizar();
        });
        HBox barra = new HBox(8, new Label("Destino desde Juliaca"), destino, limpiar);
        barra.setAlignment(Pos.CENTER_LEFT);
        resultado.getStyleClass().add("leyenda-paso");
        resultado.setWrapText(true);
        VBox cabecera = new VBox(6, titulo, nota, barra, resultado);
        cabecera.setPadding(new Insets(0, 8, 8, 8));
        setTop(cabecera);

        VBox lista = new VBox(4);
        Label tituloLista = new Label("Tramos bloqueados");
        tituloLista.getStyleClass().add("subtitulo-traza");
        lista.getChildren().add(tituloLista);
        for (String[] uv : bloqueo.carreteras()) {
            String c = EstadoRecorrido.arista(uv[0], uv[1]);
            CheckBox casilla = new CheckBox(nombres.get(uv[0]) + " – " + nombres.get(uv[1])
                    + (criticos.contains(c) ? "  (crítico)" : ""));
            casilla.setOnAction(e -> actualizar());
            casillas.put(c, casilla);
            lista.getChildren().add(casilla);
        }
        Label ayuda = new Label("Crítico: si se bloquea solo, ya deja\npueblos aislados (un \"puente\" del grafo).");
        ayuda.setTextFill(Color.web("#546e7a"));
        lista.getChildren().add(ayuda);
        lista.setPadding(new Insets(8));
        ScrollPane desplazable = new ScrollPane(lista);
        desplazable.setFitToWidth(true);
        desplazable.setPrefWidth(270);
        setLeft(desplazable);

        Pane contenedor = new Pane(lienzo);
        contenedor.setMinSize(0, 0);
        contenedor.setPrefSize(700, 500);
        lienzo.widthProperty().bind(contenedor.widthProperty());
        lienzo.heightProperty().bind(contenedor.heightProperty());
        lienzo.widthProperty().addListener(o -> actualizar());
        lienzo.heightProperty().addListener(o -> actualizar());
        setCenter(contenedor);
        actualizar();
    }

    private Set<String> bloqueados() {
        Set<String> b = new HashSet<>();
        casillas.forEach((c, casilla) -> {
            if (casilla.isSelected()) {
                b.add(c);
            }
        });
        return b;
    }

    private void actualizar() {
        Set<String> bloqueados = bloqueados();
        Set<String> llega = bloqueo.alcanzables(ORIGEN, bloqueados);
        List<String> aislados = bloqueo.aislados(ORIGEN, bloqueados);
        String texto = destino.getValue();
        String t = texto.substring(texto.lastIndexOf('(') + 1, texto.length() - 1);
        List<String> ruta = bloqueo.ruta(ORIGEN, t, bloqueados);
        Set<String> tramosRuta = new HashSet<>();
        for (int k = 1; k < ruta.size(); k++) {
            tramosRuta.add(EstadoRecorrido.arista(ruta.get(k - 1), ruta.get(k)));
        }
        resultado.setText((aislados.isEmpty() ? "Desde Juliaca se llega a los " + llega.size() + " pueblos."
                : "Quedan AISLADOS de Juliaca: " + String.join(", ", aislados.stream().map(nombres::get).toList()) + ".")
                + "  " + (ruta.isEmpty() ? "No hay ruta a " + nombres.get(t) + "."
                : "Ruta con menos tramos a " + nombres.get(t) + " (" + (ruta.size() - 1) + " tramos): "
                + String.join(" → ", ruta.stream().map(nombres::get).toList()) + "."));

        var gc = lienzo.getGraphicsContext2D();
        gc.setFill(Paleta.FONDO);
        gc.fillRect(0, 0, lienzo.getWidth(), lienzo.getHeight());
        Map<String, Integer> dist = bloqueo.red().grafo().sin(bloqueados).bfs(ORIGEN);
        DibujoRed.dibujar(gc, bloqueo.red(),
                v -> v.equals(ORIGEN) ? Paleta.FINALIZADO : llega.contains(v) ? Color.web("#bbdefb") : Color.web("#ef9a9a"),
                v -> v.equals(t) ? Paleta.RESALTADO : null,
                (u, v) -> {
                    String c = EstadoRecorrido.arista(u, v);
                    if (bloqueados.contains(c)) {
                        return new DibujoRed.Trazo(Color.web("#c62828"), 3);
                    }
                    if (tramosRuta.contains(c)) {
                        return new DibujoRed.Trazo(Color.web("#2e7d32"), 5);
                    }
                    return new DibujoRed.Trazo(Color.web("#90a4ae"), 2);
                },
                v -> dist.containsKey(v) ? dist.get(v) + (dist.get(v) == 1 ? " tramo" : " tramos") : "aislado",
                lienzo.getWidth(), lienzo.getHeight());
    }
}
