// Mirrors backend DTO records (LoginRequest/Response, EmployeeRequest/Response).
export interface LoginRequest {
  email: string
  password: string
}

export type UserType = 'SUPER_ADMIN' | 'SAAS_STAFF' | 'ORG_USER'

export interface LoginResponse {
  accessToken: string
  tokenType: string
  expiresInSeconds: number
  fullName: string
  userType: UserType
}

export const EMPLOYMENT_TYPES = ['FULL_TIME', 'PART_TIME', 'CONTRACT', 'INTERN', 'PROBATION'] as const
export type EmploymentType = (typeof EMPLOYMENT_TYPES)[number]

export interface Employee {
  id: number
  organizationId: number
  userId: number | null
  employeeCode: string
  fullName: string
  email: string | null
  phone: string | null
  position: string | null
  nationalIdSerial: string | null
  photoUrl: string | null
  address: string | null
  emergencyContactName: string | null
  emergencyContactPhone: string | null
  emergencyContactRelation: string | null
  branchId: number | null
  departmentId: number | null
  managerId: number | null
  employmentType: EmploymentType | null
  joinedAt: string | null
  active: boolean
}

export type EmployeeRequest = Partial<Omit<Employee, 'id'>> & {
  fullName: string
  password?: string
}

export interface Branch {
  id: number
  organizationId: number
  name: string
  code: string | null
  address: string | null
  phone: string | null
  managerId: number | null
  active: boolean
  latitude: number | null
  longitude: number | null
  geofenceRadiusM: number | null
}

export type BranchRequest = Partial<Omit<Branch, 'id'>> & { name: string }

export interface Department {
  id: number
  organizationId: number
  name: string
  branchId: number | null
  headId: number | null
  active: boolean
}

export type DepartmentRequest = Partial<Omit<Department, 'id'>> & { name: string }

export const ATTENDANCE_STATUSES = ['PRESENT', 'LATE', 'ABSENT', 'HALF_DAY', 'ON_LEAVE'] as const
export type AttendanceStatus = (typeof ATTENDANCE_STATUSES)[number]

export const ATTENDANCE_METHODS = ['MOBILE_GPS', 'SELFIE', 'QR', 'PIN', 'MANUAL', 'BIOMETRIC'] as const
export type AttendanceMethod = (typeof ATTENDANCE_METHODS)[number]

export interface Attendance {
  id: number
  organizationId: number
  employeeId: number
  workDate: string
  checkInAt: string | null
  checkOutAt: string | null
  checkInLatitude: number | null
  checkInLongitude: number | null
  checkOutLatitude: number | null
  checkOutLongitude: number | null
  method: AttendanceMethod | null
  status: AttendanceStatus | null
  lateMinutes: number
  workedMinutes: number
  note: string | null
}

export type AttendanceRequest = Partial<Omit<Attendance, 'id'>> & { employeeId: number; workDate: string }

// ---- Leave ----
export type LeaveStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED'

export interface LeaveType {
  id: number
  organizationId: number
  name: string
  annualDays: number
  paid: boolean
  active: boolean
}
export type LeaveTypeRequest = Partial<Omit<LeaveType, 'id'>> & { name: string }

export interface LeaveBalance {
  id: number
  organizationId: number
  employeeId: number
  leaveTypeId: number
  year: number
  entitledDays: number
  usedDays: number
}
export type LeaveBalanceRequest = Partial<Omit<LeaveBalance, 'id'>> & {
  employeeId: number
  leaveTypeId: number
  entitledDays: number
}

export interface LeaveRequest {
  id: number
  organizationId: number
  employeeId: number
  leaveTypeId: number
  fromDate: string
  toDate: string
  days: number
  reason: string | null
  status: LeaveStatus
  decidedById: number | null
  decidedAt: string | null
  decisionNote: string | null
}
export interface LeaveApplyRequest {
  leaveTypeId: number
  fromDate: string
  toDate: string
  reason?: string | null
}

// ---- Shifts ----
export interface Shift {
  id: number
  organizationId: number
  name: string
  startTime: string
  endTime: string
  active: boolean
}
export type ShiftRequest = Partial<Omit<Shift, 'id'>> & { name: string; startTime: string; endTime: string }

