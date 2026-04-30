import type { ErrorResponse } from './types'

const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL?.replace(/\/$/, '') ?? 'http://localhost:8080'

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
  const headers = new Headers(init.headers)

  if (!headers.has('Content-Type') && init.body) {
    headers.set('Content-Type', 'application/json')
  }

  if (options.token) {
    headers.set('Authorization', `Bearer ${options.token}`)
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers,
  })

  if (!response.ok) {
    throw await createApiError(response)
  }

  if (response.status === 204) {
    return undefined as TResponse
  }

  return response.json() as Promise<TResponse>
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
