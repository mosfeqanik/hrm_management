import { useState } from 'react'
import { useQuery } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Edit2, Trash2 } from 'react-feather'
import { z } from 'zod'
import { errorMessage } from '../../api/client'
import { organizationsApi, usersApi } from '../../api/platform'
import type { User, UserType } from '../../api/types'
import { useResource } from '../../api/useResource'
import {
  ActiveBadge,
  DataTable,
  IconButton,
  ErrorBox,
  Field,
  Input,
  Modal,
  ModalFooter,
  PageHeader,
  Select,
  type Column,
} from '../../components/ui'
import { label, numOrNull, orNull } from '../../lib/format'
import { AddButton, Card } from './shared'

const USER_TYPES: UserType[] = ['SUPER_ADMIN', 'SAAS_STAFF', 'ORG_USER']

const schema = z.object({
  fullName: z.string().min(1, 'Full name is required').max(150),
  email: z.string().min(1, 'Email is required').email('Email must be valid'),
  password: z.string().max(100),
  phone: z.string().max(30),
  userType: z.enum(['SUPER_ADMIN', 'SAAS_STAFF', 'ORG_USER']),
  organizationId: z.string(),
  active: z.boolean(),
})
type Form = z.infer<typeof schema>

function UserForm({ item, onClose, onSaved }: { item: User | null; onClose: () => void; onSaved: () => void }) {
  const orgs = useQuery({ queryKey: ['organizations', null], queryFn: () => organizationsApi.list() })
  const [error, setError] = useState<string | null>(null)
  const {
    register,
    handleSubmit,
    setError: setFieldError,
    formState: { errors, isSubmitting },
  } = useForm<Form>({
    resolver: zodResolver(schema),
    defaultValues: {
      fullName: item?.fullName ?? '',
      email: item?.email ?? '',
      password: '',
      phone: item?.phone ?? '',
      userType: item?.userType ?? 'ORG_USER',
      organizationId: item?.organizationId?.toString() ?? '',
      active: item?.active ?? true,
    },
  })

  const onSubmit = async (v: Form) => {
    setError(null)
    const password = orNull(v.password)
    if (!item && !password) {
      setFieldError('password', { message: 'Password is required for a new user' })
      return
    }
    if (password && password.length < 8) {
      setFieldError('password', { message: 'Password must be at least 8 characters' })
      return
    }
    try {
      const body = {
        fullName: v.fullName.trim(),
        email: v.email.trim(),
        password,
        phone: orNull(v.phone),
        userType: v.userType,
        organizationId: v.userType === 'ORG_USER' ? numOrNull(v.organizationId) : null,
        active: v.active,
        // PUT replaces roles, so send the current ones back.
        roleIds: item?.roles.map((r) => r.id),
      }
      if (item) await usersApi.update(item.id, body)
      else await usersApi.create(body)
      onSaved()
    } catch (e) {
      setError(errorMessage(e))
    }
  }

  return (
    <Modal title={item ? 'Edit user' : 'Add user'} onClose={onClose}>
      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <div className="modal-body">
          {error && <ErrorBox message={error} />}
          <div className="row">
            <div className="col-md-6">
              <Field label="Full name" error={errors.fullName?.message}>
                <Input {...register('fullName')} />
              </Field>
            </div>
            <div className="col-md-6">
              <Field label="Email" error={errors.email?.message}>
                <Input type="email" {...register('email')} />
              </Field>
            </div>
            <div className="col-md-6">
              <Field label={item ? 'New password (leave empty to keep)' : 'Password'} error={errors.password?.message}>
                <Input type="password" autoComplete="new-password" {...register('password')} />
              </Field>
            </div>
            <div className="col-md-6">
              <Field label="Phone" error={errors.phone?.message}>
                <Input {...register('phone')} />
              </Field>
            </div>
            <div className="col-md-6">
              <Field label="User type">
                <Select {...register('userType')}>
                  {USER_TYPES.map((t) => (
                    <option key={t} value={t}>
                      {label(t)}
                    </option>
                  ))}
                </Select>
              </Field>
            </div>
            <div className="col-md-6">
              <Field label="Organization (org users)">
                <Select {...register('organizationId')}>
                  <option value="">-</option>
                  {(orgs.data ?? []).map((o) => (
                    <option key={o.id} value={o.id}>
                      {o.name}
                    </option>
                  ))}
                </Select>
              </Field>
            </div>
          </div>
          <div className="form-check">
            <input className="form-check-input" type="checkbox" id="user-active" {...register('active')} />
            <label className="form-check-label" htmlFor="user-active">
              Active
            </label>
          </div>
        </div>
        <ModalFooter saving={isSubmitting} onClose={onClose} />
      </form>
    </Modal>
  )
}

export default function UsersPage() {
  const [organizationId, setOrganizationId] = useState('')
  const orgs = useQuery({ queryKey: ['organizations', null], queryFn: () => organizationsApi.list() })
  const orgName = (id: number | null) => (id == null ? 'Platform' : (orgs.data?.find((o) => o.id === id)?.name ?? `#${id}`))

  const { rows, isLoading, error, remove, refresh } = useResource(
    'users',
    usersApi,
    organizationId ? { organizationId: Number(organizationId) } : undefined,
  )
  const [editing, setEditing] = useState<User | 'new' | null>(null)

  const columns: Column<User>[] = [
    { header: 'Name', cell: (u) => <span className="text-dark font-weight-medium">{u.fullName}</span>, value: (u) => u.fullName },
    { header: 'Email', cell: (u) => u.email },
    { header: 'Type', cell: (u) => label(u.userType) },
    { header: 'Organization', cell: (u) => orgName(u.organizationId) },
    { header: 'Roles', cell: (u) => (u.roles.length ? u.roles.map((r) => r.name).join(', ') : '-') },
    { header: 'Status', cell: (u) => <ActiveBadge active={u.active} />, value: (u) => (u.active ? 'Active' : 'Inactive') },
    {
      header: 'Last login',
      cell: (u) => (u.lastLoginAt ? u.lastLoginAt.slice(0, 16).replace('T', ' ') : '-'),
      value: (u) => u.lastLoginAt,
    },
    {
      header: '',
      className: 'text-end text-nowrap',
      cell: (u) => (
        <>
          <IconButton icon={<Edit2 size={15} />} label="Edit" className="me-1" onClick={() => setEditing(u)} />
          <IconButton
            icon={<Trash2 size={15} />}
            label="Delete"
            variant="danger"
            disabled={remove.isPending}
            onClick={() => confirm(`Delete ${u.fullName}?`) && remove.mutate(u.id)}
          />
        </>
      ),
    },
  ]

  return (
    <>
      <PageHeader title="Users" trail={['Platform']} />
      <Card
        title="All users"
        actions={
          <>
            <select className="form-select w-auto" value={organizationId} onChange={(e) => setOrganizationId(e.target.value)}>
              <option value="">All organizations</option>
              {(orgs.data ?? []).map((o) => (
                <option key={o.id} value={o.id}>
                  {o.name}
                </option>
              ))}
            </select>
            <AddButton onClick={() => setEditing('new')}>Add user</AddButton>
          </>
        }
      >
        {error && <ErrorBox message={errorMessage(error)} />}
        {remove.error && <ErrorBox message={errorMessage(remove.error)} />}
        <DataTable datatable columns={columns} rows={rows} loading={isLoading} empty="No users found." />
      </Card>
      {editing && (
        <UserForm
          item={editing === 'new' ? null : editing}
          onClose={() => setEditing(null)}
          onSaved={() => {
            setEditing(null)
            refresh()
          }}
        />
      )}
    </>
  )
}
