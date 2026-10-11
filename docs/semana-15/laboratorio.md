# Laboratorio · Semana 15

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s15-apellido
```

## Paso 1: Huffman a mano

1. Con el texto "juliaca", cuenta las frecuencias y construye en papel el árbol de Huffman. Si hay empate, saca
   primero la hoja que va antes en orden alfabético; un árbol recién creado va detrás de los que ya estaban en la
   cola con su misma frecuencia (así lo hace el visualizador).
2. Escribe el código de cada letra y cuántos bits ocupa el texto. Compáralo con ASCII.
3. Escribe "juliaca" en el visualizador y compara tu árbol con el suyo.

## Paso 2: el visualizador

1. Con "pachamama paqarin", ¿qué dos árboles se unen en la quinta unión? ¿Por qué esos?
2. ¿Por qué la `a` tiene el código más corto? ¿Cuántos bits ahorra ella sola frente a un código fijo de 4 bits?
3. Prueba un texto con un solo carácter repetido ("aaaa"). ¿Qué código recibe? ¿Por qué el programa le da "0"?

## Paso 3: decodificar

1. Con los códigos de "pachamama paqarin", decodifica a mano los primeros 12 bits del texto codificado.
2. Cambia un bit en medio de la secuencia en el **Caso: compresión de mensajes** (cópiala y modifícala en papel).
   ¿Qué le pasa al resto del mensaje? ¿Por qué un solo error afecta tanto?

## Paso 4: el caso

1. Compara el ahorro en quechua, aimara y castellano. ¿Cuál se comprime más y por qué?
2. Escribe un texto que se comprima más del 80 % y otro que casi no se comprima frente al código fijo.

## Paso 5: llevar el código a tu proyecto

1. Copia la clase de la pestaña **Java** a `Huffman.java` y ejecútala.
2. Agrégale un método `decodificar(String bits, Nodo raiz)` y comprueba que recupera el texto.

## Paso 6: guardar el trabajo

```bash
git add docs/semana-15/img
git commit -m "Lab S15: Huffman"
git push -u origin lab/s15-apellido
```
