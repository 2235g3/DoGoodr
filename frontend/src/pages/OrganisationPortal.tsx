import { ChangeEvent, FormEvent, ReactNode, useEffect, useMemo, useState } from 'react'
import { Link, NavLink, useNavigate, useParams } from 'react-router-dom'
import { ApiError } from '../api/client'
import { clearAuthSession, getAccessToken, getStoredUser } from '../api/auth'
import {
  createOpportunity,
  createVolunteerHistory,
  deleteOpportunity,
  getNotifications,
  getOpportunity,
  getOpportunityHistory,
  getOrganisationApplications,
  getOrganisationOpportunities,
  getOrganisationProfile,
  markNotificationRead,
  updateApplicationStatus,
  updateOpportunity,
  updateOrganisationProfile,
  uploadOrganisationProfilePicture,
} from '../api/organisation'
import type {
  AccountType,
  ApplicationResponseDTO,
  ApplicationStatus,
  CreateOpportunityDTO,
  NotificationResponseDTO,
  OProfileResponseDTO,
  OpportunityResponseDTO,
  OpportunityStatus,
  UpdateOrganisationProfileDTO,
  VolunteerHistoryResponseDTO,
} from '../api/types'
import '../styles/organisation.css'

type LoadState = 'idle' | 'loading' | 'ready' | 'error'

const accountTypes: AccountType[] = [
  'CHARITY',
  'NGO',
  'GOVERNMENT',
  'COMMUNITY_GROUP',
  'PERSONAL',
  'OTHER',
]

const applicationStatuses: ApplicationStatus[] = [
  'APPLIED',
  'UNDER_REVIEW',
  'ACCEPTED',
  'REJECTED',
  'COMPLETED',
  'CANCELLED',
  'WITHDRAWN',
]

function useOrganisationAuth() {
  const token = getAccessToken()
  const user = getStoredUser()
  const isOrganisation = user?.role === 'ORGANISATION'

  return { token, user, isOrganisation }
}

function getErrorMessage(error: unknown) {
  if (error instanceof ApiError) {
    return error.message
  }

  return 'Something went wrong. Please try again.'
}

function formatDate(value?: string | null) {
  if (!value) {
    return 'Not set'
  }

  return new Intl.DateTimeFormat('en-GB', {
    day: '2-digit',
    month: 'short',
    year: 'numeric',
  }).format(new Date(value))
}

function formatDateTime(value?: string | null) {
  if (!value) {
    return 'Not set'
  }

  return new Intl.DateTimeFormat('en-GB', {
    day: '2-digit',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value))
}

function optionalNumber(value: FormDataEntryValue | null) {
  const raw = String(value ?? '').trim()
  return raw === '' ? null : Number(raw)
}

function cleanPayload(payload: CreateOpportunityDTO) {
  return Object.fromEntries(
    Object.entries(payload).filter(([, value]) => value !== '' && value !== null),
  ) as CreateOpportunityDTO
}

function EmptyState({ children }: { children: ReactNode }) {
  return <div className="org-empty">{children}</div>
}

function InlineError({ message }: { message: string }) {
  return (
    <p className="org-alert" role="alert">
      {message}
    </p>
  )
}

function OrganisationShell({
  title,
  eyebrow,
  children,
  action,
}: {
  title: string
  eyebrow: string
  children: ReactNode
  action?: ReactNode
}) {
  const navigate = useNavigate()
  const { token, user, isOrganisation } = useOrganisationAuth()

  function handleLogout() {
    clearAuthSession()
    navigate('/login', { replace: true })
  }

  if (!token || !user) {
    return (
      <main className="org-auth-gate">
        <section>
          <p className="org-kicker">Organisation portal</p>
          <h1>Sign in to continue.</h1>
          <p>Your organisation tools are available after login.</p>
          <Link className="button button--primary" to="/login">
            Login
          </Link>
        </section>
      </main>
    )
  }

  if (!isOrganisation) {
    return (
      <main className="org-auth-gate">
        <section>
          <p className="org-kicker">Organisation portal</p>
          <h1>This area is for organisation accounts.</h1>
          <p>Your current account role is {user.role.toLowerCase()}.</p>
          <button className="button button--secondary" type="button" onClick={handleLogout}>
            Use another account
          </button>
        </section>
      </main>
    )
  }

  return (
    <main className="org-portal">
      <aside className="org-sidebar" aria-label="Organisation navigation">
        <Link className="org-wordmark" to="/organisation">
          DoGoodr
        </Link>
        <nav className="org-nav">
          <NavLink to="/organisation" end>
            Dashboard
          </NavLink>
          <NavLink to="/organisation/profile">Profile</NavLink>
          <NavLink to="/organisation/opportunities">Opportunities</NavLink>
          <NavLink to="/organisation/applications">Applications</NavLink>
          <NavLink to="/organisation/history">Volunteer history</NavLink>
          <NavLink to="/organisation/notifications">Notifications</NavLink>
        </nav>
        <button className="org-logout" type="button" onClick={handleLogout}>
          Logout
        </button>
      </aside>

      <section className="org-main">
        <header className="org-topbar">
          <div>
            <p className="org-kicker">{eyebrow}</p>
            <h1>{title}</h1>
          </div>
          {action ? <div className="org-topbar-action">{action}</div> : null}
        </header>
        {children}
      </section>
    </main>
  )
}

