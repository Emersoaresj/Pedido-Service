package com.fiap.postech.pedido_service.gateway.port;

import com.fiap.postech.pedido_service.api.dto.PedidoStatus;
import com.fiap.postech.pedido_service.api.dto.ResponseDto;

public interface PedidoServicePort {

    ResponseDto atualizaStatusPedido(Integer id, PedidoStatus novoStatus);
}
