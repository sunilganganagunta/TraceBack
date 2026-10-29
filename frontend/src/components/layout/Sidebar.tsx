import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import {
  LayoutDashboard,
  Search,
  PlusCircle,
  CheckCircle2,
  FileText,
  FileCheck2,
  Bell,
  User as UserIcon,
  ShieldAlert,
  BarChart3,
  MapPin,
} from 'lucide-react';

interface SidebarProps {
  onCloseMobile?: () => void;
}

export const Sidebar: React.FC<SidebarProps> = ({ onCloseMobile }) => {
  const { isAuthenticated, isModerator, isAdmin } = useAuth();

  const navItems = [
    { name: 'Dashboard', path: '/dashboard', icon: LayoutDashboard, authRequired: true },
    { name: 'Browse Items', path: '/browse', icon: Search, authRequired: false },
    { name: 'Nearby Items', path: '/nearby', icon: MapPin, authRequired: false },
    { name: 'Report Lost', path: '/reports/new-lost', icon: PlusCircle, authRequired: true },
    { name: 'Report Found', path: '/reports/new-found', icon: CheckCircle2, authRequired: true },
    { name: 'My Reports', path: '/my-reports', icon: FileText, authRequired: true },
    { name: 'My Claims', path: '/my-claims', icon: FileCheck2, authRequired: true },
    { name: 'Notifications', path: '/notifications', icon: Bell, authRequired: true },
    { name: 'Profile', path: '/profile', icon: UserIcon, authRequired: true },
  ];

  return (
    <aside className="w-64 bg-navy-900 text-slate-300 flex flex-col flex-shrink-0 h-full border-r border-slate-800">
      {/* Brand area */}
      <div className="p-4 flex items-center gap-2 border-b border-slate-800">
        <div className="w-7 h-7 rounded bg-primary-600 text-white font-bold flex items-center justify-center text-sm">
          TB
        </div>
        <span className="font-semibold text-white tracking-wide text-sm">Navigation</span>
      </div>

      {/* Nav Links */}
      <nav className="flex-1 px-3 py-4 space-y-1 overflow-y-auto">
        {navItems
          .filter(item => !item.authRequired || isAuthenticated)
          .map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              onClick={onCloseMobile}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-md text-sm font-medium transition-colors ${
                  isActive
                    ? 'bg-primary-600 text-white shadow-sm'
                    : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                }`
              }
            >
              <item.icon className="w-4 h-4 flex-shrink-0" />
              <span>{item.name}</span>
            </NavLink>
          ))}

        {/* Elevated Moderator Section */}
        {isModerator && (
          <div className="pt-4 mt-4 border-t border-slate-800">
            <span className="px-3 text-xs font-semibold text-slate-400 uppercase tracking-wider">
              Moderation
            </span>
            <div className="mt-2 space-y-1">
              <NavLink
                to="/moderation"
                onClick={onCloseMobile}
                className={({ isActive }) =>
                  `flex items-center gap-3 px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                    isActive
                      ? 'bg-primary-600 text-white'
                      : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                  }`
                }
              >
                <ShieldAlert className="w-4 h-4 text-amber-400" />
                <span>Claims Review</span>
              </NavLink>
            </div>
          </div>
        )}

        {/* Admin Section */}
        {isAdmin && (
          <div className="pt-3 mt-3 border-t border-slate-800">
            <span className="px-3 text-xs font-semibold text-slate-400 uppercase tracking-wider">
              Administration
            </span>
            <div className="mt-2 space-y-1">
              <NavLink
                to="/admin"
                onClick={onCloseMobile}
                className={({ isActive }) =>
                  `flex items-center gap-3 px-3 py-2 rounded-md text-sm font-medium transition-colors ${
                    isActive
                      ? 'bg-primary-600 text-white'
                      : 'text-slate-300 hover:bg-slate-800 hover:text-white'
                  }`
                }
              >
                <BarChart3 className="w-4 h-4 text-purple-400" />
                <span>Analytics & Overview</span>
              </NavLink>
            </div>
          </div>
        )}
      </nav>

      {/* Footer system status */}
      <div className="p-3 border-t border-slate-800 text-xs text-slate-400">
        <div className="flex items-center gap-2">
          <div className="w-2 h-2 rounded-full bg-emerald-400 animate-pulse" />
          <span>MongoDB Engine Active</span>
        </div>
      </div>
    </aside>
  );
};
