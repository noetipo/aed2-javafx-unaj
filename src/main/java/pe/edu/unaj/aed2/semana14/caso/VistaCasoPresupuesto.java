package pe.edu.unaj.aed2.semana14.caso;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.semana14.caso.PresupuestoParticipativo.Seleccion;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Caso práctico semana 14: presupuesto participativo. Compara el óptimo de programación dinámica con dos reglas
 * voraces: barras con los beneficiarios de cada regla y una tabla con los proyectos que elige cada una.
 */
public class VistaCasoPresupuesto extends BorderPane {

    private final PresupuestoParticipativo presupuesto = PresupuestoParticipativo.cargar();
    private final Spinner<Integer> monto = new Spinner<>(50, 600, 250, 10);
    private final Label resultado = new Label();
    private final Canvas lienzo = new Canvas();
    private final TableView<Integer> tabla = new TableView<>();
    private List<Seleccion> selecciones = List.of();

    public VistaCasoPresupuesto() {
        Label titulo = new Label("Semana 14 · Caso: presupuesto participativo");
        titulo.getStyleClass().add("titulo-vista");
        Label nota = new Label("Cada proyecto se hace completo o no se hace. ¿Qué proyectos benefician a más personas"
                + " sin pasar el presupuesto? Proyectos, costos y beneficiarios ficticios.");
        nota.setWrapText(true);
        monto.setPrefWidth(90);
        monto.setEditable(true);
        monto.valueProperty().addListener((o, a, b) -> actualizar());
        HBox barra = new HBox(8, new Label("Presupuesto (miles de soles)"), monto);
        barra.setAlignment(Pos.CENTER_LEFT);
        resultado.getStyleClass().add("leyenda-paso");
        resultado.setWrapText(true);
        VBox cabecera = new VBox(6, titulo, nota, barra, resultado);
        cabecera.setPadding(new Insets(0, 8, 8, 8));
        setTop(cabecera);

        Pane contenedor = new Pane(lienzo);
        contenedor.setMinSize(0, 0);
        contenedor.setPrefSize(420, 400);
        lienzo.widthProperty().bind(contenedor.widthProperty());
        lienzo.heightProperty().bind(contenedor.heightProperty());
        lienzo.widthProperty().addListener(o -> dibujar());
        lienzo.heightProperty().addListener(o -> dibujar());

        tabla.getColumns().setAll(List.of(
                columna("Proyecto", k -> presupuesto.proyectos().get(k).nombre(), 250),
                columna("Costo (mil S/)", k -> String.valueOf(presupuesto.proyectos().get(k).peso()), 95),
                columna("Beneficiarios", k -> String.valueOf(presupuesto.proyectos().get(k).valor()), 95),
                columna("Óptimo (PD)", k -> marca(0, k), 85),
                columna("Voraz por sol", k -> marca(1, k), 90),
                columna("Voraz por personas", k -> marca(2, k), 115)));
        tabla.getItems().setAll(IntStream.range(0, presupuesto.proyectos().size()).boxed().toList());
        VBox derecha = new VBox(6, tabla);
        derecha.setPadding(new Insets(8));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        SplitPane division = new SplitPane(contenedor, derecha);
        division.setDividerPositions(0.36);
        setCenter(division);
        actualizar();
    }

    private static TableColumn<Integer, String> columna(String titulo, Function<Integer, String> valor, double ancho) {
        TableColumn<Integer, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(f -> new ReadOnlyObjectWrapper<>(valor.apply(f.getValue())));
        c.setSortable(false);
        c.setPrefWidth(ancho);
        return c;
    }

    private String marca(int regla, int k) {
        return selecciones.size() > regla && selecciones.get(regla).elegidos().contains(k) ? "✔" : "";
    }

    private void actualizar() {
        int m = monto.getValue();
        selecciones = List.of(presupuesto.optimo(m), presupuesto.vorazPorSol(m), presupuesto.vorazPorBeneficiarios(m));
        Seleccion o = selecciones.get(0);
        resultado.setText("Con S/ " + m + " mil, la programación dinámica beneficia a " + o.beneficiarios()
                + " personas (gasta " + o.costo() + " mil). Voraz por sol: " + selecciones.get(1).beneficiarios()
                + ". Voraz por personas: " + selecciones.get(2).beneficiarios() + ". Las reglas voraces deciden"
                + " proyecto por proyecto y no pueden deshacer una mala elección; la tabla de PD compara todas las"
                + " combinaciones sin revisarlas una por una.");
        tabla.refresh();
        dibujar();
    }

    private void dibujar() {
        var gc = lienzo.getGraphicsContext2D();
        double ancho = lienzo.getWidth();
        double alto = lienzo.getHeight();
        gc.setFill(Paleta.FONDO);
        gc.fillRect(0, 0, ancho, alto);
        if (selecciones.isEmpty()) {
            return;
        }
        int maximo = selecciones.stream().mapToInt(Seleccion::beneficiarios).max().orElse(1);
        Color[] colores = {Paleta.FINALIZADO, Paleta.NORMAL, Paleta.COMPARANDO};
        double base = alto - 60;
        double tope = 60;
        double anchoBarra = (ancho - 60) / 3 - 20;
        gc.setFont(Font.font("System", FontWeight.BOLD, 13));
        gc.setFill(Paleta.TEXTO);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("Personas beneficiadas", 10, 20);
        for (int k = 0; k < 3; k++) {
            Seleccion s = selecciones.get(k);
            double h = (base - tope) * s.beneficiarios() / maximo;
            double x = 30 + k * (anchoBarra + 20);
            gc.setFill(colores[k]);
            gc.fillRect(x, base - h, anchoBarra, h);
            gc.setFill(Paleta.TEXTO);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setFont(Font.font("System", FontWeight.BOLD, 14));
            gc.fillText(String.valueOf(s.beneficiarios()), x + anchoBarra / 2, base - h - 12);
            gc.setFont(Font.font("System", 11));
            String[] partes = s.regla().split(": |\\(");
            gc.fillText(partes[0].trim(), x + anchoBarra / 2, base + 14);
            if (partes.length > 1) {
                gc.fillText(partes[1].replace(")", "").trim(), x + anchoBarra / 2, base + 30);
            }
            gc.fillText("gasta " + s.costo() + " mil", x + anchoBarra / 2, base + 46);
        }
    }
}
