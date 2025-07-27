package com.fiap.postech.pedido_service.api.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.postech.pedido_service.domain.model.Pedido;
import com.fiap.postech.pedido_service.domain.model.PedidoItem;
import com.fiap.postech.pedido_service.api.dto.kafka.PedidoItemKafkaDTO;
import com.fiap.postech.pedido_service.api.dto.kafka.PedidoKafkaDTO;
import com.fiap.postech.pedido_service.api.dto.PedidoStatus;
import com.fiap.postech.pedido_service.api.dto.client.estoque.PedidoBaixaEstoqueRequest;
import com.fiap.postech.pedido_service.api.dto.client.estoque.PedidoBaixaEstoqueResponse;
import com.fiap.postech.pedido_service.api.dto.client.estoque.PedidoItemEstoqueBaixaDTO;
import com.fiap.postech.pedido_service.api.dto.client.pagamento.PedidoPagamentoRequest;
import com.fiap.postech.pedido_service.api.dto.client.pagamento.PedidoPagamentoResponse;
import com.fiap.postech.pedido_service.domain.exception.PedidoNotFoundException;
import com.fiap.postech.pedido_service.gateway.port.PedidoRepositoryPort;
import com.fiap.postech.pedido_service.gateway.client.PedidoEstoqueClient;
import com.fiap.postech.pedido_service.gateway.client.PedidoPagamentoClient;
import com.fiap.postech.pedido_service.gateway.port.PedidoItemRepositoryPort;
import com.fiap.postech.pedido_service.gateway.port.PedidoServicePort;
import com.fiap.postech.pedido_service.utils.ConstantUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PedidoReceiverConsumer {

    private static final String TOPICO = "pedidos-criados";
    private static final String GRUPO = "pedido-group";

    @Autowired
    private PedidoRepositoryPort pedidoRepositoryPort;
    @Autowired
    private PedidoItemRepositoryPort pedidoItemRepositoryPort;

    @Autowired
    private PedidoServicePort pedidoServicePort;

    @Autowired
    private PedidoEstoqueClient estoqueClient;
    @Autowired
    private PedidoPagamentoClient pagamentoClient;


    @KafkaListener(topics = TOPICO, groupId = GRUPO, containerFactory = "kafkaListenerContainerFactory")
    public void processarPedido(ConsumerRecord<String, String> record, Acknowledgment ack) throws JsonProcessingException {

        try {


            PedidoKafkaDTO dto = new ObjectMapper().readValue(record.value(), PedidoKafkaDTO.class);

            // 1. Buscar pedido e itens do pedido no banco
            Pedido pedido = pedidoRepositoryPort.buscarPedidoPorId(dto.getIdPedido());
            if (pedido == null) {
                log.error("Pedido não encontrado: {}", dto.getIdPedido());
                throw new PedidoNotFoundException(ConstantUtils.PEDIDO_NAO_ENCONTRADO);
            }

            List<PedidoItem> pedidoItem = pedidoItemRepositoryPort.buscarItensPedido(pedido.getIdPedido());
            pedido.setItens(pedidoItem);

            // 2. Validar e baixar estoque
            PedidoBaixaEstoqueRequest baixaRequest = mapParaRequestEstoque(dto.getItens());
            PedidoBaixaEstoqueResponse resposta = estoqueClient.baixaEstoque(baixaRequest);

            if (!resposta.isSucesso()) {
                pedido.setStatusPedido(PedidoStatus.FECHADO_SEM_ESTOQUE);
                pedidoServicePort.atualizaStatusPedido(dto.getIdPedido(), PedidoStatus.FECHADO_SEM_ESTOQUE);
                log.info("Estoque insuficiente para pedido {}", pedido.getIdPedido());
                ack.acknowledge();
                return;
            }


            // 3. Processar pagamento
            PedidoPagamentoRequest requestPagamento = mapParaRequestPagamento(dto);
            PedidoPagamentoResponse pagamentoResponse = pagamentoClient.processarPagamento(requestPagamento);

            if (!pagamentoResponse.isAprovado()) {
                // Restabelece estoque se pagamento foi recusado
                PedidoBaixaEstoqueRequest restauraRequest = mapParaRequestEstoque(dto.getItens());
                estoqueClient.restaurarEstoque(restauraRequest);
                pedido.setStatusPedido(PedidoStatus.FECHADO_SEM_CREDITO);
                pedidoServicePort.atualizaStatusPedido(dto.getIdPedido(), PedidoStatus.FECHADO_SEM_CREDITO);
                log.info("Pagamento recusado para pedido {}", pedido.getIdPedido());
                ack.acknowledge();
                return;
            }

            // 4. Tudo OK
            pedido.setStatusPedido(PedidoStatus.FECHADO_COM_SUCESSO);
            pedidoServicePort.atualizaStatusPedido(dto.getIdPedido(), PedidoStatus.FECHADO_COM_SUCESSO);
            log.info("Pedido {} finalizado com sucesso", pedido.getIdPedido());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Erro inesperado ao processar pedido: {}", e.getMessage(), e);
            ack.acknowledge();
        }
    }

    private PedidoBaixaEstoqueRequest mapParaRequestEstoque(List<PedidoItemKafkaDTO> itensKafka) {
        List<PedidoItemEstoqueBaixaDTO> itensEstoque = new ArrayList<>();
        for (PedidoItemKafkaDTO item : itensKafka) {
            PedidoItemEstoqueBaixaDTO estoqueItem = new PedidoItemEstoqueBaixaDTO();
            estoqueItem.setIdProduto(item.getIdProduto());
            estoqueItem.setQuantidade(item.getQuantidadeItem());
            itensEstoque.add(estoqueItem);
        }
        PedidoBaixaEstoqueRequest request = new PedidoBaixaEstoqueRequest();
        request.setItens(itensEstoque);
        return request;
    }

    private PedidoPagamentoRequest mapParaRequestPagamento(PedidoKafkaDTO pedidoKafkaDTO) {
        PedidoPagamentoRequest request = new PedidoPagamentoRequest();
        request.setIdPedido(pedidoKafkaDTO.getIdPedido());
        request.setIdCliente(pedidoKafkaDTO.getIdCliente());
        request.setValorTotalPedido(pedidoKafkaDTO.getValorTotalPedido());
        return request;
    }
}
