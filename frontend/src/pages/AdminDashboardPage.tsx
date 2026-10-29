import React, { useEffect, useState } from 'react';
import { analyticsApi, usersApi } from '../api/client';
import { AnalyticsSummary, User } from '../types';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import {
  BarChart3,
  Users,
  FileText,
  CheckCircle2,
  TrendingUp,
  Percent,
  Shield,
  Layers,
} from 'lucide-react';

export const AdminDashboardPage: React.FC = () => {
  const [summary, setSummary] = useState<AnalyticsSummary | null>(null);
  const [users, setUsers] = useState<User[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const loadData = async () => {
    setIsLoading(true);
    try {
      const [summaryData, usersData] = await Promise.all([
        analyticsApi.getSummary(),
        usersApi.adminGetAll(0, 20),
      ]);
      setSummary(summaryData);
      setUsers(usersData.content);
    } catch (err) {
      console.error(err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleToggleStatus = async (userId: string, currentStatus: string) => {
    const nextStatus = currentStatus === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE';
    if (!window.confirm(`Are you sure you want to change this user status to ${nextStatus}?`)) return;

    try {
      await usersApi.adminUpdateStatus(userId, nextStatus);
      loadData();
    } catch (err) {
      alert('Failed to update user status');
    }
  };

  if (isLoading) {
    return (
      <div className="text-center py-24">
        <div className="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin mx-auto" />
        <p className="mt-3 text-xs text-slate-500">Running MongoDB aggregation pipelines...</p>
      </div>
    );
  }

  return (
    <div className="space-y-8 pb-12">
      <div>
        <h1 className="text-2xl font-bold text-navy-900 flex items-center gap-2">
          <BarChart3 className="w-6 h-6 text-purple-600" />
          Admin Analytics & Platform Overview
        </h1>
        <p className="text-xs text-slate-500 mt-1">
          Aggregated in real-time directly from MongoDB collections and indexes
        </p>
      </div>

      {/* Metrics Grid */}
      {summary && (
        <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
          <Card>
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                Total Users
              </span>
              <Users className="w-4 h-4 text-primary-600" />
            </div>
            <div className="mt-2 text-2xl font-extrabold text-navy-900">
              {summary.totalUsers}
            </div>
            <span className="text-[11px] text-slate-400 mt-0.5 block">Registered accounts</span>
          </Card>

          <Card>
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                Total Reports
              </span>
              <FileText className="w-4 h-4 text-blue-600" />
            </div>
            <div className="mt-2 text-2xl font-extrabold text-navy-900">
              {summary.totalReports}
            </div>
            <span className="text-[11px] text-slate-400 mt-0.5 block">
              {summary.totalLostReports} Lost / {summary.totalFoundReports} Found
            </span>
          </Card>

          <Card>
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                Recovered Items
              </span>
              <CheckCircle2 className="w-4 h-4 text-emerald-600" />
            </div>
            <div className="mt-2 text-2xl font-extrabold text-emerald-700">
              {summary.recoveredItems}
            </div>
            <span className="text-[11px] text-slate-400 mt-0.5 block">Successfully returned</span>
          </Card>

          <Card>
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                Recovery Rate
              </span>
              <Percent className="w-4 h-4 text-purple-600" />
            </div>
            <div className="mt-2 text-2xl font-extrabold text-purple-700">
              {summary.recoveryRate}%
            </div>
            <span className="text-[11px] text-slate-400 mt-0.5 block">Recovered / Total ratio</span>
          </Card>
        </div>
      )}

      {/* Breakdown Grids */}
      {summary && (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {/* Category distribution */}
          <Card title="Reports by Category (MongoDB Aggregation)" subtitle="Grouped and counted via aggregation pipeline">
            <div className="space-y-3 mt-2">
              {summary.categoryDistribution.map((cat) => {
                const pct = summary.totalReports > 0 ? (cat.count / summary.totalReports) * 100 : 0;
                return (
                  <div key={cat.categoryName}>
                    <div className="flex justify-between text-xs mb-1">
                      <span className="font-medium text-slate-700">{cat.categoryName}</span>
                      <span className="text-slate-500">{cat.count} items ({Math.round(pct)}%)</span>
                    </div>
                    <div className="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
                      <div className="bg-primary-500 h-2 rounded-full" style={{ width: `${pct}%` }} />
                    </div>
                  </div>
                );
              })}
            </div>
          </Card>

          {/* Status distribution */}
          <Card title="Reports by Lifecycle Status" subtitle="State machine distribution">
            <div className="space-y-3 mt-2">
              {summary.reportStatusDistribution.map((st) => {
                const pct = summary.totalReports > 0 ? (st.count / summary.totalReports) * 100 : 0;
                return (
                  <div key={st.status}>
                    <div className="flex justify-between text-xs mb-1">
                      <Badge status={st.status as any} />
                      <span className="text-slate-500">{st.count} reports ({Math.round(pct)}%)</span>
                    </div>
                    <div className="w-full bg-slate-100 rounded-full h-2 overflow-hidden">
                      <div className="bg-slate-700 h-2 rounded-full" style={{ width: `${pct}%` }} />
                    </div>
                  </div>
                );
              })}
            </div>
          </Card>
        </div>
      )}

      {/* User Management Section */}
      <Card title="Platform User Management" subtitle="Manage permissions and account status">
        <div className="overflow-x-auto mt-2">
          <table className="w-full text-left text-xs text-slate-600">
            <thead className="bg-slate-50 border-b border-slate-200 text-slate-700 font-semibold uppercase text-[10px]">
              <tr>
                <th className="py-2.5 px-3">Name</th>
                <th className="py-2.5 px-3">Email</th>
                <th className="py-2.5 px-3">Roles</th>
                <th className="py-2.5 px-3">Status</th>
                <th className="py-2.5 px-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {users.map((u) => (
                <tr key={u.id} className="hover:bg-slate-50/50">
                  <td className="py-2.5 px-3 font-semibold text-slate-900">{u.name}</td>
                  <td className="py-2.5 px-3">{u.email}</td>
                  <td className="py-2.5 px-3">
                    <div className="flex gap-1">
                      {u.roles.map((r) => (
                        <span key={r} className="text-[10px] px-1.5 py-0.5 rounded bg-slate-100 font-medium">
                          {r.replace('ROLE_', '')}
                        </span>
                      ))}
                    </div>
                  </td>
                  <td className="py-2.5 px-3">
                    <Badge status={u.accountStatus} />
                  </td>
                  <td className="py-2.5 px-3 text-right">
                    <Button
                      size="sm"
                      variant={u.accountStatus === 'ACTIVE' ? 'danger' : 'outline'}
                      onClick={() => handleToggleStatus(u.id, u.accountStatus)}
                    >
                      {u.accountStatus === 'ACTIVE' ? 'Suspend' : 'Activate'}
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </Card>
    </div>
  );
};
