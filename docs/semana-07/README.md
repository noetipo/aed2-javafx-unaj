# Semana 07 · Grafos: representación y recorridos (BFS y DFS)

## Objetivos

- Modelar un problema con un **grafo**: vértices, aristas, grado, camino, ciclo y conexidad.
- Comparar la **matriz** y la **lista de adyacencia**.
- Programar los recorridos **BFS** (en anchura, con una cola) y **DFS** (en profundidad, recursivo).
- Usar BFS para hallar el **camino con menos aristas** y detectar vértices **inalcanzables**.

## 1. Vocabulario

Un **grafo** G = (V, E) tiene un conjunto de **vértices** V y de **aristas** E que unen pares de vértices.

| Término | Significado | En la red vial |
|---|---|---|
| Vértice | Un elemento | Un pueblo |
| Arista | Relación entre dos vértices | Una carretera |
| No dirigido | La arista sirve en ambos sentidos | Se viaja de ida y vuelta |
| Grado de v | Cantidad de aristas de v | Carreteras que salen del pueblo |
| Camino | Secuencia de vértices unidos por aristas | Una ruta |
| Ciclo | Camino que vuelve al inicio sin repetir aristas | Ruta circular |
| Conexo | Hay camino entre cualquier par | Ningún pueblo aislado |

## 2. Representaciones

| | Matriz de adyacencia | Lista de adyacencia |
|---|---|---|
| Idea | Tabla V × V; `m[u][v] = 1` si hay arista | Para cada vértice, la lista de sus vecinos |
| Memoria | O(V²) | O(V + E) |
| ¿Existe u–v? | O(1) | O(grado de u) |
| Recorrer los vecinos de u | O(V) | O(grado de u) |
| Conviene si… | El grafo es denso | El grafo es disperso (lo común) |

En la red vial hay 14 pueblos y 15 carreteras: la matriz tendría 196 casillas, casi todas en 0. Por eso usamos una
**lista de adyacencia**: `Map<String, List<String>>`.

## 3. BFS: recorrido en anchura

Visita por **capas**: primero el origen, luego todos sus vecinos (distancia 1), luego los vecinos de estos (distancia 2)…
Usa una **cola** (FIFO). Al descubrir un vértice se le asigna `dist = dist[padre] + 1`.

- En un grafo **sin pesos**, BFS encuentra el camino con **menos aristas**.
- Las aristas por las que se descubrió cada vértice forman el **árbol de BFS**.
- Costo: O(V + E), porque cada vértice entra una vez a la cola y cada lista de vecinos se recorre una vez.

## 4. DFS: recorrido en profundidad

Avanza lo más **lejos** posible por un camino y solo retrocede cuando ya no hay vecinos nuevos. La versión recursiva usa la
**pila de llamadas** para recordar por dónde volver, igual que en la semana 02. También cuesta O(V + E).

| | BFS | DFS |
|---|---|---|
| Estructura | Cola | Pila (recursión) |
| Forma de avanzar | Por capas | Por ramas |
| Sirve para | Caminos con menos aristas, niveles | Ciclos, componentes, orden topológico |

## 5. En la aplicación

- **Teoría**: este documento.
- **Visualizador: recorridos**: elige un pueblo de origen y pulsa **BFS** o **DFS** sobre una red vial **simplificada y
  didáctica** de la región Puno, que no incluye todas las carreteras. Blanco = sin descubrir, gris = en la cola o en la
  pila, oscuro = terminado, verde = árbol del recorrido. Abajo se ven la cola o la pila de llamadas y el orden de visita.
  - **Se evalúa** explica qué vértice sale de la cola, por qué cada vecino se encola o se ignora y, en DFS, a qué llamada
    regresa el control.
  - **Java** es una clase `Grafo` completa con `bfs` (devuelve las distancias), `dfs` (devuelve el orden) y `main`.
- **Caso: bloqueo de carreteras**: marca tramos bloqueados y BFS calcula qué pueblos quedan aislados de Juliaca y la ruta
  con menos tramos hacia un destino. Los tramos **críticos** son los que, bloqueados solos, ya aíslan pueblos (los
  "puentes" del grafo).

## 6. Código relevante

- `semana07/modelo/Grafo.java`: lista de adyacencia, BFS, DFS y camino mínimo en aristas
- `semana07/modelo/RedVial.java`: carga `pueblos.csv` y `carreteras.csv`
- `semana07/caso/BloqueoVial.java`: alcanzables, aislados y tramos críticos
