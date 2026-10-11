# Semana 11 · Todos los pares: Floyd–Warshall

## Objetivos

- Calcular los caminos más cortos entre **todos** los pares de vértices.
- Entender **Floyd–Warshall** como **programación dinámica**: la solución se arma en vueltas, y cada vuelta usa la
  tabla de la anterior.
- Reconstruir rutas con la matriz `sig` y evitar el **desborde** de `int` con el infinito.
- Usar la tabla de distancias para decidir **dónde ubicar** un servicio (centro y mediana de un grafo).

## 1. Una tabla de distancias

Los atlas de carreteras traen una tabla: fila = origen, columna = destino, casilla = km. Para armarla con Dijkstra
(semana 08) habría que ejecutarlo una vez **desde cada pueblo**. Floyd–Warshall la arma de una sola vez con tres `for`
anidados, y es muy corto de programar.

## 2. La idea: permitir una parada más en cada vuelta

Numeramos los pueblos 0, 1, …, n − 1. Sea `dist_k[i][j]` el camino más corto de i a j que **solo para** en los pueblos
0..k.

- Antes de empezar, sin paradas intermedias, `dist[i][j]` es la carretera directa: ∞ si no hay y 0 en la diagonal.
- En la vuelta k, el mejor camino de i a j que puede parar en k:
  - **no pasa por k**: vale lo mismo que antes, `dist[i][j]`;
  - **pasa por k**: va de i a k y de k a j, `dist[i][k] + dist[k][j]`.

```text
dist[i][j] ← min(dist[i][j], dist[i][k] + dist[k][j])
```

Esa es la **recurrencia** de programación dinámica. Al terminar las n vueltas se permite parar en cualquier pueblo:
la tabla tiene los mínimos.

Se puede usar **una sola matriz** porque en la vuelta k la fila k y la columna k no cambian. Ir a k pasando por k no
ayuda: `dist[i][k] + dist[k][k] = dist[i][k]`.

**Importante:** el `for` de k va **afuera**. Si se pone adentro, el algoritmo deja de ser correcto.

## 3. En la red del curso

El visualizador usa 7 pueblos del norte del lago. Juliaca → Putina **cambia dos veces**:

| Vuelta | Paradas permitidas | Juliaca → Putina |
|---|---|---|
| Antes de k = Azángaro | Juliaca, Puno, Lampa, Pucará | ∞ (no hay carretera directa) |
| k = Azángaro | … y Azángaro | 127 km (por Azángaro) |
| k = Huancané | todas | **108 km** (por Huancané) |

Es lo mismo que daba Dijkstra en la semana 08.

## 4. Reconstruir la ruta: la matriz `sig`

`sig[i][j]` es la **siguiente parada** al ir de i a j.

- Empieza como j si hay carretera directa.
- Cuando el camino mejora pasando por k, `sig[i][j] ← sig[i][k]`: para ir a j se sale por el mismo lado que para ir
  a k.

Para obtener la ruta, se parte de i y se salta a `sig[i][j]` hasta llegar a j.

## 5. El infinito y el desborde

Si ∞ se representa con `Integer.MAX_VALUE`, la suma `dist[i][k] + dist[k][j]` **desborda** y da un número negativo.
Entonces el `if` creería que encontró un camino buenísimo. Por eso el código pregunta `!= INF` **antes** de sumar. Otra
opción es usar un infinito "grande pero seguro", como `Integer.MAX_VALUE / 2`.

## 6. Costo y comparación

| | Floyd–Warshall | Dijkstra desde cada vértice |
|---|---|---|
| Tiempo | **O(n³)** | O(n · (n + m) log n) |
| Memoria | O(n²) | O(n + m) por ejecución |
| Pesos negativos | Funciona (sin ciclos negativos) | No |
| Código | Tres `for` | Cola de prioridad |

En grafos densos o pequeños (hasta unos cientos de vértices), Floyd–Warshall es simple y rápido. En redes grandes y
dispersas conviene Dijkstra desde cada origen. Con pesos negativos, un ciclo negativo se detecta porque
`dist[i][i] < 0`.

La misma idea con booleanos (`alcanza[i][j] = alcanza[i][j] || (alcanza[i][k] && alcanza[k][j])`) es el **algoritmo de
Warshall**. Calcula la **clausura transitiva**: por ejemplo, si un curso es prerrequisito **indirecto** de otro
(semana 10).

## 7. ¿Dónde poner el almacén? Centro y mediana

Con la tabla completa se puede evaluar **cada** ubicación posible:

- **Excentricidad** de v: los km a su pueblo más lejano. El **centro** del grafo minimiza la excentricidad: que el
  peor atendido quede lo más cerca posible. Es el criterio de emergencias.
- **Suma de distancias** de v. La **mediana** la minimiza: el menor recorrido total. Es el criterio de costo.

En la red del curso, con un almacén:

| Criterio | Gana | Pueblo más lejano | Recorrido total |
|---|---|---|---|
| Centro | **Puno** | 166 km | 1452 km |
| Mediana | **Juliaca** | 215 km | 1354 km |

Con **dos** almacenes, Juliaca + Juli gana con ambos criterios: nadie queda a más de 109 km. Evaluar todos los pares
de pueblos (91 opciones) es inmediato porque la tabla ya está calculada.

## 8. En la aplicación

- **Teoría**: este documento.
- **Visualizador: Floyd–Warshall**: muestra las matrices `dist` y `sig` de 7 pueblos.
  - La fila y la columna de k aparecen en azul; `dist[i][k]` y `dist[k][j]` en naranja; la casilla examinada en
    amarillo (verde si mejora).
  - Los botones **k anterior / k siguiente** saltan entre vueltas.
  - **Se evalúa** explica cada comparación ("¿ir de Puno a Lampa pasando por Juliaca es mejor?") y por qué se pregunta
    por ∞ antes de sumar.
  - **Java** es una clase `FloydWarshall` completa con `floyd`, `camino` y `main`.
- **Caso: almacén para heladas**: elige 1 o 2 almacenes y el criterio.
  - El mapa muestra las rutas desde cada almacén y marca en rojo al pueblo peor atendido.
  - Las pestañas muestran el ranking de ubicaciones y la tabla de distancias de los 14 pueblos.

## 9. Código relevante

- `semana11/modelo/FloydWarshall.java`: Floyd–Warshall, caminos, excentricidad y paso a paso
- `semana11/caso/UbicacionAlmacen.java`: evalúa todas las ubicaciones con 1 o 2 almacenes
