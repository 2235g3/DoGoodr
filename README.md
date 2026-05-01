# DoGoodr

DoGoodr is a web platform for connecting volunteers with suitable opportunities, helping organisations manage applications, and tracking the social impact created through volunteering work.

The project was built for the **Green Tech Jam** as part of the Engineers and Scientists in Business Fellowship Competition https://esbf.org.uk/. The core idea is a volunteer board with matchmaking and engagement mechanics: volunteers build a profile around their skills, interests, availability, location, and experience, while organisations create opportunities and review applications. The platform then uses server-side matching to recommend relevant opportunities and records completed volunteering through hours, points, and history.

## Project Goal

DoGoodr aims to reduce the friction between wanting to volunteer and finding a meaningful place to contribute.

Volunteers often struggle to find opportunities that match their skills, interests, availability, and location. Organisations also need better tools to advertise opportunities, manage candidates, and recognise volunteer contributions. DoGoodr addresses this by combining opportunity browsing, profile-based matching, applications, notifications, and impact tracking in one system.

## Core Features

- Public homepage with login and role-based sign-up journeys.
- Volunteer registration, onboarding, profile editing, CV upload, and profile picture upload.
- Organisation registration, profile management, opportunity creation, and application review.
- Admin dashboard for users, profiles, opportunities, applications, volunteer history, labels, and semantic tags.
- Opportunity browsing with filters, sorting, organisation browsing, and map context.
- Server-side matching algorithm using volunteer labels, opportunity labels, availability, remote preference, age, and travel distance.
- Volunteer applications and organisation-side application status management.
- Volunteer history with hours, points, organisation comments, and LinkedIn-style sharing/export text.
- Real-time notification support through WebSockets.
- File upload support for profile pictures and CVs.

## Assessment Requirements

| Requirement | How DoGoodr Meets It |
| --- | --- |
| Data gathering component | Collects volunteer profiles, organisation profiles, opportunity data, applications, labels, location preferences, availability, uploaded files, and volunteering history. |
| Server-side data analytics | Runs matching and scoring logic on the backend, calculates points/history summaries, and exposes role-specific dashboards. |
| Deployment | Designed to be deployed through the provided repository and CI/CD workflow. The backend includes Docker support for app/database deployment. |
| External APIs/frameworks | Uses React, Spring Boot, PostgreSQL, browser Geolocation, OpenStreetMap embeds/links, and LinkedIn share/export flow. |
| Real-time streaming | Uses Spring WebSockets for real-time notification delivery beyond simple request-response polling. |

## Challenge Context

This project responds to the problem statement:

> How can we create a more effective way to match volunteers with opportunities? How can we measure their social impact?

The challenge aligns with:

- **SDG 3:** Good Health and Wellbeing
- **SDG 10:** Reduced Inequalities

DoGoodr focuses on:

- Personalised volunteer recommendations.
- Better visibility of volunteering history.
- Recognition through points and future badge/reward mechanics.
- Organisation tools for managing opportunities and applications.
- Individual and community impact tracking.

## Product Flow

### Volunteer Flow

1. Register as a volunteer.
2. Complete onboarding with profile details, availability, location preferences, remote-only preference, and labels.
3. Browse and search opportunities and organisations.
4. Use current location or saved location for distance-aware discovery.
5. Generate matched opportunities once the profile is complete.
6. Apply for opportunities.
7. Track applications, notifications, volunteering history, hours, and points.
8. Share volunteering history through native sharing or LinkedIn-style export.

### Organisation Flow

1. Register as an organisation.
2. Complete the organisation profile.
3. Create opportunities with availability, location/remote status, and matching labels.
4. Review volunteer applications.
5. Update application statuses.
6. Record volunteering history, hours, points, and organisation comments.

### Admin Flow

1. Log in with an admin account.
2. Review users and account roles.
3. Inspect volunteer and organisation profiles.
4. Verify organisations.
5. Review opportunities, applications, and volunteering history.
6. Manage matching taxonomy through labels, semantic tags, and semantic links.

## Tech Stack

| Layer | Technology |
| --- | --- |
| Frontend | React, TypeScript, Vite, React Router |
| Backend | Java, Spring Boot, Spring Security |
| Database | PostgreSQL for Docker/prod-style setup, H2 for dev/test profile |
| Authentication | JWT access and refresh tokens |
| Real-time | Spring WebSockets |
| Mapping/location | Browser Geolocation and OpenStreetMap |
| Sharing | Browser share/clipboard APIs and LinkedIn share URL |
| Build/deployment support | Docker, Docker Compose, Maven wrapper, npm scripts |

## Repository Structure

