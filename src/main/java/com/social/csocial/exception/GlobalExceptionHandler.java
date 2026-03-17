package com.social.csocial.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException ex) {
        log.error("业务异常", ex);
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResource(NoResourceFoundException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", "请求路径不存在，请检查接口地址");
        body.put("errorType", ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        log.error("系统异常", ex);
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", ex.getMessage() == null ? "系统异常，请稍后重试" : ex.getMessage());
        body.put("errorType", ex.getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}

