package com.fiap.postech.pedido_service.utils;

import lombok.Data;

@Data
public class ConstantUtils {




    private ConstantUtils() {
        throw new IllegalStateException("Classe Utilitária");
    }


    //ERROS
    public static final String PEDIDO_NAO_ENCONTRADO = "Pedido não encontrado para o ID informado.";

    //SUCESSOS
    public static final String PEDIDO_ATUALIZADO = "Pedido atualizado com sucesso.";
}

