package com.puc.aluguel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ClienteUpdateRequest(
        @NotBlank String rg,
        @NotBlank String nome,
        @NotBlank String endereco,
        @NotBlank String profissao,
        @Valid @Size(max = 3) List<RendimentoDto> rendimentos) {
}
