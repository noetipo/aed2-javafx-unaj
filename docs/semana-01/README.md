# Semana 01 · Complejidad algorítmica y ordenamientos elementales

## Objetivos

- Contar operaciones elementales y expresar el costo con notación **O grande**.
- Distinguir **mejor caso, caso promedio y peor caso**.
- Contrastar el análisis teórico con mediciones empíricas (tiempo frente a n).

## 1. ¿Por qué analizar algoritmos?

Dos programas correctos pueden diferir muchísimo en tiempo. Si ordenamos los precios de 1 000 productos,
un algoritmo O(n²) hace del orden de 10⁶ comparaciones, mientras que uno O(n log n) hace unas 10⁴.

## 2. Notación asintótica

| Notación | Significado | Ejemplo |
|---|---|---|
| O(f(n)) | cota superior | burbuja es O(n²) |
| Ω(f(n)) | cota inferior | todo ordenamiento por comparación es Ω(n log n) en el peor caso |
| Θ(f(n)) | cota ajustada | selección es Θ(n²) en todos los casos |

Ordenes de crecimiento comunes: 1 < log n < n < n log n < n² < 2ⁿ.

## 3. Ordenamientos de la semana

| Algoritmo | Mejor caso | Peor caso | Estable | Idea |
|---|---|---|---|---|
| Burbuja (con parada temprana) | O(n) | O(n²) | Sí | Intercambia vecinos; el mayor "flota" al final |
| Inserción | O(n) | O(n²) | Sí | Inserta cada elemento en un prefijo ordenado |
| Selección (tarea) | O(n²) | O(n²) | No | Elige el mínimo y lo coloca al inicio |
| Merge sort | O(n log n) | O(n log n) | Sí | Divide, ordena mitades y mezcla |

## 4. En la aplicación

- **Teoría**: este documento.
- **Visualizador**: elige burbuja o inserción, cambia n y avanza paso a paso. Naranja = comparación,
  rojo = intercambio, verde = posición ordenada. En el panel derecho:
  - **Pseudocódigo**: resalta la línea que se ejecuta y muestra `fin`, `j`, `a[j]`, los contadores, etc.
  - **Java**: el mismo algoritmo en Java, con las líneas equivalentes resaltadas.
  - **Traza**: la prueba de escritorio completa; haz clic en una fila para saltar a ese paso.
- **Caso: precios de mercado**: ordena el catálogo `precios_mercado.csv` con merge sort, y con **Medir tiempos**
  grafica burbuja, inserción y merge sort para n entre 500 y 6 000.

## 5. Código relevante

- `semana01/modelo/Ordenamientos.java`: algoritmos y generación de pasos
- `semana01/modelo/EstadoArreglo.java`: un paso de la animación (record inmutable)
- `comun/benchmark/Cronometro.java`: calentamiento + mediana con `System.nanoTime()`
