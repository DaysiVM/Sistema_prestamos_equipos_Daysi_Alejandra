# TecnoUAM · Sistema de préstamo de equipos tecnológicos

Prototipo cliente-servidor para el **Centro de Capacitación TecnoUAM**, que presta computadoras portátiles,
proyectores y tabletas a sus instructores.

- **Backend:** API REST con Spring Boot 4, Spring Data JPA y PostgreSQL, protegida con Spring Security y JWT (roles USER y ADMIN).
- **Frontend:** React 18 con Axios, `useState` y `useEffect`. Consulta los datos reales de la API usando un token válido.

> Universidad Americana (UAM) · Ingeniería de Sistemas · Servicios Web
> Examen práctico integrador, segundo corte.

---

## Tabla de contenido

1. [Tecnologías](#1-tecnologías)
2. [Estructura del repositorio](#2-estructura-del-repositorio)
3. [Requisitos previos](#3-requisitos-previos)
4. [Puesta en marcha](#4-puesta-en-marcha)
5. [Variables de entorno](#5-variables-de-entorno)
6. [Cuentas de prueba](#6-cuentas-de-prueba)
7. [Endpoints](#7-endpoints)
8. [Reglas de negocio](#8-reglas-de-negocio)
9. [Seguridad: flujo JWT, 401 y 403](#9-seguridad-flujo-jwt-401-y-403)
10. [Pruebas con Postman](#10-pruebas-con-postman)
11. [Pruebas automáticas](#11-pruebas-automáticas)
12. [Equipo de trabajo](#12-equipo-de-trabajo)

---

## 1. Tecnologías

| Capa | Tecnología |
|---|---|
| Lenguaje backend | Java 21 |
| Framework | Spring Boot 4.1 (Spring Web MVC, Spring Data JPA, Bean Validation) |
| Seguridad | Spring Security 7 + JWT firmado con HS256 (librería JJWT 0.12) + BCrypt |
| Base de datos | PostgreSQL 17 (pruebas automáticas: H2 en memoria) |
| Frontend | React 18, Vite 6, Axios |
| Pruebas | JUnit 5 + MockMvc, Postman |

## 2. Estructura del repositorio

```
├── src/main/java/ni/uam/sistema_prestamos_equipos_daysi_alejandra/
│   ├── entity/        Equipo, Prestamo, Usuario y los enums (TipoEquipo, EstadoEquipo, EstadoPrestamo, Rol)
│   ├── repository/    Repositorios Spring Data JPA
│   ├── dto/           Objetos de entrada/salida de la API (records) con sus validaciones
│   ├── service/       Lógica y reglas de negocio (EquipoService, PrestamoService)
│   ├── controller/    Endpoints REST (Auth, Equipo, Prestamo)
│   ├── security/      SecurityConfig, JwtService, filtro JWT, manejadores de 401 y 403
│   ├── exception/     Excepciones de negocio y manejador global de errores
│   └── config/        DataInitializer: cuentas y equipos de demostración
├── src/test/          Pruebas de integración (H2 + MockMvc)
├── database/          Scripts SQL: 01_schema.sql (estructura) y 02_datos.sql (datos mínimos)
├── postman/           Colección Postman exportada
├── frontend/          Aplicación React (Vite)
├── .env.example       Plantilla de variables de entorno (sin secretos)
└── pom.xml
```

## 3. Requisitos previos

- **JDK 21 o superior.** No hace falta instalar Maven: el proyecto incluye `mvnw`.
- **PostgreSQL 14 o superior**, en ejecución en `localhost:5432`.
- **Node.js 18 o superior** (con npm).
- **Postman**, para ejecutar la colección de pruebas.

## 4. Puesta en marcha

### 4.1 Base de datos

Creen la base de datos, una sola vez, desde pgAdmin o con `psql`:

```sql
CREATE DATABASE tecno_uam_prestamos;
```

El backend crea las tablas automáticamente (`ddl-auto=update`) y, al iniciar, inserta las cuentas de prueba y 5 equipos de ejemplo.

Si prefieren crear todo manualmente con los scripts SQL:

```bash
psql -U postgres -d tecno_uam_prestamos -f database/01_schema.sql
```

```bash
psql -U postgres -d tecno_uam_prestamos -f database/02_datos.sql
```

### 4.2 Backend (Spring Boot, puerto 8081)

1. Copien `.env.example` como `.env` en la raíz del proyecto y completen `DB_PASSWORD` y `JWT_SECRET`.
   El archivo `.env` está en `.gitignore`, así que **nunca se sube** al repositorio.
2. Ejecuten la aplicación de una de estas dos formas:
   - **IntelliJ IDEA:** abran `SistemaPrestamosEquiposDaysiAlejandraApplication` y presionen *Run*.
     El directorio de trabajo debe ser la raíz del proyecto, que es el valor por defecto.
   - **Terminal:**

```bash
./mvnw spring-boot:run
```

Cuando el arranque termina, la consola muestra `Tomcat started on port 8081`.

### 4.3 Frontend (React, puerto 5173)

```bash
cd frontend
```

```bash
npm install
```

```bash
npm run dev
```

Abran `http://localhost:5173` e inicien sesión con una de las [cuentas de prueba](#6-cuentas-de-prueba).
Si el backend corre en otra URL, creen `frontend/.env` a partir de `frontend/.env.example` y ajusten `VITE_API_URL`.

## 5. Variables de entorno

| Variable | Obligatoria | Descripción | Valor por defecto |
|---|---|---|---|
| `DB_PASSWORD` | **Sí** | Contraseña del usuario de PostgreSQL | — |
| `JWT_SECRET` | **Sí** | Secreto para firmar los JWT (mínimo 32 caracteres) | — |
| `DB_URL` | No | URL JDBC de la base de datos | `jdbc:postgresql://localhost:5432/tecno_uam_prestamos` |
| `DB_USERNAME` | No | Usuario de PostgreSQL | `postgres` |
| `SERVER_PORT` | No | Puerto del backend | `8081` |
| `JWT_EXPIRATION_MS` | No | Vigencia del token en milisegundos | `3600000` (1 hora) |
| `CORS_ALLOWED_ORIGINS` | No | Origen permitido del frontend | `http://localhost:5173` |
| `APP_ADMIN_PASSWORD` / `APP_USER_PASSWORD` | No | Contraseñas de las cuentas de prueba | ver sección 6 |
| `VITE_API_URL` (frontend) | No | URL del backend | `http://localhost:8081` |

Para generar un `JWT_SECRET` aleatorio en PowerShell:

```powershell
[Convert]::ToBase64String((1..48 | ForEach-Object { Get-Random -Maximum 256 }))
```

## 6. Cuentas de prueba

| Usuario | Contraseña | Rol | Permisos |
|---|---|---|---|
| `admin` | `Admin123*` | ADMIN | Consultar, crear, actualizar y eliminar equipos; registrar préstamos y devoluciones |
| `usuario` | `Usuario123*` | USER | Solo consultar equipos y préstamos |

Las contraseñas se guardan en la base de datos únicamente como hash **BCrypt**.

## 7. Endpoints

URL base: `http://localhost:8081`. Todas las rutas, salvo el login, requieren la cabecera `Authorization: Bearer <token>`.

| Método | Ruta | Rol | Descripción | Respuestas |
|---|---|---|---|---|
| POST | `/api/auth/login` | Público | Autentica y devuelve el JWT | 200, 400, 401 |
| GET | `/api/equipos` | USER, ADMIN | Lista todos los equipos | 200, 401 |
| GET | `/api/equipos/{id}` | USER, ADMIN | Consulta un equipo | 200, 401, 404 |
| POST | `/api/equipos` | ADMIN | Registra un equipo; inicia DISPONIBLE | **201**, 400, 401, 403, 409 |
| PUT | `/api/equipos/{id}` | ADMIN | Modifica código, nombre y tipo | 200, 400, 401, 403, 404, 409 |
| DELETE | `/api/equipos/{id}` | ADMIN | Elimina el equipo solo si no tiene préstamos activos | **204**, 401, 403, 404, 409 |
| GET | `/api/prestamos` | USER, ADMIN | Lista préstamos (filtro opcional `?estado=ACTIVO\|DEVUELTO`) | 200, 401 |
| GET | `/api/prestamos/{id}` | USER, ADMIN | Consulta un préstamo | 200, 401, 404 |
| POST | `/api/prestamos` | ADMIN | Registra el préstamo y cambia el equipo a PRESTADO | **201**, 400, 401, 403, 404, 409 |
| PUT | `/api/prestamos/{id}/devolver` | ADMIN | Registra la devolución y libera el equipo (DISPONIBLE) | 200, 401, 403, 404, 409 |

### Ejemplos de cuerpo JSON

```json
// POST /api/auth/login
{ "username": "admin", "password": "Admin123*" }

// POST o PUT /api/equipos   (tipo: PORTATIL | PROYECTOR | TABLETA)
{ "codigoPatrimonial": "UAM-LAP-010", "nombre": "Laptop Lenovo ThinkPad E14", "tipo": "PORTATIL" }

// POST /api/prestamos   (fechas en formato yyyy-MM-dd)
{ "equipoId": 1, "nombreSolicitante": "Ing. María López",
  "fechaPrestamo": "2026-10-28", "fechaPrevistaDevolucion": "2026-11-04" }
```

### Formato de error

Todos los errores responden con la misma estructura:

```json
{
  "timestamp": "2026-10-28T20:15:03.512",
  "status": 409,
  "error": "Conflict",
  "mensaje": "El equipo UAM-LAP-001 ya está PRESTADO y no puede prestarse nuevamente",
  "ruta": "/api/prestamos"
}
```

En los errores 400 de validación se agrega `"detalles": { "campo": "mensaje" }`.

### Códigos HTTP utilizados

| Código | Cuándo |
|---|---|
| 200 OK | Consultas, actualizaciones, devoluciones y login correcto |
| 201 Created | Alta de equipo o de préstamo; incluye la cabecera `Location` |
| 204 No Content | Eliminación exitosa |
| 400 Bad Request | Campos obligatorios vacíos, tipo inválido, fecha con formato incorrecto o fecha prevista anterior a la del préstamo |
| 401 Unauthorized | Sin token, token inválido o expirado, o credenciales incorrectas en el login |
| 403 Forbidden | Token válido, pero el rol no tiene permiso (por ejemplo, USER intenta un POST) |
| 404 Not Found | El equipo o el préstamo no existe |
| 409 Conflict | Código patrimonial duplicado, equipo ya PRESTADO, eliminación de un equipo con préstamo activo o devolución repetida |

## 8. Reglas de negocio

| Regla | Dónde se implementa |
|---|---|
| Código patrimonial único | `EquipoService.crear/actualizar` (409) + restricción `UNIQUE` en la base de datos |
| Estados del equipo: DISPONIBLE o PRESTADO | `enum EstadoEquipo` + `CHECK` en la base de datos |
| Estados del préstamo: ACTIVO o DEVUELTO | `enum EstadoPrestamo` + `CHECK` en la base de datos |
| No se presta un equipo que ya está PRESTADO | `PrestamoService.registrar` (409), con bloqueo pesimista de la fila del equipo para evitar dos préstamos simultáneos |
| Al prestar, el equipo pasa a PRESTADO; al devolver, vuelve a DISPONIBLE | `PrestamoService.registrar` / `devolver`, dentro de una misma transacción |
| No se elimina un equipo con préstamos activos | `EquipoService.eliminar` (409) |
| Campos obligatorios y fechas válidas | Bean Validation en `EquipoRequest` y `PrestamoRequest` (400) |
| La fecha prevista de devolución no puede ser anterior a la del préstamo | `@AssertTrue` en `PrestamoRequest`, verificación en el servicio y `CHECK` en el script SQL |
| USER solo consulta; ADMIN gestiona | `SecurityConfig` (403) |

**Relación JPA.** `Prestamo` tiene `@ManyToOne` hacia `Equipo` y es el dueño de la relación, porque guarda la clave foránea `equipo_id`. `Equipo` tiene la otra cara, `@OneToMany(mappedBy = "equipo")`: un equipo acumula muchos préstamos en su historial, pero solo puede tener uno ACTIVO a la vez. Cuando se elimina un equipo sin préstamos activos, su historial de préstamos devueltos se borra en cascada.

## 9. Seguridad: flujo JWT, 401 y 403

```
React / Postman                         Spring Boot
      │  POST /api/auth/login {user, pass}   │
      │ ───────────────────────────────────► │ AuthenticationManager valida con BCrypt
      │ ◄─────────────────────────────────── │ JwtService firma el token (HS256): sub, rol, exp
      │  { token, rol }                      │
      │                                      │
      │  GET /api/equipos                    │
      │  Authorization: Bearer <token>       │
      │ ───────────────────────────────────► │ JwtAuthenticationFilter verifica la firma y la expiración
      │                                      │ → SecurityContext con ROLE_USER o ROLE_ADMIN
      │                                      │ → SecurityConfig decide: 200, 401 o 403
      │ ◄─────────────────────────────────── │
```

- **Sin sesiones:** `SessionCreationPolicy.STATELESS`. Cada solicitud se autentica solo con su token.
- **Secretos fuera del repositorio:** `JWT_SECRET` y `DB_PASSWORD` se leen de variables de entorno o del archivo `.env` local.
- **401 Unauthorized:** el servidor *no sabe quién eres*, porque no enviaste token, el token es inválido o expiró, o las credenciales son incorrectas. Lo responde `RestAuthenticationEntryPoint`.
- **403 Forbidden:** el servidor *sabe quién eres*, pero tu rol no tiene permiso. Por ejemplo, USER intentando un POST. Lo responde `RestAccessDeniedHandler`.

## 10. Pruebas con Postman

1. En Postman, importen `postman/TecnoUAM_Prestamos.postman_collection.json`.
2. Con el backend en ejecución, abran **Run collection** y ejecútenla en orden. También pueden enviar cada solicitud una por una.
3. La carpeta **0. Autenticación** guarda automáticamente `adminToken` y `userToken`. Las solicitudes siguientes los usan, y también los valores `equipoId` y `prestamoId` que se van generando.

Las carpetas siguen los puntos que pide el examen:

| Carpeta | Demuestra |
|---|---|
| 1. Alta de equipo | 201; 400 por datos inválidos; 409 por código duplicado |
| 2. Consulta de equipos | 200; 404 por equipo inexistente |
| 3. Actualización | 200; 404 |
| 4. Registro de préstamo | 201 y el equipo pasa a PRESTADO; 400 por fecha inválida; 404 |
| 5. Prestar nuevamente | 409 |
| 7b. Eliminación bloqueada | 409 mientras el préstamo sigue ACTIVO |
| 6. Devolución | 200 y el equipo vuelve a DISPONIBLE; 409 si se devuelve dos veces |
| 7a. Eliminación permitida | 204 y luego 404 |
| 8. Seguridad | 401 sin token y con token inválido; 200 con USER; 403 con USER; 201 con ADMIN |

Cada solicitud incluye *tests* que verifican el código HTTP esperado.

**Punto 9 (visualización en React):** creen o presten un equipo desde Postman y luego pulsen **Actualizar** o recarguen `http://localhost:5173`. La tabla refleja el cambio porque consulta la API real.

## 11. Pruebas automáticas

```bash
./mvnw test
```

Son 14 pruebas de integración (MockMvc sobre H2 en memoria) que validan los códigos 200/201/204/400/401/403/404/409 y todas las reglas de negocio. No necesitan PostgreSQL.

## 12. Equipo de trabajo

| Integrante | Responsabilidad principal |
|---|---|
| Integrante 1 | Entidades, relaciones JPA y base de datos |
| Integrante 2 | CRUD, controladores y servicios |
| Integrante 3 | Spring Security, JWT y roles |
| Integrante 4 | React, Axios y visualización |
| Integrante 5 | Pruebas Postman, integración y evidencias |
