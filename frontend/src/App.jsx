import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider, useAuth } from './AuthContext'
import Layout from './components/Layout'
import Cadastro from './pages/Cadastro'
import Carros from './pages/Carros'
import Clientes from './pages/Clientes'
import Login from './pages/Login'
import Pedidos from './pages/Pedidos'
import Perfil from './pages/Perfil'

function Protegido({ perfis, children }) {
  const { usuario, carregando } = useAuth()
  if (carregando) return <p>Carregando...</p>
  if (!usuario) return <Navigate to="/login" replace />
  if (perfis && !perfis.includes(usuario.perfil)) return <Navigate to="/pedidos" replace />
  return children
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/cadastro" element={<Cadastro />} />
          <Route element={<Protegido><Layout /></Protegido>}>
            <Route path="/pedidos" element={<Pedidos />} />
            <Route path="/carros" element={<Carros />} />
            <Route path="/perfil" element={<Protegido perfis={['CLIENTE']}><Perfil /></Protegido>} />
            <Route path="/clientes" element={<Protegido perfis={['EMPRESA', 'BANCO']}><Clientes /></Protegido>} />
          </Route>
          <Route path="*" element={<Navigate to="/pedidos" replace />} />
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  )
}
