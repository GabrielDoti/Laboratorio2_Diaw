package com.puc.aluguel.controller;

import com.puc.aluguel.dto.AutomovelRequest;
import com.puc.aluguel.dto.AutomovelResponse;
import com.puc.aluguel.service.AutomovelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/automoveis")
public class AutomovelController {

    private final AutomovelService automovelService;

    public AutomovelController(AutomovelService automovelService) {
        this.automovelService = automovelService;
    }

    @GetMapping
    public List<AutomovelResponse> listar() {
        return automovelService.listar();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AutomovelResponse registrar(Authentication auth, @Valid @RequestBody AutomovelRequest req) {
        return automovelService.registrar(auth.getName(), req);
    }
}
