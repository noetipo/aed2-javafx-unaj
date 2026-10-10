package pe.edu.unaj.aed2.comun.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.commonmark.ext.gfm.tables.TableBlock;
import org.commonmark.ext.gfm.tables.TableCell;
import org.commonmark.ext.gfm.tables.TableHead;
import org.commonmark.ext.gfm.tables.TableRow;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.node.BlockQuote;
import org.commonmark.node.BulletList;
import org.commonmark.node.Code;
import org.commonmark.node.Emphasis;
import org.commonmark.node.FencedCodeBlock;
import org.commonmark.node.HardLineBreak;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlBlock;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Image;
import org.commonmark.node.IndentedCodeBlock;
import org.commonmark.node.Link;
import org.commonmark.node.ListItem;
import org.commonmark.node.OrderedList;
import org.commonmark.node.Paragraph;
import org.commonmark.node.SoftLineBreak;
import org.commonmark.node.StrongEmphasis;
import org.commonmark.node.ThematicBreak;
import org.commonmark.parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Muestra la teoría de una semana. Lee el mismo Markdown que se ve en GitHub
 * ({@code docs/semana-XX/README.md}, empaquetado por Maven en
 * {@code pe/edu/unaj/aed2/docs/}) y lo convierte a controles JavaFX.
 * <p>
 * No usa WebView a propósito: javafx.web depende del módulo jdk.jsobject,
 * que dejó de incluirse en el JDK desde la versión 24.
 */
public class VistaTeoria extends BorderPane {

    private static final String BASE_DOCS = "/pe/edu/unaj/aed2/docs/";
    private static final Parser PARSER = Parser.builder()
            .extensions(List.of(TablesExtension.create())).build();

    /** Cómo abrir enlaces externos (http...). La App lo configura con HostServices. */
    private static Consumer<String> abridorEnlaces = url -> { };

    private final VBox contenido = new VBox(12);
    private final ScrollPane desplazable = new ScrollPane();
    private URL actual;

    public static void setAbridorEnlaces(Consumer<String> abridor) {
        abridorEnlaces = abridor;
    }

    /** @param documento ruta dentro de docs/, por ejemplo {@code "semana-01/README.md"} */
    public VistaTeoria(String documento) {
        getStyleClass().add("teoria");
        contenido.getStyleClass().add("teoria-contenido");
        contenido.setPadding(new Insets(24, 32, 32, 32));
        contenido.setMaxWidth(900);

        BorderPane centrado = new BorderPane(contenido);
        BorderPane.setAlignment(contenido, Pos.TOP_CENTER);
        desplazable.setContent(centrado);
        desplazable.setFitToWidth(true);
        setCenter(desplazable);

        URL url = VistaTeoria.class.getResource(BASE_DOCS + documento);
        if (url == null) {
            contenido.getChildren().setAll(new Label("No se encontró la teoría " + documento));
        } else {
            cargar(url);
        }
    }

    private void cargar(URL url) {
        actual = url;
        contenido.getChildren().clear();
        try (InputStream in = url.openStream()) {
            String markdown = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            agregarBloques(PARSER.parse(markdown), contenido);
        } catch (IOException e) {
            contenido.getChildren().setAll(new Label("No se pudo leer " + url + ": " + e.getMessage()));
        }
        desplazable.setVvalue(0);
    }

    // ------------------------------------------------------------------
    // Bloques
    // ------------------------------------------------------------------

    private void agregarBloques(org.commonmark.node.Node padre, VBox destino) {
        for (org.commonmark.node.Node n = padre.getFirstChild(); n != null; n = n.getNext()) {
            Node bloque = bloque(n);
            if (bloque != null) {
                destino.getChildren().add(bloque);
            }
        }
    }

