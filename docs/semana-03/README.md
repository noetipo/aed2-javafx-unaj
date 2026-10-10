# Semana 03 · Búsqueda y tablas hash

## Objetivos

- Comparar la búsqueda lineal O(n), la binaria O(log n) y la búsqueda en una tabla hash, que es O(1) en promedio.
- Calcular a mano una función hash para cadenas con el **método de Horner**.
- Explicar qué es una **colisión** y resolverla con **encadenamiento** y con **sondeo lineal**.
- Relacionar el **factor de carga** α = n/m con el costo de buscar, y entender el **rehash**.

## 1. Buscar sin recorrer todo

| Método | Requisito | Costo de buscar |
|---|---|---|
| Búsqueda lineal | ninguno | O(n) |
| Búsqueda binaria | arreglo ordenado | O(log n) |
| Tabla hash | una función hash | O(1) en promedio, O(n) en el peor caso |

La idea de la tabla hash: si a partir de la clave se pudiera **calcular** su posición, no habría que buscarla.
Una **función hash** convierte la clave en un índice entre 0 y m − 1, donde m es el número de **cubetas**.

## 2. Función hash para cadenas: método de Horner

Para una clave c₀c₁…cₖ se usa el polinomio c₀·31ᵏ + c₁·31ᵏ⁻¹ + … + cₖ, tomando el resto de dividir entre m.
Para que los números no crezcan, el resto se toma en **cada** paso:

```
h = 0
para cada carácter c de la clave:
    h = (h · 31 + código(c)) mod m
```

Ejemplo con m = 7 y la clave `"AB"` ('A' = 65, 'B' = 66):

| Carácter | Cálculo | h |
|---|---|---|
| 'A' | (0 · 31 + 65) mod 7 | 2 |
| 'B' | (2 · 31 + 66) mod 7 = 128 mod 7 | 2 |

Una buena función hash reparte las claves de forma **pareja** entre las cubetas. Si m es primo, el reparto mejora.

## 3. Colisiones

Dos claves distintas pueden dar el mismo índice: eso es una **colisión**. No se puede evitar, porque hay muchas
más claves posibles que cubetas. Hay dos formas clásicas de resolverlas.

| Técnica | Idea | Buscar una clave que no está |
|---|---|---|
| **Encadenamiento** (visualizador) | Cada cubeta es una lista enlazada con todas sus claves | Se recorre solo la cadena de su cubeta |
| **Sondeo lineal** (tarea) | Una clave por casilla; si está ocupada, se prueba la siguiente: i+1, i+2, … (mod m) | Se avanza hasta encontrar una casilla vacía |

## 4. Factor de carga y rehash

El **factor de carga** α = n / m es el largo promedio de las cadenas. Con encadenamiento, una búsqueda exitosa
revisa en promedio unas **1 + α/2** claves. Si α crece, las cadenas se alargan y la tabla se vuelve lenta.

Por eso, cuando α supera un límite (aquí 0,75), se hace **rehash**: se crea una tabla más grande (2m + 1 cubetas)
y se **vuelven a insertar** todas las claves. Como el hash usa `mod m` y m cambió, cada clave puede cambiar de cubeta.
El rehash cuesta O(n), pero ocurre pocas veces, así que el costo promedio de insertar sigue siendo O(1).

## 5. En la aplicación

- **Teoría**: este documento.
- **Visualizador: tabla hash**: escribe una clave y pulsa **Insertar** o **Buscar**. **Cargar ejemplo** inserta
  seis códigos y llega a un rehash. Amarillo = cubeta consultada, naranja = clave comparada (colisión),
  rojo = clave recién agregada, verde = encontrada.
  - **Pseudocódigo** y **Java** resaltan la línea que se ejecuta. El recuadro **Se evalúa** muestra el cálculo de
    Horner carácter por carácter, cada comparación de la cadena y a dónde sigue la ejecución.
  - La pestaña **Java** es una clase `TablaHash` completa, con `insertar`, `buscar`, `rehash` y un `main`.
    Usa **Copiar código** para llevarla a tu proyecto.
- **Caso: padrón de mototaxis**: busca un código entre 120 registros ficticios de Juliaca y compara las
  comparaciones de la tabla hash con las de la búsqueda lineal. También muestra cómo quedaron repartidas las cadenas.

## 6. Código relevante

- `semana03/modelo/TablaHash.java`: tabla con encadenamiento, rehash y operaciones paso a paso
- `semana03/caso/PadronMototaxis.java`: padrón indexado por código, con búsqueda lineal para comparar
- `semana03/tarea/TablaSondeoLineal.java`: plantilla de la tarea
