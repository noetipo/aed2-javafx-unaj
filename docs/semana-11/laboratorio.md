# Laboratorio · Semana 11

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s11-apellido
```

## Paso 1: Floyd–Warshall a mano (versión pequeña)

1. Con 4 pueblos (Juliaca, Puno, Huancané y Putina) y los km del mapa de la semana 08, escribe la matriz inicial
   (∞ donde no hay carretera directa).
2. Haz las 4 vueltas de k en papel y anota qué casillas cambian en cada una.
3. Escribe también la matriz `sig` final y reconstruye la ruta Puno → Putina.

## Paso 2: el visualizador

1. Abre **Semana 11 → Visualizador: Floyd–Warshall** y usa **k siguiente ▶** para ver cada vuelta completa. ¿En qué
   vuelta mejoran más casillas? ¿Por qué esa?
2. Sigue la casilla Juliaca → Putina. ¿En qué vueltas cambia y a qué valor?
3. Encuentra un paso marcado "sin camino por k". ¿Qué pasaría en Java si no se preguntara `!= INF` antes de sumar?
   Pruébalo: `Integer.MAX_VALUE + 49`.

## Paso 3: el orden de los for

1. Copia la clase de la pestaña **Java** a `FloydWarshall.java` y ejecútala.
2. Mueve el `for` de k **adentro** (orden i, j, k). Prueba con una cadena de 4 vértices: los arcos 0 – 2, 2 – 3 y 3 – 1,
   de 1 km cada uno. ¿Cuánto da `dist[0][1]`? ¿Cuánto debería dar? Explica en qué momento se calcula esa casilla y por
   qué todavía no conoce los caminos que necesita.
3. Con las 15 carreteras del curso (pueblos en el orden de `pueblos.csv`), el orden equivocado da por casualidad las
   mismas distancias. ¿Por qué un programa que "funciona con los datos de prueba" no está necesariamente bien?

## Paso 4: el caso

1. Abre **Caso: almacén para heladas**. Con un almacén, ¿por qué el centro (Puno) y la mediana (Juliaca) son
   distintos? ¿A quién protege cada criterio?
2. Con dos almacenes, revisa los 5 primeros del ranking. ¿Qué tienen en común?
3. En la pestaña **Tabla de distancias**, ¿cuál es el par de pueblos más alejado (el **diámetro** del grafo)?

## Paso 5: guardar el trabajo

```bash
git add docs/semana-11/img
git commit -m "Lab S11: Floyd–Warshall"
git push -u origin lab/s11-apellido
```