    private Node bloque(org.commonmark.node.Node n) {
        if (n instanceof Heading h) {
            TextFlow titulo = enLinea(h);
            titulo.getStyleClass().addAll("teoria-titulo", "teoria-h" + Math.min(h.getLevel(), 3));
            return titulo;
        }
        if (n instanceof Paragraph p) {
            // Un párrafo que solo contiene una imagen se muestra como imagen.
            if (p.getFirstChild() instanceof Image img && img.getNext() == null) {
                return imagen(img);
            }
            return enLinea(p);
        }
        if (n instanceof FencedCodeBlock c) {
            return codigo(c.getLiteral());
        }
        if (n instanceof IndentedCodeBlock c) {
            return codigo(c.getLiteral());
        }
        if (n instanceof BulletList || n instanceof OrderedList) {
            return lista(n);
        }
        if (n instanceof BlockQuote) {
            VBox cita = new VBox(8);
            cita.getStyleClass().add("teoria-cita");
            agregarBloques(n, cita);
            return cita;
        }
        if (n instanceof TableBlock) {
            return tabla(n);
        }
        if (n instanceof ThematicBreak) {
            return new Separator();
        }
        if (n instanceof HtmlBlock) {
            return null;
        }
        VBox otro = new VBox(8);
        agregarBloques(n, otro);
        return otro.getChildren().isEmpty() ? null : otro;
    }

    private Node codigo(String literal) {
        Label texto = new Label(literal.stripTrailing());
        texto.getStyleClass().add("teoria-codigo");
        texto.setMaxWidth(Double.MAX_VALUE);
        texto.setWrapText(false);
        ScrollPane barra = new ScrollPane(texto);
        barra.getStyleClass().add("teoria-codigo-marco");
        barra.setFitToHeight(true);
        barra.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        return barra;
    }

    private Node lista(org.commonmark.node.Node lista) {
        VBox caja = new VBox(4);
        caja.setPadding(new Insets(0, 0, 0, 8));
        int numero = lista instanceof OrderedList o && o.getMarkerStartNumber() != null
                ? o.getMarkerStartNumber() : 1;
        for (org.commonmark.node.Node item = lista.getFirstChild(); item != null; item = item.getNext()) {
            if (!(item instanceof ListItem)) {
                continue;
            }
            Label vineta = new Label(lista instanceof OrderedList ? (numero++) + "." : "•");
            vineta.getStyleClass().add("teoria-vineta");
            vineta.setMinWidth(22);
            VBox cuerpo = new VBox(4);
            agregarBloques(item, cuerpo);
            HBox.setHgrow(cuerpo, Priority.ALWAYS);
            HBox fila = new HBox(4, vineta, cuerpo);
            caja.getChildren().add(fila);
        }
        return caja;
    }

    private Node tabla(org.commonmark.node.Node tabla) {
        GridPane grilla = new GridPane();
        grilla.getStyleClass().add("teoria-tabla");
        int fila = 0;
        int columnas = 0;
        for (org.commonmark.node.Node seccion = tabla.getFirstChild(); seccion != null; seccion = seccion.getNext()) {
            boolean cabecera = seccion instanceof TableHead;
            for (org.commonmark.node.Node r = seccion.getFirstChild(); r != null; r = r.getNext()) {
                if (!(r instanceof TableRow)) {
                    continue;
                }
                int col = 0;
                for (org.commonmark.node.Node c = r.getFirstChild(); c != null; c = c.getNext()) {
                    if (!(c instanceof TableCell)) {
                        continue;
                    }
                    TextFlow texto = enLinea(c);
                    VBox celda = new VBox(texto);
                    celda.getStyleClass().add(cabecera ? "teoria-celda-cabecera"
                            : (fila % 2 == 0 ? "teoria-celda-par" : "teoria-celda"));
                    celda.setMaxWidth(Double.MAX_VALUE);
                    celda.setMaxHeight(Double.MAX_VALUE);
                    grilla.add(celda, col++, fila);
                }
                columnas = Math.max(columnas, col);
                fila++;
            }
        }
        for (int k = 0; k < columnas; k++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setHgrow(Priority.SOMETIMES);
            cc.setFillWidth(true);
            grilla.getColumnConstraints().add(cc);
        }
        return grilla;
    }

