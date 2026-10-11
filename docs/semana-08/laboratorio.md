# Laboratorio · Semana 08

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s08-apellido
```

## Paso 1: Dijkstra a mano

1. Con los km del mapa de **Semana 08 → Visualizador: Dijkstra**, ejecuta en papel Dijkstra desde **Puno**. Haz una tabla
   con una fila por extracción: pueblo cerrado, su distancia y la cola de prioridad después de relajar.
2. Compara tu tabla con la pestaña **Traza** del visualizador.

## Paso 2: relajaciones

1. Desde Juliaca, avanza hasta que se relaje la carretera Huancané – Putina. ¿Qué valor tenía `dist[Putina]` antes?
2. Más adelante se revisa Azángaro – Putina. ¿Por qué no mejora?
3. ¿En qué orden se cierran los pueblos? Verifica que sus distancias nunca bajan.

## Paso 3: entradas viejas

1. Agrega mentalmente una carretera Juliaca – Chucuito de 90 km. ¿Se insertaría Chucuito dos veces en la cola? ¿Cuándo se
   descartaría la entrada vieja?
2. Explica por qué la cola de Java obliga a hacer esto.

## Paso 4: el caso

1. Abre **Caso: rutas de reparto**. En la tabla, ¿para qué destinos la ruta de menos tramos es más larga en km?
2. Cambia el origen a Puno. ¿Cambia el destino más lejano?

## Paso 5: llevar el código a tu proyecto

1. Copia la clase de la pestaña **Java** a `Dijkstra.java` y ejecútala.
2. Completa el `main` con las 15 carreteras (los km están escritos sobre cada carretera en el mapa del visualizador) y
   muestra la ruta de Ayaviri a Desaguadero.

## Paso 6: guardar el trabajo

```bash
git add docs/semana-08/img
git commit -m "Lab S08: Dijkstra"
git push -u origin lab/s08-apellido
```
