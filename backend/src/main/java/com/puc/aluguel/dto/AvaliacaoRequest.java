package com.puc.aluguel.dto;

import com.puc.aluguel.model.StatusPedido;
import jakarta.validation.constraints.NotNull;

public record AvaliacaoRequest(@NotNull StatusPedido status) {
}
