# Pedido Service

Serviço Java baseado em Spring Boot para recebimento e processamento de pedidos, utilizando PostgreSQL, Kafka para mensageria, FeignClient para comunicação entre APIs e arquitetura hexagonal (Ports & Adapters).

---

## 🏗️ Arquitetura

O projeto segue o padrão de arquitetura hexagonal, separando as regras de negócio (domínio) das implementações externas (gateways/adapters):

- **API**: Endpoints REST para operações relacionadas a pedidos (consumer do kafka).
- **Domain**: Modelos, exceções e portas (interfaces) do domínio.
- **Gateway**: Implementações de acesso a dados (JPA), clientes externos (Estoque e Pagamento) e Kafka.
- **Service**: Lógica de negócio central.
- **Utils**: Utilitários e constantes.

---

## 📁 Estrutura de Pastas
```
src/main/java/com/fiap/postech/pedido_service/
├── api/
│   ├── consumer/
│   │   └── PedidoReceiverConsumer.java
│   ├── dto/
│   │   ├── PedidoStatus.java
│   │   ├── ResponseDto.java
│   │   ├── client/
│   │   │   ├── estoque/
│   │   │   │   ├── PedidoBaixaEstoqueRequest.java
│   │   │   │   ├── PedidoBaixaEstoqueResponse.java
│   │   │   │   └── PedidoItemEstoqueBaixaDTO.java
│   │   │   └── pagamento/
│   │   │       ├── PedidoPagamentoRequest.java
│   │   │       └── PedidoPagamentoResponse.java
│   │   └── kafka/
│   │       ├── PedidoItemKafkaDTO.java
│   │       └── PedidoKafkaDTO.java
│   └── mapper/
│       ├── PedidoItemMapper.java
│       └── PedidoMapper.java
├── domain/
│   ├── exception/
│   │   ├── ErroInternoException.java
│   │   ├── GlobalHandlerException.java
│   │   └── PedidoNotFoundException.java
│   ├── model/
│   │   ├── Pedido.java
│   │   └── PedidoItem.java
├── gateway/
│   ├── client/
│   │   ├── PedidoEstoqueClient.java
│   │   └── PedidoPagamentoClient.java
│   ├── database/
│   │   ├── PedidoItemRepositoryImpl.java
│   │   ├── PedidoRepositoryImpl.java
│   │   ├── entity/
│   │   │   ├── PedidoEntity.java
│   │   │   └── PedidoItemEntity.java
│   │   └── repository/
│   │       ├── PedidoItemRepositoryJPA.java
│   │       └── PedidoRepositoryJPA.java
│   ├── kafka/
│   │   └── KafkaConfig.java
│   └── port/
│       ├── PedidoItemRepositoryPort.java
│       ├── PedidoRepositoryPort.java
│       └── PedidoServicePort.java
├── service/
│   └── PedidoServiceImpl.java
├── utils/
│   └── ConstantUtils.java
└── PedidoServiceApplication.java

```
---

## 🧩 Principais Classes

- **PedidoReceiverConsumer**: Consumer do Kafka para processamento de pedidos.
- **PedidoServiceImpl**: Implementação da lógica de negócio para o domínio de pedidos.
- **PedidoRepositoryPort**: Interface para a implementação de persistência dos pedidos.
- **PedidoItemRepositoryPort**: Interface para a implementação de persistência dos itens dos pedidos.
- **PedidoRepositoryImpl**: Implementação do repositório usando JPA para acesso ao banco de dados.
- **PedidoItemRepositoryImpl**: Implementação do repositório de itens usando JPA para acesso ao banco de dados.
- **PedidoEntity**: Entidade JPA para persistência dos dados de pedidos.
- **PedidoItemEntity**: Entidade JPA para persistência dos dados dos itens de pedidos.
- **Pedido**: Modelo de domínio que representa o pedido.
- **PedidoItem**: Modelo de domínio que representa o item do pedido.
- **PedidoEstoqueClient**: Cliente para comunicação com o serviço de estoque.
- **PedidoPagamentoClient**: Cliente para comunicação com o serviço de pagamento.
- **DTOs**: Objetos para transferência de dados entre as camadas da aplicação.
- **Exceções**: Tratamento centralizado de erros e validações da aplicação.

---

## ⚙️ Configuração

O arquivo `src/main/resources/application.yml` define:

- Conexão com PostgreSQL (ajustável por variáveis de ambiente).
- Configuração do Kafka (bootstrap servers, consumer).
- JPA configurado para atualização automática do schema e exibição de SQL.
- Flyway para migrações automáticas do banco de dados.
- Configuração dos serviços de estoque e pagamento, com as URLs dos serviços (ajustável por variável de ambiente).

---

## ▶️ Executando o Projeto

1. Configure o banco PostgreSQL e ajuste as variáveis de ambiente se necessário.
2. Configure o Kafka e ajuste as variáveis de ambiente se necessário.
3. Rode o projeto com: `mvn spring-boot:run`
4. A aplicação estará disponível (escutando o kafka).

---
