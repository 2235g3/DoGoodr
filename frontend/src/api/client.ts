import type { ErrorResponse } from './types'

export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL?.replace(/\/$/, '') ?? 'http://localhost:8080'

const ACCESS_TOKEN_KEY = 'dogoodr.accessToken'
const REFRESH_TOKEN_KEY = 'dogoodr.refreshToken'

export class ApiError extends Error {
  status: number
  fieldErrors?: Record<string, string>

  constructor(message: string, status: number, fieldErrors?: Record<string, string>) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

type RequestOptions = {
  token?: string
}

export async function apiRequest<TResponse>(
  path: string,
  init: RequestInit = {},
  options: RequestOptions = {},
): Promise<TResponse> {
  const response = await makeRequest(path, init, options.token)

  if (response.status === 401 && options.token && !path.startsWith('/api/auth/')) {
    const refreshedToken = await refreshAccessToken()
    if (refreshedToken) {
      return handleResponse<TResponse>(await makeRequest(path, init, refreshedToken))
    }
  }

  return handleResponse<TResponse>(response)
}

async function makeRequest(path: string, init: RequestInit, token?: string) {
  const headers = new Headers(init.headers)

  if (!headers.has('Content-Type') && init.body && !(init.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json')
  }

  if (token) {
    headers.set('Authorization', `Bearer ${token}`)
  }

  return fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers,
  })
}

async function handleResponse<TResponse>(response: Response): Promise<TResponse> {
  if (!response.ok) {
    throw await createApiError(response)
  }

  if (response.status === 204) {
    return undefined as TResponse
  }

  return response.json() as Promise<TResponse>
}

async function refreshAccessToken() {
  const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY)
  if (!refreshToken) {
    return null
  }

  const response = await makeRequest('/api/auth/refresh', {
    method: 'POST',
    body: JSON.stringify({ refreshToken }),
  })

  if (!response.ok) {
    localStorage.removeItem(ACCESS_TOKEN_KEY)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
    return null
  }

  const tokens = (await response.json()) as { accessToken: string; refreshToken: string }
  localStorage.setItem(ACCESS_TOKEN_KEY, tokens.accessToken)
  localStorage.setItem(REFRESH_TOKEN_KEY, tokens.refreshToken)
  return tokens.accessToken
}

async function createApiError(response: Response) {
  const fallbackMessage = `Request failed with status ${response.status}`

  try {
    const body = (await response.json()) as ErrorResponse
    return new ApiError(
      body.details || body.message || fallbackMessage,
      response.status,
      body.fieldErrors,
    )
  } catch {
    return new ApiError(fallbackMessage, response.status)
  }
}
