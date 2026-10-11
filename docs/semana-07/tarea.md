# Tarea · Semana 07

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana07/tarea/TareaSemana07.java`
**Rama:** `tarea/s07-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

El grafo llega como lista de adyacencia no dirigida (`Map<String, List<String>>`).

1. **`contarComponentes`**: cantidad de componentes conexas. Pista: cada vértice sin visitar inicia una componente
   nueva; recórrela entera con BFS o DFS.
2. **`caminoMinimo(ady, s, t)`**: camino con la menor cantidad de aristas, como lista de vértices de s a t. `[s]` si
   s = t; lista vacía si no hay camino.
3. **`tieneCiclo`**: detecta un ciclo con DFS recordando el padre. El grafo puede tener varias componentes.
4. **Informe** (en la descripción del PR, máximo una página):
   - En la red vial del curso, ¿cuántas componentes hay si se bloquean Juliaca–Puno y Pucará–Ayaviri? Verifícalo con tu
     método y con el caso de la aplicación.
   - ¿Por qué en un grafo no dirigido hay que ignorar al **padre** al buscar ciclos?

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| `contarComponentes` | Correcto con vértices sueltos y grafo vacío | Falla en casos borde | No funciona | 4 |
| `caminoMinimo` | BFS con padres; camino mínimo correcto y casos s = t y sin camino | Encuentra un camino, pero no siempre el mínimo | No funciona | 6 |
| `tieneCiclo` | DFS con padre, revisa todas las componentes | Falla con varias componentes | No funciona | 5 |
| Informe | Respuestas correctas y verificadas | Incompletas | Ausente | 3 |
| Git y PR | Rama `tarea/s07-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
