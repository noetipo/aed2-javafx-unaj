package pe.edu.unaj.aed2.semana07.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
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
import pe.edu.unaj.aed2.semana07.modelo.EstadoRecorrido;
import pe.edu.unaj.aed2.semana07.modelo.Grafo;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;

import java.util.Map;

/** Semana 07: recorridos BFS y DFS sobre la red vial simplificada de la región Puno. */
public class VisualizadorRecorridos extends BorderPane {

    private static final RedVial RED = RedVial.cargar();
    private static final Map<String, String> NOMBRES = RED.nombres();

    private final PanelAnimacion<EstadoRecorrido> panel =
            new PanelAnimacion<>(VisualizadorRecorridos::dibujar, EstadoRecorrido::descripcion);
    private final PanelTraza<EstadoRecorrido> traza = new PanelTraza<>(panel, Grafo.CODIGO_BFS);
    private final ComboBox<String> origen = new ComboBox<>();
    private boolean esBfs = true;

    public VisualizadorRecorridos() {
        RED.grafo().vertices().forEach(v -> origen.getItems().add(NOMBRES.get(v) + " (" + v + ")"));
        origen.getSelectionModel().selectFirst();
        Button bfs = new Button("BFS (en anchura)");
        Button dfs = new Button("DFS (en profundidad)");
        bfs.setOnAction(e -> {
            esBfs = true;
            cargar();
        });
        dfs.setOnAction(e -> {
            esBfs = false;
            cargar();
        });
        origen.setOnAction(e -> cargar());

        HBox opciones = new HBox(8, new Label("Origen"), origen, bfs, dfs,
                new Label("Red vial simplificada y didáctica: no incluye todas las carreteras."));
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8));

        Label titulo = new Label("Semana 07 · Grafos: recorridos BFS y DFS");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.55);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        cargar();
    }

    private void cargar() {
        String texto = origen.getValue();
        String s = texto.substring(texto.lastIndexOf('(') + 1, texto.length() - 1);
        traza.setCodigo(esBfs ? Grafo.CODIGO_BFS : Grafo.CODIGO_DFS);
        panel.setPasos(esBfs ? RED.grafo().pasosBfs(s, NOMBRES) : RED.grafo().pasosDfs(s, NOMBRES));
    }

    private static void dibujar(GraphicsContext gc, EstadoRecorrido paso, double ancho, double alto) {
        boolean bfs = !paso.distancias().isEmpty() || paso.tipo() == EstadoRecorrido.Tipo.SACA;
        double abajo = 46;
        DibujoRed.dibujar(gc, RED,
                v -> DibujoRed.colorEstado(paso.estados().get(v)),
                v -> v.equals(paso.actual()) ? Paleta.COMPARANDO : v.equals(paso.vecino()) ? Paleta.RESALTADO : null,
                (u, v) -> {
                    boolean examinada = (u.equals(paso.actual()) && v.equals(paso.vecino()))
                            || (v.equals(paso.actual()) && u.equals(paso.vecino()));
                    if (examinada) {
                        return new DibujoRed.Trazo(Paleta.COMPARANDO, 4);
                    }
                    if (paso.arbol().contains(EstadoRecorrido.arista(u, v))) {
                        return new DibujoRed.Trazo(Color.web("#2e7d32"), 4);
                    }
                    return new DibujoRed.Trazo(Color.web("#b0bec5"), 1.5);
                },
                v -> paso.distancias().containsKey(v) ? "d=" + paso.distancias().get(v) : "",
                ancho, alto - abajo);

        gc.setFill(Paleta.TEXTO);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("System", FontWeight.BOLD, 13));
        String nombre = bfs ? "Cola (frente → final): " : "Pila de llamadas (abajo → arriba): ";
        gc.fillText(nombre + (paso.frontera().isEmpty() ? "vacía"
                : String.join(bfs ? ", " : " › ", paso.frontera().stream().map(NOMBRES::get).toList())), 10, alto - 32);
        gc.setFont(Font.font("System", FontWeight.NORMAL, 12));
        gc.fillText("Orden de visita: " + String.join(", ", paso.orden().stream().map(NOMBRES::get).toList()),
                10, alto - 12);
        gc.setFill(Color.web("#546e7a"));
        gc.fillText("Blanco: sin descubrir · Gris: " + (bfs ? "en la cola" : "en la pila") + " · Oscuro: terminado ·"
                + " Verde: árbol del recorrido · Naranja: actual · Amarillo: vecino examinado", 10, 12);
    }
}
