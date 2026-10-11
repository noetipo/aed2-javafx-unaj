# Tarea · Semana 10

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana10/tarea/TareaSemana10.java`
**Rama:** `tarea/s10-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

Los vértices son `0..n-1` y cada fila `{u, v}` de `arcos` es el arco u → v ("u es prerrequisito de v").

1. **`ordenTopologico(n, arcos)`**: un orden topológico con Kahn usando un arreglo `grado[]`.
   - Cualquier orden válido sirve.
   - Si hay un ciclo, retorna un arreglo vacío.
2. **`tieneCiclo(n, arcos)`**: detecta ciclos con **DFS y tres colores**.
   - Un arco a un vértice gris es un ciclo. Un arco a un vértice negro, no.
   - Un arco v → v también es un ciclo.
3. **`minimoCiclos(n, arcos)`**: cuántos ciclos académicos hacen falta si en cada uno se pueden llevar todos los
   cursos disponibles. Es el número de vértices del camino más largo.
   - Retorna 0 si n = 0 y -1 si hay un ciclo.
4. **Informe** (en la descripción del PR, máximo una página):
   - Da un grafo de 4 vértices con exactamente **dos** órdenes topológicos y escríbelos.
   - ¿Por qué `tieneCiclo` sería incorrecto si tratara a los vértices negros igual que a los grises? Muestra un
     contraejemplo.
   - Con un tope de 2 cursos por ciclo, da una malla pequeña donde la regla "por orden alfabético" necesite más ciclos
     que "primero la cadena más larga".

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| `ordenTopologico` | Kahn con arreglos, O(V + E), detecta ciclos y vértices sin arcos | Falla con ciclos o vértices aislados | No funciona | 6 |
| `tieneCiclo` | DFS con tres colores; rombos, arcos a sí mismo y varias componentes | Confunde negro con gris | No funciona | 5 |
| `minimoCiclos` | Programación sobre el orden topológico; casos n = 0 y con ciclo | Falla en los bordes | No funciona | 4 |
| Informe | Ejemplos correctos y justificados | Incompleto | Ausente | 3 |
| Git y PR | Rama `tarea/s10-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
