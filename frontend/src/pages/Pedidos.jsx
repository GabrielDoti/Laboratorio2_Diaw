import { Fragment, useEffect, useState } from 'react'
import { api } from '../api'
import { useAuth } from '../AuthContext'

const ROTULO = {
  EM_ANALISE: 'Em análise',
  APROVADO: 'Aprovado',
  REPROVADO: 'Reprovado',
  CONTRATO_EXECUTADO: 'Contrato executado',
  CANCELADO: 'Cancelado',
}
const data = (d) => new Date(d + 'T00:00:00').toLocaleDateString('pt-BR')
const brl = (n) => Number(n).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })

function FormPedido({ carros, inicial, onSalvar, onCancelar }) {
  const [f, setF] = useState(inicial || { automovelId: '', dataInicio: '', dataFim: '' })
  const [erro, setErro] = useState('')
  async function enviar(e) {
    e.preventDefault()
    setErro('')
    if (f.dataFim <= f.dataInicio) return setErro('A data final deve ser depois da inicial')
    try {
      await onSalvar({ ...f, automovelId: Number(f.automovelId) })
    } catch (err) {
      setErro(err.message)
    }
  }
  return (
    <form className="linha quebra" onSubmit={enviar}>
      <select value={f.automovelId} onChange={(e) => setF({ ...f, automovelId: e.target.value })} required>
        <option value="">Escolha o carro...</option>
        {carros.map((c) => (
          <option key={c.id} value={c.id}>{c.marca} {c.modelo} {c.ano} ({c.placa})</option>
        ))}
      </select>
      <input type="date" value={f.dataInicio} onChange={(e) => setF({ ...f, dataInicio: e.target.value })} required />
      <input type="date" value={f.dataFim} onChange={(e) => setF({ ...f, dataFim: e.target.value })} required />
      <button>Salvar</button>
      {onCancelar && <button type="button" className="secundario" onClick={onCancelar}>Fechar</button>}
      {erro && <p className="erro">{erro}</p>}
    </form>
  )
}

function FormCredito({ onSalvar, onCancelar }) {
  const [f, setF] = useState({ valor: '', taxaJuros: '', parcelas: '' })
  const [erro, setErro] = useState('')
  async function enviar(e) {
    e.preventDefault()
    setErro('')
    try {
      await onSalvar({ valor: Number(f.valor), taxaJuros: Number(f.taxaJuros), parcelas: Number(f.parcelas) })
    } catch (err) {
      setErro(err.message)
    }
  }
  return (
    <form className="linha quebra" onSubmit={enviar}>
      <input type="number" min="0.01" step="0.01" placeholder="Valor financiado (R$)" value={f.valor}
        onChange={(e) => setF({ ...f, valor: e.target.value })} required />
      <input type="number" min="0" step="0.01" placeholder="Juros (% ao mês)" value={f.taxaJuros}
        onChange={(e) => setF({ ...f, taxaJuros: e.target.value })} required />
      <input type="number" min="1" max="120" step="1" placeholder="Parcelas" value={f.parcelas}
        onChange={(e) => setF({ ...f, parcelas: e.target.value })} required />
      <button>Conceder crédito</button>
      <button type="button" className="secundario" onClick={onCancelar}>Fechar</button>
      {erro && <p className="erro">{erro}</p>}
    </form>
  )
}

