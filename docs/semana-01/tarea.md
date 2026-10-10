# Tarea · Semana 01

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana01/tarea/TareaSemana01.java`
**Rama:** `tarea/s01-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

1. **`seleccion(int[] a)`**: ordena con selección y retorna el número de comparaciones.
2. **`busquedaBinaria(int[] a, int x)`**: búsqueda binaria **iterativa**; retorna el índice o −1.
3. **`comparacionesPeorCaso(int n)`**: retorna `{n, ⌊log₂ n⌋ + 1}` (búsqueda lineal frente a binaria en el peor caso).
4. **Informe** (en la descripción del PR, máximo una página):
   - Explica por qué selección hace siempre n(n−1)/2 comparaciones.
   - Incluye una tabla con los valores de `comparacionesPeorCaso` para n = 10, 1 000 y 1 000 000, y coméntala.

No uses `Arrays.sort` ni `Collections.sort`, y no cambies las firmas de los métodos.

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| `seleccion` | Ordena y cuenta bien (pasan sus 2 pruebas) | Ordena pero el conteo falla | No ordena | 6 |
| `busquedaBinaria` | Iterativa, pasa todas las pruebas, incluido el arreglo vacío | Falla en los bordes | Lineal o no funciona | 5 |
| `comparacionesPeorCaso` | Correcta para todos los n | Error de ±1 | No implementada | 3 |
| Informe | Justificación correcta y tabla analizada | Tabla sin análisis | Ausente | 4 |
| Git y PR | Rama `tarea/s01-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