export interface ShiftAssignment {
  id: number
  organizationId: number
  employeeId: number
  shiftId: number
  workDate: string
}
export type ShiftAssignmentRequest = Partial<Omit<ShiftAssignment, 'id'>> & {
  employeeId: number
  shiftId: number
  workDate: string
}

// ---- Platform (SaaS side) ----
export const ORGANIZATION_STATUSES = ['TRIAL', 'ACTIVE', 'EXPIRED', 'SUSPENDED'] as const
export type OrganizationStatus = (typeof ORGANIZATION_STATUSES)[number]

export interface Organization {
  id: number
  name: string
  slug: string
  status: OrganizationStatus
  email: string | null
  phone: string | null
  address: string | null
  trialEndsAt: string | null
  activatedAt: string | null
  ownerUserId: number | null
}
export type OrganizationRequest = Partial<Omit<Organization, 'id' | 'activatedAt' | 'ownerUserId'>> & {
  name: string
  slug: string
  status: OrganizationStatus
}

export interface User {
  id: number
  organizationId: number | null
  email: string
  fullName: string
  phone: string | null
  userType: UserType
  active: boolean
  lastLoginAt: string | null
  roles: { id: number; name: string }[]
}
export interface UserRequest {
  organizationId?: number | null
  email: string
  password?: string | null
  fullName: string
  phone?: string | null
  userType: UserType
  active?: boolean
  roleIds?: number[]
}

// ---- Assets and documents ----
export const ASSET_STATUSES = ['AVAILABLE', 'ASSIGNED', 'MAINTENANCE', 'RETIRED'] as const
export type AssetStatus = (typeof ASSET_STATUSES)[number]

export interface Asset {
  id: number
  organizationId: number
  name: string
  assetTag: string
  serialNumber: string | null
  category: string | null
  description: string | null
  status: AssetStatus
}
export type AssetRequest = Partial<Omit<Asset, 'id'>> & { name: string; assetTag: string }

export interface AssetAssignment {
  id: number
  organizationId: number
  assetId: number
  employeeId: number
  assignedAt: string
  returnedAt: string | null
  conditionNote: string | null
}
export interface AssetAssignRequest {
  assetId: number
  employeeId: number
  assignedAt?: string | null
}
export interface AssetReturnRequest {
  returnedAt?: string | null
  conditionNote?: string | null
  assetStatus?: Exclude<AssetStatus, 'ASSIGNED'>
}

export const DOCUMENT_TYPES = [
  'NOC',
  'EXPERIENCE_LETTER',
  'SALARY_CERTIFICATE',
  'PAY_SLIP',
  'OFFER_LETTER',
  'APPOINTMENT_LETTER',
  'INCREMENT_LETTER',
  'PROMOTION_LETTER',
  'OTHER',
] as const
export type DocumentType = (typeof DOCUMENT_TYPES)[number]

export interface EmployeeDocument {
  id: number
  organizationId: number
  employeeId: number
  type: DocumentType
  title: string
  fileUrl: string
  issuedAt: string | null
  note: string | null
}
export type EmployeeDocumentRequest = Partial<Omit<EmployeeDocument, 'id'>> & {
  employeeId: number
  type: DocumentType
  title: string
  fileUrl: string
}

// ---- Payroll ----
export type PayrollStatus = 'DRAFT' | 'FINALIZED' | 'PAID'

export interface PayrollRun {
  id: number
  organizationId: number
  year: number
  month: number
  branchId: number | null
  status: PayrollStatus
  note: string | null
}
export interface PayrollRunCreateRequest {
  organizationId?: number
  year: number
  month: number
  branchId?: number | null
  note?: string | null
}

export interface Payslip {
  id: number
  organizationId: number
  payrollRunId: number
  employeeId: number
  basicSalary: number
  allowances: number
  overtime: number
  bonus: number
  commission: number
  grossSalary: number
  lateDeduction: number
  absentDeduction: number
  loanDeduction: number
  advanceDeduction: number
  netSalary: number
  daysPresent: number
  daysAbsent: number
  daysLate: number
  status: PayrollStatus
}
export interface PayslipAdjustRequest {
  overtime?: number | null
  bonus?: number | null
  commission?: number | null
  loanDeduction?: number | null
  advanceDeduction?: number | null
}
