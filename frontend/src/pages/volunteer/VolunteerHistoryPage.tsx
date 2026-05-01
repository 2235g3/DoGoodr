import { useEffect, useMemo, useState } from 'react'
import { getVolunteerHistory } from '../../api/volunteer'
import type { VolunteerHistoryDTO } from '../../api/types'
import { VolunteerNotice } from './VolunteerNotice'

export function VolunteerHistoryPage() {
  const [history, setHistory] = useState<VolunteerHistoryDTO[]>([])
  const [error, setError] = useState('')

  const totals = useMemo(
    () => ({
      hours: history.reduce((total, item) => total + item.hoursLogged, 0),
      points: history.reduce((total, item) => total + item.pointsGained, 0),
      organisations: new Set(history.map((item) => item.organisationId)).size,
    }),
    [history],
  )

  useEffect(() => {
    loadHistory()
  }, [])

  async function loadHistory() {
    setError('')
    try {
      setHistory(await getVolunteerHistory())
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load history.')
    }
  }

  return (
    <>
      <div className="admin-heading">
        <p className="eyebrow">History</p>
        <h2>Volunteering record</h2>
        <p>Review completed volunteering records, logged hours, points, and organisation notes.</p>
      </div>

      {error ? <VolunteerNotice tone="error">{error}</VolunteerNotice> : null}

      <div className="admin-stat-grid volunteer-stat-grid">
        <div className="admin-stat-card volunteer-stat-card">
          <span>Total hours</span>
          <strong>{totals.hours}</strong>
        </div>
        <div className="admin-stat-card volunteer-stat-card">
          <span>Points gained</span>
          <strong>{totals.points}</strong>
        </div>
        <div className="admin-stat-card volunteer-stat-card">
          <span>Organisations</span>
          <strong>{totals.organisations}</strong>
        </div>
      </div>

      <div className="admin-table-wrap">
        <table className="admin-table">
          <thead>
            <tr>
              <th>Opportunity</th>
              <th>Organisation</th>
              <th>Date range</th>
              <th>Hours</th>
              <th>Points</th>
              <th>Comment</th>
            </tr>
          </thead>
          <tbody>
            {history.map((item) => (
              <tr key={`${item.opportunityId}-${item.organisationId}-${item.startDate}`}>
                <td>
                  {item.opportunityTitle}
                  <small>{item.opportunityId}</small>
                </td>
                <td>{item.organisationName}</td>
                <td>
                  {formatDate(item.startDate)} - {formatDate(item.endDate)}
                </td>
                <td>{item.hoursLogged}</td>
                <td>{item.pointsGained}</td>
                <td>{item.organisationComment || '...'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  )
}

function formatDate(value?: string | null) {
  if (!value) return '...'
  return new Date(value).toLocaleDateString()
}
