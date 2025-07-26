package com.fiap.postech.pedido_service.api.dto.client.estoque;

import lombok.Data;

import java.util.List;

@Data
public class PedidoBaixaEstoqueRequest {
    private List<PedidoItemEstoqueBaixaDTO> itens;
}
