# Semana 10 · Grafos dirigidos: orden topológico

## Objetivos

- Modelar dependencias ("A va antes que B") con un **grafo dirigido**.
- Entender qué es un **orden topológico** y por qué solo existe si el grafo **no tiene ciclos** (un DAG).
- Ejecutar a mano y programar **Kahn** (grados de entrada y una cola) y el orden con **DFS** (postorden invertido).
- Detectar ciclos con los colores blanco, gris y negro, y calcular el **camino crítico**.

## 1. El problema: la malla curricular

Para llevar AED II hay que aprobar AED I, y para AED I hay que aprobar POO y Matemática Discreta. Cada prerrequisito es
un **arco dirigido** `requisito → curso`. El curso usa una malla **ficticia y simplificada** de 16 cursos y 18
prerrequisitos (no es el plan oficial de ninguna universidad).

Un **orden topológico** es una lista de todos los vértices en la que, para cada arco u → v, u aparece **antes** que v.
Es decir, un orden para llevar los cursos que nunca rompe un prerrequisito.

- Casi siempre hay **varios** órdenes válidos.
- **No existe** si hay un ciclo. Si AED I pide AED II y AED II pide AED I, ninguno puede ir primero.
- Un grafo dirigido sin ciclos se llama **DAG** (*directed acyclic graph*).

En un grafo dirigido, el **grado de entrada** de v es cuántos arcos llegan a v (sus prerrequisitos) y el **grado de
salida** es cuántos salen (los cursos que lo piden).

## 2. Kahn: primero lo que no espera a nadie

1. Calcular el grado de entrada de cada vértice.
2. Poner en una cola los de grado 0: no tienen prerrequisitos.
3. Mientras la cola no esté vacía:
   - sacar v y agregarlo al **final** del orden;
   - por cada arco v → w, restar 1 al grado de w ("un prerrequisito menos");
   - si el grado de w llega a 0, w ya no espera a nadie y entra a la cola.
4. Si al final el orden tiene **menos de n** vértices, los que faltan están atrapados en un **ciclo**.

En la malla, Kahn empieza con Matemática, Programación y Comunicación, que no tienen prerrequisitos.

## 3. DFS: el último en terminar va primero

`visitar(v)` recorre en profundidad todo lo que depende de v. Cuando termina, inserta v al **inicio** del orden. Así v
queda delante de todo lo que depende de él. El orden final es el **postorden invertido**.

Cada vértice tiene un color:

| Color | Significado |
|---|---|
| Blanco | Todavía no se visitó |
| Gris | Está en la **pila de llamadas** (`visitar` empezó y no terminó) |
| Negro | Terminó: ya está en el orden |

- Si un arco llega a un vértice **gris**, es un **arco de regreso**: ese vértice lleva al actual y el actual vuelve a
  él. Eso es un **ciclo**.
- Llegar a un vértice **negro** NO es un ciclo. Por ejemplo, Ing. Software se alcanza desde POO por dos caminos.

Kahn y DFS dan órdenes **distintos**, ambos válidos. Los dos cuestan **O(V + E)**.

| | Kahn | DFS |
|---|---|---|
| Estructura | Cola + grados de entrada | Pila de llamadas (recursión) |
| Arma el orden | Agregando al final | Insertando al inicio |
| Detecta un ciclo cuando | Quedan vértices fuera del orden | Un arco llega a un vértice gris |
| Ventaja | Dice qué está disponible "ahora" (sirve para planificar por etapas) | Corto de escribir; encuentra el arco que forma el ciclo |

## 4. Camino crítico y holgura

Si en cada ciclo académico se pueden llevar **todos** los cursos disponibles, ¿cuántos ciclos hacen falta? Tantos como
cursos tenga la **cadena de prerrequisitos más larga**, el **camino crítico**. En la malla son 6:

> Matemática → Discreta → AED I → AED II → Int. Artificial → Tesis

Se calcula recorriendo un orden topológico: `nivel[w] = max(nivel[w], nivel[v] + 1)` por cada arco v → w.

La **holgura** de un curso es cuántos ciclos se puede atrasar sin atrasar el egreso. Los cursos con holgura 0 están en
algún camino crítico: Programación y POO también lo están, porque llevan a AED I. Comunicación tiene holgura 3.

## 5. Con un tope de cursos por ciclo

Si en cada ciclo se puede llevar a lo más k cursos, ya no basta el orden topológico: hay que **elegir** entre los
disponibles. Encontrar el plan óptimo en general es un problema difícil. Una regla simple funciona muy bien: **primero
los cursos con la cadena más larga por delante**.

Con k = 3 en la malla:

| Regla | Ciclos |
|---|---|
| Por orden alfabético | 7 (POO queda para el ciclo 3 y retrasa toda su cadena) |
| Primero la cadena más larga | 6 (el mínimo posible) |

## 6. Otros usos

- Compilar módulos en el orden correcto. Maven ordena así los módulos de un proyecto grande.
- Ordenar las tareas de una obra o de un proyecto (método de la ruta crítica, CPM).
- Evaluar celdas de una hoja de cálculo que dependen de otras. Si hay una referencia circular, es un ciclo.

## 7. En la aplicación

- **Teoría**: este documento.
- **Visualizador: Kahn y DFS**: la malla dibujada de izquierda a derecha por nivel.
  - **En Kahn**, sobre cada curso se ve su grado `g`, y la posición `#n` cuando ya entró al orden. Abajo aparece la
    cola.
  - **En DFS**, los cursos en gris están en la pila de llamadas y los arcos en verde son los que llamaron a
    `visitar`.
  - Con la casilla **Con un error** se agrega el arco AED II → AED I. Kahn deja 6 cursos fuera y DFS encuentra el arco
    de regreso (en rojo).
  - **Se evalúa** explica cada grado que baja, cada llamada y a quién regresa cada `visitar` al terminar.
  - **Java** es una clase `OrdenTopologico` completa con `kahn`, `ordenDfs` y un `main` que también prueba el ciclo.
- **Caso: plan de estudios**: elige el máximo de cursos por ciclo y la regla de prioridad.
  - En el mapa, cada curso muestra en qué ciclo se lleva, y los cursos sin holgura aparecen en rojo.
  - La tabla lista el plan.

## 8. Código relevante

- `semana10/modelo/GrafoDirigido.java`: Kahn, DFS, ciclos, niveles y camino crítico, normales y paso a paso
- `semana10/modelo/MallaCurricular.java`: carga `cursos.csv` y `prerrequisitos.csv`
- `semana10/caso/PlanEstudios.java`: plan por ciclos con tope y prioridad, holgura
