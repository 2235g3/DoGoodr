import { apiRequest } from './client'
import type {
  ApplicationResponseDTO,
  ApplicationStatus,
  CreateOpportunityDTO,
  CreateVolunteerHistoryDTO,
  NotificationResponseDTO,
  OProfileResponseDTO,
  OpportunityResponseDTO,
  UpdateOpportunityDTO,
  UpdateOrganisationProfileDTO,
  UpdateVolunteerHistoryDateRangeDTO,
  VolunteerHistoryCommentDTO,
  VolunteerHistoryResponseDTO,
  VolunteeredHoursDTO,
} from './types'

export function getOrganisationProfile(token: string) {
  return apiRequest<OProfileResponseDTO>(
    '/api/organisation-profile/me',
    { method: 'GET' },
    { token },
  )
}

export function updateOrganisationProfile(
  token: string,
  payload: UpdateOrganisationProfileDTO,
) {
  return apiRequest<OProfileResponseDTO>(
    '/api/organisation-profile/me',
    {
      method: 'PUT',
      body: JSON.stringify(payload),
    },
    { token },
  )
}

export function uploadOrganisationProfilePicture(token: string, file: File) {
  const body = new FormData()
  body.append('file', file)

  return apiRequest<OProfileResponseDTO>(
    '/api/organisation-profile/me/profile-picture',
    {
      method: 'PUT',
      body,
    },
    { token },
  )
}

export function getOrganisationOpportunities(token: string, organisationId: string) {
  return apiRequest<OpportunityResponseDTO[]>(
    `/api/opportunities/organisation/${organisationId}`,
    { method: 'GET' },
    { token },
  )
}

export function getOpportunity(token: string, opportunityId: string) {
  return apiRequest<OpportunityResponseDTO>(
    `/api/opportunities/${opportunityId}`,
    { method: 'GET' },
    { token },
  )
}

export function createOpportunity(token: string, payload: CreateOpportunityDTO) {
  return apiRequest<OpportunityResponseDTO>(
    '/api/opportunities/',
    {
      method: 'POST',
      body: JSON.stringify(payload),
    },
    { token },
  )
}

export function updateOpportunity(
  token: string,
  opportunityId: string,
  payload: UpdateOpportunityDTO,
) {
  return apiRequest<OpportunityResponseDTO>(
    `/api/opportunities/${opportunityId}`,
    {
      method: 'PUT',
      body: JSON.stringify(payload),
    },
    { token },
  )
}

export function deleteOpportunity(token: string, opportunityId: string) {
  return apiRequest<void>(
    `/api/opportunities/${opportunityId}`,
    { method: 'DELETE' },
    { token },
  )
}

export function getOrganisationApplications(token: string) {
  return apiRequest<ApplicationResponseDTO[]>(
    '/api/applications/organisation',
    { method: 'GET' },
    { token },
  )
}

export function getOpportunityApplications(token: string, opportunityId: string) {
  return apiRequest<ApplicationResponseDTO[]>(
    `/api/applications/organisation/${opportunityId}`,
    { method: 'GET' },
    { token },
  )
}

export function updateApplicationStatus(
  token: string,
  applicationId: string,
  status: ApplicationStatus,
) {
  return apiRequest<ApplicationResponseDTO>(
    `/api/applications/${applicationId}/status?status=${status}`,
    { method: 'PUT' },
    { token },
  )
}

export function getNotifications(token: string) {
  return apiRequest<NotificationResponseDTO[]>(
    '/api/notifications',
    { method: 'GET' },
    { token },
  )
}

export function markNotificationRead(token: string, notificationId: string) {
  return apiRequest<void>(
    `/api/notifications/${notificationId}/read`,
    { method: 'PATCH' },
    { token },
  )
}

export function getOpportunityHistory(token: string, opportunityId: string) {
  return apiRequest<VolunteerHistoryResponseDTO[]>(
    `/api/volunteering-history/opportunity/${opportunityId}`,
    { method: 'GET' },
    { token },
  )
}

export function createVolunteerHistory(
  token: string,
  volunteerId: string,
  payload: CreateVolunteerHistoryDTO,
) {
  return apiRequest<VolunteerHistoryResponseDTO>(
    `/api/volunteering-history/volunteer/${volunteerId}`,
    {
      method: 'POST',
      body: JSON.stringify(payload),
    },
    { token },
  )
}

export function updateVolunteerHistoryDateRange(
  token: string,
  historyId: number,
  payload: UpdateVolunteerHistoryDateRangeDTO,
) {
  return apiRequest<VolunteerHistoryResponseDTO>(
    `/api/volunteering-history/${historyId}/date-range`,
    {
      method: 'PUT',
      body: JSON.stringify(payload),
    },
    { token },
  )
}

export function updateVolunteerHistoryComment(
  token: string,
  historyId: number,
  payload: VolunteerHistoryCommentDTO,
) {
  return apiRequest<VolunteerHistoryResponseDTO>(
    `/api/volunteering-history/${historyId}/comment`,
    {
      method: 'PATCH',
      body: JSON.stringify(payload),
    },
    { token },
  )
}

export function addVolunteerHistoryHours(
  token: string,
  historyId: number,
  payload: VolunteeredHoursDTO,
) {
  return apiRequest<VolunteerHistoryResponseDTO>(
    `/api/volunteering-history/${historyId}/hours`,
    {
      method: 'PATCH',
      body: JSON.stringify(payload),
    },
    { token },
  )
}
