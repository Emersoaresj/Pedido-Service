package com.fiap.postech.pedido_receiver.gateway.port;

import com.fiap.postech.pedido_receiver.domain.model.PedidoItem;

import java.util.List;

public interface PedidoItemRepositoryPort {

    List<PedidoItem> buscarItensPedido(Integer idPedido);
}
