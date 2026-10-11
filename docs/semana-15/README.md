# Semana 15 · Algoritmos voraces: códigos de Huffman

## Objetivos

- Entender la estrategia **voraz**: tomar en cada paso la mejor decisión local, sin volver atrás.
- Saber **cuándo** una regla voraz da el óptimo y cuándo no.
- Construir un **código de Huffman** con una cola de prioridad y un árbol binario.
- Comprimir y descomprimir texto con un código **libre de prefijos**.

## 1. Voraz: decidir y no volver

Un algoritmo voraz arma la solución por partes, eligiendo siempre lo que hoy parece mejor. Es rápido y simple, pero
**no siempre** da el óptimo:

| Problema | ¿La regla voraz es óptima? |
|---|---|
| Dijkstra (semana 08), Prim y Kruskal (semana 09) | Sí |
| Huffman (esta semana) | Sí |
| Selección de actividades por "la que termina primero" (tarea) | Sí |
| Mochila **fraccionaria** por valor/peso (tarea) | Sí |
| Mochila **0/1** por valor/peso (semana 14) | No: 90 frente a 93 |
| Horario de exámenes "primer día libre" (semana 13) | No: 5 días frente a 3 |

Para confiar en una regla voraz hay que **demostrar** que la elección local nunca impide llegar al óptimo. Suele
hacerse con un argumento de intercambio: "si una solución óptima no hiciera esta elección, puedo cambiarla por esta
sin empeorar".

## 2. El problema de comprimir

En ASCII cada carácter ocupa **8 bits**, sea frecuente o raro. En "pachamama paqarin" la letra **a** aparece 6 veces
de 17. Si a ella le damos un código de 2 bits y a las letras raras uno de 4, el texto ocupa menos.

Con códigos de **distinto largo** hay un problema: ¿dónde termina cada código? Si `a = 0` y `b = 01`, al leer "01" no se
sabe si es "a" seguido de algo o es "b". La solución es un código **libre de prefijos**: ningún código es el comienzo
de otro. Así se decodifica leyendo bit por bit, sin separadores.

## 3. El algoritmo de Huffman

1. Contar la frecuencia de cada carácter.
2. Crear una **hoja** por carácter y meterlas todas en una **cola de prioridad** por frecuencia (el montículo de la
   semana 06).
3. Mientras haya más de un árbol:
   - sacar los **dos menos frecuentes** (la decisión voraz);
   - unirlos bajo un nodo nuevo cuya frecuencia es la suma, y devolverlo a la cola.
4. El árbol que queda es el árbol de Huffman. El **código** de cada carácter es el camino desde la raíz: izquierda = 0,
   derecha = 1.

Los caracteres solo están en las **hojas**, así que ningún código es prefijo de otro. Los raros se unen primero y quedan
abajo, con códigos largos; los frecuentes se unen al final y quedan cerca de la raíz.

Costo: con k caracteres distintos hay k − 1 uniones, cada una O(log k) en el montículo: **O(k log k)**, más O(n) para
contar y codificar el texto.

**¿Por qué es óptimo?** Los dos caracteres menos frecuentes pueden ir siempre como hermanos en el nivel más profundo de
algún código óptimo. Unirlos primero no pierde nada, y el resto es el mismo problema con un carácter menos.

## 4. Resultados

| Texto | ASCII | Código fijo | Huffman | Ahorro frente a ASCII |
|---|---|---|---|---|
| "pachamama paqarin" | 136 | 68 | **51** | 63 % |
| Saludo en quechua (55 caracteres) | 440 | 275 | **208** | 52,7 % |
| Saludo en aimara (42 caracteres) | 336 | 210 | **160** | 52,4 % |
| Frase en castellano (70 caracteres) | 560 | 350 | **287** | 48,8 % |
| "abcdefghijklmnop" (todos una vez) | 128 | 64 | 64 | sin ganancia frente al fijo |

Cuando todos los caracteres aparecen parecido, no hay nada que aprovechar. Huffman comprime gracias a la **desigualdad**
de las frecuencias. Formatos como ZIP, JPEG y MP3 usan Huffman como una de sus etapas.

## 5. En la aplicación

- **Teoría**: este documento.
- **Visualizador: Huffman**: escribe un texto (hasta 40 caracteres) y avanza.
  - Se ven todos los árboles de la cola, de menor a mayor frecuencia. En naranja, los dos que se unen; en verde, el
    árbol nuevo.
  - Al final, cada hoja muestra su código.
  - **Se evalúa** explica cada unión y por qué el código resultante es libre de prefijos.
  - **Java** es una clase `Huffman` completa con `codigos`, `asignar` y un `main` que codifica el texto de ejemplo.
- **Caso: compresión de mensajes**: frases en quechua, aimara y castellano, o el texto que escribas.
  - La tabla muestra el código de cada carácter y sus bits.
  - Se ve el texto codificado y se comprueba que al decodificarlo vuelve el original.

## 6. Código relevante

- `semana15/modelo/Huffman.java`: frecuencias, árbol, códigos, codificar, decodificar y paso a paso
- `semana15/caso/CompresionTextos.java`: tabla y resumen de compresión
- `src/main/resources/.../semana15/frases.csv`: frases de ejemplo
