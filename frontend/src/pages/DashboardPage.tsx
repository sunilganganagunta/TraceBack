import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { reportsApi, matchesApi, claimsApi } from '../api/client';
import { ItemReport, Match, Claim } from '../types';
import { Card } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { ScoreGauge } from '../components/ui/ScoreGauge';
import {
  FileText,
  Sparkles,
  FileCheck2,
  CheckCircle2,
  PlusCircle,
  MapPin,
  ArrowRight,
} from 'lucide-react';

export const DashboardPage: React.FC = () => {
  const { user } = useAuth();

  const [myReports, setMyReports] = useState<ItemReport[]>([]);
  const [myMatches, setMyMatches] = useState<Match[]>([]);
  const [myClaims, setMyClaims] = useState<Claim[]>([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const fetchDashboardData = async () => {
      setIsLoading(true);
      try {
        const [reportsData, matchesData, claimsData] = await Promise.all([
          reportsApi.getMyReports(0, 5),
          matchesApi.getMyMatches(),
          claimsApi.getMyClaims(0, 5),
        ]);

        setMyReports(reportsData.content);
        setMyMatches(matchesData);
        setMyClaims(claimsData.content);
      } catch (err) {
        console.error('Failed to load dashboard data', err);
      } finally {
        setIsLoading(false);
      }
    };

    fetchDashboardData();
  }, []);

  // Compute 4 main summary stats from real data
  const totalReportsCount = myReports.length;
  const potentialMatchesCount = myMatches.length;
  const pendingClaimsCount = myClaims.filter(c => c.status === 'PENDING' || c.status === 'UNDER_REVIEW').length;
  const recoveredCount = myReports.filter(r => r.status === 'RETURNED' || r.status === 'CLOSED').length +
                         myClaims.filter(c => c.status === 'APPROVED' || c.status === 'COMPLETED').length;

  return (
    <div className="space-y-8 pb-12">
      {/* Welcome Banner */}
      <div>
        <h1 className="text-2xl font-bold text-navy-900">Welcome back, {user?.name}!</h1>
        <p className="text-xs text-slate-500 mt-1">
          Here is your recovery activity and intelligent matches across the platform.
        </p>
      </div>

      {/* 4 Main Summary Cards */}
      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {/* Card 1: My Reports */}
        <Link to="/my-reports">
          <Card className="hover:border-primary-300 transition-colors">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                My Reports
              </span>
              <div className="w-8 h-8 rounded-lg bg-blue-50 text-primary-600 flex items-center justify-center">
                <FileText className="w-4 h-4" />
              </div>
            </div>
            <div className="mt-2 text-3xl font-extrabold text-navy-900">
              {isLoading ? '...' : totalReportsCount}
            </div>
            <span className="text-[11px] text-slate-400 mt-1 block">
              Active lost and found filings
            </span>
          </Card>
        </Link>

        {/* Card 2: Potential Matches */}
        <Card className="hover:border-primary-300 transition-colors">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
              Potential Matches
            </span>
            <div className="w-8 h-8 rounded-lg bg-indigo-50 text-indigo-600 flex items-center justify-center">
              <Sparkles className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-2 text-3xl font-extrabold text-navy-900">
            {isLoading ? '...' : potentialMatchesCount}
          </div>
          <span className="text-[11px] text-slate-400 mt-1 block">
            Ranked by matching algorithm
          </span>
        </Card>

        {/* Card 3: Pending Claims */}
        <Link to="/my-claims">
          <Card className="hover:border-primary-300 transition-colors">
            <div className="flex items-center justify-between">
              <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
                Pending Claims
              </span>
              <div className="w-8 h-8 rounded-lg bg-amber-50 text-amber-600 flex items-center justify-center">
                <FileCheck2 className="w-4 h-4" />
              </div>
            </div>
            <div className="mt-2 text-3xl font-extrabold text-navy-900">
              {isLoading ? '...' : pendingClaimsCount}
            </div>
            <span className="text-[11px] text-slate-400 mt-1 block">
              Awaiting verification review
            </span>
          </Card>
        </Link>

        {/* Card 4: Recovered Items */}
        <Card className="hover:border-primary-300 transition-colors">
          <div className="flex items-center justify-between">
            <span className="text-xs font-semibold text-slate-500 uppercase tracking-wider">
              Recovered Items
            </span>
            <div className="w-8 h-8 rounded-lg bg-emerald-50 text-emerald-600 flex items-center justify-center">
              <CheckCircle2 className="w-4 h-4" />
            </div>
          </div>
          <div className="mt-2 text-3xl font-extrabold text-navy-900">
            {isLoading ? '...' : recoveredCount}
          </div>
          <span className="text-[11px] text-slate-400 mt-1 block">
            Successfully returned items
          </span>
        </Card>
      </div>

      {/* Two Prominent Action Buttons */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
        <Link to="/reports/new-lost">
          <div className="bg-rose-50 border border-rose-200 rounded-xl p-5 hover:bg-rose-100/70 transition-colors flex items-center justify-between shadow-sm group">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-lg bg-rose-600 text-white flex items-center justify-center shadow-sm">
                <PlusCircle className="w-5 h-5" />
              </div>
              <div>
                <h3 className="text-sm font-bold text-rose-950">Report Lost Item</h3>
                <p className="text-xs text-rose-700">File a report for something you misplaced</p>
              </div>
            </div>
            <ArrowRight className="w-5 h-5 text-rose-500 group-hover:translate-x-1 transition-transform" />
          </div>
        </Link>

        <Link to="/reports/new-found">
          <div className="bg-emerald-50 border border-emerald-200 rounded-xl p-5 hover:bg-emerald-100/70 transition-colors flex items-center justify-between shadow-sm group">
            <div className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-lg bg-emerald-600 text-white flex items-center justify-center shadow-sm">
                <CheckCircle2 className="w-5 h-5" />
              </div>
              <div>
                <h3 className="text-sm font-bold text-emerald-950">Report Found Item</h3>
                <p className="text-xs text-emerald-700">List an item you found so the owner can recover it</p>
              </div>
            </div>
            <ArrowRight className="w-5 h-5 text-emerald-500 group-hover:translate-x-1 transition-transform" />
          </div>
        </Link>
      </div>

      {/* Recent Potential Matches Section */}
      <Card
        title="Recent Potential Matches"
        subtitle="Ranked algorithmically based on category, title, description, and geospatial proximity"
        action={
          <Link to="/browse" className="text-xs font-semibold text-primary-600 hover:text-primary-800">
            Search More
          </Link>
        }
      >
        {isLoading ? (
          <div className="py-8 text-center text-xs text-slate-500">Evaluating matches...</div>
        ) : myMatches.length === 0 ? (
          <div className="py-6 text-center text-xs text-slate-500">
            No matching items found for your active reports yet. As soon as another user reports an opposing item in your area, matches will appear here.
          </div>
        ) : (
          <div className="space-y-4">
            {myMatches.slice(0, 3).map((match) => (
              <div key={match.id} className="p-4 border border-slate-200 rounded-lg bg-slate-50/50 space-y-3">
                <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-2">
                  <div>
                    <h4 className="text-sm font-bold text-slate-900">
                      Match for: {match.lostReport?.title || match.foundReport?.title}
                    </h4>
                    <p className="text-xs text-slate-500 flex items-center gap-1 mt-0.5">
                      <MapPin className="w-3.5 h-3.5 text-slate-400" />
                      Opposing Location: {match.foundReport?.locationLabel || match.lostReport?.locationLabel}
                    </p>
                  </div>
                  <Link to={`/reports/${match.lostReportId}`}>
                    <Button size="sm" variant="outline">
                      View Pair
                    </Button>
                  </Link>
                </div>

                <ScoreGauge
                  score={match.score}
                  reasons={match.reasons}
                  breakdown={match.scoreBreakdown}
                />
              </div>
            ))}
          </div>
        )}
      </Card>

      {/* Recent My Reports */}
      <Card
        title="My Recent Reports"
        subtitle="Manage your submissions and status"
        action={
          <Link to="/my-reports" className="text-xs font-semibold text-primary-600 hover:text-primary-800">
            View All ({totalReportsCount})
          </Link>
        }
      >
        {isLoading ? (
          <div className="py-8 text-center text-xs text-slate-500">Loading your reports...</div>
        ) : myReports.length === 0 ? (
          <div className="py-6 text-center text-xs text-slate-500">
            You haven't reported any items yet.
          </div>
        ) : (
          <div className="divide-y divide-slate-100">
            {myReports.map((report) => (
              <div key={report.id} className="py-3 flex items-center justify-between gap-4">
                <div className="min-w-0">
                  <div className="flex items-center gap-2">
                    <Badge type={report.reportType} />
                    <Badge status={report.status} />
                    <span className="text-xs text-slate-400">
                      {new Date(report.dateLostOrFound).toLocaleDateString()}
                    </span>
                  </div>
                  <h4 className="text-sm font-semibold text-slate-900 mt-1 truncate">
                    {report.title}
                  </h4>
                  <p className="text-xs text-slate-500 truncate flex items-center gap-1 mt-0.5">
                    <MapPin className="w-3.5 h-3.5 text-slate-400 flex-shrink-0" />
                    {report.publicLocationLabel}
                  </p>
                </div>
                <Link to={`/reports/${report.id}`}>
                  <Button size="sm" variant="outline">Details</Button>
                </Link>
              </div>
            ))}
          </div>
        )}
      </Card>
    </div>
  );
};
