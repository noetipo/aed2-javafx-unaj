# Tarea · Semana 05

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana05/tarea/TareaSemana05.java`
**Rama:** `tarea/s05-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

Implementa las piezas de un AVL sobre la clase `Nodo` de la plantilla, que guarda su `altura`:

1. **`factorEquilibrio(Nodo n)`**: altura(izq) − altura(der) con las alturas guardadas; 0 si n es null.
2. **`rotarDerecha(Nodo y)`**: sube el hijo izquierdo, actualiza las alturas (primero la de y, que queda abajo) y
   **devuelve** la nueva raíz.
3. **`rotarIzquierda(Nodo x)`**: la simétrica.
4. **`esAVL(Nodo r)`**: verifica que sea ABB, que todos los nodos cumplan |fe| ≤ 1 con las alturas **reales** y que
   la altura guardada en cada nodo sea la real.
5. **Informe** (en la descripción del PR, máximo una página):
   - Inserta 10, 20, 30, 25, 28, 27 en un AVL vacío. Dibuja el árbol después de cada inserción, indica cada caso
     de rotación y verifícalo en el visualizador.
   - ¿Por qué en `rotarDerecha` hay que actualizar la altura de y **antes** que la de x?

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| `factorEquilibrio` | Correcto, incluido null | Falla con null | No funciona | 2 |
| Rotaciones | Enlaces correctos, alturas actualizadas en el orden correcto y devuelven la nueva raíz | Enlaces correctos pero alturas mal | No funcionan | 6 |
| `esAVL` | Verifica ABB, equilibrio con alturas reales y alturas guardadas | Solo verifica el equilibrio | No funciona | 5 |
| Informe | Secuencia de árboles y casos correctos, y la pregunta bien justificada | Árboles con errores | Ausente | 5 |
| Git y PR | Rama `tarea/s05-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
