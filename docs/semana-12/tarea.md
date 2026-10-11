# Tarea · Semana 12

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana12/tarea/TareaSemana12.java`
**Rama:** `tarea/s12-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

Los vértices son `0..n-1` y `cap[u][v]` es la capacidad del arco u → v (0 si no hay arco). Puede haber arcos en ambos
sentidos. No modifiques las matrices que recibes.

1. **`flujoMaximo(cap, s, t)`**: el valor del flujo máximo con Edmonds–Karp.
2. **`corteMinimo(cap, s, t)`**: un corte mínimo. `ladoS[v]` es true si v se alcanza desde s en la red residual al
   terminar. La capacidad del corte debe ser igual al flujo máximo.
3. **`emparejamientoMaximo(puede)`**: `puede[i][j]` indica si el conductor i puede cubrir la ruta j. Cada conductor
   cubre a lo más una ruta y cada ruta tiene a lo más un conductor. Retorna cuántas rutas se pueden cubrir, usando una
   red de flujo con capacidades 1.
4. **Informe** (en la descripción del PR, máximo una página):
   - Dibuja una red de 4 vértices donde el primer camino de aumento obligue a devolver flujo después.
   - ¿Por qué ampliar un arco lleno que no está en el corte mínimo no aumenta el flujo?
   - Propón otro problema real de tu región que se resuelva con emparejamiento bipartito.

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| `flujoMaximo` | Edmonds–Karp con red residual; arcos en ambos sentidos; no modifica cap | Falla con arcos en ambos sentidos | No funciona | 7 |
| `corteMinimo` | BFS sobre la residual final; capacidad igual al flujo | Lado S incorrecto en algunos casos | No funciona | 4 |
| `emparejamientoMaximo` | Reduce a flujo con fuente y sumidero; bordes (0 filas o columnas) | Correcto solo en casos simples | No funciona | 4 |
| Informe | Ejemplo correcto, explicación clara y problema bien modelado | Incompleto | Ausente | 3 |
| Git y PR | Rama `tarea/s12-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
