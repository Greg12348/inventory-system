package com.libo.inventory.submission.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/** 库存查询的业务错误；不向调用方暴露下游服务的原始错误内容。 */
@Getter
public class InventoryQueryException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

    public InventoryQueryException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
}
