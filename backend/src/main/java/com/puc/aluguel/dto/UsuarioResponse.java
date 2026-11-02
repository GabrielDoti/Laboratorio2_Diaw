package com.puc.aluguel.dto;

import com.puc.aluguel.model.Perfil;

public record UsuarioResponse(String login, Perfil perfil, String nome) {
}
