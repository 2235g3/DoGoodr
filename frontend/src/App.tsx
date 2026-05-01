import { Route, Routes } from 'react-router-dom'
import { AdminLayout } from './components/AdminLayout'
import { VolunteerLayout } from './components/VolunteerLayout'
import { HomePage } from './pages/HomePage'
import { LoginPage } from './pages/LoginPage'
import { OrganisationSignupPage } from './pages/OrganisationSignupPage'
import { SignupChoicePage } from './pages/SignupChoicePage'
import { VolunteerSignupPage } from './pages/VolunteerSignupPage'
import {
  OrganisationApplicationsPage,
  OrganisationDashboardPage,
  OrganisationHistoryPage,
  OrganisationNotificationsPage,
  OrganisationOpportunitiesPage,
  OrganisationOpportunityFormPage,
  OrganisationProfilePage,
} from './pages/OrganisationPortal'
import { AdminApplicationsPage } from './pages/admin/AdminApplicationsPage'
import { AdminDashboard } from './pages/admin/AdminDashboard'
import { AdminHistoryPage } from './pages/admin/AdminHistoryPage'
import { AdminOpportunitiesPage } from './pages/admin/AdminOpportunitiesPage'
import { AdminProfilesPage } from './pages/admin/AdminProfilesPage'
import { AdminUsersPage } from './pages/admin/AdminUsersPage'
import { VolunteerAccountPage } from './pages/volunteer/VolunteerAccountPage'
import { VolunteerApplicationsPage } from './pages/volunteer/VolunteerApplicationsPage'
import { VolunteerDashboard } from './pages/volunteer/VolunteerDashboard'
import { VolunteerHistoryPage } from './pages/volunteer/VolunteerHistoryPage'
import { VolunteerMatchesPage } from './pages/volunteer/VolunteerMatchesPage'
import { VolunteerNotificationsPage } from './pages/volunteer/VolunteerNotificationsPage'
import { VolunteerProfilePage } from './pages/volunteer/VolunteerProfilePage'

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<HomePage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/get-started" element={<SignupChoicePage />} />
      <Route path="/signup/volunteer" element={<VolunteerSignupPage />} />
      <Route path="/signup/organisation" element={<OrganisationSignupPage />} />

      <Route path="/volunteer" element={<VolunteerLayout />}>
        <Route index element={<VolunteerDashboard />} />
        <Route path="profile" element={<VolunteerProfilePage />} />
        <Route path="matches" element={<VolunteerMatchesPage />} />
        <Route path="applications" element={<VolunteerApplicationsPage />} />
        <Route path="notifications" element={<VolunteerNotificationsPage />} />
        <Route path="history" element={<VolunteerHistoryPage />} />
        <Route path="account" element={<VolunteerAccountPage />} />
      </Route>

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
