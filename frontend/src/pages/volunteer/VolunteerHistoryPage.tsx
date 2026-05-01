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
    const shareText = `I have logged ${totals.hours} volunteering hours with ${totals.organisations} organisations through DoGoodr.`
    await shareTextValue(shareText, setMessage)
  }

  async function handleShareItem(item: VolunteerHistoryDTO) {
    const shareText = `I volunteered ${item.hoursLogged} hours with ${item.organisationName} on ${item.opportunityTitle}.`
    await shareTextValue(shareText, setMessage)
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
          <h3>Turn your volunteering record into an experience update.</h3>
          <p>
            Share a summary directly from your browser, or open a LinkedIn share intent with your
            volunteering highlights.
          </p>
        </div>
        <div className="admin-row-actions">
          <button className="button button--primary" type="button" onClick={handleShareSummary}>
            Share summary
          </button>
          <a
            className="button button--secondary"
            href={makeLinkedInShareUrl(
              `I have logged ${totals.hours} volunteering hours with ${totals.organisations} organisations through DoGoodr.`,
            )}
            target="_blank"
            rel="noreferrer"
          >
            LinkedIn
          </a>
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
                    <a
                      href={makeLinkedInShareUrl(
                        `I volunteered ${item.hoursLogged} hours with ${item.organisationName} on ${item.opportunityTitle}.`,
                      )}
                      target="_blank"
                      rel="noreferrer"
                    >
                      LinkedIn
                    </a>
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

  await navigator.clipboard.writeText(text)
  onShared('Share text copied to clipboard.')
}

function makeLinkedInShareUrl(text: string) {
  const url = new URL('https://www.linkedin.com/sharing/share-offsite/')
  url.searchParams.set('url', window.location.origin)
  url.searchParams.set('summary', text)
  return url.toString()
}

function formatDate(value?: string | null) {
  if (!value) return '...'
  return new Date(value).toLocaleDateString()
}
