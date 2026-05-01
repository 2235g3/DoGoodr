import { FormEvent, useEffect, useMemo, useState } from 'react'
import {
  createAdminLabel,
  createAdminSemanticTag,
  deleteAdminLabel,
  deleteAdminSemanticTag,
  getAdminLabels,
  getAdminSemanticTags,
  setAdminSemanticRelationship,
  updateAdminLabel,
  updateAdminSemanticTag,
} from '../../api/admin'
import type { CreateLabelDTO, LabelDTO, LabelType, SemanticTagDTO } from '../../api/types'
import { AdminNotice } from './AdminNotice'

type LabelForm = {
  id: string
  name: string
  type: LabelType
  required: boolean
  semanticTagId: string
}

type TagForm = {
  id: string
  name: string
}

type RelationshipForm = {
  oneId: string
  twoId: string
  weight: string
}

const labelTypes: LabelType[] = ['SKILL', 'INTEREST', 'CAUSE', 'LANGUAGE', 'EDUCATION', 'OTHER']

export function AdminTaxonomyPage() {
  const [labels, setLabels] = useState<LabelDTO[]>([])
  const [semanticTags, setSemanticTags] = useState<SemanticTagDTO[]>([])
  const [labelForm, setLabelForm] = useState<LabelForm>(emptyLabelForm)
  const [tagForm, setTagForm] = useState<TagForm>(emptyTagForm)
  const [relationshipForm, setRelationshipForm] = useState<RelationshipForm>(emptyRelationshipForm)
  const [message, setMessage] = useState('')
  const [error, setError] = useState('')

  const taxonomyHealth = useMemo(() => {
    const labelsWithoutTags = labels.filter((label) => !label.semanticTag).length
    const requiredLabels = labels.filter((label) => label.required).length
    return { labelsWithoutTags, requiredLabels }
  }, [labels])

  useEffect(() => {
    loadTaxonomy()
  }, [])

  async function loadTaxonomy() {
    setError('')
    try {
      const [nextLabels, nextTags] = await Promise.all([getAdminLabels(), getAdminSemanticTags()])
      setLabels(nextLabels)
      setSemanticTags(nextTags)
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to load taxonomy.')
    }
  }

  async function handleSaveLabel(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setMessage('')

    try {
      const payload = makeLabelPayload(labelForm, semanticTags)
      if (labelForm.id) {
        await updateAdminLabel(Number(labelForm.id), payload)
        setMessage('Label updated.')
      } else {
        await createAdminLabel(payload)
        setMessage('Label created.')
      }
      setLabelForm(emptyLabelForm)
      await loadTaxonomy()
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to save label.')
    }
  }

  async function handleDeleteLabel(id: number) {
    setError('')
    setMessage('')
    try {
      await deleteAdminLabel(id)
      setMessage('Label deleted.')
      await loadTaxonomy()
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to delete label.')
    }
  }

  async function handleSaveTag(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setError('')
    setMessage('')

    try {
      if (tagForm.id) {
        await updateAdminSemanticTag(Number(tagForm.id), { name: tagForm.name })
        setMessage('Semantic tag updated.')
      } else {
        await createAdminSemanticTag({ name: tagForm.name })
        setMessage('Semantic tag created.')
      }
      setTagForm(emptyTagForm)
      await loadTaxonomy()
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to save semantic tag.')
    }
  }

  async function handleDeleteTag(id: number) {
    setError('')
    setMessage('')
    try {
      await deleteAdminSemanticTag(id)
      setMessage('Semantic tag deleted and labels were unlinked.')
      await loadTaxonomy()
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to delete semantic tag.')
    }
  }

  async function handleSaveRelationship(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    const one = semanticTags.find((tag) => tag.id === Number(relationshipForm.oneId))
    const two = semanticTags.find((tag) => tag.id === Number(relationshipForm.twoId))
    if (!one?.id || !two?.id) {
      setError('Choose two semantic tags to connect.')
      return
    }

    setError('')
    setMessage('')
    try {
      await setAdminSemanticRelationship({
        one: { id: one.id, name: one.name },
        two: { id: two.id, name: two.name },
        weight: Number(relationshipForm.weight),
      })
      setRelationshipForm(emptyRelationshipForm)
      setMessage('Semantic relationship saved.')
    } catch (caughtError) {
      setError(caughtError instanceof Error ? caughtError.message : 'Unable to save relationship.')
    }
  }

  return (
    <>
      <div className="admin-heading">
        <p className="eyebrow">Matching taxonomy</p>
        <h2>Labels and semantic tags</h2>
        <p>
          Maintain the vocabulary that powers volunteer preferences, opportunity labels, and
          semantic matching.
        </p>
      </div>

      {message ? <AdminNotice tone="success">{message}</AdminNotice> : null}
      {error ? <AdminNotice tone="error">{error}</AdminNotice> : null}

      <div className="admin-stat-grid taxonomy-stat-grid">
        <div className="admin-stat-card">
          <span>Labels</span>
          <strong>{labels.length}</strong>
        </div>
        <div className="admin-stat-card">
          <span>Semantic tags</span>
          <strong>{semanticTags.length}</strong>
        </div>
        <div className="admin-stat-card">
          <span>Required labels</span>
          <strong>{taxonomyHealth.requiredLabels}</strong>
        </div>
        <div className="admin-stat-card">
          <span>Labels without tags</span>
          <strong>{taxonomyHealth.labelsWithoutTags}</strong>
        </div>
      </div>

      <div className="admin-grid-two">
        <form className="admin-panel admin-form" onSubmit={handleSaveLabel}>
          <h3>{labelForm.id ? 'Edit label' : 'Create label'}</h3>
          <label>
            Name
            <input value={labelForm.name} onChange={(event) => setLabelForm({ ...labelForm, name: event.target.value })} required />
          </label>
          <label>
            Type
            <select value={labelForm.type} onChange={(event) => setLabelForm({ ...labelForm, type: event.target.value as LabelType })}>
              {labelTypes.map((type) => (
                <option key={type} value={type}>
                  {formatEnum(type)}
                </option>
              ))}
            </select>
          </label>
          <label>
            Semantic tag
            <select value={labelForm.semanticTagId} onChange={(event) => setLabelForm({ ...labelForm, semanticTagId: event.target.value })}>
              <option value="">No semantic tag</option>
              {semanticTags.map((tag) => (
                <option key={tag.id} value={tag.id}>
                  {tag.name}
                </option>
              ))}
            </select>
          </label>
          <label className="volunteer-toggle">
            <input
              type="checkbox"
              checked={labelForm.required}
              onChange={(event) => setLabelForm({ ...labelForm, required: event.target.checked })}
            />
            Required for matching
          </label>
          <div className="admin-row-actions">
            <button className="button button--primary taxonomy-action-button" type="submit">
              {labelForm.id ? 'Update label' : 'Create label'}
            </button>
            {labelForm.id ? (
              <button className="button button--secondary" type="button" onClick={() => setLabelForm(emptyLabelForm)}>
                Cancel
              </button>
            ) : null}
          </div>
        </form>

        <form className="admin-panel admin-form" onSubmit={handleSaveTag}>
          <h3>{tagForm.id ? 'Edit semantic tag' : 'Create semantic tag'}</h3>
          <label>
            Name
            <input value={tagForm.name} onChange={(event) => setTagForm({ ...tagForm, name: event.target.value })} required />
          </label>
          <div className="admin-row-actions">
            <button className="button button--primary taxonomy-action-button" type="submit">
              {tagForm.id ? 'Update tag' : 'Create tag'}
            </button>
            {tagForm.id ? (
              <button className="button button--secondary" type="button" onClick={() => setTagForm(emptyTagForm)}>
                Cancel
              </button>
            ) : null}
          </div>
        </form>
      </div>

      <form className="admin-panel admin-form taxonomy-relationship-form" onSubmit={handleSaveRelationship}>
        <h3>Semantic relationship</h3>
        <p>Connect related semantic tags so the matching model can treat nearby ideas as similar.</p>
        <div className="admin-grid-three">
          <label>
            First tag
            <select value={relationshipForm.oneId} onChange={(event) => setRelationshipForm({ ...relationshipForm, oneId: event.target.value })} required>
              <option value="">Choose tag</option>
              {semanticTags.map((tag) => (
                <option key={tag.id} value={tag.id}>
                  {tag.name}
                </option>
              ))}
            </select>
          </label>
          <label>
            Second tag
            <select value={relationshipForm.twoId} onChange={(event) => setRelationshipForm({ ...relationshipForm, twoId: event.target.value })} required>
              <option value="">Choose tag</option>
              {semanticTags.map((tag) => (
                <option key={tag.id} value={tag.id}>
                  {tag.name}
                </option>
              ))}
            </select>
          </label>
          <label>
            Weight
            <input
              min="0"
              max="1"
              step="0.05"
              type="number"
              value={relationshipForm.weight}
              onChange={(event) => setRelationshipForm({ ...relationshipForm, weight: event.target.value })}
              required
            />
          </label>
        </div>
        <button className="button button--primary taxonomy-action-button" type="submit">
          Save relationship
        </button>
      </form>

      <div className="admin-grid-two">
        <section className="admin-panel">
          <h3>Labels</h3>
          <div className="taxonomy-list">
            {labels.map((label) => (
              <article className="taxonomy-row" key={label.id}>
                <div>
                  <strong>{label.name}</strong>
                  <span>
                    {formatEnum(label.type)} · {label.semanticTag || 'No semantic tag'} ·{' '}
                    {label.required ? 'Required' : 'Optional'}
                  </span>
                </div>
                <div className="admin-row-actions">
                  <button type="button" onClick={() => setLabelForm(makeLabelForm(label, semanticTags))}>
                    Edit
                  </button>
                  <button type="button" onClick={() => handleDeleteLabel(label.id)}>
                    Delete
                  </button>
                </div>
              </article>
            ))}
          </div>
        </section>

        <section className="admin-panel">
          <h3>Semantic tags</h3>
          <div className="taxonomy-list">
            {semanticTags.map((tag) => (
              <article className="taxonomy-row" key={tag.id}>
                <div>
                  <strong>{tag.name}</strong>
                  <span>{labels.filter((label) => label.semanticTag === tag.name).length} linked labels</span>
                </div>
                <div className="admin-row-actions">
                  <button type="button" onClick={() => setTagForm({ id: String(tag.id), name: tag.name })}>
                    Edit
                  </button>
                  {tag.id ? (
                    <button type="button" onClick={() => handleDeleteTag(tag.id!)}>
                      Delete
                    </button>
                  ) : null}
                </div>
              </article>
            ))}
          </div>
        </section>
      </div>
    </>
  )
}

