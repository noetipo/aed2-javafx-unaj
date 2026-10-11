package pe.edu.unaj.aed2.semana17.vista;

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
import pe.edu.unaj.aed2.semana17.modelo.EstadoViajante;
import pe.edu.unaj.aed2.semana17.modelo.Viajante;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Semana 17: el viajante con la heurística del vecino más cercano y la mejora 2-opt (ruta de un dron). */
public class VisualizadorViajante extends BorderPane {

    private static final RedVial RED_COMPLETA = RedVial.cargar();
    private static final RedVial ZONA = RED_COMPLETA.zona(Viajante.EJEMPLO);
    private static final Map<String, String> NOMBRES = RED_COMPLETA.nombres();
    private static final Viajante VIAJANTE = new Viajante(RED_COMPLETA, Viajante.EJEMPLO);

    private final PanelAnimacion<EstadoViajante> panel =
            new PanelAnimacion<>(VisualizadorViajante::dibujar, EstadoViajante::descripcion);
    private final PanelTraza<EstadoViajante> traza = new PanelTraza<>(panel, VIAJANTE.codigo("vecino", NOMBRES));

    public VisualizadorViajante() {
        Button vecino = new Button("Vecino más cercano");
        Button dosOpt = new Button("Mejora 2-opt");
        vecino.setOnAction(e -> {
            traza.setCodigo(VIAJANTE.codigo("vecino", NOMBRES));
            panel.setPasos(VIAJANTE.pasosVecino(NOMBRES));
        });
        dosOpt.setOnAction(e -> {
            traza.setCodigo(VIAJANTE.codigo("opt", NOMBRES));
            panel.setPasos(VIAJANTE.pasosDosOpt(NOMBRES));
        });
        Viajante.Ruta optima = VIAJANTE.fuerzaBruta();
        HBox opciones = new HBox(8, vecino, dosOpt, new Label("Un dron sale de Juliaca, visita "
                + (Viajante.EJEMPLO.size() - 1) + " postas y vuelve. La fuerza bruta (" + optima.evaluadas()
                + " rutas) dice que lo mejor posible es " + optima.km() + " km."));
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8));
        Label titulo = new Label("Semana 17 · Problemas difíciles: el viajante y sus heurísticas");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.55);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        panel.setPasos(VIAJANTE.pasosVecino(NOMBRES));
    }

    private static void dibujar(GraphicsContext gc, EstadoViajante paso, double ancho, double alto) {
        Set<String> quitados = new HashSet<>();
        Set<String> nuevos = new HashSet<>();
        if (paso.tramoA() != null) {
            quitados.add(paso.tramoA().get(0) + "-" + paso.tramoA().get(1));
            quitados.add(paso.tramoB().get(0) + "-" + paso.tramoB().get(1));
            nuevos.add(paso.tramoA().get(0) + "-" + paso.tramoB().get(0));
            nuevos.add(paso.tramoA().get(1) + "-" + paso.tramoB().get(1));
        }
        if (paso.tipo() == EstadoViajante.Tipo.ELIGE || paso.tipo() == EstadoViajante.Tipo.CIERRA) {
            nuevos.add(paso.actual() + "-" + paso.candidato());
        }
        gc.save();
        gc.translate(0, 34);
        List<String> ruta = paso.ruta();
        DibujoRuta.dibujar(gc, ZONA, ruta, paso.cerrada(), quitados, nuevos,
                v -> v.equals(ruta.get(0)) ? Paleta.FINALIZADO : ruta.contains(v) ? Color.web("#bbdefb") : Color.WHITE,
                v -> v.equals(paso.candidato()) ? Paleta.RESALTADO : v.equals(paso.actual()) ? Paleta.COMPARANDO : null,
                t -> VIAJANTE.distancia(t.split("-")[0], t.split("-")[1]), ancho, alto - 34 - 30);
        gc.restore();
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("System", 12));
        gc.setFill(Color.web("#546e7a"));
        gc.fillText("Azul: ruta del dron (km en línea recta) · Verde: tramo recién agregado · Rojo punteado: tramos que"
                + " 2-opt quita", 10, 12);
        gc.fillText("Números: orden de visita · Blanco: posta sin visitar", 10, 28);
        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font("System", FontWeight.BOLD, 14));
        gc.fillText("Largo de la ruta: " + paso.km() + " km" + (paso.cerrada() ? "" : " (falta volver a la base)"),
                10, alto - 14);
    }
}
