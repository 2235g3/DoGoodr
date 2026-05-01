import { FormEvent, useEffect, useMemo, useState } from 'react'
import {
  createVolunteerApplication,
  getMyVolunteerLabels,
  getVolunteerMatches,
  getVolunteerOrganisations,
  getVolunteerProfile,
  searchVolunteerOpportunities,
} from '../../api/volunteer'
import type {
  MatchedOpportunityDTO,
  OpportunityDTO,
  OpportunitySearchSort,
  OrganisationProfileDTO,
  VolunteerProfileDTO,
} from '../../api/types'
import { formatAvailability } from '../../utils/availability'
import { calculateVolunteerProfileCompletion } from '../../utils/volunteerProfile'
import { VolunteerNotice } from './VolunteerNotice'

type BrowseFilters = {
  q: string
  remote: 'any' | 'true' | 'false'
  organisationId: string
  maxHours: string
  startsAfter: string
  startsBefore: string
  latitude: string
  longitude: string
  maxDistanceKm: string
  sort: OpportunitySearchSort
}

export function VolunteerMatchesPage() {
  const [profile, setProfile] = useState<VolunteerProfileDTO | null>(null)
  const [organisations, setOrganisations] = useState<OrganisationProfileDTO[]>([])
  const [opportunities, setOpportunities] = useState<OpportunityDTO[]>([])
  const [matches, setMatches] = useState<MatchedOpportunityDTO[]>([])
  const [filters, setFilters] = useState<BrowseFilters>(emptyFilters)
  const [messages, setMessages] = useState<Record<string, string>>({})
  const [notice, setNotice] = useState('')
  const [error, setError] = useState('')
  const [isLoading, setIsLoading] = useState(true)
  const [isGeneratingMatches, setIsGeneratingMatches] = useState(false)
  const [isLocating, setIsLocating] = useState(false)
  const [canUseMatches, setCanUseMatches] = useState(false)

  const mappedOpportunities = useMemo(
    () => opportunities.filter((opportunity) => opportunity.latitude != null && opportunity.longitude != null),
    [opportunities],
  )

  useEffect(() => {
    let isMounted = true

    Promise.all([
      getVolunteerProfile(),
      getMyVolunteerLabels(),
      getVolunteerOrganisations(),
      searchVolunteerOpportunities({ sort: 'newest' }),
    ])
      .then(async ([nextProfile, labels, nextOrganisations, nextOpportunities]) => {
        if (!isMounted) return
        const completion = calculateVolunteerProfileCompletion(nextProfile, labels)
        setProfile(nextProfile)
        setCanUseMatches(completion.complete)
        setFilters(makeInitialFilters(nextProfile))
        setOrganisations(nextOrganisations)
        setOpportunities(nextOpportunities)

        if (completion.complete) await generateMatches()
      })
      .catch((caughtError) => {
        if (!isMounted) return
        setError(caughtError instanceof Error ? caughtError.message : 'Unable to load discovery.')
      })
      .finally(() => {
        if (isMounted) setIsLoading(false)
      })

    return () => {
      isMounted = false
    }
  }, [])

  async function handleSearch(event?: FormEvent<HTMLFormElement>) {
    event?.preventDefault()
    setError('')
    setNotice('')
    setIsLoading(true)

    try {
      setOpportunities(
        await searchVolunteerOpportunities({
          q: filters.q || undefined,
          remote: filters.remote === 'any' ? undefined : filters.remote === 'true',
          organisationId: filters.organisationId || undefined,
          maxHours: filters.maxHours ? Number(filters.maxHours) : undefined,
          startsAfter: filters.startsAfter || undefined,
          startsBefore: filters.startsBefore || undefined,
          latitude: filters.latitude ? Number(filters.latitude) : undefined,
          longitude: filters.longitude ? Number(filters.longitude) : undefined,
          maxDistanceKm: filters.maxDistanceKm ? Number(filters.maxDistanceKm) : undefined,
          sort: filters.sort,
        }),
      )
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to search opportunities.')
    } finally {
      setIsLoading(false)
    }
  }

  async function generateMatches() {
    setError('')
    setNotice('')
    setIsGeneratingMatches(true)
    try {
      const nextMatches = await getVolunteerMatches()
      setMatches(nextMatches)
      setNotice(
        nextMatches.length
          ? `Generated ${nextMatches.length} matched ${nextMatches.length === 1 ? 'opportunity' : 'opportunities'}.`
          : 'No matched opportunities yet. Try broadening your labels, availability, or travel preferences.',
      )
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to generate matches.')
    } finally {
      setIsGeneratingMatches(false)
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

  function handleUseCurrentLocation() {
    setError('')
    setNotice('')

    if (!navigator.geolocation) {
      setError('Your browser does not support current-location lookup.')
      return
    }

    setIsLocating(true)
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setFilters((current) => ({
          ...current,
          latitude: position.coords.latitude.toFixed(6),
          longitude: position.coords.longitude.toFixed(6),
          sort: 'closest',
        }))
        setNotice('Current location added to search filters.')
        setIsLocating(false)
      },
      () => {
        setError('Unable to access your current location.')
        setIsLocating(false)
      },
      { enableHighAccuracy: true, timeout: 10000 },
    )
  }

  function updateFilter<TField extends keyof BrowseFilters>(field: TField, value: BrowseFilters[TField]) {
    setFilters((current) => ({ ...current, [field]: value }))
  }

  function updateMessage(opportunityId: string, value: string) {
    setMessages((current) => ({ ...current, [opportunityId]: value }))
  }

  return (
    <>
      <div className="admin-heading browse-heading">
        <p className="eyebrow">Opportunity discovery</p>
        <h2>Browse opportunities and organisations.</h2>
        <p>
          Search open roles, compare organisations, use your location for distance-aware results,
          and apply without leaving the page.
        </p>
      </div>

      {notice ? <VolunteerNotice tone="success">{notice}</VolunteerNotice> : null}
      {error ? <VolunteerNotice tone="error">{error}</VolunteerNotice> : null}

      <form className="admin-panel browse-search-panel" onSubmit={handleSearch}>
        <label className="browse-query">
          Search
          <input
            value={filters.q}
            onChange={(event) => updateFilter('q', event.target.value)}
            placeholder="Search by role, cause, organisation, or location"
          />
        </label>
        <label>
          Remote
          <select value={filters.remote} onChange={(event) => updateFilter('remote', event.target.value as BrowseFilters['remote'])}>
            <option value="any">Any</option>
            <option value="true">Remote only</option>
            <option value="false">In person</option>
          </select>
        </label>
        <label>
          Organisation
          <select
            value={filters.organisationId}
            onChange={(event) => updateFilter('organisationId', event.target.value)}
          >
            <option value="">Any organisation</option>
            {organisations.map((organisation) => (
              <option key={organisation.id} value={organisation.id}>
                {organisation.displayName}
              </option>
            ))}
          </select>
        </label>
        <label>
          Sort
          <select value={filters.sort} onChange={(event) => updateFilter('sort', event.target.value as OpportunitySearchSort)}>
            <option value="newest">Newest</option>
            <option value="start-date">Start date</option>
            <option value="closest">Closest</option>
            <option value="hours">Hours</option>
            <option value="organisation">Organisation</option>
          </select>
        </label>
        <div className="browse-filter-row">
          <label>
            Max hours
            <input
              min="1"
              type="number"
              value={filters.maxHours}
              onChange={(event) => updateFilter('maxHours', event.target.value)}
            />
          </label>
          <label>
            Starts after
            <input
              type="date"
              value={filters.startsAfter}
              onChange={(event) => updateFilter('startsAfter', event.target.value)}
            />
          </label>
          <label>
            Starts before
            <input
              type="date"
              value={filters.startsBefore}
              onChange={(event) => updateFilter('startsBefore', event.target.value)}
            />
          </label>
          <label>
            Distance km
            <input
              min="1"
              type="number"
              value={filters.maxDistanceKm}
              onChange={(event) => updateFilter('maxDistanceKm', event.target.value)}
            />
          </label>
        </div>
        <div className="browse-location-row">
          <label>
            Latitude
            <input
              step="any"
              type="number"
              value={filters.latitude}
              onChange={(event) => updateFilter('latitude', event.target.value)}
            />
          </label>
          <label>
            Longitude
            <input
              step="any"
              type="number"
              value={filters.longitude}
              onChange={(event) => updateFilter('longitude', event.target.value)}
            />
          </label>
          <button className="button button--secondary" type="button" disabled={isLocating} onClick={handleUseCurrentLocation}>
            {isLocating ? 'Finding...' : 'Use current location'}
          </button>
          <button className="button button--primary" type="submit" disabled={isLoading}>
            {isLoading ? 'Searching...' : 'Search'}
          </button>
        </div>
      </form>

      {!canUseMatches ? (
        <section className="admin-panel browse-unlock-card">
          <div>
            <p className="eyebrow">Matching locked</p>
            <h3>Browse is open now. Matching unlocks when your profile is complete.</h3>
            <p>
              Complete your profile details and labels to turn this search page into a
              personalised recommendations feed.
            </p>
          </div>
          <a className="button button--primary" href="/volunteer/onboarding">
            Complete profile
          </a>
        </section>
      ) : (
        <section className="admin-panel">
          <div className="browse-match-heading">
            <div>
              <p className="eyebrow">Matching service</p>
              <h3>Recommended for you</h3>
              <p>
                Generate a ranked feed using your labels, availability, remote preference, age, and
                travel distance.
              </p>
            </div>
            <button
              className="button button--primary"
              type="button"
              disabled={isGeneratingMatches}
              onClick={generateMatches}
            >
              {isGeneratingMatches ? 'Generating...' : 'Generate matches'}
            </button>
          </div>
          {matches.length ? (
            <div className="browse-match-strip">
              {matches.slice(0, 4).map((match) => (
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
              No generated matches are showing yet. Opportunities need matching labels and at least
              one compatible availability slot to score strongly.
            </VolunteerNotice>
          )}
        </section>
      )}

      <div className="browse-results-layout">
        <section className="browse-results-column">
          <div className="browse-results-summary">
            <strong>{opportunities.length}</strong>
            <span>{opportunities.length === 1 ? 'open opportunity' : 'open opportunities'}</span>
          </div>
          <div className="volunteer-opportunity-grid browse-opportunity-grid">
            {opportunities.map((opportunity) => (
              <OpportunityCard
                key={opportunity.id}
                opportunity={opportunity}
                distanceKm={distanceFromFilters(filters, opportunity)}
                message={messages[opportunity.id] ?? ''}
                onMessageChange={updateMessage}
                onApply={handleApply}
              />
            ))}
          </div>
          {!opportunities.length && !isLoading ? (
            <VolunteerNotice>No opportunities match those filters yet.</VolunteerNotice>
          ) : null}
        </section>

        <aside className="admin-panel browse-map-panel">
          <h3>Map view</h3>
          <MapFrame opportunities={mappedOpportunities} profile={profile} filters={filters} />
          <div className="browse-map-list">
            {mappedOpportunities.slice(0, 6).map((opportunity) => (
              <a
                key={opportunity.id}
                href={makeOsmMarkerUrl(opportunity.latitude, opportunity.longitude)}
                target="_blank"
                rel="noreferrer"
              >
                {opportunity.title}
                <span>{opportunity.location || opportunity.organisationProfile.displayName}</span>
              </a>
            ))}
          </div>
        </aside>
      </div>

      <section className="admin-panel">
        <h3>Registered organisations</h3>
        <div className="browse-organisation-grid">
          {organisations.map((organisation) => (
            <article className="browse-organisation-card" key={organisation.id}>
              <span>{organisation.verified ? 'Verified' : 'Awaiting verification'}</span>
              <h4>{organisation.displayName}</h4>
              <p>{organisation.description || 'No organisation description yet.'}</p>
              <small>{organisation.location || 'Location TBC'}</small>
              <button
                className="button button--secondary"
                type="button"
                onClick={() => {
                  updateFilter('organisationId', organisation.id)
                  setNotice(`Filter set to ${organisation.displayName}.`)
                }}
              >
                View opportunities
              </button>
            </article>
          ))}
        </div>
      </section>
    </>
  )
}

function OpportunityCard({
  opportunity,
  match,
  distanceKm,
  message,
  onMessageChange,
  onApply,
}: {
  opportunity: OpportunityDTO
  match?: MatchedOpportunityDTO
  distanceKm?: number | null
  message: string
  onMessageChange: (opportunityId: string, value: string) => void
  onApply: (opportunity: OpportunityDTO) => void
}) {
  return (
    <article className="volunteer-opportunity-card browse-opportunity-card">
      <div className="volunteer-card-topline">
        <span>{opportunity.remote ? 'Remote' : 'In person'}</span>
        {distanceKm != null ? <span>{distanceKm.toFixed(1)} km</span> : null}
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
        <div>
          <dt>Availability</dt>
          <dd>{formatAvailability(opportunity.availability)}</dd>
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

function MapFrame({
  opportunities,
  profile,
  filters,
}: {
  opportunities: OpportunityDTO[]
  profile: VolunteerProfileDTO | null
  filters: BrowseFilters
}) {
  const firstOpportunity = opportunities[0]
  const latitude = firstOpportunity?.latitude ?? numberOrNull(filters.latitude) ?? profile?.latitude ?? 51.5072
  const longitude = firstOpportunity?.longitude ?? numberOrNull(filters.longitude) ?? profile?.longitude ?? -0.1276
  const source = `https://www.openstreetmap.org/export/embed.html?bbox=${longitude - 0.08}%2C${latitude - 0.05}%2C${longitude + 0.08}%2C${latitude + 0.05}&layer=mapnik&marker=${latitude}%2C${longitude}`

  return (
    <iframe
      title="Opportunity map"
      src={source}
      loading="lazy"
      referrerPolicy="no-referrer-when-downgrade"
    />
  )
}

const emptyFilters: BrowseFilters = {
  q: '',
  remote: 'any',
  organisationId: '',
  maxHours: '',
  startsAfter: '',
  startsBefore: '',
  latitude: '',
  longitude: '',
  maxDistanceKm: '',
  sort: 'newest',
}

function makeInitialFilters(profile: VolunteerProfileDTO): BrowseFilters {
  return {
    ...emptyFilters,
    remote: profile.remoteOnly ? 'true' : 'any',
    latitude: profile.latitude?.toString() ?? '',
    longitude: profile.longitude?.toString() ?? '',
    maxDistanceKm: profile.maxTravelDistance?.toString() ?? '',
    sort: profile.latitude != null && profile.longitude != null ? 'closest' : 'newest',
  }
}

function distanceFromFilters(filters: BrowseFilters, opportunity: OpportunityDTO) {
  const latitude = numberOrNull(filters.latitude)
  const longitude = numberOrNull(filters.longitude)
  if (latitude == null || longitude == null || opportunity.latitude == null || opportunity.longitude == null) {
    return null
  }

  const earthRadiusKm = 6371
  const latitudeDelta = toRadians(opportunity.latitude - latitude)
  const longitudeDelta = toRadians(opportunity.longitude - longitude)
  const startLatitude = toRadians(latitude)
  const endLatitude = toRadians(opportunity.latitude)
  const a =
    Math.sin(latitudeDelta / 2) * Math.sin(latitudeDelta / 2) +
    Math.cos(startLatitude) *
      Math.cos(endLatitude) *
      Math.sin(longitudeDelta / 2) *
      Math.sin(longitudeDelta / 2)

  return earthRadiusKm * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
}

function toRadians(value: number) {
  return (value * Math.PI) / 180
}

function numberOrNull(value?: string | null) {
  if (!value) return null
  const parsed = Number(value)
  return Number.isFinite(parsed) ? parsed : null
}

function makeOsmMarkerUrl(latitude?: number | null, longitude?: number | null) {
  if (latitude == null || longitude == null) return 'https://www.openstreetmap.org'
  return `https://www.openstreetmap.org/?mlat=${latitude}&mlon=${longitude}#map=14/${latitude}/${longitude}`
}

function formatDate(value?: string | null) {
  if (!value) return 'TBC'
  return new Date(value).toLocaleDateString()
}
