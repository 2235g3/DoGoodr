# Frontend Planning and Design Log

This document is the shared planning space for the new React frontend. It records product vision, UX decisions, frontend technical decisions, open questions, and backend/API dependencies.

The intended workflow is:

- Backend + technical lead defines product direction, priorities, and backend constraints.
- Frontend lead translates that vision into screens, interaction design, frontend architecture, and implementation tasks.
- Decisions are recorded here before or alongside implementation.
- Open questions stay visible until answered.

## 1. Product Vision

### One-Sentence Product Goal

TODO: Describe what the application should help users accomplish in one clear sentence.

Example shape:

> Help volunteers find suitable opportunities and help organisations manage volunteering work with minimal friction.

### Target Users

| User type | Primary goal | Confidence | Notes |
| --- | --- | --- | --- |
| Volunteer | TODO | Low |  |
| Organisation | TODO | Low |  |
| Admin | TODO | Low |  |
| Public visitor | TODO | Low |  |

### Product Tone

Use this section to describe how the app should feel.

| Dimension | Decision |
| --- | --- |
| Overall feel | TODO |
| Formality | TODO |
| Density | TODO |
| Visual warmth | TODO |
| Accessibility expectations | TODO |

## 2. Scope

### MVP Features

These are required for the first usable frontend.

| Feature | User role | Priority | Status | Notes |
| --- | --- | --- | --- | --- |
| Login | All | Must | Not started |  |
| Register volunteer | Volunteer | Must | Not started |  |
| Register organisation | Organisation | Must | Not started |  |
| Volunteer dashboard | Volunteer | TODO | Not started |  |
| Opportunity browsing | Volunteer | TODO | Not started |  |
| Opportunity detail | Volunteer | TODO | Not started |  |
| Apply to opportunity | Volunteer | TODO | Not started |  |
| Volunteer profile | Volunteer | TODO | Not started |  |
| Organisation dashboard | Organisation | TODO | Not started |  |
| Create/edit opportunity | Organisation | TODO | Not started |  |
| Review applications | Organisation | TODO | Not started |  |
| Notifications | All authenticated roles | TODO | Not started |  |
| Admin management | Admin | TODO | Not started |  |

### Explicitly Out of Scope

| Item | Reason |
| --- | --- |
| TODO | TODO |

## 3. Role Workflows

### Public Visitor

Primary journey:

1. TODO
2. TODO
3. TODO

Important decisions:

- TODO

### Volunteer

Primary journey:

1. Register or log in.
2. After registration, enter an optional onboarding flow that completes profile details, location preferences, remote-only preference, CV/photo fields, and self-assigned labels.
3. If onboarding is skipped or incomplete, land on the volunteer dashboard with a persistent reminder that a complete profile unlocks better recommendations.
4. Browse/search opportunities and organisations as the main product action, using a search experience inspired by LinkedIn job search and Rightmove property search: list, rich cards, filters, sorting, and map context.
5. Save or update location from browser geolocation, or manually enter a location and coordinates. Remote-only remains a profile preference that can be changed later.
6. Apply to opportunities from browse/search/detail cards.
7. Once the volunteer profile is complete, use the matching service as a higher-confidence recommendations area.
8. Track applications, notifications, and volunteering history.
9. Share volunteering history externally through native share links and LinkedIn-style share URLs.

Important decisions:

- Volunteers should land on the dashboard after login. The dashboard becomes the command center and surfaces onboarding reminders.
- Profile completion should unlock matching, but it should not block browsing or applying.
- Opportunity discovery is the primary volunteer workflow. Matching is a premium/recommendation layer on top of browsing, not the only feed.
- Use OpenStreetMap/embedded map links first because it avoids API key setup and is enough for coursework UX. Google Maps can replace it later if the product needs richer map interactions.
- Use browser geolocation for current-location capture. The user can save coordinates into their profile or enter them manually.

### Organisation

Primary journey:

1. Register or log in.
2. Complete organisation profile.
3. Create opportunities.
4. Review applications.
5. Accept or reject applicants.
6. Record volunteering history.

Important decisions:

- TODO: Should unverified organisations be restricted?
- TODO: Should opportunity creation be wizard-style or single-form?

### Admin

Primary journey:

1. Log in.
2. Review users and profiles.
3. Verify organisations.
4. Manage platform data where required.

Important decisions:

- TODO: How much admin UI is required for the coursework deliverable?

## 4. Information Architecture

### Proposed Route Map

Routes are provisional until confirmed.

