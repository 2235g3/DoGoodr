import {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react'
import type { ReactNode } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { getStoredAccessToken, getStoredUser } from '../api/auth'
import {
  getNotifications,
  makeNotificationSocketUrl,
  markNotificationRead,
} from '../api/notifications'
import type { NotificationDTO, UserResponseDTO } from '../api/types'

type NotificationContextValue = {
  notifications: NotificationDTO[]
  unreadCount: number
  connectionState: 'idle' | 'connecting' | 'connected' | 'disconnected'
  refreshNotifications: () => Promise<void>
  markRead: (notificationId: string) => Promise<void>
}

type ToastNotification = NotificationDTO & {
  toastId: string
}

const NotificationContext = createContext<NotificationContextValue | null>(null)

export function NotificationProvider({ children }: { children: ReactNode }) {
  const location = useLocation()
  const [auth, setAuth] = useState(() => ({
    token: getStoredAccessToken(),
    user: getStoredUser(),
  }))
  const [notifications, setNotifications] = useState<NotificationDTO[]>([])
  const [toasts, setToasts] = useState<ToastNotification[]>([])
  const [connectionState, setConnectionState] =
    useState<NotificationContextValue['connectionState']>('idle')
  const reconnectTimeoutRef = useRef<number | null>(null)
  const reconnectAttemptRef = useRef(0)
  const { token, user } = auth

  useEffect(() => {
    setAuth({
      token: getStoredAccessToken(),
      user: getStoredUser(),
    })
  }, [location.pathname])

  const refreshNotifications = useCallback(async () => {
    if (!token || !user) {
      setNotifications([])
      return
    }

    setNotifications(await getNotifications(token))
  }, [token, user])

  useEffect(() => {
    void refreshNotifications()
  }, [refreshNotifications])

  useEffect(() => {
    if (!token || !user) {
      setConnectionState('idle')
      return
    }

    const accessToken = token
    let isClosed = false
    let socket: WebSocket | null = null

    function clearReconnect() {
      if (reconnectTimeoutRef.current) {
        window.clearTimeout(reconnectTimeoutRef.current)
        reconnectTimeoutRef.current = null
      }
    }

    function connect() {
      clearReconnect()
      setConnectionState('connecting')
      socket = new WebSocket(makeNotificationSocketUrl(accessToken))

      socket.addEventListener('open', () => {
        reconnectAttemptRef.current = 0
        socket?.send(
          toStompFrame('CONNECT', {
            'accept-version': '1.2',
            'heart-beat': '10000,10000',
            Authorization: `Bearer ${accessToken}`,
          }),
        )
      })

      socket.addEventListener('message', (event) => {
        const frames = parseStompFrames(String(event.data))

        frames.forEach((frame) => {
          if (frame.command === 'CONNECTED') {
            setConnectionState('connected')
            socket?.send(
              toStompFrame('SUBSCRIBE', {
                id: 'dogoodr-notifications',
                destination: '/user/queue/notifications',
                ack: 'auto',
              }),
            )
            return
          }

          if (frame.command === 'MESSAGE' && frame.body) {
            const notification = JSON.parse(frame.body) as NotificationDTO
            receiveNotification(notification)
          }
        })
      })

      socket.addEventListener('close', () => {
        if (isClosed) return
        setConnectionState('disconnected')
        const delay = Math.min(30000, 1000 * 2 ** reconnectAttemptRef.current)
        reconnectAttemptRef.current += 1
        reconnectTimeoutRef.current = window.setTimeout(connect, delay)
      })

      socket.addEventListener('error', () => {
        socket?.close()
      })
    }

    function receiveNotification(notification: NotificationDTO) {
      setNotifications((current) => {
        if (current.some((item) => item.id === notification.id)) {
          return current
        }
        return [notification, ...current]
      })
      setToasts((current) => [
        { ...notification, toastId: `${notification.id}-${Date.now()}` },
        ...current.slice(0, 2),
      ])
    }

    connect()

    return () => {
      isClosed = true
      clearReconnect()
      socket?.close()
    }
  }, [token, user?.id])

  const markRead = useCallback(async (notificationId: string) => {
    if (!token) return
    await markNotificationRead(token, notificationId)
    setNotifications((current) =>
      current.map((notification) =>
        notification.id === notificationId ? { ...notification, read: true } : notification,
      ),
    )
    setToasts((current) => current.filter((toast) => toast.id !== notificationId))
  }, [token])

  const unreadCount = notifications.filter((notification) => !notification.read).length

  const value = useMemo(
    () => ({
      notifications,
      unreadCount,
      connectionState,
      refreshNotifications,
      markRead,
    }),
    [notifications, unreadCount, connectionState, refreshNotifications, markRead],
  )

  return (
    <NotificationContext.Provider value={value}>
      {children}
      <NotificationToasts
        notifications={toasts}
        user={user}
        onClose={(toastId) =>
          setToasts((current) => current.filter((notification) => notification.toastId !== toastId))
        }
      />
    </NotificationContext.Provider>
  )
}

export function useNotifications() {
  const value = useContext(NotificationContext)
  if (!value) {
    throw new Error('useNotifications must be used inside NotificationProvider.')
  }
  return value
}

function NotificationToasts({
  notifications,
  user,
  onClose,
}: {
  notifications: ToastNotification[]
  user: UserResponseDTO | null
  onClose: (toastId: string) => void
}) {
  if (!notifications.length || !user) return null

  const notificationPath =
    user.role === 'ORGANISATION'
      ? '/organisation/notifications'
      : user.role === 'VOLUNTEER'
        ? '/volunteer/notifications'
        : '/admin'

  return (
    <div className="notification-toast-stack" role="status" aria-live="polite">
      {notifications.map((notification) => (
        <article className="notification-toast" key={notification.toastId}>
          <div>
            <span>{formatNotificationType(notification.type)}</span>
            <p>{notification.message}</p>
          </div>
          <div className="notification-toast-actions">
            <Link to={notificationPath} onClick={() => onClose(notification.toastId)}>
              View
            </Link>
            <button type="button" onClick={() => onClose(notification.toastId)} aria-label="Dismiss">
              x
            </button>
          </div>
        </article>
      ))}
    </div>
  )
}

function toStompFrame(command: string, headers: Record<string, string>, body = '') {
  const headerLines = Object.entries(headers).map(([key, value]) => `${key}:${value}`)
  return `${command}\n${headerLines.join('\n')}\n\n${body}\0`
}

function parseStompFrames(payload: string) {
  return payload
    .split('\0')
    .filter(Boolean)
    .map((frame) => {
      const [head = '', body = ''] = frame.split('\n\n')
      const [command = '', ...headerLines] = head.split('\n')
      const headers = Object.fromEntries(
        headerLines
          .filter((line) => line.includes(':'))
          .map((line) => {
            const separatorIndex = line.indexOf(':')
            return [line.slice(0, separatorIndex), line.slice(separatorIndex + 1)]
          }),
      )
      return { command, headers, body }
    })
}

function formatNotificationType(type: string) {
  return type.replaceAll('_', ' ').toLowerCase()
}
