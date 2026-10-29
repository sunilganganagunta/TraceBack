import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { notificationsApi } from '../../api/client';
import { Bell, Menu, X, LogOut, User as UserIcon, Shield, Search } from 'lucide-react';

interface NavbarProps {
  onToggleMobileMenu: () => void;
  isMobileMenuOpen: boolean;
}

export const Navbar: React.FC<NavbarProps> = ({ onToggleMobileMenu, isMobileMenuOpen }) => {
  const { user, isAuthenticated, logout, isModerator, isAdmin } = useAuth();
  const navigate = useNavigate();
  const [unreadCount, setUnreadCount] = useState<number>(0);

  useEffect(() => {
    if (isAuthenticated) {
      notificationsApi.getUnreadCount()
        .then(count => setUnreadCount(count))
        .catch(() => setUnreadCount(0));

      const interval = setInterval(() => {
        notificationsApi.getUnreadCount()
          .then(count => setUnreadCount(count))
          .catch(() => {});
      }, 30000); // Polling every 30s
      return () => clearInterval(interval);
    }
  }, [isAuthenticated]);

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <header className="sticky top-0 z-40 bg-white border-b border-slate-200 shadow-sm">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo & Brand */}
          <div className="flex items-center gap-3">
            <button
              onClick={onToggleMobileMenu}
              className="md:hidden p-2 text-slate-600 hover:text-slate-900 focus:outline-none"
              aria-label="Toggle navigation menu"
            >
              {isMobileMenuOpen ? <X className="w-6 h-6" /> : <Menu className="w-6 h-6" />}
            </button>
            <Link to="/" className="flex items-center gap-2.5">
              <div className="w-9 h-9 rounded-lg bg-navy-900 flex items-center justify-center text-white font-bold text-lg shadow-sm">
                TB
              </div>
              <div>
                <span className="text-xl font-bold text-navy-900 tracking-tight">TraceBack</span>
                <span className="hidden sm:inline-block ml-2 text-xs px-2 py-0.5 bg-slate-100 text-slate-600 rounded font-medium">
                  Lost & Found
                </span>
              </div>
            </Link>
          </div>

          {/* Right Action Area */}
          <div className="flex items-center gap-3 sm:gap-4">
            <Link
              to="/how-it-works"
              className="hidden md:flex items-center gap-1.5 text-sm font-medium text-slate-600 hover:text-primary-600 transition-colors"
            >
              How It Works
            </Link>
            <Link
              to="/browse"
              className="hidden sm:flex items-center gap-1.5 text-sm font-medium text-slate-600 hover:text-primary-600 transition-colors"
            >
              <Search className="w-4 h-4" />
              Browse Items
            </Link>

            {isAuthenticated ? (
              <>
                {/* Notifications Bell */}
                <Link
                  to="/notifications"
                  className="relative p-2 text-slate-600 hover:text-navy-900 hover:bg-slate-100 rounded-full transition-colors"
                  title="Notifications"
                >
                  <Bell className="w-5 h-5" />
                  {unreadCount > 0 && (
                    <span className="absolute top-1 right-1 flex items-center justify-center min-w-[18px] h-[18px] text-[10px] font-bold text-white bg-red-500 rounded-full px-1">
                      {unreadCount > 9 ? '9+' : unreadCount}
                    </span>
                  )}
                </Link>

                {/* User Info & Role Badge */}
                <div className="flex items-center gap-2 pl-2 border-l border-slate-200">
                  <Link
                    to="/profile"
                    className="flex items-center gap-2 p-1 text-sm font-medium text-slate-700 hover:text-navy-900"
                  >
                    <div className="w-8 h-8 rounded-full bg-primary-100 text-primary-700 font-semibold flex items-center justify-center text-xs">
                      {user?.name.charAt(0).toUpperCase()}
                    </div>
                    <span className="hidden md:inline-block">{user?.name}</span>
                  </Link>

                  {isAdmin ? (
                    <span className="hidden lg:inline-block text-[11px] font-semibold bg-purple-100 text-purple-800 px-2 py-0.5 rounded">
                      Admin
                    </span>
                  ) : isModerator ? (
                    <span className="hidden lg:inline-block text-[11px] font-semibold bg-blue-100 text-blue-800 px-2 py-0.5 rounded">
                      Moderator
                    </span>
                  ) : null}

                  <button
                    onClick={handleLogout}
                    title="Sign Out"
                    className="p-1.5 text-slate-500 hover:text-red-600 rounded hover:bg-slate-100 transition-colors"
                  >
                    <LogOut className="w-4 h-4" />
                  </button>
                </div>
              </>
            ) : (
              <div className="flex items-center gap-2">
                <Link
                  to="/login"
                  className="px-3.5 py-1.5 text-sm font-medium text-slate-700 hover:text-navy-900 transition-colors"
                >
                  Sign In
                </Link>
                <Link
                  to="/register"
                  className="px-4 py-1.5 text-sm font-medium text-white bg-primary-600 hover:bg-primary-700 rounded-md shadow-sm transition-colors"
                >
                  Get Started
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};
