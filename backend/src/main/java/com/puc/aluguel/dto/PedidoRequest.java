package com.puc.aluguel.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PedidoRequest(
        @NotNull Long automovelId,
        @NotNull LocalDate dataInicio,
        @NotNull LocalDate dataFim) {
}
