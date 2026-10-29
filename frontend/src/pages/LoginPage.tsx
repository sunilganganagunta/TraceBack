import React, { useState } from 'react';
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Card } from '../components/ui/Card';
import { Button } from '../components/ui/Button';
import { LogIn, AlertCircle, KeyRound, Sparkles } from 'lucide-react';

export const LoginPage: React.FC = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const { login } = useAuth();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const from = (location.state as any)?.from?.pathname || '/dashboard';

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!email || !password) {
      setError('Please provide your email and password');
      return;
    }

    try {
      setIsLoading(true);
      setError(null);
      await login(email, password);
      navigate(from, { replace: true });
    } catch (err: any) {
      setError(err.response?.data?.message || 'Invalid email or password. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  const fillDemo = (demoEmail: string, demoPass: string) => {
    setEmail(demoEmail);
    setPassword(demoPass);
    setError(null);
  };

  return (
    <div className="max-w-md mx-auto py-8 sm:py-12">
      <div className="text-center mb-6">
        <div className="w-10 h-10 rounded-xl bg-navy-900 text-white font-bold text-lg flex items-center justify-center mx-auto mb-2 shadow-sm">
          TB
        </div>
        <h1 className="text-2xl font-bold text-navy-900">Sign in to TraceBack</h1>
        <p className="text-xs text-slate-500 mt-1">
          Manage your lost and found filings, matches, and recovery claims
        </p>
      </div>

      <Card>
        {error && (
          <div className="mb-4 p-3 bg-red-50 border border-red-200 text-red-700 rounded-md text-xs flex items-center gap-2">
            <AlertCircle className="w-4 h-4 flex-shrink-0" />
            <span>{error}</span>
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-xs font-semibold text-slate-700 mb-1">
              Email Address
            </label>
            <input
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="name@example.com"
              className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-2.5 border bg-white"
            />
          </div>

          <div>
            <div className="flex justify-between items-center mb-1">
              <label className="block text-xs font-semibold text-slate-700">
                Password
              </label>
            </div>
            <input
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="••••••••"
              className="w-full text-xs rounded-md border-slate-300 shadow-sm focus:border-primary-500 focus:ring-primary-500 p-2.5 border bg-white"
            />
          </div>

          <Button type="submit" size="md" className="w-full" isLoading={isLoading}>
            <LogIn className="w-4 h-4 mr-1" />
            Sign In
          </Button>
        </form>

        {/* Demo Accounts Quick-Fill Banner for Evaluators & Placement Interviews */}
        <div className="mt-6 pt-5 border-t border-slate-100">
          <div className="flex items-center gap-1.5 text-xs font-semibold text-slate-700 mb-2">
            <Sparkles className="w-3.5 h-3.5 text-primary-600" />
            <span>Demo Showcase Accounts:</span>
          </div>
          <div className="grid grid-cols-3 gap-1.5 text-[11px]">
            <button
              type="button"
              onClick={() => fillDemo('alex@traceback.demo', 'Password123!')}
              className="p-1.5 rounded bg-slate-50 hover:bg-slate-100 border border-slate-200 text-slate-700 text-center font-medium transition-colors"
            >
              Alex (User)
            </button>
            <button
              type="button"
              onClick={() => fillDemo('sam@traceback.demo', 'Password123!')}
              className="p-1.5 rounded bg-slate-50 hover:bg-slate-100 border border-slate-200 text-slate-700 text-center font-medium transition-colors"
            >
              Sam (Mod)
            </button>
            <button
              type="button"
              onClick={() => fillDemo('admin@traceback.demo', 'AdminSecret123!')}
              className="p-1.5 rounded bg-slate-50 hover:bg-slate-100 border border-slate-200 text-slate-700 text-center font-medium transition-colors"
            >
              Admin
            </button>
          </div>
        </div>

        <div className="mt-5 text-center text-xs text-slate-500">
          Don't have an account yet?{' '}
          <Link to="/register" className="font-semibold text-primary-600 hover:text-primary-800">
            Create account
          </Link>
        </div>
      </Card>
    </div>
  );
};
