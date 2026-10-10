package pe.edu.unaj.aed2;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pe.edu.unaj.aed2.comun.visual.VistaTeoria;
import pe.edu.unaj.aed2.semana01.caso.VistaCasoMercado;
import pe.edu.unaj.aed2.semana01.vista.VisualizadorOrdenamiento;
import pe.edu.unaj.aed2.semana02.caso.VistaCasoEscalinata;
import pe.edu.unaj.aed2.semana02.vista.VisualizadorQuickSort;

import java.util.function.Supplier;

/**
 * Ventana principal: menú lateral con las semanas agrupadas por unidad.
 * <p>
 * Para registrar una semana nueva, agrega una entrada en {@link #unidadI()}
 * o {@link #unidadII()} y exporta sus paquetes en module-info.java.
 */
public class App extends Application {

    /** Elemento del menú. {@code vista} es null para los nodos que solo agrupan. */
    private record Entrada(String titulo, Supplier<Node> vista) {
        static Entrada grupo(String titulo) {
            return new Entrada(titulo, null);
        }

        @Override
        public String toString() {
            return titulo;
        }
    }

    private final BorderPane raiz = new BorderPane();

    @Override
    public void start(Stage stage) {
        TreeItem<Entrada> menu = new TreeItem<>(Entrada.grupo("AED II"));
        menu.getChildren().addAll(unidadI(), unidadII());

        TreeView<Entrada> arbol = new TreeView<>(menu);
        arbol.setShowRoot(false);
        arbol.getStyleClass().add("menu-lateral");
        arbol.getSelectionModel().selectedItemProperty().addListener((o, anterior, item) -> {
            if (item != null && item.getValue().vista() != null) {
                raiz.setCenter(item.getValue().vista().get());
            }
        });

        Label encabezado = new Label("Algoritmos y Estructuras de Datos II · UNAJ");
        HBox barra = new HBox(encabezado);
        barra.getStyleClass().add("encabezado");

        raiz.setTop(barra);
        raiz.setLeft(arbol);
        raiz.setCenter(bienvenida());

        Scene escena = new Scene(raiz, 1200, 760);
        escena.getStylesheets().add(App.class.getResource("estilos.css").toExternalForm());
        stage.setTitle("AED II - UNAJ");
        stage.setScene(escena);
        stage.show();
    }

    private TreeItem<Entrada> unidadI() {
        return unidad("Unidad I",
                semana("Semana 01 · Complejidad y ordenamiento",
                        teoria("semana-01"),
                        new Entrada("Visualizador", VisualizadorOrdenamiento::new),
                        new Entrada("Caso: precios de mercado", VistaCasoMercado::new)),
                semana("Semana 02 · Recursividad y divide y vencerás",
                        teoria("semana-02"),
                        new Entrada("Visualizador: quicksort", VisualizadorQuickSort::new),
                        new Entrada("Caso: escalinata de Huaynaroque", VistaCasoEscalinata::new)));
    }

    private TreeItem<Entrada> unidadII() {
        return unidad("Unidad II");
    }

    @SafeVarargs
    private static TreeItem<Entrada> unidad(String titulo, TreeItem<Entrada>... semanas) {
        TreeItem<Entrada> item = new TreeItem<>(Entrada.grupo(titulo));
        item.getChildren().addAll(semanas);
        if (semanas.length == 0) {
            item.getChildren().add(new TreeItem<>(Entrada.grupo("(próximamente)")));
        }
        item.setExpanded(true);
        return item;
    }

    private static TreeItem<Entrada> semana(String titulo, Entrada... vistas) {
        TreeItem<Entrada> item = new TreeItem<>(Entrada.grupo(titulo));
        for (Entrada vista : vistas) {
            item.getChildren().add(new TreeItem<>(vista));
        }
        item.setExpanded(true);
        return item;
    }

    /** Entrada de menú que muestra docs/semana-XX/README.md. */
    private static Entrada teoria(String carpeta) {
        return new Entrada("Teoría", () -> new VistaTeoria(carpeta + "/README.md"));
    }

    private static Node bienvenida() {
        Label titulo = new Label("Bienvenido(a)");
        titulo.getStyleClass().add("titulo-vista");
        Label texto = new Label("""
                Elige una semana en el menú lateral.

                Cada semana tiene:
                  • Teoría: los conceptos de la semana.
                  • Visualizador: animación paso a paso, con el pseudocódigo,
                    las variables y la traza (prueba de escritorio).
                  • Caso práctico: aplicación a un problema de Juliaca / Puno.
                  • Tarea: plantilla con TODOs en el paquete semanaXX.tarea.""");
        VBox caja = new VBox(titulo, texto);
        caja.getStyleClass().add("bienvenida");
        return caja;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
