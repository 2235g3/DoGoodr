import { FormEvent, useEffect, useMemo, useState } from 'react'
import {
  createAdminUser,
  deleteAdminUser,
  getAdminUserByEmail,
  getAdminUsers,
  getAdminUsersByRole,
  updateAdminUser,
  updateAdminUserPassword,
} from '../../api/admin'
import type { Role, UserResponseDTO } from '../../api/types'
import { AdminNotice } from './AdminNotice'

type EditableUser = {
  email: string
  secondaryEmail: string
  phoneNumber: string
}

const roles: Role[] = ['ADMIN', 'VOLUNTEER', 'ORGANISATION']

export function AdminUsersPage() {
  const [users, setUsers] = useState<UserResponseDTO[]>([])
  const [edits, setEdits] = useState<Record<string, EditableUser>>({})
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')
  const [filterRole, setFilterRole] = useState<Role | 'ALL'>('ALL')
  const [emailSearch, setEmailSearch] = useState('')
  const [newEmail, setNewEmail] = useState('')
  const [newPassword, setNewPassword] = useState('')
  const [newRole, setNewRole] = useState<Role>('VOLUNTEER')
  const [passwordUserId, setPasswordUserId] = useState('')
  const [oldPassword, setOldPassword] = useState('')
  const [replacementPassword, setReplacementPassword] = useState('')

  const sortedUsers = useMemo(
    () => [...users].sort((a, b) => a.email.localeCompare(b.email)),
    [users],
  )

  useEffect(() => {
    loadUsers()
  }, [])

  async function loadUsers() {
    setError('')
    try {
      const nextUsers = await getAdminUsers()
      setUsers(nextUsers)
      setEdits(makeEditState(nextUsers))
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load users.')
    }
  }

  async function handleRoleFilter(role: Role | 'ALL') {
    setFilterRole(role)
    setError('')
    try {
      const nextUsers = role === 'ALL' ? await getAdminUsers() : await getAdminUsersByRole(role)
      setUsers(nextUsers)
      setEdits(makeEditState(nextUsers))
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to filter users.')
    }
  }

  async function handleEmailSearch(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (!emailSearch.trim()) {
      await loadUsers()
      return
    }
    setError('')
    try {
      const user = await getAdminUserByEmail(emailSearch.trim())
      setUsers([user])
      setEdits(makeEditState([user]))
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'No user found for that email.')
    }
  }

  async function handleCreateUser(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setMessage('')
    try {
      await createAdminUser({ email: newEmail, password: newPassword, role: newRole })
      setNewEmail('')
      setNewPassword('')
      setNewRole('VOLUNTEER')
      setMessage('User created.')
      await loadUsers()
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to create user.')
    }
  }

  async function handleSaveUser(id: string) {
    const edit = edits[id]
    if (!edit) return
    setError('')
    setMessage('')
    try {
      await updateAdminUser(id, {
        email: edit.email,
        secondaryEmail: edit.secondaryEmail || null,
        phoneNumber: edit.phoneNumber || null,
      })
      setMessage('User updated.')
      await loadUsers()
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to update user.')
    }
  }

  async function handleDeleteUser(id: string) {
    setError('')
    setMessage('')
    try {
      await deleteAdminUser(id)
      setMessage('User deleted.')
      await loadUsers()
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to delete user.')
    }
  }

  async function handlePasswordUpdate(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setMessage('')
    try {
      await updateAdminUserPassword(passwordUserId, {
        oldPassword,
        newPassword: replacementPassword,
      })
      setPasswordUserId('')
      setOldPassword('')
      setReplacementPassword('')
      setMessage('Password update request completed.')
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to update password.')
    }
  }

  function updateEdit(id: string, field: keyof EditableUser, value: string) {
    setEdits((current) => ({
      ...current,
      [id]: {
        ...current[id],
        [field]: value,
      },
    }))
  }

  return (
    <>
      <div className="admin-heading">
        <p className="eyebrow">User management</p>
        <h2>Users</h2>
        <p>Create, search, edit, and delete user accounts.</p>
      </div>

      {message ? <AdminNotice tone="success">{message}</AdminNotice> : null}
      {error ? <AdminNotice tone="error">{error}</AdminNotice> : null}

      <div className="admin-grid-two">
        <form className="admin-panel admin-form" onSubmit={handleCreateUser}>
          <h3>Create user</h3>
          <label>
            Email
            <input value={newEmail} onChange={(event) => setNewEmail(event.target.value)} required />
          </label>
          <label>
            Password
            <input
              type="password"
              value={newPassword}
              onChange={(event) => setNewPassword(event.target.value)}
              required
            />
          </label>
          <label>
            Role
            <select value={newRole} onChange={(event) => setNewRole(event.target.value as Role)}>
              {roles.map((role) => (
                <option key={role} value={role}>
                  {role}
                </option>
              ))}
            </select>
          </label>
          <button className="button button--primary" type="submit">
            Create
          </button>
        </form>

        <div className="admin-panel admin-form">
          <h3>Find users</h3>
          <form className="admin-inline-form" onSubmit={handleEmailSearch}>
            <input
              value={emailSearch}
              onChange={(event) => setEmailSearch(event.target.value)}
              placeholder="Search by email"
            />
            <button className="button button--secondary" type="submit">
              Search
            </button>
          </form>
          <label>
            Role filter
            <select
              value={filterRole}
              onChange={(event) => handleRoleFilter(event.target.value as Role | 'ALL')}
            >
              <option value="ALL">All roles</option>
              {roles.map((role) => (
                <option key={role} value={role}>
                  {role}
                </option>
              ))}
            </select>
          </label>
        </div>
      </div>

      <form className="admin-panel admin-form" onSubmit={handlePasswordUpdate}>
        <h3>Password update</h3>
        <div className="admin-grid-three">
          <input
            value={passwordUserId}
            onChange={(event) => setPasswordUserId(event.target.value)}
            placeholder="User id"
            required
          />
          <input
            type="password"
            value={oldPassword}
            onChange={(event) => setOldPassword(event.target.value)}
            placeholder="Old password"
            required
          />
          <input
            type="password"
            value={replacementPassword}
            onChange={(event) => setReplacementPassword(event.target.value)}
            placeholder="New password"
            required
          />
        </div>
        <button className="button button--secondary" type="submit">
          Update password
        </button>
      </form>

      <div className="admin-table-wrap">
        <table className="admin-table">
          <thead>
            <tr>
              <th>Email</th>
              <th>Role</th>
              <th>Secondary email</th>
              <th>Phone</th>
              <th>Created</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {sortedUsers.map((user) => (
              <tr key={user.id}>
                <td>
                  <input
                    value={edits[user.id]?.email ?? ''}
                    onChange={(event) => updateEdit(user.id, 'email', event.target.value)}
                  />
                  <small>{user.id}</small>
                </td>
                <td>{user.role}</td>
                <td>
                  <input
                    value={edits[user.id]?.secondaryEmail ?? ''}
                    onChange={(event) =>
                      updateEdit(user.id, 'secondaryEmail', event.target.value)
                    }
                  />
                </td>
                <td>
                  <input
                    value={edits[user.id]?.phoneNumber ?? ''}
                    onChange={(event) => updateEdit(user.id, 'phoneNumber', event.target.value)}
                  />
                </td>
                <td>{formatDate(user.createdAt)}</td>
                <td>
                  <div className="admin-row-actions">
                    <button type="button" onClick={() => handleSaveUser(user.id)}>
                      Save
                    </button>
                    <button type="button" onClick={() => handleDeleteUser(user.id)}>
                      Delete
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

function makeEditState(users: UserResponseDTO[]) {
  return users.reduce<Record<string, EditableUser>>((next, user) => {
    next[user.id] = {
      email: user.email,
      secondaryEmail: user.secondaryEmail ?? '',
      phoneNumber: user.phoneNumber ?? '',
    }
    return next
  }, {})
}

function formatDate(value?: string | null) {
  if (!value) return '...'
  return new Date(value).toLocaleDateString()
}
