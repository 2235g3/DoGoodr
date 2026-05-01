import { FormEvent, useState } from 'react'
import {
  getAdminVolunteerHistoryByOpportunityAndOrganisation,
  getAdminVolunteerHistoryByVolunteer,
} from '../../api/admin'
import type { VolunteerHistoryDTO } from '../../api/types'
import { AdminNotice } from './AdminNotice'

export function AdminHistoryPage() {
  const [history, setHistory] = useState<VolunteerHistoryDTO[]>([])
  const [volunteerId, setVolunteerId] = useState('')
  const [opportunityId, setOpportunityId] = useState('')
  const [organisationId, setOrganisationId] = useState('')
  const [error, setError] = useState('')

  async function handleVolunteerLookup(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    try {
      setHistory(await getAdminVolunteerHistoryByVolunteer(volunteerId.trim()))
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load volunteer history.')
    }
  }

  async function handleOpportunityLookup(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    try {
      setHistory(
        await getAdminVolunteerHistoryByOpportunityAndOrganisation(
          opportunityId.trim(),
          organisationId.trim(),
        ),
      )
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load opportunity history.')
    }
  }

  return (
    <>
      <div className="admin-heading">
        <p className="eyebrow">Volunteer history</p>
        <h2>History lookup</h2>
        <p>Retrieve volunteer history by volunteer profile or by opportunity and organisation.</p>
      </div>

      {error ? <AdminNotice tone="error">{error}</AdminNotice> : null}

      <div className="admin-grid-two">
        <form className="admin-panel admin-form" onSubmit={handleVolunteerLookup}>
          <h3>By volunteer</h3>
          <input
            value={volunteerId}
            onChange={(event) => setVolunteerId(event.target.value)}
            placeholder="Volunteer profile id"
            required
          />
          <button className="button button--secondary" type="submit">
            Load history
          </button>
        </form>
        <form className="admin-panel admin-form" onSubmit={handleOpportunityLookup}>
          <h3>By opportunity and organisation</h3>
          <input
            value={opportunityId}
            onChange={(event) => setOpportunityId(event.target.value)}
            placeholder="Opportunity id"
            required
          />
          <input
            value={organisationId}
            onChange={(event) => setOrganisationId(event.target.value)}
            placeholder="Organisation profile id"
            required
          />
          <button className="button button--secondary" type="submit">
            Load history
          </button>
        </form>
      </div>

      <div className="admin-table-wrap">
        <table className="admin-table">
          <thead>
            <tr>
              <th>Volunteer</th>
              <th>Opportunity</th>
              <th>Organisation</th>
              <th>Dates</th>
              <th>Hours</th>
              <th>Points</th>
              <th>Comment</th>
            </tr>
          </thead>
          <tbody>
            {history.map((item) => (
              <tr key={`${item.volunteerId}-${item.opportunityId}-${item.startDate}`}>
                <td>
                  {item.volunteerName}
                  <small>{item.volunteerId}</small>
                </td>
                <td>
                  {item.opportunityTitle}
                  <small>{item.opportunityId}</small>
                </td>
                <td>
                  {item.organisationName}
                  <small>{item.organisationId}</small>
                </td>
                <td>
                  {item.startDate} - {item.endDate}
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
