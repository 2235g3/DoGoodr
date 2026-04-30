export type Role = 'VOLUNTEER' | 'ORGANISATION' | 'ADMIN'

export type AccountType =
  | 'PERSONAL'
  | 'CHARITY'
  | 'NGO'
  | 'GOVERNMENT'
  | 'COMMUNITY_GROUP'
  | 'OTHER'

export type OpportunityStatus = 'OPEN' | 'CLOSED'

export type ApplicationStatus =
  | 'APPLIED'
  | 'ACCEPTED'
  | 'REJECTED'
  | 'CANCELLED'
  | 'UNDER_REVIEW'
  | 'WITHDRAWN'
  | 'COMPLETED'

export type NotificationType =
  | 'APPLICATION_RECEIVED'
  | 'DECISION_RECEIVED'
  | 'VOLUNTEERING_HISTORY_UPDATED'
  | 'POINTS_EARNED'

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

export type OProfileResponseDTO = {
  id: string
  displayName: string
  profilePictureUrl?: string | null
  description?: string | null
  contactEmail?: string | null
  location?: string | null
  websiteUrl?: string | null
}

export type UpdateOrganisationProfileDTO = {
  displayName?: string | null
  accountType?: AccountType | null
  description?: string | null
  contactEmail?: string | null
  location?: string | null
  websiteUrl?: string | null
}

export type OpportunityResponseDTO = {
  id: string
  title: string
  description: string
  location?: string | null
  longitude?: number | null
  latitude?: number | null
  remote: boolean
  status: OpportunityStatus
  minAge?: number | null
  startDate: string
  endDate?: string | null
  recurring?: boolean | null
  availability?: string | null
  requiredHours?: number | null
  capacity?: number | null
  dateCreated: string
  lastUpdated: string
  organisationProfile: OProfileResponseDTO
}

export type CreateOpportunityDTO = {
  title: string
  description: string
  location?: string | null
  longitude?: number | null
  latitude?: number | null
  remote?: boolean | null
  minAge?: number | null
  startDate: string
  endDate?: string | null
  recurring?: boolean | null
  availability?: string | null
  requiredHours?: number | null
  capacity?: number | null
  status?: OpportunityStatus | null
}

export type UpdateOpportunityDTO = Partial<CreateOpportunityDTO>

export type ApplicationResponseDTO = {
  id: string
  volunteerId: string
  volunteerName: string
  opportunityId: string
  opportunityName: string
  message: string
  status: ApplicationStatus
  dateApplied: string
  decisionDate?: string | null
}

export type NotificationResponseDTO = {
  id: string
  type: NotificationType
  message: string
  timestamp: string
  read: boolean
}

export type VolunteerHistoryResponseDTO = {
  volunteerId: string
  volunteerName: string
  opportunityId: string
  opportunityTitle: string
  organisationId: string
  organisationName: string
  hoursLogged: number
  startDate: string
  endDate: string
  pointsGained: number
  organisationComment?: string | null
}

export type CreateVolunteerHistoryDTO = {
  opportunityId: string
  startDate: string
  endDate: string
}
