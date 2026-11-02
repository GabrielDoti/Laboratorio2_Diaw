package com.puc.aluguel.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/** Contrato de crédito concedido por um banco para financiar um contrato de aluguel. */
@Entity
@Table(name = "contratos_credito")
public class ContratoCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;

    /** Taxa de juros em % ao mês. */
    @Column(nullable = false, precision = 7, scale = 2)
    private BigDecimal taxaJuros;

    @Column(nullable = false)
    private int parcelas;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", unique = true)
    private Contrato contrato;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "banco_id")
    private Banco banco;

    public ContratoCredito() {
    }

    public Long getId() { return id; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public BigDecimal getTaxaJuros() { return taxaJuros; }
    public void setTaxaJuros(BigDecimal taxaJuros) { this.taxaJuros = taxaJuros; }
    public int getParcelas() { return parcelas; }
    public void setParcelas(int parcelas) { this.parcelas = parcelas; }
    public Contrato getContrato() { return contrato; }
    public void setContrato(Contrato contrato) { this.contrato = contrato; }
    public Banco getBanco() { return banco; }
    public void setBanco(Banco banco) { this.banco = banco; }
}
