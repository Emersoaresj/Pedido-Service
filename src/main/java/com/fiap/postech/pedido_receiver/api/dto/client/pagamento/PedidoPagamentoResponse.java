package com.fiap.postech.pedido_receiver.api.dto.client.pagamento;

import lombok.Data;

@Data
public class PedidoPagamentoResponse {
    private boolean aprovado;
    private String mensagem;
}

