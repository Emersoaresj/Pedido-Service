package com.fiap.postech.pedido_service.gateway.database;

import com.fiap.postech.pedido_service.domain.model.PedidoItem;
import com.fiap.postech.pedido_service.domain.exception.ErroInternoException;
import com.fiap.postech.pedido_service.gateway.database.entity.PedidoItemEntity;
import com.fiap.postech.pedido_service.gateway.database.repository.PedidoItemRepositoryJPA;
import com.fiap.postech.pedido_service.gateway.port.PedidoItemRepositoryPort;
import com.fiap.postech.pedido_service.api.mapper.PedidoItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
@Repository
public class PedidoItemRepositoryImpl implements PedidoItemRepositoryPort {

    @Autowired
    private PedidoItemRepositoryJPA pedidoItemRepositoryJPA;


    @Override
    public List<PedidoItem> buscarItensPedido(Integer idPedido) {
        try {
            List<PedidoItemEntity> entity = pedidoItemRepositoryJPA.findAllByIdPedido(idPedido);
            return PedidoItemMapper.INSTANCE.entityToDomain(entity);
        } catch (Exception e) {
            throw new ErroInternoException("Erro ao buscar itens do pedido: " + e.getMessage());
        }

    }
}
