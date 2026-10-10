package pe.edu.unaj.aed2.comun.mapa;

import java.util.List;
import java.util.Optional;

/**
 * Lugares de referencia de Juliaca y Puno con coordenadas geográficas
 * aproximadas (solo con fines didácticos) y una proyección simple a
 * coordenadas de pantalla. No depende de JavaFX: lo pueden usar el modelo
 * (p. ej. para pesos de aristas en km) y la vista (para dibujar).
 */
public final class MapaSimplificado {

    /** Un punto del mapa. Latitud y longitud en grados decimales. */
    public record Lugar(String id, String nombre, double latitud, double longitud) {
    }

    public static final List<Lugar> LUGARES = List.of(
            new Lugar("JUL", "Plaza de Armas de Juliaca", -15.4997, -70.1333),
            new Lugar("AER", "Aeropuerto Inca Manco Cápac", -15.4671, -70.1581),
            new Lugar("UNAJ", "Universidad Nacional de Juliaca", -15.4870, -70.1230),
            new Lugar("TPJ", "Terminal Terrestre de Juliaca", -15.5040, -70.1290),
            new Lugar("CAR", "Caracoto", -15.5660, -70.1000),
            new Lugar("PAU", "Paucarcolla", -15.7590, -70.0590),
            new Lugar("PUN", "Plaza de Armas de Puno", -15.8402, -70.0219),
            new Lugar("UNA", "Universidad Nacional del Altiplano", -15.8240, -70.0160),
            new Lugar("MUE", "Muelle de Puno", -15.8340, -70.0110)
    );

    private static final double RADIO_TIERRA_KM = 6371.0;

    private MapaSimplificado() {
    }

    public static Optional<Lugar> buscar(String id) {
        return LUGARES.stream().filter(l -> l.id().equalsIgnoreCase(id)).findFirst();
    }

    /** Distancia en línea recta (fórmula del haversine) en kilómetros. */
    public static double distanciaKm(Lugar a, Lugar b) {
        double dLat = Math.toRadians(b.latitud() - a.latitud());
        double dLon = Math.toRadians(b.longitud() - a.longitud());
        double h = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(a.latitud())) * Math.cos(Math.toRadians(b.latitud()))
                * Math.pow(Math.sin(dLon / 2), 2);
        return 2 * RADIO_TIERRA_KM * Math.asin(Math.sqrt(h));
    }

    /**
     * Proyecta los lugares a un rectángulo de {@code ancho x alto} píxeles
     * conservando la proporción. Retorna {x, y} para el lugar indicado.
     */
    public static double[] aPantalla(Lugar lugar, List<Lugar> lugares, double ancho, double alto, double margen) {
        double minLat = lugares.stream().mapToDouble(Lugar::latitud).min().orElse(0);
        double maxLat = lugares.stream().mapToDouble(Lugar::latitud).max().orElse(1);
        double minLon = lugares.stream().mapToDouble(Lugar::longitud).min().orElse(0);
        double maxLon = lugares.stream().mapToDouble(Lugar::longitud).max().orElse(1);
        double rangoLat = Math.max(1e-9, maxLat - minLat);
        double rangoLon = Math.max(1e-9, maxLon - minLon);
        double escala = Math.min((ancho - 2 * margen) / rangoLon, (alto - 2 * margen) / rangoLat);
        double x = margen + (lugar.longitud() - minLon) * escala;
        double y = margen + (maxLat - lugar.latitud()) * escala; // el norte arriba
        return new double[]{x, y};
    }
}
