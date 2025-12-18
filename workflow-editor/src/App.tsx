import { NavLink, Route, Routes } from 'react-router-dom'
import About from './pages/About'
import Home from './pages/Home'
import NotFound from './pages/NotFound'

function App() {
  return (
    <>
      <nav style={{ display: 'flex', gap: 12, padding: 12 }}>
        <NavLink to="/">Home</NavLink>
        <NavLink to="/about">About</NavLink>
      </nav>

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<About />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
    </>
  )
}

export default App
