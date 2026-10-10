package pe.edu.unaj.aed2.comun.visual;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.traza.PasoTrazable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

/**
 * Pestañas que acompañan a un {@link PanelAnimacion} para seguir la ejecución:
 * <ul>
 *   <li><b>Pseudocódigo</b>: resalta la línea que se ejecuta y muestra las variables actuales.</li>
 *   <li><b>Java</b>: el mismo algoritmo en Java, con las líneas equivalentes resaltadas. Al lado de
 *       cada línea se muestra el valor actual de las variables que usa, y en la línea que se ejecuta,
 *       qué se evalúa y su resultado (como el depurador de un IDE).</li>
 *   <li><b>Traza</b>: prueba de escritorio, una fila por paso; al hacer clic en una fila
 *       la animación salta a ese paso.</li>
 * </ul>
 */
public class PanelTraza<T extends PasoTrazable> extends TabPane {

    private final PanelAnimacion<T> panel;
    private final VBox lineasPseudo = new VBox(2);
    private final VBox lineasJava = new VBox(0);
    private final GridPane variablesPseudo = new GridPane();
    private final GridPane variablesJava = new GridPane();
    private final ScrollPane desplazableJava;
    /** Una caja "Se evalúa" por pestaña de código: {explicación, expresión}. */
    private final List<Label[]> cajasEvaluacion = new ArrayList<>();
    private final List<Label> anotacionesJava = new ArrayList<>();
    private final TableView<Integer> tabla = new TableView<>();
    private static final int COLUMNA_MAXIMA = 50;
    private CodigoAlgoritmo codigo;
    private boolean sincronizando;

    public PanelTraza(PanelAnimacion<T> panel, CodigoAlgoritmo codigo) {
        this.panel = panel;
        getStyleClass().add("panel-traza");
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        setMinWidth(300);

        lineasPseudo.getStyleClass().add("pseudocodigo");
        lineasJava.getStyleClass().addAll("pseudocodigo", "codigo-java");
        ScrollPane desplazablePseudo = pestaniaCodigo(lineasPseudo, crearCajaEvaluacion(), variablesPseudo);
        desplazableJava = pestaniaCodigo(lineasJava, crearCajaEvaluacion(), variablesJava);
        // El botón Copiar queda fijo arriba: el código se desplaza solo hacia la línea activa.
        HBox barraCopiar = crearBarraCopiar();
        barraCopiar.setPadding(new Insets(6, 10, 6, 10));
        VBox pestaniaJava = new VBox(barraCopiar, desplazableJava);
        VBox.setVgrow(desplazableJava, Priority.ALWAYS);

        tabla.setPlaceholder(new Label("Sin pasos"));
        tabla.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        tabla.getSelectionModel().selectedIndexProperty().addListener((o, a, fila) -> {
            if (!sincronizando && fila.intValue() >= 0) {
                panel.irA(fila.intValue());
            }
        });
        VBox contenidoTraza = new VBox(tabla);
        VBox.setVgrow(tabla, Priority.ALWAYS);

        getTabs().addAll(new Tab("Pseudocódigo", desplazablePseudo),
                new Tab("Java", pestaniaJava),
                new Tab("Traza", contenidoTraza));

        setCodigo(codigo);
        panel.pasosProperty().addListener((o, a, b) -> reconstruirTabla());
        panel.indiceProperty().addListener((o, a, b) -> actualizar());
        reconstruirTabla();
    }

    /** Botón que copia la clase Java completa al portapapeles, para pegarla en un proyecto. */
    private HBox crearBarraCopiar() {
        Button copiar = new Button("Copiar código");
        Label aviso = new Label();
        aviso.getStyleClass().add("aviso-copiado");
        PauseTransition borrar = new PauseTransition(Duration.seconds(3));
        borrar.setOnFinished(e -> aviso.setText(""));
        copiar.setOnAction(e -> {
            ClipboardContent contenido = new ClipboardContent();
            contenido.putString(String.join("\n", codigo.java()) + "\n");
            Clipboard.getSystemClipboard().setContent(contenido);
            aviso.setText("Copiado. Pégalo en un archivo " + nombreArchivo() + " y ejecútalo.");
            borrar.playFromStart();
        });
        HBox barra = new HBox(10, copiar, aviso);
        barra.setAlignment(Pos.CENTER_LEFT);
        return barra;
    }

    /** "Burbuja.java" a partir de la línea "public class Burbuja". */
    private String nombreArchivo() {
        for (String linea : codigo.java()) {
            String limpia = linea.strip();
            if (limpia.startsWith("public class ")) {
                return limpia.substring("public class ".length()).split("[\\s{]")[0] + ".java";
            }
        }
        return ".java";
    }

