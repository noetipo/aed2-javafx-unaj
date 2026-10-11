# Semana 08 · Caminos más cortos: el algoritmo de Dijkstra

## Objetivos

- Modelar un problema con un **grafo ponderado**: cada arista tiene un peso (km, minutos, costo).
- Entender por qué BFS no basta cuando las aristas pesan distinto.
- Ejecutar a mano y programar **Dijkstra** con una **cola de prioridad**, reutilizando el montículo de la semana 06.
- Reconstruir la ruta con los **padres** y conocer las limitaciones del algoritmo (pesos negativos).

## 1. Menos tramos no es menos kilómetros

BFS (semana 07) halla el camino con **menos aristas**. Si cada carretera mide distinto, eso no basta. En la red del curso,
de Juliaca a Putina hay dos rutas de **2 tramos**:

| Ruta | Tramos | km |
|---|---|---|
| Juliaca → Azángaro → Putina (la que da BFS) | 2 | 127 |
| Juliaca → Huancané → Putina (la que da Dijkstra) | 2 | 108 |

Los km se **estiman** como la distancia en línea recta × 1,25 (las carreteras no son rectas). Son valores didácticos,
no distancias oficiales.

## 2. Idea de Dijkstra

Cerrar los vértices **del más cercano al más lejano**:

1. `dist[s] = 0` y todas las demás `∞`.
2. Sacar el vértice **no cerrado** con menor `dist`. Su distancia ya es **definitiva**: cualquier desvío pasaría por un
   vértice que está igual de lejos o más lejos, y los pesos no son negativos.
3. **Relajar** sus aristas: si `dist[v] + p < dist[w]`, se encontró un camino mejor a w. Se actualiza `dist[w]` y se
   guarda `padre[w] = v`.
4. Repetir hasta cerrar todos los alcanzables.

## 3. La cola de prioridad

Para encontrar rápido "el no cerrado con menor dist" se usa un **montículo de mínimos** con pares `(dist, v)`. La cola de
Java no permite **bajar** la prioridad de un elemento, así que cuando `dist[w]` mejora se inserta un par **nuevo**. El
viejo queda en la cola y, cuando sale, se **descarta**: `si d > dist[v]: continuar`.

| Implementación | Costo |
|---|---|
| Arreglo, buscando el mínimo a mano (tarea) | O(V²) |
| Montículo (visualizador) | O((V + E) log V) |

En grafos dispersos, como una red vial, gana el montículo. En grafos muy densos, la versión con arreglo.

## 4. Reconstruir la ruta

`padre[w]` dice desde dónde se llegó a w con la distancia mínima. Para obtener la ruta a t se parte de t, se sigue
`padre` hasta s y se invierte la lista. Todas las aristas padre–hijo forman el **árbol de caminos mínimos**.

## 5. Limitación: pesos negativos

Dijkstra **asume pesos ≥ 0**. Con un peso negativo, un vértice ya cerrado podría mejorar después, y el algoritmo
fallaría. Para eso existe **Bellman–Ford**, que es más lento: O(V · E).

## 6. En la aplicación

- **Teoría**: este documento.
- **Visualizador: Dijkstra**: elige el origen y avanza. Sobre cada carretera se ven sus km y sobre cada pueblo su
  distancia actual (∞, provisional o definitiva). Abajo se ve la cola de prioridad. Gris = distancia provisional,
  oscuro = cerrado, verde = árbol de caminos mínimos, naranja = arista que mejora, rojo claro = arista que no mejora.
  - **Se evalúa** explica por qué cada extracción es definitiva, cada relajación ("¿pasar por Huancané es mejor que
    lo que ya sabía?") y cuándo se descarta una entrada vieja.
  - **Java** es una clase `Dijkstra` completa con `dijkstra` (devuelve las distancias), `camino` y `main`.
- **Caso: rutas de reparto**: una distribuidora en Juliaca. En el mapa, la ruta más corta (verde) frente a la de menos
  tramos (morada), y una tabla con todos los destinos y cuántos km se pierden por contar tramos.

## 7. Código relevante

- `semana08/modelo/GrafoPonderado.java`: grafo con pesos, Dijkstra normal y paso a paso
- `semana08/caso/PlanificadorRutas.java`: compara Dijkstra con BFS
- `semana07/vista/DibujoRed.java`: el mapa, ahora con etiquetas en las carreteras
