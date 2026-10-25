# Plan de Pruebas – Proyecto 2 EIF206

**Proyecto:** Sistema de Reserva de Recursos  
**Curso:** EIF206 – Programación 3  
**Universidad:** Universidad Nacional  
**Área de trabajo:** Pruebas y calidad  
**Herramientas:** IntelliJ IDEA, Java 26, Maven, JUnit Jupiter, Surefire y Failsafe

## 1. Descripción del plan

Este documento presenta el plan de pruebas del Sistema de Reserva de Recursos, con el propósito de verificar el funcionamiento de las principales características de la aplicación y comprobar el cumplimiento de los requisitos establecidos para el Proyecto 2.

Se revisaron las pruebas automáticas existentes en el repositorio y se compararon con las nueve funcionalidades de la rúbrica. También se identificaron casos adicionales que pueden verificarse mediante pruebas unitarias y otros que necesitan comprobación manual desde la interfaz gráfica.

Las pruebas de los servicios utilizan DAO en memoria para comprobar la lógica del sistema sin depender de una conexión activa con MySQL.

Durante el proceso se desarrollaron pruebas adicionales, se identificaron errores de funcionamiento y se realizaron correcciones puntuales que posteriormente se verificaron mediante Maven.

## 2. Clasificación de las pruebas

Para organizar los casos de prueba se utilizan las siguientes clasificaciones:

- **Automática – Aprobada:** existe una prueba automatizada que se ejecutó correctamente.
- **Automática – Deshabilitada:** existe una prueba preparada que permanece deshabilitada temporalmente.
- **Automática – Pendiente:** se identificó un caso que todavía necesita una prueba adicional.
- **Manual – Pendiente:** el caso debe comprobarse directamente utilizando la aplicación.

## 3. Casos de prueba por funcionalidad

### 3.1. Ingreso y cambio de clave (5%)

- **CP-01 | Automática – Aprobada:** Iniciar sesión utilizando credenciales válidas de un funcionario. `AutenticacionServiceTest`.
- **CP-02 | Automática – Aprobada:** Iniciar sesión utilizando credenciales válidas de un administrador. `AutenticacionServiceTest`.
- **CP-03 | Automática – Aprobada:** Rechazar el ingreso cuando la contraseña es incorrecta. `AutenticacionServiceTest`.
- **CP-04 | Automática – Aprobada:** Rechazar el ingreso cuando el usuario no existe. `AutenticacionServiceTest`.
- **CP-05 | Automática – Aprobada:** Cambiar correctamente la contraseña de un usuario. `AutenticacionServiceTest`.
- **CP-06 | Automática – Aprobada:** Rechazar el cambio de contraseña si la contraseña actual es incorrecta. `AutenticacionServiceTest`.
- **CP-07 | Automática – Aprobada:** Rechazar contraseñas nuevas vacías o nulas. `AutenticacionServiceTest`.
- **CP-08 | Automática – Aprobada:** Comprobar que la contraseña anterior deja de funcionar después del cambio. `AutenticacionServiceTest`.
- **CP-09 | Automática – Aprobada:** Verificar que se eliminan los espacios adicionales del ID al iniciar sesión y cambiar la contraseña. `LoginControlTest`.
- **CP-10 | Manual – Pendiente:** Comprobar los mensajes de error y la presentación de los formularios de ingreso y cambio de contraseña.

### 3.2. Reservas (20%)

