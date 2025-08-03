package com.fiap.postech.pedido_service;

import com.fiap.postech.pedido_service.api.dto.PedidoStatus;
import com.fiap.postech.pedido_service.api.dto.ResponseDto;
import com.fiap.postech.pedido_service.domain.exception.ErroInternoException;
import com.fiap.postech.pedido_service.domain.model.Pedido;
import com.fiap.postech.pedido_service.gateway.port.PedidoRepositoryPort;
import com.fiap.postech.pedido_service.service.PedidoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceImplTest {

    @InjectMocks
    private PedidoServiceImpl pedidoService;

    @Mock
    private PedidoRepositoryPort repositoryPort;

    @Test
    void deveAtualizarStatusDoPedidoComSucesso() {
        // Arrange
        Integer idPedido = 1;
        PedidoStatus novoStatus = PedidoStatus.FECHADO_COM_SUCESSO;

        Pedido pedidoExistente = new Pedido();
        pedidoExistente.setIdPedido(idPedido);
        pedidoExistente.setIdCliente(1);
        pedidoExistente.setStatusPedido(PedidoStatus.ABERTO);
        pedidoExistente.setDataPedido(LocalDateTime.now());
        pedidoExistente.setValorTotalPedido(new BigDecimal("150.00"));

        // Simula o comportamento do repositório retornando o pedido e um response
        when(repositoryPort.buscarPedidoPorId(idPedido)).thenReturn(pedidoExistente);

        ResponseDto respostaEsperada = new ResponseDto();
        respostaEsperada.setMessage("Pedido atualizado");

        Map<String, Object> data = new HashMap<>();
        data.put("idPedido", pedidoExistente.getIdPedido());
        data.put("idCliente", pedidoExistente.getIdCliente());
        data.put("statusPedido", novoStatus); // o status já foi alterado antes do retorno
        data.put("dataPedido", pedidoExistente.getDataPedido());
        data.put("valorTotalPedido", pedidoExistente.getValorTotalPedido());

        respostaEsperada.setData(data);

        when(repositoryPort.atualizarPedido(pedidoExistente)).thenReturn(respostaEsperada);

        // Act
        ResponseDto resposta = pedidoService.atualizaStatusPedido(idPedido, novoStatus);

        // Assert
        assertEquals("Pedido atualizado", resposta.getMessage());
        assertNotNull(resposta.getData());
        assertTrue(resposta.getData() instanceof Map);

        Map<?, ?> responseData = (Map<?, ?>) resposta.getData();
        assertEquals(idPedido, responseData.get("idPedido"));
        assertEquals(1, responseData.get("idCliente"));
        assertEquals(novoStatus, responseData.get("statusPedido"));
        assertEquals(pedidoExistente.getValorTotalPedido(), responseData.get("valorTotalPedido"));
        assertEquals(pedidoExistente.getDataPedido(), responseData.get("dataPedido"));

        verify(repositoryPort).buscarPedidoPorId(idPedido);
        verify(repositoryPort).atualizarPedido(pedidoExistente);
    }


    @Test
    void deveLancarErroInternoQuandoAtualizacaoFalha() {
        // Arrange
        Integer idPedido = 1;
        PedidoStatus novoStatus = PedidoStatus.FECHADO_SEM_CREDITO;

        when(repositoryPort.buscarPedidoPorId(idPedido)).thenThrow(new RuntimeException("Falha DB"));

        // Act & Assert
        ErroInternoException exception = assertThrows(
                ErroInternoException.class,
                () -> pedidoService.atualizaStatusPedido(idPedido, novoStatus)
        );

        assertTrue(exception.getMessage().contains("Erro ao atualizar status do pedido"));

        verify(repositoryPort).buscarPedidoPorId(idPedido);
        verify(repositoryPort, never()).atualizarPedido(any());
    }
}
