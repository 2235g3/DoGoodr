export type Role = 'VOLUNTEER' | 'ORGANISATION' | 'ADMIN'

export type AccountType =
  | 'PERSONAL'
  | 'CHARITY'
  | 'NGO'
  | 'GOVERNMENT'
  | 'COMMUNITY_GROUP'
  | 'OTHER'

export type AuthResponse = {
  accessToken: string
  refreshToken: string
}

export type LoginRequest = {
  email: string
  password: string
}

export type VolunteerRegisterRequest = {
  email: string
  password: string
  forename: string
  surname: string
  preferedName: string
  dateOfBirth: string
}

export type OrganisationRegisterRequest = {
  email: string
  password: string
  displayName: string
  accountType: AccountType
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