const emptyLabelForm: LabelForm = {
  id: '',
  name: '',
  type: 'SKILL',
  required: false,
  semanticTagId: '',
}

const emptyTagForm: TagForm = {
  id: '',
  name: '',
}

const emptyRelationshipForm: RelationshipForm = {
  oneId: '',
  twoId: '',
  weight: '0.5',
}

function makeLabelPayload(form: LabelForm, tags: SemanticTagDTO[]): CreateLabelDTO {
  const tag = tags.find((semanticTag) => semanticTag.id === Number(form.semanticTagId))
  return {
    name: form.name,
    type: form.type,
    required: form.required,
    semanticTag: tag?.id ? { id: tag.id, name: tag.name } : null,
  }
}

function makeLabelForm(label: LabelDTO, tags: SemanticTagDTO[]): LabelForm {
  const tag = tags.find((semanticTag) => semanticTag.name === label.semanticTag)
  return {
    id: String(label.id),
    name: label.name,
    type: label.type,
    required: label.required,
    semanticTagId: tag?.id ? String(tag.id) : '',
  }
}

function formatEnum(value: string) {
  return value
    .toLowerCase()
    .split('_')
    .map((part) => `${part.slice(0, 1).toUpperCase()}${part.slice(1)}`)
    .join(' ')
}
