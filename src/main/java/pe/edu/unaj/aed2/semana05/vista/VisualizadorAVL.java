package pe.edu.unaj.aed2.semana05.vista;

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
import pe.edu.unaj.aed2.comun.visual.DibujoArbol;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.comun.visual.PanelAnimacion;
import pe.edu.unaj.aed2.comun.visual.PanelTraza;
import pe.edu.unaj.aed2.semana05.modelo.ArbolAVL;
import pe.edu.unaj.aed2.semana05.modelo.EstadoAVL;
import pe.edu.unaj.aed2.semana05.modelo.EstadoAVL.NodoAVL;

import java.util.ArrayList;
import java.util.List;

/** Semana 05: inserción en un árbol AVL con sus cuatro casos de rotación. */
public class VisualizadorAVL extends BorderPane {

    /** Produce, en orden, los casos LL, RR, LR y RL. */
    private static final int[] EJEMPLO = {30, 20, 10, 12, 18, 15, 25};
    private static final Color EN_PILA = Color.web("#e1bee7");
    private static final Color DESEQUILIBRADO = Color.web("#ef5350");

    private final PanelAnimacion<EstadoAVL<Integer>> panel =
            new PanelAnimacion<>(VisualizadorAVL::dibujar, EstadoAVL::descripcion);
    private final PanelTraza<EstadoAVL<Integer>> traza = new PanelTraza<>(panel, ArbolAVL.CODIGO_INSERTAR);
    private final TextField clave = new TextField("5");
    private final Label estado = new Label();
    private ArbolAVL<Integer> arbol = new ArbolAVL<>();

    public VisualizadorAVL() {
        clave.setPrefColumnCount(5);
        Button insertar = new Button("Insertar");
        Button ejemplo = new Button("Ejemplo: 4 rotaciones");
        Button ordenados = new Button("Insertar 1…15 en orden");
        Button nuevo = new Button("Árbol nuevo");
        insertar.setDefaultButton(true);
        insertar.setOnAction(e -> {
            try {
                mostrar(arbol.pasosInsertar(Integer.parseInt(clave.getText().strip())));
            } catch (NumberFormatException ex) {
                estado.setText("Escribe un número entero.");
            }
        });
        ejemplo.setOnAction(e -> cargar(EJEMPLO));
        ordenados.setOnAction(e -> {
            int[] claves = new int[15];
            for (int k = 0; k < claves.length; k++) {
                claves[k] = k + 1;
            }
            cargar(claves);
        });
        nuevo.setOnAction(e -> {
            arbol = new ArbolAVL<>();
            mostrar(arbol.estadoActual("Árbol vacío: raiz = null. Escribe una clave y pulsa Insertar."));
        });

        HBox opciones = new HBox(8, new Label("Clave"), clave, insertar, ejemplo, ordenados, nuevo, estado);
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8));

        Label titulo = new Label("Semana 05 · Árbol AVL: inserción con rotaciones");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.5);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        cargar(EJEMPLO);
    }

    /** Inserta las claves en un árbol nuevo, en una sola secuencia de pasos. */
    private void cargar(int[] claves) {
        arbol = new ArbolAVL<>();
        List<EstadoAVL<Integer>> pasos = new ArrayList<>();
        for (int c : claves) {
            pasos.addAll(arbol.pasosInsertar(c));
        }
        mostrar(pasos);
    }

    private void mostrar(List<EstadoAVL<Integer>> pasos) {
        panel.setPasos(pasos);
        estado.setText("Al terminar: n = " + arbol.tamanio() + " · altura = " + arbol.altura() + " · rotaciones "
                + arbol.rotaciones());
    }

    private static void dibujar(GraphicsContext gc, EstadoAVL<Integer> paso, double ancho, double alto) {
        double arriba = 44;
        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font("System", FontWeight.NORMAL, 12));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("Junto a cada nodo: su factor de equilibrio fe = altura(izq) − altura(der)", 10, 12);
        gc.fillText("Naranja: llamada activa · Lila: en espera · Rojo: desequilibrado · Amarillo: rotan · Verde: nuevo",
                10, 28);
        gc.save();
        gc.translate(0, arriba);
        if (paso.raiz() == null) {
            gc.setFill(Color.web("#9e9e9e"));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("Árbol vacío (raiz = null)", ancho / 2, (alto - arriba) / 2);
        } else {
            DibujoArbol.dibujar(gc, paso.raiz(), NodoAVL::izquierdo, NodoAVL::derecho,
                    v -> String.valueOf(v.clave()), v -> color(paso, v),
                    v -> (Math.abs(v.factor()) > 1 ? "!" : "") + (v.factor() > 0 ? "+" : "") + v.factor(),
                    ancho, alto - arriba - 10);
        }
        gc.restore();
    }

    private static Color color(EstadoAVL<Integer> paso, NodoAVL<Integer> v) {
        if (paso.destacados().contains(v.id())) {
            return switch (paso.tipo()) {
                case DESEQUILIBRIO -> DESEQUILIBRADO;
                case ROTACION -> Paleta.RESALTADO;
                case REPETIDO -> Paleta.COMPARANDO;
                default -> Paleta.FINALIZADO;
            };
        }
        if (v.id() == paso.actual()) {
            return Paleta.COMPARANDO;
        }
        if (paso.pila().contains(v.id())) {
            return EN_PILA;
        }
        return Paleta.NORMAL;
    }
}
