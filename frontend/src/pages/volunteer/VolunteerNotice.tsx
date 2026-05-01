import type { ReactNode } from 'react'

type VolunteerNoticeProps = {
  children: ReactNode
  tone?: 'info' | 'error' | 'success'
}

export function VolunteerNotice({ children, tone = 'info' }: VolunteerNoticeProps) {
  return <p className={`admin-notice admin-notice--${tone}`}>{children}</p>
}
