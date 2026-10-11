package pe.edu.unaj.aed2.comun;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.semana01.modelo.Ordenamientos;
import pe.edu.unaj.aed2.semana02.caso.Escalinata;
import pe.edu.unaj.aed2.semana02.modelo.QuickSort;
import pe.edu.unaj.aed2.semana03.modelo.TablaHash;
import pe.edu.unaj.aed2.semana04.modelo.ArbolBusqueda;
import pe.edu.unaj.aed2.semana05.modelo.ArbolAVL;
import pe.edu.unaj.aed2.semana06.modelo.MonticuloMin;
import pe.edu.unaj.aed2.semana07.modelo.Grafo;
import pe.edu.unaj.aed2.semana07.modelo.RedVial;
import pe.edu.unaj.aed2.semana08.modelo.GrafoPonderado;
import pe.edu.unaj.aed2.semana09.modelo.ArbolExpansion;
import pe.edu.unaj.aed2.semana10.modelo.GrafoDirigido;
import pe.edu.unaj.aed2.semana11.modelo.FloydWarshall;
import pe.edu.unaj.aed2.semana12.modelo.RedFlujo;
import pe.edu.unaj.aed2.semana13.modelo.NReinas;
import pe.edu.unaj.aed2.semana14.modelo.Mochila;
import pe.edu.unaj.aed2.semana15.modelo.Huffman;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * El código de la pestaña Java se copia tal cual a proyectos de estudiantes: aquí se compila
 * con javac y se ejecuta su main, exactamente como lo harían ellos.
 */
class CodigoJavaEjecutableTest {

    private static final Pattern CLASE = Pattern.compile("public class (\\w+)");

    static Stream<CodigoAlgoritmo> codigos() {
        return Stream.of(Ordenamientos.CODIGO_BURBUJA, Ordenamientos.CODIGO_INSERCION, QuickSort.CODIGO,
                Escalinata.CODIGO_RECURSIVO, Escalinata.CODIGO_MEMO, TablaHash.CODIGO_INSERTAR,
                ArbolBusqueda.CODIGO_INSERTAR, ArbolAVL.CODIGO_INSERTAR, MonticuloMin.CODIGO_INSERTAR,
                Grafo.CODIGO_BFS, GrafoPonderado.codigo(GrafoPonderado.desde(RedVial.cargar())),
                ArbolExpansion.codigoKruskal(GrafoPonderado.desde(ArbolExpansion.cargarTendidos())),
                GrafoDirigido.CODIGO_KAHN, FloydWarshall.codigo(GrafoPonderado.desde(RedVial.cargar())),
                RedFlujo.codigo(RedFlujo.cargarLeche()), NReinas.CODIGO,
                Mochila.CODIGO, Huffman.CODIGO);
    }

