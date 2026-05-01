import { FormEvent, useEffect, useMemo, useState } from 'react'
import { Link, useNavigate, useSearchParams } from 'react-router-dom'
import {
  getLabels,
  getMyVolunteerLabels,
  getVolunteerProfile,
  setMyVolunteerLabels,
  updateVolunteerProfile,
} from '../../api/volunteer'
import type { AssignedLabelDTO, LabelDTO, VolunteerProfileDTO } from '../../api/types'
import { calculateVolunteerProfileCompletion } from '../../utils/volunteerProfile'
import { VolunteerNotice } from './VolunteerNotice'

type OnboardingForm = {
  contactEmail: string
  location: string
  profileDescription: string
  availability: string
  longitude: string
  latitude: string
  maxTravelDistance: string
  remoteOnly: boolean
}

export function VolunteerOnboardingPage() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const isFirstRun = searchParams.get('firstRun') === '1'
  const [profile, setProfile] = useState<VolunteerProfileDTO | null>(null)
  const [form, setForm] = useState<OnboardingForm>(emptyForm)
  const [labels, setLabels] = useState<LabelDTO[]>([])
  const [assignedLabels, setAssignedLabels] = useState<AssignedLabelDTO[]>([])
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [isSaving, setIsSaving] = useState(false)
  const [isLocating, setIsLocating] = useState(false)

  const completion = useMemo(
    () => (profile ? calculateVolunteerProfileCompletion(profile, assignedLabels) : null),
    [profile, assignedLabels],
  )

  useEffect(() => {
    let isMounted = true

    Promise.all([getVolunteerProfile(), getLabels(), getMyVolunteerLabels()])
      .then(([nextProfile, nextLabels, nextAssignedLabels]) => {
        if (!isMounted) return
        setProfile(nextProfile)
        setForm(makeForm(nextProfile))
        setLabels(nextLabels)
        setAssignedLabels(nextAssignedLabels)
      })
      .catch((caughtError) => {
        if (!isMounted) return
        setError(caughtError instanceof Error ? caughtError.message : 'Unable to load onboarding.')
      })

    return () => {
      isMounted = false
    }
  }, [])

  async function handleSave(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setIsSaving(true)
    setError('')
    setMessage('')

    try {
      const [nextProfile, nextLabels] = await Promise.all([
        updateVolunteerProfile({
          contactEmail: form.contactEmail || null,
          location: form.location || null,
          profileDescription: form.profileDescription || null,
          availability: form.availability || null,
          longitude: form.longitude ? Number(form.longitude) : null,
          latitude: form.latitude ? Number(form.latitude) : null,
          maxTravelDistance: form.maxTravelDistance ? Number(form.maxTravelDistance) : null,
          remoteOnly: form.remoteOnly,
        }),
        setMyVolunteerLabels(assignedLabels),
      ])
      setProfile(nextProfile)
      setForm(makeForm(nextProfile))
      setAssignedLabels(nextLabels)
      setMessage('Profile preferences saved.')
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to save onboarding.')
    } finally {
      setIsSaving(false)
    }
  }

  function handleSkip() {
    navigate('/volunteer', { replace: isFirstRun })
  }

  function handleUseCurrentLocation() {
    setError('')
    setMessage('')

    if (!navigator.geolocation) {
      setError('Your browser does not support current-location lookup.')
      return
    }

    setIsLocating(true)
    navigator.geolocation.getCurrentPosition(
      (position) => {
        setForm((current) => ({
          ...current,
          latitude: position.coords.latitude.toFixed(6),
          longitude: position.coords.longitude.toFixed(6),
        }))
        setMessage('Current location added. Save your profile to keep it.')
        setIsLocating(false)
      },
      () => {
        setError('Unable to access your current location.')
        setIsLocating(false)
      },
      { enableHighAccuracy: true, timeout: 10000 },
    )
  }

  function toggleLabel(label: LabelDTO) {
    setAssignedLabels((current) => {
      if (current.some((assignedLabel) => assignedLabel.labelId === label.id)) {
        return current.filter((assignedLabel) => assignedLabel.labelId !== label.id)
      }
      return [...current, { labelId: label.id, weight: 1 }]
    })
  }

  function updateField<TField extends keyof OnboardingForm>(field: TField, value: OnboardingForm[TField]) {
    setForm((current) => ({ ...current, [field]: value }))
  }

  const groupedLabels = groupLabels(labels)

  return (
    <>
      <div className="admin-heading onboarding-heading">
        <p className="eyebrow">{isFirstRun ? 'Welcome to DoGoodr' : 'Profile setup'}</p>
        <h2>Build your volunteer profile.</h2>
        <p>
          A complete profile helps organisations understand you and unlocks better opportunity
          recommendations.
        </p>
      </div>

      {message ? <VolunteerNotice tone="success">{message}</VolunteerNotice> : null}
      {error ? <VolunteerNotice tone="error">{error}</VolunteerNotice> : null}

      <section className="admin-panel onboarding-progress-panel">
        <div>
          <span>Profile completion</span>
          <strong>{completion ? `${completion.percent}%` : '...'}</strong>
        </div>
        <progress value={completion?.percent ?? 0} max={100} />
        {completion && !completion.complete ? (
          <p>Still missing: {completion.missing.join(', ')}.</p>
        ) : (
          <p>Your matching profile is ready.</p>
        )}
      </section>

      <form className="onboarding-grid" onSubmit={handleSave}>
        <section className="admin-panel admin-form">
          <h3>How organisations should contact and understand you</h3>
          <label>
            Contact email
            <input
              type="email"
              value={form.contactEmail}
              onChange={(event) => updateField('contactEmail', event.target.value)}
            />
          </label>
          <label>
            About you
            <textarea
              value={form.profileDescription}
              onChange={(event) => updateField('profileDescription', event.target.value)}
              placeholder="A short, useful summary of your interests, skills, and what you want to contribute."
              rows={6}
            />
          </label>
          <label>
            Availability
            <input
              value={form.availability}
              onChange={(event) => updateField('availability', event.target.value)}
              placeholder="Weekends, evenings, one-off events..."
            />
          </label>
        </section>

        <section className="admin-panel admin-form">
          <h3>Location and travel preferences</h3>
          <label>
            Location
            <input
              value={form.location}
              onChange={(event) => updateField('location', event.target.value)}
              placeholder="Town, city, or area"
            />
          </label>
          <div className="admin-grid-two">
            <label>
              Latitude
              <input
                type="number"
                step="any"
                value={form.latitude}
                onChange={(event) => updateField('latitude', event.target.value)}
              />
            </label>
            <label>
              Longitude
              <input
                type="number"
                step="any"
                value={form.longitude}
                onChange={(event) => updateField('longitude', event.target.value)}
              />
            </label>
          </div>
          <button
            className="button button--secondary"
            type="button"
            disabled={isLocating}
            onClick={handleUseCurrentLocation}
          >
            {isLocating ? 'Finding location...' : 'Use current location'}
          </button>
          <label>
            Max travel distance in km
            <input
              min="0"
              type="number"
              value={form.maxTravelDistance}
              onChange={(event) => updateField('maxTravelDistance', event.target.value)}
            />
          </label>
          <label className="volunteer-toggle">
            <input
              type="checkbox"
              checked={form.remoteOnly}
              onChange={(event) => updateField('remoteOnly', event.target.checked)}
            />
            Remote only
          </label>
        </section>

        <section className="admin-panel onboarding-label-panel">
          <h3>Interests, skills, and causes</h3>
          <p>Choose the labels that describe what you can offer or want to support.</p>
          {Object.entries(groupedLabels).map(([type, typeLabels]) => (
            <div className="onboarding-label-group" key={type}>
              <h4>{formatLabelType(type)}</h4>
              <div className="onboarding-chip-grid">
                {typeLabels.map((label) => {
                  const selected = assignedLabels.some((assignedLabel) => assignedLabel.labelId === label.id)
                  return (
                    <button
                      className={selected ? 'onboarding-chip is-selected' : 'onboarding-chip'}
                      key={label.id}
                      type="button"
                      onClick={() => toggleLabel(label)}
                    >
                      {label.name}
                    </button>
                  )
                })}
              </div>
            </div>
          ))}
          {!labels.length ? <VolunteerNotice>No labels have been configured yet.</VolunteerNotice> : null}
        </section>

        <section className="onboarding-actions">
          <button className="button button--primary" type="submit" disabled={isSaving}>
            {isSaving ? 'Saving...' : 'Save and continue'}
          </button>
          <button className="button button--secondary" type="button" onClick={handleSkip}>
            Skip for now
          </button>
          <Link className="button button--secondary" to="/volunteer/profile">
            Edit full profile
          </Link>
        </section>
      </form>
    </>
  )
}

const emptyForm: OnboardingForm = {
  contactEmail: '',
  location: '',
  profileDescription: '',
  availability: '',
  longitude: '',
  latitude: '',
  maxTravelDistance: '',
  remoteOnly: false,
}

function makeForm(profile: VolunteerProfileDTO): OnboardingForm {
  return {
    contactEmail: profile.contactEmail ?? '',
    location: profile.location ?? '',
    profileDescription: profile.profileDescription ?? '',
    availability: profile.availability ?? '',
    longitude: profile.longitude?.toString() ?? '',
    latitude: profile.latitude?.toString() ?? '',
    maxTravelDistance: profile.maxTravelDistance?.toString() ?? '',
    remoteOnly: profile.remoteOnly,
  }
}

function groupLabels(labels: LabelDTO[]) {
  return labels.reduce<Record<string, LabelDTO[]>>((groups, label) => {
    groups[label.type] = [...(groups[label.type] ?? []), label]
    return groups
  }, {})
}

function formatLabelType(value: string) {
  return value
    .toLowerCase()
    .split('_')
    .map((part) => `${part.slice(0, 1).toUpperCase()}${part.slice(1)}`)
    .join(' ')
}
