import { FormEvent, useEffect, useState } from 'react'
import {
  getAdminOpportunities,
  getAdminOpportunitiesByOrganisation,
  getAdminOpportunityById,
} from '../../api/admin'
import type { OpportunityDTO } from '../../api/types'
import { AdminNotice } from './AdminNotice'

export function AdminOpportunitiesPage() {
  const [opportunities, setOpportunities] = useState<OpportunityDTO[]>([])
  const [opportunityId, setOpportunityId] = useState('')
  const [organisationId, setOrganisationId] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    loadOpportunities()
  }, [])

  async function loadOpportunities() {
    setError('')
    try {
      setOpportunities(await getAdminOpportunities())
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load opportunities.')
    }
  }

  async function handleFindOpportunity(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!opportunityId.trim()) {
      await loadOpportunities()
      return
    }
    setError('')
    try {
      setOpportunities([await getAdminOpportunityById(opportunityId.trim())])
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Opportunity not found.')
    }
  }

  async function handleFindByOrganisation(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!organisationId.trim()) {
      await loadOpportunities()
      return
    }
    setError('')
    try {
      setOpportunities(await getAdminOpportunitiesByOrganisation(organisationId.trim()))
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load organisation opportunities.')
    }
  }

  return (
    <>
      <div className="admin-heading">
        <p className="eyebrow">Opportunity oversight</p>
        <h2>Opportunities</h2>
        <p>Review opportunity records across the platform.</p>
      </div>

      {error ? <AdminNotice tone="error">{error}</AdminNotice> : null}

      <div className="admin-grid-two">
        <form className="admin-panel admin-inline-form" onSubmit={handleFindOpportunity}>
          <input
            value={opportunityId}
            onChange={(event) => setOpportunityId(event.target.value)}
            placeholder="Opportunity id"
          />
          <button className="button button--secondary" type="submit">
            Find opportunity
          </button>
        </form>
        <form className="admin-panel admin-inline-form" onSubmit={handleFindByOrganisation}>
          <input
            value={organisationId}
            onChange={(event) => setOrganisationId(event.target.value)}
            placeholder="Organisation profile id"
          />
          <button className="button button--secondary" type="submit">
            Filter organisation
          </button>
        </form>
      </div>

      <div className="admin-table-wrap">
        <table className="admin-table">
          <thead>
            <tr>
              <th>Title</th>
              <th>Status</th>
              <th>Organisation</th>
              <th>Dates</th>
              <th>Mode</th>
              <th>Capacity</th>
            </tr>
          </thead>
          <tbody>
            {opportunities.map((opportunity) => (
              <tr key={opportunity.id}>
                <td>
                  <strong>{opportunity.title}</strong>
                  <small>{opportunity.id}</small>
                </td>
                <td>{opportunity.status}</td>
                <td>{opportunity.organisationProfile?.displayName ?? '...'}</td>
                <td>
                  {opportunity.startDate}
                  {opportunity.endDate ? ` - ${opportunity.endDate}` : ''}
                </td>
                <td>{opportunity.remote ? 'Remote' : opportunity.location || 'In person'}</td>
                <td>{opportunity.capacity ?? '...'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  )
}