    /** Recuadro con la explicación en lenguaje natural y, debajo, la expresión en código. */
    private VBox crearCajaEvaluacion() {
        Label titulo = new Label("Se evalúa");
        titulo.getStyleClass().add("subtitulo-traza");
        Label explicacion = new Label();
        explicacion.getStyleClass().add("evaluacion-explicacion");
        explicacion.setWrapText(true);
        explicacion.setMinHeight(Region.USE_PREF_SIZE);
        Label expresion = new Label();
        expresion.getStyleClass().add("evaluacion-texto");
        expresion.setWrapText(true);
        expresion.setMinHeight(Region.USE_PREF_SIZE);
        cajasEvaluacion.add(new Label[]{explicacion, expresion});
        VBox caja = new VBox(6, titulo, explicacion, expresion);
        caja.getStyleClass().add("evaluacion-caja");
        return caja;
    }

    private static ScrollPane pestaniaCodigo(VBox lineas, Node extra, GridPane variables) {
        Label titulo = new Label("Variables");
        titulo.getStyleClass().add("subtitulo-traza");
        variables.setHgap(12);
        variables.setVgap(4);
        VBox contenido = new VBox(10, lineas);
        if (extra != null) {
            contenido.getChildren().add(extra);
        }
        contenido.getChildren().addAll(titulo, variables);
        contenido.setPadding(new Insets(10));
        ScrollPane desplazable = new ScrollPane(contenido);
        desplazable.setFitToWidth(true);
        if (extra instanceof Region recuadro) {
            // El código puede ser más ancho que la vista (barra horizontal), pero la explicación
            // debe ajustarse al ancho visible para leerse sin desplazarse.
            recuadro.maxWidthProperty().bind(Bindings.createDoubleBinding(
                    () -> Math.max(200, desplazable.getViewportBounds().getWidth() - 20),
                    desplazable.viewportBoundsProperty()));
        }
        return desplazable;
    }

    /** Cambia el código mostrado (p. ej. al elegir otro algoritmo). */
    public void setCodigo(CodigoAlgoritmo nuevo) {
        codigo = nuevo;
        llenar(lineasPseudo, nuevo.pseudocodigo());
        llenarJava(nuevo.java());
        actualizar();
    }

    /** Cada línea Java es una fila: el código y, a su derecha, la anotación con valores. */
    private void llenarJava(List<String> lineas) {
        lineasJava.getChildren().clear();
        anotacionesJava.clear();
        // Se rellena con espacios (fuente monoespaciada) para alinear las anotaciones en una columna.
        // La columna se mide solo con las líneas del algoritmo (las enlazadas al pseudocódigo), no con
        // el main ni los comentarios, y tiene un tope para que los valores queden a la vista.
        int ancho = 0;
        for (int k = 0; k < lineas.size(); k++) {
            if (codigo.estaEnlazada(k + 1)) {
                ancho = Math.max(ancho, lineas.get(k).length());
            }
        }
        ancho = Math.min(COLUMNA_MAXIMA, ancho);
        String formato = "%" + String.valueOf(lineas.size()).length() + "d  %-" + Math.max(1, ancho) + "s";
        for (int k = 0; k < lineas.size(); k++) {
            Label codigo = new Label(String.format(formato, k + 1, lineas.get(k)));
            codigo.getStyleClass().add("linea-codigo");
            codigo.setMinWidth(Region.USE_PREF_SIZE);
            Label anotacion = new Label();
            anotacion.getStyleClass().add("anotacion-valor");
            anotacion.setMinWidth(Region.USE_PREF_SIZE);
            HBox fila = new HBox(6, codigo, anotacion);
            fila.setAlignment(Pos.CENTER_LEFT);
            fila.getStyleClass().add("fila-codigo");
            fila.setMinWidth(Region.USE_PREF_SIZE);
            lineasJava.getChildren().add(fila);
            anotacionesJava.add(anotacion);
        }
    }

    private static void llenar(VBox destino, List<String> lineas) {
        destino.getChildren().clear();
        String formato = "%" + String.valueOf(lineas.size()).length() + "d  %s";
        for (int k = 0; k < lineas.size(); k++) {
            Label linea = new Label(String.format(formato, k + 1, lineas.get(k)));
            linea.getStyleClass().add("linea-codigo");
            linea.setMaxWidth(Double.MAX_VALUE);
            linea.setMinWidth(Region.USE_PREF_SIZE); // líneas largas: barra horizontal, sin "..."
            destino.getChildren().add(linea);
        }
    }

