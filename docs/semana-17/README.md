# Semana 17 · Problemas difíciles: el viajante y las heurísticas

## Objetivos

- Reconocer problemas para los que **no se conoce** un algoritmo eficiente: los **NP-difíciles**.
- Medir el crecimiento **factorial** de la fuerza bruta.
- Usar **heurísticas** (vecino más cercano) y **búsqueda local** (2-opt) para obtener buenas soluciones rápido.
- Cerrar el curso relacionando cada técnica vista con el tipo de problema que resuelve.

## 1. El problema del viajante (TSP)

Un **dron** sale de su base en Juliaca con vacunas, debe pasar **una vez** por cada posta elegida y **volver**. Vuela en
línea recta, así que la distancia entre dos pueblos es la del haversine (semana 07). ¿En qué orden visitar las postas
para recorrer **menos km**?

Parece una variante de Dijkstra, pero es muy distinto: no se busca el camino más corto entre **dos** puntos, sino el
**orden** que recorre a todos.

## 2. Fuerza bruta: exacta pero imposible de escalar

Fijada la base, hay **(n − 1)!** órdenes posibles para los otros n − 1 pueblos:

| Pueblos (con la base) | Rutas | Tiempo aproximado (≈ 60 ns por ruta) |
|---|---|---|
| 8 | 5 040 | menos de 1 ms |
| 10 | 362 880 | ≈ 20 ms |
| 11 | 3 628 800 | ≈ 0,1 s |
| 14 | 6 227 020 800 | ≈ 6 minutos |
| 21 | 2,4 × 10<sup>18</sup> | miles de años |

Cada pueblo más **multiplica** el tiempo. Ninguna computadora más rápida resuelve eso: con 30 pueblos, ni todas las
computadoras del mundo juntas.

## 3. NP-difícil en pocas palabras

- **P**: problemas que se resuelven en tiempo **polinomial** (n², n³, …). Por ejemplo, ordenar, el camino más corto, el
  árbol de expansión mínima o el flujo máximo.
- **NP-difícil**: problemas para los que no se conoce ningún algoritmo polinomial. Si se encontrara uno para alguno de
  ellos, todos los NP se resolverían en tiempo polinomial. Es la pregunta abierta **P vs NP**.

El viajante, el coloreo con el mínimo de colores (semana 13) y la mochila 0/1 en general (semana 14) son NP-difíciles.

¿Qué se hace en la práctica?

1. **Exacto con ingenio**, si n es moderado: ramificación y poda, o programación dinámica de Held–Karp en
   O(2<sup>n</sup> · n²) (tarea).
2. **Heurísticas**: reglas rápidas que dan buenas soluciones, sin garantía de óptimo.
3. **Aproximaciones** con garantía: por ejemplo, la inserción más barata nunca da más del doble del óptimo (tarea).

## 4. Vecino más cercano (voraz)

Desde donde está, ir siempre al pueblo no visitado más cercano. Al final, volver a la base. Cuesta O(n²).

Su punto débil: deja pueblos lejanos "para después" y el regreso sale caro. En el visualizador, con 7 postas, da
**464 km**, mientras que el óptimo es **392 km**.

## 5. 2-opt (búsqueda local)

Partir de una ruta y mejorarla con cambios pequeños. Se toman dos tramos que no se tocan, a → b y c → e, y se prueba
cambiarlos por a → c y b → e, **invirtiendo** el pedazo de en medio. Si acorta, se acepta. Se repite hasta que ningún
cambio mejore: un **óptimo local**.

En el mapa, 2-opt **descruza** las rutas que se cruzan. En el visualizador lleva 464 → **392 km**, que es el óptimo.
Pero **no siempre** llega al óptimo: con Juliaca, Yunguyo, Pucará, Juli, Ilave, Chucuito, Lampa y Ayaviri se queda en
460 km, y el óptimo es 458.

## 6. En el caso del dron

| Postas | Fuerza bruta | Vecino más cercano | + 2-opt |
|---|---|---|---|
| 9 | 401 km (362 880 rutas, ≈ 20 ms) | 480 km | **401 km** |
| 10 | 413 km (3,6 millones de rutas, ≈ 0,1 s) | 581 km | **413 km** |
| 13 (todos) | no se ejecuta (6 227 millones de rutas) | 762 km | **557 km** |

Con todos los pueblos ya no se sabe cuánto falta para el óptimo, pero 2-opt mejoró un 27 % al vecino más cercano en
milisegundos.

## 7. Cierre del curso: ¿qué técnica para qué problema?

| Técnica | Semanas | Cuándo |
|---|---|---|
| Divide y vencerás | 01, 02 | El problema se parte en mitades independientes (Merge Sort, Quick Sort) |
| Estructuras de búsqueda | 03 – 06, 16 | Buscar, insertar u obtener el mínimo rápido (hash, ABB, AVL, montículo, trie) |
| Recorridos de grafos | 07, 10 | Conectividad, alcanzables, orden topológico |
| Voraces | 08, 09, 15 | Hay una elección local segura (Dijkstra, Prim, Kruskal, Huffman) |
| Programación dinámica | 11, 14 | Subproblemas que se repiten (Floyd–Warshall, mochila) |
| Flujo | 12 | Capacidades y asignaciones |
| Backtracking | 13 | Búsqueda exacta con podas |
| Heurísticas | 17 | Problemas NP-difíciles grandes |

## 8. En la aplicación

- **Teoría**: este documento.
- **Visualizador: vecino más cercano y 2-opt**: con 7 postas.
  - **Vecino más cercano**: se ve cada elección y el regreso a la base.
  - **Mejora 2-opt**: los tramos quitados aparecen en rojo punteado y los nuevos en verde.
  - **Se evalúa** muestra las distancias comparadas en cada paso.
  - **Java** es una clase `Viajante` completa con `vecinoMasCercano`, `dosOpt` y un `main` con la matriz de
    distancias.
- **Caso: ruta del dron de vacunas**: elige las postas.
  - La tabla compara los tres métodos en km, rutas revisadas y tiempo.
  - Otra tabla estima cuánto tardaría la fuerza bruta con más postas, con la velocidad medida en tu computadora.

## 9. Código relevante

- `semana17/modelo/Viajante.java`: fuerza bruta, vecino más cercano, 2-opt y paso a paso
- `semana17/caso/RutaDron.java`: comparación de métodos y crecimiento factorial
- `semana17/vista/DibujoRuta.java`: la ruta sobre el mapa
