package com.fiap.postech.pedido_receiver.gateway.client;

import com.fiap.postech.pedido_receiver.api.dto.client.estoque.PedidoBaixaEstoqueRequest;
import com.fiap.postech.pedido_receiver.api.dto.client.estoque.PedidoBaixaEstoqueResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "pedido-estoque-service", url = "${estoque.service.url}")
public interface PedidoEstoqueClient {

    @PostMapping("/api/estoques/baixa")
    PedidoBaixaEstoqueResponse baixaEstoque(@RequestBody PedidoBaixaEstoqueRequest request);

    @PostMapping("/api/estoques/restaurar")
    PedidoBaixaEstoqueResponse restaurarEstoque(@RequestBody PedidoBaixaEstoqueRequest request);

}

