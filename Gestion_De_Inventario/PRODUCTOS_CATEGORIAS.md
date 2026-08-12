# Estado del Backend: Productos y Categorias

Este documento resume lo aplicado hasta ahora en los modulos de `Productos` y `Categorias`.

## Arquitectura Aplicada

Se esta usando una estructura por capas:

- `controller`: expone endpoints REST.
- `services`: contiene reglas de negocio y validaciones.
- `repository`: acceso a datos con Spring Data JPA.
- `model`: entidades JPA relacionadas con la base de datos.
- `dtos/request`: DTOs para crear y actualizar.
- `dtos/response`: DTOs para respuestas de la API.
- `mapper`: conversion manual entre entidades y DTOs.

## Categorias

### DTOs

Request:

- `CategoriaCreateDTO`
- `CategoriaUpdateDTO`

Response:

- `CategoriaDTO`

`CategoriaCreateDTO` y `CategoriaUpdateDTO` contienen:

```json
{
  "nombre": "Tecnologia",
  "descripcion": "Productos tecnologicos"
}
```

`CategoriaDTO` responde:

```json
{
  "idCategoria": 1,
  "nombre": "Tecnologia",
  "descripcion": "Productos tecnologicos",
  "estado": true
}
```

### Endpoints

Listar categorias:

```http
GET /api/categorias
```

Filtros disponibles:

```http
GET /api/categorias?page=1&estado=true&nombre=tec
```

Reglas:

- `page` inicia en `1`.
- `estado` por defecto es `true`.
- Se muestran `10` items por pagina.
- `nombre` filtra por coincidencia parcial y sin distinguir mayusculas/minusculas.

Obtener por id:

```http
GET /api/categorias/{id}
```

Crear:

```http
POST /api/categorias
```

Actualizar:

```http
PUT /api/categorias/{id}
```

Eliminar logicamente:

```http
DELETE /api/categorias/{id}
```

Reactivar:

```http
PATCH /api/categorias/{id}/reactivar
```

### Reglas de Negocio

- Al crear una categoria, `estado` se asigna como `true`.
- Al eliminar una categoria, no se elimina fisicamente; se cambia `estado` a `false`.
- Al reactivar una categoria, se cambia `estado` a `true`.
- Al actualizar, los campos son parciales:
  - Si un campo viene `null`, no se modifica.
  - Si un texto viene vacio, no se modifica.
  - Se conserva el valor anterior de la base de datos.
- Se agrego `@DynamicUpdate` para que Hibernate actualice solo columnas modificadas.

## Productos

### DTOs

Request:

- `ProductoCreateDTO`
- `ProductoUpdateDTO`

Response:

- `ProductoDTO`
- `ProductoCategoriaDTO`
- `UnidadMedidaDTO`

`ProductoCreateDTO` contiene:

```json
{
  "codigo": "PROD001",
  "nombre": "Laptop Lenovo",
  "descripcion": "Laptop para oficina",
  "idUnidadMedida": 1,
  "precioVenta": 2500.00,
  "stockMinimo": 15,
  "idCategoria": 1
}
```

`ProductoUpdateDTO` no permite cambiar `codigo`:

```json
{
  "nombre": "Laptop Lenovo ThinkPad",
  "descripcion": "Laptop para oficina actualizada",
  "idUnidadMedida": 1,
  "precioVenta": 2600.00,
  "stockMinimo": 10,
  "idCategoria": 1
}
```

`ProductoDTO` responde:

```json
{
  "idProducto": 1,
  "codigo": "PROD001",
  "nombre": "Laptop Lenovo",
  "descripcion": "Laptop para oficina",
  "precioVenta": 2500.00,
  "stockMinimo": 15.000,
  "estado": true,
  "categoria": {
    "idCategoria": 1,
    "nombre": "Tecnologia"
  },
  "unidadMedida": {
    "idUnidadMedida": 1,
    "nombre": "Unidad"
  }
}
```

### Endpoints

Listar productos:

```http
GET /api/productos
```

Filtros disponibles:

```http
GET /api/productos?page=1&estado=true&nombre=laptop&codigo=PROD&idUnidadMedida=1
```

Reglas:

- `page` inicia en `1`.
- `estado` por defecto es `true`.
- Se muestran `10` items por pagina.
- `nombre` filtra por coincidencia parcial y sin distinguir mayusculas/minusculas.
- `codigo` filtra por coincidencia parcial y sin distinguir mayusculas/minusculas.
- `idUnidadMedida` filtra por unidad de medida exacta.

Obtener por id:

```http
GET /api/productos/{id}
```

Crear:

```http
POST /api/productos
```

Actualizar:

```http
PUT /api/productos/{id}
```

Eliminar logicamente:

```http
DELETE /api/productos/{id}
```

Reactivar:

```http
PATCH /api/productos/{id}/reactivar
```

### Reglas de Negocio

- Al crear un producto, `estado` se asigna como `true`.
- `codigo` solo se envia al crear; no se modifica al actualizar.
- El producto debe tener una categoria existente.
- El producto debe tener una unidad de medida existente.
- No se permite asignar una categoria inactiva a un producto.
- Al eliminar un producto, no se elimina fisicamente; se cambia `estado` a `false`.
- Al reactivar un producto, se cambia `estado` a `true`.
- Al actualizar, los campos son parciales:
  - Si un campo viene `null`, no se modifica.
  - Si un texto viene vacio, no se modifica.
  - Se conserva el valor anterior de la base de datos.
- Se agrego `@DynamicUpdate` para que Hibernate actualice solo columnas modificadas.

## Paginacion

Las respuestas paginadas usan `PageDTO`.

Formato:

```json
{
  "items": [],
  "totalItems": 0,
  "totalPages": 0,
  "page": 1,
  "size": 10
}
```

Notas:

- `page` se maneja como base 1 para la API.
- Internamente Spring Data usa base 0.
- El backend convierte `page=1` a `PageRequest.of(0, 10)`.

## Optimizacion Contra N+1

En productos se agrego `@EntityGraph` para cargar `categoria` y `unidadMedida` junto con la consulta principal.

Esto evita consultas adicionales por cada producto al mapear el response.

Repository:

```java
@EntityGraph(attributePaths = {"categoria", "unidadMedida"})
Page<Producto> findAll(Specification<Producto> spec, Pageable pageable);
```

## Estado Actual

Los modulos de `Productos` y `Categorias` ya tienen:

- DTOs separados para request y response.
- DTOs separados para create y update.
- Mappers manuales.
- Paginacion.
- Filtros.
- Delete logico.
- Reactivacion.
- Actualizacion parcial.
- Validaciones basicas de negocio.
- Optimizacion contra N+1 en listado de productos.

