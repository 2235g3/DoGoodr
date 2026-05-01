import { FormEvent, useEffect, useState } from 'react'
import {
  createVolunteerApplication,
  getVolunteerMatches,
  getVolunteerOpportunitiesByOrganisation,
  getVolunteerOpportunityById,
} from '../../api/volunteer'
import type { MatchedOpportunityDTO, OpportunityDTO } from '../../api/types'
import { VolunteerNotice } from './VolunteerNotice'

export function VolunteerMatchesPage() {
  const [matches, setMatches] = useState<MatchedOpportunityDTO[]>([])
  const [directOpportunities, setDirectOpportunities] = useState<OpportunityDTO[]>([])
  const [messages, setMessages] = useState<Record<string, string>>({})
  const [opportunityId, setOpportunityId] = useState('')
  const [organisationId, setOrganisationId] = useState('')
  const [notice, setNotice] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    loadMatches()
  }, [])

  async function loadMatches() {
    setError('')
    try {
      setMatches(await getVolunteerMatches())
    } catch (caughtError) {
      setError(
        caughtError instanceof Error
          ? caughtError.message
          : 'Unable to load matched opportunities.',
      )
    }
  }

  async function handleOpportunitySearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!opportunityId.trim()) return
    setError('')
    setNotice('')
    try {
      setDirectOpportunities([await getVolunteerOpportunityById(opportunityId.trim())])
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to find opportunity.')
    }
  }

  async function handleOrganisationSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!organisationId.trim()) return
    setError('')
    setNotice('')
    try {
      setDirectOpportunities(await getVolunteerOpportunitiesByOrganisation(organisationId.trim()))
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load opportunities.')
    }
  }

  async function handleApply(opportunity: OpportunityDTO) {
    setError('')
    setNotice('')
    try {
      await createVolunteerApplication(opportunity.id, {
        message: messages[opportunity.id]?.trim() || 'I would like to apply for this opportunity.',
      })
      setMessages((current) => ({ ...current, [opportunity.id]: '' }))
      setNotice(`Application sent for ${opportunity.title}.`)
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to apply.')
    }
  }

  function updateMessage(opportunityId: string, value: string) {
    setMessages((current) => ({ ...current, [opportunityId]: value }))
  }

  return (
    <>
      <div className="admin-heading">
        <p className="eyebrow">Opportunity discovery</p>
        <h2>Matches</h2>
        <p>Review recommended opportunities, inspect direct links, and apply from one place.</p>
      </div>

      {notice ? <VolunteerNotice tone="success">{notice}</VolunteerNotice> : null}
      {error ? <VolunteerNotice tone="error">{error}</VolunteerNotice> : null}

      <div className="admin-grid-two">
        <form className="admin-panel admin-inline-form" onSubmit={handleOpportunitySearch}>
          <input
            value={opportunityId}
            onChange={(event) => setOpportunityId(event.target.value)}
            placeholder="Opportunity id"
          />
          <button className="button button--secondary" type="submit">
            Find
          </button>
        </form>
        <form className="admin-panel admin-inline-form" onSubmit={handleOrganisationSearch}>
          <input
            value={organisationId}
            onChange={(event) => setOrganisationId(event.target.value)}
            placeholder="Organisation profile id"
          />
          <button className="button button--secondary" type="submit">
            By organisation
          </button>
        </form>
      </div>

      {directOpportunities.length ? (
        <section className="admin-panel">
          <h3>Direct lookup</h3>
          <div className="volunteer-opportunity-grid">
            {directOpportunities.map((opportunity) => (
              <OpportunityCard
                key={opportunity.id}
                opportunity={opportunity}
                message={messages[opportunity.id] ?? ''}
                onMessageChange={updateMessage}
                onApply={handleApply}
              />
            ))}
          </div>
        </section>
      ) : null}

      <section className="admin-panel">
        <h3>Recommended for you</h3>
        {matches.length ? (
          <div className="volunteer-opportunity-grid">
            {matches.map((match) => (
              <OpportunityCard
                key={match.opportunity.id}
                opportunity={match.opportunity}
                match={match}
                message={messages[match.opportunity.id] ?? ''}
                onMessageChange={updateMessage}
                onApply={handleApply}
              />
            ))}
          </div>
        ) : (
          <VolunteerNotice>
            No matches are available yet. Profile data and backend matching rules decide what appears here.
          </VolunteerNotice>
        )}
      </section>
    </>
  )
}

function OpportunityCard({
  opportunity,
  match,
  message,
  onMessageChange,
  onApply,
}: {
  opportunity: OpportunityDTO
  match?: MatchedOpportunityDTO
  message: string
  onMessageChange: (opportunityId: string, value: string) => void
  onApply: (opportunity: OpportunityDTO) => void
}) {
  return (
    <article className="volunteer-opportunity-card">
      <div className="volunteer-card-topline">
        <span>{opportunity.status}</span>
        {match?.distanceKm != null ? <span>{match.distanceKm.toFixed(1)} km</span> : null}
        {match?.normalizedScore != null ? (
          <span>{Math.round(match.normalizedScore * 100)}% match</span>
        ) : null}
      </div>
      <h4>{opportunity.title}</h4>
      <p>{opportunity.description}</p>
      <dl className="volunteer-facts">
        <div>
          <dt>Organisation</dt>
          <dd>{opportunity.organisationProfile.displayName}</dd>
        </div>
        <div>
          <dt>Location</dt>
          <dd>{opportunity.remote ? 'Remote' : opportunity.location || 'TBC'}</dd>
        </div>
        <div>
          <dt>Starts</dt>
          <dd>{formatDate(opportunity.startDate)}</dd>
        </div>
        <div>
          <dt>Hours</dt>
          <dd>{opportunity.requiredHours ?? 'Flexible'}</dd>
        </div>
      </dl>
      <textarea
        value={message}
        onChange={(event) => onMessageChange(opportunity.id, event.target.value)}
        placeholder="Add a short application message"
        rows={4}
      />
      <button className="button button--primary" type="button" onClick={() => onApply(opportunity)}>
        Apply
      </button>
    </article>
  )
}

function formatDate(value?: string | null) {
  if (!value) return 'TBC'
  return new Date(value).toLocaleDateString()
}
