import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import {
  getAdminApplications,
  getAdminOpportunities,
  getAdminOrganisationProfiles,
  getAdminUsers,
  getAdminVolunteerProfiles,
} from '../../api/admin'
import { AdminNotice } from './AdminNotice'

type DashboardStats = {
  users: number
  volunteers: number
  organisations: number
  opportunities: number
  applications: number
}

export function AdminDashboard() {
  const [stats, setStats] = useState<DashboardStats | null>(null)
  const [error, setError] = useState('')

  useEffect(() => {
    let isMounted = true

    Promise.all([
      getAdminUsers(),
      getAdminVolunteerProfiles(),
      getAdminOrganisationProfiles(),
      getAdminOpportunities(),
      getAdminApplications(),
    ])
      .then(([users, volunteers, organisations, opportunities, applications]) => {
        if (!isMounted) return
        setStats({
          users: users.length,
          volunteers: volunteers.length,
          organisations: organisations.length,
          opportunities: opportunities.length,
          applications: applications.length,
        })
      })
      .catch((caughtError) => {
        if (!isMounted) return
        setError(caughtError instanceof Error ? caughtError.message : 'Unable to load admin data.')
      })

    return () => {
      isMounted = false
    }
  }, [])

  return (
    <>
      <div className="admin-heading">
        <p className="eyebrow">Overview</p>
        <h2>Admin dashboard</h2>
        <p>Monitor platform activity and jump into the core admin workflows.</p>
      </div>

      {error ? <AdminNotice tone="error">{error}</AdminNotice> : null}

      <div className="admin-stat-grid">
        <StatCard label="Users" value={stats?.users} to="/admin/users" />
        <StatCard label="Volunteer profiles" value={stats?.volunteers} to="/admin/profiles" />
        <StatCard label="Organisations" value={stats?.organisations} to="/admin/profiles" />
        <StatCard label="Opportunities" value={stats?.opportunities} to="/admin/opportunities" />
        <StatCard label="Applications" value={stats?.applications} to="/admin/applications" />
      </div>

      <div className="admin-panel">
        <h3>Suggested checks</h3>
        <div className="admin-action-grid">
          <Link to="/admin/profiles">Review organisation verification</Link>
          <Link to="/admin/users">Search users by email or role</Link>
          <Link to="/admin/history">Look up volunteer history records</Link>
        </div>
      </div>
    </>
  )
}

function StatCard({ label, value, to }: { label: string; value?: number; to: string }) {
  return (
    <Link className="admin-stat-card" to={to}>
      <span>{label}</span>
      <strong>{value ?? '...'}</strong>
    </Link>
  )
}
