import { useEffect, useMemo, useState } from 'react'
import { getVolunteerApplications, withdrawVolunteerApplication } from '../../api/volunteer'
import type { ApplicationDTO } from '../../api/types'
import { VolunteerNotice } from './VolunteerNotice'

export function VolunteerApplicationsPage() {
  const [applications, setApplications] = useState<ApplicationDTO[]>([])
  const [statusFilter, setStatusFilter] = useState('ALL')
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const filteredApplications = useMemo(
    () =>
      applications.filter((application) =>
        statusFilter === 'ALL' ? true : application.status === statusFilter,
      ),
    [applications, statusFilter],
  )

  const statuses = useMemo(
    () => Array.from(new Set(applications.map((application) => application.status))).sort(),
    [applications],
  )

  useEffect(() => {
    loadApplications()
  }, [])

  async function loadApplications() {
    setError('')
    try {
      setApplications(await getVolunteerApplications())
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load applications.')
    }
  }

  async function handleWithdraw(applicationId: string) {
    setError('')
    setMessage('')
    try {
      const updated = await withdrawVolunteerApplication(applicationId)
      setApplications((current) =>
        current.map((application) => (application.id === updated.id ? updated : application)),
      )
      setMessage('Application withdrawn.')
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to withdraw application.')
    }
  }

  return (
    <>
      <div className="admin-heading">
        <p className="eyebrow">Applications</p>
        <h2>Your applications</h2>
        <p>Track the opportunities you have applied for and their latest decision state.</p>
      </div>

      {error ? <VolunteerNotice tone="error">{error}</VolunteerNotice> : null}
      {message ? <VolunteerNotice tone="success">{message}</VolunteerNotice> : null}

      <div className="admin-panel admin-form">
        <label>
          Status filter
          <select value={statusFilter} onChange={(event) => setStatusFilter(event.target.value)}>
            <option value="ALL">All statuses</option>
            {statuses.map((status) => (
              <option key={status} value={status}>
                {status}
              </option>
            ))}
          </select>
        </label>
      </div>

      <div className="admin-table-wrap">
        <table className="admin-table">
          <thead>
            <tr>
              <th>Opportunity</th>
              <th>Status</th>
              <th>Applied</th>
              <th>Decision</th>
              <th>Message</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredApplications.map((application) => (
              <tr key={application.id}>
                <td>
                  {application.opportunityName}
                  <small>{application.opportunityId}</small>
                </td>
                <td>
                  <span className="volunteer-status-pill">{application.status}</span>
                </td>
                <td>{formatDate(application.dateApplied)}</td>
                <td>{formatDate(application.decisionDate)}</td>
                <td>{application.message || '...'}</td>
                <td>
                  {canWithdraw(application.status) ? (
                    <button type="button" onClick={() => handleWithdraw(application.id)}>
                      Withdraw
                    </button>
                  ) : (
                    '...'
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  )
}

function canWithdraw(status: string) {
  return ['APPLIED', 'UNDER_REVIEW', 'REJECTED'].includes(status)
}

function formatDate(value?: string | null) {
  if (!value) return '...'
  return new Date(value).toLocaleDateString()
}