- **CP-11 | Automática – Aprobada:** Crear una reserva cuando existe un recurso disponible. `ReservaServiceTest`.
- **CP-12 | Automática – Aprobada:** Impedir reservas del mismo recurso con horarios superpuestos. `ReservaServiceTest`.
- **CP-13 | Automática – Aprobada:** Permitir reservas consecutivas cuando sus horarios no se superponen. `ReservaServiceTest`.
- **CP-14 | Automática – Aprobada:** Rechazar reservas con fechas anteriores a la actual. `ReservaServiceTest`.
- **CP-15 | Automática – Aprobada:** Rechazar reservas donde la hora de finalización sea anterior a la de inicio. `ReservaServiceTest`.
- **CP-16 | Automática – Aprobada:** Rechazar reservas con la misma hora de inicio y finalización. `ReservaCasosAdicionalesTest`.
- **CP-17 | Automática – Aprobada:** Modificar una reserva conservando su identificador. `ReservaServiceTest`.
- **CP-18 | Automática – Aprobada:** Impedir que un funcionario modifique la reserva de otro funcionario. `ReservaServiceTest`.
- **CP-19 | Automática – Aprobada:** Cancelar correctamente una reserva activa. `ReservaServiceTest`.
- **CP-20 | Automática – Aprobada:** Comprobar que cancelar una reserva libera el recurso para otro usuario. `ReservaCasosAdicionalesTest`.
- **CP-21 | Automática – Aprobada:** Permitir dos reservas simultáneas cuando existen dos recursos de la misma categoría. `ReservaCasosAdicionalesTest`.
- **CP-22 | Automática – Aprobada:** Comprobar que cada funcionario obtiene sus propias reservas. `ReservaCasosAdicionalesTest`.
- **CP-23 | Automática – Deshabilitada:** Comprobar que solamente una de 20 solicitudes simultáneas obtiene el mismo recurso y horario. `ReservaConcurrenciaTest`. Preparada con DAO en memoria y `@Disabled` hasta recibir autorización del núcleo.
- **CP-24 | Automática – Aprobada:** Verificar que el sistema rechace reservas con categorías inexistentes y no almacene información inválida. `ReservaValidacionCategoriaTest`.
- **CP-25 | Manual – Pendiente:** Comprobar la creación, modificación y cancelación de reservas desde la interfaz gráfica.

### 3.3. Funcionarios (8%)

- **CP-26 | Automática – Aprobada:** Registrar funcionarios con su contraseña inicial. `FuncionarioServiceTest`.
- **CP-27 | Automática – Aprobada:** Rechazar funcionarios con identificaciones duplicadas. `FuncionarioServiceTest`.
- **CP-28 | Automática – Aprobada:** Rechazar funcionarios sin identificación o nombre válido. `FuncionarioServiceTest`.
- **CP-29 | Automática – Aprobada:** Modificar la información de un funcionario existente. `FuncionarioServiceTest`.
- **CP-30 | Automática – Aprobada:** Comprobar que la modificación de datos personales no sobrescriba una contraseña actualizada. `FuncionarioServiceTest`.
- **CP-31 | Automática – Aprobada:** Impedir eliminar funcionarios que tienen reservas activas. `FuncionarioServiceTest`.
- **CP-32 | Automática – Aprobada:** Permitir eliminar funcionarios cuyas reservas están canceladas. `FuncionarioServiceTest`.
- **CP-33 | Automática – Aprobada:** Buscar funcionarios mediante su nombre. `FuncionarioServiceTest`.
- **CP-34 | Manual – Pendiente:** Verificar los formularios de registro, modificación, búsqueda y eliminación de funcionarios.

### 3.4. Categorías de recursos (8%)

- **CP-35 | Automática – Aprobada:** Crear categorías con identificadores generados automáticamente. `CategoriaServiceTest`.
- **CP-36 | Automática – Aprobada:** Rechazar categorías con descripciones vacías o nulas. `CategoriaServiceTest`.
- **CP-37 | Automática – Aprobada:** Modificar una categoría existente. `CategoriaServiceTest`.
- **CP-38 | Automática – Aprobada:** Eliminar una categoría sin recursos asociados. `CategoriaServiceTest`.
- **CP-39 | Automática – Aprobada:** Impedir eliminar categorías que tienen recursos asociados. `CategoriaServiceTest`.
- **CP-40 | Automática – Aprobada:** Buscar categorías por descripción. `CategoriaServiceTest`.
- **CP-41 | Automática – Aprobada:** Comprobar las notificaciones a observadores cuando se crean, modifican o eliminan categorías. `CategoriaObserverTest`.
- **CP-42 | Manual – Pendiente:** Verificar la creación, modificación y eliminación de categorías desde la interfaz gráfica.

