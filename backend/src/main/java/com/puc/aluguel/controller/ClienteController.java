package com.puc.aluguel.controller;

import com.puc.aluguel.dto.ClienteResponse;
import com.puc.aluguel.dto.ClienteUpdateRequest;
import com.puc.aluguel.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping("/me")
    public ClienteResponse buscar(Authentication auth) {
        return clienteService.buscarPorLogin(auth.getName());
    }

    @PutMapping("/me")
    public ClienteResponse atualizar(Authentication auth, @Valid @RequestBody ClienteUpdateRequest req) {
        return clienteService.atualizar(auth.getName(), req);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(Authentication auth) {
        clienteService.excluir(auth.getName());
    }

    @GetMapping
    public List<ClienteResponse> listar() {
        return clienteService.listar();
    }
}
