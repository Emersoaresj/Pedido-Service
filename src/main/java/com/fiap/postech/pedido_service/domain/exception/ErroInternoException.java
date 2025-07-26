package com.fiap.postech.pedido_service.domain.exception;

public class ErroInternoException extends RuntimeException {
    public ErroInternoException(String message) {
        super(message);
    }
}
