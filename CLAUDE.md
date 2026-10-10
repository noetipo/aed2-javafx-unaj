# CLAUDE.md

Repositorio docente del curso Algoritmos y Estructuras de Datos II (UNAJ, Juliaca - Perú).
Público: estudiantes de pregrado. Todo el código, comentarios y documentación van en **español**.

## Comandos

- `./mvnw clean javafx:run` ejecuta la app (clase principal `pe.edu.unaj.aed2.App`)
- `./mvnw test` corre las pruebas (excluye `@Tag("tarea")`)
- `./mvnw test -Dgroups=tarea -DexcludedGroups=none` corre solo las pruebas de tareas

Java 17 (`maven.compiler.release`), JavaFX 21 y JUnit 5. El proyecto es **modular** (`module-info.java`).

## Convenciones

- Un paquete por semana: `pe.edu.unaj.aed2.semanaXX` con `modelo`, `vista`, `caso` y `tarea`.
- `modelo/` es Java puro: **nunca** importar `javafx.*`. Los algoritmos animables exponen
  métodos `pasosXxx(...)` que devuelven `List<Estado>` con records inmutables; la vista solo dibuja.
- Los records de pasos implementan `comun.traza.PasoTrazable` (línea del pseudocódigo + variables). El modelo
  publica un `CodigoAlgoritmo` (p. ej. `QuickSort.CODIGO`): el pseudocódigo (`List<String>`, líneas desde 1) y el
  código Java como text block, donde cada línea termina en `//@n` para enlazarla con la línea n del pseudocódigo.
  Ese Java debe compilar y ser correcto (los estudiantes lo copian); `CodigoAlgoritmoTest` verifica los enlaces.
- Cada visualizador combina `PanelAnimacion` + `PanelTraza` (pestañas Pseudocódigo, Java y Traza) en un `SplitPane`,
  con `ControlesPaso` abajo. Los colores salen de `Paleta`, nunca hardcodeados.
- La teoría de cada semana es `docs/semana-XX/README.md`. Maven la empaqueta en `pe/edu/unaj/aed2/docs/`
  y `VistaTeoria` la muestra en la app (CommonMark convertido a controles JavaFX; no usar WebView: javafx.web necesita jdk.jsobject, que no existe desde JDK 24). Es una sola fuente para GitHub y la app.
- Los benchmarks usan `comun.benchmark.Cronometro` y `GraficoComplejidad`, y corren en un `Task`
  fuera del hilo de JavaFX.
- `tarea/` contiene métodos con `// TODO n:` que lanzan `UnsupportedOperationException`. Sus pruebas
  van en `src/test/.../semanaXX/TareaSemanaXXTest.java` con `@Tag("tarea")`. **No** publicar soluciones.
- Datos de ejemplo en `src/main/resources/pe/edu/unaj/aed2/semanaXX/*.csv`, con contexto local
  (Juliaca, Puno, mercados, rutas). Se leen con `getResourceAsStream`.
- Nombres de identificadores en español sin tildes ni ñ (`tamanio`, `insercion`); en textos visibles sí van tildes.

## Agregar una semana nueva

1. Crear `semanaXX/{modelo,vista,caso,tarea}` y sus pruebas.
2. Exportar los paquetes en `module-info.java`.
3. Registrar en `App.unidadI()` o `App.unidadII()`: `teoria("semana-XX")`, el visualizador y el caso.
4. Crear `docs/semana-XX/{README.md,laboratorio.md,tarea.md,img/}` y añadir la fila en la tabla del README.
