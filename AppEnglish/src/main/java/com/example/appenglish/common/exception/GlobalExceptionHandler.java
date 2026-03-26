package com.example.appenglish.common.exception;

import com.example.appenglish.common.dto.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final Environment environment;

    public GlobalExceptionHandler(Environment environment) {
        this.environment = environment;
    }

    // 1. Xử lý lỗi logic API
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<BaseResponse<Void>> handleApiException(ApiException ex, HttpServletRequest request) {
        log.warn("ApiException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(ex.getMessage(), ex.getErrorCode(), request.getRequestURI()));
    }

    // 2. Xử lý lỗi Validation (Code cũ của bạn)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex,
                                                                              HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(BaseResponse.<Map<String, String>>builder()
                        .success(false)
                        .message("Validation failed")
                        .errorCode("VALIDATION_ERROR")
                        .data(errors)
                        .timestamp(java.time.Instant.now())
                        .path(request.getRequestURI())
                        .build());
    }

    // 3. Content-Type not supported / no handler found (e.g. wrong path or method)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<BaseResponse<Void>> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex, HttpServletRequest request) {
        log.warn("Media type not supported: {} for {}", ex.getContentType(), request.getRequestURI());
        String message = "No endpoint found for this URL and method. "
                + "Check the path (e.g. POST /api/v1/auth/login for login) and Content-Type.";
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.error(message, "ENDPOINT_NOT_FOUND", request.getRequestURI()));
    }

    // 4. Xử lý lỗi Trùng lặp (ĐÃ SỬA Ở ĐÂY)
    // Thay vì Exception.class, phải dùng đúng class DuplicateResourceException
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<BaseResponse<Void>> handleDuplicateResource(DuplicateResourceException ex, // Tham số cũng phải sửa
                                                                      HttpServletRequest request) {
        log.warn("Duplicate resource: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(BaseResponse.error(
                        ex.getMessage(),
                        "DUPLICATE_ERROR",
                        request.getRequestURI()
                ));
    }

    // 5. Bad request (e.g. LessonVideo not found, transcript missing for pronunciation)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<Void>> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        log.warn("Bad request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(ex.getMessage(), "BAD_REQUEST", request.getRequestURI()));
    }

    // 5b. DB constraint violation (e.g. cannot delete lesson with related data)
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<BaseResponse<Void>> handleDataIntegrity(org.springframework.dao.DataIntegrityViolationException ex, HttpServletRequest request) {
        log.warn("Data integrity violation: {}", ex.getMessage());
        String message = ConstraintMessageHelper.buildDeleteConstraintMessage(ex, "mục này");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error(message, "CONSTRAINT_VIOLATION", request.getRequestURI()));
    }

    // 5c. Client đóng kết nối sớm (Broken pipe) – thường khi stream video, user seek/refresh/thoát trang.
    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public ResponseEntity<BaseResponse<Void>> handleAsyncRequestNotUsable(AsyncRequestNotUsableException ex, HttpServletRequest request) {
        Throwable cause = ex.getCause();
        if (cause instanceof IOException) {
            String msg = cause.getMessage() != null ? cause.getMessage() : "";
            if (msg.contains("Broken pipe") || msg.contains("Connection reset")) {
                log.debug("Client closed connection while streaming: {}", msg);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
        }
        log.warn("Async request not usable: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.error("Request closed by client", "CLIENT_CLOSED", request.getRequestURI()));
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<BaseResponse<Void>> handleIOException(IOException ex, HttpServletRequest request) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "";
        if (msg.contains("Broken pipe") || msg.contains("Connection reset")) {
            log.debug("Client closed connection: {}", msg);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        log.warn("IO error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("I/O error", "IO_ERROR", request.getRequestURI()));
    }

    // 6. RuntimeException (e.g. Azure/timeout từ service khác) — trả JSON thay vì crash
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<Void>> handleRuntimeException(RuntimeException ex, HttpServletRequest request) {
        log.warn("RuntimeException: {}", ex.getMessage());
        String message = ex.getMessage() != null && !ex.getMessage().isBlank()
                ? ex.getMessage()
                : "Service temporarily unavailable. Please try again.";
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(BaseResponse.error(message, "SERVICE_ERROR", request.getRequestURI()));
    }

    // 7. Xử lý lỗi chung (Luôn để cuối cùng)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);

        // In local/dev, include a short root-cause message to speed up debugging.
        // In production, keep generic "Internal server error".
        boolean isProd = environment != null && environment.acceptsProfiles(org.springframework.core.env.Profiles.of("prod"));
        String message;
        if (isProd) {
            message = "Internal server error";
        } else {
            Throwable root = ex;
            while (root.getCause() != null && root.getCause() != root) {
                root = root.getCause();
            }
            String rootMsg = root.getMessage() != null ? root.getMessage() : root.getClass().getSimpleName();
            message = "Internal server error: " + rootMsg;
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error(message, "INTERNAL_ERROR", request.getRequestURI()));
    }
}