export default function Pedidos() {
  const { usuario } = useAuth()
  const cliente = usuario.perfil === 'CLIENTE'
  const banco = usuario.perfil === 'BANCO'
  const [pedidos, setPedidos] = useState(null)
  const [carros, setCarros] = useState([])
  const [aberto, setAberto] = useState(null)
  const [erro, setErro] = useState('')
  const colunas = 8

  const carregar = () => api.pedidos().then(setPedidos).catch((e) => setErro(e.message))
  useEffect(() => {
    carregar()
    api.automoveis().then(setCarros).catch(() => {})
  }, [])

  async function acao(fn) {
    setErro('')
    try {
      await fn()
      await carregar()
    } catch (e) {
      setErro(e.message)
    }
  }
  const alternar = (id, tipo) => setAberto(aberto?.id === id && aberto.tipo === tipo ? null : { id, tipo })

  return (
    <div className="cartao">
      <h1>{cliente ? 'Meus pedidos de aluguel' : 'Pedidos de aluguel'}</h1>
      {cliente && (
        <section>
          <h2>Novo pedido</h2>
          <FormPedido carros={carros} onSalvar={async (d) => { await api.criarPedido(d); await carregar() }} />
        </section>
      )}
      {erro && <p className="erro">{erro}</p>}
      {!pedidos ? <p>Carregando...</p> : (
        <table>
          <thead>
            <tr>
              <th>#</th>
              {!cliente && <th>Cliente e renda</th>}
              <th>Carro</th>
              <th>Período</th>
              <th>Status</th>
              <th>Avaliador</th>
              <th>Contrato / crédito</th>
              <th>Ações</th>
            </tr>
          </thead>
          <tbody>
            {pedidos.map((p) => (
              <Fragment key={p.id}>
                <tr>
                  <td>{p.id}</td>
                  {!cliente && (
                    <td>
                      {p.clienteNome}
                      <div className="pequeno">
                        Renda total: <strong>{brl(p.rendaTotal)}</strong>
                        {p.rendimentos.length === 0 && ' (sem rendimentos)'}
                      </div>
                      {p.rendimentos.map((r, i) => (
                        <div className="pequeno" key={i}>{r.entidadeEmpregadora}: {brl(r.valor)}</div>
                      ))}
                    </td>
                  )}
                  <td>{p.automovel}</td>
                  <td>{data(p.dataInicio)} → {data(p.dataFim)}</td>
                  <td><span className={`selo ${p.status}`}>{ROTULO[p.status]}</span></td>
                  <td>{p.avaliador || '—'}</td>
                  <td>
                    {p.dataContrato ? <div>Executado em {data(p.dataContrato)}</div> : '—'}
                    {p.credito && (
                      <div className="pequeno">
                        Crédito {brl(p.credito.valor)} em {p.credito.parcelas}x a {p.credito.taxaJuros}% a.m.
                        ({p.credito.banco})
                      </div>
                    )}
                  </td>
                  <td className="acoes">
                    {cliente && p.status === 'EM_ANALISE' && (
                      <button className="secundario" onClick={() => alternar(p.id, 'editar')}>Modificar</button>
                    )}
                    {cliente && !['CANCELADO', 'CONTRATO_EXECUTADO'].includes(p.status) && (
                      <button className="perigo" onClick={() => confirm('Cancelar este pedido?') && acao(() => api.cancelarPedido(p.id))}>Cancelar</button>
                    )}
                    {!cliente && p.status === 'EM_ANALISE' && (
                      <>
                        <button onClick={() => acao(() => api.avaliarPedido(p.id, 'APROVADO'))}>Aprovar</button>
                        <button className="perigo" onClick={() => acao(() => api.avaliarPedido(p.id, 'REPROVADO'))}>Reprovar</button>
                      </>
                    )}
                    {!cliente && ['EM_ANALISE', 'APROVADO'].includes(p.status) && (
                      <button className="secundario" onClick={() => alternar(p.id, 'editar')}>Modificar</button>
                    )}
                    {!cliente && p.status === 'APROVADO' && (
                      <button onClick={() => acao(() => api.avaliarPedido(p.id, 'CONTRATO_EXECUTADO'))}>Executar contrato</button>
                    )}
                    {banco && p.status === 'CONTRATO_EXECUTADO' && !p.credito && (
                      <button onClick={() => alternar(p.id, 'credito')}>Conceder crédito</button>
                    )}
                  </td>
                </tr>
                {aberto?.id === p.id && (
                  <tr>
                    <td colSpan={colunas}>
                      {aberto.tipo === 'editar' && (
                        <FormPedido
                          carros={carros}
                          inicial={{ automovelId: p.automovelId, dataInicio: p.dataInicio, dataFim: p.dataFim }}
                          onSalvar={async (d) => { await api.modificarPedido(p.id, d); setAberto(null); await carregar() }}
                          onCancelar={() => setAberto(null)}
                        />
                      )}
                      {aberto.tipo === 'credito' && (
                        <FormCredito
                          onSalvar={async (d) => { await api.concederCredito(p.id, d); setAberto(null); await carregar() }}
                          onCancelar={() => setAberto(null)}
                        />
                      )}
                    </td>
                  </tr>
                )}
              </Fragment>
            ))}
            {pedidos.length === 0 && (
              <tr>
                <td colSpan={colunas}>
                  {cliente
                    ? 'Você ainda não tem pedidos. Crie o primeiro acima.'
                    : 'Nenhum pedido ainda. Os pedidos aparecem aqui quando os clientes os criarem.'}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      )}
    </div>
  )
}
