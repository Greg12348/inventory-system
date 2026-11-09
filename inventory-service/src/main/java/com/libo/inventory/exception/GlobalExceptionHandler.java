package com.libo.inventory.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.net.URI;

/** 统一处理库存不存在及非法输入，不拦截其他类型的异常。 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InventoryNotFoundException.class)
    public ProblemDetail handleNotFound(InventoryNotFoundException exception,
                                      HttpServletRequest request) {
        return problem(HttpStatus.NOT_FOUND, "Inventory not found", "INVENTORY_NOT_FOUND",
                exception.getMessage(), request);
    }

    @ExceptionHandler(InvalidInputException.class)
    public ProblemDetail handleInvalidInput(InvalidInputException exception,
                                          HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid input", "INVALID_INPUT",
                exception.getMessage(), request);
    }

    // 非数字或超出 Long 范围的路径参数，在进入 Controller 方法前由 Spring 抛出此异常。
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException exception,
                                          HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid input", "INVALID_INPUT",
                "参数 " + exception.getName() + " 必须是有效的整数，且不能超出允许的数值范围", request);
    }

    private ProblemDetail problem(HttpStatus status, String title, String code,
                                  String detail, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setInstance(URI.create(request.getRequestURI()));
        // 客户端可以根据稳定的 code 判断错误类型，不必解析提示文字。
        problem.setProperty("code", code);
        return problem;
    }
}
