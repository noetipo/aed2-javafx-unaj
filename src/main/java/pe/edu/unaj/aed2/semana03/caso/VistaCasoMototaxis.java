package pe.edu.unaj.aed2.semana03.caso;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import pe.edu.unaj.aed2.comun.benchmark.GraficoComplejidad;
import pe.edu.unaj.aed2.semana03.caso.PadronMototaxis.Mototaxi;

import java.util.Arrays;
import java.util.Locale;

/**
 * Caso práctico semana 03: buscar una mototaxi por su código en un padrón de Juliaca,
 * comparando cuántas comparaciones hace la tabla hash frente a la búsqueda lineal.
 */
public class VistaCasoMototaxis extends BorderPane {

    private final PadronMototaxis padron = PadronMototaxis.desdeRecurso();
    private final TextField codigo = new TextField();
    private final Label resultado = new Label();

    public VistaCasoMototaxis() {
        Label titulo = new Label("Semana 03 · Caso: padrón de mototaxis de Juliaca");
        titulo.getStyleClass().add("titulo-vista");
        Label nota = new Label("Datos ficticios con fines didácticos. Prueba con "
                + padron.mototaxis().get(0).codigo() + ", " + padron.mototaxis().get(57).codigo() + ", "
                + padron.mototaxis().get(padron.mototaxis().size() - 1).codigo() + " o con un código que no exista.");

        codigo.setPromptText("Código, p. ej. " + padron.mototaxis().get(0).codigo());
        codigo.setPrefColumnCount(10);
        Button buscar = new Button("Buscar");
        buscar.setDefaultButton(true);
        buscar.setOnAction(e -> buscar());
        double[] promedios = padron.promedios();
        Label resumen = new Label(String.format(Locale.ROOT,
                "n = %d registros · m = %d cubetas · α = %.2f · promedio por búsqueda: hash %.2f vs lineal %.1f"
                        + " comparaciones", padron.mototaxis().size(), padron.capacidad(), padron.factorCarga(),
                promedios[0], promedios[1]));

        HBox barra = new HBox(8, new Label("Código"), codigo, buscar);
        barra.setAlignment(Pos.CENTER_LEFT);
        resultado.getStyleClass().add("leyenda-paso");
        resultado.setWrapText(true);
        VBox cabecera = new VBox(6, titulo, nota, barra, resultado, resumen);
        cabecera.setPadding(new Insets(0, 8, 8, 8));
        setTop(cabecera);

        SplitPane graficos = new SplitPane(graficoCadenas(), graficoCrecimiento());
        graficos.setDividerPositions(0.5);
        setCenter(graficos);
    }

    private void buscar() {
        String texto = codigo.getText().strip().toUpperCase(Locale.ROOT);
        if (texto.isEmpty()) {
            return;
        }
        Mototaxi m = padron.buscar(texto);
        int hash = padron.comparacionesHash(texto);
        int lineal = padron.comparacionesLineal(texto);
        resultado.setText((m == null
                ? "No existe ninguna mototaxi con el código " + texto + "."
                : texto + ": " + m.propietario() + " · " + m.asociacion() + " · paradero " + m.paradero() + ".")
                + "  Comparaciones: tabla hash " + hash + ", búsqueda lineal " + lineal + ".");
    }

    private VBox graficoCadenas() {
        CategoryAxis x = new CategoryAxis();
        x.setLabel("Largo de la cadena (claves en la cubeta)");
        NumberAxis y = new NumberAxis();
        y.setLabel("Cantidad de cubetas");
        BarChart<String, Number> grafico = new BarChart<>(x, y);
        grafico.setTitle("¿Qué tan parejo reparte el hash? (" + padron.capacidad() + " cubetas)");
        grafico.setLegendVisible(false);
        grafico.setAnimated(false);
        int[] longitudes = padron.longitudesCadenas();
        int maximo = Arrays.stream(longitudes).max().orElse(0);
        int[] cuantas = new int[maximo + 1];
        for (int largo : longitudes) {
            cuantas[largo]++;
        }
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        for (int largo = 0; largo <= maximo; largo++) {
            serie.getData().add(new XYChart.Data<>(largo + (largo == 1 ? " clave" : " claves"), cuantas[largo]));
        }
        grafico.getData().add(serie);
        VBox caja = new VBox(grafico);
        VBox.setVgrow(grafico, Priority.ALWAYS);
        return caja;
    }

    private VBox graficoCrecimiento() {
        GraficoComplejidad grafico = new GraficoComplejidad("Comparaciones promedio por búsqueda",
                "Registros en el padrón (n)", "Comparaciones");
        for (int n = 10; n <= padron.mototaxis().size(); n += 10) {
            double[] p = padron.primeros(n).promedios();
            grafico.agregarPunto("Tabla hash ≈ 1 + α/2", n, p[0]);
            grafico.agregarPunto("Búsqueda lineal ≈ n/2", n, p[1]);
        }
        VBox caja = new VBox(grafico);
        VBox.setVgrow(grafico, Priority.ALWAYS);
        return caja;
    }
}
