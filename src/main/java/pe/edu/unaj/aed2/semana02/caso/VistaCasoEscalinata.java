package pe.edu.unaj.aed2.semana02.caso;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import pe.edu.unaj.aed2.comun.benchmark.GraficoComplejidad;
import pe.edu.unaj.aed2.comun.visual.ControlesPaso;
import pe.edu.unaj.aed2.comun.visual.DibujoArbol;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.comun.visual.PanelAnimacion;
import pe.edu.unaj.aed2.comun.visual.PanelTraza;
import pe.edu.unaj.aed2.semana02.modelo.LlamadaRecursiva;
import pe.edu.unaj.aed2.semana02.modelo.PasoLlamada;

/**
 * Caso práctico semana 02: formas de subir la escalinata del cerro Huaynaroque
 * (Juliaca) con pasos de 1 o 2 peldaños. Ejecuta paso a paso las llamadas
 * recursivas, con y sin memorización, mostrando la pila de ejecución.
 */
public class VistaCasoEscalinata extends BorderPane {

    private static final int N_MAX_ARBOL = 8;
    private static final int N_MAX_GRAFICO = 25;
    private static final Color PENDIENTE = Color.web("#eceff1");

    private final PanelAnimacion<PasoLlamada> panel = new PanelAnimacion<>(this::dibujar, PasoLlamada::descripcion);
    private final PanelTraza<PasoLlamada> traza = new PanelTraza<>(panel, Escalinata.CODIGO_RECURSIVO);
    private final Slider peldanios = new Slider(1, N_MAX_ARBOL, 5);
    private final CheckBox memo = new CheckBox("Usar memorización");
    private final Label resultado = new Label();
    private LlamadaRecursiva arbol;

    public VistaCasoEscalinata() {
        Label titulo = new Label("Semana 02 · Caso: la escalinata del cerro Huaynaroque");
        titulo.getStyleClass().add("titulo-vista");
        Label enunciado = new Label("¿De cuántas formas se suben n peldaños dando pasos de 1 o 2? "
                + "formas(n) = formas(n−1) + formas(n−2)");

        peldanios.setMajorTickUnit(1);
        peldanios.setMinorTickCount(0);
        peldanios.setSnapToTicks(true);
        peldanios.setShowTickLabels(true);
        peldanios.setPrefWidth(220);
        peldanios.valueProperty().addListener((o, a, b) -> cargar());
        memo.setOnAction(e -> cargar());

        HBox opciones = new HBox(10, new Label("n ="), peldanios, memo, resultado);
        opciones.setAlignment(Pos.CENTER_LEFT);
        VBox cabecera = new VBox(6, titulo, enunciado, opciones);
        cabecera.setPadding(new Insets(0, 8, 8, 8));
        setTop(cabecera);

        traza.getTabs().add(new Tab("Crecimiento", crearGrafico()));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.5);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        cargar();
    }

    private GraficoComplejidad crearGrafico() {
        GraficoComplejidad grafico = new GraficoComplejidad("Llamadas recursivas vs. n",
                "Peldaños (n)", "Número de llamadas");
        for (int n = 1; n <= N_MAX_GRAFICO; n++) {
            grafico.agregarPunto("Recursión directa O(φⁿ)", n, Escalinata.llamadasRecursivo(n));
            grafico.agregarPunto("Con memorización O(n)", n, Escalinata.llamadasMemo(n));
        }
        return grafico;
    }

    private void cargar() {
        int n = (int) Math.round(peldanios.getValue());
        boolean conMemo = memo.isSelected();
        arbol = conMemo ? Escalinata.arbolMemo(n) : Escalinata.arbolRecursivo(n);
        resultado.setText("formas(" + n + ") = " + arbol.resultado()
                + "  ·  llamadas: " + arbol.totalLlamadas()
                + "  ·  reusadas: " + arbol.totalReusadas());
        traza.setCodigo(conMemo ? Escalinata.CODIGO_MEMO : Escalinata.CODIGO_RECURSIVO);
        panel.setPasos(Escalinata.pasos(arbol, conMemo));
    }

    private void dibujar(GraphicsContext gc, PasoLlamada paso, double ancho, double alto) {
        double altoLeyenda = 24;
        DibujoArbol.dibujar(gc, arbol, LlamadaRecursiva::izquierda, LlamadaRecursiva::derecha,
                l -> "f" + l.n(),
                l -> color(paso, l),
                ancho, alto - altoLeyenda);
        gc.setFont(Font.font(12));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.BOTTOM);
        gc.setFill(Paleta.TEXTO);
        gc.fillText("Gris: pendiente · Rojo: recién llamada · Naranja: en la pila · Verde: retornó · "
                + "Amarillo: tomada de memo", 8, alto - 6);
    }

    private static Color color(PasoLlamada paso, LlamadaRecursiva l) {
        if (l.id() == paso.nodo() && paso.tipo() == PasoLlamada.Tipo.LLAMADA) {
            return Paleta.INTERCAMBIO;
        }
        return switch (paso.estado(l.id())) {
            case PENDIENTE -> PENDIENTE;
            case EN_PILA -> Paleta.COMPARANDO;
            case RETORNADA -> Paleta.FINALIZADO;
            case REUSADA -> Paleta.RESALTADO;
        };
    }
}
