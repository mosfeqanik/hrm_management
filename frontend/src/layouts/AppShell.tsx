import { useState } from 'react'
import { NavLink, Outlet, useLocation } from 'react-router-dom'
import { Briefcase, Box, Calendar, ChevronDown, Clock, DollarSign, Globe, Grid, Home, MapPin, Power, User, Users } from 'react-feather'
import type { ComponentType } from 'react'
import { useAuth } from '../auth/AuthContext'

interface NavItem {
  to: string
  label: string
  icon: ComponentType<{ className?: string }>
  permission?: string
  /** Shown to SaaS-side users (SUPER_ADMIN / SAAS_STAFF) only. */
  platform?: boolean
  ready: boolean
}

// Sidebar entries are filtered by the user's authorities (backend permission = MODULE_VIEW).
const NAV: NavItem[] = [
  { to: '/', label: 'Dashboard', icon: Home, ready: true },
  { to: '/organizations', label: 'Organizations', icon: Globe, platform: true, ready: true },
  { to: '/users', label: 'Users', icon: User, platform: true, ready: true },
  { to: '/employees', label: 'Employees', icon: Users, permission: 'EMPLOYEE_VIEW', ready: true },
  { to: '/branches', label: 'Branches', icon: MapPin, permission: 'BRANCH_VIEW', ready: true },
  { to: '/departments', label: 'Departments', icon: Grid, permission: 'DEPARTMENT_VIEW', ready: true },
  { to: '/attendance', label: 'Attendance', icon: Clock, permission: 'ATTENDANCE_VIEW', ready: true },
  { to: '/shifts', label: 'Shifts', icon: Calendar, permission: 'SHIFT_VIEW', ready: true },
  { to: '/leave', label: 'Leave', icon: Briefcase, permission: 'LEAVE_VIEW', ready: true },
  { to: '/payroll', label: 'Payroll', icon: DollarSign, permission: 'PAYROLL_VIEW', ready: true },
  { to: '/assets', label: 'Assets', icon: Box, permission: 'ASSET_VIEW', ready: true },
]

export function AppShell() {
  const { session, signOut, can } = useAuth()
  const location = useLocation()
  const [sidebarOpen, setSidebarOpen] = useState(false) // mobile only
  const [navOpen, setNavOpen] = useState(false) // mobile topbar collapse
  const [userMenu, setUserMenu] = useState(false)
  const isPlatform = session?.userType !== 'ORG_USER'
  const items = NAV.filter((n) => (n.platform ? isPlatform : !n.permission || can(n.permission)))
  const platformItems = items.filter((n) => n.platform)
  const moduleItems = items.filter((n) => !n.platform && n.to !== '/')
  const isActive = (to: string) => (to === '/' ? location.pathname === '/' : location.pathname.startsWith(to))

  const renderItem = (n: NavItem) => (
    <li key={n.to} className={`sidebar-item ${isActive(n.to) ? 'selected' : ''}`}>
      <NavLink
        to={n.to}
        end={n.to === '/'}
        className={({ isActive: a }) => `sidebar-link ${a ? 'active' : ''}`}
        onClick={() => setSidebarOpen(false)}
      >
        <n.icon className="feather-icon" />
        <span className="hide-menu">{n.label}</span>
      </NavLink>
    </li>
  )

  const section = (title: string) => (
    <>
      <li className="list-divider" />
      <li className="nav-small-cap">
        <span className="hide-menu">{title}</span>
      </li>
    </>
  )

  return (
    <div
      id="main-wrapper"
      data-theme="light"
      data-layout="vertical"
      data-navbarbg="skin6"
      data-sidebartype="full"
      data-sidebar-position="fixed"
      data-header-position="fixed"
      data-boxed-layout="full"
      className={sidebarOpen ? 'show-sidebar' : ''}
    >
      <header className="topbar" data-navbarbg="skin6">
        <nav className="navbar top-navbar navbar-expand-lg">
          <div className="navbar-header" data-logobg="skin6">
            <a className="nav-toggler waves-effect waves-light d-block d-lg-none" onClick={() => setSidebarOpen((v) => !v)}>
              <i className={`ti-menu ${sidebarOpen ? 'ti-close' : ''}`} />
            </a>
            <div className="navbar-brand">
              <NavLink to="/">
                <img src="/template/images/freepower.svg" alt="HRM Management" className="img-fluid" />
              </NavLink>
            </div>
            <a className="topbartoggler d-block d-lg-none waves-effect waves-light" onClick={() => setNavOpen((v) => !v)}>
              <i className="ti-more" />
            </a>
          </div>
          <div className={`navbar-collapse collapse ${navOpen ? 'show' : ''}`} id="navbarSupportedContent">
            <ul className="navbar-nav float-start me-auto ms-3 ps-1">
              <li className="nav-item d-none d-md-block">
                <span className="nav-link text-muted">
                  {session?.userType.replace('_', ' ')} &middot; scope {session?.dataScope}
                </span>
              </li>
            </ul>
            <ul className="navbar-nav float-end">
              <li className={`nav-item dropdown ${userMenu ? 'show' : ''}`}>
                <a className="nav-link dropdown-toggle" role="button" onClick={() => setUserMenu((v) => !v)}>
                  <img src="/template/images/users/profile-pic.jpg" alt="user" className="rounded-circle" width="40" />
                  <span className="ms-2 d-none d-lg-inline-block">
                    <span>Hello,</span> <span className="text-dark">{session?.fullName}</span>{' '}
                    <ChevronDown className="svg-icon" />
                  </span>
                </a>
                <div className={`dropdown-menu dropdown-menu-end user-dd ${userMenu ? 'show' : ''}`}>
                  <a
                    className="dropdown-item"
                    role="button"
                    onClick={() => {
                      setUserMenu(false)
                      signOut()
                    }}
                  >
                    <Power className="svg-icon me-2 ms-1" /> Logout
                  </a>
                </div>
              </li>
            </ul>
          </div>
        </nav>
      </header>

      <aside className="left-sidebar" data-sidebarbg="skin6">
        <div className="scroll-sidebar" data-sidebarbg="skin6">
          <nav className="sidebar-nav">
            <ul id="sidebarnav">
              {renderItem(NAV[0])}
              {platformItems.length > 0 && section('Platform')}
              {platformItems.map(renderItem)}
              {moduleItems.length > 0 && section('Modules')}
              {moduleItems.map(renderItem)}
            </ul>
          </nav>
        </div>
      </aside>

      <div className="page-wrapper" key={location.pathname} onClick={() => userMenu && setUserMenu(false)}>
        <Outlet />
        <footer className="footer text-center text-muted">HRM Management &middot; built on FreeDash</footer>
      </div>
    </div>
  )
}
