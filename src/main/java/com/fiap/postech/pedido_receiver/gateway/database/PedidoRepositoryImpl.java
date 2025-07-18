package com.fiap.postech.pedido_receiver.gateway.database;

import com.fiap.postech.pedido_receiver.domain.model.Pedido;
import com.fiap.postech.pedido_receiver.api.dto.ResponseDto;
import com.fiap.postech.pedido_receiver.domain.exception.ErroInternoException;
import com.fiap.postech.pedido_receiver.gateway.port.PedidoRepositoryPort;
import com.fiap.postech.pedido_receiver.gateway.database.entity.PedidoEntity;
import com.fiap.postech.pedido_receiver.gateway.database.repository.PedidoRepositoryJPA;
import com.fiap.postech.pedido_receiver.api.mapper.PedidoMapper;
import com.fiap.postech.pedido_receiver.utils.ConstantUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Repository
public class PedidoRepositoryImpl implements PedidoRepositoryPort {

    @Autowired
    private PedidoRepositoryJPA pedidoRepositoryJPA;


    @Override
    public Pedido buscarPedidoPorId(Integer idPedido) {
        PedidoEntity pedidoEntity = pedidoRepositoryJPA.findById(idPedido)
                .orElseThrow(() -> new ErroInternoException("Pedido não encontrado com ID: " + idPedido));
        return PedidoMapper.INSTANCE.entityToDomain(pedidoEntity);
    }

    @Override
    public ResponseDto atualizarPedido(Pedido pedido) {
        try {
            PedidoEntity entity = PedidoMapper.INSTANCE.domainToEntityUpdate(pedido);
            PedidoEntity updated = pedidoRepositoryJPA.save(entity);
            return montaResponse(updated);

        } catch (Exception e) {
            log.error("Erro ao atualizar pedido", e);
            throw new ErroInternoException("Erro ao atualizar pedido: " + e.getMessage());
        }
    }

    private ResponseDto montaResponse(PedidoEntity pedidoEntity) {
        ResponseDto response = new ResponseDto();

            response.setMessage(ConstantUtils.PEDIDO_ATUALIZADO);

        Map<String, Object> data = new HashMap<>();
        data.put("idPedido", pedidoEntity.getIdPedido());
        data.put("idCliente", pedidoEntity.getIdCliente());
        data.put("statusPedido", pedidoEntity.getStatusPedido());
        data.put("dataPedido", pedidoEntity.getDataPedido());
        data.put("valorTotalPedido", pedidoEntity.getValorTotalPedido());

        response.setData(data);
        return response;
    }
}
