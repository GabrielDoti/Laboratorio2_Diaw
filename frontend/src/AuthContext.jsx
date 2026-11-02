import { createContext, useContext, useEffect, useState } from 'react'
import { api, setCredenciais, temCredenciais } from './api'

const Ctx = createContext(null)
export const useAuth = () => useContext(Ctx)

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(null)
  const [carregando, setCarregando] = useState(temCredenciais())

  useEffect(() => {
    if (!temCredenciais()) return
    api
      .me()
      .then(setUsuario)
      .catch(() => setCredenciais(null))
      .finally(() => setCarregando(false))
  }, [])

  async function entrar(login, senha) {
    setCredenciais(login, senha)
    try {
      setUsuario(await api.me())
    } catch (e) {
      setCredenciais(null)
      throw e
    }
  }

  function sair() {
    setCredenciais(null)
    setUsuario(null)
  }

  return <Ctx.Provider value={{ usuario, carregando, entrar, sair }}>{children}</Ctx.Provider>
}
