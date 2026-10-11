package pe.edu.unaj.aed2.semana06.vista;

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
import javafx.scene.text.TextAlignment;
import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.visual.ControlesPaso;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.comun.visual.PanelAnimacion;
import pe.edu.unaj.aed2.comun.visual.PanelTraza;
import pe.edu.unaj.aed2.semana06.modelo.EstadoMonticulo;
import pe.edu.unaj.aed2.semana06.modelo.Heapsort;
import pe.edu.unaj.aed2.semana06.modelo.MonticuloMin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Semana 06: montículo de mínimos (insertar y extraer) y heapsort, como árbol y como arreglo. */
public class VisualizadorMonticulo extends BorderPane {

    private static final int[] EJEMPLO = {40, 20, 50, 10, 30, 15, 5};
    private static final int[] PARA_ORDENAR = {35, 12, 48, 7, 26, 19, 41, 3, 30, 15};

    private final PanelAnimacion<EstadoMonticulo> panel =
            new PanelAnimacion<>(VisualizadorMonticulo::dibujar, EstadoMonticulo::descripcion);
    private final PanelTraza<EstadoMonticulo> traza = new PanelTraza<>(panel, MonticuloMin.CODIGO_INSERTAR);
    private final TextField clave = new TextField("25");
    private final Label estado = new Label();
    private MonticuloMin<Integer> monticulo = new MonticuloMin<>();

    public VisualizadorMonticulo() {
        clave.setPrefColumnCount(4);
        Button insertar = new Button("Insertar");
        Button extraer = new Button("Extraer mínimo");
        Button ejemplo = new Button("Cargar ejemplo");
        Button nuevo = new Button("Montículo nuevo");
        Button heapsort = new Button("Heapsort " + Arrays.toString(PARA_ORDENAR));
        insertar.setDefaultButton(true);
        insertar.setOnAction(e -> {
            try {
                mostrar(monticulo.pasosInsertar(Integer.parseInt(clave.getText().strip())),
                        MonticuloMin.CODIGO_INSERTAR);
            } catch (NumberFormatException ex) {
                estado.setText("Escribe un número entero.");
            }
        });
        extraer.setOnAction(e -> mostrar(monticulo.pasosExtraer(), MonticuloMin.CODIGO_EXTRAER));
        ejemplo.setOnAction(e -> cargarEjemplo());
        nuevo.setOnAction(e -> {
            monticulo = new MonticuloMin<>();
            mostrar(monticulo.estadoActual("Montículo vacío. Escribe un número y pulsa Insertar."),
                    MonticuloMin.CODIGO_INSERTAR);
        });
        heapsort.setOnAction(e -> {
            traza.setCodigo(Heapsort.CODIGO);
            panel.setPasos(Heapsort.pasos(PARA_ORDENAR));
            estado.setText("Heapsort sobre un arreglo aparte (montículo de MÁXIMOS).");
        });

        HBox opciones = new HBox(8, new Label("Valor"), clave, insertar, extraer, ejemplo, nuevo, heapsort);
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8, 8, 0, 8));
        estado.setPadding(new Insets(0, 8, 4, 8));

        Label titulo = new Label("Semana 06 · Montículo (cola de prioridad) y heapsort");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones, estado));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.5);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        cargarEjemplo();
    }

    private void cargarEjemplo() {
        monticulo = new MonticuloMin<>();
        List<EstadoMonticulo> pasos = new ArrayList<>();
        for (int x : EJEMPLO) {
            pasos.addAll(monticulo.pasosInsertar(x));
        }
        mostrar(pasos, MonticuloMin.CODIGO_INSERTAR);
    }

    private void mostrar(List<EstadoMonticulo> pasos, CodigoAlgoritmo codigo) {
        traza.setCodigo(codigo);
        panel.setPasos(pasos);
        estado.setText("Al terminar: n = " + monticulo.tamanio()
                + (monticulo.estaVacio() ? "" : " · mínimo = " + monticulo.minimo())
                + " · arreglo " + monticulo.arreglo());
    }

    private static void dibujar(GraphicsContext gc, EstadoMonticulo paso, double ancho, double alto) {
        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font(12));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("Naranja: elemento que se mueve · Amarillo: con quién se compara · Rojo: recién colocado"
                + " · Verde: posición final (heapsort)", 10, 12);
        gc.save();
        gc.translate(0, 24);
        DibujoMonticulo.dibujar(gc, paso.valores(), paso.tamanio(), k -> color(paso, k), ancho, alto - 24);
        gc.restore();
    }

    private static Color color(EstadoMonticulo paso, int k) {
        if (k == paso.i()) {
            return switch (paso.tipo()) {
                case COLOCA -> Paleta.INTERCAMBIO;
                case TOMA_RAIZ -> Paleta.FINALIZADO;
                default -> Paleta.COMPARANDO;
            };
        }
        if (k == paso.j()) {
            return paso.tipo() == EstadoMonticulo.Tipo.UBICA_MAYOR ? Paleta.FINALIZADO : Paleta.RESALTADO;
        }
        return Paleta.NORMAL;
    }
}
