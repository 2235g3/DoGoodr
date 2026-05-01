import type { AssignedLabelDTO, VolunteerProfileDTO } from '../api/types'

export type VolunteerProfileCompletion = {
  complete: boolean
  percent: number
  completedCount: number
  totalCount: number
  missing: string[]
}

export function calculateVolunteerProfileCompletion(
  profile: VolunteerProfileDTO,
  labels: AssignedLabelDTO[],
): VolunteerProfileCompletion {
  const checks = [
    { label: 'Contact email', done: Boolean(profile.contactEmail?.trim()) },
    { label: 'Profile description', done: Boolean(profile.profileDescription?.trim()) },
    { label: 'Availability', done: Boolean(profile.availability?.trim()) },
    {
      label: 'Location or coordinates',
      done: Boolean(profile.location?.trim()) || (profile.latitude != null && profile.longitude != null),
    },
    { label: 'Travel preference', done: profile.remoteOnly || profile.maxTravelDistance != null },
    { label: 'At least one interest or skill label', done: labels.length > 0 },
  ]

  const completedCount = checks.filter((check) => check.done).length
  const totalCount = checks.length

  return {
    complete: completedCount === totalCount,
    percent: Math.round((completedCount / totalCount) * 100),
    completedCount,
    totalCount,
    missing: checks.filter((check) => !check.done).map((check) => check.label),
  }
}
