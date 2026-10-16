# Inventario API

API REST para la gestión de inventario de un taller de mecánica y venta de repuestos: productos, categorías, movimientos de stock (entradas/salidas), usuarios y auditoría de cambios.

> Este repositorio es el **backend**. El frontend vive en un proyecto aparte: [inventario-frontend](https://github.com/jhoel-gavidia/inventario-frontend).

---

## Stack

| Tecnología | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.1 |
| Spring Web / Validation / Data JPA / Security / Actuator | (gestionados por el parent) |
| PostgreSQL | 18 |
| Flyway | (starter de Spring Boot) |
| JJWT | 0.13.0 |
| MapStruct | 1.6.3 |
| Lombok | 1.18.36 |
| Bucket4j (rate limiting) | 8.19.0 |
| SpringDoc OpenAPI | 3.1.1 |
| Testcontainers | 2.0.5 |
| JUnit / Spring Boot Test | (gestionados por el parent) |

---

## Arquitectura

Organización por **dominios (feature-based)**: cada módulo agrupa su propio controller, service, repository, entity, dto y mapper.

```
src/main/java/com/motorepuestos/inventario
├── auth/                       # Login, logout, /me, cambio de contraseña y JWT
│   ├── controller/
│   ├── dto/
│   └── service/
├── usuario/                    # CRUD de usuarios y roles
├── categoria/                  # CRUD de categorías (con soft delete)
├── producto/                   # CRUD de productos y actualización de stock
├── movimiento/                 # Registro de entradas/salidas y su detalle
├── auditoria/                  # Trazabilidad de cambios (antes/después)
├── common/
│   ├── config/                 # SecurityConfig, OpenApiConfig
│   ├── dto/                    # PaginaResponse
│   ├── exception/              # Excepciones de dominio y manejo global
│   ├── security/               # Filtros JWT, rate limit, entry point / access denied
│   └── util/                   # JsonUtil
└── InventarioApiApplication.java
```

```
src/main/resources
├── application.properties          # Configuración base (lee variables de entorno)
├── application-dev.properties      # Perfil de desarrollo
├── application-docker.properties   # Perfil para Docker Compose
├── application-prod.properties     # Perfil de producción (Swagger deshabilitado)
└── db/migration/                   # Migraciones Flyway (V1, V2, V3)
```

---

## Requisitos

- **JDK 21** (o Docker, si prefieres levantar todo en contenedores).
- **PostgreSQL 18** (o usar el de `compose.yaml`).
- **Maven Wrapper** incluido (`mvnw` / `mvnw.cmd`), no necesitas Maven instalado.

---

## Variables de entorno

La configuración base (`application.properties`) **no tiene valores por defecto** para la base de datos ni el JWT: todo se inyecta por variables de entorno. Toma como referencia `.env.example`:

| Variable | Descripción | Ejemplo |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Perfil activo (`dev`, `docker`, `prod`) | `docker` |
| `DB_URL` | URL JDBC de PostgreSQL | `jdbc:postgresql://localhost:5432/inventario` |
| `DB_USERNAME` | Usuario de la base de datos | `postgres` |
| `DB_PASSWORD` | Contraseña de la base de datos | `change_me_in_production` |
| `JWT_SECRET` | Clave secreta para firmar los JWT (mín. 256 bits) | `(cadena aleatoria larga)` |
| `JWT_EXPIRATION` | Vigencia del token en **milisegundos** | `86400000` (24 h) |
| `CORS_ALLOWED_ORIGIN` | Origen permitido para el frontend (default: `http://localhost:3000`) | `https://mi-frontend.com` |

> Para Docker Compose también se usan `POSTGRES_DB`, `POSTGRES_USER` y `POSTGRES_PASSWORD` (los consume el servicio `postgres`).

Copia el ejemplo y ajusta los valores:

```bash
cp .env.example .env
```

---

## Ejecución

### Opción A — Todo con Docker Compose (recomendada)

Levanta PostgreSQL y la API con un solo comando:

```bash
docker compose up -d --build
```

- API: http://localhost:8080
- PostgreSQL: `localhost:5432`
- Health check: http://localhost:8080/actuator/health

Para detener y eliminar contenedores (conservando los datos del volumen):

```bash
docker compose down
```

Para eliminar también los datos:

```bash
docker compose down -v
```

### Opción B — Local con Maven

Necesitas un PostgreSQL accesible y las variables de entorno exportadas (el archivo `.env` no se carga solo fuera de Docker).

```bash
# Con el perfil dev
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

En Windows (PowerShell):

```powershell
$env:SPRING_PROFILES_ACTIVE="dev"
$env:DB_URL="jdbc:postgresql://localhost:5432/inventario"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="postgres"
$env:JWT_SECRET="una-clave-larga-y-segura-de-al-menos-32-bytes"
./mvnw.cmd spring-boot:run
```

### Compilar y empaquetar

```bash
./mvnw clean package          # compila + tests + genera el jar
./mvnw clean package -DskipTests
java -jar target/inventario-api-0.0.1-SNAPSHOT.jar
```

---

## Scripts útiles

| Comando | Descripción |
|---|---|
| `./mvnw spring-boot:run` | Ejecuta la aplicación en desarrollo |
| `./mvnw clean package` | Compila, ejecuta tests y empaqueta el jar |
| `./mvnw verify` | Compila y ejecuta la suite completa (usado en CI) |
| `./mvnw test` | Ejecuta solo los tests |
| `./mvnw -DskipTests package` | Empaqueta sin ejecutar tests |

---

## Seguridad y autenticación

- Autenticación **stateless** con **JWT** almacenado en una **cookie `access_token`** (no en `localStorage`).
- La cookie se emite con `HttpOnly`, `Secure`, `SameSite=None` y `Path=/`, con una vigencia de **2 horas**.
- El `JwtAuthenticationFilter` lee el token desde la cookie, valida la firma/expiración y verifica que el `jti` no esté revocado (blacklist en memoria).
- **CSRF deshabilitado** (API stateless) y **CORS** restringido a `CORS_ALLOWED_ORIGIN` con credenciales habilitadas.
- **Rate limiting** en `POST /api/v1/auth/login` con Bucket4j: **5 intentos por IP cada 15 minutos**; al excederlos responde `429` con header `Retry-After`.
- Los errores de autenticación (`401`) y autorización (`403`) se devuelven como JSON mediante `RestAuthenticationEntryPoint` y `RestAccessDeniedHandler`.

### Roles

| Rol | Descripción |
|---|---|
| `ADMIN` | Acceso total: productos, categorías, movimientos, usuarios y auditoría. |
| `USER` | Consulta productos/categorías y registra movimientos. Sin acceso a usuarios ni auditoría. |

### Endpoints de autenticación

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| `POST` | `/api/v1/auth/login` | Público | Inicia sesión y setea la cookie. Responde `200` sin body. |
| `POST` | `/api/v1/auth/logout` | Público | Revoca el token (blacklist) y limpia la cookie. |
| `GET` | `/api/v1/auth/me` | Autenticado | Devuelve el usuario de la sesión (id, username, rol, estado). |
| `PUT` | `/api/v1/auth/password` | Autenticado | Cambia la contraseña del usuario autenticado. |

---

## Recursos (endpoints)

Base URL: `/api/v1`

### Productos

| Método | Ruta | Rol | Descripción |
|---|---|---|---|
| `GET` | `/productos` | USER, ADMIN | Lista todos los productos |
| `GET` | `/productos/{id}` | USER, ADMIN | Obtiene un producto por id |
| `POST` | `/productos` | ADMIN | Crea un producto |
| `PUT` | `/productos/{id}` | ADMIN | Actualiza un producto |
| `DELETE` | `/productos/{id}` | ADMIN | Elimina un producto |

### Categorías

| Método | Ruta | Rol | Descripción |
|---|---|---|---|
| `GET` | `/categorias` | USER, ADMIN | Lista categorías activas |
| `GET` | `/categorias/{id}` | USER, ADMIN | Obtiene una categoría |
| `POST` | `/categorias` | ADMIN | Crea una categoría |
| `PUT` | `/categorias/{id}` | ADMIN | Actualiza una categoría |
| `DELETE` | `/categorias/{id}` | ADMIN | Elimina (soft delete) una categoría |

### Movimientos

| Método | Ruta | Rol | Descripción |
|---|---|---|---|
| `GET` | `/movimientos?pagina=0&tamano=20` | USER, ADMIN | Lista movimientos paginados |
| `GET` | `/movimientos/{id}` | USER, ADMIN | Obtiene un movimiento |
| `POST` | `/movimientos` | USER, ADMIN | Registra una entrada o salida con sus detalles |

### Usuarios

| Método | Ruta | Rol | Descripción |
|---|---|---|---|
| `GET` | `/usuarios` | ADMIN | Lista usuarios |
| `GET` | `/usuarios/{id}` | ADMIN | Obtiene un usuario |
| `POST` | `/usuarios` | ADMIN | Crea un usuario |
| `PUT` | `/usuarios/{id}` | ADMIN | Actualiza un usuario |
| `DELETE` | `/usuarios/{id}` | ADMIN | Elimina un usuario |

### Auditoría

| Método | Ruta | Rol | Descripción |
|---|---|---|---|
| `GET` | `/auditorias?pagina=0&tamano=20` | ADMIN | Lista auditorías paginadas |
| `GET` | `/auditorias?entidad=Producto&entidadId=1` | ADMIN | Filtra auditorías por entidad e id |
| `GET` | `/auditorias/{id}` | ADMIN | Obtiene una auditoría (incluye antes/después) |

---

## Respuestas paginadas

Los endpoints de **movimientos** y **auditorías** devuelven un objeto `PaginaResponse<T>` en lugar de un arreglo:

```json
{
  "contenido": [ ... ],
  "pagina": 0,
  "tamano": 20,
  "totalElementos": 120,
  "totalPaginas": 6,
  "primera": true,
  "ultima": false
}
```

Parámetros de query: `pagina` (base 0, default `0`) y `tamano` (entre 1 y 100, default `20`).

> Productos, categorías y usuarios devuelven arreglos planos (`List<T>`), sin paginación.

---

## Ejemplos de uso

**Login** (guarda la cookie con `-c`):

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin1234"}' \
  -c cookies.txt
```

**Consultar el usuario de la sesión:**

```bash
curl http://localhost:8080/api/v1/auth/me -b cookies.txt
```

**Crear un producto:**

```bash
curl -X POST http://localhost:8080/api/v1/productos \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "codigo": "REP-001",
    "nombre": "Pastilla de freno",
    "categoriaId": 1,
    "precioCompra": 15.00,
    "precioVenta": 25.00,
    "stockInicial": 20,
    "estado": true
  }'
```

**Registrar una entrada de stock (varios detalles):**

```bash
curl -X POST http://localhost:8080/api/v1/movimientos \
  -H "Content-Type: application/json" \
  -b cookies.txt \
  -d '{
    "tipo": "ENTRADA",
    "detalles": [
      { "productoId": 1, "cantidad": 10 },
      { "productoId": 2, "cantidad": 5 }
    ]
  }'
```

---

## Manejo de errores

Todas las respuestas de error comparten el mismo formato (`ErrorResponse`):

```json
{
  "status": 400,
  "message": "Errores de validación",
  "errors": { "codigo": "El código es obligatorio" },
  "path": "/api/v1/productos",
  "timestamp": "2026-01-15T10:30:00"
}
```

| Código | Cuándo ocurre |
|---|---|
| `400` | Validación de entrada, error de negocio o parámetros inválidos |
| `401` | Sin autenticar o credenciales inválidas |
| `403` | Autenticado pero sin permisos para el recurso |
| `404` | Recurso no encontrado |
| `405` | Método HTTP no soportado |
| `409` | Conflicto (datos duplicados, bloqueos concurrentes) |
| `415` | `Content-Type` no soportado |
| `429` | Demasiados intentos de login |
| `500` | Error inesperado |

El manejo centralizado vive en `common/exception/GlobalExceptionHandler.java`.

---

## Modelo de datos

| Tabla | Descripción |
|---|---|
| `usuarios` | Usuarios con `username` único, `password` (BCrypt), `rol` y `estado`. |
| `categorias` | Categorías con `estado` para **soft delete** y nombre único entre las activas. |
| `productos` | Productos con `codigo` único entre activos, precios de compra/venta y `stock_actual`. |
| `movimientos` | Cabecera de un movimiento (`ENTRADA` / `SALIDA`), usuario y fecha. |
| `detalles_movimientos` | Detalle (producto + cantidad) de cada movimiento. |
| `auditorias` | Registro de cambios (`accion`, `entidad`, `entidad_id`, `datos_ant`, `datos_new` en JSONB). |

### Migraciones Flyway

| Versión | Contenido |
|---|---|
| `V1__crear_esquema_inicial.sql` | Crea todas las tablas y la FK de auditoría. |
| `V2__soft_delete_categorias.sql` | Agrega `estado` a categorías (soft delete). |
| `V3__invariantes_stock_e_indices.sql` | Checks (`stock >= 0`, `cantidad > 0`), índices y reemplazo de `UNIQUE` global por índices únicos parciales sobre registros activos. |

> En todos los perfiles `spring.jpa.hibernate.ddl-auto=validate`: el esquema **solo** se gestiona con Flyway, Hibernate no lo modifica.

---

## Tests

Las pruebas de integración usan **Testcontainers** para levantar un PostgreSQL real:

```
src/test/java/com/motorepuestos/inventario
├── auth/         # Seguridad de login/logout
├── security/     # Blacklist de tokens, rate limit
├── service/      # Tests de integración por servicio
└── support/      # AbstractIntegrationTest
```

Ejecutar la suite completa:

```bash
./mvnw verify
```

> Requiere Docker en ejecución para los contenedores de Testcontainers.

---

## Documentación de la API

Con la aplicación en ejecución (perfil `dev` o `docker`):

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

En **producción** (perfil `prod`) ambos quedan deshabilitados (`springdoc.*.enabled=false`).

---

## Docker y CI/CD

- **Dockerfile** multi-stage: compila con Maven + Temurin 21 y ejecuta sobre una imagen JRE 21 con usuario no root.
- **compose.yaml**: servicios `postgres` (18) y `api`, con `healthcheck` en ambos y la API esperando a que la base esté sana.
- **`ci.yml`**: en cada push/PR a `main` ejecuta `./mvnw verify` con JDK 21.
- **`cd.yml`**: en push a `main` construye y publica la imagen en **GitHub Container Registry** (`ghcr.io`) y luego dispara un deploy hook.

> Nota: el paso "Deploy to Render" del workflow de CD depende del secreto `RENDER_DEPLOY_HOOK` y apunta a un servicio que ya no está activo. Para un entorno real conviene reemplazarlo por el destino actual.

---

## Proyecto relacionado

- **Frontend (Next.js):** https://github.com/jhoel-gavidia/inventario-frontend

---

**Autor:** Jhoel Anderson Gavidia Calderón  
GitHub: https://github.com/jhoel-gavidia
