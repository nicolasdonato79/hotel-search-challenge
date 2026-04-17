# Hotel Search Challenge

## Descripción

Este proyecto es una API desarrollada con Spring Boot que permite registrar búsquedas de hoteles y consultar cuántas veces se repitió una misma búsqueda.

La idea principal es desacoplar la escritura usando Kafka y luego persistir en base de datos de forma asíncrona.

---

## Cómo funciona el flujo

### POST /search

Recibe una búsqueda con:

- hotelId
- fechas (checkIn / checkOut)
- edades

Hace lo siguiente:

1. Valida los datos
2. Genera un searchId
3. Publica un evento en Kafka
4. Devuelve el searchId

Ejemplo:

```json
{
  "hotelId": "1234aBc",
  "checkIn": "29/12/2023",
  "checkOut": "31/12/2023",
  "ages": [30, 29, 1, 3]
}