import { FormEvent, useEffect, useState } from 'react'
import { API_BASE_URL } from '../../api/client'
import {
  deleteVolunteerCv,
  deleteVolunteerProfilePicture,
  getVolunteerProfile,
  updateVolunteerProfile,
  uploadVolunteerCv,
  uploadVolunteerProfilePicture,
} from '../../api/volunteer'
import type { VolunteerProfileDTO } from '../../api/types'
import { VolunteerNotice } from './VolunteerNotice'

type ProfileForm = {
  forename: string
  surname: string
  preferredName: string
  contactEmail: string
  location: string
  profileDescription: string
  maxTravelDistance: string
  availability: string
}

export function VolunteerProfilePage() {
  const [profile, setProfile] = useState<VolunteerProfileDTO | null>(null)
  const [form, setForm] = useState<ProfileForm>(emptyForm)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    loadProfile()
  }, [])

  async function loadProfile() {
    setError('')
    try {
      const nextProfile = await getVolunteerProfile()
      setProfile(nextProfile)
      setForm(makeForm(nextProfile))
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load profile.')
    }
  }

  async function handleSave(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setMessage('')
    try {
      const nextProfile = await updateVolunteerProfile({
        forename: form.forename || null,
        surname: form.surname || null,
        preferredName: form.preferredName || null,
        contactEmail: form.contactEmail || null,
        location: form.location || null,
        profileDescription: form.profileDescription || null,
        maxTravelDistance: form.maxTravelDistance ? Number(form.maxTravelDistance) : null,
        availability: form.availability || null,
      })
      setProfile(nextProfile)
      setForm(makeForm(nextProfile))
      setMessage('Profile updated.')
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to update profile.')
    }
  }

  async function handleFileUpload(
    file: File | undefined,
    upload: (file: File) => Promise<VolunteerProfileDTO>,
    successMessage: string,
  ) {
    if (!file) return
    setError('')
    setMessage('')
    try {
      const nextProfile = await upload(file)
      setProfile(nextProfile)
      setMessage(successMessage)
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to upload file.')
    }
  }

  async function handleDelete(deleteAction: () => Promise<VolunteerProfileDTO>, successMessage: string) {
    setError('')
    setMessage('')
    try {
      const nextProfile = await deleteAction()
      setProfile(nextProfile)
      setMessage(successMessage)
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to delete file.')
    }
  }

  function updateField(field: keyof ProfileForm, value: string) {
    setForm((current) => ({ ...current, [field]: value }))
  }

  return (
    <>
      <div className="admin-heading">
        <p className="eyebrow">Profile</p>
        <h2>Your volunteer profile</h2>
        <p>Keep your public profile, contact preferences, CV, and availability up to date.</p>
      </div>

      {message ? <VolunteerNotice tone="success">{message}</VolunteerNotice> : null}
      {error ? <VolunteerNotice tone="error">{error}</VolunteerNotice> : null}

      <div className="admin-grid-two">
        <form className="admin-panel admin-form" onSubmit={handleSave}>
          <h3>Profile details</h3>
          <label>
            Forename
            <input value={form.forename} onChange={(event) => updateField('forename', event.target.value)} />
          </label>
          <label>
            Surname
            <input value={form.surname} onChange={(event) => updateField('surname', event.target.value)} />
          </label>
          <label>
            Preferred name
            <input
              value={form.preferredName}
              onChange={(event) => updateField('preferredName', event.target.value)}
            />
          </label>
          <label>
            Contact email
            <input
              type="email"
              value={form.contactEmail}
              onChange={(event) => updateField('contactEmail', event.target.value)}
            />
          </label>
          <label>
            Location
            <input value={form.location} onChange={(event) => updateField('location', event.target.value)} />
          </label>
          <label>
            Max travel distance
            <input
              min="0"
              type="number"
              value={form.maxTravelDistance}
              onChange={(event) => updateField('maxTravelDistance', event.target.value)}
            />
          </label>
          <label>
            Availability
            <input
              value={form.availability}
              onChange={(event) => updateField('availability', event.target.value)}
            />
          </label>
          <label>
            Profile description
            <textarea
              value={form.profileDescription}
              onChange={(event) => updateField('profileDescription', event.target.value)}
              rows={5}
            />
          </label>
          <button className="button button--primary" type="submit">
            Save profile
          </button>
        </form>

        <section className="admin-panel admin-form">
          <h3>Files and matching data</h3>
          <div className="volunteer-profile-card">
            {profile?.profilePictureUrl ? (
              <img src={resolveMediaUrl(profile.profilePictureUrl)} alt="" />
            ) : (
              <div className="volunteer-avatar-fallback">{profile?.preferredName?.slice(0, 1) ?? 'D'}</div>
            )}
            <div>
              <strong>{profile?.preferredName || profile?.forename || 'Volunteer'}</strong>
              <p>{profile?.profileDescription || 'No profile description yet.'}</p>
            </div>
          </div>

          <label>
            Profile picture
            <input
              accept="image/jpeg,image/png,image/webp"
              type="file"
              onChange={(event) =>
                handleFileUpload(
                  event.target.files?.[0],
                  uploadVolunteerProfilePicture,
                  'Profile picture uploaded.',
                )
              }
            />
          </label>
          <button
            className="button button--secondary"
            type="button"
            onClick={() => handleDelete(deleteVolunteerProfilePicture, 'Profile picture removed.')}
          >
            Remove profile picture
          </button>

          <label>
            CV
            <input
              accept="application/pdf"
              type="file"
              onChange={(event) =>
                handleFileUpload(event.target.files?.[0], uploadVolunteerCv, 'CV uploaded.')
              }
            />
          </label>
          {profile?.cvUrl ? (
            <a
              className="volunteer-file-link"
              href={resolveMediaUrl(profile.cvUrl)}
              target="_blank"
              rel="noreferrer"
            >
              View current CV
            </a>
          ) : null}
          <button
            className="button button--secondary"
            type="button"
            onClick={() => handleDelete(deleteVolunteerCv, 'CV removed.')}
          >
            Remove CV
          </button>

          <dl className="volunteer-facts">
            <div>
              <dt>Remote only</dt>
              <dd>{profile?.remoteOnly ? 'Yes' : 'No'}</dd>
            </div>
            <div>
              <dt>Total hours</dt>
              <dd>{profile?.totalHours ?? 0}</dd>
            </div>
            <div>
              <dt>Points</dt>
              <dd>{profile?.pointsBalance ?? 0}</dd>
            </div>
          </dl>
        </section>
      </div>
    </>
  )
}

const emptyForm: ProfileForm = {
  forename: '',
  surname: '',
  preferredName: '',
  contactEmail: '',
  location: '',
  profileDescription: '',
  maxTravelDistance: '',
  availability: '',
}

function makeForm(profile: VolunteerProfileDTO): ProfileForm {
  return {
    forename: profile.forename ?? '',
    surname: profile.surname ?? '',
    preferredName: profile.preferredName ?? '',
    contactEmail: profile.contactEmail ?? '',
    location: profile.location ?? '',
    profileDescription: profile.profileDescription ?? '',
    maxTravelDistance: profile.maxTravelDistance?.toString() ?? '',
    availability: profile.availability ?? '',
  }
}

function resolveMediaUrl(value: string) {
  if (value.startsWith('http://') || value.startsWith('https://')) {
    return value
  }

  return `${API_BASE_URL}${value.startsWith('/') ? value : `/${value}`}`
}
