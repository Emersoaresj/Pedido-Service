package com.fiap.postech.pedido_receiver.api.dto.client.estoque;

import lombok.Data;

import java.util.List;

@Data
public class PedidoBaixaEstoqueRequest {
    private List<PedidoItemEstoqueBaixaDTO> itens;
}
