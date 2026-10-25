# Proyecto #2 | Programacion III
## Integrantes
- Joanfer Hidalgo Chaves
- Justin Angulo Artavia
- Santiago Hernandez Chaves

## Setup
**Version JDK**: 21 o superior (`release 21` en los tres modulos). Desarrollado con ```26.0.2```

## Descripcion
Sistema de reservas distribuido: varios frontends Swing (MVC) conectados por un solo socket a un backend sin interfaz, que es el unico que habla con MySQL. Cada reserva nueva se notifica a los demas frontends con sesion iniciada, que la muestran en el area de alertas.

```
Frontend (Swing, MVC)  --TCP-->  Backend (servidor)  --JDBC-->  MySQL
```

## Modulos
| Modulo | Contenido |
|---|---|
| `common` | modelo, interfaces de service, protocolo, excepciones |
| `backend` | servidor, services, persistencia MySQL y en memoria, extractor de IA |
| `frontend` | pantallas, controllers, proxies, reportes PDF, alertas |
| `integracion` | solo pruebas: sockets reales y controllers contra el backend |

El frontend no depende del backend ni conoce la base de datos.

## Compilacion
- **Maven**: `mvn verify` desde la raiz. No necesita MySQL: las pruebas de integracion usan DAO en memoria sobre un socket real.
- **IntelliJ**: activar *Settings > Editor > GUI Designer > Generate GUI into: Java source code*, para que los `.form` generen el codigo de la interfaz y Maven compile igual.

## Ejecucion
1. Crear la base: `mariadb -uroot -p < base_datos.sql` (o `mysql`). Crea `reservas` y `reservas_test` con datos de muestra.
2. Copiar `backend/config.properties.example` a `backend/config.properties` y poner el puerto y las credenciales de MySQL.
3. Backend (desde la carpeta `backend`, la opcion `-Dreservas.dao=mysql` es obligatoria; sin ella usa datos en memoria): `java -Dreservas.dao=mysql -cp target/backend-1.0-SNAPSHOT.jar:../common/target/common-1.0-SNAPSHOT.jar:<mysql-connector-j.jar> cr.ac.una.reservas.backend.MainBackend`. Antes: `mvn -DskipTests package` desde la raiz.
4. Copiar `frontend/config.properties.example` a `frontend/config.properties` con la IP y el puerto del backend, y ejecutar `cr.ac.una.reservas.Main` del modulo `frontend`. Se pueden abrir varios frontends.
5. Usuarios de muestra: `admin`/`123`, `111`/`111`, `222`/`222`, `123`/`123`. La clave de Gemini se configura en la pestana Configuracion IA (la ve solo el administrador).

## Configuracion
- Frontend: `config.properties` con host y puerto del backend.
- Backend: `config.properties` con el puerto y las credenciales de MySQL.
- Base de datos: `base_datos.sql` (se puede correr varias veces; incluye los datos de muestra).

## Documentacion
- [`docs/SPEC.md`](docs/SPEC.md): arquitectura, decisiones y supuestos.
- [`docs/DIVISION_TRABAJO.md`](docs/DIVISION_TRABAJO.md): reparto del trabajo, hitos y reglas del equipo.
- `docs/modelo-datos.md`: dependencias funcionales y pasos hasta 3FN.
- `docs/plan-pruebas.md`: lista de verificacion por funcionalidad.