```text
.
├── backend/
│   └── project-vidalia-backend/   # Spring Boot backend, Docker config, migrations, uploads
├── frontend/                      # Current React frontend
├── legacy/
│   └── legacy-frontend/           # Original frontend kept for reference
├── documentation/                 # Planning, logo, and brainstorming material
├── infrastructure/                # Deployment/infrastructure material
└── README.md                      # Original repository README placeholder
```

## Running Locally

### Prerequisites

- Node.js and npm
- Java 17 if running the backend directly
- Docker and Docker Compose for the recommended backend/database setup

### Backend with Docker

From the backend project directory:

```bash
cd backend/project-vidalia-backend
cp .env.example .env
docker-compose up --build
```

The backend runs at:

```text
http://localhost:8080
```

Uploaded profile pictures and CVs are stored through the backend upload configuration.

### Backend in Dev Mode

For quick local development with H2:

```bash
cd backend/project-vidalia-backend
export SPRING_PROFILES_ACTIVE=dev
./mvnw spring-boot:run
```

### Frontend

From the frontend directory:

```bash
cd frontend
npm install
npm run dev
```

Vite will show the local frontend URL in the terminal, usually:

```text
http://localhost:5173
```

If that port is busy, Vite will use another nearby port.

The frontend API base URL defaults to:

```text
http://localhost:8080
```

To override it:

```bash
VITE_API_BASE_URL=http://localhost:8080 npm run dev
```

## Useful Routes

| Route | Purpose |
| --- | --- |
| `/` | Public homepage |
| `/login` | Login |
| `/get-started` | Choose volunteer or organisation sign-up |
| `/signup/volunteer` | Volunteer registration |
| `/signup/organisation` | Organisation registration |
| `/volunteer` | Volunteer dashboard |
| `/volunteer/onboarding` | Volunteer onboarding/profile completion |
| `/volunteer/profile` | Volunteer profile editor |
| `/volunteer/matches` | Browse/search opportunities and generate matches |
| `/volunteer/applications` | Volunteer applications |
| `/volunteer/notifications` | Volunteer notifications |
| `/volunteer/history` | Volunteer history, points, hours, and sharing |
| `/organisation` | Organisation dashboard |
| `/organisation/profile` | Organisation profile editor |
| `/organisation/opportunities` | Organisation opportunity management |
| `/organisation/applications` | Organisation application review |
| `/organisation/history` | Organisation volunteer history records |
| `/organisation/notifications` | Organisation notifications |
| `/admin` | Admin dashboard |
| `/admin/users` | Admin user management |
| `/admin/profiles` | Admin profile review and organisation verification |
| `/admin/opportunities` | Admin opportunity review |
| `/admin/applications` | Admin application review |
| `/admin/history` | Admin volunteering history review |
| `/admin/taxonomy` | Admin label and semantic tag management |

## Testing

### Frontend Build

```bash
cd frontend
npm run build
```

### Backend Tests

```bash
cd backend/project-vidalia-backend
./mvnw test
```

The backend test suite covers matching logic, notification services, repository behaviour, exception handling, volunteer history, and WebSocket notification integration.

## API Overview

The frontend calls the Spring backend through REST endpoints under:

```text
http://localhost:8080/api
```

Authentication endpoints include:

- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/register/volunteer`
- `POST /api/auth/register/organisation`

Protected API calls use:

```http
Authorization: Bearer <accessToken>
```

For a more detailed frontend-facing API reference, see:

```text
frontend/API_REFERENCE.md
```

## Matching and Impact

The matching service is designed to be more than a keyword search. Volunteers and organisations assign structured labels, and the backend compares those labels alongside availability, remote preference, age suitability, and distance constraints.

The product vision is:

- Volunteers can browse freely at any time.
- Completing a richer profile unlocks stronger recommendations.
- Organisations receive better-aligned applicants.
- Volunteering history becomes a personal record of contribution.
- Points and future badges/rewards encourage long-term engagement.

Future development could improve the matching algorithm by learning from user behaviour, completed volunteering history, application outcomes, and organisation feedback.

## Known Alpha Notes

- LinkedIn export currently prepares and copies share text, then opens LinkedIn sharing for user review. It does not directly post to LinkedIn.
- Map display uses OpenStreetMap and depends on opportunities having latitude/longitude data.
- Browser location lookup requires the user to grant location permission.
- Some future engagement mechanics, such as richer badges, rewards, certificates, and community impact summaries which are part of the wider product vision are not implemented. The current state of the app is proof-of-concept / MVP.


## Future Improvements

- Learning-based improvements to the matching algorithm.
- More advanced impact summaries inspired by yearly contribution recaps.
- Badge, certificate, and reward systems.
- Public volunteer/organisation profiles.
- Saved searches and notification preferences.
- Stronger accessibility and responsive testing.
- Richer map interactions with a dedicated maps API if needed.

