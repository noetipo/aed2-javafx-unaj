# Tarea · Semana 16

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana16/tarea/TareaSemana16.java`
**Rama:** `tarea/s16-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

Escribe **tu propio** trie dentro de la clase. No uses el del paquete `modelo`. Las palabras tienen solo letras de la
`a` a la `z`.

1. **`prefijoComunMasLargo(palabras)`**: el prefijo más largo que comparten todas las palabras.
2. **`contarConPrefijo(palabras, prefijos)`**: para cada prefijo, cuántas palabras empiezan con él.
   - Debe ser rápido: 100 000 palabras y 100 000 prefijos en un par de segundos.
   - Pista: un contador en cada nodo.
3. **`palabraConstruible(palabras)`**: la palabra más larga cuyos prefijos (de largo 1 en adelante) también están en
   la lista. Si hay empate, la menor alfabéticamente.
4. **Informe** (en la descripción del PR, máximo una página):
   - ¿Por qué `contarConPrefijo` con un trie es mucho más rápido que revisar todas las palabras para cada prefijo?
     Estima ambos costos.
   - ¿Cuánta memoria usa tu trie si cada nodo tiene un arreglo de 26 hijos? ¿Cuándo convendría un mapa?
   - Propón cómo combinar un trie con la `distanciaEdicion` de la semana 14 para sugerir correcciones.

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| Trie propio | Clase Nodo clara, insertar en O(m) | Funciona pero repite código | Usa el trie del modelo | 3 |
| `prefijoComunMasLargo` | Correcto con una sola palabra, sin palabras y si una es prefijo de otra | Falla en los bordes | No funciona | 4 |
| `contarConPrefijo` | Contadores en los nodos; pasa la prueba de tiempo | Correcto pero lento | No funciona | 5 |
| `palabraConstruible` | Recorrido solo por nodos marcados; desempate correcto | Desempate incorrecto | No funciona | 4 |
| Informe | Costos y memoria bien estimados | Incompleto | Ausente | 2 |
| Git y PR | Rama `tarea/s16-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
