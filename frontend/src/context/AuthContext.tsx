import React, { createContext, useContext, useState, useEffect } from 'react';
import { User } from '../types';
import { authApi } from '../api/client';

interface AuthContextType {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  isModerator: boolean;
  isAdmin: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (name: string, email: string, password: string) => Promise<void>;
  logout: () => void;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem('traceback_user');
    return saved ? JSON.parse(saved) : null;
  });
  const [token, setToken] = useState<string | null>(() => {
    return localStorage.getItem('traceback_token');
  });
  const [isLoading, setIsLoading] = useState<boolean>(true);

  useEffect(() => {
    const initAuth = async () => {
      const savedToken = localStorage.getItem('traceback_token');
      if (savedToken) {
        try {
          const freshUser = await authApi.getMe();
          setUser(freshUser);
          localStorage.setItem('traceback_user', JSON.stringify(freshUser));
        } catch (err) {
          console.error('Session expired or invalid', err);
          logout();
        }
      }
      setIsLoading(false);
    };

    initAuth();
  }, []);

  const login = async (email: string, password: string) => {
    const response = await authApi.login({ email, password });
    localStorage.setItem('traceback_token', response.token);
    localStorage.setItem('traceback_user', JSON.stringify(response.user));
    setToken(response.token);
    setUser(response.user);
  };

  const register = async (name: string, email: string, password: string) => {
    const response = await authApi.register({ name, email, password });
    localStorage.setItem('traceback_token', response.token);
    localStorage.setItem('traceback_user', JSON.stringify(response.user));
    setToken(response.token);
    setUser(response.user);
  };

  const logout = () => {
    localStorage.removeItem('traceback_token');
    localStorage.removeItem('traceback_user');
    setToken(null);
    setUser(null);
  };

  const refreshUser = async () => {
    try {
      const updated = await authApi.getMe();
      setUser(updated);
      localStorage.setItem('traceback_user', JSON.stringify(updated));
    } catch (err) {
      console.error('Error refreshing profile', err);
    }
  };

  const isModerator = user ? user.roles.includes('ROLE_MODERATOR') || user.roles.includes('ROLE_ADMIN') : false;
  const isAdmin = user ? user.roles.includes('ROLE_ADMIN') : false;

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!user && !!token,
        isLoading,
        isModerator,
        isAdmin,
        login,
        register,
        logout,
        refreshUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
