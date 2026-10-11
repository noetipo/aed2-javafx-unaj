# Laboratorio · Semana 13

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s13-apellido
```

## Paso 1: 4 reinas a mano

1. Dibuja el árbol de decisiones de 4 reinas: la raíz es la fila 0 y cada rama es una columna. Tacha las ramas
   atacadas.
2. ¿Cuántas casillas probaste hasta la primera solución? Compara con el contador del visualizador (n = 4).

## Paso 2: el visualizador

1. Con n = 6, avanza hasta el primer paso naranja (se quita una reina). ¿Qué fila se quedó sin salida y por qué?
2. Busca un paso donde la casilla está atacada por la **diagonal**. Escribe la cuenta `|tablero[f] − col| == fila − f`
   con los números.
3. Compara la cantidad de casillas probadas con n = 5, 6 y 7. ¿Por qué n = 6 necesita muchas más que n = 7?

## Paso 3: sin deshacer

1. Copia la clase de la pestaña **Java** a `NReinas.java` y ejecútala.
2. Borra la línea `tablero[fila] = -1;` y vuelve a ejecutar con n = 6. ¿Sigue funcionando? Explica qué pasa (pista:
   revisa qué lee `esSegura`).
3. Modifica la clase para que cuente **todas** las soluciones de 8 reinas (deben ser 92).

## Paso 4: el caso

1. Abre **Caso: horario de exámenes** con la regla voraz. ¿Qué curso obliga a abrir el quinto día?
2. Cambia a backtracking con 2 días. ¿Por qué es imposible? Encuentra un triángulo (tres cursos en conflicto mutuo).
3. ¿Cuántas asignaciones prueba el backtracking con 3, 4 y 5 días? ¿Por qué con más días encuentra antes una
   solución?

## Paso 5: guardar el trabajo

```bash
git add docs/semana-13/img
git commit -m "Lab S13: backtracking"
git push -u origin lab/s13-apellido
```
