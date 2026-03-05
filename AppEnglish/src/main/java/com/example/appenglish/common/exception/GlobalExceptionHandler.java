package com.example.AppEnglish.common.exception;

import com.example.AppEnglish.common.dto.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // 1. Xử lý lỗi logic API (Code cũ của bạn)
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

    // 3. Xử lý lỗi Trùng lặp (ĐÃ SỬA Ở ĐÂY)
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

    // 4. Xử lý lỗi chung (Luôn để cuối cùng)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Void>> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error("Internal server error", "INTERNAL_ERROR", request.getRequestURI()));
    }
}