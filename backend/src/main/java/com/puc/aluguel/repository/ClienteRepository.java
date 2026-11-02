package com.puc.aluguel.repository;

import com.puc.aluguel.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByUsuarioLogin(String login);
    boolean existsByCpf(String cpf);
}
