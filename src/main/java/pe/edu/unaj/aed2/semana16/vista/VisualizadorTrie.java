package pe.edu.unaj.aed2.semana16.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pe.edu.unaj.aed2.comun.visual.ControlesPaso;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.comun.visual.PanelAnimacion;
import pe.edu.unaj.aed2.comun.visual.PanelTraza;
import pe.edu.unaj.aed2.semana16.modelo.EstadoTrie;
import pe.edu.unaj.aed2.semana16.modelo.Trie;

import java.util.Arrays;
import java.util.List;

/** Semana 16: trie (árbol de prefijos), insertar palabras y autocompletar un prefijo. */
public class VisualizadorTrie extends BorderPane {

    private final PanelAnimacion<EstadoTrie> panel =
            new PanelAnimacion<>(VisualizadorTrie::dibujar, EstadoTrie::descripcion);
    private final PanelTraza<EstadoTrie> traza = new PanelTraza<>(panel, Trie.CODIGO_INSERTAR);
    private final TextField palabras = new TextField(String.join(" ", Trie.EJEMPLO));
    private final TextField prefijo = new TextField("pu");

    public VisualizadorTrie() {
        Button insertar = new Button("Insertar palabras");
        Button autocompletar = new Button("Autocompletar");
        insertar.setOnAction(e -> {
            traza.setCodigo(Trie.CODIGO_INSERTAR);
            panel.setPasos(Trie.pasosInsertar(lista()));
        });
        autocompletar.setOnAction(e -> {
            traza.setCodigo(Trie.CODIGO_AUTOCOMPLETAR);
            panel.setPasos(Trie.pasosAutocompletar(lista(), prefijo.getText().trim().toLowerCase()));
        });
        prefijo.setOnAction(e -> autocompletar.fire());
        palabras.setPrefColumnCount(24);
        prefijo.setPrefColumnCount(6);
        HBox opciones = new HBox(8, new Label("Palabras"), palabras, insertar, new Label("Prefijo"), prefijo,
                autocompletar);
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8));
        Label titulo = new Label("Semana 16 · Tries: árbol de prefijos");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.55);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        panel.setPasos(Trie.pasosInsertar(lista()));
    }

    /** Palabras separadas por espacios, en minúsculas, sin vacías ni repetidas (hasta 12). */
    private List<String> lista() {
        return Arrays.stream(palabras.getText().toLowerCase().split("\\s+")).filter(p -> !p.isBlank()).distinct()
                .limit(12).toList();
    }

    private static void dibujar(GraphicsContext gc, EstadoTrie paso, double ancho, double alto) {
        boolean buscando = !paso.resultados().isEmpty() || paso.tipo() == EstadoTrie.Tipo.NO_EXISTE;
        gc.save();
        gc.translate(0, 34);
        DibujoTrie.dibujar(gc, paso.nodos(), paso.finales(), "", p -> {
            if (p.equals(paso.nuevo())) {
                return Paleta.FINALIZADO;
            }
            if (p.equals(paso.actual())) {
                return Paleta.COMPARANDO;
            }
            if (paso.actual() != null && paso.actual().startsWith(p)) {
                return Color.web("#bbdefb");
            }
            if (buscando && !paso.resultados().isEmpty() && p.startsWith(paso.palabra())) {
                return Color.web("#fff59d");
            }
            return Color.WHITE;
        }, ancho, alto - 34 - 30);
        gc.restore();
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("System", 12));
        gc.setFill(Color.web("#546e7a"));
        gc.fillText("Naranja: nodo actual (n) · Azul: camino desde la raíz · Verde: nodo recién creado · Borde verde:"
                + " fin de palabra", 10, 12);
        gc.fillText("Amarillo: subárbol con las sugerencias del prefijo", 10, 28);
        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font("System", FontWeight.BOLD, 13));
        gc.fillText("Nodos: " + paso.nodos().size() + " · palabras: " + paso.finales().size()
                + (paso.resultados().isEmpty() ? "" : " · sugerencias: " + String.join(", ", paso.resultados())),
                10, alto - 14);
    }
}
