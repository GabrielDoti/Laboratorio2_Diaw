# Histórias do Usuário — Sistema de Aluguel de Carros

## Cliente

| ID | História | Critérios de aceite |
|----|----------|---------------------|
| HU01 | Como cliente, quero me cadastrar no sistema pra poder usar o aluguel online. | Informo login, senha e dados pessoais (RG, CPF, nome, endereço, profissão); CPF e login não podem repetir. |
| HU02 | Como cliente, quero fazer login com usuário e senha pra acessar minhas informações. | Só usuários cadastrados entram; senha inválida mostra erro; rotas protegidas exigem autenticação. |
| HU03 | Como cliente, quero consultar, editar e excluir meus dados pra mantê-los atualizados. | Posso alterar endereço, profissão e rendimentos; máximo de 3 rendimentos com entidade empregadora e valor. |
| HU04 | Como cliente, quero criar um pedido de aluguel escolhendo um automóvel e o período pra alugar o carro. | Pedido nasce com status EM_ANALISE; datas válidas (fim depois do início). |
| HU05 | Como cliente, quero ver a lista e o status dos meus pedidos pra acompanhar a análise. | Vejo só os meus pedidos, com status atualizado. |
| HU06 | Como cliente, quero modificar um pedido pra corrigir datas ou o automóvel. | Só pedidos EM_ANALISE podem ser modificados. |
| HU07 | Como cliente, quero cancelar um pedido que não quero mais. | Pedido vai pra CANCELADO; não aparece mais como ativo. |

## Agente (empresa ou banco)

| ID | História | Critérios de aceite |
|----|----------|---------------------|
| HU08 | Como agente, quero fazer login pra acessar os pedidos que preciso avaliar. | Login com perfil EMPRESA ou BANCO. |
| HU09 | Como agente, quero avaliar financeiramente um pedido pra decidir se ele segue. | Vejo a renda total e os rendimentos do cliente na própria lista de pedidos; posso marcar APROVADO ou REPROVADO. |
| HU10 | Como agente, quero modificar um pedido pra ajustar condições. | Só pedidos EM_ANALISE ou APROVADO; se estava APROVADO, volta pra EM_ANALISE e precisa ser reavaliado. |
| HU11 | Como agente, quero executar o contrato de um pedido aprovado pra concretizar o aluguel. | Só pedidos APROVADO viram CONTRATO_EXECUTADO e geram um Contrato com data de execução. |
| HU12 | Como banco, quero conceder um contrato de crédito associado a um aluguel pra financiar o cliente. | Só para aluguel com contrato executado e sem crédito; informo valor, juros (% a.m.) e parcelas; só perfil BANCO. |
| HU13 | Como agente, quero registrar automóveis (matrícula, ano, marca, modelo, placa) e seu proprietário. | Proprietário pode ser um cliente ou o próprio agente (empresa ou banco); placa e matrícula únicas. |
| HU14 | Como agente, quero consultar a lista de clientes e seus rendimentos pra apoiar a análise financeira. | Lista com nome, CPF, profissão e rendimentos. |

## Requisitos não funcionais

- Sistema web: Spring Boot 4.0.x (Java 25) no back-end, arquitetura MVC.
- Front-end em React + Vite.
- Banco de dados Supabase (PostgreSQL).
- Login obrigatório com usuário e senha (senha armazenada com hash).
- Build e dependências com Apache Maven.
