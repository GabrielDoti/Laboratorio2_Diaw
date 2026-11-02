package com.puc.aluguel.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("EMPRESA")
public class Empresa extends Agente {

    public Empresa() {
    }

    public Empresa(String nome, String cnpj, Usuario usuario) {
        super(nome, cnpj, usuario);
    }
}
