import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom'
import { clearAuthSession, getStoredUser } from '../api/auth'
import { useNotifications } from '../notifications/NotificationContext'
import { BrandHeader } from './BrandHeader'

const volunteerLinks = [
  { to: '/volunteer', label: 'Dashboard', end: true },
  { to: '/volunteer/onboarding', label: 'Onboarding' },
  { to: '/volunteer/profile', label: 'Profile' },
  { to: '/volunteer/matches', label: 'Browse' },
  { to: '/volunteer/applications', label: 'Applications' },
  { to: '/volunteer/notifications', label: 'Notifications' },
  { to: '/volunteer/history', label: 'History' },
  { to: '/volunteer/account', label: 'Account' },
]

export function VolunteerLayout() {
  const navigate = useNavigate()
  const user = getStoredUser()
  const { unreadCount } = useNotifications()

  function handleLogout() {
    clearAuthSession()
    navigate('/login', { replace: true })
  }

  if (user?.role !== 'VOLUNTEER') {
    return (
      <main className="volunteer-page">
        <BrandHeader variant="panel" />
        <section className="admin-gate">
          <p className="eyebrow">Volunteer hub</p>
          <h1>Volunteer access required</h1>
          <p>
            Log in with a volunteer account to manage your profile, discover
            matches, track applications, and review your volunteering history.
          </p>
          <div className="admin-row-actions">
            <Link className="button button--primary" to="/login">
              Log in
            </Link>
            <Link className="button button--secondary" to="/signup/volunteer">
              Create account
            </Link>
          </div>
        </section>
      </main>
    )
  }

  return (
    <main className="volunteer-page">
      <BrandHeader variant="panel">
        <div className="workspace-header-meta">
          <span>Volunteer workspace</span>
          <Link to="/volunteer/matches">Browse opportunities</Link>
        </div>
      </BrandHeader>
      <div className="admin-shell">
        <aside className="admin-sidebar volunteer-sidebar">
          <div>
            <p className="eyebrow">Volunteer hub</p>
            <h1>My DoGoodr</h1>
            <p className="admin-user">{user.email}</p>
          </div>
          <nav className="admin-nav" aria-label="Volunteer">
            {volunteerLinks.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                end={link.end}
                className={({ isActive }) => (isActive ? 'is-active' : undefined)}
              >
                <span className="notification-nav-label">{link.label}</span>
                {link.label === 'Notifications' && unreadCount > 0 ? (
                  <span className="notification-badge">{unreadCount}</span>
                ) : null}
              </NavLink>
            ))}
          </nav>
          <button className="button button--secondary" type="button" onClick={handleLogout}>
            Logout
          </button>
        </aside>
        <section className="admin-content volunteer-content">
          <Outlet />
        </section>
      </div>
    </main>
  )
}
