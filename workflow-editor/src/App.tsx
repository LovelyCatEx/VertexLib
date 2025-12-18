import {Route, Routes} from 'react-router-dom'
import Home from './pages/Home'
import NotFound from './pages/NotFound'
import {Toaster} from "sonner";

function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="*" element={<NotFound />} />
      </Routes>
      <Toaster richColors position="top-center" />
    </>
  )
}

export default App
