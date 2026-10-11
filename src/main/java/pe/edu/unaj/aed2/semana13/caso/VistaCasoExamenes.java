package pe.edu.unaj.aed2.semana13.caso;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pe.edu.unaj.aed2.comun.visual.DibujoArbol;
import pe.edu.unaj.aed2.comun.visual.DibujoGrafo;
import pe.edu.unaj.aed2.comun.visual.Paleta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Caso práctico semana 13: horario de exámenes finales. El grafo de conflictos se colorea con la regla voraz o con
 * backtracking; cada color es un día. La tabla muestra qué exámenes caen cada día.
 */
public class VistaCasoExamenes extends BorderPane {

    private static final Color[] DIAS = {
            Color.web("#64b5f6"), Color.web("#81c784"), Color.web("#ffb74d"), Color.web("#ba68c8"),
            Color.web("#f06292"), Color.web("#4db6ac"), Color.web("#a1887f")
    };

    private final HorarioExamenes horario = HorarioExamenes.cargar();
    private final ToggleButton voraz = new ToggleButton("Regla voraz (en orden)");
    private final ToggleButton backtracking = new ToggleButton("Backtracking");
    private final ComboBox<Integer> dias = new ComboBox<>();
    private final Label resultado = new Label();
    private final Canvas lienzo = new Canvas();
    private final TableView<Integer> tabla = new TableView<>();
    private int[] asignacion;

    public VistaCasoExamenes() {
        Label titulo = new Label("Semana 13 · Caso: horario de exámenes finales");
        titulo.getStyleClass().add("titulo-vista");
        Label nota = new Label("Una línea une dos cursos que comparten estudiantes: sus exámenes no pueden ser el mismo"
                + " día. ¿Cuántos días hacen falta como mínimo? Datos ficticios.");
        nota.setWrapText(true);
        ToggleGroup grupo = new ToggleGroup();
        voraz.setToggleGroup(grupo);
        backtracking.setToggleGroup(grupo);
        backtracking.setSelected(true);
        grupo.selectedToggleProperty().addListener((o, antes, ahora) -> {
            if (ahora == null) {
                antes.setSelected(true);
            }
            actualizar();
        });
        dias.getItems().setAll(2, 3, 4, 5);
        dias.getSelectionModel().select(Integer.valueOf(3));
        dias.setOnAction(e -> actualizar());
        HBox barra = new HBox(8, voraz, backtracking, new Label("Días disponibles (backtracking)"), dias);
        barra.setAlignment(Pos.CENTER_LEFT);
        resultado.getStyleClass().add("leyenda-paso");
        resultado.setWrapText(true);
        VBox cabecera = new VBox(6, titulo, nota, barra, resultado);
        cabecera.setPadding(new Insets(0, 8, 8, 8));
        setTop(cabecera);

        Pane contenedor = new Pane(lienzo);
        contenedor.setMinSize(0, 0);
        contenedor.setPrefSize(600, 500);
        lienzo.widthProperty().bind(contenedor.widthProperty());
        lienzo.heightProperty().bind(contenedor.heightProperty());
        lienzo.widthProperty().addListener(o -> dibujar());
        lienzo.heightProperty().addListener(o -> dibujar());

        TableColumn<Integer, String> dia = new TableColumn<>("Día");
        dia.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>("Día " + (f.getValue() + 1)));
        dia.setPrefWidth(70);
        TableColumn<Integer, String> examenes = new TableColumn<>("Exámenes");
        examenes.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(String.join(", ", delDia(f.getValue()))));
        examenes.setPrefWidth(380);
        dia.setSortable(false);
        examenes.setSortable(false);
        tabla.getColumns().setAll(List.of(dia, examenes));
        Label tituloTabla = new Label("Calendario");
        tituloTabla.getStyleClass().add("subtitulo-traza");
        VBox derecha = new VBox(6, tituloTabla, tabla);
        derecha.setPadding(new Insets(8));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        SplitPane division = new SplitPane(contenedor, derecha);
        division.setDividerPositions(0.55);
        setCenter(division);
        actualizar();
    }

    private List<String> delDia(int d) {
        List<String> l = new ArrayList<>();
        for (int v = 0; v < horario.ids().size(); v++) {
            if (asignacion != null && asignacion[v] == d) {
                l.add(horario.nombres().get(horario.ids().get(v)));
            }
        }
        return l;
    }

    private void actualizar() {
        int minimo = horario.minimoDias();
        int usados = java.util.Arrays.stream(horario.voraz()).max().orElse(-1) + 1;
        if (voraz.isSelected()) {
            asignacion = horario.voraz();
            resultado.setText("Regla voraz: cada curso, en el orden de la lista, va al primer día libre. Usa " + usados
                    + " días, pero bastan " + minimo + ". Decidir rápido sin volver atrás puede salir caro.");
        } else {
            HorarioExamenes.Resultado r = horario.backtracking(dias.getValue());
            asignacion = r.dias();
            resultado.setText(r.encontrado()
                    ? "Backtracking con " + dias.getValue() + " días: encontró un calendario después de probar "
                    + r.intentos() + " asignaciones (curso, día)." + (dias.getValue() == minimo
                    ? " Es el mínimo: con " + (minimo - 1) + " días no hay forma." : "")
                    : "Backtracking con " + dias.getValue() + " días: probó " + r.intentos() + " asignaciones y NO hay"
                    + " calendario posible. Hacen falta al menos " + minimo + " días.");
        }
        int total = asignacion == null ? 0 : java.util.Arrays.stream(asignacion).max().orElse(-1) + 1;
        tabla.getItems().setAll(IntStream.range(0, total).boxed().toList());
        dibujar();
    }

    private void dibujar() {
        var gc = lienzo.getGraphicsContext2D();
        double ancho = lienzo.getWidth();
        double alto = lienzo.getHeight();
        gc.setFill(Paleta.FONDO);
        gc.fillRect(0, 0, ancho, alto);
        List<String> ids = horario.ids();
        int n = ids.size();
        double cx = ancho / 2;
        double cy = alto / 2;
        double radio = Math.min(ancho, alto) / 2 - 60;
        double[][] p = new double[n][2];
        for (int v = 0; v < n; v++) {
            double angulo = -Math.PI / 2 + 2 * Math.PI * v / n;
            p[v][0] = cx + radio * Math.cos(angulo);
            p[v][1] = cy + radio * Math.sin(angulo);
        }
        for (int a = 0; a < n; a++) {
            for (int b = a + 1; b < n; b++) {
                if (horario.conflicto(ids.get(a), ids.get(b))) {
                    DibujoGrafo.arista(gc, p[a][0], p[a][1], p[b][0], p[b][1], null, false, Color.web("#90a4ae"), 1.5);
                }
            }
        }
        for (int v = 0; v < n; v++) {
            Color c = asignacion == null ? Color.WHITE : DIAS[asignacion[v] % DIAS.length];
            DibujoArbol.nodo(gc, p[v][0], p[v][1], "", c, 22);
            gc.setFill(Paleta.TEXTO);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.setFont(Font.font("System", FontWeight.BOLD, 11));
            gc.fillText(asignacion == null ? ids.get(v) : "D" + (asignacion[v] + 1), p[v][0], p[v][1]);
            gc.setFont(Font.font("System", 12));
            gc.setTextBaseline(VPos.TOP);
            gc.fillText(horario.nombres().get(ids.get(v)), p[v][0], p[v][1] + 25);
        }
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.setFill(Color.web("#546e7a"));
        gc.setFont(Font.font(12));
        gc.fillText("Cada color es un día (D1, D2, …). Una línea = comparten estudiantes.", 10, 14);
    }
}
