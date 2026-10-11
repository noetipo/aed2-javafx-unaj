# Semana 13 · Backtracking

## Objetivos

- Reconocer problemas de **búsqueda**: hay que elegir una combinación entre muchísimas posibles.
- Programar **backtracking**: elegir, probar recursivamente y **deshacer** si no funcionó.
- Usar **podas** para no explorar ramas que ya se sabe que fracasan.
- Comparar backtracking (exacto, pero puede ser lento) con una regla **voraz** (rápida, pero puede fallar).

## 1. La idea

Muchos problemas se resuelven tomando decisiones una tras otra: dónde va la reina de esta fila, qué día va este
examen, si llevo o no este saco. Backtracking recorre esas decisiones como un **árbol**:

1. **Elegir** una opción para la decisión actual.
2. Si no viola ninguna regla, **avanzar** recursivamente a la siguiente decisión.
3. Si más adelante se llega a un callejón sin salida, **volver atrás** (*backtrack*), **deshacer** la elección y
   probar la siguiente opción.

```text
resolver(decisión):
    si ya se tomaron todas: ¡solución!
    para cada opción:
        si es válida: elegirla; si resolver(decisión + 1): éxito; deshacerla
    fracaso: volver a la decisión anterior
```

La línea **deshacerla** es la que distingue al backtracking. En general, sin ella la elección fallida queda "pegada" y
contamina las pruebas siguientes. Por ejemplo, en `contarCaminos` (tarea), una casilla que quedó marcada como visitada
bloquea caminos que sí existen. (En N reinas el daño no se nota por cómo se revisan los ataques: el laboratorio lo
explora.)

## 2. N reinas

Hay que poner n reinas en un tablero de n × n sin que ninguna ataque a otra (misma fila, columna o diagonal).

- Cada fila lleva exactamente una reina, así que la decisión k es **en qué columna va la reina de la fila k**.
- Una casilla (fila, col) está atacada por la reina de la fila f si `tablero[f] == col` (misma columna) o si
  `|tablero[f] − col| == fila − f` (misma diagonal).

Con n = 6, la primera solución es `[1, 3, 5, 0, 2, 4]`. La búsqueda prueba casillas, se queda sin salida varias veces
y vuelve a filas de arriba. Con n = 8 hay **92** soluciones; la primera es `[0, 4, 7, 5, 2, 6, 1, 3]`.

**Poda**: no se prueba la fila siguiente si la casilla ya está atacada. Sin poda, habría que revisar
n<sup>n</sup> tableros: con n = 8 son más de 16 millones. Con poda se prueban unos pocos miles de casillas.

## 3. Costo

En el peor caso, backtracking es **exponencial**: puede explorar todo el árbol de decisiones. Sirve cuando:

- el problema es pequeño o mediano;
- las podas eliminan muchas ramas;
- hace falta la solución **exacta** o **todas** las soluciones.

## 4. Coloreo de grafos: el horario de exámenes

Dos cursos que comparten estudiantes no pueden tomar el examen el mismo día. Si cada curso es un vértice, cada
conflicto es una arista y cada día un color, el problema es **colorear el grafo** con la menor cantidad de colores.

- **Regla voraz**: en el orden de la lista, cada curso va al primer día libre. Es rápida y nunca vuelve atrás, pero en
  el caso de esta semana usa **5 días**.
- **Backtracking**: con k días, prueba para cada curso los días posibles y deshace cuando un curso se queda sin día. Con
  3 días encuentra un calendario; con 2 demuestra que es **imposible**. El mínimo es **3**.

Encontrar el mínimo de colores es un problema **NP-difícil**: no se conoce ningún algoritmo rápido para grafos
grandes (semana 17). Por eso en la práctica se combinan reglas voraces con búsqueda limitada.

## 5. En la aplicación

- **Teoría**: este documento.
- **Visualizador: N reinas**: elige n (de 4 a 8) y avanza.
  - Verde: la reina se coloca. Rojo: la casilla está atacada, y una línea la une con la reina que ataca. Naranja: se
    quita la reina (backtracking).
  - **Se evalúa** explica cada ataque (columna o diagonal, con las distancias), cada llamada `colocar(fila + 1)` y a
    dónde regresa la ejecución cuando una fila no tiene salida.
  - **Java** es una clase `NReinas` completa con `resolver`, `colocar`, `esSegura` y un `main` que dibuja el tablero
    de 8 × 8.
- **Caso: horario de exámenes**: el grafo de conflictos de 10 cursos, coloreado con la regla voraz o con backtracking
  (elige cuántos días). La tabla arma el calendario.

## 6. Código relevante

- `semana13/modelo/NReinas.java`: backtracking normal y paso a paso
- `semana13/caso/HorarioExamenes.java`: coloreo voraz y con backtracking
- `src/main/resources/.../semana13/`: cursos y conflictos (ficticios)
