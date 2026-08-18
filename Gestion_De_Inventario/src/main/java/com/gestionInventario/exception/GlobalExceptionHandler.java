package com.gestionInventario.exception;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.gestionInventario.dtos.response.ErrorResponseDTO;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 1. Recurso no encontrado (HTTP 404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleResourceNotFoundException(
            ResourceNotFoundException ex, 
            HttpServletRequest request) {

        ErrorResponseDTO errorDTO = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    // 2. Fallos de validación de Bean Validation - @Valid (HTTP 400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationExceptions(
            MethodArgumentNotValidException ex, 
            HttpServletRequest request) {

        Map<String, String> fieldErrors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        ErrorResponseDTO errorDTO = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Datos de entrada inválidos",
                request.getRequestURI(),
                fieldErrors
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    // 3. Reglas de negocio/Argumentos inválidos (HTTP 400)
    @ExceptionHandler({BusinessRuleException.class, IllegalArgumentException.class})
    public ResponseEntity<ErrorResponseDTO> handleBadRequestException(
            RuntimeException ex,
            HttpServletRequest request) {

        ErrorResponseDTO errorDTO = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    // 4. Violaciones de integridad conocidas (HTTP 409/400)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex,
            HttpServletRequest request) {

        ConstraintError constraintError = clasificarConstraint(ex)
                .orElse(new ConstraintError(
                        HttpStatus.BAD_REQUEST,
                        "La solicitud viola una restricción de datos"));

        ErrorResponseDTO errorDTO = new ErrorResponseDTO(
                constraintError.status().value(),
                constraintError.status().getReasonPhrase(),
                constraintError.message(),
                request.getRequestURI()
        );

        return ResponseEntity.status(constraintError.status()).body(errorDTO);
    }

    // 5. Rutas o recursos estáticos no encontrados (HTTP 404)
    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public ResponseEntity<ErrorResponseDTO> handleRouteNotFoundException(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponseDTO errorDTO = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                "La ruta solicitada no existe",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    // 6. Captura general para errores no controlados (HTTP 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(
            Exception ex, 
            HttpServletRequest request) {

        logger.error("Error inesperado en {}", request.getRequestURI(), ex);

        ErrorResponseDTO errorDTO = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ocurrió un error interno",
                request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
    }

    private Optional<ConstraintError> clasificarConstraint(DataIntegrityViolationException ex) {
        String detalle = obtenerDetalleConstraint(ex);

        if (contiene(detalle, "uq_usuarios_correo")) {
            return Optional.of(conflict("Ya existe un usuario con ese correo"));
        }
        if (contiene(detalle, "uq_productos_codigo")) {
            return Optional.of(conflict("Ya existe un producto con ese código"));
        }
        if (contiene(detalle, "uq_compras_comprobante")) {
            return Optional.of(conflict("Ya existe una compra con ese comprobante"));
        }
        if (contiene(detalle, "uq_proveedores_ruc")) {
            return Optional.of(conflict("Ya existe un proveedor con ese RUC"));
        }
        if (contiene(detalle, "uq_proveedores_correo")) {
            return Optional.of(conflict("Ya existe un proveedor con ese correo"));
        }
        if (contiene(detalle, "categorias") && esDuplicado(detalle)) {
            return Optional.of(conflict("Ya existe una categoría con ese nombre"));
        }
        if (contiene(detalle, "almacenes") && esDuplicado(detalle)) {
            return Optional.of(conflict("Ya existe un almacén con ese nombre"));
        }
        if (contiene(detalle, "duplicate entry") || contiene(detalle, "duplicate key")) {
            return Optional.of(conflict("Ya existe un registro con esos datos únicos"));
        }
        if (contiene(detalle, "foreign key") || contiene(detalle, "not null") || contiene(detalle, "check constraint")) {
            return Optional.of(new ConstraintError(
                    HttpStatus.BAD_REQUEST,
                    "La solicitud viola una restricción de datos"));
        }

        return Optional.empty();
    }

    private ConstraintError conflict(String message) {
        return new ConstraintError(HttpStatus.CONFLICT, message);
    }

    private String obtenerDetalleConstraint(Throwable ex) {
        Throwable current = ex;
        StringBuilder detalle = new StringBuilder();

        while (current != null) {
            if (current.getMessage() != null) {
                detalle.append(' ').append(current.getMessage());
            }

            if (current instanceof org.hibernate.exception.ConstraintViolationException constraintException
                    && constraintException.getConstraintName() != null) {
                detalle.append(' ').append(constraintException.getConstraintName());
            }

            current = current.getCause();
        }

        return detalle.toString().toLowerCase();
    }

    private boolean contiene(String value, String fragment) {
        return value != null && value.contains(fragment);
    }

    private boolean esDuplicado(String detalle) {
        return contiene(detalle, "duplicate entry") || contiene(detalle, "duplicate key");
    }

    private record ConstraintError(HttpStatus status, String message) {
    }
}
