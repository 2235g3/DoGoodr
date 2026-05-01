import { useEffect, useMemo, useState } from 'react'
import { getVolunteerHistory } from '../../api/volunteer'
import type { VolunteerHistoryDTO } from '../../api/types'
import { VolunteerNotice } from './VolunteerNotice'

export function VolunteerHistoryPage() {
  const [history, setHistory] = useState<VolunteerHistoryDTO[]>([])
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const totals = useMemo(
    () => ({
      hours: history.reduce((total, item) => total + item.hoursLogged, 0),
      points: history.reduce((total, item) => total + item.pointsGained, 0),
      organisations: new Set(history.map((item) => item.organisationId)).size,
    }),
    [history],
  )

  useEffect(() => {
    loadHistory()
  }, [])

  async function loadHistory() {
    setError('')
    try {
      setHistory(await getVolunteerHistory())
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load history.')
    }
  }

  async function handleShareSummary() {
    const shareText = buildLinkedInSummaryText(history, totals)
    await shareTextValue(shareText, setMessage)
  }

  async function handleShareItem(item: VolunteerHistoryDTO) {
    const shareText = buildLinkedInExperienceText(item)
    await shareTextValue(shareText, setMessage)
  }

  async function handleLinkedInExport(text: string) {
    const linkedInWindow = window.open(makeLinkedInShareUrl(), '_blank', 'noopener,noreferrer')
    await copyTextValue(text)
    setMessage(
      linkedInWindow
        ? 'LinkedIn experience text copied. Paste it into the LinkedIn post composer to share your contribution.'
        : 'LinkedIn experience text copied. Open LinkedIn and paste it into a post to share your contribution.',
    )
  }

  return (
    <>
      <div className="admin-heading">
        <p className="eyebrow">History</p>
        <h2>Volunteering record</h2>
        <p>Review completed volunteering records, logged hours, points, and organisation notes.</p>
      </div>

      {message ? <VolunteerNotice tone="success">{message}</VolunteerNotice> : null}
      {error ? <VolunteerNotice tone="error">{error}</VolunteerNotice> : null}

      <div className="admin-stat-grid volunteer-stat-grid">
        <div className="admin-stat-card volunteer-stat-card">
          <span>Total hours</span>
          <strong>{totals.hours}</strong>
        </div>
        <div className="admin-stat-card volunteer-stat-card">
          <span>Points gained</span>
          <strong>{totals.points}</strong>
        </div>
        <div className="admin-stat-card volunteer-stat-card">
          <span>Organisations</span>
          <strong>{totals.organisations}</strong>
        </div>
      </div>

      <section className="admin-panel history-share-panel">
        <div>
          <p className="eyebrow">Share your impact</p>
          <h3>Share your contributions to LinkedIn.</h3>
          <p>
            Export a polished volunteering experience update. We copy the text first, then open
            LinkedIn so you can review it before posting.
          </p>
        </div>
        <div className="admin-row-actions">
          <button
            className="button button--primary"
            type="button"
            onClick={() => handleLinkedInExport(buildLinkedInSummaryText(history, totals))}
            disabled={history.length === 0}
          >
            Export to LinkedIn
          </button>
          <button className="button button--secondary" type="button" onClick={handleShareSummary}>
            Share summary
          </button>
        </div>
      </section>

      <div className="admin-table-wrap">
        <table className="admin-table">
          <thead>
            <tr>
              <th>Opportunity</th>
              <th>Organisation</th>
              <th>Date range</th>
              <th>Hours</th>
              <th>Points</th>
              <th>Comment</th>
              <th>Share</th>
            </tr>
          </thead>
          <tbody>
            {history.map((item) => (
              <tr key={`${item.opportunityId}-${item.organisationId}-${item.startDate}`}>
                <td>
                  {item.opportunityTitle}
                  <small>{item.opportunityId}</small>
                </td>
                <td>{item.organisationName}</td>
                <td>
                  {formatDate(item.startDate)} - {formatDate(item.endDate)}
                </td>
                <td>{item.hoursLogged}</td>
                <td>{item.pointsGained}</td>
                <td>{item.organisationComment || '...'}</td>
                <td>
                  <div className="admin-row-actions">
                    <button type="button" onClick={() => handleShareItem(item)}>
                      Share
                    </button>
                    <button
                      type="button"
                      onClick={() => handleLinkedInExport(buildLinkedInExperienceText(item))}
                    >
                      LinkedIn
                    </button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  )
}

async function shareTextValue(text: string, onShared: (message: string) => void) {
  if (navigator.share) {
    await navigator.share({
      title: 'My DoGoodr volunteering experience',
      text,
      url: window.location.origin,
    })
    onShared('Share sheet opened.')
    return
  }

  await copyTextValue(text)
  onShared('Share text copied to clipboard.')
}

async function copyTextValue(text: string) {
  await navigator.clipboard.writeText(text)
}

function makeLinkedInShareUrl() {
  const url = new URL('https://www.linkedin.com/sharing/share-offsite/')
  url.searchParams.set('url', window.location.origin)
  return url.toString()
}

function buildLinkedInSummaryText(
  history: VolunteerHistoryDTO[],
  totals: { hours: number; organisations: number; points: number },
) {
  const recentHighlights = history
    .slice(0, 3)
    .map((item) => `- ${item.opportunityTitle} with ${item.organisationName}: ${item.hoursLogged} hours`)
    .join('\n')

  return [
    'I am proud to share my volunteering experience through DoGoodr.',
    '',
    `I have contributed ${totals.hours} hours across ${totals.organisations} organisation${totals.organisations === 1 ? '' : 's'}, earning ${totals.points} community impact points.`,
    recentHighlights ? `\nRecent highlights:\n${recentHighlights}` : '',
    '',
    'Volunteering has helped me build practical experience, support meaningful community work, and keep showing up where help is needed.',
  ]
    .filter(Boolean)
    .join('\n')
}

function buildLinkedInExperienceText(item: VolunteerHistoryDTO) {
  return [
    `I volunteered with ${item.organisationName} as part of "${item.opportunityTitle}".`,
    '',
    `Contribution: ${item.hoursLogged} hours`,
    `Dates: ${formatDate(item.startDate)} to ${formatDate(item.endDate)}`,
    item.organisationComment ? `Organisation note: ${item.organisationComment}` : '',
    '',
    'Grateful for the chance to contribute to meaningful community work through DoGoodr.',
  ]
    .filter(Boolean)
    .join('\n')
}

function formatDate(value?: string | null) {
  if (!value) return '...'
  return new Date(value).toLocaleDateString()
}
