package pe.edu.unaj.aed2.comun.visual;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

/**
 * Barra de controles estándar para un {@link PanelAnimacion}:
 * reiniciar, anterior, reproducir/pausar, siguiente, final y velocidad.
 */
public class ControlesPaso extends HBox {

    public ControlesPaso(PanelAnimacion<?> panel) {
        super(8);
        setAlignment(Pos.CENTER_LEFT);
        setPadding(new Insets(8));
        getStyleClass().add("controles-paso");

        Button reiniciar = new Button("⏮");
        Button anterior = new Button("◀");
        Button reproducir = new Button();
        Button siguiente = new Button("▶");
        Button fin = new Button("⏭");

        reiniciar.setOnAction(e -> panel.reiniciar());
        anterior.setOnAction(e -> {
            panel.pausar();
            panel.anterior();
        });
        siguiente.setOnAction(e -> {
            panel.pausar();
            panel.siguiente();
        });
        fin.setOnAction(e -> panel.irAlFinal());
        reproducir.setOnAction(e -> {
            if (panel.reproduciendoProperty().get()) {
                panel.pausar();
            } else {
                panel.reproducir();
            }
        });
        reproducir.textProperty().bind(Bindings.when(panel.reproduciendoProperty())
                .then("⏸ Pausar").otherwise("⏵ Reproducir"));
        reproducir.setMinWidth(110);

        Slider velocidad = new Slider(0.25, 4, 1);
        velocidad.setPrefWidth(120);
        velocidad.valueProperty().addListener((o, a, v) -> panel.setVelocidad(v.doubleValue()));
        Label etiquetaVelocidad = new Label();
        etiquetaVelocidad.textProperty().bind(Bindings.format("x%.2f", velocidad.valueProperty()));

        Label contador = new Label();
        contador.textProperty().bind(Bindings.createStringBinding(
                () -> "Paso " + (panel.totalProperty().get() == 0 ? 0 : panel.indiceProperty().get() + 1)
                        + " / " + panel.totalProperty().get(),
                panel.indiceProperty(), panel.totalProperty()));

        Region espacio = new Region();
        HBox.setHgrow(espacio, Priority.ALWAYS);

        getChildren().addAll(reiniciar, anterior, reproducir, siguiente, fin,
                espacio, new Label("Velocidad"), velocidad, etiquetaVelocidad, contador);
    }
}
