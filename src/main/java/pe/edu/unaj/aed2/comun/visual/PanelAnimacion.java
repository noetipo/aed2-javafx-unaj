package pe.edu.unaj.aed2.comun.visual;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.List;
import java.util.function.Function;

/**
 * Lienzo que reproduce una secuencia de pasos precalculados.
 * <p>
 * El modelo genera la lista de pasos (estados inmutables, sin JavaFX) y la vista
 * solo decide cómo dibujar cada uno. Así el algoritmo se prueba con JUnit y la
 * animación permite avanzar, retroceder o reproducir.
 *
 * @param <T> tipo de paso (por ejemplo, un record con el estado del arreglo)
 */
public class PanelAnimacion<T> extends BorderPane {

    /** Estrategia de dibujo de un paso sobre el lienzo. */
    @FunctionalInterface
    public interface Dibujante<T> {
        void dibujar(GraphicsContext gc, T paso, double ancho, double alto);
    }

    private static final Duration INTERVALO_BASE = Duration.millis(600);

    private final Canvas lienzo = new Canvas();
    private final Label leyenda = new Label();
    private final Dibujante<T> dibujante;
    private final Function<T, String> descriptor;

    private final IntegerProperty indice = new SimpleIntegerProperty(0);
    private final IntegerProperty total = new SimpleIntegerProperty(0);
    private final BooleanProperty reproduciendo = new SimpleBooleanProperty(false);
    private final Timeline reproduccion;

    private final ReadOnlyObjectWrapper<List<T>> pasos = new ReadOnlyObjectWrapper<>(List.of());

    public PanelAnimacion(Dibujante<T> dibujante, Function<T, String> descriptor) {
        this.dibujante = dibujante;
        this.descriptor = descriptor;

        Pane contenedor = new Pane(lienzo);
        contenedor.setPrefSize(720, 420);
        contenedor.setMinSize(0, 0);
        lienzo.widthProperty().bind(contenedor.widthProperty());
        lienzo.heightProperty().bind(contenedor.heightProperty());
        lienzo.widthProperty().addListener(o -> redibujar());
        lienzo.heightProperty().addListener(o -> redibujar());

        leyenda.getStyleClass().add("leyenda-paso");
        leyenda.setWrapText(true);
        BorderPane.setMargin(leyenda, new Insets(6, 8, 6, 8));

        setCenter(contenedor);
        setBottom(leyenda);

        reproduccion = new Timeline(new KeyFrame(INTERVALO_BASE, e -> {
            if (!siguiente()) {
                pausar();
            }
        }));
        reproduccion.setCycleCount(Timeline.INDEFINITE);
        indice.addListener(o -> redibujar());
    }

    /** Reemplaza la secuencia de pasos y vuelve al primero. */
    public void setPasos(List<T> nuevosPasos) {
        pausar();
        List<T> copia = List.copyOf(nuevosPasos);
        total.set(copia.size());
        pasos.set(copia);
        if (indice.get() == 0) {
            redibujar();
        } else {
            indice.set(0);
        }
    }

    public boolean siguiente() {
        if (indice.get() < pasos.get().size() - 1) {
            indice.set(indice.get() + 1);
            return true;
        }
        return false;
    }

    public boolean anterior() {
        if (indice.get() > 0) {
            indice.set(indice.get() - 1);
            return true;
        }
        return false;
    }

    public void reiniciar() {
        pausar();
        indice.set(0);
    }

    public void irAlFinal() {
        pausar();
        indice.set(Math.max(0, pasos.get().size() - 1));
    }

    /** Salta directamente al paso {@code k} (se ignora si está fuera de rango). */
    public void irA(int k) {
        if (k >= 0 && k < pasos.get().size() && k != indice.get()) {
            pausar();
            indice.set(k);
        }
    }

    public void reproducir() {
        if (pasos.get().isEmpty()) {
            return;
        }
        if (indice.get() == pasos.get().size() - 1) {
            indice.set(0);
        }
        reproduccion.play();
        reproduciendo.set(true);
    }

    public void pausar() {
        if (reproduccion != null) {
            reproduccion.stop();
        }
        reproduciendo.set(false);
    }

    /** Multiplicador de velocidad: 1.0 = un paso cada 600 ms. */
    public void setVelocidad(double factor) {
        reproduccion.setRate(Math.max(0.1, factor));
    }

    public ReadOnlyIntegerProperty indiceProperty() {
        return indice;
    }

    public ReadOnlyIntegerProperty totalProperty() {
        return total;
    }

    public ReadOnlyBooleanProperty reproduciendoProperty() {
        return reproduciendo;
    }

    /** Secuencia actual de pasos; cambia cada vez que se llama a {@link #setPasos}. */
    public ReadOnlyObjectProperty<List<T>> pasosProperty() {
        return pasos.getReadOnlyProperty();
    }

    public List<T> pasos() {
        return pasos.get();
    }

    /** Texto explicativo de un paso (el mismo que aparece en la leyenda). */
    public String describir(T paso) {
        return descriptor.apply(paso);
    }

    public T pasoActual() {
        List<T> lista = pasos.get();
        return lista.isEmpty() ? null : lista.get(Math.min(indice.get(), lista.size() - 1));
    }

    public void redibujar() {
        GraphicsContext gc = lienzo.getGraphicsContext2D();
        double ancho = lienzo.getWidth();
        double alto = lienzo.getHeight();
        gc.setFill(Paleta.FONDO);
        gc.fillRect(0, 0, ancho, alto);
        T paso = pasoActual();
        if (paso == null) {
            leyenda.setText("Sin pasos para mostrar.");
            return;
        }
        dibujante.dibujar(gc, paso, ancho, alto);
        leyenda.setText(descriptor.apply(paso));
    }
}
