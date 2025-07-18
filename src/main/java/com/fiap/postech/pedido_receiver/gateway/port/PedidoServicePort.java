package com.fiap.postech.pedido_receiver.gateway.port;

import com.fiap.postech.pedido_receiver.api.dto.PedidoStatus;
import com.fiap.postech.pedido_receiver.api.dto.ResponseDto;

public interface PedidoServicePort {

    ResponseDto atualizaStatusPedido(Integer id, PedidoStatus novoStatus);
}
