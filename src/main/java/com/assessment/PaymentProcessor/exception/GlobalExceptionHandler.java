package com.assessment.PaymentProcessor.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIError> handleGlobalException(Exception ex, HttpServletRequest request) {
        log.error("Exception Instance/Type: {}", ex.getClass().getName(), ex);

        APIError apiError = APIError.builder()
                .message("An unexpected error occurred")
                .error(ex.getLocalizedMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<APIError> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.error("Unexpected error occurred: {}", ex.getHttpInputMessage(), ex);
        APIError apiError = APIError.builder()
                .message(ex.getMostSpecificCause().getMessage().split(":")[0])
                .error(ex.getMessage())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .path(request.getRequestURI())
                .build();

        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIError> handleApiException(APIException ex, HttpServletRequest request) {
        log.error("APIException: {}", ex.getMessage(), ex);
        APIError apiError = APIError.builder()
                .error(ex.getLocalizedMessage())
                .message(ex.getMessage())
                .statusCode(ex.getStatusCode())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(ex.getStatusCode()).body(apiError);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<APIError> userNotFoundException(UserNotFoundException ex, HttpServletRequest request) {
        log.error("UserNotFoundException: {}", ex.getMessage(), ex);
        APIError apiError = APIError.builder()
                .error(ex.getLocalizedMessage())
                .message(ex.getMessage())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<APIError> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        log.error("BadCredentialsException: {}", ex.getMessage(), ex);
        APIError apiError = APIError.builder()
                .error(ex.getLocalizedMessage())
                .message(ex.getMessage())
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<APIError> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest request) {
        log.error("AccessDeniedException: {}", ex.getMessage(), ex);
        APIError apiError = APIError.builder()
                .error(ex.getLocalizedMessage())
                .message(ex.getMessage())
                .statusCode(HttpStatus.FORBIDDEN.value())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<APIError> handleExpiredJwt(ExpiredJwtException e, HttpServletRequest request) {
        log.error("ExpiredJwtException: {}", e.getMessage(), e);
        APIError apiError = APIError.builder()
                .message("JWT token has expired")
                .error("JWT token expired")
                .statusCode(HttpServletResponse.SC_UNAUTHORIZED)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<APIError> handleInvalidJwt(JwtException e, HttpServletRequest request) {
        log.error("JwtException: {}", e.getMessage(), e);
        APIError apiError = APIError.builder()
                .message("Invalid JWT token")
                .error("JWT token invalid")
                .statusCode(HttpServletResponse.SC_UNAUTHORIZED)
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }



    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIError> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request){
        String errorMessages = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));
        APIError apiError = APIError.builder()
                .error(errorMessages)
                .message("Validation Failed")
                .statusCode(400)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(ex.getStatusCode()).body(apiError);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<APIError> handleConstraintViolationException(ConstraintViolationException ex, HttpServletRequest request){
        String errorMessages = ex.getConstraintViolations()
                .stream()
                .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
                .collect(Collectors.joining(", "));
        APIError apiError = APIError.builder()
                .error(errorMessages)
                .message("Validation Failed")
                .statusCode(400)
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(apiError);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<APIError> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request){
        APIError apiError = APIError.builder()
               .error(ex.getLocalizedMessage())
               .message(ex.getMessage())
               .statusCode(HttpStatus.NOT_FOUND.value())
               .path(request.getRequestURI())
               .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(apiError);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<APIError> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request){
        APIError apiError = APIError.builder()
                .error(ex.getLocalizedMessage())
                .message(ex.getMessage())
                .statusCode(HttpStatus.FAILED_DEPENDENCY.value())
                .path(request.getRequestURI())
                .build();
        return ResponseEntity.status(HttpStatus.FAILED_DEPENDENCY.value()).body(apiError);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<APIError> handleNoResourceFound(NoResourceFoundException e, HttpServletRequest request) {
        APIError apiError = APIError.builder()
                .message(e.getMessage())
                .error(e.getMessage())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<APIError> handleNoHandlerFound(NoHandlerFoundException e, HttpServletRequest request) {
        APIError apiError = APIError.builder()
                .message("No handler found for the requested URL")
                .error(e.getMessage())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .path(request.getRequestURI())
                .build();
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

}