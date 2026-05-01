import { Link } from 'react-router-dom'
import logo from '../assets/logo.png'
import type { ReactNode } from 'react'

type BrandHeaderProps = {
  variant?: 'light' | 'panel'
  children?: ReactNode
  showPublicNav?: boolean
}

export function BrandHeader({ children, showPublicNav = true, variant = 'light' }: BrandHeaderProps) {
  return (
    <header className={`brand-header brand-header--${variant}`}>
      <Link className="brand-mark" to="/" aria-label="DoGoodr home">
        <img src={logo} alt="DoGoodr" />
      </Link>
      {children ? children : null}
      {!children && showPublicNav ? (
        <nav className="public-nav" aria-label="Primary">
          <Link to="/login">Login</Link>
          <Link className="nav-pill" to="/get-started">
            Get started
          </Link>
        </nav>
      ) : null}
    </header>
  )
}
