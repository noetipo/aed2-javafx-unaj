# Tarea · Semana 02

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana02/tarea/TareaSemana02.java`
**Rama:** `tarea/s02-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

1. **`potencia(long base, int exponente)`**: potencia rápida recursiva en O(log e). Calcula x^(e/2) **una sola vez**.
2. **`maximo(int[] a, int inicio, int fin)`**: máximo por divide y vencerás (dos mitades). Lanza
   `IllegalArgumentException` si `inicio > fin`.
3. **`kEsimoMenor(int[] a, int k)`**: quickselect usando `QuickSort.particionar`. Continúa solo en la mitad
   que contiene la posición k.
4. **Informe** (en la descripción del PR, máximo una página):
   - Escribe la recurrencia de `potencia` y de `maximo`, y resuelve cada una.
   - Explica por qué quickselect es O(n) en promedio, aunque quicksort sea O(n log n).

`potencia` y `maximo` deben ser recursivos. No uses `Math.pow`, `Arrays.sort` ni `Collections.sort`.

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| `potencia` | O(log e), una sola llamada recursiva por nivel, pasa las pruebas | Correcta pero O(e), o con dos llamadas por nivel | No funciona | 5 |
| `maximo` | Divide en mitades, valida el rango, pasa las pruebas | Recursión lineal (n−1 + 1) | No funciona | 4 |
| `kEsimoMenor` | Quickselect correcto, funciona con valores repetidos | Ordena todo el arreglo | No funciona | 5 |
| Informe | Recurrencias bien planteadas y resueltas, y argumento correcto | Recurrencias sin resolver | Ausente | 4 |
| Git y PR | Rama `tarea/s02-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
