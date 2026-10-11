# Laboratorio · Semana 12

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s12-apellido
```

## Paso 1: flujo máximo a mano

1. Dibuja en papel la red de **Semana 12 → Visualizador: Edmonds–Karp** con sus capacidades.
2. Ejecuta Edmonds–Karp. Para cada vuelta, escribe el camino (el de menos tramos), su cuello de botella y el total.
3. Dibuja la **red residual** después de la vuelta 3. ¿Qué arcos hacia atrás aparecen?

## Paso 2: devolver flujo

1. En el visualizador, avanza hasta la vuelta 4. ¿Qué arco se recorre al revés? ¿Cuántos camiones se devuelven?
2. Explica con tus palabras por qué, sin esa devolución, el total se quedaría en 13.

## Paso 3: el corte mínimo

1. Al final, ¿qué pueblos quedan en el lado S? ¿Qué arcos forman el corte?
2. Encuentra otro corte (otra separación entre Ayaviri y Juliaca) y calcula su capacidad. ¿Es mayor o igual a 16?

## Paso 4: el caso

1. Abre **Caso: leche a la planta**. Con la producción inicial, ¿cuántos camiones no llegan?
2. ¿Por qué ampliar Azángaro → Huancané no sirve, aunque esté lleno? Explícalo con el corte mínimo.
3. Baja la producción de Ayaviri poco a poco. ¿Desde qué valor llega toda la leche de Ayaviri? ¿Por qué Putina sigue
   perdiendo 2 camiones aunque bajes la de Ayaviri a 0?

## Paso 5: llevar el código a tu proyecto

1. Copia la clase de la pestaña **Java** a `FlujoMaximo.java` y ejecútala.
2. Modifica el `main` para que también imprima el corte mínimo (los vértices alcanzables desde s en la red
   residual al terminar).

## Paso 6: guardar el trabajo

```bash
git add docs/semana-12/img
git commit -m "Lab S12: flujo máximo"
git push -u origin lab/s12-apellido
```
