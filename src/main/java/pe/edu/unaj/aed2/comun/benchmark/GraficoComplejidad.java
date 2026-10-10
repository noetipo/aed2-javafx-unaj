package pe.edu.unaj.aed2.comun.benchmark;

import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Gráfico de líneas "tamaño de entrada (n) vs. tiempo (ms)" para comparar
 * empíricamente el crecimiento de varios algoritmos.
 * Los puntos pueden agregarse desde cualquier hilo.
 */
public class GraficoComplejidad extends LineChart<Number, Number> {

    private final Map<String, Series<Number, Number>> series = new LinkedHashMap<>();

    public GraficoComplejidad(String titulo) {
        this(titulo, "Tamaño de entrada (n)", "Tiempo (ms)");
    }

    public GraficoComplejidad(String titulo, String ejeX, String ejeY) {
        super(new NumberAxis(), new NumberAxis());
        setTitle(titulo);
        getXAxis().setLabel(ejeX);
        getYAxis().setLabel(ejeY);
        setCreateSymbols(true);
        setAnimated(false);
    }

    /** Agrega el punto (n, valor) a la serie indicada, creándola si no existe. */
    public void agregarPunto(String serie, Number n, Number valor) {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(() -> agregarPunto(serie, n, valor));
            return;
        }
        series.computeIfAbsent(serie, nombre -> {
            Series<Number, Number> s = new XYChart.Series<>();
            s.setName(nombre);
            getData().add(s);
            return s;
        }).getData().add(new XYChart.Data<>(n, valor));
    }

    public void limpiar() {
        if (!Platform.isFxApplicationThread()) {
            Platform.runLater(this::limpiar);
            return;
        }
        series.clear();
        getData().clear();
    }
}
