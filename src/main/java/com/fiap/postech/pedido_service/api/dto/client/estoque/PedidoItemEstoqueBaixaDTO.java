package com.fiap.postech.pedido_service.api.dto.client.estoque;

import lombok.Data;

@Data
public class PedidoItemEstoqueBaixaDTO {

    private Integer idProduto;
    private Integer quantidadeItem;
}

