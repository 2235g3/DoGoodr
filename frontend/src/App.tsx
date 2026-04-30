import { Route, Routes } from 'react-router-dom'
import { AdminLayout } from './components/AdminLayout'
import { HomePage } from './pages/HomePage'
import { LoginPage } from './pages/LoginPage'
import { OrganisationSignupPage } from './pages/OrganisationSignupPage'
import { SignupChoicePage } from './pages/SignupChoicePage'
import { RoleLandingPage } from './pages/RoleLandingPage'
import { VolunteerSignupPage } from './pages/VolunteerSignupPage'
import { AdminApplicationsPage } from './pages/admin/AdminApplicationsPage'
import { AdminDashboard } from './pages/admin/AdminDashboard'
import { AdminHistoryPage } from './pages/admin/AdminHistoryPage'
import { AdminOpportunitiesPage } from './pages/admin/AdminOpportunitiesPage'
import { AdminProfilesPage } from './pages/admin/AdminProfilesPage'
import { AdminUsersPage } from './pages/admin/AdminUsersPage'

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
      <Route path="/admin" element={<AdminLayout />}>
        <Route index element={<AdminDashboard />} />
        <Route path="users" element={<AdminUsersPage />} />
        <Route path="profiles" element={<AdminProfilesPage />} />
        <Route path="opportunities" element={<AdminOpportunitiesPage />} />
        <Route path="applications" element={<AdminApplicationsPage />} />
        <Route path="history" element={<AdminHistoryPage />} />
      </Route>
    </Routes>
  )
}
