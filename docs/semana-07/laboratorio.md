# Laboratorio · Semana 07

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s07-apellido
```

## Paso 1: representar el grafo

1. A partir de `src/main/resources/pe/edu/unaj/aed2/semana07/carreteras.csv`, escribe en papel la **lista de
   adyacencia** de Juliaca, Puno y Juli.
2. ¿Cuál es el pueblo de mayor grado? ¿Cuántas casillas en 1 tendría la matriz de adyacencia?

## Paso 2: BFS a mano y en la aplicación

1. Haz en papel un BFS desde **Puno**: anota la cola después de sacar cada pueblo.
2. Abre **Unidad II → Semana 07 → Visualizador: recorridos**, elige Puno y pulsa **BFS**. Compara tu cola con la del
   visualizador.
3. ¿A cuántos tramos de Puno está Moho? ¿Por qué BFS garantiza que es el mínimo?

## Paso 3: DFS

1. Con el mismo origen pulsa **DFS**. Anota la pila de llamadas cuando se visita Desaguadero.
2. ¿Por qué DFS visita Yunguyo antes que Lampa, si Lampa está más cerca de Juliaca?
3. Compara el árbol de BFS con el de DFS. ¿Cuál es más "ancho" y cuál más "profundo"?

## Paso 4: el caso

1. Abre **Caso: bloqueo de carreteras** y bloquea Juliaca–Azángaro. ¿Queda aislado Azángaro? ¿Por qué?
2. Bloquea además Putina–Huancané. ¿Qué pasa ahora?
3. Explica por qué Juliaca–Puno es crítico y Juliaca–Lampa no.

## Paso 5: llevar el código a tu proyecto

1. Copia la clase de la pestaña **Java** a `Grafo.java` y ejecútala.
2. Agrega un método `List<String> camino(String s, String t)` que use BFS con padres, y pruébalo con Ayaviri →
   Desaguadero.

## Paso 6: guardar el trabajo

```bash
git add docs/semana-07/img
git commit -m "Lab S07: BFS, DFS y bloqueos"
git push -u origin lab/s07-apellido
```
