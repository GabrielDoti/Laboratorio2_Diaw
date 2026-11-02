import { useEffect, useState } from 'react'
import { api } from '../api'
import { useAuth } from '../AuthContext'

const vazio = { marca: '', modelo: '', ano: new Date().getFullYear(), placa: '', matricula: '', tipoProprietario: '', proprietarioId: '' }

export default function Carros() {
  const { usuario } = useAuth()
  const agente = usuario.perfil !== 'CLIENTE'
  const [lista, setLista] = useState(null)
  const [clientes, setClientes] = useState([])
  const [f, setF] = useState({ ...vazio, tipoProprietario: usuario.perfil })
  const [erro, setErro] = useState('')
  const [ok, setOk] = useState('')

  const carregar = () => api.automoveis().then(setLista).catch((e) => setErro(e.message))
  useEffect(() => {
    carregar()
    if (agente) api.clientes().then(setClientes).catch(() => {})
  }, [agente])

  const campo = (k) => ({ value: f[k], onChange: (e) => setF({ ...f, [k]: e.target.value }), required: true })

  async function cadastrar(e) {
    e.preventDefault()
    setErro('')
    setOk('')
    try {
      await api.criarAutomovel({
        matricula: f.matricula,
        ano: Number(f.ano),
        marca: f.marca,
        modelo: f.modelo,
        placa: f.placa,
        tipoProprietario: f.tipoProprietario,
        proprietarioId: f.tipoProprietario === 'CLIENTE' ? Number(f.proprietarioId) : null,
      })
      setF({ ...vazio, tipoProprietario: usuario.perfil })
      setOk('Automóvel cadastrado!')
      carregar()
    } catch (err) {
      setErro(err.message)
    }
  }

  if (!lista) return <p className={erro ? 'erro' : ''}>{erro || 'Carregando...'}</p>
  return (
    <div className="cartao">
      <h1>Automóveis</h1>
      <table>
        <thead>
          <tr><th>Marca</th><th>Modelo</th><th>Ano</th><th>Placa</th><th>Matrícula</th><th>Propriedade</th></tr>
        </thead>
        <tbody>
          {lista.map((a) => (
            <tr key={a.id}>
              <td>{a.marca}</td><td>{a.modelo}</td><td>{a.ano}</td><td>{a.placa}</td>
              <td>{a.matricula}</td><td>{a.tipoProprietario}</td>
            </tr>
          ))}
          {lista.length === 0 && <tr><td colSpan="6">Nenhum automóvel cadastrado.</td></tr>}
        </tbody>
      </table>

      {agente && (
        <form onSubmit={cadastrar}>
          <h2>Cadastrar automóvel</h2>
          <div className="grade">
            <label>Marca<input {...campo('marca')} /></label>
            <label>Modelo<input {...campo('modelo')} /></label>
            <label>Ano<input type="number" min="1950" {...campo('ano')} /></label>
            <label>Placa<input {...campo('placa')} /></label>
            <label>Matrícula<input {...campo('matricula')} /></label>
            <label>
              Propriedade de
              <select value={f.tipoProprietario} onChange={(e) => setF({ ...f, tipoProprietario: e.target.value })}>
                <option value={usuario.perfil}>{usuario.perfil === 'BANCO' ? 'Banco (eu)' : 'Empresa (eu)'}</option>
                <option value="CLIENTE">Um cliente</option>
              </select>
            </label>
            {f.tipoProprietario === 'CLIENTE' && (
              <label>
                Cliente proprietário
                <select value={f.proprietarioId} onChange={(e) => setF({ ...f, proprietarioId: e.target.value })} required>
                  <option value="">Escolha...</option>
                  {clientes.map((c) => <option key={c.id} value={c.id}>{c.nome} ({c.cpf})</option>)}
                </select>
              </label>
            )}
          </div>
          {erro && <p className="erro">{erro}</p>}
          {ok && <p className="ok">{ok}</p>}
          <button>Cadastrar</button>
        </form>
      )}
    </div>
  )
}
