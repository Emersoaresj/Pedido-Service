package com.fiap.postech.pedido_service;

import com.fiap.postech.pedido_service.api.dto.PedidoStatus;
import com.fiap.postech.pedido_service.api.dto.ResponseDto;
import com.fiap.postech.pedido_service.api.mapper.PedidoMapper;
import com.fiap.postech.pedido_service.domain.exception.ErroInternoException;
import com.fiap.postech.pedido_service.domain.model.Pedido;
import com.fiap.postech.pedido_service.gateway.database.PedidoRepositoryImpl;
import com.fiap.postech.pedido_service.gateway.database.entity.PedidoEntity;
import com.fiap.postech.pedido_service.gateway.database.repository.PedidoRepositoryJPA;
import com.fiap.postech.pedido_service.utils.ConstantUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class PedidoRepositoryImplTest {

    @InjectMocks
    private PedidoRepositoryImpl repository;

    @Mock
    private PedidoRepositoryJPA pedidoRepositoryJPA;

    @Mock
    private PedidoMapper mapper; // se necessário injetar manualmente (gerado com MapStruct)

    @Test
    void deveBuscarPedidoPorIdComSucesso() {
        // Arrange
        Integer id = 1;

        // Criar a entidade que será retornada pelo repositório JPA
        PedidoEntity entity = new PedidoEntity();
        entity.setIdPedido(id);
        entity.setIdCliente(1);
        entity.setStatusPedido(PedidoStatus.ABERTO);
        entity.setValorTotalPedido(BigDecimal.valueOf(100));
        entity.setDataPedido(LocalDateTime.now());

        // Esperado após o mapper
        Pedido pedidoEsperado = PedidoMapper.INSTANCE.entityToDomain(entity);

        // Mock do repository JPA
        when(pedidoRepositoryJPA.findById(id)).thenReturn(Optional.of(entity));

        // Act
        Pedido result = repository.buscarPedidoPorId(id);

        // Assert
        assertNotNull(result);
        assertEquals(pedidoEsperado.getIdPedido(), result.getIdPedido());
        assertEquals(pedidoEsperado.getIdCliente(), result.getIdCliente());
        assertEquals(pedidoEsperado.getStatusPedido(), result.getStatusPedido());
        assertEquals(pedidoEsperado.getValorTotalPedido(), result.getValorTotalPedido());
        assertEquals(pedidoEsperado.getDataPedido(), result.getDataPedido());

        verify(pedidoRepositoryJPA).findById(id);
    }


    @Test
    void deveLancarErroQuandoPedidoNaoEncontrado() {
        // Arrange
        Integer id = 1;
        when(pedidoRepositoryJPA.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ErroInternoException exception = assertThrows(
                ErroInternoException.class,
                () -> repository.buscarPedidoPorId(id)
        );

        assertTrue(exception.getMessage().contains("Pedido não encontrado com ID: " + id));
    }

    @Test
    void deveAtualizarPedidoComSucesso() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1);
        pedido.setIdCliente(1);
        pedido.setStatusPedido(PedidoStatus.ABERTO);
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setValorTotalPedido(BigDecimal.valueOf(250));

        PedidoEntity entityToSave = PedidoMapper.INSTANCE.domainToEntityUpdate(pedido);
        PedidoEntity savedEntity = entityToSave;

        when(pedidoRepositoryJPA.save(entityToSave)).thenReturn(savedEntity);

        // Act
        ResponseDto response = repository.atualizarPedido(pedido);

        // Assert
        assertEquals(ConstantUtils.PEDIDO_ATUALIZADO, response.getMessage());

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) response.getData();
        assertEquals(pedido.getIdPedido(), data.get("idPedido"));
        assertEquals(pedido.getIdCliente(), data.get("idCliente"));
        assertEquals(pedido.getStatusPedido(), data.get("statusPedido"));
        assertEquals(pedido.getDataPedido(), data.get("dataPedido"));
        assertEquals(pedido.getValorTotalPedido(), data.get("valorTotalPedido"));

        verify(pedidoRepositoryJPA).save(entityToSave);
    }

    @Test
    void deveLancarErroAoFalharAoAtualizarPedido() {
        // Arrange
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1);

        when(pedidoRepositoryJPA.save(any())).thenThrow(new RuntimeException("Falha na base"));

        // Act & Assert
        ErroInternoException exception = assertThrows(
                ErroInternoException.class,
                () -> repository.atualizarPedido(pedido)
        );

        assertTrue(exception.getMessage().contains("Erro ao atualizar pedido"));
    }
}

