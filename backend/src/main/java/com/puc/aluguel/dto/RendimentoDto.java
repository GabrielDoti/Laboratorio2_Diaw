package com.puc.aluguel.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RendimentoDto(
        @NotBlank String entidadeEmpregadora,
        @NotNull @DecimalMin("0.0") BigDecimal valor) {
}