export function OrganisationDashboardPage() {
  const { token } = useOrganisationAuth()
  const [state, setState] = useState<LoadState>('loading')
  const [error, setError] = useState('')
  const [profile, setProfile] = useState<OProfileResponseDTO | null>(null)
  const [opportunities, setOpportunities] = useState<OpportunityResponseDTO[]>([])
  const [applications, setApplications] = useState<ApplicationResponseDTO[]>([])
  const [notifications, setNotifications] = useState<NotificationResponseDTO[]>([])

  useEffect(() => {
    if (!token) {
      return
    }
    const accessToken = token

    async function loadDashboard() {
      try {
        setState('loading')
        const nextProfile = await getOrganisationProfile(accessToken)
        const [nextOpportunities, nextApplications, nextNotifications] = await Promise.all([
          getOrganisationOpportunities(accessToken, nextProfile.id),
          getOrganisationApplications(accessToken),
          getNotifications(accessToken),
        ])

        setProfile(nextProfile)
        setOpportunities(nextOpportunities)
        setApplications(nextApplications)
        setNotifications(nextNotifications)
        setState('ready')
      } catch (caughtError) {
        setError(getErrorMessage(caughtError))
        setState('error')
      }
    }

    void loadDashboard()
  }, [token])

  const openOpportunities = opportunities.filter((item) => item.status === 'OPEN').length
  const pendingApplications = applications.filter((item) =>
    ['APPLIED', 'UNDER_REVIEW'].includes(item.status),
  ).length
  const unreadNotifications = notifications.filter((item) => !item.read).length

  return (
    <OrganisationShell
      eyebrow="Organisation dashboard"
      title={profile?.displayName ?? 'Your organisation'}
      action={
        <Link className="button button--primary" to="/organisation/opportunities/new">
          New opportunity
        </Link>
      }
    >
      {state === 'loading' ? <EmptyState>Loading organisation workspace...</EmptyState> : null}
      {state === 'error' ? <InlineError message={error} /> : null}
      {state === 'ready' ? (
        <>
          <section className="org-metrics" aria-label="Organisation summary">
            <article>
              <span>{opportunities.length}</span>
              <p>Total opportunities</p>
            </article>
            <article>
              <span>{openOpportunities}</span>
              <p>Open opportunities</p>
            </article>
            <article>
              <span>{pendingApplications}</span>
              <p>Applications to review</p>
            </article>
            <article>
              <span>{unreadNotifications}</span>
              <p>Unread notifications</p>
            </article>
          </section>

          <section className="org-grid-two">
            <div className="org-panel">
              <div className="org-panel-head">
                <h2>Recent applications</h2>
                <Link to="/organisation/applications">View all</Link>
              </div>
              {applications.length === 0 ? (
                <EmptyState>No applications yet.</EmptyState>
              ) : (
                <div className="org-list">
                  {applications.slice(0, 5).map((application) => (
                    <article className="org-list-row" key={application.id}>
                      <div>
                        <strong>{application.volunteerName || 'Unnamed volunteer'}</strong>
                        <p>{application.opportunityName}</p>
                      </div>
                      <span className={`org-status org-status--${application.status.toLowerCase()}`}>
                        {application.status.replace('_', ' ')}
                      </span>
                    </article>
                  ))}
                </div>
              )}
            </div>

            <div className="org-panel">
              <div className="org-panel-head">
                <h2>Open opportunities</h2>
                <Link to="/organisation/opportunities">Manage</Link>
              </div>
              {openOpportunities === 0 ? (
                <EmptyState>Create an opportunity to start receiving applications.</EmptyState>
              ) : (
                <div className="org-list">
                  {opportunities
                    .filter((opportunity) => opportunity.status === 'OPEN')
                    .slice(0, 5)
                    .map((opportunity) => (
                      <article className="org-list-row" key={opportunity.id}>
                        <div>
                          <strong>{opportunity.title}</strong>
                          <p>{opportunity.location || 'Remote or flexible'}</p>
                        </div>
                        <span>{formatDate(opportunity.startDate)}</span>
                      </article>
                    ))}
                </div>
              )}
            </div>
          </section>
        </>
      ) : null}
    </OrganisationShell>
  )
}

