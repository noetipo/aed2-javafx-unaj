package pe.edu.unaj.aed2.semana14.caso;

import pe.edu.unaj.aed2.semana14.modelo.Mochila;
import pe.edu.unaj.aed2.semana14.modelo.Mochila.Producto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Caso práctico semana 14: presupuesto participativo. La municipalidad tiene un presupuesto y una lista de proyectos
 * con su costo y cuántas personas benefician; cada proyecto se hace entero o no se hace. Es una mochila 0/1: costo =
 * peso, beneficiarios = valor. Datos ficticios. Los costos son múltiplos de 10 mil soles (la tabla usa decenas).
 */
public class PresupuestoParticipativo {

    /** Una forma de elegir proyectos. */
    public record Seleccion(String regla, List<Integer> elegidos, int costo, int beneficiarios) {
    }

    private static final String RECURSO = "/pe/edu/unaj/aed2/semana14/proyectos.csv";
    /** Unidad de la tabla: 10 mil soles. */
    public static final int UNIDAD = 10;

    private final List<Producto> proyectos;

    public PresupuestoParticipativo(List<Producto> proyectos) {
        this.proyectos = List.copyOf(proyectos);
    }

    /** Proyectos con el costo en miles de soles. */
    public static PresupuestoParticipativo cargar() {
        List<Producto> lista = new ArrayList<>();
        try (InputStream in = PresupuestoParticipativo.class.getResourceAsStream(RECURSO)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró " + RECURSO);
            }
            BufferedReader lector = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            lector.readLine();
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.isBlank()) {
                    String[] c = linea.split(";");
                    lista.add(new Producto(c[0], Integer.parseInt(c[1]), Integer.parseInt(c[2])));
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return new PresupuestoParticipativo(lista);
    }

    public List<Producto> proyectos() {
        return proyectos;
    }

    private List<Producto> enDecenas() {
        return proyectos.stream().map(p -> new Producto(p.nombre(), p.peso() / UNIDAD, p.valor())).toList();
    }

    private Seleccion seleccion(String regla, List<Integer> elegidos) {
        List<Integer> orden = new ArrayList<>(elegidos);
        orden.sort(null);
        return new Seleccion(regla, orden, Mochila.peso(proyectos, orden), Mochila.valor(proyectos, orden));
    }

    /** Programación dinámica: la mayor cantidad de beneficiarios sin pasar el presupuesto (en miles). */
    public Seleccion optimo(int presupuesto) {
        return seleccion("Programación dinámica (óptimo)", Mochila.resolver(enDecenas(), presupuesto / UNIDAD)
                .elegidos());
    }

    /** Regla voraz: primero los de más beneficiarios por sol. */
    public Seleccion vorazPorSol(int presupuesto) {
        return seleccion("Voraz: más beneficiarios por sol", Mochila.voraz(proyectos, presupuesto).elegidos());
    }

    /** Regla voraz ingenua: primero los proyectos con más beneficiarios. */
    public Seleccion vorazPorBeneficiarios(int presupuesto) {
        List<Integer> orden = new ArrayList<>(IntStream.range(0, proyectos.size()).boxed().toList());
        orden.sort(Comparator.comparingInt(k -> -proyectos.get(k).valor()));
        int libre = presupuesto;
        List<Integer> elegidos = new ArrayList<>();
        for (int k : orden) {
            if (proyectos.get(k).peso() <= libre) {
                libre -= proyectos.get(k).peso();
                elegidos.add(k);
            }
        }
        return seleccion("Voraz: más beneficiarios primero", elegidos);
    }
}
