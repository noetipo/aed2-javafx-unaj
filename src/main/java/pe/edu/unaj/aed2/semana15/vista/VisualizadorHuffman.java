package pe.edu.unaj.aed2.semana15.vista;

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
import pe.edu.unaj.aed2.comun.visual.ControlesPaso;
import pe.edu.unaj.aed2.comun.visual.DibujoArbol;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.comun.visual.PanelAnimacion;
import pe.edu.unaj.aed2.comun.visual.PanelTraza;
import pe.edu.unaj.aed2.semana15.modelo.EstadoHuffman;
import pe.edu.unaj.aed2.semana15.modelo.Huffman;
import pe.edu.unaj.aed2.semana15.modelo.Huffman.Nodo;

import java.util.ArrayList;
import java.util.List;

/** Semana 15: algoritmo voraz de Huffman, uniendo siempre los dos árboles menos frecuentes. */
public class VisualizadorHuffman extends BorderPane {

    private static final int MAXIMO = 40;

    private final PanelAnimacion<EstadoHuffman> panel =
            new PanelAnimacion<>(VisualizadorHuffman::dibujar, EstadoHuffman::descripcion);
    private final TextField texto = new TextField(Huffman.EJEMPLO);
    private final Label aviso = new Label();

    public VisualizadorHuffman() {
        PanelTraza<EstadoHuffman> traza = new PanelTraza<>(panel, Huffman.CODIGO);
        Button construir = new Button("Construir");
        construir.setOnAction(e -> cargar());
        texto.setOnAction(e -> cargar());
        texto.setPrefColumnCount(26);
        HBox opciones = new HBox(8, new Label("Texto"), texto, construir, aviso);
        opciones.setAlignment(Pos.CENTER_LEFT);
        opciones.setPadding(new Insets(8));
        Label titulo = new Label("Semana 15 · Algoritmos voraces: códigos de Huffman");
        titulo.getStyleClass().add("titulo-vista");
        setTop(new VBox(titulo, opciones));
        SplitPane division = new SplitPane(panel, traza);
        division.setDividerPositions(0.55);
        setCenter(division);
        setBottom(new ControlesPaso(panel));
        cargar();
    }

    private void cargar() {
        String t = texto.getText();
        if (t.isEmpty()) {
            aviso.setText("Escribe al menos un carácter.");
            return;
        }
        if (t.length() > MAXIMO) {
            t = t.substring(0, MAXIMO);
            texto.setText(t);
            aviso.setText("Se usan los primeros " + MAXIMO + " caracteres.");
        } else {
            aviso.setText("");
        }
        panel.setPasos(Huffman.pasos(t));
    }

    private static void dibujar(GraphicsContext gc, EstadoHuffman paso, double ancho, double alto) {
        gc.setTextBaseline(VPos.CENTER);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(Font.font("System", 12));
        gc.setFill(Color.web("#546e7a"));
        gc.fillText("Cada árbol de la cola, de menor a mayor frecuencia · Naranja: los dos que se unen · Verde: el"
                + " árbol nuevo", 10, 14);
        boolean codigos = !paso.codigos().isEmpty();
        gc.fillText(codigos ? "Bajo cada hoja, su código: izquierda = 0, derecha = 1"
                : "Hoja \"a:6\" = carácter a, aparece 6 veces · ␣ = espacio", 10, 30);

        List<Nodo> bosque = new ArrayList<>(paso.bosque());
        if (paso.tipo() == EstadoHuffman.Tipo.UNE) {
            // Se muestran también los dos extraídos (ahora dentro del nuevo) para que se vea la unión.
            bosque.remove(paso.nuevo());
            bosque.add(0, paso.nuevo());
        }
        double arriba = 50;
        double abajo = 40;
        int hojasTotal = bosque.stream().mapToInt(VisualizadorHuffman::hojas).sum();
        double x = 10;
        for (Nodo arbol : bosque) {
            double anchoArbol = (ancho - 20) * hojas(arbol) / Math.max(1, hojasTotal);
            gc.save();
            gc.translate(x, arriba);
            DibujoArbol.dibujar(gc, arbol, Nodo::izq, Nodo::der, Nodo::etiqueta,
                    n -> color(paso, arbol, n),
                    codigos ? n -> n.esHoja() ? paso.codigos().get(n.caracter()) : "" : null,
                    anchoArbol, alto - arriba - abajo);
            gc.restore();
            x += anchoArbol;
        }

        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFill(Paleta.TEXTO);
        gc.setFont(Font.font("System", FontWeight.BOLD, 13));
        gc.fillText(codigos ? "Códigos listos" : "Árboles en la cola: " + paso.bosque().size(), 10, alto - 16);
    }

    private static Color color(EstadoHuffman paso, Nodo arbol, Nodo n) {
        if (paso.tipo() == EstadoHuffman.Tipo.UNE) {
            if (n == paso.nuevo()) {
                return Paleta.FINALIZADO;
            }
            if (n == paso.a() || n == paso.b()) {
                return Paleta.COMPARANDO;
            }
        }
        return n.esHoja() ? Color.web("#e3f2fd") : Color.web("#cfd8dc");
    }

    private static int hojas(Nodo n) {
        return n.esHoja() ? 1 : hojas(n.izq()) + hojas(n.der());
    }
}
