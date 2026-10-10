# Laboratorio · Semana 03

Duración estimada: 90 minutos.

## Paso 0: actualizar el repositorio

```bash
git checkout main
git fetch upstream
git merge upstream/main
git checkout -b lab/s03-apellido
```

## Paso 1: calcular un hash a mano

1. Calcula en papel `hash("SOL", 7)` con el método de Horner ('S' = 83, 'O' = 79, 'L' = 76).
2. Abre **Semana 03 → Visualizador: tabla hash**, pulsa **Tabla nueva**, escribe `SOL` y pulsa **Insertar**.
3. En el paso de la línea 2, compara tu cálculo con el recuadro **Se evalúa**.

## Paso 2: colisiones

1. Pulsa **Cargar ejemplo** y avanza hasta la inserción de `JUL-347`. ¿Con qué clave colisiona? ¿En qué cubeta?
2. Busca dos claves nuevas que caigan en la **misma** cubeta (prueba códigos parecidos) e insértalas.
3. Busca la segunda clave y cuenta las comparaciones en la pestaña **Traza**.

## Paso 3: rehash

1. Con **Cargar ejemplo**, detente en el paso **REHASH**. Anota en una tabla en qué cubeta estaba cada clave antes
   y después del rehash.
2. ¿Por qué cambian de cubeta si las claves son las mismas?
3. Con m = 15 y el límite 0,75, ¿cuántas claves más se pueden insertar antes del siguiente rehash?

## Paso 4: tabla hash frente a búsqueda lineal

1. Abre **Caso: padrón de mototaxis** y busca tres códigos que existan y uno que no.
2. Anota las comparaciones de cada método. ¿Cuál es el peor caso de la búsqueda lineal?
3. Observa el gráfico de cadenas: ¿hay cubetas vacías? ¿Cuál es la cadena más larga?

## Paso 5: llevar el código a tu proyecto

1. En el visualizador, pestaña **Java**, pulsa **Copiar código**.
2. Pégalo en un archivo `TablaHash.java` en una carpeta vacía y ejecuta:

```bash
javac TablaHash.java
java TablaHash
```

3. Modifica el `main` para insertar tu propio padrón de 10 códigos y buscar en él.

## Paso 6: guardar el trabajo

```bash
git add docs/semana-03/img
git commit -m "Lab S03: hash, colisiones y rehash"
git push -u origin lab/s03-apellido
```
