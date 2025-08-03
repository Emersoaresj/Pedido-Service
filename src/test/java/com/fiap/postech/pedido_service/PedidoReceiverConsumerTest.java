package com.fiap.postech.pedido_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.postech.pedido_service.api.consumer.PedidoReceiverConsumer;
import com.fiap.postech.pedido_service.api.dto.PedidoStatus;
import com.fiap.postech.pedido_service.api.dto.client.estoque.PedidoBaixaEstoqueResponse;
import com.fiap.postech.pedido_service.api.dto.client.pagamento.PedidoPagamentoResponse;
import com.fiap.postech.pedido_service.api.dto.kafka.PedidoItemKafkaDTO;
import com.fiap.postech.pedido_service.api.dto.kafka.PedidoKafkaDTO;
import com.fiap.postech.pedido_service.domain.exception.PedidoNotFoundException;
import com.fiap.postech.pedido_service.domain.model.Pedido;
import com.fiap.postech.pedido_service.domain.model.PedidoItem;
import com.fiap.postech.pedido_service.gateway.client.PedidoEstoqueClient;
import com.fiap.postech.pedido_service.gateway.client.PedidoPagamentoClient;
import com.fiap.postech.pedido_service.gateway.port.PedidoItemRepositoryPort;
import com.fiap.postech.pedido_service.gateway.port.PedidoRepositoryPort;
import com.fiap.postech.pedido_service.gateway.port.PedidoServicePort;
import feign.FeignException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.Acknowledgment;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoReceiverConsumerTest {

    @InjectMocks
    private PedidoReceiverConsumer consumer;

    @Mock
    private PedidoRepositoryPort pedidoRepositoryPort;

    @Mock
    private PedidoItemRepositoryPort pedidoItemRepositoryPort;

    @Mock
    private PedidoServicePort pedidoServicePort;

    @Mock
    private PedidoEstoqueClient estoqueClient;

    @Mock
    private PedidoPagamentoClient pagamentoClient;

    @Mock
    private Acknowledgment acknowledgment;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PedidoKafkaDTO mockPedidoKafkaDTO() {
        PedidoItemKafkaDTO item = new PedidoItemKafkaDTO();
        item.setIdProduto(1);
        item.setQuantidadeItem(2);

        PedidoKafkaDTO dto = new PedidoKafkaDTO();
        dto.setIdPedido(1);
        dto.setIdCliente(1);
        dto.setValorTotalPedido(new BigDecimal("100.00"));
        dto.setItens(List.of(item));
        return dto;
    }

    private Pedido mockPedido() {
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1);
        pedido.setStatusPedido(PedidoStatus.ABERTO);
        return pedido;
    }

    @Test
    void deveLidarComPedidoNaoEncontradoSemLancarExcecao() throws Exception {
        PedidoKafkaDTO dto = mockPedidoKafkaDTO();
        String mensagem = objectMapper.writeValueAsString(dto);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("pedidos-criados", 0, 0, null, mensagem);

        when(pedidoRepositoryPort.buscarPedidoPorId(dto.getIdPedido())).thenReturn(null);

        // Act (não esperamos exceção)
        consumer.processarPedido(record, acknowledgment);

        // Assert: a exceção é tratada internamente, então só verificamos que houve acknowledge
        verify(acknowledgment).acknowledge();
    }


    @Test
    void deveFecharPedidoSemEstoque() throws Exception {
        PedidoKafkaDTO dto = mockPedidoKafkaDTO();
        String mensagem = objectMapper.writeValueAsString(dto);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("pedidos-criados", 0, 0, null, mensagem);

        when(pedidoRepositoryPort.buscarPedidoPorId(dto.getIdPedido())).thenReturn(mockPedido());
        when(pedidoItemRepositoryPort.buscarItensPedido(any())).thenReturn(List.of(new PedidoItem()));
        when(estoqueClient.baixaEstoque(any())).thenReturn(new PedidoBaixaEstoqueResponse(false, "Estoque insuficiente"));

        consumer.processarPedido(record, acknowledgment);

        verify(pedidoServicePort).atualizaStatusPedido(dto.getIdPedido(), PedidoStatus.FECHADO_SEM_ESTOQUE);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void deveFecharPedidoSemCreditoQuandoPagamentoRecusado() throws Exception {
        PedidoKafkaDTO dto = mockPedidoKafkaDTO();
        String mensagem = objectMapper.writeValueAsString(dto);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("pedidos-criados", 0, 0, null, mensagem);

        when(pedidoRepositoryPort.buscarPedidoPorId(dto.getIdPedido())).thenReturn(mockPedido());
        when(pedidoItemRepositoryPort.buscarItensPedido(any())).thenReturn(List.of(new PedidoItem()));
        when(estoqueClient.baixaEstoque(any())).thenReturn(new PedidoBaixaEstoqueResponse(true, "Estoque baixado com sucesso"));

        FeignException erroPagamento = mock(FeignException.class);
        when(erroPagamento.status()).thenReturn(402);
        when(pagamentoClient.processarPagamento(any())).thenThrow(erroPagamento);

        consumer.processarPedido(record, acknowledgment);

        verify(pedidoServicePort).atualizaStatusPedido(dto.getIdPedido(), PedidoStatus.FECHADO_SEM_CREDITO);
        verify(estoqueClient).restaurarEstoque(any());
        verify(acknowledgment).acknowledge();
    }

    @Test
    void deveFecharPedidoComSucessoQuandoTudoOK() throws Exception {
        PedidoKafkaDTO dto = mockPedidoKafkaDTO();
        String mensagem = objectMapper.writeValueAsString(dto);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("pedidos-criados", 0, 0, null, mensagem);

        when(pedidoRepositoryPort.buscarPedidoPorId(dto.getIdPedido())).thenReturn(mockPedido());
        when(pedidoItemRepositoryPort.buscarItensPedido(any())).thenReturn(List.of(new PedidoItem()));
        when(estoqueClient.baixaEstoque(any())).thenReturn(new PedidoBaixaEstoqueResponse(true, "Estoque baixado com sucesso"));
        when(pagamentoClient.processarPagamento(any())).thenReturn(new PedidoPagamentoResponse(true, "Pagamento aprovado"));

        consumer.processarPedido(record, acknowledgment);

        verify(pedidoServicePort).atualizaStatusPedido(dto.getIdPedido(), PedidoStatus.FECHADO_COM_SUCESSO);
        verify(acknowledgment).acknowledge();
    }

    @Test
    void deveLogarErroQuandoExceptionGenericaOcorre() throws Exception {
        PedidoKafkaDTO dto = mockPedidoKafkaDTO();
        String mensagem = objectMapper.writeValueAsString(dto);
        ConsumerRecord<String, String> record = new ConsumerRecord<>("pedidos-criados", 0, 0, null, mensagem);

        when(pedidoRepositoryPort.buscarPedidoPorId(any())).thenThrow(new RuntimeException("Erro inesperado"));

        consumer.processarPedido(record, acknowledgment);

        verify(acknowledgment).acknowledge();
    }
}

