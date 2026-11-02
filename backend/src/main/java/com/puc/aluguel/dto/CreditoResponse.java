package com.puc.aluguel.dto;

import com.puc.aluguel.model.ContratoCredito;

import java.math.BigDecimal;

public record CreditoResponse(
        Long id,
        BigDecimal valor,
        BigDecimal taxaJuros,
        int parcelas,
        String banco) {

    public static CreditoResponse de(ContratoCredito c) {
        return new CreditoResponse(c.getId(), c.getValor(), c.getTaxaJuros(), c.getParcelas(),
                c.getBanco().getNome());
    }
}
