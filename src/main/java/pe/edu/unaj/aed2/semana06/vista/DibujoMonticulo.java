package pe.edu.unaj.aed2.semana06.vista;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pe.edu.unaj.aed2.comun.visual.Paleta;

import java.util.List;
import java.util.function.IntFunction;

/**
 * Dibuja un montículo de dos formas a la vez: como árbol completo (arriba) y como el arreglo
 * que realmente se guarda (abajo). La posición k del arreglo es el nodo k del árbol: sus hijos
 * están en 2k + 1 y 2k + 2.
 */
public final class DibujoMonticulo {

    private DibujoMonticulo() {
    }

    /**
     * @param valores  contenido del arreglo
     * @param tamanio  posiciones que forman el montículo (el árbol solo dibuja esas)
     * @param color    color de cada posición
     */
    public static void dibujar(GraphicsContext gc, List<String> valores, int tamanio, IntFunction<Color> color,
                               double ancho, double alto) {
        double altoArreglo = 70;
        dibujarArbol(gc, valores, tamanio, color, ancho, alto - altoArreglo - 10);
        dibujarArreglo(gc, valores, tamanio, color, ancho, alto - altoArreglo, altoArreglo);
    }

    private static void dibujarArbol(GraphicsContext gc, List<String> valores, int tamanio, IntFunction<Color> color,
                                     double ancho, double alto) {
        if (tamanio == 0) {
            gc.setFill(Color.web("#9e9e9e"));
            gc.setFont(Font.font(14));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.fillText("Montículo vacío", ancho / 2, alto / 2);
            return;
        }
        int niveles = 32 - Integer.numberOfLeadingZeros(tamanio);
        double dy = Math.min(80, (alto - 40) / Math.max(1, niveles - 1));
        double maxPorNivel = Math.pow(2, niveles - 1);
        double anchoNodo = Math.max(26, Math.min(76, ancho / maxPorNivel - 8));
        double altoNodo = 28;
        gc.setStroke(Paleta.BORDE);
        gc.setLineWidth(1.3);
        for (int k = 1; k < tamanio; k++) {
            double[] h = posicion(k, ancho, dy);
            double[] p = posicion((k - 1) / 2, ancho, dy);
            gc.strokeLine(p[0], p[1], h[0], h[1]);
        }
        for (int k = 0; k < tamanio; k++) {
            double[] c = posicion(k, ancho, dy);
            caja(gc, c[0] - anchoNodo / 2, c[1] - altoNodo / 2, anchoNodo, altoNodo, valores.get(k), color.apply(k),
                    13);
            gc.setFill(Color.web("#78909c"));
            gc.setFont(Font.font(10));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.TOP);
            gc.fillText(String.valueOf(k), c[0], c[1] + altoNodo / 2 + 1);
        }
    }

    private static double[] posicion(int k, double ancho, double dy) {
        int nivel = 31 - Integer.numberOfLeadingZeros(k + 1);
        int enNivel = (k + 1) - (1 << nivel);
        double x = (enNivel + 0.5) * ancho / (1 << nivel);
        return new double[]{x, 24 + nivel * dy};
    }

    private static void dibujarArreglo(GraphicsContext gc, List<String> valores, int tamanio, IntFunction<Color> color,
                                       double ancho, double y, double alto) {
        int n = valores.size();
        if (n == 0) {
            return;
        }
        double margen = 10;
        double celda = Math.min(80, (ancho - 2 * margen) / n);
        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font("System", FontWeight.BOLD, 12));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.TOP);
        gc.fillText("Arreglo:", margen, y);
        for (int k = 0; k < n; k++) {
            double x = margen + k * celda;
            Color c = k < tamanio ? color.apply(k) : Paleta.FINALIZADO;
            caja(gc, x + 1, y + 18, celda - 2, 30, valores.get(k), c, 12);
            gc.setFill(Color.web("#78909c"));
            gc.setFont(Font.font(10));
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.TOP);
            gc.fillText(String.valueOf(k), x + celda / 2, y + 50);
        }
    }

    private static void caja(GraphicsContext gc, double x, double y, double w, double h, String texto, Color relleno,
                             double tamanoLetra) {
        gc.setFill(relleno);
        gc.fillRoundRect(x, y, w, h, 8, 8);
        gc.setStroke(Paleta.BORDE);
        gc.setLineWidth(1.2);
        gc.strokeRoundRect(x, y, w, h, 8, 8);
        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font("System", FontWeight.BOLD, tamanoLetra));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(texto, x + w / 2, y + h / 2, w - 4);
    }
}