| Route | Access | Purpose | Status |
| --- | --- | --- | --- |
| `/` | Public | Public landing or redirect hub | Proposed |
| `/login` | Public | Sign in | Proposed |
| `/register/volunteer` | Public | Volunteer registration | Proposed |
| `/register/organisation` | Public | Organisation registration | Proposed |
| `/volunteer` | Volunteer | Volunteer dashboard | Proposed |
| `/volunteer/onboarding` | Volunteer | Guided profile completion and label selection | Proposed |
| `/volunteer/profile` | Volunteer | Profile editor | Proposed |
| `/volunteer/browse` | Volunteer | Browse/search opportunities and organisations with map context | Proposed |
| `/volunteer/opportunities/:id` | Volunteer | Opportunity detail | Proposed |
| `/volunteer/applications` | Volunteer | Application tracking | Proposed |
| `/volunteer/history` | Volunteer | Volunteering history | Proposed |
| `/organisation` | Organisation | Organisation dashboard | Proposed |
| `/organisation/profile` | Organisation | Organisation profile editor | Proposed |
| `/organisation/opportunities` | Organisation | Manage opportunities | Proposed |
| `/organisation/opportunities/new` | Organisation | Create opportunity | Proposed |
| `/organisation/opportunities/:id/edit` | Organisation | Edit opportunity | Proposed |
| `/organisation/applications` | Organisation | Review applications | Proposed |
| `/admin` | Admin | Admin dashboard | Proposed |
| `/admin/users` | Admin | User management | Proposed |
| `/admin/organisations` | Admin | Organisation verification | Proposed |

### Navigation Model

| Role | Primary nav items | Secondary nav items | Notes |
| --- | --- | --- | --- |
| Volunteer | TODO | TODO |  |
| Organisation | TODO | TODO |  |
| Admin | TODO | TODO |  |

## 5. Screen Design Notes

Each screen should be filled in before implementation starts or as the design becomes clear.

### Screen Template

Use this template when adding a new screen.

```text
Screen:
Role:
Goal:
Primary actions:
Secondary actions:
Data required:
Empty state:
Loading state:
Error state:
Mobile considerations:
Open backend questions:
```

### Volunteer Dashboard

Goal: Give the volunteer a quick read on profile completeness, applications, matches, and next actions.

Primary content:

- Profile completion reminder and progress.
- Primary action: browse opportunities.
- Secondary action: complete onboarding/profile.
- Stats: points, hours, applications, matches, unread notifications.
- Clear message that matching unlocks when profile information and labels are complete.

Open decisions:

- Whether matching should require a CV/profile picture, or only profile fields plus labels.

### Opportunity Browse

Goal: Make opportunity discovery useful even before the matching service is available.

Primary content:

- Search query over opportunity title, description, organisation name, and location.
- Filters for remote, organisation, required hours, date, status, and distance when coordinates exist.
- Sorts for newest, start date, closest, hours, and organisation.
- Results list with apply action and organisation context.
- Map panel using OpenStreetMap embed or marker links for results with coordinates.
- Organisation browse alongside opportunities.

Open decisions:

- Whether to persist saved searches.
- Whether organisations should have dedicated public profile pages.

### Organisation Dashboard

Goal: TODO

Primary content:

- TODO

Open decisions:

- TODO

## 6. Visual Design Direction

### Design Principles

- Clear over decorative.
- Fast to scan for repeated operational tasks.
- Warm enough for a volunteering product, but not sentimental.
- Accessible by default: readable contrast, keyboard-friendly controls, visible focus states, predictable forms.
- Mobile layouts should remain fully usable, not merely compressed desktop screens.

### Design Tokens

These are placeholders until the visual direction is confirmed.

| Token | Decision |
| --- | --- |
| Primary colour | TODO |
| Accent colour | TODO |
| Background | TODO |
| Text colour | TODO |
| Border colour | TODO |
| Success | TODO |
| Warning | TODO |
| Error | TODO |
| Font | TODO |
| Spacing scale | TODO |
| Corner radius | TODO |

### Component Style Notes

| Component | Direction |
| --- | --- |
| Buttons | TODO |
| Forms | TODO |
| Tables | TODO |
| Cards | TODO |
| Modals | TODO |
| Toasts | TODO |
| Navigation | TODO |
| Dashboard panels | TODO |

## 7. Frontend Technical Plan

### Proposed Stack

