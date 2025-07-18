package com.fiap.postech.pedido_receiver.gateway.database.repository;

import com.fiap.postech.pedido_receiver.gateway.database.entity.PedidoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepositoryJPA extends JpaRepository<PedidoEntity, Integer> {
}

