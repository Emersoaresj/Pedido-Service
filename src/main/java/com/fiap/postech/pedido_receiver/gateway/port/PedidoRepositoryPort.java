package com.fiap.postech.pedido_receiver.gateway.port;

import com.fiap.postech.pedido_receiver.domain.model.Pedido;
import com.fiap.postech.pedido_receiver.api.dto.ResponseDto;

public interface PedidoRepositoryPort {

    Pedido buscarPedidoPorId(Integer idPedido);

    ResponseDto atualizarPedido(Pedido pedido);
}
