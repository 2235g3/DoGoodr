import { FormEvent, useEffect, useState } from 'react'
import {
  getAdminApplicationById,
  getAdminApplications,
  getAdminApplicationsByOpportunity,
  getAdminApplicationsByVolunteer,
} from '../../api/admin'
import type { ApplicationDTO } from '../../api/types'
import { AdminNotice } from './AdminNotice'

export function AdminApplicationsPage() {
  const [applications, setApplications] = useState<ApplicationDTO[]>([])
  const [applicationId, setApplicationId] = useState('')
  const [opportunityId, setOpportunityId] = useState('')
  const [volunteerId, setVolunteerId] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    loadApplications()
  }, [])

  async function loadApplications() {
    setError('')
    try {
      setApplications(await getAdminApplications())
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load applications.')
    }
  }

  async function handleFindApplication(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!applicationId.trim()) {
      await loadApplications()
      return
    }
    setError('')
    try {
      setApplications([await getAdminApplicationById(applicationId.trim())])
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Application not found.')
    }
  }

  async function handleFindOpportunity(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!opportunityId.trim()) {
      await loadApplications()
      return
    }
    setError('')
    try {
      setApplications(await getAdminApplicationsByOpportunity(opportunityId.trim()))
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load opportunity applications.')
    }
  }

  async function handleFindVolunteer(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!volunteerId.trim()) {
      await loadApplications()
      return
    }
    setError('')
    try {
      setApplications(await getAdminApplicationsByVolunteer(volunteerId.trim()))
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load volunteer applications.')
    }
  }

  return (
    <>
      <div className="admin-heading">
        <p className="eyebrow">Application review</p>
        <h2>Applications</h2>
        <p>Inspect applications globally, by opportunity, by volunteer, or by id.</p>
      </div>

      {error ? <AdminNotice tone="error">{error}</AdminNotice> : null}

      <div className="admin-grid-three">
        <form className="admin-panel admin-inline-form" onSubmit={handleFindApplication}>
          <input
            value={applicationId}
            onChange={(event) => setApplicationId(event.target.value)}
            placeholder="Application id"
          />
          <button className="button button--secondary" type="submit">
            Find
          </button>
        </form>
        <form className="admin-panel admin-inline-form" onSubmit={handleFindOpportunity}>
          <input
            value={opportunityId}
            onChange={(event) => setOpportunityId(event.target.value)}
            placeholder="Opportunity id"
          />
          <button className="button button--secondary" type="submit">
            By opportunity
          </button>
        </form>
        <form className="admin-panel admin-inline-form" onSubmit={handleFindVolunteer}>
          <input
            value={volunteerId}
            onChange={(event) => setVolunteerId(event.target.value)}
            placeholder="Volunteer id"
          />
          <button className="button button--secondary" type="submit">
            By volunteer
          </button>
        </form>
      </div>

      <div className="admin-table-wrap">
        <table className="admin-table">
          <thead>
            <tr>
              <th>Opportunity</th>
              <th>Volunteer</th>
              <th>Status</th>
              <th>Applied</th>
              <th>Message</th>
            </tr>
          </thead>
          <tbody>
            {applications.map((application) => (
              <tr key={application.id}>
                <td>
                  <strong>{application.opportunityName}</strong>
                  <small>{application.opportunityId}</small>
                </td>
                <td>
                  {application.volunteerName}
                  <small>{application.volunteerId}</small>
                </td>
                <td>{application.status}</td>
                <td>{formatDate(application.dateApplied)}</td>
                <td>{application.message}</td>
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
