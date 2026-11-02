import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../api'
import RendimentosEditor from '../components/RendimentosEditor'

const vazio = { login: '', senha: '', nome: '', rg: '', cpf: '', endereco: '', profissao: '' }

export default function Cadastro() {
  const navigate = useNavigate()
  const [f, setF] = useState(vazio)
  const [rendimentos, setRendimentos] = useState([])
  const [erro, setErro] = useState('')
  const campo = (k) => ({ value: f[k], onChange: (e) => setF({ ...f, [k]: e.target.value }), required: true })

  async function enviar(e) {
    e.preventDefault()
    setErro('')
    try {
      await api.registrar({
        ...f,
        rendimentos: rendimentos.map((r) => ({ ...r, valor: Number(r.valor) })),
      })
      navigate('/login')
    } catch (err) {
      setErro(err.message)
    }
  }

  return (
    <form className="cartao" onSubmit={enviar}>
      <h1>Cadastro de cliente</h1>
      <div className="grade">
        <label>Usuário<input {...campo('login')} minLength={3} /></label>
        <label>Senha<input type="password" {...campo('senha')} minLength={6} /></label>
        <label>Nome<input {...campo('nome')} /></label>
        <label>RG<input {...campo('rg')} /></label>
        <label>CPF<input {...campo('cpf')} /></label>
        <label>Profissão<input {...campo('profissao')} /></label>
      </div>
      <label>Endereço<input {...campo('endereco')} /></label>
      <RendimentosEditor valor={rendimentos} onChange={setRendimentos} />
      {erro && <p className="erro">{erro}</p>}
      <button>Cadastrar</button>
      <p>
        Já tem conta? <Link to="/login">Entrar</Link>
      </p>
    </form>
  )
}
