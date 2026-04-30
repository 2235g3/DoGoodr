import { Link } from 'react-router-dom'
import logo from '../assets/logo.png'

type BrandHeaderProps = {
  variant?: 'light' | 'panel'
}

export function BrandHeader({ variant = 'light' }: BrandHeaderProps) {
  return (
    <header className={`brand-header brand-header--${variant}`}>
      <Link className="brand-mark" to="/" aria-label="DoGoodr home">
        <img src={logo} alt="DoGoodr" />
      </Link>
      <nav className="public-nav" aria-label="Primary">
        <Link to="/login">Login</Link>
        <Link className="nav-pill" to="/get-started">
          Get started
        </Link>
      </nav>
    </header>
  )
}
