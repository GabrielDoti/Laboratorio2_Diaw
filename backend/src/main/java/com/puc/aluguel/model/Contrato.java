package com.puc.aluguel.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/** Contrato de aluguel, gerado quando um agente executa um pedido aprovado. */
@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataExecucao = LocalDate.now();

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", unique = true)
    private Pedido pedido;

    /** Agente (empresa ou banco) que executou o contrato. */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "agente_id")
    private Agente agente;

    /** Contrato de crédito associado, concedido por um banco (0..1). */
    @OneToOne(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    private ContratoCredito credito;

    public Contrato() {
    }

    public Long getId() { return id; }
    public LocalDate getDataExecucao() { return dataExecucao; }
    public void setDataExecucao(LocalDate dataExecucao) { this.dataExecucao = dataExecucao; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
    public Agente getAgente() { return agente; }
    public void setAgente(Agente agente) { this.agente = agente; }
    public ContratoCredito getCredito() { return credito; }
    public void setCredito(ContratoCredito credito) { this.credito = credito; }
}
