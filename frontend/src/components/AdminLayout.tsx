import { Link, NavLink, Outlet, useNavigate } from 'react-router-dom'
import { clearAuthSession, getStoredUser } from '../api/auth'
import { BrandHeader } from './BrandHeader'

const adminLinks = [
  { to: '/admin', label: 'Dashboard', end: true },
  { to: '/admin/users', label: 'Users' },
  { to: '/admin/profiles', label: 'Profiles' },
  { to: '/admin/opportunities', label: 'Opportunities' },
  { to: '/admin/applications', label: 'Applications' },
  { to: '/admin/history', label: 'History' },
  { to: '/admin/taxonomy', label: 'Taxonomy' },
]

export function AdminLayout() {
  const navigate = useNavigate()
  const user = getStoredUser()

  function handleLogout() {
    clearAuthSession()
    navigate('/login', { replace: true })
  }

  if (user?.role !== 'ADMIN') {
    return (
      <main className="admin-page">
        <BrandHeader variant="panel" />
        <section className="admin-gate">
          <p className="eyebrow">Admin console</p>
          <h1>Admin access required</h1>
          <p>
            Log in with an admin account to manage users, profiles, opportunities,
            applications, and volunteer history records.
          </p>
          <div className="admin-row-actions">
            <Link className="button button--primary" to="/login">
              Log in
            </Link>
            <Link className="button button--secondary" to="/">
              Return home
            </Link>
          </div>
        </section>
      </main>
    )
  }

  return (
    <main className="admin-page">
      <BrandHeader variant="panel">
        <div className="workspace-header-meta">
          <span>Admin workspace</span>
          <Link to="/admin/taxonomy">Tune taxonomy</Link>
        </div>
      </BrandHeader>
      <div className="admin-shell">
        <aside className="admin-sidebar">
          <div>
            <p className="eyebrow">Admin console</p>
            <h1>Operations</h1>
            <p className="admin-user">{user?.email ?? 'No user session'}</p>
          </div>
          <nav className="admin-nav" aria-label="Admin">
            {adminLinks.map((link) => (
              <NavLink
                key={link.to}
                to={link.to}
                end={link.end}
                className={({ isActive }) => (isActive ? 'is-active' : undefined)}
              >
                {link.label}
              </NavLink>
            ))}
          </nav>
          <button className="button button--secondary" type="button" onClick={handleLogout}>
            Logout
          </button>
        </aside>
        <section className="admin-content">
          <Outlet />
        </section>
      </div>
    </main>
  )
}
