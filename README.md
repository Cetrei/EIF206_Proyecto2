# Proyecto #2 | Programacion III
## Integrantes
- Joanfer Hidalgo Chaves
- Justin Angulo Artavia
- Santiago Hernandez Chaves

## Setup
**Version JDK**: 21 o superior (`release 21` en todos los modulos). Desarrollado con ```26.0.2```

**Maven**: 3.9 o superior. **Base de datos**: MySQL 8 o MariaDB.

## Descripcion
Sistema de reservas de recursos con arquitectura distribuida. Dos procesos independientes que se comunican por sockets con hilos: el **Frontend** (Swing, MVC, sin ninguna referencia a la base de datos) y el **Backend** (servidor sin interfaz, unico que habla con MySQL). El Frontend usa el patron Proxy: cada service remoto tiene una clase proxy local que envia y recibe las peticiones por el socket. Cuando un usuario registra una reserva, el Backend notifica de forma asincrona a los demas Frontend con sesion, que la muestran en el area de Alertas (ver o descartar).

```
Frontend (Swing, MVC)  --sockets-->  Backend (servidor)  --JDBC-->  MySQL
```

Se pueden abrir varios Frontend contra un unico Backend, incluso en maquinas distintas.

## Modulos
| Modulo | Contenido |
|---|---|
| `common` | modelo, interfaces de service, protocolo, excepciones |
| `backend` | servidor, services, persistencia MySQL, extractor de IA |
| `frontend` | pantallas, controllers, proxies, reportes PDF, alertas |
| `integracion` | solo pruebas: sockets reales y controllers contra el backend |

## Base de datos
Ejecutar una vez el script (se puede repetir sin problema):
```
mysql -uroot -p < base_datos.sql
```
Crea las bases `reservas` (con datos de muestra) y `reservas_test` (solo tablas, la usan las pruebas de integracion de la base).

## Configuracion
Copiar cada `config.properties.example` a `config.properties` en su modulo y ajustarlo:
- `backend/config.properties`: `puerto` del servidor y credenciales de MySQL (`bd.host`, `bd.puerto`, `bd.base`, `bd.usuario`, `bd.clave`).
- `frontend/config.properties`: `host` (IP del Backend) y `puerto`. Sin archivo se usa `localhost:5050`.

El archivo se busca en la carpeta de trabajo y, si no esta, en la carpeta del modulo (`backend` o `frontend`).

## Ejecucion con Maven
Desde la raiz:
```
mvn -DskipTests package
```
Backend (persiste siempre en MySQL; `<mysql-connector-j.jar>` esta en `~/.m2/repository/com/mysql/mysql-connector-j/9.0.0/`):
```
cd backend
java -cp target/backend-1.0-SNAPSHOT.jar:../common/target/common-1.0-SNAPSHOT.jar:<mysql-connector-j.jar> cr.ac.una.reservas.backend.MainBackend
```
Frontend (repetir en otra terminal para abrir mas; `<forms_rt.jar>` esta en `~/.m2/repository/com/intellij/forms_rt/7.0.3/`):
```
cd frontend
java -cp target/frontend-1.0-SNAPSHOT.jar:../common/target/common-1.0-SNAPSHOT.jar:<forms_rt.jar> cr.ac.una.reservas.Main
```
En Windows el separador del classpath es `;` en lugar de `:`.

## Ejecucion con IntelliJ IDEA
1. `File > Open` y elegir el `pom.xml` de la raiz (Open as Project). Esperar la importacion de Maven.
2. `Settings > Editor > GUI Designer > Generate GUI into: Java source code`.
3. Ejecutar `cr.ac.una.reservas.backend.MainBackend` (modulo `backend`) y despues `cr.ac.una.reservas.Main` (modulo `frontend`). Para abrir varios Frontend, en la configuracion de ejecucion activar `Allow multiple instances`.

## Pruebas
```
mvn verify
```
Corre las pruebas de unidad (Surefire) y de integracion (Failsafe) con JUnit Jupiter, sin necesitar MySQL: usan DAO en memoria sobre un socket real. Algunas pruebas de interfaz abren ventanas Swing por unos segundos. `DaoMySqlIT` prueba la persistencia real contra `reservas_test` y se omite sola si no hay base; para ejecutarla:
```
mvn verify -Dbd.puerto=3306 -Dbd.usuario=root -Dbd.clave=<clave>
```

## Usuarios de muestra
| ID | Clave | Rol |
|---|---|---|
| `admin` | `123` | Administrador |
| `111`, `222`, `123` | igual al ID | Funcionario |

La clave de Gemini para el asistente de reservas se configura en la pestana Configuracion IA (la ve solo el administrador).

## Documentacion
- [`docs/SPEC.md`](docs/SPEC.md): arquitectura, decisiones y supuestos.
- [`docs/modelo-datos.md`](docs/modelo-datos.md): dependencias funcionales y pasos hasta 3FN.
- [`docs/plan-pruebas.md`](docs/plan-pruebas.md): lista de verificacion por funcionalidad.
