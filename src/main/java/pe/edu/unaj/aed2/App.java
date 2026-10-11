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
import pe.edu.unaj.aed2.semana03.caso.VistaCasoMototaxis;
import pe.edu.unaj.aed2.semana03.vista.VisualizadorTablaHash;
import pe.edu.unaj.aed2.semana04.caso.VistaCasoDiccionario;
import pe.edu.unaj.aed2.semana04.vista.VisualizadorArbol;
import pe.edu.unaj.aed2.semana05.caso.VistaCasoAVL;
import pe.edu.unaj.aed2.semana05.vista.VisualizadorAVL;
import pe.edu.unaj.aed2.semana06.caso.VistaCasoTriaje;
import pe.edu.unaj.aed2.semana06.vista.VisualizadorMonticulo;
import pe.edu.unaj.aed2.semana07.caso.VistaCasoBloqueo;
import pe.edu.unaj.aed2.semana07.vista.VisualizadorRecorridos;
import pe.edu.unaj.aed2.semana08.caso.VistaCasoRutas;
import pe.edu.unaj.aed2.semana08.vista.VisualizadorDijkstra;
import pe.edu.unaj.aed2.semana09.caso.VistaCasoFibra;
import pe.edu.unaj.aed2.semana09.vista.VisualizadorArbolExpansion;
import pe.edu.unaj.aed2.semana10.caso.VistaCasoPlan;
import pe.edu.unaj.aed2.semana10.vista.VisualizadorOrdenTopologico;
import pe.edu.unaj.aed2.semana11.caso.VistaCasoAlmacen;
import pe.edu.unaj.aed2.semana11.vista.VisualizadorFloyd;
import pe.edu.unaj.aed2.semana12.caso.VistaCasoLeche;
import pe.edu.unaj.aed2.semana12.vista.VisualizadorFlujo;
import pe.edu.unaj.aed2.semana13.caso.VistaCasoExamenes;
import pe.edu.unaj.aed2.semana13.vista.VisualizadorReinas;
import pe.edu.unaj.aed2.semana14.caso.VistaCasoPresupuesto;
import pe.edu.unaj.aed2.semana14.vista.VisualizadorMochila;

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
        VistaTeoria.setAbridorEnlaces(getHostServices()::showDocument);
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
                        new Entrada("Caso: escalinata de Huaynaroque", VistaCasoEscalinata::new)),
                semana("Semana 03 · Búsqueda y tablas hash",
                        teoria("semana-03"),
                        new Entrada("Visualizador: tabla hash", VisualizadorTablaHash::new),
                        new Entrada("Caso: padrón de mototaxis", VistaCasoMototaxis::new)),
                semana("Semana 04 · Árboles binarios de búsqueda",
                        teoria("semana-04"),
                        new Entrada("Visualizador: ABB", VisualizadorArbol::new),
                        new Entrada("Caso: diccionario quechua", VistaCasoDiccionario::new)),
                semana("Semana 05 · Árboles AVL",
                        teoria("semana-05"),
                        new Entrada("Visualizador: AVL", VisualizadorAVL::new),
                        new Entrada("Caso: diccionario con AVL", VistaCasoAVL::new)),
                semana("Semana 06 · Montículos y colas de prioridad",
                        teoria("semana-06"),
                        new Entrada("Visualizador: montículo y heapsort", VisualizadorMonticulo::new),
                        new Entrada("Caso: emergencias con triaje", VistaCasoTriaje::new)));
    }

    private TreeItem<Entrada> unidadII() {
        return unidad("Unidad II",
                semana("Semana 07 · Grafos: BFS y DFS",
                        teoria("semana-07"),
                        new Entrada("Visualizador: recorridos", VisualizadorRecorridos::new),
                        new Entrada("Caso: bloqueo de carreteras", VistaCasoBloqueo::new)),
                semana("Semana 08 · Caminos más cortos: Dijkstra",
                        teoria("semana-08"),
                        new Entrada("Visualizador: Dijkstra", VisualizadorDijkstra::new),
                        new Entrada("Caso: rutas de reparto", VistaCasoRutas::new)),
                semana("Semana 09 · Árbol de expansión mínima",
                        teoria("semana-09"),
                        new Entrada("Visualizador: Kruskal y Prim", VisualizadorArbolExpansion::new),
                        new Entrada("Caso: red de fibra óptica", VistaCasoFibra::new)),
                semana("Semana 10 · Orden topológico",
                        teoria("semana-10"),
                        new Entrada("Visualizador: Kahn y DFS", VisualizadorOrdenTopologico::new),
                        new Entrada("Caso: plan de estudios", VistaCasoPlan::new)),
                semana("Semana 11 · Todos los pares: Floyd–Warshall",
                        teoria("semana-11"),
                        new Entrada("Visualizador: Floyd–Warshall", VisualizadorFloyd::new),
                        new Entrada("Caso: almacén para heladas", VistaCasoAlmacen::new)),
                semana("Semana 12 · Flujo máximo",
                        teoria("semana-12"),
                        new Entrada("Visualizador: Edmonds–Karp", VisualizadorFlujo::new),
                        new Entrada("Caso: leche a la planta", VistaCasoLeche::new)),
                semana("Semana 13 · Backtracking",
                        teoria("semana-13"),
                        new Entrada("Visualizador: N reinas", VisualizadorReinas::new),
                        new Entrada("Caso: horario de exámenes", VistaCasoExamenes::new)),
                semana("Semana 14 · Programación dinámica",
                        teoria("semana-14"),
                        new Entrada("Visualizador: mochila 0/1", VisualizadorMochila::new),
                        new Entrada("Caso: presupuesto participativo", VistaCasoPresupuesto::new)));
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
