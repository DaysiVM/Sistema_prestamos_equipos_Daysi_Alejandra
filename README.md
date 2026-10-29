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
