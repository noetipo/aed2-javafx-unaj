# Semana 14 · Programación dinámica: la mochila 0/1

## Objetivos

- Reconocer cuándo un problema tiene **subproblemas que se repiten** y **subestructura óptima**.
- Diseñar una solución de **programación dinámica (PD)**: qué significa cada casilla, la recurrencia y el orden de
  llenado.
- Resolver la **mochila 0/1** y **reconstruir** la solución desde la tabla.
- Ver por qué las reglas **voraces** fallan en este problema.

## 1. El problema

Un comerciante va a la feria de Puno en su mototaxi de carga, que soporta 10 decenas de kg. Cada producto se lleva
**entero o no se lleva** (por eso es "0/1"). ¿Qué llevar para ganar lo más posible? Las ganancias son ficticias.

| Producto | Peso | Ganancia |
|---|---|---|
| Queso de Ayaviri | 2 | 30 |
| Trucha | 6 | 60 |
| Quinua | 5 | 48 |
| Chuño | 5 | 45 |
| Papa nativa | 3 | 15 |

- **Regla voraz** (primero la mejor ganancia por kg): elige queso (15 por unidad de peso) y trucha (10 por unidad).
  Pesan 8 y ya no cabe nada más: **90**.
- **Óptimo**: quinua + chuño pesan 10 y suman **93**.

La regla voraz se equivocó porque llenó la mochila "casi bien" y desperdició capacidad. Probar todas las combinaciones
funciona, pero son 2<sup>n</sup>: con 50 productos serían más de mil billones.

## 2. La idea de la programación dinámica

Definir un **subproblema** que se resuelve con subproblemas más pequeños y guardar las respuestas en una **tabla**,
para no recalcularlas (como el `memo` de la escalinata en la semana 02).

**Casilla:** `dp[i][c]` = la mejor ganancia usando solo los **primeros i productos** con **capacidad c**.

**Recurrencia:** con el producto i hay dos opciones.

- **No llevarlo**: queda `dp[i−1][c]`.
- **Llevarlo** (si cabe): ocupa `peso[i]`, así que para los anteriores queda `c − peso[i]`. Vale
  `dp[i−1][c − peso[i]] + valor[i]`.

```text
dp[i][c] = max(dp[i−1][c], dp[i−1][c − peso[i]] + valor[i])     (la segunda solo si peso[i] ≤ c)
```

**Casos base:** `dp[0][c] = 0`, porque sin productos no hay ganancia.

**Orden de llenado:** fila por fila. Cada casilla usa dos de la fila de arriba, que ya están calculadas.

**Respuesta:** `dp[n][C]`.

Costo: **O(n · C)** en tiempo y memoria. Ojo: depende de la **capacidad**. Con capacidades enormes la tabla no cabe;
por eso se dice que es "pseudo-polinomial".

## 3. Reconstruir la solución

La tabla da la mejor ganancia, pero no dice qué llevar. Se recorre desde `dp[n][C]` hacia arriba:

- Si `dp[i][c] ≠ dp[i−1][c]`, el producto i cambió el resultado: **se lleva**, y se continúa en `c − peso[i]`.
- Si son iguales, no hacía falta: se sube en la misma columna.

## 4. ¿Cuándo sirve la programación dinámica?

Hacen falta dos ingredientes:

1. **Subestructura óptima**: la mejor solución se arma con mejores soluciones de subproblemas.
2. **Subproblemas superpuestos**: los mismos subproblemas aparecen muchas veces. Si no se repiten, basta "divide y
   vencerás" (Merge Sort).

| Técnica | Idea | Garantía |
|---|---|---|
| Voraz | Elegir lo mejor ahora y no volver | Óptimo solo en algunos problemas (Dijkstra, Prim, Kruskal, Huffman) |
| Backtracking | Probar y deshacer | Óptimo, pero puede ser exponencial |
| Programación dinámica | Tabla de subproblemas | Óptimo, en tiempo polinomial (o pseudo-polinomial) |

Otros problemas clásicos de PD: el cambio de monedas, la subsecuencia común más larga (comparar textos o ADN), la
distancia de edición (correctores ortográficos) y Floyd–Warshall (semana 11).

## 5. Caso: presupuesto participativo

La municipalidad tiene un presupuesto y 10 proyectos con su costo y la cantidad de personas que benefician. Todo es
ficticio. Es una mochila 0/1: el costo es el peso y los beneficiarios son el valor. Con S/ 250 mil:

| Regla | Personas beneficiadas |
|---|---|
| Programación dinámica (óptimo) | **7400** |
| Voraz: más beneficiarios por sol | 7350 |
| Voraz: más beneficiarios primero | 6450 |

## 6. En la aplicación

- **Teoría**: este documento.
- **Visualizador: mochila 0/1**: la tabla se llena casilla por casilla.
  - En azul, la casilla de arriba (no llevar); en naranja, la diagonal (llevar).
  - Al final, la reconstrucción se marca en verde.
  - **Se evalúa** explica las dos opciones de cada casilla con sus números.
  - **Java** es una clase `Mochila` completa con `resolver` (tabla y reconstrucción) y un `main` con los productos de
    la feria.
- **Caso: presupuesto participativo**: elige el presupuesto. Se comparan las tres reglas con barras y con una tabla
  de los proyectos que elige cada una.

## 7. Código relevante

- `semana14/modelo/Mochila.java`: tabla de PD, reconstrucción, regla voraz y paso a paso
- `semana14/caso/PresupuestoParticipativo.java`: óptimo frente a dos reglas voraces
- `src/main/resources/.../semana14/proyectos.csv`: proyectos ficticios
