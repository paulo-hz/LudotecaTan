# Ludoteca (Tutorial Full-Stack Capgemini)

Aplicación web desarrollada como proyecto de iniciación full-stack, basada en la gestión de una ludoteca (catálogo de juegos de mesa, categorías, autores, clientes y control de prestamos).

---

## 🛠️ Stack Tecnológico

### Backend (`tutorial/`)
- **Java 17+** con **Spring Boot**.
- **Spring Data JPA** & **Hibernate** para la persistencia.
- **Base de datos H2** (en memoria) para desarrollo y testing rápido.
- **ModelMapper** / DTOs para mapeo de entidades a transferencia de datos.
- **Maven** como gestor de dependencias y ciclo de vida.
- **Eclipse IDE**.

### Frontend (`client/`)
- **Angular 17+** (Standalone Components, nueva sintaxis de control de flujo `@if` / `@for`).
- **Angular Material** (diálogos modales, tablas paginadas, formularios reactivos).
- **TypeScript**, **SCSS** y **HTML5**.
- **Visual Studio Code**.

---

## 📁 Estructura del Repositorio

```text
LudotecaTan/
├── client/                     # Frontend en Angular 17+
│   ├── src/app/
│   │   ├── category/           # Módulo/Componentes de Categorías
│   │   ├── author/             # Módulo/Componentes de Autores (paginado)
│   │   ├── game/               # Módulo/Componentes de Juegos (filtrado)
│   │   └── core/               # Layout, Header y servicios comunes
│   └── package.json
├── tutorial/                   # Backend Spring Boot
│   ├── src/main/java/com/ccsw/tutorial/
│   │   ├── category/           # Controller, Service, Repository, Entity, DTO
│   │   ├── author/             # Paginación y ordenación
│   │   ├── game/               # Filtros y relaciones
│   │   └── config/             # Configuración de ModelMapper, CORS, etc.
│   ├── src/main/resources/
│   │   ├── application.properties
│   │   └── data.sql            # Semilla inicial de datos en H2
│   └── pom.xml
└── .gitignore                  # Exclusiones de librerías y temporales
