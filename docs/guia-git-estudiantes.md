# Guía de Git para estudiantes

Esta guía explica cómo trabajar con el repositorio del curso y cómo entregar cada tarea con un **Pull Request (PR)**.

## 1. Configuración inicial (una sola vez)

```bash
git config --global user.name "Nombres Apellidos"
git config --global user.email "tu-correo@unaj.edu.pe"
```

## 2. Hacer fork y clonar

1. Entra al repositorio del docente en GitHub (https://github.com/noetipo/aed2-javafx-unaj) y pulsa **Fork**. Así tendrás tu copia en `github.com/<tu-usuario>/aed2-javafx-unaj`.
2. Clona **tu fork**, no el del docente:

```bash
git clone https://github.com/<tu-usuario>/aed2-javafx-unaj.git
cd aed2-javafx-unaj
```

3. Registra el repositorio del docente como `upstream` para recibir las semanas nuevas:

```bash
git remote add upstream https://github.com/noetipo/aed2-javafx-unaj.git
git remote -v
```

## 3. Traer el material de cada semana

Haz esto antes de empezar cada tarea:

```bash
git checkout main
git fetch upstream
git merge upstream/main
git push origin main
```

## 4. Una rama por tarea

Usa el formato **`tarea/sXX-apellido`**, en minúsculas y sin tildes:

```bash
git checkout -b tarea/s01-quispe
```

Trabaja solo en `src/main/java/pe/edu/unaj/aed2/semanaXX/tarea/` (y en `docs/semana-XX/img/` si se piden capturas).

## 5. Probar y confirmar cambios

```bash
./mvnw test -Dgroups=tarea -DexcludedGroups=none   # todas las pruebas deben pasar
git status
git add src/main/java/pe/edu/unaj/aed2/semana01/tarea
git commit -m "Tarea semana 01: seleccion y busqueda binaria"
git push -u origin tarea/s01-quispe
```

Haz commits pequeños y con mensajes claros. Varios commits por tarea es lo normal.

## 6. Abrir el Pull Request

1. En GitHub, en tu fork, aparecerá el botón **Compare & pull request**.
2. Base: `noetipo/aed2-javafx-unaj` · `main`. Compare: `tu-usuario/aed2-javafx-unaj` · `tarea/s01-quispe`.
3. Título: `Tarea S01 - Apellido Nombre`.
4. En la descripción indica qué completaste, qué te costó y la salida de las pruebas.

El docente revisará y comentará en el PR. Para corregir algo, haz nuevos commits en **la misma rama** y vuelve a hacer `git push`; el PR se actualiza solo.

## 7. Problemas frecuentes

| Problema | Solución |
|---|---|
| `rejected - non-fast-forward` al hacer push | `git pull --rebase origin <tu-rama>` y vuelve a hacer push |
| Hiciste cambios en `main` por error | `git checkout -b tarea/sXX-apellido` (te llevas los cambios) y luego `git checkout main && git reset --hard upstream/main` |
| Conflicto al hacer merge de upstream | Abre los archivos marcados, elige el contenido correcto, quita las marcas `<<<<<<<`, luego `git add` y `git commit` |
| `./mvnw: Permission denied` | `chmod +x mvnw` |