    @ParameterizedTest
    @MethodSource("codigos")
    void compilaYSeEjecuta(CodigoAlgoritmo codigo, @TempDir Path carpeta) throws Exception {
        String fuente = String.join("\n", codigo.java()) + "\n";
        Matcher m = CLASE.matcher(fuente);
        assertTrue(m.find(), "El código debe ser una clase pública completa");
        String clase = m.group(1);
        Files.writeString(carpeta.resolve(clase + ".java"), fuente);

        Path bin = Path.of(System.getProperty("java.home"), "bin");
        String compilacion = ejecutar(carpeta, bin.resolve("javac").toString(), clase + ".java");
        assertEquals("", compilacion.strip(), "javac reportó errores o advertencias");
        String salida = ejecutar(carpeta, bin.resolve("java").toString(), "-cp", ".", clase);

        if (fuente.contains("Ordenado: ")) {
            int[] original = numeros(salida, "Original: ");
            int[] ordenado = numeros(salida, "Ordenado: ");
            int[] esperado = original.clone();
            Arrays.sort(esperado);
            assertArrayEquals(esperado, ordenado, salida);
            if (clase.equals("Monticulo")) {
                assertTrue(salida.contains("Extraídos: 5 10 15 20 30 40 50"), salida);
            }
        } else if (clase.startsWith("Escalinata")) {
            assertTrue(salida.contains("formas(10) = 89"), salida);
        } else if (clase.equals("TablaHash")) {
            assertTrue(salida.contains("buscar(\"JUL-347\") = true"), salida);
            assertTrue(salida.contains("buscar(\"PUN-999\") = false"), salida);
        } else if (clase.equals("ArbolBusqueda")) {
            assertTrue(salida.contains("Inorden: [20, 30, 35, 40, 45, 50, 60, 65, 70, 80]"), salida);
            assertTrue(salida.contains("buscar(45) = true"), salida);
            assertTrue(salida.contains("buscar(55) = false"), salida);
            assertTrue(salida.contains("Sin 30: [20, 35, 40, 45, 50, 60, 65, 70, 80]"), salida);
        } else if (clase.equals("ArbolAVL")) {
            assertTrue(salida.contains("Inorden: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]"), salida);
            assertTrue(salida.contains("Altura: 4"), salida);
            assertTrue(salida.contains("Raíz: 8"), salida);
        } else if (clase.equals("Grafo")) {
            assertTrue(salida.contains("BFS desde Juliaca: {Juliaca=0, Puno=1, Lampa=1, Pucará=1, Azángaro=1,"
                    + " Huancané=1, Chucuito=2, Ayaviri=2, Putina=2, Moho=2, Ilave=3, Juli=4, Yunguyo=5,"
                    + " Desaguadero=5}"), salida);
            assertTrue(salida.contains("DFS desde Juliaca: [Juliaca, Puno, Chucuito, Ilave, Juli, Yunguyo,"
                    + " Desaguadero, Lampa, Pucará, Ayaviri, Azángaro, Putina, Huancané, Moho]"), salida);
        } else if (clase.equals("Dijkstra")) {
            assertTrue(salida.contains("Distancias desde Juliaca: {Azángaro=83, Huancané=65, Juliaca=0, Puno=49,"
                    + " Putina=108}"), salida);
            assertTrue(salida.contains("Camino a Putina: [Juliaca, Huancané, Putina] = 108 km"), salida);
        } else if (clase.equals("ArbolExpansion")) {
            assertTrue(salida.contains("Kruskal: [Putina-Huancané 43, Azángaro-Putina 44, Juliaca-Puno 49,"
                    + " Juliaca-Huancané 65] = 201 km"), salida);
            assertTrue(salida.contains("Prim desde Juliaca: [Juliaca-Puno 49, Juliaca-Huancané 65,"
                    + " Huancané-Putina 43, Putina-Azángaro 44] = 201 km"), salida);
        } else if (clase.equals("OrdenTopologico")) {
            assertTrue(salida.contains("Kahn: [Matemática, Programación, Cálculo, Discreta, POO, Estadística, AED I,"
                    + " AED II, IA]"), salida);
            assertTrue(salida.contains("DFS:  [Programación, POO, Matemática, Discreta, AED I, AED II, Cálculo,"
                    + " Estadística, IA]"), salida);
            assertTrue(salida.contains("Con AED II -> AED I: Hay un ciclo: no existe orden topológico"), salida);
        } else if (clase.equals("FloydWarshall")) {
            assertTrue(salida.contains("Juliaca: [0, 49, 83, 65, 108]"), salida);
            assertTrue(salida.contains("Azángaro: [83, 132, 0, 87, 44]"), salida);
            assertTrue(salida.contains("Putina: [108, 157, 44, 43, 0]"), salida);
            assertTrue(salida.contains("Puno -> Putina: [Puno, Juliaca, Huancané, Putina] = 157 km"), salida);
        } else if (clase.equals("FlujoMaximo")) {
            assertTrue(salida.contains("Flujo máximo Ayaviri -> Juliaca: 16 camiones por día"), salida);
            assertTrue(salida.contains("Azángaro -> Pucará: 3/6"), salida);
            assertTrue(salida.contains("Lampa -> Pucará: 6/6"), salida);
        } else if (clase.equals("NReinas")) {
            assertTrue(salida.contains("Columnas por fila: [0, 4, 7, 5, 2, 6, 1, 3]"), salida);
            assertTrue(salida.contains("Q . . . . . . . "), salida);
        } else if (clase.equals("Mochila")) {
            assertTrue(salida.contains("Ganancia máxima: 93 soles"), salida);
            assertTrue(salida.contains("Llevar: [Quinua, Chuño]"), salida);
        } else if (clase.equals("Huffman")) {
            assertTrue(salida.contains("Huffman: 51 bits; ASCII: 136 bits"), salida);
            assertTrue(salida.contains("a=11"), salida);
        } else {
            throw new AssertionError("Agrega aquí cómo verificar la salida de " + clase);
        }
    }

    private static int[] numeros(String salida, String prefijo) {
        String linea = salida.lines().filter(l -> l.startsWith(prefijo)).findFirst().orElseThrow();
        String dentro = linea.substring(linea.indexOf('[') + 1, linea.indexOf(']'));
        return Arrays.stream(dentro.split(",\\s*")).mapToInt(Integer::parseInt).toArray();
    }

    private static String ejecutar(Path carpeta, String... comando) throws IOException, InterruptedException {
        Process p = new ProcessBuilder(List.of(comando)).directory(carpeta.toFile())
                .redirectErrorStream(true).start();
        String salida = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        assertTrue(p.waitFor(60, TimeUnit.SECONDS), "Tiempo agotado: " + String.join(" ", comando));
        assertEquals(0, p.exitValue(), salida);
        return salida;
    }
}
