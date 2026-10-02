# SplitBill

App Android (Java) para dividir gastos entre amigos, con una API en Spring Boot y PostgreSQL.

## Cómo cumple los requisitos del proyecto

| Requisito | Dónde está |
|---|---|
| Login y autenticación con hash de contraseña | `LoginActivity` / `RegisterActivity` → `POST /api/auth/login` y `/register`. El servidor guarda la contraseña con **BCrypt** (`AuthService`). La sesión queda en **SharedPreferences** (`SessionManager`). |
| CRUD con base de datos local SQLite | **Mis recibos**: `ManagerDataBase` (SQLiteOpenHelper) + `ReceiptContract` + `ReceiptRepository` (insertar, listar, buscar, actualizar, borrado lógico). Las fotos se guardan como **archivos** de la app. |
| También Room | **Historial de la cuenta rápida**: `QuickSplitHistory` (@Entity), `QuickSplitHistoryDao` (@Dao) y `AppDatabase` (@Database), en un archivo de base distinto al de SQLite. |
| Dos recursos del dispositivo | **Cámara**: foto del recibo (`ReceiptFormActivity`). **Contactos**: agregar integrantes desde la agenda (`MembersActivity`). Los dos piden el permiso en el momento de usarlo. |
| API en Spring Boot con PostgreSQL, 3 CRUD consumidos | **Grupos**, **Integrantes** y **Gastos**, consumidos con **Retrofit**. Además, la liquidación y la cuenta rápida se **calculan en el servidor** (`SplitCalculator`). |

## Estructura (MVC)

```
backend/                      Spring Boot 4 · Java 17 · Maven
  controller/   C  → reciben las peticiones HTTP (AuthController, GroupController, MemberController,
                     ExpenseController, CalculationController)
  service/      lógica de negocio y validaciones; SplitCalculator hace toda la matemática
  repository/   M  → acceso a PostgreSQL con Spring Data JPA
  entity/       M  → tablas (users, groups, members, expenses)
  dto/          datos que entran y salen en JSON
  exception/    errores → {"message": "..."} con el código HTTP correcto
  resources/schema.sql   las tablas, escritas a mano (prefijos use_, grp_, mem_, exp_ y status 0/1)

app/                          Android · Java
  controller/   C  → las Activities (una por pantalla)
  view/         V  → adaptadores de RecyclerView y formato de pesos  (+ los XML de res/layout)
  model/        M  → repositorios: Retrofit para la API y SQLite para los recibos
  model/remote/ RetrofitClient y ApiService (la interfaz con todos los endpoints)
  entity/       POJOs que Gson llena con el JSON, y Receipt para SQLite
  manager/      ManagerDataBase, ReceiptContract y SessionManager
```

## Pantallas

Login · Registro · Inicio (grupos, cuenta rápida, recibos) · Nuevo/editar grupo · Detalle del grupo ·
Integrantes · Nuevo/editar gasto · Liquidación · Cuenta rápida · Mis recibos · Nuevo/editar recibo.

## Cómo correrlo

1. **Base de datos** (Docker Desktop encendido):
   ```
   cd backend
   docker compose up -d
   ```
   Usa el puerto **5433** para no chocar con otro PostgreSQL instalado en el equipo.
2. **API**:
   ```
   cd backend
   ./mvnw spring-boot:run
   ```
   Queda en `http://localhost:8080`. Las tablas se crean solas con `schema.sql`.
   Pruebas del cálculo: `./mvnw test`.
3. **App**: abrir la carpeta del proyecto en Android Studio y darle Run.
   - En el **emulador** funciona tal cual (usa `http://10.0.2.2:8080/`).
   - En un **celular físico**: en `local.properties` poner
     `splitbill.apiBaseUrl=http://localhost:8080/` y correr `adb reverse tcp:8080 tcp:8080`.

## Endpoints

| Método | Ruta | Qué hace |
|---|---|---|
| POST | `/api/auth/register` | Crear cuenta |
| POST | `/api/auth/login` | Iniciar sesión |
| GET | `/api/groups?userId=1` | Grupos del usuario (con total e integrantes) |
| GET / PUT / DELETE | `/api/groups/{id}` | Ver, editar, eliminar grupo |
| POST | `/api/groups` | Crear grupo (quien lo crea queda como integrante) |
| GET / POST | `/api/groups/{id}/members` | Listar / agregar integrantes |
| PUT / DELETE | `/api/members/{id}` | Editar / eliminar integrante |
| GET / POST | `/api/groups/{id}/expenses` | Listar / agregar gastos |
| PUT / DELETE | `/api/expenses/{id}` | Editar / eliminar gasto |
| GET | `/api/groups/{id}/settlement` | Saldos y transferencias (calculado en el servidor) |
| POST | `/api/calculations/quick-split` | Cuenta rápida con propina (no se guarda) |

## Reglas del cálculo

- Los montos son **pesos enteros** (`long`), nunca `double`, para no perder pesos por redondeo.
- Cada gasto se reparte en **partes iguales** entre los integrantes activos. Si no da exacto, los primeros
  pagan 1 peso más (100 / 3 = 34, 33, 33), así la suma siempre cuadra.
- Saldo = lo que pagó − lo que le tocaba. Las transferencias salen de un algoritmo voraz: el que más debe le
  paga al que más le deben, y se repite; resultan como máximo (integrantes − 1) pagos.
- No se puede eliminar a un integrante que pagó gastos (la liquidación quedaría descuadrada).
- Los borrados son **lógicos** (`status = 0`), igual en PostgreSQL y en SQLite.
