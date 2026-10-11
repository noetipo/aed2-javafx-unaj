# Tarea · Semana 09

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana09/tarea/TareaSemana09.java`
**Rama:** `tarea/s09-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

Los vértices son `0..n-1`. Una lista de tramos es un `int[][]` donde cada fila es `{u, v, km}`. Una matriz `w` usa
`w[i][j]` = km, o `-1` si no hay arista.

1. **`primMatriz(w)`**: los km del MST con Prim **sin** cola de prioridad, en O(V²).
   - Usa un arreglo `costo[]` con el tramo más barato que une cada vértice con el árbol.
   - Retorna -1 si el grafo no es conexo.
2. **`costoConTramoObligatorio(n, tramos, i)`**: el árbol más barato que **incluye** el tramo `tramos[i]` (por ejemplo, un
   cable que ya existe).
   - Pista: une primero sus extremos y luego aplica Kruskal.
   - Retorna -1 si el grafo no es conexo.
3. **`espaciamiento(n, tramos, k)`**: agrupa los vértices en **k zonas** uniendo primero los más cercanos (Kruskal que se
   detiene cuando quedan k grupos).
   - Retorna el km del tramo más corto entre dos zonas distintas: cuanto mayor, mejor separadas están.
   - Retorna -1 si nunca quedan exactamente k grupos.
4. **Informe** (en la descripción del PR, máximo una página):
   - Explica con un ejemplo por qué la propiedad del ciclo permite descartar un tramo.
   - En `costoConTramoObligatorio`, ¿cuántos km más cuesta el árbol en el peor caso respecto del MST? Justifica.
   - ¿Para qué podría servirle a una municipalidad de Puno el `espaciamiento` (agrupar pueblos en zonas)?

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| `primMatriz` | Correcto en el ejemplo y en grafos aleatorios; detecta grafos no conexos | Falla con grafos no conexos | No funciona | 6 |
| `costoConTramoObligatorio` | Usa union-find, no modifica la entrada y detecta el caso no conexo | Correcto pero modifica la entrada | No funciona | 5 |
| `espaciamiento` | Correcto para todo k y el caso imposible | Falla en los bordes (k = n o no conexo) | No funciona | 5 |
| Informe | Ejemplos correctos y justificación clara | Incompleto | Ausente | 2 |
| Git y PR | Rama `tarea/s09-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
