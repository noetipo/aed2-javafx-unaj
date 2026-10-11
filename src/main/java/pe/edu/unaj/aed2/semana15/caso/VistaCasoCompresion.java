package pe.edu.unaj.aed2.semana15.caso;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pe.edu.unaj.aed2.semana15.caso.CompresionTextos.Fila;
import pe.edu.unaj.aed2.semana15.caso.CompresionTextos.Resumen;
import pe.edu.unaj.aed2.semana15.modelo.Huffman;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;

/**
 * Caso práctico semana 15: compresión de mensajes. Se elige o escribe un texto; la tabla muestra el código de cada
 * carácter y el resumen compara ASCII, código fijo y Huffman, con el texto codificado y decodificado.
 */
public class VistaCasoCompresion extends BorderPane {

    private final Map<String, String> frases = CompresionTextos.frases();
    private final ComboBox<String> idioma = new ComboBox<>();
    private final TextArea texto = new TextArea();
    private final Label resultado = new Label();
    private final TextArea bits = new TextArea();
    private final TableView<Fila> tabla = new TableView<>();

    public VistaCasoCompresion() {
        Label titulo = new Label("Semana 15 · Caso: compresión de mensajes en quechua, aimara y castellano");
        titulo.getStyleClass().add("titulo-vista");
        Label nota = new Label("Elige una frase o escribe la tuya. Cada carácter recibe un código según cuántas veces"
                + " aparece: los frecuentes, cortos; los raros, largos.");
        nota.setWrapText(true);
        idioma.getItems().setAll(frases.keySet());
        idioma.getSelectionModel().selectFirst();
        idioma.setOnAction(e -> texto.setText(frases.get(idioma.getValue())));
        texto.setPrefRowCount(3);
        texto.setWrapText(true);
        texto.textProperty().addListener((o, a, b) -> actualizar());
        HBox barra = new HBox(8, new Label("Ejemplo"), idioma);
        barra.setAlignment(Pos.CENTER_LEFT);
        resultado.getStyleClass().add("leyenda-paso");
        resultado.setWrapText(true);
        VBox cabecera = new VBox(6, titulo, nota, barra, texto, resultado);
        cabecera.setPadding(new Insets(0, 8, 8, 8));
        setTop(cabecera);

        tabla.getColumns().setAll(List.of(
                columna("Carácter", f -> Huffman.visible(f.caracter()), 80),
                columna("Veces", f -> String.valueOf(f.veces()), 70),
                columna("Código", Fila::codigo, 140),
                columna("Bits", f -> f.veces() + " × " + f.codigo().length() + " = " + f.bits(), 120)));
        bits.setEditable(false);
        bits.setWrapText(true);
        bits.setStyle("-fx-font-family: monospace;");
        Label tituloBits = new Label("Texto codificado (y al decodificarlo se recupera el original)");
        tituloBits.getStyleClass().add("subtitulo-traza");
        VBox derecha = new VBox(6, tituloBits, bits);
        derecha.setPadding(new Insets(8));
        VBox.setVgrow(bits, Priority.ALWAYS);
        VBox izquierda = new VBox(6, tabla);
        izquierda.setPadding(new Insets(8));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        SplitPane division = new SplitPane(izquierda, derecha);
        division.setDividerPositions(0.45);
        setCenter(division);
        texto.setText(frases.get(idioma.getValue()));
    }

    private static TableColumn<Fila, String> columna(String titulo, Function<Fila, String> valor, double ancho) {
        TableColumn<Fila, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(valor.apply(f.getValue())));
        c.setSortable(false);
        c.setPrefWidth(ancho);
        return c;
    }

    private void actualizar() {
        String t = texto.getText();
        if (t.isEmpty()) {
            tabla.getItems().clear();
            bits.setText("");
            resultado.setText("Escribe un texto.");
            return;
        }
        tabla.getItems().setAll(CompresionTextos.tabla(t));
        Resumen r = CompresionTextos.resumir(t);
        resultado.setText(String.format(Locale.ROOT, "%d caracteres (%d distintos). ASCII: %d bits · código fijo:"
                        + " %d bits · Huffman: %d bits. Ahorro frente a ASCII: %.1f %%.%s", r.caracteres(),
                tabla.getItems().size(), r.ascii(), r.fijo(), r.huffman(), r.ahorro(),
                r.huffman() >= r.fijo() * 0.97 ? " Casi no mejora al código fijo: todos los caracteres aparecen"
                        + " parecido, no hay nada que aprovechar." : ""));
        bits.setText(r.bits() + "\n\nDecodificado: " + r.decodificado()
                + (r.decodificado().equals(t) ? "\n✔ Igual al original." : "\n✘ No coincide."));
    }
}
