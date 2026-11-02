package com.puc.aluguel.service;

import com.puc.aluguel.dto.*;
import com.puc.aluguel.model.*;
import com.puc.aluguel.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final PasswordEncoder passwordEncoder;

    public ClienteService(ClienteRepository clienteRepository, UsuarioRepository usuarioRepository,
                          PedidoRepository pedidoRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** CREATE - cadastro de cliente (usuário + dados pessoais). */
    @Transactional
    public ClienteResponse cadastrar(RegistroRequest req) {
        if (usuarioRepository.existsByLogin(req.login())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login já está em uso");
        }
        if (clienteRepository.existsByCpf(req.cpf())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já cadastrado");
        }
        Usuario usuario = usuarioRepository.save(
                new Usuario(req.login(), passwordEncoder.encode(req.senha()), Perfil.CLIENTE));

        Cliente cliente = new Cliente();
        cliente.setUsuario(usuario);
        cliente.setRg(req.rg());
        cliente.setCpf(req.cpf());
        cliente.setNome(req.nome());
        cliente.setEndereco(req.endereco());
        cliente.setProfissao(req.profissao());
        aplicarRendimentos(cliente, req.rendimentos());
        return ClienteResponse.de(clienteRepository.save(cliente));
    }

    /** READ - dados do cliente logado. */
    @Transactional(readOnly = true)
    public ClienteResponse buscarPorLogin(String login) {
        return ClienteResponse.de(obterCliente(login));
    }

    /** READ - lista de clientes (uso dos agentes). */
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clienteRepository.findAll().stream().map(ClienteResponse::de).toList();
    }

    /** UPDATE - atualiza dados do cliente logado. */
    @Transactional
    public ClienteResponse atualizar(String login, ClienteUpdateRequest req) {
        Cliente cliente = obterCliente(login);
        cliente.setRg(req.rg());
        cliente.setNome(req.nome());
        cliente.setEndereco(req.endereco());
        cliente.setProfissao(req.profissao());
        cliente.getRendimentos().clear();
        aplicarRendimentos(cliente, req.rendimentos());
        return ClienteResponse.de(clienteRepository.save(cliente));
    }

    /** DELETE - remove o cliente logado, seus pedidos e o usuário. */
    @Transactional
    public void excluir(String login) {
        Cliente cliente = obterCliente(login);
        Usuario usuario = cliente.getUsuario();
        pedidoRepository.deleteByClienteId(cliente.getId());
        clienteRepository.delete(cliente);
        clienteRepository.flush();
        usuarioRepository.delete(usuario);
    }

    public Cliente obterCliente(String login) {
        return clienteRepository.findByUsuarioLogin(login)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
    }

    private void aplicarRendimentos(Cliente cliente, List<RendimentoDto> rendimentos) {
        if (rendimentos == null) {
            return;
        }
        if (rendimentos.size() > Cliente.MAX_RENDIMENTOS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Máximo de " + Cliente.MAX_RENDIMENTOS + " rendimentos");
        }
        for (RendimentoDto r : rendimentos) {
            cliente.getRendimentos().add(new Rendimento(r.entidadeEmpregadora(), r.valor(), cliente));
        }
    }
}
