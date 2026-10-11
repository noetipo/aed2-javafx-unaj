# Laboratorio · Semana 06

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s06-apellido
```

## Paso 1: árbol y arreglo

1. Dibuja el árbol completo que representa el arreglo `[5, 20, 10, 40, 30, 50, 15]`. ¿Cumple la regla del montículo?
2. ¿Cuál es el padre de la posición 6? ¿Y los hijos de la posición 1?
3. Abre **Semana 06 → Visualizador: montículo y heapsort** (se carga ese ejemplo) y verifica.

## Paso 2: insertar y extraer

1. Inserta 25 y luego 1. Para cada uno, anota en la pestaña **Traza** cuántas comparaciones hizo.
2. Pulsa **Extraer mínimo** tres veces. ¿Por qué el último elemento pasa a la raíz y no, por ejemplo, el hijo menor?
3. ¿Qué pasaría si al hundir se eligiera el hijo **mayor**? Busca un ejemplo donde se rompa la regla.

## Paso 3: heapsort

1. Pulsa **Heapsort** y detente al terminar la fase 1 (el primer paso **UBICA_MAYOR**). Escribe el arreglo: ¿es un
   montículo de máximos?
2. ¿Por qué el for de construcción empieza en n/2 − 1 y no en n − 1?
3. Cuenta cuántos pasos **INTERCAMBIO** hubo en total. Compáralo con burbuja (semana 01) para los mismos 10 números.

## Paso 4: el caso de emergencias

1. Abre **Caso: emergencias con triaje** y mueve el control hasta el llamado 8. ¿Quién está en la raíz y por qué?
2. Busca un paciente P5 que haya esperado más de una hora. ¿A cuántos pacientes más urgentes dejó pasar?
3. Compara la espera total de ambas políticas en el gráfico y en el resumen. ¿Por qué es la misma?

## Paso 5: llevar el código a tu proyecto

1. Copia la clase de la pestaña **Java** a `Monticulo.java` y ejecútala.
2. Reescribe el `main` usando `java.util.PriorityQueue<Integer>` en lugar de tu clase y compara la salida.

## Paso 6: guardar el trabajo

```bash
git add docs/semana-06/img
git commit -m "Lab S06: montículos, heapsort y triaje"
git push -u origin lab/s06-apellido
```
