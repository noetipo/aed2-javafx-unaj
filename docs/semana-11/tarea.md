# Tarea · Semana 11

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana11/tarea/TareaSemana11.java`
**Rama:** `tarea/s11-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

En la matriz `w`, `w[i][j]` es el peso del arco i → j, o `-1` si no hay arco (`w[i][i] = 0`). El grafo puede ser
dirigido. No modifiques las matrices que recibes.

1. **`floydWarshall(w)`**: las distancias mínimas entre todos los pares. Devuelve `INFINITO` donde no hay camino, sin
   desbordar el `int`.
2. **`clausuraTransitiva(arco)`**: el algoritmo de Warshall con booleanos. `alcanza[i][j]` es true si hay un camino de
   **uno o más** arcos de i a j.
3. **`centro(w)`**: el vértice con menor excentricidad (la distancia a su vértice más lejano).
   - Si hay empate, el de menor índice.
   - Retorna -1 si ningún vértice alcanza a todos.
4. **Informe** (en la descripción del PR, máximo una página):
   - Explica con tus palabras la recurrencia `dist[i][j] = min(dist[i][j], dist[i][k] + dist[k][j])` y por qué el `for`
     de k debe ir afuera.
   - Con `clausuraTransitiva` sobre la malla de la semana 10, ¿de cuántos cursos es prerrequisito (directo o indirecto)
     Matemática Básica?
   - ¿Cómo detectarías un ciclo de peso negativo con la matriz final de Floyd–Warshall?

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| `floydWarshall` | Correcto en grafos dirigidos y no dirigidos, sin desborde y sin modificar w | Falla con inalcanzables | No funciona | 7 |
| `clausuraTransitiva` | Warshall O(n³) con booleanos; ciclos y diagonal correctos | Marca la diagonal siempre en true | No funciona | 5 |
| `centro` | Usa la matriz de distancias; empates y caso -1 | Falla en los bordes | No funciona | 3 |
| Informe | Recurrencia bien explicada, conteo correcto y método para ciclos negativos | Incompleto | Ausente | 3 |
| Git y PR | Rama `tarea/s11-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
