package com.puc.aluguel.controller;

import com.puc.aluguel.dto.AvaliacaoRequest;
import com.puc.aluguel.dto.CreditoRequest;
import com.puc.aluguel.dto.PedidoRequest;
import com.puc.aluguel.dto.PedidoResponse;
import com.puc.aluguel.model.Perfil;
import com.puc.aluguel.security.SecurityUtils;
import com.puc.aluguel.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(Authentication auth, @Valid @RequestBody PedidoRequest req) {
        return pedidoService.criar(auth.getName(), req);
    }

    @GetMapping
    public List<PedidoResponse> listar(Authentication auth) {
        return pedidoService.listar(auth.getName(), SecurityUtils.perfil(auth));
    }

    @PutMapping("/{id}")
    public PedidoResponse modificar(Authentication auth, @PathVariable Long id,
                                    @Valid @RequestBody PedidoRequest req) {
        if (SecurityUtils.perfil(auth) == Perfil.CLIENTE) {
            return pedidoService.modificar(auth.getName(), id, req);
        }
        return pedidoService.modificarPorAgente(auth.getName(), id, req);
    }

    /** Cancelamento: marca o pedido como CANCELADO (não apaga o registro). */
    @DeleteMapping("/{id}")
    public PedidoResponse cancelar(Authentication auth, @PathVariable Long id) {
        return pedidoService.cancelar(auth.getName(), id);
    }

    @PatchMapping("/{id}/avaliacao")
    public PedidoResponse avaliar(Authentication auth, @PathVariable Long id,
                                  @Valid @RequestBody AvaliacaoRequest req) {
        return pedidoService.avaliar(auth.getName(), id, req);
    }

    /** Banco concede crédito para um aluguel com contrato executado. */
    @PostMapping("/{id}/credito")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse concederCredito(Authentication auth, @PathVariable Long id,
                                          @Valid @RequestBody CreditoRequest req) {
        return pedidoService.concederCredito(auth.getName(), id, req);
    }
}
