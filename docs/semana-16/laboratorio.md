# Laboratorio · Semana 16

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s16-apellido
```

## Paso 1: un trie a mano

1. Dibuja el trie de: inti, illa, inka, intikuna, ichu. Marca los nodos que son fin de palabra.
2. ¿Cuántos nodos tiene, sin contar la raíz? ¿Cuántas letras suman las cinco palabras?
3. Escribe esas palabras en el visualizador y compara.

## Paso 2: el visualizador

1. Con las palabras por defecto, avanza la inserción de "puka". ¿Qué nodos se reutilizan y cuáles se crean?
2. ¿Por qué "puñuy" queda a la derecha de "puriy"? (pista: el código de la letra ñ).
3. Autocompleta "pa", "p" y "pi". ¿Cuántos pasos da cada uno antes de recolectar?

## Paso 3: la marca de fin de palabra

1. Inserta "wasi" y "wasikuna". ¿Qué pasaría con "wasi" si no existiera la marca de fin de palabra?
2. En la pestaña **Java**, agrega un método `contiene(String palabra)` y pruébalo con "wasi", "wasik" y "wasikuna".

## Paso 4: el caso

1. Abre **Caso: autocompletar quechua**. Escribe letra por letra "yachay". ¿Cómo cambian las sugerencias y los nodos
   visitados?
2. ¿Qué letra inicial tiene el subárbol más grande? ¿Por qué?
3. Discusión: ¿qué estructura usarías para un diccionario de 100 000 palabras con autocompletado? ¿Y si solo
   necesitaras buscar palabras exactas?

## Paso 5: guardar el trabajo

```bash
git add docs/semana-16/img
git commit -m "Lab S16: tries"
git push -u origin lab/s16-apellido
```
