package com.fiap.postech.pedido_service.api.dto.client.estoque;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PedidoBaixaEstoqueResponse {
    private boolean sucesso;
    private String mensagem;
}
