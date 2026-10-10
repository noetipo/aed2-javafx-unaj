# Laboratorio · Semana 04

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s04-apellido
```

## Paso 1: construir un ABB a mano

1. Dibuja en papel el ABB que resulta de insertar 50, 30, 70, 20, 40, 60, 80, 35, 45 y 65, en ese orden.
2. Abre **Semana 04 → Visualizador: ABB** (se carga ese mismo ejemplo) y compara.
3. Escribe el recorrido **inorden**, y verifícalo con el botón **Inorden**.

## Paso 2: seguir la recursión de insertar

1. Inserta 55. En la pestaña **Traza**, anota la columna `pila` en cada paso.
2. ¿En qué paso se engancha el nodo 55 al árbol? ¿Qué hacen las llamadas de los niveles de arriba con el nodo que
   reciben?

## Paso 3: los tres casos de eliminar

1. Con el árbol de ejemplo, elimina 20 (hoja). Vuelve a cargar el ejemplo y elimina 60 (un hijo). Repite con 30
   (dos hijos).
2. Para 30: ¿cuál es el sucesor? ¿Por qué el sucesor nunca tiene hijo izquierdo?
3. Elimina la raíz (50) y explica por qué cambia la raíz del árbol.

## Paso 4: forma y costo

1. Abre **Caso: diccionario quechua**. Anota la altura de cada árbol y el promedio de comparaciones.
2. Busca `yuraq` y `ñan`. ¿Cuántas comparaciones hace cada árbol? ¿Por qué `ñan` está al final del orden?
3. Busca `inti`: el árbol mezclado hace **más** comparaciones que el alfabético. ¿Contradice eso el promedio?
   ¿Qué dice el promedio que no dice una sola búsqueda?
4. En el visualizador, pulsa **Árbol nuevo** e inserta 10, 20, 30, 40, 50. ¿Qué forma tiene? ¿Qué orden de inserción
   de esas mismas claves da un árbol de altura 3?

## Paso 5: llevar el código a tu proyecto

1. En la pestaña **Java**, pulsa **Copiar código** y pégalo en `ArbolBusqueda.java`.
2. Ejecútalo con `javac ArbolBusqueda.java` y `java ArbolBusqueda`.
3. Agrega un método `int minimo()` iterativo y pruébalo en el `main`.

## Paso 6: guardar el trabajo

```bash
git add docs/semana-04/img
git commit -m "Lab S04: ABB, eliminación y forma del árbol"
git push -u origin lab/s04-apellido
```
