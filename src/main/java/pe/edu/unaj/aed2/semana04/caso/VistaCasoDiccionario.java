package pe.edu.unaj.aed2.semana04.caso;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import pe.edu.unaj.aed2.comun.visual.DibujoArbol;
import pe.edu.unaj.aed2.comun.visual.Paleta;
import pe.edu.unaj.aed2.semana04.caso.DiccionarioQuechua.Entrada;
import pe.edu.unaj.aed2.semana04.modelo.ArbolBusqueda;
import pe.edu.unaj.aed2.semana04.modelo.EstadoArbol.NodoVista;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Caso práctico semana 04: diccionario quechua–español en un ABB. Compara el árbol que se
 * obtiene al insertar en orden alfabético (degenerado) con el de un orden mezclado.
 */
public class VistaCasoDiccionario extends BorderPane {

    private final DiccionarioQuechua diccionario = DiccionarioQuechua.desdeRecurso();
    private final TextField palabra = new TextField("inti");
    private final Label resultado = new Label();
    private final Canvas lienzoAlfabetico = new Canvas();
    private final Canvas lienzoMezclado = new Canvas();
    private final Label etiquetaAlfabetico = new Label();
    private final Label etiquetaMezclado = new Label();
    private List<String> caminoAlfabetico = List.of();
    private List<String> caminoMezclado = List.of();

    public VistaCasoDiccionario() {
        Label titulo = new Label("Semana 04 · Caso: diccionario quechua–español");
        titulo.getStyleClass().add("titulo-vista");
        Label nota = new Label("Vocabulario básico con grafía referencial (hay variantes regionales). El mismo"
                + " diccionario, insertado en dos órdenes distintos.");

        palabra.setPrefColumnCount(10);
        Button buscar = new Button("Buscar");
        buscar.setDefaultButton(true);
        buscar.setOnAction(e -> buscar());
        HBox barra = new HBox(8, new Label("Palabra"), palabra, buscar);
        barra.setAlignment(Pos.CENTER_LEFT);

        double[] promedios = diccionario.promedios();
        Label resumen = new Label(String.format(Locale.ROOT,
                "n = %d palabras · altura mínima posible ⌈log₂(n+1)⌉ = %d · alfabético: altura %d, %.1f"
                        + " comparaciones en promedio · mezclado: altura %d, %.1f comparaciones en promedio",
                diccionario.tamanio(), diccionario.alturaMinima(), diccionario.arbolAlfabetico().altura(),
                promedios[0], diccionario.arbolMezclado().altura(), promedios[1]));
        resultado.getStyleClass().add("leyenda-paso");
        resultado.setWrapText(true);

        VBox cabecera = new VBox(6, titulo, nota, barra, resultado, resumen);
        cabecera.setPadding(new Insets(0, 8, 8, 8));
        setTop(cabecera);

        SplitPane division = new SplitPane(
                panelArbol("Insertado en orden alfabético (degenerado)", lienzoAlfabetico, etiquetaAlfabetico),
                panelArbol("Insertado en orden mezclado", lienzoMezclado, etiquetaMezclado));
        division.setDividerPositions(0.5);
        setCenter(division);
        buscar();
    }

    private VBox panelArbol(String titulo, Canvas lienzo, Label camino) {
        camino.setWrapText(true);
        Label etiqueta = new Label(titulo);
        etiqueta.getStyleClass().add("subtitulo-traza");
        Pane contenedor = new Pane(lienzo);
        contenedor.setMinSize(0, 0);
        contenedor.setPrefSize(500, 400);
        lienzo.widthProperty().bind(contenedor.widthProperty());
        lienzo.heightProperty().bind(contenedor.heightProperty());
        lienzo.widthProperty().addListener(o -> redibujar());
        lienzo.heightProperty().addListener(o -> redibujar());
        VBox panel = new VBox(6, etiqueta, camino, contenedor);
        panel.setPadding(new Insets(8));
        VBox.setVgrow(contenedor, Priority.ALWAYS);
        return panel;
    }

    private void buscar() {
        String texto = palabra.getText().strip().toLowerCase(Locale.ROOT);
        if (texto.isEmpty()) {
            return;
        }
        String significado = diccionario.traducir(texto);
        resultado.setText((significado == null
                ? "\"" + texto + "\" no está en el diccionario."
                : texto + " = " + significado + ".")
                + "  Comparaciones: árbol alfabético " + diccionario.comparacionesAlfabetico(texto)
                + ", árbol mezclado " + diccionario.comparacionesMezclado(texto) + ".");
        caminoAlfabetico = caminoDe(diccionario.arbolAlfabetico(), texto);
        caminoMezclado = caminoDe(diccionario.arbolMezclado(), texto);
        etiquetaAlfabetico.setText("Camino: " + String.join(" → ", caminoAlfabetico));
        etiquetaMezclado.setText("Camino: " + String.join(" → ", caminoMezclado));
        redibujar();
    }

    /** Palabras de los nodos que visita la búsqueda, en orden (para resaltarlos y listarlos). */
    private static List<String> caminoDe(ArbolBusqueda<Entrada> arbol, String texto) {
        List<String> camino = new ArrayList<>();
        NodoVista<Entrada> actual = arbol.vista();
        while (actual != null) {
            camino.add(actual.clave().palabra());
            int c = texto.compareTo(actual.clave().palabra());
            if (c == 0) {
                break;
            }
            actual = c < 0 ? actual.izquierdo() : actual.derecho();
        }
        return camino;
    }

    private void redibujar() {
        dibujar(lienzoAlfabetico, diccionario.arbolAlfabetico(), caminoAlfabetico);
        dibujar(lienzoMezclado, diccionario.arbolMezclado(), caminoMezclado);
    }

    private void dibujar(Canvas lienzo, ArbolBusqueda<Entrada> arbol, List<String> camino) {
        var gc = lienzo.getGraphicsContext2D();
        gc.setFill(Paleta.FONDO);
        gc.fillRect(0, 0, lienzo.getWidth(), lienzo.getHeight());
        String buscada = palabra.getText().strip().toLowerCase(Locale.ROOT);
        DibujoArbol.dibujar(gc, arbol.vista(), NodoVista::izquierdo, NodoVista::derecho,
                v -> v.clave().palabra(),
                v -> v.clave().palabra().equals(buscada) ? Paleta.FINALIZADO
                        : camino.contains(v.clave().palabra()) ? Paleta.COMPARANDO : Color.web("#e3f2fd"),
                lienzo.getWidth(), lienzo.getHeight());
    }
}
