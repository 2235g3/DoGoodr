import { FormEvent, useEffect, useState } from 'react'
import {
  deleteAdminOrganisationProfile,
  deleteAdminVolunteerProfile,
  getAdminOrganisationProfileById,
  getAdminOrganisationProfiles,
  getAdminVolunteerProfileById,
  getAdminVolunteerProfiles,
  verifyAdminOrganisationProfile,
} from '../../api/admin'
import type { OrganisationProfileDTO, VolunteerProfileDTO } from '../../api/types'
import { AdminNotice } from './AdminNotice'

export function AdminProfilesPage() {
  const [volunteers, setVolunteers] = useState<VolunteerProfileDTO[]>([])
  const [organisations, setOrganisations] = useState<OrganisationProfileDTO[]>([])
  const [volunteerSearch, setVolunteerSearch] = useState('')
  const [organisationSearch, setOrganisationSearch] = useState('')
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    loadProfiles()
  }, [])

  async function loadProfiles() {
    setError('')
    try {
      const [nextVolunteers, nextOrganisations] = await Promise.all([
        getAdminVolunteerProfiles(),
        getAdminOrganisationProfiles(),
      ])
      setVolunteers(nextVolunteers)
      setOrganisations(nextOrganisations)
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load profiles.')
    }
  }

  async function handleVolunteerSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!volunteerSearch.trim()) {
      await loadProfiles()
      return
    }
    setError('')
    try {
      setVolunteers([await getAdminVolunteerProfileById(volunteerSearch.trim())])
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Volunteer profile not found.')
    }
  }

  async function handleOrganisationSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!organisationSearch.trim()) {
      await loadProfiles()
      return
    }
    setError('')
    try {
      setOrganisations([await getAdminOrganisationProfileById(organisationSearch.trim())])
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Organisation profile not found.')
    }
  }

  async function handleVerifyOrganisation(id: string) {
    setMessage('')
    setError('')
    try {
      await verifyAdminOrganisationProfile(id)
      setMessage('Organisation verified.')
      await loadProfiles()
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to verify organisation.')
    }
  }

  async function handleDeleteVolunteer(id: string) {
    setMessage('')
    setError('')
    try {
      await deleteAdminVolunteerProfile(id)
      setMessage('Volunteer profile deleted.')
      await loadProfiles()
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to delete volunteer profile.')
    }
  }

  async function handleDeleteOrganisation(id: string) {
    setMessage('')
    setError('')
    try {
      await deleteAdminOrganisationProfile(id)
      setMessage('Organisation profile deleted.')
      await loadProfiles()
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to delete organisation profile.')
    }
  }

  return (
    <>
      <div className="admin-heading">
        <p className="eyebrow">Profile management</p>
        <h2>Profiles</h2>
        <p>Inspect volunteer profiles and verify or remove organisation profiles.</p>
      </div>

      {message ? <AdminNotice tone="success">{message}</AdminNotice> : null}
      {error ? <AdminNotice tone="error">{error}</AdminNotice> : null}

      <div className="admin-grid-two">
        <form className="admin-panel admin-inline-form" onSubmit={handleVolunteerSearch}>
          <input
            value={volunteerSearch}
            onChange={(event) => setVolunteerSearch(event.target.value)}
            placeholder="Volunteer profile id"
          />
          <button className="button button--secondary" type="submit">
            Find volunteer
          </button>
        </form>
        <form className="admin-panel admin-inline-form" onSubmit={handleOrganisationSearch}>
          <input
            value={organisationSearch}
            onChange={(event) => setOrganisationSearch(event.target.value)}
            placeholder="Organisation profile id"
          />
          <button className="button button--secondary" type="submit">
            Find organisation
          </button>
        </form>
      </div>

      <section className="admin-panel">
        <h3>Volunteer profiles</h3>
        <div className="admin-card-grid">
          {volunteers.map((profile) => (
            <article className="admin-record-card" key={profile.id}>
              <strong>{profile.preferredName || profile.forename}</strong>
              <p>{[profile.forename, profile.surname].filter(Boolean).join(' ')}</p>
              <small>{profile.id}</small>
              <dl>
                <div>
                  <dt>Location</dt>
                  <dd>{profile.location || '...'}</dd>
                </div>
                <div>
                  <dt>Hours</dt>
                  <dd>{profile.totalHours ?? 0}</dd>
                </div>
                <div>
                  <dt>Points</dt>
                  <dd>{profile.pointsBalance ?? 0}</dd>
                </div>
              </dl>
              <button type="button" onClick={() => handleDeleteVolunteer(profile.id)}>
                Delete profile
              </button>
            </article>
          ))}
        </div>
      </section>

      <section className="admin-panel">
        <h3>Organisation profiles</h3>
        <div className="admin-card-grid">
          {organisations.map((profile) => (
            <article className="admin-record-card" key={profile.id}>
              <strong>{profile.displayName}</strong>
              <p>{profile.description || 'No description provided.'}</p>
              <small>{profile.id}</small>
              <dl>
                <div>
                  <dt>Status</dt>
                  <dd>{profile.verified ? 'Verified' : 'Unverified'}</dd>
                </div>
                <div>
                  <dt>Type</dt>
                  <dd>{profile.accountType?.replace('_', ' ') || '...'}</dd>
                </div>
                <div>
                  <dt>Location</dt>
                  <dd>{profile.location || '...'}</dd>
                </div>
                <div>
                  <dt>Contact</dt>
                  <dd>{profile.contactEmail || '...'}</dd>
                </div>
              </dl>
              <div className="admin-row-actions">
                <button type="button" onClick={() => handleVerifyOrganisation(profile.id)}>
                  Verify
                </button>
                <button type="button" onClick={() => handleDeleteOrganisation(profile.id)}>
                  Delete
                </button>
              </div>
            </article>
          ))}
        </div>
      </section>
    </>
  )
}
