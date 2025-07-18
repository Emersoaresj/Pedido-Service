package com.fiap.postech.pedido_receiver.domain.exception;

public class ErroInternoException extends RuntimeException {
    public ErroInternoException(String message) {
        super(message);
    }
}
