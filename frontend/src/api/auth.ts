import { apiRequest } from './client'
import type { AuthResponse, LoginRequest, UserResponseDTO } from './types'

const ACCESS_TOKEN_KEY = 'dogoodr.accessToken'
const REFRESH_TOKEN_KEY = 'dogoodr.refreshToken'
const USER_KEY = 'dogoodr.user'

export async function login(request: LoginRequest) {
  return apiRequest<AuthResponse>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(request),
  })
}

export async function getCurrentUser(accessToken: string) {
  return apiRequest<UserResponseDTO>(
    '/api/user/me',
    {
      method: 'GET',
    },
    { token: accessToken },
  )
}

export function storeAuthSession(tokens: AuthResponse, user: UserResponseDTO) {
  localStorage.setItem(ACCESS_TOKEN_KEY, tokens.accessToken)
  localStorage.setItem(REFRESH_TOKEN_KEY, tokens.refreshToken)
  localStorage.setItem(USER_KEY, JSON.stringify(user))
}

export function clearAuthSession() {
  localStorage.removeItem(ACCESS_TOKEN_KEY)
  localStorage.removeItem(REFRESH_TOKEN_KEY)
  localStorage.removeItem(USER_KEY)
}

export function getPostLoginPath(role: UserResponseDTO['role']) {
  switch (role) {
    case 'VOLUNTEER':
      return '/volunteer'
    case 'ORGANISATION':
      return '/organisation'
    case 'ADMIN':
      return '/admin'
  }
}
