package com.fiap.postech.pedido_service.api.dto.client.pagamento;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PedidoPagamentoResponse {
    private boolean aprovado;
    private String mensagem;
}

