import { useEffect, useState } from 'react'
import { api } from '../api'

export default function Clientes() {
  const [lista, setLista] = useState(null)
  const [erro, setErro] = useState('')
  useEffect(() => {
    api.clientes().then(setLista).catch((e) => setErro(e.message))
  }, [])
  if (erro) return <p className="erro">{erro}</p>
  if (!lista) return <p>Carregando...</p>
  const brl = (n) => Number(n).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })
  return (
    <div className="cartao">
      <h1>Clientes</h1>
      <table>
        <thead>
          <tr><th>Nome</th><th>CPF</th><th>Profissão</th><th>Rendimentos</th></tr>
        </thead>
        <tbody>
          {lista.map((c) => (
            <tr key={c.id}>
              <td>{c.nome}</td>
              <td>{c.cpf}</td>
              <td>{c.profissao}</td>
              <td>{c.rendimentos.map((r) => `${r.entidadeEmpregadora}: ${brl(r.valor)}`).join(' | ') || '—'}</td>
            </tr>
          ))}
          {lista.length === 0 && <tr><td colSpan="4">Nenhum cliente cadastrado.</td></tr>}
        </tbody>
      </table>
    </div>
  )
}