export function OrganisationProfilePage() {
  const { token } = useOrganisationAuth()
  const [state, setState] = useState<LoadState>('loading')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [profile, setProfile] = useState<OProfileResponseDTO | null>(null)
  const [form, setForm] = useState<UpdateOrganisationProfileDTO>({})
  const [isSaving, setIsSaving] = useState(false)

  useEffect(() => {
    if (!token) {
      return
    }
    const accessToken = token

    async function loadProfile() {
      try {
        setState('loading')
        const nextProfile = await getOrganisationProfile(accessToken)
        setProfile(nextProfile)
        setForm({
          displayName: nextProfile.displayName,
          description: nextProfile.description ?? '',
          contactEmail: nextProfile.contactEmail ?? '',
          location: nextProfile.location ?? '',
          websiteUrl: nextProfile.websiteUrl ?? '',
        })
        setState('ready')
      } catch (caughtError) {
        setError(getErrorMessage(caughtError))
        setState('error')
      }
    }

    void loadProfile()
  }, [token])

  function updateField(field: keyof UpdateOrganisationProfileDTO, value: string) {
    setForm((current) => ({ ...current, [field]: value }))
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!token) {
      return
    }

    try {
      setIsSaving(true)
      setError('')
      setSuccess('')
      const updated = await updateOrganisationProfile(token, form)
      setProfile(updated)
      setSuccess('Profile saved.')
    } catch (caughtError) {
      setError(getErrorMessage(caughtError))
    } finally {
      setIsSaving(false)
    }
  }

  async function handleFileChange(event: ChangeEvent<HTMLInputElement>) {
    if (!token || !event.target.files?.[0]) {
      return
    }

    try {
      setError('')
      setSuccess('')
      const updated = await uploadOrganisationProfilePicture(token, event.target.files[0])
      setProfile(updated)
      setSuccess('Profile picture updated.')
    } catch (caughtError) {
      setError(getErrorMessage(caughtError))
    }
  }

  return (
    <OrganisationShell eyebrow="Profile" title="Organisation profile">
      {state === 'loading' ? <EmptyState>Loading profile...</EmptyState> : null}
      {state === 'error' ? <InlineError message={error} /> : null}
      {state === 'ready' ? (
        <section className="org-grid-two">
          <div className="org-panel">
            <h2>Public details</h2>
            {error ? <InlineError message={error} /> : null}
            {success ? <p className="org-success">{success}</p> : null}
            <form className="org-form" onSubmit={handleSubmit}>
              <label>
                Display name
                <input
                  value={form.displayName ?? ''}
                  onChange={(event) => updateField('displayName', event.target.value)}
                />
              </label>
              <label>
                Account type
                <select
                  value={form.accountType ?? ''}
                  onChange={(event) =>
                    setForm((current) => ({
                      ...current,
                      accountType: event.target.value
                        ? (event.target.value as AccountType)
                        : undefined,
                    }))
                  }
                >
                  <option value="">Keep current type</option>
                  {accountTypes.map((type) => (
                    <option value={type} key={type}>
                      {type.replace('_', ' ')}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Contact email
                <input
                  type="email"
                  value={form.contactEmail ?? ''}
                  onChange={(event) => updateField('contactEmail', event.target.value)}
                />
              </label>
              <label>
                Location
                <input
                  value={form.location ?? ''}
                  onChange={(event) => updateField('location', event.target.value)}
                />
              </label>
              <label>
                Website
                <input
                  type="url"
                  value={form.websiteUrl ?? ''}
                  onChange={(event) => updateField('websiteUrl', event.target.value)}
                />
              </label>
              <label>
                Description
                <textarea
                  value={form.description ?? ''}
                  onChange={(event) => updateField('description', event.target.value)}
                  rows={6}
                />
              </label>
              <button className="button button--primary" type="submit" disabled={isSaving}>
                {isSaving ? 'Saving...' : 'Save profile'}
              </button>
            </form>
          </div>

          <aside className="org-panel">
            <h2>Profile picture</h2>
            {profile?.profilePictureUrl ? (
              <img className="org-avatar-preview" src={profile.profilePictureUrl} alt="" />
            ) : null}
            <label className="org-upload">
              Upload image
              <input type="file" accept="image/png,image/jpeg,image/webp" onChange={handleFileChange} />
            </label>
          </aside>
        </section>
      ) : null}
    </OrganisationShell>
  )
}

export function OrganisationOpportunitiesPage() {
  const { token } = useOrganisationAuth()
  const [state, setState] = useState<LoadState>('loading')
  const [error, setError] = useState('')
  const [opportunities, setOpportunities] = useState<OpportunityResponseDTO[]>([])
  const [statusFilter, setStatusFilter] = useState<'ALL' | OpportunityStatus>('ALL')

  async function loadOpportunities() {
    if (!token) {
      return
    }

    try {
      setState('loading')
      const profile = await getOrganisationProfile(token)
      setOpportunities(await getOrganisationOpportunities(token, profile.id))
      setState('ready')
    } catch (caughtError) {
      setError(getErrorMessage(caughtError))
      setState('error')
    }
  }

  useEffect(() => {
    void loadOpportunities()
  }, [token])

  async function handleDelete(opportunityId: string) {
    if (!token || !window.confirm('Delete this opportunity?')) {
      return
    }

    try {
      await deleteOpportunity(token, opportunityId)
      setOpportunities((current) => current.filter((item) => item.id !== opportunityId))
    } catch (caughtError) {
      setError(getErrorMessage(caughtError))
    }
  }

  const visibleOpportunities = opportunities.filter((opportunity) =>
    statusFilter === 'ALL' ? true : opportunity.status === statusFilter,
  )

  return (
    <OrganisationShell
      eyebrow="Opportunities"
      title="Manage opportunities"
      action={
        <Link className="button button--primary" to="/organisation/opportunities/new">
          New opportunity
        </Link>
      }
    >
      <div className="org-toolbar">
        <label>
          Status
          <select
            value={statusFilter}
            onChange={(event) => setStatusFilter(event.target.value as 'ALL' | OpportunityStatus)}
          >
            <option value="ALL">All</option>
            <option value="OPEN">Open</option>
            <option value="CLOSED">Closed</option>
          </select>
        </label>
      </div>

      {state === 'loading' ? <EmptyState>Loading opportunities...</EmptyState> : null}
      {error ? <InlineError message={error} /> : null}
      {state === 'ready' && visibleOpportunities.length === 0 ? (
        <EmptyState>No opportunities match this view.</EmptyState>
      ) : null}
      {state === 'ready' && visibleOpportunities.length > 0 ? (
        <div className="org-table-wrap">
          <table className="org-table">
            <thead>
              <tr>
                <th>Opportunity</th>
                <th>Status</th>
                <th>Start</th>
                <th>Capacity</th>
                <th>Location</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {visibleOpportunities.map((opportunity) => (
                <tr key={opportunity.id}>
                  <td>
                    <strong>{opportunity.title}</strong>
                    <span>{opportunity.availability || 'Availability not specified'}</span>
                  </td>
                  <td>
                    <span className={`org-status org-status--${opportunity.status.toLowerCase()}`}>
                      {opportunity.status}
                    </span>
                  </td>
                  <td>{formatDate(opportunity.startDate)}</td>
                  <td>{opportunity.capacity ?? 'Flexible'}</td>
                  <td>{opportunity.remote ? 'Remote' : opportunity.location || 'Not set'}</td>
                  <td>
                    <div className="org-row-actions">
                      <Link to={`/organisation/opportunities/${opportunity.id}/edit`}>Edit</Link>
                      <button type="button" onClick={() => void handleDelete(opportunity.id)}>
                        Delete
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      ) : null}
    </OrganisationShell>
  )
}

export function OrganisationOpportunityFormPage({ mode }: { mode: 'create' | 'edit' }) {
  const { token } = useOrganisationAuth()
  const { opportunityId } = useParams()
  const navigate = useNavigate()
  const [state, setState] = useState<LoadState>(mode === 'edit' ? 'loading' : 'ready')
  const [error, setError] = useState('')
  const [isSaving, setIsSaving] = useState(false)
  const [form, setForm] = useState<CreateOpportunityDTO>({
    title: '',
    description: '',
    location: '',
    remote: false,
    minAge: null,
    startDate: '',
    endDate: '',
    recurring: false,
    availability: '',
    requiredHours: null,
    capacity: null,
    status: 'OPEN',
  })

  useEffect(() => {
    if (!token || mode !== 'edit' || !opportunityId) {
      return
    }
    const accessToken = token
    const currentOpportunityId = opportunityId

    async function loadOpportunity() {
      try {
        setState('loading')
        const opportunity = await getOpportunity(accessToken, currentOpportunityId)
        setForm({
          title: opportunity.title,
          description: opportunity.description,
          location: opportunity.location ?? '',
          longitude: opportunity.longitude ?? null,
          latitude: opportunity.latitude ?? null,
          remote: opportunity.remote,
          minAge: opportunity.minAge ?? null,
          startDate: opportunity.startDate,
          endDate: opportunity.endDate ?? '',
          recurring: opportunity.recurring ?? false,
          availability: opportunity.availability ?? '',
          requiredHours: opportunity.requiredHours ?? null,
          capacity: opportunity.capacity ?? null,
          status: opportunity.status,
        })
        setState('ready')
      } catch (caughtError) {
        setError(getErrorMessage(caughtError))
        setState('error')
      }
    }

    void loadOpportunity()
  }, [mode, opportunityId, token])

  function updateField<K extends keyof CreateOpportunityDTO>(
    field: K,
    value: CreateOpportunityDTO[K],
  ) {
    setForm((current) => ({ ...current, [field]: value }))
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!token) {
      return
    }

    const formData = new FormData(event.currentTarget)
    const payload = cleanPayload({
      title: String(formData.get('title') ?? ''),
      description: String(formData.get('description') ?? ''),
      location: String(formData.get('location') ?? ''),
      remote: formData.get('remote') === 'on',
      minAge: optionalNumber(formData.get('minAge')),
      startDate: String(formData.get('startDate') ?? ''),
      endDate: String(formData.get('endDate') ?? ''),
      recurring: formData.get('recurring') === 'on',
      availability: String(formData.get('availability') ?? ''),
      requiredHours: optionalNumber(formData.get('requiredHours')),
      capacity: optionalNumber(formData.get('capacity')),
      status: String(formData.get('status') ?? 'OPEN') as OpportunityStatus,
    })

    try {
      setIsSaving(true)
      setError('')
      if (mode === 'edit' && opportunityId) {
        await updateOpportunity(token, opportunityId, payload)
      } else {
        await createOpportunity(token, payload)
      }
      navigate('/organisation/opportunities')
    } catch (caughtError) {
      setError(getErrorMessage(caughtError))
    } finally {
      setIsSaving(false)
    }
  }

  return (
    <OrganisationShell
      eyebrow="Opportunities"
      title={mode === 'edit' ? 'Edit opportunity' : 'Create opportunity'}
    >
      {state === 'loading' ? <EmptyState>Loading opportunity...</EmptyState> : null}
      {state === 'error' ? <InlineError message={error} /> : null}
      {state === 'ready' ? (
        <form className="org-panel org-form org-form-wide" onSubmit={handleSubmit}>
          {error ? <InlineError message={error} /> : null}
          <label>
            Title
            <input
              name="title"
              value={form.title}
              onChange={(event) => updateField('title', event.target.value)}
              required
            />
          </label>
          <label>
            Description
            <textarea
              name="description"
              value={form.description}
              onChange={(event) => updateField('description', event.target.value)}
              rows={7}
              required
            />
          </label>
          <div className="org-form-grid">
            <label>
              Start date
              <input
                name="startDate"
                type="date"
                value={form.startDate}
                onChange={(event) => updateField('startDate', event.target.value)}
                required
              />
            </label>
            <label>
              End date
              <input
                name="endDate"
                type="date"
                value={form.endDate ?? ''}
                onChange={(event) => updateField('endDate', event.target.value)}
              />
            </label>
            <label>
              Status
              <select
                name="status"
                value={form.status ?? 'OPEN'}
                onChange={(event) => updateField('status', event.target.value as OpportunityStatus)}
              >
                <option value="OPEN">Open</option>
                <option value="CLOSED">Closed</option>
              </select>
            </label>
            <label>
              Location
              <input
                name="location"
                value={form.location ?? ''}
                onChange={(event) => updateField('location', event.target.value)}
              />
            </label>
            <label>
              Minimum age
              <input
                name="minAge"
                type="number"
                min="0"
                max="21"
                value={form.minAge ?? ''}
                onChange={(event) =>
                  updateField('minAge', event.target.value ? Number(event.target.value) : null)
                }
              />
            </label>
            <label>
              Required hours
              <input
                name="requiredHours"
                type="number"
                min="0"
                value={form.requiredHours ?? ''}
                onChange={(event) =>
                  updateField(
                    'requiredHours',
                    event.target.value ? Number(event.target.value) : null,
                  )
                }
              />
            </label>
            <label>
              Capacity
              <input
                name="capacity"
                type="number"
                min="1"
                value={form.capacity ?? ''}
                onChange={(event) =>
                  updateField('capacity', event.target.value ? Number(event.target.value) : null)
                }
              />
            </label>
            <label>
              Availability
              <input
                name="availability"
                value={form.availability ?? ''}
                onChange={(event) => updateField('availability', event.target.value)}
                placeholder="Weekday mornings, weekends"
              />
            </label>
          </div>
          <div className="org-checks">
            <label>
              <input
                name="remote"
                type="checkbox"
                checked={Boolean(form.remote)}
                onChange={(event) => updateField('remote', event.target.checked)}
              />
              Remote opportunity
            </label>
            <label>
              <input
                name="recurring"
                type="checkbox"
                checked={Boolean(form.recurring)}
                onChange={(event) => updateField('recurring', event.target.checked)}
              />
              Recurring opportunity
            </label>
          </div>
          <div className="org-form-actions">
            <button className="button button--primary" type="submit" disabled={isSaving}>
              {isSaving ? 'Saving...' : mode === 'edit' ? 'Save changes' : 'Create opportunity'}
            </button>
            <Link className="button button--secondary" to="/organisation/opportunities">
              Cancel
            </Link>
          </div>
        </form>
      ) : null}
    </OrganisationShell>
  )
}

export function OrganisationApplicationsPage() {
  const { token } = useOrganisationAuth()
  const [state, setState] = useState<LoadState>('loading')
  const [error, setError] = useState('')
  const [applications, setApplications] = useState<ApplicationResponseDTO[]>([])
  const [statusFilter, setStatusFilter] = useState<'ALL' | ApplicationStatus>('ALL')
  const [isUpdating, setIsUpdating] = useState('')

  useEffect(() => {
    if (!token) {
      return
    }
    const accessToken = token

    async function loadApplications() {
      try {
        setState('loading')
        setApplications(await getOrganisationApplications(accessToken))
        setState('ready')
      } catch (caughtError) {
        setError(getErrorMessage(caughtError))
        setState('error')
      }
    }

    void loadApplications()
  }, [token])

  async function handleStatusChange(applicationId: string, status: ApplicationStatus) {
    if (!token) {
      return
    }

    try {
      setIsUpdating(applicationId)
      const updated = await updateApplicationStatus(token, applicationId, status)
      setApplications((current) =>
        current.map((application) => (application.id === applicationId ? updated : application)),
      )
    } catch (caughtError) {
      setError(getErrorMessage(caughtError))
    } finally {
      setIsUpdating('')
    }
  }

  const visibleApplications = applications.filter((application) =>
    statusFilter === 'ALL' ? true : application.status === statusFilter,
  )

  return (
    <OrganisationShell eyebrow="Applications" title="Review applications">
      <div className="org-toolbar">
        <label>
          Status
          <select
            value={statusFilter}
            onChange={(event) => setStatusFilter(event.target.value as 'ALL' | ApplicationStatus)}
          >
            <option value="ALL">All</option>
            {applicationStatuses.map((status) => (
              <option value={status} key={status}>
                {status.replace('_', ' ')}
              </option>
            ))}
          </select>
        </label>
      </div>
      {state === 'loading' ? <EmptyState>Loading applications...</EmptyState> : null}
      {error ? <InlineError message={error} /> : null}
      {state === 'ready' && visibleApplications.length === 0 ? (
        <EmptyState>No applications match this view.</EmptyState>
      ) : null}
      {state === 'ready' && visibleApplications.length > 0 ? (
        <div className="org-application-grid">
          {visibleApplications.map((application) => (
            <article className="org-panel org-application-card" key={application.id}>
              <div>
                <span className={`org-status org-status--${application.status.toLowerCase()}`}>
                  {application.status.replace('_', ' ')}
                </span>
                <h2>{application.volunteerName || 'Unnamed volunteer'}</h2>
                <p className="org-muted">{application.opportunityName}</p>
              </div>
              <p>{application.message || 'No application message supplied.'}</p>
              <div className="org-card-footer">
                <span>Applied {formatDateTime(application.dateApplied)}</span>
                <select
                  value={application.status}
                  disabled={isUpdating === application.id}
                  onChange={(event) =>
                    void handleStatusChange(
                      application.id,
                      event.target.value as ApplicationStatus,
                    )
                  }
                >
                  {applicationStatuses.map((status) => (
                    <option value={status} key={status}>
                      {status.replace('_', ' ')}
                    </option>
                  ))}
                </select>
              </div>
            </article>
          ))}
        </div>
      ) : null}
    </OrganisationShell>
  )
}

export function OrganisationHistoryPage() {
  const { token } = useOrganisationAuth()
  const [state, setState] = useState<LoadState>('loading')
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')
  const [opportunities, setOpportunities] = useState<OpportunityResponseDTO[]>([])
  const [applications, setApplications] = useState<ApplicationResponseDTO[]>([])
  const [selectedOpportunityId, setSelectedOpportunityId] = useState('')
  const [history, setHistory] = useState<VolunteerHistoryResponseDTO[]>([])
  const [isCreating, setIsCreating] = useState(false)

  useEffect(() => {
    if (!token) {
      return
    }
    const accessToken = token

    async function loadBaseData() {
      try {
        setState('loading')
        const profile = await getOrganisationProfile(accessToken)
        const [nextOpportunities, nextApplications] = await Promise.all([
          getOrganisationOpportunities(accessToken, profile.id),
          getOrganisationApplications(accessToken),
        ])
        setOpportunities(nextOpportunities)
        setApplications(nextApplications)
        setSelectedOpportunityId(nextOpportunities[0]?.id ?? '')
        setState('ready')
      } catch (caughtError) {
        setError(getErrorMessage(caughtError))
        setState('error')
      }
    }

    void loadBaseData()
  }, [token])

  useEffect(() => {
    if (!token || !selectedOpportunityId) {
      setHistory([])
      return
    }
    const accessToken = token

    async function loadHistory() {
      try {
        setHistory(await getOpportunityHistory(accessToken, selectedOpportunityId))
      } catch (caughtError) {
        setError(getErrorMessage(caughtError))
      }
    }

    void loadHistory()
  }, [selectedOpportunityId, token])

  const acceptedApplications = useMemo(
    () =>
      applications.filter(
        (application) =>
          application.status === 'ACCEPTED' && application.opportunityId === selectedOpportunityId,
      ),
    [applications, selectedOpportunityId],
  )

  async function handleCreateHistory(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!token) {
      return
    }

    const formData = new FormData(event.currentTarget)
    const volunteerId = String(formData.get('volunteerId') ?? '')

    try {
      setIsCreating(true)
      setError('')
      setSuccess('')
      const created = await createVolunteerHistory(token, volunteerId, {
        opportunityId: selectedOpportunityId,
        startDate: String(formData.get('startDate') ?? ''),
        endDate: String(formData.get('endDate') ?? ''),
      })
      setHistory((current) => [created, ...current])
      setSuccess('Volunteer history entry created.')
      event.currentTarget.reset()
    } catch (caughtError) {
      setError(getErrorMessage(caughtError))
    } finally {
      setIsCreating(false)
    }
  }

  return (
    <OrganisationShell eyebrow="Volunteer history" title="Record completed work">
      {state === 'loading' ? <EmptyState>Loading volunteer history tools...</EmptyState> : null}
      {state === 'error' ? <InlineError message={error} /> : null}
      {state === 'ready' ? (
        <section className="org-grid-two">
          <div className="org-panel">
            <h2>Create history entry</h2>
            {error ? <InlineError message={error} /> : null}
            {success ? <p className="org-success">{success}</p> : null}
            <form className="org-form" onSubmit={handleCreateHistory}>
              <label>
                Opportunity
                <select
                  value={selectedOpportunityId}
                  onChange={(event) => setSelectedOpportunityId(event.target.value)}
                  required
                >
                  {opportunities.map((opportunity) => (
                    <option value={opportunity.id} key={opportunity.id}>
                      {opportunity.title}
                    </option>
                  ))}
                </select>
              </label>
              <label>
                Accepted volunteer
                <select name="volunteerId" required>
                  <option value="">Select volunteer</option>
                  {acceptedApplications.map((application) => (
                    <option value={application.volunteerId} key={application.id}>
                      {application.volunteerName}
                    </option>
                  ))}
                </select>
              </label>
              <div className="org-form-grid">
                <label>
                  Start date
                  <input name="startDate" type="date" required />
                </label>
                <label>
                  End date
                  <input name="endDate" type="date" required />
                </label>
              </div>
              <button className="button button--primary" type="submit" disabled={isCreating}>
                {isCreating ? 'Creating...' : 'Create entry'}
              </button>
            </form>
            <p className="org-small-note">
              Existing entries can be viewed here. The backend response does not currently expose
              history log IDs, so inline edits are waiting on that field.
            </p>
          </div>

          <div className="org-panel">
            <h2>Entries for selected opportunity</h2>
            {history.length === 0 ? (
              <EmptyState>No history entries for this opportunity.</EmptyState>
            ) : (
              <div className="org-list">
                {history.map((entry) => (
                  <article
                    className="org-list-row org-list-row--stacked"
                    key={`${entry.volunteerId}-${entry.opportunityId}-${entry.startDate}`}
                  >
                    <div>
                      <strong>{entry.volunteerName}</strong>
                      <p>
                        {formatDate(entry.startDate)} to {formatDate(entry.endDate)}
                      </p>
                    </div>
                    <span>{entry.hoursLogged} hours</span>
                    <p>{entry.organisationComment || 'No organisation comment yet.'}</p>
                  </article>
                ))}
              </div>
            )}
          </div>
        </section>
      ) : null}
    </OrganisationShell>
  )
}

export function OrganisationNotificationsPage() {
  const { token } = useOrganisationAuth()
  const [state, setState] = useState<LoadState>('loading')
  const [error, setError] = useState('')
  const [notifications, setNotifications] = useState<NotificationResponseDTO[]>([])

  useEffect(() => {
    if (!token) {
      return
    }
    const accessToken = token

    async function loadNotifications() {
      try {
        setState('loading')
        setNotifications(await getNotifications(accessToken))
        setState('ready')
      } catch (caughtError) {
        setError(getErrorMessage(caughtError))
        setState('error')
      }
    }

    void loadNotifications()
  }, [token])

  async function handleMarkRead(notificationId: string) {
    if (!token) {
      return
    }

    try {
      await markNotificationRead(token, notificationId)
      setNotifications((current) =>
        current.map((notification) =>
          notification.id === notificationId ? { ...notification, read: true } : notification,
        ),
      )
    } catch (caughtError) {
      setError(getErrorMessage(caughtError))
    }
  }

  return (
    <OrganisationShell eyebrow="Notifications" title="Organisation updates">
      {state === 'loading' ? <EmptyState>Loading notifications...</EmptyState> : null}
      {state === 'error' ? <InlineError message={error} /> : null}
      {error && state === 'ready' ? <InlineError message={error} /> : null}
      {state === 'ready' && notifications.length === 0 ? (
        <EmptyState>No notifications yet.</EmptyState>
      ) : null}
      {state === 'ready' && notifications.length > 0 ? (
        <div className="org-list org-notification-list">
          {notifications.map((notification) => (
            <article
              className={`org-panel org-notification ${notification.read ? '' : 'is-unread'}`}
              key={notification.id}
            >
              <div>
                <span className="org-status">{notification.type.replaceAll('_', ' ')}</span>
                <h2>{notification.message}</h2>
                <p>{formatDateTime(notification.timestamp)}</p>
              </div>
              {!notification.read ? (
                <button
                  className="button button--secondary"
                  type="button"
                  onClick={() => void handleMarkRead(notification.id)}
                >
                  Mark read
                </button>
              ) : (
                <span className="org-muted">Read</span>
              )}
            </article>
          ))}
        </div>
      ) : null}
    </OrganisationShell>
  )
}
