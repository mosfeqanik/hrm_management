import { useState, type ReactNode } from 'react'
import { useMutation } from '@tanstack/react-query'
import { errorMessage } from '../../api/client'
import { Button, ErrorBox, Field, Input, Modal, Select } from '../../components/ui'
import { label, orNull } from '../../lib/format'

/** Badge whose colour comes from a status -> Bootstrap class map. */
export function Badge<S extends string>({ value, classes }: { value: S; classes: Record<S, string> }) {
  return <span className={`badge ${classes[value]}`}>{label(value)}</span>
}

/** Small select that saves a new status as soon as it changes. */
export function StatusSelect<S extends string>({
  value,
  options,
  onChange,
  disabled,
}: {
  value: S
  options: readonly S[]
  onChange: (status: S) => void
  disabled?: boolean
}) {
  return (
    <select
      className="form-select form-select-sm w-auto d-inline-block"
      value={value}
      disabled={disabled}
      onChange={(e) => onChange(e.target.value as S)}
    >
      {options.map((s) => (
        <option key={s} value={s}>
          {label(s)}
        </option>
      ))}
    </select>
  )
}

/** Change a status with an optional note (the backend keeps it in the record's history). */
export function StatusModal<S extends string>({
  title,
  current,
  options,
  onClose,
  onSave,
  onDone,
}: {
  title: string
  current: S
  options: readonly S[]
  onClose: () => void
  onSave: (status: S, note: string | null) => Promise<unknown>
  onDone: () => void
}) {
  const [status, setStatus] = useState<S>(current)
  const [note, setNote] = useState('')
  const save = useMutation({ mutationFn: () => onSave(status, orNull(note)), onSuccess: onDone })

  return (
    <Modal title={title} onClose={onClose}>
      <div className="modal-body">
        {save.error && <ErrorBox message={errorMessage(save.error)} />}
        <Field label="Status">
          <Select value={status} onChange={(e) => setStatus(e.target.value as S)}>
            {options.map((s) => (
              <option key={s} value={s}>
                {label(s)}
              </option>
            ))}
          </Select>
        </Field>
        <Field label="Note (optional)">
          <Input value={note} onChange={(e) => setNote(e.target.value)} />
        </Field>
      </div>
      <div className="modal-footer">
        <Button variant="secondary" onClick={onClose}>
          Cancel
        </Button>
        <Button disabled={status === current || save.isPending} onClick={() => save.mutate()}>
          {save.isPending ? 'Saving...' : 'Save'}
        </Button>
      </div>
    </Modal>
  )
}

export interface HistoryRow {
  id: number
  changedAt: string
  change: string
  changedByName: string | null
  note: string | null
}

export function HistoryModal({
  title,
  rows,
  loading,
  error,
  onClose,
}: {
  title: string
  rows: HistoryRow[]
  loading: boolean
  error: unknown
  onClose: () => void
}) {
  return (
    <Modal title={title} onClose={onClose}>
      <div className="modal-body">
        {error != null && <ErrorBox message={errorMessage(error)} />}
        {loading && <p className="text-muted">Loading...</p>}
        {!loading && rows.length === 0 && <p className="text-muted mb-0">No changes recorded yet.</p>}
        <ul className="list-unstyled mb-0">
          {rows.map((h) => (
            <li key={h.id} className="border-bottom py-2">
              <div className="text-dark">{h.change}</div>
              <small className="text-muted">
                {h.changedAt.replace('T', ' ').slice(0, 16)} &middot; {h.changedByName ?? 'System'}
                {h.note && <> &middot; {h.note}</>}
              </small>
            </li>
          ))}
        </ul>
      </div>
      <div className="modal-footer">
        <Button variant="secondary" onClick={onClose}>
          Close
        </Button>
      </div>
    </Modal>
  )
}

/** Card wrapper used by every platform page. */
export function Card({ title, children }: { title: string; children: ReactNode }) {
  return (
    <div className="container-fluid">
      <div className="card">
        <div className="card-body">
          <h4 className="card-title">{title}</h4>
          {children}
        </div>
      </div>
    </div>
  )
}
