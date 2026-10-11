# Tarea · Semana 08

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana08/tarea/TareaSemana08.java`
**Rama:** `tarea/s08-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

El grafo llega como **matriz de adyacencia** `w`, donde `w[i][j]` es el peso o `-1` si no hay arista.

1. **`dijkstraMatriz(w, s)`**: Dijkstra **sin** cola de prioridad. En cada vuelta busca, recorriendo el arreglo, el vértice
   no cerrado de menor distancia: O(V²). Devuelve `INFINITO` para los inalcanzables. Cuidado con el desborde de `int`.
2. **`caminoMatriz(w, s, t)`**: la ruta más corta como lista de vértices, guardando los padres.
3. **`distanciaConParada(w, s, parada, t)`**: la ruta más corta de s a t que pasa **obligatoriamente** por `parada`, o
   -1 si no es posible.
4. **Informe** (en la descripción del PR, máximo una página):
   - Da un grafo de 3 vértices con **un peso negativo** en el que Dijkstra dé una distancia equivocada, y explica en qué
     paso falla.
   - ¿Cuándo conviene la versión O(V²) de esta tarea y cuándo la versión con montículo del visualizador?

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| `dijkstraMatriz` | Correcto en el ejemplo y en grafos aleatorios (contra Floyd–Warshall), sin desborde | Falla con vértices inalcanzables | No funciona | 7 |
| `caminoMatriz` | Reconstruye con padres, incluidos los casos s = t y sin camino | Camino correcto pero falla en los bordes | No funciona | 4 |
| `distanciaConParada` | Dos Dijkstra y el caso imposible | No detecta el caso imposible | No funciona | 3 |
| Informe | Contraejemplo correcto y comparación justificada | Incompleto | Ausente | 4 |
| Git y PR | Rama `tarea/s08-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
