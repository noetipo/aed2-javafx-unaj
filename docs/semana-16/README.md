# Semana 16 · Tries: árbol de prefijos

## Objetivos

- Conocer el **trie**, una estructura especializada en cadenas de texto.
- Insertar, buscar y **autocompletar** en un tiempo que depende del largo de la palabra, no de cuántas palabras hay.
- Comparar el trie con las estructuras del curso: tabla hash (semana 03) y árbol de búsqueda (semana 04).

## 1. La idea

En un trie, cada **arista** es una letra y cada **nodo** representa el prefijo que se lee desde la raíz hasta él. Las
palabras que empiezan igual **comparten** su comienzo:

```text
(raíz)
  +-- p
      +-- a
      |   +-- c - h - a         pacha
      |   +-- r - a             para
      +-- u
          +-- k - a             puka
          +-- m - a             puma
          +-- r - i - y         puriy
          +-- ñ - u - y         puñuy
```

Los nodos donde termina una palabra llevan una **marca**. Sin ella no se distinguiría "pum", que es solo un prefijo de
"puma", de una palabra real.

## 2. Operaciones

| Operación | Cómo | Costo (m = largo de la palabra) |
|---|---|---|
| Insertar | Bajar letra por letra; crear los nodos que falten; marcar el último | O(m) |
| Buscar | Bajar letra por letra; si falta un hijo, no está; al final, ¿tiene marca? | O(m) |
| Autocompletar | Bajar por el prefijo y **recolectar** todas las palabras de ese subárbol | O(m + tamaño del resultado) |

Si los hijos se guardan en un mapa ordenado (`TreeMap`), recolectar en preorden devuelve las palabras **en orden
alfabético** sin ordenar nada.

## 3. ¿Trie, hash o árbol de búsqueda?

| | Tabla hash | Árbol de búsqueda | Trie |
|---|---|---|---|
| Buscar una palabra exacta | O(m) en promedio | O(m · log n) | O(m) |
| Todas las palabras con un prefijo | Revisar todas | Bajar y recorrer un rango | **Bajar y recorrer un subárbol** |
| Orden alfabético | No | Sí | Sí |
| Memoria | Poca | Poca | Un nodo por prefijo distinto, puede ser mucha |

El trie brilla en el **autocompletado**, los correctores, el enrutamiento de direcciones IP y los diccionarios de
juegos de palabras.

## 4. El diccionario quechua

Las 38 palabras del diccionario de la semana 04 tienen 179 letras en total, pero el trie las guarda en **143 nodos**:
los comienzos comunes (p-, pu-, pa-, ya-, wa-, …) se guardan una sola vez.

Para autocompletar "pu" el trie visita 12 nodos: 2 para bajar y 10 del subárbol con puka, puma, puriy y puñuy. Una
búsqueda lineal revisaría las 38 palabras. La diferencia crece con el tamaño del diccionario: con 100 000 palabras,
el trie sigue bajando solo 2 niveles.

## 5. En la aplicación

- **Teoría**: este documento.
- **Visualizador: trie**: escribe palabras (por defecto puma, puka, puriy, puñuy, pacha, para).
  - **Insertar palabras**: se ve cada nodo creado (verde) o reutilizado (camino azul).
  - **Autocompletar**: baja por el prefijo y resalta en amarillo el subárbol con las sugerencias.
  - **Se evalúa** explica cada letra: si el hijo existe, si se crea y por qué hace falta la marca de fin de palabra.
  - **Java** es una clase `Trie` completa con `insertar`, `autocompletar` y un `main`.
- **Caso: autocompletar quechua**: al escribir, se sugieren palabras con su significado. Se dibuja el subárbol de la
  primera letra y se compara el trabajo del trie con una búsqueda lineal.

## 6. Código relevante

- `semana16/modelo/Trie.java`: trie genérico (palabra → dato), autocompletar y paso a paso
- `semana16/caso/AutocompletadoQuechua.java`: el diccionario de la semana 04 en un trie
- `semana16/vista/DibujoTrie.java`: dibujo de un trie a partir de sus prefijos
