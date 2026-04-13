# Hotel Search Challenge

## Descripción

Este proyecto es una API desarrollada con Spring Boot que permite registrar búsquedas de hoteles y consultar cuántas veces se repitió una misma búsqueda.

La idea principal es desacoplar la escritura usando Kafka y luego persistir en base de datos de forma asíncrona.


## Cómo funciona su flujo

### POST /search

Recibe una búsqueda con:

* hotelId
* fechas (checkIn / checkOut)
* edades

Hace lo siguiente:

1. valida los datos
2. genera un searchId
3. publica un evento en Kafka
4. devuelve el searchId

---

### GET /count

Recibe un searchId y:

1. busca la búsqueda original
2. cuenta cuántas veces se repitió en la base
3. devuelve el resultado

---

## Cómo levantar el proyecto

Requisitos:

* Docker
* Docker Compose

Ejecutar:


docker-compose up --build


---
* API: http://localhost:8080
* Swagger: http://localhost:8080/swagger-ui.html
* Tambien se agregó una collection de Postman para probar la API, 
  se encuentra en el proyecto en la carpeta resources