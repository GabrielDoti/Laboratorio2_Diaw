import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext'

export default function Layout() {
  const { usuario, sair } = useAuth()
  const navigate = useNavigate()
  const cliente = usuario.perfil === 'CLIENTE'

  return (
    <>
      <header className="topo">
        <strong> Aluguel de Carros</strong>
        <nav>
          <NavLink to="/pedidos">Pedidos</NavLink>
          <NavLink to="/carros">Carros</NavLink>
          {cliente ? <NavLink to="/perfil">Meus dados</NavLink> : <NavLink to="/clientes">Clientes</NavLink>}
        </nav>
        <span className="usuario">
          {usuario.nome} <small>({usuario.perfil})</small>
          <button
            className="link"
            onClick={() => {
              sair()
              navigate('/login')
            }}
          >
            Sair
          </button>
        </span>
      </header>
      <main>
        <Outlet />
      </main>
    </>
  )
}
