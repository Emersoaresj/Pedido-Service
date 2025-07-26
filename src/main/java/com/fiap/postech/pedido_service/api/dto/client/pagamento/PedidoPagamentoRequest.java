package com.fiap.postech.pedido_service.api.dto.client.pagamento;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PedidoPagamentoRequest {
    private Integer idPedido;
    private Integer idCliente;
    private BigDecimal valorTotalPedido;
}
