package com.fiap.postech.pedido_service.api.dto.client.pagamento;

import lombok.Data;

@Data
public class PedidoPagamentoResponse {
    private boolean aprovado;
    private String mensagem;
}

