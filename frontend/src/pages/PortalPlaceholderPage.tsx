import { Link } from 'react-router-dom'
import { BrandHeader } from '../components/BrandHeader'

type PortalPlaceholderPageProps = {
  role: 'Volunteer' | 'Organisation'
}

export function PortalPlaceholderPage({ role }: PortalPlaceholderPageProps) {
  return (
    <main className="page placeholder-page">
      <BrandHeader variant="panel" />
      <section className="placeholder-card">
        <p className="eyebrow">{role} signup</p>
        <h1>{role} portal coming next.</h1>
        <p>
          This route is ready for the dedicated signup flow once the form design
          and backend handoff are confirmed.
        </p>
        <Link className="button button--secondary" to="/get-started">
          Back to choices
        </Link>
      </section>
    </main>
  )
}
