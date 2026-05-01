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

export type LabelType = 'SKILL' | 'INTEREST' | 'CAUSE' | 'LANGUAGE' | 'EDUCATION' | 'OTHER'

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

export type CreateUserDTO = {
  email: string
  password: string
  role: Role
}

export type UpdateUserDTO = {
  email?: string | null
  secondaryEmail?: string | null
  phoneNumber?: string | null
}

export type UpdateUserPasswordDTO = {
  oldPassword: string
  newPassword: string
}

export type VolunteerProfileDTO = {
  id: string
  forename: string
  surname?: string | null
  preferredName: string
  profilePictureUrl?: string | null
  cvUrl?: string | null
  contactEmail?: string | null
  location?: string | null
  profileDescription?: string | null
  longitude?: number | null
  latitude?: number | null
  maxTravelDistance?: number | null
  remoteOnly: boolean
  totalHours?: number | null
  availability?: string | null
  dateOfBirth: string
  lastUpdated: string
  pointsBalance?: number | null
}

export type UpdateVolunteerProfileDTO = {
  forename?: string | null
  surname?: string | null
  preferredName?: string | null
  contactEmail?: string | null
  location?: string | null
  profileDescription?: string | null
  longitude?: number | null
  latitude?: number | null
  maxTravelDistance?: number | null
  remoteOnly?: boolean | null
  availability?: string | null
}

export type OrganisationProfileDTO = {
  id: string
  displayName: string
  accountType?: AccountType | null
  verified: boolean
  profilePictureUrl?: string | null
  description?: string | null
  contactEmail?: string | null
  location?: string | null
  websiteUrl?: string | null
}

export type OProfileResponseDTO = OrganisationProfileDTO

export type UpdateOrganisationProfileDTO = {
  displayName?: string | null
  accountType?: AccountType | null
  description?: string | null
  contactEmail?: string | null
  location?: string | null
  websiteUrl?: string | null
}

export type OpportunityDTO = {
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
  organisationProfile: OrganisationProfileDTO
}

export type OpportunityResponseDTO = OpportunityDTO

export type OpportunitySearchSort = 'newest' | 'start-date' | 'closest' | 'hours' | 'organisation'

export type OpportunitySearchParams = {
  q?: string
  remote?: boolean
  organisationId?: string
  maxHours?: number
  startsAfter?: string
  startsBefore?: string
  latitude?: number
  longitude?: number
  maxDistanceKm?: number
  sort?: OpportunitySearchSort
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

export type ApplicationDTO = {
  id: string
  volunteerId: string
  volunteerName: string
  opportunityId: string
  opportunityName: string
  message: string
  status: ApplicationStatus | string
  dateApplied: string
  decisionDate?: string | null
}

export type ApplicationResponseDTO = ApplicationDTO

export type CreateApplicationDTO = {
  message: string
}

export type MatchedOpportunityDTO = {
  opportunity: OpportunityDTO
  finalScore: number
  normalizedScore?: number | null
  distanceKm?: number | null
}

export type LabelDTO = {
  id: number
  name: string
  semanticTag?: string | null
  required: boolean
  type: LabelType
}

export type AssignedLabelDTO = {
  labelId: number
  weight: number
}

export type NotificationDTO = {
  id: string
  type: NotificationType
  message: string
  timestamp: string
  read: boolean
}

export type NotificationResponseDTO = NotificationDTO

export type VolunteerHistoryDTO = {
  id: number
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

export type VolunteerHistoryResponseDTO = VolunteerHistoryDTO

export type CreateVolunteerHistoryDTO = {
  opportunityId: string
  startDate: string
  endDate: string
}

export type UpdateVolunteerHistoryDateRangeDTO = {
  startDate: string
  endDate: string
}

export type VolunteerHistoryCommentDTO = {
  comment: string
}

export type VolunteeredHoursDTO = {
  hours: number
}

export type ErrorResponse = {
  timestamp?: string
  status?: number
  message?: string
  path?: string
  details?: string
  fieldErrors?: Record<string, string>
}