### 3.5. Recursos (8%)

- **CP-43 | Automática – Aprobada:** Registrar recursos asociados a categorías válidas. `RecursoServiceTest`.
- **CP-44 | Automática – Aprobada:** Rechazar recursos con identificaciones duplicadas. `RecursoServiceTest`.
- **CP-45 | Automática – Aprobada:** Rechazar recursos asociados a categorías inexistentes. `RecursoServiceTest`.
- **CP-46 | Automática – Aprobada:** Modificar los datos de un recurso existente. `RecursoServiceTest`.
- **CP-47 | Automática – Aprobada:** Eliminar recursos que no tienen restricciones. `RecursoServiceTest`.
- **CP-48 | Automática – Aprobada:** Impedir eliminar recursos asociados a reservas activas. `RecursoServiceTest`.
- **CP-49 | Automática – Aprobada:** Permitir eliminar recursos asociados únicamente a reservas canceladas. `RecursoServiceTest`.
- **CP-50 | Automática – Aprobada:** Filtrar recursos por categoría, descripción o identificación. `RecursoServiceTest`.
- **CP-51 | Manual – Pendiente:** Comprobar los formularios, filtros y mensajes de validación de recursos.

### 3.6. Calendarización (8%)

- **CP-52 | Automática – Aprobada:** Consultar reservas activas correspondientes a una fecha determinada. `ReservaServiceTest`.
- **CP-53 | Automática – Aprobada:** Excluir reservas canceladas al consultar las reservas activas de una fecha. `ReservaCasosAdicionalesTest`.
- **CP-54 | Manual – Pendiente:** Comprobar que las reservas se muestren en las fechas correctas del calendario.
- **CP-55 | Manual – Pendiente:** Verificar que la calendarización refleje los horarios y recursos reservados.
- **CP-56 | Manual – Pendiente:** Comprobar que el calendario se actualice después de crear, modificar o cancelar una reserva.
- **CP-57 | Manual – Pendiente:** Verificar la navegación entre fechas y la visualización del calendario.

### 3.7. Actividades (8%)

- **CP-58 | Automática – Aprobada:** Rechazar reservas que no tienen una actividad definida. `ReservaServiceTest`.
- **CP-59 | Automática – Aprobada:** Comprobar que una actividad puede modificarse dentro de una reserva existente. `ReservaServiceTest`.
- **CP-60 | Automática – Aprobada:** Contabilizar actividades agrupadas por semana. `EstadisticaServiceTest`.
- **CP-61 | Manual – Pendiente:** Comprobar que las actividades se muestran correctamente en su pestaña.
- **CP-62 | Manual – Pendiente:** Verificar la consulta y presentación de actividades por fechas.
- **CP-63 | Manual – Pendiente:** Comprobar que los reportes de actividades muestran los datos correspondientes.

### 3.8. Estadísticas (15%)

- **CP-64 | Automática – Aprobada:** Contabilizar los recursos reservados dentro de un período. `EstadisticaServiceTest`.
- **CP-65 | Automática – Aprobada:** Excluir reservas que se encuentran fuera del período seleccionado. `EstadisticaServiceTest`.
- **CP-66 | Automática – Aprobada:** Rechazar rangos de fechas inválidos o incompletos. `EstadisticaServiceTest`.
- **CP-67 | Automática – Aprobada:** Agrupar y ordenar las actividades por semana. `EstadisticaServiceTest`.
- **CP-68 | Automática – Aprobada:** Excluir reservas canceladas de las estadísticas. `EstadisticaServiceTest`.
- **CP-69 | Automática – Aprobada:** Incluir reservas que se encuentran exactamente en los límites del período consultado. `EstadisticaServiceTest`.
- **CP-70 | Manual – Pendiente:** Comprobar que los gráficos representan correctamente los resultados calculados.
- **CP-71 | Manual – Pendiente:** Verificar que los filtros de fechas actualizan las estadísticas.
- **CP-72 | Manual – Pendiente:** Comprobar la presentación y generación de los reportes estadísticos.

