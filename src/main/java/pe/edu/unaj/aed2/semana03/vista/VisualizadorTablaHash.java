package pe.edu.unaj.aed2.semana03.vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.comun.visual.ControlesPaso;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.comun.visual.PanelAnimacion;
import pe.edu.unaj.aed2.comun.visual.PanelTraza;
import pe.edu.unaj.aed2.semana03.modelo.EstadoTabla;
import pe.edu.unaj.aed2.semana03.modelo.TablaHash;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Semana 03: tabla hash con encadenamiento; inserción y búsqueda paso a paso. */
public class VisualizadorTablaHash extends BorderPane {

    private static final int CAPACIDAD_INICIAL = 7;
    private static final String[] EJEMPLO = {"JUL-102", "JUL-215", "PUN-031", "JUL-347", "CAR-008", "JUL-520"};

    private final PanelAnimacion<EstadoTabla> panel =
            new PanelAnimacion<>(VisualizadorTablaHash::dibujar, EstadoTabla::descripcion);
    private final PanelTraza<EstadoTabla> traza = new PanelTraza<>(panel, TablaHash.CODIGO_INSERTAR);
    private final TextField clave = new TextField("JUL-102");
    private final Label estado = new Label();
    private TablaHash<String> tabla = new TablaHash<>(CAPACIDAD_INICIAL);

    public VisualizadorTablaHash() {
        clave.setPrefColumnCount(10);
        Button insertar = new Button("Insertar");
        Button buscar = new Button("Buscar");
        Button ejemplo = new Button("Cargar ejemplo");
        Button nueva = new Button("Tabla nueva");
        insertar.setDefaultButton(true);
        insertar.setOnAction(e -> operar(true));
        buscar.setOnAction(e -> operar(false));
        ejemplo.setOnAction(e -> cargarEjemplo());
        nueva.setOnAction(e -> {
            tabla = new TablaHash<>(CAPACIDAD_INICIAL);
            mostrar(tabla.estadoActual(), TablaHash.CODIGO_INSERTAR);
        });

        HBox opciones = new HBox(8, new Label("Clave"), clave, insertar, buscar, ejemplo, nueva, estado);
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8));

        Label titulo = new Label("Semana 03 · Tabla hash con encadenamiento");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.5);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        cargarEjemplo();
    }

    private void operar(boolean esInsercion) {
        String texto = clave.getText().strip().toUpperCase(Locale.ROOT);
        if (texto.isEmpty()) {
            return;
        }
        List<EstadoTabla> pasos = esInsercion ? tabla.pasosInsertar(texto, texto) : tabla.pasosBuscar(texto);
        mostrar(pasos, esInsercion ? TablaHash.CODIGO_INSERTAR : TablaHash.CODIGO_BUSCAR);
    }

    /** Inserta las claves de ejemplo, una tras otra, en una sola secuencia de pasos. */
    private void cargarEjemplo() {
        tabla = new TablaHash<>(CAPACIDAD_INICIAL);
        List<EstadoTabla> pasos = new ArrayList<>();
        for (String c : EJEMPLO) {
            pasos.addAll(tabla.pasosInsertar(c, c));
        }
        mostrar(pasos, TablaHash.CODIGO_INSERTAR);
    }

    private void mostrar(List<EstadoTabla> pasos, CodigoAlgoritmo codigo) {
        traza.setCodigo(codigo);
        panel.setPasos(pasos);
        estado.setText("Al terminar: n = " + tabla.tamanio() + " · m = " + tabla.capacidad() + " · α = "
                + String.format(Locale.ROOT, "%.2f", tabla.factorCarga()));
    }

    private static void dibujar(GraphicsContext gc, EstadoTabla paso, double ancho, double alto) {
        List<List<String>> cubetas = paso.cubetas();
        int m = cubetas.size();
        double arriba = 40;
        double fila = Math.min(46, (alto - arriba - 10) / m);
        double xIndice = 16;
        double anchoIndice = 46;
        double anchoNodo = 86;
        double separacion = 26;

        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font("System", FontWeight.BOLD, 13));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.CENTER);
        double alfa = (double) paso.tamanio() / m;
        gc.fillText("m = " + m + "   n = " + paso.tamanio() + "   α = n/m = "
                + String.format(Locale.ROOT, "%.2f", alfa), xIndice, 16);
        if (paso.cubeta() >= 0) {
            gc.fillText("clave = \"" + paso.clave() + "\"  →  i = " + paso.cubeta(), ancho / 2, 16);
        }

        gc.setFont(Font.font("Monospaced", 12));
        for (int i = 0; i < m; i++) {
            double y = arriba + i * fila;
            double alto2 = fila - 8;
            boolean actual = i == paso.cubeta();
            gc.setFill(actual ? Color.web("#fff59d") : Color.WHITE);
            gc.fillRect(xIndice, y, anchoIndice, alto2);
            gc.setStroke(Paleta.BORDE);
            gc.setLineWidth(actual ? 2.5 : 1);
            gc.strokeRect(xIndice, y, anchoIndice, alto2);
            gc.setFill(Paleta.TEXTO);
            gc.setTextAlign(TextAlignment.CENTER);
            gc.fillText("[" + i + "]", xIndice + anchoIndice / 2, y + alto2 / 2);

            List<String> cadena = cubetas.get(i);
            double x = xIndice + anchoIndice + separacion;
            gc.setLineWidth(1.2);
            gc.strokeLine(xIndice + anchoIndice, y + alto2 / 2, x, y + alto2 / 2);
            if (cadena.isEmpty()) {
                gc.setFill(Color.web("#9e9e9e"));
                gc.setTextAlign(TextAlignment.LEFT);
                gc.fillText("null", x + 4, y + alto2 / 2);
                continue;
            }
            for (int k = 0; k < cadena.size(); k++) {
                gc.setFill(colorNodo(paso, i, k));
                gc.fillRoundRect(x, y, anchoNodo, alto2, 8, 8);
                gc.setStroke(Paleta.BORDE);
                gc.strokeRoundRect(x, y, anchoNodo, alto2, 8, 8);
                gc.setFill(Paleta.TEXTO);
                gc.setTextAlign(TextAlignment.CENTER);
                gc.fillText(cadena.get(k), x + anchoNodo / 2, y + alto2 / 2);
                if (k + 1 < cadena.size()) {
                    double x2 = x + anchoNodo + separacion;
                    gc.strokeLine(x + anchoNodo, y + alto2 / 2, x2 - 4, y + alto2 / 2);
                    gc.fillPolygon(new double[]{x2, x2 - 8, x2 - 8},
                            new double[]{y + alto2 / 2, y + alto2 / 2 - 4, y + alto2 / 2 + 4}, 3);
                }
                x += anchoNodo + separacion;
            }
        }
    }

    private static Color colorNodo(EstadoTabla paso, int i, int k) {
        if (i != paso.cubeta() || k != paso.posicion()) {
            return Paleta.NORMAL;
        }
        return switch (paso.tipo()) {
            case COMPARACION -> Paleta.COMPARANDO;
            case AGREGADA -> Paleta.INTERCAMBIO;
            case ENCONTRADA, YA_EXISTE, FIN, REHASH, CARGA -> Paleta.FINALIZADO;
            default -> Paleta.NORMAL;
        };
    }
}
