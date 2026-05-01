import { useEffect, useMemo, useState } from 'react'
import {
  getUnreadVolunteerNotifications,
  getVolunteerNotifications,
  markVolunteerNotificationRead,
} from '../../api/volunteer'
import type { NotificationDTO } from '../../api/types'
import { VolunteerNotice } from './VolunteerNotice'

export function VolunteerNotificationsPage() {
  const [notifications, setNotifications] = useState<NotificationDTO[]>([])
  const [showUnreadOnly, setShowUnreadOnly] = useState(false)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const sortedNotifications = useMemo(
    () =>
      [...notifications].sort(
        (a, b) => new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime(),
      ),
    [notifications],
  )

  useEffect(() => {
    loadNotifications(showUnreadOnly)
  }, [showUnreadOnly])

  async function loadNotifications(unreadOnly = showUnreadOnly) {
    setError('')
    try {
      setNotifications(
        unreadOnly ? await getUnreadVolunteerNotifications() : await getVolunteerNotifications(),
      )
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load notifications.')
    }
  }

  async function handleMarkRead(notificationId: string) {
    setError('')
    setMessage('')
    try {
      await markVolunteerNotificationRead(notificationId)
      setMessage('Notification marked as read.')
      await loadNotifications()
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to update notification.')
    }
  }

  return (
    <>
      <div className="admin-heading">
        <p className="eyebrow">Notifications</p>
        <h2>Updates</h2>
        <p>Read messages about decisions, points, and volunteering history updates.</p>
      </div>

      {message ? <VolunteerNotice tone="success">{message}</VolunteerNotice> : null}
      {error ? <VolunteerNotice tone="error">{error}</VolunteerNotice> : null}

      <div className="admin-panel">
        <label className="volunteer-toggle">
          <input
            checked={showUnreadOnly}
            type="checkbox"
            onChange={(event) => setShowUnreadOnly(event.target.checked)}
          />
          Unread only
        </label>
      </div>

      <div className="admin-card-grid">
        {sortedNotifications.map((notification) => (
          <article
            className={`admin-record-card volunteer-notification-card ${
              notification.read ? 'is-read' : ''
            }`}
            key={notification.id}
          >
            <div className="volunteer-card-topline">
              <span>{notification.type}</span>
              <span>{formatDate(notification.timestamp)}</span>
            </div>
            <strong>{notification.read ? 'Read' : 'Unread'}</strong>
            <p>{notification.message}</p>
            {!notification.read ? (
              <button type="button" onClick={() => handleMarkRead(notification.id)}>
                Mark read
              </button>
            ) : null}
          </article>
        ))}
      </div>
    </>
  )
}

function formatDate(value?: string | null) {
  if (!value) return '...'
  return new Date(value).toLocaleString()
}
