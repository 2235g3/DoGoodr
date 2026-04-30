# Vidalia Backend API Reference

This file is the frontend-facing API reference for the new React app. It is derived from the Spring controllers and DTOs in `backend/project-vidalia-backend` as of 2026-04-30.

Use this document when building API clients, hooks, route loaders, forms, and frontend TypeScript types.

## Base Rules

### Base URL

Local backend default:

```text
http://localhost:8080
```

All REST endpoints below are relative to that base URL.

### Authentication

Most endpoints require a JWT access token:

```http
Authorization: Bearer <accessToken>
```

Unauthenticated endpoints:

- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/register/volunteer`
- `POST /api/auth/register/organisation`
- WebSocket handshake endpoint, depending on backend configuration

### Content Types

Default JSON requests:

```http
Content-Type: application/json
```

File upload requests:

```http
Content-Type: multipart/form-data
```

Multipart file field name:

```text
file
```

### Date Formats

Backend DTOs use Java date/time types. Send and expect:

- `LocalDate`: `YYYY-MM-DD`
- `LocalDateTime`: ISO-like date-time string

### Common Error Shape

Validation and backend errors are returned as:

```ts
type ErrorResponse = {
  timestamp: string
  status: number
  message: string
  path?: string
  details?: string
  fieldErrors?: Record<string, string>
}
```

Common statuses:

- `400`: invalid request or validation failure
- `401`: missing/invalid auth token or bad credentials
- `403`: authenticated but not allowed
- `404`: resource not found
- `409`: duplicate resource
- `500`: backend/server error

## Enums

```ts
type Role = 'VOLUNTEER' | 'ORGANISATION' | 'ADMIN'

type AccountType =
  | 'PERSONAL'
  | 'CHARITY'
  | 'NGO'
  | 'GOVERNMENT'
  | 'COMMUNITY_GROUP'
  | 'OTHER'

type ApplicationStatus =
  | 'APPLIED'
  | 'ACCEPTED'
  | 'REJECTED'
  | 'CANCELLED'
  | 'UNDER_REVIEW'
  | 'WITHDRAWN'
  | 'COMPLETED'

type OpportunityStatus = 'OPEN' | 'CLOSED'

type NotificationType =
  | 'APPLICATION_RECEIVED'
  | 'DECISION_RECEIVED'
  | 'VOLUNTEERING_HISTORY_UPDATED'
  | 'POINTS_EARNED'

type LabelType =
  | 'SKILL'
  | 'INTEREST'
  | 'CAUSE'
  | 'LANGUAGE'
  | 'EDUCATION'
  | 'OTHER'
```

## Shared DTO Types

### Auth

```ts
type AuthResponse = {
  accessToken: string
  refreshToken: string
}

type LoginRequest = {
  email: string
  password: string
}

type RefreshTokenRequest = {
  refreshToken: string
}

type VRegisterRequest = {
  email: string
  password: string
  forename: string
  surname: string
  preferedName: string
  dateOfBirth: string
}

type ORegisterRequest = {
  email: string
  password: string
  displayName: string
  accountType: AccountType
}
```

Important spelling note: volunteer registration uses `preferedName` with one `r` after `prefe`, matching the backend DTO.

### User

```ts
type UserResponseDTO = {
  id: string
  email: string
  secondaryEmail?: string | null
  phoneNumber?: string | null
  role: Role
  lastLogin?: string | null
  createdAt: string
}

type CreateUserDTO = {
  email: string
  password: string
  role: Role
}

type UpdateUserDTO = {
  email?: string | null
  secondaryEmail?: string | null
  phoneNumber?: string | null
}

