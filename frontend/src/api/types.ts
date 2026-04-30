export type Role = 'VOLUNTEER' | 'ORGANISATION' | 'ADMIN'

export type AuthResponse = {
  accessToken: string
  refreshToken: string
}

export type LoginRequest = {
  email: string
  password: string
}

export type UserResponseDTO = {
  id: string
  email: string
  secondaryEmail?: string | null
  phoneNumber?: string | null
  role: Role
  lastLogin?: string | null
  createdAt: string
}

export type ErrorResponse = {
  timestamp?: string
  status?: number
  message?: string
  path?: string
  details?: string
  fieldErrors?: Record<string, string>
}
