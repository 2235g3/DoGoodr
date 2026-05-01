import { Link } from 'react-router-dom'
import { getPostLoginPath, getStoredUser } from '../api/auth'
import { BrandHeader } from '../components/BrandHeader'

export function HomePage() {
  const user = getStoredUser()
  const dashboardPath = user ? getPostLoginPath(user.role) : null

  return (
    <main className="page home-page">
      <BrandHeader>
        <nav className="public-nav" aria-label="Primary">
          {dashboardPath ? (
            <Link className="nav-pill" to={dashboardPath}>
              To Dashboard
            </Link>
          ) : (
            <>
              <Link to="/login">Login</Link>
              <Link className="nav-pill" to="/get-started">
                Get started
              </Link>
            </>
          )}
        </nav>
      </BrandHeader>

      <section className="home-hero" aria-labelledby="home-title">
        <div className="hero-copy">
          <p className="eyebrow">Community work, matched with care</p>
          <h1 id="home-title">Find the right place to do good.</h1>
          <p className="hero-text">
            DoGoodr helps people discover volunteering opportunities and gives
            organisations a calmer way to coordinate the work that matters.
          </p>
          <div className="hero-actions">
            {dashboardPath ? (
              <Link className="button button--primary" to={dashboardPath}>
                To Dashboard
              </Link>
            ) : (
              <>
                <Link className="button button--primary" to="/get-started">
                  Get started
                </Link>
                <Link className="button button--secondary" to="/login">
                  Login
                </Link>
              </>
            )}
          </div>
        </div>

        <div className="hero-stage" aria-hidden="true">
          <div className="match-card match-card--primary">
            <span>Fresh match</span>
            <strong>Garden support morning</strong>
            <p>Skills, distance, and availability line up beautifully.</p>
          </div>
          <div className="match-card match-card--secondary">
            <span>Organisation</span>
            <strong>Community Kitchen</strong>
            <p>3 new applicants waiting for review.</p>
          </div>
          <div className="hero-orbit hero-orbit--one" />
          <div className="hero-orbit hero-orbit--two" />
        </div>
      </section>

      <section className="home-strip" aria-label="Platform highlights">
        <article>
          <span className="metric">01</span>
          <h2>For volunteers</h2>
          <p>Browse welcoming opportunities shaped around time, place, and fit.</p>
        </article>
        <article>
          <span className="metric">02</span>
          <h2>For organisations</h2>
          <p>Create opportunities, review interest, and keep records tidy.</p>
        </article>
        <article>
          <span className="metric">03</span>
          <h2>For communities</h2>
          <p>Make local action easier to find, join, and sustain.</p>
        </article>
      </section>
    </main>
  )
}
