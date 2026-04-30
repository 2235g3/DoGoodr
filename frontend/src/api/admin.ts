import { apiRequest } from './client'
import { getStoredAccessToken } from './auth'
import type {
  ApplicationDTO,
  CreateUserDTO,
  OpportunityDTO,
  OrganisationProfileDTO,
  Role,
  UpdateUserDTO,
  UpdateUserPasswordDTO,
  UserResponseDTO,
  VolunteerHistoryDTO,
  VolunteerProfileDTO,
} from './types'

function adminToken() {
  const token = getStoredAccessToken()
  if (!token) {
    throw new Error('You need to log in as an admin to use this area.')
  }
  return token
}

export function getAdminUsers() {
  return apiRequest<UserResponseDTO[]>('/api/admin/user/', {}, { token: adminToken() })
}

export function getAdminUserById(id: string) {
  return apiRequest<UserResponseDTO>(`/api/admin/user/${id}`, {}, { token: adminToken() })
}

export function getAdminUserByEmail(email: string) {
  return apiRequest<UserResponseDTO>(
    `/api/admin/user/email?email=${encodeURIComponent(email)}`,
    {},
    { token: adminToken() },
  )
}

export function getAdminUsersByRole(role: Role) {
  return apiRequest<UserResponseDTO[]>(
    `/api/admin/user/role?role=${role}`,
    {},
    { token: adminToken() },
  )
}

export function createAdminUser(payload: CreateUserDTO) {
  return apiRequest<UserResponseDTO>(
    '/api/admin/user/',
    {
      method: 'POST',
      body: JSON.stringify(payload),
    },
    { token: adminToken() },
  )
}

export function updateAdminUser(id: string, payload: UpdateUserDTO) {
  return apiRequest<UserResponseDTO>(
    `/api/admin/user/${id}`,
    {
      method: 'PUT',
      body: JSON.stringify(payload),
    },
    { token: adminToken() },
  )
}

export function updateAdminUserPassword(id: string, payload: UpdateUserPasswordDTO) {
  return apiRequest<void>(
    `/api/admin/user/password/${id}`,
    {
      method: 'PUT',
      body: JSON.stringify(payload),
    },
    { token: adminToken() },
  )
}

export function deleteAdminUser(id: string) {
  return apiRequest<void>(
    `/api/admin/user/${id}`,
    { method: 'DELETE' },
    { token: adminToken() },
  )
}

export function getAdminVolunteerProfiles() {
  return apiRequest<VolunteerProfileDTO[]>(
    '/api/admin/volunteer-profile/',
    {},
    { token: adminToken() },
  )
}

export function getAdminVolunteerProfileById(id: string) {
  return apiRequest<VolunteerProfileDTO>(
    `/api/admin/volunteer-profile/${id}`,
    {},
    { token: adminToken() },
  )
}

export function deleteAdminVolunteerProfile(id: string) {
  return apiRequest<void>(
    `/api/admin/volunteer-profile/${id}`,
    { method: 'DELETE' },
    { token: adminToken() },
  )
}

export function getAdminOrganisationProfiles() {
  return apiRequest<OrganisationProfileDTO[]>(
    '/api/admin/organisation-profile/',
    {},
    { token: adminToken() },
  )
}

export function getAdminOrganisationProfileById(id: string) {
  return apiRequest<OrganisationProfileDTO>(
    `/api/admin/organisation-profile/${id}`,
    {},
    { token: adminToken() },
  )
}

export function verifyAdminOrganisationProfile(id: string) {
  return apiRequest<OrganisationProfileDTO>(
    `/api/admin/organisation-profile/${id}/verify`,
    { method: 'PUT' },
    { token: adminToken() },
  )
}

export function deleteAdminOrganisationProfile(id: string) {
  return apiRequest<void>(
    `/api/admin/organisation-profile/${id}`,
    { method: 'DELETE' },
    { token: adminToken() },
  )
}

export function getAdminOpportunities() {
  return apiRequest<OpportunityDTO[]>('/api/opportunities/', {}, { token: adminToken() })
}

export function getAdminOpportunityById(id: string) {
  return apiRequest<OpportunityDTO>(`/api/opportunities/${id}`, {}, { token: adminToken() })
}

export function getAdminOpportunitiesByOrganisation(organisationId: string) {
  return apiRequest<OpportunityDTO[]>(
    `/api/opportunities/organisation/${organisationId}`,
    {},
    { token: adminToken() },
  )
}

export function getAdminApplications() {
  return apiRequest<ApplicationDTO[]>('/api/applications/admin/', {}, { token: adminToken() })
}

export function getAdminApplicationById(id: string) {
  return apiRequest<ApplicationDTO>(
    `/api/applications/admin/${id}`,
    {},
    { token: adminToken() },
  )
}

export function getAdminApplicationsByOpportunity(opportunityId: string) {
  return apiRequest<ApplicationDTO[]>(
    `/api/applications/admin/opportunity/${opportunityId}`,
    {},
    { token: adminToken() },
  )
}

export function getAdminApplicationsByVolunteer(volunteerId: string) {
  return apiRequest<ApplicationDTO[]>(
    `/api/applications/admin/volunteer/${volunteerId}`,
    {},
    { token: adminToken() },
  )
}

export function getAdminVolunteerHistoryByVolunteer(volunteerId: string) {
  return apiRequest<VolunteerHistoryDTO[]>(
    `/api/admin/volunteering-history/volunteer/${volunteerId}`,
    {},
    { token: adminToken() },
  )
}

export function getAdminVolunteerHistoryByOpportunityAndOrganisation(
  opportunityId: string,
  organisationId: string,
) {
  return apiRequest<VolunteerHistoryDTO[]>(
    `/api/admin/volunteering-history/opportunity/${opportunityId}/organisation/${organisationId}`,
    {},
    { token: adminToken() },
  )
}
