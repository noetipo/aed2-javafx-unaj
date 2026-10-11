# Laboratorio · Semana 10

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s10-apellido
```

## Paso 1: Kahn a mano

1. Dibuja en papel la malla de **Semana 10 → Visualizador: Kahn y DFS** y escribe el grado de entrada de cada curso.
2. Ejecuta Kahn. Haz una tabla con una fila por curso sacado de la cola: el curso, los grados que bajan y la cola
   después.
3. Compara con la pestaña **Traza**. ¿En qué paso entra AED I a la cola? ¿Por qué no antes?

## Paso 2: DFS a mano

1. Ejecuta DFS en papel, revisando los cursos y los arcos en el orden del visualizador. Anota cuándo cada curso pasa a
   gris y cuándo a negro.
2. ¿Qué curso es el primero en terminar? ¿Por qué queda al **final** del orden?
3. Encuentra un paso en que el arco llega a un curso **negro**. Explica por qué eso no es un ciclo.
4. Compara el orden de DFS con el de Kahn. ¿Los dos respetan todos los prerrequisitos?

## Paso 3: el ciclo

1. Marca **Con un error** y ejecuta Kahn hasta el final. ¿Qué cursos quedan fuera? ¿Cuáles están en el ciclo y cuáles
   quedan fuera solo porque dependen de él?
2. Ejecuta DFS con el error. ¿Qué hay en la pila de llamadas cuando aparece el arco rojo?

## Paso 4: el caso

1. Abre **Caso: plan de estudios**. Con 3 cursos por ciclo, compara las dos reglas. ¿Qué curso se retrasa por orden
   alfabético? ¿Por qué eso cuesta un ciclo entero?
2. ¿Cuál es el mínimo de ciclos con 2 cursos por ciclo? ¿Y con 1? Explica de dónde sale cada número.
3. Comunicación tiene holgura 3. ¿Qué significa eso para un estudiante?

## Paso 5: llevar el código a tu proyecto

1. Copia la clase de la pestaña **Java** a `OrdenTopologico.java` y ejecútala.
2. Agrega al `main` los 16 cursos y los 18 prerrequisitos de la malla (ver `cursos.csv` y `prerrequisitos.csv`) y
   verifica que tu orden respete todos los arcos.

## Paso 6: guardar el trabajo

```bash
git add docs/semana-10/img
git commit -m "Lab S10: orden topológico"
git push -u origin lab/s10-apellido
```
