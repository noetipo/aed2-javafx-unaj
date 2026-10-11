# Semana 09 · Árbol de expansión mínima: Kruskal y Prim

## Objetivos

- Reconocer cuándo un problema pide **conectar todo al menor costo**: un árbol de expansión mínima (MST, por sus siglas
  en inglés).
- Ejecutar a mano y programar **Kruskal** con **union-find** y **Prim** con una cola de prioridad.
- Justificar por qué una estrategia **voraz** da el óptimo (propiedad del corte y del ciclo).
- Distinguir el árbol de **expansión** mínima del árbol de **caminos** mínimos de Dijkstra (semana 08).

## 1. El problema

Un proyecto quiere llevar **fibra óptica** a los 14 pueblos de la red del curso. El cable puede ir por 19 posibles
tramos: las 15 carreteras de la semana 07 y 4 rutas más (Azángaro – Pucará, Azángaro – Ayaviri, Azángaro – Huancané
y Lampa – Ayaviri). Cada tramo tiene sus km
estimados (línea recta × 1,25, valores didácticos). ¿Por cuáles tramos se tiende cable para que **todos** los pueblos
queden conectados y se use el **menor total** de km?

- Si la solución tuviera un **ciclo**, se podría quitar un tramo del ciclo y todo seguiría conectado con menos cable.
  Por eso la solución es un **árbol**.
- Un árbol con n vértices tiene exactamente **n − 1 aristas**. Aquí, 13 tramos.
- Árbol de **expansión**: toca todos los vértices. **Mínima**: con el menor peso total entre todos los árboles posibles.

En la red del curso, el MST usa **541 km**. Con solo las carreteras (sin las 4 rutas extra) serían 577 km.

## 2. Dos reglas que hacen funcionar a los algoritmos voraces

- **Propiedad del corte**: si se separan los vértices en dos grupos, el tramo **más barato** que cruza de un grupo al otro
  está en algún MST. Todo árbol debe cruzar esa frontera con algún tramo, y cambiar cualquier otro por el más barato
  nunca empeora el total.
- **Propiedad del ciclo**: el tramo **más caro** de un ciclo no hace falta. Los demás tramos del ciclo ya conectan sus
  extremos.

Kruskal y Prim aplican estas reglas una y otra vez, sin volver atrás.

## 3. Kruskal: de menor a mayor

1. Ordenar todos los tramos por km.
2. Recorrerlos en ese orden. Un tramo se **acepta** si une dos pueblos que todavía están en **grupos distintos**. Si ya
   están en el mismo grupo, cerraría un ciclo y se **descarta**.
3. Terminar cuando el árbol tenga n − 1 tramos.

En la red del curso, Kruskal acepta 12 tramos seguidos (de 19 a 50 km). Después descarta 5 tramos (de 53 a 73 km)
porque ya conectan pueblos que están en el mismo grupo. Al final acepta Juli – Desaguadero (75 km): es la única forma de
llegar a Desaguadero.

### Union-find (conjuntos disjuntos)

¿Cómo saber rápido si dos pueblos ya están en el mismo grupo? Cada pueblo guarda un **jefe**, y el jefe de un grupo se
apunta a sí mismo.

```java
private String encontrar(String x) {
    jefe.putIfAbsent(x, x);                  // un pueblo nuevo es jefe de sí mismo
    if (!jefe.get(x).equals(x)) {
        jefe.put(x, encontrar(jefe.get(x))); // compresión de caminos
    }
    return jefe.get(x);
}
```

- `encontrar(x)` sube por los jefes hasta el representante del grupo.
- Unir dos grupos es una sola asignación: `jefe[encontrar(u)] = encontrar(v)`.
- La **compresión de caminos** hace que, después de buscar, todos apunten directo al representante.
- Si además se cuelga siempre el grupo **más pequeño** del más grande (unión por rango), cada operación cuesta
  prácticamente O(1).

| Paso de Kruskal | Costo |
|---|---|
| Ordenar los E tramos | O(E log E) |
| E consultas y uniones con union-find | casi O(E) |
| **Total** | **O(E log E)** |

## 4. Prim: crecer desde un pueblo

