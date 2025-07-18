package com.fiap.postech.pedido_receiver.api.mapper;

import com.fiap.postech.pedido_receiver.domain.model.Pedido;
import com.fiap.postech.pedido_receiver.gateway.database.entity.PedidoEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PedidoMapper {

    PedidoMapper INSTANCE = Mappers.getMapper(PedidoMapper.class);

    @Mapping(target = "itens", ignore = true)
    Pedido entityToDomain (PedidoEntity pedidoEntity);

    PedidoEntity domainToEntityUpdate(Pedido pedido);

}
