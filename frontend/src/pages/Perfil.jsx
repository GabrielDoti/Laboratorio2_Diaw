import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api'
import { useAuth } from '../AuthContext'
import RendimentosEditor from '../components/RendimentosEditor'

export default function Perfil() {
  const { sair } = useAuth()
  const navigate = useNavigate()
  const [f, setF] = useState(null)
  const [msg, setMsg] = useState({ tipo: '', texto: '' })

  useEffect(() => {
    api.meuPerfil().then(setF).catch((e) => setMsg({ tipo: 'erro', texto: e.message }))
  }, [])

  if (!f) return <p className={msg.tipo}>{msg.texto || 'Carregando...'}</p>
  const campo = (k) => ({ value: f[k], onChange: (e) => setF({ ...f, [k]: e.target.value }), required: true })

  async function salvar(e) {
    e.preventDefault()
    try {
      const atualizado = await api.atualizarPerfil({
        rg: f.rg,
        nome: f.nome,
        endereco: f.endereco,
        profissao: f.profissao,
        rendimentos: f.rendimentos.map((r) => ({ ...r, valor: Number(r.valor) })),
      })
      setF(atualizado)
      setMsg({ tipo: 'ok', texto: 'Dados atualizados!' })
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message })
    }
  }

  async function excluir() {
    if (!confirm('Excluir sua conta? Essa ação não pode ser desfeita.')) return
    try {
      await api.excluirConta()
      sair()
      navigate('/login')
    } catch (err) {
      setMsg({ tipo: 'erro', texto: err.message })
    }
  }

  return (
    <form className="cartao" onSubmit={salvar}>
      <h1>Meus dados</h1>
      <p>
        Usuário: <strong>{f.login}</strong> · CPF: <strong>{f.cpf}</strong>
      </p>
      <div className="grade">
        <label>Nome<input {...campo('nome')} /></label>
        <label>RG<input {...campo('rg')} /></label>
        <label>Profissão<input {...campo('profissao')} /></label>
      </div>
      <label>Endereço<input {...campo('endereco')} /></label>
      <RendimentosEditor valor={f.rendimentos} onChange={(r) => setF({ ...f, rendimentos: r })} />
      {msg.texto && <p className={msg.tipo}>{msg.texto}</p>}
      <div className="linha">
        <button>Salvar</button>
        <button type="button" className="perigo" onClick={excluir}>Excluir conta</button>
      </div>
    </form>
  )
}
