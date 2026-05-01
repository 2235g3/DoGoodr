export const availabilityOptions = [
  { value: 'weekday-mornings', label: 'Weekday mornings' },
  { value: 'weekday-afternoons', label: 'Weekday afternoons' },
  { value: 'weekday-evenings', label: 'Weekday evenings' },
  { value: 'weekends', label: 'Weekends' },
  { value: 'one-off-events', label: 'One-off events' },
  { value: 'recurring-weekly', label: 'Recurring weekly' },
  { value: 'school-holidays', label: 'School holidays' },
  { value: 'flexible', label: 'Flexible' },
]

export function parseAvailability(value?: string | null) {
  if (!value) return []

  return value
    .split(/[\n,]/)
    .map((token) => token.trim())
    .filter(Boolean)
}

export function hasAvailability(value: string, token: string) {
  return parseAvailability(value).includes(token)
}

export function toggleAvailability(value: string, token: string, checked: boolean) {
  const tokens = parseAvailability(value).filter((currentToken) => currentToken !== token)
  if (checked) tokens.push(token)
  return availabilityOptions
    .map((option) => option.value)
    .filter((option) => tokens.includes(option))
    .join(', ')
}

export function formatAvailability(value?: string | null) {
  const tokens = parseAvailability(value)
  if (!tokens.length) return 'Flexible'

  return tokens
    .map((token) => availabilityOptions.find((option) => option.value === token)?.label ?? token)
    .join(', ')
}
