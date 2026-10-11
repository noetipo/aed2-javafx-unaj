# Laboratorio · Semana 05

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s05-apellido
```

## Paso 1: factores de equilibrio a mano

1. Dibuja el ABB que resulta de insertar 50, 30, 70, 20, 40 y 10 **sin** reequilibrar.
2. Escribe al lado de cada nodo su altura y su fe. ¿Qué nodo tiene |fe| = 2? ¿Qué caso es?
3. En **Semana 05 → Visualizador: AVL**, pulsa **Árbol nuevo**, inserta esas claves y comprueba tu respuesta.

## Paso 2: los cuatro casos

1. Pulsa **Ejemplo: 4 rotaciones** y detente en cada paso **DESEQUILIBRIO**.
2. Completa una tabla con: clave insertada, nodo desequilibrado, su fe, caso (LL, RR, LR o RL) y nueva raíz del
   subárbol.
3. En los casos dobles, dibuja el árbol después de la **primera** rotación. ¿Por qué todavía no está equilibrado?

## Paso 3: claves ordenadas

1. Pulsa **Insertar 1…15 en orden**. ¿Qué altura tiene el resultado? ¿Y la de un ABB común con las mismas claves?
2. En la pestaña **Traza**, cuenta cuántas rotaciones hubo. ¿De qué caso fueron todas? ¿Por qué?

## Paso 4: el caso

1. Abre **Caso: diccionario con AVL** y busca `yuraq`. Compara las comparaciones del ABB y del AVL.
2. Verifica que la altura del AVL cumple la cota 1,44 · log₂(n + 2).

## Paso 5: llevar el código a tu proyecto

1. Copia la clase de la pestaña **Java** a `ArbolAVL.java` y ejecútala.
2. Cambia el `main` para insertar 1000 claves en orden y muestra la altura. Compárala con log₂(1000).

## Paso 6: guardar el trabajo

```bash
git add docs/semana-05/img
git commit -m "Lab S05: rotaciones AVL"
git push -u origin lab/s05-apellido
```
