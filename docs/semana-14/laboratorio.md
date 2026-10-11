# Laboratorio · Semana 14

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s14-apellido
```

## Paso 1: la tabla a mano

1. Con los 5 productos de la feria y capacidad 10, llena en papel las filas 0, 1 y 2 de la tabla `dp`.
2. Compara con el visualizador. Para la casilla `dp[2][8]`, ¿qué valían las dos opciones?

## Paso 2: el visualizador

1. Avanza hasta la fila 4 (chuño). ¿En qué casilla llevar el chuño mejora por primera vez el valor de arriba?
2. Sigue la reconstrucción. ¿Por qué en la fila 5 (papa nativa) se sube sin llevarla?
3. Cambia mentalmente la capacidad a 8. Usando solo la tabla, ¿cuál sería la mejor ganancia? (pista: está en la
   última fila).

## Paso 3: la regla voraz

1. Calcula a mano la regla voraz (mejor ganancia por kg primero). ¿Cuánto da?
2. Inventa otro producto que haga que la regla voraz falle todavía más. Agrégalo al `main` de la pestaña **Java** y
   compruébalo.

## Paso 4: el caso

1. Abre **Caso: presupuesto participativo** con S/ 250 mil. ¿Qué proyectos elige cada regla?
2. Busca un presupuesto donde las tres reglas coincidan y otro donde la diferencia sea la mayor.
3. Discusión: además de "más beneficiarios", ¿qué otros criterios usaría una municipalidad? ¿Cómo los meterías en el
   modelo?

## Paso 5: guardar el trabajo

```bash
git add docs/semana-14/img
git commit -m "Lab S14: programación dinámica"
git push -u origin lab/s14-apellido
```
