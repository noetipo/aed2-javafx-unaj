package pe.edu.unaj.aed2.comun.visual;

import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Muestra la teoría de una semana. Lee el mismo Markdown que se ve en GitHub
 * ({@code docs/semana-XX/README.md}, empaquetado por Maven en
 * {@code pe/edu/unaj/aed2/docs/}) y lo renderiza como HTML en un WebView.
 */
public class VistaTeoria extends BorderPane {

    private static final String BASE_DOCS = "/pe/edu/unaj/aed2/docs/";

    private static final String ESTILO = """
            body { font-family: -apple-system, 'Segoe UI', Roboto, sans-serif; font-size: 15px;
                   line-height: 1.6; color: #212121; max-width: 880px; margin: 0 auto; padding: 24px 32px; }
            h1 { color: #8b1c1c; border-bottom: 2px solid #8b1c1c; padding-bottom: 6px; }
            h2 { color: #263238; border-bottom: 1px solid #cfd8dc; padding-bottom: 4px; margin-top: 28px; }
            table { border-collapse: collapse; margin: 12px 0; width: 100%; }
            th, td { border: 1px solid #cfd8dc; padding: 6px 10px; text-align: left; vertical-align: top; }
            th { background: #eceff1; }
            tr:nth-child(even) td { background: #fafafa; }
            code { background: #eceff1; padding: 1px 5px; border-radius: 3px;
                   font-family: 'JetBrains Mono', Menlo, Consolas, monospace; font-size: 13px; }
            pre { background: #263238; color: #eceff1; padding: 12px 16px; border-radius: 6px; overflow-x: auto; }
            pre code { background: none; color: inherit; padding: 0; }
            blockquote { border-left: 4px solid #90caf9; margin: 12px 0; padding: 4px 14px; color: #455a64;
                         background: #f5f9fd; }
            img { max-width: 100%; }
            """;

    private static final Parser PARSER = Parser.builder()
            .extensions(List.of(TablesExtension.create())).build();
    private static final HtmlRenderer RENDERER = HtmlRenderer.builder()
            .extensions(List.of(TablesExtension.create())).build();

    private final WebView web = new WebView();

    /** @param documento ruta dentro de docs/, por ejemplo {@code "semana-01/README.md"} */
    public VistaTeoria(String documento) {
        setCenter(web);
        WebEngine motor = web.getEngine();
        // Los enlaces a otros .md (p. ej. la guía de Git) también se muestran renderizados.
        motor.locationProperty().addListener((o, anterior, nueva) -> {
            if (nueva != null && nueva.endsWith(".md")) {
                try {
                    cargar(new URL(nueva));
                } catch (IOException e) {
                    motor.loadContent("<p>No se pudo abrir " + nueva + "</p>");
                }
            }
        });
        URL url = VistaTeoria.class.getResource(BASE_DOCS + documento);
        if (url == null) {
            motor.loadContent("<p>No se encontró la teoría <code>" + documento + "</code>.</p>");
            return;
        }
        try {
            cargar(url);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void cargar(URL url) throws IOException {
        String markdown;
        try (InputStream in = url.openStream()) {
            markdown = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        String cuerpo = RENDERER.render(PARSER.parse(markdown));
        String base = url.toExternalForm().substring(0, url.toExternalForm().lastIndexOf('/') + 1);
        web.getEngine().loadContent("<!DOCTYPE html><html><head><meta charset='utf-8'>"
                + "<base href='" + base + "'><style>" + ESTILO + "</style></head><body>"
                + cuerpo + "</body></html>");
    }
}
