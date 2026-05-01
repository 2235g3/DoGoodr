import type { ReactNode } from 'react'

type AdminNoticeProps = {
  children: ReactNode
  tone?: 'info' | 'error' | 'success'
}

export function AdminNotice({ children, tone = 'info' }: AdminNoticeProps) {
  return <p className={`admin-notice admin-notice--${tone}`}>{children}</p>
}
