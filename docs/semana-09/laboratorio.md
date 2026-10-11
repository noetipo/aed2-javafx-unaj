# Laboratorio · Semana 09

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s09-apellido
```

## Paso 1: Kruskal a mano

1. En **Semana 09 → Visualizador: Kruskal y Prim**, mira el primer paso de Kruskal. Copia en papel los 19 tramos
   ordenados por km (aparecen abajo del mapa y en la pestaña **Traza**).
2. Ejecuta Kruskal en papel. Escribe los grupos después de cada tramo aceptado, por ejemplo `{Chucuito, Puno}`,
   `{Azángaro, Pucará}`, …
3. ¿Qué tramos descartaste? Para cada uno, escribe el ciclo que habría cerrado.
4. Compara con el visualizador: ¿coinciden el total (541 km) y los descartes?

## Paso 2: union-find por dentro

1. Avanza hasta que se acepte Juliaca – Puno (49 km). En **Se evalúa**, ¿cuáles son `encontrar(Juliaca)` y
   `encontrar(Puno)`? ¿Por qué no son Juliaca y Puno?
2. Dibuja los jefes (flechas) de los pueblos del norte en ese momento. Luego aplica la compresión de caminos al buscar
   `encontrar(Lampa)`. ¿Cómo quedan las flechas?
3. ¿Qué pasaría con `encontrar` si nunca se comprimieran los caminos y los grupos se unieran siempre en fila?

## Paso 3: Prim desde dos pueblos distintos

1. Ejecuta Prim desde **Juliaca** y luego desde **Desaguadero**. Anota el orden en que se conectan los pueblos.
2. ¿Sale el mismo total? ¿Salen los mismos tramos? Explica por qué.
3. Encuentra un paso en que Prim **descarte** un tramo de la cola. ¿Por qué estaba ese tramo en la cola si al final no
   sirvió?

## Paso 4: el caso

1. Abre **Caso: red de fibra óptica** con centro en Juliaca. ¿Cuántos km de cable ahorra Kruskal frente a Dijkstra?
2. ¿A qué pueblos llega la señal más lejos con el diseño de Kruskal? Explica por qué ocurre con Huancané y Moho.
3. Si fueras el responsable del proyecto, ¿qué diseño elegirías? ¿Agregarías algún tramo de respaldo? Justifica con
   números.

## Paso 5: llevar el código a tu proyecto

1. Copia la clase de la pestaña **Java** a `ArbolExpansion.java` y ejecútala.
2. Completa el `main` con los 19 tramos (los km están escritos sobre cada tramo en el mapa del visualizador). Verifica
   que Kruskal y Prim den 541 km.

## Paso 6: guardar el trabajo

```bash
git add docs/semana-09/img
git commit -m "Lab S09: árbol de expansión mínima"
git push -u origin lab/s09-apellido
```
