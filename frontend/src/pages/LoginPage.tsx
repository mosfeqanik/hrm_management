import { useState } from 'react'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { authApi } from '../api/auth'
import { errorMessage } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import { ErrorBox, Field, Input } from '../components/ui'

const schema = z.object({
  email: z.string().min(1, 'Email is required').email('Email must be valid'),
  password: z.string().min(1, 'Password is required'),
})
type Form = z.infer<typeof schema>

export default function LoginPage() {
  const { session, signIn } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()
  const [error, setError] = useState<string | null>(null)
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<Form>({ resolver: zodResolver(schema) })

  if (session) return <Navigate to="/" replace />

  const from = (location.state as { from?: { pathname: string } } | null)?.from?.pathname ?? '/'

  const onSubmit = async (values: Form) => {
    setError(null)
    try {
      const res = await authApi.login(values)
      signIn(res.accessToken, res.fullName)
      navigate(from, { replace: true })
    } catch (e) {
      setError(errorMessage(e))
    }
  }

  return (
    <div className="main-wrapper">
      <div
        className="auth-wrapper d-flex no-block justify-content-center align-items-center position-relative"
        style={{ background: 'url(/template/images/big/auth-bg.jpg) no-repeat center center' }}
      >
        <div className="auth-box row">
          <div
            className="col-lg-7 col-md-5 modal-bg-img"
            style={{ backgroundImage: 'url(/template/images/big/3.jpg)' }}
          />
          <div className="col-lg-5 col-md-7 bg-white">
            <div className="p-3">
              <div className="text-center">
                <img src="/template/images/big/icon.png" alt="HRM Management" />
              </div>
              <h2 className="mt-3 text-center">Sign In</h2>
              <p className="text-center">Enter your email address and password to access the admin panel.</p>
              <form className="mt-4" onSubmit={handleSubmit(onSubmit)} noValidate>
                {error && <ErrorBox message={error} />}
                <Field label="Email" error={errors.email?.message}>
                  <Input type="email" autoComplete="username" placeholder="enter your email" {...register('email')} />
                </Field>
                <Field label="Password" error={errors.password?.message}>
                  <Input
                    type="password"
                    autoComplete="current-password"
                    placeholder="enter your password"
                    {...register('password')}
                  />
                </Field>
                <button type="submit" className="btn w-100 btn-dark" disabled={isSubmitting}>
                  {isSubmitting ? 'Signing in...' : 'Sign In'}
                </button>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
