import { Link } from 'react-router-dom'
import { BrandHeader } from '../components/BrandHeader'

export function LoginPage() {
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

        <form className="auth-card">
          <label>
            Email
            <input type="email" name="email" placeholder="you@example.com" />
          </label>
          <label>
            Password
            <input type="password" name="password" placeholder="Password" />
          </label>
          <button className="button button--primary" type="button">
            Login
          </button>
          <p className="form-note">
            New to DoGoodr? <Link to="/get-started">Choose a signup portal</Link>
          </p>
        </form>
      </section>
    </main>
  )
}
