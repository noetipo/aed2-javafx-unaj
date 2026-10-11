# Tarea · Semana 06

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana06/tarea/TareaSemana06.java`
**Rama:** `tarea/s06-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

1. **`esMonticuloMin(int[] a)`**: verifica la propiedad de montículo de mínimos en todo el arreglo.
2. **`construirMonticulo(int[] a)`**: convierte el arreglo **en el mismo lugar** en un montículo de mínimos, con el
   método de abajo hacia arriba (hundir desde n/2 − 1 hasta 0).
3. **`kMayores(int[] a, int k)`**: los k mayores, de mayor a menor, usando `java.util.PriorityQueue` con a lo más k
   elementos dentro. No ordenes todo el arreglo y no modifiques la entrada.
4. **Informe** (en la descripción del PR, máximo una página):
   - Construye a mano el montículo de `[9, 4, 7, 1, 8, 2, 6, 3, 5]` de abajo hacia arriba y dibuja el árbol después
     de cada llamada a hundir.
   - ¿Por qué `kMayores` cuesta O(n log k) y no O(n log n)? ¿Cuándo conviene frente a ordenar todo?

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| `esMonticuloMin` | Revisa cada posición contra su padre, incluidos los arreglos vacíos | Revisa solo algunos niveles | No funciona | 3 |
| `construirMonticulo` | De abajo hacia arriba, en el mismo arreglo, con el resultado exacto esperado | Produce un montículo válido pero distinto (p. ej. insertando uno por uno) | No funciona | 6 |
| `kMayores` | Cola de tamaño k, valida k y no modifica la entrada | Ordena todo el arreglo | No funciona | 5 |
| Informe | Construcción paso a paso correcta y análisis de costo justificado | Incompleto | Ausente | 4 |
| Git y PR | Rama `tarea/s06-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
