package com.puc.aluguel.service;

import com.puc.aluguel.dto.AutomovelRequest;
import com.puc.aluguel.dto.AutomovelResponse;
import com.puc.aluguel.model.Agente;
import com.puc.aluguel.model.Automovel;
import com.puc.aluguel.model.Banco;
import com.puc.aluguel.model.TipoProprietario;
import com.puc.aluguel.repository.AgenteRepository;
import com.puc.aluguel.repository.AutomovelRepository;
import com.puc.aluguel.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AutomovelService {

    private final AutomovelRepository automovelRepository;
    private final AgenteRepository agenteRepository;
    private final ClienteRepository clienteRepository;

    public AutomovelService(AutomovelRepository automovelRepository, AgenteRepository agenteRepository,
                            ClienteRepository clienteRepository) {
        this.automovelRepository = automovelRepository;
        this.agenteRepository = agenteRepository;
        this.clienteRepository = clienteRepository;
    }

    public List<AutomovelResponse> listar() {
        return automovelRepository.findAll().stream().map(AutomovelResponse::de).toList();
    }

    /**
     * Registra um automóvel. O proprietário pode ser um cliente (informando o id do cliente)
     * ou o próprio agente logado (empresa ou banco).
     */
    public AutomovelResponse registrar(String loginAgente, AutomovelRequest req) {
        Agente agente = agenteRepository.findByUsuarioLogin(loginAgente)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Agente não encontrado"));
        if (automovelRepository.existsByPlaca(req.placa())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Placa já cadastrada");
        }
        if (automovelRepository.existsByMatricula(req.matricula())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Matrícula já cadastrada");
        }

        Long donoId;
        if (req.tipoProprietario() == TipoProprietario.CLIENTE) {
            if (req.proprietarioId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o cliente proprietário");
            }
            if (!clienteRepository.existsById(req.proprietarioId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente proprietário não encontrado");
            }
            donoId = req.proprietarioId();
        } else {
            TipoProprietario tipoDoAgente = agente instanceof Banco ? TipoProprietario.BANCO : TipoProprietario.EMPRESA;
            if (req.tipoProprietario() != tipoDoAgente) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Seu perfil só registra carros como propriedade de CLIENTE ou " + tipoDoAgente);
            }
            donoId = agente.getId();
        }

        Automovel a = new Automovel();
        a.setMatricula(req.matricula());
        a.setAno(req.ano());
        a.setMarca(req.marca());
        a.setModelo(req.modelo());
        a.setPlaca(req.placa());
        a.setTipoProprietario(req.tipoProprietario());
        a.setProprietarioId(donoId);
        return AutomovelResponse.de(automovelRepository.save(a));
    }
}
