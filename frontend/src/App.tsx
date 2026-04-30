import { Route, Routes } from 'react-router-dom'
import { HomePage } from './pages/HomePage'
import { LoginPage } from './pages/LoginPage'
import { OrganisationSignupPage } from './pages/OrganisationSignupPage'
import { SignupChoicePage } from './pages/SignupChoicePage'
import { RoleLandingPage } from './pages/RoleLandingPage'
import { VolunteerSignupPage } from './pages/VolunteerSignupPage'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/get-started" element={<SignupChoicePage />} />
      <Route path="/signup/volunteer" element={<VolunteerSignupPage />} />
      <Route path="/signup/organisation" element={<OrganisationSignupPage />} />
      <Route path="/volunteer" element={<RoleLandingPage role="VOLUNTEER" />} />
      <Route
        path="/organisation"
        element={<RoleLandingPage role="ORGANISATION" />}
      />
      <Route path="/admin" element={<RoleLandingPage role="ADMIN" />} />
    </Routes>
  )
}
