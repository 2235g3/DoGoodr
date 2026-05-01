import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import {
  getUnreadVolunteerNotifications,
  getMyVolunteerLabels,
  getVolunteerApplications,
  getVolunteerHistory,
  getVolunteerMatches,
  getVolunteerProfile,
} from '../../api/volunteer'
import { calculateVolunteerProfileCompletion, type VolunteerProfileCompletion } from '../../utils/volunteerProfile'
import { VolunteerNotice } from './VolunteerNotice'

type DashboardStats = {
  points: number
  hours: number
  applications: number
  matches: number
  unread: number
}

export function VolunteerDashboard() {
  const [stats, setStats] = useState<DashboardStats | null>(null)
  const [completion, setCompletion] = useState<VolunteerProfileCompletion | null>(null)
  const [name, setName] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    let isMounted = true

    Promise.all([
      getVolunteerProfile(),
      getVolunteerApplications(),
      getVolunteerHistory(),
      getVolunteerMatches().catch(() => []),
      getUnreadVolunteerNotifications(),
      getMyVolunteerLabels(),
    ])
      .then(([profile, applications, history, matches, unread, labels]) => {
        if (!isMounted) return
        setName(profile.preferredName || profile.forename)
        setCompletion(calculateVolunteerProfileCompletion(profile, labels))
        setStats({
          points: profile.pointsBalance ?? 0,
          hours:
            profile.totalHours ??
            history.reduce((total, item) => total + item.hoursLogged, 0),
          applications: applications.length,
          matches: matches.length,
          unread: unread.length,
        })
      })
      .catch((caughtError) => {
        if (!isMounted) return
        setError(caughtError instanceof Error ? caughtError.message : 'Unable to load dashboard.')
      })

    return () => {
      isMounted = false
    }
  }, [])

  return (
    <>
      <div className="admin-heading">
        <p className="eyebrow">Overview</p>
        <h2>{name ? `Welcome back, ${name}.` : 'Volunteer dashboard'}</h2>
        <p>Keep your profile fresh, discover suitable opportunities, and track your activity.</p>
      </div>

      {error ? <VolunteerNotice tone="error">{error}</VolunteerNotice> : null}

      {completion && !completion.complete ? (
        <section className="admin-panel volunteer-completion-card">
          <div>
            <p className="eyebrow">Profile reminder</p>
            <h3>Complete your profile to unlock stronger matches.</h3>
            <p>
              Your profile is {completion.percent}% complete. Add {completion.missing.join(', ')}
              {' '}to help the matching service understand what fits you.
            </p>
          </div>
          <div className="volunteer-completion-meter">
            <strong>{completion.percent}%</strong>
            <progress value={completion.percent} max={100} />
          </div>
          <Link className="button button--primary" to="/volunteer/onboarding">
            Complete profile
          </Link>
        </section>
      ) : null}

      <div className="admin-stat-grid volunteer-stat-grid">
        <StatCard label="Points" value={stats?.points} to="/volunteer/history" />
        <StatCard label="Hours" value={stats?.hours} to="/volunteer/history" />
        <StatCard label="Applications" value={stats?.applications} to="/volunteer/applications" />
        <StatCard label="Matches" value={stats?.matches} to="/volunteer/matches" />
        <StatCard label="Unread" value={stats?.unread} to="/volunteer/notifications" />
      </div>

      <div className="admin-grid-two">
        <section className="admin-panel">
          <h3>Next best actions</h3>
          <div className="admin-action-grid volunteer-action-grid">
            <Link to="/volunteer/matches">Browse opportunities</Link>
            <Link to="/volunteer/profile">Update profile</Link>
            <Link to="/volunteer/applications">Check applications</Link>
          </div>
        </section>
        <section className="admin-panel volunteer-callout">
          <p className="eyebrow">Discovery</p>
          <h3>Search first, match second.</h3>
          <p>
            Browse all open opportunities now. When your profile and labels are complete, matches
            become a curated layer on top of search.
          </p>
        </section>
      </div>
    </>
  )
}

function StatCard({ label, value, to }: { label: string; value?: number; to: string }) {
  return (
    <Link className="admin-stat-card volunteer-stat-card" to={to}>
      <span>{label}</span>
      <strong>{value ?? '...'}</strong>
    </Link>
  )
}
