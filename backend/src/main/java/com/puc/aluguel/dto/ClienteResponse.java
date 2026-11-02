package com.puc.aluguel.dto;

import com.puc.aluguel.model.Cliente;

import java.util.List;

public record ClienteResponse(
        Long id,
        String login,
        String rg,
        String cpf,
        String nome,
        String endereco,
        String profissao,
        List<RendimentoDto> rendimentos) {

    public static ClienteResponse de(Cliente c) {
        List<RendimentoDto> rends = c.getRendimentos().stream()
                .map(r -> new RendimentoDto(r.getEntidadeEmpregadora(), r.getValor()))
                .toList();
        return new ClienteResponse(c.getId(), c.getUsuario().getLogin(), c.getRg(), c.getCpf(),
                c.getNome(), c.getEndereco(), c.getProfissao(), rends);
    }
}
