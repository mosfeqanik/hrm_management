import { useState } from 'react'
import { useMutation, useQuery } from '@tanstack/react-query'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { Edit2, Trash2, UserCheck } from 'react-feather'
import { z } from 'zod'
import { errorMessage } from '../../api/client'
import { organizationsApi, usersApi } from '../../api/platform'
import { ORGANIZATION_STATUSES, type Organization, type OrganizationStatus } from '../../api/types'
import { useResource } from '../../api/useResource'
import {
  Button,
  DataTable,
  IconButton,
  Textarea,
  ErrorBox,
  Field,
  Input,
  Modal,
  ModalFooter,
  PageHeader,
  Select,
  type Column,
} from '../../components/ui'
import { label, orNull } from '../../lib/format'
import { AddButton, Card } from './shared'

const STATUS_BADGE: Record<OrganizationStatus, string> = {
  TRIAL: 'bg-info',
  ACTIVE: 'bg-success',
  EXPIRED: 'bg-secondary',
  SUSPENDED: 'bg-danger',
}

const schema = z.object({
  name: z.string().min(1, 'Name is required').max(150),
  status: z.enum(ORGANIZATION_STATUSES),
  email: z.union([z.literal(''), z.string().email('Email must be valid')]),
  phone: z.string().max(30),
  address: z.string().max(500),
  trialEndsAt: z.string(),
})
type Form = z.infer<typeof schema>

const toLocal = (v: string | null | undefined) => (v ? v.slice(0, 16) : '')

function OrganizationForm({
  item,
  onClose,
  onSaved,
}: {
  item: Organization | null
  onClose: () => void
  onSaved: () => void
}) {
  const [error, setError] = useState<string | null>(null)
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<Form>({
    resolver: zodResolver(schema),
    defaultValues: {
      name: item?.name ?? '',
      status: item?.status ?? 'TRIAL',
      email: item?.email ?? '',
      phone: item?.phone ?? '',
      address: item?.address ?? '',
      trialEndsAt: toLocal(item?.trialEndsAt),
    },
  })

  const onSubmit = async (v: Form) => {
    setError(null)
    const body = {
      name: v.name.trim(),
      status: v.status,
      email: orNull(v.email),
      phone: orNull(v.phone),
      address: orNull(v.address),
      trialEndsAt: v.trialEndsAt ? `${v.trialEndsAt}:00` : null,
    }
    try {
      if (item) await organizationsApi.update(item.id, body)
      else await organizationsApi.create(body)
      onSaved()
    } catch (e) {
      setError(errorMessage(e))
    }
  }

  return (
    <Modal title={item ? 'Edit organization' : 'Add organization'} onClose={onClose}>
      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <div className="modal-body">
          {error && <ErrorBox message={error} />}
          <div className="row">
            <div className="col-12">
              <Field label="Name" error={errors.name?.message}>
                <Input {...register('name')} />
              </Field>
            </div>
            <div className="col-md-6">
              <Field label="Status">
                <Select {...register('status')}>
                  {ORGANIZATION_STATUSES.map((s) => (
                    <option key={s} value={s}>
                      {label(s)}
                    </option>
                  ))}
                </Select>
              </Field>
            </div>
            <div className="col-md-6">
              <Field label="Trial ends">
                <Input type="datetime-local" {...register('trialEndsAt')} />
              </Field>
            </div>
            <div className="col-md-6">
              <Field label="Email" error={errors.email?.message}>
                <Input type="email" {...register('email')} />
              </Field>
            </div>
            <div className="col-md-6">
              <Field label="Phone" error={errors.phone?.message}>
                <Input {...register('phone')} />
              </Field>
            </div>
          </div>
          <Field label="Address" error={errors.address?.message}>
            <Textarea {...register('address')} />
          </Field>
        </div>
        <ModalFooter saving={isSubmitting} onClose={onClose} />
      </form>
    </Modal>
  )
}

