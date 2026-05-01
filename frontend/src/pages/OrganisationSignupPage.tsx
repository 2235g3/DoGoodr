import { FormEvent, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ApiError } from '../api/client'
import {
  getCurrentUser,
  getPostLoginPath,
  registerOrganisation,
  storeAuthSession,
} from '../api/auth'
import type { AccountType } from '../api/types'
import { BrandHeader } from '../components/BrandHeader'

const accountTypes: Array<{ value: AccountType; label: string }> = [
  { value: 'CHARITY', label: 'Charity' },
  { value: 'NGO', label: 'NGO' },
  { value: 'COMMUNITY_GROUP', label: 'Community group' },
  { value: 'GOVERNMENT', label: 'Government' },
  { value: 'PERSONAL', label: 'Personal' },
  { value: 'OTHER', label: 'Other' },
]

export function OrganisationSignupPage() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [displayName, setDisplayName] = useState('')
  const [accountType, setAccountType] = useState<AccountType>('CHARITY')
  const [error, setError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setIsSubmitting(true)

    try {
      const tokens = await registerOrganisation({
        email,
        password,
        displayName,
        accountType,
      })
      const user = await getCurrentUser(tokens.accessToken)
      storeAuthSession(tokens, user)
      navigate(getPostLoginPath(user.role), { replace: true })
    } catch (caughtError) {
      if (caughtError instanceof ApiError) {
        setError(caughtError.message)
      } else {
        setError('Unable to create your organisation account right now.')
      }
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <main className="page signup-page signup-page--organisation">
      <BrandHeader variant="panel" />

      <section className="signup-shell" aria-labelledby="organisation-signup-title">
        <div className="signup-intro">
          <p className="eyebrow">Organisation signup</p>
          <h1 id="organisation-signup-title">Bring volunteers into your work.</h1>
          <p>
            Set up an organisation account to publish opportunities, review
            applications, and keep volunteering activity organised.
          </p>
        </div>

        <form className="auth-card signup-card" onSubmit={handleSubmit}>
          <label>
            Organisation name
            <input
              type="text"
              value={displayName}
              onChange={(event) => setDisplayName(event.target.value)}
              autoComplete="organization"
              required
            />
          </label>

          <label>
            Organisation type
            <select
              value={accountType}
              onChange={(event) => setAccountType(event.target.value as AccountType)}
              required
            >
              {accountTypes.map((type) => (
                <option key={type.value} value={type.value}>
                  {type.label}
                </option>
              ))}
            </select>
          </label>

          <label>
            Email
            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              autoComplete="email"
              placeholder="team@example.org"
              required
            />
          </label>

          <label>
            Password
            <input
              type="password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              autoComplete="new-password"
              placeholder="At least 8 characters"
              required
            />
          </label>

          {error ? (
            <p className="form-error" role="alert">
              {error}
            </p>
          ) : null}

          <button className="button button--dark" type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Creating account...' : 'Create organisation account'}
          </button>

          <p className="form-note">
            Already have an account? <Link to="/login">Login</Link>
          </p>
        </form>
      </section>
    </main>
  )
}
