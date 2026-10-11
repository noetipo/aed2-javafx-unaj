package pe.edu.unaj.aed2.semana10.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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
import pe.edu.unaj.aed2.semana07.vista.DibujoRed;
import pe.edu.unaj.aed2.semana10.modelo.EstadoTopologico;
import pe.edu.unaj.aed2.semana10.modelo.GrafoDirigido;
import pe.edu.unaj.aed2.semana10.modelo.MallaCurricular;

import java.util.Map;

/** Semana 10: orden topológico de la malla curricular con Kahn y con DFS, con o sin un ciclo. */
public class VisualizadorOrdenTopologico extends BorderPane {

    private static final MallaCurricular MALLA = MallaCurricular.cargar();
    private static final Map<String, String> NOMBRES = MALLA.nombres();

    private final PanelAnimacion<EstadoTopologico> panel = new PanelAnimacion<>(this::dibujar,
            EstadoTopologico::descripcion);
    private final PanelTraza<EstadoTopologico> traza = new PanelTraza<>(panel, GrafoDirigido.CODIGO_KAHN);
    private final CheckBox conError = new CheckBox("Con un error: AED II como prerrequisito de AED I");
    private boolean esKahn = true;

    public VisualizadorOrdenTopologico() {
        Button kahn = new Button("Kahn (grados de entrada)");
        Button dfs = new Button("DFS (postorden invertido)");
        kahn.setOnAction(e -> {
            esKahn = true;
            cargar();
        });
        dfs.setOnAction(e -> {
            esKahn = false;
            cargar();
        });
        conError.setOnAction(e -> cargar());
        HBox opciones = new HBox(8, kahn, dfs, conError, new Label("Malla ficticia y simplificada."));
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8));

        Label titulo = new Label("Semana 10 · Grafos dirigidos: orden topológico");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.55);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        cargar();
    }

    private GrafoDirigido grafo() {
        return conError.isSelected() ? MALLA.grafoConError() : MALLA.grafo();
    }

    private void cargar() {
        traza.setCodigo(esKahn ? GrafoDirigido.CODIGO_KAHN : GrafoDirigido.CODIGO_DFS);
        panel.setPasos(esKahn ? grafo().pasosKahn(NOMBRES) : grafo().pasosDfs(NOMBRES));
    }

    private void dibujar(GraphicsContext gc, EstadoTopologico paso, double ancho, double alto) {
        boolean kahn = !paso.grados().isEmpty();
        double arriba = 30;
        double abajo = 46;
        gc.save();
        gc.translate(0, arriba);
        DibujoMalla.dibujar(gc, MALLA, grafo(),
                v -> DibujoRed.colorEstado(paso.estados().get(v)),
                v -> v.equals(paso.actual()) ? Paleta.COMPARANDO : v.equals(paso.vecino()) ? Paleta.RESALTADO : null,
                (u, v) -> {
                    String c = EstadoTopologico.arco(u, v);
                    if (c.equals(paso.arcoCiclo())) {
                        return new DibujoRed.Trazo(Paleta.INTERCAMBIO, 4);
                    }
                    if (u.equals(paso.actual()) && v.equals(paso.vecino())) {
                        return new DibujoRed.Trazo(Paleta.COMPARANDO, 4);
                    }
                    if (paso.arcosHechos().contains(c)) {
                        return kahn ? new DibujoRed.Trazo(Color.web("#e0e0e0"), 1.2)
                                : new DibujoRed.Trazo(Color.web("#2e7d32"), 3);
                    }
                    return new DibujoRed.Trazo(Color.web("#78909c"), 1.6);
                },
                v -> {
                    int k = paso.orden().indexOf(v);
                    if (kahn) {
                        return k >= 0 ? "#" + (k + 1) : "g = " + paso.grados().get(v);
                    }
                    return "";
                },
                ancho, alto - arriba - abajo);
        gc.restore();

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("System", FontWeight.NORMAL, 12));
        gc.setFill(Color.web("#546e7a"));
        gc.fillText(kahn ? "g = prerrequisitos pendientes (grado de entrada) · #n = posición en el orden"
                : "Blanco: sin visitar · Gris: en la pila de llamadas · Oscuro: terminado (en el orden)", 10, 12);
        gc.fillText(kahn ? "Blanco: espera · Gris: en la cola · Oscuro: en el orden · Arco claro: ya se quitó · Rojo: ciclo"
                : "Verde: arco por el que se llamó a visitar · Naranja: arco actual · Rojo: arco de regreso (ciclo)",
                10, 28);
        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font("System", FontWeight.BOLD, 13));
        gc.fillText((kahn ? "Cola (frente → final): " : "Pila de llamadas (abajo → arriba): ")
                + (paso.frontera().isEmpty() ? "vacía" : String.join(kahn ? ", " : " › ",
                paso.frontera().stream().map(NOMBRES::get).toList())), 10, alto - 34);
        gc.setFont(Font.font("System", FontWeight.NORMAL, 12));
        gc.fillText("Orden: " + (paso.orden().isEmpty() ? "(vacío)"
                : String.join(", ", paso.orden().stream().map(NOMBRES::get).toList())), 10, alto - 14);
    }
}