### 3.9. Alertas (20%)

- **CP-73 | Automática – Aprobada:** Mostrar las alertas más recientes al principio de la lista. `AlertasControlTest`.
- **CP-74 | Automática – Aprobada:** Descartar una alerta sin eliminar las demás. `AlertasControlTest`.
- **CP-75 | Automática – Aprobada:** Limpiar las alertas al cerrar sesión. `AlertasControlTest`.
- **CP-76 | Automática – Aprobada:** Ignorar notificaciones que no corresponden a alertas de reserva. `AlertasControlTest`.
- **CP-77 | Automática – Aprobada:** Enviar una alerta a los clientes conectados, excepto al autor de la reserva. `ServidorIT`.
- **CP-78 | Automática – Aprobada:** Impedir que las conexiones sin sesión activa reciban las notificaciones. `ServidorIT`.
- **CP-79 | Manual – Pendiente:** Abrir una alerta y comprobar que se muestran sus detalles.
- **CP-80 | Manual – Pendiente:** Verificar que el sistema solicita confirmación antes de descartar una alerta.
- **CP-81 | Manual – Pendiente:** Comprobar que las alertas permanezcan visibles al cambiar entre pestañas.
- **CP-82 | Manual – Pendiente:** Comprobar visualmente la recepción de alertas utilizando dos instancias independientes del Frontend.

## 4. Revisión de las pruebas existentes

Se revisaron los seis archivos principales de pruebas de servicios, obteniendo la siguiente distribución:

| Archivo | Métodos de prueba |
|---|---:|
| AutenticacionServiceTest | 11 |
| ReservaServiceTest | 21 |
| FuncionarioServiceTest | 12 |
| CategoriaServiceTest | 13 |
| RecursoServiceTest | 13 |
| EstadisticaServiceTest | 10 |
| **Total** | **80** |

Aunque la asignación inicial indicaba 79 pruebas, en la versión revisada del repositorio se identificaron 80 métodos `@Test` entre los seis archivos.

El repositorio también contiene otras pruebas de backend, frontend e integración que complementan la cobertura de las funcionalidades principales.

## 5. Pruebas adicionales implementadas

Durante el trabajo de pruebas y calidad se crearon dos nuevas clases dentro de `backend/src/test/java/cr/ac/una/reservas/reserva`.

### 5.1. ReservaCasosAdicionalesTest.java

Se desarrollaron cinco pruebas adicionales para verificar situaciones importantes relacionadas con reservas:

1. `cancelarReservaLiberaRecurso`: verifica que, cuando una reserva es cancelada, el recurso correspondiente vuelva a estar disponible para otra reserva.

2. `dosRecursosPermitenDosReservasSimultaneas`: comprueba que dos usuarios puedan reservar recursos diferentes de la misma categoría durante un horario coincidente.

3. `rechazaReservaConHorasIguales`: verifica que el sistema no permita registrar reservas donde la hora de inicio y la hora de finalización sean iguales.

4. `calendarizacionNoIncluyeReservasCanceladas`: comprueba que las reservas canceladas no aparezcan en la consulta de reservas activas de una fecha específica.

5. `listaSoloReservasDelFuncionarioSolicitado`: verifica que la consulta de reservas por funcionario devuelva únicamente aquellas que corresponden al identificador solicitado.

**Resultado:** 5 pruebas ejecutadas, 5 exitosas, 0 fallos.

### 5.2. ReservaValidacionCategoriaTest.java

Se desarrolló una prueba adicional para comprobar que el sistema no acepte reservas con categorías de recursos inexistentes.

