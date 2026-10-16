import { api } from './client'
import { crud } from './crud'
import type { Organization, OrganizationRequest, User, UserRequest } from './types'

export const organizationsApi = {
  ...crud<Organization, OrganizationRequest>('/api/organizations'),
  setOwner: (id: number, ownerUserId: number) =>
    api.patch<Organization>(`/api/organizations/${id}/owner`, { ownerUserId }).then((r) => r.data),
}

export const usersApi = crud<User, UserRequest>('/api/users')
