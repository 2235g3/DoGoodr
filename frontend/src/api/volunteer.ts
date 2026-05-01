import { apiRequest } from './client'
import { getStoredAccessToken } from './auth'
import type {
  ApplicationDTO,
  CreateApplicationDTO,
  MatchedOpportunityDTO,
  NotificationDTO,
  OpportunityDTO,
  UpdateUserDTO,
  UpdateUserPasswordDTO,
  UpdateVolunteerProfileDTO,
  UserResponseDTO,
  VolunteerHistoryDTO,
  VolunteerProfileDTO,
} from './types'

function volunteerToken() {
  const token = getStoredAccessToken()
  if (!token) {
    throw new Error('You need to log in as a volunteer to use this area.')
  }
  return token
}

export function getVolunteerProfile() {
  return apiRequest<VolunteerProfileDTO>(
    '/api/volunteer-profile/me',
    {},
    { token: volunteerToken() },
  )
}

export function updateVolunteerProfile(payload: UpdateVolunteerProfileDTO) {
  return apiRequest<VolunteerProfileDTO>(
    '/api/volunteer-profile/me',
    {
      method: 'PUT',
      body: JSON.stringify(payload),
    },
    { token: volunteerToken() },
  )
}

export function uploadVolunteerProfilePicture(file: File) {
  const body = new FormData()
  body.append('file', file)
  return apiRequest<VolunteerProfileDTO>(
    '/api/volunteer-profile/me/profile-picture',
    {
      method: 'PUT',
      body,
    },
    { token: volunteerToken() },
  )
}

export function uploadVolunteerCv(file: File) {
  const body = new FormData()
  body.append('file', file)
  return apiRequest<VolunteerProfileDTO>(
    '/api/volunteer-profile/me/cv',
    {
      method: 'PUT',
      body,
    },
    { token: volunteerToken() },
  )
}

export function deleteVolunteerProfilePicture() {
  return apiRequest<VolunteerProfileDTO>(
    '/api/volunteer-profile/me/profile-picture',
    { method: 'DELETE' },
    { token: volunteerToken() },
  )
}

export function deleteVolunteerCv() {
  return apiRequest<VolunteerProfileDTO>(
    '/api/volunteer-profile/me/cv',
    { method: 'DELETE' },
    { token: volunteerToken() },
  )
}

export function getVolunteerMatches() {
  return apiRequest<MatchedOpportunityDTO[]>(
    '/api/matching/volunteer',
    {},
    { token: volunteerToken() },
  )
}

export function getVolunteerOpportunityById(id: string) {
  return apiRequest<OpportunityDTO>(`/api/opportunities/${id}`, {}, { token: volunteerToken() })
}

export function getOpenVolunteerOpportunities() {
  return apiRequest<OpportunityDTO[]>('/api/opportunities/open', {}, { token: volunteerToken() })
}

export function getVolunteerOpportunitiesByOrganisation(organisationId: string) {
  return apiRequest<OpportunityDTO[]>(
    `/api/opportunities/organisation/${organisationId}`,
    {},
    { token: volunteerToken() },
  )
}

export function getVolunteerApplications() {
  return apiRequest<ApplicationDTO[]>('/api/applications/me', {}, { token: volunteerToken() })
}

export function createVolunteerApplication(opportunityId: string, payload: CreateApplicationDTO) {
  return apiRequest<ApplicationDTO>(
    `/api/applications/me/${opportunityId}`,
    {
      method: 'POST',
      body: JSON.stringify(payload),
    },
    { token: volunteerToken() },
  )
}

export function withdrawVolunteerApplication(applicationId: string) {
  return apiRequest<ApplicationDTO>(
    `/api/applications/me/${applicationId}/withdraw`,
    { method: 'PUT' },
    { token: volunteerToken() },
  )
}

export function getVolunteerNotifications() {
  return apiRequest<NotificationDTO[]>('/api/notifications', {}, { token: volunteerToken() })
}

export function getUnreadVolunteerNotifications() {
  return apiRequest<NotificationDTO[]>('/api/notifications/unread', {}, { token: volunteerToken() })
}

export function markVolunteerNotificationRead(notificationId: string) {
  return apiRequest<void>(
    `/api/notifications/${notificationId}/read`,
    { method: 'PATCH' },
    { token: volunteerToken() },
  )
}

export function getVolunteerHistory() {
  return apiRequest<VolunteerHistoryDTO[]>(
    '/api/volunteering-history/me',
    {},
    { token: volunteerToken() },
  )
}

export function updateCurrentUser(payload: UpdateUserDTO) {
  return apiRequest<UserResponseDTO>(
    '/api/user/me',
    {
      method: 'PUT',
      body: JSON.stringify(payload),
    },
    { token: volunteerToken() },
  )
}

export function updateCurrentUserPassword(payload: UpdateUserPasswordDTO) {
  return apiRequest<void>(
    '/api/user/me/password',
    {
      method: 'PUT',
      body: JSON.stringify(payload),
    },
    { token: volunteerToken() },
  )
}
