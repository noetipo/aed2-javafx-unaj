# Semana 06 · Montículos y colas de prioridad

## Objetivos

- Representar un **árbol completo en un arreglo** y moverse por él con índices.
- Implementar un **montículo de mínimos**: insertar (flotar) y extraer el mínimo (hundir) en O(log n).
- Usar un montículo como **cola de prioridad**, y conocer `PriorityQueue` de Java.
- Entender **heapsort**: construcción del montículo en O(n) y ordenamiento en O(n log n) sin memoria extra.

## 1. Cola de prioridad

Una cola normal (FIFO) atiende por **orden de llegada**. Una **cola de prioridad** atiende siempre al elemento
**más prioritario**, sin importar cuándo llegó: el paciente más grave, el proceso más urgente, el camino más corto
(lo usaremos en Dijkstra).

| Implementación | insertar | extraer el mínimo |
|---|---|---|
| Lista desordenada | O(1) | O(n) |
| Lista ordenada | O(n) | O(1) |
| **Montículo** | **O(log n)** | **O(log n)** |

## 2. El montículo

Un **montículo de mínimos** es un árbol binario que cumple dos reglas:

1. **Forma**: es **completo**: todos los niveles están llenos, salvo el último, que se llena de izquierda a derecha.
2. **Orden**: cada padre es **≤** que sus hijos. Por eso el mínimo está siempre en la raíz.

Como es completo, se guarda **en un arreglo, sin punteros**:

| Posición | Fórmula (desde 0) |
|---|---|
| hijo izquierdo de k | 2k + 1 |
| hijo derecho de k | 2k + 2 |
| padre de k | (k − 1) / 2 |

Ojo: un montículo **no** está ordenado. Solo garantiza que cada padre es menor que sus hijos, no que el hijo izquierdo
sea menor que el derecho.

## 3. Operaciones

**Insertar (flotar):** se coloca al final del arreglo (la primera hoja libre) y se intercambia con su padre mientras
el padre sea mayor.

**Extraer el mínimo (hundir):** se toma a[0], el **último** pasa a la raíz y se intercambia con su **hijo menor**
mientras sea mayor que él. Se elige el hijo menor para que, al subir, quede menor que su hermano.

Ambas recorren a lo más una rama: **O(log n)**.

## 4. Heapsort

1. **Construir** un montículo de **máximos** en el mismo arreglo: hundir cada posición desde n/2 − 1 hasta 0. Las
   hojas ya son montículos. Hacerlo de abajo hacia arriba cuesta **O(n)**, menos que insertar uno por uno.
2. Repetir n − 1 veces: intercambiar la raíz (el mayor) con el último elemento del montículo, que pasa a su **lugar
   definitivo**, achicar el montículo y hundir la nueva raíz.

| | Mejor | Promedio | Peor | Memoria extra | Estable |
|---|---|---|---|---|---|
| Heapsort | O(n log n) | O(n log n) | O(n log n) | O(1) | No |
| Merge sort (semana 01) | O(n log n) | O(n log n) | O(n log n) | O(n) | Sí |
| Quicksort (semana 02) | O(n log n) | O(n log n) | O(n²) | O(log n) | No |

## 5. En Java

`java.util.PriorityQueue` es un montículo de **mínimos**: `add` / `offer` insertan, `poll` extrae el mínimo y `peek`
lo consulta sin sacarlo. Para un montículo de máximos: `new PriorityQueue<>(Comparator.reverseOrder())`.

## 6. En la aplicación

- **Teoría**: este documento.
- **Visualizador: montículo y heapsort**: el montículo se dibuja **a la vez** como árbol y como arreglo, con los mismos
  índices. **Insertar** muestra cómo flota el valor y **Extraer mínimo** cómo se hunde la raíz. **Heapsort** ordena un
  arreglo aparte con un montículo de máximos; en verde, las posiciones que ya quedaron en su lugar.
  - **Se evalúa** explica cada comparación con su padre o con sus hijos (cuál es el hijo menor o mayor y por qué) y a
    dónde vuelve la ejecución (al while, al for de construcción o al de ordenamiento).
  - **Java** es una clase `Monticulo` completa con `insertar`, `extraerMin`, `heapsort` (que devuelve un arreglo
    ordenado) y `main`.
- **Caso: emergencias con triaje**: 24 llegadas ficticias a un tópico con un médico. Con una cola por orden de llegada,
  un paciente P1 espera en promedio 41,8 min; con la cola de prioridad, 3,0 min. Un P5 pasa de 23,0 a 63,0 min. La
  espera **total** es la misma (650 min): el montículo no acelera la atención, decide **a quién** se atiende primero.

## 7. Código relevante

- `semana06/modelo/MonticuloMin.java`: montículo genérico, normal y paso a paso
- `semana06/modelo/Heapsort.java`: heapsort con montículo de máximos
- `semana06/vista/DibujoMonticulo.java`: dibuja el árbol y el arreglo
- `semana06/caso/TriajeEmergencias.java`: simulación con las dos políticas
