# Semana 12 · Flujo máximo: Edmonds–Karp

## Objetivos

- Modelar problemas de **capacidad** con una **red de flujo**: cuánto puede pasar de un origen a un destino.
- Entender la **red residual** y por qué a veces hay que **devolver** flujo.
- Ejecutar a mano y programar **Edmonds–Karp** (Ford–Fulkerson con BFS).
- Usar el **teorema de flujo máximo y corte mínimo** para encontrar el verdadero cuello de botella.

## 1. El problema

La cuenca lechera de Ayaviri envía leche en camiones a una planta en Juliaca. Cada ruta admite a lo más cierta
cantidad de camiones por día: su **capacidad**. Las capacidades son **ficticias**, con fines didácticos. ¿Cuántos
camiones pueden llegar por día como máximo?

Una red de flujo es un grafo **dirigido** con una capacidad `c(u, v)` en cada arco, una **fuente** s y un **sumidero**
t. Un flujo válido cumple dos reglas:

- **Capacidad**: por cada arco pasa entre 0 y `c(u, v)`.
- **Conservación**: en cada vértice intermedio, lo que entra es igual a lo que sale. La leche no se queda en el
  camino.

## 2. Caminos de aumento y la red residual

Idea de Ford–Fulkerson: mientras exista un camino de s a t con espacio libre, enviar por él lo más posible.

- El **residual** de u → v es `capacidad − flujo`: cuánto cabe todavía.
- Si u → v lleva flujo, aparece un residual **hacia atrás**, v → u, igual a ese flujo. Recorrerlo significa
  **devolver** camiones de u → v para mandarlos por otro lado.
- El **cuello de botella** de un camino es su menor residual: no se puede enviar más que eso.

¿Por qué hace falta devolver? En la red del curso, el primer camino manda 6 camiones por Ayaviri → Azángaro → Pucará →
Juliaca. Más adelante, para que también pase la leche que va por Lampa, el algoritmo **devuelve 3** de los que iban de
Azángaro a Pucará y los manda por Huancané. Sin los arcos hacia atrás, una mala decisión inicial no tendría arreglo.

## 3. Edmonds–Karp

Edmonds–Karp es Ford–Fulkerson donde el camino de aumento se busca con **BFS**: siempre el de **menos tramos**. Así
termina en **O(V · E²)** sin importar las capacidades. Si se elige cualquier camino, en algunos casos el algoritmo se
vuelve muy lento.

En la pestaña Java el flujo se guarda en una matriz con `flujo[v][u] = −flujo[u][v]`. Con eso, el residual de
cualquier par es simplemente `cap[u][v] − flujo[u][v]`, hacia adelante y hacia atrás.

| Vuelta | Camino de aumento | Cuello | Total |
|---|---|---|---|
| 1 | Ayaviri → Azángaro → Pucará → Juliaca | 6 | 6 |
| 2 | Ayaviri → Azángaro → Huancané → Juliaca | 4 | 10 |
| 3 | Ayaviri → Lampa → Pucará → Juliaca | 3 | 13 |
| 4 | Ayaviri → Lampa → Pucará **⇠ Azángaro** → Huancané → Juliaca | 1 | 14 |
| 5 | Ayaviri → Lampa → Pucará **⇠ Azángaro** → Putina → Huancané → Juliaca | 2 | 16 |

## 4. Corte mínimo

Un **corte** separa los vértices en dos grupos: S, con la fuente, y T, con el sumidero. Su capacidad es la suma de los
arcos que van de S a T. Todo flujo tiene que cruzar el corte, así que **ningún flujo supera a ningún corte**.

**Teorema de flujo máximo y corte mínimo:** el flujo máximo es **igual** a la capacidad del corte mínimo.

Cuando Edmonds–Karp termina, S son los vértices que todavía se alcanzan desde s por la red residual. En la red del
curso, S = {Ayaviri, Lampa}, y el corte es Ayaviri → Azángaro (10) + Lampa → Pucará (6) = **16**.

El corte responde la pregunta práctica: **¿qué ampliar?** Ampliar un tramo fuera del corte mínimo no aumenta el
envío, aunque ese tramo esté lleno.

## 5. Varias fuentes y emparejamientos

- **Varias fuentes o destinos**: se agrega una fuente ficticia unida a cada fuente real, con capacidad igual a su
  producción. El caso de esta semana lo hace con tres cuencas.
- **Emparejamiento bipartito**: asignar conductores a rutas, docentes a cursos o postulantes a vacantes. Se arma una
  red fuente → conductores → rutas → sumidero, con capacidades 1. El flujo máximo es el máximo de parejas (tarea).

## 6. En la aplicación

- **Teoría**: este documento.
- **Visualizador: Edmonds–Karp**: sobre cada arco se ve `flujo/capacidad`, y el azul es más grueso cuanto más lleno
  está el arco.
  - El camino de aumento aparece en naranja, el arco recorrido al revés en morado y el cuello de botella en rojo.
  - Al final se marca el lado S y el corte mínimo.
  - **Se evalúa** explica cada camino, cada devolución y por qué el total es igual al corte.
  - **Java** es una clase `FlujoMaximo` completa con `edmondsKarp`, `bfs` y `main`.
- **Caso: leche a la planta**: con la producción de cada cuenca (Ayaviri, Azángaro, Putina), muestra cuánto llega, el
  corte mínimo y una tabla con la ganancia de ampliar cada tramo.
  - Con 14 + 8 + 5 = 27 camiones, llegan 17.
  - Solo ampliar Pucará → Juliaca ayuda (+3). Ampliar Azángaro → Huancané, que también está lleno, no sirve:
    entonces el límite pasa a ser Huancané → Juliaca.

## 7. Código relevante

- `semana12/modelo/RedFlujo.java`: Edmonds–Karp, corte mínimo y paso a paso
- `semana12/caso/CuencaLechera.java`: fuente ficticia, envío por cuenca y ampliaciones
- `src/main/resources/.../semana12/rutas_leche.csv`: arcos y capacidades
