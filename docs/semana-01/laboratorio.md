# Laboratorio · Semana 01

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s01-apellido
```

## Paso 1: ejecutar la aplicación

```bash
./mvnw clean javafx:run
```

Abre **Unidad I → Semana 01 → Visualizador**.

## Paso 2: conteo de operaciones

1. Con **Burbuja** y n = 8, pulsa **⏭** y anota el número de comparaciones e intercambios.
2. Repite con **Inserción** usando el mismo arreglo (no pulses "Nuevo arreglo").
3. Pregunta: ¿por qué inserción suele hacer menos comparaciones que burbuja con datos aleatorios?

## Paso 3: mejor y peor caso

En `OrdenamientosTest` revisa `burbujaEnArregloOrdenadoHaceNMenosUnaComparacion` y
`insercionEnPeorCasoHaceNCuadradoMedios`. Ejecuta:

```bash
./mvnw test -Dtest=OrdenamientosTest
```

Escribe una prueba nueva que verifique que **inserción** en un arreglo ya ordenado hace n − 1 comparaciones.

## Paso 4: medición empírica

1. Abre **Caso: precios de mercado** y pulsa **Medir tiempos**.
2. Toma una captura del gráfico y guárdala en `docs/semana-01/img/benchmark-apellido.png`.
3. Si n se duplica de 3 000 a 6 000, ¿cuánto crece el tiempo de burbuja? ¿Y el de merge sort? Relaciónalo con O(n²) y O(n log n).

## Paso 5: guardar el trabajo

```bash
git add docs/semana-01/img src/test
git commit -m "Lab S01: conteo de comparaciones y benchmark"
git push -u origin lab/s01-apellido
```
