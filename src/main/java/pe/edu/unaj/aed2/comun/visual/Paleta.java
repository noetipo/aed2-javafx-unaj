package pe.edu.unaj.aed2.comun.visual;

import javafx.scene.paint.Color;

/**
 * Colores comunes a todos los visualizadores, para que cada semana
 * use el mismo significado visual (comparar, intercambiar, visitado, etc.).
 */
public final class Paleta {

    public static final Color FONDO = Color.web("#fafafa");
    public static final Color TEXTO = Color.web("#212121");
    public static final Color BORDE = Color.web("#424242");

    /** Elemento en reposo. */
    public static final Color NORMAL = Color.web("#90caf9");
    /** Elementos que se están comparando. */
    public static final Color COMPARANDO = Color.web("#ffb74d");
    /** Elementos que se acaban de mover o intercambiar. */
    public static final Color INTERCAMBIO = Color.web("#e57373");
    /** Elementos en su posición final / ya procesados. */
    public static final Color FINALIZADO = Color.web("#81c784");
    /** Nodo o vértice visitado en un recorrido. */
    public static final Color VISITADO = Color.web("#ba68c8");
    /** Camino o resultado destacado. */
    public static final Color RESALTADO = Color.web("#fdd835");

    /** Colores para series de gráficos (benchmark). */
    public static final Color[] SERIES = {
            Color.web("#1e88e5"), Color.web("#e53935"), Color.web("#43a047"),
            Color.web("#fb8c00"), Color.web("#8e24aa"), Color.web("#00897b")
    };

    private Paleta() {
    }

    /** Convierte un color a formato CSS (#rrggbb). */
    public static String css(Color c) {
        return String.format("#%02x%02x%02x",
                (int) Math.round(c.getRed() * 255),
                (int) Math.round(c.getGreen() * 255),
                (int) Math.round(c.getBlue() * 255));
    }
}
