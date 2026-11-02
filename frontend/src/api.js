const BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080'

let credenciais = null
try {
  credenciais = sessionStorage.getItem('auth')
} catch {
  
}

export function setCredenciais(login, senha) {
  credenciais = login ? btoa(unescape(encodeURIComponent(`${login}:${senha}`))) : null
  try {
    if (credenciais) sessionStorage.setItem('auth', credenciais)
    else sessionStorage.removeItem('auth')
  } catch {
    
  }
}

export const temCredenciais = () => Boolean(credenciais)

async function req(metodo, caminho, corpo) {
  const headers = { 'Content-Type': 'application/json' }
  if (credenciais) headers.Authorization = `Basic ${credenciais}`
  let res
  try {
    res = await fetch(`${BASE}${caminho}`, {
      method: metodo,
      headers,
      body: corpo ? JSON.stringify(corpo) : undefined,
    })
  } catch {
    throw new Error('Não consegui falar com o servidor. O back-end está rodando?')
  }
  if (res.status === 204) return null
  const dados = await res.json().catch(() => null)
  if (!res.ok) {
    if (res.status === 401) throw new Error('Usuário ou senha inválidos')
    throw new Error(dados?.erro || `Erro ${res.status}`)
  }
  return dados
}

export const api = {
  me: () => req('GET', '/api/auth/me'),
  registrar: (d) => req('POST', '/api/auth/register', d),
  meuPerfil: () => req('GET', '/api/clientes/me'),
  atualizarPerfil: (d) => req('PUT', '/api/clientes/me', d),
  excluirConta: () => req('DELETE', '/api/clientes/me'),
  clientes: () => req('GET', '/api/clientes'),
  automoveis: () => req('GET', '/api/automoveis'),
  criarAutomovel: (d) => req('POST', '/api/automoveis', d),
  pedidos: () => req('GET', '/api/pedidos'),
  criarPedido: (d) => req('POST', '/api/pedidos', d),
  modificarPedido: (id, d) => req('PUT', `/api/pedidos/${id}`, d),
  cancelarPedido: (id) => req('DELETE', `/api/pedidos/${id}`),
  concederCredito: (id, d) => req('POST', `/api/pedidos/${id}/credito`, d),
  avaliarPedido: (id, status) => req('PATCH', `/api/pedidos/${id}/avaliacao`, { status }),
}
