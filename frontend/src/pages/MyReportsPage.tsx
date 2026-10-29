import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { reportsApi } from '../api/client';
import { ItemReport } from '../types';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { EmptyState } from '../components/ui/EmptyState';
import { PlusCircle, MapPin, Eye, Trash2, Calendar } from 'lucide-react';

export const MyReportsPage: React.FC = () => {
  const [reports, setReports] = useState<ItemReport[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  const loadReports = async () => {
    setIsLoading(true);
    try {
      const data = await reportsApi.getMyReports(0, 50);
      setReports(data.content);
    } catch (err) {
      console.error('Failed to load my reports', err);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    loadReports();
  }, []);

  const handleWithdraw = async (id: string, title: string) => {
    if (!window.confirm(`Are you sure you want to withdraw the report '${title}'?`)) return;
    try {
      await reportsApi.withdraw(id);
      loadReports();
    } catch (err) {
      alert('Failed to withdraw report');
    }
  };

  return (
    <div className="space-y-6 pb-12">
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4">
        <div>
          <h1 className="text-2xl font-bold text-navy-900">My Reports</h1>
          <p className="text-xs text-slate-500 mt-1">
            Track and manage your submitted lost and found items
          </p>
        </div>

        <div className="flex items-center gap-2">
          <Link to="/reports/new-lost">
            <Button size="sm" variant="outline">
              <PlusCircle className="w-4 h-4 mr-1" /> Lost Item
            </Button>
          </Link>
          <Link to="/reports/new-found">
            <Button size="sm">
              <PlusCircle className="w-4 h-4 mr-1" /> Found Item
            </Button>
          </Link>
        </div>
      </div>

      {isLoading ? (
        <div className="text-center py-16">
          <div className="w-8 h-8 border-4 border-primary-200 border-t-primary-600 rounded-full animate-spin mx-auto" />
          <p className="mt-2 text-xs text-slate-500">Loading your reports...</p>
        </div>
      ) : reports.length === 0 ? (
        <EmptyState
          title="No Reports Filed Yet"
          description="Have you lost or found something? File a report so our matching engine can help find a match."
          actionText="Report Lost Item"
          onAction={() => window.location.href = '/reports/new-lost'}
        />
      ) : (
        <div className="space-y-3">
          {reports.map((report) => (
            <Card key={report.id} className="p-4 sm:p-5">
              <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-3">
                <div className="space-y-1">
                  <div className="flex items-center gap-2">
                    <Badge type={report.reportType} />
                    <Badge status={report.status} />
                    <span className="text-xs text-slate-400 flex items-center gap-1">
                      <Calendar className="w-3 h-3" />
                      {new Date(report.dateLostOrFound).toLocaleDateString()}
                    </span>
                  </div>

                  <h3 className="text-base font-semibold text-slate-900 mt-1">
                    {report.title}
                  </h3>

                  <div className="flex flex-wrap items-center gap-3 text-xs text-slate-500">
                    <span>Category: <strong className="text-slate-700">{report.categoryName}</strong></span>
                    {report.brand && <span>Brand: <strong className="text-slate-700">{report.brand}</strong></span>}
                    <span className="flex items-center gap-1">
                      <MapPin className="w-3.5 h-3.5 text-slate-400" />
                      {report.publicLocationLabel}
                    </span>
                  </div>
                </div>

                <div className="flex items-center gap-2 flex-shrink-0 self-end sm:self-center">
                  <Link to={`/reports/${report.id}`}>
                    <Button size="sm" variant="outline">
                      <Eye className="w-3.5 h-3.5 mr-1" /> Details & Matches
                    </Button>
                  </Link>

                  {report.status !== 'WITHDRAWN' && (
                    <Button
                      size="sm"
                      variant="ghost"
                      className="text-red-600 hover:text-red-700 hover:bg-red-50"
                      onClick={() => handleWithdraw(report.id, report.title)}
                      title="Withdraw Report"
                    >
                      <Trash2 className="w-3.5 h-3.5" />
                    </Button>
                  )}
                </div>
              </div>
            </Card>
          ))}
        </div>
      )}
    </div>
  );
};
