# Proyecto #2 | Programacion III
## Integrantes
- Joanfer Hidalgo Chaves
- Justin Angulo Artavia
- Santiago Hernandez Chaves

## Setup
**Version JDK**: ```26.0.2``` (minimo 21)

## Preparacion
1. Crear la base de datos con `base_datos.sql` en MySQL.
2. Copiar `backend/config.properties.example` a `backend/config.properties` y poner las credenciales de MySQL. Copiar `frontend/config.properties.example` a `frontend/config.properties` y poner la IP del Backend.

## Maven
Compilar desde la raiz del proyecto:
```
mvn -DskipTests package
```

**Linux** (desde la raiz)

Backend:
```
java -cp backend/target/backend-1.0-SNAPSHOT.jar:common/target/common-1.0-SNAPSHOT.jar:$HOME/.m2/repository/com/mysql/mysql-connector-j/9.0.0/mysql-connector-j-9.0.0.jar cr.ac.una.reservas.backend.MainBackend
```
Frontend (una terminal por cada uno):
```
java -cp frontend/target/frontend-1.0-SNAPSHOT.jar:common/target/common-1.0-SNAPSHOT.jar:$HOME/.m2/repository/com/intellij/forms_rt/7.0.3/forms_rt-7.0.3.jar cr.ac.una.reservas.Main
```

**Windows** (Simbolo del sistema, desde la raiz)

Backend:
```
java -cp "backend\target\backend-1.0-SNAPSHOT.jar;common\target\common-1.0-SNAPSHOT.jar;%USERPROFILE%\.m2\repository\com\mysql\mysql-connector-j\9.0.0\mysql-connector-j-9.0.0.jar" cr.ac.una.reservas.backend.MainBackend
```
Frontend (una ventana por cada uno):
```
java -cp "frontend\target\frontend-1.0-SNAPSHOT.jar;common\target\common-1.0-SNAPSHOT.jar;%USERPROFILE%\.m2\repository\com\intellij\forms_rt\7.0.3\forms_rt-7.0.3.jar" cr.ac.una.reservas.Main
```

Pruebas:
```
mvn verify
```

## IntelliJ IDEA
1. `File > Open` y elegir el `pom.xml` de la raiz.
2. `Settings > Editor > GUI Designer > Generate GUI into: Java source code`.
3. Ejecutar `cr.ac.una.reservas.backend.MainBackend` (modulo `backend`).
4. Ejecutar `cr.ac.una.reservas.Main` (modulo `frontend`). Para abrir varios, activar `Allow multiple instances` en su configuracion de ejecucion.

## Usuarios
`admin` / `123` (administrador). `111`, `222` y `123` con clave igual a su ID (funcionarios).
