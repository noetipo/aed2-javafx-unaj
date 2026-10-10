# Semana 04 · Árboles binarios de búsqueda

## Objetivos

- Definir un **árbol binario de búsqueda (ABB)** y reconocer su propiedad de orden.
- Programar recursivamente **insertar**, **eliminar** y los **recorridos**, y de forma iterativa **buscar**.
- Explicar los tres casos de la eliminación y el papel del **sucesor inorden**.
- Relacionar el costo de las operaciones con la **altura** del árbol, y no solo con n.

## 1. Vocabulario

| Término | Significado |
|---|---|
| Raíz | Nodo de arriba; no tiene padre |
| Hoja | Nodo sin hijos |
| Subárbol izquierdo / derecho | Árbol que cuelga de cada hijo |
| Profundidad de un nodo | Cantidad de aristas desde la raíz |
| Altura del árbol | Cantidad de niveles; aquí, árbol vacío = 0 y un solo nodo = 1 |

## 2. Propiedad de ABB

Para **todo** nodo r:

- todas las claves de su subárbol izquierdo son **menores** que r.clave;
- todas las de su subárbol derecho son **mayores**.

No basta con comparar un nodo con sus hijos directos: la condición vale para **todo** el subárbol (la tarea lo pide verificar).

## 3. Operaciones

| Operación | Idea | Costo |
|---|---|---|
| Buscar | Bajar a la izquierda o a la derecha según la comparación | O(h) |
| Insertar | Bajar igual que al buscar y colgar el nodo nuevo en el primer lugar vacío | O(h) |
| Eliminar | Buscar el nodo y resolver uno de tres casos | O(h) |
| Inorden | Izquierdo, raíz, derecho: entrega las claves **ordenadas** | O(n) |

Aquí **h es la altura**. En un árbol equilibrado h ≈ log₂ n; en uno degenerado h = n.

### Insertar recursivo: "devuelve la raíz del subárbol"

```java
Nodo insertar(Nodo r, int x) {
    if (r == null) return new Nodo(x);              // lugar vacío: aquí va x
    if (x < r.clave) r.izq = insertar(r.izq, x);   // reengancha lo que devuelva
    else if (x > r.clave) r.der = insertar(r.der, x);
    return r;
}
```

Cada llamada **devuelve** la raíz de su subárbol, y el padre la vuelve a asignar en `r.izq` o `r.der`. En casi todos
los niveles se reasigna el mismo nodo; solo en el nivel donde había `null` se engancha el nodo nuevo. Esta misma idea
hace que **eliminar** funcione.

### Eliminar: tres casos

| Caso | Qué se devuelve en lugar de r |
|---|---|
| r es una **hoja** | `null` |
| r tiene **un solo hijo** | ese hijo, con todo su subárbol |
| r tiene **dos hijos** | r se queda, pero toma la clave de su **sucesor** (el menor del subárbol derecho) y luego se elimina el sucesor original, que tiene a lo más un hijo |

## 4. La forma importa

Las mismas claves insertadas en distinto orden producen árboles distintos. Si llegan **ordenadas**, cada clave va a
la derecha de la anterior y el árbol degenera en una lista de altura n: buscar vuelve a ser O(n). Los árboles
**AVL** y **rojinegros** se reacomodan solos para mantener h = O(log n).

## 5. En la aplicación

- **Teoría**: este documento.
- **Visualizador: ABB**: escribe un número y pulsa **Insertar**, **Buscar** o **Eliminar**, o recorre con **Inorden**.
  Naranja = nodo actual, lila = llamadas que esperan en la pila (o camino de la búsqueda), rojo = nuevo o a eliminar,
  amarillo = sucesor, verde = encontrado o emitido.
  - **Se evalúa** explica cada comparación y, en la recursión, a qué llamada regresa el control y qué hace esa llamada
    con el nodo devuelto (lo reengancha en `r.izq` o `r.der`).
  - **Java** es una clase `ArbolBusqueda` completa con `buscar`, `insertar`, `eliminar`, `inorden` y `main`, lista
    para **Copiar código**.
- **Caso: diccionario quechua**: el mismo vocabulario insertado en orden alfabético (árbol degenerado de altura n)
  y en orden mezclado. Busca una palabra y compara las comparaciones de cada árbol.

## 6. Código relevante

- `semana04/modelo/ArbolBusqueda.java`: ABB genérico con operaciones normales y paso a paso
- `semana04/caso/DiccionarioQuechua.java`: dos árboles con el mismo contenido y distinta forma
- `comun/visual/DibujoArbol.java`: dibuja cualquier árbol binario y achica los nodos si no caben