La prueba `noPermiteReservarCategoriaInexistente` utiliza un identificador de categoría que no se encuentra registrado en el DAO.

En la primera ejecución, la prueba falló porque el servicio aceptaba la reserva, aunque la categoría solicitada no existiera.

Después de corregir la validación en `ReservaServiceImpl`, se adaptó la prueba para comprobar que el servicio genere una `ReglaDeNegocioException` y que no se almacene ninguna reserva inválida.

En la última ejecución de Maven, esta prueba terminó correctamente.

### 5.3. Resumen de las pruebas adicionales

| Clase | Pruebas agregadas | Estado |
|---|---:|---|
| ReservaCasosAdicionalesTest | 5 | Aprobadas |
| ReservaValidacionCategoriaTest | 1 | Aprobada |
| **Total** | **6** | **Aprobadas** |

Las pruebas se realizaron utilizando DAO falsos en memoria y no requieren una conexión con MySQL.

## 6. Prueba de concurrencia

Se verificó que `ReservaConcurrenciaTest` utiliza 20 hilos para intentar reservar el mismo recurso durante el mismo horario.

La prueba utiliza DAO en memoria y construye `ReservaServiceImpl` mediante la inyección de dependencias por constructor.

Su objetivo es comprobar que solamente una solicitud consiga realizar la reserva exitosamente y que no se generen reservas duplicadas.

En ejecuciones anteriores, las cinco repeticiones de la prueba terminaron correctamente.

Posteriormente, se agregó `@Disabled` a la clase para cumplir con la indicación de mantenerla deshabilitada hasta recibir autorización del núcleo.

Se ejecutó nuevamente Maven y se obtuvo `BUILD SUCCESS`. La prueba apareció como omitida, confirmando que la anotación fue aplicada correctamente.

Cuando el núcleo autorice su ejecución, se podrá retirar `@Disabled` y volver a ejecutar la prueba para comprobar la concurrencia.

**Estado: IMPLEMENTADA, VERIFICADA PREVIAMENTE Y DESHABILITADA.**

## 7. Resultados de la ejecución

Se utilizó IntelliJ IDEA 2026.2 con Java 26.0.2 y Maven para realizar las pruebas del proyecto. La configuración de compilación utiliza Java 21.

Se ejecutó el ciclo `verify` de Maven desde la raíz del repositorio.

### 7.1. Resultados de la última ejecución

| Módulo | Pruebas aprobadas | Omitidas | Fallos |
|---|---:|---:|---:|
| common | 59 | 0 | 0 |
| backend | 149 | 7 | 0 |
| frontend | 32 | 0 | 0 |
| integracion | 22 | 0 | 0 |
| **Total** | **262** | **7** | **0** |

**Resultado general: BUILD SUCCESS.**

Se ejecutaron 262 pruebas satisfactoriamente. No se registraron fallos ni errores.

Las siete pruebas omitidas corresponden a seis pruebas de integración con MySQL y una prueba de concurrencia deshabilitada intencionalmente mediante `@Disabled`.

La prueba `ReservaValidacionCategoriaTest` se ejecutó correctamente después de corregir la validación de categorías inexistentes en `ReservaServiceImpl`.

El resultado confirma que las pruebas habilitadas pueden ejecutarse sin necesitar una base de datos MySQL activa.

### 7.2. Pruebas de MySQL

Las seis pruebas omitidas de MySQL corresponden a la clase `DaoMySqlIT`.

Estas pruebas requieren una base de datos de pruebas con el esquema correspondiente y se omiten cuando el entorno necesario no está disponible.

Por esta razón, el proyecto logró completar `mvn verify` sin una conexión activa con MySQL.

Sin embargo, estas seis pruebas específicas de persistencia no se consideran aprobadas, ya que no fueron ejecutadas.

### 7.3. Herramientas utilizadas

Se utilizaron las siguientes herramientas:

