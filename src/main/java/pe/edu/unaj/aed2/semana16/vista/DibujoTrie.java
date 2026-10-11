package pe.edu.unaj.aed2.semana16.vista;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pe.edu.unaj.aed2.comun.visual.Paleta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Dibuja un trie a partir de los prefijos de sus nodos. Cada nodo muestra su última letra; los que son fin de palabra
 * tienen borde grueso y la palabra escrita debajo.
 */
public final class DibujoTrie {

    private DibujoTrie() {
    }

    /**
     * @param desde dibuja solo el subárbol de este prefijo ("" = todo el trie)
     */
    public static void dibujar(GraphicsContext gc, Set<String> nodos, Set<String> finales, String desde,
                               Function<String, Color> relleno, double ancho, double alto) {
        Map<String, List<String>> hijos = new HashMap<>();
        for (String p : nodos) {
            if (p.length() > desde.length() && p.startsWith(desde)) {
                hijos.computeIfAbsent(p.substring(0, p.length() - 1), k -> new ArrayList<>()).add(p);
            }
        }
        hijos.values().forEach(l -> l.sort(null));
        Map<String, double[]> pos = new HashMap<>();
        int[] hoja = {0};
        int profundidad = ubicar(desde, 0, hijos, pos, hoja);
        int hojas = Math.max(1, hoja[0]);
        double margen = 30;
        double dx = (ancho - 2 * margen) / Math.max(1, hojas - 1);
        double dy = Math.min(68, (alto - 2 * margen - 20) / Math.max(1, profundidad));
        double radio = Math.max(8, Math.min(17, Math.min(dx, dy) * 0.42));
        Function<String, double[]> xy = p -> {
            double[] q = pos.get(p);
            return new double[]{hojas == 1 ? ancho / 2 : margen + q[0] * dx, margen + q[1] * dy};
        };
        gc.setStroke(Paleta.BORDE);
        gc.setLineWidth(1.5);
        for (Map.Entry<String, List<String>> e : hijos.entrySet()) {
            if (!pos.containsKey(e.getKey())) {
                continue;
            }
            double[] a = xy.apply(e.getKey());
            for (String h : e.getValue()) {
                double[] b = xy.apply(h);
                gc.strokeLine(a[0], a[1], b[0], b[1]);
            }
        }
        for (String p : pos.keySet()) {
            double[] a = xy.apply(p);
            boolean fin = finales.contains(p);
            gc.setFill(relleno.apply(p));
            gc.fillOval(a[0] - radio, a[1] - radio, 2 * radio, 2 * radio);
            gc.setStroke(fin ? Color.web("#2e7d32") : Paleta.BORDE);
            gc.setLineWidth(fin ? 3.5 : 1.5);
            gc.strokeOval(a[0] - radio, a[1] - radio, 2 * radio, 2 * radio);
            gc.setFill(Paleta.TEXTO);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.setTextBaseline(VPos.CENTER);
            gc.setFont(Font.font("System", FontWeight.BOLD, Math.max(9, radio * 0.8)));
            gc.fillText(p.isEmpty() ? "·" : p.equals(desde) && !desde.isEmpty() ? desde
                    : p.substring(p.length() - 1), a[0], a[1]);
            if (fin) {
                gc.setFont(Font.font("System", FontWeight.NORMAL, 11));
                gc.setFill(Color.web("#2e7d32"));
                gc.setTextBaseline(VPos.TOP);
                gc.fillText(p, a[0], a[1] + radio + 2);
            }
        }
    }

    /** Ubica las hojas de izquierda a derecha y cada padre en medio de sus hijos; retorna la profundidad máxima. */
    private static int ubicar(String p, int nivel, Map<String, List<String>> hijos, Map<String, double[]> pos,
                              int[] hoja) {
        List<String> h = hijos.getOrDefault(p, List.of());
        if (h.isEmpty()) {
            pos.put(p, new double[]{hoja[0]++, nivel});
            return nivel;
        }
        int maximo = nivel;
        for (String hijo : h) {
            maximo = Math.max(maximo, ubicar(hijo, nivel + 1, hijos, pos, hoja));
        }
        double centro = (pos.get(h.get(0))[0] + pos.get(h.get(h.size() - 1))[0]) / 2;
        pos.put(p, new double[]{centro, nivel});
        return maximo;
    }
}
