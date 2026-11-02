import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext'

export default function Login() {
  const { usuario, entrar } = useAuth()
  const navigate = useNavigate()
  const [login, setLogin] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  if (usuario) return <Navigate to="/pedidos" replace />

  async function enviar(e) {
    e.preventDefault()
    setErro('')
    setEnviando(true)
    try {
      await entrar(login, senha)
      navigate('/pedidos')
    } catch (err) {
      setErro(err.message)
    } finally {
      setEnviando(false)
    }
  }

  return (
    <form className="cartao estreito" onSubmit={enviar}>
      <h1>Entrar</h1>
      <label>
        Usuário
        <input value={login} onChange={(e) => setLogin(e.target.value)} required autoFocus />
      </label>
      <label>
        Senha
        <input type="password" value={senha} onChange={(e) => setSenha(e.target.value)} required />
      </label>
      {erro && <p className="erro">{erro}</p>}
      <button disabled={enviando}>{enviando ? 'Entrando...' : 'Entrar'}</button>
      <p>
        Ainda não tem conta? <Link to="/cadastro">Cadastre-se</Link>
      </p>
    </form>
  )
}
