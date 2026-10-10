# Laboratorio · Semana 02

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s02-apellido
```

## Paso 1: rastrear quicksort

1. Ejecuta `./mvnw clean javafx:run` y abre **Semana 02 → Visualizador: quicksort**.
2. Con n = 8 y entrada aleatoria, avanza paso a paso. Antes de cada **PIVOTE_UBICADO**, predice en papel la
   posición final del pivote.
3. Dibuja el árbol de llamadas `quicksort(inicio, fin)` que observaste.

## Paso 2: peor caso

1. Cambia la entrada a **Ya ordenado (peor caso)** con n = 16. Anota las comparaciones y la profundidad.
2. Compara con la entrada aleatoria. ¿Qué recurrencia describe cada caso?
3. Ejecuta las pruebas:

```bash
./mvnw test -Dtest=QuickSortTest
```

4. En `QuickSort.particionar`, modifica una **copia** de la clase para usar como pivote el elemento central
   (intercámbialo con `a[fin]` antes de particionar). Comprueba que la profundidad con un arreglo ordenado
   baja a ≈ log₂ n.

## Paso 3: árbol de llamadas y memorización

1. Abre **Caso: escalinata de Huaynaroque** con n = 6. Cuenta cuántas veces aparece `f2` en el árbol.
2. Activa **Usar memorización** y vuelve a contar.
3. Con el gráfico, estima cuántas llamadas haría la versión directa para n = 40.

## Paso 4: pila de ejecución

Escribe en un `main` de prueba una llamada a `Escalinata.formasRecursivo(100_000)`. ¿Qué excepción aparece?
¿Por qué `formasMemo(100_000)` también falla, aunque haga solo 2n − 1 llamadas?

## Paso 5: guardar el trabajo

```bash
git add docs/semana-02/img
git commit -m "Lab S02: quicksort y memorizacion"
git push -u origin lab/s02-apellido
```
