# Versões dos modelos

- `v1/`: modelagem inicial (antes de implementar contrato e crédito no código).
- Raiz de `docs/`: versão atual (v2), alinhada com o código final.

## O que mudou da v1 para a v2
- Removida a interface `Proprietario`: no código a propriedade do automóvel é `tipoProprietario` + `proprietarioId`.
- `Pedido` é avaliado por **0..1** agente (era `0..*`), porque o código guarda um avaliador.
- Adicionadas as classes `Contrato` e `ContratoCredito` no código (já estavam no diagrama), com atributo `parcelas`.
- Agente passa a poder **modificar pedidos** (HU10) e **consultar clientes e rendimentos** (UC12).
- Enum `TipoProprietario` incluído no diagrama.
