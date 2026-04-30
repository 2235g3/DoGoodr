import { Route, Routes } from 'react-router-dom'
import { HomePage } from './pages/HomePage'
import { LoginPage } from './pages/LoginPage'
import { OrganisationSignupPage } from './pages/OrganisationSignupPage'
import { SignupChoicePage } from './pages/SignupChoicePage'
import { RoleLandingPage } from './pages/RoleLandingPage'
import {
  OrganisationApplicationsPage,
  OrganisationDashboardPage,
  OrganisationHistoryPage,
  OrganisationNotificationsPage,
  OrganisationOpportunitiesPage,
  OrganisationOpportunityFormPage,
  OrganisationProfilePage,
} from './pages/OrganisationPortal'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/get-started" element={<SignupChoicePage />} />
      <Route path="/signup/volunteer" element={<VolunteerSignupPage />} />
      <Route path="/signup/organisation" element={<OrganisationSignupPage />} />
      <Route path="/volunteer" element={<RoleLandingPage role="VOLUNTEER" />} />
      <Route path="/organisation" element={<OrganisationDashboardPage />} />
      <Route path="/organisation/profile" element={<OrganisationProfilePage />} />
      <Route path="/organisation/opportunities" element={<OrganisationOpportunitiesPage />} />
      <Route
        path="/organisation/opportunities/new"
        element={<OrganisationOpportunityFormPage mode="create" />}
      />
      <Route
        path="/organisation/opportunities/:opportunityId/edit"
        element={<OrganisationOpportunityFormPage mode="edit" />}
      />
      <Route path="/organisation/applications" element={<OrganisationApplicationsPage />} />
      <Route path="/organisation/history" element={<OrganisationHistoryPage />} />
      <Route path="/organisation/notifications" element={<OrganisationNotificationsPage />} />
      <Route path="/admin" element={<RoleLandingPage role="ADMIN" />} />
    </Routes>
  )
}
