package pe.edu.unaj.aed2.semana06.caso;

import pe.edu.unaj.aed2.semana06.modelo.MonticuloMin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Caso práctico semana 06: un tópico de emergencias con un solo médico que atiende a un paciente
 * cada {@link #DURACION} minutos. Se simulan dos políticas sobre las mismas llegadas:
 * por orden de llegada (cola FIFO) y por prioridad de triaje (montículo de mínimos).
 */
public class TriajeEmergencias {

    public static final String RECURSO = "/pe/edu/unaj/aed2/semana06/emergencias.csv";
    public static final int DURACION = 5;

    /**
     * Paciente; el "menor" es el más urgente: primero la prioridad (1 = inmediata … 5 = no urgente)
     * y, a igual prioridad, el que llegó antes.
     */
    public record Paciente(int orden, int minuto, String nombre, int prioridad, String motivo)
            implements Comparable<Paciente> {
        @Override
        public int compareTo(Paciente otro) {
            if (prioridad != otro.prioridad) {
                return Integer.compare(prioridad, otro.prioridad);
            }
            return Integer.compare(orden, otro.orden);
        }

        @Override
        public String toString() {
            return "P" + prioridad + " " + nombre;
        }
    }

    /** Un paciente atendido y el estado de la cola justo antes de llamarlo. */
    public record Atencion(Paciente paciente, int minuto, List<Paciente> colaAntes) {
        public int espera() {
            return minuto - paciente.minuto();
        }
    }

    private final List<Paciente> llegadas;
    private final List<Atencion> porPrioridad;
    private final List<Atencion> porLlegada;

    public TriajeEmergencias(List<Paciente> llegadas) {
        this.llegadas = List.copyOf(llegadas);
        this.porPrioridad = simular(true);
        this.porLlegada = simular(false);
    }

    public static TriajeEmergencias desdeRecurso() {
        try (InputStream in = TriajeEmergencias.class.getResourceAsStream(RECURSO)) {
            if (in == null) {
                throw new IllegalStateException("No se encontró " + RECURSO);
            }
            BufferedReader lector = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            lector.readLine();
            List<Paciente> lista = new ArrayList<>();
            String linea;
            while ((linea = lector.readLine()) != null) {
                if (!linea.isBlank()) {
                    String[] c = linea.split(";");
                    lista.add(new Paciente(lista.size(), Integer.parseInt(c[0]), c[1], Integer.parseInt(c[2]), c[3]));
                }
            }
            return new TriajeEmergencias(lista);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Simula la atención: cuando el médico se libera, entran a la cola todos los que ya llegaron y se
     * llama al siguiente según la política.
     */
    private List<Atencion> simular(boolean conPrioridad) {
        MonticuloMin<Paciente> monticulo = new MonticuloMin<>();
        Deque<Paciente> fifo = new ArrayDeque<>();
        List<Atencion> atenciones = new ArrayList<>();
        int siguiente = 0;
        int libre = 0;
        while (atenciones.size() < llegadas.size()) {
            boolean vacia = conPrioridad ? monticulo.estaVacio() : fifo.isEmpty();
            int t = vacia ? Math.max(libre, llegadas.get(siguiente).minuto()) : libre;
            while (siguiente < llegadas.size() && llegadas.get(siguiente).minuto() <= t) {
                Paciente p = llegadas.get(siguiente++);
                if (conPrioridad) {
                    monticulo.insertar(p);
                } else {
                    fifo.addLast(p);
                }
            }
            List<Paciente> antes = conPrioridad ? monticulo.arreglo() : List.copyOf(fifo);
            Paciente atendido = conPrioridad ? monticulo.extraerMin() : fifo.removeFirst();
            atenciones.add(new Atencion(atendido, t, antes));
            libre = t + DURACION;
        }
        return atenciones;
    }

    public List<Paciente> llegadas() {
        return llegadas;
    }

    public List<Atencion> porPrioridad() {
        return porPrioridad;
    }

    public List<Atencion> porLlegada() {
        return porLlegada;
    }

    /** Espera promedio (minutos) de cada prioridad 1..5; NaN si no hubo pacientes de esa prioridad. */
    public static double[] esperaPromedio(List<Atencion> atenciones) {
        double[] suma = new double[6];
        int[] cuantos = new int[6];
        for (Atencion a : atenciones) {
            suma[a.paciente().prioridad()] += a.espera();
            cuantos[a.paciente().prioridad()]++;
        }
        double[] promedio = new double[6];
        for (int p = 1; p <= 5; p++) {
            promedio[p] = cuantos[p] == 0 ? Double.NaN : suma[p] / cuantos[p];
        }
        return promedio;
    }
}
