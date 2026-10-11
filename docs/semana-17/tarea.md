# Tarea · Semana 17

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana17/tarea/TareaSemana17.java`
**Rama:** `tarea/s17-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

`d[i][j]` es la distancia entre i y j (simétrica). La ruta empieza en 0, visita todos una vez y vuelve a 0.

1. **`ramificacionYPoda(d)`**: el largo de la ruta óptima con backtracking que **poda** las rutas parciales que ya no
   pueden mejorar la mejor encontrada. Debe resolver 12 vértices en pocos segundos.
2. **`heldKarp(d)`**: el largo óptimo con programación dinámica sobre subconjuntos, en O(2<sup>n</sup> · n²). Debe
   resolver 16 vértices en menos de 3 segundos.
3. **`insercionMasBarata(d)`**: la heurística de inserción más barata. Retorna el orden de visita (empieza en 0). Con
   distancias que cumplen la desigualdad triangular, nunca da más del doble del óptimo.
4. **Informe** (en la descripción del PR, máximo una página):
   - Mide cuánto tardan tus tres métodos con n = 8, 10, 12, 14 y 16 (el que se pueda). Haz una tabla y comenta.
   - ¿Cuánta memoria usa Held–Karp con n = 25? ¿Por qué deja de ser práctico aunque sea mucho mejor que (n − 1)!?
   - Compara la inserción más barata con el vecino más cercano en 3 instancias.

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| `ramificacionYPoda` | Óptimo correcto con buena cota inicial; resuelve 12 vértices a tiempo | Correcto pero no pasa la prueba de tiempo | No funciona | 5 |
| `heldKarp` | Subconjuntos con bits; 16 vértices a tiempo | Correcto pero lento o con mucha memoria | No funciona | 6 |
| `insercionMasBarata` | Ruta válida y dentro del doble del óptimo | Ruta válida pero mala | No funciona | 4 |
| Informe | Mediciones, cálculo de memoria y comparación | Incompleto | Ausente | 3 |
| Git y PR | Rama `tarea/s17-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
