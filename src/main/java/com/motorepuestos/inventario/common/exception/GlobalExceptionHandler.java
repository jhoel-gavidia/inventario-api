package com.motorepuestos.inventario.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        log.warn("Recurso no encontrado: {}", exception.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);
    }

    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            ResourceConflictException exception,
            HttpServletRequest request
    ) {
        log.warn("Conflicto de recurso: {}", exception.getMessage());
        return buildResponse(HttpStatus.CONFLICT, exception.getMessage(), request);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(
            BusinessException exception,
            HttpServletRequest request
    ) {
        log.warn("Error de negocio: {}", exception.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {

        Map<String, String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Valor inválido",
                        (existing, duplicate) -> existing,
                        LinkedHashMap::new
                ));

        log.warn("Errores de validación en {}: {}", request.getRequestURI(), errors);

        return buildValidationResponse(errors, request);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleMethodValidation(
            HandlerMethodValidationException exception,
            HttpServletRequest request
    ) {

        Map<String, String> errors = new LinkedHashMap<>();

        for (ParameterValidationResult result : exception.getParameterValidationResults()) {
            String nombre = result.getMethodParameter() != null
                    ? result.getMethodParameter().getParameterName()
                    : null;

            String clave = nombre != null ? nombre : "parametro";

            result.getResolvableErrors().forEach(error ->
                    errors.putIfAbsent(
                            clave,
                            error.getDefaultMessage() != null
                                    ? error.getDefaultMessage()
                                    : "Valor inválido"
                    )
            );
        }

        log.warn("Errores de validación en {}: {}", request.getRequestURI(), errors);

        return buildValidationResponse(errors, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        Map<String, String> errors = exception.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        v -> v.getMessage(),
                        (existing, duplicate) -> existing,
                        LinkedHashMap::new
                ));

        log.warn("Violación de restricciones en {}: {}", request.getRequestURI(), errors);

        return buildValidationResponse(errors, request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        log.warn("Cuerpo de la petición inválido en {}", request.getRequestURI());
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición no es válido",
                request
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParameter(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "Falta el parámetro '{}' en {}",
                exception.getParameterName(),
                request.getRequestURI()
        );
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Falta el parámetro obligatorio '" + exception.getParameterName() + "'",
                request
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        String tipo = exception.getRequiredType() != null
                ? exception.getRequiredType().getSimpleName()
                : "desconocido";
        String message = String.format(
                "El parámetro '%s' debe ser de tipo %s",
                exception.getName(),
                tipo
        );
        log.warn("Tipo inválido en {}: {}", request.getRequestURI(), message);
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {

        log.warn("Método no soportado en {}: {}", request.getRequestURI(), exception.getMethod());

        return buildResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                String.format(
                        "El método %s no está soportado en este recurso",
                        exception.getMethod()
                ),
                request
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception,
            HttpServletRequest request
    ) {
        log.warn(
                "Content-Type no soportado en {}: {}",
                request.getRequestURI(),
                exception.getContentType()
        );
        return buildResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "El Content-Type '" + exception.getContentType() + "' no está soportado",
                request
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFound(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) {
        log.warn("Recurso no encontrado en {}", request.getRequestURI());
        return buildResponse(HttpStatus.NOT_FOUND, "El recurso no existe", request);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException exception,
            HttpServletRequest request
    ) {
        log.warn("Error de autenticación en {}", request.getRequestURI());

        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                "Credenciales inválidas",
                request
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(
            IllegalStateException exception,
            HttpServletRequest request
    ) {
        log.error(
                "Estado inválido en {}: {}",
                request.getRequestURI(),
                exception.getMessage(),
                exception
        );
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado. Contacte al administrador.",
                request
        );
    }

    @ExceptionHandler(UnauthenticatedUserException.class)
    public ResponseEntity<ErrorResponse> handleUnauthenticatedUser(
            UnauthenticatedUserException exception,
            HttpServletRequest request
    ) {
        log.warn("Sin usuario autenticado en {}", request.getRequestURI());
        return buildResponse(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {
        log.warn("Acceso denegado en {}", request.getRequestURI());

        return buildResponse(
                HttpStatus.FORBIDDEN,
                "No tiene permisos para acceder a este recurso",
                request
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException exception,
            HttpServletRequest request
    ) {
        log.error(
                "Violación de integridad en {}: {}",
                request.getRequestURI(),
                exception.getMostSpecificCause().getMessage()
        );
        return buildResponse(
                HttpStatus.CONFLICT,
                "La operación entra en conflicto con el estado actual del recurso. Inténtelo de nuevo.",
                request
        );
    }

    @ExceptionHandler(PessimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handlePessimisticLocking(
            PessimisticLockingFailureException exception,
            HttpServletRequest request
    ) {
        log.error(
                "No se pudo obtener el bloqueo de {}: {}",
                request.getRequestURI(),
                exception.getMessage()
        );
        return buildResponse(
                HttpStatus.CONFLICT,
                "El recurso está siendo modificado por otra operación. Inténtelo de nuevo.",
                request
        );
    }

    @ExceptionHandler(CannotAcquireLockException.class)
    public ResponseEntity<ErrorResponse> handleCannotAcquireLock(
            CannotAcquireLockException exception,
            HttpServletRequest request
    ) {
        log.error(
                "No se pudo adquirir el bloqueo de {}: {}",
                request.getRequestURI(),
                exception.getMessage()
        );
        return buildResponse(
                HttpStatus.CONFLICT,
                "El recurso está siendo modificado por otra operación. Inténtelo de nuevo.",
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(
            Exception exception,
            HttpServletRequest request
    ) {
        log.error("Error no controlado en {}", request.getRequestURI(), exception);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error inesperado. Contacte al administrador.",
                request
        );
    }

    private ResponseEntity<ErrorResponse> buildValidationResponse(
            Map<String, String> errors,
            HttpServletRequest request
    ) {

        ErrorResponse response = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message("Errores de validación")
                .errors(errors)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.badRequest().body(response);
    }

    private ResponseEntity<ErrorResponse> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        ErrorResponse response = ErrorResponse.builder()
                .status(status.value())
                .message(message)
                .path(request.getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(status).body(response);
    }
}