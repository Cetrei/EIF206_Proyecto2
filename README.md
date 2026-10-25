# Proyecto #2 | Programacion III
## Integrantes
- Joanfer Hidalgo Chaves
- Justin Angulo Artavia
- Santiago Hernandez Chaves

## Setup
**Version JDK**: ```26.0.2``` (minimo 21)

**Base de datos**: MySQL, crear con `base_datos.sql`. Las credenciales se configuran en `backend/config.properties` y la direccion del Backend en `frontend/config.properties`.

**Pruebas**: `mvn verify`

## IntelliJ IDEA
1. `File > Open` y elegir el `pom.xml` de la raiz.
2. `Settings > Editor > GUI Designer > Generate GUI into: Java source code`.
3. Arriba a la derecha elegir `MainBackend` y pulsar Run.
4. Luego elegir `Frontend` y pulsar Run. Se puede repetir para abrir mas de un Frontend.

## Usuarios
`admin` / `123` (administrador). 
`111`, `222` y `123` con clave igual a su ID (funcionarios).
