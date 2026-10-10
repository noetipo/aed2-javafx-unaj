# Semana 02 · Recursividad y divide y vencerás

## Objetivos

- Diseñar funciones recursivas identificando el **caso base** y el **caso recursivo**.
- Leer el **árbol de llamadas** y relacionarlo con el costo del algoritmo.
- Aplicar **divide y vencerás** (quicksort) y plantear su recurrencia.
- Reconocer subproblemas repetidos y eliminarlos con **memorización**.

## 1. Recursividad

Una función recursiva se define en términos de sí misma sobre una entrada más pequeña:

```java
long formas(int n) {
    if (n <= 1) return 1;                 // caso base
    return formas(n - 1) + formas(n - 2); // caso recursivo
}
```

Toda recursión necesita:

1. **Caso base**, que se resuelve sin llamarse.
2. **Avance hacia el caso base**: cada llamada trabaja con una entrada menor.

Sin lo anterior aparece `StackOverflowError`, porque cada llamada ocupa un marco en la **pila de ejecución**.

## 2. Divide y vencerás

1. **Dividir** el problema en subproblemas del mismo tipo.
2. **Resolver** cada subproblema de forma recursiva.
3. **Combinar** las soluciones.

| Algoritmo | Dividir | Combinar | Recurrencia | Costo |
|---|---|---|---|---|
| Merge sort (semana 01) | Mitades exactas, O(1) | Mezcla, O(n) | T(n) = 2T(n/2) + n | O(n log n) |
| Quicksort | Partición, O(n) | Nada | promedio T(n) = 2T(n/2) + n | O(n log n) en promedio |
| Quicksort, peor caso | Partición desbalanceada | Nada | T(n) = T(n−1) + n | O(n²) |
| Búsqueda binaria | Mitad, O(1) | Nada | T(n) = T(n/2) + 1 | O(log n) |
| Potencia rápida (tarea) | e/2 | Elevar al cuadrado | T(e) = T(e/2) + 1 | O(log e) |

## 3. Quicksort con partición de Lomuto

1. Se toma como **pivote** el último elemento del rango.
2. Se recorre el rango y se envían a la izquierda los menores que el pivote (zona `[inicio, i)`).
3. El pivote se coloca en la posición `i`, que ya es su **posición final**.
4. Se ordenan recursivamente `[inicio, i−1]` y `[i+1, fin]`.

Con un arreglo **ya ordenado** el pivote siempre es el máximo, la partición deja n−1 elementos de un lado y la
profundidad de recursión llega a n−1. Por eso en la práctica se elige un pivote aleatorio o la mediana de tres.

## 4. Memorización

`formas(n)` recalcula los mismos subproblemas muchas veces: hace 2·formas(n) − 1 llamadas, que crecen como φⁿ ≈ 1,618ⁿ.
Si cada resultado se guarda en un `Map` la primera vez que se calcula, cada subproblema se resuelve una sola vez y
el número de llamadas baja a 2n − 1. Esta idea es la base de la programación dinámica.

## 5. En la aplicación

- **Teoría**: este documento.
- **Visualizador: quicksort**: las pestañas **Pseudocódigo** y **Traza** muestran la línea que se ejecuta y los
  valores de `inicio`, `fin`, `pivote`, `i` y `j` en cada paso. El fondo celeste marca el rango de la llamada actual, morado el pivote, amarillo la
  zona de menores, naranja el elemento comparado y verde los pivotes ya ubicados. Prueba la entrada
  "Ya ordenado (peor caso)" y observa la profundidad de recursión.
- **Caso: escalinata de Huaynaroque**: ejecuta `formas(n)` llamada por llamada sobre el árbol (gris = pendiente,
  naranja = en la pila, verde = retornó, amarillo = tomada de memo). La traza muestra la **pila de ejecución** y,
  con memorización, el contenido de `memo`. La pestaña **Crecimiento** grafica las llamadas para n = 1…25.

## 6. Código relevante

- `semana02/modelo/QuickSort.java`: `ordenar`, `particionar`, `profundidadMaxima` y `pasos`
- `semana02/modelo/LlamadaRecursiva.java`: nodo del árbol de llamadas
- `semana02/caso/Escalinata.java`: versión recursiva directa y memorizada, con sus árboles
- `comun/visual/DibujoArbol.java`: dibuja el árbol de llamadas
