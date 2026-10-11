package pe.edu.unaj.aed2.semana11.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
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
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana08.modelo.GrafoPonderado;
import pe.edu.unaj.aed2.semana11.modelo.EstadoFloyd;
import pe.edu.unaj.aed2.semana11.modelo.FloydWarshall;

import java.util.List;
import java.util.Map;

/** Semana 11: Floyd–Warshall paso a paso sobre 7 pueblos del norte del lago, con las matrices dist y sig. */
public class VisualizadorFloyd extends BorderPane {

    private static final RedVial RED = RedVial.cargar();
    private static final GrafoPonderado GRAFO = GrafoPonderado.desde(RED);
    private static final GrafoPonderado SUBRED = FloydWarshall.subred(GRAFO, FloydWarshall.SUBRED);
    private static final Map<String, String> NOMBRES = RED.nombres();

    private static final Color FILA_K = Color.web("#e3f2fd");
    private static final Color MEJORADA = Color.web("#c8e6c9");
    private static final Color DIAGONAL = Color.web("#eeeeee");

    private final PanelAnimacion<EstadoFloyd> panel =
            new PanelAnimacion<>(VisualizadorFloyd::dibujar, EstadoFloyd::descripcion);

    public VisualizadorFloyd() {
        PanelTraza<EstadoFloyd> traza = new PanelTraza<>(panel, FloydWarshall.codigo(GRAFO));
        Button kAnterior = new Button("◀ k anterior");
        Button kSiguiente = new Button("k siguiente ▶");
        kAnterior.setOnAction(e -> saltarK(-1));
        kSiguiente.setOnAction(e -> saltarK(1));
        HBox opciones = new HBox(8, kAnterior, kSiguiente, new Label("Pueblos: "
                + String.join(", ", FloydWarshall.SUBRED.stream().map(NOMBRES::get).toList())
                + " · km estimados (semana 08)."));
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8));

        Label titulo = new Label("Semana 11 · Todos los pares: Floyd–Warshall");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.55);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        panel.setPasos(FloydWarshall.pasos(SUBRED, NOMBRES));
    }

    /** Salta al inicio de la vuelta de k siguiente (o anterior). */
    private void saltarK(int sentido) {
        List<EstadoFloyd> pasos = panel.pasos();
        int actual = panel.indiceProperty().get();
        for (int p = actual + sentido; p >= 0 && p < pasos.size(); p += sentido) {
            EstadoFloyd e = pasos.get(p);
            if (e.tipo() == EstadoFloyd.Tipo.NUEVO_K || (sentido > 0 && e.tipo() == EstadoFloyd.Tipo.FIN)
                    || (sentido < 0 && p == 0)) {
                panel.irA(p);
                return;
            }
        }
    }

    private static void dibujar(GraphicsContext gc, EstadoFloyd paso, double ancho, double alto) {
        int n = paso.vertices().size();
        double celda = Math.max(26, Math.min(48, Math.min((ancho - 40) / (2 * (n + 1)), (alto - 150) / (n + 1))));
        double arriba = 68;
        double x0 = 10;
        double x1 = x0 + (n + 1) * celda + 20;
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("System", FontWeight.NORMAL, 12));
        gc.setFill(Color.web("#546e7a"));
        gc.fillText("Azul: fila y columna de k · Naranja: dist[i][k] y dist[k][j] · Amarillo: dist[i][j] examinada",
                10, 12);
        gc.fillText("Verde: mejoró en esta vuelta de k · ∞: todavía no se conoce camino", 10, 28);
        matriz(gc, paso, "dist (km)", x0, arriba, celda, true);
        matriz(gc, paso, "sig (siguiente parada)", x1, arriba, celda, false);

        double y = arriba + (n + 1) * celda + 30;
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font("System", FontWeight.BOLD, 13));
        if (paso.k() >= 0) {
            String k = NOMBRES.get(paso.vertices().get(paso.k()));
            gc.fillText("Vuelta k = " + paso.k() + ": ¿conviene pasar por " + k + "?", 10, y);
            if (paso.i() >= 0) {
                String i = NOMBRES.get(paso.vertices().get(paso.i()));
                String j = NOMBRES.get(paso.vertices().get(paso.j()));
                int ik = paso.dist().get(paso.i()).get(paso.k());
                int kj = paso.dist().get(paso.k()).get(paso.j());
                gc.setFont(Font.font("System", FontWeight.NORMAL, 13));
                gc.fillText(i + " → " + k + " → " + j + ":  " + texto(ik) + " + " + texto(kj)
                        + (ik == FloydWarshall.INF || kj == FloydWarshall.INF ? "  (no hay camino por " + k + ")"
                        : " = " + (ik + kj) + " km"), 10, y + 22);
            }
        } else {
            gc.fillText(paso.tipo() == EstadoFloyd.Tipo.FIN ? "Listo: distancias mínimas entre todos los pares."
                    : "Matriz inicial: solo carreteras directas.", 10, y);
        }
    }

    private static void matriz(GraphicsContext gc, EstadoFloyd paso, String titulo, double x, double y, double celda,
                               boolean esDist) {
        int n = paso.vertices().size();
        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font("System", FontWeight.BOLD, 13));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText(titulo, x, y - 8);
        gc.setTextAlign(TextAlignment.CENTER);
        for (int a = 0; a < n; a++) {
            String id = paso.vertices().get(a);
            boolean esK = a == paso.k();
            gc.setFont(Font.font("System", esK ? FontWeight.BOLD : FontWeight.NORMAL, 11));
            gc.setFill(esK ? Color.web("#1565c0") : Paleta.TEXTO);
            gc.fillText(id, x + (a + 1.5) * celda, y + celda / 2);
            gc.fillText(id, x + celda / 2, y + (a + 1.5) * celda);
        }
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double cx = x + (j + 1) * celda;
                double cy = y + (i + 1) * celda;
                Color fondo = Color.WHITE;
                if (i == j) {
                    fondo = DIAGONAL;
                } else if (i == paso.i() && j == paso.j()) {
                    fondo = paso.tipo() == EstadoFloyd.Tipo.MEJORA ? Paleta.FINALIZADO : Paleta.RESALTADO;
                } else if (esDist && paso.i() >= 0 && ((i == paso.i() && j == paso.k())
                        || (i == paso.k() && j == paso.j()))) {
                    fondo = Paleta.COMPARANDO;
                } else if (paso.mejoradas().contains(EstadoFloyd.casilla(i, j))) {
                    fondo = MEJORADA;
                } else if (i == paso.k() || j == paso.k()) {
                    fondo = FILA_K;
                }
                gc.setFill(fondo);
                gc.fillRect(cx, cy, celda, celda);
                gc.setStroke(Color.web("#b0bec5"));
                gc.setLineWidth(1);
                gc.strokeRect(cx, cy, celda, celda);
                String valor;
                if (esDist) {
                    valor = texto(paso.dist().get(i).get(j));
                } else {
                    int s = paso.sig().get(i).get(j);
                    valor = s < 0 ? "—" : paso.vertices().get(s);
                }
                gc.setFill(Paleta.TEXTO);
                gc.setFont(Font.font("System", FontWeight.NORMAL, esDist ? 13 : 11));
                gc.fillText(valor, cx + celda / 2, cy + celda / 2);
            }
        }
    }

    private static String texto(int d) {
        return d == FloydWarshall.INF ? "∞" : String.valueOf(d);
    }
}
