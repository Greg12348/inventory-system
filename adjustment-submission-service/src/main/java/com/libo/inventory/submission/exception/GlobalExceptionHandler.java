package com.libo.inventory.submission.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InventoryQueryException.class)
    public ProblemDetail handleInventoryQuery(InventoryQueryException exception,
                                              HttpServletRequest request) {
        return problem(exception.getStatus(), exception.getCode(), exception.getMessage(), request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleInvalidParameter(MethodArgumentTypeMismatchException exception,
                                                HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "INVALID_INPUT",
                "参数 " + exception.getName() + " 必须是有效的整数，且不能超出允许的数值范围", request);
    }

    private ProblemDetail problem(HttpStatus status, String code, String message,
                                  HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, message);
        problem.setTitle(status.getReasonPhrase());
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("code", code);
        return problem;
    }
}
