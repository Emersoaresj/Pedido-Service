package com.fiap.postech.pedido_receiver.gateway.client;

import com.fiap.postech.pedido_receiver.api.dto.client.pagamento.PedidoPagamentoRequest;
import com.fiap.postech.pedido_receiver.api.dto.client.pagamento.PedidoPagamentoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "pedido-pagamento-service", url = "${pagamento.service.url}")
public interface PedidoPagamentoClient {

    @PostMapping("/api/pagamentos/processar")
    PedidoPagamentoResponse processarPagamento(PedidoPagamentoRequest request);
}

