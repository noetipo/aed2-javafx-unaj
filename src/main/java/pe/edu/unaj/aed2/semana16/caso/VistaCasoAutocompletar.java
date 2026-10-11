package pe.edu.unaj.aed2.semana16.caso;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.semana16.caso.AutocompletadoQuechua.Sugerencia;
import pe.edu.unaj.aed2.semana16.vista.DibujoTrie;

import java.util.List;

/**
 * Caso práctico semana 16: buscador del diccionario quechua que sugiere palabras mientras se escribe. Muestra el
 * subárbol del trie que empieza con la primera letra escrita y resalta el prefijo.
 */
public class VistaCasoAutocompletar extends BorderPane {

    private final AutocompletadoQuechua buscador = AutocompletadoQuechua.cargar();
    private final TextField entrada = new TextField("pu");
    private final Label resultado = new Label();
    private final Canvas lienzo = new Canvas();
    private final TableView<Sugerencia> tabla = new TableView<>();

    public VistaCasoAutocompletar() {
        Label titulo = new Label("Semana 16 · Caso: autocompletar el diccionario quechua");
        titulo.getStyleClass().add("titulo-vista");
        Label nota = new Label("Escribe el comienzo de una palabra. El diccionario es el de la semana 04 ("
                + buscador.trie().palabras() + " palabras); ahora está guardado en un trie.");
        nota.setWrapText(true);
        entrada.setPrefColumnCount(14);
        entrada.textProperty().addListener((o, a, b) -> actualizar());
        HBox barra = new HBox(8, new Label("Buscar"), entrada);
        barra.setAlignment(Pos.CENTER_LEFT);
        resultado.getStyleClass().add("leyenda-paso");
        resultado.setWrapText(true);
        VBox cabecera = new VBox(6, titulo, nota, barra, resultado);
        cabecera.setPadding(new Insets(0, 8, 8, 8));
        setTop(cabecera);

        Pane contenedor = new Pane(lienzo);
        contenedor.setMinSize(0, 0);
        contenedor.setPrefSize(650, 450);
        lienzo.widthProperty().bind(contenedor.widthProperty());
        lienzo.heightProperty().bind(contenedor.heightProperty());
        lienzo.widthProperty().addListener(o -> dibujar());
        lienzo.heightProperty().addListener(o -> dibujar());

        TableColumn<Sugerencia, String> palabra = new TableColumn<>("Palabra");
        palabra.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().palabra()));
        palabra.setPrefWidth(120);
        TableColumn<Sugerencia, String> significado = new TableColumn<>("Significado");
        significado.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(f.getValue().significado()));
        significado.setPrefWidth(200);
        tabla.getColumns().setAll(List.of(palabra, significado));
        Label tituloTabla = new Label("Sugerencias");
        tituloTabla.getStyleClass().add("subtitulo-traza");
        VBox derecha = new VBox(6, tituloTabla, tabla);
        derecha.setPadding(new Insets(8));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        SplitPane division = new SplitPane(contenedor, derecha);
        division.setDividerPositions(0.62);
        setCenter(division);
        actualizar();
    }

    private String prefijo() {
        return entrada.getText().trim().toLowerCase();
    }

    private void actualizar() {
        String p = prefijo();
        List<Sugerencia> s = buscador.sugerencias(p);
        tabla.getItems().setAll(s);
        resultado.setText((p.isEmpty() ? "Sin prefijo: se sugieren todas las palabras. "
                : s.isEmpty() ? "Ninguna palabra empieza con \"" + p + "\". " : s.size() + " palabra"
                + (s.size() == 1 ? "" : "s") + " empiezan con \"" + p + "\". ")
                + "El trie visitó " + buscador.pasosTrie(p) + " nodos; una búsqueda lineal revisaría las "
                + buscador.revisionesLineales() + " palabras. El trie guarda las " + buscador.letras()
                + " letras del diccionario en " + (buscador.trie().nodos() - 1) + " nodos: los comienzos comunes se"
                + " guardan una sola vez.");
        dibujar();
    }

    private void dibujar() {
        var gc = lienzo.getGraphicsContext2D();
        gc.setFill(Paleta.FONDO);
        gc.fillRect(0, 0, lienzo.getWidth(), lienzo.getHeight());
        String p = prefijo();
        String desde = p.isEmpty() ? "" : p.substring(0, 1);
        if (!buscador.trie().prefijos(false).contains(desde)) {
            gc.setFill(Paleta.TEXTO);
            gc.fillText("Ninguna palabra empieza con \"" + desde + "\".", 20, 30);
            return;
        }
        DibujoTrie.dibujar(gc, buscador.trie().prefijos(false), buscador.trie().prefijos(true), desde,
                x -> !p.isEmpty() && x.startsWith(p) ? Color.web("#fff59d")
                        : p.startsWith(x) ? Color.web("#bbdefb") : Color.WHITE,
                lienzo.getWidth(), lienzo.getHeight());
    }
}
