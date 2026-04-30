import { FormEvent, useState } from 'react'
import { Link } from 'react-router-dom'
import { useNavigate } from 'react-router-dom'
import { ApiError } from '../api/client'
import { getCurrentUser, getPostLoginPath, login, storeAuthSession } from '../api/auth'
import { BrandHeader } from '../components/BrandHeader'

export function LoginPage() {
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [isSubmitting, setIsSubmitting] = useState(false)

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setIsSubmitting(true)

    try {
      const tokens = await login({ email, password })
      const user = await getCurrentUser(tokens.accessToken)
      storeAuthSession(tokens, user)
      navigate(getPostLoginPath(user.role), { replace: true })
    } catch (caughtError) {
      if (caughtError instanceof ApiError) {
        setError(caughtError.message)
      } else {
        setError('Unable to sign in right now. Please try again.')
      }
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <main className="page auth-page">
      <BrandHeader variant="panel" />

      <section className="auth-shell" aria-labelledby="login-title">
        <div className="auth-intro">
          <p className="eyebrow">Welcome back</p>
          <h1 id="login-title">Pick up where your good work left off.</h1>
          <p>
            Sign in to manage applications, opportunities, profiles, and
            volunteering activity.
          </p>
        </div>

        <form className="auth-card" onSubmit={handleSubmit}>
          <label>
            Email
            <input
              type="email"
              name="email"
              placeholder="you@example.com"
              autoComplete="email"
              value={email}
              onChange={(event) => setEmail(event.target.value)}
              required
            />
          </label>
          <label>
            Password
            <input
              type="password"
              name="password"
              placeholder="Password"
              autoComplete="current-password"
              value={password}
              onChange={(event) => setPassword(event.target.value)}
              required
            />
          </label>
          {error ? (
            <p className="form-error" role="alert">
              {error}
            </p>
          ) : null}
          <button className="button button--primary" type="submit" disabled={isSubmitting}>
            {isSubmitting ? 'Signing in...' : 'Login'}
          </button>
          <p className="form-note">
            New to DoGoodr? <Link to="/get-started">Choose a signup portal</Link>
          </p>
        </form>
      </section>
    </main>
  )
}