- **JUnit Jupiter:** para escribir y ejecutar las pruebas automatizadas.
- **Maven:** para compilar y verificar los módulos del proyecto.
- **Surefire Plugin:** para ejecutar las pruebas unitarias.
- **Failsafe Plugin:** para ejecutar las pruebas de integración.
- **IntelliJ IDEA:** para desarrollar, ejecutar y revisar los resultados de las pruebas.

No fue necesario agregar nuevas dependencias a los archivos `pom.xml`.

## 8. Incidencias encontradas y correcciones realizadas

### 8.1. Corrección de la prueba de cambio de contraseña

**Archivo:** `integracion/src/test/java/cr/ac/una/reservas/autenticacion/LoginControlTest.java`

**Prueba afectada:** `recortaEspaciosDelIdAlCambiarClave`

Durante la primera ejecución de Maven se identificó un error de tipo `NoSuchMethodException`.

La prueba intentaba ejecutar el método `confirmarCambioClave` utilizando una firma de cuatro parámetros:

`confirmarCambioClave(JDialog, CampoTexto, CampoTexto, CampoTexto)`

Sin embargo, la implementación actual de `LoginControl` utiliza un único parámetro de tipo `DialogoCambioClave`.

Esta diferencia provocaba que la prueba de integración fallara y que Maven terminara con `BUILD FAILURE`.

**Corrección realizada:**

Se actualizó `LoginControlTest.java` para utilizar la clase `DialogoCambioClave` y acceder al método con su firma actual.

También se adaptó el manejo del aviso de confirmación para permitir que la prueba finalizara automáticamente.

No fue necesario modificar el funcionamiento principal de la aplicación.

**Comprobación:**

Después de actualizar la prueba, se ejecutó nuevamente Maven. La prueba terminó correctamente y el proyecto obtuvo `BUILD SUCCESS`.

**Estado: CORREGIDO Y VERIFICADO.**

### 8.2. Corrección de validación de categorías inexistentes

**Archivo corregido:** `backend/src/main/java/cr/ac/una/reservas/reserva/ReservaServiceImpl.java`

**Prueba creada:** `ReservaValidacionCategoriaTest.java`

Durante la revisión del servicio de reservas se identificó un posible problema al intentar reservar una categoría de recursos inexistente.

Para comprobarlo, se creó una prueba que intentaba registrar una reserva utilizando el identificador de categoría `999999`, sin haber creado dicha categoría.

**Resultado inicial:**

La prueba falló porque el servicio devolvía un resultado exitoso, a pesar de que la categoría solicitada no existía.

Esto permitía aceptar una reserva sin un recurso válido asignado.

**Corrección realizada:**

Se agregó una validación al método `validarDatosBasicos` de `ReservaServiceImpl`.

La validación recorre los identificadores de categorías solicitadas y comprueba que cada uno exista en el DAO correspondiente.

Cuando una categoría no existe, se genera una `ReglaDeNegocioException`, impidiendo continuar con el registro de la reserva.

También se adaptó la prueba para comprobar que se genera la excepción esperada y que el DAO no almacena una reserva inválida.

**Comprobación:**

Se ejecutaron nuevamente las pruebas mediante Maven y el proyecto terminó con `BUILD SUCCESS`.

**Estado: CORREGIDO Y VERIFICADO.**

### 8.3. Estado de la prueba de concurrencia

Se identificó que `ReservaConcurrenciaTest.java` estaba habilitada, aunque en la distribución del trabajo se indicaba utilizar `@Disabled` hasta recibir autorización del núcleo.

Inicialmente, la prueba se ejecutó cinco veces mediante `@RepeatedTest(5)` y todas las repeticiones finalizaron correctamente.

Posteriormente, se agregó la anotación `@Disabled("Pendiente de autorizacion del nucleo")` para cumplir con el requisito establecido.

Después se ejecutó nuevamente `mvn verify`. La prueba apareció como omitida y el proyecto terminó con `BUILD SUCCESS`.

La lógica de la prueba permanece preparada para su ejecución cuando sea autorizada.

