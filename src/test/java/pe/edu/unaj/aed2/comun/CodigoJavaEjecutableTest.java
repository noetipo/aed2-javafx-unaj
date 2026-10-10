package pe.edu.unaj.aed2.comun;

import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import pe.edu.unaj.aed2.comun.traza.CodigoAlgoritmo;
import pe.edu.unaj.aed2.semana01.modelo.Ordenamientos;
import pe.edu.unaj.aed2.semana02.caso.Escalinata;
import pe.edu.unaj.aed2.semana02.modelo.QuickSort;
import pe.edu.unaj.aed2.semana03.modelo.TablaHash;

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
                Escalinata.CODIGO_RECURSIVO, Escalinata.CODIGO_MEMO, TablaHash.CODIGO_INSERTAR);
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
        } else if (clase.startsWith("Escalinata")) {
            assertTrue(salida.contains("formas(10) = 89"), salida);
        } else if (clase.equals("TablaHash")) {
            assertTrue(salida.contains("buscar(\"JUL-347\") = true"), salida);
            assertTrue(salida.contains("buscar(\"PUN-999\") = false"), salida);
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
