package com.puc.aluguel.repository;

import com.puc.aluguel.model.Agente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AgenteRepository extends JpaRepository<Agente, Long> {
    Optional<Agente> findByUsuarioLogin(String login);
}