**Estado: IMPLEMENTADA Y DESHABILITADA. PENDIENTE DE AUTORIZACIÓN DEL NÚCLEO.**

## 9. Pruebas manuales pendientes

Aunque los resultados automáticos fueron satisfactorios, todavía es necesario comprobar el funcionamiento de varios componentes desde la interfaz gráfica.

Las comprobaciones manuales pendientes incluyen:

- **Ingreso y cambio de clave:** verificar visualmente los formularios y los mensajes de error.
- **Reservas:** comprobar la creación, modificación y cancelación desde las pantallas.
- **Funcionarios:** verificar los formularios de registro, modificación y eliminación.
- **Categorías:** comprobar la gestión de categorías desde su pestaña.
- **Recursos:** verificar la búsqueda, filtrado y modificación de recursos.
- **Calendarización:** comprobar que las reservas aparezcan en las fechas y horarios correctos.
- **Actividades:** revisar la visualización y presentación de las actividades registradas.
- **Estadísticas:** comprobar que los gráficos correspondan a la información de las reservas.
- **Alertas:** verificar la recepción, visualización y descarte de notificaciones.

Para las alertas también se requiere una prueba con dos instancias independientes del Frontend conectadas al mismo Backend.

En esta comprobación, un usuario deberá crear una reserva desde una instancia y el otro deberá recibir la notificación correspondiente.

Además, será necesario verificar que el usuario pueda consultar los detalles de la alerta, descartarla con confirmación y seguir visualizando las demás alertas al cambiar de pestaña.

Estas verificaciones permanecen pendientes y no se consideran aprobadas hasta que sean ejecutadas.

## 10. Pendientes para la entrega

- Comunicar al núcleo la corrección realizada en `ReservaServiceImpl`.
- Comunicar al equipo la actualización de `LoginControlTest`.
- Confirmar con el núcleo cuándo debe habilitarse `ReservaConcurrenciaTest`.
- Completar las comprobaciones manuales de la interfaz gráfica.
- Ejecutar las pruebas específicas de MySQL cuando exista un entorno de pruebas disponible.
- Ejecutar nuevamente `mvn verify` antes de la entrega definitiva si se realizan nuevos cambios.
- Mantener los cambios actualizados en GitHub.
- Preparar el Pull Request final para incorporar las modificaciones al repositorio principal.

## 11. Estado general del trabajo

Durante el desarrollo de la Parte B se revisaron las pruebas existentes, se documentó su cobertura y se agregaron seis pruebas nuevas relacionadas con reservas.

También se identificaron y corrigieron dos problemas: una prueba de integración desactualizada en el cambio de contraseña y una validación incompleta de categorías inexistentes al registrar reservas.

Las pruebas adicionales permitieron comprobar situaciones relacionadas con disponibilidad de recursos, cancelaciones, horarios, consultas por funcionario y validación de categorías.

La prueba de concurrencia se encontraba implementada y había superado sus cinco ejecuciones anteriores. Posteriormente, se deshabilitó temporalmente mediante `@Disabled`, tal como establecía la distribución del trabajo.

La última ejecución de Maven registró **262 pruebas exitosas, 7 omitidas y ningún fallo ni error**, obteniendo `BUILD SUCCESS`.

Las seis pruebas de integración con MySQL permanecen pendientes de ejecución en un entorno con la base de datos correspondiente.

Asimismo, todavía deben completarse las verificaciones manuales de las diferentes pantallas, principalmente las relacionadas con las alertas asincrónicas y la interacción entre múltiples instancias del Frontend.

Por lo tanto, el trabajo presenta resultados satisfactorios en las pruebas automáticas habilitadas, mientras que la validación manual y las comprobaciones específicas de base de datos continúan pendientes.

Las modificaciones realizadas fuera del área originalmente asignada a la Parte B deberán comunicarse al equipo para que sean revisadas antes de incorporarlas al proyecto principal.