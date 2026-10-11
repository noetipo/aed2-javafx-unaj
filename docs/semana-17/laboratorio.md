# Laboratorio · Semana 17

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s17-apellido
```

## Paso 1: contar rutas

1. Con la base y 3 postas, escribe todas las rutas posibles. ¿Cuántas hay? ¿Cuántas son la misma ruta recorrida al
   revés?
2. Completa la tabla de (n − 1)! hasta n = 15 y estima el tiempo con 60 ns por ruta.

## Paso 2: vecino más cercano

1. En **Semana 17 → Visualizador**, avanza el vecino más cercano. ¿En qué paso la elección voraz "se equivoca"? (Pista:
   mira el regreso a la base).
2. ¿Cambiaría el resultado si la base fuera Puno? Pruébalo en la pestaña **Java** cambiando el orden de la matriz.

## Paso 3: 2-opt

1. Avanza la mejora 2-opt. Para cada mejora, escribe qué dos tramos se quitaron y cuántos km se ahorraron.
2. ¿Por qué invertir el pedazo del medio es necesario? Dibuja la ruta antes y después.

## Paso 4: el caso

1. Abre **Caso: ruta del dron de vacunas** con las 9 postas iniciales. Compara los tres métodos.
2. Agrega postas de una en una hasta que la fuerza bruta deje de ejecutarse. ¿Cuánto tardó la última vez que sí corrió?
3. Con todas las postas, ¿cuánto mejora 2-opt al vecino más cercano? ¿Puedes saber si es el óptimo?

## Paso 5: reflexión final

Escribe en tu informe, en media página, qué técnica del curso usarías para:

1. Ordenar 1 millón de boletas de un mercado.
2. Saber si dos pueblos están conectados después de un huaico.
3. Repartir 30 entregas con 3 mototaxis.

## Paso 6: guardar el trabajo

```bash
git add docs/semana-17/img
git commit -m "Lab S17: viajante y heurísticas"
git push -u origin lab/s17-apellido
```
