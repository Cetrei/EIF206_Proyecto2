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

## Configuracion
- Frontend: `config.properties` con host y puerto del backend.
- Backend: `config.properties` con el puerto y las credenciales de MySQL.
- Base de datos: `base_datos.sql` (se puede correr varias veces; incluye los datos de muestra).

## Documentacion
- [`docs/SPEC.md`](docs/SPEC.md): arquitectura, decisiones y supuestos.
- [`docs/DIVISION_TRABAJO.md`](docs/DIVISION_TRABAJO.md): reparto del trabajo, hitos y reglas del equipo.
- `docs/modelo-datos.md` (por crear): dependencias funcionales y pasos hasta 3FN.
- `docs/plan-pruebas.md` (por crear): lista de verificacion por funcionalidad.
