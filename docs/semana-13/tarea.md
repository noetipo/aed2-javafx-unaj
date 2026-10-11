# Tarea · Semana 13

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana13/tarea/TareaSemana13.java`
**Rama:** `tarea/s13-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

Las tres funciones se resuelven con backtracking: elegir, probar recursivamente y **deshacer**.

1. **`contarReinas(n)`**: cuántas formas distintas hay de poner n reinas sin que se ataquen. Con n = 8 son 92.
2. **`subconjuntoConSuma(pesos, objetivo)`**: elegir algunos sacos para cargar **exactamente** `objetivo` kg.
   - Retorna qué sacos se eligen, o `null` si es imposible.
   - Usa la poda "si ya me pasé del objetivo, vuelvo atrás".
3. **`contarCaminos(mapa)`**: cuántos caminos **simples** (sin repetir casillas) hay de `S` a `E`, moviéndose en las
   4 direcciones. `#` es pared.
4. **Informe** (en la descripción del PR, máximo una página):
   - Dibuja el árbol de decisiones de `subconjuntoConSuma({5, 3, 2}, 5)` y marca dónde actúa la poda.
   - ¿Cuántas llamadas hace tu `contarReinas(8)`? Compáralo con 8<sup>8</sup>.
   - ¿Por qué `contarCaminos` debe **desmarcar** la casilla al volver? Muestra un mapa donde falla si no lo hace.

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| `contarReinas` | Correcto de n = 1 a 8 con poda por ataques | Correcto pero sin poda (muy lento) | No funciona | 5 |
| `subconjuntoConSuma` | Backtracking con poda, retorna una elección válida o null | Correcto sin poda | No funciona | 5 |
| `contarCaminos` | Marca y desmarca; paredes y bordes | Cuenta caminos repetidos o no simples | No funciona | 5 |
| Informe | Árbol, conteo y contraejemplo correctos | Incompleto | Ausente | 3 |
| Git y PR | Rama `tarea/s13-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