    private Node imagen(Image img) {
        try {
            ImageView vista = new ImageView(new javafx.scene.image.Image(
                    new URL(actual, img.getDestination()).toExternalForm(), true));
            vista.setPreserveRatio(true);
            vista.setFitWidth(820);
            return vista;
        } catch (MalformedURLException e) {
            return new Label("[imagen: " + img.getDestination() + "]");
        }
    }

    // ------------------------------------------------------------------
    // Texto en línea
    // ------------------------------------------------------------------

    private TextFlow enLinea(org.commonmark.node.Node padre) {
        List<Node> nodos = new ArrayList<>();
        agregarEnLinea(padre, nodos, List.of());
        TextFlow flujo = new TextFlow(nodos.toArray(Node[]::new));
        flujo.getStyleClass().add("teoria-parrafo");
        flujo.setLineSpacing(3);
        return flujo;
    }

    private void agregarEnLinea(org.commonmark.node.Node padre, List<Node> nodos, List<String> estilos) {
        for (org.commonmark.node.Node n = padre.getFirstChild(); n != null; n = n.getNext()) {
            if (n instanceof org.commonmark.node.Text t) {
                nodos.add(texto(t.getLiteral(), estilos));
            } else if (n instanceof SoftLineBreak) {
                nodos.add(texto(" ", estilos));
            } else if (n instanceof HardLineBreak) {
                nodos.add(texto("\n", estilos));
            } else if (n instanceof StrongEmphasis) {
                agregarEnLinea(n, nodos, con(estilos, "teoria-negrita"));
            } else if (n instanceof Emphasis) {
                agregarEnLinea(n, nodos, con(estilos, "teoria-cursiva"));
            } else if (n instanceof Code c) {
                Label codigo = new Label(c.getLiteral());
                codigo.getStyleClass().add("teoria-codigo-en-linea");
                nodos.add(codigo);
            } else if (n instanceof Link l) {
                nodos.add(enlace(l));
            } else if (n instanceof Image img) {
                nodos.add(imagen(img));
            } else if (n instanceof HtmlInline h) {
                nodos.add(texto(h.getLiteral(), estilos));
            } else {
                agregarEnLinea(n, nodos, estilos);
            }
        }
    }

    private Node enlace(Link l) {
        StringBuilder etiqueta = new StringBuilder();
        for (org.commonmark.node.Node n = l.getFirstChild(); n != null; n = n.getNext()) {
            if (n instanceof org.commonmark.node.Text t) {
                etiqueta.append(t.getLiteral());
            } else if (n instanceof Code c) {
                etiqueta.append(c.getLiteral());
            }
        }
        Hyperlink enlace = new Hyperlink(etiqueta.toString());
        enlace.getStyleClass().add("teoria-enlace");
        String destino = l.getDestination();
        enlace.setOnAction(e -> {
            try {
                URL url = new URL(actual, destino);
                if (destino.startsWith("http")) {
                    abridorEnlaces.accept(destino);
                } else if (url.getPath().endsWith(".md")) {
                    cargar(url);
                } else if (url.getPath().endsWith("/")) {
                    cargar(new URL(url, "README.md"));
                }
            } catch (MalformedURLException ex) {
                enlace.setDisable(true);
            }
        });
        return enlace;
    }

    private static Text texto(String literal, List<String> estilos) {
        Text t = new Text(literal);
        t.getStyleClass().add("teoria-texto");
        t.getStyleClass().addAll(estilos);
        return t;
    }

    private static List<String> con(List<String> estilos, String nuevo) {
        List<String> copia = new ArrayList<>(estilos);
        copia.add(nuevo);
        return copia;
    }
}
