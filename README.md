# Hotel Search Challenge

## Descripción

Este proyecto es una API Challenge desarrollada con Spring Boot que permite registrar búsquedas de hoteles y consultar cuántas veces se repitió una misma búsqueda.

La idea principal es desacoplar la escritura usando Kafka y luego persistir en base de datos de forma asíncrona y usando hexagonal.

---

## Cómo funciona su flujo

### POST /search

Recibe una búsqueda con:

- hotelId
- fechas (checkIn / checkOut)
- edades

Hace lo siguiente:

1. valida los datos
2. genera un searchId
3. publica un evento en Kafka
4. devuelve el searchId

Ejemplo request:

```json
{
  "hotelId": "1234aBc",
  "checkIn": "29/12/2023",
  "checkOut": "31/12/2023",
  "ages": [30, 29, 1, 3]
}
```

Ejemplo response:

```json
{
  "searchId": "uuid-123"
}
```

---

### GET /count

Recibe un searchId y:

1. busca la búsqueda original
2. cuenta cuántas veces se repitió en la base
3. devuelve el resultado

Ejemplo request:

```text
GET /count?searchId=uuid-123
```

Ejemplo response:

```json
{
  "searchId": "uuid-123",
  "count": 2,
  "search": {
    "hotelId": "1234aBc",
    "checkIn": "29/12/2023",
    "checkOut": "31/12/2023",
    "ages": [30, 29, 1, 3]
  }
}
```

---

## Cómo levantar el proyecto

### Requisitos

- Docker
- Docker Compose

### Ejecutar

```bash
docker-compose up --build
```

---

## Accesos

API:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

---

## Pruebas

También se agregó una collection de Postman para probar la API.

Se encuentra en el proyecto en la carpeta:

```text
src/main/resources
```
