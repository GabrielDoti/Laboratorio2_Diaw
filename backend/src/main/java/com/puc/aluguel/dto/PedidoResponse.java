package com.puc.aluguel.dto;

import com.puc.aluguel.model.Contrato;
import com.puc.aluguel.model.Pedido;
import com.puc.aluguel.model.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponse(
        Long id,
        Long clienteId,
        String clienteNome,
        Long automovelId,
        String automovel,
        LocalDate dataInicio,
        LocalDate dataFim,
        LocalDateTime dataCriacao,
        StatusPedido status,
        String avaliador,
        BigDecimal rendaTotal,
        List<RendimentoDto> rendimentos,
        LocalDate dataContrato,
        CreditoResponse credito) {

    public static PedidoResponse de(Pedido p) {
        String desc = p.getAutomovel().getMarca() + " " + p.getAutomovel().getModelo()
                + " (" + p.getAutomovel().getPlaca() + ")";
        String aval = p.getAvaliador() == null ? null : p.getAvaliador().getNome();

        List<RendimentoDto> rends = p.getCliente().getRendimentos().stream()
                .map(r -> new RendimentoDto(r.getEntidadeEmpregadora(), r.getValor()))
                .toList();
        BigDecimal renda = rends.stream()
                .map(RendimentoDto::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Contrato c = p.getContrato();
        LocalDate dataContrato = c == null ? null : c.getDataExecucao();
        CreditoResponse credito = (c == null || c.getCredito() == null)
                ? null : CreditoResponse.de(c.getCredito());

        return new PedidoResponse(p.getId(), p.getCliente().getId(), p.getCliente().getNome(),
                p.getAutomovel().getId(), desc, p.getDataInicio(), p.getDataFim(),
                p.getDataCriacao(), p.getStatus(), aval, renda, rends, dataContrato, credito);
    }
}
