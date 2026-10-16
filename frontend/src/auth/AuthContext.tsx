import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from 'react'
import { TOKEN_KEY } from '../api/client'
import type { UserType } from '../api/types'

/** Claims set by the backend JwtService. */
export interface Session {
  email: string
  userId: number
  userType: UserType
  organizationId: number | null
  employeeId: number | null
  branchId: number | null
  dataScope: string
  authorities: string[]
  fullName: string
  expiresAt: number
}

const NAME_KEY = 'hrm.fullName'

function decode(token: string | null): Session | null {
  if (!token) return null
  try {
    const payload = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/')
    const bytes = Uint8Array.from(atob(payload), (ch) => ch.charCodeAt(0))
    const c = JSON.parse(new TextDecoder().decode(bytes))
    const expiresAt = (c.exp ?? 0) * 1000
    if (expiresAt <= Date.now()) return null
    return {
      email: c.sub,
      userId: c.userId,
      userType: c.userType,
      organizationId: c.organizationId ?? null,
      employeeId: c.employeeId ?? null,
      branchId: c.branchId ?? null,
      dataScope: c.dataScope,
      authorities: c.authorities ?? [],
      fullName: localStorage.getItem(NAME_KEY) ?? c.sub,
      expiresAt,
    }
  } catch {
    return null
  }
}

interface AuthValue {
  session: Session | null
  signIn: (token: string, fullName: string) => void
  signOut: () => void
  can: (permission: string) => boolean
}

const AuthContext = createContext<AuthValue | null>(null)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [session, setSession] = useState<Session | null>(() => decode(localStorage.getItem(TOKEN_KEY)))

  const signIn = useCallback((token: string, fullName: string) => {
    localStorage.setItem(TOKEN_KEY, token)
    localStorage.setItem(NAME_KEY, fullName)
    setSession(decode(token))
  }, [])

  const signOut = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(NAME_KEY)
    setSession(null)
  }, [])

  const value = useMemo<AuthValue>(
    () => ({
      session,
      signIn,
      signOut,
      // UI hint only; the backend enforces @PreAuthorize on every call.
      can: (permission) => session?.authorities.includes(permission) ?? false,
    }),
    [session, signIn, signOut],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth(): AuthValue {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used inside AuthProvider')
  return ctx
}
