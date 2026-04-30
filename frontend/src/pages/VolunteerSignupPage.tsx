import { FormEvent, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { ApiError } from '../api/client'
import {
  getCurrentUser,
  getPostLoginPath,
  registerVolunteer,
  storeAuthSession,
} from '../api/auth'
import { BrandHeader } from '../components/BrandHeader'

export function VolunteerSignupPage() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [forename, setForename] = useState('')
  const [surname, setSurname] = useState('')
  const [preferredName, setPreferredName] = useState('')
  const [dateOfBirth, setDateOfBirth] = useState('')
  const [error, setError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setIsSubmitting(true)

    try {
      const tokens = await registerVolunteer({
        email,
        password,
        forename,
        surname,
        preferedName: preferredName,
        dateOfBirth,
      })
      const user = await getCurrentUser(tokens.accessToken)
      storeAuthSession(tokens, user)
      navigate(getPostLoginPath(user.role), { replace: true })
    } catch (caughtError) {
      if (caughtError instanceof ApiError) {
        setError(caughtError.message)
      } else {
        setError('Unable to create your volunteer account right now.')
      }
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <main className="page signup-page signup-page--volunteer">
      <BrandHeader variant="panel" />

      <section className="signup-shell" aria-labelledby="volunteer-signup-title">
        <div className="signup-intro">
          <p className="eyebrow">Volunteer signup</p>
          <h1 id="volunteer-signup-title">Start finding work that fits.</h1>
          <p>
            Create a volunteer profile so DoGoodr can help you discover
            opportunities that suit your time, interests, and local community.
          </p>
        </div>

        <form className="auth-card signup-card" onSubmit={handleSubmit}>
          <div className="form-grid">
            <label>
              Forename
              <input
                type="text"
                value={forename}
                onChange={(event) => setForename(event.target.value)}
                autoComplete="given-name"
                required
              />
            </label>
            <label>
              Surname
              <input
                type="text"
                value={surname}
                onChange={(event) => setSurname(event.target.value)}
                autoComplete="family-name"
                required
              />
            </label>
          </div>

          <label>
            Preferred name
            <input
              type="text"
              value={preferredName}
              onChange={(event) => setPreferredName(event.target.value)}
              autoComplete="nickname"
              required
            />
          </label>

          <label>
            Date of birth
            <input
              type="date"
              value={dateOfBirth}
              onChange={(event) => setDateOfBirth(event.target.value)}
              required
            />
          </label>

          <label>
            Email
            <input
              type="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              autoComplete="email"
              placeholder="you@example.com"
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

          <button className="button button--primary" type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Creating account...' : 'Create volunteer account'}
          </button>

          <p className="form-note">
            Already have an account? <Link to="/login">Login</Link>
          </p>
        </form>
      </section>
    </main>
  )
}
