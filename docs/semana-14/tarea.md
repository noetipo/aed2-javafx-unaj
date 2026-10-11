# Tarea · Semana 14

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana14/tarea/TareaSemana14.java`
**Rama:** `tarea/s14-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

En cada función, escribe en un comentario qué significa cada casilla de tu tabla, la recurrencia y los casos base.
Nada de recursión sin memoria: la solución debe ser O(tamaño de la tabla).

1. **`cambioMonedas(monedas, monto)`**: la menor cantidad de monedas (ilimitadas de cada valor) que suman exactamente
   `monto`. Retorna -1 si es imposible.
2. **`subsecuenciaComun(a, b)`**: el largo de la subsecuencia común más larga. No tiene que ser contigua: en
   "ABCBDAB" y "BDCABA" es 4 ("BCBA").
3. **`distanciaEdicion(a, b)`**: la menor cantidad de inserciones, borrados o cambios de un carácter para convertir
   `a` en `b`. Por ejemplo, "killa" → "qilla" vale 1.
4. **Informe** (en la descripción del PR, máximo una página):
   - Con las monedas {1, 3, 4} y monto 6, compara la regla voraz con tu tabla.
   - Llena a mano la tabla de `distanciaEdicion("inti", "anti")` y marca el camino de la solución.
   - ¿Cómo usarías `distanciaEdicion` para sugerir palabras en el diccionario quechua de la semana 04?

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| `cambioMonedas` | Tabla O(monto · monedas), caso imposible y monto 0; rápido con montos grandes | Correcto pero lento (recursión sin memoria) | No funciona | 5 |
| `subsecuenciaComun` | Tabla correcta con cadenas vacías | Falla en los bordes | No funciona | 5 |
| `distanciaEdicion` | Tabla con casos base y las tres operaciones | Olvida una operación | No funciona | 5 |
| Informe | Tablas a mano y explicación correctas | Incompleto | Ausente | 3 |
| Git y PR | Rama `tarea/s14-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
