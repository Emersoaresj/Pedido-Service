package com.fiap.postech.pedido_service.gateway.port;

import com.fiap.postech.pedido_service.domain.model.Pedido;
import com.fiap.postech.pedido_service.api.dto.ResponseDto;

public interface PedidoRepositoryPort {

    Pedido buscarPedidoPorId(Integer idPedido);

    ResponseDto atualizarPedido(Pedido pedido);
}
