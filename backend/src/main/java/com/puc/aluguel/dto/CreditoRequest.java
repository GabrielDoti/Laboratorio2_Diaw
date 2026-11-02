package com.puc.aluguel.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreditoRequest(
        @NotNull @DecimalMin("0.01") BigDecimal valor,
        @NotNull @DecimalMin("0.0") BigDecimal taxaJuros,
        @Min(1) @Max(120) int parcelas) {
}
