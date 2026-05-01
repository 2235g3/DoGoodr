import { FormEvent, useState } from 'react'
import { getStoredUser, storeCurrentUser } from '../../api/auth'
import { updateCurrentUser, updateCurrentUserPassword } from '../../api/volunteer'
import { VolunteerNotice } from './VolunteerNotice'

export function VolunteerAccountPage() {
  const storedUser = getStoredUser()
  const [email, setEmail] = useState(storedUser?.email ?? '')
  const [secondaryEmail, setSecondaryEmail] = useState(storedUser?.secondaryEmail ?? '')
  const [phoneNumber, setPhoneNumber] = useState(storedUser?.phoneNumber ?? '')
  const [oldPassword, setOldPassword] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  async function handleDetailsSave(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setMessage('')
    try {
      const nextUser = await updateCurrentUser({
        email,
        secondaryEmail: secondaryEmail || null,
        phoneNumber: phoneNumber || null,
      })
      storeCurrentUser(nextUser)
      setMessage('Account details updated.')
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to update account.')
    }
  }

  async function handlePasswordSave(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setMessage('')
    try {
      await updateCurrentUserPassword({ oldPassword, newPassword })
      setOldPassword('')
      setNewPassword('')
      setMessage('Password updated.')
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to update password.')
    }
  }

  return (
    <>
      <div className="admin-heading">
        <p className="eyebrow">Account</p>
        <h2>Account settings</h2>
        <p>Manage your login email, contact details, and password.</p>
      </div>

      {message ? <VolunteerNotice tone="success">{message}</VolunteerNotice> : null}
      {error ? <VolunteerNotice tone="error">{error}</VolunteerNotice> : null}

      <div className="admin-grid-two">
        <form className="admin-panel admin-form" onSubmit={handleDetailsSave}>
          <h3>Contact details</h3>
          <label>
            Email
            <input type="email" value={email} onChange={(event) => setEmail(event.target.value)} required />
          </label>
          <label>
            Secondary email
            <input
              type="email"
              value={secondaryEmail}
              onChange={(event) => setSecondaryEmail(event.target.value)}
            />
          </label>
          <label>
            Phone number
            <input
              value={phoneNumber}
              onChange={(event) => setPhoneNumber(event.target.value)}
              placeholder="+44123123456"
            />
          </label>
          <button className="button button--primary" type="submit">
            Save details
          </button>
        </form>

        <form className="admin-panel admin-form" onSubmit={handlePasswordSave}>
          <h3>Password</h3>
          <label>
            Current password
            <input
              type="password"
              value={oldPassword}
              onChange={(event) => setOldPassword(event.target.value)}
              required
            />
          </label>
          <label>
            New password
            <input
              type="password"
              value={newPassword}
              onChange={(event) => setNewPassword(event.target.value)}
              required
            />
          </label>
          <button className="button button--secondary" type="submit">
            Update password
          </button>
        </form>
      </div>
    </>
  )
}
