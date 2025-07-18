package com.fiap.postech.pedido_receiver.api.mapper;

import com.fiap.postech.pedido_receiver.domain.model.PedidoItem;
import com.fiap.postech.pedido_receiver.gateway.database.entity.PedidoItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface PedidoItemMapper {

    PedidoItemMapper INSTANCE = Mappers.getMapper(PedidoItemMapper.class);

    @Mapping(target = "idPedido", ignore = true)
    List<PedidoItem> entityToDomain(List<PedidoItemEntity> entity);

}

