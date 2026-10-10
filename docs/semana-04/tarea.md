# Tarea · Semana 04

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana04/tarea/TareaSemana04.java`
**Rama:** `tarea/s04-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

Implementa estos métodos **recursivos** sobre la clase `Nodo` de la plantilla:

1. **`altura(Nodo r)`**: árbol vacío = 0, un nodo = 1.
2. **`contarHojas(Nodo r)`**: nodos sin hijos.
3. **`postorden(Nodo r, List<Integer> salida)`**: izquierdo, derecho, raíz.
4. **`esABB(Nodo r)`**: verifica la propiedad de ABB en **todo** el árbol. Pista: un auxiliar con el rango permitido
   `(minimo, maximo)`. Cuidado con los extremos: el árbol puede contener `Integer.MIN_VALUE` y `Integer.MAX_VALUE`
   (usa `long` o `Integer` con `null` para el rango).
5. **Informe** (en la descripción del PR, máximo una página):
   - Dibuja un árbol en el que cada nodo sea mayor que su hijo izquierdo y menor que su hijo derecho, pero que **no**
     sea un ABB. Explica por qué comparar solo con los hijos directos no basta.
   - ¿En qué orden habría que insertar 1, 2, …, 7 para obtener un ABB de altura 3? Generaliza la idea.

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| `altura` y `contarHojas` | Recursivos, con casos base correctos | Funcionan con un caso base de más o de menos | No funcionan | 5 |
| `postorden` | Orden correcto y maneja el árbol vacío | Orden parcialmente correcto | No funciona | 3 |
| `esABB` | Usa rangos y pasa todas las pruebas, incluidos los extremos | Solo compara con los hijos directos | No funciona | 6 |
| Informe | Contraejemplo correcto y orden de inserción justificado | Respuestas incompletas | Ausente | 4 |
| Git y PR | Rama `tarea/s04-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
