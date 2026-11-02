package com.puc.aluguel.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BANCO")
public class Banco extends Agente {

    public Banco() {
    }

    public Banco(String nome, String cnpj, Usuario usuario) {
        super(nome, cnpj, usuario);
    }
}