function OwnerModal({ org, onClose, onSaved }: { org: Organization; onClose: () => void; onSaved: () => void }) {
  const [userId, setUserId] = useState(org.ownerUserId?.toString() ?? '')
  const users = useQuery({
    queryKey: ['users', { organizationId: org.id }],
    queryFn: () => usersApi.list({ organizationId: org.id }),
  })
  const save = useMutation({ mutationFn: () => organizationsApi.setOwner(org.id, Number(userId)), onSuccess: onSaved })

  return (
    <Modal title={`Owner of ${org.name}`} onClose={onClose}>
      <div className="modal-body">
        {save.error && <ErrorBox message={errorMessage(save.error)} />}
        {users.error && <ErrorBox message={errorMessage(users.error)} />}
        <Field label="Owner user">
          <Select value={userId} onChange={(e) => setUserId(e.target.value)}>
            <option value="">Select...</option>
            {(users.data ?? []).map((u) => (
              <option key={u.id} value={u.id}>
                {u.fullName} ({u.email})
              </option>
            ))}
          </Select>
        </Field>
        <p className="text-muted small mb-0">
          Create the user first on the Users page, with this organization. The owner gets the ORG_OWNER role.
        </p>
      </div>
      <div className="modal-footer">
        <Button variant="secondary" onClick={onClose}>
          Cancel
        </Button>
        <Button disabled={!userId || save.isPending} onClick={() => save.mutate()}>
          {save.isPending ? 'Saving...' : 'Set owner'}
        </Button>
      </div>
    </Modal>
  )
}

export default function OrganizationsPage() {
  const { rows, isLoading, error, remove, refresh } = useResource('organizations', organizationsApi)
  const [editing, setEditing] = useState<Organization | 'new' | null>(null)
  const [ownerOf, setOwnerOf] = useState<Organization | null>(null)

  const columns: Column<Organization>[] = [
    { header: 'ID', cell: (o) => o.id },
    { header: 'Name', cell: (o) => <span className="text-dark font-weight-medium">{o.name}</span>, value: (o) => o.name },
    {
      header: 'Status',
      cell: (o) => <span className={`badge ${STATUS_BADGE[o.status]}`}>{label(o.status)}</span>,
      value: (o) => label(o.status),
    },
    { header: 'Email', cell: (o) => o.email ?? '-', value: (o) => o.email },
    {
      header: 'Owner',
      cell: (o) => (o.ownerUserId ? `User #${o.ownerUserId}` : <span className="text-muted">Not set</span>),
      value: (o) => o.ownerUserId,
    },
    { header: 'Trial ends', cell: (o) => (o.trialEndsAt ? o.trialEndsAt.slice(0, 10) : '-') },
    {
      header: '',
      className: 'text-end text-nowrap',
      cell: (o) => (
        <>
          <IconButton icon={<UserCheck size={15} />} label="Set owner" className="me-1" onClick={() => setOwnerOf(o)} />
          <IconButton icon={<Edit2 size={15} />} label="Edit" className="me-1" onClick={() => setEditing(o)} />
          <IconButton
            icon={<Trash2 size={15} />}
            label="Delete"
            variant="danger"
            disabled={remove.isPending}
            onClick={() => confirm(`Delete ${o.name}? This removes its data.`) && remove.mutate(o.id)}
          />
        </>
      ),
    },
  ]

  return (
    <>
      <PageHeader title="Organizations" trail={['Platform']} />
      <Card
        title="Client organizations"
        actions={<AddButton onClick={() => setEditing('new')}>Add organization</AddButton>}
      >
        {error && <ErrorBox message={errorMessage(error)} />}
        {remove.error && <ErrorBox message={errorMessage(remove.error)} />}
        <DataTable datatable columns={columns} rows={rows} loading={isLoading} empty="No organizations yet." />
      </Card>
      {editing && (
        <OrganizationForm
          item={editing === 'new' ? null : editing}
          onClose={() => setEditing(null)}
          onSaved={() => {
            setEditing(null)
            refresh()
          }}
        />
      )}
      {ownerOf && (
        <OwnerModal
          org={ownerOf}
          onClose={() => setOwnerOf(null)}
          onSaved={() => {
            setOwnerOf(null)
            refresh()
          }}
        />
      )}
    </>
  )
}
