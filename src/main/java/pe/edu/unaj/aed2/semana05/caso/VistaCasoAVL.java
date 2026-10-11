package pe.edu.unaj.aed2.semana05.caso;

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
import pe.edu.unaj.aed2.semana04.modelo.EstadoArbol.NodoVista;
import pe.edu.unaj.aed2.semana05.modelo.EstadoAVL.NodoAVL;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Caso práctico semana 05: el mismo diccionario, insertado en orden alfabético, en un ABB común
 * y en un AVL. El AVL se mantiene bajo gracias a las rotaciones.
 */
public class VistaCasoAVL extends BorderPane {

    private final DiccionarioAVL diccionario = DiccionarioAVL.desdeRecurso();
    private final TextField palabra = new TextField("yuraq");
    private final Label resultado = new Label();
    private final Canvas lienzoABB = new Canvas();
    private final Canvas lienzoAVL = new Canvas();
    private final Label caminoABB = new Label();
    private final Label caminoAVL = new Label();
    private List<String> recorridoABB = List.of();
    private List<String> recorridoAVL = List.of();

    public VistaCasoAVL() {
        Label titulo = new Label("Semana 05 · Caso: el diccionario quechua, ahora con AVL");
        titulo.getStyleClass().add("titulo-vista");
        Label nota = new Label("Las " + diccionario.tamanio() + " palabras se insertan en orden alfabético, el peor"
                + " caso para un ABB común (semana 04).");

        palabra.setPrefColumnCount(10);
        Button buscar = new Button("Buscar");
        buscar.setDefaultButton(true);
        buscar.setOnAction(e -> buscar());
        HBox barra = new HBox(8, new Label("Palabra"), palabra, buscar);
        barra.setAlignment(Pos.CENTER_LEFT);

        double[] p = diccionario.promedios();
        Label resumen = new Label(String.format(Locale.ROOT,
                "ABB: altura %d, %.1f comparaciones en promedio · AVL: altura %d (cota 1,44·log₂(n+2) = %.1f),"
                        + " %.1f comparaciones en promedio · rotaciones del AVL al cargar: %s",
                diccionario.abb().altura(), p[0], diccionario.avl().altura(), diccionario.cotaAVL(), p[1],
                diccionario.rotaciones()));
        resultado.getStyleClass().add("leyenda-paso");
        resultado.setWrapText(true);
        resumen.setWrapText(true);

        VBox cabecera = new VBox(6, titulo, nota, barra, resultado, resumen);
        cabecera.setPadding(new Insets(0, 8, 8, 8));
        setTop(cabecera);

        SplitPane division = new SplitPane(
                panel("ABB común (degenerado)", lienzoABB, caminoABB),
                panel("AVL (equilibrado con rotaciones)", lienzoAVL, caminoAVL));
        division.setDividerPositions(0.4);
        setCenter(division);
        buscar();
    }

    private VBox panel(String titulo, Canvas lienzo, Label camino) {
        Label etiqueta = new Label(titulo);
        etiqueta.getStyleClass().add("subtitulo-traza");
        camino.setWrapText(true);
        Pane contenedor = new Pane(lienzo);
        contenedor.setMinSize(0, 0);
        contenedor.setPrefSize(500, 400);
        lienzo.widthProperty().bind(contenedor.widthProperty());
        lienzo.heightProperty().bind(contenedor.heightProperty());
        lienzo.widthProperty().addListener(o -> redibujar());
        lienzo.heightProperty().addListener(o -> redibujar());
        VBox caja = new VBox(6, etiqueta, camino, contenedor);
        caja.setPadding(new Insets(8));
        VBox.setVgrow(contenedor, Priority.ALWAYS);
        return caja;
    }

    private void buscar() {
        String texto = palabra.getText().strip().toLowerCase(Locale.ROOT);
        if (texto.isEmpty()) {
            return;
        }
        String significado = diccionario.traducir(texto);
        resultado.setText((significado == null ? "\"" + texto + "\" no está en el diccionario."
                : texto + " = " + significado + ".")
                + "  Comparaciones: ABB " + diccionario.comparacionesABB(texto) + ", AVL "
                + diccionario.comparacionesAVL(texto) + ".");
        recorridoABB = new ArrayList<>();
        recorrer(diccionario.abb().vista(), texto, NodoVista::clave, NodoVista::izquierdo, NodoVista::derecho,
                recorridoABB::add);
        recorridoAVL = new ArrayList<>();
        recorrer(diccionario.avl().vista(), texto, NodoAVL::clave, NodoAVL::izquierdo, NodoAVL::derecho,
                recorridoAVL::add);
        caminoABB.setText("Camino: " + String.join(" → ", recorridoABB));
        caminoAVL.setText("Camino: " + String.join(" → ", recorridoAVL));
        redibujar();
    }

    private static <N> void recorrer(N raiz, String texto, Function<N, Entrada> clave,
                                     Function<N, N> izq, Function<N, N> der,
                                     Consumer<String> visitar) {
        N actual = raiz;
        while (actual != null) {
            String p = clave.apply(actual).palabra();
            visitar.accept(p);
            int c = texto.compareTo(p);
            if (c == 0) {
                return;
            }
            actual = c < 0 ? izq.apply(actual) : der.apply(actual);
        }
    }

    private void redibujar() {
        String buscada = palabra.getText().strip().toLowerCase(Locale.ROOT);
        var gc = lienzoABB.getGraphicsContext2D();
        gc.setFill(Paleta.FONDO);
        gc.fillRect(0, 0, lienzoABB.getWidth(), lienzoABB.getHeight());
        DibujoArbol.dibujar(gc, diccionario.abb().vista(), NodoVista::izquierdo, NodoVista::derecho,
                v -> v.clave().palabra(), v -> color(v.clave().palabra(), buscada, recorridoABB),
                lienzoABB.getWidth(), lienzoABB.getHeight());
        gc = lienzoAVL.getGraphicsContext2D();
        gc.setFill(Paleta.FONDO);
        gc.fillRect(0, 0, lienzoAVL.getWidth(), lienzoAVL.getHeight());
        DibujoArbol.dibujar(gc, diccionario.avl().vista(), NodoAVL::izquierdo, NodoAVL::derecho,
                v -> v.clave().palabra(), v -> color(v.clave().palabra(), buscada, recorridoAVL),
                lienzoAVL.getWidth(), lienzoAVL.getHeight());
    }

    private static Color color(String p, String buscada, List<String> camino) {
        if (p.equals(buscada)) {
            return Paleta.FINALIZADO;
        }
        return camino.contains(p) ? Paleta.COMPARANDO : Color.web("#e3f2fd");
    }
}
