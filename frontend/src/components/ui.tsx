import { forwardRef, useEffect } from 'react'
import type { ButtonHTMLAttributes, InputHTMLAttributes, ReactNode, SelectHTMLAttributes } from 'react'

// Thin wrappers over FreeDash / Bootstrap 5 classes.

export function Button({
  variant = 'primary',
  size,
  className = '',
  ...props
}: ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: 'primary' | 'secondary' | 'danger' | 'dark'
  size?: 'sm'
}) {
  const v = { primary: 'btn-primary', secondary: 'btn-outline-secondary', danger: 'btn-danger', dark: 'btn-dark' }[variant]
  return <button {...props} className={`btn ${v} ${size === 'sm' ? 'btn-sm' : ''} ${className}`} />
}

export function Field({ label, error, children }: { label: string; error?: string; children: ReactNode }) {
  return (
    <div className="form-group mb-3">
      <label className="form-label text-dark">{label}</label>
      {children}
      {error && <div className="text-danger small mt-1">{error}</div>}
    </div>
  )
}

export const Input = forwardRef<HTMLInputElement, InputHTMLAttributes<HTMLInputElement>>((props, ref) => (
  <input ref={ref} {...props} className={`form-control ${props.className ?? ''}`} />
))

export const Select = forwardRef<HTMLSelectElement, SelectHTMLAttributes<HTMLSelectElement>>((props, ref) => (
  <select ref={ref} {...props} className={`form-select ${props.className ?? ''}`} />
))

export function ErrorBox({ message }: { message: string }) {
  return (
    <div className="alert alert-danger py-2" role="alert">
      {message}
    </div>
  )
}

export function Modal({ title, onClose, children }: { title: string; onClose: () => void; children: ReactNode }) {
  useEffect(() => {
    const onKey = (e: KeyboardEvent) => e.key === 'Escape' && onClose()
    document.addEventListener('keydown', onKey)
    return () => document.removeEventListener('keydown', onKey)
  }, [onClose])

  return (
    <>
      <div className="modal-backdrop fade show" />
      <div className="modal fade show d-block" tabIndex={-1} role="dialog" aria-modal="true" onClick={onClose}>
        <div className="modal-dialog modal-dialog-scrollable modal-lg" onClick={(e) => e.stopPropagation()}>
          <div className="modal-content">
            <div className="modal-header">
              <h5 className="modal-title">{title}</h5>
              <button type="button" className="btn-close" aria-label="Close" onClick={onClose} />
            </div>
            {children}
          </div>
        </div>
      </div>
    </>
  )
}

export interface Column<T> {
  header: string
  cell: (row: T) => ReactNode
  className?: string
}

/** Bootstrap table with loading and empty rows. */
export function DataTable<T extends { id: number }>({
  columns,
  rows,
  loading,
  empty = 'Nothing to show.',
}: {
  columns: Column<T>[]
  rows: T[]
  loading?: boolean
  empty?: string
}) {
  return (
    <div className="table-responsive">
      <table className="table table-hover align-middle">
        <thead>
          <tr>
            {columns.map((c, i) => (
              <th key={i} scope="col" className={c.className}>
                {c.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {loading && (
            <tr>
              <td colSpan={columns.length} className="text-center text-muted">
                Loading...
              </td>
            </tr>
          )}
          {!loading && rows.length === 0 && (
            <tr>
              <td colSpan={columns.length} className="text-center text-muted">
                {empty}
              </td>
            </tr>
          )}
          {rows.map((r) => (
            <tr key={r.id}>
              {columns.map((c, i) => (
                <td key={i} className={c.className}>
                  {c.cell(r)}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}

export function Tabs<K extends string>({
  tabs,
  active,
  onChange,
}: {
  tabs: { key: K; label: string }[]
  active: K
  onChange: (key: K) => void
}) {
  return (
    <ul className="nav nav-tabs mb-3">
      {tabs.map((t) => (
        <li key={t.key} className="nav-item">
          <a
            role="button"
            className={`nav-link ${t.key === active ? 'active' : ''}`}
            onClick={() => onChange(t.key)}
          >
            {t.label}
          </a>
        </li>
      ))}
    </ul>
  )
}

export function ActiveBadge({ active }: { active: boolean }) {
  return <span className={`badge ${active ? 'bg-success' : 'bg-secondary'}`}>{active ? 'Active' : 'Inactive'}</span>
}

export function ModalFooter({ saving, onClose }: { saving: boolean; onClose: () => void }) {
  return (
    <div className="modal-footer">
      <Button type="button" variant="secondary" onClick={onClose}>
        Cancel
      </Button>
      <Button type="submit" disabled={saving}>
        {saving ? 'Saving...' : 'Save'}
      </Button>
    </div>
  )
}

/** Page title + breadcrumb row, as in the template's .page-breadcrumb block. */
export function PageHeader({ title, trail = [], actions }: { title: string; trail?: string[]; actions?: ReactNode }) {
  return (
    <div className="page-breadcrumb">
      <div className="row">
        <div className="col-7 align-self-center">
          <h4 className="page-title text-truncate text-dark font-weight-medium mb-1">{title}</h4>
          <nav aria-label="breadcrumb">
            <ol className="breadcrumb m-0 p-0">
              <li className="breadcrumb-item text-muted">Home</li>
              {trail.map((t) => (
                <li key={t} className="breadcrumb-item text-muted active" aria-current="page">
                  {t}
                </li>
              ))}
            </ol>
          </nav>
        </div>
        <div className="col-5 align-self-center">
          <div className="float-end">{actions}</div>
        </div>
      </div>
    </div>
  )
}
