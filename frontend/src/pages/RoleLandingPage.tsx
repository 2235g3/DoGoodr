import { Link } from 'react-router-dom'
import { BrandHeader } from '../components/BrandHeader'
import type { Role } from '../api/types'

type RoleLandingPageProps = {
  role: Role
}

const roleContent: Record<Role, { eyebrow: string; title: string; body: string }> = {
  VOLUNTEER: {
    eyebrow: 'Volunteer dashboard',
    title: 'Your volunteer space is ready.',
    body: 'The authenticated route is wired. Next we will turn this into the volunteer dashboard, profile, matches, and applications area.',
  },
  ORGANISATION: {
    eyebrow: 'Organisation dashboard',
    title: 'Your organisation space is ready.',
    body: 'The authenticated route is wired. Next we will add opportunity management, application review, and organisation profile tools.',
  },
  ADMIN: {
    eyebrow: 'Admin dashboard',
    title: 'Your admin space is ready.',
    body: 'The authenticated route is wired. Next we will decide which admin workflows belong in the first frontend version.',
  },
}

export function RoleLandingPage({ role }: RoleLandingPageProps) {
  const content = roleContent[role]

  return (
    <main className="page placeholder-page">
      <BrandHeader variant="panel" />
      <section className="placeholder-card">
        <p className="eyebrow">{content.eyebrow}</p>
        <h1>{content.title}</h1>
        <p>{content.body}</p>
        <Link className="button button--secondary" to="/">
          Back home
        </Link>
      </section>
    </main>
  )
}
