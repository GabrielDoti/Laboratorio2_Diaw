package com.puc.aluguel.service;

import com.puc.aluguel.dto.AvaliacaoRequest;
import com.puc.aluguel.dto.CreditoRequest;
import com.puc.aluguel.dto.PedidoRequest;
import com.puc.aluguel.dto.PedidoResponse;
import com.puc.aluguel.model.*;
import com.puc.aluguel.repository.AgenteRepository;
import com.puc.aluguel.repository.AutomovelRepository;
import com.puc.aluguel.repository.PedidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final AutomovelRepository automovelRepository;
    private final AgenteRepository agenteRepository;
    private final ClienteService clienteService;

    public PedidoService(PedidoRepository pedidoRepository, AutomovelRepository automovelRepository,
                         AgenteRepository agenteRepository, ClienteService clienteService) {
        this.pedidoRepository = pedidoRepository;
        this.automovelRepository = automovelRepository;
        this.agenteRepository = agenteRepository;
        this.clienteService = clienteService;
    }

    @Transactional
    public PedidoResponse criar(String login, PedidoRequest req) {
        validarPeriodo(req);
        Cliente cliente = clienteService.obterCliente(login);
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setAutomovel(obterAutomovel(req.automovelId()));
        pedido.setDataInicio(req.dataInicio());
        pedido.setDataFim(req.dataFim());
        pedido.setStatus(StatusPedido.EM_ANALISE);
        return PedidoResponse.de(pedidoRepository.save(pedido));
    }

    /** Cliente vê só os seus pedidos; agentes veem todos. */
    @Transactional(readOnly = true)
    public List<PedidoResponse> listar(String login, Perfil perfil) {
        List<Pedido> pedidos;
        if (perfil == Perfil.CLIENTE) {
            Cliente cliente = clienteService.obterCliente(login);
            pedidos = pedidoRepository.findByClienteIdOrderByDataCriacaoDesc(cliente.getId());
        } else {
            pedidos = pedidoRepository.findAllByOrderByDataCriacaoDesc();
        }
        return pedidos.stream().map(PedidoResponse::de).toList();
    }

    @Transactional
    public PedidoResponse modificar(String login, Long id, PedidoRequest req) {
        validarPeriodo(req);
        Pedido pedido = obterPedidoDoCliente(login, id);
        if (pedido.getStatus() != StatusPedido.EM_ANALISE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Só é possível modificar pedidos em análise");
        }
        pedido.setAutomovel(obterAutomovel(req.automovelId()));
        pedido.setDataInicio(req.dataInicio());
        pedido.setDataFim(req.dataFim());
        return PedidoResponse.de(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponse cancelar(String login, Long id) {
        Pedido pedido = obterPedidoDoCliente(login, id);
        if (pedido.getStatus() == StatusPedido.CONTRATO_EXECUTADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Pedido com contrato executado não pode ser cancelado");
        }
        pedido.setStatus(StatusPedido.CANCELADO);
        return PedidoResponse.de(pedidoRepository.save(pedido));
    }

    /** Agente avalia o pedido: aprova, reprova ou executa o contrato. */
    @Transactional
    public PedidoResponse avaliar(String loginAgente, Long id, AvaliacaoRequest req) {
        Agente agente = agenteRepository.findByUsuarioLogin(loginAgente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Agente não encontrado"));
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));

        StatusPedido novo = req.status();
        StatusPedido atual = pedido.getStatus();
        boolean valido = switch (novo) {
            case APROVADO, REPROVADO -> atual == StatusPedido.EM_ANALISE;
            case CONTRATO_EXECUTADO -> atual == StatusPedido.APROVADO;
            default -> false;
        };
        if (!valido) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Transição de " + atual + " para " + novo + " não é permitida");
        }
        pedido.setStatus(novo);
        if (novo == StatusPedido.CONTRATO_EXECUTADO) {
            Contrato contrato = new Contrato();
            contrato.setPedido(pedido);
            contrato.setAgente(agente);
            pedido.setContrato(contrato);
        } else {
            pedido.setAvaliador(agente);
        }
        return PedidoResponse.de(pedidoRepository.save(pedido));
    }

    /**
     * Agente (empresa ou banco) modifica um pedido ainda não contratado.
     * Se o pedido já estava aprovado, volta para análise (a avaliação financeira precisa ser refeita).
     */
    @Transactional
    public PedidoResponse modificarPorAgente(String loginAgente, Long id, PedidoRequest req) {
        validarPeriodo(req);
        agenteRepository.findByUsuarioLogin(loginAgente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Agente não encontrado"));
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
        StatusPedido atual = pedido.getStatus();
        if (atual != StatusPedido.EM_ANALISE && atual != StatusPedido.APROVADO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Só é possível modificar pedidos em análise ou aprovados");
        }
        pedido.setAutomovel(obterAutomovel(req.automovelId()));
        pedido.setDataInicio(req.dataInicio());
        pedido.setDataFim(req.dataFim());
        if (atual == StatusPedido.APROVADO) {
            pedido.setStatus(StatusPedido.EM_ANALISE);
            pedido.setAvaliador(null);
        }
        return PedidoResponse.de(pedidoRepository.save(pedido));
    }

    /** Banco concede um contrato de crédito para um aluguel com contrato executado. */
    @Transactional
    public PedidoResponse concederCredito(String loginBanco, Long id, CreditoRequest req) {
        Agente agente = agenteRepository.findByUsuarioLogin(loginBanco)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Agente não encontrado"));
        if (!(agente instanceof Banco banco)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só bancos podem conceder crédito");
        }
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
        Contrato contrato = pedido.getContrato();
        if (pedido.getStatus() != StatusPedido.CONTRATO_EXECUTADO || contrato == null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O crédito só pode ser concedido para um aluguel com contrato executado");
        }
        if (contrato.getCredito() != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este aluguel já tem crédito concedido");
        }
        ContratoCredito credito = new ContratoCredito();
        credito.setValor(req.valor());
        credito.setTaxaJuros(req.taxaJuros());
        credito.setParcelas(req.parcelas());
        credito.setBanco(banco);
        credito.setContrato(contrato);
        contrato.setCredito(credito);
        return PedidoResponse.de(pedidoRepository.save(pedido));
    }

    private Pedido obterPedidoDoCliente(String login, Long id) {
        Cliente cliente = clienteService.obterCliente(login);
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido não encontrado"));
        if (!pedido.getCliente().getId().equals(cliente.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Esse pedido não é seu");
        }
        return pedido;
    }

    private Automovel obterAutomovel(Long id) {
        return automovelRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Automóvel não encontrado"));
    }

    private void validarPeriodo(PedidoRequest req) {
        if (!req.dataFim().isAfter(req.dataInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A data final deve ser depois da data inicial");
        }
    }
}
