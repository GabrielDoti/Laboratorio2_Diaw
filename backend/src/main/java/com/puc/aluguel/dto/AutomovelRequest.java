package com.puc.aluguel.dto;

import com.puc.aluguel.model.TipoProprietario;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AutomovelRequest(
        @NotBlank String matricula,
        @Min(1950) int ano,
        @NotBlank String marca,
        @NotBlank String modelo,
        @NotBlank String placa,
        @NotNull TipoProprietario tipoProprietario,
        Long proprietarioId) {
}
