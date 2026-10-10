# AED II · JavaFX · UNAJ

Repositorio del curso **Algoritmos y Estructuras de Datos II** de la Universidad Nacional de Juliaca.
Cada semana trae:

- **Teoría** en `docs/semana-XX/README.md`
- **Visualizador JavaFX** para ver la estructura o el algoritmo paso a paso
- **Caso práctico** con datos de Juliaca y Puno
- **Tarea** con una plantilla de TODOs y pruebas automáticas

## Requisitos

| Herramienta | Versión |
|---|---|
| JDK | 17 o superior |
| Maven | no hace falta instalarlo: usa `./mvnw` (Windows: `mvnw.cmd`) |
| IDE | IntelliJ IDEA Community o VS Code con Extension Pack for Java |

JavaFX se descarga solo como dependencia de Maven.

## Clonar y ejecutar

```bash
# 1. Haz un fork en GitHub y clona TU fork
git clone https://github.com/<tu-usuario>/aed2-javafx-unaj.git
cd aed2-javafx-unaj

# 2. Ejecuta la aplicación
./mvnw clean javafx:run

# 3. Ejecuta las pruebas del curso
./mvnw test

# 4. Ejecuta las pruebas de las tareas (fallan hasta que completes los TODOs)
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

Si usas un IDE, ejecuta la clase `pe.edu.unaj.aed2.App`.

## Estructura

```
src/main/java/pe/edu/unaj/aed2/
├── App.java              ventana principal con menú lateral (Unidad I / Unidad II)
├── comun/
│   ├── visual/           PanelAnimacion, ControlesPaso, Paleta, DibujoArbol, DibujoGrafo
│   ├── benchmark/        Cronometro, GraficoComplejidad
│   └── mapa/             MapaSimplificado (coordenadas Juliaca/Puno)
└── semanaXX/
    ├── modelo/           estructura en Java puro, SIN JavaFX (se prueba con JUnit)
    ├── vista/            visualizador JavaFX
    ├── caso/             caso práctico (lógica + vista)
    └── tarea/            plantilla con TODOs para el estudiante
```

## Semanas

| Semana | Unidad | Tema | Docs |
|---|---|---|---|
| 01 | I | Complejidad algorítmica y ordenamientos elementales | [docs/semana-01](docs/semana-01/README.md) |

## Entregar tareas

Resumen (detalle en [docs/guia-git-estudiantes.md](docs/guia-git-estudiantes.md)):

```bash
git checkout -b tarea/s01-apellido
# ... completa los TODOs en semana01/tarea ...
./mvnw test -Dgroups=tarea -DexcludedGroups=none
git add src/main/java/pe/edu/unaj/aed2/semana01/tarea
git commit -m "Tarea semana 01 - Apellido Nombre"
git push -u origin tarea/s01-apellido
# luego abre un Pull Request hacia el repositorio del docente
```
