import { Link } from 'react-router-dom'
import { BrandHeader } from '../components/BrandHeader'

export function SignupChoicePage() {
  return (
    <main className="page choice-page">
      <BrandHeader />

      <section className="choice-intro" aria-labelledby="choice-title">
        <p className="eyebrow">Start with the right doorway</p>
        <h1 id="choice-title">How will you use DoGoodr?</h1>
      </section>

      <section className="choice-grid" aria-label="Signup options">
        <article className="choice-panel choice-panel--volunteer">
          <div>
            <span className="choice-kicker">Volunteer</span>
            <h2>Find work that fits your life.</h2>
            <p>
              Build a profile, discover meaningful opportunities, and keep your
              volunteering journey in one place.
            </p>
          </div>
          <Link className="button button--primary" to="/signup/volunteer">
            Sign up as volunteer
          </Link>
        </article>

        <article className="choice-panel choice-panel--organisation">
          <div>
            <span className="choice-kicker">Organisation</span>
            <h2>Bring the right people into your work.</h2>
            <p>
              Share opportunities, review applications, and manage volunteer
              activity with less admin drag.
            </p>
          </div>
          <Link className="button button--dark" to="/signup/organisation">
            Sign up as organisation
          </Link>
        </article>
      </section>
    </main>
  )
}
