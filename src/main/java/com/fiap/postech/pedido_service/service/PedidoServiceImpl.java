package com.fiap.postech.pedido_service.service;

import com.fiap.postech.pedido_service.domain.model.Pedido;
import com.fiap.postech.pedido_service.api.dto.PedidoStatus;
import com.fiap.postech.pedido_service.api.dto.ResponseDto;
import com.fiap.postech.pedido_service.domain.exception.ErroInternoException;
import com.fiap.postech.pedido_service.gateway.port.PedidoRepositoryPort;
import com.fiap.postech.pedido_service.gateway.port.PedidoServicePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PedidoServiceImpl implements PedidoServicePort {

    @Autowired
    private PedidoRepositoryPort repositoryPort;


    @Override
    public ResponseDto atualizaStatusPedido(Integer id, PedidoStatus novoStatus) {
        try {
            Pedido pedidoExistente = repositoryPort.buscarPedidoPorId(id);
            pedidoExistente.setStatusPedido(novoStatus);

            return repositoryPort.atualizarPedido(pedidoExistente);
        } catch (Exception e) {
            log.error("Erro ao atualizar status do pedido: {}", e.getMessage());
            throw new ErroInternoException("Erro ao atualizar status do pedido: " + e.getMessage());
        }

    }
}
