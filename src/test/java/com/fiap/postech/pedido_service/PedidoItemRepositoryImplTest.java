package com.fiap.postech.pedido_service;

import com.fiap.postech.pedido_service.api.mapper.PedidoItemMapper;
import com.fiap.postech.pedido_service.domain.exception.ErroInternoException;
import com.fiap.postech.pedido_service.domain.model.PedidoItem;
import com.fiap.postech.pedido_service.gateway.database.PedidoItemRepositoryImpl;
import com.fiap.postech.pedido_service.gateway.database.entity.PedidoItemEntity;
import com.fiap.postech.pedido_service.gateway.database.repository.PedidoItemRepositoryJPA;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PedidoItemRepositoryImplTest {

    @InjectMocks
    private PedidoItemRepositoryImpl repository;

    @Mock
    private PedidoItemRepositoryJPA pedidoItemRepositoryJPA;

    @Test
    void deveBuscarItensDoPedidoComSucesso() {
        // Arrange
        Integer idPedido = 1;

        PedidoItemEntity itemEntity = new PedidoItemEntity();
        itemEntity.setIdPedido(idPedido);
        itemEntity.setIdProduto(1);
        itemEntity.setQuantidadeItem(2);
        itemEntity.setPrecoUnitarioItem(BigDecimal.valueOf(50));

        List<PedidoItemEntity> entityList = List.of(itemEntity);
        List<PedidoItem> expectedDomainList = PedidoItemMapper.INSTANCE.entityToDomain(entityList);

        when(pedidoItemRepositoryJPA.findAllByIdPedido(idPedido)).thenReturn(entityList);

        // Act
        List<PedidoItem> result = repository.buscarItensPedido(idPedido);

        // Assert
        assertNotNull(result);
        assertEquals(expectedDomainList.size(), result.size());
        assertEquals(expectedDomainList.get(0).getIdProduto(), result.get(0).getIdProduto());
        assertEquals(expectedDomainList.get(0).getQuantidadeItem(), result.get(0).getQuantidadeItem());
        assertEquals(expectedDomainList.get(0).getPrecoUnitarioItem(), result.get(0).getPrecoUnitarioItem());

        verify(pedidoItemRepositoryJPA).findAllByIdPedido(idPedido);
    }

    @Test
    void deveLancarErroInternoAoFalharNaBuscaDeItens() {
        // Arrange
        Integer idPedido = 1;

        when(pedidoItemRepositoryJPA.findAllByIdPedido(idPedido))
                .thenThrow(new RuntimeException("Erro no banco"));

        // Act & Assert
        ErroInternoException exception = assertThrows(
                ErroInternoException.class,
                () -> repository.buscarItensPedido(idPedido)
        );

        assertTrue(exception.getMessage().contains("Erro ao buscar itens do pedido"));
        verify(pedidoItemRepositoryJPA).findAllByIdPedido(idPedido);
    }
}

