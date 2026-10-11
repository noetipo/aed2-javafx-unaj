package pe.edu.unaj.aed2.semana10.caso;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.semana07.vista.DibujoRed;
import pe.edu.unaj.aed2.semana10.caso.PlanEstudios.Prioridad;
import pe.edu.unaj.aed2.semana10.modelo.MallaCurricular;
import pe.edu.unaj.aed2.semana10.vista.DibujoMalla;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Caso práctico semana 10: plan de estudios ciclo por ciclo con un tope de cursos por ciclo. En el mapa, cada
 * curso lleva el ciclo en que se lleva ("C3") y los cursos sin holgura se resaltan en rojo; la tabla lista los ciclos.
 */
public class VistaCasoPlan extends BorderPane {

    private static final Color CRITICO = Color.web("#c62828");

    private final PlanEstudios plan = new PlanEstudios(MallaCurricular.cargar());
    private final Map<String, String> nombres = plan.malla().nombres();
    private final Spinner<Integer> maximo = new Spinner<>(1, 6, 3);
    private final ComboBox<Prioridad> prioridad = new ComboBox<>();
    private final Label resultado = new Label();
    private final Canvas lienzo = new Canvas();
    private final TableView<Integer> tabla = new TableView<>();
    private List<List<String>> ciclos = List.of();

    public VistaCasoPlan() {
        Label titulo = new Label("Semana 10 · Caso: plan de estudios ciclo por ciclo");
        titulo.getStyleClass().add("titulo-vista");
        Label nota = new Label("Cada ciclo puedes llevar solo los cursos cuyos prerrequisitos ya aprobaste, y hasta un"
                + " máximo de cursos. ¿En cuántos ciclos terminas? ¿Importa cuáles eliges primero? Malla ficticia.");
        nota.setWrapText(true);
        prioridad.getItems().setAll(Prioridad.values());
        prioridad.getSelectionModel().select(Prioridad.ALFABETICO);
        prioridad.setOnAction(e -> actualizar());
        maximo.valueProperty().addListener((o, a, b) -> actualizar());
        maximo.setPrefWidth(70);
        HBox barra = new HBox(8, new Label("Máximo de cursos por ciclo"), maximo, new Label("Si no entran todos,"
                + " elegir"), prioridad);
        barra.setAlignment(Pos.CENTER_LEFT);
        resultado.getStyleClass().add("leyenda-paso");
        resultado.setWrapText(true);
        VBox cabecera = new VBox(6, titulo, nota, barra, resultado);
        cabecera.setPadding(new Insets(0, 8, 8, 8));
        setTop(cabecera);

        Pane contenedor = new Pane(lienzo);
        contenedor.setMinSize(0, 0);
        contenedor.setPrefSize(650, 500);
        lienzo.widthProperty().bind(contenedor.widthProperty());
        lienzo.heightProperty().bind(contenedor.heightProperty());
        lienzo.widthProperty().addListener(o -> dibujar());
        lienzo.heightProperty().addListener(o -> dibujar());

        TableColumn<Integer, String> ciclo = new TableColumn<>("Ciclo");
        ciclo.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>("C" + (f.getValue() + 1)));
        ciclo.setPrefWidth(60);
        TableColumn<Integer, String> cursos = new TableColumn<>("Cursos que se llevan");
        cursos.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(String.join(", ",
                ciclos.get(f.getValue()).stream().map(nombres::get).toList())));
        cursos.setPrefWidth(380);
        ciclo.setSortable(false);
        cursos.setSortable(false);
        tabla.getColumns().setAll(List.of(ciclo, cursos));
        Label tituloTabla = new Label("Plan resultante");
        tituloTabla.getStyleClass().add("subtitulo-traza");
        VBox derecha = new VBox(6, tituloTabla, tabla);
        derecha.setPadding(new Insets(8));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        SplitPane division = new SplitPane(contenedor, derecha);
        division.setDividerPositions(0.62);
        setCenter(division);
        actualizar();
    }

    private void actualizar() {
        int k = maximo.getValue();
        ciclos = plan.plan(k, prioridad.getValue());
        tabla.getItems().setAll(java.util.stream.IntStream.range(0, ciclos.size()).boxed().toList());
        int alfabetico = plan.plan(k, Prioridad.ALFABETICO).size();
        int cadena = plan.plan(k, Prioridad.CADENA_MAS_LARGA).size();
        int cursos = plan.malla().cursos().size();
        int cota = Math.max(plan.minimoCiclos(), (cursos + k - 1) / k);
        resultado.setText("Con " + k + (k == 1 ? " curso" : " cursos") + " por ciclo: por orden alfabético, "
                + alfabetico + " ciclos; primero la cadena más larga, " + cadena + " ciclos. Ningún plan baja de "
                + cota + " (camino crítico de " + plan.minimoCiclos() + " cursos"
                + (cota > plan.minimoCiclos() ? ", y " + cursos + " cursos ÷ " + k + " por ciclo" : "") + ")."
                + (alfabetico > cadena ? " Elegir mal retrasa la cadena crítica y cuesta "
                + (alfabetico - cadena) + (alfabetico - cadena == 1 ? " ciclo." : " ciclos.") : ""));
        dibujar();
    }

    private void dibujar() {
        Map<String, Integer> cicloDe = new HashMap<>();
        for (int i = 0; i < ciclos.size(); i++) {
            for (String v : ciclos.get(i)) {
                cicloDe.put(v, i);
            }
        }
        int total = ciclos.size();
        var gc = lienzo.getGraphicsContext2D();
        gc.setFill(Paleta.FONDO);
        gc.fillRect(0, 0, lienzo.getWidth(), lienzo.getHeight());
        DibujoMalla.dibujar(gc, plan.malla(), plan.malla().grafo(),
                v -> Color.web("#bbdefb").interpolate(Color.web("#ffb74d"),
                        total <= 1 ? 0 : (double) cicloDe.get(v) / (total - 1)),
                v -> plan.holgura(v) == 0 ? CRITICO : null,
                (u, v) -> plan.esArcoCritico(u, v) ? new DibujoRed.Trazo(CRITICO, 3.5)
                        : new DibujoRed.Trazo(Color.web("#90a4ae"), 1.5),
                v -> "C" + (cicloDe.get(v) + 1),
                lienzo.getWidth(), lienzo.getHeight() - 22);
        gc.setFill(Color.web("#546e7a"));
        gc.setFont(javafx.scene.text.Font.font(12));
        gc.setTextAlign(javafx.scene.text.TextAlignment.LEFT);
        gc.fillText("Rojo: cursos sin holgura (caminos críticos: atrasarlos atrasa el egreso) · Cn: ciclo en que se lleva",
                10, lienzo.getHeight() - 8);
    }
}
