package com.puc.aluguel.controller;

import com.puc.aluguel.dto.ClienteResponse;
import com.puc.aluguel.dto.RegistroRequest;
import com.puc.aluguel.dto.UsuarioResponse;
import com.puc.aluguel.model.Perfil;
import com.puc.aluguel.repository.AgenteRepository;
import com.puc.aluguel.repository.ClienteRepository;
import com.puc.aluguel.security.SecurityUtils;
import com.puc.aluguel.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final ClienteService clienteService;
    private final ClienteRepository clienteRepository;
    private final AgenteRepository agenteRepository;

    public AuthController(ClienteService clienteService, ClienteRepository clienteRepository,
                          AgenteRepository agenteRepository) {
        this.clienteService = clienteService;
        this.clienteRepository = clienteRepository;
        this.agenteRepository = agenteRepository;
    }

    /** Cadastro público de cliente. */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse registrar(@Valid @RequestBody RegistroRequest req) {
        return clienteService.cadastrar(req);
    }

    /** Valida as credenciais (Basic) e devolve quem está logado. */
    @GetMapping("/me")
    public UsuarioResponse me(Authentication auth) {
        Perfil perfil = SecurityUtils.perfil(auth);
        String login = auth.getName();
        String nome = perfil == Perfil.CLIENTE
                ? clienteRepository.findByUsuarioLogin(login).map(c -> c.getNome()).orElse(login)
                : agenteRepository.findByUsuarioLogin(login).map(a -> a.getNome()).orElse(login);
        return new UsuarioResponse(login, perfil, nome);
    }
}
