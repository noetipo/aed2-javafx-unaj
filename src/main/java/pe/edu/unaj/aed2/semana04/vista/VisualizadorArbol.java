package pe.edu.unaj.aed2.semana04.vista;

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
import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.visual.ControlesPaso;
import pe.edu.unaj.aed2.comun.visual.DibujoArbol;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.comun.visual.PanelAnimacion;
import pe.edu.unaj.aed2.comun.visual.PanelTraza;
import pe.edu.unaj.aed2.semana04.modelo.ArbolBusqueda;
import pe.edu.unaj.aed2.semana04.modelo.EstadoArbol;
import pe.edu.unaj.aed2.semana04.modelo.EstadoArbol.NodoVista;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/** Semana 04: árbol binario de búsqueda; insertar, buscar, eliminar e inorden paso a paso. */
public class VisualizadorArbol extends BorderPane {

    private static final int[] EJEMPLO = {50, 30, 70, 20, 40, 60, 80, 35, 45, 65};
    private static final Color EN_PILA = Color.web("#e1bee7");
    private static final Color ELIMINAR = Color.web("#ef9a9a");

    private final PanelAnimacion<EstadoArbol<Integer>> panel =
            new PanelAnimacion<>(VisualizadorArbol::dibujar, EstadoArbol::descripcion);
    private final PanelTraza<EstadoArbol<Integer>> traza =
            new PanelTraza<>(panel, ArbolBusqueda.CODIGO_INSERTAR);
    private final TextField clave = new TextField("55");
    private final Label estado = new Label();
    private ArbolBusqueda<Integer> arbol = new ArbolBusqueda<>();

    public VisualizadorArbol() {
        clave.setPrefColumnCount(5);
        Button insertar = new Button("Insertar");
        Button buscar = new Button("Buscar");
        Button eliminar = new Button("Eliminar");
        Button inorden = new Button("Inorden");
        Button ejemplo = new Button("Cargar ejemplo");
        Button nuevo = new Button("Árbol nuevo");
        insertar.setDefaultButton(true);
        insertar.setOnAction(e -> conClave(x -> arbol.pasosInsertar(x), ArbolBusqueda.CODIGO_INSERTAR));
        buscar.setOnAction(e -> conClave(x -> arbol.pasosBuscar(x), ArbolBusqueda.CODIGO_BUSCAR));
        eliminar.setOnAction(e -> conClave(x -> arbol.pasosEliminar(x), ArbolBusqueda.CODIGO_ELIMINAR));
        inorden.setOnAction(e -> mostrar(arbol.pasosInorden(), ArbolBusqueda.CODIGO_INORDEN));
        ejemplo.setOnAction(e -> cargarEjemplo());
        nuevo.setOnAction(e -> {
            arbol = new ArbolBusqueda<>();
            mostrar(arbol.estadoActual("Árbol vacío: raiz = null. Escribe una clave y pulsa Insertar."),
                    ArbolBusqueda.CODIGO_INSERTAR);
        });

        HBox opciones = new HBox(8, new Label("Clave"), clave, insertar, buscar, eliminar, inorden, ejemplo,
                nuevo, estado);
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8));

        Label titulo = new Label("Semana 04 · Árbol binario de búsqueda");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.5);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        cargarEjemplo();
    }

    private void conClave(Function<Integer, List<EstadoArbol<Integer>>> operacion, CodigoAlgoritmo codigo) {
        try {
            int x = Integer.parseInt(clave.getText().strip());
            mostrar(operacion.apply(x), codigo);
        } catch (NumberFormatException e) {
            estado.setText("Escribe un número entero.");
        }
    }

    /** Inserta las claves de ejemplo en una sola secuencia de pasos. */
    private void cargarEjemplo() {
        arbol = new ArbolBusqueda<>();
        List<EstadoArbol<Integer>> pasos = new ArrayList<>();
        for (int c : EJEMPLO) {
            pasos.addAll(arbol.pasosInsertar(c));
        }
        mostrar(pasos, ArbolBusqueda.CODIGO_INSERTAR);
    }

    private void mostrar(List<EstadoArbol<Integer>> pasos, CodigoAlgoritmo codigo) {
        traza.setCodigo(codigo);
        panel.setPasos(pasos);
        estado.setText("Al terminar: n = " + arbol.tamanio() + " · altura = " + arbol.altura());
    }

    private static void dibujar(GraphicsContext gc, EstadoArbol<Integer> paso, double ancho, double alto) {
        double arriba = 44;
        double abajo = paso.salida().isEmpty() ? 10 : 40;
        gc.save();
        gc.translate(0, arriba);
        if (paso.raiz() == null) {
            gc.setFill(Color.web("#9e9e9e"));
            gc.setFont(Font.font(14));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText("Árbol vacío (raiz = null)", ancho / 2, (alto - arriba) / 2);
        } else {
            DibujoArbol.dibujar(gc, paso.raiz(), NodoVista::izquierdo, NodoVista::derecho,
                    v -> String.valueOf(v.clave()), v -> color(paso, v), ancho, alto - arriba - abajo);
        }
        gc.restore();

        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font("System", FontWeight.BOLD, 13));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFont(Font.font("System", FontWeight.NORMAL, 12));
        gc.fillText("Naranja: nodo actual · Lila: llamadas en espera (o camino de la búsqueda)", 10, 12);
        gc.fillText("Rojo: nuevo o a eliminar · Amarillo: sucesor · Verde: encontrado o emitido", 10, 28);
        gc.setFont(Font.font("System", FontWeight.BOLD, 13));
        if (!paso.salida().isEmpty()) {
            gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 15));
            gc.fillText("Salida inorden: " + paso.salida(), 10, alto - 18);
        }
    }

    private static Color color(EstadoArbol<Integer> paso, NodoVista<Integer> v) {
        if (v.id() == paso.destacado()) {
            return switch (paso.tipo()) {
                case ENCONTRADO, EMITE, FIN, REEMPLAZO -> Paleta.FINALIZADO;
                case SUCESOR, COMPARACION -> Paleta.RESALTADO;
                case A_ELIMINAR, RETORNO -> ELIMINAR;
                default -> Paleta.INTERCAMBIO;
            };
        }
        if (v.id() == paso.actual()) {
            return Paleta.COMPARANDO;
        }
        if (paso.salida().contains(v.clave())) {
            return Paleta.FINALIZADO;
        }
        if (paso.pila().contains(v.id())) {
            return EN_PILA;
        }
        return Paleta.NORMAL;
    }
}
