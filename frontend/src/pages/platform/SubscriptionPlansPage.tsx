import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { errorMessage } from '../../api/client'
import { subscriptionPlansApi } from '../../api/platform'
import { BILLING_CYCLES, PLAN_FEATURES, type SubscriptionPlan } from '../../api/types'
import { useResource } from '../../api/useResource'
import { useAuth } from '../../auth/AuthContext'
import {
  ActiveBadge,
  Button,
  DataTable,
  ErrorBox,
  Field,
  Input,
  Modal,
  ModalFooter,
  PageHeader,
  Select,
  type Column,
} from '../../components/ui'
import { formatMoney, label, numOrNull, orNull } from '../../lib/format'
import { Card } from './shared'

const count = z.string().regex(/^\d*$/, 'Whole number')

const schema = z.object({
  name: z.string().min(1, 'Name is required').max(100),
  description: z.string().max(500),
  price: z.string().regex(/^\d+(\.\d{1,2})?$/, 'Enter a price, e.g. 49.99'),
  currency: z.union([z.literal(''), z.string().length(3, '3-letter code')]),
  billingCycle: z.enum(BILLING_CYCLES),
  maxEmployees: count,
  maxBranches: count,
  trialDays: count,
  features: z.array(z.enum(PLAN_FEATURES)),
  active: z.boolean(),
})
type Form = z.infer<typeof schema>

function PlanForm({ item, onClose, onSaved }: { item: SubscriptionPlan | null; onClose: () => void; onSaved: () => void }) {
  const [error, setError] = useState<string | null>(null)
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<Form>({
    resolver: zodResolver(schema),
    defaultValues: {
      name: item?.name ?? '',
      description: item?.description ?? '',
      price: item?.price.toString() ?? '',
      currency: item?.currency ?? 'USD',
      billingCycle: item?.billingCycle ?? 'MONTHLY',
      maxEmployees: item?.maxEmployees?.toString() ?? '',
      maxBranches: item?.maxBranches?.toString() ?? '',
      trialDays: item?.trialDays.toString() ?? '14',
      features: item?.features ?? [],
      active: item?.active ?? true,
    },
  })

  const onSubmit = async (v: Form) => {
    setError(null)
    const body = {
      name: v.name.trim(),
      description: orNull(v.description),
      price: Number(v.price),
      currency: orNull(v.currency.toUpperCase()),
      billingCycle: v.billingCycle,
      maxEmployees: numOrNull(v.maxEmployees),
      maxBranches: numOrNull(v.maxBranches),
      trialDays: numOrNull(v.trialDays) ?? 0,
      features: v.features,
      active: v.active,
    }
    try {
      if (item) await subscriptionPlansApi.update(item.id, body)
      else await subscriptionPlansApi.create(body)
      onSaved()
    } catch (e) {
      setError(errorMessage(e))
    }
  }

  return (
    <Modal title={item ? 'Edit plan' : 'Add plan'} onClose={onClose}>
      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <div className="modal-body">
          {error && <ErrorBox message={error} />}
          <div className="row">
            <div className="col-md-6">
              <Field label="Name" error={errors.name?.message}>
                <Input {...register('name')} />
              </Field>
            </div>
            <div className="col-md-3">
              <Field label="Price" error={errors.price?.message}>
                <Input inputMode="decimal" {...register('price')} />
              </Field>
            </div>
            <div className="col-md-3">
              <Field label="Currency" error={errors.currency?.message}>
                <Input maxLength={3} {...register('currency')} />
              </Field>
            </div>
            <div className="col-md-3">
              <Field label="Billing cycle">
                <Select {...register('billingCycle')}>
                  {BILLING_CYCLES.map((c) => (
                    <option key={c} value={c}>
                      {label(c)}
                    </option>
                  ))}
                </Select>
              </Field>
            </div>
            <div className="col-md-3">
              <Field label="Max employees" error={errors.maxEmployees?.message}>
                <Input inputMode="numeric" placeholder="No limit" {...register('maxEmployees')} />
              </Field>
            </div>
            <div className="col-md-3">
              <Field label="Max branches" error={errors.maxBranches?.message}>
                <Input inputMode="numeric" placeholder="No limit" {...register('maxBranches')} />
              </Field>
            </div>
            <div className="col-md-3">
              <Field label="Trial days" error={errors.trialDays?.message}>
                <Input inputMode="numeric" {...register('trialDays')} />
              </Field>
            </div>
          </div>
          <Field label="Description" error={errors.description?.message}>
            <Input {...register('description')} />
          </Field>
          <Field label="Features">
            <div className="row">
              {PLAN_FEATURES.map((f) => (
                <div key={f} className="col-md-4">
                  <label className="form-check">
                    <input type="checkbox" className="form-check-input" value={f} {...register('features')} />
                    <span className="form-check-label">{label(f)}</span>
                  </label>
                </div>
              ))}
            </div>
          </Field>
          <label className="form-check">
            <input type="checkbox" className="form-check-input" {...register('active')} />
            <span className="form-check-label">Active (can be sold)</span>
          </label>
        </div>
        <ModalFooter saving={isSubmitting} onClose={onClose} />
      </form>
    </Modal>
  )
}

export default function SubscriptionPlansPage() {
  const { session } = useAuth()
  const canEdit = session?.userType === 'SUPER_ADMIN'
  const { rows, isLoading, error, remove, refresh } = useResource('subscription-plans', subscriptionPlansApi)
  const [editing, setEditing] = useState<SubscriptionPlan | 'new' | null>(null)

  const columns: Column<SubscriptionPlan>[] = [
    { header: 'Name', cell: (p) => <span className="text-dark font-weight-medium">{p.name}</span> },
    {
      header: 'Price',
      cell: (p) => `${formatMoney(p.price)} ${p.currency ?? ''} / ${p.billingCycle === 'MONTHLY' ? 'month' : 'year'}`,
    },
    { header: 'Employees', cell: (p) => p.maxEmployees ?? 'No limit' },
    { header: 'Branches', cell: (p) => p.maxBranches ?? 'No limit' },
    { header: 'Trial', cell: (p) => `${p.trialDays} days` },
    { header: 'Features', cell: (p) => <small>{p.features.map(label).join(', ') || '-'}</small> },
    { header: 'Status', cell: (p) => <ActiveBadge active={p.active} /> },
    {
      header: '',
      className: 'text-end text-nowrap',
      cell: (p) =>
        canEdit && (
          <>
            <Button variant="secondary" size="sm" className="me-2" onClick={() => setEditing(p)}>
              Edit
            </Button>
            <Button
              variant="danger"
              size="sm"
              disabled={remove.isPending}
              onClick={() => confirm(`Delete plan ${p.name}?`) && remove.mutate(p.id)}
            >
              Delete
            </Button>
          </>
        ),
    },
  ]

  return (
    <>
      <PageHeader
        title="Subscription Plans"
        trail={['Platform']}
        actions={canEdit && <Button onClick={() => setEditing('new')}>Add plan</Button>}
      />
      <Card title="Plans">
        {error && <ErrorBox message={errorMessage(error)} />}
        {remove.error && <ErrorBox message={errorMessage(remove.error)} />}
        <DataTable columns={columns} rows={rows} loading={isLoading} empty="No plans yet." />
      </Card>
      {editing && (
        <PlanForm
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
