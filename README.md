# Sistema de Gestión de Biblioteca

Trabajo Práctico N° 2 – Ingeniería de Software (Prof. Ariel Reynoso)
Integrantes: Agustina Gallardo y Candela Quevedo

Aplicación de consola en Java que gestiona socios, libros, préstamos y devoluciones de una biblioteca, con arquitectura en capas y persistencia en PostgreSQL.

## Requisitos

- Java 17
- Maven
- PostgreSQL y pgAdmin
- IntelliJ IDEA (recomendado)

## 1. Crear la base de datos

Los scripts están en la carpeta `sql/` y se ejecutan en pgAdmin, en este orden:

1. `01_crear_bd.sql`: abrir el Query Tool sobre la base **postgres** y ejecutarlo. Crea la base `biblioteca`.
2. `02_tablas.sql`: abrir el Query Tool sobre la base **biblioteca** y ejecutarlo. Crea las tablas `socio`, `libro` y `prestamo`.
3. `03_datos.sql`: en la misma base, ejecutarlo. Carga datos de prueba.

## 2. Configurar la conexión

1. En `src/main/resources/`, copiar `db.properties.example` y renombrar la copia a `db.properties`.
2. Completar los datos de PostgreSQL:

   db.url=jdbc:postgresql://localhost:5432/biblioteca 
   db.user=postgres
   db.password=tu_contraseña

El archivo `db.properties` no se sube al repositorio (está en `.gitignore`).
## 3. Ejecutar

Desde IntelliJ: abrir `src/main/java/biblioteca/presentacion/Main.java` y ejecutarlo (Run).

Desde la terminal, en la carpeta del proyecto:

    mvn compile exec:java

Al iniciar se muestra el menú para elegir el perfil: Socio, Bibliotecario o Administrador.
## Perfiles y operaciones

| Perfil | Operaciones |
|---|---|
| Socio | Consultar libros y su disponibilidad. Consultar sus propios préstamos. |
| Bibliotecario | Registrar, modificar, consultar y dar de baja socios. Consultar libros. Registrar préstamos y devoluciones. Consultar el historial de un socio. Listar préstamos vigentes y vencidos. |
| Administrador | Registrar, modificar, consultar y dar de baja libros. Consultar socios y su historial. Listar préstamos vigentes y vencidos. |

Para ingresar como Socio se indica un número de socio existente.

## Datos de prueba

| Caso | Resultado esperado al registrar un préstamo |
|---|---|
| Socio 1003 (inactivo) | Rechazado |
| Socio 1002 (tiene un préstamo vencido) | Rechazado |
| Socio 1004 (tiene 3 préstamos sin devolver) | Rechazado |
| Libro 8 "El Aleph" (inactivo) | Rechazado |
| Socio 1001 con el libro 2 | Se registra y muestra el comprobante |

## Estructura del proyecto

```
sql/                       Scripts de base de datos
src/main/java/biblioteca/
  modelo/                  Socio, Libro, Prestamo
  presentacion/            Menús de consola
  negocio/                 Servicios con las reglas de negocio
  datos/                   Conexión y DAOs
```