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

1. Copiar el archivo `db.properties.example` y renombrar la copia a `db.properties`.
2. Completar el usuario y la contraseña de PostgreSQL.

El archivo `db.properties` no se sube al repositorio.

## 3. Ejecutar

PENDIENTE: completar con Candela.

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