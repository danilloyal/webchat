# Especificação de Sistema: Web Chat

Este documento estabelece a arquitetura base, requisitos, limites e volumetria para uma plataforma de comunicação em tempo real distribuída, suportando texto, voz e presença.

---

### Requisitos do Sistema

#### Requisitos Funcionais (RF)
* **Live Status:** Atualização e exibição do estado de presença em tempo real dos utilizadores.
* **Administração de Canais:** Criação, edição, exclusão e gestão de acessos a canais pelo dono do espaço.
* **Conversação por Texto:** Envio e receção de mensagens instantâneas dentro de canais específicos.
* **Conversação por Voz:** Canais de áudio de alta performance e baixa latência.
* **Histórico de Mensagens:** Armazenamento persistente e recuperação paginada das mensagens do canal.

#### Requisitos Não Funcionais (RNF)
* **Disponibilidade:** Alvo de 99,9% de uptime na métrica mensal.
* **Consistência:** Modelo de consistência eventual para o armazenamento de mensagens.
* **Estratégia de Cache (Redis):**
  * **Sessões:** Armazenamento centralizado de tokens JWT e sessões ativas.
  * **Mensagens Quentes:** Cache das últimas 50 mensagens de canais com alto fluxo de interações.
  * **Status:** Estado atualizado de presença mapeado em memória RAM.
* **Observabilidade:** Monitorização em tempo real da taxa de acerto do cache (Cache Hit/Miss Ratio) e latência P99 por endpoint.

---

### Limitações Rígidas (Guardrails)

* **Mensagens:** Tamanho máximo restrito a 2.000 caracteres por envio.
* **Canais de Voz:** Limite estrito de 50 utilizadores ativos em simultâneo por sala.
* **Canais de Texto:** Capacidade máxima de 1.000 utilizadores ativos no canal ao mesmo tempo.
* **Rate Limiter:** Bloqueio aplicado na camada de WebSocket limitado a 5 mensagens por segundo por conexão.
* **Segregação de Dados:** Separação física e arquitetural entre a base de dados de mensagens e a de utilizadores/canais.

---

### Cenário de Carga e Volumetria

* **Utilizadores Ativos:** 5.000 utilizadores diários (DAU), distribuídos de forma não uniforme entre os canais.
* **Média de Escrita:** 15 mensagens diárias por utilizador.
* **Morfologia da Mensagem:** ~40 caracteres (codificação UTF-8) + 40 Bytes de metadados obrigatórios do sistema.

#### Projeção Matemática de Armazenamento
* **Fórmula de Cálculo:** 
  $$\text{DAU} \times \text{Msg/Dia} \times [(\text{Caracteres} \times \text{Fator UTF-8}) + \text{Metadados}]$$
* **Payload Médio por Mensagem:** $(40 \times 1,2 \text{ Bytes}) + 40 \text{ Bytes} = 88 \text{ Bytes}$
* **Armazenamento Diário:** $5.000 \times 15 \times 88 \text{ Bytes} \approx \mathbf{6,45 \text{ MB/dia}}$
* **Acumulado Anual (Texto + Metadados):** $6,45 \text{ MB} \times 365 \text{ dias} \approx \mathbf{2,35 \text{ GB/ano}}$

---

### Catálogo de Endpoints

#### Gestão de Utilizadores e Autenticação
* `POST /api/v1/users` — Criar utilizador
* `GET /api/v1/users/{user_id}` — Consultar perfil público do utilizador
* `POST /api/v1/auth/login` — Autenticar utilizador e emitir Token JWT

#### Gestão de Canais e Membros
* `POST /api/v1/channels` — Criar canal *(Atribui permissões de dono ao criador)*
* `PUT /api/v1/channels/{channel_id}` — Editar propriedades do canal *(Apenas Dono)*
* `DELETE /api/v1/channels/{channel_id}` — Excluir canal *(Apenas Dono)*
* `POST /api/v1/channels/{channel_id}/join` — Ingressar num canal
* `POST /api/v1/channels/{channel_id}/leave` — Sair de um canal
* `GET /api/v1/users/me/channels` — Consultar lista de canais em que o utilizador ingressou
* `GET /api/v1/channels/{channel_id}/active-users` — Consultar utilizadores ativos e online no canal *(Leitura via Redis)*

#### Histórico e Tempo Real
* `GET /api/v1/channels/{channel_id}/messages?limit=50&before={message_id}` — Paginação reversa do histórico de mensagens *(Primeiro lê o cache, faz fallback para NoSQL em caso de scroll antigo)*
* `WebSocket (wss://://app.com)` — Ligação única persistente para publicação de mensagens em tempo real e batimento de coração (Heartbeat) de presença.

#### Sinalização de Chat de Voz
* `POST /api/v1/channels/{channel_id}/voice/start` — Iniciar sessão de voz no canal *(Aloca infraestrutura no servidor de média)*
* `POST /api/v1/channels/{channel_id}/voice/join` — Ingressar no chat de voz *(Retorna credenciais WebRTC e IP do nó SFU)*
* `POST /api/v1/channels/{channel_id}/voice/leave` — Sair/Encerrar participação no chat de voz do canal