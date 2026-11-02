package com.puc.aluguel.config;

import com.puc.aluguel.model.*;
import com.puc.aluguel.repository.AgenteRepository;
import com.puc.aluguel.repository.AutomovelRepository;
import com.puc.aluguel.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Cria dados de demonstração: um banco, uma empresa e alguns carros.
 * Logins de teste: banco / banco123 e locadora / locadora123.
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(@Value("${app.seed.enabled}") boolean enabled,
                           UsuarioRepository usuarios, AgenteRepository agentes,
                           AutomovelRepository automoveis, PasswordEncoder encoder) {
        return args -> {
            if (!enabled || usuarios.existsByLogin("banco")) {
                return;
            }
            Usuario uBanco = usuarios.save(new Usuario("banco", encoder.encode("banco123"), Perfil.BANCO));
            Usuario uEmpresa = usuarios.save(new Usuario("locadora", encoder.encode("locadora123"), Perfil.EMPRESA));
            Agente banco = agentes.save(new Banco("Banco Demo S.A.", "00.000.000/0001-00", uBanco));
            Agente empresa = agentes.save(new Empresa("Locadora Demo Ltda", "11.111.111/0001-11", uEmpresa));

            criarCarro(automoveis, "MAT-001", 2023, "Fiat", "Argo", "ABC1D23", TipoProprietario.EMPRESA, empresa.getId());
            criarCarro(automoveis, "MAT-002", 2022, "Volkswagen", "Polo", "DEF4G56", TipoProprietario.EMPRESA, empresa.getId());
            criarCarro(automoveis, "MAT-003", 2024, "Chevrolet", "Onix", "HIJ7K89", TipoProprietario.BANCO, banco.getId());
        };
    }

    private void criarCarro(AutomovelRepository repo, String matricula, int ano, String marca,
                            String modelo, String placa, TipoProprietario tipo, Long donoId) {
        Automovel a = new Automovel();
        a.setMatricula(matricula);
        a.setAno(ano);
        a.setMarca(marca);
        a.setModelo(modelo);
        a.setPlaca(placa);
        a.setTipoProprietario(tipo);
        a.setProprietarioId(donoId);
        repo.save(a);
    }
}