1. El árbol empieza con un solo pueblo s. Sus tramos entran a una **cola de prioridad** (el montículo de la semana 06).
2. Sacar el tramo más barato. Si llega a un pueblo que ya está en el árbol, se descarta (cerraría un ciclo). Si no, se
   acepta, el pueblo se conecta y sus tramos hacia pueblos que faltan entran a la cola.
3. Terminar cuando estén todos los pueblos.

En cada vuelta, Prim aplica la propiedad del corte: los pueblos ya conectados frente a los que faltan.

| Implementación | Costo |
|---|---|
| Cola de prioridad (visualizador) | O(E log V) |
| Arreglo `costo[]`, buscando el mínimo a mano (tarea) | O(V²) |

**Prim se parece mucho a Dijkstra.** Dijkstra compara `dist[v] + p` (la distancia **acumulada** desde el origen). Prim
compara solo `p` (lo que cuesta **ese** tramo).

## 5. ¿Kruskal o Prim?

| | Kruskal | Prim |
|---|---|---|
| Idea | Tramos globales de menor a mayor | Crece un solo árbol desde un pueblo |
| Estructura clave | Union-find | Cola de prioridad |
| Durante la ejecución hay | Un bosque (varios grupos) | Un solo árbol |
| Conviene en | Grafos dispersos, tramos ya en una lista | Grafos densos (versión con matriz) |

Los dos dan el **mismo total**, y Prim lo da desde cualquier pueblo de inicio: 541 km en la red del curso. Si hay km
repetidos, pueden elegir tramos distintos con el mismo total. Si todos los pesos son distintos, el MST es único.

## 6. Expansión mínima ≠ caminos mínimos

Ambos son árboles con n − 1 tramos, pero resuelven problemas distintos:

| Desde Juliaca | Cable total | Señal de Juliaca a Huancané |
|---|---|---|
| Árbol de **expansión** mínima (Kruskal) | **541 km** | 198 km (da la vuelta por Lampa, Pucará, Azángaro y Putina) |
| Árbol de **caminos** mínimos (Dijkstra) | 644 km | **65 km** (directo) |

- El MST minimiza el **total**: sirve para tender cable, tuberías o redes eléctricas.
- El árbol de Dijkstra minimiza la distancia **desde el origen** a cada pueblo: sirve para rutas y para la latencia.

## 7. Un detalle importante: un árbol no tiene respaldo

En un árbol, **cada tramo es un puente** (semana 07): si se corta uno, la red se parte en dos. Las redes reales agregan
algunos tramos extra para tener respaldo. Es decir, aceptan pagar un poco más que el MST a cambio de tolerancia a
fallas.

## 8. En la aplicación

- **Teoría**: este documento.
- **Visualizador: Kruskal y Prim**: elige el algoritmo (y en Prim, el pueblo de inicio) y avanza paso a paso.
  - **En Kruskal**, cada color es un grupo de union-find y se ve cómo los grupos se van fusionando. Abajo aparecen
    los tramos que faltan revisar, en orden.
  - **En Prim**, los pueblos conectados se ven en verde y los tramos en la cola en ámbar. Abajo aparece la cola de
    prioridad.
  - En ambos, el naranja marca el tramo que se acepta y el rojo uno que cerraría un ciclo.
  - **Se evalúa** explica qué consulta `encontrar(u) != encontrar(v)` y con qué representantes, por qué un tramo se
    descarta y por qué es seguro aceptar el más barato (propiedad del corte).
  - **Java** es una clase `ArbolExpansion` completa con union-find, `kruskal`, `prim` y `main`.
- **Caso: red de fibra óptica**: compara en el mapa el diseño de Kruskal (verde) con el de Dijkstra (morado).
  - Sobre cada pueblo se ven los km de cable que recorre la señal desde el centro elegido.
  - La tabla compara ambos diseños pueblo por pueblo, con un costo ficticio por km.

## 9. Código relevante

- `semana09/modelo/UnionFind.java`: conjuntos disjuntos con compresión de caminos
- `semana09/modelo/ArbolExpansion.java`: Kruskal y Prim, normales y paso a paso
- `semana09/caso/ProyectoFibra.java`: compara expansión mínima y caminos mínimos
- `src/main/resources/.../semana09/tendidos.csv`: los 19 posibles tramos
