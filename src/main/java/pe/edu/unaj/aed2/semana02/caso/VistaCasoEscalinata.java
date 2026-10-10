package pe.edu.unaj.aed2.semana02.caso;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pe.edu.unaj.aed2.comun.benchmark.GraficoComplejidad;
import pe.edu.unaj.aed2.comun.visual.DibujoArbol;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.semana02.modelo.LlamadaRecursiva;

/**
 * Caso práctico semana 02: formas de subir la escalinata del cerro Huaynaroque
 * (Juliaca) con pasos de 1 o 2 peldaños. Muestra el árbol de llamadas con y sin
 * memorización, y cómo crece el número de llamadas con n.
 */
public class VistaCasoEscalinata extends BorderPane {

    private static final int N_MAX_ARBOL = 8;
    private static final int N_MAX_GRAFICO = 25;

    private final Canvas lienzo = new Canvas();
    private final Slider peldanios = new Slider(1, N_MAX_ARBOL, 5);
    private final CheckBox memo = new CheckBox("Usar memorización");
    private final Label resultado = new Label();

    public VistaCasoEscalinata() {
        Label titulo = new Label("Semana 02 · Caso: la escalinata del cerro Huaynaroque");
        titulo.getStyleClass().add("titulo-vista");
        Label enunciado = new Label("¿De cuántas formas se suben n peldaños dando pasos de 1 o 2? "
                + "formas(n) = formas(n−1) + formas(n−2)");
        enunciado.setPadding(new Insets(0, 8, 0, 8));
        setTop(new VBox(titulo, enunciado));

        SplitPane division = new SplitPane(crearPanelArbol(), crearPanelGrafico());
        division.setDividerPositions(0.6);
        setCenter(division);
        actualizar();
    }

    private VBox crearPanelArbol() {
        peldanios.setMajorTickUnit(1);
        peldanios.setMinorTickCount(0);
        peldanios.setSnapToTicks(true);
        peldanios.setShowTickLabels(true);
        peldanios.setPrefWidth(220);
        peldanios.valueProperty().addListener((o, a, b) -> actualizar());
        memo.setOnAction(e -> actualizar());

        HBox barra = new HBox(10, new Label("n ="), peldanios, memo);
        barra.setAlignment(Pos.CENTER_LEFT);

        Label leyenda = new Label("Azul: llamada calculada · Verde: caso base · Amarillo: resultado tomado de la memoria");
        leyenda.getStyleClass().add("leyenda-paso");

        Pane contenedor = new Pane(lienzo);
        contenedor.setMinSize(0, 0);
        contenedor.setPrefSize(600, 400);
        lienzo.widthProperty().bind(contenedor.widthProperty());
        lienzo.heightProperty().bind(contenedor.heightProperty());
        lienzo.widthProperty().addListener(o -> dibujar());
        lienzo.heightProperty().addListener(o -> dibujar());

        VBox panel = new VBox(8, barra, resultado, contenedor, leyenda);
        panel.setPadding(new Insets(8));
        VBox.setVgrow(contenedor, Priority.ALWAYS);
        return panel;
    }

    private VBox crearPanelGrafico() {
        GraficoComplejidad grafico = new GraficoComplejidad("Llamadas recursivas vs. n",
                "Peldaños (n)", "Número de llamadas");
        for (int n = 1; n <= N_MAX_GRAFICO; n++) {
            grafico.agregarPunto("Recursión directa O(φⁿ)", n, Escalinata.llamadasRecursivo(n));
            grafico.agregarPunto("Con memorización O(n)", n, Escalinata.llamadasMemo(n));
        }
        VBox panel = new VBox(grafico);
        panel.setPadding(new Insets(8));
        VBox.setVgrow(grafico, Priority.ALWAYS);
        return panel;
    }

    private void actualizar() {
        int n = (int) Math.round(peldanios.getValue());
        LlamadaRecursiva arbol = memo.isSelected() ? Escalinata.arbolMemo(n) : Escalinata.arbolRecursivo(n);
        resultado.setText("formas(" + n + ") = " + arbol.resultado()
                + "   ·   llamadas: " + arbol.totalLlamadas()
                + "   ·   reusadas de memoria: " + arbol.totalReusadas());
        dibujar();
    }

    private void dibujar() {
        int n = (int) Math.round(peldanios.getValue());
        LlamadaRecursiva arbol = memo.isSelected() ? Escalinata.arbolMemo(n) : Escalinata.arbolRecursivo(n);
        var gc = lienzo.getGraphicsContext2D();
        gc.setFill(Paleta.FONDO);
        gc.fillRect(0, 0, lienzo.getWidth(), lienzo.getHeight());
        DibujoArbol.dibujar(gc, arbol, LlamadaRecursiva::izquierda, LlamadaRecursiva::derecha,
                l -> "f" + l.n(),
                l -> l.reusada() ? Paleta.RESALTADO : (l.n() <= 1 ? Paleta.FINALIZADO : Paleta.NORMAL),
                lienzo.getWidth(), lienzo.getHeight());
    }
}
