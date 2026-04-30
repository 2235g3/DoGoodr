import { Route, Routes } from 'react-router-dom'
import { HomePage } from './pages/HomePage'
import { LoginPage } from './pages/LoginPage'
import { SignupChoicePage } from './pages/SignupChoicePage'
import { PortalPlaceholderPage } from './pages/PortalPlaceholderPage'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/get-started" element={<SignupChoicePage />} />
      <Route
        path="/signup/volunteer"
        element={<PortalPlaceholderPage role="Volunteer" />}
      />
      <Route
        path="/signup/organisation"
        element={<PortalPlaceholderPage role="Organisation" />}
      />
    </Routes>
  )
}