type UpdateUserPasswordDTO = {
  oldPassword: string
  newPassword: string
}
```

Phone numbers must be E.164 format, for example `+44123123456`.

### Profiles

```ts
type VProfileResponseDTO = {
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

type UpdateVolunteerProfileDTO = {
  forename?: string | null
  surname?: string | null
  preferredName?: string | null
  contactEmail?: string | null
  location?: string | null
  profileDescription?: string | null
  maxTravelDistance?: number | null
  availability?: string | null
}

type OProfileResponseDTO = {
  id: string
  displayName: string
  profilePictureUrl?: string | null
  description?: string | null
  contactEmail?: string | null
  location?: string | null
  websiteUrl?: string | null
}

type UpdateOrganisationProfileDTO = {
  displayName?: string | null
  accountType?: AccountType | null
  description?: string | null
  contactEmail?: string | null
  location?: string | null
  websiteUrl?: string | null
}
```

Frontend note: `OProfileResponseDTO` currently does not expose `accountType` or `verified`, even though these exist in backend model/update flows.

### Opportunities

```ts
type OpportunityResponseDTO = {
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

type CreateOpportunityDTO = {
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

type UpdateOpportunityDTO = Partial<CreateOpportunityDTO>
```

Validation notes:

- `title` max 255 characters.
- `description` max 5000 characters.
- `location` max 255 characters.
- `minAge` must be between `0` and `21`.
- `requiredHours` must be `0` or greater.
- `capacity` must be at least `1`.
- `availability` max 255 characters.

### Applications

```ts
type ApplicationResponseDTO = {
  id: string
  volunteerId: string
  volunteerName: string
  opportunityId: string
  opportunityName: string
  message: string
  status: string
  dateApplied: string
  decisionDate?: string | null
}

type CreateApplicationDTO = {
  message: string
}
```

Application messages are trimmed and HTML-escaped by the backend. Max length is 2000 characters.

### Matching

```ts
type MatchedOpportunityDTO = {
  opportunity: OpportunityResponseDTO
  finalScore: number
  normalizedScore?: number | null
  distanceKm?: number | null
}
```

### Notifications

```ts
type NotificationResponseDTO = {
  id: string
  type: NotificationType
  message: string
  timestamp: string
  read: boolean
}

type CreateNotificationDTO = {
  recipientId: string
  type: NotificationType
  message: string
}
```

### Volunteer History

```ts
type VolunteerHistoryResponseDTO = {
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

type CreateVolunteerHistoryDTO = {
  opportunityId: string
  startDate: string
  endDate: string
}

type UpdateVolunteerHistoryDateRangeDTO = {
  startDate: string
  endDate: string
}

type VolunteerHistoryCommentDTO = {
  comment: string
}

type VolunteeredHoursDTO = {
  hours: number
}
```

Hours must be between `0` and `8`.

## Endpoint Reference

### Auth

| Method | Path | Access | Request | Response | Notes |
| --- | --- | --- | --- | --- | --- |
| `POST` | `/api/auth/login` | Public | `LoginRequest` | `AuthResponse` | Use returned access token for protected calls. |
| `POST` | `/api/auth/refresh` | Public | `RefreshTokenRequest` | `AuthResponse` | Returns new access and refresh tokens. |
| `POST` | `/api/auth/register/volunteer` | Public | `VRegisterRequest` | `AuthResponse` | Creates user and volunteer profile. |
| `POST` | `/api/auth/register/organisation` | Public | `ORegisterRequest` | `AuthResponse` | Creates user and organisation profile. |

### Current User

All endpoints require `ADMIN`, `VOLUNTEER`, or `ORGANISATION`.

| Method | Path | Request | Response | Notes |
| --- | --- | --- | --- | --- |
| `GET` | `/api/user/me` | None | `UserResponseDTO` | Get logged-in user. |
| `PUT` | `/api/user/me` | `UpdateUserDTO` | `UserResponseDTO` | Update current user contact fields. |
| `PUT` | `/api/user/me/password` | `UpdateUserPasswordDTO` | Empty `200` | Changes current user's password. |
| `DELETE` | `/api/user/me` | None | Empty `204` | Deletes current user. |

### Admin Users

All endpoints require `ADMIN`.

| Method | Path | Request | Response | Notes |
| --- | --- | --- | --- | --- |
| `GET` | `/api/admin/user/` | None | `UserResponseDTO[]` | List all users. |
| `GET` | `/api/admin/user/{id}` | None | `UserResponseDTO` | Get user by UUID. |
| `GET` | `/api/admin/user/email?email={email}` | None | `UserResponseDTO` | Get user by email. |
| `GET` | `/api/admin/user/role?role={role}` | None | `UserResponseDTO[]` | Filter by `Role`. |
| `POST` | `/api/admin/user/` | `CreateUserDTO` | `UserResponseDTO` | Creates user. |
| `PUT` | `/api/admin/user/{id}` | `UpdateUserDTO` | `UserResponseDTO` | Updates user contact fields. |
| `PUT` | `/api/admin/user/password/{id}` | `UpdateUserPasswordDTO` | Empty `200` | Changes a user's password. |
| `DELETE` | `/api/admin/user/{id}` | None | Empty `204` | Deletes user. |

### Volunteer Profile

All endpoints require `VOLUNTEER`.

| Method | Path | Request | Response | Notes |
| --- | --- | --- | --- | --- |
| `GET` | `/api/volunteer-profile/me` | None | `VProfileResponseDTO` | Get current volunteer profile. |
| `PUT` | `/api/volunteer-profile/me` | `UpdateVolunteerProfileDTO` | `VProfileResponseDTO` | Update current volunteer profile. |
| `PUT` | `/api/volunteer-profile/me/profile-picture` | multipart `file` | `VProfileResponseDTO` | Upload profile picture. |
| `PUT` | `/api/volunteer-profile/me/cv` | multipart `file` | `VProfileResponseDTO` | Upload CV PDF. |
| `DELETE` | `/api/volunteer-profile/me/profile-picture` | None | `VProfileResponseDTO` | Delete profile picture. |
| `DELETE` | `/api/volunteer-profile/me/cv` | None | `VProfileResponseDTO` | Delete CV. |

Upload constraints from backend config:

- Profile pictures: JPEG, PNG, WebP; max 5 MB by default.
- CVs: PDF; max 10 MB by default.

### Admin Volunteer Profiles

All endpoints require `ADMIN`.

| Method | Path | Request | Response | Notes |
| --- | --- | --- | --- | --- |
| `GET` | `/api/admin/volunteer-profile/` | None | `VProfileResponseDTO[]` | List volunteer profiles. |
| `GET` | `/api/admin/volunteer-profile/{id}` | None | `VProfileResponseDTO` | Get volunteer profile by profile UUID. |
| `DELETE` | `/api/admin/volunteer-profile/{id}` | None | Empty `204` | Delete volunteer profile. |

### Organisation Profile

All endpoints require `ORGANISATION`.

| Method | Path | Request | Response | Notes |
| --- | --- | --- | --- | --- |
| `GET` | `/api/organisation-profile/me` | None | `OProfileResponseDTO` | Get current organisation profile. |
| `PUT` | `/api/organisation-profile/me` | `UpdateOrganisationProfileDTO` | `OProfileResponseDTO` | Update current organisation profile. |
| `PUT` | `/api/organisation-profile/me/profile-picture` | multipart `file` | `OProfileResponseDTO` | Upload profile picture. |
| `DELETE` | `/api/organisation-profile/me/profile-picture` | None | `OProfileResponseDTO` | Delete profile picture. |

### Admin Organisation Profiles

All endpoints require `ADMIN`.

| Method | Path | Request | Response | Notes |
| --- | --- | --- | --- | --- |
| `GET` | `/api/admin/organisation-profile/` | None | `OProfileResponseDTO[]` | List organisation profiles. |
| `GET` | `/api/admin/organisation-profile/{id}` | None | `OProfileResponseDTO` | Get organisation profile by profile UUID. |
| `PUT` | `/api/admin/organisation-profile/{id}/verify` | None | `OProfileResponseDTO` | Mark organisation as verified. |
| `DELETE` | `/api/admin/organisation-profile/{id}` | None | Empty `204` | Delete organisation profile. |

### Opportunities

| Method | Path | Access | Request | Response | Notes |
| --- | --- | --- | --- | --- | --- |
| `GET` | `/api/opportunities/` | `ADMIN` | None | `OpportunityResponseDTO[]` | List all opportunities. |
| `GET` | `/api/opportunities/organisation/{organisationId}` | `ADMIN`, `VOLUNTEER`, `ORGANISATION` | None | `OpportunityResponseDTO[]` | List opportunities for an organisation profile UUID. |
| `GET` | `/api/opportunities/{id}` | `ADMIN`, `VOLUNTEER`, `ORGANISATION` | None | `OpportunityResponseDTO` | Organisation users are restricted to their own opportunities. |
| `POST` | `/api/opportunities/` | `ORGANISATION` | `CreateOpportunityDTO` | `OpportunityResponseDTO` | Creates opportunity under current organisation profile. |
| `PUT` | `/api/opportunities/{id}` | `ORGANISATION` | `UpdateOpportunityDTO` | `OpportunityResponseDTO` | Updates an opportunity owned by current organisation. |
| `DELETE` | `/api/opportunities/{id}` | `ORGANISATION` | None | Empty `204` | Deletes an opportunity owned by current organisation. |

Frontend gap: there is currently no public or volunteer endpoint for listing all open opportunities. Volunteers can get matched opportunities via matching, or list by known organisation ID.

### Applications

| Method | Path | Access | Request | Response | Notes |
| --- | --- | --- | --- | --- | --- |
| `GET` | `/api/applications/admin/` | `ADMIN` | None | `ApplicationResponseDTO[]` | List all applications. |
| `GET` | `/api/applications/admin/{id}` | `ADMIN` | None | `ApplicationResponseDTO` | Get application by UUID. |
| `GET` | `/api/applications/admin/opportunity/{opportunityId}` | `ADMIN` | None | `ApplicationResponseDTO[]` | List applications for opportunity UUID. |
| `GET` | `/api/applications/admin/volunteer/{volunteerId}` | `ADMIN` | None | `ApplicationResponseDTO[]` | List applications for volunteer identifier. |
| `GET` | `/api/applications/me` | `VOLUNTEER` | None | `ApplicationResponseDTO[]` | List current volunteer applications. |
| `POST` | `/api/applications/me/{opportunityId}` | `VOLUNTEER` | `CreateApplicationDTO` | `ApplicationResponseDTO` | Apply to opportunity. |
| `GET` | `/api/applications/organisation` | `ORGANISATION` | None | `ApplicationResponseDTO[]` | List applications for current organisation. |
| `GET` | `/api/applications/organisation/{opportunityId}` | `ORGANISATION` | None | `ApplicationResponseDTO[]` | List applications for one of current organisation's opportunities. |
| `PUT` | `/api/applications/{id}/status?status={status}` | `ORGANISATION` | None | `ApplicationResponseDTO` | Update application status. |

Status query value must be one of `ApplicationStatus`.

### Matching

| Method | Path | Access | Request | Response | Notes |
| --- | --- | --- | --- | --- | --- |
| `GET` | `/api/matching/volunteer` | `VOLUNTEER` | None | `MatchedOpportunityDTO[]` | Returns opportunity matches for current volunteer profile. |

Implementation warning: `MatchingController` currently declares `MatchingService` and `VolunteerProfileService` fields without `final` while using `@RequiredArgsConstructor`, so dependency injection may fail until the backend is corrected.

### Notifications

All list/read endpoints require `ADMIN`, `VOLUNTEER`, or `ORGANISATION`. Create requires `ADMIN`.

| Method | Path | Request | Response | Notes |
| --- | --- | --- | --- | --- |
| `GET` | `/api/notifications` | None | `NotificationResponseDTO[]` | List notifications for current user. |
| `GET` | `/api/notifications/unread` | None | `NotificationResponseDTO[]` | List unread notifications for current user. |
| `GET` | `/api/notifications/{notificationId}` | None | `NotificationResponseDTO` | Get current user's notification by UUID. |
| `PATCH` | `/api/notifications/{notificationId}/read` | None | Empty `204` | Mark notification as read. |
| `POST` | `/api/notifications` | `CreateNotificationDTO` | `NotificationResponseDTO` | Admin-only create notification. |

### Volunteer History

| Method | Path | Access | Request | Response | Notes |
| --- | --- | --- | --- | --- | --- |
| `GET` | `/api/volunteering-history/me` | `VOLUNTEER` | None | `VolunteerHistoryResponseDTO[]` | Current volunteer's history. |
| `GET` | `/api/volunteering-history/opportunity/{opportunityId}` | `ORGANISATION` | None | `VolunteerHistoryResponseDTO[]` | History for current organisation's opportunity. |
| `POST` | `/api/volunteering-history/volunteer/{volunteerId}` | `ORGANISATION` | `CreateVolunteerHistoryDTO` | `VolunteerHistoryResponseDTO` | Create history entry for volunteer profile UUID. |
| `PUT` | `/api/volunteering-history/{logId}/date-range` | `ORGANISATION` | `UpdateVolunteerHistoryDateRangeDTO` | `VolunteerHistoryResponseDTO` | Update date range. |
| `PATCH` | `/api/volunteering-history/{logId}/comment` | `ORGANISATION` | `VolunteerHistoryCommentDTO` | `VolunteerHistoryResponseDTO` | Set organisation comment. |
| `PATCH` | `/api/volunteering-history/{logId}/hours` | `ORGANISATION` | `VolunteeredHoursDTO` | `VolunteerHistoryResponseDTO` | Add/update logged hours. |

Frontend gap: `VolunteerHistoryResponseDTO` does not expose the history log ID, but update endpoints require `{logId}`. The frontend cannot reliably edit history entries from this DTO alone unless the backend adds the log ID.

### Admin Volunteer History

All endpoints require `ADMIN`.

| Method | Path | Request | Response | Notes |
| --- | --- | --- | --- | --- |
| `GET` | `/api/admin/volunteering-history/volunteer/{volunteerId}` | None | `VolunteerHistoryResponseDTO[]` | History for volunteer profile UUID. |
| `GET` | `/api/admin/volunteering-history/opportunity/{opportunityId}/organisation/{organisationId}` | None | `VolunteerHistoryResponseDTO[]` | History for opportunity and organisation profile UUIDs. |

## WebSocket Notes

The backend config enables a STOMP WebSocket broker. Exact runtime paths come from `WebSocketProperties`, so confirm configured values before implementing realtime notifications.

Known source files:

- `backend/project-vidalia-backend/src/main/java/com/vidalia/backend/websocket/WebSocketConfig.java`
- `backend/project-vidalia-backend/src/main/java/com/vidalia/backend/websocket/WebSocketProperties.java`
- `backend/project-vidalia-backend/src/main/java/com/vidalia/backend/service/NotificationService.java`

Frontend should treat WebSockets as a later enhancement unless realtime notifications are required for the first MVP.

## Frontend Integration Notes

### Suggested Auth Flow

1. Submit login or registration request.
2. Store `accessToken` and `refreshToken` according to the agreed auth-storage decision.
3. Call `GET /api/user/me` to retrieve the current user's role and profile-neutral account data.
4. Role-route the user:
   - `VOLUNTEER`: fetch `/api/volunteer-profile/me`
   - `ORGANISATION`: fetch `/api/organisation-profile/me`
   - `ADMIN`: use admin endpoints

### Known API Gaps / Backend Questions

| Area | Issue | Frontend impact |
| --- | --- | --- |
| Auth | `AuthResponse` does not include user role/profile. | Frontend must call `/api/user/me` after auth. |
| Opportunities | No volunteer/public endpoint to list all open opportunities. | Opportunity browsing may need backend support or rely on matching/organisation-specific lists. |
| Organisation profile | Response does not expose `verified` or `accountType`. | UI cannot show verification state unless backend adds it. |
| Volunteer profile | Update DTO does not expose `remoteOnly`, `longitude`, or `latitude`. | UI cannot update those matching-related fields unless backend adds them. |
| Volunteer history | Response does not expose history `logId`. | UI cannot call update endpoints for a selected row unless backend adds it. |
| Matching | Controller injection appears broken unless fields are made `final` or constructor is added. | Matching endpoint may fail at runtime until backend is fixed. |
| Labels/semantic tags | Services and DTOs exist, but no REST controllers were found. | Frontend cannot manage or assign labels through documented endpoints yet. |
