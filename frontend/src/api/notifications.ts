import { apiRequest, API_BASE_URL } from './client'
import type { NotificationDTO } from './types'

export function getNotifications(token: string) {
  return apiRequest<NotificationDTO[]>('/api/notifications', {}, { token })
}

export function getUnreadNotifications(token: string) {
  return apiRequest<NotificationDTO[]>('/api/notifications/unread', {}, { token })
}

export function markNotificationRead(token: string, notificationId: string) {
  return apiRequest<void>(
    `/api/notifications/${notificationId}/read`,
    { method: 'PATCH' },
    { token },
  )
}

export function makeNotificationSocketUrl(token: string) {
  const socketBaseUrl = API_BASE_URL || window.location.origin
  const socketUrl = new URL('/ws', socketBaseUrl)
  socketUrl.protocol = socketUrl.protocol === 'https:' ? 'wss:' : 'ws:'
  socketUrl.searchParams.set('token', token)
  return socketUrl.toString()
}
