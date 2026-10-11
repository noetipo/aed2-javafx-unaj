package pe.edu.unaj.aed2.semana09.vista;

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
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana07.vista.DibujoRed;
import pe.edu.unaj.aed2.semana08.modelo.GrafoPonderado;
import pe.edu.unaj.aed2.semana09.modelo.ArbolExpansion;
import pe.edu.unaj.aed2.semana09.modelo.EstadoArbolExpansion;

import java.util.List;
import java.util.Map;

/** Semana 09: árbol de expansión mínima con Kruskal y Prim sobre los posibles tendidos de fibra óptica. */
public class VisualizadorArbolExpansion extends BorderPane {

    private static final RedVial RED = ArbolExpansion.cargarTendidos();
    private static final GrafoPonderado GRAFO = GrafoPonderado.desde(RED);
    private static final Map<String, String> NOMBRES = RED.nombres();
    /** Un color por grupo de union-find (los grupos se numeran desde 1). */
    private static final Color[] GRUPOS = {
            Color.web("#64b5f6"), Color.web("#ffb74d"), Color.web("#ba68c8"), Color.web("#4db6ac"),
            Color.web("#f06292"), Color.web("#aed581"), Color.web("#a1887f")
    };
    private static final int MAXIMO_PENDIENTES = 9;

    private final PanelAnimacion<EstadoArbolExpansion> panel =
            new PanelAnimacion<>(VisualizadorArbolExpansion::dibujar, EstadoArbolExpansion::descripcion);
    private final PanelTraza<EstadoArbolExpansion> traza =
            new PanelTraza<>(panel, ArbolExpansion.codigoKruskal(GRAFO));
    private final ComboBox<String> origen = new ComboBox<>();
    private boolean esKruskal = true;

    public VisualizadorArbolExpansion() {
        GRAFO.vertices().forEach(v -> origen.getItems().add(NOMBRES.get(v) + " (" + v + ")"));
        origen.getSelectionModel().selectFirst();
        origen.setDisable(true);
        Button kruskal = new Button("Kruskal");
        Button prim = new Button("Prim");
        kruskal.setOnAction(e -> {
            esKruskal = true;
            origen.setDisable(true);
            cargar();
        });
        prim.setOnAction(e -> {
            esKruskal = false;
            origen.setDisable(false);
            cargar();
        });
        origen.setOnAction(e -> cargar());
        HBox opciones = new HBox(8, kruskal, prim, new Label("Origen (Prim)"), origen,
                new Label("Posibles tendidos de fibra; km estimados, red didáctica."));
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8));

        Label titulo = new Label("Semana 09 · Árbol de expansión mínima: Kruskal y Prim");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.55);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        cargar();
    }

    private void cargar() {
        if (esKruskal) {
            traza.setCodigo(ArbolExpansion.codigoKruskal(GRAFO));
            panel.setPasos(ArbolExpansion.pasosKruskal(GRAFO, NOMBRES));
        } else {
            String texto = origen.getValue();
            String s = texto.substring(texto.lastIndexOf('(') + 1, texto.length() - 1);
            traza.setCodigo(ArbolExpansion.codigoPrim(GRAFO));
            panel.setPasos(ArbolExpansion.pasosPrim(GRAFO, s, NOMBRES));
        }
    }

    private static void dibujar(GraphicsContext gc, EstadoArbolExpansion paso, double ancho, double alto) {
        boolean prim = !paso.conectados().isEmpty();
        double arriba = 30;
        double abajo = 46;
        String examinada = paso.u() == null ? null : EstadoRecorrido.arista(paso.u(), paso.v());
        gc.save();
        gc.translate(0, arriba);
        DibujoRed.dibujar(gc, RED,
                v -> {
                    if (prim) {
                        return paso.conectados().contains(v) ? Paleta.FINALIZADO : Color.WHITE;
                    }
                    Integer g = paso.grupos().get(v);
                    return g == null ? Color.WHITE : GRUPOS[(g - 1) % GRUPOS.length];
                },
                v -> v.equals(paso.u()) || v.equals(paso.v()) ? Paleta.RESALTADO : null,
                (u, v) -> {
                    String c = EstadoRecorrido.arista(u, v);
                    if (c.equals(examinada)) {
                        Color color = switch (paso.tipo()) {
                            case DESCARTA -> Paleta.INTERCAMBIO;
                            case OMITE -> Color.web("#bdbdbd");
                            default -> Paleta.COMPARANDO;
                        };
                        return new DibujoRed.Trazo(color, 6);
                    }
                    if (paso.arbol().contains(c)) {
                        return new DibujoRed.Trazo(Color.web("#2e7d32"), 4);
                    }
                    if (paso.descartadas().contains(c)) {
                        return new DibujoRed.Trazo(Color.web("#ef9a9a"), 2);
                    }
                    if (paso.candidatas().contains(c)) {
                        return new DibujoRed.Trazo(Color.web("#ffcc80"), 3);
                    }
                    return new DibujoRed.Trazo(Color.web("#cfd8dc"), 1.5);
                },
                (u, v) -> String.valueOf(GRAFO.km(u, v)),
                v -> "",
                ancho, alto - arriba - abajo);
        gc.restore();

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("System", FontWeight.NORMAL, 12));
        gc.setFill(Color.web("#546e7a"));
        gc.fillText(prim ? "Verde: pueblo conectado · Blanco: falta conectar · Ámbar: tramo en la cola"
                : "Cada color es un grupo de union-find · Blanco: pueblo todavía solo", 10, 12);
        gc.fillText("Verde oscuro: tramo del árbol · Naranja: se acepta · Rojo: cerraría un ciclo (descartado)",
                10, 28);

        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font("System", FontWeight.BOLD, 13));
        int k = paso.arbol().size();
        gc.fillText("Árbol: " + (k == 1 ? "1 tramo" : k + " tramos") + " · " + paso.total() + " km", 10, alto - 34);
        gc.setFont(Font.font("System", FontWeight.NORMAL, 12));
        List<String> p = paso.pendientes();
        String lista = p.isEmpty() ? (prim ? "vacía" : "ninguno")
                : String.join(" ", p.subList(0, Math.min(p.size(), MAXIMO_PENDIENTES)))
                + (p.size() > MAXIMO_PENDIENTES ? " … (+" + (p.size() - MAXIMO_PENDIENTES) + ")" : "");
        gc.fillText((prim ? "Cola de prioridad (de menor a mayor): " : "Tramos por revisar, en orden: ") + lista,
                10, alto - 14);
    }
}
