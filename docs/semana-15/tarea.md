# Tarea · Semana 15

**Archivo a completar:** `src/main/java/pe/edu/unaj/aed2/semana15/tarea/TareaSemana15.java`
**Rama:** `tarea/s15-apellido` · **Entrega:** Pull Request (ver [guía de Git](../guia-git-estudiantes.md))

## Enunciado

No modifiques los arreglos que recibes.

1. **`maxActividades(inicio, fin)`**: las reservas del auditorio. ¿Cuántas actividades sin superponerse se pueden
   aceptar como máximo? Una puede empezar justo cuando termina otra. Usa la regla voraz "la que termina primero".
2. **`mochilaFraccionaria(peso, valor, capacidad)`**: se puede llevar una **parte** de cada producto. Retorna el mayor
   valor posible. Aquí sí sirve la regla de valor/peso.
3. **`decodificar(bits, codigos)`**: decodifica con un código libre de prefijos. Lanza `IllegalArgumentException` si
   los bits no forman una secuencia válida (por ejemplo, si sobra un pedazo al final).
4. **Informe** (en la descripción del PR, máximo una página):
   - Da un ejemplo donde las reglas "la que **empieza** primero" y "la **más corta**" fallen en `maxActividades`.
   - Explica con un argumento de intercambio por qué "la que **termina** primero" nunca falla.
   - ¿Por qué la regla valor/peso funciona en la mochila fraccionaria y no en la 0/1?

## Verificación

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none
```

## Rúbrica (20 puntos)

| Criterio | Excelente | Suficiente | Insuficiente | Pts |
|---|---|---|---|---|
| `maxActividades` | Regla correcta en O(n log n), sin modificar los arreglos | Correcto pero modifica la entrada | No funciona | 5 |
| `mochilaFraccionaria` | Fracción del último producto, capacidad 0 y "todo cabe" | Falla en los bordes | No funciona | 5 |
| `decodificar` | Mapa invertido o árbol; detecta bits sobrantes | No detecta errores | No funciona | 4 |
| Informe | Contraejemplos y argumento de intercambio correctos | Incompleto | Ausente | 4 |
| Git y PR | Rama `tarea/s15-apellido`, commits claros, PR con el título correcto | Commits poco descriptivos | Entrega fuera de PR | 2 |
