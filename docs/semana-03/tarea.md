# Tarea · Semana 03

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana03/tarea/TablaSondeoLineal.java`
**Rama:** `tarea/s03-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

Implementa una tabla hash con **direccionamiento abierto y sondeo lineal**. Cada casilla guarda a lo más una clave;
si `hash(clave)` está ocupada, se prueba `(i + 1) mod m`, `(i + 2) mod m`, etc.

1. **`insertar(String clave)`**: retorna `false` si ya existía y `true` si la insertó. Lanza `IllegalStateException`
   si la tabla está llena.
2. **`buscar(String clave)`**: se detiene al encontrar la clave o una casilla `null`, y nunca da más de m pasos.
3. **`sondeos(String clave)`**: cantidad de casillas revisadas al buscar.
4. **Informe** (en la descripción del PR, máximo una página):
   - Explica por qué `buscar` puede detenerse en la primera casilla `null`.
   - Inserta los 6 códigos del visualizador en una tabla de sondeo lineal con m = 7. Dibuja la tabla resultante y
     compárala con la de encadenamiento. ¿Qué es el **agrupamiento primario**?
   - ¿Qué problema aparecería si quisieras **eliminar** una clave poniendo `null` en su casilla?

No uses `HashMap`, `HashSet` ni otras colecciones: solo el arreglo `tabla`. No cambies las firmas.

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| `insertar` | Sondeo con vuelta (mod m), rechaza duplicados y detecta la tabla llena | Falla en la vuelta o con la tabla llena | No funciona | 6 |
| `buscar` | Se detiene en `null` o tras m pasos, y pasa todas las pruebas | Recorre toda la tabla siempre | No funciona | 4 |
| `sondeos` | Cuenta exacta en búsquedas exitosas y fallidas | Error de ±1 | No implementado | 3 |
| Informe | Las tres preguntas correctas, con la tabla dibujada | Respuestas incompletas | Ausente | 5 |
| Git y PR | Rama `tarea/s03-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
