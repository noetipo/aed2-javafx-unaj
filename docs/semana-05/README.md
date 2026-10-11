# Semana 05 · Árboles AVL

## Objetivos

- Explicar por qué un ABB puede degenerar y cómo un **AVL** lo evita.
- Calcular el **factor de equilibrio** de cada nodo y detectar un desequilibrio.
- Reconocer los **cuatro casos** (LL, RR, LR, RL) y resolverlos con **rotaciones** simples o dobles.
- Justificar que la altura de un AVL es O(log n).

## 1. El problema

En la semana 04 vimos que, si las claves llegan ordenadas, un ABB queda como una lista de altura n y buscar cuesta O(n).
Un **AVL** (Adelson-Velski y Landis, 1962) es un ABB que se **reequilibra solo** después de cada inserción.

## 2. Factor de equilibrio

Cada nodo guarda su **altura** (hoja = 1, árbol vacío = 0). Su **factor de equilibrio** es:

```
fe = altura(izquierdo) − altura(derecho)
```

Un árbol es AVL si **todos** sus nodos cumplen **|fe| ≤ 1**, es decir, fe ∈ {−1, 0, +1}.

## 3. Insertar en un AVL

1. Se baja como en un ABB común y se crea el nodo nuevo en un lugar vacío.
2. **A la vuelta de la recursión**, cada llamada recalcula la altura de su nodo y su fe.
3. El primer nodo, de abajo hacia arriba, con |fe| = 2 se corrige con una rotación. Después de corregirlo, el resto
   del camino ya queda equilibrado.

## 4. Los cuatro casos

Se miran dos cosas: hacia qué lado pesa el nodo desequilibrado y por qué lado de su hijo entró la clave nueva.

| Caso | fe del nodo | La clave entró… | Solución |
|---|---|---|---|
| **LL** | +2 | a la izquierda del hijo izquierdo | rotación simple a la **derecha** |
| **RR** | −2 | a la derecha del hijo derecho | rotación simple a la **izquierda** |
| **LR** | +2 | a la derecha del hijo izquierdo | izquierda sobre el hijo, luego derecha sobre el nodo |
| **RL** | −2 | a la izquierda del hijo derecho | derecha sobre el hijo, luego izquierda sobre el nodo |

### Rotación a la derecha (caso LL)

```
        y                x
       / \              / \
      x   C    →       A   y
     / \                  / \
    A   B                B   C
```

x sube, y baja a su derecha, y el subárbol B (que estaba entre x e y) cambia de padre. El **orden no cambia**: el
inorden sigue siendo A, x, B, y, C. Solo cambian dos enlaces y dos alturas, así que la rotación es **O(1)**.

En los casos LR y RL los tres nodos forman un zigzag; la primera rotación los pone en línea recta (un caso simple) y
la segunda lo resuelve.

## 5. ¿Cuánto mide un AVL?

Se demuestra que un AVL con n nodos tiene altura menor que **1,44 · log₂(n + 2)**. Por eso buscar e insertar cuestan
O(log n) siempre, aunque las claves lleguen ordenadas. Por ejemplo, 1023 claves insertadas en orden forman un árbol de
10 niveles, frente a los 1023 de un ABB común.

## 6. En la aplicación

- **Teoría**: este documento.
- **Visualizador: AVL**: junto a cada nodo aparece su fe; en rojo si |fe| = 2.
  - **Ejemplo: 4 rotaciones** inserta 30, 20, 10, 12, 18, 15 y 25, que producen en orden los casos LL, RR, LR y RL.
  - **Insertar 1…15 en orden** muestra que el resultado es un árbol perfecto de altura 4.
  - **Se evalúa** explica, a la vuelta de cada llamada, la nueva altura, el fe, cómo se elige el caso y qué enlaces
    cambia cada rotación.
  - **Java** es una clase `ArbolAVL` completa con `rotarDerecha`, `rotarIzquierda`, `insertar` y `main`.
- **Caso: diccionario con AVL**: las 38 palabras de la semana 04, insertadas en orden alfabético, dan un ABB de
  altura 38 y un AVL de altura 6.

## 7. Código relevante

- `semana05/modelo/ArbolAVL.java`: inserción con rotaciones, normal y paso a paso
- `semana05/caso/DiccionarioAVL.java`: el mismo diccionario en un ABB y en un AVL