| Area | Proposed choice | Status | Notes |
| --- | --- | --- | --- |
| Build tool | Vite | Proposed |  |
| Language | TypeScript | Proposed |  |
| Framework | React | Proposed |  |
| Routing | React Router | Proposed |  |
| Server state | TanStack Query | Proposed | Requires dependency approval during setup |
| HTTP client | Axios or fetch wrapper | Proposed | Backend already uses token auth |
| Styling | Plain CSS or CSS Modules | Proposed | Keep design system lightweight |
| Forms | Native React first | Proposed | Add library only if forms become heavy |
| Validation | Zod optional | Proposed | Useful if API DTOs need runtime checks |
| Icons | lucide-react | Proposed | Lightweight and consistent |

### Folder Structure

```text
frontend-new/
  src/
    api/
    app/
    components/
      common/
      layout/
    features/
      admin/
      applications/
      auth/
      notifications/
      opportunities/
      profiles/
      volunteer-history/
    hooks/
    routes/
    styles/
    types/
```

### Frontend Architecture Rules

- Keep API calls in `src/api` or feature-specific API modules.
- Keep route-level screens in feature folders.
- Keep reusable layout and form controls in `src/components`.
- Keep authentication state separate from domain data.
- Prefer backend DTO-shaped TypeScript types until there is a reason to transform them.
- Do not edit or depend on the old `frontend/` implementation.

## 8. Backend/API Dependencies

This section is for questions the frontend lead needs answered by the backend + technical lead.

| Question | Area | Impact | Status | Answer |
| --- | --- | --- | --- | --- |
| What is the canonical base URL for local frontend development? | API | Needed for API client setup | Open |  |
| Should access and refresh tokens be stored in localStorage, sessionStorage, or another approach? | Auth | Affects auth implementation | Open |  |
| What should happen after login for each role? | Auth/UX | Affects routing | Open |  |
| Are organisations blocked from creating opportunities until verified? | Organisation | Affects UI permissions | Open |  |
| Which admin features are required in the first frontend version? | Admin | Affects MVP scope | Open |  |

## 9. Decision Log

Record decisions here as they are made.

| Date | Decision | Owner | Rationale |
| --- | --- | --- | --- |
| 2026-04-30 | Build the new frontend from scratch in `frontend-new/`. | Backend + technical lead | Avoid coupling to the current unfinished frontend. |
| 2026-04-30 | Treat Codex as frontend lead and user as backend + technical lead. | Both | Keeps implementation ownership and product guidance clear. |
| 2026-05-01 | Volunteer discovery becomes browse/search first, with matching unlocked by profile completion. | Backend + technical lead | Volunteers need a useful app before the recommendation data is complete; matching should reward richer profiles without blocking core use. |
| 2026-05-01 | Use OpenStreetMap and browser geolocation for the first map/location implementation. | Frontend lead | Avoids API-key setup and keeps the coursework build portable. |

## 10. Open Questions

Questions that block design or implementation should live here until resolved.

| Question | Needed by | Priority | Status |
| --- | --- | --- | --- |
| What is the desired first MVP journey: volunteer-first, organisation-first, or balanced? | Route and screen planning | High | Open |
| Should the app have a public marketing-style landing page, or should `/` route users quickly into login/register? | Public UX | Medium | Open |
| What visual references or comparable products should guide the design? | Visual direction | Medium | Open |
| What deadline or coursework milestone should the frontend plan optimize for? | Implementation sequencing | High | Open |
| Should profile completion require a CV and profile picture, or only core fields and labels? | Matching gate | Medium | Open |
| Should social sharing create a public share page, or only prefilled share intents for now? | Volunteer history sharing | Medium | Open |

## 11. Implementation Phases

### Phase 0: Planning

Status: In progress

Goals:

- Confirm product scope.
- Confirm route map.
- Confirm visual direction.
- Confirm backend/API assumptions.

### Phase 1: Scaffold

Status: Not started

Goals:

- Create Vite React TypeScript app in `frontend-new/`.
- Add routing.
- Add global layout.
- Add design tokens.
- Add API client.
- Add auth state.

### Phase 2: First Vertical Slice

Status: Not started

Recommended slice:

1. Login.
2. Token handling.
3. Role-aware redirect.
4. Authenticated dashboard shell.
5. Logout.

### Phase 3: Core Role Workflows

Status: Not started

Recommended order:

1. Volunteer profile.
2. Opportunity browsing and detail.
3. Applications.
4. Organisation opportunity management.
5. Organisation application review.

### Phase 4: Polish and QA

Status: Not started

Goals:

- Responsive review.
- Empty/loading/error states.
- Accessibility pass.
- Browser testing.
- Backend integration testing.