    private void reconstruirTabla() {
        List<T> pasos = panel.pasos();
        Set<String> nombres = new LinkedHashSet<>();
        pasos.forEach(p -> nombres.addAll(p.variables().keySet()));

        List<TableColumn<Integer, String>> columnas = new ArrayList<>();
        columnas.add(columna("#", k -> String.valueOf(k + 1), 40));
        columnas.add(columna("Línea", k -> {
            int l = pasos.get(k).linea();
            return l == 0 ? "" : String.valueOf(l);
        }, 50));
        int largoEvaluacion = pasos.stream().mapToInt(p -> p.evaluacion().length()).max().orElse(0);
        if (largoEvaluacion > 0) {
            // Ancho según la evaluación más larga, para que el resultado (verdadero/falso) no se corte.
            columnas.add(columna("Evaluación", k -> pasos.get(k).evaluacion(),
                    Math.min(900, largoEvaluacion * 7.6 + 24)));
        }
        for (String nombre : nombres) {
            // Ancho según el encabezado, para que nombres como "huboIntercambio" no se corten.
            columnas.add(columna(nombre, k -> pasos.get(k).variables().getOrDefault(nombre, ""),
                    Math.max(60, nombre.length() * 9 + 24)));
        }
        columnas.add(columna("Acción", k -> panel.describir(pasos.get(k)), 320));

        sincronizando = true;
        tabla.getColumns().setAll(columnas);
        tabla.getItems().setAll(IntStream.range(0, pasos.size()).boxed().toList());
        sincronizando = false;
        actualizar();
    }

    private TableColumn<Integer, String> columna(String titulo,
                                                 IntFunction<String> valor, double ancho) {
        TableColumn<Integer, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(valor.apply(f.getValue())));
        c.setSortable(false);
        c.setPrefWidth(ancho);
        return c;
    }

    private void actualizar() {
        if (codigo == null) {
            return;
        }
        T paso = panel.pasoActual();
        int lineaActual = paso == null ? 0 : paso.linea();
        resaltar(lineasPseudo, Set.of(lineaActual));
        Set<Integer> enJava = codigo.lineasJava(lineaActual);
        resaltar(lineasJava, enJava);
        int principal = codigo.lineaPrincipal(lineaActual);
        if (principal > 0) {
            asegurarVisible(principal);
        }

        Map<String, String> valores = paso == null ? Map.of() : paso.variables();
        mostrarVariables(variablesPseudo, valores);
        mostrarVariables(variablesJava, valores);

        String evaluado = paso == null ? "" : paso.evaluacion();
        String explicado = paso == null ? "" : paso.explicacion();
        for (Label[] caja : cajasEvaluacion) {
            caja[0].setText(explicado.isBlank() ? "—" : explicado);
            caja[1].setText(evaluado.isBlank() ? "" : "En código:  " + evaluado);
            caja[1].setVisible(!evaluado.isBlank());
            caja[1].setManaged(!evaluado.isBlank());
        }
        List<String> java = codigo.java();
        for (int k = 0; k < anotacionesJava.size() && k < java.size(); k++) {
            Label anotacion = anotacionesJava.get(k);
            anotacion.getStyleClass().remove("anotacion-evaluacion");
            if (principal == k + 1 && !evaluado.isBlank()) {
                anotacion.setText("◀ " + evaluado);
                anotacion.getStyleClass().add("anotacion-evaluacion");
            } else {
                anotacion.setText(CodigoAlgoritmo.valoresEnLinea(java.get(k), valores));
            }
        }

        int indice = panel.indiceProperty().get();
        if (indice < tabla.getItems().size()) {
            sincronizando = true;
            tabla.getSelectionModel().clearAndSelect(indice);
            tabla.scrollTo(Math.max(0, indice - 3));
            sincronizando = false;
        }
    }

    private static void resaltar(VBox lineas, Set<Integer> numeros) {
        for (int k = 0; k < lineas.getChildren().size(); k++) {
            Node nodo = lineas.getChildren().get(k);
            nodo.getStyleClass().remove("linea-actual");
            if (numeros.contains(k + 1)) {
                nodo.getStyleClass().add("linea-actual");
            }
        }
    }

    /** Desplaza la pestaña Java para que la línea resaltada quede a la vista. */
    private void asegurarVisible(int numeroLinea) {
        int total = lineasJava.getChildren().size();
        if (total > 1) {
            desplazableJava.setVvalue(Math.max(0, Math.min(1, (numeroLinea - 4) / (double) (total - 1))));
        }
    }

    private static void mostrarVariables(GridPane destino, Map<String, String> valores) {
        destino.getChildren().clear();
        int fila = 0;
        for (Map.Entry<String, String> e : valores.entrySet()) {
            Label nombre = new Label(e.getKey());
            nombre.getStyleClass().add("nombre-variable");
            Label valor = new Label(e.getValue());
            valor.getStyleClass().add("valor-variable");
            destino.addRow(fila++, nombre, valor);
        }
    }
}
